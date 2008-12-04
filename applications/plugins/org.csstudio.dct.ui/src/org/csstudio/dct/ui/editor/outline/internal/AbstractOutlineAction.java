package org.csstudio.dct.ui.editor.outline.internal;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.internal.Project;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.views.contentoutline.ContentOutline;

/**
 * Base class for actions that are used in the outline´s popup menu.
 * 
 * @author Sven Wende
 * 
 */
public abstract class AbstractOutlineAction implements IViewActionDelegate {
	private IElement selectedElement;
	private ContentOutline outlineView;

	/**
	 * {@inheritDoc}
	 */
	public void init(IViewPart view) {
		outlineView = (ContentOutline) view;
	}

	/**
	 * {@inheritDoc}
	 */
	public void run(IAction action) {
		Command command = createCommand(selectedElement);
		execute(command);

		// select new elements if necessary
		if (command instanceof ISelectAfterExecution) {
			IElement element = ((ISelectAfterExecution) command).getElementToSelect();

			if (element != null) {
				outlineView.setSelection(new StructuredSelection(element));
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;

			Object s = ssel.getFirstElement();

			if (s instanceof IElement) {
				selectedElement = (IElement) s;
			} else {
				selectedElement = null;
			}
		}
	}

	protected abstract Command createCommand(IElement selection);

	protected Project getProject() {
		Project result = null;
		IPage currentPage = outlineView.getCurrentPage();

		if (currentPage instanceof OutlinePage) {
			result = ((OutlinePage) currentPage).getInput();
		}

		return result;
	}
	
	/**
	 * Executes the specified command.
	 * 
	 * @param command
	 *            a command
	 */
	private void execute(Command command) {
		CommandStack commandStack = getCommandStack();
		if (commandStack != null) {
			commandStack.execute(command);
		} else {
			throw new IllegalArgumentException("Could not execute command. No command stack available");
		}
	}

	/**
	 * Returns the command stack.
	 * 
	 * @return the central command stack
	 */
	private CommandStack getCommandStack() {
		CommandStack result = null;

		IPage currentPage = outlineView.getCurrentPage();

		if (currentPage instanceof OutlinePage) {
			result = ((OutlinePage) currentPage).getCommandStack();
		}

		return result;
	}
}
