/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.sds.internal.rules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test case for class.
 *
 * {@link ParameterDescriptor}
 *
 * @author Alexander Will
 * @version $Revision: 1.3 $
 *
 */
public final class ParameterDescriptorTest {
    /**
     * Test the <code>ParameterDescriptor</code> class.
     */
    @Test
    public void testParameterDescriptor() {
        ParameterDescriptor pd = new ParameterDescriptor(
                "channel"); //$NON-NLS-1$

        assertEquals("channel", pd.getChannel()); //$NON-NLS-1$

        pd.setChannel("channel2"); //$NON-NLS-1$

        assertEquals("channel2", pd.getChannel()); //$NON-NLS-1$

        pd = new ParameterDescriptor();

        // test the defaults as well
        assertEquals("", pd.getChannel()); //$NON-NLS-1$
    }

    /**
     * Test method for
     * {@link org.csstudio.sds.internal.rules.ParameterDescriptor#clone()}.
     */
    @Test
    public void testClone() {
        ParameterDescriptor pd = new ParameterDescriptor(
                "channel"); //$NON-NLS-1$
        ParameterDescriptor pd2 = pd.clone();

        assertEquals("channel", pd.getChannel()); //$NON-NLS-1$

        assertEquals("channel", pd2.getChannel()); //$NON-NLS-1$

        assertEquals(pd, pd2);
    }

    /**
     * Test method for
     * {@link org.csstudio.sds.internal.rules.ParameterDescriptor#equals(Object)}.
     */
    @Test
    public void testEquals() {
        ParameterDescriptor pd1 = new ParameterDescriptor(
                "channel", "#FFFFFF"); //$NON-NLS-1$
        ParameterDescriptor pd2 = new ParameterDescriptor(
                "channel", "#FFFFFF"); //$NON-NLS-1$
        ParameterDescriptor pd3 = new ParameterDescriptor(
                "channel", "${Alarm1}"); //$NON-NLS-1$
        ParameterDescriptor pd4 = new ParameterDescriptor(
                "channel", "Arial, 10, bold"); //$NON-NLS-1$

        assertTrue(pd1.equals(pd2));
        assertFalse(pd1.equals(pd3));
        assertFalse(pd1.equals(pd4));
        assertFalse(pd3.equals(pd4));

        assertTrue(pd1.hashCode() == pd2.hashCode());
        assertFalse(pd1.hashCode() == pd3.hashCode());
        assertFalse(pd1.hashCode() == pd4.hashCode());
        assertFalse(pd3.hashCode() == pd4.hashCode());
    }

}
