package be.unamur.info.b314.compiler.main.checking;

import be.unamur.info.b314.compiler.SlipLexer;
import be.unamur.info.b314.compiler.SlipParser;
import be.unamur.info.b314.compiler.exception.SymbolAlreadyDefinedException;
import be.unamur.info.b314.compiler.exception.SymbolNotFoundException;
import be.unamur.info.b314.compiler.main.SlipErrorStrategy;
import be.unamur.info.b314.compiler.symboltable.*;
import be.unamur.info.b314.compiler.symboltable.SlipSymbol.Type;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;
import be.unamur.info.b314.compiler.symboltable.SlipSymbol;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;


public class CheckPhaseVisitor extends CheckSlipVisitor<Type> {

    CheckPhaseVisitor(ParseTreeProperty<SlipScope> scopes, ErrorHandler e) {
        super(e);
        this.scopes = scopes;
    }

    public static void main(String[] args) throws IOException {
        File input = new File(System.getProperty("user.dir") + "/src/test/resources/DefPhaseTest.slip");
        SlipLexer lexer = new SlipLexer(new ANTLRInputStream(new FileInputStream(input)));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SlipParser parser = new SlipParser(tokens);
        parser.setErrorHandler(new SlipErrorStrategy());

        SlipParser.ProgramContext tree = parser.program();
        ErrorHandler errorHandler = new ErrorHandler();
        GlobalDefinitionPhase visitor = new GlobalDefinitionPhase(errorHandler);
        visitor.visit(tree);
        CheckPhaseVisitor second = new CheckPhaseVisitor(visitor.getScopes(), errorHandler);
        second.visitProgram(tree);
    }

    private ParseTreeProperty<SlipScope> scopes;
    private SlipScope currentScope;
    private boolean assignationContext = false;

    @Override
    public Type visitProgram(SlipParser.ProgramContext ctx) {
        System.out.println("=== START  SECOND PHASE ===");

        if (ctx.prog() != null) {
            visit(ctx.prog());
        }
        System.out.println("=== END SECOND PHASE ===");
        return Type.VOID;
    }

    @Override
    public Type visitProg(SlipParser.ProgContext ctx){
        SlipScope globalScope = scopes.get(ctx);
        this.currentScope = globalScope;
        System.out.println("SCOPE : " + currentScope.getName());

        visitChildren(ctx);

        System.out.println(this.currentScope);
        this.currentScope = null;
        return Type.VOID;
    }

    @Override
    public Type visitMainDecl(SlipParser.MainDeclContext ctx) {
        this.currentScope = scopes.get(ctx);

        visitChildren(ctx);

        this.currentScope = this.currentScope.getParentScope();

        return Type.VOID;
    }

    @Override
    public Type visitFuncDecl(SlipParser.FuncDeclContext ctx){
        // function inspection
        SlipScope localScope = scopes.get(ctx);

        this.currentScope = localScope;
        System.out.println("SCOPE : " + currentScope.getName());

        if (ctx.argList() != null) {
            visit(ctx.argList());
        }

        for (SlipParser.InstBlockContext inst: ctx.instBlock()){
            visit(inst);
        }
        System.out.println(currentScope);
        this.currentScope = localScope.getParentScope();
        System.out.println("SCOPE : " + currentScope.getName());
        return Type.VOID;
    }

    @Override
    public Type visitArgList(SlipParser.ArgListContext ctx) {

        for(SlipParser.VarDefContext varDef : ctx.varDef()) {
            visit(varDef);
        }
        return Type.VOID;
    }

    @Override
    public Type visitInstBlock(SlipParser.InstBlockContext ctx){
        for (ParseTree child: ctx.children) {
            visit(child);
        }
        return Type.VOID;
    }

