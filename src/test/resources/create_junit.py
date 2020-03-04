#!/usr/bin/env python3

import os
import glob

class_name = "Slip{}Test"

pre_txt = """
package be.unamur.info.b314.compiler.main;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class {n} {{

    private static final Logger LOG = LoggerFactory.getLogger({n}.class);

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder(); // Create a temporary folder for outputs deleted after tests

    @Rule
    public TestRule watcher = new TestWatcher() {{ // Prints message on logger before each test
        @Override
        protected void starting(Description description) {{
            LOG.info(String.format("Starting test: %s()...",
                    description.getMethodName()));
        }};
    }};
"""

post_txt = """
}
"""

test_ok_txt = """
    @Test
    public void test_{n}_ok() throws Exception{{
        CompilerTestHelper.launchCompilation("{p}", testFolder.newFile(), true, "{c}");
    }}

"""

test_ko_txt = """
    @Test
    public void test_{n}_ko() throws Exception{{
        CompilerTestHelper.launchCompilation("{p}", testFolder.newFile(), false, "{c}");
    }}

"""

# list test directories
tests_dirs = []
for s in os.listdir():
    if os.path.isdir(s):
        for se in os.listdir(s):
            if os.path.isdir(s + '/' + se):
                tests_dirs.append((s, se))

# add tests
for s,se in tests_dirs:
    print('-->', s, se)
    cname = class_name.format('{}{}'.format(s.title(), se.title()))
    full_txt = pre_txt.format(n=cname)

    # add OK tests
    full_txt += "\n    // tests OK"
    for t in os.listdir(s + '/' + se + '/ok'):
        if t[-5:] != '.slip':
            continue

        full_txt += test_ok_txt.format(n=t[:-5], p='/{}/{}/ok/{}'.format(s, se, t), c='{}::{}: {}'.format(s, se, t))

    # add KO tests
    full_txt += "\n    // tests KO"
    for t in os.listdir(s + '/' + se + '/ko'):
        if t[-5:] != '.slip':
            continue

        full_txt += test_ko_txt.format(n=t[:-5], p='/{}/{}/ko/{}'.format(s, se, t), c='{}::{}: {}'.format(s, se, t))

    full_txt += post_txt

    with open('../java/be/unamur/info/b314/compiler/main/{}.java'.format(cname), 'w') as f:
        f.write(full_txt)



