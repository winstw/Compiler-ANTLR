package be.unamur.info.b314.compiler.main.checking;

import be.unamur.info.b314.compiler.SlipBaseVisitor;
import be.unamur.info.b314.compiler.SlipLexer;
import be.unamur.info.b314.compiler.SlipParser;
import be.unamur.info.b314.compiler.exception.SymbolAlreadyDefinedException;
import be.unamur.info.b314.compiler.exception.SymbolNotFoundException;
import be.unamur.info.b314.compiler.main.SlipErrorStrategy;
import be.unamur.info.b314.compiler.symboltable.*;
import be.unamur.info.b314.compiler.symboltable.SlipSymbol.Types;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;
import be.unamur.info.b314.compiler.symboltable.SlipSymbol;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

public class SecondPassVisitor extends SlipBaseVisitor<Types> {
    public static void main(String[] args) throws IOException {
        File input = new File(System.getProperty("user.dir") + "/src/test/resources/DefPhaseTest.slip");
        SlipLexer lexer = new SlipLexer(new ANTLRInputStream(new FileInputStream(input)));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SlipParser parser = new SlipParser(tokens);
        parser.setErrorHandler(new SlipErrorStrategy());

        SlipParser.ProgramContext tree = parser.program();
        GlobalDefinitionPhase visitor = new GlobalDefinitionPhase();
        visitor.visit(tree);
        SecondPassVisitor second = new SecondPassVisitor(visitor.getScopes());
        second.visitProgram(tree);
    }
    private ParseTreeProperty<SlipScope> scopes;
    private SlipScope currentScope;
    private boolean errorOccuried = false;

    public SecondPassVisitor(ParseTreeProperty<SlipScope> scopes){
        this.scopes = scopes;
    }

    public boolean hasErrorOccuried() {
        return errorOccuried;
    }

    @Override
    public Types visitProgram(SlipParser.ProgramContext ctx) {
        System.out.println("=== START  SECOND PHASE ===");

        if (ctx.prog() != null) {
            visit(ctx.prog());
        }

        System.out.println("=== END SECOND PHASE ===");
        System.out.println(this.currentScope);
        return Types.VOID;
    }

    @Override
    public Types visitProg(SlipParser.ProgContext ctx){
        SlipScope globalScope = scopes.get(ctx);
        this.currentScope = globalScope;
        System.out.println("SCOPE : " + currentScope.getName());
        for(ParseTree child: ctx.children) {
            visit(child);
        }
        return Types.VOID;
    }

    @Override
    public Types visitFuncDecl(SlipParser.FuncDeclContext ctx){
        // !!! scope creation and function inspection
        SlipScope localScope = scopes.get(ctx);
        this.currentScope = localScope;
        System.out.println("SCOPE : " + currentScope.getName());
        for (SlipParser.InstBlockContext inst: ctx.instBlock()){
            visit(inst);
        }
        this.currentScope = localScope.getParentScope();
        System.out.println("SCOPE : " + currentScope.getName());
        return Types.VOID;
    }

    @Override
    public Types visitInstBlock(SlipParser.InstBlockContext ctx){
        for (ParseTree child: ctx.children) {
            visit(child);
        }
        return Types.VOID;
    }

    @Override
    public Types visitFuncExpr(SlipParser.FuncExprContext ctx) {
        try {
            SlipMethodSymbol scopedFunc = (SlipMethodSymbol) this.currentScope.resolve(ctx.ID().getText());
            System.out.println("FUNC CALL : " + scopedFunc + " Type : " + scopedFunc.getType());
            Iterator<Types> declaredParams = scopedFunc.getParameterTypes();
            for (ParseTree param : ctx.exprD()){
                Types effectiveParam = visit(param);
                Types declaredParam = declaredParams.next();
                System.out.println("EFFECTIVE : " + effectiveParam + " DECLARED : " + declaredParam);
                if (effectiveParam != declaredParam){
                    System.out.println("WRONG ARGUMENT FOR " + ctx.ID() + " FUNCTION : " +
                            effectiveParam + " EXPECTED : " + declaredParam);
                }
            }
            return scopedFunc.getType();
        } catch (Exception e) {
            System.out.println("FUNCTION DOES NOT EXIST : " + ctx.ID());
        }
        return Types.VOID;
    }

