/**
 *
 */
package org.csstudio.platform.internal.simpledal.dal;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.concurrent.Semaphore;

import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.Timestamp;
import org.csstudio.domain.common.strings.StringUtil;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.model.pvs.ValueType;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.SettableState;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link DalConnector}.
 *
 * To run the tests, put the following records on your IOC:
 *
 * <pre>
 *  record(ai,&quot;unittest:ai&quot;) {
 *  field(DESC,&quot;read only&quot;)
 *  field(SCAN,&quot;.1 second&quot;)
 *  field(INP,&quot;23.45&quot;)
 *  field(HIHI,&quot;35.00&quot;)
 *  field(HIGH,&quot;30.00&quot;)
 *  field(LOW,&quot;10.00&quot;)
 *  field(LOLO,&quot;5.00&quot;)
 *  field(HOPR,&quot;40.00&quot;)
 *  field(LOPR,&quot;2.30&quot;)
 *  field(HHSV,&quot;NO_ALARM&quot;)
 *  field(LLSV,&quot;NO_ALARM&quot;)
 *  field(HSV,&quot;NO_ALARM&quot;)
 *  field(LSV,&quot;NO_ALARM&quot;)
 *  }
 *
 *  record(ai,&quot;unittest:ai:write&quot;) {
 *  field(DESC,&quot;read+write&quot;)
 *  field(SCAN,&quot;.1 second&quot;)
 *  field(INP,&quot;23.45&quot;)
 *  field(HIHI,&quot;35.00&quot;)
 *  field(HIGH,&quot;30.00&quot;)
 *  field(LOW,&quot;10.00&quot;)
 *  field(LOLO,&quot;5.00&quot;)
 *  field(HOPR,&quot;40.00&quot;)
 *  field(LOPR,&quot;2.30&quot;)
 *  field(HHSV,&quot;NO_ALARM&quot;)
 *  field(LLSV,&quot;NO_ALARM&quot;)
 *  field(HSV,&quot;NO_ALARM&quot;)
 *  field(LSV,&quot;NO_ALARM&quot;)
 *  }
 * </pre>
 *
 * @author Sven Wende
 *
 */
