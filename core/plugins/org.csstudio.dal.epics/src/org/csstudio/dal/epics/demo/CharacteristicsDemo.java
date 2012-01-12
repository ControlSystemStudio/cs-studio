package org.csstudio.dal.epics.demo;

import java.util.Map;

import org.csstudio.dal.epics.EPICSApplicationContext;
import org.csstudio.dal.epics.EpicsPropertyCharacteristics;
import org.epics.css.dal.AccessType;
import org.epics.css.dal.DoubleProperty;
import org.epics.css.dal.PropertyCharacteristics;
import org.epics.css.dal.RemoteException;
import org.epics.css.dal.spi.DefaultPropertyFactoryService;
import org.epics.css.dal.spi.LinkPolicy;
import org.epics.css.dal.spi.PropertyFactory;

public class CharacteristicsDemo {
	
	public static void main(String[] args) throws RemoteException, InstantiationException {
		
		PropertyFactory pf = DefaultPropertyFactoryService.getPropertyFactoryService().getPropertyFactory(new EPICSApplicationContext("test"), LinkPolicy.SYNC_LINK_POLICY);
		
		DoubleProperty dp = pf.getProperty("manyChannel_001", DoubleProperty.class, null);
	
		//ask for all characteristics
		String[] names = dp.getCharacteristicNames();
		Map<String, Object> characteristics = dp.getCharacteristics(names);
		System.out.println(characteristics);
		
		//ask only for the new characteristics
		String hostname = (String) dp.getCharacteristic(PropertyCharacteristics.C_HOSTNAME);
		System.out.println("Hostname: " + hostname);
		String dataType = (String) dp.getCharacteristic(PropertyCharacteristics.C_DATATYPE);
		System.out.println("DataType: " + dataType);
		AccessType accessType = (AccessType) dp.getCharacteristic(PropertyCharacteristics.C_ACCESS_TYPE);
		System.out.println("AccessType: " + accessType);
		Integer nelm = (Integer)dp.getCharacteristic(EpicsPropertyCharacteristics.EPICS_NUMBER_OF_ELEMENTS);
		System.out.println("NELM: " + nelm);
	}
}
