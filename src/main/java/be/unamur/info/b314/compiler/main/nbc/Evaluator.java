package be.unamur.info.b314.compiler.main.nbc;

import be.unamur.info.b314.compiler.SlipBaseVisitor;
import be.unamur.info.b314.compiler.SlipLexer;
import be.unamur.info.b314.compiler.SlipParser;
import be.unamur.info.b314.compiler.main.checking.CheckPhaseVisitor;
import be.unamur.info.b314.compiler.main.checking.ErrorHandler;
import be.unamur.info.b314.compiler.main.checking.GlobalDefinitionPhase;
import be.unamur.info.b314.compiler.symboltable.SlipBaseSymbol;
import be.unamur.info.b314.compiler.symboltable.SlipMethodSymbol;
import be.unamur.info.b314.compiler.symboltable.SlipScope;
import be.unamur.info.b314.compiler.symboltable.SlipSymbol;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.atn.SemanticContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
            visit(ctx.prog());
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
        for (ParseTree child : ctx.children) {
            visit(child);
        }
        this.currentScope = this.currentScope.getParentScope();
        return null;
    }

    @Override
    public Object visitMainDecl(SlipParser.MainDeclContext ctx) {
        this.currentScope = this.scopes.get(ctx);
        visitChildren(ctx);
        this.currentScope = this.currentScope.getParentScope();
        return null;
    }

    @Override
    public Void visitFuncDecl(SlipParser.FuncDeclContext ctx){
        this.currentScope = this.scopes.get(ctx);

        for (SlipParser.InstBlockContext inst: ctx.instBlock()){
            visit(inst);
        }
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
        for (SlipParser.InstructionContext inst: ctx.instruction()) {
            visit(inst);
        }
        return null;
    }

//    public String visitDeclaration(SlipParser.DeclarationContext ctx) {
//        return visitChildren(ctx);
//    }

    public Void visitAssignInstr(SlipParser.AssignInstrContext ctx) {
        SlipBaseSymbol symbol = (SlipBaseSymbol) this.currentScope.resolve(ctx.exprG().getToken(SlipParser.ID, 0).getText());
        if (ctx.exprD() != null) {
            symbol.setValue(visit(ctx.exprD()));
            System.out.println(String.format("ASSIGNATION SYMBOL:  %s VALUE: %s", symbol, symbol.getValue()));
        }
        return null;
    }

    public String visitString(SlipParser.StringContext ctx){
        return ctx.STRING().getText();
    }
    public String visitIntExpr(SlipParser.IntExprContext ctx){
        return ctx.number().getText();
    }
    public Boolean visitTrueExpr(SlipParser.TrueExprContext ctx){
        return true;
    }
    public Boolean visitFalseExpr(SlipParser.FalseExprContext ctx){
        return false;
    }
    public Boolean visitNotExpr(SlipParser.NotExprContext ctx){
        return ! (Boolean) visit(ctx.exprD());
    }
    public Boolean visitAndOrExpr(SlipParser.AndOrExprContext ctx){
       if (ctx.AND() != null) {
           return (Boolean) visit(ctx.exprD(0)) && (Boolean) visit(ctx.exprD(1));
       }
       else {
           return (Boolean) visit(ctx.exprD(0)) || (Boolean) visit(ctx.exprD(1));
        }
    }

    public Object visitFuncExpr(SlipParser.FuncExprContext ctx) {
        SlipScope method = (SlipMethodSymbol) this.currentScope.resolve(ctx.ID().getText());
        this.currentScope = method;
        return null;
    }

    public Void visitVarDecl(SlipParser.VarDeclContext ctx) {
        if (ctx.exprD() != null) {
            Object value = visit(ctx.exprD());
            for (ParseTree var : ctx.ID()) {
                SlipBaseSymbol symbol = (SlipBaseSymbol) this.currentScope.resolve(var.getText());
                symbol.setValue(value);
                System.out.println(String.format("INITIALIZATION SYMBOL:  %s VALUE: %s", symbol, symbol.getValue()));
            }
        }
        return null;
    }
//

}


