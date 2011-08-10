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

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 09.01.2009
 */
public class ExtUserPrmDataConstUnitTest {
    
    private GSDFileDBO _gsdFileDBO;
    
    /**
     * @param out
     * @return
     */
    @Nonnull
    public final ExtUserPrmData createExtUserPrmData(@Nonnull final ParsedGsdFileModel out, final int index, @Nonnull final String minBit, @Nonnull final String maxBit, @Nonnull final String def ) {
        final ExtUserPrmData extUserPrmData = new ExtUserPrmData(out, index, "");
        extUserPrmData.setMinBit(minBit);
        extUserPrmData.setMaxBit(maxBit);
        extUserPrmData.setDefault(def);
        return extUserPrmData;
    }
    
    /**
     * @param out
     */
    public final void setExtUserPrmDataConst(@Nonnull final ParsedGsdFileModel out) {
        out.setExtUserPrmDataConst(new KeyValuePair("key(0)", "0x77"));
        out.setExtUserPrmDataConst(new KeyValuePair("key(1)", "0x77"));
        out.setExtUserPrmDataConst(new KeyValuePair("key(2)", "0x77"));
        out.setExtUserPrmDataConst(new KeyValuePair("key(3)", "0x77"));
        out.setExtUserPrmDataConst(new KeyValuePair("key(4)", "0x77"));
    }
    
    @Before
    public void setUp() throws Exception {
        _gsdFileDBO = new GSDFileDBO("JUnitTest", "#Profibus_DP\nVendor_Name            = JUnitTest");
    }
    
    
    /**
     * Test method for {@link org.csstudio.config.ioconfig.model.pbmodel.gsdParser.ExtUserPrmDataConst#ExtUserPrmDataConst(java.lang.String, java.lang.String)}.
     */
    @Test
    public final void testEmptyExtUserPrmDataConst() {
        final ParsedGsdFileModel out = new ParsedGsdFileModel(_gsdFileDBO);
        testExtUserPrmDataConst(out, 0, 4, 255, 4, 255, 3);
    }
    
    /**
     * @param out
     */
    public final void testExtUserPrmDataConst(@Nonnull final ParsedGsdFileModel out, @Nonnull final Integer... expec) {
        int i = 0;
        ExtUserPrmData extUserPrmData = createExtUserPrmData(out, 0, "0", "0", "0");
        out.setExtUserPrmDataDefault(extUserPrmData, 0);
        
        List<Integer> extUserPrmDataConst = out.getExtUserPrmDataConst();
        assertEquals(expec[i++], extUserPrmDataConst.get(0));
        
        extUserPrmData = createExtUserPrmData(out, 1, "1", "2", "2");
        out.setExtUserPrmDataDefault(extUserPrmData, 0);
        
        extUserPrmDataConst = out.getExtUserPrmDataConst();
        assertEquals(expec[i++], extUserPrmDataConst.get(0));
        
        extUserPrmData = createExtUserPrmData(out, 2, "0", "7", "255");
        out.setExtUserPrmDataDefault(extUserPrmData, 1);
        
        extUserPrmDataConst = out.getExtUserPrmDataConst();
        assertEquals(expec[i++], extUserPrmDataConst.get(1));
        
        extUserPrmData = createExtUserPrmData(out, 3, "0", "15", "55555");
        out.setExtUserPrmDataDefault(extUserPrmData, 2);
        
        extUserPrmDataConst = out.getExtUserPrmDataConst();
        assertEquals(expec[i++], extUserPrmDataConst.get(0));
        
        extUserPrmDataConst = out.getExtUserPrmDataConst();
        assertEquals(expec[i++], extUserPrmDataConst.get(1));
        
        extUserPrmDataConst = out.getExtUserPrmDataConst();
        assertEquals(expec[i++], extUserPrmDataConst.get(2));
    }
    
    @Test
    public final void testFilledExtUserPrmDataConst() {
        final ParsedGsdFileModel out = new ParsedGsdFileModel(_gsdFileDBO);
        setExtUserPrmDataConst(out);
        testExtUserPrmDataConst(out, 118, 116, 255, 116, 255, 3);
    }
    
    /**
     * Test method for {@link org.csstudio.config.ioconfig.model.pbmodel.gsdParser.IndexValueData#getIndex()}.
     */
    @Ignore("Not yet implemented")
    @Test
    public final void testGetIndex() {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.csstudio.config.ioconfig.model.pbmodel.gsdParser.IndexValueData#getValue()}.
     */
    @Ignore("Not yet implemented")
    @Test
    public final void testGetValue() {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.csstudio.config.ioconfig.model.pbmodel.gsdParser.IndexValueData#IndexValueData(java.lang.String, java.lang.String)}.
     */
    @Ignore("Not yet implemented")
    @Test
    public final void testIndexValueData() {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.csstudio.config.ioconfig.model.pbmodel.gsdParser.IndexValueData#setIndex(java.lang.String)}.
     */
    @Ignore("Not yet implemented")
    @Test
    public final void testSetIndex() {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.csstudio.config.ioconfig.model.pbmodel.gsdParser.IndexValueData#setValue(java.lang.String)}.
     */
    @Ignore("Not yet implemented")
    @Test
    public final void testSetValue() {
        fail("Not yet implemented");
    }
    
    /**
     * Test method for {@link org.csstudio.config.ioconfig.model.pbmodel.gsdParser.IndexValueData#toString()}.
     */
    @Ignore("Not yet implemented")
    @Test
    public final void testToString() {
        fail("Not yet implemented");
    }
    
}
