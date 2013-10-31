/*******************************************************************************
* Copyright (c) 2010-2013 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.alarm.beast.notifier.actions;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.alarm.beast.client.AADataStructure;
import org.csstudio.alarm.beast.client.AlarmTreeItem;
import org.csstudio.alarm.beast.notifier.AAData;
import org.csstudio.alarm.beast.notifier.Activator;
import org.csstudio.alarm.beast.notifier.ItemInfo;
import org.csstudio.alarm.beast.notifier.model.IActionHandler;
import org.csstudio.alarm.beast.notifier.model.IActionProvider;
import org.csstudio.alarm.beast.notifier.model.IActionValidator;
import org.csstudio.alarm.beast.notifier.model.IAutomatedAction;
import org.csstudio.alarm.beast.notifier.util.CSVParser;

/**
 * Factory for automated actions.
 * Instantiate automated actions from information provided in details in {@link AADataStructure}
 * @author Fred Arnaud (Sopra Group)
 */
@SuppressWarnings("nls")
public class AutomatedActionFactory {

	/** Singleton instance */
    private static AutomatedActionFactory instance = null;

    /** Reference count for instance */
    private AtomicInteger references = new AtomicInteger();

	/** Map scheme => class name */
	private Map<String, IActionProvider> schemeMap;

	/** Pattern for automated action command scheme */
    final private static Pattern SchemePattern = Pattern.compile("^([_A-Za-z0-9]+):.*");
    
	/** Pattern for {@link String} */
	final private static String StringPattern = 
			"\"((?:[^\\\\\"]+|\\\\(?:[btnfr\"'\\\\]|[0-3]?[0-7]{1,2}|u[0-9a-fA-F]{4}))*)\"";

	/** Pattern for automated action command sequence scheme */
	final private static char SequenceToken = ';';
	final private static Pattern SequencePattern = Pattern
			.compile(StringPattern + "(?:\\ *" + SequenceToken + "\\ *" + StringPattern + ")+");
    
    
	public static AutomatedActionFactory getInstance() throws Exception {
		synchronized (AutomatedActionFactory.class) {
			if (instance == null)
				instance = new AutomatedActionFactory();
		}
		instance.references.incrementAndGet();
		return instance;
	}

	/** Initialize the factory with automated actions scheme/implementations */
	public void init(Map<String, IActionProvider> schemeMap) {
		this.schemeMap = schemeMap;
	}

	/**
	 * Instantiate an automated action, if the scheme found (or not) in {@link AADataStructure}
	 * details is unknown, it returns a {@link CommandActionImpl}
	 * @param notifier
	 * @param id
	 * @param item
	 * @param delay
	 * @param details
	 * @return
	 */
	public IAutomatedAction getNotificationAction(final AlarmTreeItem aaItem,
			final AADataStructure auto_action, boolean isManual) {
		if (auto_action == null)
			return null;
		IAutomatedAction action = null;
		final ItemInfo item = ItemInfo.fromItem(aaItem);
		// Find sequence
		String details = auto_action.getDetails().trim();
		final Matcher sequenceMatcher = SequencePattern.matcher(details);
		if (sequenceMatcher.matches()) {
			String[] commands = null;
			CSVParser parser = new CSVParser(';', '"', '\\', true, true);
			try {
				commands = parser.parseLine(details);
				AutomatedActionSequence actionSequence = new AutomatedActionSequence();
				for (int index = 0; index < commands.length; index++) {
					final AAData data = new AAData(commands[index], auto_action.getDelay(), isManual);
					IAutomatedAction aa = createAutomatedAction(item, data);
					if(aa != null) actionSequence.add(aa);
				}
				if (actionSequence.size() > 0)
					action = (IAutomatedAction) actionSequence;
			} catch (IOException e) {
				Activator.getLogger().log(Level.INFO,
						"Unrecognized command pattern: {0}", details);
			}
		} else {
			final AAData data = new AAData(auto_action.getDetails(), auto_action.getDelay(), isManual);
			action = createAutomatedAction(item, data);
		}
		return action;
	}

	public IAutomatedAction getNotificationAction(final AlarmTreeItem aaItem,
			final AADataStructure auto_action) {
		return getNotificationAction(aaItem, auto_action, false);
	}

	private IAutomatedAction createAutomatedAction(ItemInfo item, AAData data) {
		final String details = data.getDetails();
		// Find scheme in details
		final Matcher schemeMatcher = SchemePattern.matcher(details.trim());
		if (!schemeMatcher.matches()) {
			Activator.getLogger().log(Level.INFO,
					"Unrecognized command pattern: {0}", details);
			return null;
		}
		final String scheme = schemeMatcher.group(1);
		// Locate automated action for schema
		final IActionProvider provider = schemeMap.get(scheme);
		if (provider == null) {
			Activator.getLogger().log(Level.INFO,
					"Unrecognized command scheme: {0}", scheme);
			return null;
		}
		// Create and initialize action
		final IAutomatedAction action = provider.getNotifier();
		final IActionValidator validator = provider.getValidator();
		try {
			IActionHandler handler = null;
			if (validator != null) {
				validator.init(details);
				if (validator.validate()) {
					handler = validator.getHandler();
				} else {
					Activator.getLogger().log(Level.SEVERE, "Action validation failed");
					return null;
				}
			}
			action.init(item, data, handler);
		} catch (Exception e) {
			Activator.getLogger().log(Level.SEVERE, e.getMessage());
			return null;
		}
		return action;
	}
}
