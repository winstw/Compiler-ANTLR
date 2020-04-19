
package be.unamur.info.b314.compiler.main;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlipSemanticsFunc_DeclTest {

    private static final Logger LOG = LoggerFactory.getLogger(SlipSemanticsFunc_DeclTest.class);

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
    public void test_param_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/func_decl/ok/param.slip", testFolder.newFile(), true, "semantics::func_decl: param.slip");
    }


    @Test
    public void test_simple_boolean_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/func_decl/ok/simple_boolean.slip", testFolder.newFile(), true, "semantics::func_decl: simple_boolean.slip");
    }


    @Test
    public void test_void_func_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/func_decl/ok/void_func.slip", testFolder.newFile(), true, "semantics::func_decl: void_func.slip");
    }


    // tests KO
    @Test
    public void test_call_without_param_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/func_decl/ko/call_without_param.slip", testFolder.newFile(), false, "semantics::func_decl: call_without_param.slip");
    }


    @Test
    public void test_wrong_return_type_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/func_decl/ko/wrong_return_type.slip", testFolder.newFile(), false, "semantics::func_decl: wrong_return_type.slip");
    }


    @Test
    public void test_boolean_return_integer_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/func_decl/ko/boolean_return_integer.slip", testFolder.newFile(), false, "semantics::func_decl: boolean_return_integer.slip");
    }


    @Test
    public void test_call_with_wrong_param_type_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/func_decl/ko/call_with_wrong_param_type.slip", testFolder.newFile(), false, "semantics::func_decl: call_with_wrong_param_type.slip");
    }


    @Test
    public void test_void_return_integer_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/func_decl/ko/void_return_integer.slip", testFolder.newFile(), false, "semantics::func_decl: void_return_integer.slip");
    }


    @Test
    public void test_wrong_type_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/func_decl/ko/wrong_type.slip", testFolder.newFile(), false, "semantics::func_decl: wrong_type.slip");
    }


}
