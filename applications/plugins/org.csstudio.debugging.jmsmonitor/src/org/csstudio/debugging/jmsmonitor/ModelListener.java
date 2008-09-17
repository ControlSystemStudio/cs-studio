package org.csstudio.debugging.jmsmonitor;

/** Interface for model notifications
 *  @author Kay Kasemir
 */
public interface ModelListener
{
    /** Invoked when something in the model changed: New messages, ... */
    void modelChanged(Model model);
}
