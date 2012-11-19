package org.csstudio.alarm.beast.notifier.actions;

import org.csstudio.alarm.beast.notifier.model.IActionValidator;
import org.csstudio.alarm.beast.notifier.model.IAutomatedAction;
import org.csstudio.alarm.beast.notifier.model.INotificationAction;

/** Automated action that sends SMS.
 *
 *  <p>plugin.xml registers this for the "smsto" scheme.
 *  @author Kay Kasemir
 */
public class AutomatedSmsAction implements IAutomatedAction
{
    /** {@inheritDoc} */
    @Override
    public IActionValidator getValidator()
    {
        // No validator
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public INotificationAction getNotifier()
    {
        return new SMSNotificationAction();
    }
}
