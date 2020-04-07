package be.unamur.info.b314.compiler.main.checking;

import be.unamur.info.b314.compiler.SlipParser;
import be.unamur.info.b314.compiler.symboltable.SlipSymbol;
import org.antlr.v4.runtime.Token;

public class SemanticChecker {

    /**
     * @modifies System.err
     * @effect print a message on System.err if semantic is wrong
     * @return true if there is no semantic error, else return false
     */
    public static boolean run(SlipParser.ProgramContext tree) {

        GlobalDefinitionPhase definitionPhase = new GlobalDefinitionPhase();
        definitionPhase.visit(tree);
        SecondPassVisitor checkPhase = new SecondPassVisitor(definitionPhase.getScopes());
        checkPhase.visit(tree);

        return !(definitionPhase.hasErrorOccurred() || checkPhase.hasErrorOccurred());
    }

    public static SlipSymbol.Type getType(int typeToken) {
        switch (typeToken) {
            case SlipParser.VOIDTYPE: return SlipSymbol.Type.VOID;
            case SlipParser.INTEGERTYPE: return SlipSymbol.Type.INTEGER;
            case SlipParser.CHARTYPE: return SlipSymbol.Type.CHARACTER;
            case SlipParser.BOOLEANTYPE: return SlipSymbol.Type.BOOLEAN;
            default: return SlipSymbol.Type.STRUCT;
        }
    }

    public static void printError(Token t, String msg) {
        System.err.printf("line %d:%d %s\n", t.getLine(), t.getCharPositionInLine(), msg);
    }



}