    @Override
    public Type visitFuncExpr(SlipParser.FuncExprContext ctx) {
        try {
            String funcName = ctx.ID().getText();
            SlipSymbol symbol = this.currentScope.resolve(funcName);

            SlipMethodSymbol scopedFunc;
            if (symbol instanceof SlipMethodSymbol) {
                scopedFunc = (SlipMethodSymbol) symbol;
            } else {
                // in case of recursive call we try to get the Method symbol of the same name out of the function's scope
                SlipSymbol sameNameSymbol = this.currentScope.getParentScope().resolve(funcName);
                if (sameNameSymbol instanceof SlipMethodSymbol){
                    scopedFunc = (SlipMethodSymbol) sameNameSymbol;
                } else throw new SymbolNotFoundException();
            }

            System.out.println("FUNC CALL : " + scopedFunc + " Type : " + scopedFunc.getType());

            if(!checkEqual(
                    ctx.exprD().size(),
                    scopedFunc.getNumberOfParameters(),
                    ctx.start,
                    String.format("function %s expects %d argument(s)", funcName, scopedFunc.getNumberOfParameters()))
               ){
                return scopedFunc.getType();
            }

            Iterator<Type> declaredParamTypes = scopedFunc.getParameterTypes();
            for (SlipParser.ExprDContext param : ctx.exprD()){
                Type actualParamType = visit(param);
                if (declaredParamTypes.hasNext()) {
                    Type declaredParamType = declaredParamTypes.next();
                    System.out.println("EFFECTIVE : " + actualParamType + " DECLARED : " + declaredParamType);
                    checkEqual(actualParamType, declaredParamType, param.start,
                               String.format("parameter %s of %s should be of type %s instead of %s",
                                             param.getText(), funcName ,declaredParamType, actualParamType));
                }
            }

            return scopedFunc.getType();
        } catch (SymbolNotFoundException e) {
            signalError(ctx.start, String.format("function %s is never defined", ctx.ID().getText()));
        }
        return Type.VOID;
    }

