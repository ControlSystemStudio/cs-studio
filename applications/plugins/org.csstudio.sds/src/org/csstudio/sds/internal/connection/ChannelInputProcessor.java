package org.csstudio.sds.internal.connection;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.csstudio.platform.ExecutorAccess;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.sds.internal.model.logic.RuleEngine;
import org.csstudio.sds.internal.statistics.MeasureCategoriesEnum;
import org.csstudio.sds.internal.statistics.TimeTrackedRunnable;
import org.csstudio.sds.model.WidgetProperty;
import org.epics.css.dal.DynamicValueState;
import org.epics.css.dal.context.ConnectionState;

/**
 * A channel input process encapsulates the logical rules that have to be
 * applied, when a dynamic value change occurs.
 * 
 * Dynamic value changes are delegated to these processor. Afterwards the
 * processor processes its rules to transform the received value and then it
 * forwards the transformed value to a widget property.
 * 
 * @author Sven Wende
 * 
 */
public final class ChannelInputProcessor {
	
	/**
	 * The channel, which is managed by this processor.
	 */
	private ChannelReference _reference;

	/**
	 * A rule engine.
	 */
	private RuleEngine _ruleEngine;

	/**
	 * The managed widget property.
	 */
	private WidgetProperty _widgetProperty;

	/**
	 * Property values, which are applied for certain connection states.
	 */
	private Map<ConnectionState, Object> _connectionStatePropertyValues;

	/**
	 * Property values, which are applied for certain condition states.
	 */
	private Map<DynamicValueState, Object> _conditionStatePropertyValues;

	/**
	 * Constructor.
	 * 
	 * @param reference
	 *            a channel reference
	 * @param ruleEngine
	 *            a rule engine
	 * @param widgetProperty
	 *            a widget property
	 * @param connectionStatePropertyValues
	 *            property values, which are applied for certain connection
	 *            states
	 * @param conditionStatePropertyValues
	 *            Property values, which are applied for certain condition
	 *            states
	 */
	public ChannelInputProcessor(
			final ChannelReference reference,
			final RuleEngine ruleEngine,
			final WidgetProperty widgetProperty,
			final Map<ConnectionState, Object> connectionStatePropertyValues,
			final Map<DynamicValueState, Object> conditionStatePropertyValues) {
		assert reference != null;
		assert ruleEngine != null;
		assert widgetProperty != null;
		_reference = reference;
		_ruleEngine = ruleEngine;
		_widgetProperty = widgetProperty;
		_connectionStatePropertyValues = connectionStatePropertyValues;
		_conditionStatePropertyValues = conditionStatePropertyValues;
	}

	/**
	 * This method is called by connectors (see {@link Connector}), when a
	 * dynamic value has changed.
	 * 
	 * @param newValue
	 *            the new value
	 */
	public void valueChanged(final Object newValue) {
		// wenn als Value null zurückgegeben wird, dann bedeutet dies mitunter,  dass kein Wert gesetzt werden darf
		Object value = _ruleEngine.processRule(_reference, newValue);
		if (value != null) {
			asyncApplyValueToProperty(value);
		}
	}

	/**
	 * This method is called by connectors (see {@link Connector}), when the
	 * connection state changes.
	 * 
	 * @param state
	 *            the current connection state
	 */
	public void connectionStateChanged(final ConnectionState state) {
		if (_connectionStatePropertyValues != null
				&& _connectionStatePropertyValues.containsKey(state)) {
			asyncApplyValueToProperty(_connectionStatePropertyValues.get(state));
		}
	}

	/**
	 * This method is called by connectors (see {@link Connector}), when the
	 * condition state changes.
	 * 
	 * @param state
	 *            the current condition state
	 */
	public void conditionStateChanged(final DynamicValueState state) {
		if (_conditionStatePropertyValues != null
				&& _conditionStatePropertyValues.containsKey(state)) {
			asyncApplyValueToProperty(_conditionStatePropertyValues.get(state));
		}
	}

	/**
	 * Asynchroniously applies the specified value to the widget property.
	 * 
	 * @param value
	 *            the new value
	 */
	private void asyncApplyValueToProperty(final Object value) {
		MyRunnable r = new MyRunnable(_widgetProperty, value);
		
		BundelingThread.getInstance().addRunnable(r);
		
//		ExecutorAccess.getInstance().getExecutorService().execute(r);
	}

	class MyRunnable extends TimeTrackedRunnable {
		private WidgetProperty _widgetProperty;

		private Object _value;

		public MyRunnable(WidgetProperty property, Object value) {
			super(MeasureCategoriesEnum.PROPERTY_EVENT_CATEGORY);
			_widgetProperty = property;
			_value = value;
		}

		@Override
		protected void doRun() {
			// update the widget property
			_widgetProperty.setPropertyValue(_value);
		}

		@Override
		public boolean equals(Object obj) {
			boolean result = false;

			if (obj instanceof MyRunnable) {
				MyRunnable r = (MyRunnable) obj;

				result = (r._widgetProperty == _widgetProperty);
			}
			return result;
		}
	}

}
