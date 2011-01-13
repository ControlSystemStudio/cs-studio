/*
* Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
* Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
*
* THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
* WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUTNOT LIMITED
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
package org.csstudio.utility.documentviewer;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * TODO (hrickens) :
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.1 $
 * @since 17.08.2010
 */
public class DocumentTableBuilder {
    public static TableViewer createDocumentTable(@Nonnull final Composite group) {
        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        Composite tableComposite = new Composite(group, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(tableComposite);
        tableComposite.setLayout(tableColumnLayout);

        final TableViewer tableViewer = new TableViewer(tableComposite, SWT.H_SCROLL | SWT.V_SCROLL
                | SWT.MULTI | SWT.FULL_SELECTION);
        tableViewer.getTable().setHeaderVisible(true);
        tableViewer.getTable().setLinesVisible(true);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(tableViewer.getTable());

        createHierarchyColumn(tableColumnLayout, tableViewer);
        createNodeNameColumn(tableColumnLayout, tableViewer);
        createSubjectColumn(tableColumnLayout, tableViewer);
        createDateColumn(tableColumnLayout, tableViewer);

        createDescColumn(tableColumnLayout, tableViewer);
        createKeywordsColumn(tableColumnLayout, tableViewer);

        tableViewer.setContentProvider(new TableContentProvider());
        return tableViewer;
    }

    /**
     * @param tableColumnLayout
     * @param tableViewer
     * @return
     */
    private static void createHierarchyColumn(final TableColumnLayout tableColumnLayout,
                                              final TableViewer tableViewer) {
        // Column Hierarchy
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setText("Hierarchy");
        column.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(@Nonnull final ViewerCell cell) {
                
                Object element = cell.getElement();
                if (element instanceof String) {
                    String msg = (String) element;
                    cell.setText(msg);
                } else if (element instanceof HierarchyDocument) {
                    cell.setText( ((HierarchyDocument) element).getNode().getNodeType().getName());
                }
            }
        });
        AbstractColumnViewerSorter columnViewerSorter = new AbstractColumnViewerSorter(tableViewer, column) {

            @Override
            protected int doCompare(@Nullable final Viewer viewer,
                                    @Nullable final Object e1,
                                    @Nullable final Object e2) {
                if ( (e1 != null) && (e2 != null)) {
                    HierarchyDocument doc1 = (HierarchyDocument) e1;
                    HierarchyDocument doc2 = (HierarchyDocument) e2;
                    return doc1.getNode().getNodeType().compareTo(doc2.getNode().getNodeType());
                }
                return -1;
            }

        };

