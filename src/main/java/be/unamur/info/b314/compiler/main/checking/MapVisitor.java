package be.unamur.info.b314.compiler.main.checking;

import be.unamur.info.b314.compiler.SlipBaseVisitor;
import be.unamur.info.b314.compiler.SlipParser;
import be.unamur.info.b314.compiler.main.ErrorHandler;

public class MapVisitor extends SlipBaseVisitor<Boolean> {

    private boolean hasRobot = false;
    private boolean hasTreasure = false;
    private boolean hasEnemies = false;
    private ErrorHandler eh;

    public MapVisitor(ErrorHandler e) {
        super();
        this.eh = e;
    }

    @Override
    public Boolean visitMap(SlipParser.MapContext ctx) {
        int nbLines = Integer.parseInt(ctx.NAT(0).getText());
        int nbColumns = Integer.parseInt(ctx.NAT(1).getText());
        int requiredNbChar = nbLines * nbColumns;
        int actualNbChar = ctx.map_char().size();
        boolean isValidMap = eh.checkEqual(actualNbChar, requiredNbChar, ctx.map_char(actualNbChar - 1).start, String.format("Not enough characters in map : %d, expected %d", actualNbChar, requiredNbChar));
        visitChildren(ctx);

        if (!hasTreasure){
            eh.signalError(ctx.start, "map must contain one Treasure!");
        }
        if (!hasRobot){
            eh.signalError(ctx.start, "map must contain one Robot!");
        }
        if (!hasEnemies) {
            eh.signalError(ctx.start, "map must contain at least one enemy!");
        }

        return isValidMap;
    }

    @Override
    public Boolean visitMap_char(SlipParser.Map_charContext ctx) {
        String mapChar = ctx.getText();

        if (mapChar.equals("@")) {
            if (hasRobot) {
                eh.signalError(ctx.start, "Too many Robots, map must contain one!");
            } else {
                hasRobot = true;
            }
        }

        if (mapChar.equals("X")) {
            if (hasTreasure) {
                eh.signalError(ctx.start, "Too many Treasures, map must contain one!");
            } else {
                hasTreasure = true;
            }
        }

        if (mapChar.equals("Q")) {
            hasEnemies = true;
        }

        return null;
    }
}
