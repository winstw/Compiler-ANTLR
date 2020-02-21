package be.unamur.info.b314.compiler.main;

import be.unamur.info.b314.compiler.SlipLexer;
import be.unamur.info.b314.compiler.SlipParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertTrue;


public class SlipImportSyntaxTest {

    //
    // Serie variables OK
    //

    @Test
    public void testimport() throws Exception {
        try {
            runParserProgram("/syntax/import/ok/import.slip");
            assertTrue(true);
        } catch (SlipErrorStrategy.ParserException e) {
            assertTrue(false);
        }
    }

    //
    // Serie variables NOK
    //

    @Test(expected = SlipErrorStrategy.ParserException.class)
    public void testimport_double_import() throws Exception {
        runParserProgram("/syntax/import/ko/double_import.slip");
    }


    //
    // Helping method
    //

    public void runParserProgram(String inputPath) throws IOException, URISyntaxException {
        File input = new File(SlipImportSyntaxTest.class.getResource(inputPath).toURI());
        SlipLexer lexer = new SlipLexer(new ANTLRInputStream(new FileInputStream(input)));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SlipParser parser = new SlipParser(tokens);
        parser.setErrorHandler(new SlipErrorStrategy());

        parser.program();
    }

}
