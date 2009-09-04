package org.csstudio.opibuilder.widgets.editparts;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.opibuilder.commands.AddWidgetCommand;
import org.csstudio.opibuilder.commands.OrphanChildCommand;
import org.csstudio.opibuilder.commands.SetBoundsCommand;
import org.csstudio.opibuilder.commands.WidgetCreateCommand;
import org.csstudio.opibuilder.commands.WidgetDeleteCommand;
import org.csstudio.opibuilder.editor.OPIEditor;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.model.GroupingContainerModel;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**The action will remove a group and move all the selected widgets to the group's parent.
 * @author Xihui Chen
 *
 */
public class RemoveGroupAction implements IObjectActionDelegate {


	private IWorkbenchPart targetPart;
	/**
	 * The current selection.
	 */
	private IStructuredSelection selection;
	
	
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}

	public void run(IAction action) {
		CompoundCommand compoundCommand = new CompoundCommand("Remove Group");	
		
		GroupingContainerModel containerModel = getSelectedContainer();
		
		Point leftCorner = containerModel.getLocation();
		for(AbstractWidgetModel widget : containerModel.getChildren()){			
			compoundCommand.add(new OrphanChildCommand(containerModel, widget));
			compoundCommand.add(new AddWidgetCommand(containerModel.getParent(), widget));
			compoundCommand.add(new SetBoundsCommand(widget, 
					new Rectangle(widget.getLocation(), widget.getSize()).translate(leftCorner)));		
		}		
		compoundCommand.add(new WidgetDeleteCommand(containerModel.getParent(), containerModel));
		if(targetPart instanceof OPIEditor){
			execute(compoundCommand);
		}
	}

	
	/**
	 * Executes the given {@link Command} using the command stack.  The stack is obtained by
	 * calling {@link #getCommandStack()}, which uses <code>IAdapatable</code> to retrieve the
	 * stack from the workbench part.
	 * @param command the command to execute
	 */
	protected void execute(Command command) {
		if (command == null || !command.canExecute())
			return;
		getCommandStack().execute(command);
	}

	/**
	 * Returns the editor's command stack. This is done by asking the workbench part for its
	 * CommandStack via 
	 * {@link org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)}.
	 * @return the command stack
	 */
	protected CommandStack getCommandStack() {
		return (CommandStack)targetPart.getAdapter(CommandStack.class);
	}
	
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;
		}
	}
	
	/**
	 * Gets the widget models of all currently selected EditParts.
	 * 
	 * @return a list with all widget models that are currently selected
	 */
	protected final GroupingContainerModel getSelectedContainer() {
		return ((GroupingContainerEditPart)selection.getFirstElement()).getCastedModel();
	}

}
