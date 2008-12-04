package org.csstudio.dct.ui.editor.tables;

import java.util.List;

import org.csstudio.dct.ui.editor.AbstractTableRowAdapter;
import org.csstudio.dct.ui.editor.ITableRow;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;

/**
 * Implementation for an easy customizable 2-column table that allows for
 * editing arbitrary objects.
 * 
 * The model for the table is a list of {@link ITableRow}s. Each
 * {@link ITableRow} represents an adapter for an arbitrary object.
 * 
 * So - to use this table you just need to prepare {@link ITableRow} adapters
 * for your model objects. Feel free to implement {@link ITableRow} directly or
 * inherit from {@link AbstractTableRowAdapter} which already implements
 * defaults for all adapter methods so that you only need to override methods on
 * demand.
 * 
 * @author Sven Wende
 * 
 */
public class TableCitizenTable extends BaseTable<List<ITableRow>> {
	private static final String KEY = "key";//$NON-NLS-1$ 
	private static final String VALUE = "value"; //$NON-NLS-1$

	public TableCitizenTable(Composite parent, int style, CommandStack commandStack) {
		super(parent, style, commandStack);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getViewerInput(List<ITableRow> rows) {
		return rows;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected TableViewer doCreateViewer(Composite parent, int style) {
		// define column names
		String[] columnNames = new String[] { KEY, VALUE };

		// create table
		final Table table = new Table(parent, style | SWT.FULL_SELECTION | SWT.HIDE_SELECTION | SWT.DOUBLE_BUFFERED | SWT.SCROLL_PAGE);
		table.setLinesVisible(true);
		// table.setLayoutData(LayoutUtil.createGridDataForFillingCell());
		table.setHeaderVisible(true);

		// create viewer
		TableViewer viewer = new TableViewer(table);

		TableViewerColumn keyColumn = new TableViewerColumn(viewer, SWT.NONE);
		keyColumn.getColumn().setText("Name");
		keyColumn.getColumn().setMoveable(false);
		keyColumn.getColumn().setWidth(200);
		keyColumn.setEditingSupport(new KeyColumnEditingSupport(viewer));

		TableViewerColumn valColumn = new TableViewerColumn(viewer, SWT.NONE);
		valColumn.getColumn().setText("Value");
		valColumn.getColumn().setMoveable(false);
		valColumn.getColumn().setWidth(300);
		valColumn.setEditingSupport(new ValueColumnEditingSupport(viewer));

		// define column properties
		viewer.setColumnProperties(columnNames);

		// configure keyboard support
		TableViewerFocusCellManager focusCellManager = new TableViewerFocusCellManager(viewer, new FocusCellOwnerDrawHighlighter(viewer));

		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(viewer) {
			protected boolean isEditorActivationEvent(final ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
						|| (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.F2)
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};

		TableViewerEditor
				.create(viewer, focusCellManager, actSupport, ColumnViewerEditor.TABBING_HORIZONTAL
						| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.TABBING_VERTICAL
						| ColumnViewerEditor.KEYBOARD_ACTIVATION);

		viewer.setContentProvider(new ContentProvider());
		viewer.setLabelProvider(new LabelProvider());
		
//		viewer.setCellModifier(new CellModifier(viewer, getCommandStack()));

		// CellEditor[] cellEditors = new CellEditor[2];
		// cellEditors[0] = new TextCellEditor(viewer.getTable());
		// cellEditors[1] = new TextCellEditor(viewer.getTable());
		// viewer.setCellEditors(cellEditors);

		return viewer;
	}


	/**
	 * Creates additional cell editors. The default implementation provides an
	 * empty list. Subclasses may override.
	 * 
	 * @param parent
	 *            the parent composite
	 * @return a list with cell editors
	 */
	protected CellEditor getKeyCellEditor(Composite parent, Object modelObject) {
		return new TextCellEditor(parent);
	}

	protected CellEditor getValueCellEditor(Composite parent, Object modelObject) {
		return new TextCellEditor(parent);

	}

	class KeyColumnEditingSupport extends EditingSupport {

		public KeyColumnEditingSupport(ColumnViewer viewer) {
			super(viewer);
		}

		@Override
		protected boolean canEdit(Object element) {
			ITableRow row = (ITableRow) element;
			return row.canModifyKey();
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return getKeyCellEditor(((TableViewer) getViewer()).getTable(), element);
		}

		@Override
		protected Object getValue(Object element) {
			ITableRow row = (ITableRow) element;
			return row.getKey();
		}

		@Override
		protected void setValue(Object element, Object value) {
			ITableRow row = (ITableRow) element;
			row.setKey(value.toString());
			getViewer().refresh();
		}
	}

	class ValueColumnEditingSupport extends EditingSupport {

		public ValueColumnEditingSupport(ColumnViewer viewer) {
			super(viewer);
		}

		@Override
		protected boolean canEdit(Object element) {
			ITableRow row = (ITableRow) element;
			return row.canModifyValue();
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return getValueCellEditor(((TableViewer) getViewer()).getTable(), element);
		}

		@Override
		protected Object getValue(Object element) {
			ITableRow row = (ITableRow) element;
			return row.getValue();
		}

		@Override
		protected void setValue(Object element, Object value) {
			ITableRow row = (ITableRow) element;
			row.setValue(value);
			getViewer().refresh();
		}
	}

	static final class CellModifier implements ICellModifier {
		private Viewer viewer;
		private CommandStack commandStack;

		public CellModifier(Viewer viewer, CommandStack commandStack) {
			assert viewer != null;
			assert commandStack != null;
			this.viewer = viewer;
			this.commandStack = commandStack;
		}

		public boolean canModify(Object element, String property) {
			ITableRow row = (ITableRow) element;

			boolean result = false;

			if (KEY.equals(property)) {
				result = row.canModifyKey();
			} else if (VALUE.equals(property)) {
				result = row.canModifyValue();
			}

			return result;
		}

		public Object getValue(Object element, String property) {
			ITableRow row = (ITableRow) element;

			Object result = false;

			if (KEY.equals(property)) {
				result = row.getKey();
			} else if (VALUE.equals(property)) {
				result = row.getValue();
			}

			return result;
		}

		public void modify(Object element, String property, Object value) {
			ITableRow row;
			if (element instanceof Item) {
				row = (ITableRow) ((Item) element).getData();
			} else {
				row = (ITableRow) element;
			}

			if (KEY.equals(property)) {
				row.setKey(value.toString());
			} else if (VALUE.equals(property)) {
				SetValueCommand cmd = new SetValueCommand(row, value);
				commandStack.execute(cmd);
				// row.setValue(value);
			}

			viewer.refresh();
		}
	}

	private static class SetValueCommand extends Command {
		private ITableRow delegate;
		private Object value;
		private Object oldValue;

		private SetValueCommand(ITableRow delegate, Object value) {
			this.delegate = delegate;
			this.value = value;
		}

		@Override
		public void execute() {
			oldValue = delegate.getValue();
			delegate.setValue(value);
		}

		@Override
		public void undo() {
			delegate.setValue(oldValue);
		}
	}

	static final class ContentProvider implements IStructuredContentProvider {
		/**
		 * {@inheritDoc}
		 */
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {

		}

		/**
		 * {@inheritDoc}
		 */
		public Object[] getElements(final Object parent) {
			return ((List<ITableRow>) parent).toArray();
		}

		/**
		 * {@inheritDoc}
		 */
		public void dispose() {

		}
	}

	static final class LabelProvider extends ColumnLabelProvider {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void update(final ViewerCell cell) {
			ITableRow row = (ITableRow) cell.getElement();
			int index = cell.getColumnIndex();

			// set the text
			cell.setText(getText(row, index));

			// image
			if (index == 0) {
				Image img = row.getImage();

				if (img != null) {
					cell.setImage(img);
				}
			}

			// background color
			RGB bgColor = null;

			switch (index) {
			case 0:
				bgColor = row.getBackgroundColorForKey();
				break;
			case 1:
				bgColor = row.getBackgroundColorForValue();
				break;
			default:
				bgColor = null;
				break;
			}

			if (bgColor != null) {
				cell.setBackground(CustomMediaFactory.getInstance().getColor(bgColor));
			}

			// foreground color
			RGB fgColor = null;

			switch (index) {
			case 0:
				fgColor = row.getForegroundColorForKey();
				break;
			case 1:
				fgColor = row.getForegroundColorForValue();
				break;
			default:
				fgColor = null;
				break;
			}

			if (fgColor != null) {
				cell.setForeground(CustomMediaFactory.getInstance().getColor(fgColor));
			}

			// font
			FontData font = null;

			switch (index) {
			case 0:
				font = row.getFontForKey();
				break;
			case 1:
				font = row.getFontForValue();
				break;
			default:
				font = null;
				break;
			}

			if (font != null) {
				cell.setFont(CustomMediaFactory.getInstance().getFont(font));
			}
		}

		/**
		 * Returns the text to display.
		 * 
		 * @param element
		 *            the current element
		 * @param columnIndex
		 *            the current column index
		 * @return The text to display in the viewer
		 */
		private String getText(final Object element, final int columnIndex) {
			ITableRow row = (ITableRow) element;
			String result = "";
			switch (columnIndex) {
			case 0:
				result = row.getKeyDescription();
				break;
			case 1:
				Object v = row.getValueForDisplay();
				result = v != null ? v.toString() : null;
				break;
			default:
				break;
			}
			return result;
		}

		/**
		 * {@inheritDoc}
		 */
		public String getToolTipText(final Object element) {
			return null;
		}

		/**
		 * {@inheritDoc}
		 */
		public Point getToolTipShift(final Object object) {
			return new Point(5, 5);
		}

		/**
		 * {@inheritDoc}
		 */
		public int getToolTipDisplayDelayTime(final Object object) {
			return 100;
		}

		/**
		 * {@inheritDoc}
		 */
		public int getToolTipTimeDisplayed(final Object object) {
			return 10000;
		}

		/**
		 * {@inheritDoc}
		 */
		private Font getFont(final Object element, final int column) {
			ITableRow row = (ITableRow) element;

			// font
			FontData font = null;

			switch (column) {
			case 0:
				font = row.getFontForKey();
				break;
			case 1:
				font = row.getFontForValue();
				break;
			default:
				font = null;
				break;
			}

			Font result = font != null ? CustomMediaFactory.getInstance().getFont(font) : null;

			return result;
		}
	}

}
