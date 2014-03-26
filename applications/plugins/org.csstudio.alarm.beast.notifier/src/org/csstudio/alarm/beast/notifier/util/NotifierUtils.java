/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.alarm.beast.SeverityLevel;
import org.csstudio.alarm.beast.client.AADataStructure;
import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.client.AlarmTreePV;
import org.csstudio.alarm.beast.notifier.ActionID;
import org.csstudio.alarm.beast.notifier.model.IActionProvider;
import org.csstudio.alarm.beast.notifier.model.IActionValidator;
import org.csstudio.alarm.beast.ui.clientmodel.AlarmUpdateInfo;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.epics.util.time.Timestamp;

/**
 * Alarm Notifier utilities.
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class NotifierUtils {
	
	/** Pattern for automated action command scheme */
    final private static Pattern SchemePattern = Pattern.compile("^([_A-Za-z0-9]+):.*");
	
	/**
	 * Read automated action extension points from plugin.xml.
	 * @return Map<String, IAutomatedAction>, extension points referenced by their scheme.
	 * @throws CoreException if implementations don't provide the correct IAutomatedAction
	 */
	public static Map<String, IActionProvider> getActions() throws CoreException {
	    final Map<String, IActionProvider> map = new HashMap<String, IActionProvider>();
		final IExtensionRegistry reg = Platform.getExtensionRegistry();
		final IConfigurationElement[] extensions = reg
				.getConfigurationElementsFor(IActionProvider.EXTENSION_POINT);
		for (IConfigurationElement element : extensions)
		{
			final String scheme = element.getAttribute("scheme");
			final IActionProvider action = (IActionProvider) element.createExecutableExtension("action");
			map.put(scheme, action);
		}
		return map;
	}
	
	/** @return {@link ActionID} for the specified {@link AlarmTreeItem} and {@link AADataStructure} */
	public static ActionID getActionID(AlarmTreeItem item, AADataStructure aa) {
		return new ActionID(item.getPathName(), aa.getTitle());
	}

	/**
	 * Create an {@link AlarmUpdateInfo} from an {@link AlarmTreePV}.
	 * @param pv
	 * @return
	 */
	public static AlarmUpdateInfo getInfofromPVItem(final AlarmTreePV pv)
    {
        final String name = pv.getPathName();
        final SeverityLevel severity = pv.getSeverity();
        final String status = pv.getMessage();
        final SeverityLevel current_severity = pv.getCurrentSeverity();
        final String current_message = pv.getCurrentMessage();
        final String value = pv.getValue();
        final Timestamp timestamp = pv.getTimestamp();
        return new AlarmUpdateInfo(name, current_severity, current_message,
                severity, status, value, timestamp);
    }
	
	/**
	 * Perform a validation on automated action details.
	 * @param details
	 * @throws Exception
	 */
	public static void performValidation(String details) throws Exception {
		if (details == null || details.isEmpty()) {
			throw new Exception("Empty details");
		}
		Map<String, IActionProvider> schemeMap = getActions();
		final Matcher schemeMatcher = SchemePattern.matcher(details.trim());
		if (!schemeMatcher.matches()) {
			throw new Exception("Unrecognized command pattern");
		}
		final String scheme = schemeMatcher.group(1);
		// Locate automated action for schema
		final IActionProvider provider = schemeMap.get(scheme);
		if (provider == null) {
			throw new Exception("Unrecognized command scheme: " + scheme);
		}
		// Create and initialize action
		final IActionValidator validator = provider.getValidator();
		if (validator != null) {
			validator.init(details);
			validator.validate();
		}
	}
}
