
/**
 * 
 */

package org.csstudio.nams.common.material.regelwerk;

import java.math.BigDecimal;

import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.service.logging.declaration.ILogger;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ValueType;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.dal.Timestamp;

/**
 * @author Goesta Steen, TR, MW
 * 
 */
public class ProcessVariableRegel implements VersandRegel {

	/**
	 * Listener to be informed on PV changes.
	 * 
	 * @author C1 WPS / KM, MZ
	 * 
	 * @param <T>
	 *            Type of expected values.
	 */
	private static class ProcessVariableChangeListener<T> implements
			IProcessVariableValueListener<T> {
		/**
		 * The last received value, set on received value changes.
		 */
		private volatile T _lastReceivedValue;

		/**
		 * Marker if PV of this listener is connected, set by changes of
		 * connection state.
		 */
		private volatile boolean _isConnected;

		/**
		 * Creates a new instance of this listener.
		 */
		public ProcessVariableChangeListener() {
			this._lastReceivedValue = null;
			this._isConnected = false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
        public void connectionStateChanged(final ConnectionState connectionState) {
			ProcessVariableRegel.logger.logDebugMessage(this,
					"ConnectionState changed, new state: " + connectionState);
			if (ConnectionState.CONNECTED.equals(connectionState)) {
				this._isConnected = true;
			} else {
				this._isConnected = false;
			}
		}

		/**
		 * Returns the last received value.
		 * 
		 * @return Last received value, may be null.
		 */
		public T currentValue() {
			return this._lastReceivedValue;
		}

		@Override
        public void errorOccured(final String error) {
			ProcessVariableRegel.logger.logWarningMessage(this, "Error reported by simple DAL : " + error);
		}

		/**
		 * Determines if last state is the connected state.
		 */
		public boolean isConnected() {
			return this._isConnected;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
        public void valueChanged(final T value, final Timestamp timestamp) {
		    String tempValue = null;
		    String tempTimestamp = null;
		    tempValue = (value != null) ? value.toString() : "null";
		    tempTimestamp = (timestamp != null) ? timestamp.toString() : "null";
		    ProcessVariableRegel.logger.logDebugMessage(this, tempTimestamp
					+ " Value changed, new Value: " + tempValue);
			this._lastReceivedValue = value;
		}
	}

	private static ILogger logger;

	public static ILogger getLogger() {
		return ProcessVariableRegel.logger;
	}

	public static void staticInject(
			final org.csstudio.nams.service.logging.declaration.ILogger logger) {
		ProcessVariableRegel.logger = logger;

	}

	private ProcessVariableChangeListener<?> _processVariableChangeListener;
	private final Operator operator;
	private final SuggestedProcessVariableType suggestedProcessVariableType;

	private final Object compValue;

	private final IProcessVariableAddress channelName;

	public ProcessVariableRegel(
			final IProcessVariableConnectionService pvService,
			final IProcessVariableAddress channelName, final Operator operator,
			final SuggestedProcessVariableType suggestedProcessVariableType,
			final Object compValue) {
		this.channelName = channelName;
		this.operator = operator;
		this.suggestedProcessVariableType = suggestedProcessVariableType;
		
		// FIXME mz 2008-11-12: The param comp value must be translated to fit the param SuggestedProcessVariableType.
		// This will be a fast fix for the bug reported by MM on 11.11.08. A better solution would be different constructors.
		//this.compValue = compValue;
		// FIXME-END.
		
		if (SuggestedProcessVariableType.LONG
				.equals(suggestedProcessVariableType)) {
			final ProcessVariableChangeListener<Long> intListener = new ProcessVariableChangeListener<Long>();
			pvService.register(intListener, channelName, ValueType.LONG);
			this._processVariableChangeListener = intListener;
			this.compValue = Long.valueOf(compValue.toString());
		} else if (SuggestedProcessVariableType.DOUBLE
				.equals(suggestedProcessVariableType)) {
			final ProcessVariableChangeListener<Double> doubleListener = new ProcessVariableChangeListener<Double>();
			pvService.register(doubleListener, channelName, ValueType.DOUBLE);
			this._processVariableChangeListener = doubleListener;
			this.compValue = Double.valueOf(compValue.toString());
		} else if (SuggestedProcessVariableType.STRING
				.equals(suggestedProcessVariableType)) {
			final ProcessVariableChangeListener<String> stringListener = new ProcessVariableChangeListener<String>();
			pvService.register(stringListener, channelName, ValueType.STRING);
			this._processVariableChangeListener = stringListener;
			this.compValue = compValue.toString();
		} else {
			throw new RuntimeException("Unknown suggested type: "
					+ suggestedProcessVariableType.toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.c1wps.desy.ams.allgemeines.regelwerk.VersandRegel#pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(de.c1wps.desy.ams.allgemeines.AlarmNachricht,
	 *      de.c1wps.desy.ams.allgemeines.regelwerk.Pruefliste)
	 */
	@Override
    public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
			final AlarmNachricht nachricht, final Pruefliste bisherigesErgebnis) {
		// evaluation is done on first processing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.c1wps.desy.ams.allgemeines.regelwerk.VersandRegel#pruefeNachrichtAufTimeOuts(de.c1wps.desy.ams.allgemeines.regelwerk.Pruefliste,
	 *      de.c1wps.desy.ams.allgemeines.Millisekunden)
	 */
	@Override
    public Millisekunden pruefeNachrichtAufTimeOuts(
			final Pruefliste bisherigesErgebnis,
			final Millisekunden verstricheneZeitSeitErsterPruefung) {
		// timeouts are irrelevant
		return Millisekunden.valueOf(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.c1wps.desy.ams.allgemeines.regelwerk.VersandRegel#pruefeNachrichtErstmalig(de.c1wps.desy.ams.allgemeines.AlarmNachricht,
	 *      de.c1wps.desy.ams.allgemeines.regelwerk.Pruefliste)
	 */
	@Override
    public Millisekunden pruefeNachrichtErstmalig(
			final AlarmNachricht nachricht, final Pruefliste ergebnisListe) {
		if (this._processVariableChangeListener.isConnected()) {
			final Object currentValue = this._processVariableChangeListener
					.currentValue();
			if ((currentValue != null)
					&& this.suggestedProcessVariableType
							.getSuggestedTypeClass().isAssignableFrom(
									currentValue.getClass())) {
				ProcessVariableRegel.logger.logDebugMessage(this,
						"Current value from PV: " + currentValue
								+ " will be compared to: " + this.compValue);

				// equals: the default result that will be used if the
				// currentValue is not an instance of Number. This means that
				// string-based comparison (equals/unequals) also works, but
				// support for additional string comparison operators would
				// have to be added explicitly.
				boolean equals = this.compValue.equals(currentValue);

				if (this.operator.equals(Operator.EQUALS)) {
					if (currentValue instanceof Number) {
						final BigDecimal compareValueAsBigDecimal = new BigDecimal(
								this.compValue.toString());
						BigDecimal currentValueAsBigDecimal = new BigDecimal(
								currentValue.toString());

						/*
						 * The current values scale is set to the scale of the
						 * compare value by rounding half up Known issues: It's
						 * not possible to get a higher precision by adding
						 * zeros behind the point The used double value doesn't
						 * support more then one trailing zeros
						 * 
						 * Proposed solution: Introduce a new integer object
						 * into the configuration TObject and database which
						 * holds the scale entered in the UI. This is needed
						 * because of the behavior of trailing zeros of database
						 * types is not predictable
						 */
						currentValueAsBigDecimal = currentValueAsBigDecimal
								.setScale(compareValueAsBigDecimal.scale(),
										BigDecimal.ROUND_HALF_UP);

						if (compareValueAsBigDecimal
								.equals(currentValueAsBigDecimal)) {
							ergebnisListe
									.setzeErgebnisFuerRegelFallsVeraendert(
											this, RegelErgebnis.ZUTREFFEND);
						} else {
							ergebnisListe
									.setzeErgebnisFuerRegelFallsVeraendert(
											this,
											RegelErgebnis.NICHT_ZUTREFFEND);
						}
						return Millisekunden.valueOf(0);
					}
					if (equals) {
						ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(
								this, RegelErgebnis.ZUTREFFEND);
					} else {
						ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(
								this, RegelErgebnis.NICHT_ZUTREFFEND);
					}
					return Millisekunden.valueOf(0);
				} else if (this.operator.equals(Operator.UNEQUALS)) {
					if (currentValue instanceof Number) {
						final BigDecimal compareValueAsBigDecimal = new BigDecimal(
								this.compValue.toString());
						BigDecimal currentValueAsBigDecimal = new BigDecimal(
								currentValue.toString());

						/*
						 * see note above (case equals)!
						 */
						currentValueAsBigDecimal = currentValueAsBigDecimal
								.setScale(compareValueAsBigDecimal.scale(),
										BigDecimal.ROUND_HALF_UP);
						if (!compareValueAsBigDecimal
								.equals(currentValueAsBigDecimal)) {
							ergebnisListe
									.setzeErgebnisFuerRegelFallsVeraendert(
											this, RegelErgebnis.ZUTREFFEND);
						} else {
							ergebnisListe
									.setzeErgebnisFuerRegelFallsVeraendert(
											this,
											RegelErgebnis.NICHT_ZUTREFFEND);
						}
						return Millisekunden.valueOf(0);
					}
					if (!equals) {
						ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(
								this, RegelErgebnis.ZUTREFFEND);
					} else {
						ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(
								this, RegelErgebnis.NICHT_ZUTREFFEND);
					}
					return Millisekunden.valueOf(0);
				} else if (this.operator.equals(Operator.SMALLER)) {
					if (currentValue instanceof Number) {
						final Number currentValueAsNumber = (Number) currentValue;
						final double currentValueAsDouble = currentValueAsNumber
								.doubleValue();

						final double compValueAsDouble = ((Number) this.compValue)
								.doubleValue();

						if (currentValueAsDouble < compValueAsDouble) {
							ergebnisListe
									.setzeErgebnisFuerRegelFallsVeraendert(
											this, RegelErgebnis.ZUTREFFEND);
						} else {
							ergebnisListe
									.setzeErgebnisFuerRegelFallsVeraendert(
											this,
											RegelErgebnis.NICHT_ZUTREFFEND);
						}
						return Millisekunden.valueOf(0);
					} else {
						throw new RuntimeException("illegal type: "
								+ currentValue.getClass().getSimpleName());
					}
				} else if (this.operator.equals(Operator.GREATER)) {
					if (currentValue instanceof Number) {
						final Number currentValueAsNumber = (Number) currentValue;
						final double currentValueAsDouble = currentValueAsNumber
								.doubleValue();
						
						// BUG:
						// Hier gibt es eine ClassCastException
						// compValue ist vom Typ String
						final double compValueAsDouble = ((Number) this.compValue)
								.doubleValue();

						if (currentValueAsDouble > compValueAsDouble) {
							ergebnisListe
									.setzeErgebnisFuerRegelFallsVeraendert(
											this, RegelErgebnis.ZUTREFFEND);
						} else {
							ergebnisListe
									.setzeErgebnisFuerRegelFallsVeraendert(
											this,
											RegelErgebnis.NICHT_ZUTREFFEND);
						}
						return Millisekunden.valueOf(0);
					} else {
						throw new RuntimeException("illegal type: "
								+ currentValue.getClass().getSimpleName());
					}
				}
			} else {
				if (currentValue == null) {
					// es besteht zwar eine Verbindung aber es steht kein Wert
					// zur Verfügung:
					// Sicherheitsverhalten: Nachricht senden:
					ProcessVariableRegel.logger
							.logWarningMessage(
									this,
									"No value recieved from connected PV Service to PV (via DAL), Channel: "
											+ this.channelName
											+ ". Message will be accepted as fail-over behavior.");
					ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(this,
							RegelErgebnis.ZUTREFFEND);
				}
			}
		} else {
			ProcessVariableRegel.logger.logErrorMessage(this,
					"No connection to PV (via DAL), Channel: "
							+ this.channelName);
			ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(this,
					RegelErgebnis.ZUTREFFEND);
		}
		return Millisekunden.valueOf(0);
	}

	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder("(PVRegel: ");
		stringBuilder.append("Channelname: ");
		stringBuilder.append(this.channelName);
		stringBuilder.append(" Operator: ");
		stringBuilder.append(this.operator);
		stringBuilder.append(" compValue: ");
		stringBuilder.append(this.compValue);
		stringBuilder.append(")");
		return stringBuilder.toString();
	}

}
