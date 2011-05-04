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
package org.csstudio.archive.common.service.mysqlimpl.persistengine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.csstudio.archive.common.service.util.DataRescueException;
import org.csstudio.archive.common.service.util.DataRescueResult;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.Lists;

/**
 * TODO (bknerr) :
 *
 * @author bknerr
 * @since 11.04.2011
 */
public class PersistDataToFileRescuerTest {
    private static File RESCUE_DIR;

    private static List<String> STATEMENTS;

    // CHECKSTYLE:OFF
    @Rule
    public TemporaryFolder _folder = new TemporaryFolder();
    // CHECKSTYLE:ON

    @Before
    public void setup() throws IOException {
        RESCUE_DIR = _folder.newFolder("test");

        STATEMENTS = Lists.newArrayList("FIRST",
                                        "SECOND",
                                        "THIRD");
    }
    
    @Test
    public void saveToPathTest() throws DataRescueException, IOException, ClassNotFoundException {
        TimeInstant now = TimeInstantBuilder.fromNow();
    
        DataRescueResult result = PersistDataToFileRescuer.with(STATEMENTS).at(now).to(RESCUE_DIR).rescue();
    
        File infile = new File(result.getFilePath());
        Assert.assertNotNull(infile);
        
        List<String> resultFromFile = readStatementsFromFile(infile);
        
        Assert.assertEquals(STATEMENTS.size(), resultFromFile.size());

        int i = 0;
        for (String stmt : STATEMENTS) {
            Assert.assertEquals(stmt + PersistDataToFileRescuer.SQL_STATEMENT_DELIMITER, resultFromFile.get(i));
            i++;
        }
    }
    
    private List<String> readStatementsFromFile(File infile) throws IOException {
        List<String> result = Lists.newArrayList();
        try {
            BufferedReader in = new BufferedReader(new FileReader(infile));
            String str;
            while ((str = in.readLine()) != null) {
                result.add(str);
            }
            in.close();
        } catch (IOException e) {
            // Ignore
        }
        return result;
    }
}
