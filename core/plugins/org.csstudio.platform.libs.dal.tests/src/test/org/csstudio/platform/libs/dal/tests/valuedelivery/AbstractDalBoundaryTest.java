package org.csstudio.platform.libs.dal.tests.valuedelivery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.epics.css.dal.DataExchangeException;
import org.epics.css.dal.DynamicValueAdapter;
import org.epics.css.dal.DynamicValueCondition;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.DynamicValueListener;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.context.ConnectionEvent;
import org.epics.css.dal.context.ConnectionState;
import org.epics.css.dal.context.LinkAdapter;
import org.epics.css.dal.context.LinkListener;
import org.epics.css.dal.epics.EPICSPlug;
import org.epics.css.dal.impl.DefaultApplicationContext;
import org.epics.css.dal.simple.AnyData;
import org.epics.css.dal.simple.AnyDataChannel;
import org.epics.css.dal.simple.ChannelListener;
import org.epics.css.dal.simple.ConnectionParameters;
import org.epics.css.dal.simple.MetaData;
import org.epics.css.dal.simple.RemoteInfo;
import org.epics.css.dal.simple.Severity;
import org.epics.css.dal.simple.SimpleDALBroker;
import org.epics.css.dal.spi.Plugs;

import com.cosylab.util.CommonException;

/**
 * The abstract test class provides for registering and deregistering of ChannelListeners.
 * It also allows for collecting results and checking the correctness.
 *
 * @author jpenning
 */
abstract class AbstractDalBoundaryTest extends TestCase {
    
    private static final boolean DISPLAY_CALLBACK_SEQUENCE = false;
    
    private static final String IP_DESY_PRODUCTION = "131.169.115.234 131.169.115.236";
    private static final String IP_DESY_KRYKPCGASTA = "131.169.109.56";
    private static final String IP_DESY_MKSHERAZK = "131.169.115.247";
    private static final String IP_DESY_HELGES_PC = "131.169.115.201";
    private static final String LOCALHOST = "127.0.0.1";
    private static final String EPICS_CHANNEL_ADDR = IP_DESY_MKSHERAZK;
    
    protected static final long SLEEP_TIME_MSEC = 3000;
    protected static final String DOES_NOT_EXIST_NAME = "DOES_NOT_EXIST";
    protected static final String SAW_CALC_0 = "SawCalc0";
    protected static final String CONSTANT_PV = "ConstantPV";
    protected static final String CONSTANT_PV2 = "ConstantPV2";
    protected static final String CONSTANT_PV_PASSIVE = "ConstantPVPassive";
    protected static final String STRING_IN_PV = "DAL-TestStringIn_si";
    
    // Object under test
    private SimpleDALBroker _broker;
    
    // Store data for deregistering
    private Map<String, ConnectItem> _name2connectItem;
    
    protected List<Result> _results = null;
    
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
        
