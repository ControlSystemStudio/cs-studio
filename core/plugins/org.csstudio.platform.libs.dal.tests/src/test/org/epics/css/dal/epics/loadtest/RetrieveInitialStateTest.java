package org.epics.css.dal.epics.loadtest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.epics.css.dal.DynamicValueAdapter;
import org.epics.css.dal.DynamicValueEvent;
import org.epics.css.dal.DynamicValueListener;
import org.epics.css.dal.epics.EPICSPlug;
import org.epics.css.dal.simple.ConnectionParameters;
import org.epics.css.dal.simple.RemoteInfo;
import org.epics.css.dal.simple.SimpleDALBroker;
import org.epics.css.dal.spi.Plugs;

import com.cosylab.util.CommonException;

@SuppressWarnings("unchecked")
public class RetrieveInitialStateTest extends TestCase {

    // Object under test
    private SimpleDALBroker _broker;

    // Store of registered channels, used for callback processing and deregistering
    private List<ChannelItem> _channelItems;

    public void setUp() throws Exception {
        // Gateway
//        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", "131.169.115.234 131.169.115.236");
//        System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list","NO");

        // EPICS plug
        System.setProperty(Plugs.PLUGS, EPICSPlug.PLUG_TYPE);
        System.setProperty(Plugs.PLUGS_DEFAULT, EPICSPlug.PLUG_TYPE);
        System.setProperty(Plugs.PLUG_PROPERTY_FACTORY_CLASS + EPICSPlug.PLUG_TYPE,
                           org.epics.css.dal.epics.PropertyFactoryImpl.class.getName());

        _broker = SimpleDALBroker.getInstance();
    }

    public void testOkSlowlyEnough() throws InstantiationException, CommonException, InterruptedException {
        final int WAIT_FOR_CALLBACK_PROCESSING_MSEC = 2000;
        final int CHUNK_SIZE = 100;
        final int CHUNK_WAIT_MSEC = 200;
        final int EXISTING_PV_SIZE = 500;
        final int REQUESTED_PV_SIZE = 5000;

        performTest(WAIT_FOR_CALLBACK_PROCESSING_MSEC,
                    CHUNK_SIZE,
                    CHUNK_WAIT_MSEC,
                    EXISTING_PV_SIZE,
                    REQUESTED_PV_SIZE);
        // Everything is fine
    }

    public void testTooFewWaitTime() throws InstantiationException, CommonException, InterruptedException {
        final int WAIT_FOR_CALLBACK_PROCESSING_MSEC = 1000;
        final int CHUNK_SIZE = 250;
        final int CHUNK_WAIT_MSEC = 200;
        final int EXISTING_PV_SIZE = 500;
        final int REQUESTED_PV_SIZE = 5000;

        performTest(WAIT_FOR_CALLBACK_PROCESSING_MSEC,
                    CHUNK_SIZE,
                    CHUNK_WAIT_MSEC,
                    EXISTING_PV_SIZE,
                    REQUESTED_PV_SIZE);
        // This time not all callbacks have been delivered within one second.
        // (this test sometimes is fine)
    }

    public void testTooQuick() throws InstantiationException, CommonException, InterruptedException {
        final int WAIT_FOR_CALLBACK_PROCESSING_MSEC = 5000;
        final int CHUNK_SIZE = 500;
        final int CHUNK_WAIT_MSEC = 100;
        final int EXISTING_PV_SIZE = 500;
        final int REQUESTED_PV_SIZE = 5000;

        performTest(WAIT_FOR_CALLBACK_PROCESSING_MSEC,
                    CHUNK_SIZE,
                    CHUNK_WAIT_MSEC,
                    EXISTING_PV_SIZE,
                    REQUESTED_PV_SIZE);
        // The check tells that not all existing pvs were called back.
        // Unfortunately there is no error message (or we don't understand how to check for properly).
    }

    public void testTooMuch() throws InstantiationException, CommonException, InterruptedException {
        final int WAIT_FOR_CALLBACK_PROCESSING_MSEC = 5000;
        final int CHUNK_SIZE = 250;
        final int CHUNK_WAIT_MSEC = 200;
        final int EXISTING_PV_SIZE = 500;
        final int REQUESTED_PV_SIZE = 10000;

        performTest(WAIT_FOR_CALLBACK_PROCESSING_MSEC,
                    CHUNK_SIZE,
                    CHUNK_WAIT_MSEC,
                    EXISTING_PV_SIZE,
                    REQUESTED_PV_SIZE);
        // Here we get an out-of-memory-error while trying to create a new thread.
        // This is because we hit the OS limit. How could this be handled?
    }

