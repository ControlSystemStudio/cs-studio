/**
 *
 */
package org.csstudio.platform.internal.simpledal;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.model.pvs.ValueType;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.IProcessVariableWriteListener;
import org.csstudio.platform.simpledal.ProcessVariableValueAdapter;
import org.csstudio.platform.simpledal.SettableState;
import org.csstudio.dal.Timestamp;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Sven Wende
 *
 */
public class ProcessVariableConnectionServiceTest {
    private static final class ConnectorMock extends AbstractConnector {
        protected Object _value = 2.0;
        protected Object _characteristicValue = 4;
        protected SettableState _settableState = SettableState.SETTABLE;

        private ConnectorMock(IProcessVariableAddress pvAddress, ValueType valueType) {
            super(pvAddress, valueType);
        }

        @Override
        protected void doDispose() throws Exception {

        }

        @Override
        protected void doGetCharacteristicAsynchronously(String characteristicId, ValueType valueType, IProcessVariableValueListener listener)
                throws Exception {
            listener.valueChanged(_characteristicValue, new Timestamp());
        }

        @Override
        protected Object doGetCharacteristicSynchronously(String characteristicId, ValueType valueType) throws Exception {
            return _characteristicValue;
        }

        @Override
        protected void doGetValueAsynchronously(IProcessVariableValueListener listener) throws Exception {
            listener.valueChanged(_value, new Timestamp());
        }

        @Override
        protected Object doGetValueSynchronously() throws Exception {
            return _value;
        }

        @Override
        protected SettableState doIsSettable() throws Exception {
            return _settableState;
        }

        @Override
        protected void doSetValueAsynchronously(Object value, IProcessVariableWriteListener listener) throws Exception {
            _value = value;
        }

        @Override
        protected boolean doSetValueSynchronously(Object value) throws Exception {
            _value = value;
            return true;
        }

        @Override
        protected void doInit() throws Exception {

        }
    }

    private ProcessVariableConnectionService _service;

    private IConnectorFactory _connectorFactory;

    private IProcessVariableAddress pv;

    private IProcessVariableAddress pvCharacteristic;

    private ConnectorMock connector;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        pv = ProcessVariableAdressFactory.getInstance().createProcessVariableAdress("dal-epics://unittest:ai");
        pvCharacteristic = ProcessVariableAdressFactory.getInstance().createProcessVariableAdress("dal-epics://unittest:ai[minimum]");

        connector = new ConnectorMock(pv, ValueType.DOUBLE);

        _connectorFactory = createMock(ConnectorFactory.class);
        expect(_connectorFactory.createConnector(eq(pv), eq(ValueType.DOUBLE))).andReturn(connector);
        expect(_connectorFactory.createConnector(eq(pvCharacteristic), eq(ValueType.DOUBLE))).andReturn(connector).anyTimes();
        replay(_connectorFactory);

