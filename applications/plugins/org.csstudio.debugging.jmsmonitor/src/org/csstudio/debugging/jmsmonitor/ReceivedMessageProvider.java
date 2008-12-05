package org.csstudio.debugging.jmsmonitor;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/** Provider that links model (received messages) to table viewer.
 *  @author Kay Kasemir
 */
public class ReceivedMessageProvider implements IStructuredContentProvider
{
    private ReceivedMessage[] messages = null;

    /** Remember the new input.
     *  Should be the result of call to TableViewer.setInput(ReceivedMessage[])
     *  in GUI.
     */
    public void inputChanged(Viewer viewer, Object old_input, Object new_input)
    {
        messages = (ReceivedMessage[]) new_input;
    }

    /** {@inheritDoc} */
    public Object[] getElements(Object inputElement)
    {
        return messages;
    }

    /** {@inheritDoc} */
    public void dispose()
    {
        // Nothing to dispose
    }
}
