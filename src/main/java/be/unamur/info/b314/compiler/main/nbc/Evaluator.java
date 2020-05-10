package be.unamur.info.b314.compiler.main.nbc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import be.unamur.info.b314.compiler.SlipBaseVisitor;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import be.unamur.info.b314.compiler.SlipLexer;
import be.unamur.info.b314.compiler.SlipParser;
import be.unamur.info.b314.compiler.main.checking.CheckPhaseVisitor;
import be.unamur.info.b314.compiler.main.ErrorHandler;
import be.unamur.info.b314.compiler.main.checking.GlobalDefinitionPhase;
import be.unamur.info.b314.compiler.main.checking.StructExprGVisitor;
import be.unamur.info.b314.compiler.main.symboltable.SlipArraySymbol;
import be.unamur.info.b314.compiler.main.symboltable.SlipMethodSymbol;
import be.unamur.info.b314.compiler.main.symboltable.SlipScope;
import be.unamur.info.b314.compiler.main.symboltable.SlipStructureSymbol;
import be.unamur.info.b314.compiler.main.symboltable.SlipSymbol;
import be.unamur.info.b314.compiler.main.symboltable.SlipVariableSymbol;

public class Evaluator extends SlipBaseVisitor<Object> {
    private ParseTreeProperty<SlipScope> scopes;
    SlipScope currentScope;
    NbcCompiler compiler;
    ErrorHandler eh;

    static public void main(String[] args) throws IOException {
        File input = new File(System.getProperty("user.dir") + "/src/test/resources/DefPhaseTest.slip");
        SlipLexer lexer = new SlipLexer(new ANTLRInputStream(new FileInputStream(input)));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SlipParser parser = new SlipParser(tokens);
        SlipParser.ProgramContext tree = parser.program();
        ErrorHandler errorHandler = new ErrorHandler();
        GlobalDefinitionPhase visitor = new GlobalDefinitionPhase(errorHandler);
        tree.accept(visitor);
        CheckPhaseVisitor second = new CheckPhaseVisitor(visitor.getScopes(), errorHandler, System.getProperty("user.dir") + "/src/test/resources");
        second.visitProgram(tree);
        NbcCompiler compiler = new NbcCompiler(new File(System.getProperty("user.dir") + "/" + "output.nbc"));
        Evaluator evaluator = new Evaluator(second.getScopes(), errorHandler, compiler);
        evaluator.visitProgram(tree);
        compiler.compile();

    }

    public Evaluator(ParseTreeProperty<SlipScope> scopes, ErrorHandler e, NbcCompiler compiler) {
        super();
        this.eh = e;
        this.scopes = scopes;
        this.compiler = compiler;
    }

    @Override
    public Object visitProgram(SlipParser.ProgramContext ctx) {
        if (ctx.prog() != null) {
            ctx.prog().accept(this);
        }
        return null;
    }

    @Override
    public Object visitProg(SlipParser.ProgContext ctx) {
        this.currentScope = this.scopes.get(ctx);
        // obligé de visiter les déclarations pour initialiser les valeurs des variables le cas échéant
        ctx.declaration().forEach(decl -> decl.accept(this));

        ctx.mainDecl().accept(this);
        this.currentScope = this.currentScope.getParentScope();
        return null;
    }

    @Override
    public Object visitMainDecl(SlipParser.MainDeclContext ctx) {
        this.currentScope = this.scopes.get(ctx);
        ctx.children.stream()
                .filter(child -> !(child instanceof TerminalNodeImpl)) // keep only statement children
                .forEach(child -> child.accept(this));

        this.currentScope = this.currentScope.getParentScope();
        return null;
    }

    @Override
    public Void visitIfThenInstr(SlipParser.IfThenInstrContext ctx) {
        Boolean guard = (Boolean) ctx.exprD().accept(this);
        if (guard) {
            ctx.instruction().forEach(instruction -> instruction.accept(this));
        }
        return null;
    }

    @Override
    public Void visitIfThenElseInstr(SlipParser.IfThenElseInstrContext ctx) {
        Boolean guard = (Boolean) ctx.exprD().accept(this);
        if (guard) {
            ctx.guardedBlock(0).instruction().forEach(instruction -> instruction.accept(this));
        } else {
            ctx.guardedBlock(1).instruction().forEach(instruction -> instruction.accept(this));
        }
        return null;
    }


    @Override
    public Void visitDeclaration(SlipParser.DeclarationContext ctx) {
        ctx.children.forEach(children -> children.accept(this));
        return null;
    }