        _service = new ProcessVariableConnectionService(_connectorFactory);
    }

    /**
     * Test method for
     * {@link org.csstudio.platform.internal.simpledal.ProcessVariableConnectionService#getConnectors()}.
     *
     * @throws Exception
     */
    @Test
    public final void testGetConnectors() throws Exception {
        Double value = _service.readValueSynchronously(pv, ValueType.DOUBLE);
        assertEquals(1, _service.getConnectors().size());
        assertTrue(_service.getConnectors().contains(connector));
    }

    @Test(timeout=1000)
    public final void testCleanupThread1() throws Exception {
        // temporary connectors are only blocked for a certain amount of time
        Double value = _service.readValueSynchronously(pv, ValueType.DOUBLE);
        assertEquals(1, _service.getConnectors().size());
        assertTrue(_service.getConnectors().contains(connector));
        // .. wait
        Thread.sleep(AbstractConnector.BLOCKING_TIMEOUT * 4);
        assertEquals(0, _service.getConnectors().size());

    }

    @Test(timeout=1000)
    public final void testCleanupThread2() throws Exception {
        // permanent connectors live until they are explicitly unregistered
        IProcessVariableValueListener listener = new ProcessVariableValueAdapter();

        // .. connect
        _service.register(listener, pv, ValueType.DOUBLE);
        assertEquals(1, _service.getConnectors().size());
        assertTrue(_service.getConnectors().contains(connector));

        // .. wait
        Thread.sleep(AbstractConnector.BLOCKING_TIMEOUT * 2);
        assertEquals(1, _service.getConnectors().size());
        assertTrue(_service.getConnectors().contains(connector));

        // .. disconnect explicitly
        _service.unregister(listener);
        Thread.sleep(AbstractConnector.BLOCKING_TIMEOUT * 2);
        assertEquals(0, _service.getConnectors().size());
    }

    @Test(timeout=1000)
    public final void testCleanupThread3() throws Exception {
        // permanent connectors live until they are no longer referenced (after
        // garbage collection=
        IProcessVariableValueListener listener = new ProcessVariableValueAdapter();

        // .. connect
        _service.register(listener, pv, ValueType.DOUBLE);

        assertEquals(1, _service.getConnectors().size());
        assertTrue(_service.getConnectors().contains(connector));

        // .. wait
        Thread.sleep(AbstractConnector.BLOCKING_TIMEOUT * 2);
        assertEquals(1, _service.getConnectors().size());
        assertTrue(_service.getConnectors().contains(connector));

        // .. stop referencing the listener and do garbage collection
        listener = null;
        System.gc();

        // .. wait
        Thread.sleep(AbstractConnector.BLOCKING_TIMEOUT * 2);
        assertEquals(0, _service.getConnectors().size());

    }

    /**
     * Test method for
     * {@link org.csstudio.platform.internal.simpledal.ProcessVariableConnectionService#readValueSynchronously(org.csstudio.platform.model.pvs.IProcessVariableAddress, org.csstudio.platform.model.pvs.ValueType)}.
     */
    @Test
    public final void testReadValueSynchronously() throws Exception {
        // .. get a value
        Double value = _service.readValueSynchronously(pv, ValueType.DOUBLE);
        assertEquals(connector._value, value);

        // .. get a characteristic
        value = _service.readValueSynchronously(pvCharacteristic, ValueType.DOUBLE);
        assertEquals(connector._characteristicValue, value);
    }

    /**
     * Test method for
     * {@link org.csstudio.platform.internal.simpledal.ProcessVariableConnectionService#readValueAsynchronously(org.csstudio.platform.model.pvs.IProcessVariableAddress, org.csstudio.platform.model.pvs.ValueType, org.csstudio.platform.simpledal.IProcessVariableValueListener)}.
     */
    @Test
    @Ignore("Not implemented yet!")
    public final void testReadValueAsynchronously() {
        // FIXME mz: 2008-12-09: Implementieren!!!
    }

    /**
     * Test method for
     * {@link org.csstudio.platform.internal.simpledal.ProcessVariableConnectionService#writeValueSynchronously(org.csstudio.platform.model.pvs.IProcessVariableAddress, java.lang.Object, org.csstudio.platform.model.pvs.ValueType)}.
     * @throws ConnectionException
     */
    @Test
    public final void testWriteValueSynchronously() throws ConnectionException {
        double newValue = 5.0;
        _service.writeValueSynchronously(pv, newValue, ValueType.DOUBLE);
        assertEquals(newValue, _service.readValueSynchronously(pv, ValueType.DOUBLE));
    }

    /**
     * Test method for
     * {@link org.csstudio.platform.internal.simpledal.ProcessVariableConnectionService#writeValueAsynchronously(org.csstudio.platform.model.pvs.IProcessVariableAddress, java.lang.Object, org.csstudio.platform.model.pvs.ValueType, IProcessVariableWriteListener)}.
     * @throws ConnectionException
     */
    @Test(timeout=1000)
    public final void testWriteValueAsynchronously() throws ConnectionException {
        double newValue = 5.0;
        _service.writeValueAsynchronously(pv, newValue, ValueType.DOUBLE, null);
        assertEquals(newValue, _service.readValueSynchronously(pv, ValueType.DOUBLE));
    }

    /**
     * Test method for
     * {@link org.csstudio.platform.internal.simpledal.ProcessVariableConnectionService#register(org.csstudio.platform.simpledal.IProcessVariableValueListener, org.csstudio.platform.model.pvs.IProcessVariableAddress, org.csstudio.platform.model.pvs.ValueType)}.
     */
    @Test
    public final void testRegister() {
        IProcessVariableValueListener listener = new ProcessVariableValueAdapter();
        _service.register(listener, pv, ValueType.DOUBLE);
        assertEquals(1, _service.getConnectors().size());
        assertTrue(_service.getConnectors().contains(connector));
    }

    /**
     * Test method for
     * {@link org.csstudio.platform.internal.simpledal.ProcessVariableConnectionService#unregister(org.csstudio.platform.simpledal.IProcessVariableValueListener)}.
     * @throws InterruptedException
     */
    @Test(timeout=1000)
    public final void testUnregister() throws InterruptedException {
        IProcessVariableValueListener listener = new ProcessVariableValueAdapter();
        _service.register(listener, pv, ValueType.DOUBLE);
        assertEquals(1, _service.getConnectors().size());
        assertTrue(_service.getConnectors().contains(connector));
        _service.unregister(listener);
        Thread.sleep(AbstractConnector.BLOCKING_TIMEOUT*4);
        assertEquals(0, _service.getConnectors().size());
    }

    /**
     * Test method for
     * {@link org.csstudio.platform.internal.simpledal.ProcessVariableConnectionService#checkWriteAccessSynchronously(org.csstudio.platform.model.pvs.IProcessVariableAddress)}.
     */
    @Test
    public final void testCheckWriteAccessSynchronously() {
        SettableState state = _service.checkWriteAccessSynchronously(pv);
        assertEquals(connector._settableState, state);
    }

    @Test
    @Ignore("Not implemented yet!")
    public void testGetConnectorsSuffix() {
        // FIXME mz: 2008-12-09: Implementieren!!!
    }

}
