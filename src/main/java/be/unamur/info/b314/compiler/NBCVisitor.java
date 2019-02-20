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
        /* Here you need to print the instructions  */
        printer.printLoadAdress(var, 0);
        ctx.expression().accept(this); // Compute expression
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
        printer.printLoad(NBCCodeTypes.Int, var);
        /* Here you need to print the variables  */
        return null;
    }

 //   @Override
    public Integer visitPlusMinusExpr(PlayPlusParser.PlusMinusExprContext ctx) {
        String nLeft = ctx.left.getText();
        String nRight = ctx.right.getText();
        String operator = ctx.op.getText();
        PlayPlusParser.AffectInstrContext instr = (PlayPlusParser.AffectInstrContext) ctx.parent;
        String var = instr.ID().getText();
        int value = 0;
        if(operator.equals("+")){
            value = Integer.parseInt(nLeft) + Integer.parseInt(nRight); // Get value
            printer.printAdd(NBCCodeTypes.Int, var, value);
        } else {
            value = Integer.parseInt(nLeft) - Integer.parseInt(nRight); // Get value
            printer.printSub(NBCCodeTypes.Int, var, value);
        }
        return null;
    }

}
