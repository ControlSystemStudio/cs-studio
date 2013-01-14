package org.csstudio.sds.ui.internal.editor.newproperties;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.sds.internal.model.PointlistProperty;
import org.csstudio.sds.ui.SdsUiPlugin;
import org.csstudio.sds.ui.internal.editor.newproperties.table.ColumnConfig;
import org.csstudio.sds.ui.internal.editor.newproperties.table.ConvenienceTableWrapper;
import org.csstudio.sds.ui.internal.editor.newproperties.table.ITableRow;
import org.csstudio.sds.ui.internal.properties.IntegerCellEditor;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class PointListSection extends AbstractBaseSection<PointlistProperty> {

	private TableViewer tableViewer;
	private List<TableEditor> tableEditors = new ArrayList<TableEditor>();

	public PointListSection(String propertyId) {
		super(propertyId);
	}

	@Override
	protected void doCreateControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		GridLayoutFactory.swtDefaults().numColumns(5).applyTo(parent);

		// .. table for viewing and editing the entries
		Composite tableComposite = new Composite(parent, SWT.NONE);
		GridDataFactory.fillDefaults().hint(STANDARD_WIDGET_WIDTH, 100)
				.span(5, 1).applyTo(tableComposite);

		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);

		Table table = getWidgetFactory().createTable(tableComposite,
				SWT.FULL_SELECTION | SWT.DOUBLE_BUFFERED | SWT.SCROLL_PAGE | SWT.V_SCROLL);
		table.setLinesVisible(true);
		table.setHeaderVisible(false);

		tableViewer = ConvenienceTableWrapper.equip(table, 
				new ColumnConfig("x", "x", 30, 10, false), 
				new ColumnConfig("y", "y", 30, 10, false), 
				new ColumnConfig("remove", "Remove", 30, -1, true),
				new ColumnConfig("up", "Up", 30, -1, true));

		// .. button to add new entries to the table
		final Hyperlink addHyperLink = getWidgetFactory().createHyperlink(parent, "Add Point...", SWT.NONE);
		addHyperLink.setUnderlined(false);

		addHyperLink.addHyperlinkListener(new HyperlinkAdapter() {

			@Override
			public void linkActivated(HyperlinkEvent e) {
				PointlistProperty property = getMainWidgetProperty();

				if (property != null) {
					PointDialog dialog = new PointDialog(addHyperLink.getShell());
					if (Window.OK == dialog.open()) {
						Point point = dialog.getPoint();
						PointList original = property.getPropertyValue();
						PointList list = original.getCopy();
						list.addPoint(point);
						applyPropertyChange(list);
					}
				}
			}
		});
	}

	@Override
	protected void doRefreshControls(PointlistProperty widgetProperty) {
		// .. (re)create the table editors used for removing single lines of the
		// table
		if (tableEditors != null) {
			// .. dispose existing editors
			for (TableEditor editor : tableEditors) {
				if (editor.getEditor() != null) {
					editor.getEditor().dispose();
				}
				editor.dispose();

			}

			tableEditors.clear();
		}
		// .. create new editors
		if (widgetProperty != null && tableViewer.getContentProvider() != null) {
			PointList original = widgetProperty.getPropertyValue();
			PointList list = original.getCopy();
			List<ITableRow> rows = new ArrayList<ITableRow>();

			for (int i = 0; i < list.size(); i++) {
				rows.add(new PointRowAdapter(list, list.getPoint(i), i));
			}
			tableViewer.setInput(rows);

			TableItem[] items = tableViewer.getTable().getItems();
			for (int i = 0; i < items.length; i++) {
				final TableItem item = items[i];

				TableEditor deleteTableEditor = new TableEditor(tableViewer
						.getTable());
				Button deleteButton = new Button(tableViewer.getTable(),
						SWT.FLAT);
				deleteButton.setImage(CustomMediaFactory.getInstance()
						.getImageFromPlugin(SdsUiPlugin.PLUGIN_ID,
								"icons/delete.gif"));
				deleteButton.pack();
				deleteTableEditor.minimumWidth = deleteButton.getSize().x;
				deleteTableEditor.horizontalAlignment = SWT.LEFT;
				deleteTableEditor.setEditor(deleteButton, item, 2);
				deleteButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						 PointRowAdapter data = (PointRowAdapter)item.getData();
						 data.setValue(2, "true");
					}
				});
				tableEditors.add(deleteTableEditor);

				TableEditor upTableEditor = new TableEditor(tableViewer
						.getTable());
				final Button upButton = new Button(tableViewer.getTable(),
						SWT.FLAT);
				upButton.setImage(CustomMediaFactory.getInstance()
						.getImageFromPlugin(SdsUiPlugin.PLUGIN_ID,
								"icons/search_prev.gif"));
				upButton.pack();
				upTableEditor.minimumWidth = upButton.getSize().x;
				upTableEditor.horizontalAlignment = SWT.LEFT;
				upTableEditor.setEditor(upButton, item, 3);
				upButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						PointRowAdapter data = (PointRowAdapter)item.getData();
						data.setValue(3, "true");
					}
				});

				tableEditors.add(upTableEditor);
			}
		}
	}

	private class PointRowAdapter implements ITableRow {

		private final PointList _list;
		private final Point _point;
		private final int _index;

		public PointRowAdapter(PointList list, Point point, int index) {
			_list = list;
			_point = point;
			_index = index;
		}

		public boolean canModify(int column) {
			return true;
		}

		public RGB getBackgroundColor(int column) {
			return null;
		}

		public CellEditor getCellEditor(int column, Composite parent) {
			switch (column) {
			case 0:
				return new IntegerCellEditor(parent);
			case 1:
				return new IntegerCellEditor(parent);
			default:
				return null;
			}
		}

		public String getDisplayValue(int column) {
			switch (column) {
			case 0:
				return String.valueOf(_point.x);
			case 1:
				return String.valueOf(_point.y);
			default:
				return null;
			}
		}

		public String getEditingValue(int column) {
			switch (column) {
			case 0:
				return String.valueOf(_point.x);
			case 1:
				return String.valueOf(_point.y);
			default:
				return null;
			}
		}

		public Font getFont(int column) {
			return null;
		}

		public RGB getForegroundColor(int column) {
			return null;
		}

		public Image getImage(int column) {
			return null;
		}

		public String getTooltip() {
			return null;
		}

		public void setValue(int column, Object value) {
			
			switch (column) {
			case 0:
				_point.x = (Integer)value;
				_list.setPoint(_point, _index);
				applyPropertyChange(_list);
				break;
			case 1:
				_point.y = (Integer)value;
				_list.setPoint(_point, _index);
				applyPropertyChange(_list);
				break;
			case 2:
				_list.removePoint(_index);
				applyPropertyChange(_list);
				break;
			case 3:
				movePoint(_list, _index);
				applyPropertyChange(_list);
				break;
			default:
				break;
			}
		}

		/**
		 * Moves the current selected Point one step up or down, depending on
		 * the given boolean.
		 * 
		 * @param up
		 *            True, if the Point should be moved up, false otherwise
		 */
		private void movePoint(PointList list, int index) {
			if (index > 0) {
				
				Point point1 = list.getPoint(index);
				Point point2 = list.getPoint(index-1);
				
				list.setPoint(point1, index-1);
				list.setPoint(point2, index);
			}
		}

		public int compareTo(ITableRow o) {
			return 0;
		}

	}

	/**
	 * This class represents a Dialog for editing a Point.
	 * 
	 * @author Kai Meyer
	 */
	private final class PointDialog extends Dialog {
		/**
		 * The Spinner for the x-value of the Point.
		 */
		private Spinner _xSpinner;
		/**
		 * The Spinner for the y-value of the Point.
		 */
		private Spinner _ySpinner;
		/**
		 * A boolean, which indicates if the Point is new.
		 */
		private Point _result;

		/**
		 * Creates an input dialog with OK and Cancel buttons. Note that the
		 * dialog will have no visual representation (no widgets) until it is
		 * told to open.
		 * <p>
		 * Note that the <code>open</code> method blocks for input dialogs.
		 * </p>
		 * 
		 * @param parentShell
		 *            the parent shell, or <code>null</code> to create a
		 *            top-level shell
		 * @param dialogTitle
		 *            the dialog title, or <code>null</code> if none
		 * @param dialogMessage
		 *            the dialog message, or <code>null</code> if none
		 * @param initialValue
		 *            the initial input value, or <code>null</code> if none
		 * @param isNew
		 *            true id the Point is new, false otherwise
		 */
		public PointDialog(final Shell parentShell) {
			super(parentShell);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void configureShell(final Shell shell) {
			super.configureShell(shell);
			shell.setText("New Point");
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Control createDialogArea(final Composite parent) {
			Composite composite = (Composite) super.createDialogArea(parent);
			composite.setLayout(new GridLayout(2, false));
			Label label = new Label(composite, SWT.NONE);
			label.setText("x:");
			_xSpinner = new Spinner(composite, SWT.BORDER);
			_xSpinner.setMaximum(10000);
			_xSpinner.setMinimum(-10000);
			_xSpinner.setSelection(100);
			label = new Label(composite, SWT.NONE);
			label.setText("y:");
			_ySpinner = new Spinner(composite, SWT.BORDER);
			_ySpinner.setMaximum(10000);
			_ySpinner.setMinimum(-10000);
			_ySpinner.setSelection(100);
			return composite;
		}

		public Point getPoint() {
			return _result;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void okPressed() {
			this.getButton(IDialogConstants.OK_ID).setFocus();
			_result = new Point(_xSpinner.getSelection(), _ySpinner
					.getSelection());
			super.okPressed();
		}
	}
}
