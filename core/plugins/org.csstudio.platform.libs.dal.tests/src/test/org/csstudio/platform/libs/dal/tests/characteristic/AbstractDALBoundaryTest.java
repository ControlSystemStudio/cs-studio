package org.csstudio.platform.libs.dal.tests.characteristic;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.DynamicValueListener;
import org.epics.css.dal.DynamicValueProperty;
import org.epics.css.dal.SimpleProperty;
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
 * The abstract test class provides for registering and deregistering of Channel- and DynamicValueListeners.
 * It also allows for collecting results and checking the correctness.
 *
 * @author jpenning
 */
@SuppressWarnings("unchecked")
public abstract class AbstractDALBoundaryTest extends TestCase {

    private static final String IP_DESY_PRODUCTION = "131.169.115.234 131.169.115.236";
    private static final String IP_DESY_KRYKPCGASTA = "131.169.109.56";
    private static final String IP_DESY_MKSHERAZK = "131.169.115.247";
    private static final String LOCALHOST = "127.0.0.1";
    private static final String EPICS_CHANNEL_ADDR = LOCALHOST;

    protected static final long SLEEP_TIME_MSEC = 3*2000;
    protected static final String DOES_NOT_EXIST_NAME = "DOES_NOT_EXIST";
    protected static final String SAW_CALC_0 = "SawCalc0";
    protected static final String CONSTANT_PV = "ConstantPV";
    protected static final String CONSTANT_PV2 = "ConstantPV2";
    protected static final String PASSIVE_PV = "ConstantPVPassive";
    protected static final String STRING_IN_PV = "DAL-TestStringIn_si";

    // Object under test
    private SimpleDALBroker _broker;

    // Store data for deregistering
    private Map<String, ConnectItem> _name2connectItem;

    @Override
    protected void setUp() throws Exception {

        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", EPICS_CHANNEL_ADDR);

        // do not multicast
        System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list", "NO");

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
        _broker = SimpleDALBroker.newInstance(new DefaultApplicationContext("setup in AbstractDALBoundaryTest"));

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
        //_broker.releaseAll();
        _broker = null;
    }

    protected final ChannelListenerCallbackResults registerChannelListenerForPV(final String name) throws InstantiationException,
                                                                                                  CommonException {
        ConnectionParameters cp = newConnectionParameters(name, null, String.class);
        ChannelListenerCallbackResults result = new ChannelListenerCallbackResults();
        ChannelListener channelListener = new ChannelListenerImpl(result);
        getBroker().registerListener(cp, channelListener);
        ConnectItem item = new ConnectItem();
        item.channelListener = channelListener;
        item.connectionParameters = cp;
        _name2connectItem.put(name, item);
        return result;
    }

    protected final ChannelListenerCallbackResults registerChannelListenerForPV(final String name,
                                                                                final Class<?> type) throws InstantiationException,
                                                                                                    CommonException {
        ConnectionParameters cp = newConnectionParameters(name, null, type);
        ChannelListenerCallbackResults result = new ChannelListenerCallbackResults();
        ChannelListener channelListener = new ChannelListenerImpl(result);
        getBroker().registerListener(cp, channelListener);
        ConnectItem item = new ConnectItem();
        item.channelListener = channelListener;
        item.connectionParameters = cp;
        _name2connectItem.put(name, item);
        return result;
    }

    protected final DynamicValueListenerCallbackResults registerDynamicValueListenerForPV(final String name) throws InstantiationException,
                                                                                                            CommonException {
        return registerDynamicValueListenerForPV(name, String.class);
    }

    protected final DynamicValueListenerCallbackResults registerDynamicValueListenerForPV(final String name,
                                                                                          Class<?> type) throws InstantiationException,
                                                                                                        CommonException {
        ConnectionParameters cp = newConnectionParameters(name, null, type);
        DynamicValueListenerCallbackResults result = new DynamicValueListenerCallbackResults();
        DynamicValueListener listener = new DynamicValueListenerImpl(result);
        getBroker().registerListener(cp, listener);
        ConnectItem item = new ConnectItem();
        item.dynamicValueListener = listener;
        item.connectionParameters = cp;
        _name2connectItem.put(name, item);
        return result;
    }

