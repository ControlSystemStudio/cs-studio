package de.c1wps.desy.ams.allgemeines.regelwerk;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.AlarmNachrichtEnum;
import org.csstudio.nams.common.material.regelwerk.Pruefliste;
import org.csstudio.nams.common.material.regelwerk.RegelErgebnis;
import org.csstudio.nams.common.material.regelwerk.Regelwerkskennung;
import org.csstudio.nams.common.material.regelwerk.StringRegel;
import org.csstudio.nams.common.material.regelwerk.StringRegelOperator;
import org.csstudio.nams.common.testutils.AbstractObject_TestCase;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.junit.Test;

public class StringRegel_Test extends
		AbstractObject_TestCase<StringRegel> {

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		StringRegel.staticInject(new Logger(){
			public void logDebugMessage(Object caller, String message) {
				// TODO Auto-generated method stub
				
			}

			public void logDebugMessage(Object caller, String message,
					Throwable throwable) {
				// TODO Auto-generated method stub
				
			}

			public void logErrorMessage(Object caller, String message) {
				// TODO Auto-generated method stub
				
			}

			public void logErrorMessage(Object caller, String message,
					Throwable throwable) {
				// TODO Auto-generated method stub
				
			}

			public void logFatalMessage(Object caller, String message) {
				// TODO Auto-generated method stub
				
			}
			
			public void logFatalMessage(Object caller, String message,
					Throwable throwable) {
				// TODO Auto-generated method stub
				
			}

			public void logInfoMessage(Object caller, String message) {
				// TODO Auto-generated method stub
				
			}

			public void logInfoMessage(Object caller, String message,
					Throwable throwable) {
				// TODO Auto-generated method stub
				
			}

			public void logWarningMessage(Object caller, String message) {
				// TODO Auto-generated method stub
				
			}

			public void logWarningMessage(Object caller, String message,
					Throwable throwable) {
				// TODO Auto-generated method stub
				
			}});
	}
	
	@Test
	public void testNumeric() throws Throwable {

		// equal StringRegel - true
		StringRegel sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_EQUAL, AlarmNachrichtEnum.AMS_REINSERTED.toString(), "5");
		Pruefliste pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel);
		Map<String, String> map = new HashMap<String, String>();
		map.put(AlarmNachrichtEnum.AMS_REINSERTED.toString(), "5");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
		
		// equal StringRegel - non true - greater
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_EQUAL, AlarmNachrichtEnum.AMS_REINSERTED.toString(), "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel); 
		map = new HashMap<String, String>();
		map.put(AlarmNachrichtEnum.AMS_REINSERTED.toString(), "6");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
		
		// equal StringRegel - non true - smaller
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_EQUAL, AlarmNachrichtEnum.AMS_REINSERTED.toString(), "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel); 
		map = new HashMap<String, String>();
		map.put(AlarmNachrichtEnum.AMS_REINSERTED.toString(), "4");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
		
		// gt StringRegel - true 
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_GT, AlarmNachrichtEnum.APPLICATION_ID.toString(), "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel); 
		map = new HashMap<String, String>();
		map.put(AlarmNachrichtEnum.APPLICATION_ID.toString(), "6");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));

		// gt StringRegel - non true - equal
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_GT, AlarmNachrichtEnum.APPLICATION_ID.toString(), "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel); 
		map = new HashMap<String, String>();
		map.put(AlarmNachrichtEnum.APPLICATION_ID.toString(), "5");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
		
		// gt StringRegel - non true - smaller
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_GT, AlarmNachrichtEnum.APPLICATION_ID.toString(), "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel); 
		map = new HashMap<String, String>();
		map.put(AlarmNachrichtEnum.APPLICATION_ID.toString(), "4");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
		
		// gtEqual StringRegel - true - greater
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_GT_EQUAL, AlarmNachrichtEnum.CLASS.toString(), "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel); 
		map = new HashMap<String, String>();
		map.put(AlarmNachrichtEnum.CLASS.toString(), "6");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));

		// gtEqual StringRegel - true - slightly greater
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_GT_EQUAL, AlarmNachrichtEnum.CLASS.toString(), "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel); 
		map = new HashMap<String, String>();
		map.put(AlarmNachrichtEnum.CLASS.toString(), "5.001");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
		
		// gtEqual StringRegel - true - equal
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_GT_EQUAL, AlarmNachrichtEnum.CLASS.toString(), "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel); 
		map = new HashMap<String, String>();
		map.put(AlarmNachrichtEnum.CLASS.toString(), "5");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
		
		// gtEqual StringRegel - not true - smaller
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_GT_EQUAL, AlarmNachrichtEnum.CLASS.toString(), "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel); 
		map = new HashMap<String, String>();
		map.put(AlarmNachrichtEnum.CLASS.toString(), "4");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
		
		// lt StringRegel - not true - greater
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_LT, AlarmNachrichtEnum.DESTINATION.toString(), "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel); 
		map = new HashMap<String, String>();
		map.put(AlarmNachrichtEnum.DESTINATION.toString(), "6");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));

		// lt StringRegel - not true - equal
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_LT, AlarmNachrichtEnum.DESTINATION.toString(), "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel); 
		map = new HashMap<String, String>();
		map.put(AlarmNachrichtEnum.DESTINATION.toString(), "5");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));

		// lt StringRegel - true - slightly smaller
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_LT, AlarmNachrichtEnum.DESTINATION.toString(), "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel); 
		map = new HashMap<String, String>();
		map.put(AlarmNachrichtEnum.DESTINATION.toString(), "4.999");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
		
		// lt StringRegel - true - smaller
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_LT, AlarmNachrichtEnum.DESTINATION.toString(), "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel); 
		map = new HashMap<String, String>();
		map.put(AlarmNachrichtEnum.DESTINATION.toString(), "4");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
		
		// ltEqual StringRegel - not true - greater
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_LT_EQUAL, AlarmNachrichtEnum.DOMAIN.toString(), "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel); 
		map = new HashMap<String, String>();
		map.put(AlarmNachrichtEnum.DOMAIN.toString(), "6");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));

		// ltEqual StringRegel - true - equal
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_LT_EQUAL, AlarmNachrichtEnum.DOMAIN.toString(), "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel); 
		map = new HashMap<String, String>();
		map.put(AlarmNachrichtEnum.DOMAIN.toString(), "5");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));

		// ltEqual StringRegel - true - slightly smaller
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_LT_EQUAL, AlarmNachrichtEnum.DOMAIN.toString(), "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel); 
		map = new HashMap<String, String>();
		map.put(AlarmNachrichtEnum.DOMAIN.toString(), "4.999");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));

		// ltEqual StringRegel - not true - slightly greater
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_LT_EQUAL, AlarmNachrichtEnum.DOMAIN.toString(), "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel); 
		map = new HashMap<String, String>();
		map.put(AlarmNachrichtEnum.DOMAIN.toString(), "5.001");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
		
		// ltEqual StringRegel - true - smaller
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_LT_EQUAL, AlarmNachrichtEnum.DOMAIN.toString(), "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel); 
		map = new HashMap<String, String>();
		map.put(AlarmNachrichtEnum.DOMAIN.toString(), "4");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
		
		// not Equal StringRegel - true - greater
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_NOT_EQUAL, AlarmNachrichtEnum.EVENTTIME.toString(), "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel); 
		map = new HashMap<String, String>();
		map.put(AlarmNachrichtEnum.EVENTTIME.toString(), "6");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));

		// not Equal StringRegel - not true - equal
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_NOT_EQUAL, AlarmNachrichtEnum.EVENTTIME.toString(), "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel); 
		map = new HashMap<String, String>();
		map.put(AlarmNachrichtEnum.EVENTTIME.toString(), "5");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));

		// not Equal StringRegel - true - slightly smaller
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_NOT_EQUAL, AlarmNachrichtEnum.EVENTTIME.toString(), "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel); 
		map = new HashMap<String, String>();
		map.put(AlarmNachrichtEnum.EVENTTIME.toString(), "4.999");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));

		// not Equal StringRegel - true - slightly greater
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_NOT_EQUAL, AlarmNachrichtEnum.EVENTTIME.toString(), "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel); 
		map = new HashMap<String, String>();
		map.put(AlarmNachrichtEnum.EVENTTIME.toString(), "5.001");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
		
		// not Equal StringRegel - true - smaller
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_NOT_EQUAL, AlarmNachrichtEnum.EVENTTIME.toString(), "5");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel); 
		map = new HashMap<String, String>();
		map.put(AlarmNachrichtEnum.EVENTTIME.toString(), "4");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
	}
	
	@Test
	public void testText() throws Throwable {
		// equal StringRegel - true
		StringRegel sRegel = new StringRegel(StringRegelOperator.OPERATOR_TEXT_EQUAL, AlarmNachrichtEnum.FACILITY.toString(), "Some Test-Text");
		Pruefliste pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel);
		Map<String, String> map = new HashMap<String, String>();
		map.put(AlarmNachrichtEnum.FACILITY.toString(), "Some Test-Text");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
		
		// equal StringRegel - not true
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_TEXT_EQUAL, AlarmNachrichtEnum.FACILITY.toString(), "Some Test-Text");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel);
		map = new HashMap<String, String>();
		map.put(AlarmNachrichtEnum.FACILITY.toString(), "Some Test-Text2");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
		
		// not equal StringRegel - true
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_TEXT_NOT_EQUAL, AlarmNachrichtEnum.HOST.toString(), "Some Test-Text");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel);
		map = new HashMap<String, String>();
		map.put(AlarmNachrichtEnum.HOST.toString(), "Some Test-Text2");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
		
		// not equal StringRegel - not true
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_TEXT_NOT_EQUAL, AlarmNachrichtEnum.HOST.toString(), "Some Test-Text");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel);
		map = new HashMap<String, String>();
		map.put(AlarmNachrichtEnum.HOST.toString(), "Some Test-Text");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
	}
	
	@Test
	public void testTime() throws Throwable {
		// timeAfter StringRegel
		StringRegel sRegel = new StringRegel(StringRegelOperator.OPERATOR_TIME_AFTER, AlarmNachrichtEnum.LOCATION.toString(), "5/26/2008");
		Pruefliste pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel);
		Map<String, String> map = new HashMap<String, String>();
		
		// one day later
		map.put(AlarmNachrichtEnum.LOCATION.toString(), "5/27/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
				
		// same day
		map.put(AlarmNachrichtEnum.LOCATION.toString(), "5/26/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
		
		// one day before
		map.put(AlarmNachrichtEnum.LOCATION.toString(), "5/25/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
		
		
		// timeAfterEqual StringRegel
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_TIME_AFTER_EQUAL, AlarmNachrichtEnum.LOCATION.toString(), "5/26/2008");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel);
		map = new HashMap<String, String>();
		
		// one day later
		map.put(AlarmNachrichtEnum.LOCATION.toString(), "5/27/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
				
		// same day
		map.put(AlarmNachrichtEnum.LOCATION.toString(), "5/26/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
		
		// one day before
		map.put(AlarmNachrichtEnum.LOCATION.toString(), "5/25/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
		
		
		// timeBefore StringRegel
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_TIME_BEFORE, AlarmNachrichtEnum.LOCATION.toString(), "5/26/2008");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel);
		map = new HashMap<String, String>();
		
		// one day later
		map.put(AlarmNachrichtEnum.LOCATION.toString(), "5/27/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
				
		// same day
		map.put(AlarmNachrichtEnum.LOCATION.toString(), "5/26/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
		
		// one day before
		map.put(AlarmNachrichtEnum.LOCATION.toString(), "5/25/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
		
		
		// timeBeforeEqual StringRegel
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_TIME_BEFORE_EQUAL, AlarmNachrichtEnum.LOCATION.toString(), "5/26/2008");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel);
		map = new HashMap<String, String>();
		
		// one day later
		map.put(AlarmNachrichtEnum.LOCATION.toString(), "5/27/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
				
		// same day
		map.put(AlarmNachrichtEnum.LOCATION.toString(), "5/26/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
		
		// one day before
		map.put(AlarmNachrichtEnum.LOCATION.toString(), "5/25/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
		
		
		// equal StringRegel
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_TIME_EQUAL, AlarmNachrichtEnum.LOCATION.toString(), "5/26/2008");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel);
		map = new HashMap<String, String>();
		
		// one day later
		map.put(AlarmNachrichtEnum.LOCATION.toString(), "5/27/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
				
		// same day
		map.put(AlarmNachrichtEnum.LOCATION.toString(), "5/26/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
		
		// one day before
		map.put(AlarmNachrichtEnum.LOCATION.toString(), "5/25/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
		
		
		// timeAfterEqual StringRegel
		sRegel = new StringRegel(StringRegelOperator.OPERATOR_TIME_NOT_EQUAL, AlarmNachrichtEnum.LOCATION.toString(), "5/26/2008");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),sRegel);
		map = new HashMap<String, String>();
		
		// one day later
		map.put(AlarmNachrichtEnum.LOCATION.toString(), "5/27/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
				
		// same day
		map.put(AlarmNachrichtEnum.LOCATION.toString(), "5/26/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
		
		// one day before
		map.put(AlarmNachrichtEnum.LOCATION.toString(), "5/25/2008");
		sRegel.pruefeNachrichtErstmalig(new AlarmNachricht(map), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(sRegel));
	}
	
	@Override
	protected StringRegel getNewInstanceOfClassUnderTest() {
		return new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_EQUAL, "egal", "5");
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected StringRegel[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		StringRegel[] regels = new StringRegel[3];
		regels[0] = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_EQUAL, "egal", "5");
		regels[1] = new StringRegel(StringRegelOperator.OPERATOR_NUMERIC_GT_EQUAL, "egal1", "6");
		regels[2] = new StringRegel(StringRegelOperator.OPERATOR_TEXT_EQUAL, "egal2", "7");
		
		return regels;
	}
}
