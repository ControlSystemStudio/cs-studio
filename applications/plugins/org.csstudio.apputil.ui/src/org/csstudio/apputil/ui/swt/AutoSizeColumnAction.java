package org.csstudio.apputil.ui.swt;

import org.csstudio.apputil.ui.Activator;
import org.eclipse.jface.action.Action;

/** Action that enables/disables AutoSizeControlListener
 *  <p>
 *  Meant to be placed in the context menu of a Table
 *  that uses an AutoSizeControlListener.
 *  @author Kay Kasemir
 *  @see AutoSizeControlListener
 */
public class AutoSizeColumnAction extends Action
{
    final private AutoSizeControlListener autosize;
    
    public AutoSizeColumnAction(final AutoSizeControlListener autosize)
    {
        super("Autosize Columns", AS_CHECK_BOX);
        this.autosize = autosize;
        setImageDescriptor(Activator.getImageDescriptor("icons/autosize.gif"));
        setChecked(autosize.isAutosizing());
    }

    @Override
    public void run()
    {
        autosize.enableAutosize(isChecked());
    }
}
