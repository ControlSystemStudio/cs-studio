/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.sds.ui.internal.statistics;

import java.text.NumberFormat;

import org.csstudio.domain.common.LayoutUtil;
import org.csstudio.sds.internal.statistics.MeasureCategoriesEnum;
import org.csstudio.sds.internal.statistics.StatisticUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

/**
 * This view displays the current performance measures by category.
 *
 * @author Sven Wende
 *
 */
public final class PerformanceMeasuresView extends ViewPart {

    /**
     * The view identification as configured in the plugin.xml.
     */
    public static final String VIEW_ID = "org.csstudio.sds.ui.internal.statistics.PerformanceMeasuresView"; //$NON-NLS-1$

    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(final Composite parent) {
        parent.setLayout(LayoutUtil.createGridLayout(1, 0, 10, 10));

        // create a table viewer, which displays the current measures
        final TableViewer measureResultsViewer = createMeasureResultsTable(parent);

        // create a button, which resets the statistics
        Button resetStatisticButton = new Button(parent, SWT.NONE);
        resetStatisticButton.setText("Reset");
        resetStatisticButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(final MouseEvent e) {
                StatisticUtil.getInstance().init();
                measureResultsViewer.refresh();
            }

        });

        final Display display = Display.getCurrent();
        final Job job = new Job("Long Running Job") {
            @Override
            protected IStatus run(final IProgressMonitor monitor) {
                try {
                    display.asyncExec(new Runnable() {
                        public void run() {
                            if (!measureResultsViewer.getControl().isDisposed()) {
                                measureResultsViewer.refresh();
                            }
                        }

                    });

                    return Status.OK_STATUS;
                } finally {
                    schedule(500);
                }
            }
        };

        // job.setSystem(true);
        job.schedule();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus() {

    }

    /**
     * Creates a table viewer, which enables the user to enter aliases.
     *
     * @param parent
     *            the parent composite
     *
     * @return the created viewer
     */
    private TableViewer createMeasureResultsTable(final Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setLayout(LayoutUtil.createGridLayout(1, 0, 0, 0));
        group.setText("Performance Measures");
        group.setLayoutData(LayoutUtil
                .createGridDataForHorizontalFillingCell(100));

        // define column names
        String[] columnNames = new String[] {
                "PROP_NAME", "PROP_VALUE", "PROP_DESCRIPTION" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        // create table
        final Table table = new Table(group, SWT.FULL_SELECTION
                | SWT.SCROLL_PAGE);
        table.setLinesVisible(true);
        table.setLayoutData(LayoutUtil.createGridDataForFillingCell());
        table.setHeaderVisible(true);

        TableColumn column = new TableColumn(table, SWT.CENTER, 0);
        column.setText("Category");
        column.setWidth(140);

        column = new TableColumn(table, SWT.LEFT, 1);
        column.setText("Exec [Counter]");
        column.setWidth(140);

        column = new TableColumn(table, SWT.LEFT, 2);
        column.setText("Time [Execution]");
        column.setWidth(140);

        column = new TableColumn(table, SWT.LEFT, 3);
        column.setText("Time [Total]");
        column.setWidth(140);

        column = new TableColumn(table, SWT.LEFT, 4);
        column.setText("Avg Exec [ calls / ms");
        column.setWidth(140);

        column = new TableColumn(table, SWT.LEFT, 5);
        column.setText("Avg Exec Time [ms / call");
        column.setWidth(140);

        column = new TableColumn(table, SWT.LEFT, 6);
        column.setText("Avg Total Time [ms / call");
        column.setWidth(140);

        // create viewer
        final TableViewer viewer = new TableViewer(table);
        // viewer.setUseHashlookup(true);

        // define column properties
        viewer.setColumnProperties(columnNames);

        // content and label provider
        viewer.setContentProvider(new MeasuresTableContentProvider());
        viewer.setLabelProvider(new MeasuresTableLabelProvider());
        viewer.setInput(MeasureCategoriesEnum.values());

        return viewer;
    }

    /**
     * Content provider for the encapsulated measures table.
     *
     * @author Sven Wende
     */
    protected final class MeasuresTableContentProvider implements
            IStructuredContentProvider {
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
        public Object[] getElements(final Object parent) {
            return (Object[]) parent;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void dispose() {

        }
    }

    /**
     * Label provider for the encapsulated measures table.
     *
     * @author Sven Wende
     */
    protected final class MeasuresTableLabelProvider implements
            ITableLabelProvider {
        /**
         * A number formatter needed for output.
         */
        private NumberFormat _formatter = NumberFormat.getNumberInstance();

        /**
         * The used statistics util.
         */
        private StatisticUtil _statisticUtil = StatisticUtil.getInstance();

        /**
         * {@inheritDoc}
         */
        @Override
        public Image getColumnImage(final Object element, final int columnIndex) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getColumnText(final Object element, final int columnIndex) {
            String result = ""; //$NON-NLS-1$
            MeasureCategoriesEnum category = (MeasureCategoriesEnum) element;

            switch (columnIndex) {
            case 0:
                result = category.toString();
                break;
            case 1:
                result = _formatter.format(_statisticUtil
                        .getExecutionCount(category));
                break;
            case 2:
                result = _formatter.format(_statisticUtil
                        .getExecutionTimeSum(category));
                break;
            case 3:
                result = _formatter.format(_statisticUtil.getRunningTime());
                break;
            case 4:
                result = _formatter.format(_statisticUtil
                        .getAverageCallsPerMs(category));
                break;
            case 5:
                result = _formatter.format(_statisticUtil
                        .getAverageExecutionTimePerCall(category));
                break;
            case 6:
                result = _formatter.format(_statisticUtil
                        .getAverageTimeBetweenCalls(category));
                break;
            default:
                break;
            }

            assert result != null : "result!=null"; //$NON-NLS-1$;

            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addListener(final ILabelProviderListener listener) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void dispose() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isLabelProperty(final Object element,
                final String property) {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        public void removeListener(final ILabelProviderListener listener) {
        }
    }

}
