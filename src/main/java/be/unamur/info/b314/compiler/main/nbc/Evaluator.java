package be.unamur.info.b314.compiler.main.nbc;

import be.unamur.info.b314.compiler.SlipBaseVisitor;
import be.unamur.info.b314.compiler.SlipLexer;
import be.unamur.info.b314.compiler.SlipParser;
import be.unamur.info.b314.compiler.exception.SymbolNotFoundException;
import be.unamur.info.b314.compiler.main.checking.CheckPhaseVisitor;
import be.unamur.info.b314.compiler.main.checking.CheckSlipVisitor;
import be.unamur.info.b314.compiler.main.checking.ErrorHandler;
import be.unamur.info.b314.compiler.main.checking.GlobalDefinitionPhase;
import be.unamur.info.b314.compiler.main.checking.MapVisitor;
import be.unamur.info.b314.compiler.symboltable.*;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.atn.SemanticContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.antlr.v4.runtime.tree.xpath.XPath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Evaluator extends CheckSlipVisitor<Object> {
    String[][] map = null;
    String currentPath = "";
    private ParseTreeProperty<SlipScope> scopes;
    SlipScope currentScope;
    NbcCompiler compiler;
    static public void main(String[] args) throws IOException {
        File input = new File(System.getProperty("user.dir") + "/src/test/resources/DefPhaseTest.slip");
        SlipLexer lexer = new SlipLexer(new ANTLRInputStream(new FileInputStream(input)));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SlipParser parser = new SlipParser(tokens);
        SlipParser.ProgramContext tree = parser.program();
        ErrorHandler errorHandler = new ErrorHandler();
        GlobalDefinitionPhase visitor = new GlobalDefinitionPhase(errorHandler);
        tree.accept(visitor);
        CheckPhaseVisitor second = new CheckPhaseVisitor(visitor.getScopes(), errorHandler);
        second.visitProgram(tree);
        NbcCompiler compiler = new NbcCompiler();
        Evaluator evaluator = new Evaluator(second.getScopes(), errorHandler, System.getProperty("user.dir") + "/src/test/resources/", compiler);
        evaluator.visitProgram(tree);
        System.out.println(compiler);

    }

    public Evaluator(ParseTreeProperty<SlipScope> scopes, ErrorHandler e, String currentPath, NbcCompiler compiler){
        super(e);
        this.scopes = scopes;
        this.currentPath = currentPath;
        this.compiler = compiler;
    }

    @Override
    public Object visitProgram(SlipParser.ProgramContext ctx) {
        if (ctx.prog() != null) {
            ctx.prog().accept(this);
        }
        return null;
    }

    private String[][] loadMapFile(SlipParser.ImpDeclContext ctx) {
        // populate map field from file
        String filename = ctx.FILENAME().getText().replace("\"", "");
        String filePath = this.currentPath + filename;
        try {
            SlipLexer mapLexer = new SlipLexer(new ANTLRInputStream(new FileInputStream(filePath)));
            CommonTokenStream tokens = new CommonTokenStream(mapLexer);
            SlipParser parser = new SlipParser(tokens);
            SlipParser.MapContext tree = parser.map();
            boolean isValidMap = new MapVisitor(this.errorHandler).visit(tree);
            if (isValidMap){
                this.visit(tree);
            }
        } catch (FileNotFoundException e) {
            signalError(ctx.start , String.format("Cannot load map file %s", filePath));
            e.printStackTrace();
        } catch (RecognitionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object visitProg(SlipParser.ProgContext ctx){
        if (this.currentPath != null) {
            this.loadMapFile(ctx.impDecl());
        }


        this.currentScope = this.scopes.get(ctx);
        // obligé de visiter les déclarations pour initialiser les valeurs des variables le cas échéant
        ctx.declaration().forEach(decl -> decl.accept(this));

        ctx.mainDecl().accept(this);
        this.currentScope = this.currentScope.getParentScope();
        return null;
    }


    public Void visitMap(SlipParser.MapContext ctx) {
        System.out.println("=== MAP EVAL ===");
        int nbLines = Integer.parseInt(ctx.NAT(0).getText());
        int nbColumns = Integer.parseInt(ctx.NAT(1).getText());
        this.map = new String[nbLines][nbColumns];
        for (int line = 0; line < nbLines; line++) {
            for (int col = 0; col < nbColumns; col++) {
                int index = line * nbColumns + col;
                map[line][col] = ctx.map_char(index).getText();
                System.out.print(map[line][col]);
            }
            System.out.println();
        }
        System.out.println("=== MAP EVAL ===");

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

    public Void visitIfThenInstr(SlipParser.IfThenInstrContext ctx) {
       Boolean guard = (Boolean) ctx.exprD().accept(this);
       if (guard){
           ctx.instruction().forEach(instruction -> instruction.accept(this));
       }
        return null;
    }

    public Void visitIfThenElseInstr(SlipParser.IfThenElseInstrContext ctx) {
        Boolean guard = (Boolean) ctx.exprD().accept(this);
        if (guard){
            ctx.guardedBlock(0).instruction().forEach(instruction -> instruction.accept(this));
        } else {
            ctx.guardedBlock(1).instruction().forEach(instruction -> instruction.accept(this));
        }
        return null;
    }

    // @Override
    // public Void visitFuncDecl(SlipParser.FuncDeclContext ctx){
    //     this.currentScope = this.scopes.get(ctx);

    //     ctx.instBlock().forEach(instruction -> instruction.accept(this));

    //     this.currentScope = this.currentScope.getParentScope();
    //     return null;
    // }

    @Override
    public Void visitDeclaration(SlipParser.DeclarationContext ctx){
        visitChildren(ctx);
        return null;
    }

    @Override
    public String visitArgList(SlipParser.ArgListContext ctx) {
//        for(SlipParser.VarDefContext varDef : ctx.varDef()) {
//            args.add(varDef.ID().get(0).getText());
//        }
//        return String.join(", ", args);
        return null;
    }

    @Override
    public String visitInstBlock(SlipParser.InstBlockContext ctx){
        ctx.instruction().forEach(instruction -> instruction.accept(this));
        return null;
    }

//    public String visitDeclaration(SlipParser.DeclarationContext ctx) {
//        return visitChildren(ctx);
//    }

    private List<Integer> findIndexes(SlipParser.ExprGContext ctx){
        List<Integer> indexes = ctx.getRuleContexts(SlipParser.ExprDContext.class)// get all "index expressions"
                .stream()
                .map(nbContext -> (Integer) nbContext.accept(this))
                .collect(Collectors.toList());
        return indexes;
    }

    public Void visitAssignInstr(SlipParser.AssignInstrContext ctx) {
        String varName = ctx.exprG().getToken(SlipParser.ID, 0).getText();
        SlipSymbol symbol = this.currentScope.resolve(varName);

        if (symbol != null) {
            if (ctx.exprD() != null) {
                Object value = visit(ctx.exprD());
                if (!symbol.isArray()) {
                    SlipVariableSymbol varSymbol = (SlipVariableSymbol) symbol;
                    varSymbol.setValue(value);
                    System.out.println(String.format("ASSIGNATION SYMBOL:  %s VALUE: %s", varSymbol, varSymbol.getValue()));
                } else {
                    SlipArraySymbol arraySymbol = (SlipArraySymbol) symbol;

                    List<Integer> indexes = this.findIndexes(ctx.exprG());
                    arraySymbol.setValue(indexes, value);
                    System.out.println(String.format("ASSIGNATION SYMBOL:  %s%s VALUE: %s", varName, indexes, value));
                }
            }
        } else {
            System.out.print("error no var with name " +  varName);
            System.out.println("in scope " +  this.currentScope.getName());
        }
        return null;
    }

    @Override
    public String visitString(SlipParser.StringContext ctx){
        return ctx.STRING().getText();
    }
    @Override
    public Character visitChar(SlipParser.CharContext ctx){
        return ctx.CHAR().getText().charAt(1);
    }

    @Override
    public Integer visitIntExpr(SlipParser.IntExprContext ctx){
        System.out.println("in visit int" +  Integer.parseInt(ctx.number().getText()));
        return Integer.parseInt(ctx.number().getText());
    }
    @Override
    public Object visitParens(SlipParser.ParensContext ctx){
        return ctx.exprD().accept(this);
    }


    @Override
    public Integer visitUnaryMinusExpr(SlipParser.UnaryMinusExprContext ctx){
        return - (Integer) ctx.exprD().accept(this);
    }

    @Override
    public Integer visitTimesDivideExpr(SlipParser.TimesDivideExprContext ctx){
        Integer left = (Integer) ctx.exprD(0).accept(this);
        Integer right = (Integer) ctx.exprD(1).accept(this);
        switch (ctx.op.getType()) {
            case SlipParser.TIMES:
                return left * right;
            case SlipParser.DIVIDE:
                // TODO CATCH DIVIDE BY 0
                return left / right;
            case SlipParser.MODULO:
                return left % right;
            default:
                return null;
        }
    }

    @Override
    public Object visitExprGExpr(SlipParser.ExprGExprContext ctx){
        System.out.println("in ExprGexpr" + ctx.exprG().accept(this));
        return ctx.exprG().accept(this);
//        return ctx.exprG().accept(this);
    }

    @Override
    public Integer visitPlusMinusExpr(SlipParser.PlusMinusExprContext ctx){
        Integer left = (Integer) ctx.exprD(0).accept(this);
        Integer right = (Integer) ctx.exprD(1).accept(this);

        System.out.println(String.format("LEFT: %S RIGHT: %s ",left, right));
        if (ctx.op.getType() == SlipParser.PLUS) return  left + right;
        else return left - right;
    }

    public Boolean visitTrueExpr(SlipParser.TrueExprContext ctx){
        return true;
    }
    public Boolean visitFalseExpr(SlipParser.FalseExprContext ctx){
        return false;
    }
    public Boolean visitNotExpr(SlipParser.NotExprContext ctx){
        Boolean toNegateExpr = (Boolean) visit(ctx.exprD());
        if (toNegateExpr != null) {
            return !toNegateExpr;
        }
        else return toNegateExpr;
    }

    public Boolean visitAndOrExpr(SlipParser.AndOrExprContext ctx){
       if (ctx.AND() != null) {
           return (Boolean) visit(ctx.exprD(0)) && (Boolean) visit(ctx.exprD(1));
       }
       else {
           return (Boolean) visit(ctx.exprD(0)) || (Boolean) visit(ctx.exprD(1));
        }
    }

    public Boolean visitComparExpr(SlipParser.ComparExprContext ctx){
        Object left = ctx.exprD(0).accept(this);
        Object right = ctx.exprD(1).accept(this);
        // TODO check if == ok here
        if(ctx.op.getType() == SlipParser.EQUAL) return left == right;
        else return left != right;
    }
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
    public Void visitUntilInstr(SlipParser.UntilInstrContext ctx){
        int iterations = 0;
        do {
            ctx.instruction().forEach(instruction -> instruction.accept(this));
            iterations++;
        } while((Boolean) ctx.exprD().accept(this) && iterations < 1000);

        if (iterations == 1000) {
            signalError(ctx.start, "INFINITE LOOP");
        }

        return null;
    }

    @Override
    public Void visitWhileInstr(SlipParser.WhileInstrContext ctx){
        int iterations = 0;
        while ((Boolean) ctx.exprD().accept(this)  && iterations < 1000) {
            ctx.instruction().forEach(instruction -> instruction.accept(this));
            iterations ++;
        }


        if (iterations == 1000) {
            signalError(ctx.start, "INFINITE LOOP");
        }

        return null;
    }

    @Override
    public Void visitForInstr(SlipParser.ForInstrContext ctx){
        //    FOR ID AFFECT exprD TO exprD DO instruction+ END

        SlipVariableSymbol counter = (SlipVariableSymbol) currentScope.resolve(ctx.ID().getText());
        counter.setValue(ctx.exprD(0).accept(this));
        int iterations = 0;

        while((Boolean) ctx.exprD(1).accept(this) && iterations < 1000) {
            ctx.instruction().forEach(instruction -> instruction.accept(this));
            iterations++;
        }


        if (iterations == 1000) {
            signalError(ctx.start, "INFINITE LOOP");
        }

        return null;
    }

    public Object visitFuncExpr(SlipParser.FuncExprContext ctx) {
        String funcName = ctx.ID().getText();
        SlipMethodSymbol scopedFunc = (SlipMethodSymbol) this.currentScope.resolve(funcName + "_fn");


        // create a function call scope, with all variables cloned from the scope linked to the method declaration
        // we have already collected all functions declarations in the previous phases
        SlipScope funcCallScope = new SlipBaseScope("aname", this.currentScope, scopedFunc);

         // assign each param in the method call scope
         Iterator<SlipVariableSymbol> paramIter = scopedFunc.getParameters(); // get declared params in method definition
         List<SlipParser.ExprDContext> args = ctx.exprD(); // args to the function call
        System.out.println("ARGS LIST SIZE IN FUNCTION CALL : " + ctx.exprD().size());
         int i = 0;
         while (paramIter.hasNext() && i < args.size()){
             // get param from method definition
             SlipVariableSymbol param = paramIter.next();
             System.out.println("PARAM : " + param.getName());
             // assign value from function call to param
             SlipVariableSymbol paramInScope = (SlipVariableSymbol) funcCallScope.resolve(param.getName());
             System.out.println("PARAM IN ScOPE : " + paramInScope.getName() + ctx.exprD(i).getText());

             System.out.println("Get arg value " + ctx.exprD(i).accept(this));

             paramInScope.setValue(ctx.exprD(i).accept(this));
             System.out.println(String.format("set param %s : %s", paramInScope.getName(), paramInScope.getValue()));
             i++;
         }

        // CAUTION DO NOT CHANGE SCOPE TOO SOON OR YOU WON'T BE ABLE TO USE OLD PARAMS in NEW CALL
        // WHEN EMBEDDED CALLS
        this.currentScope = funcCallScope;

        scopedFunc.getBody().forEach(instBlock -> instBlock.accept(this));

        // return method return value (variable with same name as method which should be declared in scope)
        SlipVariableSymbol returnSymbol = (SlipVariableSymbol) this.currentScope.resolve(funcName);

        this.currentScope = this.currentScope.getParentScope();

        // TODO delete funcCallScope

        System.out.println("Return value" + returnSymbol.getValue());
        return returnSymbol.getValue();
    }

    @Override
    public Object visitLeftExprArray(SlipParser.LeftExprArrayContext ctx) {
        System.out.println("in left expression Array" +  ctx.ID().getText());

        SlipArraySymbol array = (SlipArraySymbol) currentScope.resolve(ctx.ID().getText());

        List<Integer> indexes = findIndexes(ctx);
        System.out.println("INDEXES : " + indexes);
        System.out.println("in left expression array" +  array.getValue(indexes));
        return array.getValue(indexes);
    }


    public Object visitLeftExprRecord(SlipParser.LeftExprIDContext ctx) {
        return null;
    }

    @Override
    public Object visitLeftExprID(SlipParser.LeftExprIDContext ctx) {
        System.out.println("in left expression ID" +  ctx.ID().getText());

        // TODO handle struct case
        SlipVariableSymbol variable = (SlipVariableSymbol) currentScope.resolve(ctx.ID().getText());

        System.out.println("in left expression variable" +  variable.getValue());
        return variable.getValue();
    }
    public Void visitVarDecl(SlipParser.VarDeclContext ctx) {
        if (ctx.exprD() != null) {
            Object value = ctx.exprD().accept(this);
            ctx.ID().forEach(var -> {
                SlipVariableSymbol symbol = (SlipVariableSymbol) this.currentScope.resolve(var.getText());
                if (symbol != null) {
                    symbol.setValue(value);
                } else {
                    System.out.print("error no var with name " + var.getText());
                    System.out.println("in scope " + this.currentScope.getName());
                }

                System.out.println(String.format("INITIALIZATION SYMBOL:  %s VALUE: %s", symbol, symbol.getValue()));
            });
        }
        return null;
    }

    @Override
    public Void visitLeftAction(SlipParser.LeftActionContext ctx){
        int arg;
        if (ctx.exprD() != null) {
            arg = (Integer) ctx.exprD().accept(this);
        } else {
            arg = 1;
        }
        this.compiler.addAction(NbcCompiler.ActionType.LEFT, arg);
        return null;
    }

    public Void visitRightAction(SlipParser.RightActionContext ctx){
        int arg;
        if (ctx.exprD() != null) {
            arg = (Integer) ctx.exprD().accept(this);
        } else {
            arg = 1;
        }
        this.compiler.addAction(NbcCompiler.ActionType.RIGHT, arg);
        return null;
    }
    public Void visitUpAction(SlipParser.UpActionContext ctx){
        int arg;
        if (ctx.exprD() != null) {
            arg = (Integer) ctx.exprD().accept(this);
        } else {
            arg = 1;
        }
        this.compiler.addAction(NbcCompiler.ActionType.UP, arg);
        return null;
    }
    public Void visitDownAction(SlipParser.DownActionContext ctx){
        int arg;
        if (ctx.exprD() != null) {
            arg = (Integer) ctx.exprD().accept(this);
        } else {
            arg = 1;
        }
        this.compiler.addAction(NbcCompiler.ActionType.DOWN, arg);
        return null;
    }
    public Void visitJumpAction(SlipParser.JumpActionContext ctx){
        this.compiler.addAction(NbcCompiler.ActionType.JUMP);
        return null;
    }

    public Void visitDigAction(SlipParser.DigActionContext ctx){
        this.compiler.addAction(NbcCompiler.ActionType.DIG);
        return null;
    }

    public Void visitFightAction(SlipParser.FightActionContext ctx){
        this.compiler.addAction(NbcCompiler.ActionType.FIGHT);
        return null;
    }

}


