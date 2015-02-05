package org.csstudio.dal.epics.demo;

import java.util.Map;

import org.csstudio.dal.AccessType;
import org.csstudio.dal.DoubleProperty;
import org.csstudio.dal.PropertyCharacteristics;
import org.csstudio.dal.RemoteException;
import org.csstudio.dal.epics.EPICSApplicationContext;
import org.csstudio.dal.epics.EpicsPropertyCharacteristics;
import org.csstudio.dal.spi.DefaultPropertyFactoryService;
import org.csstudio.dal.spi.LinkPolicy;
import org.csstudio.dal.spi.PropertyFactory;

public class CharacteristicsDemo {

	public static void main(final String[] args) throws RemoteException, InstantiationException {

		final PropertyFactory pf = DefaultPropertyFactoryService.getPropertyFactoryService().getPropertyFactory(new EPICSApplicationContext("test"), LinkPolicy.SYNC_LINK_POLICY);

		final DoubleProperty dp = pf.getProperty("manyChannel_001", DoubleProperty.class, null);

		//ask for all characteristics
		final String[] names = dp.getCharacteristicNames();
		final Map<String, Object> characteristics = dp.getCharacteristics(names);
		System.out.println(characteristics);

		//ask only for the new characteristics
		final String hostname = (String) dp.getCharacteristic(PropertyCharacteristics.C_HOSTNAME);
		System.out.println("Hostname: " + hostname);
		final String dataType = (String) dp.getCharacteristic(PropertyCharacteristics.C_DATATYPE);
		System.out.println("DataType: " + dataType);
		final AccessType accessType = (AccessType) dp.getCharacteristic(PropertyCharacteristics.C_ACCESS_TYPE);
		System.out.println("AccessType: " + accessType);
		final Integer nelm = (Integer)dp.getCharacteristic(EpicsPropertyCharacteristics.EPICS_NUMBER_OF_ELEMENTS);
		System.out.println("NELM: " + nelm);
	}
}
