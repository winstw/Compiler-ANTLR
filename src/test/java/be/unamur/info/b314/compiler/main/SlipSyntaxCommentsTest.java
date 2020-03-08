
package be.unamur.info.b314.compiler.main;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlipSyntaxCommentsTest {

    private static final Logger LOG = LoggerFactory.getLogger(SlipSyntaxCommentsTest.class);

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); // Create a temporary folder for outputs deleted after tests

    @Rule
    public TestRule watcher = new TestWatcher() { // Prints message on logger before each test
        @Override
        protected void starting(Description description) {
            LOG.info(String.format("Starting test: %s()...",
                    description.getMethodName()));
        };
    };

    // tests OK
    @Test
    public void test_all_comment_types_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/comments/ok/all_comment_types.slip", testFolder.newFile(), true, "syntax::comments: all_comment_types.slip");
    }


    // tests KO
    @Test
    public void test_incomplete_multi_line7_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/comments/ko/incomplete_multi_line7.slip", testFolder.newFile(), false, "syntax::comments: incomplete_multi_line7.slip");
    }


    @Test
    public void test_incomplete_multi_line6_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/comments/ko/incomplete_multi_line6.slip", testFolder.newFile(), false, "syntax::comments: incomplete_multi_line6.slip");
    }


    @Test
    public void test_incomplete_multi_line2_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/comments/ko/incomplete_multi_line2.slip", testFolder.newFile(), false, "syntax::comments: incomplete_multi_line2.slip");
    }


    @Test
    public void test_incomplete_multi_line1_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/comments/ko/incomplete_multi_line1.slip", testFolder.newFile(), false, "syntax::comments: incomplete_multi_line1.slip");
    }


    @Test
    public void test_incomplete_multi_line5_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/comments/ko/incomplete_multi_line5.slip", testFolder.newFile(), false, "syntax::comments: incomplete_multi_line5.slip");
    }


    @Test
    public void test_incomplete_single_line2_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/comments/ko/incomplete_single_line2.slip", testFolder.newFile(), false, "syntax::comments: incomplete_single_line2.slip");
    }


    @Test
    public void test_incomplete_multi_line8_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/comments/ko/incomplete_multi_line8.slip", testFolder.newFile(), false, "syntax::comments: incomplete_multi_line8.slip");
    }


    @Test
    public void test_incomplete_multi_line3_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/comments/ko/incomplete_multi_line3.slip", testFolder.newFile(), false, "syntax::comments: incomplete_multi_line3.slip");
    }


    @Test
    public void test_incomplete_single_line3_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/comments/ko/incomplete_single_line3.slip", testFolder.newFile(), false, "syntax::comments: incomplete_single_line3.slip");
    }


    @Test
    public void test_incomplete_single_line1_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/comments/ko/incomplete_single_line1.slip", testFolder.newFile(), false, "syntax::comments: incomplete_single_line1.slip");
    }


    @Test
    public void test_incomplete_single_line4_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/comments/ko/incomplete_single_line4.slip", testFolder.newFile(), false, "syntax::comments: incomplete_single_line4.slip");
    }


    @Test
    public void test_incomplete_multi_line4_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/comments/ko/incomplete_multi_line4.slip", testFolder.newFile(), false, "syntax::comments: incomplete_multi_line4.slip");
    }


    @Test
    public void test_incomplete_single_line5_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/comments/ko/incomplete_single_line5.slip", testFolder.newFile(), false, "syntax::comments: incomplete_single_line5.slip");
    }


}
