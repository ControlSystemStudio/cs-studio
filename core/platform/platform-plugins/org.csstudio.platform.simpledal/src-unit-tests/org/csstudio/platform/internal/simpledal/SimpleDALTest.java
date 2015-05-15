package org.csstudio.platform.internal.simpledal;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.DALPropertyFactoriesProvider;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.model.pvs.ValueType;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.csstudio.dal.CharacteristicInfo;
import org.csstudio.dal.DoubleProperty;
import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.Timestamp;
import org.csstudio.dal.spi.PropertyFactory;

public class SimpleDALTest extends TestCase {

    class IPVVListener implements IProcessVariableValueListener {
        Object value;
        Timestamp timestamp;
        String error;
        ConnectionState state;

        public void connectionStateChanged(ConnectionState connectionState) {
            this.state= connectionState;
        }
        public void errorOccured(String error) {
            this.error=error;
        }
        public void valueChanged(Object value, Timestamp timestamp) {
            this.value=value;
            this.timestamp=timestamp;
        }
    }

    private ProcessVariableAdressFactory addressFactory;
    private IProcessVariableConnectionService connectionService;

    public void setUp() throws Exception {
        // the factory for pv addresses
        addressFactory = ProcessVariableAdressFactory.getInstance();

        assertNotNull(addressFactory);

        // the connection service
        connectionService = ProcessVariableConnectionServiceFactory.getDefault().createProcessVariableConnectionService();

        assertNotNull(connectionService);
    }

