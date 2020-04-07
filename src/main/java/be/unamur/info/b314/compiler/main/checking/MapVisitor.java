package be.unamur.info.b314.compiler.main.checking;

import be.unamur.info.b314.compiler.SlipBaseVisitor;
import be.unamur.info.b314.compiler.SlipParser;

import static be.unamur.info.b314.compiler.main.checking.SemanticChecker.printError;

public class MapVisitor extends SlipBaseVisitor<Boolean> {
    public Boolean visitMap(SlipParser.MapContext ctx) {
        Boolean errorOccurred = false;
        System.out.println("=== MAP START ===");
        int nbLines = Integer.parseInt(ctx.NAT(0).getText());
        int nbColumns = Integer.parseInt(ctx.NAT(1).getText());
        int requiredNbChar = nbLines * nbColumns;
        int actualNbChar = ctx.map_char().size();
        if (requiredNbChar != actualNbChar) {
            errorOccurred = true;
            printError(ctx.map_char(actualNbChar - 1).start, String.format("Not enough characters in map : %d, expected %d", actualNbChar, requiredNbChar));
        }
        System.out.println("=== MAP STOP ===");
        return errorOccurred;
    }
}
