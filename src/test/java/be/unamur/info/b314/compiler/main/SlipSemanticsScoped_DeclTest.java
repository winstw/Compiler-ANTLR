
package be.unamur.info.b314.compiler.main;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlipSemanticsScoped_DeclTest {

    private static final Logger LOG = LoggerFactory.getLogger(SlipSemanticsScoped_DeclTest.class);

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
    public void test_mixed_decl_inside_fn_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/scoped_decl/ok/mixed_decl_inside_fn.slip", testFolder.newFile(), true, "semantics::scoped_decl: mixed_decl_inside_fn.slip");
    }


    // tests KO
    @Test
    public void test_scoped_decl_wrong_type_init_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/scoped_decl/ko/scoped_decl_wrong_type_init.slip", testFolder.newFile(), false, "semantics::scoped_decl: scoped_decl_wrong_type_init.slip");
    }


}
