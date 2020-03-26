package be.unamur.info.b314.compiler.main;

import be.unamur.info.b314.compiler.SlipBaseVisitor;
import be.unamur.info.b314.compiler.SlipLexer;
import be.unamur.info.b314.compiler.SlipParser;
import be.unamur.info.b314.compiler.exception.SymbolAlreadyDefinedException;
import be.unamur.info.b314.compiler.symboltable.*;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class DefinitionPhaseVisitor extends SlipBaseVisitor<Integer> {

    public static void main(String[] args) throws IOException {
        File input = new File(System.getProperty("user.dir") + "/src/test/resources/DefPhaseTest.slip");
        SlipLexer lexer = new SlipLexer(new ANTLRInputStream(new FileInputStream(input)));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SlipParser parser = new SlipParser(tokens);
        parser.setErrorHandler(new SlipErrorStrategy());

        SlipParser.ProgramContext tree = parser.program();
        DefinitionPhaseVisitor visitor = new DefinitionPhaseVisitor();
        visitor.visit(tree);
    }

    private ParseTreeProperty<SlipScope> scopes = new ParseTreeProperty<>();
    private SlipScope currentScope;
    private boolean errorOccuried = false;

    @Override
    public Integer visitProgram(SlipParser.ProgramContext ctx) {
        System.out.println("=== START ===");

        if (ctx.prog() != null) {
            visit(ctx.prog());
        }

        System.out.println("=== STOP ===");
        return 1;
    }

    @Override
    public Integer visitProg(SlipParser.ProgContext ctx) {
        currentScope = new SlipGlobalScope();
        scopes.put(ctx, currentScope);

        List<SlipParser.VarDeclContext> vars = ctx.varDecl();
        List<SlipParser.FuncDeclContext> functs = ctx.funcDecl();

        for (SlipParser.VarDeclContext var : vars) {
            visit(var);
        }

        for (SlipParser.FuncDeclContext func : functs) {
            visit(func);
        }

        visit(ctx.mainDecl());

        System.out.println(currentScope);
        currentScope = null;

        return 1;
    }

    @Override
    public Integer visitVarDecl(SlipParser.VarDeclContext ctx) {

        visit(ctx.varDef());

        return 1;
    }

    @Override
    public Integer visitVarDef(SlipParser.VarDefContext ctx) {
        SlipSymbol.Types type = getType(ctx.type().start.getType());

        if (type == SlipSymbol.Types.STRUCT) {
            defineStructure(ctx);
        } else {
            defineVariable(ctx, type);
        }

        return 1;
    }

    /**
     * @modifies this, System.err
     * @effect add ctx to currentScope if it doesn't contain it, else print an error
     * @return the SlipStructureScope added to currentScope
     */
    private void defineStructure(SlipParser.VarDefContext ctx) {

        for (TerminalNode node : ctx.ID()) {
            String name = node.getText();
            SlipStructureSymbol symbol = new SlipStructureSymbol(name, currentScope);

            try {
                currentScope.define(symbol);
                currentScope = symbol;
                visit(ctx.type().structure());
                System.out.println(currentScope);
                currentScope = currentScope.getParentScope();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (SymbolAlreadyDefinedException e) {
                errorOccuried = true;
                printError(node.getSymbol(), String.format("structure symbol \"%s\" already exists in %s scope", name, currentScope.getName()));

            }

        }

    }

    /**
     * @modifies this, System.err
     * @effect add ctx to currentScope if it doesn't contain it, else print an error
     */
    private void defineVariable(SlipParser.VarDefContext ctx, SlipSymbol.Types type) {

        for (TerminalNode node : ctx.ID()) {
            String name = node.getText();
            SlipSymbol symbol = new SlipVariableSymbol(name, type);

            try {
                currentScope.define(symbol);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (SymbolAlreadyDefinedException e) {
                errorOccuried = true;
                printError(node.getSymbol(), String.format("variable symbol \"%s\" already exists in %s scope", name, currentScope.getName()));

            }

            System.out.println(symbol);
        }

    }

    @Override
    public Integer visitStructure(SlipParser.StructureContext ctx) {
        List<SlipParser.VarDeclContext> vars = ctx.varDecl();

        for (SlipParser.VarDeclContext var : vars) {
            visit(var);
        }

        return 1;
    }

    @Override
    public Integer visitFuncDecl(SlipParser.FuncDeclContext ctx) {
        defineFunction(ctx);
        return 1;
    }

    /**
     * @modifies this, System.err
     * @effect add ctx to currentScope if it doesn't contain it, else print an error
     * @return the SlipMethodSymbol added to currentScope
     */
    private void defineFunction(SlipParser.FuncDeclContext ctx) {
        String name = ctx.ID().getText();
        SlipSymbol.Types type = getType(ctx.funcType().start.getType());
        SlipMethodSymbol symbol = new SlipMethodSymbol(name, type, currentScope);

        try {
            currentScope.define(symbol);
            currentScope = symbol;
            scopes.put(ctx, symbol);

            for (SlipParser.VarDefContext var : ctx.argList().varDef()) {
                visit(var);
            }

            for (SlipParser.InstBlockContext instBlock : ctx.instBlock()) {
                for (SlipParser.VarDeclContext var : instBlock.varDecl()) {
                    visit(var);
                }
            }

            System.out.println(currentScope);
            currentScope = currentScope.getParentScope();

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (SymbolAlreadyDefinedException e) {
            errorOccuried = true;
            printError(ctx.getStart(), "function symbol already exists in " + currentScope.getName() + " scope");
        }

    }

    @Override
    public Integer visitMainDecl(SlipParser.MainDeclContext ctx) {
        SlipMethodSymbol symbol = new SlipMethodSymbol("main", SlipSymbol.Types.VOID, currentScope);

        try {
            currentScope.define(symbol);
            currentScope = symbol;
            scopes.put(ctx, symbol);

            System.out.println(currentScope);
            currentScope = currentScope.getParentScope();

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (SymbolAlreadyDefinedException e) {
            errorOccuried = true;
            printError(ctx.getStart(), "function symbol already exists in " + currentScope.getName() + " scope");
        }

        return 1;
    }

    public static SlipSymbol.Types getType(int typeToken) {
        switch (typeToken) {
            case SlipParser.VOIDTYPE: return SlipSymbol.Types.VOID;
            case SlipParser.INTEGERTYPE: return SlipSymbol.Types.INTEGER;
            case SlipParser.CHARTYPE: return SlipSymbol.Types.CHARACTER;
            case SlipParser.BOOLEANTYPE: return SlipSymbol.Types.BOOLEAN;
            default: return SlipSymbol.Types.STRUCT;
        }
    }

    public static void printError(Token t, String msg) {
        System.err.printf("line %d:%d %s\n", t.getLine(), t.getCharPositionInLine(), msg);
    }
}
