
package be.unamur.info.b314.compiler.main.checking;

import be.unamur.info.b314.compiler.SlipBaseVisitor;
import be.unamur.info.b314.compiler.SlipParser;
import be.unamur.info.b314.compiler.exception.SymbolNotFoundException;
import be.unamur.info.b314.compiler.symboltable.SlipScope;
import be.unamur.info.b314.compiler.symboltable.SlipStructureSymbol;
import be.unamur.info.b314.compiler.symboltable.SlipSymbol;
import be.unamur.info.b314.compiler.symboltable.SlipSymbol.Type;
import org.antlr.v4.runtime.tree.ParseTree;

public class StructExprGVisitor extends SlipBaseVisitor<SlipSymbol> {

    private SlipScope currentScope;
    private ErrorHandler eh;

    public StructExprGVisitor(SlipScope currentScope, ErrorHandler e) {
        super();
        this.currentScope = currentScope;
        this.eh = e;
    }

    @Override
    public SlipSymbol visit(ParseTree tree) {
        if (tree instanceof SlipParser.LeftExprIDContext || tree instanceof SlipParser.LeftExprRecordContext) {
            return super.visit(tree);
        } else {
            System.out.println("SOMETHING WENT WRONG BE CAREFUL");
            return null;
        }
    }

    @Override
    public SlipSymbol visitLeftExprID(SlipParser.LeftExprIDContext ctx){
        String idName = ctx.ID().getText();

        try {
            SlipSymbol declaredId = currentScope.resolve(idName);

            if (declaredId.getType() == Type.STRUCT) {
                this.currentScope = (SlipStructureSymbol) declaredId;
            }

            return declaredId;
        } catch (SymbolNotFoundException e){
            String errorMessage = String.format("use of undeclared identifier %s", idName);
            eh.signalError(ctx.ID().getSymbol(), errorMessage);
            return null;
        }
    }

    @Override
    public SlipSymbol visitLeftExprRecord(SlipParser.LeftExprRecordContext ctx){

        SlipSymbol leftSymbol = visit(ctx.exprG(0));

        String name= ctx.exprG(1).getToken(SlipParser.ID, 0).getText();
        try {
            SlipSymbol rightSymbol = currentScope.resolve(name);
            Type type = rightSymbol.getType();
            if (type == Type.STRUCT) {
                currentScope = (SlipStructureSymbol) rightSymbol;
            }
            return rightSymbol;
        } catch (SymbolNotFoundException e) {
            String errorMessage = String.format("%s doesn't exist in %s scope", name, currentScope.getName());
            eh.signalError(ctx.exprG(1).start, errorMessage);
            return leftSymbol;
        }
    }
}