    @Override
    public Void visitStructDecl(SlipParser.StructDeclContext ctx) {

        SlipScope previousScope = currentScope;
        ctx.ID().forEach(structID -> {
            SlipStructureSymbol struct = (SlipStructureSymbol) this.currentScope.resolve(structID.getText());
            this.currentScope = struct;
            ctx.children.forEach(children -> children.accept(this));
            this.currentScope = previousScope;
        });
        return null;
    }

    @Override
    public Void visitArrayDecl(SlipParser.ArrayDeclContext ctx) {
        if (ctx.initArrays() != null){
            List<Object> initValues;
            if (ctx.number().size() == 1) { // only one dimension
                initValues = ctx.initArrays().initVar().stream()
                        .map(initVar -> initVar.accept(this)).collect(Collectors.toList());
            } else { // two dimensions
                initValues = ctx.initArrays().initVar().stream()
                        .flatMap(var -> var.initArrays().initVar()
                                .stream()
                                .map(initVar -> initVar.accept(this)))
                        .collect(Collectors.toList());
            }
            ctx.ID().forEach(arrayID -> {
                SlipArraySymbol arraySymbol = (SlipArraySymbol) this.currentScope.resolve(arrayID.getText());
                arraySymbol.setValues(initValues);
                });
        }
        return null;
    }

    @Override
    public Object visitInitVar(SlipParser.InitVarContext ctx) {
            return ctx.exprD().accept(this);
    }

    @Override
    public String visitArgList(SlipParser.ArgListContext ctx) {
        return null;
    }

    @Override
    public String visitInstBlock(SlipParser.InstBlockContext ctx) {
        ctx.children.forEach(children -> children.accept(this));
        return null;
    }

    private List<Integer> findIndexes(SlipParser.ExprGContext ctx) {
        List<SlipParser.ExprDContext> exprDContexts = ctx.getRuleContexts(SlipParser.ExprDContext.class);
        while (exprDContexts.size() == 0) {
            ctx = ctx.getRuleContext(SlipParser.ExprGContext.class, 1);
            exprDContexts = ctx.getRuleContexts(SlipParser.ExprDContext.class);
        }
        List<Integer> indexes = exprDContexts // get all "index expressions"
                .stream()
                .map(nbContext -> (Integer) nbContext.accept(this))
                .collect(Collectors.toList());
        return indexes;
    }

    @Override
    public Void visitAssignInstr(SlipParser.AssignInstrContext ctx) {
        TerminalNode id = ctx.exprG().getToken(SlipParser.ID, 0);
        SlipSymbol symbol;
        String varName;
        // not a struct
        if (id != null) {
            varName = id.getText();
            symbol = this.currentScope.resolve(varName);
        } else { // STRUCT
            StructExprGVisitor structVisitor = new StructExprGVisitor(currentScope, eh);
            symbol = structVisitor.visit(ctx.exprG());
            varName = symbol.getName();
        }
        if (symbol != null) {
            if (ctx.exprD() != null) {
                Object value = visit(ctx.exprD());
                if (!(symbol instanceof SlipArraySymbol)) {
                    SlipVariableSymbol varSymbol = (SlipVariableSymbol) symbol;
                    varSymbol.setValue(value);
                } else {
                    SlipArraySymbol arraySymbol = (SlipArraySymbol) symbol;
                    List<Integer> indexes = this.findIndexes(ctx.exprG());
                    arraySymbol.setValue(indexes, value);
                }
            }
        }
        return null;
    }

    @Override
    public String visitString(SlipParser.StringContext ctx) {
        return ctx.STRING().getText();
    }

    @Override
    public Character visitChar(SlipParser.CharContext ctx) {
        return ctx.CHAR().getText().charAt(1);
    }

    @Override
    public Integer visitIntExpr(SlipParser.IntExprContext ctx) {
        return Integer.parseInt(ctx.number().getText());
    }

    @Override
    public Object visitParens(SlipParser.ParensContext ctx) {
        return ctx.exprD().accept(this);
    }


    @Override
    public Integer visitUnaryMinusExpr(SlipParser.UnaryMinusExprContext ctx) {
        return -(Integer) ctx.exprD().accept(this);
    }

    @Override
    public Integer visitTimesDivideExpr(SlipParser.TimesDivideExprContext ctx) {
        Integer left = (Integer) ctx.exprD(0).accept(this);
        Integer right = (Integer) ctx.exprD(1).accept(this);
        switch (ctx.op.getType()) {
            case SlipParser.TIMES:
                return left * right;
            case SlipParser.DIVIDE:
                return left / right;
            case SlipParser.MODULO:
                return Math.floorMod(left, right);
            default:
                return null;
        }
    }