    /**
     * @param waitForCallbackProcessingMsec wait after registering the pvs to allow for processing of the callbacks
     * @param chunkSize PVs are registered / deregistered in chunks of this size
     * @param chunkWaitMsec wait time after each chunk
     * @param existingPvSize the SoftIOC provides for the existing ConstantPVs
     * @param requestedPvSize amount of pvs requested (more than the SoftIOC delivers)
     * @throws InstantiationException
     * @throws CommonException
     * @throws InterruptedException
     */
    private void performTest(final int waitForCallbackProcessingMsec,
                            final int chunkSize,
                            final int chunkWaitMsec,
                            final int existingPvSize,
                            final int requestedPvSize) throws InstantiationException,
                                                      CommonException,
                                                      InterruptedException {

        Set<String> pvs = createPVNames(requestedPvSize);

        _channelItems = new ArrayList<ChannelItem>();

        registerAllPVs(pvs, chunkSize, chunkWaitMsec);

        waitFixedTime(waitForCallbackProcessingMsec);

        assertEquals(existingPvSize, checkForCallbacks());

        deregisterAllPVs(chunkSize, chunkWaitMsec);
    }

    private Set<String> createPVNames(final int REQUESTED_PV_SIZE) {
        Set<String> result = new HashSet<String>();

        for (int i = 0; i < REQUESTED_PV_SIZE; i++) {
            result.add(String.format("ConstantPV_%04d", i));
        }

        return result;
    }

    private void registerAllPVs(final Collection<String> pvs,
                                final int chunkSize,
                                final int chunkWaitMsec) throws InstantiationException,
                                                        CommonException,
                                                        InterruptedException {
        int i = 0;
        for (final String pv : pvs) {
            ChannelItem item = new ChannelItem(pv,
                                               newConnectionParameters(pv, null, String.class),
                                               newDynamicValueListener(pv));
            registerDynamicValueListener(item);
            i++;
            if ( (i % chunkSize) == 0) {
                waitFixedTime(chunkWaitMsec);
            }
        }
    }

    private void deregisterAllPVs(final int chunkSize, final int chunkWaitMsec) throws InstantiationException,
                                                                               CommonException,
                                                                               InterruptedException {
        int i = 0;
        for (ChannelItem item : _channelItems) {
            deRegisterDynamicValueListener(item);
            i++;
            if ( (i % chunkSize) == 0) {
                waitFixedTime(chunkWaitMsec);
            }
        }
    }

    private void waitFixedTime(final int delayInMsec) throws InterruptedException {
        Thread.sleep(delayInMsec);
    }

    private int checkForCallbacks() {
        int calledCount = 0;
        for (ChannelItem item : _channelItems) {
            if (item.isCalled()) {
                calledCount++;
            }
        }
        return calledCount;
    }

    private ConnectionParameters newConnectionParameters(final String pvName,
                                                         final String characteristic,
                                                         final Class<?> type) {
        return new ConnectionParameters(newRemoteInfo(pvName, characteristic), type);
    }

    private RemoteInfo newRemoteInfo(final String pvName, final String characteristic) {
        return new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX + "EPICS", pvName, characteristic, null);
    }

    private DynamicValueListener newDynamicValueListener(final String name) {
        return new DynamicValueListenerImplementation();
    }

    private void registerDynamicValueListener(final ChannelItem item) throws InstantiationException,
                                                                     CommonException,
                                                                     InterruptedException {
        _broker.registerListener(item._connectionParameters, item._dynamicValueListener);
        synchronized (_channelItems) {
            _channelItems.add(item);
        }
    }

    private void deRegisterDynamicValueListener(final ChannelItem item) throws InstantiationException,
                                                                       CommonException {
        _broker.deregisterListener(item._connectionParameters, item._dynamicValueListener);
    }

    /**
     * DynamicValueListener used to record the callbacks
     */
    private final class DynamicValueListenerImplementation extends DynamicValueAdapter {

        @Override
        public void conditionChange(final DynamicValueEvent event) {
            checkIfCalled(event, "conditionChange");
        }

        @Override
        public void valueChanged(final DynamicValueEvent event) {
            assertTrue(event.getCondition().hasValue());
            checkIfCalled(event, "valueChanged");
        }

        private void checkIfCalled(final DynamicValueEvent event, final String callbackName) {
            if (event.getProperty().isConnected()) {

                if (event.getValue() != null) {
                    assertEquals("66", event.getData().stringValue());
                    assertEquals("WARNING", event.getData().getSeverity().getSeverityInfo());
                    assertEquals("HIGH_ALARM", event.getData().getSeverity().descriptionToString());
                    assertTrue(event.getCondition().hasValue());
                }

                synchronized (_channelItems) {
                    for (ChannelItem item : _channelItems) {
                        if (item._name.equals(event.getProperty().getUniqueName())) {
                            item.setCalled();
                        }
                    }
                }
            }
        }
    }

    /**
     * Store for recording callback processing and data for deregistration
     */
    private static class ChannelItem {
        String _name;
        ConnectionParameters _connectionParameters;
        DynamicValueListener _dynamicValueListener;
        private boolean _called;

        public ChannelItem(final String name,
                           final ConnectionParameters connectionParameters,
                           final DynamicValueListener dynamicValueListener) {
            _name = name;
            _connectionParameters = connectionParameters;
            _dynamicValueListener = dynamicValueListener;
        }

        public void setCalled() {
            _called = true;
        }

        public boolean isCalled() {
            return _called;
        }

    }

}
