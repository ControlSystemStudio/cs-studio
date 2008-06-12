/**
 * 
 */
package org.csstudio.nams.common.material.regelwerk;

import java.math.BigDecimal;

import org.csstudio.nams.common.fachwert.Millisekunden;
import org.csstudio.nams.common.material.AlarmNachricht;
import org.csstudio.nams.service.logging.declaration.Logger;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;

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
			_lastReceivedValue = null;
			_isConnected = false;
		}

		/**
		 * {@inheritDoc}
		 */
		public void connectionStateChanged(ConnectionState connectionState) {
			logger.logDebugMessage(this,
					"ConnectionState changed, new state: " + connectionState);
			if (ConnectionState.CONNECTED.equals(connectionState)) {
				_isConnected = true;
			} else {
				_isConnected = false;
			}
		}

		/**
		 * Returns the last received value.
		 * 
		 * @return Last received value, may be null.
		 */
		public T currentValue() {
			return _lastReceivedValue;
		}

		/**
		 * Determines if last state is the connected state.
		 */
		public boolean isConnected() {
			return _isConnected;
		}

		/**
		 * {@inheritDoc}
		 */
		public void valueChanged(T value) {
			logger.logDebugMessage(this,
					"Value changed, new Value: " + value.toString());
			_lastReceivedValue = value;
		}

		public void errorOccured(String error) {

		}
	}

	private ProcessVariableChangeListener<?> _processVariableChangeListener;
	private final Operator operator;
	private final SuggestedProcessVariableType suggestedProcessVariableType;
	private final Object compValue;
	private final IProcessVariableAddress channelName;
	private final IProcessVariableConnectionService pvService;
	private static Logger logger;

	public ProcessVariableRegel(IProcessVariableConnectionService pvService,
			IProcessVariableAddress channelName, Operator operator,
			SuggestedProcessVariableType suggestedProcessVariableType,
			Object compValue) {
		this.pvService = pvService;
		this.channelName = channelName;
		this.operator = operator;
		this.suggestedProcessVariableType = suggestedProcessVariableType;
		this.compValue = compValue;
		if (SuggestedProcessVariableType.LONG
				.equals(suggestedProcessVariableType)) {
			ProcessVariableChangeListener<Long> intListener = new ProcessVariableChangeListener<Long>();
			pvService.registerForLongValues(intListener, channelName);
			_processVariableChangeListener = intListener;
		} else if (SuggestedProcessVariableType.DOUBLE
				.equals(suggestedProcessVariableType)) {
			ProcessVariableChangeListener<Double> doubleListener = new ProcessVariableChangeListener<Double>();
			pvService.registerForDoubleValues(doubleListener, channelName);
			_processVariableChangeListener = doubleListener;
		} else if (SuggestedProcessVariableType.STRING
				.equals(suggestedProcessVariableType)) {
			ProcessVariableChangeListener<String> stringListener = new ProcessVariableChangeListener<String>();
			pvService.registerForStringValues(stringListener, channelName);
			_processVariableChangeListener = stringListener;
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
	public void pruefeNachrichtAufBestaetigungsUndAufhebungsNachricht(
			AlarmNachricht nachricht, Pruefliste bisherigesErgebnis) {
		// evaluation is done on first processing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.c1wps.desy.ams.allgemeines.regelwerk.VersandRegel#pruefeNachrichtAufTimeOuts(de.c1wps.desy.ams.allgemeines.regelwerk.Pruefliste,
	 *      de.c1wps.desy.ams.allgemeines.Millisekunden)
	 */
	public Millisekunden pruefeNachrichtAufTimeOuts(
			Pruefliste bisherigesErgebnis,
			Millisekunden verstricheneZeitSeitErsterPruefung) {
		// timeouts are irrelevant
		return Millisekunden.valueOf(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.c1wps.desy.ams.allgemeines.regelwerk.VersandRegel#pruefeNachrichtErstmalig(de.c1wps.desy.ams.allgemeines.AlarmNachricht,
	 *      de.c1wps.desy.ams.allgemeines.regelwerk.Pruefliste)
	 */
	public Millisekunden pruefeNachrichtErstmalig(AlarmNachricht nachricht,
			Pruefliste ergebnisListe) {
		if (_processVariableChangeListener.isConnected()) {
			Object currentValue = _processVariableChangeListener.currentValue();
			if (currentValue != null
					&& suggestedProcessVariableType.getSuggestedTypeClass()
							.isAssignableFrom(currentValue.getClass())) {
				logger.logDebugMessage(
						this,
						"Current value from PV: " + currentValue
								+ " will be compared to: " + compValue);

				// equals: the default result that will be used if the
				// currentValue is not an instance of Number. This means that
				// string-based comparison (equals/unequals) also works, but
				// support for additional string comparison operators would
				// have to be added explicitly.
				boolean equals = compValue.equals(currentValue);

				if (operator.equals(Operator.EQUALS)) {
					if (currentValue instanceof Number) {
						BigDecimal compareValueAsBigDecimal = new BigDecimal(
								compValue.toString());
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
				} else if (operator.equals(Operator.UNEQUALS)) {
					if (currentValue instanceof Number) {
						BigDecimal compareValueAsBigDecimal = new BigDecimal(
								compValue.toString());
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
				} else if (operator.equals(Operator.SMALLER)) {
					if (currentValue instanceof Number) {
						Number currentValueAsNumber = (Number) currentValue;
						double currentValueAsDouble = currentValueAsNumber
								.doubleValue();

						double compValueAsDouble = ((Number) compValue)
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
				} else if (operator.equals(Operator.GREATER)) {
					if (currentValue instanceof Number) {
						Number currentValueAsNumber = (Number) currentValue;
						double currentValueAsDouble = currentValueAsNumber
								.doubleValue();

						double compValueAsDouble = ((Number) compValue)
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
			}
		} else {
			logger.logErrorMessage(this,
					"No connection to PV (via DAL), Channel: " + channelName);
		}
		ergebnisListe.setzeErgebnisFuerRegelFallsVeraendert(this,
				RegelErgebnis.ZUTREFFEND);
		return Millisekunden.valueOf(0);
	}
	
	public static void staticInject(
			org.csstudio.nams.service.logging.declaration.Logger logger) {
		ProcessVariableRegel.logger = logger;
		
	}

	public static Logger getLogger() {
		return logger;
	}
	
}
