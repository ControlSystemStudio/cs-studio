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
package org.csstudio.archive.common.reader;

import java.util.Collections;
import java.util.NoSuchElementException;

import org.csstudio.archive.common.reader.facade.IServiceProvider;
import org.csstudio.archive.common.reader.testdata.TestUtils;
import org.csstudio.data.values.IMinMaxDoubleValue;
import org.csstudio.domain.desy.epics.typesupport.EpicsSystemVariableSupport;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for {@link DesyArchiveValueIterator}. 
 * 
 * @author bknerr
 * @since 20.06.2011
 */
public class DesyArchiveValueIteratorUnitTest {
    

    @BeforeClass
    public static void setup() {
        EpicsSystemVariableSupport.install();
    }
    
    @Test
    public void testFilledIterator() throws Exception {
        final TimeInstant start = TimeInstantBuilder.fromMillis(0L);
        final TimeInstant end = TimeInstantBuilder.fromMillis(50L);
        
        final IServiceProvider provider = 
            TestUtils.createCustomizedMockedServiceProvider(TestUtils.CHANNEL_NAME_1, 
                                                            start, 
                                                            end, 
                                                            TestUtils.CHANNEL_1_SAMPLES);
        
        
        final DesyArchiveValueIterator iter = 
            new DesyArchiveValueIterator(provider, TestUtils.CHANNEL_NAME_1, start, end, null);
        
        Assert.assertTrue(iter.hasNext());
        IMinMaxDoubleValue next = (IMinMaxDoubleValue) iter.next();
        Assert.assertTrue(next.getValue() == 10.0);
        Assert.assertTrue(next.getMinimum() == 9.0);
        Assert.assertTrue(next.getMaximum() == 11.0);
        
        Assert.assertTrue(iter.hasNext());
        next = (IMinMaxDoubleValue) iter.next();
        Assert.assertTrue(next.getValue() == 20.0);
        Assert.assertTrue(next.getMinimum() == 19.0);
        Assert.assertTrue(next.getMaximum() == 21.0);
        
        Assert.assertFalse(iter.hasNext());
    }
    

    @Test(expected=NoSuchElementException.class)
    public void testEmptyIterator() throws Exception {
        final TimeInstant instant = TimeInstantBuilder.fromMillis(1L);
        
        final IServiceProvider provider = 
            TestUtils.createCustomizedMockedServiceProvider("", instant, instant, Collections.emptyList());
        
        final DesyArchiveValueIterator iter = 
            new DesyArchiveValueIterator(provider, "", instant, instant, null);
        
        Assert.assertFalse(iter.hasNext());
        
        iter.next(); // expect NSEE
    }
}
