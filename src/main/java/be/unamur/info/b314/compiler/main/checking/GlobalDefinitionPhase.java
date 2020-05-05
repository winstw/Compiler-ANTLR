package be.unamur.info.b314.compiler.main.checking;

import be.unamur.info.b314.compiler.SlipLexer;
import be.unamur.info.b314.compiler.SlipParser;
import be.unamur.info.b314.compiler.exception.SymbolAlreadyDefinedException;
import be.unamur.info.b314.compiler.symboltable.*;
import be.unamur.info.b314.compiler.symboltable.SlipSymbol.Type;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class GlobalDefinitionPhase extends CheckSlipVisitor<Type> {

    public GlobalDefinitionPhase(ErrorHandler e) {
        super(e);
        this.scopes = new ParseTreeProperty<>();
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

    private SlipScope currentScope;

    @Override
    public Type visitProgram(SlipParser.ProgramContext ctx) {
        System.out.println("=== START ===");

        if (ctx.prog() != null) {
            visit(ctx.prog());
        } else if (ctx.map() != null) {
            MapVisitor mapVisitor = new MapVisitor(this.errorHandler);
            mapVisitor.visit(ctx.map());
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
        boolean isConst = ctx.getParent().getStart().getText().equals("const");

        Type type = visit(ctx.scalar());

        for (TerminalNode node : ctx.ID()) {
            String name = node.getText();
            SlipSymbol symbol = new SlipVariableSymbol(name, type, !isConst);

            try {
                currentScope.define(symbol);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (SymbolAlreadyDefinedException e) {
                signalError(node.getSymbol(), String.format("variable symbol \"%s\" already exists in %s scope", name, currentScope.getName()));
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
        boolean isConst = ctx.getParent().getStart().getText().equals("const");

        System.out.println("=== START STRUCT DECL ===");

        for (TerminalNode node : ctx.ID()) {
            String name = node.getText();
            SlipStructureSymbol symbol = new SlipStructureSymbol(name, currentScope, !isConst);

            try {
                currentScope.define(symbol);
                currentScope = symbol;

                for (SlipParser.DeclarationContext var : ctx.declaration()) {
                    var.accept(this);
                }

                System.out.println(currentScope);
                currentScope = currentScope.getParentScope();

            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (SymbolAlreadyDefinedException e) {
                signalError(node.getSymbol(), String.format("structure symbol \"%s\" already exists in %s scope", name, currentScope.getName()));
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
        boolean isConst = ctx.getParent().getStart().getText().equals("const");
        System.out.println("CONST : " + isConst);
        Type type = visit(ctx.scalar());

        for (TerminalNode node : ctx.ID()) {
            String name = node.getText();
            List<Integer> arraySizes = ctx.number()
                    .stream()
                    .map(numCtx -> Integer.parseInt(numCtx.getText()))
                    .collect(Collectors.toList());
            SlipSymbol symbol = new SlipArraySymbol(name, type, !isConst, arraySizes);

            try {
                currentScope.define(symbol);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (SymbolAlreadyDefinedException e) {
                signalError(node.getSymbol(), String.format("array symbol \"%s\" already exists in %s scope", name, currentScope.getName()));

            }

        }

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
        return Type.VOID;
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
            signalError(ctx.getStart(), "function symbol already exists in " + currentScope.getName() + " scope");
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
            errorHandler.signalError(ctx.getStart(), "main symbol already exists in " + currentScope.getName() + " scope");
        }

        scopes.put(ctx, symbol);

        return Type.VOID;
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

