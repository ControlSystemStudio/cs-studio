/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.domain.common.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test for {@link FileCopy}.
 *
 * @author bknerr
 * @since 30.05.2011
 */
public class FileCopyUnitTest {

    private static final String CONTENT = "Test content for temp files:\nwith\nfour\nlines\n";

    // CHECKSTYLE OFF: |
    @Rule
    public TemporaryFolder _testFolder = new TemporaryFolder();
    // CHECKSTYLE ON: |


    @Test
    public void testCopyFileToFile() throws IOException {

        final File tmpSource = _testFolder.newFile("source.test");
        insertContent(tmpSource, CONTENT);
        final File tmpTarget = _testFolder.newFile("target.test");

        FileCopy.copy(tmpSource, tmpTarget);

        Assert.assertTrue(fileContentsMatch(tmpSource, tmpTarget));
    }

    @Test
    public void testCopyFileToDirectory() throws IOException {
        final File tmpSource = _testFolder.newFile("source.test");
        insertContent(tmpSource, CONTENT);
        final File tmpTargetFolder = _testFolder.newFolder("target.folder");

        FileCopy.copy(tmpSource, tmpTargetFolder);

        final File tmpTarget = new File(tmpTargetFolder, "source.test");
        Assert.assertTrue(tmpTarget.exists());
        Assert.assertTrue(fileContentsMatch(tmpSource, tmpTarget));

    }

    @Test
    public void testCopyDirectory() throws IOException {
        final File tmpSource = _testFolder.newFolder("source.folder");

        final File tmpSubFolderSource = new File(tmpSource, "subfolder");
        if(!tmpSubFolderSource.mkdir()) {
            Assert.fail("tmp folder creation failed");
        }

        final File tmpTargetFolder = _testFolder.newFolder("target.folder");


        FileCopy.copy(tmpSource, tmpTargetFolder);


        final File tmpTarget = new File(tmpTargetFolder, "source.folder");
        Assert.assertTrue(tmpTarget.exists());
        final File tmpTargetSubFolder = new File(tmpTarget, "subfolder");
        Assert.assertTrue(tmpTargetSubFolder.exists());

    }

    private void insertContent(final File file,
                               final String content) throws IOException {
        final FileWriter writer = new FileWriter(file, true);
        writer.append(content);
        writer.close();
    }

    private boolean fileContentsMatch(final File first,
                                      final File second) throws IOException {

        final String firstStr = readFileIntoString(first);
        final String secStr = readFileIntoString(second);
        return firstStr.equals(secStr);
    }

    private String readFileIntoString(final File file) throws IOException {
        if (!file.isFile()) {
            throw new IllegalArgumentException("File " + file.getName() + " cannot be read into string (is it a directory?).");
        }

        FileInputStream fin = null;
        BufferedReader myInput = null;
        try {
            fin =  new FileInputStream(file);
            myInput = new BufferedReader(new InputStreamReader(fin));
            final StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = myInput.readLine()) != null) {
                       sb.append(line).append("\n");
            }
            return sb.toString();
        } finally {
            if (fin != null) {
                fin.close();
            }
            if (myInput != null) {
                myInput.close();
            }
        }
    }

}
