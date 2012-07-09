package org.csstudio.alarm.beast.notifier.actions;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.alarm.beast.client.AADataStructure;
import org.csstudio.alarm.beast.notifier.ActionExtensionPoint;
import org.csstudio.alarm.beast.notifier.ActionID;
import org.csstudio.alarm.beast.notifier.Activator;
import org.csstudio.alarm.beast.notifier.AlarmNotifier;
import org.csstudio.alarm.beast.notifier.ItemInfo;
import org.csstudio.alarm.beast.notifier.model.IActionValidator;
import org.csstudio.alarm.beast.notifier.model.INotificationAction;

/**
 * Factory for automated actions.
 * Instantiate automated actions from information provided in details in {@link AADataStructure}
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class NotificationActionFactory {

	/** Singleton instance */
    private static NotificationActionFactory instance = null;
    
    /** Reference count for instance */
    private AtomicInteger references = new AtomicInteger();
	
	/** Map scheme => class name */
	private Map<String, ActionExtensionPoint> schemeMap;
	
	/** Pattern for automated action commend scheme */
    final protected static Pattern SchemePattern = Pattern.compile("^([_A-Za-z0-9]+):.*");
	
	public static NotificationActionFactory getInstance() throws Exception {
		synchronized (NotificationActionFactory.class) {
			if (instance == null)
				instance = new NotificationActionFactory();
		}
		instance.references.incrementAndGet();
		return instance;
	}
	
	/** Initialize the factory with automated actions scheme/implementations */
	public void init(Map<String, ActionExtensionPoint> schemeMap) {
		this.schemeMap = schemeMap;
	}
	
	/**
	 * Instantiate an automated action, if the scheme found (or not) in {@link AADataStructure}
	 * details is unknown, it returns a {@link DefaultNotificationAction}
	 * @param notifier
	 * @param id
	 * @param item
	 * @param delay
	 * @param details
	 * @return
	 */
	public INotificationAction getNotificationAction(AlarmNotifier notifier,
			ActionID id, ItemInfo item, int delay, String details) {
		INotificationAction action = null;
		try {
			// find scheme in details
			Matcher schemeMatcher = SchemePattern.matcher(details.trim());
			if (schemeMatcher.matches()) {
				String scheme = schemeMatcher.group(1);
				ActionExtensionPoint impl = schemeMap.get(scheme);

				if (impl != null && impl.getActionClass() != null) {
					action = (INotificationAction) Class.forName(
							impl.getActionClass()).newInstance();
					if (impl.getValidatorClass() != null) {
						IActionValidator validator = (IActionValidator) Class
								.forName(impl.getValidatorClass())
								.newInstance();
						validator.init(details);
						action.init(notifier, id, item, delay, details, validator);
					} else {
						action.init(notifier, id, item, delay, details);
					}
				} else {
					Activator.getLogger().log(Level.INFO,
							"Unrecognized command scheme: {0}", scheme);
				}
			} else {
				Activator.getLogger().log(Level.INFO,
						"Unrecognized command pattern: {0}", details);
			}
		} catch (InstantiationException e) {
			Activator.getLogger().log(Level.SEVERE,
					"Exception during automated action init: {0}", e.getMessage());
		} catch (IllegalAccessException e) {
			Activator.getLogger().log(Level.SEVERE,
					"Exception during automated action init: {0}", e.getMessage());
		} catch (ClassNotFoundException e) {
			Activator.getLogger().log(Level.SEVERE,
					"Exception during automated action init: {0}", e.getMessage());
		}
		// if undefined => try simple command execution
		if(action == null) {
			action = new DefaultNotificationAction();
			action.init(notifier, id, item, delay, details);
		}
		return action;
	}
}
