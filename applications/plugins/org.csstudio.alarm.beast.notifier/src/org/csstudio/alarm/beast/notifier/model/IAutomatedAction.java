package org.csstudio.alarm.beast.notifier.model;

/** Interface to be implemented by automated actions
 *
 *  <p>Plugins can register an automated actions for
 *  a schema, and provide an implementation of this interface
 *  to handle the action.
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public interface IAutomatedAction
{
    /** ID of the extension point, defined in plugin.xml */
    final public static String EXTENSION_POINT = "org.csstudio.alarm.beast.notifier";

    /** Automated actions should provide a validator
     *  that checks the action specification
     *  for correctness.
     *
     *  <p>For example, an email notification handler
     *  can check for valid email format.
     *
     *  @return {@link IActionValidator} or <code>null</code> if none is provided
     */
    public IActionValidator getValidator();

    /** Provide the class that performs the automated action
     *  @return INotificationAction
     */
    public INotificationAction getNotifier();
}
