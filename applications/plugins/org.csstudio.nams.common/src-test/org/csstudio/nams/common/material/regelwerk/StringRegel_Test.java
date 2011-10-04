package org.csstudio.nams.common.material.regelwerk;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.csstudio.nams.common.fachwert.MessageKeyEnum;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.Regelwerkskennung;
import org.csstudio.nams.common.testutils.AbstractTestObject;
import org.csstudio.nams.service.logging.declaration.ILogger;
import org.junit.Test;

public class StringRegel_Test extends AbstractTestObject<StringRegel> {

	@Test
	public void testNumeric() throws Throwable {

		// equal StringRegel - true
		StringRegel sRegel = new StringRegel(
				StringRegelOperator.OPERATOR_NUMERIC_EQUAL,
				MessageKeyEnum.AMS_REINSERTED, "5");
		Pruefliste pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),
				sRegel);
		Map<MessageKeyEnum, String> map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.AMS_REINSERTED, "5");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// equal StringRegel - non true - greater
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_EQUAL,
				MessageKeyEnum.AMS_REINSERTED, "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.AMS_REINSERTED, "6");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// equal StringRegel - non true - smaller
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_EQUAL,
				MessageKeyEnum.AMS_REINSERTED, "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.AMS_REINSERTED, "4");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// gt StringRegel - true
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_GT,
				MessageKeyEnum.APPLICATION_ID, "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.APPLICATION_ID, "6");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// gt StringRegel - non true - equal
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_GT,
				MessageKeyEnum.APPLICATION_ID, "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.APPLICATION_ID, "5");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// gt StringRegel - non true - smaller
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_GT,
				MessageKeyEnum.APPLICATION_ID, "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.APPLICATION_ID, "4");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// gtEqual StringRegel - true - greater
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_GT_EQUAL,
				MessageKeyEnum.CLASS, "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.CLASS, "6");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// gtEqual StringRegel - true - slightly greater
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_GT_EQUAL,
				MessageKeyEnum.CLASS, "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.CLASS, "5.001");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// gtEqual StringRegel - true - equal
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_GT_EQUAL,
				MessageKeyEnum.CLASS, "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.CLASS, "5");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// gtEqual StringRegel - not true - smaller
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_GT_EQUAL,
				MessageKeyEnum.CLASS, "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.CLASS, "4");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// lt StringRegel - not true - greater
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_LT,
				MessageKeyEnum.DESTINATION, "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.DESTINATION, "6");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// lt StringRegel - not true - equal
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_LT,
				MessageKeyEnum.DESTINATION, "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.DESTINATION, "5");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// lt StringRegel - true - slightly smaller
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_LT,
				MessageKeyEnum.DESTINATION, "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.DESTINATION, "4.999");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// lt StringRegel - true - smaller
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_LT,
				MessageKeyEnum.DESTINATION, "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.DESTINATION, "4");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// ltEqual StringRegel - not true - greater
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_LT_EQUAL,
				MessageKeyEnum.DOMAIN, "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.DOMAIN, "6");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// ltEqual StringRegel - true - equal
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_LT_EQUAL,
				MessageKeyEnum.DOMAIN, "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.DOMAIN, "5");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// ltEqual StringRegel - true - slightly smaller
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_LT_EQUAL,
				MessageKeyEnum.DOMAIN, "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.DOMAIN, "4.999");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// ltEqual StringRegel - not true - slightly greater
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_LT_EQUAL,
				MessageKeyEnum.DOMAIN, "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.DOMAIN, "5.001");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// ltEqual StringRegel - true - smaller
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_LT_EQUAL,
				MessageKeyEnum.DOMAIN, "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.DOMAIN, "4");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// not Equal StringRegel - true - greater
		sRegel = new StringRegel(
				StringRegelOperator.OPERATOR_NUMERIC_NOT_EQUAL,
				MessageKeyEnum.EVENTTIME, "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.EVENTTIME, "6");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// not Equal StringRegel - not true - equal
		sRegel = new StringRegel(
				StringRegelOperator.OPERATOR_NUMERIC_NOT_EQUAL,
				MessageKeyEnum.EVENTTIME, "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.EVENTTIME, "5");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// not Equal StringRegel - true - slightly smaller
		sRegel = new StringRegel(
				StringRegelOperator.OPERATOR_NUMERIC_NOT_EQUAL,
				MessageKeyEnum.EVENTTIME, "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.EVENTTIME, "4.999");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// not Equal StringRegel - true - slightly greater
		sRegel = new StringRegel(
				StringRegelOperator.OPERATOR_NUMERIC_NOT_EQUAL,
				MessageKeyEnum.EVENTTIME, "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.EVENTTIME, "5.001");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// not Equal StringRegel - true - smaller
		sRegel = new StringRegel(
				StringRegelOperator.OPERATOR_NUMERIC_NOT_EQUAL,
				MessageKeyEnum.EVENTTIME, "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.EVENTTIME, "4");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));
	}

	@Test
	public void testText() throws Throwable {
		// equal StringRegel - true
		StringRegel sRegel = new StringRegel(
				StringRegelOperator.OPERATOR_TEXT_EQUAL,
				MessageKeyEnum.FACILITY, "Some Test-Text");
		Pruefliste pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),
				sRegel);
		Map<MessageKeyEnum, String> map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.FACILITY, "Some Test-Text");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// equal StringRegel - not true
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_TEXT_EQUAL,
				MessageKeyEnum.FACILITY, "Some Test-Text");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.FACILITY, "Some Test-Text2");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// not equal StringRegel - true
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_TEXT_NOT_EQUAL,
				MessageKeyEnum.HOST, "Some Test-Text");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.HOST, "Some Test-Text2");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// not equal StringRegel - not true
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_TEXT_NOT_EQUAL,
				MessageKeyEnum.HOST, "Some Test-Text");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();
		map.put(MessageKeyEnum.HOST, "Some Test-Text");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));
	}

	@Test
	public void testTime() throws Throwable {
		// timeAfter StringRegel
		StringRegel sRegel = new StringRegel(
				StringRegelOperator.OPERATOR_TIME_AFTER,
				MessageKeyEnum.LOCATION, "5/26/2008");
		Pruefliste pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),
				sRegel);
		Map<MessageKeyEnum, String> map = new HashMap<MessageKeyEnum, String>();

		// one day later
		map.put(MessageKeyEnum.LOCATION, "5/27/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// same day
		map.put(MessageKeyEnum.LOCATION, "5/26/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// one day before
		map.put(MessageKeyEnum.LOCATION, "5/25/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// timeAfterEqual StringRegel
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_TIME_AFTER_EQUAL,
				MessageKeyEnum.LOCATION, "5/26/2008");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();

		// one day later
		map.put(MessageKeyEnum.LOCATION, "5/27/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// same day
		map.put(MessageKeyEnum.LOCATION, "5/26/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// one day before
		map.put(MessageKeyEnum.LOCATION, "5/25/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// timeBefore StringRegel
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_TIME_BEFORE,
				MessageKeyEnum.LOCATION, "5/26/2008");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();

		// one day later
		map.put(MessageKeyEnum.LOCATION, "5/27/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// same day
		map.put(MessageKeyEnum.LOCATION, "5/26/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// one day before
		map.put(MessageKeyEnum.LOCATION, "5/25/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// timeBeforeEqual StringRegel
		sRegel = new StringRegel(
				StringRegelOperator.OPERATOR_TIME_BEFORE_EQUAL,
				MessageKeyEnum.LOCATION, "5/26/2008");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();

		// one day later
		map.put(MessageKeyEnum.LOCATION, "5/27/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// same day
		map.put(MessageKeyEnum.LOCATION, "5/26/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// one day before
		map.put(MessageKeyEnum.LOCATION, "5/25/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// equal StringRegel
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_TIME_EQUAL,
				MessageKeyEnum.LOCATION, "5/26/2008");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();

		// one day later
		map.put(MessageKeyEnum.LOCATION, "5/27/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// same day
		map.put(MessageKeyEnum.LOCATION, "5/26/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// one day before
		map.put(MessageKeyEnum.LOCATION, "5/25/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// timeAfterEqual StringRegel
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_TIME_NOT_EQUAL,
				MessageKeyEnum.LOCATION, "5/26/2008");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), sRegel);
		map = new HashMap<MessageKeyEnum, String>();

		// one day later
		map.put(MessageKeyEnum.LOCATION, "5/27/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// same day
		map.put(MessageKeyEnum.LOCATION, "5/26/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));

		// one day before
		map.put(MessageKeyEnum.LOCATION, "5/25/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(sRegel));
	}

	@Override
	protected StringRegel getNewInstanceOfClassUnderTest() {
		return new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_EQUAL,
				MessageKeyEnum.AMS_REINSERTED, "5");
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected StringRegel[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		final StringRegel[] regels = new StringRegel[3];
		regels[0] = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_EQUAL,
				MessageKeyEnum.APPLICATION_ID, "5");
		regels[1] = new StringRegel(
				StringRegelOperator.OPERATOR_NUMERIC_GT_EQUAL,
				MessageKeyEnum.CLASS, "6");
		regels[2] = new StringRegel(StringRegelOperator.OPERATOR_TEXT_EQUAL,
				MessageKeyEnum.DESTINATION, "7");

		return regels;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		StringRegel.staticInject(new ILogger() {
			public void logDebugMessage(final Object caller,
					final String message) {
			}

			public void logDebugMessage(final Object caller,
					final String message, final Throwable throwable) {
			}

			public void logErrorMessage(final Object caller,
					final String message) {
			}

			public void logErrorMessage(final Object caller,
					final String message, final Throwable throwable) {
			}

			public void logFatalMessage(final Object caller,
					final String message) {
			}

			public void logFatalMessage(final Object caller,
					final String message, final Throwable throwable) {
			}

			public void logInfoMessage(final Object caller, final String message) {
			}

			public void logInfoMessage(final Object caller,
					final String message, final Throwable throwable) {
			}

			public void logWarningMessage(final Object caller,
					final String message) {
			}

			public void logWarningMessage(final Object caller,
					final String message, final Throwable throwable) {
			}
		});
	}
}
