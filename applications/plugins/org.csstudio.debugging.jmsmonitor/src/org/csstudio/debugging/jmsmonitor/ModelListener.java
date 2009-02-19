package org.csstudio.debugging.jmsmonitor;

/** Interface for model notifications
 *  @author Kay Kasemir
 */
public interface ModelListener
{
    /** Invoked when something in the model changed: Server, new messages, ... */
    void modelChanged(Model model);
}
