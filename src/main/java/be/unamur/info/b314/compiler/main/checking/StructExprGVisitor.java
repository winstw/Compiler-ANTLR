
package be.unamur.info.b314.compiler.main.checking;

import be.unamur.info.b314.compiler.SlipParser;
import be.unamur.info.b314.compiler.exception.SymbolNotFoundException;
import be.unamur.info.b314.compiler.symboltable.SlipScope;
import be.unamur.info.b314.compiler.symboltable.SlipStructureSymbol;
import be.unamur.info.b314.compiler.symboltable.SlipSymbol;
import be.unamur.info.b314.compiler.symboltable.SlipSymbol.Type;
import org.antlr.v4.runtime.tree.ParseTree;

public class StructExprGVisitor extends CheckSlipVisitor<Type> {

    private SlipScope currentScope;

    public StructExprGVisitor(SlipScope currentScope, ErrorHandler e) {
        super(e);
        this.currentScope = currentScope;
    }

    @Override
    public Type visit(ParseTree tree) {
        if (tree instanceof SlipParser.LeftExprIDContext || tree instanceof SlipParser.LeftExprRecordContext) {
            return super.visit(tree);
        } else {
            System.out.println("SOMETHING WENT WRONG BE CAREFUL");
            return Type.VOID;
        }
    }

    @Override
    public Type visitLeftExprID(SlipParser.LeftExprIDContext ctx){
        String idName = ctx.ID().getText();

        try {
            SlipSymbol declaredId = currentScope.resolve(idName);

            if (declaredId.getType() == Type.STRUCT) {
                this.currentScope = (SlipStructureSymbol) declaredId;
            }

            return declaredId.getType();
        } catch (SymbolNotFoundException e){
            signalError(ctx.ID().getSymbol(), String.format("use of undeclared identifier %s", idName));
        }
        return Type.VOID;
    }

    @Override
    public Type visitLeftExprRecord(SlipParser.LeftExprRecordContext ctx){

        Type type = visit(ctx.exprG(0));

        String name= ctx.exprG(1).getToken(SlipParser.ID, 0).getText();
        try {
            SlipSymbol symbol = currentScope.resolve(name);
            type = symbol.getType();
            if (type == Type.STRUCT) {
                currentScope = (SlipStructureSymbol) symbol;
            }

        } catch (SymbolNotFoundException e) {
            signalError(ctx.exprG(1).start, String.format("%s doesn't exist in %s scope", name, currentScope.getName()));
        }

        return type;
    }
}
