
package be.unamur.info.b314.compiler.main;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlipSyntaxActionsTest {

    private static final Logger LOG = LoggerFactory.getLogger(SlipSyntaxActionsTest.class);

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
    public void test_right_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/actions/ok/right.slip", testFolder.newFile(), true, "syntax::actions: right.slip");
    }


    @Test
    public void test_left_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/actions/ok/left.slip", testFolder.newFile(), true, "syntax::actions: left.slip");
    }


    @Test
    public void test_down_jump_right_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/actions/ok/down_jump_right.slip", testFolder.newFile(), true, "syntax::actions: down_jump_right.slip");
    }


    // tests KO
    @Test
    public void test_fight_arg_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/actions/ko/fight_arg.slip", testFolder.newFile(), false, "syntax::actions: fight_arg.slip");
    }


}