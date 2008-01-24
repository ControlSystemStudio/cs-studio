/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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
package org.csstudio.sds.internal.connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.platform.model.pvs.ControlSystemEnum;
import org.csstudio.platform.model.pvs.DalPropertyTypes;
import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.security.ActivationService;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.csstudio.platform.simpledal.ValueType;
import org.csstudio.sds.internal.connection.custom.MultiThreadConnectionFactory;
import org.csstudio.sds.internal.connection.dal.DalConnectorFactory;
import org.csstudio.sds.internal.connection.dal.SystemConnector;
import org.csstudio.sds.internal.model.logic.RuleEngine;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.DisplayModel;
import org.csstudio.sds.model.DynamicsDescriptor;
import org.csstudio.sds.model.WidgetProperty;
import org.csstudio.sds.model.logic.ParameterDescriptor;
import org.csstudio.sds.model.logic.RuleDescriptor;
import org.csstudio.sds.model.logic.RuleService;
import org.csstudio.sds.util.ChannelReferenceValidationException;
import org.epics.css.dal.DoubleProperty;
import org.epics.css.dal.DynamicValueProperty;

/**
 * A connection service interprets the dynamic configuration of SDS widgets and
 * connects them to a layer, which provides and/or takes dynamic value updates.
 * 
 * This abstract base class defines and implements the generic infrastructure
 * for handling the dynamic configuration of widget models. It encapsulates most
 * of the necessary processing routines, e.g.:
 * 
 * <ul>
 * <li>- prepare channel names by the means of configured alias replacements</li>
 * <li>- preparing the used scripting rules</li>
 * <li>- caching listener states</li>
 * <li>- connecting and disconnecting display models</li>
 * </ul>
 * 
 * To allow for the implementation of different connection services that connect
 * via different layers (e.g. via the Data Access Layer (DAL) or via simple,
 * prototypic threading solutions), some communication details are obtained by
 * querying an <code>{@link IConnectorFactory}</code>, which delivers the
 * necessary connection layer specific listeners or allows for registering /
 * unregistering of those listeners.
 * 
 * @author Sven Wende
 * @version $Revision$
 */
public final class ConnectionService {
	/**
	 * Contains the connection service listeners.
	 */
	private List<IConnectionServiceStateListener> _listeners;

	/**
	 * The connector factories.
	 */
	private Map<ControlSystemEnum, IConnectorFactory> _connectorFactories;

	/**
	 * Connector states (organized by widget).
	 */
	private HashMap<AbstractWidgetModel, ActiveConnectorsState> _connectorStates = new HashMap<AbstractWidgetModel, ActiveConnectorsState>();

	/**
	 * Connector states (organized by channel).
	 */
	private HashMap<IProcessVariableAddress, ActiveConnectorsState> _connectorStatesByChannel = new HashMap<IProcessVariableAddress, ActiveConnectorsState>();

	/**
	 * The singleton instance.
	 */
	private static ConnectionService _instance;

	/**
	 * Returns the singleton instance.
	 * 
	 * @return
	 */
	public static ConnectionService getInstance() {
		if (_instance == null) {
			_instance = new ConnectionService();
		}
		return _instance;
	}

	/**
	 * Constructor.
	 * 
	 * @param connectorFactory
	 *            the used connector factory.
	 */
	private ConnectionService() {
		_connectorFactories = new HashMap<ControlSystemEnum, IConnectorFactory>();
		IConnectorFactory dalFactory = new DalConnectorFactory();
		IConnectorFactory sdsFactory = new MultiThreadConnectionFactory();

		_connectorFactories.put(ControlSystemEnum.DAL_EPICS, sdsFactory);
		_connectorFactories.put(ControlSystemEnum.DAL_TINE, dalFactory);
		_connectorFactories.put(ControlSystemEnum.DAL_TANGO, dalFactory);
		_connectorFactories.put(ControlSystemEnum.EPICS, dalFactory);
		_connectorFactories.put(ControlSystemEnum.TINE, dalFactory);
		_connectorFactories.put(ControlSystemEnum.TANGO, dalFactory);
		_connectorFactories.put(ControlSystemEnum.DAL_SIMULATOR, dalFactory);
		_connectorFactories.put(ControlSystemEnum.SDS_SIMULATOR, dalFactory);
		_connectorFactories.put(ControlSystemEnum.UNKNOWN, sdsFactory);

		// _connectorFactories.put(ControlSystemEnum.DAL_EPICS, sdsFactory); //
		// FIXME: Points to SDS ! Only for presentation!
		// _connectorFactories.put(ControlSystemEnum.EPICS, sdsFactory); //
		// FIXME: Points to SDS ! Only for presentation!
		_listeners = new ArrayList<IConnectionServiceStateListener>();
	}

