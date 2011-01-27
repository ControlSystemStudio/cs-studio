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

import javax.annotation.Nonnull;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test
 * 
 * @author bknerr
 * @since 26.01.2011
 */
public class IArchiveRequestTypeUnitTest {
    
    public static final IArchiveRequestTypeParameter<Integer> TEST_PARAM_I =
        new IArchiveRequestTypeParameter<Integer>() {
        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnull
        public String getName() {
            return "testi";
        }
        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnull
        public Integer getValue() {
            return Integer.valueOf(1000);
        }
    };
    public static final IArchiveRequestTypeParameter<Double> TEST_PARAM_D =
        new IArchiveRequestTypeParameter<Double>() {
        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnull
        public String getName() {
            return "testd";
        }
        /**
         * {@inheritDoc}
         */
        @Override
        @Nonnull
        public Double getValue() {
            return Double.valueOf(1000.0);
        }
    };
    
    private static final class ART extends AbstractArchiveRequestType {
        public ART(String id, 
                   String desc, 
                   IArchiveRequestTypeParameter<?>... params) {
            super(id, desc, params);
        }

    }
    
    @Test(expected=ClassCastException.class)
    public void testIncorrectUse() {
        {
            IArchiveRequestType art = new ART("Typ1", "T1", TEST_PARAM_I, TEST_PARAM_D);
            IArchiveRequestTypeParameter<Integer> p = art.getParameter(TEST_PARAM_D.getName());
            Assert.assertNotNull(p);
            Integer value = p.getValue();
        }
    }
    
    @Test
    public void testCorrectUse() {
        {
            IArchiveRequestType art = new ART("Typ1", "T1");
            Assert.assertNull(art.getParameter("notexist"));
        }
        {
            IArchiveRequestType art = new ART("Typ1", "T1", TEST_PARAM_I);
            IArchiveRequestTypeParameter<Integer> p = art.getParameter(TEST_PARAM_I.getName());
            Assert.assertNotNull(p);
            Assert.assertEquals(TEST_PARAM_I.getValue(), p.getValue());
        }
        {
            IArchiveRequestType art = new ART("Typ1", "T1", TEST_PARAM_I, TEST_PARAM_D);
            IArchiveRequestTypeParameter<Double> p = art.getParameter(TEST_PARAM_D.getName());
            Assert.assertNotNull(p);
            Assert.assertEquals(TEST_PARAM_D.getValue(), p.getValue());
        }
    }
}