        tableColumnLayout.setColumnData(column.getColumn(), new ColumnWeightData(2, 100, true));
        columnViewerSorter.setSorter(columnViewerSorter, AbstractColumnViewerSorter.ASC);
    }

    /**
     * @param tableColumnLayout
     * @param tableViewer
     */
    private static void createNodeNameColumn(final TableColumnLayout tableColumnLayout,
                                             final TableViewer tableViewer) {
     // Column Hierarchy
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setText("Node name");
        column.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(@Nonnull final ViewerCell cell) {
                Object element = cell.getElement();
                if (element instanceof HierarchyDocument) {
                    cell.setText( ((HierarchyDocument) element).getNode().getName());
                }
            }
        });
        new AbstractColumnViewerSorter(tableViewer, column) {

            @Override
            protected int doCompare(@Nullable final Viewer viewer,
                                    @Nullable final Object e1,
                                    @Nullable final Object e2) {
                if ( (e1 != null) && (e2 != null)) {
                    HierarchyDocument doc1 = (HierarchyDocument) e1;
                    HierarchyDocument doc2 = (HierarchyDocument) e2;
                    return compareStrings(doc1.getNode().getName(), doc2.getNode().getName());
                }
                return -1;
            }

        };

        tableColumnLayout.setColumnData(column.getColumn(), new ColumnWeightData(2, 100, true));
    }

    /**
     * @param tableColumnLayout
     * @param tableViewer
     */
    private static void createSubjectColumn(final TableColumnLayout tableColumnLayout,
                                            final TableViewer tableViewer) {
        // Column Subject
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setText("Subject");
        column.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(@Nonnull final ViewerCell cell) {
                Object element = cell.getElement();
                if (element instanceof HierarchyDocument) {
                    cell.setText(((HierarchyDocument) element).getDocument().getSubject());
                }
            }
        });
        new AbstractColumnViewerSorter(tableViewer, column) {

            @Override
            protected int doCompare(@Nullable final Viewer viewer,@Nullable  final Object e1,@Nullable  final Object e2) {
                if((e1!=null)&&(e2!=null)) {
                    HierarchyDocument doc1 = (HierarchyDocument) e1;
                    HierarchyDocument doc2 = (HierarchyDocument) e2;
                    return compareStrings(doc1.getDocument().getSubject(), doc2.getDocument().getSubject());
                }
                return -1;
            }


        };

        tableColumnLayout.setColumnData(column.getColumn(), new ColumnWeightData(2, 100, true));

        // Column Mime Type
        column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setText("MimeType");
        column.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(@Nonnull final ViewerCell cell) {
                Object element = cell.getElement();
                if (element instanceof HierarchyDocument) {
                    cell.setText(((HierarchyDocument) element).getDocument().getMimeType());
                }
            }
        });

        new AbstractColumnViewerSorter(tableViewer, column) {

            @Override
            protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                HierarchyDocument doc1 = (HierarchyDocument) e1;
                HierarchyDocument doc2 = (HierarchyDocument) e2;
                return compareStrings(doc1.getDocument().getMimeType(), doc2.getDocument().getMimeType());
            }
        };

        tableColumnLayout.setColumnData(column.getColumn(), new ColumnPixelData(30, true));
    }

    /**
     * @param tableColumnLayout
     * @param tableViewer
     * @return
     * @return
     */
    private static TableViewerColumn createDateColumn(final TableColumnLayout tableColumnLayout,
                                                       final TableViewer tableViewer) {
        TableViewerColumn column;
        // Column Create Date
        column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setText("Create Date");
        column.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(final ViewerCell cell) {
                Object element = cell.getElement();
                if (element instanceof HierarchyDocument) {
                    cell.setText(((HierarchyDocument) element).getDocument().getCreatedDate().toString());
                }
            }
        });

        tableColumnLayout.setColumnData(column.getColumn(), new ColumnWeightData(3, 80, true));

        return column;
    }

    /**
     * @param tableColumnLayout
     * @param tableViewer
     */
    private static void createDescColumn(final TableColumnLayout tableColumnLayout,
                                         final TableViewer tableViewer) {
        final TableViewerColumn column2 = new TableViewerColumn(tableViewer, SWT.NONE);
        column2.getColumn().setText("Desc");
        column2.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(final ViewerCell cell) {
                Object element = cell.getElement();
                if (element instanceof HierarchyDocument) {
                    HierarchyDocument document = (HierarchyDocument) element;
                    cell.setText(document.getDocument().getDesclong());
                }
            }
        });
        new AbstractColumnViewerSorter(tableViewer, column2) {

            @Override
            protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                HierarchyDocument doc1 = (HierarchyDocument) e1;
                HierarchyDocument doc2 = (HierarchyDocument) e2;
                return compareStrings(doc1.getDocument().getDesclong(), doc2.getDocument().getDesclong());
            }
        };

        tableColumnLayout.setColumnData(column2.getColumn(), new ColumnWeightData(6, 140, true));
    }

    /**
     * @param tableColumnLayout
     * @param tableViewer
     */
    private static void createKeywordsColumn(final TableColumnLayout tableColumnLayout,
                                             final TableViewer tableViewer) {
        final TableViewerColumn column2 = new TableViewerColumn(tableViewer, SWT.NONE);
        column2.getColumn().setText("Key Words");
        column2.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(final ViewerCell cell) {
                Object element = cell.getElement();
                if (element instanceof HierarchyDocument) {
                    HierarchyDocument document = (HierarchyDocument) element;
                    cell.setText(document.getDocument().getKeywords());
                }
            }
        });
        new AbstractColumnViewerSorter(tableViewer, column2) {

            @Override
            protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                HierarchyDocument doc1 = (HierarchyDocument) e1;
                HierarchyDocument doc2 = (HierarchyDocument) e2;
                return compareStrings(doc1.getDocument().getKeywords(), doc2.getDocument().getKeywords());
            }
        };

        tableColumnLayout.setColumnData(column2.getColumn(), new ColumnWeightData(2, 75, true));
    }

    private static int compareStrings(final String string1, final String string2) {
        if((string1==null)&&(string2==null)) {
            return 0;
        } else if(string1==null) {
            return 1;
        } else if(string2==null) {
            return -1;
        }
        return string1.compareToIgnoreCase(string2);
    }

    /**
     *
     * TODO (hrickens) :
     *
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.1 $
     * @since 17.08.2010
     */
    private abstract static class AbstractColumnViewerSorter extends ViewerComparator {
        public static final int ASC = 1;

        public static final int NONE = 0;

        public static final int DESC = -1;

        private int _direction = 0;

        private final TableViewerColumn _column;

        private final ColumnViewer _viewer;

        public AbstractColumnViewerSorter(final ColumnViewer viewer, final TableViewerColumn column) {
            _column = column;
            _viewer = viewer;
            _column.getColumn().addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(final SelectionEvent e) {
                    if( (AbstractColumnViewerSorter.this._viewer.getComparator() != null)
                            && ( AbstractColumnViewerSorter.this._viewer.getComparator() == AbstractColumnViewerSorter.this )) {
                            int direction = AbstractColumnViewerSorter.this._direction;

                            if( direction == ASC ) {
                                setSorter(AbstractColumnViewerSorter.this, DESC);
                            } else if( direction == DESC ) {
                                setSorter(AbstractColumnViewerSorter.this, NONE);
                            }
                    } else {
                        setSorter(AbstractColumnViewerSorter.this, ASC);
                    }
                }
            });
        }

        public void setSorter(final AbstractColumnViewerSorter sorter, final int direction) {
            if( direction == NONE ) {
                _column.getColumn().getParent().setSortColumn(null);
                _column.getColumn().getParent().setSortDirection(SWT.NONE);
                _viewer.setComparator(null);
            } else {
                _column.getColumn().getParent().setSortColumn(_column.getColumn());
                sorter._direction = direction;

                if( direction == ASC ) {
                    _column.getColumn().getParent().setSortDirection(SWT.DOWN);
                } else {
                    _column.getColumn().getParent().setSortDirection(SWT.UP);
                }

                if( _viewer.getComparator() == sorter ) {
                    _viewer.refresh();
                } else {
                    _viewer.setComparator(sorter);
                }

            }
        }

        @Override
        public int compare(final Viewer viewer, final Object e1, final Object e2) {
            return _direction * doCompare(viewer, e1, e2);
        }

        protected abstract int doCompare(Viewer viewer, Object e1, Object e2);
    }

    public static void makeMenus(@Nonnull final TableViewer viewer) {
        Menu menu = new Menu(viewer.getControl());
        MenuItem showItem = new MenuItem(menu, SWT.PUSH);
        showItem.addSelectionListener(new ShowDocumentSelectionListener(viewer));
        showItem.setText("&Open");
        showItem.setImage(PlatformUI.getWorkbench().getSharedImages()
                .getImage(ISharedImages.IMG_OBJ_FOLDER));

        viewer.getTable().setMenu(menu);
    }


    /**
     * This class provides the content for the table.
     */
    private static class TableContentProvider implements IStructuredContentProvider {

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        public final Object[] getElements(final Object arg0) {
            if (arg0 instanceof List) {
                List<HierarchyDocument> list = (List<HierarchyDocument>) arg0;
                if(list.isEmpty()) {
                    return new String[] {"Kein Dokument gefundne!"};
                }
                return list.toArray(new HierarchyDocument[list.size()]);
            } else if (arg0 instanceof Set) {
                Set docSet = (Set) arg0;
                if(docSet.isEmpty()) {
                    return new String[] {"Kein Dokument gefundne!"};
                    }
                return docSet.toArray(new HierarchyDocument[docSet.size()]);
            } else if (arg0 instanceof String[]) {
                String[] msg = (String[]) arg0;
                return msg;
            }

            return null;
        }

        /**
         * Disposes any resources.
         */
        public final void dispose() {
            // We don't create any resources, so we don't dispose any
        }

        /**
         * Called when the input changes.
         *
         * @param arg0
         *            the parent viewer
         * @param arg1
         *            the old input
         * @param arg2
         *            the new input
         */
        public final void inputChanged(final Viewer arg0, final Object arg1, final Object arg2) {
            // do noting
        }
    }

}