	/**
	 * Backdoor method for unit tests. Do never call this by yourself.
	 * 
	 * @param factory
	 *            a factory which is used for all control systems
	 */
	protected void overrideFactories(IConnectorFactory factory) {
		for (ControlSystemEnum controlSystem : _connectorFactories.keySet()) {
			_connectorFactories.put(controlSystem, factory);
		}
	}

	/**
	 * Adds the specified connection service state listener.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void addConnectionServiceStateListener(
			final IConnectionServiceStateListener listener) {
		assert listener != null;
		if (!_listeners.contains(listener)) {
			_listeners.add(listener);
		}
	}

	/**
	 * Removes the specified connection service state listener.
	 * 
	 * @param listener
	 *            the listener
	 */
	public void removeConnectionServiceStateListener(
			final IConnectionServiceStateListener listener) {
		assert listener != null;
		if (_listeners.contains(listener)) {
			_listeners.remove(listener);
		}
	}

	/**
	 * Fires events for all connection service state listener.
	 */
	private void fireConnectionServiceStateChange() {
		for (IConnectionServiceStateListener l : _listeners) {
			l.connectionServiceStateChanged(this);
		}
	}

	/**
	 * Connects the specified display model to the control system.
	 * 
	 * @param model
	 *            the model
	 * @param refreshRate
	 *            the refresh rate
	 * @throws SdsException
	 *             A connection exception is thrown if the connection could not
	 *             be established.
	 * @deprecated
	 */
	public void connect(final DisplayModel model, final int refreshRate) {

		for (AbstractWidgetModel widgetModel : model.getWidgets()) {
			doConnectWidgetModel(widgetModel, refreshRate);
		}
	}

	/**
	 * Connects the specified widget model.
	 * 
	 * @param parent
	 *            the parent display model
	 * 
	 * @param widgetModel
	 *            the widget model
	 * @param refreshRate
	 *            a refresh rate hint
	 * @throws SdsException
	 *             an exception
	 */
	public void connectWidgetModel(final DisplayModel parent,
			final AbstractWidgetModel widgetModel, final int refreshRate) {
		doConnectWidgetModel(widgetModel, refreshRate);
	}

	public void connectWidgetModel(final AbstractWidgetModel widgetModel) {
		// TODO: Hier wird neues Model übergeben ... wenn Parent-Bez. zwischen
		// Model-Elementen implementiert--> eliminieren!!!
		doConnectWidgetModel(widgetModel, 10);
	}

	public void disConnectWidgetModel(final AbstractWidgetModel widgetModel) {
		doDisconnectWidgetModel(widgetModel);
	}

	/**
	 * Disconnects the specified display model from the control system.
	 * 
	 * @param displayModel
	 *            the display model
	 */
	public void disconnectModel(final DisplayModel displayModel) {
		for (AbstractWidgetModel widgetModel : displayModel.getWidgets()) {
			doDisconnectWidgetModel(widgetModel);
		}
	}

	/**
	 * During the connection process, listeners will be connected to control
	 * system channels to receive dynamic values which should be visualized in
	 * the UI. For a clean disconnect it is vital to track which listeners were
	 * registered. Via this method the necessary informations can be cached in a
	 * state object.
	 * 
	 * @param widgetModel
	 *            the widget model for which a listener was registered
	 * @param channelReference
	 *            a channel reference for which the listener was registered
	 * @param connector
	 *            the listener itself
	 */
	private void addListenerState(final AbstractWidgetModel widgetModel,
			final IProcessVariableAddress channelReference,
			final SystemConnector connector) {
		synchronized (_connectorStates) {
			getStateByWidgetModel(widgetModel).addConnector(channelReference,
					connector);

		}

		synchronized (_connectorStatesByChannel) {
			getStateByChannel(channelReference).addConnector(channelReference,
					connector);
		}

		fireConnectionServiceStateChange();
	}

	/**
	 * Removes listener state informations that where registered via
	 * {@link #addListenerState(AbstractWidgetModel, ChannelReference, Object)}.
	 * 
	 * @param widgetModel
	 *            the widget model for which a listener was registered
	 * @param channelReference
	 *            a channel reference for which the listener was registered
	 * @param connector
	 *            the listener itself
	 */
	private void removeListenerState(final AbstractWidgetModel widgetModel,
			final IProcessVariableAddress channelReference,
			final SystemConnector connector) {

		ActiveConnectorsState state = _connectorStates.get(widgetModel);
		state.removeConnector(channelReference, connector);
		if (state.getConnectors().size() <= 0) {
			_connectorStates.remove(widgetModel);
		}

		state = getStateByChannel(channelReference);
		state.removeConnector(channelReference, connector);
		if (state.getConnectors().size() <= 0) {
			_connectorStatesByChannel.remove(channelReference);
		}

		fireConnectionServiceStateChange();
	}

