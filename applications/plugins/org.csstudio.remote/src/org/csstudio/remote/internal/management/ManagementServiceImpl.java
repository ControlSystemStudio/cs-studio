/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.remote.internal.management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.remote.management.CommandDescription;
import org.csstudio.remote.management.CommandParameterDefinition;
import org.csstudio.remote.management.CommandParameterEnumValue;
import org.csstudio.remote.management.CommandParameterType;
import org.csstudio.remote.management.CommandParameters;
import org.csstudio.remote.management.CommandResult;
import org.csstudio.remote.management.IDynamicParameterValues;
import org.csstudio.remote.management.IManagementCommand;
import org.csstudio.remote.management.IManagementCommandService;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

/**
 * <p>
 * Implements the Management Command Service based on the
 * <code>managementCommands</code> extension point. This implementation makes
 * available all commands registered as extensions of that extension point.
 * </p>
 * 
 * <p>
 * Note that this service is not a remote service. For remote administration,
 * this service must be pubished as a remote service by another plug-in.
 * </p>
 * 
 * @author Joerg Rathlev
 */
public class ManagementServiceImpl implements IManagementCommandService {
	
	private static final String EXTENSION_POINT_ID =
		"org.csstudio.remote.managementCommands";
	
	
	private Map<String, CommandContribution> _commands;

	/**
	 * Creates a new instance of this service implementation. 
	 */
	public ManagementServiceImpl() {
		System.out.println("");
	}

	/**
	 * {@inheritDoc}
	 */
	public CommandDescription[] getSupportedCommands() {
		synchronized (this) {
			if (_commands == null) {
				readCommandsExtensionPoint();
			}
		}
		List<CommandDescription> result = new ArrayList<CommandDescription>();
		for (CommandContribution command : _commands.values()) {
			result.add(command.getDescription());
		}
		return (CommandDescription[]) result.toArray(
				new CommandDescription[result.size()]);
	}

