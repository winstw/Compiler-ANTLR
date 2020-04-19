
package be.unamur.info.b314.compiler.main;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlipSemanticsMapTest {

    private static final Logger LOG = LoggerFactory.getLogger(SlipSemanticsMapTest.class);

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
    public void test_map_one_line_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/map/ok/map_one_line.slip", testFolder.newFile(), true, "semantics::map: map_one_line.slip");
    }


    // tests KO
    @Test
    public void test_map_wrong_size_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/map/ko/map_wrong_size.slip", testFolder.newFile(), false, "semantics::map: map_wrong_size.slip");
    }


}
