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
package org.csstudio.config.ioconfig.editorparts;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 *
 * @author hrickens
 * @since 07.10.2011
 */
public class NodeEditiorUnitTest {

    /**
     * Test method for {@link org.csstudio.config.ioconfig.editorparts.AbstractNodeEditor#getShortDesc(org.eclipse.swt.widgets.Text)}.
     */
    @Test
    public void testGetShortDesc() {
        final String resultShort = "ABCabc123!$%";
        final String resultFull = "ABCDEFGHIJabcdefghij1234567890!$%&/()=?*";
        final FacilityEditor facilityEditor = new FacilityEditor();


        String desc = facilityEditor.getShortDesc(resultShort);
        assertEquals(resultShort, desc);

        desc = facilityEditor.getShortDesc(resultFull);
        assertEquals(resultFull, desc);

        desc = facilityEditor.getShortDesc(resultFull+resultFull);
        assertEquals(resultFull, desc);

        desc = facilityEditor.getShortDesc(resultFull+"\r\n"+resultFull);
        assertEquals(resultFull, desc);

        desc = facilityEditor.getShortDesc(resultShort+"\r\n"+resultFull);
        assertEquals(resultShort, desc);

        desc = facilityEditor.getShortDesc(resultFull+"\r"+resultFull);
        assertEquals(resultFull, desc);

        desc = facilityEditor.getShortDesc(resultShort+"\r"+resultFull);
        assertEquals(resultShort, desc);

        desc = facilityEditor.getShortDesc(resultFull+"\n"+resultFull);
        assertEquals(resultFull, desc);

        desc = facilityEditor.getShortDesc(resultShort+"\n"+resultFull);
        assertEquals(resultShort, desc);
    }

    @Test
    public void testGetShortDescFromNull() {
        final FacilityEditor facilityEditor = new FacilityEditor();

        String desc = facilityEditor.getShortDesc(null);
        assertEquals("", desc);
    }

    @Test
    public void testGetShortDescFromEmpty() {
    	final FacilityEditor facilityEditor = new FacilityEditor();
    	
    	String desc = facilityEditor.getShortDesc("");
    	assertEquals("", desc);
    }
    
    @Test
    public void testGetShortDescFromEndOfLine() {
    	final FacilityEditor facilityEditor = new FacilityEditor();
    	
    	String desc = facilityEditor.getShortDesc("\r");
    	assertEquals("", desc);
    	
    	desc = facilityEditor.getShortDesc("\n");
    	assertEquals("", desc);
    	
    	desc = facilityEditor.getShortDesc("\n\r");
    	assertEquals("", desc);
    }
    
}
