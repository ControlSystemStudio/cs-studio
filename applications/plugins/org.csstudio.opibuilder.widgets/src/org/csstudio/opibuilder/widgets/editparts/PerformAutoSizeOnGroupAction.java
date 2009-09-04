package org.csstudio.opibuilder.widgets.editparts;

import org.csstudio.opibuilder.commands.SetBoundsCommand;
import org.csstudio.opibuilder.editor.OPIEditor;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.widgets.model.GroupingContainerModel;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
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

/**The action will auto size the group according its children.
 * @author Xihui Chen
 *
 */
public class PerformAutoSizeOnGroupAction implements IObjectActionDelegate {


	private IWorkbenchPart targetPart;
	/**
	 * The current selection.
	 */
	private IStructuredSelection selection;
	
	
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}

	public void run(IAction action) {
		CompoundCommand compoundCommand = new CompoundCommand("Perform AutoSize");	
		
		GroupingContainerModel containerModel = getContainerModel();
		IFigure figure = getContainerFigure();

		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, 
		maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
		
		for(AbstractWidgetModel widget : containerModel.getChildren()){
			int leftX = widget.getLocation().x;
			int upY = widget.getLocation().y;
			int rightX = widget.getLocation().x + widget.getSize().width;
			int bottomY = widget.getLocation().y + widget.getSize().height;
			if( leftX<minX)
				minX = leftX;
			if( upY < minY)
				minY = upY;
			if(rightX > maxX)
				maxX =rightX;
			if(bottomY > maxY)
				maxY = bottomY;	
			
	
		}
		Point tranlateSize = new Point(minX,minY);
		
		compoundCommand.add(new SetBoundsCommand(containerModel, 
				new Rectangle(containerModel.getLocation().translate(tranlateSize), new Dimension(
						maxX - minX + figure.getInsets().left + figure.getInsets().right,
						maxY - minY + figure.getInsets().top + figure.getInsets().bottom))));
		

		for(AbstractWidgetModel widget : containerModel.getChildren()){
			compoundCommand.add(new SetBoundsCommand(widget, new Rectangle(
					widget.getLocation().translate(tranlateSize.getNegated()), 
					widget.getSize())));
		}
		
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
	protected final GroupingContainerModel getContainerModel() {
		return ((GroupingContainerEditPart)selection.getFirstElement()).getCastedModel();
	}

	/**
	 * Gets the widget models of all currently selected EditParts.
	 * 
	 * @return a list with all widget models that are currently selected
	 */
	protected final IFigure getContainerFigure() {
		return ((GroupingContainerEditPart)selection.getFirstElement()).getFigure();
	}
		
		
		
}
