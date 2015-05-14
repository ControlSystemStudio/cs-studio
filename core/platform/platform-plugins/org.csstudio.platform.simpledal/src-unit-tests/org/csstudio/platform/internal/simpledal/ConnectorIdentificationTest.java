/**
 *
 */
package org.csstudio.platform.internal.simpledal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.model.pvs.ValueType;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link ConnectorIdentification}.
 *
 * @author Sven Wende
 *
 */
public class ConnectorIdentificationTest {
    IProcessVariableAddress pv1;
    IProcessVariableAddress pv1_with_characteristic_1;
    IProcessVariableAddress pv1_with_characteristic_2;

    IProcessVariableAddress pv2;
    IProcessVariableAddress pv2_with_characteristic_1;

    /**
     * Test method for
     * {@link org.csstudio.platform.internal.simpledal.ConnectorIdentification#hashCode()}.
     */
    @Before
    public void setup() {
        pv1 = ProcessVariableAdressFactory.getInstance()
                .createProcessVariableAdress("dal-epics://pv1");
        pv1_with_characteristic_1 = ProcessVariableAdressFactory.getInstance()
                .createProcessVariableAdress("dal-epics://pv1[timestamp]");
        pv1_with_characteristic_2 = ProcessVariableAdressFactory.getInstance()
                .createProcessVariableAdress("dal-epics://pv1[maximum]");

        pv2 = ProcessVariableAdressFactory.getInstance()
                .createProcessVariableAdress("dal-epics://pv2");
        pv2_with_characteristic_1 = ProcessVariableAdressFactory.getInstance()
                .createProcessVariableAdress("dal-epics://pv2[timestamp]");

    }

    /**
     * Test method for
     * {@link org.csstudio.platform.internal.simpledal.ConnectorIdentification#equals(java.lang.Object)}.
     */
    @Test
    public void testEqualsAndHashcode() {
        // pv1 / pv1
        shouldEqual(pv1, ValueType.DOUBLE, pv1, ValueType.DOUBLE);
        shouldEqual(pv1, ValueType.STRING, pv1, ValueType.STRING);
        shouldNotEqual(pv1, ValueType.DOUBLE, pv1, ValueType.STRING);

        // pv1_with_characteristic_1 / pv1_with_characteristic_1
        shouldEqual(pv1_with_characteristic_1, ValueType.DOUBLE,
                pv1_with_characteristic_1, ValueType.DOUBLE);
        shouldEqual(pv1_with_characteristic_1, ValueType.STRING,
                pv1_with_characteristic_1, ValueType.STRING);
        shouldNotEqual(pv1_with_characteristic_1, ValueType.DOUBLE,
                pv1_with_characteristic_1, ValueType.STRING);

        // pv1_with_characteristic_2 / pv1_with_characteristic_2
        shouldEqual(pv1_with_characteristic_2, ValueType.DOUBLE,
                pv1_with_characteristic_2, ValueType.DOUBLE);
        shouldEqual(pv1_with_characteristic_2, ValueType.STRING,
                pv1_with_characteristic_2, ValueType.STRING);
        shouldNotEqual(pv1_with_characteristic_2, ValueType.DOUBLE,
                pv1_with_characteristic_2, ValueType.STRING);

        // pv1 / pv1_with_characteristic_1
        shouldEqual(pv1, ValueType.DOUBLE, pv1_with_characteristic_1,
                ValueType.DOUBLE);
        shouldEqual(pv1, ValueType.STRING, pv1_with_characteristic_1,
                ValueType.STRING);
        shouldNotEqual(pv1, ValueType.DOUBLE, pv1_with_characteristic_1,
                ValueType.STRING);

        // pv1 / pv1_with_characteristic_2
        shouldEqual(pv1, ValueType.DOUBLE, pv1_with_characteristic_2,
                ValueType.DOUBLE);
        shouldEqual(pv1, ValueType.STRING, pv1_with_characteristic_2,
                ValueType.STRING);
        shouldNotEqual(pv1, ValueType.DOUBLE, pv1_with_characteristic_2,
                ValueType.STRING);

        // pv1_with_characteristic_1 / pv1_with_characteristic_2
        shouldEqual(pv1_with_characteristic_1, ValueType.DOUBLE,
                pv1_with_characteristic_2, ValueType.DOUBLE);
        shouldEqual(pv1_with_characteristic_1, ValueType.STRING,
                pv1_with_characteristic_2, ValueType.STRING);
        shouldNotEqual(pv1_with_characteristic_1, ValueType.DOUBLE,
                pv1_with_characteristic_2, ValueType.STRING);

        // pv 1 / pv2
        shouldNotEqual(pv1, ValueType.DOUBLE, pv2, ValueType.DOUBLE);
        shouldNotEqual(pv1, ValueType.STRING, pv2, ValueType.STRING);
        shouldNotEqual(pv1, ValueType.STRING, pv2, ValueType.DOUBLE);

        // pv 1 / pv2_with_characteristic_1
        shouldNotEqual(pv1, ValueType.DOUBLE, pv2_with_characteristic_1,
                ValueType.DOUBLE);
        shouldNotEqual(pv1, ValueType.STRING, pv2_with_characteristic_1,
                ValueType.STRING);
        shouldNotEqual(pv1, ValueType.STRING, pv2_with_characteristic_1,
                ValueType.DOUBLE);

        // pv1_with_characteristic_1 / pv2_with_characteristic_1
        shouldNotEqual(pv1_with_characteristic_1, ValueType.DOUBLE,
                pv2_with_characteristic_1, ValueType.DOUBLE);
        shouldNotEqual(pv1_with_characteristic_1, ValueType.STRING,
                pv2_with_characteristic_1, ValueType.STRING);
        shouldNotEqual(pv1_with_characteristic_1, ValueType.STRING,
                pv2_with_characteristic_1, ValueType.DOUBLE);
    }

    /**
     * Test method for
     * {@link org.csstudio.platform.internal.simpledal.ConnectorIdentification#getProcessVariableAddress()}.
     */
    @Test
    public void testGetProcessVariableAddress() {
        ConnectorIdentification id = new ConnectorIdentification(pv1,
                ValueType.DOUBLE);
        assertEquals(pv1, id.getProcessVariableAddress());

        id = new ConnectorIdentification(pv2, ValueType.STRING);
        assertEquals(pv2, id.getProcessVariableAddress());
    }

    /**
     * Test method for
     * {@link org.csstudio.platform.internal.simpledal.ConnectorIdentification#getValueType()}.
     */
    @Test
    public void testGetValueType() {
        ConnectorIdentification id = new ConnectorIdentification(pv1,
                ValueType.DOUBLE);
        assertEquals(ValueType.DOUBLE, id.getValueType());

        id = new ConnectorIdentification(pv2, ValueType.STRING);
        assertEquals(ValueType.STRING, id.getValueType());
    }

    private void shouldNotEqual(IProcessVariableAddress pv1, ValueType vt1,
            IProcessVariableAddress pv2, ValueType vt2) {
        ConnectorIdentification id1 = new ConnectorIdentification(pv1, vt1);
        ConnectorIdentification id2 = new ConnectorIdentification(pv2, vt2);
        assertNotSame(id1, id2);
        assertNotSame(id1.hashCode(), id2.hashCode());
    }

    private void shouldEqual(IProcessVariableAddress pv1, ValueType vt1,
            IProcessVariableAddress pv2, ValueType vt2) {
        ConnectorIdentification id1 = new ConnectorIdentification(pv1, vt1);
        ConnectorIdentification id2 = new ConnectorIdentification(pv2, vt2);
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

}