    @Override
    public Object visitExprGExpr(SlipParser.ExprGExprContext ctx) {
        return ctx.exprG().accept(this);
    }

    @Override
    public Integer visitPlusMinusExpr(SlipParser.PlusMinusExprContext ctx) {
        Integer left = (Integer) ctx.exprD(0).accept(this);
        Integer right = (Integer) ctx.exprD(1).accept(this);

        if (ctx.op.getType() == SlipParser.PLUS) return left + right;
        else return left - right;
    }

    @Override
    public Boolean visitTrueExpr(SlipParser.TrueExprContext ctx) {
        return true;
    }

    @Override
    public Boolean visitFalseExpr(SlipParser.FalseExprContext ctx) {
        return false;
    }

    @Override
    public Boolean visitNotExpr(SlipParser.NotExprContext ctx) {
        Boolean toNegateExpr = (Boolean) visit(ctx.exprD());
        if (toNegateExpr != null) {
            return !toNegateExpr;
        } else return toNegateExpr;
    }

    @Override
    public Boolean visitAndOrExpr(SlipParser.AndOrExprContext ctx) {
        if (ctx.AND() != null) {
            return (Boolean) visit(ctx.exprD(0)) && (Boolean) visit(ctx.exprD(1));
        } else {
            return (Boolean) visit(ctx.exprD(0)) || (Boolean) visit(ctx.exprD(1));
        }
    }

    @Override
    public Boolean visitComparExpr(SlipParser.ComparExprContext ctx) {
        Object left = ctx.exprD(0).accept(this);
        Object right = ctx.exprD(1).accept(this);
        if (ctx.op.getType() == SlipParser.EQUAL) return left == right;
        else return left != right;
    }

    @Override
    public Boolean visitComparIntExpr(SlipParser.ComparIntExprContext ctx) {
        Integer left = (Integer) ctx.exprD(0).accept(this);
        Integer right = (Integer) ctx.exprD(1).accept(this);

        switch (ctx.op.getType()) {
            case SlipParser.LT:
                return left < right;
            case SlipParser.LTOE:
                return left <= right;
            case SlipParser.GT:
                return left > right;
            case SlipParser.GTOE:
                return left >= right;
            default:
                return null;
        }
    }

    @Override
    public Void visitUntilInstr(SlipParser.UntilInstrContext ctx) {
        int iterations = 0;
        do {
            ctx.instruction().forEach(instruction -> instruction.accept(this));
            iterations++;
        } while ((Boolean) ctx.exprD().accept(this) && iterations < 1000);

        if (iterations == 1000) {
            eh.signalError(ctx.start, "INFINITE LOOP");
        }

        return null;
    }

    @Override
    public Void visitWhileInstr(SlipParser.WhileInstrContext ctx) {
        int iterations = 0;
        while ((Boolean) ctx.exprD().accept(this) && iterations < 1000) {
            ctx.instruction().forEach(instruction -> instruction.accept(this));
            iterations++;
        }

        if (iterations == 1000) {
            eh.signalError(ctx.start, "INFINITE LOOP");
        }

        return null;
    }

    @Override
    public Void visitForInstr(SlipParser.ForInstrContext ctx) {
        //    FOR ID AFFECT exprD TO exprD DO instruction+ END

        SlipVariableSymbol counter = (SlipVariableSymbol) currentScope.resolve(ctx.ID().getText());
        counter.setValue(ctx.exprD(0).accept(this));
        int iterations = 0;

        while ((Boolean) ctx.exprD(1).accept(this) && iterations < 1000) {
            ctx.instruction().forEach(instruction -> instruction.accept(this));
            iterations++;
        }


        if (iterations == 1000) {
            eh.signalError(ctx.start, "INFINITE LOOP");
        }

        return null;
    }

