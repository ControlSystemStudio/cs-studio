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
package org.csstudio.archive.common.engine.model;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import junit.framework.Assert;

import org.csstudio.archive.common.engine.service.IServiceProvider;
import org.csstudio.archive.common.service.ArchiveServiceException;
import org.csstudio.archive.common.service.IArchiveEngineFacade;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.sample.ArchiveMultiScalarSample;
import org.csstudio.archive.common.service.sample.ArchiveSample;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.data.values.IValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.domain.desy.epics.typesupport.EpicsIMetaDataTypeSupport;
import org.csstudio.domain.desy.epics.typesupport.EpicsIValueTypeSupport;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.utility.pv.PV;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for {@link DesyArchivePVListener}.
 *
 * @author bknerr
 * @since 09.08.2011
 */
public class DesyArchivePVListenerUnitTest {

    private IServiceProvider _provider;
    private IArchiveEngineFacade _facade;
    private ArchiveChannelId _channelId;
    private String _channelName;
    private PV _doublePv;
    private Double _fstSampleVal;
    private PV _collDoublePv;
    private double[] _sndSampleVal;

    @Before
    public void setupMocks() throws OsgiServiceUnavailableException {

        EpicsIMetaDataTypeSupport.install();
        EpicsIValueTypeSupport.install();

        _provider = mock(IServiceProvider.class);
        _facade = mock(IArchiveEngineFacade.class);
        when(_provider.getEngineFacade()).thenReturn(_facade);

        _channelId = new ArchiveChannelId(1L);
        _channelName = "testChannel";
        _fstSampleVal = 26.0;
        _sndSampleVal = new double[] {21.0, 22.0, 23.0};

        _doublePv = mock(PV.class);
        when(_doublePv.getStateInfo()).thenReturn("Startup");
        when(_doublePv.isConnected()).thenReturn(true);

        final IValue val1 =
            ValueFactory.createDoubleValue(TimestampFactory.createTimestamp(1L, 0L),
                                           ValueFactory.createOKSeverity(),
                                           "FooStatys",
                                           ValueFactory.createNumericMetaData(20.0, 30.0, 22.0, 28.0, 21.0, 29.0, 1, "ms"),
                                           IValue.Quality.Original,
                                           new double[] {_fstSampleVal});

        when(_doublePv.getValue()).thenReturn(val1);


        _collDoublePv = mock(PV.class);
        when(_collDoublePv.getStateInfo()).thenReturn("Startup");
        when(_collDoublePv.isConnected()).thenReturn(true);

        final IValue val2 =
            ValueFactory.createDoubleValue(TimestampFactory.createTimestamp(1L, 0L),
                                           ValueFactory.createOKSeverity(),
                                           "FooStatys",
                                           ValueFactory.createNumericMetaData(19.0, 31.0, 22.0, 28.0, 21.0, 29.0, 1, "ms"),
                                           IValue.Quality.Original,
                                           _sndSampleVal);

        when(_collDoublePv.getValue()).thenReturn(val2);
    }

    @Test
    public void testDoubleSample() throws ArchiveServiceException {



        final DesyArchivePVListener<Double, ISystemVariable<Double>> listener =
            new DesyArchivePVListener<Double, ISystemVariable<Double>>(_provider,
                                                                       _channelName,
                                                                       _channelId,
                                                                       Double.class) {
            @SuppressWarnings("synthetic-access")
            @Override
            protected void addSampleToBuffer(@Nonnull final IArchiveSample<Double, ISystemVariable<Double>> sample) {
                Assert.assertTrue(sample instanceof ArchiveSample);
                Assert.assertEquals(_channelId, sample.getChannelId());
                Assert.assertEquals(_channelName, sample.getSystemVariable().getName());
                Assert.assertEquals(_fstSampleVal, sample.getValue());
            }
        };
        listener.pvValueUpdate(_doublePv);
        listener.pvValueUpdate(_doublePv);

        verify(_facade, times(1)).writeChannelStatusInfo(eq(_channelId), eq(true), eq("Startup"), any(TimeInstant.class));
        verify(_facade, times(1)).writeChannelDisplayRangeInfo(eq(_channelId), eq(20.0), eq(30.0));

        listener.pvDisconnected(_doublePv);
        verify(_facade, times(1)).writeChannelStatusInfo(eq(_channelId), eq(false), eq("Startup"), any(TimeInstant.class));
    }

    @Test
    public void testCollectionDoubleSample() throws ArchiveServiceException {
        @SuppressWarnings("rawtypes")
        final DesyArchivePVListener<ArrayList, ISystemVariable<ArrayList>> listener =
            new DesyArchivePVListener<ArrayList, ISystemVariable<ArrayList>>(_provider,
                                                                             _channelName,
                                                                             _channelId,
                                                                             ArrayList.class) {
            @SuppressWarnings("synthetic-access")
            @Override
            protected void addSampleToBuffer(@Nonnull final IArchiveSample<ArrayList, ISystemVariable<ArrayList>> sample) {
                Assert.assertTrue(sample instanceof ArchiveMultiScalarSample);
                Assert.assertEquals(_channelId, sample.getChannelId());
                Assert.assertEquals(_channelName, sample.getSystemVariable().getName());
                @SuppressWarnings("unchecked")
                final List<Double> value = sample.getValue();
                Assert.assertEquals(_sndSampleVal.length, value.size());
                for (int i = 0; i < value.size(); i++) {
                    Assert.assertEquals(_sndSampleVal[i], value.get(i));
                }
            }
        };
        listener.pvValueUpdate(_collDoublePv);
        listener.pvValueUpdate(_collDoublePv);

        verify(_facade, times(1)).writeChannelStatusInfo(eq(_channelId), eq(true), eq("Startup"), any(TimeInstant.class));
        verify(_facade, times(1)).writeChannelDisplayRangeInfo(eq(_channelId), eq(19.0), eq(31.0));

        listener.pvDisconnected(_collDoublePv);
        verify(_facade, times(1)).writeChannelStatusInfo(eq(_channelId), eq(false), eq("Startup"), any(TimeInstant.class));
    }
}
