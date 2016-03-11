package org.csstudio.dct.ui.editor.tables;

import java.util.List;

import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

/**
 * Convenience wrapper for a SWT table viewer that allows for easy and fine
 * grained customization of all aspects of a table, like
 *
 * <ul>
 * <li>number of columns</li>
 * <li>fore and background color of used in cells</li>
 * <li>font used in cells</li>
 * <li>cell editors</li>
 * <li>cell content</li>
 * </ul>
 *
 * The model for the table is a list of {@link ITableRow}s. Each
 * {@link ITableRow} represents an adapter for an arbitrary object.
 *
 * To use this table just prepare {@link ITableRow} adapters for your model
 * objects.
 *
 * @author Sven Wende
 *
 */
public final class ConvenienceTableWrapper {
    private ColumnConfig[] columnConfigurations;
    private TableViewer viewer;
    private CommandStack commandStack;
    private Table table;

    /**
     * Constructor.
     *
     * @param parent
     *            the parent composite
     * @param style
     *            the SWT style constants describing the behavior and appearance
     *            of the table
     * @param commandStack
     *            a command stack which is used when table cells are edited
     * @param columnConfigurations
     *            the configuration the table columns
     */
    public ConvenienceTableWrapper(Composite parent, int style, CommandStack commandStack, ColumnConfig[] columnConfigurations) {
        this.columnConfigurations = columnConfigurations;
        this.commandStack = commandStack;
        viewer = doCreateViewer(parent, style);
    }

    /**
     * Sets the table input.
     *
     * @param input
     *            a list with table rows representing the table input
     */
    public void setInput(List<ITableRow> input) {
        if (input != null) {
            viewer.setInput(input);
            viewer.refresh();
        }
    }

    /**
     * Returns the table viewer.
     *
     * @return the table viewer
     */
    public TableViewer getViewer() {
        return viewer;
    }

    /**
     * Template method. Subclasses should create the table viewer here.
     *
     * @param parent
     *            a widget which will be the parent of the new instance (cannot
     *            be null)
     * @param style
     *            the style of widget to construct
     * @return the table viewer
     */
    private TableViewer doCreateViewer(Composite parent, int style) {
        // create table
        table = new Table(parent, style | SWT.FULL_SELECTION | SWT.HIDE_SELECTION | SWT.DOUBLE_BUFFERED | SWT.SCROLL_PAGE);
        table.setLinesVisible(true);
        table.setHeaderVisible(false);

        // create viewer
        viewer = new TableViewer(table);

        // create columns
        String[] columnNames = new String[columnConfigurations.length];

        for (int i = 0; i < columnConfigurations.length; i++) {
            columnNames[i] = columnConfigurations[i].getId();
            TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
            column.getColumn().setText(columnConfigurations[i].getTitle());
            column.getColumn().setMoveable(false);
            column.getColumn().setWidth(columnConfigurations[i].getWidth());
            column.setEditingSupport(new DelegatingColumnEditingSupport(viewer, i, commandStack));
        }

        viewer.setColumnProperties(columnNames);

        ColumnViewerToolTipSupport.enableFor(viewer, ToolTip.NO_RECREATE);

        // configure keyboard support
        TableViewerFocusCellManager focusCellManager = new TableViewerFocusCellManager(viewer, new FocusCellOwnerDrawHighlighter(viewer));

        ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(viewer) {
            @Override
            protected boolean isEditorActivationEvent(final ColumnViewerEditorActivationEvent event) {
                return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
                        || event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION
                        || (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.F2)
                        || event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
            }

        };

        TableViewerEditor
                .create(viewer, focusCellManager, actSupport, ColumnViewerEditor.TABBING_HORIZONTAL
                        | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.TABBING_VERTICAL
                        | ColumnViewerEditor.KEYBOARD_ACTIVATION);

        // .. sorter
        viewer.setSorter(new ViewerSorter() {
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                ITableRow r1 = (ITableRow) e1;
                ITableRow r2 = (ITableRow) e2;
                return r1.compareTo(r2);
            }
        });

        viewer.setContentProvider(new ContentProvider());
        viewer.setLabelProvider(new LabelProvider());

        return viewer;
    }

    /**
     * Editing support implementation.
     *
     * @author Sven Wende
     */
    static final class DelegatingColumnEditingSupport extends EditingSupport {
        private int columnIndex;
        private CommandStack commandStack;

        public DelegatingColumnEditingSupport(ColumnViewer viewer, int columnIndex, CommandStack commandStack) {
            super(viewer);
            assert columnIndex >= 0 : "columnIndex>=0";
            assert commandStack != null;
            this.columnIndex = columnIndex;
            this.commandStack = commandStack;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected boolean canEdit(Object element) {
            ITableRow row = (ITableRow) element;
            return row.canModify(columnIndex);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected CellEditor getCellEditor(Object element) {
            ITableRow row = (ITableRow) element;
            return row.getCellEditor(columnIndex, ((TableViewer) getViewer()).getTable());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Object getValue(Object element) {
            ITableRow row = (ITableRow) element;
            return row.getEditingValue(columnIndex);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void setValue(Object element, Object value) {
            ITableRow row = (ITableRow) element;
            row.setValue(columnIndex, value, commandStack);
            getViewer().refresh();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ColumnViewer getViewer() {
            return super.getViewer();
        }
    }

    /**
     * Content provider implementation.
     *
     * @author Sven Wende
     */
    static final class ContentProvider implements IStructuredContentProvider {
        /**
         * {@inheritDoc}
         */
        @Override
        public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {

        }

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings("unchecked")
        public Object[] getElements(final Object parent) {
            return ((List<ITableRow>) parent).toArray();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void dispose() {

        }
    }

    /**
     * Label provider implementation.
     *
     * @author Sven Wende
     */
    final class LabelProvider extends ColumnLabelProvider {

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
            Image img = row.getImage(index);

            if (img != null) {
                cell.setImage(img);
            }

            // background color
            RGB bgColor = row.getBackgroundColor(index);

            if (bgColor != null) {
                cell.setBackground(CustomMediaFactory.getInstance().getColor(bgColor));
            }

            // foreground color
            RGB fgColor = row.getForegroundColor(index);

            if (fgColor != null) {
                cell.setForeground(CustomMediaFactory.getInstance().getColor(fgColor));
            }

            // font
            Font font = row.getFont(index);

            if (font != null) {
                cell.setFont(font);
            }
        }

        /**
         * Returns the text to display.
         *
         * @param element
         *            the current element
         * @param column
         *            the current column index
         * @return The text to display in the viewer
         */
        private String getText(final Object element, final int column) {
            ITableRow row = (ITableRow) element;
            String result = row.getDisplayValue(column);
            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getToolTipText(final Object element) {
            ITableRow row = (ITableRow) element;
            return row.getTooltip();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Point getToolTipShift(final Object object) {
            return new Point(5, 5);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getToolTipDisplayDelayTime(final Object object) {
            return 100;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getToolTipTimeDisplayed(final Object object) {
            return 10000;
        }

    }

}
