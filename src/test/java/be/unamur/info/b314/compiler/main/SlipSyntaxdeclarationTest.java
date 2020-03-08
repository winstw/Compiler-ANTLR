package be.unamur.info.b314.compiler.main;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlipSyntaxdeclarationTest {

    private static final Logger LOG = LoggerFactory.getLogger(SlipSyntaxdeclarationTest.class);

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); // Create a temporary folder for outputs deleted after tests

    @Rule
    public TestRule watcher = new TestWatcher() { // Prints message on logger before each test
        @Override
        protected void starting(Description description) {
            LOG.info(String.format("Starting test: %s()...",
                    description.getMethodName()));
        }
    ;
    };

    //
    // Serie declaration OK
    //
    @Test
    public void testdeclaration_declaration_ok_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/declaration/ok/declaration_ok.slip", testFolder.newFile(), true, "declaration: declaration_ok");
    }

    //
    // Serie declaration KO
    //
    @Test
    public void testdeclaration_declaration_int_ko() throws Exception {
        CompilerTestHelper.launchCompilation("/syntax/declaration/ko/declaration_int.slip", testFolder.newFile(), false, "declaration: declaration_int");
    }

    @Test
    public void testdeclaration_declaration_bool_ko() throws Exception {
        CompilerTestHelper.launchCompilation("/syntax/declaration/ko/declaration_bool.slip", testFolder.newFile(), false, "declaration: declaration_bool");
    }

}