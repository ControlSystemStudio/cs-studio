package org.csstudio.opibuilder.widgets.editparts;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.commands.AddWidgetCommand;
import org.csstudio.opibuilder.commands.OrphanChildCommand;
import org.csstudio.opibuilder.commands.WidgetCreateCommand;
import org.csstudio.opibuilder.editor.OPIEditor;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.DisplayEditpart;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;

/**The action will create a group which contains all the selected widgets.
 * @author Xihui Chen
 *
 */
public class CreateGroupAction implements IObjectActionDelegate {

	
	public CreateGroupAction() {
		// TODO Auto-generated constructor stub
	}

	private IWorkbenchPart targetPart;
	/**
	 * The current selection.
	 */
	private IStructuredSelection selection;
	
	
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}

	public void run(IAction action) {
		MessageDialog.openInformation(targetPart.getSite().getShell(), "heeloo", 
				selection.getFirstElement().toString());
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;
		}
	}

}
