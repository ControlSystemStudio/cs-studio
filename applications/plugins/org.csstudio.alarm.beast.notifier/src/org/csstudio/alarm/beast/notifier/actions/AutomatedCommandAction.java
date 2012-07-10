package org.csstudio.alarm.beast.notifier.actions;

import org.csstudio.alarm.beast.notifier.model.IActionValidator;
import org.csstudio.alarm.beast.notifier.model.IAutomatedAction;
import org.csstudio.alarm.beast.notifier.model.INotificationAction;

/** Automated action that executes an external command.
 *
 *  <p>plugin.xml registers this for the "cmd" scheme.
 *  @author Kay Kasemir
 */
public class AutomatedCommandAction implements IAutomatedAction
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
        return new CommandNotificationAction();
    }
}
