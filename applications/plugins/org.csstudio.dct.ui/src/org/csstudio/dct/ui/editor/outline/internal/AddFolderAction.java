package org.csstudio.dct.ui.editor.outline.internal;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IFolder;
import org.csstudio.dct.model.commands.AddFolderCommand;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.ui.PlatformUI;

public class AddFolderAction extends AbstractOutlineAction {

	public AddFolderAction()  {
	}

	@Override
	protected Command createCommand(IElement selection) {
		Command result = null;

		if (selection instanceof IFolder) {
			InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Enter Folder Name",
					"Please enter a name for the new folder:", "", new IInputValidator() {
						public String isValid(String newText) {
							return null;
						}
					});
			
			if(dialog.open()==InputDialog.OK){
				String name = dialog.getValue();
				result = new AddFolderCommand((IFolder) selection, name);
			}
		}

		return result;
	}

}
