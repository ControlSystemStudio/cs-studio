package org.csstudio.alarm.beast.ui.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.csstudio.alarm.beast.ui.Messages;
import org.csstudio.ui.util.Activator;
import org.csstudio.ui.util.swt.stringtable.RowEditDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/** Adapted editor for automated actions table with String[] entries (2 colums),
 *  allows up/down ordering, edit and delete
 *  @author Sopra Group
 */
public class ActionTableEditor extends Composite {

	private static final String DELETE = "delete"; //$NON-NLS-1$
	private static final String DOWN = "down"; //$NON-NLS-1$
	private static final String UP = "up"; //$NON-NLS-1$
	private static final String EDIT = "edit"; //$NON-NLS-1$
	private final TableViewer tableViewer;
	private final static ImageRegistry images = new ImageRegistry();
	private Button editButton;
	private Button upButton;
	private Button downButton;
	// private Button deleteButton;

	static {
		// Buttons: edit/up/down/delete
		images.put(EDIT, Activator.getImageDescriptor("icons/edit.gif")); //$NON-NLS-1$
		images.put(UP, Activator.getImageDescriptor("icons/up.gif")); //$NON-NLS-1$
		images.put(DOWN, Activator.getImageDescriptor("icons/down.gif")); //$NON-NLS-1$
		// images.put(DELETE, Activator.getImageDescriptor("icons/delete.gif")); //$NON-NLS-1$
	}

	public ActionTableEditor(final Composite parent, final String[] headers,
			final List<String[]> items, final RowEditDialog rowEditDialog,
			final int[] columnsMinWidth) {
		super(parent, 0);
		final int table_columns = headers.length;
		setLayout(new GridLayout(2, false));

		//Edit-able Table in its own Composite for TableColumnLayout
		final Composite table_parent = new Composite(this, 0);
		final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 4);
		gd.heightHint = 150;
		table_parent.setLayoutData(gd);
		final TableColumnLayout table_layout = new TableColumnLayout();
		table_parent.setLayout(table_layout);

