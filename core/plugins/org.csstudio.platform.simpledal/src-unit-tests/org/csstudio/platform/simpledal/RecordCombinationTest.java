package org.csstudio.platform.simpledal;

import static org.junit.Assert.assertNotNull;

import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.model.pvs.ValueType;
import org.csstudio.dal.CharacteristicInfo;
import org.csstudio.dal.DoubleProperty;
import org.csstudio.dal.Timestamp;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RecordCombinationTest {

    private final class ObjectValueListener implements IProcessVariableValueListener<Object> {
        public void connectionStateChanged(final ConnectionState connectionState) {
            // TODO Auto-generated method stub

        }

        public void errorOccured(final String error) {
            //System.out.println("Error: " + error);

        }

        public void valueChanged(final Object value, final Timestamp timestamp) {
            //System.out.println("Object Value change: " + value + " on " + timestamp);
            assertNotNull(value);
        }
    }

//    private final class DoubleValueListener implements IProcessVariableValueListener<Double> {
//        @Override
//        public void connectionStateChanged(ConnectionState connectionState) {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void errorOccured(String error) {
//            //System.out.println("Error: " + error);
//
//        }
//
//        @Override
//        public void valueChanged(Double value, Timestamp timestamp) {
//            //System.out.println("Double Value change: " + value + " on " + timestamp);
//            assertNotNull(value);
//        }
//    }
//
//    private final class EnumValueListener implements IProcessVariableValueListener<Enum> {
//        @Override
//        public void connectionStateChanged(ConnectionState connectionState) {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void errorOccured(String error) {
//            //System.out.println("Error: " + error);
//
//        }
//
//        @Override
//        public void valueChanged(Enum value, Timestamp timestamp) {
//            //System.out.println("Enum Value change: " + value + " on " + timestamp);
//            assertNotNull(value);
//        }
//    }

    private ProcessVariableAdressFactory _addressFactory;
    private IProcessVariableConnectionService _connectionService;

    @Before
    public void setUp() throws Exception {
        // the factory for pv addresses
        _addressFactory = ProcessVariableAdressFactory.getInstance();

        assertNotNull(_addressFactory);

        // the connection service
        _connectionService = ProcessVariableConnectionServiceFactory.getDefault()
                .createProcessVariableConnectionService();

        assertNotNull(_connectionService);
    }

    @Test
    public void testname() throws Exception {
        final CharacteristicInfo[] infos= CharacteristicInfo.getDefaultCharacteristics(DoubleProperty.class, null);
        // String rawName= ControlSystemEnum.DAL_SIMULATOR.getPrefix()+"://D1:P1";
        final String rawName = ControlSystemEnum.EPICS.getPrefix() + "://krykWetter:fdUsed_ai";
        for (int i = infos.length-1; i >= 0; i--) {

        final String rawNameHHSV = ControlSystemEnum.EPICS.getPrefix() + "://krykWetter:fdUsed_ai["+infos[i].getName()+"]";
//        String rawNameHHSV = ControlSystemEnum.EPICS.getPrefix() + "://krykWetter:fdUsed_ai";
        // String rawName= ControlSystemEnum.EPICS.getPrefix()+"://krykWetter:fdUsed_ai";
        // String rawName= ControlSystemEnum.EPICS.getPrefix()+"://krykWetter:fdUsed_ai";

        //System.out.println(rawName);

//        IProcessVariableAddress record = _addressFactory.createProcessVariableAdress(rawName);
        final IProcessVariableAddress hhsv = _addressFactory.createProcessVariableAdress(rawNameHHSV);
        //System.out.println("Characteristic: "+hhsv.getFullName());
        //System.out.println("Characteristic: "+hhsv.getCharacteristic());

//        //System.out.println(record.toString());
//        //System.out.println(record.toDalRemoteInfo().toString());

//        _connectionService.readValueAsynchronously(hhsv, ValueType.OBJECT, new ObjectValueListener());
        final Object readValueSynchronously = _connectionService.readValueSynchronously(hhsv, ValueType.OBJECT);
        //System.out.println("Value: " + readValueSynchronously);
        //System.out.println("----------)");
        }
//        _connectionService.readValueAsynchronously(record, ValueType.DOUBLE,
//                new DoubleValueListener());
//        _connectionService.readValueAsynchronously(hhsv, ValueType.DOUBLE,
//                new DoubleValueListener());

        Thread.sleep(5000);

    }

    @After
    public void tearDown() throws Exception {
    }

}
