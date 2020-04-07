package be.unamur.info.b314.compiler.main.checking;

import be.unamur.info.b314.compiler.SlipParserBaseVisitor;
import be.unamur.info.b314.compiler.SlipParser;

import static be.unamur.info.b314.compiler.main.checking.SemanticChecker.printError;

public class MapVisitor extends SlipParserBaseVisitor<Integer> {
    public Integer visitMap(SlipParser.MapContext ctx) {
        System.out.println("in map visitor");
        int nbLines = Integer.parseInt(ctx.NAT(0).getText());
        int nbColumns = Integer.parseInt(ctx.NAT(1).getText());
        int actualNbLines = ctx.line().size();
        if (nbLines != actualNbLines) {
            printError(ctx.line(actualNbLines - 1).start, String.format("Not enough lines : %d, expected %d", actualNbLines, nbLines ));
        }
        for (SlipParser.LineContext line: ctx.line()) {
            int actualNbColumns = line.getChildCount() - 1;
            if (actualNbColumns != nbColumns) {
                printError(line.stop, String.format("Wrong number of columns: %d, expected %d", actualNbColumns, nbColumns));
            }
        }

        return 0;
    }
}
