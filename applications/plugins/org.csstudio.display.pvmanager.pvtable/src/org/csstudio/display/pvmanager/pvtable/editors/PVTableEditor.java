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
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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

		tableViewer = new TableViewer(parent, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.MULTI | SWT.VIRTUAL);
		table = tableViewer.getTable();
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
							model.updatePVName(item, new ProcessVariableName(value.toString()));
							if (item.getRow() == oldSize) {
								group.add(latestValueOf(channel(value.toString())));
							} else {
								group.set(item.getRow(), latestValueOf(channel(value.toString())));
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
		tblclmnPvName.setWidth(100);
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
		tblclmnValue.setWidth(100);
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
				return Util.alarmOf(item.getValue()).getAlarmSeverity().toString();
			}
		});
		TableColumn tblclmnAlarm = tableViewerColumn_2.getColumn();
		tblclmnAlarm.setWidth(100);
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
		tblclmnTime.setWidth(100);
		tblclmnTime.setText("Time");
		tableViewer.setContentProvider(new ContentProvider());
		tableViewer.setInput(new PVTableModel());

		pv.addPVValueChangeListener(pvListener);
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