    public void testMandatoryCharacteristics() {

        try {

            CharacteristicInfo[] infos= CharacteristicInfo.getDefaultCharacteristics(DoubleProperty.class, null);

            assertNotNull(infos);

            for (int i = 0; i < infos.length; i++) {
                CharacteristicInfo info= infos[i];
                assertNotNull(info);

                String rawName= ControlSystemEnum.DAL_SIMULATOR.getPrefix()+"://D3:P1["+info.getName()+"]";

                IProcessVariableAddress ia= addressFactory.createProcessVariableAdress(rawName);

                Object value= connectionService.readValueSynchronously(ia,ValueType.OBJECT);

                System.out.println(info.getName()+" "+value);

                assertNotNull("'"+info.getName()+"' is null",value);
                assertTrue("'"+info.getName()+"' is "+value.getClass().getName(), info.getType().isAssignableFrom(value.getClass()));


            }


        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    public void testGetValue() {

        try {

            //IProcessVariableAddress ia= addressFactory.createProcessVariableAdress(ControlSystemEnum.DAL_SIMULATOR, "D1", "P1", null);

            String rawName= ControlSystemEnum.DAL_SIMULATOR.getPrefix()+"://D1:P1";

            System.out.println(rawName);

            IProcessVariableAddress ia= addressFactory.createProcessVariableAdress(rawName);

            System.out.println(ia.toString());
            System.out.println(ia.toDalRemoteInfo().toString());

            double d= connectionService.readValueSynchronously(ia, ValueType.DOUBLE);

            System.out.println(d);

            d= d+1.0;

            connectionService.writeValueAsynchronously(ia, d, ValueType.DOUBLE, null);

            double d1= connectionService.readValueSynchronously(ia, ValueType.DOUBLE);

            System.out.println(d);

            assertEquals(d, d1, 0.0001);


        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }


    }

    public void testSeverityCharacteristic() {

        try {

            //IProcessVariableAddress ia= addressFactory.createProcessVariableAdress(ControlSystemEnum.DAL_SIMULATOR, "D1", "P1", null);

            String rawName= ControlSystemEnum.DAL_SIMULATOR.getPrefix()+"://D1:P1";

            IProcessVariableAddress ia= addressFactory.createProcessVariableAdress(rawName);

            System.out.println("RI: "+ia.toDalRemoteInfo().toString());

            double d= connectionService.readValueSynchronously(ia, ValueType.DOUBLE);

            rawName= rawName+"[severity]";

            ia= addressFactory.createProcessVariableAdress(rawName);

            System.out.println("RI: "+ia.toDalRemoteInfo().toString());

            assertTrue("isCharacteristic()==false",ia.isCharacteristic());
            assertEquals("severity", ia.getCharacteristic());

            //PropertyProxyImpl pp= SimulatorPlug.getInstance().getSimulatedPropertyProxy(ia.toDalRemoteInfo().getRemoteName());
            //System.out.println("PP: "+pp.getUniqueName());
            //pp.setCondition(new DynamicValueCondition(EnumSet.of(DynamicValueState.ALARM), new Timestamp(),"STATUS1"));

            IPVVListener l = new IPVVListener();
            connectionService.register(l, ia, ValueType.STRING);

            String s= connectionService.readValueSynchronously(ia, ValueType.STRING);
            System.out.println("severity: "+s);
            assertNotNull(s);
            //assertEquals(DynamicValueState.ALARM.toString(), s);

            rawName= ControlSystemEnum.DAL_SIMULATOR.getPrefix()+"://D1:P1[test]";
            IProcessVariableAddress ia1= addressFactory.createProcessVariableAdress(rawName);
            connectionService.register(l, ia1, ValueType.STRING);

            /*pp= SimulatorPlug.getInstance().getSimulatedPropertyProxy(ia.toDalRemoteInfo().getName());
            System.out.println("PP: "+pp.getUniqueName());
            pp.setCondition(new DynamicValueCondition(EnumSet.of(DynamicValueState.ERROR), new Timestamp(),"STATUS2"));
            assertNotNull(l.value);
            assertEquals(DynamicValueState.ERROR.toString(), l.value);*/


            /*pp.simulateCharacteristicChange("test", "T");
            assertNotNull(l.value);
            assertEquals("T", l.value);*/

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }


    }

    public void testCharacteristicInfo() {
        try {

            CharacteristicInfo[] ci= CharacteristicInfo.getDefaultCharacteristics(null);

            Set<CharacteristicInfo> set= new HashSet<CharacteristicInfo>(Arrays.asList(ci));

            assertTrue(set.contains(CharacteristicInfo.C_SEVERITY));
            assertTrue(set.contains(CharacteristicInfo.C_STATUS));
            assertTrue(set.contains(CharacteristicInfo.C_TIMESTAMP));


        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testCharacteristics() {

        try {

            CharacteristicInfo[] ci= CharacteristicInfo.getDefaultCharacteristics(DoubleProperty.class,null);

            for (int i = 0; i < ci.length; i++) {

                if ("displayName".equals(ci[i].getName()) || "warningMax".equals(ci[i].getName()) || "warningMin".equals(ci[i].getName()) || "alarmMax".equals(ci[i].getName()) || "alarmMin".equals(ci[i].getName())) {
                    // FIXME: fix this in DAL simulator
                    continue;
                }

                String rawName= ControlSystemEnum.DAL_SIMULATOR.getPrefix()+"://D2:P2["+ci[i].getName()+"]";
                IProcessVariableAddress ia= addressFactory.createProcessVariableAdress(rawName);

                assertEquals(rawName, ia.getRawName());
                assertTrue(ia.isCharacteristic());

                Object o= connectionService.readValueSynchronously(ia, ValueType.OBJECT);

                System.out.println("RI: "+ia.toDalRemoteInfo().toString()+" "+o);

                assertNotNull(o);
                assertTrue(ci[i].getType().isAssignableFrom(o.getClass()));

            }




        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    public void testSlowChannels() {

        try {

            //SimulatorUtilities.putConfiguration(SimulatorUtilities.CONNECTION_DELAY, new Long(1000));

            String rawName= ControlSystemEnum.DAL_SIMULATOR.getPrefix()+"://D1:S1";
            IProcessVariableAddress ia= addressFactory.createProcessVariableAdress(rawName);
            double d= connectionService.readValueSynchronously(ia, ValueType.DOUBLE);
            assertEquals(0.0, d, 0.0001);

            rawName= ControlSystemEnum.DAL_SIMULATOR.getPrefix()+"://D1:S2";
            ia= addressFactory.createProcessVariableAdress(rawName);
            connectionService.writeValueAsynchronously(ia, 10.0, ValueType.DOUBLE, null);
            d= connectionService.readValueSynchronously(ia, ValueType.DOUBLE);
            assertEquals(10.0, d, 0.0001);

            rawName= ControlSystemEnum.DAL_SIMULATOR.getPrefix()+"://D1:S3";
            ia= addressFactory.createProcessVariableAdress(rawName);
            IPVVListener l= new IPVVListener();
            connectionService.register(l, ia, ValueType.DOUBLE);
            connectionService.writeValueAsynchronously(ia, 10.0,  ValueType.DOUBLE, null);
            d= connectionService.readValueSynchronously(ia, ValueType.DOUBLE);
            assertEquals(10.0, d, 0.0001);
            assertNotNull(l.value);
            assertNotNull(l.state);
            assertNull(l.error);



        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            //SimulatorUtilities.putConfiguration(SimulatorUtilities.CONNECTION_DELAY, new Long(0));
        }

    }

    public void testDestroy() {

        try {

            String rawName= ControlSystemEnum.DAL_SIMULATOR.getPrefix()+"://D2:P1";
            IProcessVariableAddress ia= addressFactory.createProcessVariableAdress(rawName);
            IPVVListener l= new IPVVListener();
            connectionService.register(l, ia, ValueType.DOUBLE);

            PropertyFactory factory = DALPropertyFactoriesProvider.getInstance()
            .getPropertyFactory(ia.getControlSystem());
            DynamicValueProperty pp= factory.getPropertyFamily().getFirst("D2:P1");
            assertNotNull(pp);
            assertEquals(org.csstudio.dal.context.ConnectionState.CONNECTED, pp.getConnectionState());

            connectionService.writeValueAsynchronously(ia, 10.0, ValueType.DOUBLE, null);
            double d= connectionService.readValueSynchronously(ia,ValueType.DOUBLE);
            assertEquals(10.0, d, 0.0001);
            assertNotNull(l.value);
            assertNotNull(l.state);
            assertNull(l.error);

            Thread.sleep(11000);

            l.value=null;
            Thread.sleep(1100);
            assertNotNull(l.value);
            assertEquals(org.csstudio.dal.context.ConnectionState.CONNECTED, pp.getConnectionState());

            connectionService.unregister(l);

            l.value=null;
            Thread.sleep(1100);
            assertNull(l.value);
            //assertEquals(org.csstudio.dal.context.ConnectionState.DESTROYED, pp.getConnectionState());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }


}
