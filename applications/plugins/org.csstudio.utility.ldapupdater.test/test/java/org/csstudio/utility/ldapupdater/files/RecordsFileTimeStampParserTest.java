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
import java.io.IOException;

import junit.framework.Assert;

import org.csstudio.utility.ldapUpdater.files.RecordsFileTimeStampParser;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test for {@link RecordsFileTimeStampParser}. 
 * 
 * @author bknerr
 * @since 28.04.2011
 */
public class RecordsFileTimeStampParserTest {
    
    @Rule
    public TemporaryFolder _tempFolder = new TemporaryFolder();
    
    public File setup() throws IOException {
        File startDir = _tempFolder.newFolder("startDir");
        new File(startDir, "foo" + RecordsFileTimeStampParser.RECORDS_FILE_SUFFIX).createNewFile();
        new File(startDir, "bar" + RecordsFileTimeStampParser.RECORDS_FILE_SUFFIX).createNewFile();
        new File(startDir, "xxx").createNewFile();
        new File(startDir, "yyy").createNewFile();
        return startDir;
    }
    
    @Test
    public void testParserLevel0() throws IOException {
        
        File startDir = setup();
        
        RecordsFileTimeStampParser parser = new RecordsFileTimeStampParser(startDir, 0);
        Assert.assertEquals(0, parser.getIocFileMap().size());
    }
    
    @Test
    public void testParserLevel1() throws IOException {

        File startDir = setup();

        RecordsFileTimeStampParser parser = new RecordsFileTimeStampParser(startDir, 1);
        Assert.assertEquals(2, parser.getIocFileMap().size());
    }
    
    @Test
    public void testParserLevel2() throws IOException {
        File startDir = setup();

        File dir1 = new File(startDir, "dir1");
        dir1.mkdir();
        new File(dir1, "foo2" + RecordsFileTimeStampParser.RECORDS_FILE_SUFFIX).createNewFile();
        
        RecordsFileTimeStampParser parser = new RecordsFileTimeStampParser(startDir, 2);
        Assert.assertEquals(3, parser.getIocFileMap().size());
    }
}
