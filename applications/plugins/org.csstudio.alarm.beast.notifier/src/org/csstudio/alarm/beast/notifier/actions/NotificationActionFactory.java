package org.csstudio.alarm.beast.notifier.actions;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.alarm.beast.client.AADataStructure;
import org.csstudio.alarm.beast.notifier.ActionID;
import org.csstudio.alarm.beast.notifier.Activator;
import org.csstudio.alarm.beast.notifier.AlarmNotifier;
import org.csstudio.alarm.beast.notifier.ItemInfo;
import org.csstudio.alarm.beast.notifier.model.IActionValidator;
import org.csstudio.alarm.beast.notifier.model.IAutomatedAction;
import org.csstudio.alarm.beast.notifier.model.INotificationAction;

/**
 * Factory for automated actions.
 * Instantiate automated actions from information provided in details in {@link AADataStructure}
 * @author Fred Arnaud (Sopra Group)
 */
@SuppressWarnings("nls")
public class NotificationActionFactory {

	/** Singleton instance */
    private static NotificationActionFactory instance = null;

    /** Reference count for instance */
    private AtomicInteger references = new AtomicInteger();

	/** Map scheme => class name */
	private Map<String, IAutomatedAction> schemeMap;

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
	public void init(Map<String, IAutomatedAction> schemeMap) {
		this.schemeMap = schemeMap;
	}

	/**
	 * Instantiate an automated action, if the scheme found (or not) in {@link AADataStructure}
	 * details is unknown, it returns a {@link CommandNotificationAction}
	 * @param notifier
	 * @param id
	 * @param item
	 * @param delay
	 * @param details
	 * @return
	 */
	public INotificationAction getNotificationAction(final AlarmNotifier notifier,
			final ActionID id, final ItemInfo item, final int delay, final String details)
	{
		// find scheme in details
	    final Matcher schemeMatcher = SchemePattern.matcher(details.trim());
		if (! schemeMatcher.matches())
		{
		    Activator.getLogger().log(Level.INFO,
                "Unrecognized command pattern: {0}", details);
		    return null;
		}
		final String scheme = schemeMatcher.group(1);
		// Locate automated action for schema
		final IAutomatedAction action = schemeMap.get(scheme);
		if (action == null)
		{
		    Activator.getLogger().log(Level.INFO,
	            "Unrecognized command scheme: {0}", scheme);
		    return null;
		}
		// Create and init. notification
		final INotificationAction result = action.getNotifier();
		final IActionValidator validator = action.getValidator();
		if (validator != null)
		{
			validator.init(details);
			result.init(notifier, id, item, delay, details, validator);
		}
		else
		    result.init(notifier, id, item, delay, details);
		return result;
	}
}
