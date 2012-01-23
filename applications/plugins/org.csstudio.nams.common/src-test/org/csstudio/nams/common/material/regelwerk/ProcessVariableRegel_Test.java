package org.csstudio.nams.common.material.regelwerk;

import junit.framework.Assert;

import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.common.material.Regelwerkskennung;
import org.csstudio.nams.common.testutils.AbstractTestObject;
import org.csstudio.nams.service.logging.declaration.ILogger;
import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ValueType;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.dal.simple.RemoteInfo;
import org.junit.Test;

public class ProcessVariableRegel_Test extends
		AbstractTestObject<ProcessVariableRegel> {

	private ConnectionServiceMock _connectionServiceMock;

	@Test
	public void testMatchOfDoubleValuesEquals5() throws Throwable {

		final IProcessVariableAddress channelName = this
				.createDefaultPVAdress();
		final Operator operator = Operator.EQUALS;
		final SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.DOUBLE;
		final Object compValue = 5d;

		final ProcessVariableRegel pvRegel = new ProcessVariableRegel(
				this._connectionServiceMock, channelName, operator,
				suggestedProcessVariableType, compValue);

		// Without connection:
		Pruefliste pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),
				pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// Now all with connection...
		this._connectionServiceMock
				.sendNewConnectionState(ConnectionState.CONNECTED);

		// Without any current value:
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// With a not matching value:
		this._connectionServiceMock.sendNewValue(new Double(4.0));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		this._connectionServiceMock.sendNewValue(new Double(5.1));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// With matching value:
		this._connectionServiceMock.sendNewValue(new Double(5.0));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		this._connectionServiceMock.sendNewValue(new Double(5.0000001));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));
	}

	@Test
	public void testMatchOfDoubleValuesSmaller5_compValueAsString()
			throws Throwable {

		final IProcessVariableAddress channelName = this
				.createDefaultPVAdress();
		final Operator operator = Operator.SMALLER;
		final SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.DOUBLE;
		final Object compValue = "5.0";

		final ProcessVariableRegel pvRegel = new ProcessVariableRegel(
				this._connectionServiceMock, channelName, operator,
				suggestedProcessVariableType, compValue);

		// Without connection:
		Pruefliste pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),
				pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// Now all with connection...
		this._connectionServiceMock
				.sendNewConnectionState(ConnectionState.CONNECTED);

		// Without any current value:
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// With a not matching value:
		this._connectionServiceMock.sendNewValue(new Double(6.0));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		this._connectionServiceMock.sendNewValue(new Double(5.0));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// With matching value:
		this._connectionServiceMock.sendNewValue(new Double(4.9));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));
	}

	@Test
	public void testMatchOfDoubleValuesGreaterThan5() throws Throwable {
		final IProcessVariableAddress channelName = this
				.createDefaultPVAdress();
		final Operator operator = Operator.GREATER;
		final SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.DOUBLE;
		final Object compValue = 5d;

		final ProcessVariableRegel pvRegel = new ProcessVariableRegel(
				this._connectionServiceMock, channelName, operator,
				suggestedProcessVariableType, compValue);

		// Without connection:
		Pruefliste pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),
				pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// Now all with connection...
		this._connectionServiceMock
				.sendNewConnectionState(ConnectionState.CONNECTED);

		// Without any current value:
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// With a not matching value:
		this._connectionServiceMock.sendNewValue(new Double(4.0));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		this._connectionServiceMock.sendNewValue(new Double(5.0));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// With matching value:
		this._connectionServiceMock.sendNewValue(new Double(6.1));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));
	}

	@Test
	public void testMatchOfDoubleValuesSmallerThan5() throws Throwable {

		final IProcessVariableAddress channelName = this
				.createDefaultPVAdress();
		final Operator operator = Operator.SMALLER;
		final SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.DOUBLE;
		final Object compValue = 5d;

		final ProcessVariableRegel pvRegel = new ProcessVariableRegel(
				this._connectionServiceMock, channelName, operator,
				suggestedProcessVariableType, compValue);

		// Without connection:
		Pruefliste pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),
				pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// Now all with connection...
		this._connectionServiceMock
				.sendNewConnectionState(ConnectionState.CONNECTED);

		// Without any current value:
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// With a not matching value:
		this._connectionServiceMock.sendNewValue(new Double(6.0));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		this._connectionServiceMock.sendNewValue(new Double(5.0));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// With matching value:
		this._connectionServiceMock.sendNewValue(new Double(4.9));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));
	}

	@Test
	public void testMatchOfDoubleValuesUnequals5() throws Throwable {
		final IProcessVariableAddress channelName = this
				.createDefaultPVAdress();
		final Operator operator = Operator.UNEQUALS;
		final SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.DOUBLE;
		final Object compValue = 5d;

		final ProcessVariableRegel pvRegel = new ProcessVariableRegel(
				this._connectionServiceMock, channelName, operator,
				suggestedProcessVariableType, compValue);

		// Without connection:
		Pruefliste pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),
				pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// Now all with connection...
		this._connectionServiceMock
				.sendNewConnectionState(ConnectionState.CONNECTED);

		// Without any current value:
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// With a not matching value:
		this._connectionServiceMock.sendNewValue(new Double(5.0));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		this._connectionServiceMock.sendNewValue(new Double(5.000001));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// With matching value:
		this._connectionServiceMock.sendNewValue(new Double(4.9));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		this._connectionServiceMock.sendNewValue(new Double(5.1));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));
	}

	@Test
	public void testMatchOfLongValuesEquals5() throws Throwable {
		final IProcessVariableAddress channelName = this
				.createDefaultPVAdress();
		final Operator operator = Operator.EQUALS;
		final SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.LONG;
		final Object compValue = 5l;

		final ProcessVariableRegel pvRegel = new ProcessVariableRegel(
				this._connectionServiceMock, channelName, operator,
				suggestedProcessVariableType, compValue);

		// Without connection:
		Pruefliste pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),
				pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// Now all with connection...
		this._connectionServiceMock
				.sendNewConnectionState(ConnectionState.CONNECTED);

		// Without any current value:
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// With a not matching value:
		this._connectionServiceMock.sendNewValue(new Long(4));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		this._connectionServiceMock.sendNewValue(new Long(6));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		this._connectionServiceMock.sendNewValue(new Long(50));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// With matching value:
		this._connectionServiceMock.sendNewValue(new Long(5));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));
	}

	@Test
	public void testMatchOfLongValuesGreaterThan5() throws Throwable {
		final IProcessVariableAddress channelName = this
				.createDefaultPVAdress();
		final Operator operator = Operator.GREATER;
		final SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.LONG;
		final Object compValue = 5l;

		final ProcessVariableRegel pvRegel = new ProcessVariableRegel(
				this._connectionServiceMock, channelName, operator,
				suggestedProcessVariableType, compValue);

		// Without connection:
		Pruefliste pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),
				pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// Now all with connection...
		this._connectionServiceMock
				.sendNewConnectionState(ConnectionState.CONNECTED);

		// Without any current value:
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// With a not matching value:
		this._connectionServiceMock.sendNewValue(new Long(4));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		this._connectionServiceMock.sendNewValue(new Long(5));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// With matching value:
		this._connectionServiceMock.sendNewValue(new Long(6));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));
	}

	@Test
	public void testMatchOfLongValuesSmallerThan5() throws Throwable {

		final IProcessVariableAddress channelName = this
				.createDefaultPVAdress();
		final Operator operator = Operator.SMALLER;
		final SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.LONG;
		final Object compValue = 5l;

		final ProcessVariableRegel pvRegel = new ProcessVariableRegel(
				this._connectionServiceMock, channelName, operator,
				suggestedProcessVariableType, compValue);

		Pruefliste pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),
				pvRegel);

		// Without connection:
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// Now all with connection...
		this._connectionServiceMock
				.sendNewConnectionState(ConnectionState.CONNECTED);

		// Without any current value:
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test2"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// With a not matching value:
		this._connectionServiceMock.sendNewValue(new Long(6));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test2"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		this._connectionServiceMock.sendNewValue(new Long(5));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test2"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// With matching value:
		this._connectionServiceMock.sendNewValue(new Long(4));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test2"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));
	}

	@Test
	public void testMatchOfLongValuesUnequals5() throws Throwable {
		final IProcessVariableAddress channelName = this
				.createDefaultPVAdress();
		final Operator operator = Operator.UNEQUALS;
		final SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.LONG;
		final Object compValue = 5l;

		final ProcessVariableRegel pvRegel = new ProcessVariableRegel(
				this._connectionServiceMock, channelName, operator,
				suggestedProcessVariableType, compValue);

		// Without connection:
		Pruefliste pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),
				pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// Now all with connection...
		this._connectionServiceMock
				.sendNewConnectionState(ConnectionState.CONNECTED);

		// Without any current value:
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// With a not matching value:
		this._connectionServiceMock.sendNewValue(new Long(5));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// With matching value:
		this._connectionServiceMock.sendNewValue(new Long(4));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		this._connectionServiceMock.sendNewValue(new Long(6));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		this._connectionServiceMock.sendNewValue(new Long(50));
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));
	}

	@Test
	public void testMatchOfStringValueEquals() throws Throwable {
		final IProcessVariableAddress channelName = this
				.createDefaultPVAdress();
		final Operator operator = Operator.EQUALS;
		final SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.STRING;
		final Object compValue = "Foo";

		final ProcessVariableRegel pvRegel = new ProcessVariableRegel(
				this._connectionServiceMock, channelName, operator,
				suggestedProcessVariableType, compValue);

		// Without connection:
		Pruefliste pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),
				pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// Now all with connection...
		this._connectionServiceMock
				.sendNewConnectionState(ConnectionState.CONNECTED);

		// Without any current value:
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// With a not matching value:
		this._connectionServiceMock.sendNewValue("NotFoo");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// With matching value:
		this._connectionServiceMock.sendNewValue("Foo");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));
	}

	@Test
	public void testMatchOfStringValueUnequals() throws Throwable {
		final IProcessVariableAddress channelName = this
				.createDefaultPVAdress();
		final Operator operator = Operator.UNEQUALS;
		final SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.STRING;
		final Object compValue = "Foo";

		final ProcessVariableRegel pvRegel = new ProcessVariableRegel(
				this._connectionServiceMock, channelName, operator,
				suggestedProcessVariableType, compValue);

		// Without connection:
		Pruefliste pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"),
				pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// Now all with connection...
		this._connectionServiceMock
				.sendNewConnectionState(ConnectionState.CONNECTED);

		// Without any current value:
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// With a not matching value:
		this._connectionServiceMock.sendNewValue("Foo");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.NICHT_ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));

		// With matching value:
		this._connectionServiceMock.sendNewValue("NotFoo");
		pListe = new Pruefliste(Regelwerkskennung.valueOf("Test1"), pvRegel);
		pvRegel.pruefeNachrichtErstmalig(new AlarmNachricht("Nachricht"),
				pListe);
		Assert.assertEquals(RegelErgebnis.ZUTREFFEND, pListe
				.gibErgebnisFuerRegel(pvRegel));
	}

	ConnectionServiceMock createPVServiceMock() {
		return new ConnectionServiceMock();
	}

	@Override
	protected ProcessVariableRegel getNewInstanceOfClassUnderTest() {
		final IProcessVariableConnectionService pvService = this
				.createPVServiceMock();
		final IProcessVariableAddress channelName = this
				.createDefaultPVAdress();
		final Operator operator = Operator.EQUALS;
		final SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.STRING;
		final Object compValue = "test";

		return new ProcessVariableRegel(pvService, channelName, operator,
				suggestedProcessVariableType, compValue);
	}

	@Override
	protected Object getNewInstanceOfIncompareableTypeInAccordingToClassUnderTest() {
		return new Object();
	}

	@Override
	protected ProcessVariableRegel[] getThreeDiffrentNewInstanceOfClassUnderTest() {
		final ProcessVariableRegel[] regels = new ProcessVariableRegel[3];
		{
			final IProcessVariableConnectionService pvService = this
					.createPVServiceMock();
			final IProcessVariableAddress channelName = this
					.createDefaultPVAdress();
			final Operator operator = Operator.EQUALS;
			final SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.STRING;
			final Object compValue = "test2";

			regels[0] = new ProcessVariableRegel(pvService, channelName,
					operator, suggestedProcessVariableType, compValue);
		}
		{
			final IProcessVariableConnectionService pvService = this
					.createPVServiceMock();
			final IProcessVariableAddress channelName = this
					.createDefaultPVAdress();
			final Operator operator = Operator.UNEQUALS;
			final SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.STRING;
			final Object compValue = "test";

			regels[1] = new ProcessVariableRegel(pvService, channelName,
					operator, suggestedProcessVariableType, compValue);
		}
		{
			final IProcessVariableConnectionService pvService = this
					.createPVServiceMock();
			final IProcessVariableAddress channelName = this
					.createDefaultPVAdress();
			final Operator operator = Operator.EQUALS;
			final SuggestedProcessVariableType suggestedProcessVariableType = SuggestedProcessVariableType.LONG;
			final Object compValue = 42l;

			regels[2] = new ProcessVariableRegel(pvService, channelName,
					operator, suggestedProcessVariableType, compValue);
		}
		return regels;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this._connectionServiceMock = this.createPVServiceMock();
		ProcessVariableRegel.staticInject(new ILogger() {

			@Override
            public void logDebugMessage(final Object caller,
					final String message) {
			    //
			}

			@Override
            public void logDebugMessage(final Object caller,
					final String message, final Throwable throwable) {
			    //
			}

			@Override
            public void logErrorMessage(final Object caller,
					final String message) {
			    //
			}

			@Override
            public void logErrorMessage(final Object caller,
					final String message, final Throwable throwable) {
			    //
			}

			@Override
            public void logFatalMessage(final Object caller,
					final String message) {
			    //
			}

			@Override
            public void logFatalMessage(final Object caller,
					final String message, final Throwable throwable) {
			    //
			}

			@Override
            public void logInfoMessage(final Object caller, final String message) {
			    //
			}

			@Override
            public void logInfoMessage(final Object caller,
					final String message, final Throwable throwable) {
			    //
			}

			@Override
            public void logWarningMessage(final Object caller,
					final String message) {
			    //
			}

			@Override
            public void logWarningMessage(final Object caller,
					final String message, final Throwable throwable) {
			    //
			}
		});
	}

	private IProcessVariableAddress createDefaultPVAdress() {
		return new IProcessVariableAddress() {

			@Override
            public String getCharacteristic() {
				Assert.fail();
				return null;
			}

			@Override
            public ControlSystemEnum getControlSystem() {
				Assert.fail();
				return null;
			}

			@Override
            public String getDevice() {
				Assert.fail();
				return null;
			}

			@Override
            public String getFullName() {
				Assert.fail();
				return null;
			}

			@Override
            public String getProperty() {
				Assert.fail();
				return null;
			}

			@Override
            public String getRawName() {
				Assert.fail();
				return null;
			}

			@Override
            public ValueType getValueTypeHint() {
				Assert.fail();
				return null;
			}

			@Override
            public boolean isCharacteristic() {
				Assert.fail();
				return false;
			}

			@Override
            public RemoteInfo toDalRemoteInfo() {
				Assert.fail();
				return null;
			}

			@Override
            public IProcessVariableAddress deriveNoCharacteristicPart() {
				Assert.fail();
				return null;
			}

			@Override
            public IProcessVariableAddress deriveCharacteristic(
					String characteristic) {
				Assert.fail();
				return null;
			}
		};
	}

}