    @Override
    public Type visitDeclaration(SlipParser.DeclarationContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Type visitVarDecl(SlipParser.VarDeclContext ctx){
        boolean isConst = ctx.getParent().getStart().getText().equals("const");

        Type definedVarType = visit(ctx.scalar());
        System.out.println("VAR DECLARATION : " + definedVarType);

        if (this.currentScope.getName() != "global") {
            for (TerminalNode id : ctx.ID()) {
                try {
                    this.currentScope.define(new SlipVariableSymbol(id.getText(), definedVarType, !isConst));
                } catch (SymbolAlreadyDefinedException e) {
                    signalError(id.getSymbol(), String.format("variable symbol \"%s\" already exists in %s scope", id.getText(), currentScope.getName()));
                }
            }
        }

        // type check initialisation
        if (ctx.exprD() != null) {
            Type initVarType = visit(ctx.exprD());
            System.out.println("in visitVarDECL, init var : " + initVarType);
            checkEqual(initVarType,
                               definedVarType,
                               ctx.exprD().start,
                               String.format("cannot assign expression of type %s to variable of type %s", initVarType, definedVarType));
        } else if (isConst) {
            signalError(ctx.start,String.format("const var not initialized: %s", ctx.ID(0).getText()));
        }

        return definedVarType;
    }

    @Override
    public Type visitArrayDecl(SlipParser.ArrayDeclContext ctx) {
        boolean isConst = ctx.getParent().getStart().getText().equals("const");
        Type definedVarType = visit(ctx.scalar());

        if (currentScope.getName() != "global") {
            for (TerminalNode node : ctx.ID()) {
                String name = node.getText();
                SlipSymbol symbol = new SlipArraySymbol(name, definedVarType, !isConst);

                try {
                    currentScope.define(symbol);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (SymbolAlreadyDefinedException e) {
                    signalError(node.getSymbol(), String.format("array symbol \"%s\" already exists in %s scope", name, currentScope.getName()));

                }

            }
        }

        // type check initialisation
        if (ctx.initArrays() != null) {
            Type initVarType = visit(ctx.initArrays());

            System.out.println("in visitVarDECL, init var : " + initVarType);

            int expectedFirstDimLength = Integer.parseInt(ctx.number(0).getText());
            int actualFirstDimLength = ctx.initArrays().initVar().size();
            checkEqual(
                    expectedFirstDimLength,
                    actualFirstDimLength,
                    ctx.start,
                    "First dimension size does not match between declaration and initialization");

            if (ctx.number().size() > 1) {
                int expectedSecondDimLength = Integer.parseInt(ctx.number(1).getText());
                for (SlipParser.InitVarContext init : ctx.initArrays().initVar()) {
                    checkEqual(init.initArrays().initVar().size(),
                            expectedSecondDimLength,
                            init.start,
                            "Second dimension size does not match between declaration and initialization");
                }
            }
            checkEqual(initVarType,
                          definedVarType,
                          ctx.initArrays().start,
                          String.format("cannot assign expression of type %s to variable of type %s", initVarType, definedVarType));
        } else if (isConst) {
            signalError(ctx.start,String.format("const array not initialized: %s", ctx.ID(0).getText()));
        }
        return definedVarType;
    }

    @Override
    public Type visitInitArrays(SlipParser.InitArraysContext ctx) {
        Type type = null;
        for (SlipParser.InitVarContext var : ctx.initVar()) {
            if (type == null) {
                type = visit(var);
            } else checkEqual(type,
                    visit(var),
                    var.start,
                    String.format("Array initialized with different types: %s and %s", type, visit(var)));
        }

        return type;
    }

    @Override
    public Type visitStructDecl(SlipParser.StructDeclContext ctx) {

        if (currentScope.getName() != "global") {
            for (TerminalNode node : ctx.ID()) {
                String name = node.getText();
                SlipStructureSymbol symbol = new SlipStructureSymbol(name, currentScope, true);

                try {
                    currentScope.define(symbol);
                    currentScope = symbol;

                    for (SlipParser.DeclarationContext var : ctx.declaration()) {
                        visit(var);
                    }

                    currentScope = currentScope.getParentScope();

                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (SymbolAlreadyDefinedException e) {
                    signalError(node.getSymbol(), String.format("structure symbol \"%s\" already exists in %s scope", name, currentScope.getName()));
                }
            }
        }

        return Type.STRUCT;
    }

    @Override
    public Type visitVarDef(SlipParser.VarDefContext ctx){
        boolean isConst = ctx.getParent().getStart().getText().equals("const");

        Type definedVarType = visit(ctx.scalar());
        // add new var definition to the local scope (already done in first pass for global scope)
        if (this.currentScope.getName() != "global") {
            for (TerminalNode id : ctx.ID()) {
                try {
                    this.currentScope.define(new SlipVariableSymbol(id.getText(), definedVarType, !isConst));
                } catch (SymbolAlreadyDefinedException e) {
                    signalError(id.getSymbol(), String.format("param symbol \"%s\" already exists in %s scope", id.getText(), currentScope.getName()));
                }
            }
        }
        return definedVarType;
    }

    @Override
    public Type visitInitVar(SlipParser.InitVarContext ctx){
        if (ctx.exprD() != null) {
            return visit(ctx.exprD());
        }
        else return visit(ctx.initArrays());
    }

    @Override
    public Type visitConstDecl(SlipParser.ConstDeclContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Type visitScalar(SlipParser.ScalarContext ctx){
        if (ctx.BOOLEANTYPE() != null){
            return Type.BOOLEAN;
        }
        if (ctx.CHARTYPE() != null){
            return Type.CHARACTER;
        }
        else {
            return Type.INTEGER;
        }
    }

    @Override
    public Type visitExprGExpr(SlipParser.ExprGExprContext ctx){
        return visit(ctx.exprG());
    }

    @Override
    public Type visitLeftExprID(SlipParser.LeftExprIDContext ctx){
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
                    signalError(ctx.ID().getSymbol(), errorMessage);
                }
            }
            return symbol.getType();
        } catch (SymbolNotFoundException e){
            signalError(ctx.ID().getSymbol(), String.format("use of undeclared identifier %s", idName));
        }
        return Type.VOID;
    }

    @Override
    public Type visitLeftExprArray(SlipParser.LeftExprArrayContext ctx){
        String idName = ctx.ID().getText();
        try {
            SlipSymbol symbol = currentScope.resolve(idName);
            if (this.assignationContext && !symbol.isAssignable()) {
                    signalError(ctx.ID().getSymbol(), String.format("cannot assign to const array \"%s\"", idName));
            }
                return symbol.getType();
        } catch (SymbolNotFoundException e){
            signalError(ctx.ID().getSymbol(), String.format("use of undeclared identifier %s", idName));
        }

        return Type.VOID;
    }

    @Override
    public Type visitLeftExprRecord(SlipParser.LeftExprRecordContext ctx){

        StructExprGVisitor visitor = new StructExprGVisitor(currentScope, this.errorHandler);
        Type type = visitor.visit(ctx);

        return type;
    }

    @Override
    public Type visitAssignInstr(SlipParser.AssignInstrContext ctx) {
        this.assignationContext = true;
        Type exprGType = visit(ctx.exprG());
        Type exprDType = visit(ctx.exprD());

        checkEqual(exprGType,
                     exprDType,
                     ctx.start,
                     String.format("cannot assign expression of type %s to variable of type %s", exprDType, exprGType));

        this.assignationContext = false;
        return Type.VOID;
    }

    @Override
    public Type visitParens(SlipParser.ParensContext ctx){
        return visit(ctx.exprD());
    }

    @Override
    public Type visitString(SlipParser.StringContext ctx){
        return Type.STRING;
    }

    @Override
    public Type visitChar(SlipParser.CharContext ctx) {
        return Type.CHARACTER;
    }

    @Override
    public Type visitIntExpr(SlipParser.IntExprContext ctx){
        return Type.INTEGER;
    }

    @Override
    public Type visitUnaryMinusExpr(SlipParser.UnaryMinusExprContext ctx){
        Type type = visit(ctx.exprD());

        checkEqual(type, Type.INTEGER, ctx.exprD().start, "can only negate integer expression");

        return Type.INTEGER;
    }

    private void checkAndVisitExprD(Type requiredType, String errorMessage, List<SlipParser.ExprDContext> exprDContexts){
        for (SlipParser.ExprDContext expr: exprDContexts) {
            Type type = visit(expr);
            checkEqual(type, requiredType, expr.start, errorMessage);
        }
    }

    @Override
    public Type visitTimesDivideExpr(SlipParser.TimesDivideExprContext ctx){
        checkAndVisitExprD(Type.INTEGER, "can only use '*', '/' and '%' operators on expressions of type integer", ctx.exprD());
        return Type.INTEGER;
    }

    @Override
    public Type visitPlusMinusExpr(SlipParser.PlusMinusExprContext ctx){
        checkAndVisitExprD(Type.INTEGER, "can only use '+' and '-' operators on expressions of type integer", ctx.exprD());
        return Type.INTEGER;
    }

    @Override
    public Type visitComparIntExpr(SlipParser.ComparIntExprContext ctx) {
        checkAndVisitExprD(Type.INTEGER, "can only compare integer expressions", ctx.exprD());
        return Type.BOOLEAN;
    }

    @Override
    public Type visitAndOrExpr(SlipParser.AndOrExprContext ctx){
        checkAndVisitExprD(Type.BOOLEAN, "can only use 'AND' & 'OR' on expressions of type boolean", ctx.exprD());
        return Type.BOOLEAN;
    }

    @Override
    public Type visitTrueExpr(SlipParser.TrueExprContext ctx){
        return Type.BOOLEAN;
    }

    @Override
    public Type visitFalseExpr(SlipParser.FalseExprContext ctx){
        return Type.BOOLEAN;
    }

    @Override
    public Type visitComparExpr(SlipParser.ComparExprContext ctx){

        Type leftExprType = visit(ctx.exprD(0));
        Type rightExprType = visit(ctx.exprD(1));
        checkEqual(leftExprType, rightExprType, ctx.start, "can only compare expressions of the same type");

        return Type.BOOLEAN;
    }



    @Override
    public Type visitNotExpr(SlipParser.NotExprContext ctx){
        checkEqual(visit(ctx.exprD()),
                     Type.BOOLEAN,
                     ctx.exprD().start,
                     String.format("%s must be of boolean type", ctx.exprD().getText()));
        return Type.BOOLEAN;
    }

    private Type checkGuardAndVisitInstr(Type actualType, Token errorToken, List<SlipParser.InstructionContext> toVisitContexts){
        checkEqual(actualType, Type.BOOLEAN, errorToken, "expression must be of type boolean");
        for (SlipParser.InstructionContext ctx : toVisitContexts) {
            visit(ctx);
        }
        return Type.VOID;
    }

    @Override
    public Type visitIfThenInstr(SlipParser.IfThenInstrContext ctx) {
        return checkGuardAndVisitInstr(visit(ctx.exprD()), ctx.exprD().start, ctx.instruction());
    }

    @Override
    public Type visitWhileInstr(SlipParser.WhileInstrContext ctx) {
        return checkGuardAndVisitInstr(visit(ctx.exprD()), ctx.exprD().start, ctx.instruction());
    }

    @Override
    public Type visitIfThenElseInstr(SlipParser.IfThenElseInstrContext ctx) {
        return checkGuardAndVisitInstr(visit(ctx.exprD()), ctx.exprD().start, ctx.instruction());
    }

    @Override
    public Type visitUntilInstr(SlipParser.UntilInstrContext ctx) {
        return checkGuardAndVisitInstr(visit(ctx.exprD()), ctx.exprD().start, ctx.instruction());
    }

    @Override
    public Type visitForInstr(SlipParser.ForInstrContext ctx) {

        Type initValueType = visit(ctx.exprD(0));
        Type conditionValueType = visit(ctx.exprD(1));

        checkEqual(initValueType, Type.INTEGER, ctx.exprD(0).start, "expression must be of type integer");

        checkEqual(conditionValueType, Type.BOOLEAN, ctx.exprD(1).start, "expression must be of type boolean");

        for (SlipParser.InstructionContext inst : ctx.instruction()) {
            visit(inst);
        }

        return Type.VOID;
    }

    @Override
    public Type visitActionType(SlipParser.ActionTypeContext ctx) {

        if (ctx.exprD() != null) {
            Type type = visit(ctx.exprD());

            checkEqual(type, Type.INTEGER, ctx.exprD().start, "expression must be of type integer");
        }

        return Type.VOID;
    }
}
