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
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.annotation.Nonnull;

import junit.framework.Assert;

import org.csstudio.archive.common.engine.model.internal.MyEpicsPVFactory;
import org.csstudio.data.values.IDoubleValue;
import org.csstudio.domain.desy.softioc.BasicSoftIocConfigurator;
import org.csstudio.domain.desy.softioc.ISoftIocConfigurator;
import org.csstudio.domain.desy.softioc.SoftIoc;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * Tests for a numeric channel with different .ADEL, .MDEL settings whether the update events
 * of {@link PV} are correctly triggered.   
 * 
 * @author bknerr
 * @since 31.05.2011
 */
public class PvMdelVsAdelHeadlessTest {
    
    private static Double ADEL = 1.1;
    private static Double MDEL = 0.9;
    
    final List<Double> _values = Lists.newLinkedList();
    
    private SoftIoc _softIoc;

    private final class TestListener implements PVListener {
        /**
         * Constructor.
         */
        public TestListener() {
            // Empty
        }
        @Override
        public void pvValueUpdate(@Nonnull final PV pv) {
            IDoubleValue value = (IDoubleValue) pv.getValue();
            synchronized (_values) {
                _values.add(Double.valueOf(value.getValue()));
            }
        }
        @Override
        public void pvDisconnected(@Nonnull final PV pv) {
            // Empty
        }
    }
    
    @Before
    public void setup() throws IOException, URISyntaxException {
        URL dbBundleResourceUrl = PvMdelVsAdelHeadlessTest.class.getClassLoader().getResource("db/adelVsMdel.db");
        URL dbFileUrl = FileLocator.toFileURL(dbBundleResourceUrl);
        
        ISoftIocConfigurator cfg = new BasicSoftIocConfigurator().with(new File(dbFileUrl.getFile()));
        _softIoc = new SoftIoc(cfg);
        _softIoc.start();
        
        // ATTENTION: dont use EpicsPlugin.ID, since then the bundle is activated and the default prefs
        // are read immediately
        IEclipsePreferences prefs = new DefaultScope().getNode("org.csstudio.platform.libs.epics");
        prefs.put("use_pure_java", "false");
    }
    
    
    @Test
    public void testMdelForAiRecord() throws Exception {
        testPVMdel("SoftIocTest:adelVsMdel_ai", "VALUE", MDEL);
    }
    @Test
    public void testMdelForCalcRecord() throws Exception {
        testPVMdel("SoftIocTest:adelVsMdel", "VALUE", MDEL);
    }
    @Test
    public void testAdelForAiRecord() throws Exception {
        testPVMdel("SoftIocTest:adelVsMdel_ai", "ARCHIVE", ADEL);
    }
    @Test
    public void testAdelForCalcRecord() throws Exception {
        testPVMdel("SoftIocTest:adelVsMdel", "ARCHIVE", ADEL);
    }

    private void testPVMdel(@Nonnull final String pvName, 
                            @Nonnull final String monitorMode,
                            @Nonnull final Double expDeadband) throws Exception {
        PV pv = new MyEpicsPVFactory().createPV(pvName, monitorMode);
        addListenerAndRunPV(pv);
        checkForUpdateSensitivity(expDeadband);
    }

    private void addListenerAndRunPV(@Nonnull final PV pv) throws Exception, InterruptedException {
        pv.addListener(new TestListener());
        pv.start();
        while(true) {
            synchronized (_values) {
                if (_values.size() >= 2) {
                    break;
                }
            }
            Thread.sleep(100l);
        }
        pv.stop();
    }

    private void checkForUpdateSensitivity(final double deadband) {
        synchronized (_values) {
            Assert.assertTrue(_values.size() > 1);
            Double lastValue = _values.get(0) - (deadband + 0.1);
            for (Double value : _values) {
                Assert.assertTrue(Math.abs(value - lastValue) > deadband);
                lastValue = value;
            }
            _values.clear();
        }
    }
    
    @After
    public void stopSoftIoc() throws IOException {
        _softIoc.stop();
    }
}