    protected final DynamicValueListenerCallbackResults registerDynamicValueListenerForPV(final String name,
                                                                                          final String characteristic,
                                                                                          Class<?> type) throws InstantiationException,
                                                                                                        CommonException {
        ConnectionParameters cp = newConnectionParameters(name, characteristic, type);
        DynamicValueListenerCallbackResults result = new DynamicValueListenerCallbackResults();
        DynamicValueListener listener = new DynamicValueListenerImpl(result);
        getBroker().registerListener(cp, listener);
        ConnectItem item = new ConnectItem();
        item.dynamicValueListener = listener;
        item.connectionParameters = cp;
        _name2connectItem.put(name, item);
        return result;
    }

    protected void deregisterListenerForPV(final String name) throws InstantiationException,
                                                             CommonException {
        ConnectItem item = _name2connectItem.get(name);
        assert item != null : "error deregistering pv " + name;

        if (item.channelListener != null) {
            getBroker().deregisterListener(item.connectionParameters, item.channelListener);
        } else {
            getBroker().deregisterListener(item.connectionParameters, item.dynamicValueListener);
        }
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

    protected void checkConnected(final ChannelListenerCallbackResultItem result,
                                  final int count,
                                  final String name,
                                  final String value,
                                  final String severityInfo,
                                  final String alarmInfo) {
       checkConnected(result, count, 0, name, value, severityInfo, alarmInfo);
    }

    /*
     * Extended variant of checkConnected method above, which allows for a count of
     * events, which differs from the actual count for delta.
     */
    protected void checkConnected(final ChannelListenerCallbackResultItem result,
    		final int count,
    		final int delta,
    		final String name,
    		final String value,
    		final String severityInfo,
    		final String alarmInfo) {
    	assertTrue(Math.abs(count-result.count) <= delta);

        assertEquals(name, result.channel.getUniqueName());

        // TODO (jpenning) simple dal api: Is hasValue precondition for access to the value (eg. via stringValue)?
        assertTrue(result.channel.getProperty().getCondition().hasValue());
        assertEquals(value, result.channel.getData().stringValue());

        assertEquals(ConnectionState.CONNECTED, result.channel.getProperty().getConnectionState());
        assertEquals("CONNECTED", result.channel.getStateInfo());

        assertEquals(severityInfo, result.channel.getData().getSeverity().getSeverityInfo());
        assertEquals(alarmInfo, result.channel.getData().getSeverity().descriptionToString());
    }

    protected void checkConnectedWithCondition(final DynamicValuelListenerCallbackResultItem result,
                                  final int count,
                                  final String name,
                                  final String value,
                                  final String severityInfo,
                                  final String alarmInfo) {
        checkConnected(result, count, name, value, severityInfo, alarmInfo);

        // TODO (jpenning) simple dal api: What does the condition tell me?
        assertFalse(result.event.getCondition().isNormal());
        assertFalse(result.event.getCondition().isOK());
        assertFalse(result.event.getCondition().isLinkNotAvailable());
    }

    protected void checkConnected(final DynamicValuelListenerCallbackResultItem result,
                                  final int count,
                                  final String name,
                                  final String value,
                                  final String severityInfo,
                                  final String alarmInfo) {
        assertEquals(count, result.count);

        // TODO (jpenning) simple dal api: Which one is correct / preferred?
        assertEquals(name, result.event.getProperty().getUniqueName());
        //        assertEquals(name, result.event.getData().getMetaData().getName());

        assertTrue(result.event.getProperty().getCondition().hasValue());
        assertEquals(value, result.event.getData().stringValue());

        assertEquals(ConnectionState.CONNECTED, ((DynamicValueProperty) result.event.getProperty())
                .getConnectionState());
        assertTrue(result.event.getProperty().isConnected());

        assertEquals(severityInfo, result.event.getData().getSeverity().getSeverityInfo());
        assertEquals(alarmInfo, result.event.getData().getSeverity().descriptionToString());
        assertEquals(alarmInfo, result.event.getCondition().getDescription());
    }

    private static class ConnectItem {
        public ChannelListener channelListener = null;
        public DynamicValueListener dynamicValueListener = null;
        public ConnectionParameters connectionParameters = null;
    }

    protected static class ChannelListenerCallbackResultItem {
        public AnyDataChannel channel = null;
        public int count = 0;

        public boolean wasCalled() {
            return count > 0;
        }
    }

    protected static class ChannelListenerCallbackResults {
        public ChannelListenerCallbackResultItem dataUpdate = new ChannelListenerCallbackResultItem();
        public ChannelListenerCallbackResultItem stateUpdate = new ChannelListenerCallbackResultItem();
    }

    private static class ChannelListenerImpl implements ChannelListener {

        private final ChannelListenerCallbackResults _results;

        public ChannelListenerImpl(final ChannelListenerCallbackResults results) {
            _results = results;
        }

        public void channelDataUpdate(final AnyDataChannel channel) {
            _results.dataUpdate.channel = channel;
            _results.dataUpdate.count++;
        }

        public void channelStateUpdate(final AnyDataChannel channel) {
            _results.stateUpdate.channel = channel;
            _results.stateUpdate.count++;
        }

    }

    protected static class DynamicValuelListenerCallbackResultItem {
        public DynamicValueEvent event = null;
        public int count = 0;

        public boolean wasCalled() {
            return count > 0;
        }
    }

    protected static class DynamicValueListenerCallbackResults {
        DynamicValuelListenerCallbackResultItem conditionChange = new DynamicValuelListenerCallbackResultItem();
        DynamicValuelListenerCallbackResultItem errorResponse = new DynamicValuelListenerCallbackResultItem();
        DynamicValuelListenerCallbackResultItem timelagStarts = new DynamicValuelListenerCallbackResultItem();
        DynamicValuelListenerCallbackResultItem timelagStops = new DynamicValuelListenerCallbackResultItem();
        DynamicValuelListenerCallbackResultItem timeoutStarts = new DynamicValuelListenerCallbackResultItem();
        DynamicValuelListenerCallbackResultItem timeoutStops = new DynamicValuelListenerCallbackResultItem();
        DynamicValuelListenerCallbackResultItem valueChanged = new DynamicValuelListenerCallbackResultItem();
        DynamicValuelListenerCallbackResultItem valueUpdated = new DynamicValuelListenerCallbackResultItem();
    }

    private static class DynamicValueListenerImpl implements
            DynamicValueListener<String, SimpleProperty<String>> {

        private final DynamicValueListenerCallbackResults _results;

        public DynamicValueListenerImpl(final DynamicValueListenerCallbackResults results) {
            _results = results;
        }

        public void conditionChange(final DynamicValueEvent<String, SimpleProperty<String>> event) {
            updateResult(_results.conditionChange, event);
        }

        public void errorResponse(final DynamicValueEvent<String, SimpleProperty<String>> event) {
            updateResult(_results.errorResponse, event);
        }

        public void timelagStarts(final DynamicValueEvent<String, SimpleProperty<String>> event) {
            updateResult(_results.timelagStarts, event);
        }

        public void timelagStops(final DynamicValueEvent<String, SimpleProperty<String>> event) {
            updateResult(_results.timelagStops, event);
        }

        public void timeoutStarts(final DynamicValueEvent<String, SimpleProperty<String>> event) {
            updateResult(_results.timeoutStarts, event);
        }

        public void timeoutStops(final DynamicValueEvent<String, SimpleProperty<String>> event) {
            updateResult(_results.timeoutStops, event);
        }

        public void valueChanged(final DynamicValueEvent<String, SimpleProperty<String>> event) {
            updateResult(_results.valueChanged, event);
        }

        public void valueUpdated(final DynamicValueEvent<String, SimpleProperty<String>> event) {
            updateResult(_results.valueUpdated, event);
        }

        private void updateResult(final DynamicValuelListenerCallbackResultItem item,
                                  final DynamicValueEvent<String, SimpleProperty<String>> event) {
            item.event = event;
            item.count++;
        }

    }

}