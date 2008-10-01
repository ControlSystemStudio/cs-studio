package org.csstudio.platform.internal.simpledal;


import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.csstudio.platform.simpledal.ValueType;
import org.epics.css.dal.CharacteristicInfo;
import org.epics.css.dal.DoubleProperty;
import org.epics.css.dal.DynamicValueCondition;
import org.epics.css.dal.DynamicValueState;
import org.epics.css.dal.Timestamp;
import org.epics.css.dal.simulation.PropertyProxyImpl;
import org.epics.css.dal.simulation.SimulatorPlug;
import org.epics.css.dal.simulation.SimulatorUtilities;

public class SimpleDALTest extends TestCase {
	
	class IPVVListener implements IProcessVariableValueListener {
		Object value;
		Timestamp timestamp;
		
		public void connectionStateChanged(ConnectionState connectionState) {
			// TODO Auto-generated method stub
			
		}
		public void errorOccured(String error) {
			// TODO Auto-generated method stub
			
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
	
	public void testGetValue() {
		
		try {

			//IProcessVariableAddress ia= addressFactory.createProcessVariableAdress(ControlSystemEnum.DAL_SIMULATOR, "D1", "P1", null);

			String rawName= ControlSystemEnum.DAL_SIMULATOR.getPrefix()+"://D1:P1";
			
			System.out.println(rawName);
			
			IProcessVariableAddress ia= addressFactory.createProcessVariableAdress(rawName);
			
			System.out.println(ia.toString());
			System.out.println(ia.toDalRemoteInfo().toString());
		
			double d= connectionService.getValueAsDouble(ia);
		
			System.out.println(d);
			
			d= d+1.0;
			
			connectionService.setValue(ia, d);
			
			double d1= connectionService.getValueAsDouble(ia);
			
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
		
			double d= connectionService.getValueAsDouble(ia);
		
			rawName= rawName+"[severity]";
			
			ia= addressFactory.createProcessVariableAdress(rawName);
			
			System.out.println("RI: "+ia.toDalRemoteInfo().toString());
			
			assertTrue("isCharacteristic()==false",ia.isCharacteristic());
			assertEquals("severity", ia.getCharacteristic());

			PropertyProxyImpl pp= SimulatorPlug.getInstance().getSimulatedPropertyProxy(ia.toDalRemoteInfo().getName());
			System.out.println("PP: "+pp.getUniqueName());
			pp.setCondition(new DynamicValueCondition(EnumSet.of(DynamicValueState.ALARM), new Timestamp(),"STATUS1"));
			
			String s= connectionService.getValueAsString(ia);
			System.out.println("severity: "+s);
			assertNotNull(s);
			assertEquals(DynamicValueState.ALARM.toString(), s);
			
			IPVVListener l = new IPVVListener();
			connectionService.registerForStringValues(l, ia);
			
			rawName= ControlSystemEnum.DAL_SIMULATOR.getPrefix()+"://D1:P1[test]";
			IProcessVariableAddress ia1= addressFactory.createProcessVariableAdress(rawName);
			connectionService.registerForStringValues(l, ia1);
			
			pp= SimulatorPlug.getInstance().getSimulatedPropertyProxy(ia.toDalRemoteInfo().getName());
			System.out.println("PP: "+pp.getUniqueName());
			pp.setCondition(new DynamicValueCondition(EnumSet.of(DynamicValueState.ERROR), new Timestamp(),"STATUS2"));
			assertNotNull(l.value);
			assertEquals(DynamicValueState.ERROR.toString(), l.value);
			

			pp.simulateCharacteristicChange("test", "T");
			assertNotNull(l.value);
			assertEquals("T", l.value);
		
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	
	}
	
	public void testCharacteristicInfo() {
		try {
			
			CharacteristicInfo[] ci= CharacteristicInfo.getDefaultCharacteristics(null);
			
			Set<CharacteristicInfo> set= new HashSet<CharacteristicInfo>(Arrays.asList(ci));
			
			assertTrue(set.contains(DalConnector.C_SEVERITY_INFO));
			assertTrue(set.contains(DalConnector.C_STATUS_INFO));
			assertTrue(set.contains(DalConnector.C_TIMESTAMP_INFO));
			
			
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
				
				Object o= connectionService.getValue(ia, ValueType.OBJECT);
				
				System.out.println("RI: "+ia.toDalRemoteInfo().toString()+" "+o);
				
				assertNotNull(o);
				assertTrue(ci[i].getType().isAssignableFrom(o.getClass()));
				
			}
			

			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
	

}
