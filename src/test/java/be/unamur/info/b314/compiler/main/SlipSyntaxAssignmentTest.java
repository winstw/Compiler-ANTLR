
package be.unamur.info.b314.compiler.main;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlipSyntaxAssignmentTest {

    private static final Logger LOG = LoggerFactory.getLogger(SlipSyntaxAssignmentTest.class);

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
    public void test_simple_array_cell_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/assignment/ok/simple_array_cell.slip", testFolder.newFile(), true, "syntax::assignment: simple_array_cell.slip");
    }


    @Test
    public void test_multi_array_cell_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/assignment/ok/multi_array_cell.slip", testFolder.newFile(), true, "syntax::assignment: multi_array_cell.slip");
    }


    @Test
    public void test_char_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/assignment/ok/char.slip", testFolder.newFile(), true, "syntax::assignment: char.slip");
    }


    @Test
    public void test_integer_parens_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/assignment/ok/integer_parens.slip", testFolder.newFile(), true, "syntax::assignment: integer_parens.slip");
    }


    @Test
    public void test_record_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/assignment/ok/record.slip", testFolder.newFile(), true, "syntax::assignment: record.slip");
    }


    @Test
    public void test_boolean_parens_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/assignment/ok/boolean_parens.slip", testFolder.newFile(), true, "syntax::assignment: boolean_parens.slip");
    }


    @Test
    public void test_function_call_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/assignment/ok/function_call.slip", testFolder.newFile(), true, "syntax::assignment: function_call.slip");
    }


    // tests KO
    @Test
    public void test_assign_to_function_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/assignment/ko/assign_to_function.slip", testFolder.newFile(), false, "syntax::assignment: assign_to_function.slip");
    }


    @Test
    public void test_equal_symbol_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/assignment/ko/equal_symbol.slip", testFolder.newFile(), false, "syntax::assignment: equal_symbol.slip");
    }


}
