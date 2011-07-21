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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.sample.ArchiveSample;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.archive.common.service.util.DataRescueException;
import org.csstudio.archive.common.service.util.DataRescueResult;
import org.csstudio.domain.desy.epics.types.EpicsEnum;
import org.csstudio.domain.desy.epics.types.EpicsSystemVariable;
import org.csstudio.domain.desy.system.ControlSystem;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test for {@link ArchiveEngineSampleRescuer}.
 *
 * @author bknerr
 * @since Mar 28, 2011
 */
public class ArchiveEngineSampleRescuerUnitTest {

    private static File RESCUE_DIR;

    private static List<IArchiveSample<Object, ISystemVariable<Object>>> SAMPLES;

    @Rule
    public TemporaryFolder _folder = new TemporaryFolder();

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Before
    public void setup() {
        RESCUE_DIR = _folder.newFolder("test");

        final IArchiveSample<Double, ISystemVariable<Double>> sample1 =
            new ArchiveSample<Double, ISystemVariable<Double>>(new ArchiveChannelId(1),
                                                               new EpicsSystemVariable("foo",
                                                                                       Double.valueOf(2.0),
                                                                                       ControlSystem.EPICS_DEFAULT,
                                                                                       TimeInstantBuilder.fromNow(),
                                                                                       null),
                                                               null);
        final IArchiveSample<Integer, ISystemVariable<Integer>> sample2 =
            new ArchiveSample<Integer, ISystemVariable<Integer>>(new ArchiveChannelId(3),
                    new EpicsSystemVariable("bar",
                                            Integer.valueOf(26),
                                            ControlSystem.EPICS_DEFAULT,
                                            TimeInstantBuilder.fromNow(),
                                            null),
                                            null);
        final IArchiveSample<EpicsEnum, ISystemVariable<EpicsEnum>> sample3 =
            new ArchiveSample<EpicsEnum, ISystemVariable<EpicsEnum>>(new ArchiveChannelId(3),
                    new EpicsSystemVariable("bar",
                                            EpicsEnum.createFromRaw(666),
                                            ControlSystem.EPICS_DEFAULT,
                                            TimeInstantBuilder.fromNow(),
                                            null),
                                            null);

        SAMPLES = new ArrayList();
        SAMPLES.add((IArchiveSample) sample1);
        SAMPLES.add((IArchiveSample) sample2);
        SAMPLES.add((IArchiveSample) sample3);

    }

    @Test
    public void saveToPathTest() throws DataRescueException, IOException, ClassNotFoundException {
        final TimeInstant now = TimeInstantBuilder.fromNow();

        final DataRescueResult rescueResult = ArchiveEngineSampleRescuer.with(SAMPLES).at(now).to(RESCUE_DIR).rescue();

        final File infile = new File(rescueResult.getFilePath());
        Assert.assertNotNull(infile);

        final List<IArchiveSample<?, ?>> result = readSamplesFromFile(infile);

        Assert.assertEquals(3, result.size());
        Assert.assertEquals(Double.valueOf(2.0), result.get(0).getValue());
        Assert.assertEquals(Integer.valueOf(26), result.get(1).getValue());
        Assert.assertEquals(Integer.valueOf(666), ((EpicsEnum) result.get(2).getValue()).getRaw());
    }

    private List<IArchiveSample<?, ?>> readSamplesFromFile(final File infile) throws IOException,
                                                                         FileNotFoundException,
                                                                         ClassNotFoundException {
        final ObjectInputStream objectIn = new ObjectInputStream(new BufferedInputStream(new FileInputStream(infile)));
        @SuppressWarnings("unchecked")
        final
        List<IArchiveSample<?,?>> result = (List<IArchiveSample<?,?>>) objectIn.readObject();
        objectIn.close();
        return result;
    }

}
