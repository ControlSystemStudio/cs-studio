/**
 * 
 */
package org.csstudio.display.pvmanager.pvtable.editors;

import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.pvmanager.ExpressionLanguage.latestValueOf;
import static org.epics.pvmanager.extra.ExpressionLanguage.group;

import java.util.List;
import java.util.logging.Logger;

import org.csstudio.csdata.ProcessVariableName;
import org.csstudio.display.pvmanager.pvtable.EmptyEditorInput;
import org.csstudio.display.pvmanager.pvtable.PVTableModel;
import org.csstudio.display.pvmanager.pvtable.PVTableModel.Item;
import org.csstudio.display.pvmanager.pvtable.PVTableModelListener;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVValueChangeListener;
import org.epics.pvmanager.data.SimpleValueFormat;
import org.epics.pvmanager.data.Util;
import org.epics.pvmanager.data.ValueFormat;
import org.epics.pvmanager.extra.DynamicGroup;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

/**
 * @author shroffk
 * 
 */
public class PVTableEditor extends EditorPart {

	private boolean editingDone = false;

	private class ContentProvider implements IStructuredContentProvider {

		private PVTableModelListener listener = new PVTableModelListener() {

			@Override
			public void dataChanged() {
				if (!tableViewer.isCellEditorActive() || editingDone) {
					tableViewer.refresh();
				}
			}
		};

		public Object[] getElements(Object inputElement) {
			return ((PVTableModel) inputElement).getItems();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (oldInput != null) {
				((PVTableModel) oldInput).removePVTableModelListener(listener);
			}
			if (newInput != null) {
				((PVTableModel) newInput).addPVTableModelListener(listener);
			}
		}
	}

	public static final String ID = PVTableEditor.class.getName();
	private static Logger logger = Logger.getLogger(ID);

	private Table table;
	private TableViewer tableViewer;

	private DynamicGroup group = group();
	private PV<List<Object>> pv = PVManager.read(group)
			.andNotify(SWTUtil.onSWTThread()).atHz(2);
	private final PVValueChangeListener pvListener = new PVValueChangeListener() {

		@Override
		public void pvValueChanged() {
			// TODO Auto-generated method stub
			PVTableModel model = (PVTableModel) tableViewer.getInput();
			if (model != null) {
				model.updateValues(pv.getValue(), group.lastExceptions());
			}
		}
	};

