/**
 * 
 */
package org.csstudio.display.pvmanager.pvtable.editors;

import static org.epics.pvmanager.ExpressionLanguage.channel;
import static org.epics.pvmanager.ExpressionLanguage.latestValueOf;
import static org.epics.pvmanager.extra.ExpressionLanguage.group;
import static org.epics.pvmanager.util.TimeDuration.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellNavigationStrategy;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationListener;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnViewerEditorDeactivationEvent;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.FocusCellHighlighter;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.epics.pvmanager.PV;
import org.epics.pvmanager.PVManager;
import org.epics.pvmanager.PVReader;
import org.epics.pvmanager.PVReaderListener;
import org.epics.pvmanager.data.SimpleValueFormat;
import org.epics.pvmanager.data.ValueFormat;
import org.epics.pvmanager.data.ValueUtil;
import org.epics.pvmanager.extra.DynamicGroup;
import com.swtdesigner.TableViewerColumnSorter;

/**
 * @author shroffk
 * 
 */
public class PVTableEditor extends EditorPart implements ISelectionProvider {

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
	private static Logger logger = Logger.getLogger(PVTableEditor.class
			.getName());

	private Table table;
	private TableViewer tableViewer;

	private PVTableModel pvTableModel;
	private boolean isDirty = false;

