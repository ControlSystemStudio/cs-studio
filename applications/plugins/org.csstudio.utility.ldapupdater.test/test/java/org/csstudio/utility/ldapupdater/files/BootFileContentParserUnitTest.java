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
import org.csstudio.utility.ldap.model.IOC;
import org.csstudio.utility.ldap.service.util.LdapFieldsAndAttributes;
import org.csstudio.utility.ldapUpdater.files.BootFileContentParser;
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
    
    
    @Rule
    public static TemporaryFolder _tempFolder = new TemporaryFolder();
    
    private static File _tempDir;
    
    @Before
    public void setup() {
        _tempDir = _tempFolder.newFolder("dir");
    }
    
    @Test
    public void testValidFile() throws IOException, ParseException {
        
        createAndWriteFileForIoc(_tempDir, "foo.boot", "  "  + LdapFieldsAndAttributes.ATTR_VAL_IOC_IP_ADDRESS + "  = 1.2.3.4 ");
        createAndWriteFileForIoc(_tempDir, "bar.boot", " # tallest man on earth\n" +
                                 LdapFieldsAndAttributes.ATTR_VAL_IOC_IP_ADDRESS + "=1.1.1.1");

        createAndWriteFileForIoc(_tempDir, "foo.records", "rec1\nrec2\nrec3");
        createAndWriteFileForIoc(_tempDir, "bar.records", "");
                
        BootFileContentParser parser = new BootFileContentParser(_tempDir, Sets.newHashSet("foo", "bar"));
        Map<String, IOC> outMap = parser.getIocMap();
        
        Assert.assertEquals(2, outMap.size());
        Assert.assertEquals(new IpAddress("1.1.1.1"), outMap.get("bar").getIpAddress());
        Assert.assertEquals(new IpAddress("1.2.3.4"), outMap.get("foo").getIpAddress());

        Assert.assertEquals(3, outMap.get("foo").getRecordSet().size());
        Assert.assertEquals(0, outMap.get("bar").getRecordSet().size());
    }
    
    @SuppressWarnings("unused")
    @Test(expected=FileNotFoundException.class) // .records file missing
    public void testMissingRecordsFile() throws IOException, ParseException {
        createAndWriteFileForIoc(_tempDir, "xxx", "tralala");
        
        new BootFileContentParser(_tempDir, Sets.newHashSet("xxx"));
    }

    @SuppressWarnings("unused")
    @Test(expected=FileNotFoundException.class) // .records file missing
    public void testMissingBootFile() throws IOException, ParseException {
        createAndWriteFileForIoc(_tempDir, "xxx.records", "tralala");
        createAndWriteFileForIoc(_tempDir, "xxx", "tralala");
        
        new BootFileContentParser(_tempDir, Sets.newHashSet("xxx"));
    }

    @SuppressWarnings("unused")
    @Test(expected=ParseException.class) // .records file missing
    public void testInvalidBootFile() throws IOException, ParseException {
        createAndWriteFileForIoc(_tempDir, "xxx.records", "");
        createAndWriteFileForIoc(_tempDir, "xxx.boot", "tralala");
        
        new BootFileContentParser(_tempDir, Sets.newHashSet("xxx"));
    }
    
    private File createAndWriteFileForIoc(@Nonnull final File dir,
                                          @Nonnull final String fileName, 
                                          @Nonnull final String line) throws IOException {
        File file = new File(dir, fileName);
        FileWriter writer = new FileWriter(file);
        writer.write(line);
        writer.close();
        return file;
    }
}
