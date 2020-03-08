
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
    public void test_ExpressionScalar_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/expression/ok/ExpressionScalar.slip", testFolder.newFile(), true, "syntax::expression: ExpressionScalar.slip");
    }


    @Test
    public void test_ExpressionArray_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/expression/ok/ExpressionArray.slip", testFolder.newFile(), true, "syntax::expression: ExpressionArray.slip");
    }


    // tests KO
    @Test
    public void test_ExpressionBooleanKO_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/expression/ko/ExpressionBooleanKO.slip", testFolder.newFile(), false, "syntax::expression: ExpressionBooleanKO.slip");
    }


}