        _results = new ArrayList<Result>();
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
        _results = null;
    }
    
    protected final void registerChannelListenerForPV(final String name) throws InstantiationException,
                                                                        CommonException {
        registerChannelListenerForPV(name, String.class);
    }
    
    protected final void registerChannelListenerForPV(final String name, final Class<?> type) throws InstantiationException,
                                                                                             CommonException {
        ConnectionParameters cp = newConnectionParameters(name, null, type);
        ChannelListener channelListener = new ChannelListenerImpl();
        ConnectItem item = new ConnectItem();
        item.channelListener = channelListener;
        item.connectionParameters = cp;
        item.dvListener= new DynamicValueAdapter() {
        	@Override
        	public void conditionChange(DynamicValueEvent event) {
        		System.out.println(event);
        		//System.out.println(event.getError());
        	}
        };
        _name2connectItem.put(name, item);
        getBroker().registerListener(item.connectionParameters, item.channelListener);
        getBroker().registerListener(item.connectionParameters, item.dvListener);
    }
    
    protected void deregisterListenerForPV(final String name) throws InstantiationException,
                                                             CommonException {
        ConnectItem item = _name2connectItem.remove(name);
        assertNotNull("error deregistering pv " + name, item);
        
        getBroker().deregisterListener(item.connectionParameters, item.channelListener);
        getBroker().deregisterListener(item.connectionParameters, item.dvListener);
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
    	public DynamicValueListener dvListener= null;
        public ChannelListener channelListener = null;
        public ConnectionParameters connectionParameters = null;
    }
    
    private class ChannelListenerImpl implements ChannelListener {
        
        private int _callNumber = 0;
        
        public void channelDataUpdate(final AnyDataChannel channel) {
            int currentCallNumber = traceCallbackStart("DATA", channel);
            
            //            _results.add(new Result("channelDataUpdate", channel));
            //            printChannel("channelDataUpdate", channel);
            
            if (channel.getProperty().isOperational()) {
                System.out.println("--> isMetaDataInitialized " + channel.getProperty().isMetaDataInitialized());
                printMetaData(channel.getData().getMetaData());
                printData(channel.getData());
            }
            traceCallbackEnd("DATA", currentCallNumber, channel);
        }
        
        public void channelStateUpdate(final AnyDataChannel channel) {
            int currentCallNumber = traceCallbackStart("STATE", channel);

            _results.add(new Result("channelStateUpdate", channel));
            //            printChannel("channelStateUpdate", channel);
            if (channel.getProperty().isOperational()) {
                System.out.println("--> isMetaDataInitialized " + channel.getProperty().isMetaDataInitialized());
                printMetaData(channel.getData().getMetaData());
                printData(channel.getData());
            }
            
            traceCallbackEnd("STATE", currentCallNumber, channel);
        }
        
        private int traceCallbackStart(String callbackName, AnyDataChannel channel) {
            int result = 0;
            if (DISPLAY_CALLBACK_SEQUENCE) {
                synchronized (this) {
                    result = _callNumber;
                    _callNumber++;
                    System.out.println("===== " + callbackName + " callback " + result
                            + " for '" + channel.getUniqueName() + "' starts (" + channel.getProperty().getConnectionState() + ")");
                }
            }
            return result;
        }
        
        private void traceCallbackEnd(String callbackName, int currentCallNumber, AnyDataChannel channel) {
            if (DISPLAY_CALLBACK_SEQUENCE) {
                System.out.println("===== " + callbackName + " callback " + currentCallNumber
                        + " for '" + channel.getUniqueName() + "'ends");
            }
        }
        
        private void printChannel(String calledMethod, AnyDataChannel channel) {
            System.out.println("connection state: " + channel.getProperty().getConnectionState());
            if (channel.getProperty().getConnectionState() != ConnectionState.DESTROYED) {
                System.out.println("getStateInfo: " + channel.getStateInfo());
                System.out.println("isConnected: " + channel.isConnected() + " isRunning: "
                        + channel.isRunning());
                System.out.println("isMetaDataInitialized: " + channel.isMetaDataInitialized());
                // System.out.println("isWriteAllowed: " + channel.isWriteAllowed());
                //                try {
                //                    printProperty(channel.getProperty());
                //                } catch (DataExchangeException e) {
                //                    throw new RuntimeException(e);
                //                }
            }
        }
        
        private void printProperty(DynamicValueProperty<?> property) throws DataExchangeException {
            System.out.println("  == property ==");
            System.out.println("  connection state: " + property.getConnectionState());
            System.out.println("  is connected: " + property.isConnected());
            if (property.isConnected()) {
                System.out.println("  description: " + property.getDescription());
                System.out.println("  state info: " + property.getStateInfo());
                System.out.println("  condition:" + property.getCondition());
                
                System.out.print("  characteristic names: ");
                for (String characteristicName : property.getCharacteristicNames()) {
                    System.out.print(characteristicName + " ");
                }
                System.out.println();
                
                System.out.println("  characteristic 'RTYP': "
                        + ((String[]) property.getCharacteristic("RTYP"))[0]);
                System.out.println("  data type: " + property.getDataType());
            }
        }
        
        private void printMetaData(MetaData metaData) {
            System.out.println("  == meta data ==");
            System.out.println("  data type: " + metaData.getDataType());
            System.out.println("  description: " + metaData.getDescription());
            System.out.println("  format: " + metaData.getFormat());
            System.out.println("  alarm low: " + metaData.getAlarmLow());
            System.out.println("  warn low: " + metaData.getWarnLow());
            System.out.println("  warn high: " + metaData.getWarnHigh());
            System.out.println("  alarm high: " + metaData.getAlarmHigh());
        }
        
        private void printData(AnyData anyData) {
            System.out.println("  == data ==");
            System.out.println("  toString: " + anyData);
            System.out.println("  status: " + anyData.getStatus());
            System.out.println("  timestamp: " + anyData.getTimestamp());
            System.out.println("  isValid: " + anyData.isValid());
            printSeverity(anyData.getSeverity());
            if (anyData.isValid()) {
                System.out.println("  anyValue: " + anyData.anyValue());
            }
        }
        
        private void printSeverity(Severity severity) {
            System.out.println("    == severity ==");
            System.out.println("    toString: " + severity);
            System.out.println("    info: " + severity.getSeverityInfo());
            System.out.println("    description string: " + severity.descriptionToString());
            System.out.println("    hasValue: " + severity.hasValue());
        }
        
    }
    
    protected static class Result {
        protected final String calledMethod;
        protected final ConnectionState connectionState;
        protected final DynamicValueCondition condition;
        protected Object anyValue;
        
        public Result(String calledMethod, AnyDataChannel channel) {
            this.calledMethod = calledMethod;
            this.connectionState = channel.getProperty().getConnectionState();
            this.condition = channel.getProperty().getCondition();
            if (connectionState == ConnectionState.OPERATIONAL) {
                anyValue = channel.getData().anyValue();
            }
        }
    }
    
    protected Result getResultFor(ConnectionState state) {
        Result found = null;
        for (Result result : _results) {
            if (result.connectionState == state) {
                found = result;
                break;
            }
        }
        return found;
    }
    
    protected boolean hasConnectionState(ConnectionState state) {
        return getResultFor(state) != null;
    }
    
}