    @Override
    public Types visitVarDecl(SlipParser.VarDeclContext ctx){
        Types definedVarType = visit(ctx.varDef());
        System.out.println("VAR DECLARATION : " + definedVarType);
        // type check initialisation
        if (ctx.initVar() != null) {
            Types initVarType = visit(ctx.initVar());
            System.out.println("in visitVarDECL, init var : " + initVarType);
            if (initVarType != null) {
                System.out.println("MATCH : " + (initVarType == definedVarType));
            }
        }
        // add new var declarations to the local scope (already done in first pass for global scope)
        if (this.currentScope.getName() != "global") {
            for (TerminalNode id : ctx.varDef().ID()) {
                try {
                    this.currentScope.define(new SlipVariableSymbol(id.getText(), definedVarType, true));
                } catch (SymbolAlreadyDefinedException e) {
                    System.out.println("ALREADY DEFINED VAR : " + id);
                }
            }
        }
        return Types.VOID;
    }

    @Override
    public Types visitVarDef(SlipParser.VarDefContext ctx){
        return visit(ctx.type());
    }

    @Override
    public Types visitScalar(SlipParser.ScalarContext ctx){
        if (ctx.BOOLEANTYPE() != null){
            return Types.BOOLEAN;
        }
        if (ctx.CHARTYPE() != null){
            return Types.CHARACTER;
        }
        else {
            return Types.INTEGER;
        }
    }

    @Override
    public Types visitExprGExpr(SlipParser.ExprGExprContext ctx){
        return visit(ctx.exprG());
    }

    @Override
    public Types visitLeftExprID(SlipParser.LeftExprIDContext ctx){
        String idName = ctx.ID().getText();
        try {
            SlipSymbol declaredId = currentScope.resolve(idName);
            if (declaredId != null ) {
                return declaredId.getType();
            }
        } catch (SymbolNotFoundException e){
            System.out.println("IDENTIFIER NOT FOUND IN SCOPE : " + idName);
        }
        return Types.VOID;
    }
//    @Override
//    public Types visitLeftExprArray(SlipParser.LeftExprArrayContext ctx){
//
//    }
//    @Override
//    public Types visitLeftExprRecord(SlipParser.LeftExprRecordContext ctx){
//        return visit(ctx.ID());
//    }
//

    @Override
    public Types visitParens(SlipParser.ParensContext ctx){
        return visit(ctx.exprD());
    }

    @Override
    public Types visitInitVar(SlipParser.InitVarContext ctx){
        return visit(ctx.exprD());

    }

    @Override
    public Types visitString(SlipParser.StringContext ctx){
        return Types.STRING;
    }

    @Override
    public Types visitIntExpr(SlipParser.IntExprContext ctx){
        return Types.INTEGER;
    }
    @Override
    public Types visitUnaryMinusExpr(SlipParser.UnaryMinusExprContext ctx){
        return Types.INTEGER;
    }
    @Override
    public Types visitTimesDivideExpr(SlipParser.TimesDivideExprContext ctx){
        return Types.INTEGER;
    }
    @Override
    public Types visitPlusMinusExpr(SlipParser.PlusMinusExprContext ctx){
        return Types.INTEGER;
    }

    @Override
    public Types visitTrueExpr(SlipParser.TrueExprContext ctx){
        return Types.BOOLEAN;
    }
    @Override
    public Types visitAndOrExpr(SlipParser.AndOrExprContext ctx){
        return Types.BOOLEAN;
    }
    @Override
    public Types visitFalseExpr(SlipParser.FalseExprContext ctx){
        return Types.BOOLEAN;
    }

    @Override
    public Types visitComparExpr(SlipParser.ComparExprContext ctx){
        return Types.BOOLEAN;
    }
    @Override
    public Types visitNotExpr(SlipParser.NotExprContext ctx){
        if (visit(ctx.exprD()) != Types.BOOLEAN) {
            throw new RuntimeException("not boolean"); // to improve
        }
        return Types.BOOLEAN;
    }


}
