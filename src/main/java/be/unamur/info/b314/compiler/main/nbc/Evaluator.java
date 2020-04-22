package be.unamur.info.b314.compiler.main.nbc;

import be.unamur.info.b314.compiler.SlipBaseVisitor;
import be.unamur.info.b314.compiler.SlipLexer;
import be.unamur.info.b314.compiler.SlipParser;
import be.unamur.info.b314.compiler.exception.SymbolNotFoundException;
import be.unamur.info.b314.compiler.main.checking.CheckPhaseVisitor;
import be.unamur.info.b314.compiler.main.checking.ErrorHandler;
import be.unamur.info.b314.compiler.main.checking.GlobalDefinitionPhase;
import be.unamur.info.b314.compiler.symboltable.*;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.atn.SemanticContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class Evaluator extends SlipBaseVisitor<Object> {
    private ParseTreeProperty<SlipScope> scopes;
    SlipScope currentScope;
    static public void main(String[] args) throws IOException {
        File input = new File(System.getProperty("user.dir") + "/src/test/resources/DefPhaseTest.slip");
        SlipLexer lexer = new SlipLexer(new ANTLRInputStream(new FileInputStream(input)));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SlipParser parser = new SlipParser(tokens);
        SlipParser.ProgramContext tree = parser.program();
        ErrorHandler errorHandler = new ErrorHandler();
        GlobalDefinitionPhase visitor = new GlobalDefinitionPhase(errorHandler);
        visitor.visit(tree);
        CheckPhaseVisitor second = new CheckPhaseVisitor(visitor.getScopes(), errorHandler);
        second.visitProgram(tree);
        Evaluator evaluator = new Evaluator(second.getScopes());
        evaluator.visitProgram(tree);
    }

    String[][] map = null;
    public Evaluator(ParseTreeProperty<SlipScope> scopes){
        this.scopes = scopes;
    }

    @Override
    public Object visitProgram(SlipParser.ProgramContext ctx) {
        if (ctx.prog() != null) {
            ctx.prog().accept(this);
        }
        return null;
    }

    private String[][] createMap(String filename) {
        // populate map field from file
        return new String[1][1];
    }

    @Override
    public Object visitProg(SlipParser.ProgContext ctx){
        this.map = createMap(ctx.impDecl().FILENAME().getText());
        this.currentScope = this.scopes.get(ctx);
//        for (ParseTree child : ctx.children) {
//            visit(child);
//        }
        ctx.mainDecl().accept(this);
        this.currentScope = this.currentScope.getParentScope();
        return null;
    }

    @Override
    public Object visitMainDecl(SlipParser.MainDeclContext ctx) {
        this.currentScope = this.scopes.get(ctx);
        System.out.println(ctx.instruction().size());
        for (SlipParser.InstructionContext inst: ctx.instruction()) {
            System.out.println(inst.getText());
            inst.accept(this);
        }
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

    @Override
    public Void visitFuncDecl(SlipParser.FuncDeclContext ctx){
        this.currentScope = this.scopes.get(ctx);

        ctx.instBlock().forEach(instruction -> instruction.accept(this));

        this.currentScope = this.currentScope.getParentScope();
        return null;
    }

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

    public Void visitAssignInstr(SlipParser.AssignInstrContext ctx) {
        String varName = ctx.exprG().getToken(SlipParser.ID, 0).getText();
        SlipBaseSymbol symbol = (SlipBaseSymbol) this.currentScope.resolve(varName);
        if (symbol != null) {
            if (ctx.exprD() != null) {
                symbol.setValue(visit(ctx.exprD()));
                System.out.println(String.format("ASSIGNATION SYMBOL:  %s VALUE: %s", symbol, symbol.getValue()));
            }
        } else {
            System.out.print("error no var with name " +  varName);
            System.out.println("in scope " +  this.currentScope.getName());
        }
        return null;
    }

    public String visitString(SlipParser.StringContext ctx){
        return ctx.STRING().getText();
    }

    @Override
    public Integer visitIntExpr(SlipParser.IntExprContext ctx){
        System.out.println("in visit int" +  Integer.parseInt(ctx.number().getText()));
        return Integer.parseInt(ctx.number().getText());
    }

    @Override
    public Integer visitUnaryMinusExpr(SlipParser.UnaryMinusExprContext ctx){
        return - (Integer) ctx.exprD().accept(this);
    }

    @Override
    public Integer visitTimesDivideExpr(SlipParser.TimesDivideExprContext ctx){
        Integer left = (Integer) ctx.exprD(0).accept(this);
        Integer right = (Integer) ctx.exprD(0).accept(this);

        if (ctx.TIMES() != null) return  left * right;
        else return left / right;
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

    public Object visitFuncExpr(SlipParser.FuncExprContext ctx) {
        String funcName = ctx.ID().getText();
        SlipMethodSymbol scopedFunc = (SlipMethodSymbol) this.currentScope.resolve(funcName + "_fn");


        // create a function call scope, with all variables cloned from the scope linked to the method declaration
        // we have already collected all functions declarations in the previous phases
        SlipScope funcCallScope = new SlipBaseScope("aname", this.currentScope, scopedFunc);

         // assign each param in the m
         Iterator<SlipBaseSymbol> paramIter = scopedFunc.getParameters(); // get declared params in method definition
         List<SlipParser.ExprDContext> args = ctx.exprD(); // args to the function call
        System.out.println("ARGS LIST SIZE IN FUNCTION CALL : " + ctx.exprD().size());
         int i = 0;
         while (paramIter.hasNext() && i < args.size()){
             // get param from method definition
             SlipBaseSymbol param = paramIter.next();
             System.out.println("PARAM : " + param.getName());
             // assign value from function call to param
             SlipBaseSymbol paramInScope = (SlipBaseSymbol) funcCallScope.resolve(param.getName());
             System.out.println("PARAM IN ScOPE : " + paramInScope.getName() + ctx.exprD(i).getText());

             System.out.println("Get arg value " + ctx.exprD(i).accept(this));

             paramInScope.setValue(ctx.exprD(i).accept(this));
             System.out.println(String.format("set param %s : %s",  paramInScope.getName(),paramInScope.getValue()));
             i++;
         }

        // CAUTION DO NOT CHANGE SCOPE TOO SOON OR YOU WON'T BE ABLE TO USE OLD PARAMS in NEW CALL
        // WHEN EMBEDDED CALLS
        this.currentScope = funcCallScope;

        scopedFunc.getBody().forEach(instBlock -> instBlock.accept(this));

        // return method return value (variable with same name as method which should be declared in scope)
        SlipBaseSymbol returnSymbol = (SlipBaseSymbol) this.currentScope.resolve(funcName);

        this.currentScope = this.currentScope.getParentScope();

        // TODO delete funcCallScope

        System.out.println("Return value" + returnSymbol.getValue());
        return returnSymbol.getValue();
    }

    @Override
    public Object visitLeftExprID(SlipParser.LeftExprIDContext ctx) {
        System.out.println("in left expression ID" +  ctx.ID().getText());

        SlipBaseSymbol variable = (SlipBaseSymbol) currentScope.resolve(ctx.ID().getText());
        System.out.println("in left expression variable" +  variable.getValue());
        return variable.getValue();
    }
    public Void visitVarDecl(SlipParser.VarDeclContext ctx) {
        if (ctx.exprD() != null) {
            Object value = ctx.exprD().accept(this);
            for (ParseTree var : ctx.ID()) {
                SlipBaseSymbol symbol = (SlipBaseSymbol) this.currentScope.resolve(var.getText());
                if (symbol != null) {
                    symbol.setValue(value);
                }
                else {
                   System.out.print("error no var with name " +  var.getText());
                    System.out.println("in scope " +  this.currentScope.getName());
                }

                System.out.println(String.format("INITIALIZATION SYMBOL:  %s VALUE: %s", symbol, symbol.getValue()));
            }
        }
        return null;
    }
//

}