public class DalConnectorTest {

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * Test method for
     * {@link org.csstudio.platform.internal.simpledal.dal.DalConnector#doIsSettable()}.
     */
    @Test
    public final void testDoIsSettable() {
        // a settable pv
        IProcessVariableAddress pv = ProcessVariableAdressFactory.getInstance().createProcessVariableAdress("dal-epics://unittest:ai:write");
        DalConnector connector = new DalConnector(pv, ValueType.DOUBLE);
        SettableState state = connector.isSettable();
        assertEquals(SettableState.SETTABLE, state);

        // an unknown pv
        pv = ProcessVariableAdressFactory.getInstance().createProcessVariableAdress("dal-epics://unknown");
        connector = new DalConnector(pv, ValueType.DOUBLE);

        state = connector.isSettable();
        assertEquals(SettableState.UNKNOWN, state);
    }

    /**
     * Test method for
     * {@link org.csstudio.platform.internal.simpledal.dal.DalConnector#doDispose()}.
     *
     * @throws Exception
     */
    @Test
    public final void testDoDispose() throws Exception {
        IProcessVariableAddress pv = ProcessVariableAdressFactory.getInstance().createProcessVariableAdress("dal-epics://unittest:ai");
        DalConnector connector = new DalConnector(pv, ValueType.DOUBLE);
        Double value = connector.getValueSynchronously();
        DynamicValueProperty dalProperty = connector.getDalProperty();

        // verify, that we are connected
        assertNotNull(value);
        assertEquals(org.csstudio.dal.context.ConnectionState.CONNECTED, dalProperty.getConnectionState());

        // dispose
        connector.dispose();
        Thread.sleep(2000);

        // verify that we are disconnected
        assertEquals(org.csstudio.dal.context.ConnectionState.DESTROYED, dalProperty.getConnectionState());
    }

    /**
     * Test method for
     * {@link org.csstudio.platform.internal.simpledal.dal.DalConnector#doGetCharacteristicAsynchronously(java.lang.String, org.csstudio.platform.model.pvs.ValueType, org.csstudio.platform.simpledal.IProcessVariableValueListener)}.
     */
    @Test
    public final void testDoGetCharacteristicAsynchronously() throws Exception {
        // ... get [minimum] characteristic in all type combinations
        testGetCharacteristicAsynchronously("dal-epics://unittest:ai[minimum]", ValueType.DOUBLE, 2.3);
        testGetCharacteristicAsynchronously("dal-epics://unittest:ai[minimum]", ValueType.DOUBLE_SEQUENCE, new double[] { 2.3 });
        testGetCharacteristicAsynchronously("dal-epics://unittest:ai[minimum]", ValueType.LONG, 2l);
        testGetCharacteristicAsynchronously("dal-epics://unittest:ai[minimum]", ValueType.LONG_SEQUENCE, new long[] { 2l });
        testGetCharacteristicAsynchronously("dal-epics://unittest:ai[minimum]", ValueType.OBJECT, new Double(2.3));
        // TODO:
        testGetCharacteristicAsynchronously("dal-epics://unittest:ai[minimum]",
        ValueType.OBJECT_SEQUENCE, new Object[] { new Double(2.3) });
        // TODO:
        // testGetCharacteristicAsynchronously("dal-epics://unittest:ai[minimum]",
        // ValueType.STRING, "2");
        // TODO:
        // testGetCharacteristicAsynchronously("dal-epics://unittest:ai[minimum]",
        // ValueType.STRING_SEQUENCE, new String[] { "2" });

        // ... get [maximum] characteristic in all type combinations
        testGetCharacteristicAsynchronously("dal-epics://unittest:ai[maximum]", ValueType.DOUBLE, 40.0);
        testGetCharacteristicAsynchronously("dal-epics://unittest:ai[maximum]", ValueType.DOUBLE_SEQUENCE, new double[] { 40.0 });
        testGetCharacteristicAsynchronously("dal-epics://unittest:ai[maximum]", ValueType.LONG, 40l);
        testGetCharacteristicAsynchronously("dal-epics://unittest:ai[maximum]", ValueType.LONG_SEQUENCE, new long[] { 40l });
        testGetCharacteristicAsynchronously("dal-epics://unittest:ai[maximum]", ValueType.OBJECT, new Double(40.0));
        // TODO:
        // testGetCharacteristicAsynchronously("dal-epics://unittest:ai[maximum]",
        // ValueType.OBJECT_SEQUENCE, new Object[] { new Double(40.0) });
        // TODO:
        // testGetCharacteristicAsynchronously("dal-epics://unittest:ai[maximum]",
        // ValueType.STRING, "40");
        // TODO:
        // testGetCharacteristicAsynchronously("dal-epics://unittest:ai[maximum]",
        // ValueType.STRING_SEQUENCE, new String[] { "40" });

        // ... get other available characteristics
        testGetCharacteristicSynchronously("dal-epics://unittest:ai[graphMin]", ValueType.DOUBLE, 2.3);
        testGetCharacteristicSynchronously("dal-epics://unittest:ai[graphMax]", ValueType.DOUBLE, 40.0);
        testGetCharacteristicSynchronously("dal-epics://unittest:ai[resolution]", ValueType.DOUBLE, 65535.0);
        testGetCharacteristicSynchronously("dal-epics://unittest:ai[format]", ValueType.STRING, "%s");
        testGetCharacteristicSynchronously("dal-epics://unittest:ai[units]", ValueType.STRING, "N/A");
        testGetCharacteristicSynchronously("dal-epics://unittest:ai[scaleType]", ValueType.STRING, "linear");
    }

    private void testGetCharacteristicAsynchronously(String pvName, ValueType vt, Object expectedValue) throws Exception {
        IProcessVariableAddress pv = ProcessVariableAdressFactory.getInstance().createProcessVariableAdress(pvName);

        // create connector
        DalConnector connector = new DalConnector(pv, vt);

        // create listener
        TestListener listener = new TestListener();

        // get value
        listener.getSemaphore().acquire();
        listener.autoReleaseSemaphore(3000);

        connector.doGetCharacteristicAsynchronously(pv.getCharacteristic(), vt, listener);

        // wait
        listener.getSemaphore().acquire();
        listener.getSemaphore().release();

        // verify value
        verifyValue("ASYNC C-GET", pv, vt, listener.getReceivedValue(), expectedValue);
    }

    /**
     * Test method for
     * {@link org.csstudio.platform.internal.simpledal.dal.DalConnector#doGetCharacteristicSynchronously(java.lang.String, org.csstudio.platform.model.pvs.ValueType)}.
     */
    @Test
    public final void testDoGetCharacteristicSynchronously() throws Exception {
        // ... get [minimum] characteristic in all type combinations
        testGetCharacteristicSynchronously("dal-epics://unittest:ai[minimum]", ValueType.DOUBLE, 2.3);
        testGetCharacteristicSynchronously("dal-epics://unittest:ai[minimum]", ValueType.DOUBLE_SEQUENCE, new double[] { 2.3 });
        testGetCharacteristicSynchronously("dal-epics://unittest:ai[minimum]", ValueType.LONG, 2l);
        testGetCharacteristicSynchronously("dal-epics://unittest:ai[minimum]", ValueType.LONG_SEQUENCE, new long[] { 2l });
        testGetCharacteristicSynchronously("dal-epics://unittest:ai[minimum]", ValueType.OBJECT, new Double(2.3));
        // TODO:
        // testGetCharacteristicSynchronously("dal-epics://unittest:ai[minimum]",
        // ValueType.OBJECT_SEQUENCE, new Object[] { new Double(2.3) });
        // TODO:
        // testGetCharacteristicSynchronously("dal-epics://unittest:ai[minimum]",
        // ValueType.STRING, "2");
        // TODO:
        // testGetCharacteristicSynchronously("dal-epics://unittest:ai[minimum]",
        // ValueType.STRING_SEQUENCE, new String[] { "2" });

        // ... get [maximum] characteristic in all type combinations
        testGetCharacteristicSynchronously("dal-epics://unittest:ai[maximum]", ValueType.DOUBLE, 40.0);
        testGetCharacteristicSynchronously("dal-epics://unittest:ai[maximum]", ValueType.DOUBLE_SEQUENCE, new double[] { 40.0 });
        testGetCharacteristicSynchronously("dal-epics://unittest:ai[maximum]", ValueType.LONG, 40l);
        testGetCharacteristicSynchronously("dal-epics://unittest:ai[maximum]", ValueType.LONG_SEQUENCE, new long[] { 40l });
        testGetCharacteristicSynchronously("dal-epics://unittest:ai[maximum]", ValueType.OBJECT, new Double(40.0));
        // TODO:
        // testGetCharacteristicSynchronously("dal-epics://unittest:ai[maximum]",
        // ValueType.OBJECT_SEQUENCE, new Object[] { new Double(40.0) });
        // TODO:
        // testGetCharacteristicSynchronously("dal-epics://unittest:ai[maximum]",
        // ValueType.STRING, "40");
        // TODO:
        // testGetCharacteristicSynchronously("dal-epics://unittest:ai[maximum]",
        // ValueType.STRING_SEQUENCE, new String[] { "40" });

        // ... get other available characteristics
        testGetCharacteristicSynchronously("dal-epics://unittest:ai[graphMin]", ValueType.DOUBLE, 2.3);
        testGetCharacteristicSynchronously("dal-epics://unittest:ai[graphMax]", ValueType.DOUBLE, 40.0);
        testGetCharacteristicSynchronously("dal-epics://unittest:ai[resolution]", ValueType.DOUBLE, 65535.0);
        testGetCharacteristicSynchronously("dal-epics://unittest:ai[format]", ValueType.STRING, "%s");
        testGetCharacteristicSynchronously("dal-epics://unittest:ai[units]", ValueType.STRING, "N/A");
        testGetCharacteristicSynchronously("dal-epics://unittest:ai[scaleType]", ValueType.STRING, "linear");
    }

    private void testGetCharacteristicSynchronously(String pvName, ValueType vt, Object expectedValue) throws Exception {
        IProcessVariableAddress pv = ProcessVariableAdressFactory.getInstance().createProcessVariableAdress(pvName);

        // create connector
        DalConnector connector = new DalConnector(pv, vt);

        // get value
        Object value = connector.doGetCharacteristicSynchronously(pv.getCharacteristic(), vt);

        // verify value
        verifyValue("SYNC-C-GET", pv, vt, value, expectedValue);
    }

    /**
     * Test method for
     * {@link org.csstudio.platform.internal.simpledal.dal.DalConnector#doGetValueAsynchronously(org.csstudio.platform.simpledal.IProcessVariableValueListener)}.
     */
    @Test
    public final void testDoGetValueAsynchronously() throws Exception {
        testGetValueAsynchronously("dal-epics://unittest:ai", ValueType.DOUBLE, 23.45);
        testGetValueAsynchronously("dal-epics://unittest:ai", ValueType.DOUBLE_SEQUENCE, new double[] { 23.45 });
        testGetValueAsynchronously("dal-epics://unittest:ai", ValueType.LONG, 23l);
        testGetValueAsynchronously("dal-epics://unittest:ai", ValueType.LONG_SEQUENCE, new long[] { 23l });
        testGetValueAsynchronously("dal-epics://unittest:ai", ValueType.OBJECT, new Double(23.45));
        // TODO: testGetValueAsynchronously("dal-epics://unittest:ai",
        // ValueType.OBJECT_SEQUENCE, new Object[] { new Double(23.45) });
        testGetValueAsynchronously("dal-epics://unittest:ai", ValueType.STRING, "23");
        testGetValueAsynchronously("dal-epics://unittest:ai", ValueType.STRING_SEQUENCE, new String[] { "23" });
    }

    private void testGetValueAsynchronously(String pvName, ValueType vt, Object expectedValue) throws Exception {
        IProcessVariableAddress pv = ProcessVariableAdressFactory.getInstance().createProcessVariableAdress(pvName);

        // create connector
        DalConnector connector = new DalConnector(pv, vt);

        // create listener
        TestListener listener = new TestListener();

        // get value
        listener.getSemaphore().acquire();
        listener.autoReleaseSemaphore(3000);
        connector.doGetValueAsynchronously(listener);

        // wait
        listener.getSemaphore().acquire();
        listener.getSemaphore().release();

        // verify value
        verifyValue("ASYNC GET", pv, vt, listener.getReceivedValue(), expectedValue);
    }

    /**
     * Test method for
     * {@link org.csstudio.platform.internal.simpledal.dal.DalConnector#doGetValueSynchronously()}.
     */
    @Test
    public final void testDoGetValueSynchronously() throws Exception {
        testGetValueSynchronously("dal-epics://unittest:ai", ValueType.DOUBLE, 23.45);
        testGetValueSynchronously("dal-epics://unittest:ai", ValueType.DOUBLE_SEQUENCE, new double[] { 23.45 });
        testGetValueSynchronously("dal-epics://unittest:ai", ValueType.LONG, 23l);
        testGetValueSynchronously("dal-epics://unittest:ai", ValueType.LONG_SEQUENCE, new long[] { 23l });
        testGetValueSynchronously("dal-epics://unittest:ai", ValueType.OBJECT, new Double(23.45));
        // TODO: testGetValueSynchronously("dal-epics://unittest:ai", ValueType.OBJECT_SEQUENCE, new Object[] { new Double(23.45) });
        testGetValueSynchronously("dal-epics://unittest:ai", ValueType.STRING, "23");
        testGetValueSynchronously("dal-epics://unittest:ai", ValueType.STRING_SEQUENCE, new String[] { "23" });
    }

    private void testGetValueSynchronously(String pvName, ValueType vt, Object expectedValue) throws Exception {
        IProcessVariableAddress pv = ProcessVariableAdressFactory.getInstance().createProcessVariableAdress(pvName);

        // create connector
        DalConnector connector = new DalConnector(pv, vt);

        // get value
        Object value = connector.doGetValueSynchronously();

        // verify value
        verifyValue("SYNC-GET", pv, vt, value, expectedValue);
    }

    /**
     * Test method for
     * {@link org.csstudio.platform.internal.simpledal.dal.DalConnector#doSetValueAsynchronously(java.lang.Object)}.
     */
    @Test
    public final void testDoSetValueAsynchronously() throws Exception {
        testSetValueAsynchronously("dal-epics://unittest:ai:write", ValueType.DOUBLE, 1.1, 1.2);
        testSetValueAsynchronously("dal-epics://unittest:ai:write", ValueType.DOUBLE_SEQUENCE, new double[] { 1.3 }, new double[] { 1.4 });
        testSetValueAsynchronously("dal-epics://unittest:ai:write", ValueType.LONG, 2l, 3l);
        testSetValueAsynchronously("dal-epics://unittest:ai:write", ValueType.LONG_SEQUENCE, new long[] { 1l }, new long[] { 4l });
        testSetValueAsynchronously("dal-epics://unittest:ai:write", ValueType.STRING, "2", "3");
        testSetValueAsynchronously("dal-epics://unittest:ai:write", ValueType.STRING_SEQUENCE, new String[] { "1" }, new String[] { "4" });
    }

    private void testSetValueAsynchronously(String pvName, ValueType vt, Object value1, Object value2) throws Exception {
        IProcessVariableAddress pv = ProcessVariableAdressFactory.getInstance().createProcessVariableAdress(pvName);

        // create connector
        DalConnector connector = new DalConnector(pv, vt);

        // set new value 1
        connector.setValueAsynchronously(value1, null);
        Thread.sleep(2000);

        // get current value
        Object currentValue1 = connector.doGetValueSynchronously();
        assertNotNull(currentValue1);
        assertTrue(compareValues(value1, currentValue1));

        // set new value 1
        connector.setValueSynchronously(value2);
        Thread.sleep(2000);

        // get current value
        Object currentValue2 = connector.doGetValueSynchronously();
        assertNotNull(currentValue2);
        assertTrue(compareValues(value2, currentValue2));
    }

    /**
     * Test method for
     * {@link org.csstudio.platform.internal.simpledal.dal.DalConnector#doSetValueSynchronously(java.lang.Object)}.
     */
    @Test
    public final void testDoSetValueSynchronously() throws Exception {
        testSetValueSynchronously("dal-epics://unittest:ai:write", ValueType.DOUBLE, 1.1, 1.2);
        testSetValueSynchronously("dal-epics://unittest:ai:write", ValueType.DOUBLE_SEQUENCE, new double[] { 1.3 }, new double[] { 1.4 });
        testSetValueSynchronously("dal-epics://unittest:ai:write", ValueType.LONG, 2l, 3l);
        testSetValueSynchronously("dal-epics://unittest:ai:write", ValueType.LONG_SEQUENCE, new long[] { 1l }, new long[] { 4l });
        testSetValueSynchronously("dal-epics://unittest:ai:write", ValueType.STRING, "2", "3");
        testSetValueSynchronously("dal-epics://unittest:ai:write", ValueType.STRING_SEQUENCE, new String[] { "1" }, new String[] { "4" });
    }

    private void testSetValueSynchronously(String pvName, ValueType vt, Object value1, Object value2) throws Exception {
        IProcessVariableAddress pv = ProcessVariableAdressFactory.getInstance().createProcessVariableAdress(pvName);

        // create connector
        DalConnector connector = new DalConnector(pv, vt);

        // set new value 1
        connector.setValueSynchronously(value1);

        // get current value
        Object currentValue1 = connector.doGetValueSynchronously();
        assertNotNull(currentValue1);
        assertTrue(compareValues(value1, currentValue1));

        // set new value 1
        connector.setValueSynchronously(value2);

        // get current value
        Object currentValue2 = connector.doGetValueSynchronously();
        assertNotNull(currentValue2);
        assertTrue(compareValues(value2, currentValue2));
    }

    private void verifyValue(String action, IProcessVariableAddress pv, ValueType vt, Object value, Object expectedValue) {

        boolean valueExists = value != null;
        boolean valueTypeOk = valueExists ? vt.getJavaType().isAssignableFrom(value.getClass()) : false;
        boolean valueOk = valueExists ? compareValues(value, expectedValue) : false;
        boolean ok = valueExists && valueOk && valueOk;

        StringBuffer output = new StringBuffer();

        output.append(ok ? "OK" : "ERROR");
        output.append(": ");

        output.append(action);
        output.append(": ");
        output.append(pv);
        output.append(": ");
        output.append(vt);
        output.append(": ");
        output.append(StringUtil.printArrays(value));

        if (!ok) {
            if (!valueExists) {
                output.append(": value is missing");
            } else if (!valueTypeOk) {
                output.append(": value is of wrong type");
            } else if (!valueOk) {
                output.append(": value does not match, expected ");
                output.append(StringUtil.printArrays(expectedValue));
                output.append(" instead !");

            }
        }

        System.out.println(output.toString());

        assertNotNull(value);
        assertTrue(valueTypeOk);
        assertTrue(valueOk);
    }

    private boolean compareValues(Object value, Object expected) {
        boolean result = true;

        if (expected == null) {
            result = value == null;
        } else if (expected instanceof double[] && value instanceof double[]) {
            result = Arrays.equals((double[]) expected, (double[]) value);
        } else if (expected instanceof long[] && value instanceof long[]) {
            result = Arrays.equals((long[]) expected, (long[]) value);
        } else if (expected instanceof String[] && value instanceof String[]) {
            result = Arrays.equals((String[]) expected, (String[]) value);
        } else if (expected instanceof Object[] && value instanceof Object[]) {
            Object[] expectedArray = (Object[]) expected;
            Object[] valueArray = (Object[]) value;

            if (expectedArray.length == valueArray.length) {
                result = true;
                for (int i = 0; i < expectedArray.length; i++) {
                    Object o1 = expectedArray[i];
                    Object o2 = valueArray[i];
                    result &= (o1 != null && o2 != null && o1.toString().equals(o2.toString()));
                }
            }
        } else {
            result = expected.equals(value);
        }

        return result;
    }

    static class TestListener implements IProcessVariableValueListener {
        private Object receivedValue;
        private Object receivedError;
        private Semaphore semaphore;

        public TestListener() {
            semaphore = new Semaphore(1);
        }

        public void errorOccured(String error) {
            receivedError = error;
            semaphore.release();
        }

        public void valueChanged(Object value, Timestamp timestamp) {
            receivedValue = value;
            semaphore.release();
        }

        public void connectionStateChanged(ConnectionState connectionState) {

        }

        public Object getReceivedValue() {
            return receivedValue;
        }

        public Object getReceivedError() {
            return receivedError;
        }

        public Semaphore getSemaphore() {
            return semaphore;
        }

        public void autoReleaseSemaphore(long timeout) {
            Job job = new Job("Auto-Release") {
                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    semaphore.release();
                    return Status.OK_STATUS;
                }
            };
            job.schedule(timeout);
        }

    }
}
