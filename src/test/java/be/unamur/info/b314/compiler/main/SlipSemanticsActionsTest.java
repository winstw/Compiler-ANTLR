
package be.unamur.info.b314.compiler.main;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlipSemanticsActionsTest {

    private static final Logger LOG = LoggerFactory.getLogger(SlipSemanticsActionsTest.class);

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
    public void test_right_integer_var_param_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/actions/ok/right_integer_var_param.slip", testFolder.newFile(), true, "semantics::actions: right_integer_var_param.slip");
    }


    // tests KO
    @Test
    public void test_right_boolean_arg_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/actions/ko/right_boolean_arg.slip", testFolder.newFile(), false, "semantics::actions: right_boolean_arg.slip");
    }


}
