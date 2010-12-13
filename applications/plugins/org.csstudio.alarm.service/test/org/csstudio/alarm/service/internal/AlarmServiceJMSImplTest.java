package org.csstudio.alarm.service.internal;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.IAlarmInitItem;
import org.csstudio.alarm.service.declaration.IAlarmMessage;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.epics.css.dal.impl.DefaultApplicationContext;
import org.epics.css.dal.simple.SimpleDALBroker;
import org.epics.css.dal.spi.Plugs;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for retrieval of the initial alarm state of not existing pvs.
 */
public class AlarmServiceJMSImplTest {
    
    private static final String IP_DESY_PRODUCTION = "131.169.115.234 131.169.115.236";
    private static final String IP_DESY_KRYKPCGASTA = "131.169.109.56";
    private static final String IP_DESY_MKSHERAZK = "131.169.115.247";
    private static final String LOCALHOST = "127.0.0.1";
    private static final String EPICS_CHANNEL_ADDRESS = LOCALHOST;
    
    @Before
    public void setupEnvironment() throws Exception {
        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", EPICS_CHANNEL_ADDRESS);
        
        // do not multicast
        System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list", "NO");
        
        // use common executor
        System.setProperty("EPICSPlug.property.use_common_executor", "TRUE");
        
        // Channel access answers after 100msec, so we will only wait twice as long
        // System.setProperty(Plugs.INITIAL_CONNECTION_TIMEOUT, "200"); // This is default
        
        // EPICS plug
        System.setProperty(Plugs.PLUGS, "EPICS");
        System.setProperty(Plugs.PLUGS_DEFAULT, "EPICS");
        System.setProperty(Plugs.PLUG_PROPERTY_FACTORY_CLASS + "EPICS",
                           "org.epics.css.dal.epics.PropertyFactoryImpl");
    }
    
    /**
     * This test retrieves a lot of not-existing pvs and checks that they all have been reported as such.
     */
    @SuppressWarnings("synthetic-access")
    @Test
    public void testRetrieveInitialState() throws Exception {
        boolean failed = false;
        
        IAlarmService service = new AlarmServiceJMSImplForUnitTest();
        List<IAlarmInitItem> initItems = new ArrayList<IAlarmInitItem>();
        addInitItems(initItems);
        service.retrieveInitialState(initItems);
        
        for (IAlarmInitItem item : initItems) {
            boolean wasInitialized = ((InitItemForTest) item)._wasInitialized;
            boolean wasNotFound = ((InitItemForTest) item)._wasNotFound;
            if (!wasInitialized && !wasNotFound) {
                // System.out.println(item.getPVName() + " was not processed");
                failed = true;
            }
        }
        Assert.assertFalse(failed);
    }
    
    private void addInitItems(@Nonnull final List<IAlarmInitItem> initItems) {
        for (int i = 0; i < 1000; i++) {
            initItems.add(new InitItemForTest("Not existing pv " + i));
        }
    }
    
    /**
     * init item representing a real pv
     */
    private static class InitItemForTest implements IAlarmInitItem {
        
        private final String _pvName;
        public boolean _wasInitialized = false;
        public boolean _wasNotFound = false;
        
        public InitItemForTest(@Nonnull final String pvName) {
            _pvName = pvName;
        }
        
        @Override
        public String getPVName() {
            return _pvName;
        }
        
        @Override
        public void init(IAlarmMessage alarmMessage) {
            _wasInitialized = true;
        }
        
        @Override
        public void notFound(String pvName) {
            _wasNotFound = true;
        }
        
    }
    
    /**
     * Overrides the creation of the simple dal broker to make it unit-testable.
     */
    private static class AlarmServiceJMSImplForUnitTest extends AlarmServiceJMSImpl {
        
        @Override
        protected int getPvChunkSize() {
            return 100;
        }
        
        @Override
        protected int getPvChunkWaitMsec() {
            return 500;
        }
        
        @Override
        protected int getPvRegisterWaitMsec() {
            return 0;
        }
        
        @Override
        protected SimpleDALBroker newSimpleDALBroker() {
            return SimpleDALBroker.newInstance(new DefaultApplicationContext("test"));
        }
    }
}