	private DynamicGroup group = group();
	private PVReader<List<Object>> pv = PVManager.read(group)
			.notifyOn(SWTUtil.swtThread()).every(hz(2));
	private final PVReaderListener pvListener = new PVReaderListener() {

		@Override
		public void pvChanged() {
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

	/**
	 * Add a List of ProcessVariable to the pv Table Editor
	 * 
	 * @param processVariables
	 */
	public void addProcessVariables(Collection<ProcessVariable> processVariables) {
		if (processVariables != null) {
			PVTableModel model = (PVTableModel) tableViewer.getInput();
			if (model != null) {
				for (ProcessVariable processVariable : processVariables) {
					model.addPVName(processVariable);
					group.add(latestValueOf(channel(processVariable.getName())));
					isDirty = true;
					firePropertyChange(PROP_DIRTY);
				}
			}
		}
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
		if (newResourcePath.getFileExtension() == null
				|| !ext.equals("css-pvtable")) {
			newResourcePath = newResourcePath.removeFileExtension();
			newResourcePath = newResourcePath.addFileExtension("css-pvtable");
		}
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IFile newFile = root.getFile(newResourcePath);
		if (newFile == null || !saveToFile(null, newFile))
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
			List<ProcessVariable> pvs = PVTableStaXParser.readPVTableFile(file
					.getLocationURI().getPath());
			for (ProcessVariable processVariable : pvs) {
				pvTableModel.addPVName(processVariable);
				group.add(latestValueOf(channel(processVariable.getName())));
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
		pv.removePVReaderListener(pvListener);
		unregisterSelectionListener();
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

		// Make the Columns stretch with the table
		table.addControlListener(new ControlAdapter() {

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
				} else {
					// table is getting bigger so make the table
					// bigger first and then make the columns wider
					// to match the client area width
					columns = table.getColumns();
					int newWidth = area.width / columns.length >= 100 ? area.width
							/ columns.length
							: 100;
					for (TableColumn tableColumn : columns) {
						tableColumn.setWidth(newWidth);
					}
				}
			}
		});

		final TableViewerColumn tableViewerColumn = new TableViewerColumn(
				tableViewer, SWT.NONE);
		new TableViewerColumnSorter(tableViewerColumn) {
			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				// Ensure that the empty "" remains in the last position always
				// irrespective of the direction of sorting
				String s1 = super.getValue(e1).toString();
				String s2 = super.getValue(e2).toString();
				if (s1.equals(s2) || s2.equals(""))
					return 0;
				else
					return s1.compareTo(s2);
			}
		};

		tableViewerColumn.setEditingSupport(new EditingSupport(tableViewer) {
			private boolean initialized = false;

			protected boolean canEdit(Object element) {
				return true;
			}

			protected CellEditor getCellEditor(Object element) {
				TextCellEditor editor = new TextCellEditor(table) {
					@Override
					public void activate(
							ColumnViewerEditorActivationEvent activationEvent) {
						if (activationEvent.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION) {
							super.activate(activationEvent);
						} else if (activationEvent.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED) {
							// this.text =
							// String.valueOf(activationEvent.character);
							this.setValue(String
									.valueOf(activationEvent.character));
						}
					}
				};
				CellEditor[] editors = new CellEditor[1];
				editors[0] = editor;
				tableViewer.setCellEditors(editors);
				return editor;
			}

			protected Object getValue(Object element) {
				Item item = (Item) element;
				if (item == null || item.getProcessVariableName() == null)
					return "";
				return item.getProcessVariableName().getName();
			}

			@Override
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
								// Do nothing: the row was the empty one
								// anyway
							} else {
								group.remove(item.getRow());
							}
							model.removeItem(item);
							model.updateValues(group.lastExceptions());
						} else {
							// We are updating the row
							model.updatePVName(item,
									new ProcessVariable(value.toString()));
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
						if (!initialized) {
							((Text) getCellEditor(element).getControl())
									.setSelection(1);
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

		// Alarm Column
		TableViewerColumn tableViewerColumn_2 = new TableViewerColumn(
				tableViewer, SWT.NONE);
		new TableViewerColumnSorter(tableViewerColumn_2) {
			@Override
			protected int doCompare(Viewer viewer, Object e1, Object e2) {
				// TODO Remove this method, if your getValue(Object) returns
				// Comparable.
				// Typical Comparable are String, Integer, Double, etc.
				return super.doCompare(viewer, e1, e2);
			}

			@Override
			protected Object getValue(Object o) {
				// TODO remove this method, if your EditingSupport returns value
				Item item = (Item) o;
				if (item == null || item.getValue() == null)
					return null;
				return ValueUtil.alarmOf(item.getValue()).getAlarmSeverity();
			}
		};
		tableViewerColumn_2.setLabelProvider(new PVColumnLabelProvider() {
			public Image getImage(Object element) {
				return null;
			}

			public String getText(Object element) {
				Item item = (Item) element;
				if (item == null || item.getValue() == null)
					return null;
				return ValueUtil.alarmOf(item.getValue()).getAlarmSeverity()
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
				return ValueUtil.timeOf(item.getValue()).getTimeStamp().toString();
			}
		});
		TableColumn tblclmnTime = tableViewerColumn_3.getColumn();
		tblclmnTime.setText("Time");

		// Navigation and Focus support
		final CellNavigationStrategy naviStrat = new CellNavigationStrategy() {

			/**
			 * is the given event an event which moves the selection to another
			 * cell
			 * 
			 * @param viewer
			 *            the viewer we are working for
			 * @param event
			 *            the key event
			 * @return <code>true</code> if a new cell is searched
			 */
			public boolean isNavigationEvent(ColumnViewer viewer, Event event) {
				switch (event.keyCode) {
				case SWT.ARROW_UP:
				case SWT.ARROW_DOWN:
				case SWT.ARROW_LEFT:
				case SWT.ARROW_RIGHT:
				case SWT.CR:
				case SWT.HOME:
				case SWT.PAGE_DOWN:
				case SWT.PAGE_UP:
				case SWT.END:
					return true;
				default:
					return false;
				}
			}

			/**
			 * @param viewer
			 *            the viewer we are working for
			 * @param currentSelectedCell
			 *            the cell currently selected
			 * @param event
			 *            the key event
			 * @return the cell which is highlighted next or <code>null</code>
			 *         if the default implementation is taken. E.g. it's fairly
			 *         impossible to react on PAGE_DOWN requests
			 */
			public ViewerCell findSelectedCell(ColumnViewer viewer,
					ViewerCell currentSelectedCell, Event event) {

				ViewerCell cell = null;
				switch (event.keyCode) {
				case SWT.CR:
					if (currentSelectedCell != null) {
						cell = currentSelectedCell.getNeighbor(
								ViewerCell.BELOW, false);
					}
					if (cell != null) {
						tableViewer.getTable().setSelection(
								(TableItem) cell.getItem());
					}
					break;
				case SWT.ARROW_UP:
					if (currentSelectedCell != null) {
						cell = currentSelectedCell.getNeighbor(
								ViewerCell.ABOVE, false);
					}
					break;
				case SWT.ARROW_DOWN:
					if (currentSelectedCell != null) {
						cell = currentSelectedCell.getNeighbor(
								ViewerCell.BELOW, false);
					}
					break;
				default:
					cell = super.findSelectedCell(viewer, currentSelectedCell,
							event);
				}
				return cell;
			}
		};

		TableViewerFocusCellManager focusCellManager = new TableViewerFocusCellManager(
				tableViewer, new FocusCellHighlighter(tableViewer) {
				}, naviStrat);

		// Editing support
		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(
				tableViewer) {
			protected boolean isEditorActivationEvent(
					ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
						|| (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED
								&& event.keyCode > 31 && event.keyCode < 127)
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}

		};

		TableViewerEditor.create(tableViewer, focusCellManager, actSupport,
				ColumnViewerEditor.TABBING_HORIZONTAL
						| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
						| ColumnViewerEditor.TABBING_VERTICAL
						| ColumnViewerEditor.KEYBOARD_ACTIVATION);

		tableViewer.getColumnViewerEditor().addEditorActivationListener(
				new ColumnViewerEditorActivationListener() {

					public void afterEditorActivated(
							ColumnViewerEditorActivationEvent event) {
						// TODO replace hack to move cursor to the end of the
						// text(shroffk)
						if (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED) {							
							((Text) tableViewer.getCellEditors()[0]
									.getControl())
									.setSelection(((Text) tableViewer
											.getCellEditors()[0].getControl())
											.getText().length());
						}
						// ((Text) this.getControl())
						// .setSelection(((Text) this.getControl())
						// .getText().length());
						//
					}

					public void afterEditorDeactivated(
							ColumnViewerEditorDeactivationEvent event) {
						// move to the next line
						if (event.eventType == ColumnViewerEditorDeactivationEvent.EDITOR_SAVED) {
							Event test = new Event();
							test.keyCode = SWT.CR;
							naviStrat.findSelectedCell(tableViewer, tableViewer
									.getColumnViewerEditor().getFocusCell(),
									test);
						}
					}

					public void beforeEditorActivated(
							ColumnViewerEditorActivationEvent event) {

					}

					public void beforeEditorDeactivated(
							ColumnViewerEditorDeactivationEvent event) {

					}

				});

		tableViewer.setContentProvider(new ContentProvider());
		tableViewer.setInput(pvTableModel);
		registerSelectionListener();

		// This is new code
		// First we create a menu Manager
		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(new Separator(
						IWorkbenchActionConstants.MB_ADDITIONS));
			}
		});
		Menu menu = menuManager.createContextMenu(table);
		// Set the MenuManager
		table.setMenu(menu);
		getSite().registerContextMenu(menuManager, this);
		table.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.DEL) {
					PVTableModel model = (PVTableModel) tableViewer.getInput();
					ISelection tableSelection = tableViewer.getSelection();
					// TODO cleanup (shroffk)
					if (tableSelection instanceof StructuredSelection) {
						Object[] selection = ((StructuredSelection) tableSelection)
								.toArray();
						Arrays.sort(selection, new Comparator<Object>() {

							@Override
							public int compare(Object o1, Object o2) {
								return (new Integer(((Item) o1).getRow())
										.compareTo(new Integer(((Item) o2)
												.getRow())))
										* (-1);
							}

						});

						Item item = null;
						for (Object element : selection) {
							item = (PVTableModel.Item) element;
							group.remove(item.getRow());
							model.removeItem(item);
							model.updateValues(group.lastExceptions());
						}
						// Set the selection to the index of the first deleted
						// element
						if (item != null) {
							tableViewer.getTable().setSelection(item.getRow());
						} else {
							tableViewer.getTable().setSelection(
									model.getItems().length);
						}
						tableViewer.getTable().setFocus();

						isDirty = true;
						firePropertyChange(PROP_DIRTY);
					}
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});

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
		pv.addPVReaderListener(pvListener);

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

	private List<ISelectionChangedListener> selectionListeners = new ArrayList<ISelectionChangedListener>();

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionListeners.add(listener);
	}

	private ISelectionChangedListener changeNotification = new ISelectionChangedListener() {

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			SelectionChangedEvent newEvent = new SelectionChangedEvent(
					PVTableEditor.this, getSelection());
			for (ISelectionChangedListener listener : selectionListeners) {
				listener.selectionChanged(newEvent);
			}
		}
	};

	private void registerSelectionListener() {
		tableViewer.addSelectionChangedListener(changeNotification);
	}

	private void unregisterSelectionListener() {
		tableViewer.removeSelectionChangedListener(changeNotification);
	}

	@Override
	public ISelection getSelection() {
		ISelection tableSelection = tableViewer.getSelection();
		if (tableSelection instanceof StructuredSelection) {
			List<ProcessVariable> variables = new ArrayList<ProcessVariable>();
			for (Object element : ((StructuredSelection) tableSelection)
					.toArray()) {
				variables.add(((PVTableModel.Item) element)
						.getProcessVariableName());
			}
			variables.remove(null);
			return new StructuredSelection(variables);
		} else {
			return null;
		}
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		selectionListeners.remove(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

}
