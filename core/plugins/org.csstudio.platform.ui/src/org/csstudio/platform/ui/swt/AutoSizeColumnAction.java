package org.csstudio.platform.ui.swt;

import org.csstudio.platform.ui.CSSPlatformUiPlugin;
import org.csstudio.platform.ui.internal.localization.Messages;
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
		super(Messages.AutoSizeColumnAction_Text, AS_CHECK_BOX);
		this.autosize = autosize;
		setImageDescriptor(CSSPlatformUiPlugin.getImageDescriptor("icons/autosize.gif")); //$NON-NLS-1$
		setChecked(autosize.isAutosizing());
	}
	
	@Override
	public void run()
	{
		autosize.enableAutosize(isChecked());
	}
}
