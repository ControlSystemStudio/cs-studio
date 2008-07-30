package org.csstudio.nams.common.material;

import junit.framework.Assert;

import org.csstudio.nams.common.testutils.AbstractObject_TestCase;
import org.junit.Test;

/**
 * CUT: {@link AlarmNachricht}.
 */
public class AlarmNachricht_Test extends
		AbstractObject_TestCase<AlarmNachricht> {

	@Test
	public void testInitialize() {
		final String message = "Hallo Welt!";

		final AlarmNachricht alarmNachricht = new AlarmNachricht(message);
		final AlarmNachricht gleicheAlarmNachricht = new AlarmNachricht(message);
		final AlarmNachricht ungleicheAlarmNachricht = new AlarmNachricht(
				"Doof!");

		Assert.assertEquals(message, alarmNachricht.gibNachrichtenText());

		Assert.assertEquals(alarmNachricht, gleicheAlarmNachricht);
		Assert.assertFalse(alarmNachricht.equals(ungleicheAlarmNachricht));
	}

	@Test
	public void testLocalClone() {
		final String message = "Hallo Welt!";

		final AlarmNachricht alarmNachricht = new AlarmNachricht(message);

		final AlarmNachricht alarmNachrichtKlon = alarmNachricht.clone();

		Assert.assertFalse(alarmNachricht == alarmNachrichtKlon);
		Assert.assertEquals(alarmNachricht, alarmNachrichtKlon);
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
				new AlarmNachricht("Test-Nachricht 3") };
	}
}
