
package be.unamur.info.b314.compiler.main;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlipSyntaxDeclarationTest {

    private static final Logger LOG = LoggerFactory.getLogger(SlipSyntaxDeclarationTest.class);

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
    public void test_DeclarationRecord_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/declaration/ok/DeclarationRecord.slip", testFolder.newFile(), true, "syntax::declaration: DeclarationRecord.slip");
    }


    @Test
    public void test_DeclarationEnum_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/declaration/ok/DeclarationEnum.slip", testFolder.newFile(), true, "syntax::declaration: DeclarationEnum.slip");
    }


    @Test
    public void test_DeclarationScalar_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/declaration/ok/DeclarationScalar.slip", testFolder.newFile(), true, "syntax::declaration: DeclarationScalar.slip");
    }


    @Test
    public void test_DeclarationArray_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/declaration/ok/DeclarationArray.slip", testFolder.newFile(), true, "syntax::declaration: DeclarationArray.slip");
    }


    // tests KO
    @Test
    public void test_declaration_int_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/declaration/ko/declaration_int.slip", testFolder.newFile(), false, "syntax::declaration: declaration_int.slip");
    }


    @Test
    public void test_declaration_bool_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/declaration/ko/declaration_bool.slip", testFolder.newFile(), false, "syntax::declaration: declaration_bool.slip");
    }


    @Test
    public void test_DeclarationEnumKO_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/declaration/ko/DeclarationEnumKO.slip", testFolder.newFile(), false, "syntax::declaration: DeclarationEnumKO.slip");
    }


    @Test
    public void test_DeclarationRecordKO_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/declaration/ko/DeclarationRecordKO.slip", testFolder.newFile(), false, "syntax::declaration: DeclarationRecordKO.slip");
    }


}
