
package be.unamur.info.b314.compiler.main;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlipSemanticsDeclarationTest {

    private static final Logger LOG = LoggerFactory.getLogger(SlipSemanticsDeclarationTest.class);

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
    public void test_boolean_decl_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/declaration/ok/boolean_decl.slip", testFolder.newFile(), true, "semantics::declaration: boolean_decl.slip");
    }


    @Test
    public void test_const_init_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/declaration/ok/const_init.slip", testFolder.newFile(), true, "semantics::declaration: const_init.slip");
    }


    // tests KO
    @Test
    public void test_variable_symbol_already_used_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/declaration/ko/variable_symbol_already_used.slip", testFolder.newFile(), false, "semantics::declaration: variable_symbol_already_used.slip");
    }


    @Test
    public void test_const_without_init_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/declaration/ko/const_without_init.slip", testFolder.newFile(), false, "semantics::declaration: const_without_init.slip");
    }


    @Test
    public void test_boolean_integer_init_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/declaration/ko/boolean_integer_init.slip", testFolder.newFile(), false, "semantics::declaration: boolean_integer_init.slip");
    }


    @Test
    public void test_function_symbol_already_used_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/declaration/ko/function_symbol_already_used.slip", testFolder.newFile(), false, "semantics::declaration: function_symbol_already_used.slip");
    }


}
