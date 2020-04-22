
package be.unamur.info.b314.compiler.main;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlipSyntaxFunc_DeclTest {

    private static final Logger LOG = LoggerFactory.getLogger(SlipSyntaxFunc_DeclTest.class);

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
    public void test_simple_func_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/func_decl/ok/simple_func.slip", testFolder.newFile(), true, "syntax::func_decl: simple_func.slip");
    }


    @Test
    public void test_fun_name_already_defined_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/func_decl/ok/fun_name_already_defined.slip", testFolder.newFile(), true, "syntax::func_decl: fun_name_already_defined.slip");
    }


    @Test
    public void test_record_in_func_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/func_decl/ok/record_in_func.slip", testFolder.newFile(), true, "syntax::func_decl: record_in_func.slip");
    }


    // tests KO
    @Test
    public void test_no_instruction_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/func_decl/ko/no_instruction.slip", testFolder.newFile(), false, "syntax::func_decl: no_instruction.slip");
    }


    @Test
    public void test_no_return_type_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/func_decl/ko/no_return_type.slip", testFolder.newFile(), false, "syntax::func_decl: no_return_type.slip");
    }


}
