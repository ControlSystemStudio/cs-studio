package org.csstudio.dct.ui.editor;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.UUID;

import org.csstudio.dct.model.IElement;
import org.csstudio.dct.ui.Activator;
import org.csstudio.dct.ui.editor.tables.ConvenienceTableWrapper;
import org.csstudio.dct.ui.editor.tables.ITableRow;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.csstudio.platform.ui.util.LayoutUtil;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;

/**
 * Base class for all editing forms. The class already prepares input element
 * that are common to all model parts and takes care of refreshes.
 * 
 * @author Sven Wende
 * 
 * @param <E>
 *            the type of element that is edited with a form
 */
public abstract class AbstractForm<E extends IElement> implements CommandStackListener {
	private Composite mainComposite;
	private E input;
	private Label headlineLabel;
	private ConvenienceTableWrapper commonTable;
	private Link link;
	private DctEditor editor;

	/**
	 * Constructor.
	 * 
	 * @param editor
	 *            a DCT editor instance
	 */
	public AbstractForm(DctEditor editor) {
		assert editor != null;
		assert editor.getCommandStack() != null;
		this.editor = editor;
		editor.getCommandStack().addCommandStackListener(this);
	}

	/**
	 * Creates the controls for this editing form.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	public final void createControl(Composite parent) {
		// .. main composite
		mainComposite = new Composite(parent, SWT.None);
		GridLayout layout = LayoutUtil.createGridLayout(1, 5, 5, 5);
		mainComposite.setLayout(layout);

		// .. headline label
		headlineLabel = new Label(mainComposite, SWT.NONE);
		headlineLabel.setFont(CustomMediaFactory.getInstance().getFont("Arial", 16, SWT.BOLD));
		headlineLabel.setText(doGetFormLabel());
		headlineLabel.setLayoutData(LayoutUtil.createGridData());

		// .. jump links
		link = new Link(mainComposite, SWT.NONE);
		link.setLayoutData(LayoutUtil.createGridDataForHorizontalFillingCell());
		link.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				UUID id = null;

				try {
					id = UUID.fromString(event.text);

					if (id != null) {
						editor.selectItemInOutline(id);
					}
				} catch (Exception e) {
					CentralLogger.getInstance().warn(this, e);
				}

			}
		});

		// .. create expand bar
		ExpandBar expandBar = new ExpandBar(mainComposite, SWT.V_SCROLL);
		expandBar.setLayoutData(LayoutUtil.createGridDataForFillingCell());
		expandBar.setSpacing(8);

		// create overview
		Composite composite = new Composite(expandBar, SWT.NONE);
		composite.setLayout(LayoutUtil.createGridLayout(1, 5, 8, 8));

		commonTable = WidgetUtil.createKeyColumErrorTable(composite, getCommandStack());
		commonTable.getViewer().getControl().setLayoutData(LayoutUtil.createGridDataForHorizontalFillingCell(100));

		ExpandItem expandItem = new ExpandItem(expandBar, SWT.NONE, 0);
		expandItem.setText("Common Settings");
		expandItem.setHeight(170);
		expandItem.setControl(composite);
		expandItem.setExpanded(true);
		expandItem.setImage(CustomMediaFactory.getInstance().getImageFromPlugin(Activator.PLUGIN_ID, "icons/tab_common.png"));

		// .. let subclasses add their own widgets
		doCreateControl(expandBar, getCommandStack());
	}

	/**
	 * Returns the command stack.
	 * @return the command stack
	 */
	public CommandStack getCommandStack() {
		return editor.getCommandStack();
	}

	/**
	 * Returns the main composite.
	 * 
	 * @return the main composite
	 */
	public final Composite getMainComposite() {
		return mainComposite;
	}

	/**
	 * Sets the input object for this editing form.
	 * 
	 * @param in
	 *            the current input element for the form
	 */
	@SuppressWarnings("unchecked")
	public final void setInput(Object in) {
		this.input = (E) in;

		// .. refresh links
		link.setText(doGetLinkText(input));

		// .. refresh headline label
		headlineLabel.setText(doGetFormLabel());

		// prepare input for overview table
		List<ITableRow> rows = new ArrayList<ITableRow>();
		rows.add(new BeanPropertyTableRowAdapter("Identifier", input, "id", true));
		rows.add(new NameTableRowAdapter(input));
		doAddCommonRows(rows, input);
		commonTable.setInput(rows);

		doSetInput(input);
	}
	
	/**
	 * Returns the input object for this editing form.
	 * 
	 * @return the input object
	 */
	public final E getInput() {
		return input;
	}

	/**
	 * Refreshes this form.
	 */
	public final void refresh() {
		if (input != null) {
			setInput(input);
		}
	}
	
	/**
	 *{@inheritDoc}
	 */
	public final void commandStackChanged(EventObject event) {
		refresh();
	}
	
	/**
	 * Template method. Subclasses return a label text for the form title here.
	 * 
	 * @return a label text for the form title
	 */
	protected abstract String doGetFormLabel();

	/**
	 * Template method. Subclasses can add table rows for the "common settings"
	 * table here.
	 * 
	 * @param rows
	 *            a list with table rows for the "common settings"
	 * @param input
	 *            the current input element for the form
	 */
	protected abstract void doAddCommonRows(List<ITableRow> rows, E input);


	/**
	 * Subclasses my provide a text, which will appear underneath the headline.
	 * The text can contain links in the following format
	 * 
	 * <code>
	 * 		<a href="${elementId}">link</a>
	 * </code>
	 * 
	 * @param input
	 *            the current input element for the form 
	 * @return a text with link that will appear underneath the headline
	 */
	protected abstract String doGetLinkText(E input);

	/**
	 * Templates method. Used by subclasses to prepare their widgets.
	 * 
	 * @param bar
	 *            the expand bar
	 * 
	 * @param commandStack
	 *            the command stack
	 */
	protected abstract void doCreateControl(ExpandBar bar, CommandStack commandStack);

	/**
	 * Template method that is called, when the input for this form changes.
	 * Subclasses should refresh their widgets when this method is called.
	 * 
	 * @param input
	 *            the current input object
	 */
	protected abstract void doSetInput(E input);



}
