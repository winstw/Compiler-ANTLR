package be.unamur.info.b314.compiler.main.checking;

import be.unamur.info.b314.compiler.SlipLexer;
import be.unamur.info.b314.compiler.SlipParser;
import be.unamur.info.b314.compiler.exception.SymbolAlreadyDefinedException;
import be.unamur.info.b314.compiler.exception.SymbolNotFoundException;

import be.unamur.info.b314.compiler.main.ErrorHandler;
import be.unamur.info.b314.compiler.main.symboltable.*;
import be.unamur.info.b314.compiler.main.symboltable.SlipSymbol.Type;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;
import be.unamur.info.b314.compiler.main.symboltable.SlipSymbol;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @overview a CheckPhaseVisitor is subtype of CheckSlipVisitor with additional methods to check Slip expressions and instructions type
 * A CheckPhaseVisitor is mutable
 */
public class CheckPhaseVisitor extends CheckSlipVisitor {

    public CheckPhaseVisitor(ParseTreeProperty<SlipScope> scopes, ErrorHandler e, String currentPath) {
        super(e, scopes);
        this.currentPath = currentPath;
    }

    public static void main(String[] args) throws IOException {
        File input = new File(System.getProperty("user.dir") + "/src/test/resources/DefPhaseTest.slip");
        SlipLexer lexer = new SlipLexer(new ANTLRInputStream(new FileInputStream(input)));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SlipParser parser = new SlipParser(tokens);
        SlipParser.ProgramContext tree = parser.program();
        ErrorHandler errorHandler = new ErrorHandler();
        GlobalDefinitionPhase visitor = new GlobalDefinitionPhase(errorHandler);
        visitor.visit(tree);
        CheckPhaseVisitor second = new CheckPhaseVisitor(visitor.getScopes(), errorHandler, System.getProperty("user.dir"));
        second.visitProgram(tree);
    }

    private boolean assignationContext = false;
    private String currentPath;

    @Override
    public Type visitProgram(SlipParser.ProgramContext ctx) {

        if (ctx.prog() != null) {
            visit(ctx.prog());
        }

        return null;
    }

    /**
     * @return null
     * @modifies this
     * @effects visit ctx.children
     */
    @Override
    public Type visitProg(SlipParser.ProgContext ctx) {
        SlipScope globalScope = scopes.get(ctx);
        this.currentScope = globalScope;

        visitChildren(ctx);

        this.currentScope = null;
        return null;
    }

