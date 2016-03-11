package org.csstudio.sds.ui.internal.editor.newproperties.table;

import java.util.List;

import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
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

    public static final TableViewer equip(Table table,
            ColumnConfig... columnConfigurations) {
        return equip(table.getParent(), table, columnConfigurations);
    }

    public static final TableViewer equip(Composite parent,
            ColumnConfig[] columnConfigurations) {
        // .. create a composite arround the table that allows for weighted
        // column width of the table
        Composite tableComposite = new Composite(parent, SWT.NONE);
        GridDataFactory.fillDefaults().grab(true, false).hint(0, 100).applyTo(
                tableComposite);
        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        tableComposite.setLayout(tableColumnLayout);

        // create table
        Table table = new Table(tableComposite, SWT.FULL_SELECTION
                | SWT.HIDE_SELECTION | SWT.DOUBLE_BUFFERED | SWT.SCROLL_PAGE);
        table.setLinesVisible(true);
        table.setHeaderVisible(false);

        return equip(tableComposite, table, columnConfigurations);
    }

    private static final TableViewer equip(Composite parent, Table table,
            ColumnConfig[] columnConfigurations) {
        TableColumnLayout tableColumnLayout = (TableColumnLayout) parent
                .getLayout();

        // create the table viewer
        TableViewer viewer = new TableViewer(table);

        // .. create table columns
        String[] columnNames = new String[columnConfigurations.length];

        for (int i = 0; i < columnConfigurations.length; i++) {
            ColumnConfig config = columnConfigurations[i];
            columnNames[i] = config.getId();
            TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
            column.getColumn().setText(config.getTitle());
            column.getColumn().setAlignment(SWT.LEFT);

            tableColumnLayout.setColumnData(column.getColumn(), config
                    .getWeight() > 0 ? new ColumnWeightData(config.getWeight(),
                    config.getMinimumWidth(), config.isResizable())
                    : new ColumnPixelData(config.getMinimumWidth(), config
                            .isResizable()));

            column.setEditingSupport(new DelegatingColumnEditingSupport(viewer,
                    i));
        }

        viewer.setColumnProperties(columnNames);

        ColumnViewerToolTipSupport.enableFor(viewer, ToolTip.NO_RECREATE);

        // configure keyboard support
        TableViewerFocusCellManager focusCellManager = new TableViewerFocusCellManager(
                viewer, new FocusCellOwnerDrawHighlighter(viewer));

        ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(
                viewer) {
            @Override
            protected boolean isEditorActivationEvent(
                    final ColumnViewerEditorActivationEvent event) {
                return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
                        || event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION
                        || (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.F2)
                        || event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
            }

        };

        TableViewerEditor.create(viewer, focusCellManager, actSupport,
                ColumnViewerEditor.TABBING_HORIZONTAL
                        | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
                        | ColumnViewerEditor.TABBING_VERTICAL
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

        public DelegatingColumnEditingSupport(ColumnViewer viewer,
                int columnIndex) {
            super(viewer);
            assert columnIndex >= 0 : "columnIndex>=0";
            this.columnIndex = columnIndex;
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
            return row.getCellEditor(columnIndex, ((TableViewer) getViewer())
                    .getTable());
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
            row.setValue(columnIndex, value);
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
        public void inputChanged(final Viewer viewer, final Object oldInput,
                final Object newInput) {

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
            Image img = row.getImage(index);

            if (img != null) {
                cell.setImage(img);
            }

            // background color
            RGB bgColor = row.getBackgroundColor(index);

            if (bgColor != null) {
                cell.setBackground(CustomMediaFactory.getInstance().getColor(
                        bgColor));
            }

            // foreground color
            RGB fgColor = row.getForegroundColor(index);

            if (fgColor != null) {
                cell.setForeground(CustomMediaFactory.getInstance().getColor(
                        fgColor));
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
