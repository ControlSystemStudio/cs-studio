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

        Field field = record.getField("DESC");
        assertEquals("DESC", field.getField());
        assertEquals("Wert1_Index", field.getValue());

        field = record.getField("PINI");
        assertEquals("PINI", field.getField());
        assertEquals("YES", field.getValue());

        field = record.getField("PHAS");
        assertEquals("PHAS", field.getField());
        assertEquals("1.0", field.getValue());

        field = record.getField("DTYP");
        assertEquals("DTYP", field.getField());
        assertEquals("PBDP", field.getValue());

        field = record.getField("NOBT");
        assertEquals("NOBT", field.getField());
        assertEquals("8.0", field.getValue());

        field = record.getField("OUT");
        assertEquals("OUT", field.getField());
        assertEquals("@PBDP1: 29/7 'T=INT8'", field.getValue());

        field = record.getField("B0");
        assertEquals("B0", field.getField());
        assertEquals("1.0", field.getValue());

        record = parseFile.getRecord("22MFG_U12_li");
        assertNotNull(record);
        assertEquals("22MFG_U12_li", record.getRecordName());
        assertEquals("longin", record.getRecordType());

        field = record.getField("DESC");
        assertEquals("DESC", field.getField());
        assertEquals("22MFG U12 Dreieckspannung", field.getValue());

        field = record.getField("SCAN");
        assertEquals("SCAN", field.getField());
        assertEquals("1 second", field.getValue());

        field = record.getField("DTYP");
        assertEquals("DTYP", field.getField());
        assertEquals("PBDP", field.getValue());

        field = record.getField("FLNK");
        assertEquals("FLNK", field.getField());
        assertEquals("22MFG_U12_calc", field.getValue());

        field = record.getField("INP");
        assertEquals("INP", field.getField());
        assertEquals("@PBDP1: 29/6 'T=INT16'", field.getValue());

        record = parseFile.getRecord("22MFG_U12_calc");
        assertNotNull(record);
        assertEquals("22MFG_U12_calc", record.getRecordName());
        assertEquals("calc", record.getRecordType());

        field = record.getField("DESC");
        assertEquals("DESC", field.getField());
        assertEquals("22MFG Dreieckspannung U12", field.getValue());

        field = record.getField("CALC");
        assertEquals("CALC", field.getField());
        assertEquals("(((A&255)<<8)|((A&65280)>>8))/1.", field.getValue());

        field = record.getField("INPA");
        assertEquals("INPA", field.getField());
        assertEquals("22MFG_U12_li", field.getValue());

        field = record.getField("EGU");
        assertEquals("EGU", field.getField());
        assertEquals("V", field.getValue());

        field = record.getField("PREC");
        assertEquals("PREC", field.getField());
        assertEquals("0.0", field.getValue());

        field = record.getField("LOPR");
        assertEquals("LOPR", field.getField());
        assertEquals("0.0", field.getValue());

        field = record.getField("HOPR");
        assertEquals("HOPR", field.getField());
        assertEquals("9999.0", field.getValue());

        field = record.getField("ADEL");
        assertEquals("ADEL", field.getField());
        assertEquals("5.0", field.getValue());

        field = record.getField("MDEL");
        assertEquals("MDEL", field.getField());
        assertEquals("1.0", field.getValue());
    }
}