	/**
	 * Return the connector states (organized by widget).
	 * 
	 * @return the connector states (organized by widget).
	 */
	public HashMap<AbstractWidgetModel, ActiveConnectorsState> getStatesByWidgetModel() {
		return _connectorStates;
	}

	/**
	 * Return the connector states (organized by channel).
	 * 
	 * @return the connector states (organized by channel).
	 */
	public HashMap<IProcessVariableAddress, ActiveConnectorsState> getStatesByChannel() {
		return _connectorStatesByChannel;
	}

	/**
	 * Gets a listener state object for the specified widget model. If a state
	 * object is already cached for this widget model it is returned, otherwise
	 * a new state object will be created.
	 * 
	 * @param widgetModel
	 *            a widget model
	 * @return a listner state object
	 */
	private ActiveConnectorsState getStateByWidgetModel(
			final AbstractWidgetModel widgetModel) {
		ActiveConnectorsState state;

		if (_connectorStates.containsKey(widgetModel)) {
			state = _connectorStates.get(widgetModel);
		} else {
			state = new ActiveConnectorsState();
			_connectorStates.put(widgetModel, state);
		}

		assert state != null;

		return state;
	}

	/**
	 * Gets a listener state object for the specified channel. If a state object
	 * is already cached for this channel it is returned, otherwise a new state
	 * object will be created.
	 * 
	 * @param channelReference
	 *            a channel reference
	 * @return a listener state object
	 */
	private ActiveConnectorsState getStateByChannel(
			final IProcessVariableAddress channelReference) {
		ActiveConnectorsState state;

		if (_connectorStatesByChannel.containsKey(channelReference)) {
			state = _connectorStatesByChannel.get(channelReference);
		} else {
			state = new ActiveConnectorsState();
			_connectorStatesByChannel.put(channelReference, state);
		}

		assert state != null;

		return state;
	}

	/**
	 * Converts parameter descriptors to channel references.
	 * 
	 * @param parameterDescriptors
	 *            parameter descriptors
	 * @param aliases
	 *            aliases
	 * 
	 * @return channel references
	 */
	private ChannelReference[] toPropertyReferences(
			final ParameterDescriptor[] parameterDescriptors,
			final Map<String, String> aliases) {
		int n = parameterDescriptors.length;
		ChannelReference[] refs = new ChannelReference[n];

		for (int i = 0; i < n; i++) {
			refs[i] = toPropertyReference(parameterDescriptors[i]);
		}

		return refs;
	}

	/**
	 * Converts a parameter descriptor to a channel reference.
	 * 
	 * @param parameterDescriptor
	 *            parameter descriptors
	 * 
	 * @return channel reference
	 */
	private ChannelReference toPropertyReference(
			final ParameterDescriptor parameterDescriptor) {
		return new ChannelReference(parameterDescriptor.getChannel(),
				parameterDescriptor.getType(), parameterDescriptor.getValue());
	}

