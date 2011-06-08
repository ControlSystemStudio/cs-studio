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
import java.util.Iterator;
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
 * Attention! <br/>
 * Epics does not guarantee that a connection to an EPICS PV with ADEL/MDEL == x yields a 
 * value stream of i(0)..i(n), in which <br/>
 * <code>abs(i(j)-i(j+1)) >= x, 0 < j < n-1</code> <br/>
 * is always true. The very first value abs(i(0)-i(1)) may not be larger than the specified deadband.
 * 
 * That is due to the internals of Epics. On connection the very first update event is always the 
 * most recent value - NOT the most recent value according to the 'ARCHIVE' or 'MONITOR' connection. 
 * Only from the second value update on, those values are delivered that correspond to the ARCHIVE 
 * or MONITOR fields in the IOC.
 * 
 * Hence, two identically configured connections that observe the very same PV may yield 
 * different value streams for identical time intervals depending on when precisely they have been
 * started (and stopped and restarted and so on), AND they do not ensure that consecutive values present
 * in the delivered stream feature the configured deadband either!
 * 
 * @author bknerr
 * @since 31.05.2011
 */
public class PvMdelVsAdelHeadlessTest {
    
    private static Double ADEL = 1.1;
    private static Double MDEL = 0.9;
    
    private SoftIoc _softIoc;

    private final class TestListener implements PVListener {
        public final List<Double> _values = Lists.newLinkedList();

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
                Double v = Double.valueOf(value.getValue());
                _values.add(v);
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
        
        // ATTENTION: dont use EpicsPlugin.ID, since then that bundle is activated and the default prefs
        // are read immediately into the EpicsPlugin singleton. 
        IEclipsePreferences prefs = new DefaultScope().getNode("org.csstudio.platform.libs.epics");
        // Then this 
        prefs.put("use_pure_java", "false");
    }
    
    
    @Test
    public void testMdelForCalcRecord() throws Exception {
        testPV("SoftIocTest:adelVsMdel", "VALUE", MDEL);
    }
    @Test
    public void testAdelForCalcRecord() throws Exception {
        testPV("SoftIocTest:adelVsMdel", "ARCHIVE", ADEL);
    }
    @Test
    public void testMdelForAiRecord() throws Exception {
        testPV("SoftIocTest:adelVsMdel_ai", "VALUE", MDEL);
    }
    @Test
    public void testAdelForAiRecord() throws Exception {
        testPV("SoftIocTest:adelVsMdel_ai", "ARCHIVE", ADEL);
    }

    
    private void testPV(@Nonnull final String pvName, 
                        @Nonnull final String monitorMode,
                        @Nonnull final Double expDeadband) throws Exception {
        PV pv = new MyEpicsPVFactory().createPV(pvName, monitorMode);
        TestListener listener = addListenerAndRunPV(pv);
        checkForUpdateSensitivity(listener._values, expDeadband);
    }

    private TestListener addListenerAndRunPV(@Nonnull final PV pv) throws Exception, InterruptedException {
        final TestListener listener = new TestListener();
        pv.addListener(listener);
        pv.start();
        while(true) {
            synchronized (listener._values) {
                if (listener._values.size() >= 4) { // at least 4 values
                    break;
                }
            }
            Thread.sleep(100l);
        }
        pv.stop();
        
        return listener;
    }

    private void checkForUpdateSensitivity(final List<Double> values, final double deadband) {
        synchronized (values) {
            try {
                Assert.assertTrue(values.size() > 2); 
                Iterator<Double> iterator = values.iterator();
                // forget the first value, which may or may not adhere to the specified deadband 
                iterator.next();
                // get the second value as first value, and init last value 
                Double lastValue = iterator.next() - (deadband + 0.1);
                for (;iterator.hasNext();) {
                    Double value = (Double) iterator.next();
                    Assert.assertTrue(Math.abs(value - lastValue) > deadband);
                    
                }
            } finally {
                values.clear();
            }
        }
    }
    
    @After
    public void stopSoftIoc() throws IOException {
        _softIoc.stop();
    }
}
