/**
 * 
 */
package org.csstudio.display.pvmanager.pvtable.editors;

import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.pvmanager.ExpressionLanguage.latestValueOf;
import static org.epics.pvmanager.extra.ExpressionLanguage.group;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.display.pvmanager.pvtable.EmptyEditorInput;
import org.csstudio.display.pvmanager.pvtable.PVTableModel;
import org.csstudio.display.pvmanager.pvtable.PVTableModel.Item;
import org.csstudio.display.pvmanager.pvtable.PVTableModelListener;
import org.csstudio.display.pvmanager.pvtable.PVTableStaXParser;
import org.csstudio.utility.pvmanager.ui.SWTUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.CellEditor;
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
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
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

	private PVTableModel pvTableModel;
	private boolean isDirty = false;

	private DynamicGroup group = group();
	private PV<List<Object>> pv = PVManager.read(group)
			.andNotify(SWTUtil.onSWTThread()).atHz(2);
	private final PVValueChangeListener pvListener = new PVValueChangeListener() {

		@Override
		public void pvValueChanged() {
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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		IFile file = getEditorInputFile(this.getEditorInput());
		if (file != null)
			saveToFile(monitor, file);
		else
			doSaveAs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		SaveAsDialog saveAsDialog = new SaveAsDialog(getSite().getShell());
		saveAsDialog.setBlockOnOpen(true);
		IFile currentFile = getEditorInputFile(this.getEditorInput());
		if (currentFile != null)
			saveAsDialog.setOriginalFile(currentFile);
		saveAsDialog.open();
		// The path to the new resource relative to the workspace
        IPath newResourcePath = saveAsDialog.getResult();
        if (newResourcePath == null)
            return;
        String ext = newResourcePath.getFileExtension();
        if (newResourcePath.getFileExtension() == null  ||  !ext.equals("css-pvtable"))
        {
        	newResourcePath = newResourcePath.removeFileExtension();
        	newResourcePath.addFileExtension("css-pvtable");
        }
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IFile newFile = root.getFile(newResourcePath);
        if (newFile == null  ||  !saveToFile(null, newFile))
            return;
        // Update input and title
        setInput(new FileEditorInput(newFile));
        this.setPartName(newFile.getName());
	}

	/**
	 * Save current model content to given file, mark editor as clean.
	 * 
	 * @param monitor
	 *            <code>IProgressMonitor</code>, may be null.
	 * @param file
	 *            The file to use. May not exist, but I think its container has
	 *            to.
	 * @return Returns <code>true</code> when successful.
	 */
	private boolean saveToFile(IProgressMonitor monitor, IFile file) {
		boolean ok = true;
		if (monitor != null)
			monitor.beginTask("Save PV Table", IProgressMonitor.UNKNOWN);
		InputStream stream = new ByteArrayInputStream(PVTableStaXParser
				.createByteBuffer(Arrays.asList(pvTableModel.getItems()))
				.toByteArray());
		try {
			if (file.exists())
				file.setContents(stream, true, false, monitor);
			else
				file.create(stream, true, monitor);
			if (monitor != null)
				monitor.done();
			// Mark as clean
			isDirty = false;
			firePropertyChange(IEditorPart.PROP_DIRTY);
		} catch (Exception e) {
			ok = false;
			if (monitor != null)
				monitor.setCanceled(true);
		} finally {
			try {
				stream.close();
			} catch (Exception e) { /* NOP */
			}
		}
		return ok;
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
		pvTableModel = new PVTableModel();
		IFile file = getEditorInputFile(input);
		if (file != null) {
			List<ProcessVariable> pvs = PVTableStaXParser
					.readPVTableFile(file.getLocationURI().getPath());
			for (ProcessVariable processVariable : pvs) {
				pvTableModel.addPVName(processVariable);
				group.add(latestValueOf(channel(processVariable
						.getName())));
			}
		}
	}

	private IFile getEditorInputFile(IEditorInput input) {
		if (input instanceof EmptyEditorInput) {
			return null;
		} else {
			IFile file = (IFile) input.getAdapter(IFile.class);
			if (file != null)
				return file;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return isDirty;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return true;
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
				return item.getProcessVariableName().getName();
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
							model.updatePVName(item, new ProcessVariable(
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
						isDirty = true;
						firePropertyChange(PROP_DIRTY);
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
				return item.getProcessVariableName().getName();
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
		int initialWidth = table.getSize().x / columns.length >= 100 ? table
				.getSize().x / columns.length : 100;
		for (TableColumn tableColumn : columns) {
			tableColumn.setWidth(initialWidth);
		}

		tableViewer.setContentProvider(new ContentProvider());
		tableViewer.setInput(pvTableModel);

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
				int width = area.width - table.computeTrim(0, 0, 0, 0).width
						- vBar.getSize().x;
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
					int newWidth = area.width / columns.length >= 100 ? area.width
							/ columns.length
							: 100;
					for (TableColumn tableColumn : columns) {
						tableColumn.setWidth(newWidth);
					}
					table.setSize(columns.length * newWidth, area.height);
				} else {
					// table is getting bigger so make the table
					// bigger first and then make the columns wider
					// to match the client area width
					columns = table.getColumns();
					int newWidth = area.width / columns.length >= 100 ? area.width
							/ columns.length
							: 100;
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
		this.table.setFocus();
	}

}