		tableViewer = new TableViewer(table_parent, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		final Table table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		//Create non edit-able columns for automated actions table
		final TableViewerColumn view_col1 = new TableViewerColumn(tableViewer, 0);
		final TableColumn col1 = view_col1.getColumn();
		col1.setText(headers[0]);
		col1.setMoveable(true);
		col1.setResizable(true);
		table_layout.setColumnData(col1, new ColumnWeightData(100, columnsMinWidth[0]));
		view_col1.setLabelProvider(new StringMultiColumnsLabelProvider(tableViewer));

		final TableViewerColumn view_col2 = new TableViewerColumn(tableViewer, 0);
		final TableColumn col2 = view_col2.getColumn();
		col2.setText(headers[1]);
		col2.setMoveable(true);
		col2.setResizable(true);
		table_layout.setColumnData(col2, new ColumnWeightData(100, columnsMinWidth[1]));
		view_col2.setLabelProvider(new StringMultiColumnsLabelProvider(tableViewer));
		
		tableViewer.setContentProvider(new ActionTableContentProvider<String[]>());
		updateInput(items);
		if (rowEditDialog != null) {
			editButton = createEditButton(table_columns, rowEditDialog);
		}
		upButton = createUpButton();
		downButton = createDownButton();
		// deleteButton = createDeleteButton();

		// Enable buttons when items are selected
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setButtonsEnable();
			}
		});
	}

	public void updateInput(List<String[]> new_items) {
		List<String[]> tmpItems = new ArrayList<String[]>();
//		String[] mailData = new String[2];
//		mailData[0] = Messages.EditAAItemDialog_SendMail;
//		mailData[1] = "";
//		String[] smsData = new String[2];
//		smsData[0] = Messages.EditAAItemDialog_SendSMS;
//		smsData[1] = "";
//		String[] phoneData = new String[2];
//		phoneData[0] = Messages.EditAAItemDialog_PhoneCall;
//		phoneData[1] = "";
//		for (String[] row : new_items) {
//			if (Messages.EditAAItemDialog_SendMail.equals(row[0])) {
//				mailData[1] = row[1];
//			}
//			if (Messages.EditAAItemDialog_SendSMS.equals(row[0])) {
//				smsData[1] = row[1];
//			}
//			if (Messages.EditAAItemDialog_PhoneCall.equals(row[0])) {
//				phoneData[1] = row[1];
//			}
//		}
//		tmpItems.add(mailData);
//		tmpItems.add(smsData);
//		tmpItems.add(phoneData);
		new_items.clear();
		new_items.addAll(tmpItems);
		tableViewer.setInput(new_items);
	}

	/** Refresh the editor after the list of items was changed */
	public void refresh() {
		tableViewer.refresh();
	}

	private Button createEditButton(final int numColumns,
			final RowEditDialog rowEditDialog) {
		final Button edit = new Button(this, SWT.PUSH);
		edit.setImage(images.get(EDIT));
		edit.setToolTipText(Messages.StringTableEditor_EditToolTip);
		edit.setLayoutData(new GridData());
		edit.setEnabled(false);
		edit.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				final List<String[]> items = (List<String[]>) tableViewer.getInput();
				final Integer index = (Integer) ((IStructuredSelection) tableViewer
						.getSelection()).getFirstElement();

				if (index == ActionTableContentProvider.ADD_ELEMENT) { // empty row
					String[] emptyData = new String[numColumns];
					Arrays.fill(emptyData, ""); //$NON-NLS-1$
					rowEditDialog.setRowData(emptyData);
				} else {
					rowEditDialog.setRowData(items.get(index));
				}

				if (rowEditDialog.open() != Window.OK)
					return;

				if (index == ActionTableContentProvider.ADD_ELEMENT)
					items.add(rowEditDialog.getRowData());
				else {
					items.set(index, rowEditDialog.getRowData());
				}
				tableViewer.refresh();
			}
		});
		return edit;
	}

	private Button createUpButton() {
		final Button up = new Button(this, SWT.PUSH);
		up.setImage(images.get(UP));
		up.setToolTipText(Messages.StringTableEditor_MoveUpToolTip);
		up.setLayoutData(new GridData());
		up.setEnabled(false);
		up.addSelectionListener(new SelectionAdapter() {
			@Override
			@SuppressWarnings("unchecked")
			public void widgetSelected(SelectionEvent e) {
				final List<String[]> items = (List<String[]>) tableViewer.getInput();
				final Integer index = (Integer) ((IStructuredSelection) tableViewer
						.getSelection()).getFirstElement();
				if (index == (int) ActionTableContentProvider.ADD_ELEMENT
						|| index < 1)
					return;
				items.add(index - 1, items.get(index));
				items.remove(index + 1);
				tableViewer.refresh();
				tableViewer.getTable().setSelection(index - 1);
			}
		});
		return up;
	}

	private Button createDownButton() {
		final Button down = new Button(this, SWT.PUSH);
		down.setImage(images.get(DOWN));
		down.setToolTipText(Messages.StringTableEditor_MoveDownToolTip);
		down.setLayoutData(new GridData());
		down.setEnabled(false);
		down.addSelectionListener(new SelectionAdapter() {
			@Override
			@SuppressWarnings("unchecked")
			public void widgetSelected(SelectionEvent e) {
				final List<String[]> items = (List<String[]>) tableViewer.getInput();
				final int index = (Integer) ((IStructuredSelection) tableViewer
						.getSelection()).getFirstElement();
				if (index == (int) ActionTableContentProvider.ADD_ELEMENT
						|| index >= items.size() - 1)
					return;
				items.add(index + 2, items.get(index));
				items.remove(index);
				tableViewer.refresh();
				tableViewer.getTable().setSelection(index + 1);
			}
		});
		return down;
	}

	@SuppressWarnings("unused")
	private Button createDeleteButton() {
		final Button delete = new Button(this, SWT.PUSH);
		delete.setImage(images.get(DELETE));
		delete.setToolTipText(Messages.StringTableEditor_DeleteToolTip);
		delete.setLayoutData(new GridData());
		delete.setEnabled(false);
		delete.addSelectionListener(new SelectionAdapter() {
			@Override
			@SuppressWarnings("unchecked")
			public void widgetSelected(SelectionEvent e) {
				final List<String[]> items = (List<String[]>) tableViewer.getInput();
				final Object sel[] = ((IStructuredSelection) tableViewer
						.getSelection()).toArray();
				for (Object s : sel) {
					final int index = (Integer) s;
					((String[]) items.get(index))[1] = "";
				}
				tableViewer.refresh();
			}
		});
		return delete;
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (enabled) {
			tableViewer.getTable().setEnabled(enabled);
			setButtonsEnable();
		} else
			for (Control control : this.getChildren())
				control.setEnabled(enabled);
	}

	private void setButtonsEnable() {
		final IStructuredSelection sel = (IStructuredSelection) tableViewer
				.getSelection();
		final int count = sel.size();
		if (editButton != null)
			editButton.setEnabled(count == 1);
		upButton.setEnabled(count == 1);
		downButton.setEnabled(count == 1);
		// deleteButton.setEnabled(count > 0);
	}

	public TableViewer getTableViewer() {
		return tableViewer;
	}
	
	private static class ActionTableContentProvider<T> implements IStructuredContentProvider {
		/** Magic number for the final 'add' element */
		final public static Integer ADD_ELEMENT = new Integer(-1);
		private List<T> items;

		/** {@inheritDoc} */
		@Override
		@SuppressWarnings("unchecked")
		public void inputChanged(final Viewer viewer, final Object old,
				final Object new_input) {
			items = (List<T>) new_input;
		}
		
		/** {@inheritDoc} */
		@Override
		public Object[] getElements(Object arg0) {
			int N = items.size();
			final Integer result[] = new Integer[N];
			for (int i = 0; i < N; ++i) {
				result[i] = i;
			}
			return result;
		}
		
		/** {@inheritDoc} */
		@Override
		public void dispose() { }
	}
	
	private static class StringMultiColumnsLabelProvider extends CellLabelProvider {
		final private TableViewer tableViewer;

		public StringMultiColumnsLabelProvider(final TableViewer tableViewer) {
			super();
			this.tableViewer = tableViewer;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void update(ViewerCell cell) {
			final List<String[]> items = (List<String[]>) tableViewer.getInput();
			final int index = ((Integer) cell.getElement()).intValue();
			// if this is the extra row
			if (index < 0) {
				cell.setText(""); //$NON-NLS-1$
			} else {
				final int column = cell.getColumnIndex();
				String text = items.get(index)[column];
				// Not sure whether to look for '\r' or '\n'. Try both
				int nl = text.indexOf('\r');
				if (nl < 0) nl = text.indexOf('\n');
				if (nl > 0) text = text.substring(0, nl) + "..."; //$NON-NLS-1$
				cell.setText(text);
			}
		}
	}
}
