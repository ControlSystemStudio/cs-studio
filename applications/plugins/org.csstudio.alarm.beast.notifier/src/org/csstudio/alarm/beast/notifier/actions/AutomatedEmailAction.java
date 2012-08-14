package org.csstudio.alarm.beast.notifier.actions;

import org.csstudio.alarm.beast.notifier.model.IActionValidator;
import org.csstudio.alarm.beast.notifier.model.IAutomatedAction;
import org.csstudio.alarm.beast.notifier.model.INotificationAction;
import org.csstudio.alarm.beast.notifier.util.EMailCommandValidator;

/** Automated action that sends email.
 *
 *  <p>plugin.xml registers this for the "mailto" scheme.
 *  @author Kay Kasemir
 */
public class AutomatedEmailAction implements IAutomatedAction
{
    /** {@inheritDoc} */
    @Override
    public IActionValidator getValidator()
    {
        return new EMailCommandValidator();
    }

    /** {@inheritDoc} */
    @Override
    public INotificationAction getNotifier()
    {
        return new EmailNotificationAction();
    }
}
