
/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.ams.filter;

import java.math.BigDecimal;
import java.sql.Connection;
import javax.jms.MapMessage;
import org.csstudio.ams.AMSException;
import org.csstudio.ams.Log;
import org.csstudio.ams.dbAccess.HoldsAnDatabaseId;
import org.csstudio.ams.dbAccess.configdb.FilterConditionProcessVariableDAO;
import org.csstudio.ams.dbAccess.configdb.FilterConditionProcessVariableTObject;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.model.pvs.ValueType;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.csstudio.dal.Timestamp;

/**
 * A filter condition based on PV-values.
 * 
 * This filter takes a DAL-channel-address to access a specific value to be
 * compared to an given value.
 * 
 * @author C1 WPS / KM, MZ
 */
public strictfp class FilterConditionProcessVariable implements
		IFilterCondition {

	/**
	 * Operators supported by this filter-condition.
	 */
	static public enum Operator implements HoldsAnDatabaseId {
		/**
		 * The given Value is equal to the value on the channel.
		 */
		EQUALS((short) 1),

		/**
		 * The given Value is not equal to the value on the channel.
		 */
		UNEQUALS((short) 2),

		/**
		 * The value on the channel is smaller than the given value.
		 */
		SMALLER((short) 3),

		/**
		 * The value on the channel is greater than the given value.
		 */
		GREATER((short) 4);

		/**
		 * Returns the Operator for given database-id or {@code null} if id is
		 * unknown.
		 */
		public static Operator findOperatorOfDBId(short id) {
			for (Operator op : Operator.values()) {
				if (op._dbid == id) {
					return op;
				}
			}

			return null;
		}

		/**
		 * The database-id.
		 */
		private short _dbid;

		/**
		 * Creates a value-representation with given database-id.
		 * 
		 * @param id
		 *            The id in database:
		 */
		Operator(short id) {
			_dbid = id;
		}

		/**
		 * Returns the database-id of this Operator.
		 * 
		 * <strong>Pay attention:</strong> Please do never use this method
		 * outside the DAOs!
		 */
		public short asDatabaseId() {
			return _dbid;
		}
	}

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
			Log.log(this, Log.DEBUG, "ConnectionState changed, new state: " + connectionState);
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
		public void valueChanged(T value, Timestamp timestamp) {
			Log.log(this, Log.DEBUG, "Value changed, new Value: " + value.toString());
			_lastReceivedValue = value;
		}

		public void errorOccured(String error) {
			
		}

        public void valueChanged(T value) {
            
        }
	}

	/**
	 * Expected type of channel.
	 */
	static public enum SuggestedProcessVariableType implements
			HoldsAnDatabaseId {
		
		STRING((short) 1, String.class, new Operator[] { Operator.EQUALS,
				Operator.UNEQUALS }, new Parser<String>() {
			public String parse(String dbString) {
				return dbString;
			}

			public String toDbString(Object value) {
				return (String) value;
			}
		}),
		
		DOUBLE((short) 3, Double.class, Operator.values(),
				new Parser<Double>() {
			public Double parse(String dbString) {
			    
	             // TODO: If the string does not contain a long value, the method Long.valueOf()
                //       throws a NumberFormatException. That causes an endless loop in the
                //       AmsDistributor
                Double result = null;
                
                try {
                    result = Double.valueOf(dbString);
                } catch(NumberFormatException nfe) {
                    
                    Log.log(this, Log.WARN, "[*** NumberFormatException ***]: " + nfe.getMessage());
                    result = NumberValidator.getCleanDouble(dbString);
                    Log.log(this, Log.WARN, "Extract from string '" + dbString + "' the value " + result);
                    if(result == null) {
                        
                        // TODO: Sinnvoller Standardwert?
                        result = new Double(0);
                        Log.log(this, Log.WARN, "Cannot get a valid number from string '" + dbString + "'. Using default value " + result);
                    }
                }
                
                return result;
			}

			public String toDbString(Object value) {
				Double d = (Double) value;
				return Double.toString(d.doubleValue());
			}
		}),

		LONG((short) 2, Long.class, Operator.values(),
				new Parser<Long>() {
			public Long parse(String dbString) {
			    
			    // TODO: If the string does not contain a long value, the method Long.valueOf()
			    //       throws a NumberFormatException. That causes an endless loop in the
			    //       AmsDistributor
			    Long result = null;
			    
			    try {
			        result = Long.valueOf(dbString);
			    } catch(NumberFormatException nfe) {
			        
			        Log.log(this, Log.WARN, "[*** NumberFormatException ***]: " + nfe.getMessage());
			        result = NumberValidator.getCleanLong(dbString);
			        Log.log(this, Log.WARN, "Extract from string '" + dbString + "' the value " + result);
			        if(result == null) {
			            
			            // TODO: Sinnvoller Standardwert?
			            result = new Long(0);
			            Log.log(this, Log.WARN, "Cannot get a valid number from string '" + dbString + "'. Using default value " + result);
			        }
			    }
			    
				return result;
			}

			public String toDbString(Object value) {
				Long l = (Long) value;
				return Long.toString(l.intValue());
			}
		});

		static private interface Parser<T> {
			public T parse(String stringValue);

			public String toDbString(Object value);
		}

		/**
		 * Returns the Operator for given database-id or {@code null} if id is
		 * unknown.
		 */
		public static SuggestedProcessVariableType findOperatorOfDBId(short id) {
			for (SuggestedProcessVariableType spvt : SuggestedProcessVariableType
					.values()) {
				if (spvt._dbid == id) {
					return spvt;
				}
			}

			return null;
		}

		/**
		 * The database-id.
		 */
		private short _dbid;
		private Class<?> _suggestedTypeClass;

		private Parser<?> _parser;
		
		/**
		 * The comparison operators supported by this type.
		 */
		private Operator[] _supportedOperators;

		/**
		 * Creates a value-representation with given database-id.
		 * 
		 * @param id
		 *            The id in database:
		 */
		SuggestedProcessVariableType(short id, Class<?> suggestedTypeClass,
				Operator[] supportedOperators, Parser<?> parser) {
			_dbid = id;
			_suggestedTypeClass = suggestedTypeClass;
			_supportedOperators = supportedOperators;
			_parser = parser;
		}
		
		/**
		 * Returns the comparison operators supported by this type.
		 * @return an array of the comparison operators supported by this type.
		 */
		public Operator[] getSupportedOperators() {
			// return a copy of the array, so the caller cannot modify our
			// internal one
			Operator[] ops = new Operator[_supportedOperators.length];
			System.arraycopy(_supportedOperators, 0, ops, 0, ops.length);
			return ops;
		}

		/**
		 * Returns the database-id of this SuggestedProcessVariableType-Value.
		 * 
		 * <strong>Pay attention:</strong> Please do never use this method
		 * outside the DAOs or UI combos!
		 */
		public short asDatabaseId() {
			return _dbid;
		}

		/**
		 * The Java-class of this type.
		 */
		public Class<?> getSuggestedTypeClass() {
			return _suggestedTypeClass;
		}

		/**
		 * Checks if given value is parsable depending on the type of this
		 * valus.
		 * 
		 * @param value
		 *            The value to be checked.
		 * @return {@code true} if the value is parsable, {@code false}
		 *         otherwise.
		 */
		public boolean isParsableValue(String value) {
			try {
				this.parseDatabaseValue(value);
			} catch (Exception ex) {
				return false;
			}
			return true;
		}

		@SuppressWarnings("unchecked")
		public <T> T parseDatabaseValue(String dbString) {
			return (T) this._suggestedTypeClass.cast(this._parser
					.parse(dbString));
		}

		/**
		 * Formats the given value as DB-representation depending on the
		 * suggested type class.
		 * 
		 * @require this.getSuggestedTypeClass().isAssignableFrom(value.getClass())
		 */
		public String toDbString(Object value) {
			assert this.getSuggestedTypeClass().isAssignableFrom(
					value.getClass()) : "Precondition violated: this.getSuggestedTypeClass().isAssignableFrom(value.getClass())";

			return _parser.toDbString(this.getSuggestedTypeClass().cast(value));
		}
	}

	private FilterConditionProcessVariableTObject filterConditionConfiguration;

	private ProcessVariableChangeListener<?> _processVariableChangeListener;

	/**
	 * Timeout to wait for a connection state.
	 * 
	 * TODO Make this configurable by a preference page.
	 */
	private static final int TIMEOUT_OF_CONNECTION_ATTEMP = 5;

	/**
	 * Called by {@link #init(Connection, int, int)}, introduced to makes this
	 * filter partly testable (remark: DAL must run on given machine and tested
	 * channel must be able to access to run a test for this method).
	 * 
	 * Initialize the filter. Will be called before any call to match.
	 * 
	 * @param configuration
	 *            configuration of this filter.
	 * @param iFilterConditionID
	 *            ID of this filter condition instance.
	 * @param filterID
	 *            ID of filter this filter condition is currently used in.
	 * @throws Exception
	 *             If an initialize error occours.
	 * @require configuration != null
	 * @require iFilterConditionID > -1
	 * @require filterID > -1
	 * @require pva != null
	 */
	public void doInit(
			final FilterConditionProcessVariableTObject configuration,
			final int filterConditionID, final int filterID,
			IProcessVariableConnectionService pvcService,
			IProcessVariableAddress pva) throws Exception {
		filterConditionConfiguration = configuration;
		SuggestedProcessVariableType suggestedtype = filterConditionConfiguration
				.getSuggestedType();

		if (SuggestedProcessVariableType.LONG.equals(suggestedtype)) {
			ProcessVariableChangeListener<Long> intListener = new ProcessVariableChangeListener<Long>();
			pvcService.register(intListener, pva, ValueType.LONG);
			_processVariableChangeListener = intListener;
		} else if (SuggestedProcessVariableType.DOUBLE.equals(suggestedtype)) {
			ProcessVariableChangeListener<Double> doubleListener = new ProcessVariableChangeListener<Double>();
			pvcService.register(doubleListener, pva, ValueType.DOUBLE);
			_processVariableChangeListener = doubleListener;
		} else if (SuggestedProcessVariableType.STRING.equals(suggestedtype)) {
			ProcessVariableChangeListener<String> stringListener = new ProcessVariableChangeListener<String>();
			pvcService.register(stringListener, pva, ValueType.STRING);
			_processVariableChangeListener = stringListener;
		} else {
			throw new RuntimeException("Unknown suggested type: "
					+ suggestedtype.toString());
		}

		int timer = 0;
		while (!_processVariableChangeListener.isConnected()) {
			Thread.yield();
			if (timer == TIMEOUT_OF_CONNECTION_ATTEMP) {
				Log.log(this, Log.ERROR, "Connection can not be initialized during configuration timeout.");
				break;
			}
			Thread.sleep(1000);
			timer++;
		}
	}

	/**
	 * Loads the filter configuration from the database using
	 * {@link FilterConditionProcessVariableDAO}.
	 * 
	 * {@inheritDoc}
	 */
	public final void init(final Connection conDb, final int filterConditionID,
			final int filterID) throws AMSException {
		try {
			FilterConditionProcessVariableTObject configuration = FilterConditionProcessVariableDAO
					.select(conDb, filterConditionID);
			if (configuration == null)
				throw new AMSException(
						"FilterConditionProcessVariable.filterConditionID="
								+ filterConditionID + " not found.");
			String processVariableChannelName = configuration
					.getProcessVariableChannelName();
			IProcessVariableAddress variableAdress = ProcessVariableAdressFactory
					.getInstance().createProcessVariableAdress(
							processVariableChannelName);
			this.doInit(configuration, filterConditionID, filterID,
					ProcessVariableConnectionServiceFactory.getDefault()
							.getProcessVariableConnectionService(),
					variableAdress);
		} catch (Exception e) {
			Log.log(this, Log.FATAL, "FilterConditionProcessVariable#init failed!", e);
			throw new AMSException(
					"FilterConditionProcessVariable#init failed!", e);
		}
	}

	/**
	 * Checks if the acutely resulting value of configured DAL-channel matches
	 * configured limit using configured comparison-method. The message stayed
	 * unused.
	 * 
	 * {@inheritDoc}
	 */
	public boolean match(final MapMessage map) {
		if (_processVariableChangeListener.isConnected()) {
			Operator operator = filterConditionConfiguration.getOperator();
			SuggestedProcessVariableType suggestedtype = filterConditionConfiguration
					.getSuggestedType();
			Object compValue = filterConditionConfiguration.getCompValue();
			Object currentValue = _processVariableChangeListener.currentValue();
			if (currentValue != null
					&& suggestedtype.getSuggestedTypeClass().isAssignableFrom(
							currentValue.getClass())) {
				
				Log.log(this, Log.DEBUG, "Current value from PV: " + currentValue
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
						 * because of the behavior of trailing zeros of
						 * database types is not predictable
						 */
						currentValueAsBigDecimal = currentValueAsBigDecimal
								.setScale(compareValueAsBigDecimal.scale(),
										BigDecimal.ROUND_HALF_UP);

						return compareValueAsBigDecimal
								.equals(currentValueAsBigDecimal);
					}
					return equals;
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

						return !compareValueAsBigDecimal
								.equals(currentValueAsBigDecimal);
					}
					return !equals;
				} else if (operator.equals(Operator.SMALLER)) {
					if (currentValue instanceof Number) {
						Number currentValueAsNumber = (Number) currentValue;
						double currentValueAsDouble = currentValueAsNumber
								.doubleValue();

						double compValueAsDouble = ((Number) compValue)
								.doubleValue();

						return currentValueAsDouble < compValueAsDouble;
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

						boolean b = currentValueAsDouble > compValueAsDouble;
						return b;
					} else {
						throw new RuntimeException("illegal type: "
								+ currentValue.getClass().getSimpleName());
					}
				}
			}
		} else {
			Log.log(this, Log.ERROR, "No connection to PV (via DAL), Channel: "
							+ filterConditionConfiguration
									.getProcessVariableChannelName());
		}
		return true;
	}

}
