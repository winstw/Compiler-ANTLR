package be.unamur.info.b314.compiler.main.checking;

import be.unamur.info.b314.compiler.SlipParser;

public class MapVisitor extends CheckSlipVisitor<Boolean> {

    public MapVisitor(ErrorHandler e) {
        super(e);
    }

    public Boolean visitMap(SlipParser.MapContext ctx) {
        System.out.println("=== MAP CHECK START ===");
        int nbLines = Integer.parseInt(ctx.NAT(0).getText());
        int nbColumns = Integer.parseInt(ctx.NAT(1).getText());
        int requiredNbChar = nbLines * nbColumns;
        int actualNbChar = ctx.map_char().size();
        boolean isValidMap = checkEqual(actualNbChar, requiredNbChar, ctx.map_char(actualNbChar - 1).start, String.format("Not enough characters in map : %d, expected %d", actualNbChar, requiredNbChar));
        System.out.println("=== MAP CHECK STOP ===");
        return isValidMap;
    }
}
