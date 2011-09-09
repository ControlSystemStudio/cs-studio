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
package org.csstudio.utility.epicsDataBaseCompare.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.csstudio.domain.common.resource.CssResourceLocator;
import org.csstudio.domain.common.resource.CssResourceLocator.RepoDomain;
import org.csstudio.utility.epicsDataBaseCompare.Activator;
import org.junit.Test;


/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @since 09.09.2011
 */
public class EpicsDBParserUnitTest {
    @Test
    public void testname() throws Exception {
        final EpicsDBParser epicsDBParser = new EpicsDBParser();
        final String resFilePath =
            CssResourceLocator.composeResourceLocationString(RepoDomain.APPLICATIONS,
                                                             Activator.PLUGIN_ID+".test",
                                                             "res-test/testDBFile.db");
        final EpicsDBFile parseFile = epicsDBParser.parseFile(resFilePath);
        assertEquals(3, parseFile.getRecords().size());
        EpicsRecord record = parseFile.getRecord("22MFG_Wert1_HB_mbboD");
        assertNotNull(record);
        assertEquals("22MFG_Wert1_HB_mbboD", record.getRecordName());
        assertEquals("mbboDirect", record.getRecordType());

        record = parseFile.getRecord("22MFG_U12_li");
        assertNotNull(record);
        assertEquals("22MFG_U12_li", record.getRecordName());
        assertEquals("longin", record.getRecordType());

        record = parseFile.getRecord("22MFG_U12_calc");
        assertNotNull(record);
        assertEquals("22MFG_U12_calc", record.getRecordName());
        assertEquals("calc", record.getRecordType());

    }
}
