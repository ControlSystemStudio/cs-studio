package de.c1wps.desy.ams.allgemeines.regelwerk;

import org.csstudio.ams.configurationStoreService.util.Operator;
import org.csstudio.ams.configurationStoreService.util.SuggestedProcessVariableType;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.regelwerk.ProcessVariableRegel;
import org.csstudio.nams.common.material.regelwerk.Pruefliste;
import org.csstudio.nams.common.material.regelwerk.RegelErgebnis;
import org.csstudio.nams.common.material.regelwerk.Regelwerkskennung;
import org.csstudio.nams.common.testutils.AbstractObject_TestCase;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.DalPropertyTypes;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.ValueType;
import org.epics.css.dal.context.RemoteInfo;
import org.junit.Test;

public class ProcessVariableRegel_Test extends
		AbstractObject_TestCase<ProcessVariableRegel> {

	
	private ConnectionServiceMock _connectionServiceMock;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		_connectionServiceMock = createPVServiceMock();
		ProcessVariableRegel.staticInject(new Logger(){

			@Override
			public void logDebugMessage(Object caller, String message) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void logDebugMessage(Object caller, String message,
					Throwable throwable) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void logErrorMessage(Object caller, String message) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void logErrorMessage(Object caller, String message,
					Throwable throwable) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void logFatalMessage(Object caller, String message) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void logFatalMessage(Object caller, String message,
					Throwable throwable) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void logInfoMessage(Object caller, String message) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void logInfoMessage(Object caller, String message,
					Throwable throwable) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void logWarningMessage(Object caller, String message) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void logWarningMessage(Object caller, String message,
					Throwable throwable) {
				// TODO Auto-generated method stub
				
			}});
	}
	
	@Test
	public void testMatchOfLongValuesSmallerThan5() throws Throwable {
		
		IProcessVariableAddress channelName = createDefaultPVAdress();
		Operator operator = Operator.SMALLER;
		SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.LONG;
		Object compValue = 5l;

		ProcessVariableRegel pvRegel = new ProcessVariableRegel(_connectionServiceMock, channelName,
				operator, suggestedProcessVariableType, compValue);

		Pruefliste pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		
		// Without connection:
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		// Now all with connection...
		_connectionServiceMock.sendNewConnectionState(ConnectionState.CONNECTED);
		
		// Without any current value:
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test2"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		// With a not matching value:
		_connectionServiceMock.sendNewValue(new Long(6));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test2"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		_connectionServiceMock.sendNewValue(new Long(5));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test2"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		// With matching value:
		_connectionServiceMock.sendNewValue(new Long(4));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test2"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
	}

	@Test
	public void testMatchOfDoubleValuesSmallerThan5() throws Throwable {

		IProcessVariableAddress channelName = createDefaultPVAdress();
		Operator operator = Operator.SMALLER;
		SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.DOUBLE;
		Object compValue = 5d;

		ProcessVariableRegel pvRegel = new ProcessVariableRegel(_connectionServiceMock, channelName,
				operator, suggestedProcessVariableType, compValue);
		
		// Without connection:
		Pruefliste pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		// Now all with connection...
		_connectionServiceMock.sendNewConnectionState(ConnectionState.CONNECTED);
		
		// Without any current value:
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		// With a not matching value:
		_connectionServiceMock.sendNewValue(new Double(6.0));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		_connectionServiceMock.sendNewValue(new Double(5.0));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		// With matching value:
		_connectionServiceMock.sendNewValue(new Double(4.9));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
	}

	@Test
	public void testMatchOfLongValuesGreaterThan5() throws Throwable {
		IProcessVariableAddress channelName = createDefaultPVAdress();
		Operator operator = Operator.GREATER;
		SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.LONG;
		Object compValue = 5l;

		ProcessVariableRegel pvRegel = new ProcessVariableRegel(_connectionServiceMock, channelName,
				operator, suggestedProcessVariableType, compValue);
		
		// Without connection:
		Pruefliste pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		// Now all with connection...
		_connectionServiceMock.sendNewConnectionState(ConnectionState.CONNECTED);
		
		// Without any current value:
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		// With a not matching value:
		_connectionServiceMock.sendNewValue(new Long(4));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		_connectionServiceMock.sendNewValue(new Long(5));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		// With matching value:
		_connectionServiceMock.sendNewValue(new Long(6));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
	}
	
	@Test
	public void testMatchOfDoubleValuesGreaterThan5() throws Throwable {
		IProcessVariableAddress channelName = createDefaultPVAdress();
		Operator operator = Operator.GREATER;
		SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.DOUBLE;
		Object compValue = 5d;

		ProcessVariableRegel pvRegel = new ProcessVariableRegel(_connectionServiceMock, channelName,
				operator, suggestedProcessVariableType, compValue);
		
		// Without connection:
		Pruefliste pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		// Now all with connection...
		_connectionServiceMock.sendNewConnectionState(ConnectionState.CONNECTED);
		
		// Without any current value:
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		// With a not matching value:
		_connectionServiceMock.sendNewValue(new Double(4.0));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		_connectionServiceMock.sendNewValue(new Double(5.0));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		// With matching value:
		_connectionServiceMock.sendNewValue(new Double(6.1));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
	}
	
	@Test
	public void testMatchOfLongValuesEquals5() throws Throwable {
		IProcessVariableAddress channelName = createDefaultPVAdress();
		Operator operator = Operator.EQUALS;
		SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.LONG;
		Object compValue = 5l;

		ProcessVariableRegel pvRegel = new ProcessVariableRegel(_connectionServiceMock, channelName,
				operator, suggestedProcessVariableType, compValue);
		
		// Without connection:
		Pruefliste pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		// Now all with connection...
		_connectionServiceMock.sendNewConnectionState(ConnectionState.CONNECTED);
		
		// Without any current value:
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		// With a not matching value:
		_connectionServiceMock.sendNewValue(new Long(4));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		_connectionServiceMock.sendNewValue(new Long(6));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		_connectionServiceMock.sendNewValue(new Long(50));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		// With matching value:
		_connectionServiceMock.sendNewValue(new Long(5));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
	}
	
	@Test
	public void testMatchOfDoubleValuesEquals5() throws Throwable {
		
		IProcessVariableAddress channelName = createDefaultPVAdress();
		Operator operator = Operator.EQUALS;
		SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.DOUBLE;
		Object compValue = 5d;

		ProcessVariableRegel pvRegel = new ProcessVariableRegel(_connectionServiceMock, channelName,
				operator, suggestedProcessVariableType, compValue);
		
		// Without connection:
		Pruefliste pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		// Now all with connection...
		_connectionServiceMock.sendNewConnectionState(ConnectionState.CONNECTED);
		
		// Without any current value:
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		// With a not matching value:
		_connectionServiceMock.sendNewValue(new Double(4.0));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		_connectionServiceMock.sendNewValue(new Double(5.1));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
				
		// With matching value:
		_connectionServiceMock.sendNewValue(new Double(5.0));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		_connectionServiceMock.sendNewValue(new Double(5.0000001));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
	}
	
	@Test
	public void testMatchOfLongValuesUnequals5() throws Throwable {
		IProcessVariableAddress channelName = createDefaultPVAdress();
		Operator operator = Operator.UNEQUALS;
		SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.LONG;
		Object compValue = 5l;

		ProcessVariableRegel pvRegel = new ProcessVariableRegel(_connectionServiceMock, channelName,
				operator, suggestedProcessVariableType, compValue);
		
		// Without connection:
		Pruefliste pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		// Now all with connection...
		_connectionServiceMock.sendNewConnectionState(ConnectionState.CONNECTED);
		
		// Without any current value:
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		// With a not matching value:
		_connectionServiceMock.sendNewValue(new Long(5));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		// With matching value:
		_connectionServiceMock.sendNewValue(new Long(4));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		_connectionServiceMock.sendNewValue(new Long(6));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		_connectionServiceMock.sendNewValue(new Long(50));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
	}
	
	@Test
	public void testMatchOfDoubleValuesUnequals5() throws Throwable {
		IProcessVariableAddress channelName = createDefaultPVAdress();
		Operator operator = Operator.UNEQUALS;
		SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.DOUBLE;
		Object compValue = 5d;

		ProcessVariableRegel pvRegel = new ProcessVariableRegel(_connectionServiceMock, channelName,
				operator, suggestedProcessVariableType, compValue);
		
		// Without connection:
		Pruefliste pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		// Now all with connection...
		_connectionServiceMock.sendNewConnectionState(ConnectionState.CONNECTED);
		
		// Without any current value:
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		// With a not matching value:
		_connectionServiceMock.sendNewValue(new Double(5.0));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		_connectionServiceMock.sendNewValue(new Double(5.000001));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		// With matching value:
		_connectionServiceMock.sendNewValue(new Double(4.9));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		_connectionServiceMock.sendNewValue(new Double(5.1));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
	}
	
	@Test
	public void testMatchOfStringValueEquals() throws Throwable {
		IProcessVariableAddress channelName = createDefaultPVAdress();
		Operator operator = Operator.EQUALS;
		SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.STRING;
		Object compValue = "Foo";

		ProcessVariableRegel pvRegel = new ProcessVariableRegel(_connectionServiceMock, channelName,
				operator, suggestedProcessVariableType, compValue);
		
		// Without connection:
		Pruefliste pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		// Now all with connection...
		_connectionServiceMock.sendNewConnectionState(ConnectionState.CONNECTED);
		
		// Without any current value:
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		// With a not matching value:
		_connectionServiceMock.sendNewValue("NotFoo");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		// With matching value:
		_connectionServiceMock.sendNewValue("Foo");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
	}
	
	@Test
	public void testMatchOfStringValueUnequals() throws Throwable {
		IProcessVariableAddress channelName = createDefaultPVAdress();
		Operator operator = Operator.UNEQUALS;
		SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.STRING;
		Object compValue = "Foo";

		ProcessVariableRegel pvRegel = new ProcessVariableRegel(_connectionServiceMock, channelName,
				operator, suggestedProcessVariableType, compValue);

		// Without connection:
		Pruefliste pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		// Now all with connection...
		_connectionServiceMock.sendNewConnectionState(ConnectionState.CONNECTED);
		
		// Without any current value:
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		// With a not matching value:
		_connectionServiceMock.sendNewValue("Foo");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
		
		// With matching value:
		_connectionServiceMock.sendNewValue("NotFoo");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"), pListe);
		assertEquals(RegelErgebnis.ZUTREFFEND, pListe.gibErgebnisFuerRegel(pvRegel));
	}
	
	
	
	@Override
	protected ProcessVariableRegel getNewInstanceOfClassUnderTest() {
		IProcessVariableConnectionService pvService = createPVServiceMock();
		IProcessVariableAddress channelName = createDefaultPVAdress();
		Operator operator = Operator.EQUALS;
		SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.STRING;
		Object compValue = "test";

		return new ProcessVariableRegel(pvService, channelName, operator,
				suggestedProcessVariableType, compValue);
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected ProcessVariableRegel[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		ProcessVariableRegel[] regels = new ProcessVariableRegel[3];
		{
			IProcessVariableConnectionService pvService = createPVServiceMock();
			IProcessVariableAddress channelName = createDefaultPVAdress();
			Operator operator = Operator.EQUALS;
			SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.STRING;
			Object compValue = "test2";

			regels[0] = new ProcessVariableRegel(pvService, channelName,
					operator, suggestedProcessVariableType, compValue);
		}
		{
			IProcessVariableConnectionService pvService = createPVServiceMock();
			IProcessVariableAddress channelName = createDefaultPVAdress();
			Operator operator = Operator.UNEQUALS;
			SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.STRING;
			Object compValue = "test";

			regels[1] = new ProcessVariableRegel(pvService, channelName,
					operator, suggestedProcessVariableType, compValue);
		}
		{
			IProcessVariableConnectionService pvService = createPVServiceMock();
			IProcessVariableAddress channelName = createDefaultPVAdress();
			Operator operator = Operator.EQUALS;
			SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.LONG;
			Object compValue = 42l;

			regels[2] = new ProcessVariableRegel(pvService, channelName,
					operator, suggestedProcessVariableType, compValue);
		}
		return regels;
	}

	ConnectionServiceMock createPVServiceMock() {
		return new ConnectionServiceMock();
	}

	private IProcessVariableAddress createDefaultPVAdress() {
		return new IProcessVariableAddress() {

			public String getCharacteristic() {
				fail();
				return null;
			}

			public ControlSystemEnum getControlSystem() {
				fail();
				return null;
			}

			public String getDevice() {
				fail();
				return null;
			}

			public String getFullName() {
				fail();
				return null;
			}

			public String getProperty() {
				fail();
				return null;
			}

			public String getRawName() {
				fail();
				return null;
			}

			public DalPropertyTypes getTypeHint() {
				fail();
				return null;
			}

			public boolean isCharacteristic() {
				fail();
				return false;
			}

			public RemoteInfo toDalRemoteInfo() {
				fail();
				return null;
			}

			public ValueType getValueTypeHint() {
				fail();
				return null;
			}
		};
	}

}
