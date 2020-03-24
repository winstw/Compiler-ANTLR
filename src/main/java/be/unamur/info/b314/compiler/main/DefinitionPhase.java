package be.unamur.info.b314.compiler.main;

import be.unamur.info.b314.compiler.SlipBaseListener;
import be.unamur.info.b314.compiler.SlipLexer;
import be.unamur.info.b314.compiler.SlipParser;
import be.unamur.info.b314.compiler.symboltable.SlipGlobalScope;
import be.unamur.info.b314.compiler.symboltable.SlipScope;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DefinitionPhase extends SlipBaseListener {

    public static void main(String[] args) throws IOException {
        File input = new File(System.getProperty("user.dir") + "/src/test/resources/DefPhaseTest.slip");
        SlipLexer lexer = new SlipLexer(new ANTLRInputStream(new FileInputStream(input)));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SlipParser parser = new SlipParser(tokens);
        parser.setErrorHandler(new SlipErrorStrategy());

        SlipParser.ProgramContext tree = parser.program();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new DefinitionPhase(), tree);
    }

    private SlipScope currentScope;

    @Override
    public void enterProgram(SlipParser.ProgramContext ctx) {
        currentScope = new SlipGlobalScope();
        System.out.println("On rentre dans le programme");
    }

    @Override
    public void exitProgram(SlipParser.ProgramContext ctx) {
        currentScope = null;
        System.out.println("On sort du programme!!");
    }

    @Override
    public void exitVarDef(SlipParser.VarDefContext ctx) {
        defineVariable(ctx);
    }

    private void defineVariable(SlipParser.VarDefContext ctx) {

    }
}
