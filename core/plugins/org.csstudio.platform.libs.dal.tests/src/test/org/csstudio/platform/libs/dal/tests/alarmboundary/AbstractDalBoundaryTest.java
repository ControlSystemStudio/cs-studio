package org.csstudio.platform.libs.dal.tests.alarmboundary;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.epics.css.dal.DynamicValueCondition;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.epics.EPICSPlug;
import org.epics.css.dal.impl.DefaultApplicationContext;
import org.epics.css.dal.simple.AnyDataChannel;
import org.epics.css.dal.simple.ChannelListener;
import org.epics.css.dal.simple.ConnectionParameters;
import org.epics.css.dal.simple.RemoteInfo;
import org.epics.css.dal.simple.SimpleDALBroker;
import org.epics.css.dal.spi.Plugs;

import com.cosylab.util.CommonException;

/**
 * The abstract test class provides for registering and deregistering of ChannelListeners.
 *
 * It also allows for collecting results and checking the correctness.
 *
 * Here is the list of pv used for the tests. Put them into your soft ioc as necessary.
record(calc, "ConstantPV") {
   field(DESC, "$")
   field(SCAN, "10 second")
   field(PHAS, "10")
   field(CALC, "A")
   field(INPA, "66")
   field(EGU, "Counts")
   field(HOPR, "100")
   field(LOPR, "0")
   field(HIHI, "70")
   field(LOLO, "15")
   field(HIGH, "65")
   field(LOW, "20")
   field(HHSV, "MAJOR")
   field(LLSV, "MAJOR")
   field(HSV, "MINOR")
   field(LSV, "MINOR")
}

 *
 *
 * @author jpenning
 * @since 18.4.2011
 */
abstract class AbstractDalBoundaryTest extends TestCase {
    
    private static final String LOCALHOST = "127.0.0.1";
    private static final String EPICS_CHANNEL_ADDR = LOCALHOST;
    
    protected static final long SLEEP_TIME_MSEC = 3000;
    protected static final String CONSTANT_PV_MODIFIED = "ConstantPVModified";
    
    // Object under test
    private SimpleDALBroker _broker;
    
    // Store data for deregistering
    private Map<String, ConnectItem> _name2connectItem;
    
    protected Result _stateResult = null;
    protected Result _dataResult = null;
    
    @Override
    protected void setUp() throws Exception {
        
        //System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", EPICS_CHANNEL_ADDR);
        
        // do not multicast
        //System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list", "NO");
        
        // use common executor
        System.setProperty(EPICSPlug.PROPERTY_USE_COMMON_EXECUTOR, "TRUE");
        
        // debug output in caj (see CAJConstants.CAJ_DEBUG)
        // System.setProperty("CAJ_DEBUG", "TRUE");
        
        // EPICS plug
        System.setProperty(Plugs.PLUGS, EPICSPlug.PLUG_TYPE);
        System.setProperty(Plugs.PLUGS_DEFAULT, EPICSPlug.PLUG_TYPE);
        System.setProperty(Plugs.PLUG_PROPERTY_FACTORY_CLASS + EPICSPlug.PLUG_TYPE,
                           org.epics.css.dal.epics.PropertyFactoryImpl.class.getName());
        
        // The configuration is read from the system properties
        _broker = SimpleDALBroker
                .newInstance(new DefaultApplicationContext("setup in AbstractDALBoundaryTest"));
        
        _name2connectItem = new HashMap<String, ConnectItem>();
    }
    
    protected void useJNI() {
        System.setProperty("EPICSPlug.use_jni", "true");
        
        // path to jca.dll is found using java.library.path
        // System.setProperty("java.library.path", "libs/win32/x86"); // ahem, no, I put jca.dll in the root of the project.
        
        // path to Com.dll and ca.dll is hardcoded to windows
        System.setProperty("gov.aps.jca.jni.epics.win32-x86.library.path", "libs/win32/x86");
    }
    
    @Override
    protected void tearDown() throws Exception {
        // free all resources and drop the broker
        _broker.releaseAll();
        _broker = null;
        _stateResult = null;
        _dataResult = null;
    }
    
    protected final void registerChannelListenerForPV(final String name) throws InstantiationException,
                                                                        CommonException {
        registerChannelListenerForPV(name, Double.class);
    }
    
    protected final void registerChannelListenerForPV(final String name, final Class<?> type) throws InstantiationException,
                                                                                             CommonException {
        ConnectionParameters cp = newConnectionParameters(name, null, type);
        ChannelListener channelListener = new ChannelListenerImpl();
        ConnectItem item = new ConnectItem();
        item.channelListener = channelListener;
        item.connectionParameters = cp;
        _name2connectItem.put(name, item);
        getBroker().registerListener(item.connectionParameters, item.channelListener);
    }
    
    protected void deregisterListenerForPV(final String name) throws InstantiationException,
                                                             CommonException {
        ConnectItem item = _name2connectItem.remove(name);
        assertNotNull("error deregistering pv " + name, item);
        
        getBroker().deregisterListener(item.connectionParameters, item.channelListener);
    }
    
    protected SimpleDALBroker getBroker() {
        return _broker;
    }
    
    protected ConnectionParameters newConnectionParameters(final String pvName,
                                                           final String characteristic,
                                                           final Class<?> type) {
        return new ConnectionParameters(newRemoteInfo(pvName, characteristic), type);
    }
    
    protected RemoteInfo newRemoteInfo(final String pvName, final String characteristic) {
        return new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX + "EPICS", pvName, characteristic, null);
    }
    
    private static class ConnectItem {
        public ChannelListener channelListener = null;
        public ConnectionParameters connectionParameters = null;
    }
    
    private class ChannelListenerImpl implements ChannelListener {
        
        public void channelDataUpdate(final AnyDataChannel channel) {
            _dataResult = new Result("channelDataUpdate", channel);
        	//Thread.dumpStack();
        }
        
        public void channelStateUpdate(final AnyDataChannel channel) {
            _stateResult = new Result("channelStateUpdate", channel);
        	//Thread.dumpStack();
            //System.out.println("channelStateUpdate " + _stateResult.severityInfo);
        }
        
    }
    
    protected static class Result {
        protected final String calledMethod;
        protected final ConnectionState connectionState;
        protected final DynamicValueCondition condition;
        protected Object anyValue;
        protected boolean isMetaDataInitialized;
        protected Object alarmHigh;
        protected String severityInfo;
        
        public Result(String calledMethod, AnyDataChannel channel) {
            this.calledMethod = calledMethod;
            this.connectionState = channel.getProperty().getConnectionState();
            this.condition = channel.getProperty().getCondition();
            this.isMetaDataInitialized = channel.getProperty().isMetaDataInitialized();
            this.anyValue = channel.getData().anyValue();
            this.alarmHigh = channel.getData().getMetaData().getAlarmHigh();
            this.severityInfo = channel.getData().getSeverity().getSeverityInfo();
            
            System.err.println("R "+calledMethod+" "+connectionState+" "+isMetaDataInitialized+" "+severityInfo+" "+anyValue);
        }
    }
    
}