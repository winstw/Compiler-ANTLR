package be.unamur.info.b314.compiler.main.checking;

import be.unamur.info.b314.compiler.SlipBaseVisitor;
import be.unamur.info.b314.compiler.SlipLexer;
import be.unamur.info.b314.compiler.SlipParser;
import be.unamur.info.b314.compiler.exception.SymbolAlreadyDefinedException;
import be.unamur.info.b314.compiler.main.SlipErrorStrategy;
import be.unamur.info.b314.compiler.symboltable.*;
import be.unamur.info.b314.compiler.symboltable.SlipSymbol.Type;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static be.unamur.info.b314.compiler.main.checking.SemanticChecker.getType;
import static be.unamur.info.b314.compiler.main.checking.SemanticChecker.printError;

public class GlobalDefinitionPhase extends SlipBaseVisitor<Type> {

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
    private boolean errorOccurred = false;

    public ParseTreeProperty<SlipScope> getScopes() {
        return scopes;
    }

    public boolean hasErrorOccurred() {
        return errorOccurred;
    }

    @Override
    public Type visitProgram(SlipParser.ProgramContext ctx) {
        System.out.println("=== START ===");

        if (ctx.prog() != null) {
            visit(ctx.prog());
        } else if (ctx.map() != null) {
            MapVisitor mapVisitor = new MapVisitor();
            this.errorOccurred = mapVisitor.visit(ctx.map());
        }

        System.out.println("=== STOP ===");
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

    /**
     * @modifies this, System.err
     * @effect add ctx to currentScope if it doesn't contain it, else print an error
     */
    private void defineVariable(SlipParser.VarDeclContext ctx) {
        Type type = visit(ctx.scalar());

        for (TerminalNode node : ctx.ID()) {
            String name = node.getText();
            SlipSymbol symbol = new SlipVariableSymbol(name, type, true);

            try {
                currentScope.define(symbol);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (SymbolAlreadyDefinedException e) {
                errorOccurred = true;
                printError(node.getSymbol(), String.format("variable symbol \"%s\" already exists in %s scope", name, currentScope.getName()));

            }

        }

    }

    @Override
    public Type visitStructDecl(SlipParser.StructDeclContext ctx) {

        defineStructure(ctx);

        return null;
    }

    /**
     * @modifies this, System.err
     * @effect add ctx to currentScope if it doesn't contain it, else print an error
     */
    private void defineStructure(SlipParser.StructDeclContext ctx) {

        System.out.println("=== START STRUCT DECL ===");

        for (TerminalNode node : ctx.ID()) {
            String name = node.getText();
            SlipStructureSymbol symbol = new SlipStructureSymbol(name, currentScope, true);

            try {
                currentScope.define(symbol);
                currentScope = symbol;

                for (SlipParser.DeclarationContext var : ctx.declaration()) {
                    visit(var);
                }

                System.out.println(currentScope);
                currentScope = currentScope.getParentScope();

            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (SymbolAlreadyDefinedException e) {
                errorOccurred = true;
                printError(node.getSymbol(), String.format("structure symbol \"%s\" already exists in %s scope", name, currentScope.getName()));
            }

        }

        System.out.println("=== END STRUCT DECL ===");

    }

    @Override
    public Type visitArrayDecl(SlipParser.ArrayDeclContext ctx) {

        defineArray(ctx);

        return null;
    }

    /**
     * @modifies this, System.err
     * @effect add ctx to currentScope if it doesn't contain it, else print an error
     */
    private void defineArray(SlipParser.ArrayDeclContext ctx) {

        Type type = visit(ctx.scalar());

        for (TerminalNode node : ctx.ID()) {
            String name = node.getText();
            SlipSymbol symbol = new SlipArraySymbol(name, type, true);

            try {
                currentScope.define(symbol);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (SymbolAlreadyDefinedException e) {
                errorOccurred = true;
                printError(node.getSymbol(), String.format("array symbol \"%s\" already exists in %s scope", name, currentScope.getName()));

            }

        }

    }

    @Override
    public Type visitConstDecl(SlipParser.ConstDeclContext ctx) {

        visit(ctx.getChild(0));

        return null;
    }

    @Override
    public Type visitConstVar(SlipParser.ConstVarContext ctx) {

        defineConstant(ctx);

        return null;
    }

    /**
     * @modifies this, System.err
     * @effect add ctx to currentScope if it doesn't contain it, else print an error
     */
    private void defineConstant(SlipParser.ConstVarContext ctx) {

        Type type = visit(ctx.scalar());
        String name = ctx.ID().getText();
        SlipSymbol symbol = new SlipVariableSymbol(name, type, false);

        try {
            currentScope.define(symbol);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (SymbolAlreadyDefinedException e) {
            errorOccurred = true;
            printError(ctx.ID().getSymbol(), String.format("constant symbol \"%s\" already exists in %s scope", name, currentScope.getName()));
        }

    }

    @Override
    public Type visitConstStruct(SlipParser.ConstStructContext ctx) {

        defineConstantStructure(ctx);

        return null;
    }

    /**
     * @modifies this, System.err
     * @effect add ctx to currentScope if it doesn't contain it, else print an error
     */
    private void defineConstantStructure(SlipParser.ConstStructContext ctx) {

            String name = ctx.ID().getText();
            SlipStructureSymbol symbol = new SlipStructureSymbol(name, currentScope, false);

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
                errorOccurred = true;
                printError(ctx.ID().getSymbol(), String.format("constant structure symbol \"%s\" already exists in %s scope", name, currentScope.getName()));
            }

    }

    @Override
    public Type visitConstArray(SlipParser.ConstArrayContext ctx) {

        defineConstantArray(ctx);

        return null;
    }

    /**
     * @modifies this, System.err
     * @effect add ctx to currentScope if it doesn't contain it, else print an error
     */
    private void defineConstantArray(SlipParser.ConstArrayContext ctx) {

        Type type = visit(ctx.scalar());

        String name = ctx.ID().getText();
        SlipSymbol symbol = new SlipArraySymbol(name, type, true);

        try {
            currentScope.define(symbol);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (SymbolAlreadyDefinedException e) {
            errorOccurred = true;
            printError(ctx.start, String.format("cosntant array symbol \"%s\" already exists in %s scope", name, currentScope.getName()));
        }

    }

    @Override
    public Type visitFuncDecl(SlipParser.FuncDeclContext ctx) {
        defineFunction(ctx);
        return Type.VOID;
    }

    /**
     * @modifies this, System.err
     * @effect add ctx to currentScope if it doesn't contain it, else print an error
     * @return the SlipMethodSymbol added to currentScope
     */
    private void defineFunction(SlipParser.FuncDeclContext ctx) {
        String name = ctx.ID().getText();
        Type type = getType(ctx.funcType().start.getType());
        SlipMethodSymbol symbol = new SlipMethodSymbol(name, type, currentScope);
        scopes.put(ctx, symbol);

        try {
            currentScope.define(symbol);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (SymbolAlreadyDefinedException e) {
            errorOccurred = true;
            printError(ctx.getStart(), "function symbol already exists in " + currentScope.getName() + " scope");
        }

        if (ctx.argList() != null) {
            for (SlipParser.VarDefContext var : ctx.argList().varDef()) {
                for (TerminalNode node : var.ID()) {
                    symbol.addParameter(visit(var.scalar()));
                }
            }
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
            errorOccurred = true;
            printError(ctx.getStart(), "main symbol already exists in " + currentScope.getName() + " scope");
        }

        scopes.put(ctx, symbol);

        return Type.VOID;
    }

    @Override
    public Type visitScalar(SlipParser.ScalarContext ctx) {
        Type type = getType(ctx.start.getType());
        return type;
    }


}
