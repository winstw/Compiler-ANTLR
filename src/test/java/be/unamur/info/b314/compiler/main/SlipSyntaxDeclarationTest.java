
package be.unamur.info.b314.compiler.main;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlipSyntaxDeclarationTest {

    private static final Logger LOG = LoggerFactory.getLogger(SlipSyntaxDeclarationTest.class);

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
    public void test_declaration_ok_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/declaration/ok/declaration_ok.slip", testFolder.newFile(), true, "syntax::declaration: declaration_ok.slip");
    }


    // tests KO
    @Test
    public void test_declaration_bool_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/declaration/ko/declaration_bool.slip", testFolder.newFile(), false, "syntax::declaration: declaration_bool.slip");
    }


    @Test
    public void test_declaration_int_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/declaration/ko/declaration_int.slip", testFolder.newFile(), false, "syntax::declaration: declaration_int.slip");
    }


}
