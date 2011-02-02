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
package org.csstudio.archive.common.service.requesttypes;

import org.csstudio.archive.common.service.requesttypes.internal.ArchiveRequestTypeParameter;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test
 * 
 * @author bknerr
 * @since 26.01.2011
 */
public class IArchiveRequestTypeUnitTest {
    
    private static final ArchiveRequestTypeParameter<Integer> TEST_PARAM_I = 
        new ArchiveRequestTypeParameter<Integer>("testi", Integer.valueOf(1000));
    private static final ArchiveRequestTypeParameter<Double> TEST_PARAM_D = 
        new ArchiveRequestTypeParameter<Double>("testd", Double.valueOf(1000.0));
    
    /**
     * Internal request type for testing. 
     * 
     * @author bknerr
     * @since 01.02.2011
     */
    private static final class ART extends AbstractArchiveRequestType {
        public ART(String id, 
                   String desc, 
                   IArchiveRequestTypeParameter<?>... params) {
            super(id, desc, params);
        }

    }
    // CHECKSTYLE OFF: AvoidNestedBlocks
    @Test(expected=RequestTypeParameterException.class)
    public void testIncorrectUse1() throws RequestTypeParameterException {
        {
            IArchiveRequestType art = new ART("Typ1", "T1", TEST_PARAM_I, TEST_PARAM_D);
            // Type mismatch on getting
            @SuppressWarnings("unused")
            IArchiveRequestTypeParameter<Integer> p = art.getParameter(TEST_PARAM_D.getName(), Integer.class);
        }
    }
    @Test(expected=RequestTypeParameterException.class)
    public void testIncorrectUse2() throws RequestTypeParameterException {
        {
            IArchiveRequestType art = new ART("Typ1", "T1", TEST_PARAM_I, TEST_PARAM_D);
            IArchiveRequestTypeParameter<Double> p = art.getParameter(TEST_PARAM_D.getName(), Double.class);
            Assert.assertNotNull(p);
            Assert.assertEquals(Double.class, p.getValueType());
            // Type mismatch on setting
            art.setParameter(TEST_PARAM_D.getName(), "Here shouldn't be a string");
        }
    }
    @Test(expected=RequestTypeParameterException.class)
    public void testIncorrectUse3() throws RequestTypeParameterException {
        IArchiveRequestType art = new ART("Typ1", "T1");
        Assert.assertNull(art.getParameter("notexist", Object.class));
    }
    
    
    
    @Test
    public void testCorrectUse() throws RequestTypeParameterException {
        {
            IArchiveRequestType art = new ART("Typ1", "T1", TEST_PARAM_I);
            // Type dispatching perhaps via TypeSupport pattern...
            IArchiveRequestTypeParameter<Integer> p = art.getParameter(TEST_PARAM_I.getName(), 
                                                                       TEST_PARAM_I.getValueType());
            Assert.assertNotNull(p);
            Integer value = p.getValue();
            Assert.assertEquals(TEST_PARAM_I.getValue(), value);
            
            art.setParameter(TEST_PARAM_I.getName(), Integer.valueOf(4711));
            p = art.getParameter(TEST_PARAM_I.getName(), 
                                 TEST_PARAM_I.getValueType());
            Assert.assertEquals(Integer.valueOf(4711), p.getValue());
            
        }
        {
            IArchiveRequestType art = new ART("Typ1", "T1", TEST_PARAM_I, TEST_PARAM_D);
            IArchiveRequestTypeParameter<Double> p = art.getParameter(TEST_PARAM_D.getName(), 
                                                                      TEST_PARAM_D.getValueType());
            Assert.assertNotNull(p);
            Assert.assertEquals(TEST_PARAM_D.getValue(), p.getValue());
        }
    }
    // CHECKSTYLE ON: AvoidNestedBlocks
}
