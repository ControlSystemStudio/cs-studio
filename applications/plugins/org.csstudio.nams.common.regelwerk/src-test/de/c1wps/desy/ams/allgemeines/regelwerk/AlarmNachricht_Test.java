package de.c1wps.desy.ams.allgemeines.regelwerk;

import java.util.HashMap;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.Regelwerkskennung;
import org.csstudio.nams.common.testutils.AbstractObject_TestCase;

public class AlarmNachricht_Test extends AbstractObject_TestCase<AlarmNachricht>{

	@Override
	protected AlarmNachricht getNewInstanceOfClassUnderTest() {
		
		return new AlarmNachricht("test");
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected AlarmNachricht[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		AlarmNachricht[] instances = new AlarmNachricht[3];
		instances[0] = new AlarmNachricht(new HashMap<MessageKeyEnum, String>());
		instances[1] = new AlarmNachricht(new HashMap<MessageKeyEnum, String>());
		instances[2] = new AlarmNachricht(new HashMap<MessageKeyEnum, String>());
		return instances;
	}
	
	public void testValueFor(){
		HashMap<MessageKeyEnum,String> map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.LOCATION, "Tschernobüllerbü");
		AlarmNachricht nachricht = new AlarmNachricht(map);
		
		assertEquals("", nachricht.getValueFor(MessageKeyEnum.DESTINATION));
		assertEquals("Tschernobüllerbü", nachricht.getValueFor(MessageKeyEnum.LOCATION));
	}
	
	public void testMatechedWithRegelwerk(){
		HashMap<MessageKeyEnum,String> map = new HashMap<MessageKeyEnum, String>();
		AlarmNachricht nachricht = new AlarmNachricht(map);
		Regelwerkskennung regelwerkskennung = Regelwerkskennung.valueOf(5, "testWerk");
		
		assertEquals("", nachricht.getValueFor(MessageKeyEnum.AMS_REINSERTED));
		
		nachricht.matchedMessageWithRegelwerk(regelwerkskennung);
		
		assertEquals(String.valueOf(5), nachricht.getValueFor(MessageKeyEnum.AMS_REINSERTED));
	}
	
}
