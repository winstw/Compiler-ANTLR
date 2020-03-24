package be.unamur.info.b314.compiler.main;

import be.unamur.info.b314.compiler.SlipBaseListener;
import be.unamur.info.b314.compiler.SlipParser;

public class TestWalker extends SlipBaseListener {

    @Override
    public void enterProgram(SlipParser.ProgramContext ctx) {
        System.out.println("On rentre dans le programme");
    }

    @Override
    public void exitProgram(SlipParser.ProgramContext ctx) {
        System.out.println("On sort du programme!!");
    }
}
