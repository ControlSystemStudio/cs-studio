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
public class CopyOfCreateGroupAction extends SelectionAction implements IObjectActionDelegate {

	
	/**
	 * @param part the OPI Editor
	 * @param pasteWidgetsAction pass the paste action will 
	 * help to update the enable state of the paste action
	 * after copy action invoked.
	 */
	public CopyOfCreateGroupAction(OPIEditor part) {
		super(part);
		setText("Create Group");
		setId("org.csstudio.opibuilder.actions.createGroup");
	}

	@Override
	protected boolean calculateEnabled() {
		if(getSelectedObjects().size() == 0 || 
				(getSelectedObjects().size() == 1 && 
						getSelectedObjects().get(0) instanceof DisplayEditpart)
						|| getSelectedWidgetModels().size() <=0)
			return false;
		
		//if the selected widgets don't have the same parent, the action should be disabled.
		AbstractWidgetModel parent = null;
		for(AbstractWidgetModel child : getSelectedWidgetModels()){
			if(parent != null && parent != child.getParent())
				return false;
			parent = child.getParent();
		}
		return true;
	}
	
	
	@Override
	public void run() {
		CompoundCommand compoundCommand = new CompoundCommand("Create Group");
		
		for(AbstractWidgetModel widget : getSelectedWidgetModels()){
			compoundCommand.add(new OrphanChildCommand(widget.getParent(), widget));
		}
		
		
		
		//compoundCommand.add(new AddWidgetCommand())
		
		
	}
	
	/**
	 * Gets the widget models of all currently selected EditParts.
	 * 
	 * @return a list with all widget models that are currently selected
	 */
	@SuppressWarnings("unchecked")
	protected final List<AbstractWidgetModel> getSelectedWidgetModels() {
		List selection = getSelectedObjects();
	
		List<AbstractWidgetModel> selectedWidgetModels = new ArrayList<AbstractWidgetModel>();
	
		for (Object o : selection) {
			if (o instanceof AbstractBaseEditPart) {
				selectedWidgetModels.add(((AbstractBaseEditPart) o)
						.getCastedModel());
			}
		}
		return selectedWidgetModels;
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
			selection = (IStructuredSelection) selection;
		}
	}

}
