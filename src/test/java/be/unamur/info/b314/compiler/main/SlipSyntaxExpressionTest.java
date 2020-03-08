
package be.unamur.info.b314.compiler.main;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlipSyntaxExpressionTest {

    private static final Logger LOG = LoggerFactory.getLogger(SlipSyntaxExpressionTest.class);

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
    public void test_ExpressionIntegerSum_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/expression/ok/ExpressionIntegerSum.slip", testFolder.newFile(), true, "syntax::expression: ExpressionIntegerSum.slip");
    }


    @Test
    public void test_ExpressionBooleanNot_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/expression/ok/ExpressionBooleanNot.slip", testFolder.newFile(), true, "syntax::expression: ExpressionBooleanNot.slip");
    }


    @Test
    public void test_ExpressionBooleanAnd_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/expression/ok/ExpressionBooleanAnd.slip", testFolder.newFile(), true, "syntax::expression: ExpressionBooleanAnd.slip");
    }


    @Test
    public void test_ExpressionBooleanGreater_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/expression/ok/ExpressionBooleanGreater.slip", testFolder.newFile(), true, "syntax::expression: ExpressionBooleanGreater.slip");
    }


    @Test
    public void test_ExpressionBoolean_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/expression/ok/ExpressionBoolean.slip", testFolder.newFile(), true, "syntax::expression: ExpressionBoolean.slip");
    }


    @Test
    public void test_ExpressionBooleanEqual_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/expression/ok/ExpressionBooleanEqual.slip", testFolder.newFile(), true, "syntax::expression: ExpressionBooleanEqual.slip");
    }


    @Test
    public void test_ExpressionIntegerDivide_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/expression/ok/ExpressionIntegerDivide.slip", testFolder.newFile(), true, "syntax::expression: ExpressionIntegerDivide.slip");
    }


    @Test
    public void test_ExpressionBooleanDifferent_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/expression/ok/ExpressionBooleanDifferent.slip", testFolder.newFile(), true, "syntax::expression: ExpressionBooleanDifferent.slip");
    }


    @Test
    public void test_ExpressionBooleanLessEq_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/expression/ok/ExpressionBooleanLessEq.slip", testFolder.newFile(), true, "syntax::expression: ExpressionBooleanLessEq.slip");
    }


    @Test
    public void test_ExpressionIntegerOpposite_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/expression/ok/ExpressionIntegerOpposite.slip", testFolder.newFile(), true, "syntax::expression: ExpressionIntegerOpposite.slip");
    }


    @Test
    public void test_ExpressionInteger_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/expression/ok/ExpressionInteger.slip", testFolder.newFile(), true, "syntax::expression: ExpressionInteger.slip");
    }


    @Test
    public void test_ExpressionIntegerDifference_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/expression/ok/ExpressionIntegerDifference.slip", testFolder.newFile(), true, "syntax::expression: ExpressionIntegerDifference.slip");
    }


    @Test
    public void test_ExpressionIntegerProduct_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/expression/ok/ExpressionIntegerProduct.slip", testFolder.newFile(), true, "syntax::expression: ExpressionIntegerProduct.slip");
    }


    @Test
    public void test_ExpressionBooleanGreaterEq_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/expression/ok/ExpressionBooleanGreaterEq.slip", testFolder.newFile(), true, "syntax::expression: ExpressionBooleanGreaterEq.slip");
    }


    @Test
    public void test_ExpressionBooleanOr_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/expression/ok/ExpressionBooleanOr.slip", testFolder.newFile(), true, "syntax::expression: ExpressionBooleanOr.slip");
    }


    @Test
    public void test_ExpressionArray_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/expression/ok/ExpressionArray.slip", testFolder.newFile(), true, "syntax::expression: ExpressionArray.slip");
    }


    @Test
    public void test_ExpressionChar_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/expression/ok/ExpressionChar.slip", testFolder.newFile(), true, "syntax::expression: ExpressionChar.slip");
    }


    @Test
    public void test_ExpressionBooleanLess_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/expression/ok/ExpressionBooleanLess.slip", testFolder.newFile(), true, "syntax::expression: ExpressionBooleanLess.slip");
    }


    // tests KO
    @Test
    public void test_ExpressionSymbolKO_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/expression/ko/ExpressionSymbolKO.slip", testFolder.newFile(), false, "syntax::expression: ExpressionSymbolKO.slip");
    }


}
