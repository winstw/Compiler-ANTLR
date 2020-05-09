package be.unamur.info.b314.compiler.main.checking;

import be.unamur.info.b314.compiler.SlipBaseVisitor;
import be.unamur.info.b314.compiler.SlipParser;
import be.unamur.info.b314.compiler.exception.SymbolAlreadyDefinedException;
import be.unamur.info.b314.compiler.main.ErrorHandler;
import be.unamur.info.b314.compiler.main.symboltable.*;
import be.unamur.info.b314.compiler.main.symboltable.SlipSymbol.Type;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;


public abstract class CheckSlipVisitor extends SlipBaseVisitor<SlipSymbol.Type> {
    protected ErrorHandler eh;
    protected ParseTreeProperty<SlipScope> scopes;
    protected SlipScope currentScope;

    public CheckSlipVisitor(ErrorHandler errorHandler, ParseTreeProperty<SlipScope> scopes) {
        super();
        this.eh = errorHandler;
        this.scopes = scopes;
    }

    public ParseTreeProperty<SlipScope> getScopes() {
        return scopes;
    }

    /**
     * @modifies this, System.err
     * @effect add ctx to currentScope if it doesn't contain it, else print an error
     */
    protected void defineVariable(SlipParser.VarDeclContext ctx) {
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
                String errorMessage = String.format("variable symbol \"%s\" already exists in %s scope", name, currentScope.getName());
                eh.signalError(node.getSymbol(), errorMessage);
            }

        }
    }

    /**
     * @modifies this, System.err
     * @effect add ctx to currentScope if it doesn't contain it, else print an error
     */
    protected void defineArray(SlipParser.ArrayDeclContext ctx) {
        boolean isConst = ctx.getParent().getStart().getText().equals("const");
        Type type = visit(ctx.scalar());

        for (TerminalNode node : ctx.ID()) {
            String name = node.getText();
            SlipSymbol symbol;
            int first_dim = Integer.parseInt(ctx.number(0).getText());

            if (ctx.number(1) == null) {
                symbol = new SlipArraySymbol(name, type, !isConst, first_dim, 1);
            } else {
                int second_dim = Integer.parseInt(ctx.number(1).getText());
                symbol = new SlipArraySymbol(name, type, !isConst, first_dim, second_dim);
            }

            try {
                currentScope.define(symbol);
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (SymbolAlreadyDefinedException e) {
                eh.signalError(node.getSymbol(), String.format("array symbol \"%s\" already exists in %s scope", name, currentScope.getName()));

            }

        }

    }

    /**
     * @modifies this, System.err
     * @effect add ctx to currentScope if it doesn't contain it, else print an error
     */
    protected void defineStructure(SlipParser.StructDeclContext ctx) {
        boolean isConst = ctx.getParent().getStart().getText().equals("const");

        for (TerminalNode node : ctx.ID()) {
            String name = node.getText();
            SlipStructureSymbol symbol = new SlipStructureSymbol(name, currentScope, !isConst);

            try {
                currentScope.define(symbol);
                currentScope = symbol;

                for (SlipParser.DeclarationContext var : ctx.declaration()) {
                    var.accept(this);
                }

                currentScope = currentScope.getParentScope();

            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (SymbolAlreadyDefinedException e) {
                String errorMessage = String.format("structure symbol \"%s\" already exists in %s scope", name, currentScope.getName());
                eh.signalError(node.getSymbol(), errorMessage);
            }

        }

    }

}
