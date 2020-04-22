
package be.unamur.info.b314.compiler.main;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlipSemanticsExpressionTest {

    private static final Logger LOG = LoggerFactory.getLogger(SlipSemanticsExpressionTest.class);

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
    public void test_multiplication_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/expression/ok/multiplication.slip", testFolder.newFile(), true, "semantics::expression: multiplication.slip");
    }


    @Test
    public void test_or_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/expression/ok/or.slip", testFolder.newFile(), true, "semantics::expression: or.slip");
    }


    @Test
    public void test_less_than_or_equal_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/expression/ok/less_than_or_equal.slip", testFolder.newFile(), true, "semantics::expression: less_than_or_equal.slip");
    }


    @Test
    public void test_addition_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/expression/ok/addition.slip", testFolder.newFile(), true, "semantics::expression: addition.slip");
    }


    @Test
    public void test_minus_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/expression/ok/minus.slip", testFolder.newFile(), true, "semantics::expression: minus.slip");
    }


    @Test
    public void test_less_than_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/expression/ok/less_than.slip", testFolder.newFile(), true, "semantics::expression: less_than.slip");
    }


    @Test
    public void test_division_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/expression/ok/division.slip", testFolder.newFile(), true, "semantics::expression: division.slip");
    }


    @Test
    public void test_not_equals_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/expression/ok/not_equals.slip", testFolder.newFile(), true, "semantics::expression: not_equals.slip");
    }


    @Test
    public void test_equals_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/expression/ok/equals.slip", testFolder.newFile(), true, "semantics::expression: equals.slip");
    }


    @Test
    public void test_and_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/expression/ok/and.slip", testFolder.newFile(), true, "semantics::expression: and.slip");
    }


    @Test
    public void test_soustraction_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/expression/ok/soustraction.slip", testFolder.newFile(), true, "semantics::expression: soustraction.slip");
    }


    @Test
    public void test_modulo_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/expression/ok/modulo.slip", testFolder.newFile(), true, "semantics::expression: modulo.slip");
    }


    @Test
    public void test_gretear_than_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/expression/ok/gretear_than.slip", testFolder.newFile(), true, "semantics::expression: gretear_than.slip");
    }


    @Test
    public void test_greater_than_or_equal_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/expression/ok/greater_than_or_equal.slip", testFolder.newFile(), true, "semantics::expression: greater_than_or_equal.slip");
    }


    @Test
    public void test_not_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/expression/ok/not.slip", testFolder.newFile(), true, "semantics::expression: not.slip");
    }


    // tests KO
    @Test
    public void test_unknown_variable_call_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/expression/ko/unknown_variable_call.slip", testFolder.newFile(), false, "semantics::expression: unknown_variable_call.slip");
    }


    @Test
    public void test_unknown_function_call_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/semantics/expression/ko/unknown_function_call.slip", testFolder.newFile(), false, "semantics::expression: unknown_function_call.slip");
    }


}
