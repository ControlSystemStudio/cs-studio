package de.c1wps.desy.ams.allgemeines;

import org.junit.Test;

import de.c1wps.desy.ams.AbstractObject_TestCase;

/**
 * CUT: {@link AlarmNachricht}.
 */
public class AlarmNachricht_Test extends
		AbstractObject_TestCase<AlarmNachricht> {
	
	@Test
	public void testInitialize() {
		String message = "Hallo Welt!";

		AlarmNachricht alarmNachricht = new AlarmNachricht(message);
		AlarmNachricht gleicheAlarmNachricht = new AlarmNachricht(message);
		AlarmNachricht ungleicheAlarmNachricht = new AlarmNachricht("Doof!");

		assertEquals(message, alarmNachricht.gibNachrichtenText());

		assertEquals(alarmNachricht, gleicheAlarmNachricht);
		assertFalse(alarmNachricht.equals(ungleicheAlarmNachricht));
	}	

	@Test
	public void testLocalClone() {
		String message = "Hallo Welt!";

		AlarmNachricht alarmNachricht = new AlarmNachricht(message);

		AlarmNachricht alarmNachrichtKlon = alarmNachricht.clone();

		assertFalse(alarmNachricht == alarmNachrichtKlon);
		assertEquals(alarmNachricht, alarmNachrichtKlon);
	}

	@Override
	protected AlarmNachricht getNewInstanceOfClassUnderTest() {
		return new AlarmNachricht("Test-Nachricht");
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected AlarmNachricht[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		return new AlarmNachricht[] { new AlarmNachricht("Test-Nachricht 1"),
				new AlarmNachricht("Test-Nachricht 2"),
				new AlarmNachricht("Test-Nachricht 3")};
	}
}
