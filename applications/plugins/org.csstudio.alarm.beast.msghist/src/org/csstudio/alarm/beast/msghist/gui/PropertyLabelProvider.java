package org.csstudio.alarm.beast.msghist.gui;

import org.csstudio.alarm.beast.msghist.model.Message;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.osgi.util.NLS;

/** CellLabelProvider that fills cell with a property of a Message.
 *  @author Kay Kasemir
 */
public class PropertyLabelProvider extends CellLabelProvider
{
    final protected String property;
    
    /** Create label provider for Message
     *  @param property Name of property to display
     */
    public PropertyLabelProvider(final String property)
    {
        this.property = property;
    }

    /** Show "Property: Value" as tool-tip */
    @Override
	public String getToolTipText(final Object element)
    {
        final Message message = (Message) element;
        return NLS.bind("{0}: {1}",	property, message.getProperty(property));
	}

    /** Fill table cell
     *  @see CellLabelProvider
     */
	@Override
    public void update(ViewerCell cell)
    {
        final Message message = (Message) cell.getElement();
        cell.setText(message.getProperty(property));
    }
}
