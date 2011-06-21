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

import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;

import javax.annotation.Nonnull;

import org.csstudio.archive.common.reader.facade.IServiceProvider;
import org.csstudio.archive.common.reader.testdata.TestUtils;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveReaderFacade;
import org.csstudio.archive.common.service.channel.ArchiveChannel;
import org.csstudio.domain.desy.epics.typesupport.EpicsSystemVariableSupport;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.types.Limits;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test for {@link EquidistantTimeBinsIterator}.
 * 
 * @author bknerr
 * @since 21.06.2011
 */
public class EquidistantTimeBinsIteratorUnitTest {
    
    
    @BeforeClass
    public static void setup() {
        EpicsSystemVariableSupport.install();
    }
    
    @Test(expected=NoSuchElementException.class)
    public void testEmptyIteratorWithoutLastSampleBefore() throws Exception {
        final TimeInstant instant = TimeInstantBuilder.fromMillis(1L);
        final IServiceProvider provider = 
            new IServiceProvider() {
                @SuppressWarnings({ "unchecked", "rawtypes" })
                @Override
                @Nonnull
                public IArchiveReaderFacade getReaderFacade() throws OsgiServiceUnavailableException {
                    IArchiveReaderFacade mock = Mockito.mock(IArchiveReaderFacade.class);
                    try {
                        Mockito.when(mock.readSamples("", instant, instant, null)).thenReturn((Collection) Collections.emptyList());
                        Mockito.when(mock.getChannelByName("")).thenReturn(TestUtils.CHANNEL);
                        Mockito.when(mock.readDisplayLimits("")).thenReturn((Limits) Limits.<Double>create(0.0, 10.0));
                        Mockito.when(mock.readLastSampleBefore("", instant)).thenReturn(null);
                    } catch (final ArchiveServiceException e) {
                        Assert.fail("Only reachable by intention.");
                    }
                    return mock;
                }
            };
        
        final EquidistantTimeBinsIterator<Double> iter = 
            new EquidistantTimeBinsIterator<Double>(provider, "", instant, instant, null, 100);
        
        Assert.assertFalse(iter.hasNext());
        
        iter.next(); // expect NSEE
    }
}
