/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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
 /**
 *
 */
package org.csstudio.platform.internal.model.pvs;

import static org.junit.Assert.*;

import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link ControlSystemEnum}.
 *
 * @author Sven Wende
 *
 */
public class ControlSystemEnumTest {

    /**
     * Set up.
     */
    @Before
    public void setUp() {
    }

    /**
     * Test method for
     * {@link org.csstudio.platform.model.pvs.ControlSystemEnum#getPrefix()}.
     */
    @Test
    public void testGetPrefix() {
        for (ControlSystemEnum cs : ControlSystemEnum.values()) {
            assertNotNull(cs.getPrefix());
        }
    }

    /**
     * Test method for
     * {@link org.csstudio.platform.model.pvs.ControlSystemEnum#getResponsibleDalPlugId()}.
     */
    @Test
    public void testGetResponsibleDalPlugId() {
        for (ControlSystemEnum cs : ControlSystemEnum.values()) {
            if (cs.isSupportedByDAL()) {
                assertNotNull(cs.getResponsibleDalPlugId());
            } else {
                assertNull(
                        cs.name()
                                + " is not supported by DAL but provides a DAL plug ID which is "
                                + cs.getResponsibleDalPlugId(), cs.getResponsibleDalPlugId());
            }
        }
    }

    /**
     * Test method for
     * {@link org.csstudio.platform.model.pvs.ControlSystemEnum#findByPrefix(java.lang.String)}.
     */
    @Test
    public void testFindByPrefix() {
        for (ControlSystemEnum cs : ControlSystemEnum.values()) {
            ControlSystemEnum csFound = cs.findByPrefix(cs.getPrefix());

            assertEquals(csFound, cs);
        }
    }
}
