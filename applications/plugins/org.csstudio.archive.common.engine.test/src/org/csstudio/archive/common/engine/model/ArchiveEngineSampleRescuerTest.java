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

import javax.annotation.Nonnull;

import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.sample.ArchiveSample;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.archive.common.service.util.DataRescueException;
import org.csstudio.archive.common.service.util.DataRescueResult;
import org.csstudio.domain.desy.epics.alarm.EpicsSystemVariable;
import org.csstudio.domain.desy.epics.types.EpicsEnum;
import org.csstudio.domain.desy.system.ControlSystem;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.types.CssValueType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * TODO (bknerr) : 
 * 
 * @author bknerr
 * @since Mar 28, 2011
 */
public class ArchiveEngineSampleRescuerTest {
    
    private static File RESCUE_DIR;
    
    private static List<IArchiveSample<Object, ISystemVariable<Object>>> SAMPLES;
    
    @Rule
    public TemporaryFolder _folder = new TemporaryFolder();
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Before
    public void setup() throws IOException {  
        RESCUE_DIR = _folder.newFolder("test");
        
        IArchiveSample<Double, ISystemVariable<Double>> sample1 = 
            new ArchiveSample<Double, ISystemVariable<Double>>(new ArchiveChannelId(1), 
                                                               new EpicsSystemVariable("foo", 
                                                                                       new CssValueType<Double>(2.0), 
                                                                                       ControlSystem.EPICS_DEFAULT, 
                                                                                       TimeInstantBuilder.fromNow(),
                                                                                       null), 
                                                               null);
        IArchiveSample<Integer, ISystemVariable<Integer>> sample2 = 
            new ArchiveSample<Integer, ISystemVariable<Integer>>(new ArchiveChannelId(3), 
                    new EpicsSystemVariable("bar", 
                                            new CssValueType<Integer>(Integer.valueOf(26)), 
                                            ControlSystem.EPICS_DEFAULT, 
                                            TimeInstantBuilder.fromNow(),
                                            null), 
                                            null);
        IArchiveSample<EpicsEnum, ISystemVariable<EpicsEnum>> sample3 = 
            new ArchiveSample<EpicsEnum, ISystemVariable<EpicsEnum>>(new ArchiveChannelId(3), 
                    new EpicsSystemVariable("bar", 
                                            new CssValueType<EpicsEnum>(EpicsEnum.create(1, "foo", 666)), 
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
        TimeInstant now = TimeInstantBuilder.fromNow();

        DataRescueResult rescueResult = ArchiveEngineSampleRescuer.with(SAMPLES).at(now).to(RESCUE_DIR).rescue();
        
        File infile = new File(rescueResult.getFilePath());
        Assert.assertNotNull(infile);

        List<IArchiveSample<?, ?>> result = readSamplesFromFile(infile);
        
        Assert.assertEquals(3, result.size());
        Assert.assertEquals(Double.valueOf(2.0), (Double) result.get(0).getValue());
        Assert.assertEquals(Integer.valueOf(26), (Integer) result.get(1).getValue());
        Assert.assertEquals(Integer.valueOf(666), ((EpicsEnum) result.get(2).getValue()).getRaw());
        Assert.assertEquals("foo", ((EpicsEnum) result.get(2).getValue()).getState());
    }

    private List<IArchiveSample<?, ?>> readSamplesFromFile(File infile) throws IOException,
                                                                         FileNotFoundException,
                                                                         ClassNotFoundException {
        ObjectInputStream objectIn = new ObjectInputStream(new BufferedInputStream(new FileInputStream(infile)));
        @SuppressWarnings("unchecked")
        List<IArchiveSample<?,?>> result = (List<IArchiveSample<?,?>>) objectIn.readObject();
        objectIn.close();
        return result;
    }

}
