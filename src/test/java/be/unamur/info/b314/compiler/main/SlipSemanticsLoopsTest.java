
package be.unamur.info.b314.compiler.main;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlipSemanticsLoopsTest {

    private static final Logger LOG = LoggerFactory.getLogger(SlipSemanticsLoopsTest.class);

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
    public void test_nested_loops_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/loops/ok/nested_loops.slip", testFolder.newFile(), true, "semantics::loops: nested_loops.slip");
    }


    // tests KO
    @Test
    public void test_while_wrong_guard_type_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/loops/ko/while_wrong_guard_type.slip", testFolder.newFile(), false, "semantics::loops: while_wrong_guard_type.slip");
    }


    @Test
    public void test_repeat_no_boolean_until_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/loops/ko/repeat_no_boolean_until.slip", testFolder.newFile(), false, "semantics::loops: repeat_no_boolean_until.slip");
    }


}
