package org.csstudio.dct.ui.editor.tables;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;

public abstract class BaseTable<E> {
	private E input;

	private TableViewer viewer;
	
	private CommandStack commandStack;

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            a widget which will be the parent of the new instance (cannot
	 *            be null)
	 * @param style
	 *            the style of widget to construct
	 */
	public BaseTable(Composite parent, int style, CommandStack commandStack) {
		assert commandStack != null;
		this.commandStack = commandStack;
		viewer = doCreateViewer(parent, style);
	}

	public final void setInput(E input) {
		this.input = input;

		if (input != null) {
			viewer.setInput(getViewerInput(this.input));
			viewer.refresh();
		}
	}

	public CommandStack getCommandStack() {
		return commandStack;
	}
	
	public E getInput() {
		return input;
	}
	
	public TableViewer getViewer() {
		return viewer;
	}

	/**
	 * Template method. Subclasses should create the table viewer here.
	 * 
	 * @param parent
	 *            a widget which will be the parent of the new instance (cannot
	 *            be null)
	 * @param style
	 *            the style of widget to construct
	 * @return the table viewer
	 */
	protected abstract TableViewer doCreateViewer(Composite parent, int style);

	protected abstract Object getViewerInput(E input);

}
