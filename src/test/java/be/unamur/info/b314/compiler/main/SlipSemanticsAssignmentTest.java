
package be.unamur.info.b314.compiler.main;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlipSemanticsAssignmentTest {

    private static final Logger LOG = LoggerFactory.getLogger(SlipSemanticsAssignmentTest.class);

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
    public void test_integer_function_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/assignment/ok/integer_function.slip", testFolder.newFile(), true, "semantics::assignment: integer_function.slip");
    }


    @Test
    public void test_boolean_literal_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/assignment/ok/boolean_literal.slip", testFolder.newFile(), true, "semantics::assignment: boolean_literal.slip");
    }


    @Test
    public void test_boolean_expr_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/assignment/ok/boolean_expr.slip", testFolder.newFile(), true, "semantics::assignment: boolean_expr.slip");
    }


    @Test
    public void test_integer_variable_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/assignment/ok/integer_variable.slip", testFolder.newFile(), true, "semantics::assignment: integer_variable.slip");
    }


    @Test
    public void test_record_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/assignment/ok/record.slip", testFolder.newFile(), true, "semantics::assignment: record.slip");
    }


    @Test
    public void test_boolean_variable_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/assignment/ok/boolean_variable.slip", testFolder.newFile(), true, "semantics::assignment: boolean_variable.slip");
    }


    @Test
    public void test_1D_array_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/assignment/ok/1D_array.slip", testFolder.newFile(), true, "semantics::assignment: 1D_array.slip");
    }


    @Test
    public void test_2D_array_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/assignment/ok/2D_array.slip", testFolder.newFile(), true, "semantics::assignment: 2D_array.slip");
    }


    @Test
    public void test_integer_expr_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/assignment/ok/integer_expr.slip", testFolder.newFile(), true, "semantics::assignment: integer_expr.slip");
    }


    @Test
    public void test_boolean_function_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/assignment/ok/boolean_function.slip", testFolder.newFile(), true, "semantics::assignment: boolean_function.slip");
    }


    @Test
    public void test_integer_literal_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/assignment/ok/integer_literal.slip", testFolder.newFile(), true, "semantics::assignment: integer_literal.slip");
    }


    @Test
    public void test_char_litteral_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/assignment/ok/char_litteral.slip", testFolder.newFile(), true, "semantics::assignment: char_litteral.slip");
    }


    // tests KO
    @Test
    public void test_char_assign_unknown_char_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/assignment/ko/char_assign_unknown_char.slip", testFolder.newFile(), false, "semantics::assignment: char_assign_unknown_char.slip");
    }


    @Test
    public void test_const_char_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/assignment/ko/const_char.slip", testFolder.newFile(), false, "semantics::assignment: const_char.slip");
    }


    @Test
    public void test_integer_assign_char_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/assignment/ko/integer_assign_char.slip", testFolder.newFile(), false, "semantics::assignment: integer_assign_char.slip");
    }


    @Test
    public void test_bool_assign_string_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/assignment/ko/bool_assign_string.slip", testFolder.newFile(), false, "semantics::assignment: bool_assign_string.slip");
    }


    @Test
    public void test_const_int_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/assignment/ko/const_int.slip", testFolder.newFile(), false, "semantics::assignment: const_int.slip");
    }


    @Test
    public void test_bool_assign_char_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/assignment/ko/bool_assign_char.slip", testFolder.newFile(), false, "semantics::assignment: bool_assign_char.slip");
    }


    @Test
    public void test_integer_assign_bool_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/assignment/ko/integer_assign_bool.slip", testFolder.newFile(), false, "semantics::assignment: integer_assign_bool.slip");
    }


    @Test
    public void test_const_bool_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/assignment/ko/const_bool.slip", testFolder.newFile(), false, "semantics::assignment: const_bool.slip");
    }


    @Test
    public void test_char_assign_bool_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/assignment/ko/char_assign_bool.slip", testFolder.newFile(), false, "semantics::assignment: char_assign_bool.slip");
    }


    @Test
    public void test_record_assign_record_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/assignment/ko/record_assign_record.slip", testFolder.newFile(), false, "semantics::assignment: record_assign_record.slip");
    }


    @Test
    public void test_integer_assign_string_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/assignment/ko/integer_assign_string.slip", testFolder.newFile(), false, "semantics::assignment: integer_assign_string.slip");
    }


    @Test
    public void test_char_assign_int_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/assignment/ko/char_assign_int.slip", testFolder.newFile(), false, "semantics::assignment: char_assign_int.slip");
    }


    @Test
    public void test_bool_assign_int_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/assignment/ko/bool_assign_int.slip", testFolder.newFile(), false, "semantics::assignment: bool_assign_int.slip");
    }


    @Test
    public void test_char_assign_string_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/assignment/ko/char_assign_string.slip", testFolder.newFile(), false, "semantics::assignment: char_assign_string.slip");
    }


    @Test
    public void test_array_assign_array_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/assignment/ko/array_assign_array.slip", testFolder.newFile(), false, "semantics::assignment: array_assign_array.slip");
    }


}
