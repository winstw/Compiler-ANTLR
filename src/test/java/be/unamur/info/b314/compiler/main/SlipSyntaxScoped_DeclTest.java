
package be.unamur.info.b314.compiler.main;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlipSyntaxScoped_DeclTest {

    private static final Logger LOG = LoggerFactory.getLogger(SlipSyntaxScoped_DeclTest.class);

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
    public void test_var_in_fun_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/scoped_decl/ok/var_in_fun.slip", testFolder.newFile(), true, "syntax::scoped_decl: var_in_fun.slip");
    }


    // tests KO
    @Test
    public void test_var_end_fun_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/scoped_decl/ko/var_end_fun.slip", testFolder.newFile(), false, "syntax::scoped_decl: var_end_fun.slip");
    }


}
