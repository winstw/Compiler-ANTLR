package be.unamur.info.b314.compiler.main;

import be.unamur.info.b314.compiler.SlipLexer;
import be.unamur.info.b314.compiler.SlipParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SlipMain {

    public static void main(String[] args) throws IOException {

        File input = new File(System.getProperty("user.dir") + "/src/test/resources/syntax/comments/ok/all_comment_types.slip");
        SlipLexer lexer = new SlipLexer(new ANTLRInputStream(new FileInputStream(input)));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SlipParser parser = new SlipParser(tokens);
        parser.setErrorHandler(new SlipErrorStrategy());

        SlipParser.ProgramContext tree = parser.program();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new TestWalker(), tree);

    }

}
