package org.csstudio.display.pvtable.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/** Action that adds a new PV to the model.
 *  @author Kay Kasemir
 */
public class AddAction extends Action
{
	private PVTableViewerHelper helper;
	
	class PVNameValidator implements IInputValidator
	{
		public String isValid(String name)
		{
			if (name == null  ||  name.length() < 1)
				return ("Enter PV name");
			if (name.matches("^[a-zA-Z0-9_:.]+$"))
				return null;
			return "PV contains invalid characters";
		}
	}

	public AddAction(PVTableViewerHelper helper)
	{
		this.helper = helper;
		setText("Add");
		setToolTipText("Add new PV");
        setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
        		.getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD));
	}

	@Override
	public void run()
    {
    		InputDialog dlg = new InputDialog(null,
    				"Add PV",
    				"Enter new PV name",
    				null, new PVNameValidator());
    		if (dlg.open() == InputDialog.OK)
                helper.getPVListModel().addPV(dlg.getValue());
    }
}
