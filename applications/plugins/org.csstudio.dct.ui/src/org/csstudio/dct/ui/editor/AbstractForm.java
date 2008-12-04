package org.csstudio.dct.ui.editor;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.model.IInstance;
import org.csstudio.dct.model.IPropertyContainer;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.ui.editor.tables.TableCitizenTable;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.platform.ui.util.LayoutUtil;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;

/**
 * Base class for editing forms for {@link IRecord}, {@link IInstance} and
 * {@link IPrototype}.
 * 
 * The class already prepares an input table for properties which are common to
 * all three mentioned model parts.
 * 
 * @author Sven Wende
 * 
 * @param <E>
 */
public abstract class AbstractForm<E extends IElement> implements CommandStackListener {
	private Composite mainComposite;
	private E input;
	private Label headlineLabel;
//	private TableCitizenTable propertyTable;
	private CommandStack commandStack;

	public AbstractForm(CommandStack commandStack) {
		assert commandStack != null;
		this.commandStack = commandStack;
		commandStack.addCommandStackListener(this);
	}

	/**
	 * Creates the controls for this editing form.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param commandStack
	 *            a command stack
	 */
	public void createControl(Composite parent) {
		// .. main composite
		mainComposite = new Composite(parent, SWT.None);
		GridLayout layout = LayoutUtil.createGridLayout(1, 15, 15, 15);
		mainComposite.setLayout(layout);

		// .. headline label
		headlineLabel = new Label(mainComposite, SWT.NONE);
		headlineLabel.setFont(CustomMediaFactory.getInstance().getFont("Arial", 16, SWT.BOLD));
		headlineLabel.setText("Edit " + input + ":");
		headlineLabel.setLayoutData(LayoutUtil.createGridData());

		// .. let subclasses add their own widgets
		doCreateControl(mainComposite, commandStack);
	}

	public CommandStack getCommandStack() {
		return commandStack;
	}

	/**
	 * Returns the main composite.
	 * 
	 * @return the main composite
	 */
	public Composite getMainComposite() {
		return mainComposite;
	}

	/**
	 * Sets the input object for this editing form.
	 * 
	 * @param input
	 *            the input object
	 */
	public void setInput(Object in) {
		this.input = (E) in;

		// .. refresh headline label
		headlineLabel.setText("Edit " + (this.input != null ? this.input.getId().toString() : "?") + ":");

		doSetInput(input);
	}

	/**
	 * Returns the input object for this editing form.
	 * 
	 * @return the input object
	 */
	public E getInput() {
		return input;
	}

	/**
	 * Refreshes this form.
	 */
	public void refresh() {
		E input = getInput();
		if (input != null) {
			setInput(input);
		}
	}

	/**
	 * Templates method. Used by subclasses to prepare their widgets.
	 * 
	 * @param parent
	 *            the parent composite
	 * 
	 * @param commandStack
	 *            the command stack
	 */
	protected abstract void doCreateControl(Composite parent, CommandStack commandStack);

	/**
	 * Template method that is called, when the input for this form changes.
	 * Subclasses should refresh their widgets when this method is called.
	 * 
	 * @param input
	 *            the current input object
	 */
	protected abstract void doSetInput(E input);

	/**
	 *{@inheritDoc}
	 */
	public void commandStackChanged(EventObject event) {
		refresh();
	}

}