	/**
	 * Reads the remote management commands from the extension point.
	 */
	private void readCommandsExtensionPoint() {
		_commands = new HashMap<String, CommandContribution>();
		IExtension[] extensions = Platform.getExtensionRegistry()
				.getExtensionPoint(EXTENSION_POINT_ID)
				.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] configElements =
				extension.getConfigurationElements();
			for (IConfigurationElement configElement : configElements) {
				readCommandContribution(configElement);
			}
		}
	}

	/**
	 * Reads a single management command contribution from the specified
	 * configuration element.
	 * 
	 * @param configElement
	 *            the configuration element.
	 */
	private void readCommandContribution(IConfigurationElement configElement) {
		String id = configElement.getAttribute("id");
		try {
			CommandContribution.Builder builder =
				new CommandContribution.Builder()
					.setIdentifier(id)
					.setLabel(configElement.getAttribute("label"))
					.setCommandImplementation(
							(IManagementCommand) configElement
								.createExecutableExtension("class"));
			readParameterDefinitions(configElement, builder);
			CommandContribution command = builder.build();
			_commands.put(id, command);
		} catch (Exception e) {
			String contributor = configElement.getContributor().getName();
//			TODO (jhatje): Change logging.
//			log.error(this, "The management command with id " + id + 
//					" from plug-in " + contributor + " is invalid. " +
//					e.getMessage(), e);
		}
	}

	/**
	 * Reads the parameter definitions from the configuration element.
	 * 
	 * @param commandConfigElement
	 *            the command configuration element.
	 * @param commandBuilder
	 *            the command contribution builder.
	 */
	private void readParameterDefinitions(
			IConfigurationElement commandConfigElement,
			CommandContribution.Builder commandBuilder) {
		IConfigurationElement[] children = commandConfigElement.getChildren();
		for (IConfigurationElement parameterConfig : children) {
			CommandParameterDefinition.Builder definitionBuilder =
				new CommandParameterDefinition.Builder()
					.setIdentifier(parameterConfig.getAttribute("id"))
					.setLabel(parameterConfig.getAttribute("label"));

			String parameterType = parameterConfig.getName();
			IDynamicParameterValues dynamicValues = null;
			if ("stringParameter".equals(parameterType)) {
				definitionBuilder.setType(CommandParameterType.STRING);
			} else if ("integerParameter".equals(parameterType)) {
				definitionBuilder.setType(CommandParameterType.INTEGER)
					.setMinimum(readIntegerParameterAttribute(
						parameterConfig, "minimum", Integer.MIN_VALUE))
					.setMaximum(readIntegerParameterAttribute(
						parameterConfig, "maximum", Integer.MAX_VALUE));
			} else if ("enumerationParameter".equals(parameterType)) {
				definitionBuilder.setType(CommandParameterType.ENUMERATION);
				readEnumerationValues(parameterConfig, definitionBuilder);
			} else if ("dynamicEnumerationParameter".equals(parameterType)) {
				definitionBuilder.setType(CommandParameterType.DYNAMIC_ENUMERATION);
				try {
					dynamicValues = (IDynamicParameterValues)
							parameterConfig.createExecutableExtension("class");
				} catch (CoreException e) {
					throw new RuntimeException(
							"Could not create object for dynamic paramter values.",
							e);
				}
			} else {
				throw new RuntimeException("Unknown parameter type: " +
						parameterType);
			}
			commandBuilder.addParameter(definitionBuilder.build(), dynamicValues);
		}
	}

	/**
	 * Reads the enumeration values of the specified enumeration parameter
	 * configuration element.
	 * 
	 * @param parameterConfig
	 *            the configuration element.
	 * @param definitionBuilder
	 *            the builder for the parameter definition.
	 */
	private void readEnumerationValues(IConfigurationElement parameterConfig,
			CommandParameterDefinition.Builder definitionBuilder) {
		IConfigurationElement[] children =
			parameterConfig.getChildren("enumerationValue");
		for (IConfigurationElement enumerationValue : children) {
			String value = enumerationValue.getAttribute("value");
			String label = enumerationValue.getAttribute("label");
			definitionBuilder.addEnumerationValue(
					new CommandParameterEnumValue(value, label));
		}
	}

	/**
	 * Reads a numeric attribute from the integer parameter configuration
	 * element.
	 * 
	 * @param parameterConfig
	 *            the configuration element.
	 * @param attribute
	 *            the name of the attribute to read.
	 * @param defaultValue
	 *            the default value. This value will be returned if the
	 *            attribute is not specified in the configuration element or if
	 *            the attribute does not contain an integer value.
	 * @return the attribute value, or the default value if the attribute was
	 *         not given.
	 */
	private int readIntegerParameterAttribute(
			IConfigurationElement parameterConfig, String attribute,
			int defaultValue) {
		String str = parameterConfig.getAttribute(attribute);
		if (str != null) {
			try {
				return Integer.parseInt(str);
			} catch (NumberFormatException e) {
				throw new RuntimeException("Invalid " + attribute +
						" for integer parameter. \"" + str +
						"\" is not an integer.", e);
			}
		} else {
			return defaultValue;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public CommandResult execute(String commandId, CommandParameters parameters) {
		CommandContribution command = _commands.get(commandId);
		if (command != null) {
			try {
				return command.getCommandImplementation().execute(parameters);
			} catch (RuntimeException e) {
				return CommandResult.createFailureResult(e);
			}
		} else {
			return CommandResult.createFailureResult(
					"Command not supported: " + commandId);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public CommandParameterEnumValue[] getDynamicEnumerationValues(
			String commandId, String parameterId) {
		CommandContribution command = _commands.get(commandId);
		if (command != null) {
			try {
				return command.getDynamicEnumerationValues(parameterId);
			} catch (RuntimeException e) {
				return new CommandParameterEnumValue[0];
			}
		} else {
			return new CommandParameterEnumValue[0];
		}
	}
	
}
