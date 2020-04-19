
package be.unamur.info.b314.compiler.main;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlipSyntaxWhileTest {

    private static final Logger LOG = LoggerFactory.getLogger(SlipSyntaxWhileTest.class);

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
    public void test_simple_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/while/ok/simple.slip", testFolder.newFile(), true, "syntax::while: simple.slip");
    }


    // tests KO
    @Test
    public void test_without_do_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/while/ko/without_do.slip", testFolder.newFile(), false, "syntax::while: without_do.slip");
    }


}
