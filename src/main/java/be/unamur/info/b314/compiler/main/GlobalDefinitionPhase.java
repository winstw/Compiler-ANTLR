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

public class GlobalDefinitionPhase extends SlipBaseVisitor<Integer> {

    public static void main(String[] args) throws IOException {
        File input = new File(System.getProperty("user.dir") + "/src/test/resources/DefPhaseTest.slip");
        SlipLexer lexer = new SlipLexer(new ANTLRInputStream(new FileInputStream(input)));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SlipParser parser = new SlipParser(tokens);
        parser.setErrorHandler(new SlipErrorStrategy());

        SlipParser.ProgramContext tree = parser.program();
        GlobalDefinitionPhase visitor = new GlobalDefinitionPhase();
        visitor.visit(tree);
    }

    private ParseTreeProperty<SlipScope> scopes = new ParseTreeProperty<>();
    private SlipScope currentScope;
    private boolean errorOccuried = false;

    public ParseTreeProperty<SlipScope> getScopes() {
        return scopes;
    }

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
        List<SlipParser.ConstDeclContext> consts = ctx.constDecl();

        for (SlipParser.VarDeclContext var : vars) {
            visit(var);
        }

        for (SlipParser.ConstDeclContext constant : consts) {
            visit(constant);
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
     */
    private void defineStructure(SlipParser.VarDefContext ctx) {

        for (TerminalNode node : ctx.ID()) {
            String name = node.getText();
            SlipStructureSymbol symbol = new SlipStructureSymbol(name, currentScope, true);

            try {
                currentScope.define(symbol);
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
            SlipSymbol symbol = new SlipVariableSymbol(name, type, true);

            try {
                currentScope.define(symbol);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (SymbolAlreadyDefinedException e) {
                errorOccuried = true;
                printError(node.getSymbol(), String.format("variable symbol \"%s\" already exists in %s scope", name, currentScope.getName()));

            }

        }

    }

    @Override
    public Integer visitConstDecl(SlipParser.ConstDeclContext ctx) {

        SlipSymbol.Types type = getType(ctx.type().start.getType());

        if (type == SlipSymbol.Types.STRUCT) {
            defineConstantStructure(ctx);
        } else {
            defineConstant(ctx, type);
        }

        return 1;
    }

    /**
     * @modifies this, System.err
     * @effect add ctx to currentScope if it doesn't contain it, else print an error
     */
    private void defineConstantStructure(SlipParser.ConstDeclContext ctx) {

            String name = ctx.ID().getText();
            SlipStructureSymbol symbol = new SlipStructureSymbol(name, currentScope, false);

            try {
                currentScope.define(symbol);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (SymbolAlreadyDefinedException e) {
                errorOccuried = true;
                printError(ctx.ID().getSymbol(), String.format("structure symbol \"%s\" already exists in %s scope", name, currentScope.getName()));

            }

    }

    /**
     * @modifies this, System.err
     * @effect add ctx to currentScope if it doesn't contain it, else print an error
     */
    private void defineConstant(SlipParser.ConstDeclContext ctx, SlipSymbol.Types type) {

        String name = ctx.ID().getText();
        SlipSymbol symbol = new SlipVariableSymbol(name, type, false);

        try {
            currentScope.define(symbol);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (SymbolAlreadyDefinedException e) {
            errorOccuried = true;
            printError(ctx.ID().getSymbol(), String.format("variable symbol \"%s\" already exists in %s scope", name, currentScope.getName()));

        }

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
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (SymbolAlreadyDefinedException e) {
            errorOccuried = true;
            printError(ctx.getStart(), "function symbol already exists in " + currentScope.getName() + " scope");
        }

        for (SlipParser.VarDefContext var : ctx.argList().varDef()) {
            for (TerminalNode node : var.ID()) {
                symbol.addParameter(getType(var.type().start.getType()));
            }
        }

    }

    @Override
    public Integer visitMainDecl(SlipParser.MainDeclContext ctx) {
        SlipMethodSymbol symbol = new SlipMethodSymbol("main", SlipSymbol.Types.VOID, currentScope);

        try {
            currentScope.define(symbol);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (SymbolAlreadyDefinedException e) {
            errorOccuried = true;
            printError(ctx.getStart(), "main symbol already exists in " + currentScope.getName() + " scope");
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
