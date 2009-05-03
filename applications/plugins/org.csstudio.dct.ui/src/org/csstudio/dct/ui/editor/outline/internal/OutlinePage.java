package org.csstudio.dct.ui.editor.outline.internal;

import java.util.EventObject;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IProject;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.model.commands.AddRecordCommand;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
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

		initDragAndDrop(viewer);

		viewer.getControl().setMenu(menuManager.createContextMenu(viewer.getControl()));
		getSite().registerContextMenu("css.dct.outline", menuManager, viewer);
	}

	private IElement selectedElement;

	private void initDragAndDrop(final TreeViewer viewer) {

		viewer.addDragSupport(DND.DROP_MOVE | DND.DROP_COPY, new Transfer[] { TextTransfer.getInstance() }, new DragSourceListener() {
			public void dragFinished(DragSourceEvent event) {

			}

			public void dragSetData(DragSourceEvent event) {
				event.doit = true;
				event.data = selectedElement.getName()!=null?selectedElement.getName():"a";

			}

			public void dragStart(DragSourceEvent event) {
				IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();

				if (sel != null) {
					selectedElement = (IElement) sel.getFirstElement();
				}

				event.doit = selectedElement != null;
			}

		});

		viewer.addDropSupport(DND.DROP_MOVE | DND.DROP_COPY, new Transfer[] { TextTransfer.getInstance() }, new DropTargetListener() {

			public void dragEnter(DropTargetEvent event) {

			}

			public void dragLeave(DropTargetEvent event) {

			}

			public void dragOperationChanged(DropTargetEvent event) {

			}

			public void dragOver(DropTargetEvent event) {
				TreeItem item = (TreeItem) event.item;

				event.detail = DND.DROP_NONE;
				event.feedback = DND.FEEDBACK_NONE;

				if (item != null) {
					IElement element = (IElement) item.getData();

					if (element != null) {
						if (element instanceof IContainer) {
							event.detail = DND.DROP_DEFAULT;
							event.feedback = DND.FEEDBACK_SELECT;
						}
					}
				}

			}

			public void drop(DropTargetEvent event) {
				TreeItem item = (TreeItem) event.item;

				if (item != null) {
					IElement element = (IElement) item.getData();

					if (element != null) {
						if (element instanceof IContainer) {
							AddRecordCommand cmd = new AddRecordCommand((IContainer) element, (IRecord) selectedElement);
							getCommandStack().execute(cmd);
						}
					}
				}
			}

			public void dropAccept(DropTargetEvent event) {
				event.detail = DND.DROP_COPY;
			}

		});

		// int operations = DND.DROP_MOVE | DND.DROP_COPY;
		//
		// final DragSource source = new DragSource(tree, operations);
		// source.setTransfer(types);
		//
		// final TreeItem[] dragSourceItem = new TreeItem[1];
		//
		// source.addDragListener(new DragSourceListener() {
		// public void dragStart(DragSourceEvent event) {
		// TreeItem[] selection = tree.getSelection();
		// if (selection.length > 0 && selection[0].getItemCount() == 0) {
		// event.doit = true;
		// dragSourceItem[0] = selection[0];
		// } else {
		// event.doit = false;
		// }
		// };
		//
		// public void dragSetData(DragSourceEvent event) {
		// event.data = dragSourceItem[0].getText();
		// }
		//
		// public void dragFinished(DragSourceEvent event) {
		// if (event.detail == DND.DROP_MOVE) {
		// dragSourceItem[0].dispose();
		// }
		// dragSourceItem[0] = null;
		// }
		// });
		//
		// DropTarget target = new DropTarget(tree, operations);
		// target.setTransfer(types);
		// target.addDropListener(new DropTargetAdapter() {
		// public void dragOver(DropTargetEvent event) {
		// event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
		// if (event.item != null) {
		// TreeItem item = (TreeItem) event.item;
		// Point pt = display.map(null, tree, event.x, event.y);
		// Rectangle bounds = item.getBounds();
		// if (pt.y < bounds.y + bounds.height / 3) {
		// event.feedback |= DND.FEEDBACK_INSERT_BEFORE;
		// } else if (pt.y > bounds.y + 2 * bounds.height / 3) {
		// event.feedback |= DND.FEEDBACK_INSERT_AFTER;
		// } else {
		// event.feedback |= DND.FEEDBACK_SELECT;
		// }
		// }
		// }
		//
		// public void drop(DropTargetEvent event) {
		// if (event.data == null) {
		// event.detail = DND.DROP_NONE;
		// return;
		// }
		// String text = (String) event.data;
		// if (event.item == null) {
		// TreeItem item = new TreeItem(tree, SWT.NONE);
		// item.setText(text);
		// } else {
		// TreeItem item = (TreeItem) event.item;
		// Point pt = display.map(null, tree, event.x, event.y);
		// Rectangle bounds = item.getBounds();
		// TreeItem parent = item.getParentItem();
		// if (parent != null) {
		// TreeItem[] items = parent.getItems();
		// int index = 0;
		// for (int i = 0; i < items.length; i++) {
		// if (items[i] == item) {
		// index = i;
		// break;
		// }
		// }
		// if (pt.y < bounds.y + bounds.height / 3) {
		// TreeItem newItem = new TreeItem(parent, SWT.NONE, index);
		// newItem.setText(text);
		// } else if (pt.y > bounds.y + 2 * bounds.height / 3) {
		// TreeItem newItem = new TreeItem(parent, SWT.NONE, index + 1);
		// newItem.setText(text);
		// } else {
		// TreeItem newItem = new TreeItem(item, SWT.NONE);
		// newItem.setText(text);
		// }
		//
		// } else {
		// TreeItem[] items = tree.getItems();
		// int index = 0;
		// for (int i = 0; i < items.length; i++) {
		// if (items[i] == item) {
		// index = i;
		// break;
		// }
		// }
		// if (pt.y < bounds.y + bounds.height / 3) {
		// TreeItem newItem = new TreeItem(tree, SWT.NONE, index);
		// newItem.setText(text);
		// } else if (pt.y > bounds.y + 2 * bounds.height / 3) {
		// TreeItem newItem = new TreeItem(tree, SWT.NONE, index + 1);
		// newItem.setText(text);
		// } else {
		// TreeItem newItem = new TreeItem(item, SWT.NONE);
		// newItem.setText(text);
		// }
		// }
		//
		// }
		// }
		// });
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
