/**
 * 
 */
package org.csstudio.dct.ui.editor.outline.internal;

import java.util.EventObject;

import org.csstudio.dct.model.internal.Project;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

public final class OutlinePage extends ContentOutlinePage implements CommandStackListener {
	private Project input;
	private CommandStack commandStack;
	private TreeViewer viewer;

	public OutlinePage(Project input, CommandStack commandStack) {
		this.input = input;
		this.commandStack = commandStack;
	}

	public void setInput(Project input) {
		this.input = input;

		if (getTreeViewer() != null) {
			getTreeViewer().setInput(input);
		}
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		viewer = getTreeViewer();
		viewer.setLabelProvider(WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider());

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

	public void setCommandStack(CommandStack commandStack) {
		this.commandStack = commandStack;
		this.commandStack.addCommandStackListener(this);

	}

	public CommandStack getCommandStack() {
		return commandStack;
	}

	public Project getInput() {
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