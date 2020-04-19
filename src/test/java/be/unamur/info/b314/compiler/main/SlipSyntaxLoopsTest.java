
package be.unamur.info.b314.compiler.main;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlipSyntaxLoopsTest {

    private static final Logger LOG = LoggerFactory.getLogger(SlipSyntaxLoopsTest.class);

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
    public void test_for_simple_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/loops/ok/for_simple.slip", testFolder.newFile(), true, "syntax::loops: for_simple.slip");
    }


    @Test
    public void test_while_simple_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/loops/ok/while_simple.slip", testFolder.newFile(), true, "syntax::loops: while_simple.slip");
    }


    @Test
    public void test_repeat_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/loops/ok/repeat.slip", testFolder.newFile(), true, "syntax::loops: repeat.slip");
    }


    // tests KO
    @Test
    public void test_for_no_do_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/loops/ko/for_no_do.slip", testFolder.newFile(), false, "syntax::loops: for_no_do.slip");
    }


    @Test
    public void test_while_no_do_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/loops/ko/while_no_do.slip", testFolder.newFile(), false, "syntax::loops: while_no_do.slip");
    }


    @Test
    public void test_repeat_do_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/loops/ko/repeat_do.slip", testFolder.newFile(), false, "syntax::loops: repeat_do.slip");
    }


    @Test
    public void test_for_not_declared_counter_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/loops/ko/for_not_declared_counter.slip", testFolder.newFile(), false, "syntax::loops: for_not_declared_counter.slip");
    }


}