	public static PVTableEditor createPVTableEditor() {
		try {
			IWorkbench workbench = PlatformUI.getWorkbench();
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			IWorkbenchPage page = window.getActivePage();

			return (PVTableEditor) page.openEditor(new EmptyEditorInput(),
					PVTableEditor.ID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 */
	public PVTableEditor() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite,
	 * org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(input.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void dispose() {
		super.dispose();
		pv.removePVValueChangeListener(pvListener);
		// TODO Clear the group
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		tableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION
				| SWT.MULTI | SWT.VIRTUAL);
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableViewerColumn tableViewerColumn = new TableViewerColumn(
				tableViewer, SWT.NONE);
		tableViewerColumn.setEditingSupport(new EditingSupport(tableViewer) {
			protected boolean canEdit(Object element) {
				return true;
			}

			protected CellEditor getCellEditor(Object element) {
				return new TextCellEditor(table);
			}

			protected Object getValue(Object element) {
				Item item = (Item) element;
				if (item == null || item.getProcessVariableName() == null)
					return "";
				return item.getProcessVariableName().getProcessVariableName();
			}

			protected void setValue(Object element, Object value) {
				try {
					editingDone = true;
					Item item = (Item) element;
					PVTableModel model = (PVTableModel) tableViewer.getInput();
					if (item != null && model != null) {
						int oldSize = model.getItems().length - 1;
						if (value == null || value.toString().trim().isEmpty()) {
							// We are removing the row
							if (item.getRow() == oldSize) {
								// Do nothing: the row was the empty one anyway
							} else {
								group.remove(item.getRow());
							}
							model.removeItem(item);
							model.updateValues(group.lastExceptions());
						} else {
							// We are updating the row
							model.updatePVName(item, new ProcessVariableName(
									value.toString()));
							if (item.getRow() == oldSize) {
								group.add(latestValueOf(channel(value
										.toString())));
							} else {
								group.set(
										item.getRow(),
										latestValueOf(channel(value.toString())));
							}
							model.updateValues(group.lastExceptions());
						}
					}
				} finally {
					editingDone = false;
				}
			}
		});
		tableViewerColumn.setLabelProvider(new PVColumnLabelProvider() {

			public Image getImage(Object element) {
				return null;
			}

			public String getText(Object element) {
				Item item = (Item) element;
				if (item == null || item.getProcessVariableName() == null)
					return null;
				return item.getProcessVariableName().getProcessVariableName();
			}
		});
		TableColumn tblclmnPvName = tableViewerColumn.getColumn();
		tblclmnPvName.setText("PV Name");

		TableViewerColumn tableViewerColumn_1 = new TableViewerColumn(
				tableViewer, SWT.NONE);
		tableViewerColumn_1.setLabelProvider(new PVColumnLabelProvider() {
			private ValueFormat format = new SimpleValueFormat(3);

			public Image getImage(Object element) {
				return null;
			}

			public String getText(Object element) {
				Item item = (Item) element;
				if (item == null || item.getValue() == null)
					return null;
				return format.format(item.getValue());
			}
		});
		TableColumn tblclmnValue = tableViewerColumn_1.getColumn();
		tblclmnValue.setText("Value");

		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(
				tableViewer, SWT.NONE);
		tableViewerColumn_2.setLabelProvider(new PVColumnLabelProvider() {
			public Image getImage(Object element) {
				return null;
			}

			public String getText(Object element) {
				Item item = (Item) element;
				if (item == null || item.getValue() == null)
					return null;
				return Util.alarmOf(item.getValue()).getAlarmSeverity()
						.toString();
			}
		});
		TableColumn tblclmnAlarm = tableViewerColumn_2.getColumn();
		tblclmnAlarm.setText("Alarm");

		TableViewerColumn tableViewerColumn_3 = new TableViewerColumn(
				tableViewer, SWT.NONE);
		tableViewerColumn_3.setLabelProvider(new PVColumnLabelProvider() {
			public Image getImage(Object element) {
				return null;
			}

			public String getText(Object element) {
				Item item = (Item) element;
				if (item == null || item.getValue() == null)
					return null;
				return Util.timeOf(item.getValue()).getTimeStamp().toString();
			}
		});
		TableColumn tblclmnTime = tableViewerColumn_3.getColumn();
		tblclmnTime.setText("Time");
		// Set the Column width
		TableColumn[] columns = table.getColumns();
		int initialWidth = table.getSize().x/columns.length >= 100 ? table.getSize().x/columns.length : 100;
		for (TableColumn tableColumn : columns) {
			tableColumn.setWidth(initialWidth);
		}
		
		tableViewer.setContentProvider(new ContentProvider());
		tableViewer.setInput(new PVTableModel());

		table.addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent e) {
				ViewerCell cell = tableViewer.getCell(new Point(e.x, e.y));
				if (cell != null) {
					Item item = (Item) cell.getElement();
					if (item != null && item.getException() != null)
						table.setToolTipText(item.getException().getMessage());
					else
						table.setToolTipText(null);
				}
			}
		});

		pv.addPVValueChangeListener(pvListener);
		
		// Make the Columns stretch with the table
		parent.addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(ControlEvent e) {
				Rectangle area = table.getClientArea();
				Point size = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				ScrollBar vBar = table.getVerticalBar();
				int width = area.width - table.computeTrim(0,0,0,0).width - vBar.getSize().x;
				if (size.y > area.height + table.getHeaderHeight()) {
					// Subtract the scrollbar width from the total column width
					// if a vertical scrollbar will be required
					Point vBarSize = vBar.getSize();
					width -= vBarSize.x;
				}
				Point oldSize = table.getSize();
				TableColumn[] columns;
				if (oldSize.x > area.width) {
					// table is getting smaller so make the columns 
					// smaller first and then resize the table to
					// match the client area width
					columns = table.getColumns();
					int newWidth = area.width/columns.length >= 100 ? area.width/columns.length : 100;
					for (TableColumn tableColumn : columns) {
						tableColumn.setWidth(newWidth);
					}
					table.setSize(columns.length * newWidth, area.height);
				} else {
					// table is getting bigger so make the table 
					// bigger first and then make the columns wider
					// to match the client area width
					columns = table.getColumns();
					int newWidth = area.width/columns.length >= 100 ? area.width/columns.length : 100;
					table.setSize(columns.length * newWidth, area.height);
					for (TableColumn tableColumn : columns) {
						tableColumn.setWidth(newWidth);
					}
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
	}

}