    @Override
    public Object visitFuncExpr(SlipParser.FuncExprContext ctx) {
        SlipScope previousScope = currentScope;
        String funcName = ctx.ID().getText();
        SlipMethodSymbol scopedFunc = (SlipMethodSymbol) this.currentScope.resolve(funcName + "_fn");

        // create a function call scope, with all variables cloned from the scope linked to the method declaration
        // we have already collected all functions declarations in the previous phases
        SlipMethodSymbol calledFunc = scopedFunc.clone();

        // assign each param in the method call scope
        Iterator<SlipVariableSymbol> paramIter = scopedFunc.getParameters(); // get declared params in method definition
        List<SlipParser.ExprDContext> args = ctx.exprD(); // args to the function call
        int i = 0;
        while (paramIter.hasNext() && i < args.size()) {
            // get param from method definition
            SlipVariableSymbol param = paramIter.next();
            // assign value from function call to param
            SlipVariableSymbol paramInScope = (SlipVariableSymbol) calledFunc.resolve(param.getName());

            paramInScope.setValue(ctx.exprD(i).accept(this));
            i++;
        }

        this.currentScope = calledFunc;

        scopedFunc.getBody().forEach(instBlock -> instBlock.accept(this));

        // return method return value (variable with same name as method which should be declared in scope)
        SlipVariableSymbol returnSymbol = (SlipVariableSymbol) this.currentScope.resolve(funcName);

        this.currentScope = previousScope;

        return returnSymbol.getValue();
    }

    @Override
    public Object visitLeftExprArray(SlipParser.LeftExprArrayContext ctx) {

        SlipArraySymbol array = (SlipArraySymbol) currentScope.resolve(ctx.ID().getText());

        List<Integer> indexes = findIndexes(ctx);
        return array.getValue(indexes);
    }

    @Override
    public Object visitLeftExprRecord(SlipParser.LeftExprRecordContext ctx) {
        SlipSymbol symbol = new StructExprGVisitor(currentScope, eh).visit(ctx);
        if (symbol instanceof SlipArraySymbol) {
            return ((SlipArraySymbol) symbol).getValue(findIndexes(ctx.exprG(1)));
        }
        return ((SlipVariableSymbol) symbol).getValue();

    }

    @Override
    public Object visitLeftExprID(SlipParser.LeftExprIDContext ctx) {

        SlipSymbol symbol = currentScope.resolve(ctx.ID().getText());

        if (symbol instanceof SlipStructureSymbol) {
            return null;
        }
        SlipVariableSymbol variable = (SlipVariableSymbol) symbol;

        return variable.getValue();
    }

    @Override
    public Void visitVarDecl(SlipParser.VarDeclContext ctx) {
        if (ctx.exprD() != null) {
            Object value = ctx.exprD().accept(this);
            ctx.ID().forEach(var -> {
                SlipVariableSymbol symbol = (SlipVariableSymbol) this.currentScope.resolve(var.getText());
                if (symbol != null) {
                    symbol.setValue(value);
                }
            });
        }
        return null;
    }

    /**
     * Add a "move" action (RIGHT, LEFT, UP, DOWN) to the compiler
     *
     * @param argContext The context corresponding to the argument of the action.
     * @param actionType The type of the action to add.
     */
    private void addMoveAction(SlipParser.ExprDContext argContext, NbcCompiler.ActionType actionType) {
        int arg;
        if (argContext != null) {
            arg = (Integer) argContext.accept(this);
        } else {
            arg = 1;
        }
        this.compiler.addAction(actionType, arg);
    }

    @Override
    public Void visitLeftAction(SlipParser.LeftActionContext ctx) {
        this.addMoveAction(ctx.exprD(), NbcCompiler.ActionType.LEFT);
        return null;
    }

    @Override
    public Void visitRightAction(SlipParser.RightActionContext ctx) {
        this.addMoveAction(ctx.exprD(), NbcCompiler.ActionType.RIGHT);
        return null;
    }

    @Override
    public Void visitUpAction(SlipParser.UpActionContext ctx) {
        this.addMoveAction(ctx.exprD(), NbcCompiler.ActionType.UP);
        return null;
    }

    @Override
    public Void visitDownAction(SlipParser.DownActionContext ctx) {
        this.addMoveAction(ctx.exprD(), NbcCompiler.ActionType.DOWN);
        return null;
    }

    @Override
    public Void visitJumpAction(SlipParser.JumpActionContext ctx) {
        this.compiler.addAction(NbcCompiler.ActionType.JUMP);
        return null;
    }

    @Override
    public Void visitFightAction(SlipParser.FightActionContext ctx) {
        this.compiler.addAction(NbcCompiler.ActionType.FIGHT);
        return null;
    }

    @Override
    public Void visitDigAction(SlipParser.DigActionContext ctx) {
        this.compiler.addAction(NbcCompiler.ActionType.DIG);
        return null;
    }

    @Override
    public Void visitDig(SlipParser.DigContext ctx) {
        this.compiler.addAction(NbcCompiler.ActionType.DIG);
        return null;
    }
}


