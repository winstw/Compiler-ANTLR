package be.unamur.info.b314.compiler;

import be.unamur.info.b314.compiler.NBCPrinter.NBCCodeTypes;

import java.util.Map;

/**
 * Print PCode for a given tree using provided symbol table and printer. This 
 * class uses ANTLR visitor mechanism.
 */
public class NBCVisitor extends PlayPlusBaseVisitor<Object> {

    private final Map<String, Integer> symTable;

    private final NBCPrinter printer;

    public NBCVisitor(Map<String, Integer> symTable, NBCPrinter printer) {
        this.symTable = symTable;
        this.printer = printer;
    }

    @Override
    public Object visitRoot(PlayPlusParser.RootContext ctx) {
       // printer.printSetStackPointer(symTable.size()); // Reserve space for Syntax.variables
        printer.printComments("Start instructions");
        super.visitRoot(ctx); // Print instructions
        printer.printComments("End instructions");
        printer.printStop(); // Stop execution
        return null;
    }

    @Override
    public Object visitAffectInstr(PlayPlusParser.AffectInstrContext ctx) {
        String var = ctx.ID().getText();
        printer.printLoadAdress(NBCCodeTypes.Int, 0, symTable.get(var)); // Load variable adress
        ctx.expression().accept(this); // Compute expression
     //   printer.printStore(PCodeTypes.Int);
        return null;
    }

    @Override
    public Object visitConstantExpr(PlayPlusParser.ConstantExprContext ctx) {
        int value = Integer.parseInt(ctx.NUMBER().getText()); // Get value
        printer.printLoadConstant(NBCCodeTypes.Int, value); // Load constant value
        return null;
    }

    @Override
    public Object visitVariableExpr(PlayPlusParser.VariableExprContext ctx) {
        String var = ctx.ID().getText();
        printer.printLoad(NBCCodeTypes.Int, 0, symTable.get(var)); // Load value in cell at given adress
        return null;
    }

    @Override
    public Object visitPlusMinusExpr(PlayPlusParser.PlusMinusExprContext ctx) {
        ctx.expression(0).accept(this); // Print left expression
        ctx.expression(1).accept(this); // Print right expression
        if(ctx.PLUS()!= null){
            printer.printAdd(NBCCodeTypes.Int);
        } else {
            printer.printSub(NBCCodeTypes.Int);
        }
        return null;
    }

}
