
package be.unamur.info.b314.compiler.main;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlipSyntaxImportTest {

    private static final Logger LOG = LoggerFactory.getLogger(SlipSyntaxImportTest.class);

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
    public void test_import_after_comment_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/import/ok/import_after_comment.slip", testFolder.newFile(), true, "syntax::import: import_after_comment.slip");
    }


    @Test
    public void test_import_map_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/import/ok/import_map.slip", testFolder.newFile(), true, "syntax::import: import_map.slip");
    }


    @Test
    public void test_shortest_file_name_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/import/ok/shortest_file_name.slip", testFolder.newFile(), true, "syntax::import: shortest_file_name.slip");
    }


    @Test
    public void test_import_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/import/ok/import.slip", testFolder.newFile(), true, "syntax::import: import.slip");
    }


    @Test
    public void test_shortest_file_name_with_number_ok() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/import/ok/shortest_file_name_with_number.slip", testFolder.newFile(), true, "syntax::import: shortest_file_name_with_number.slip");
    }


    // tests KO
    @Test
    public void test_file_name_starting_with_number_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/import/ko/file_name_starting_with_number.slip", testFolder.newFile(), false, "syntax::import: file_name_starting_with_number.slip");
    }


    @Test
    public void test_invalid_extension_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/import/ko/invalid_extension.slip", testFolder.newFile(), false, "syntax::import: invalid_extension.slip");
    }


    @Test
    public void test_import_after_main_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/import/ko/import_after_main.slip", testFolder.newFile(), false, "syntax::import: import_after_main.slip");
    }


    @Test
    public void test_double_import_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/import/ko/double_import.slip", testFolder.newFile(), false, "syntax::import: double_import.slip");
    }


    @Test
    public void test_missing_hashtag_ko() throws Exception{
        CompilerTestHelper.launchCompilation("/syntax/import/ko/missing_hashtag.slip", testFolder.newFile(), false, "syntax::import: missing_hashtag.slip");
    }


}
