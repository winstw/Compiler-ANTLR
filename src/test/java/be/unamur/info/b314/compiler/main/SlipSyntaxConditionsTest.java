
package be.unamur.info.b314.compiler.main;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlipSyntaxConditionsTest {

    private static final Logger LOG = LoggerFactory.getLogger(SlipSyntaxConditionsTest.class);

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
    public void test_simple_if_then_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/conditions/ok/simple_if_then.slip", testFolder.newFile(), true, "syntax::conditions: simple_if_then.slip");
    }


    @Test
    public void test_simple_if_then_else_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/conditions/ok/simple_if_then_else.slip", testFolder.newFile(), true, "syntax::conditions: simple_if_then_else.slip");
    }


    // tests KO
    @Test
    public void test_if_do_block_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/conditions/ko/if_do_block.slip", testFolder.newFile(), false, "syntax::conditions: if_do_block.slip");
    }


}
