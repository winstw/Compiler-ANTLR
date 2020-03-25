package be.unamur.info.b314.compiler.main;

import be.unamur.info.b314.compiler.SlipBaseListener;
import be.unamur.info.b314.compiler.SlipLexer;
import be.unamur.info.b314.compiler.SlipParser;
import be.unamur.info.b314.compiler.exception.SymbolAlreadyDefinedException;
import be.unamur.info.b314.compiler.symboltable.*;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DefinitionPhase extends SlipBaseListener {

    public static void main(String[] args) throws IOException {
        File input = new File(System.getProperty("user.dir") + "/src/test/resources/DefPhaseTest.slip");
        SlipLexer lexer = new SlipLexer(new ANTLRInputStream(new FileInputStream(input)));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SlipParser parser = new SlipParser(tokens);
        parser.setErrorHandler(new SlipErrorStrategy());

        SlipParser.ProgramContext tree = parser.program();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new DefinitionPhase(), tree);
    }

    private ParseTreeProperty<SlipScope> scopes = new ParseTreeProperty<>();
    private SlipScope currentScope;
    private boolean errorOccuried = false;

    @Override
    public void enterProgram(SlipParser.ProgramContext ctx) {
        pushScope(ctx, new SlipGlobalScope());
    }

    @Override
    public void exitProgram(SlipParser.ProgramContext ctx) {
        System.out.println(currentScope);
        currentScope = null;
    }

    @Override
    public void enterFuncDecl(SlipParser.FuncDeclContext ctx) {
        SlipScope scope = defineFunction(ctx);
        pushScope(ctx, scope);
    }

    /**
     * @modifies this, System.err
     * @effect add ctx to currentScope if it doesn't contain it, else print an error
     * @return the SlipMethodSymbol added to currentScope
     */
    private SlipMethodSymbol defineFunction(SlipParser.FuncDeclContext ctx) {
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

        return symbol;
    }

    @Override
    public void exitFuncDecl(SlipParser.FuncDeclContext ctx) {
        System.out.println(currentScope);
        popScope();
    }

    @Override
    public void enterVarDef(SlipParser.VarDefContext ctx) {
        SlipSymbol.Types type = getType(ctx.type().start.getType());

        if (type == SlipSymbol.Types.STRUCT) {
            SlipScope scope = defineStructure(ctx);
            pushScope(ctx, scope);
        }

    }

    /**
     * @modifies this, System.err
     * @effect add ctx to currentScope if it doesn't contain it, else print an error
     * @return the SlipStructureScope added to currentScope
     */
    private SlipStructureSymbol defineStructure(SlipParser.VarDefContext ctx) {
        String name = ctx.ID(0).getText();
        SlipStructureSymbol symbol = new SlipStructureSymbol(name, currentScope);

        for (TerminalNode node : ctx.ID()) {

            name = node.getText();
            try {
                currentScope.define(symbol);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (SymbolAlreadyDefinedException e) {
                errorOccuried = true;
                printError(node.getSymbol(), String.format("structure symbol \"%s\" already exists in %s scope", name, currentScope.getName()));

            }

        }

        return symbol;
    }

    @Override
    public void enterMainDecl(SlipParser.MainDeclContext ctx) {

    }

    @Override
    public void exitMainDecl(SlipParser.MainDeclContext ctx) {

    }

    @Override
    public void exitVarDef(SlipParser.VarDefContext ctx) {
        SlipSymbol.Types type = getType(ctx.type().start.getType());

        if (type == SlipSymbol.Types.STRUCT) {
            System.out.println(currentScope);
            popScope();
        } else {
            defineVariable(ctx, type);
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

    /**
     * @modifies: this
     * @effect currentScope_post == scope, add scope to scopes
     */
    private void pushScope(ParserRuleContext ctx, SlipScope scope) {
        currentScope = scope;
        scopes.put(ctx, scope);
    }

    private void popScope() {
        currentScope = currentScope.getParentScope();
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
