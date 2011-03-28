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

import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.archive.common.service.channel.ArchiveChannelId;
import org.csstudio.archive.common.service.sample.ArchiveSample;
import org.csstudio.archive.common.service.sample.IArchiveSample;
import org.csstudio.archive.common.service.util.DataRescueException;
import org.csstudio.domain.desy.epics.alarm.EpicsSystemVariable;
import org.csstudio.domain.desy.epics.types.EpicsEnum;
import org.csstudio.domain.desy.system.ControlSystem;
import org.csstudio.domain.desy.system.ISystemVariable;
import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.domain.desy.types.CssValueType;
import org.eclipse.core.internal.preferences.PreferencesService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * TODO (bknerr) : 
 * 
 * @author bknerr
 * @since Mar 28, 2011
 */
public class ArchiveEngineSampleRescuerTest {
    
    private static URL RESCUE_DIR;
    
    private static List<IArchiveSample<Object, ISystemVariable<Object>>> SAMPLES;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Before
    void setup() throws MalformedURLException {  
        RESCUE_DIR = new URL("test");
        
        ArchiveSample<Double, ISystemVariable<Double>> sample1 = 
            new ArchiveSample<Double, ISystemVariable<Double>>(new ArchiveChannelId(1), 
                                                               new EpicsSystemVariable("foo", 
                                                                                       new CssValueType<Double>(2.0), 
                                                                                       ControlSystem.EPICS_DEFAULT, 
                                                                                       TimeInstantBuilder.fromNow(),
                                                                                       null), 
                                                               null);
        ArchiveSample<EpicsEnum, ISystemVariable<EpicsEnum>> sample2 = 
            new ArchiveSample<EpicsEnum, ISystemVariable<EpicsEnum>>(new ArchiveChannelId(1), 
                    new EpicsSystemVariable("bar", 
                                            new CssValueType<EpicsEnum>(EpicsEnum.createInstance(1, "ON", 10)), 
                                            ControlSystem.EPICS_DEFAULT, 
                                            TimeInstantBuilder.fromNow(),
                                            null), 
                                            null);
        
        SAMPLES = new ArrayList();
        SAMPLES.add((ArchiveSample) sample1);
        SAMPLES.add((ArchiveSample)sample2);
        
    }
    
    @Test
    void saveToPathTest() throws DataRescueException {
        ArchiveEngineSampleRescuer.with(SAMPLES).to(RESCUE_DIR).rescue();
        
        testIfFileExists(RESCUE_DIR);
        
        
        
        // READ IN FROM FILE
        int objectCount = 0;
        Junk object = null;
        
        objectIn = new ObjectInputStream(new BufferedInputStream(new FileInputStream("C:/JunkObjects.bin")));
        
        // Read from the stream until we hit the end
        while (objectCount < 3) {
            object = (Junk) objectIn.readObject();
            objectCount++;
            System.out.println(object);
        }
        
        objectIn.close();


        
    }
    
    /**
     * 
     */
    private void testIfFileExists() {
        ObjectInputStream objectIn = null;
        // TODO Auto-generated method stub
        
    }

    @After
    void clear() {
        
    }
}