	/**
	 * Connects the specified widget model to the control system. Thereby the
	 * necessary listeners will be connected to control system channels and/or
	 * widget properties.
	 * 
	 * @param parent
	 *            the display model, which is the parent of the given widget
	 * @param widgetModel
	 *            the widget model
	 * @param refreshRate
	 *            the refresh reate
	 * @throws SdsException
	 *             an exception
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void doConnectWidgetModel(final AbstractWidgetModel widgetModel,
			final int refreshRate) {
		// get the aliases
		Map<String, String> aliases = widgetModel.getAllInheritedAliases();

		for (String key : widgetModel.getAliases().keySet()) {
			aliases.put(key, widgetModel.getAliases().get(key));
		}

		// connect the widget to the widget management API
		String permissionId = widgetModel.getPermissionID();

		if (permissionId != null && permissionId.length() > 0) {
			ActivationService.getInstance().registerObject(permissionId,
					widgetModel);
		}

		// create a connection context
		ConnectionContext connectionContext = new ConnectionContext(aliases);

		// connect each single property
		for (String propertyId : widgetModel.getPropertyNames()) {
			WidgetProperty property = widgetModel.getProperty(propertyId);

			// read the dynamics descriptor
			DynamicsDescriptor dynamicsDescriptor = property
					.getDynamicsDescriptor();

			// a dynamics descriptor must not exist
			if (dynamicsDescriptor != null) {
				// get all input references
				ChannelReference[] references = toPropertyReferences(
						dynamicsDescriptor.getInputChannels(), aliases);

				// input references must not exist
				if (references.length > 0) {
					// create a rule
					RuleDescriptor ruleDescriptor = RuleService.getInstance()
							.getRuleDescriptor(dynamicsDescriptor.getRuleId());

					// a rule descriptor must not exist
					if (ruleDescriptor != null) {

						// create a rule engine
						final RuleEngine ruleEngine = new RuleEngine(
								ruleDescriptor.getRule(), references);

						// connect the channels
						for (final ChannelReference reference : references) {
							if (reference.getRawChannelName()!=null && reference.getRawChannelName().trim().length()>0) {
								IProcessVariableAddress processVariable = convertChannelReference(
										reference, connectionContext);

								if (processVariable != null) {
									// create channel input processor
									ChannelInputProcessor processor = new ChannelInputProcessor(
											reference,
											ruleEngine,
											property,
											dynamicsDescriptor
													.getConnectionStateDependentPropertyValues(),
											dynamicsDescriptor
													.getConditionStateDependentPropertyValues());
								
									ValueType type = determineValueType(property,
											processVariable);

									SystemConnector connector = new SystemConnector(
											processVariable, type, processor);

									ProcessVariableConnectionServiceFactory
											.getProcessVariableConnectionService()
											.register(connector, processVariable,
													type);

									addListenerState(widgetModel, processVariable,
											connector);
								}	
							}
						}
					}
				}

				// set up handling of outgoing events

				// a output channel must not exist
				if (dynamicsDescriptor.getOutputChannel() != null) {
					ChannelReference outReference = toPropertyReference(dynamicsDescriptor
							.getOutputChannel());

					IProcessVariableAddress processVariable = convertChannelReference(
							outReference, connectionContext);

					if (processVariable != null) {
						// // create write connector
						// Connector writeConnector = _connectorFactories.get(
						// processVariable.getControlSystem())
						// .createConnector(processVariable);
						//
						// if (writeConnector != null) {
						// // initialize write connector propertly
						// writeConnector.setPropertyType(property
						// .getPropertyType());
						// writeConnector
						// .setConnectionContext(connectionContext);
						//
						// // make write connector listen to widget property
						// // changes
						// property.addPropertyChangeListener(writeConnector
						// .getPropertyChangeListener());
						//
						// // memorize the state
						// addListenerState(widgetModel, processVariable,
						// writeConnector);
						//
						// // connect
						// writeConnector.connect();
						// }

						ValueType type = determineValueType(property,
								processVariable);

						SystemConnector connector = new SystemConnector(
								processVariable, type, null);
						property.addPropertyChangeListener(connector);
						
						addListenerState(widgetModel, processVariable,
								connector);
					}
				}

			}
		}
	}

	private ValueType determineValueType(WidgetProperty property,
			IProcessVariableAddress processVariable) {
		// 1. choice, is there a type hint directly on
		// the pv ?
		ValueType type = processVariable.getValueTypeHint();

		// 2nd choice
		if (type == null) {
			// take the type hint, provided
			// by the widget
			// property
			type = property.getPropertyType().getTypeHint();
		}

		// 3rd choice, take double
		if (type == null) {
			type = ValueType.DOUBLE;
		}
		return type;
	}

	/**
	 * Converts the specified channel reference to to a
	 * {@link IProcessVariableAddress} object.
	 * 
	 * @param channelReference
	 *            the channel reference
	 * @param connectionContext
	 *            the connection context
	 * @return a {@link IProcessVariableAddress} object
	 */
	private IProcessVariableAddress convertChannelReference(
			final ChannelReference channelReference,
			final ConnectionContext connectionContext) {
		IProcessVariableAddress result = null;
		try {
			String realName = channelReference
					.getCanonicalName(connectionContext.getAliases());
			result = ProcessVariableAdressFactory.getInstance()
					.createProcessVariableAdress(realName);
			// result = new RemoteInfo(realName, "DEFAULT", "EPICS");

		} catch (ChannelReferenceValidationException e) {
			result = null;
		}

		return result;
	}

	/**
	 * Disconnects the specified widget model from the control system. Thereby
	 * all registered listeners will be removed from control system channels
	 * and/or widget properties.
	 * 
	 * @param widgetModel
	 *            the widget model
	 */
	@SuppressWarnings("unchecked")
	private void doDisconnectWidgetModel(final AbstractWidgetModel widgetModel) {
		ActiveConnectorsState state = _connectorStates.get(widgetModel);

		if (state != null) {
			// disconnect all dynamic value listeners from DAL properties
			HashMap<IProcessVariableAddress, List<SystemConnector>> list = state
					.getConnectors();

			for (IProcessVariableAddress ref : list.keySet()) {
				List<SystemConnector> connectors = list.get(ref);
				for (SystemConnector connector : connectors) {
					// connector.disconnect();
					ProcessVariableConnectionServiceFactory.getProcessVariableConnectionService().unregister(connector);
					// remove the state information
					removeListenerState(widgetModel, ref, connector);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
