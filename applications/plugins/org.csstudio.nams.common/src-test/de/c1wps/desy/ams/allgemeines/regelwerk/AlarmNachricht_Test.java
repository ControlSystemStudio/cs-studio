package de.c1wps.desy.ams.allgemeines.regelwerk;

import java.util.HashMap;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.AlarmNachricht;
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
	
}
