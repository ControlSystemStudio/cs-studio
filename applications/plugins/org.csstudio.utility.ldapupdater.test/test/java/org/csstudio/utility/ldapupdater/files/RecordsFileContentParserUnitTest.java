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
package org.csstudio.utility.ldapupdater.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.SortedSet;

import org.csstudio.utility.ldapupdater.model.Record;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test for {@link RecordsFileContentParser}.
 *
 * @author bknerr
 * @since 28.04.2011
 */
public class RecordsFileContentParserUnitTest {
    // CHECKSTYLE OFF: VisibilityModifier
    @Rule
    public TemporaryFolder _tempFolder = new TemporaryFolder();
    // CHECKSTYLE ON: VisibilityModifier

    @Test(expected=FileNotFoundException.class)
    public void testNotExisting() throws IOException {
        RecordsFileContentParser.parse(new File("notExists"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNotAFile() throws IOException {
        final File dir = _tempFolder.newFolder("iAmAFolder");

        RecordsFileContentParser.parse(dir);
    }


    @Test
    public void testEmptyFile() throws IOException {
        final File emptyFile= _tempFolder.newFile("empty");
        final SortedSet<Record> records = RecordsFileContentParser.parse(emptyFile);

        Assert.assertTrue(records.isEmpty());
    }

    @Test
    public void testNonEmptyFile() throws IOException {
        final File nonEmptyFile= _tempFolder.newFile("notEmpty");
        final FileWriter writer = new FileWriter(nonEmptyFile);
        writer.write("b \n");
        writer.write(" c\n");
        writer.write("#lupsch\n");
        writer.write("a\t\n");
        writer.write("\t X \n");
        writer.write("B\n");
        writer.write(" # fupsi\n");
        writer.close();

        final SortedSet<Record> records = RecordsFileContentParser.parse(nonEmptyFile);

        Assert.assertEquals(5, records.size());

        final Iterator<Record> iter = records.iterator();
        Assert.assertEquals("a", iter.next().getName());
        Assert.assertEquals("B", iter.next().getName());
        Assert.assertEquals("b", iter.next().getName());
        Assert.assertEquals("c", iter.next().getName());
        Assert.assertEquals("X", iter.next().getName());
    }
}
