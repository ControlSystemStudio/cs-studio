package org.csstudio.dct.ui.editor.outline.internal;

import java.util.EventObject;

import org.csstudio.dct.model.IProject;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.WorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

/**
 * An outline page implementation that displays a {@link IProject} in the
 * outline view using a tree.
 * 
 * @author Sven Wende
 * 
 */
public final class OutlinePage extends ContentOutlinePage implements CommandStackListener {
	private IProject input;
	private CommandStack commandStack;
	private TreeViewer viewer;

	/**
	 * Constructor.
	 * 
	 * @param input
	 *            the project to display
	 * @param commandStack
	 *            a command stack
	 */
	public OutlinePage(IProject input, CommandStack commandStack) {
		this.input = input;
		this.commandStack = commandStack;
	}

	/**
	 * Sets the input for the outline.
	 * 
	 * @param input
	 *            the project to display
	 */
	public void setInput(IProject input) {
		this.input = input;

		if (getTreeViewer() != null) {
			getTreeViewer().setInput(input);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		viewer = getTreeViewer();
		viewer.setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());
		viewer.setUseHashlookup(true);

		viewer.setContentProvider(new WorkbenchContentProvider());
		viewer.setAutoExpandLevel(TreeViewer.ALL_LEVELS);

		viewer.setInput(new WorkbenchAdapter() {
			public Object[] getChildren(Object o) {
				return new Object[] { input };
			}
		});

		MenuManager menuManager = new MenuManager();
		menuManager.add(new Separator("add.ext"));
		menuManager.add(new Separator("remove.ext"));
		menuManager.add(new Action("Refresh") {

			@Override
			public void run() {
				viewer.refresh();
			}

		});

		viewer.getControl().setMenu(menuManager.createContextMenu(viewer.getControl()));
		getSite().registerContextMenu("css.dct.outline", menuManager, viewer);
	}

	/**
	 * Sets the command stack.
	 * 
	 * @param commandStack
	 *            the command stack
	 */
	public void setCommandStack(CommandStack commandStack) {
		this.commandStack = commandStack;
		this.commandStack.addCommandStackListener(this);

	}

	/**
	 * Returns the command stack.
	 * 
	 * @return the command stack
	 */
	public CommandStack getCommandStack() {
		return commandStack;
	}

	/**
	 * Returns the current input.
	 * 
	 * @return the current project
	 */
	public IProject getInput() {
		return input;
	}

	/**
	 * {@inheritDoc}
	 */
	public void commandStackChanged(EventObject event) {
		if (getTreeViewer() != null) {
			getTreeViewer().refresh();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSelection(ISelection selection) {
		if (getTreeViewer() != null) {
			getTreeViewer().setSelection(selection, true);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		super.selectionChanged(event);

		IStructuredSelection sel = (IStructuredSelection) event.getSelection();

		if (sel != null && sel.getFirstElement() != null) {
			viewer.setExpandedState(sel.getFirstElement(), true);
			viewer.refresh(sel.getFirstElement(), false);
			System.out.println("XXXXXXXX" + sel.getFirstElement());
		}
	}
}
