package org.csstudio.alarm.beast.ui.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.csstudio.alarm.beast.ui.Activator;
import org.csstudio.alarm.beast.ui.Messages;
import org.csstudio.ui.util.swt.stringtable.RowEditDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class OldEditAAItemDialog extends RowEditDialog
{
	private Combo titleCombo;
	private TableViewer tableViewer;
	
	private String currentSelec = ""; // default selection
	private List<String> currentInput = null;
	
	private static final String SEPARATOR = ","; //$NON-NLS-1$
	private static final String ADD = "add"; //$NON-NLS-1$
	private static final String DELETE = "delete"; //$NON-NLS-1$
	private final static ImageRegistry images = new ImageRegistry();
	private Button addButton;
	private Button deleteButton;

	static {
		// Buttons: add/delete
		images.put(ADD, Activator.getImageDescriptor("icons/add.gif")); //$NON-NLS-1$
		images.put(DELETE, Activator.getImageDescriptor("icons/delete.gif")); //$NON-NLS-1$
	}
	
	protected OldEditAAItemDialog(final Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite parent_composite = (Composite) super.createDialogArea(parent);
		final Composite composite = new Composite(parent_composite, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(2, false));

		final Label titleLable = new Label(composite, 0);
		titleLable.setText(Messages.EditAAItemDialog_Title);
		titleLable.setLayoutData(new GridData());

		titleCombo = new Combo(composite, SWT.BORDER | SWT.SINGLE);
//		titleCombo.add(Messages.EditAAItemDialog_SendMail);
//		titleCombo.add(Messages.EditAAItemDialog_SendSMS);
//		titleCombo.add(Messages.EditAAItemDialog_PhoneCall);
		int actionIndex = titleCombo.indexOf(currentSelec);
		if (actionIndex != -1) {
			titleCombo.select(actionIndex);
		}
		titleCombo.setEnabled(false);
		titleCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		addButton = createAddButton(composite);
		addButton.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, false, 1, 1));

		final Composite table_parent = new Composite(composite, 0);
		final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 4);
		gd.widthHint = 310;
		gd.heightHint = 100;
		table_parent.setLayoutData(gd);
		final TableColumnLayout table_layout = new TableColumnLayout();
		table_parent.setLayout(table_layout);

		tableViewer = new TableViewer(table_parent, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		final Table table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(false);

		// Create edit-able column
		final TableViewerColumn view_col = new TableViewerColumn(tableViewer, 0);
		final TableColumn col = view_col.getColumn();
		col.setResizable(true);
		table_layout.setColumnData(col, new ColumnWeightData(100, 300));
		view_col.setLabelProvider(new StringColumnLabelProvider(tableViewer));
		view_col.setEditingSupport(new StringColumnEditor(tableViewer));
		tableViewer.setContentProvider(new StringTableContentProvider<String>());
		if (currentInput == null) {
			currentInput = new ArrayList<String>();
		}
		tableViewer.setInput(currentInput);
		// Enable buttons when items are selected
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
		    public void selectionChanged(SelectionChangedEvent event) {
				setButtonsEnable();
			}
		});

		deleteButton = createDeleteButton(composite);
		deleteButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));

		return parent_composite;
	}
    
	private Button createAddButton(Composite parent) {
		final Button add = new Button(parent, SWT.PUSH);
		add.setImage(images.get(ADD));
		add.setToolTipText(Messages.StringTableEditor_AddToolTip);
		add.setLayoutData(new GridData());
		add.setEnabled(true);
		add.addSelectionListener(new SelectionAdapter() {
			@Override
			@SuppressWarnings("unchecked")
			public void widgetSelected(SelectionEvent e) {
				final List<String> items = (List<String>) tableViewer.getInput();
				items.add(Messages.StringTableEditor_AddRowText);
				tableViewer.refresh();
				tableViewer.getTable().setSelection(items.size() - 1);
			}
		});
		return add;
	}

	private Button createDeleteButton(Composite parent) {
		final Button delete = new Button(parent, SWT.PUSH);
		delete.setImage(images.get(DELETE));
		delete.setToolTipText(Messages.StringTableEditor_DeleteToolTip);
		delete.setLayoutData(new GridData());
		delete.setEnabled(false);
		delete.addSelectionListener(new SelectionAdapter() {
			@Override
			@SuppressWarnings("unchecked")
			public void widgetSelected(SelectionEvent e) {
				final List<String> items = (List<String>) tableViewer.getInput();
				final Object sel[] = ((IStructuredSelection) tableViewer
						.getSelection()).toArray();
				int adjust = 0;
				for (Object s : sel) {
					final int index = (Integer) s;
					items.remove(index - adjust);
					// What used to be index N is now N-1...
					++adjust;
				}
				tableViewer.refresh();
			}
		});
		return delete;
	}
	
	public void setEnabled(boolean enabled) {
		if (enabled) {
			tableViewer.getTable().setEnabled(enabled);
			setButtonsEnable();
		}
	}

	private void setButtonsEnable() {
		final IStructuredSelection sel = (IStructuredSelection) tableViewer
				.getSelection();
		final int count = sel.size();
		deleteButton.setEnabled(count > 0);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void okPressed() {
		rowData[0] = titleCombo == null ? "" : titleCombo.getItem(
				titleCombo.getSelectionIndex()).trim();
		rowData[1] = "";
		if (tableViewer != null) {
			StringBuilder sb = new StringBuilder();
			List<String> input = (List<String>) tableViewer.getInput();
			for (int index = 0; index < input.size(); index++) {
				String str = input.get(index);
				if (!Messages.StringTableEditor_AddRowText.equals(str)) {
					sb.append(str.trim());
					if (index < input.size() - 1) {
						sb.append(SEPARATOR);
					}
				}
			}
			rowData[1] = sb.toString();
		}
		super.okPressed();
	}
    
	@Override
	public void setRowData(final String[] rowData) {
		super.setRowData(rowData);
		this.currentSelec = rowData[0];
		currentInput = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(rowData[1], SEPARATOR);
		while (st.hasMoreElements()) {
			currentInput.add(st.nextToken());
		}
	}
	
	private static class StringTableContentProvider<T> implements IStructuredContentProvider {
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
	
	private static class StringColumnEditor extends EditingSupport {
		final private TableViewer table_viewer;

		public StringColumnEditor(final TableViewer viewer) {
			super(viewer);
			this.table_viewer = viewer;
		}

		@Override
		protected boolean canEdit(final Object element) {
			return true;
		}

		@Override
		protected CellEditor getCellEditor(final Object element) {
			final Table parent = (Table) getViewer().getControl();
			return new TextCellEditor(parent);
		}

		@Override
		protected Object getValue(Object element) {
			final int index = ((Integer) element).intValue();
			@SuppressWarnings("unchecked")
			final List<String> items = (List<String>) table_viewer.getInput();
			return items.get(index);
		}

		@Override
		protected void setValue(Object element, Object value) {
			@SuppressWarnings("unchecked")
			final List<String> items = (List<String>) table_viewer.getInput();
			final int index = ((Integer) element).intValue();
			items.set(index, value.toString());
			getViewer().refresh(element);
		}
	}
	
	private static class StringColumnLabelProvider extends CellLabelProvider {
		final private TableViewer viewer;

		public StringColumnLabelProvider(final TableViewer viewer) {
			this.viewer = viewer;
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public void update(final ViewerCell cell) {
			final List<String> items = (List<String>) viewer.getInput();
			final int index = ((Integer) cell.getElement()).intValue();
			if (index < 0) {
				cell.setText("");
			} else {
				cell.setText(items.get(index));
			}
		}
	}
}
