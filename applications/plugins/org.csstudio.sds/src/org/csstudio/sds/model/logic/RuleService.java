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
package org.csstudio.sds.model.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csstudio.sds.ErrorMessagesTracker;
import org.csstudio.sds.SdsPlugin;
import org.csstudio.sds.internal.model.logic.ScriptedRule;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.mozilla.javascript.RhinoException;

/**
 * This class provides access to all available rules.
 * 
 * @author Alexander Will
 * @version $Revision$
 * 
 */
public final class RuleService extends ErrorMessagesTracker {
	/**
	 * The workspace project that holds all the rules.
	 */
	public static final String SCRIPT_PROJECT_NAME = "SDS Script Rules"; //$NON-NLS-1$

	/**
	 * The file extension of SDS scripts.
	 */
	public static final String SCRIPT_FILE_EXTENSION = "css-sdss"; //$NON-NLS-1$	

	/**
	 * The shared instance of this class.
	 */
	private static RuleService _instance = null;

	/**
	 * All registered Java rules that are registered via an extension point.
	 */
	private Map<String, RuleDescriptor> _javaRuleDescriptors = null;

	/**
	 * All registered script rules.
	 */
	private Map<String, RuleDescriptor> _scriptRuleDescriptors = null;

	/**
	 * Private constructor due to the singleton pattern.
	 */
	private RuleService() {
		_javaRuleDescriptors = Collections
				.synchronizedMap(new HashMap<String, RuleDescriptor>());
		_scriptRuleDescriptors = Collections
				.synchronizedMap(new HashMap<String, RuleDescriptor>());

		lookupExtensions();
		lookupScripts();
	}

	/**
	 * Return the shared instance of this class.
	 * 
	 * @return The shared instance of this class.
	 */
	public static RuleService getInstance() {
		if (_instance == null) {
			_instance = new RuleService();
		}

		return _instance;
	}

	/**
	 * Return the IDs of all registered rules.
	 * 
	 * @return The IDs of all registered rules.
	 */
	public Set<String> getRuleIds() {
		return new HashSet<String>(_javaRuleDescriptors.keySet());
	}

	/**
	 * Return the rule descriptor for the given rulee ID.
	 * 
	 * @param ruleId
	 *            A rule ID.
	 * @return The rule descriptor for the given type ID.
	 */
	public RuleDescriptor getRuleDescriptor(final String ruleId) {
		return _javaRuleDescriptors.get(ruleId);
	}

	/**
	 * Perform a lookup for plugins that provide extensions for the
	 * <code>rules</code> extension point.
	 */
	@SuppressWarnings("unchecked")
	private void lookupExtensions() {
		IExtensionRegistry extReg = Platform.getExtensionRegistry();
		String id = SdsPlugin.EXTPOINT_RULES;
		IConfigurationElement[] confElements = extReg
				.getConfigurationElementsFor(id);

		for (IConfigurationElement element : confElements) {
			IRule type = null;
			String ruleId = element.getAttribute("ruleId"); //$NON-NLS-1$
			String description = element.getAttribute("name"); //$NON-NLS-1$

			String[] parameterDescriptions = null;
			Class[] parameterTypes = null;

			IConfigurationElement[] parameterElements = element
					.getChildren("parameterDescription"); //$NON-NLS-1$

			if (parameterElements != null) {
				parameterDescriptions = new String[parameterElements.length];
				parameterTypes = new Class[parameterElements.length]; 
				int i = 0;
				for (IConfigurationElement parameterElement : parameterElements) {
					String parameterDescription = parameterElement
							.getAttribute("name"); //$NON-NLS-1$
					parameterDescriptions[i] = parameterDescription;
					Class valueType = null;
					try {
						String attribute = parameterElement.getAttribute("type");
						valueType = Class.forName(attribute);
						parameterTypes[i] = valueType;
					} catch (Exception e) {
						parameterTypes[i] = Object.class;
					}
					i++;
				}
			}

			try {
				type = (IRule) element.createExecutableExtension("class"); //$NON-NLS-1$
			} catch (CoreException e) {
				trackException(e);
			}

			Class returnType = null;
			try {
				returnType = Class.forName(element.getAttribute("returnType"));
			} catch (Exception e) {
				// ignore
			}

			if (type != null && ruleId != null && returnType != null) {
				RuleDescriptor descriptor = new RuleDescriptor(ruleId,
						description, parameterDescriptions, parameterTypes, returnType, type,
						false);
				_javaRuleDescriptors.put(ruleId, descriptor);
			}
		}
	}

	/**
	 * Register the scripted rules.
	 */
	@SuppressWarnings("unchecked")
	private void lookupScripts() {
		IProject scriptProject = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(SCRIPT_PROJECT_NAME);

		try {
			IResource[] resources = scriptProject.members();

			for (IResource resource : resources) {
				if (resource instanceof IFile
						&& SCRIPT_FILE_EXTENSION.equalsIgnoreCase(resource
								.getFileExtension())) {

					IFile scriptFile = (IFile) resource;

					updateScriptedRule(scriptFile.getName(), scriptFile);
				}
			}
		} catch (Exception ex) {
			trackException(ex);
		}
	}

	/**
	 * Update the definition of a scripted rule.
	 * 
	 * @param ruleId
	 *            The ID of the rule.
	 * @param scriptFile
	 *            The file resource that contains a script rule or
	 *            <code>null</code> to remove the rule definition.
	 */
	public void updateScriptedRule(final String ruleId, final IFile scriptFile) {
		try {
			if (_scriptRuleDescriptors.containsKey(ruleId)) {
				_scriptRuleDescriptors.remove(ruleId);
			}

			if (scriptFile != null) {
				ScriptedRule type = new ScriptedRule(ruleId, scriptFile
						.getContents());

				RuleDescriptor typeDescriptor = new RuleDescriptor(ruleId, type
						.getDescription(), type.getParameterDescriptions(), type.getParameterTypes(), 
						type.getReturnType(), type, true);

				_scriptRuleDescriptors.put(ruleId, typeDescriptor);
			}
		} catch (Exception ex) {
			trackException(ex);
		}
	}

	/**
	 * Track the given exception.
	 * 
	 * @param e
	 *            The exception to track.
	 */
	private void trackException(final Exception e) {
		Throwable cause = e.getCause();

		String errorMessage = e.getMessage();

		if (cause != null) {
			if (cause instanceof RhinoException) {
				RhinoException re = (RhinoException) cause;
				errorMessage += "\n    The cause was [" + re.getMessage() + "]."; //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		trackErrorMessage(errorMessage);
	}

	/**
	 * @return A list which contains all rule descriptors.
	 */
	public List<RuleDescriptor> getRegisteredRuleDescriptors() {
		ArrayList<RuleDescriptor> result = new ArrayList<RuleDescriptor>();

		result.addAll(_javaRuleDescriptors.values());
		result.addAll(_scriptRuleDescriptors.values());

		return result;
	}
}
