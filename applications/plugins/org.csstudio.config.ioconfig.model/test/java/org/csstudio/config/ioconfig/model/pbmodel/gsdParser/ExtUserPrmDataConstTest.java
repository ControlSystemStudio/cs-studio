/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
/*
 * $Id$
 */
package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 09.01.2009
 */

public class ExtUserPrmDataConstTest {

    /**
     * Test method for {@link org.csstudio.config.ioconfig.model.pbmodel.gsdParser.ExtUserPrmDataConst#ExtUserPrmDataConst(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testEmptyExtUserPrmDataConst() {
        ParsedGsdFileModel out = new ParsedGsdFileModel("Test");
        ExtUserPrmData extUserPrmData = new ExtUserPrmData(out, 0, "");
        extUserPrmData.setMinBit("0");
        extUserPrmData.setMaxBit("0");
        extUserPrmData.setDefault("1");
        out.setExtUserPrmDataDefault(extUserPrmData, 0);
        
        List<Integer> extUserPrmDataConst = out.getExtUserPrmDataConst();
        assertEquals(Integer.valueOf(1), extUserPrmDataConst.get(0));
        
        extUserPrmData = new ExtUserPrmData(out, 1, "");
        extUserPrmData.setMinBit("1");
        extUserPrmData.setMaxBit("2");
        extUserPrmData.setDefault("3");
        out.setExtUserPrmDataDefault(extUserPrmData, 0);
        
        extUserPrmDataConst = out.getExtUserPrmDataConst();
        assertEquals(Integer.valueOf(7), extUserPrmDataConst.get(0));
        
        extUserPrmData = new ExtUserPrmData(out, 2, "");
        extUserPrmData.setMinBit("0");
        extUserPrmData.setMaxBit("7");
        extUserPrmData.setDefault("255");
        out.setExtUserPrmDataDefault(extUserPrmData, 1);

        extUserPrmDataConst = out.getExtUserPrmDataConst();
        assertEquals(Integer.valueOf(255), extUserPrmDataConst.get(1));
        
        extUserPrmData = new ExtUserPrmData(out, 3, "");
        extUserPrmData.setMinBit("0");
        extUserPrmData.setMaxBit("15");
        extUserPrmData.setDefault("55555");
        out.setExtUserPrmDataDefault(extUserPrmData, 2);
        
        extUserPrmDataConst = out.getExtUserPrmDataConst();
        assertEquals(Integer.valueOf(3), extUserPrmDataConst.get(2));

        extUserPrmDataConst = out.getExtUserPrmDataConst();
        assertEquals(Integer.valueOf(217), extUserPrmDataConst.get(3));
        
    }

    @Test
    public void testFilledExtUserPrmDataConst() {
        ParsedGsdFileModel out = new ParsedGsdFileModel("Test");
        
        out.setExtUserPrmDataConst(new KeyValuePair("key(0)", "0x77"));
        out.setExtUserPrmDataConst(new KeyValuePair("key(1)", "0x77"));
        out.setExtUserPrmDataConst(new KeyValuePair("key(2)", "0x77"));
        out.setExtUserPrmDataConst(new KeyValuePair("key(3)", "0x77"));
        out.setExtUserPrmDataConst(new KeyValuePair("key(4)", "0x77"));
        
        
        ExtUserPrmData extUserPrmData = new ExtUserPrmData(out, 0, "");
        extUserPrmData.setMinBit("0");
        extUserPrmData.setMaxBit("0");
        extUserPrmData.setDefault("0");
        out.setExtUserPrmDataDefault(extUserPrmData, 0);
        
        List<Integer> extUserPrmDataConst = out.getExtUserPrmDataConst();
        assertEquals(Integer.valueOf(118), extUserPrmDataConst.get(0));
        
        extUserPrmData = new ExtUserPrmData(out, 1, "");
        extUserPrmData.setMinBit("1");
        extUserPrmData.setMaxBit("2");
        extUserPrmData.setDefault("2");
        out.setExtUserPrmDataDefault(extUserPrmData, 0);
        
        extUserPrmDataConst = out.getExtUserPrmDataConst();
        assertEquals(Integer.valueOf(116), extUserPrmDataConst.get(0));
        
        extUserPrmData = new ExtUserPrmData(out, 2, "");
        extUserPrmData.setMinBit("0");
        extUserPrmData.setMaxBit("7");
        extUserPrmData.setDefault("255");
        out.setExtUserPrmDataDefault(extUserPrmData, 1);
        
        extUserPrmDataConst = out.getExtUserPrmDataConst();
        assertEquals(Integer.valueOf(255), extUserPrmDataConst.get(1));
        
        extUserPrmData = new ExtUserPrmData(out, 3, "");
        extUserPrmData.setMinBit("0");
        extUserPrmData.setMaxBit("15");
        extUserPrmData.setDefault("55555");
        out.setExtUserPrmDataDefault(extUserPrmData, 2);
        
        extUserPrmDataConst = out.getExtUserPrmDataConst();
        assertEquals(Integer.valueOf(3), extUserPrmDataConst.get(2));

        extUserPrmDataConst = out.getExtUserPrmDataConst();
        assertEquals(Integer.valueOf(217), extUserPrmDataConst.get(3));

        extUserPrmDataConst = out.getExtUserPrmDataConst();
        assertEquals(Integer.valueOf(119), extUserPrmDataConst.get(4));
        
    }
    
    /**
     * Test method for {@link org.csstudio.config.ioconfig.model.pbmodel.gsdParser.IndexValueData#IndexValueData(java.lang.String, java.lang.String)}.
     */
    @Ignore("Not yet implemented")
    @Test
    public void testIndexValueData() {
        fail("Not yet implemented");
    }

    /**
     * Test method for {@link org.csstudio.config.ioconfig.model.pbmodel.gsdParser.IndexValueData#getIndex()}.
     */
    @Ignore("Not yet implemented")
    @Test
    public void testGetIndex() {
        fail("Not yet implemented");
    }

    /**
     * Test method for {@link org.csstudio.config.ioconfig.model.pbmodel.gsdParser.IndexValueData#setIndex(java.lang.String)}.
     */
    @Ignore("Not yet implemented")
    @Test
    public void testSetIndex() {
        fail("Not yet implemented");
    }

    /**
     * Test method for {@link org.csstudio.config.ioconfig.model.pbmodel.gsdParser.IndexValueData#getValue()}.
     */
    @Ignore("Not yet implemented")
    @Test
    public void testGetValue() {
        fail("Not yet implemented");
    }

    /**
     * Test method for {@link org.csstudio.config.ioconfig.model.pbmodel.gsdParser.IndexValueData#setValue(java.lang.String)}.
     */
    @Ignore("Not yet implemented")
    @Test
    public void testSetValue() {
        fail("Not yet implemented");
    }

    /**
     * Test method for {@link org.csstudio.config.ioconfig.model.pbmodel.gsdParser.IndexValueData#toString()}.
     */
    @Ignore("Not yet implemented")
    @Test
    public void testToString() {
        fail("Not yet implemented");
    }

}
