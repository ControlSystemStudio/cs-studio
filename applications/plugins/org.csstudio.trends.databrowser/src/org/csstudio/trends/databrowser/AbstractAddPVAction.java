package org.csstudio.trends.databrowser;

import org.csstudio.trends.databrowser.Messages;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/** Abstract base for the AddPVActions of the PlotEditor and the ConfigView.
 *  @author Kay Kasemir
 */
public abstract class AbstractAddPVAction extends Action
{
	class PVNameValidator implements IInputValidator
	{
		public String isValid(String name)
		{
			if (name == null  ||  name.length() < 1)
				return (Messages.EnterPVName);
			if (name.matches("^[a-zA-Z0-9_:.]+$")) //$NON-NLS-1$
				return null;
			return Messages.PVContainsInvalidChars;
		}
	}

	public AbstractAddPVAction()
	{
		setText(Messages.AddPV);
		setToolTipText(Messages.AddPV_TT);
        setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
        		.getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD));
        setDisabledImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD_DISABLED));
	}

	@Override
	public void run()
    {
		InputDialog dlg = new InputDialog(null,
				Messages.AddPV_TT,
				Messages.EnterNewPVName,
				null, new PVNameValidator());
		if (dlg.open() == InputDialog.OK)
            addPV(dlg.getValue());
    }
    
    protected abstract void addPV(String pv_name);
}
