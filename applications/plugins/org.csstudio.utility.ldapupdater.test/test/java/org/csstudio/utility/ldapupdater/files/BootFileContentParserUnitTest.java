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
import java.text.ParseException;
import java.util.Map;

import javax.annotation.Nonnull;

import junit.framework.Assert;

import org.csstudio.domain.desy.net.IpAddress;
import org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes;
import org.csstudio.utility.ldapupdater.model.IOC;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.Sets;

/**
 * Test for {@link BootFileContentParser}.
 *
 * @author bknerr
 * @since 28.04.2011
 */
public class BootFileContentParserUnitTest {

    private static File TEMP_DIR;

    // CHECKSTYLE OFF: VisibilityModifier
    @Rule
    public TemporaryFolder _tempFolder = new TemporaryFolder();
    // CHECKSTYLE ON: VisibilityModifier


    @Before
    public void setup() {
        TEMP_DIR = _tempFolder.newFolder("dir");
    }

    @Test
    public void testValidFile() throws IOException, ParseException {

        createAndWriteFileForIoc(TEMP_DIR, "foo.boot", "  "  + LdapFieldsAndAttributes.ATTR_VAL_IOC_IP_ADDRESS + "  = 1.2.3.4 ");
        createAndWriteFileForIoc(TEMP_DIR, "bar.boot", " # tallest man on earth\n" +
                                 LdapFieldsAndAttributes.ATTR_VAL_IOC_IP_ADDRESS + "=1.1.1.1");

        createAndWriteFileForIoc(TEMP_DIR, "foo.records", "rec1\nrec2\nrec3");
        createAndWriteFileForIoc(TEMP_DIR, "bar.records", "");

        final BootFileContentParser parser = new BootFileContentParser(TEMP_DIR, Sets.newHashSet("foo", "bar"));
        final Map<String, IOC> outMap = parser.getIocMap();

        Assert.assertEquals(2, outMap.size());
        Assert.assertEquals(new IpAddress("1.1.1.1"), outMap.get("bar").getIpAddress());
        Assert.assertEquals(new IpAddress("1.2.3.4"), outMap.get("foo").getIpAddress());

        Assert.assertEquals(3, outMap.get("foo").getRecordSet().size());
        Assert.assertEquals(0, outMap.get("bar").getRecordSet().size());
    }

    @SuppressWarnings("unused")
    @Test(expected=FileNotFoundException.class) // .records file missing
    public void testMissingRecordsFile() throws IOException, ParseException {
        createAndWriteFileForIoc(TEMP_DIR, "xxx", "tralala");

        new BootFileContentParser(TEMP_DIR, Sets.newHashSet("xxx"));
    }

    @SuppressWarnings("unused")
    @Test(expected=FileNotFoundException.class) // .records file missing
    public void testMissingBootFile() throws IOException, ParseException {
        createAndWriteFileForIoc(TEMP_DIR, "xxx.records", "tralala");
        createAndWriteFileForIoc(TEMP_DIR, "xxx", "tralala");

        new BootFileContentParser(TEMP_DIR, Sets.newHashSet("xxx"));
    }

    @SuppressWarnings("unused")
    @Test(expected=ParseException.class) // .records file missing
    public void testInvalidBootFile() throws IOException, ParseException {
        createAndWriteFileForIoc(TEMP_DIR, "xxx.records", "");
        createAndWriteFileForIoc(TEMP_DIR, "xxx.boot", "tralala");

        new BootFileContentParser(TEMP_DIR, Sets.newHashSet("xxx"));
    }

    @Nonnull
    private File createAndWriteFileForIoc(@Nonnull final File dir,
                                          @Nonnull final String fileName,
                                          @Nonnull final String line) throws IOException {
        final File file = new File(dir, fileName);
        final FileWriter writer = new FileWriter(file);
        writer.write(line);
        writer.close();
        return file;
    }
}
