package org.csstudio.platform.internal.simpledal;


import java.util.Arrays;

import junit.framework.TestCase;

import org.csstudio.platform.internal.simpledal.dal.EpicsUtil;
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
import org.csstudio.dal.DoubleSeqAccess;
import org.csstudio.dal.DynamicValueProperty;
import org.csstudio.dal.SequencePropertyCharacteristics;
import org.csstudio.dal.Timestamp;
import org.csstudio.dal.spi.PropertyFactory;

public class SimpleDAL_EPICSTest extends TestCase {

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

                String rawName= ControlSystemEnum.DAL_EPICS.getPrefix()+"://PV_AO_03["+info.getName()+"]";

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

    public void testOverloadType() {

        try {

            String rawName= ControlSystemEnum.DAL_EPICS.getPrefix()+"://PV_AO_01";
            IProcessVariableAddress ia= addressFactory.createProcessVariableAdress(rawName);
            IPVVListener l1= new IPVVListener();
            connectionService.register(l1, ia, ValueType.STRING);

            Thread.sleep(1000);

            assertNotNull(l1.value);
            assertEquals(String.class, l1.value.getClass());

            rawName= ControlSystemEnum.DAL_EPICS.getPrefix()+"://PV_AO_01[graphMax]";
            ia= addressFactory.createProcessVariableAdress(rawName);
            IPVVListener l2= new IPVVListener();
            connectionService.register(l2, ia, ValueType.DOUBLE);

            Thread.sleep(1000);

            assertNotNull(l2.value);
            assertEquals(Double.class, l2.value.getClass());


        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    public void testCharacteristicsFromFields() {

        try {

            String rawName= ControlSystemEnum.DAL_EPICS.getPrefix()+"://PV_AO_03[minimum]";
            IProcessVariableAddress ia= addressFactory.createProcessVariableAdress(rawName);
            double min= connectionService.readValueSynchronously(ia,ValueType.DOUBLE);

            rawName= ControlSystemEnum.DAL_EPICS.getPrefix()+"://PV_AI_03[maximum]";
            ia= addressFactory.createProcessVariableAdress(rawName);
            double max= connectionService.readValueSynchronously(ia,ValueType.DOUBLE);

            rawName= ControlSystemEnum.DAL_EPICS.getPrefix()+"://PV_AI_03[graphMin]";
            ia= addressFactory.createProcessVariableAdress(rawName);
            double graphMin= connectionService.readValueSynchronously(ia,ValueType.DOUBLE);

            rawName= ControlSystemEnum.DAL_EPICS.getPrefix()+"://PV_AI_03[graphMax]";
            ia= addressFactory.createProcessVariableAdress(rawName);
            double graphMax= connectionService.readValueSynchronously(ia,ValueType.DOUBLE);

            assertEquals(min, graphMin, 0.0001);
            assertEquals(max, graphMax, 0.0001);



            rawName= ControlSystemEnum.DAL_EPICS.getPrefix()+"://PV_AO_03[minimum]";
            ia= addressFactory.createProcessVariableAdress(rawName);
            min= connectionService.readValueSynchronously(ia,ValueType.DOUBLE);

            rawName= ControlSystemEnum.DAL_EPICS.getPrefix()+"://PV_AO_03[maximum]";
            ia= addressFactory.createProcessVariableAdress(rawName);
            max= connectionService.readValueSynchronously(ia,ValueType.DOUBLE);

            rawName= ControlSystemEnum.DAL_EPICS.getPrefix()+"://PV_AO_03[graphMin]";
            ia= addressFactory.createProcessVariableAdress(rawName);
            graphMin= connectionService.readValueSynchronously(ia,ValueType.DOUBLE);

            rawName= ControlSystemEnum.DAL_EPICS.getPrefix()+"://PV_AO_03[graphMax]";
            ia= addressFactory.createProcessVariableAdress(rawName);
            graphMax= connectionService.readValueSynchronously(ia,ValueType.DOUBLE);

            assertTrue(Math.abs(min-graphMin) > 0.0001);
            assertTrue(Math.abs(max-graphMax) > 0.0001);


        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    public void testDestroyEPICS() {

        try {

            String rawName= ControlSystemEnum.DAL_EPICS.getPrefix()+"://PV_AO_11";
            IProcessVariableAddress ia= addressFactory.createProcessVariableAdress(rawName);
            IPVVListener l= new IPVVListener();
            connectionService.register(l, ia, ValueType.DOUBLE);

            PropertyFactory factory = DALPropertyFactoriesProvider.getInstance()
            .getPropertyFactory(ia.getControlSystem());
            DynamicValueProperty pp= factory.getPropertyFamily().getFirst("PV_AO_11");
            assertNotNull(pp);
            //assertEquals(org.csstudio.dal.context.ConnectionState.INITIAL, pp.getConnectionState());

            connectionService.writeValueAsynchronously(ia, 10.0, ValueType.DOUBLE, null);
            double d= connectionService.readValueSynchronously(ia, ValueType.DOUBLE);
            assertEquals(10.0, d, 0.0001);
            assertEquals(org.csstudio.dal.context.ConnectionState.CONNECTED, pp.getConnectionState());

            assertNotNull(l.value);
            assertNotNull(l.state);
            assertNull(l.error);

            Thread.sleep(3100);
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

    public void testWaveform() {

        try {

            String[] names= new String[]{"T:2000_wf","T:2000s_wf","T:4000_wf","T:4000s_wf","T:8000_wf","T:8000s_wf"};
            //String[] names= new String[]{"T:2000_wf","T:2000s_wf","T:4000_wf","T:4000s_wf","T:8000_wf","T:8000s_wf","T:8000c_wf"};

            PropertyFactory fac = DALPropertyFactoriesProvider.getInstance()
            .getPropertyFactory(ControlSystemEnum.DAL_EPICS);

            for (int i = 0; i < names.length; i++) {
                System.out.println("*** "+names[i]+" ***");
                DynamicValueProperty<?> p= fac.getProperty(names[i]);

                EpicsUtil.waitTillConnected(p, 10000);

                System.out.println("class: "+p.getClass().getName());
                System.out.println("RTYP: "+Arrays.toString((String[])p.getCharacteristic("RTYP")));
                System.out.println("fieldType: "+p.getCharacteristic("fieldType"));
                System.out.println("size: "+p.getCharacteristic(SequencePropertyCharacteristics.C_SEQUENCE_LENGTH));
                System.out.println("value: "+p.getValue());

                DoubleSeqAccess dsa= p.getDataAccess(DoubleSeqAccess.class);
                double[] dd= dsa.getValue();
                System.out.println("value size: "+dd.length);
                //System.out.println(Arrays.toString(dd));

                fac.getPropertyFamily().destroy(p);

            }

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

}
