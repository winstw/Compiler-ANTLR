package be.unamur.info.b314.compiler.main.checking;

import be.unamur.info.b314.compiler.SlipLexer;
import be.unamur.info.b314.compiler.SlipParser;
import be.unamur.info.b314.compiler.exception.SymbolAlreadyDefinedException;
import be.unamur.info.b314.compiler.main.ErrorHandler;
import be.unamur.info.b314.compiler.main.symboltable.*;
import be.unamur.info.b314.compiler.main.symboltable.SlipSymbol.Type;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class GlobalDefinitionPhase extends CheckSlipVisitor {

    private boolean isMap = false;

    public GlobalDefinitionPhase(ErrorHandler e) {
        super(e, new ParseTreeProperty<>());
    }

    public static void main(String[] args) throws IOException {
        File input = new File(System.getProperty("user.dir") + "/src/test/resources/DefPhaseTest.slip");
        SlipLexer lexer = new SlipLexer(new ANTLRInputStream(new FileInputStream(input)));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SlipParser parser = new SlipParser(tokens);
        ErrorHandler errorHandler = new ErrorHandler();
        SlipParser.ProgramContext tree = parser.program();
        GlobalDefinitionPhase visitor = new GlobalDefinitionPhase(errorHandler);
        visitor.visit(tree);
    }

    public boolean isMap() {
        return isMap;
    }

    @Override
    public Type visitProgram(SlipParser.ProgramContext ctx) {

        visitChildren(ctx);

        return null;
    }

    @Override
    public Type visitMap(SlipParser.MapContext ctx) {
        isMap = true;
        MapVisitor mapVisitor = new MapVisitor(this.eh);
        mapVisitor.visit(ctx);

        return null;
    }

    @Override
    public Type visitProg(SlipParser.ProgContext ctx) {
        currentScope = new SlipGlobalScope();
        scopes.put(ctx, currentScope);

        visitChildren(ctx);

        System.out.println(currentScope);
        currentScope = null;

        return null;
    }

    @Override
    public Type visitDeclaration(SlipParser.DeclarationContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Type visitVarDecl(SlipParser.VarDeclContext ctx) {

        defineVariable(ctx);

        return null;
    }

    @Override
    public Type visitStructDecl(SlipParser.StructDeclContext ctx) {

        defineStructure(ctx);

        return null;
    }

    @Override
    public Type visitArrayDecl(SlipParser.ArrayDeclContext ctx) {

        defineArray(ctx);

        return null;
    }

    @Override
    public Type visitConstDecl(SlipParser.ConstDeclContext ctx) {
        System.out.println("IN visitCONST"+ ctx.getChild(0).getText());
        visit(ctx.getChild(1));

        return null;
    }


    @Override
    public Type visitFuncDecl(SlipParser.FuncDeclContext ctx) {
        defineFunction(ctx);
        return null;
    }

    /**
     * @modifies this, System.err
     * @effect add ctx to currentScope if it doesn't contain it, else print an error
     * @return the SlipMethodSymbol added to currentScope
     */
    private void defineFunction(SlipParser.FuncDeclContext ctx) {
        String name = ctx.ID().getText();
        Type type = visit(ctx.funcType());
        SlipMethodSymbol symbol = new SlipMethodSymbol(name + "_fn", type, currentScope);
        symbol.setBody(ctx.instBlock());
        scopes.put(ctx, symbol);

        try {
            currentScope.define(symbol);
            SlipSymbol returnVar = new SlipVariableSymbol(name, type, true);
            symbol.define(returnVar);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (SymbolAlreadyDefinedException e) {
            eh.signalError(ctx.getStart(), "function symbol already exists in " + currentScope.getName() + " scope");
        }

        if (ctx.argList() != null) {
            for (SlipParser.VarDefContext var : ctx.argList().varDef()) {
                for (TerminalNode node : var.ID()) {
                    symbol.addParameter(new SlipVariableSymbol(node.getText(), visit(var.scalar()), true));
                }
            }
        }
    }

    @Override
    public Type visitFuncType(SlipParser.FuncTypeContext ctx) {

        if (ctx.scalar() == null) {
            return Type.VOID;
        } else {
            return visit(ctx.scalar());
        }

    }

    @Override
    public Type visitMainDecl(SlipParser.MainDeclContext ctx) {
        SlipMethodSymbol symbol = new SlipMethodSymbol("main", Type.VOID, currentScope);

        try {
            currentScope.define(symbol);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (SymbolAlreadyDefinedException e) {
            eh.signalError(ctx.getStart(), "main symbol already exists in " + currentScope.getName() + " scope");
        }

        scopes.put(ctx, symbol);

        return symbol.getType();
    }

    @Override
    public Type visitScalar(SlipParser.ScalarContext ctx) {
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

}

