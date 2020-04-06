package be.unamur.info.b314.compiler.main.checking;

import be.unamur.info.b314.compiler.SlipBaseVisitor;
import be.unamur.info.b314.compiler.SlipParser;
import be.unamur.info.b314.compiler.exception.SymbolNotFoundException;
import be.unamur.info.b314.compiler.symboltable.SlipScope;
import be.unamur.info.b314.compiler.symboltable.SlipStructureSymbol;
import be.unamur.info.b314.compiler.symboltable.SlipSymbol;
import be.unamur.info.b314.compiler.symboltable.SlipSymbol.Types;
import org.antlr.v4.runtime.tree.ParseTree;

import static be.unamur.info.b314.compiler.main.checking.SemanticChecker.printError;

public class StructExprGVisitor extends SlipBaseVisitor<Types> {

    private SlipScope currentScope;
    private boolean errorOccurred;

    public StructExprGVisitor(SlipScope currentScope) {
        this.currentScope = currentScope;
        this.errorOccurred = false;
    }

    public boolean hasErrorOccurred() {
        return errorOccurred;
    }

    @Override
    public Types visit(ParseTree tree) {
        if (tree instanceof SlipParser.LeftExprIDContext || tree instanceof SlipParser.LeftExprRecordContext) {
            return super.visit(tree);
        } else {
            System.out.println("SOMETHING WENT WRONG BE CAREFULL");
            return Types.VOID;
        }
    }

    @Override
    public Types visitLeftExprID(SlipParser.LeftExprIDContext ctx){
        String idName = ctx.ID().getText();

        try {
            SlipSymbol declaredId = currentScope.resolve(idName);

            if (declaredId.getType() == SlipSymbol.Types.STRUCT) {
                this.currentScope = (SlipStructureSymbol) declaredId;
            }

            return declaredId.getType();
        } catch (SymbolNotFoundException e){
            errorOccurred = true;
            printError(ctx.ID().getSymbol(), String.format("use of undeclared identifier %s", idName));
        }
        return Types.VOID;
    }

    @Override
    public Types visitLeftExprRecord(SlipParser.LeftExprRecordContext ctx){

        SlipSymbol.Types type = visit(ctx.exprG());

        String name = ctx.ID().getText();
        try {
            SlipSymbol symbol = currentScope.resolve(name);
            type = symbol.getType();
            if (type == SlipSymbol.Types.STRUCT) {
                currentScope = (SlipStructureSymbol) symbol;
            }
            System.out.println(symbol);

        } catch (SymbolNotFoundException e) {
            printError(ctx.ID().getSymbol(), String.format("%s doesn't exist in %s scope", name, currentScope.getName()));
        }

        return type;
    }
}
