
package be.unamur.info.b314.compiler.main;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlipSyntaxImportTest {

    private static final Logger LOG = LoggerFactory.getLogger(SlipSyntaxImportTest.class);

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
    public void test_import_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/import/ok/import.slip", testFolder.newFile(), true, "syntax::import: import.slip");
    }


    // tests KO
    @Test
    public void test_double_import_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/import/ko/double_import.slip", testFolder.newFile(), false, "syntax::import: double_import.slip");
    }


}