    /**
     * @return null
     * @modifies this, System.out
     * @effects if there is a problem with map code or map file, errorHandler.signalError()
     * else if there is no map, print a message on System.out
     */
    @Override
    public Type visitImpDecl(SlipParser.ImpDeclContext ctx) {
        String filename = ctx.FILENAME().getText().replace("\"", "");
        String filePath = this.currentPath + "/" + filename;
        System.out.println("MAP FILE PATH " + filePath);

        File mapFile = new File(filePath);
        if (mapFile.exists() && mapFile.isFile()) {
            try {
                SlipLexer mapLexer = new SlipLexer(new ANTLRInputStream(new FileInputStream(filePath)));
                CommonTokenStream tokens = new CommonTokenStream(mapLexer);
                SlipParser parser = new SlipParser(tokens);
                SlipParser.MapContext tree = parser.map();

                ErrorHandler mapEh = new ErrorHandler();
                GlobalDefinitionPhase gdp = new GlobalDefinitionPhase(mapEh);

                gdp.visit(tree);

                if (!gdp.isMap()) {
                    String errorMessage = "provided map file isn't a map";
                    eh.signalError(ctx.start, errorMessage);
                }

                if (mapEh.hasErrorOccurred()) {
                    String errorMessage = "provided map file contains errors";
                    eh.signalError(ctx.start, errorMessage);
                }

            } catch (RecognitionException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("NO MAP FILE PROVIDED!");
        }

        return null;
    }

    /**
     * @return Type.VOID
     * @effects visit each ctx.children
     */
    @Override
    public Type visitMainDecl(SlipParser.MainDeclContext ctx) {
        this.currentScope = scopes.get(ctx);

        visitChildren(ctx);

        this.currentScope = this.currentScope.getParentScope();

        return Type.VOID;
    }

    /**
     * @return null
     * @modifies this
     * @effects visit each ctx.argList and ctx.instBlock
     */
    @Override
    public Type visitFuncDecl(SlipParser.FuncDeclContext ctx) {
        // function inspection
        SlipScope localScope = scopes.get(ctx);

        this.currentScope = localScope;

        if (ctx.argList() != null) {
            visit(ctx.argList());
        }

        ctx.instBlock().forEach(inst -> inst.accept(this));

        this.currentScope = localScope.getParentScope();
        return null;
    }

    @Override
    public Type visitFuncExpr(SlipParser.FuncExprContext ctx) {
        try {
            String funcName = ctx.ID().getText();
            SlipMethodSymbol scopedFunc = (SlipMethodSymbol) this.currentScope.resolve(funcName + "_fn");

            if (eh.checkEqual(
                    ctx.exprD().size(),
                    scopedFunc.getNumberOfParameters(),
                    ctx.start,
                    String.format("function %s expects %d argument(s)", funcName, scopedFunc.getNumberOfParameters()))
            ) {
                Iterator<SlipVariableSymbol> declaredParamTypes = scopedFunc.getParameters();
                for (SlipParser.ExprDContext param : ctx.exprD()) {
                    Type actualParamType = visit(param);
                    if (declaredParamTypes.hasNext()) {
                        Type declaredParamType = declaredParamTypes.next().getType();
                        String errorMessage = String.format("parameter %s of %s should be of type %s instead of %s", param.getText(), funcName, declaredParamType, actualParamType);
                        eh.checkEqual(actualParamType, declaredParamType, param.start, errorMessage);
                    }
                }
            }

            return scopedFunc.getType();
        } catch (SymbolNotFoundException e) {
            String errorMessage = String.format("function %s is never defined", ctx.ID().getText());
            eh.signalError(ctx.start, errorMessage);
        }
        return Type.VOID;
    }

    /**
     * @return variable's type
     * @modifies this, System.err
     * @effects if currentScope != SlipGlobalScope, defineVariable(ctx)
     * if there is an initialisation, checks that its type is the same as the variable's type, else errorHandler.signalError()
     * if the variable is a constant and there is no initialisation, errorHandler.signalError()
     */
    @Override
    public Type visitVarDecl(SlipParser.VarDeclContext ctx) {
        boolean isConst = ctx.getParent().getStart().getText().equals("const");

        Type definedVarType = visit(ctx.scalar());

        if (!(this.currentScope instanceof SlipGlobalScope)) {
            defineVariable(ctx);
        }

        // type check initialisation
        if (ctx.exprD() != null) {
            Type initVarType = visit(ctx.exprD());
            String errorMessage = String.format("cannot assign expression of type %s to variable of type %s", initVarType, definedVarType);
            eh.checkEqual(initVarType, definedVarType, ctx.exprD().start, errorMessage);
        } else if (isConst) {
            String errorMessage = String.format("const var not initialized: %s", ctx.ID(0).getText());
            eh.signalError(ctx.start, errorMessage);
        }

        return definedVarType;
    }

    /**
     * @return array's type
     * @modifies this, System.err
     * @effects if currentScope != SlipGlobalScope, defineArray(ctx)
     * if there is an initialisation, checks that its type is the same as the array's type, else errorHandler.signalError()
     * if the array is a constant and there is no initialisation, errorHandler.signalError()
     */
    @Override
    public Type visitArrayDecl(SlipParser.ArrayDeclContext ctx) {
        boolean isConst = ctx.getParent().getStart().getText().equals("const");
        Type definedVarType = visit(ctx.scalar());

        if (!(this.currentScope instanceof SlipGlobalScope)) {
            defineArray(ctx);
        }

        // type check initialisation
        if (ctx.initArrays() != null) {
            Type initVarType = visit(ctx.initArrays());

            int expectedFirstDimLength = Integer.parseInt(ctx.number(0).getText());
            int actualFirstDimLength = ctx.initArrays().initVar().size();
            String errorMessage = "First dimension size does not match between declaration and initialization";
            eh.checkEqual(expectedFirstDimLength, actualFirstDimLength, ctx.start, errorMessage);

            if (ctx.number().size() > 1) {
                int expectedSecondDimLength = Integer.parseInt(ctx.number(1).getText());
                for (SlipParser.InitVarContext init : ctx.initArrays().initVar()) {
                    errorMessage = "Second dimension size does not match between declaration and initialization";
                    eh.checkEqual(init.initArrays().initVar().size(), expectedSecondDimLength, init.start, errorMessage);
                }
            }
            errorMessage = String.format("cannot assign expression of type %s to variable of type %s", initVarType, definedVarType);
            eh.checkEqual(initVarType, definedVarType, ctx.initArrays().start, errorMessage);
        } else if (isConst) {
            String errorMessage = String.format("const array not initialized: %s", ctx.ID(0).getText());
            eh.signalError(ctx.start, errorMessage);
        }

        return definedVarType;
    }

    /**
     * @return the type of the first ctx.initVar
     * @modifies this, System.err
     * @effects visit each ctx.initVar. If they are not of the same type, errorHandler.signalError()
     */
    @Override
    public Type visitInitArrays(SlipParser.InitArraysContext ctx) {
        Type type = null;
        for (SlipParser.InitVarContext var : ctx.initVar()) {
            if (type == null) {
                type = visit(var);
            } else {
                String errorMessage = String.format("Array initialized with different types: %s and %s", type, visit(var));
                eh.checkEqual(type, visit(var), var.start, errorMessage);
            }
        }

        return type;
    }

    /**
     * @return Type.STRUCT
     * @modifies this
     * @effects if currentScope != SlipGlobalScope, defineStruct(ctx)
     */
    @Override
    public Type visitStructDecl(SlipParser.StructDeclContext ctx) {

        if (!(this.currentScope instanceof SlipGlobalScope)) {
            defineStructure(ctx);
        }

        return Type.STRUCT;
    }

    /**
     * @modifies this, System.err
     * @effects add ctx to currentScope if it doesn't contain it, else print an error
     */
    @Override
    public Type visitVarDef(SlipParser.VarDefContext ctx) {
        boolean isConst = ctx.getParent().getStart().getText().equals("const");

        Type definedVarType = visit(ctx.scalar());

        for (TerminalNode id : ctx.ID()) {
            try {
                this.currentScope.define(new SlipVariableSymbol(id.getText(), definedVarType, !isConst));
            } catch (SymbolAlreadyDefinedException e) {
                String errorMessage = String.format("param symbol \"%s\" already exists in %s scope", id.getText(), currentScope.getName());
                eh.signalError(id.getSymbol(), errorMessage);
            }
        }

        return definedVarType;
    }

    /**
     * @return the type of ctx.exprD || ctx.initArrays
     * @modifies this, System.err
     * @effects if ctx.initArrays() != visitInitArrays(ctx.initArrays)
     */
    @Override
    public Type visitInitVar(SlipParser.InitVarContext ctx) {
        if (ctx.exprD() != null) {
            return visit(ctx.exprD());
        } else {
            return visit(ctx.initArrays());
        }
    }

    /**
     * @return Did you read this ?
     * @modifies this, System.err
     * @effects hello James
     */
    @Override
    public Type visitLeftExprID(SlipParser.LeftExprIDContext ctx) {
        String idName = ctx.ID().getText();

        try {
            SlipSymbol symbol = currentScope.resolve(idName);

            if (this.assignationContext) {
                String errorMessage = null;

                // if we get an array in the table while in this rule, we are facing an array without bracket
                if (symbol instanceof SlipArraySymbol) {
                    errorMessage = String.format("assignation to array \"%s\" requires an index between brackets", idName);
                }

                // check if assignable in assignation context
                if (!symbol.isAssignable()) {
                    errorMessage = String.format("cannot assign to const variable \"%s\"", idName);
                }

                if (errorMessage != null) {
                    eh.signalError(ctx.ID().getSymbol(), errorMessage);
                }
            }
            return symbol.getType();
        } catch (SymbolNotFoundException e) {
            String errorMessage = String.format("use of undeclared identifier %s", idName);
            eh.signalError(ctx.ID().getSymbol(), errorMessage);
        }
        return null;
    }

    /**
     * @return type of ctx
     */
    @Override
    public Type visitLeftExprArray(SlipParser.LeftExprArrayContext ctx) {
        String idName = ctx.ID().getText();
        try {
            SlipSymbol symbol = currentScope.resolve(idName);
            if (this.assignationContext && !symbol.isAssignable()) {
                String errorMessage = String.format("cannot assign to const array \"%s\"", idName);
                eh.signalError(ctx.ID().getSymbol(), errorMessage);
            }
            return symbol.getType();
        } catch (SymbolNotFoundException e) {
            String errorMessage = String.format("use of undeclared identifier %s", idName);
            eh.signalError(ctx.ID().getSymbol(), errorMessage);
        }

        return Type.VOID;
    }

    /**
     * @return the type of the variable represented by ctx
     */
    @Override
    public Type visitLeftExprRecord(SlipParser.LeftExprRecordContext ctx) {

        StructExprGVisitor visitor = new StructExprGVisitor(currentScope, this.eh);

        SlipSymbol symbol = visitor.visit(ctx);

        return symbol.getType();
    }

    /**
     * @return Type.VOID
     * @modifies this, System.err
     * @effects checks if types on both side of the assignation are the same, if not errorHandler.signalError()
     */
    @Override
    public Type visitAssignInstr(SlipParser.AssignInstrContext ctx) {
        this.assignationContext = true;
        Type exprGType = visit(ctx.exprG());
        this.assignationContext = false;
        Type exprDType = visit(ctx.exprD());

        String errorMessage = String.format("cannot assign expression of type %s to variable of type %s", exprDType, exprGType);
        eh.checkEqual(exprGType, exprDType, ctx.start, errorMessage);

        return Type.VOID;
    }

    /**
     * @return the type of the expression between parenthesis
     */
    @Override
    public Type visitParens(SlipParser.ParensContext ctx) {
        return visit(ctx.exprD());
    }

    /**
     * @return Type.STRING
     */
    @Override
    public Type visitString(SlipParser.StringContext ctx) {
        return Type.STRING;
    }

    /**
     * @return Type.CHARACTER
     */
    @Override
    public Type visitChar(SlipParser.CharContext ctx) {
        return Type.CHARACTER;
    }

    /**
     * @return Type.INTEGER
     */
    @Override
    public Type visitIntExpr(SlipParser.IntExprContext ctx) {
        return Type.INTEGER;
    }

    /**
     * @return type.INTEGER
     * @modifies this, System.err
     * @effects checks if ctx.exprD is of type integer, if not errorHandler.signalError()
     */
    @Override
    public Type visitUnaryMinusExpr(SlipParser.UnaryMinusExprContext ctx) {
        Type type = visit(ctx.exprD());

        String errorMessage = "can only negate integer expression";
        eh.checkEqual(type, Type.INTEGER, ctx.exprD().start, errorMessage);

        return Type.INTEGER;
    }

    /**
     * @modifies this, System.err
     * @effects if the type of exprDContexts id different than requiredType, errorHandler.signalError()
     */
    private void checkAndVisitExprD(Type requiredType, String errorMessage, List<SlipParser.ExprDContext> exprDContexts) {
        for (SlipParser.ExprDContext expr : exprDContexts) {
            Type type = visit(expr);
            eh.checkEqual(type, requiredType, expr.start, errorMessage);
        }
    }

    /**
     * @modifies this, System.err
     * @effects checks that both sides of the operator are of type integer, else errorHandler.signalError()
     * @return Type.INTEGER
     */
    @Override
    public Type visitTimesDivideExpr(SlipParser.TimesDivideExprContext ctx) {
        String errorMessage = "can only use '*', '/' and '%' operators on expressions of type integer";
        checkAndVisitExprD(Type.INTEGER, errorMessage, ctx.exprD());
        return Type.INTEGER;
    }

    /**
     * @modifies this, System.err
     * @effects checks that both sides of the operator are of type integer, else errorHandler.signalError()
     * @return Type.INTEGER
     */
    @Override
    public Type visitPlusMinusExpr(SlipParser.PlusMinusExprContext ctx) {
        String errorMessage = "can only use '+' and '-' operators on expressions of type integer";
        checkAndVisitExprD(Type.INTEGER, errorMessage, ctx.exprD());
        return Type.INTEGER;
    }

    /**
     * @modifies this, System.err
     * @effects checks that both sides of the operator are of type integer, else errorHandler.signalError()
     * @return Type.BOOLEAN
     */
    @Override
    public Type visitComparIntExpr(SlipParser.ComparIntExprContext ctx) {
        String errorMessage = "can only compare integer expressions";
        checkAndVisitExprD(Type.INTEGER, errorMessage, ctx.exprD());
        return Type.BOOLEAN;
    }

    /**
     * @modifies this, System.err
     * @effects checks that both sides of the operator are of type boolean, else errorHandler.signalError()
     * @return Type.BOOLEAN
     */
    @Override
    public Type visitAndOrExpr(SlipParser.AndOrExprContext ctx) {
        String errorMessage = "can only use 'AND' & 'OR' on expressions of type boolean";
        checkAndVisitExprD(Type.BOOLEAN, errorMessage, ctx.exprD());
        return Type.BOOLEAN;
    }

    /**
     * @return Type.BOOLEAN
     */
    @Override
    public Type visitTrueExpr(SlipParser.TrueExprContext ctx) {
        return Type.BOOLEAN;
    }

    /**
     * @return Type.BOOLEAN
     */
    @Override
    public Type visitFalseExpr(SlipParser.FalseExprContext ctx) {
        return Type.BOOLEAN;
    }

    /**
     * @modifies this, System.err
     * @effects if ctx.exprD are of different types errorHandler.signalError()
     * @return Type.BOOLEAN
     */
    @Override
    public Type visitComparExpr(SlipParser.ComparExprContext ctx) {

        Type leftExprType = visit(ctx.exprD(0));
        Type rightExprType = visit(ctx.exprD(1));
        String errorMessage = "can only compare expressions of the same type";
        eh.checkEqual(leftExprType, rightExprType, ctx.start, errorMessage);

        return Type.BOOLEAN;
    }

    /**
     * @modifies this, System.err
     * @effects if ctx.exprD is not of type boolean errorHandler.signalError()
     * @return Type.BOOLEAN
     */
    @Override
    public Type visitNotExpr(SlipParser.NotExprContext ctx) {
        String errorMessage = String.format("%s must be of boolean type", ctx.exprD().getText());
        eh.checkEqual(visit(ctx.exprD()), Type.BOOLEAN, ctx.exprD().start, errorMessage);
        return Type.BOOLEAN;
    }

    private Type checkGuardAndVisitInstr(Type actualType, Token errorToken, List<SlipParser.InstructionContext> toVisitContexts) {
        String errorMessage = "expression must be of type boolean";
        eh.checkEqual(actualType, Type.BOOLEAN, errorToken, errorMessage);
        for (SlipParser.InstructionContext ctx : toVisitContexts) {
            visit(ctx);
        }
        return Type.VOID;
    }

    /**
     * @modifies this, System.err
     * @effects checks that the type of ctx.exprD is boolean and visit each ctx.instruction
     * @return Type.VOID
     */
    @Override
    public Type visitIfThenInstr(SlipParser.IfThenInstrContext ctx) {
        return checkGuardAndVisitInstr(visit(ctx.exprD()), ctx.exprD().start, ctx.instruction());
    }

    /**
     * @modifies this, System.err
     * @effects checks that the type of ctx.exprD is boolean and visit each ctx.instruction
     * @return Type.VOID
     */
    @Override
    public Type visitWhileInstr(SlipParser.WhileInstrContext ctx) {
        return checkGuardAndVisitInstr(visit(ctx.exprD()), ctx.exprD().start, ctx.instruction());
    }

    /**
     * @modifies this, System.err
     * @effects checks that the type of ctx.exprD is boolean and visit each ctx.instruction
     * @return Type.VOID
     */
    @Override
    public Type visitIfThenElseInstr(SlipParser.IfThenElseInstrContext ctx) {
        return checkGuardAndVisitInstr(visit(ctx.exprD()), ctx.exprD().start,
                ctx.guardedBlock().stream().flatMap(block -> block.instruction().stream()).collect(Collectors.toList())
        );
    }

    /**
     * @modifies this, System.err
     * @effects checks that the type of ctx.exprD is boolean and visit each ctx.instruction
     * @return Type.VOID
     */
    @Override
    public Type visitUntilInstr(SlipParser.UntilInstrContext ctx) {
        return checkGuardAndVisitInstr(visit(ctx.exprD()), ctx.exprD().start, ctx.instruction());
    }

    /**
     * @modifies this, System.err
     * @effects checks that the type of ctx.exprD is boolean and visit each ctx.instruction
     * @return Type.VOID
     */
    @Override
    public Type visitForInstr(SlipParser.ForInstrContext ctx) {

        Type initValueType = visit(ctx.exprD(0));
        Type conditionValueType = visit(ctx.exprD(1));

        String errorMessage = "expression must be of type integer";
        eh.checkEqual(initValueType, Type.INTEGER, ctx.exprD(0).start, errorMessage);

        errorMessage = "expression must be of type boolean";
        eh.checkEqual(conditionValueType, Type.BOOLEAN, ctx.exprD(1).start, errorMessage);

        for (SlipParser.InstructionContext inst : ctx.instruction()) {
            visit(inst);
        }

        return Type.VOID;
    }

    /**
     * @modifies this, System.err
     * @effects checks that if ctx.exprD != null, ctx.expD is of type integer, else errorHandler.signalError()
     * @return Type.VOID
     */
    @Override
    public Type visitLeftAction(SlipParser.LeftActionContext ctx) {
        return this.checkActionArg(ctx);
    }

    /**
     * @modifies this, System.err
     * @effects checks that if ctx.exprD != null, ctx.expD is of type integer, else errorHandler.signalError()
     * @return Type.VOID
     */
    @Override
    public Type visitRightAction(SlipParser.RightActionContext ctx) {
        return this.checkActionArg(ctx);
    }

    /**
     * @modifies this, System.err
     * @effects checks that if ctx.exprD != null, ctx.expD is of type integer, else errorHandler.signalError()
     * @return Type.VOID
     */
    @Override
    public Type visitUpAction(SlipParser.UpActionContext ctx) {
        return this.checkActionArg(ctx);
    }

    /**
     * @modifies this, System.err
     * @effects checks that if ctx.exprD != null, ctx.expD is of type integer, else errorHandler.signalError()
     * @return Type.VOID
     */
    @Override
    public Type visitDownAction(SlipParser.DownActionContext ctx) {
        return this.checkActionArg(ctx);
    }

    /**
     * @modifies this, System.err
     * @effects checks that if ctx.exprD != null, ctx.expD is of type integer, else errorHandler.signalError()
     * @return Type.VOID
     */
    @Override
    public Type visitJumpAction(SlipParser.JumpActionContext ctx) {
        return this.checkActionArg(ctx);
    }

    /**
     * @modifies this, System.err
     * @effects checks that if ctx.exprD != null, ctx.expD is of type integer, else errorHandler.signalError()
     * @return Type.VOID
     */
    private Type checkActionArg(SlipParser.ActionTypeContext ctx) {
        SlipParser.ExprDContext arg = ctx.getChild(SlipParser.ExprDContext.class, 0);
        if (arg != null) {
            Type type = arg.accept(this);
            String errorMessage = "expression must be of type integer";
            eh.checkEqual(type, Type.INTEGER, arg.start, errorMessage);
        }
        return Type.VOID;
    }

}
