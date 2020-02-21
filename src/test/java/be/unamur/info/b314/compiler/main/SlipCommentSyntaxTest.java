package be.unamur.info.b314.compiler.main;

import be.unamur.info.b314.compiler.SlipLexer;
import be.unamur.info.b314.compiler.SlipParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;

import java.io.*;
import java.net.URISyntaxException;

import static org.junit.Assert.*;


public class SlipCommentSyntaxTest {

    //
    // Serie variables OK
    //
    @Test
    public void testcomments_all_type() throws Exception {
        try {
            runParserProgram("/syntax/comments/ok/all_comment_types.slip");
            assertTrue(true);
        } catch (SlipErrorStrategy.ParserException e) {
            assertTrue(false);
        }
    }

    //
    // Serie variables NOK
    //

    @Test(expected = SlipErrorStrategy.ParserException.class)
    public void testcomments_commenting_main() throws Exception {
        runParserProgram("/syntax/comments/ko/commenting_main");
    }

    @Test(expected = SlipErrorStrategy.ParserException.class)
    public void testcomments_incomplete_single_line1() throws Exception {
        runParserProgram("/syntax/comments/ko/incomplete_single_line1.slip");
    }

    @Test(expected = SlipErrorStrategy.ParserException.class)
    public void testcomments_incomplete_single_line2() throws Exception {
        runParserProgram("/syntax/comments/ko/incomplete_single_line2.slip");
    }

    @Test(expected = SlipErrorStrategy.ParserException.class)
    public void testcomments_incomplete_single_line3() throws Exception {
        runParserProgram("/syntax/comments/ko/incomplete_single_line3.slip");
    }

    @Test(expected = SlipErrorStrategy.ParserException.class)
    public void testcomments_incomplete_single_line4() throws Exception {
        runParserProgram("/syntax/comments/ko/incomplete_single_line4.slip");
    }

    @Test(expected = SlipErrorStrategy.ParserException.class)
    public void testcomments_incomplete_single_line5() throws Exception {
        runParserProgram("/syntax/comments/ko/incomplete_single_line5.slip");
    }

    @Test(expected = SlipErrorStrategy.ParserException.class)
    public void testcomments_incomplete_multi_line1() throws Exception {
        runParserProgram("/syntax/comments/ko/incomplete_multi_line1.slip");
    }

    @Test(expected = SlipErrorStrategy.ParserException.class)
    public void testcomments_incomplete_multi_line2() throws Exception {
        runParserProgram("/syntax/comments/ko/incomplete_multi_line2.slip");
    }

    @Test(expected = SlipErrorStrategy.ParserException.class)
    public void testcomments_incomplete_multi_line3() throws Exception {
        runParserProgram("/syntax/comments/ko/incomplete_multi_line3.slip");
    }

    @Test(expected = SlipErrorStrategy.ParserException.class)
    public void testcomments_incomplete_multi_line4() throws Exception {
        runParserProgram("/syntax/comments/ko/incomplete_multi_line4.slip");
    }

    @Test(expected = SlipErrorStrategy.ParserException.class)
    public void testcomments_incomplete_multi_line5() throws Exception {
        runParserProgram("/syntax/comments/ko/incomplete_multi_line5.slip");
    }

    @Test(expected = SlipErrorStrategy.ParserException.class)
    public void testcomments_incomplete_multi_line6() throws Exception {
        runParserProgram("/syntax/comments/ko/incomplete_multi_line6.slip");
    }

    @Test(expected = SlipErrorStrategy.ParserException.class)
    public void testcomments_incomplete_multi_line7() throws Exception {
        runParserProgram("/syntax/comments/ko/incomplete_multi_line7.slip");
    }

    @Test(expected = SlipErrorStrategy.ParserException.class)
    public void testcomments_incomplete_multi_line8() throws Exception {
        runParserProgram("/syntax/comments/ko/incomplete_multi_line8.slip");
    }


    //
    // Helping method
    //

    public void runParserProgram(String inputPath) throws IOException, URISyntaxException {
        File input = new File(SlipCommentSyntaxTest.class.getResource(inputPath).toURI());
        SlipLexer lexer = new SlipLexer(new ANTLRInputStream(new FileInputStream(input)));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SlipParser parser = new SlipParser(tokens);
        parser.setErrorHandler(new SlipErrorStrategy());

        parser.program();
    }

}
