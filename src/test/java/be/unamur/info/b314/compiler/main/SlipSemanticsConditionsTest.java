
package be.unamur.info.b314.compiler.main;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlipSemanticsConditionsTest {

    private static final Logger LOG = LoggerFactory.getLogger(SlipSemanticsConditionsTest.class);

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
    public void test_if_then_else_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/conditions/ok/if_then_else.slip", testFolder.newFile(), true, "semantics::conditions: if_then_else.slip");
    }


    // tests KO
    @Test
    public void test_if_guard_not_boolean_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/conditions/ko/if_guard_not_boolean.slip", testFolder.newFile(), false, "semantics::conditions: if_guard_not_boolean.slip");
    }


    @Test
    public void test_if_no_else_instruction_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/conditions/ko/if_no_else_instruction.slip", testFolder.newFile(), false, "semantics::conditions: if_no_else_instruction.slip");
    }


    @Test
    public void test_if_no_then_instruction_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/conditions/ko/if_no_then_instruction.slip", testFolder.newFile(), false, "semantics::conditions: if_no_then_instruction.slip");
    }


}
