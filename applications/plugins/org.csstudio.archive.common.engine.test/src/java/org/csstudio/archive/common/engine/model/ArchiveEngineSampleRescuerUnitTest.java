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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.log4j.PropertyConfigurator;
import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.sample.ArchiveSample;
import org.csstudio.archive.common.service.sample.ArchiveSampleProtos;
import org.csstudio.archive.common.service.sample.ArchiveSampleProtos.Samples;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.archive.common.service.util.ArchiveTypeConversionSupport;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarm;
import org.csstudio.domain.desy.epics.types.EpicsEnum;
import org.csstudio.domain.desy.epics.types.EpicsSystemVariable;
import org.csstudio.domain.desy.system.ControlSystem;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.protobuf.TextFormat;

/**
 * Test for {@link ArchiveEngineSampleRescuer}.
 *
 * @author bknerr
 * @since Mar 28, 2011
 */
public class ArchiveEngineSampleRescuerUnitTest {
    /**
     * Rolling file appender for serialised data of samples.
     */
    private static final Logger RESCUE_LOG =
        LoggerFactory.getLogger("SerializedSamplesRescueLogger");

    private List<IArchiveSample<Object, ISystemVariable<Object>>> _samples;
    private static File RESCUE_FILE;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Before
    public void setup() {
        PropertyConfigurator.configure("../../../products/DESY/plugins/org.csstudio.archive.common.engine.product.log4j/log4j.properties");

        RESCUE_FILE = new File("rescue/samples/samples.ser");

        ArchiveTypeConversionSupport.install();

        final IArchiveSample<Double, ISystemVariable<Double>> sample1 =
            new ArchiveSample<Double, ISystemVariable<Double>>(new ArchiveChannelId(1),
                                                               new EpicsSystemVariable("leonard",
                                                                                       Double.valueOf(2.0),
                                                                                       ControlSystem.EPICS_DEFAULT,
                                                                                       TimeInstantBuilder.fromNow(),
                                                                                       EpicsAlarm.UNKNOWN),
                                                               null);
        final IArchiveSample<Integer, ISystemVariable<Integer>> sample2 =
            new ArchiveSample<Integer, ISystemVariable<Integer>>(new ArchiveChannelId(2),
                                                                 new EpicsSystemVariable("sheldon",
                                                                                         Integer.valueOf(26),
                                                                                         ControlSystem.EPICS_DEFAULT,
                                                                                         TimeInstantBuilder.fromNow(),
                                                                                         EpicsAlarm.UNKNOWN),
                                                                 null);
        final IArchiveSample<EpicsEnum, ISystemVariable<EpicsEnum>> sample3 =
            new ArchiveSample<EpicsEnum, ISystemVariable<EpicsEnum>>(new ArchiveChannelId(3),
                                                                     new EpicsSystemVariable("howard",
                                                                                             EpicsEnum.createFromRaw(666),
                                                                                             ControlSystem.EPICS_DEFAULT,
                                                                                             TimeInstantBuilder.fromNow(),
                                                                                             EpicsAlarm.UNKNOWN),
                                                                     null);
        final IArchiveSample<Collection<Double>, ISystemVariable<Collection<Double>>> sample4 =
            new ArchiveSample<Collection<Double>, ISystemVariable<Collection<Double>>>(new ArchiveChannelId(3),
                    new EpicsSystemVariable("rajesh",
                                            Lists.newArrayList(1.0, 2.0, 3.0, 4.0, 5.0),
                                            ControlSystem.EPICS_DEFAULT,
                                            TimeInstantBuilder.fromNow(),
                                            EpicsAlarm.UNKNOWN),
                                            null);

        _samples = new ArrayList();
        _samples.add((IArchiveSample) sample1);
        _samples.add((IArchiveSample) sample2);
        _samples.add((IArchiveSample) sample3);
        _samples.add((IArchiveSample) sample4);

    }

    @Test
    public void saveToPathTest() throws InterruptedException, TypeSupportException {
        Assert.assertTrue(RESCUE_FILE.exists() && RESCUE_FILE.isFile());

        final ArchiveSampleProtos.Samples.Builder gpbSamplesBuilder =
            ArchiveSampleProtos.Samples.newBuilder();

        final Samples gpbSamples = buildGPBSamples(gpbSamplesBuilder);

        RESCUE_LOG.info(gpbSamples.toString());

        Thread.sleep(500);

        final Samples result = readSamplesFromFile(gpbSamplesBuilder);

        assertResult(result);
    }

    @SuppressWarnings("unchecked")
    private void assertResult(@Nonnull final Samples result) throws TypeSupportException {
        Assert.assertEquals(_samples.size(), result.getSampleCount());

        final Iterator<IArchiveSample<Object, ISystemVariable<Object>>> iterator = _samples.iterator();

        Assert.assertEquals(iterator.next().getValue(),
                            ArchiveTypeConversionSupport.fromArchiveString(Double.class, result.getSample(0).getData()));
        Assert.assertEquals(iterator.next().getValue(),
                            ArchiveTypeConversionSupport.fromArchiveString(Integer.class, result.getSample(1).getData()));
        Assert.assertEquals(iterator.next().getValue(),
                            ArchiveTypeConversionSupport.fromArchiveString(EpicsEnum.class, result.getSample(2).getData()));


        final Collection<Double> collResult =
            ArchiveTypeConversionSupport.fromMultiScalarArchiveString(ArrayList.class,
                                                                      Double.class,
                                                                      result.getSample(3).getData());
        Assert.assertTrue(Iterables.size(collResult) == 5);

        final Iterator<Double> iter = collResult.iterator();
        for (final Double element : (Collection<Double>) iterator.next().getValue()) {
            Assert.assertEquals(iter.next(), element);
        }
    }

    private Samples buildGPBSamples(@Nonnull final ArchiveSampleProtos.Samples.Builder gpbSamples) throws TypeSupportException {
        final ArchiveSampleProtos.ArchiveSample.Builder builder =
            ArchiveSampleProtos.ArchiveSample.newBuilder();

        for (final IArchiveSample<Object, ISystemVariable<Object>> sample : _samples) {
            final ISystemVariable<Object> sysVar = sample.getSystemVariable();
            //builder.clear();
            final ArchiveSampleProtos.ArchiveSample gpbSample =
                builder.setChannelId(sysVar.getName())
                       .setControlSystemId(sysVar.getOrigin().getId())
                       .setNanosSinceEpoch(sysVar.getTimestamp().getNanos())
                       .setData(ArchiveTypeConversionSupport.toArchiveString(sysVar.getData()))
                       .build();
            gpbSamples.addSample(gpbSample);
        }
        return gpbSamples.build();
    }

    private Samples readSamplesFromFile(@Nonnull final ArchiveSampleProtos.Samples.Builder gpbSamples) {
        gpbSamples.clear();
        gpbSamples.build();
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream("rescue/samples/samples.ser"), "ASCII");
            TextFormat.merge(reader, gpbSamples);
        } catch (final UnsupportedEncodingException e) {
            Assert.fail(e.getMessage());
        } catch (final IOException e) {
            Assert.fail(e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Assert.fail(e.getMessage());
                }
            }
        }
        final Samples result = gpbSamples.build();
        return result;
    }

    @AfterClass
    public static void teardown() throws IOException {
        Files.write(new byte[0], RESCUE_FILE);
        Assert.assertTrue(RESCUE_FILE.length() == 0L);
    }
}
