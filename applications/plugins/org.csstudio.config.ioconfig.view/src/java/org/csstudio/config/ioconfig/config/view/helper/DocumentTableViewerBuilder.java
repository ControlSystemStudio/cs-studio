/*
		* Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
		* Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
		*
		* THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
		* WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT
		NOT LIMITED
		* TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE
		AND
		* NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
		BE LIABLE
		* FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
		CONTRACT,
		* TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
		SOFTWARE OR
		* THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE
		DEFECTIVE
		* IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING,
		REPAIR OR
		* CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART
		OF THIS LICENSE.
		* NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS
		DISCLAIMER.
		* DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
		ENHANCEMENTS,
		* OR MODIFICATIONS.
		* THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
		MODIFICATION,
		* USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
		DISTRIBUTION OF THIS
		* PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU
		MAY FIND A COPY
		* AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
		*/
package org.csstudio.config.ioconfig.config.view.helper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.DocumentDBO;
import org.csstudio.config.ioconfig.model.IDocument;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.Repository;
import org.csstudio.config.ioconfig.model.tools.Helper;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.2 $
 * @since 30.07.2010
 */
public class DocumentTableViewerBuilder {
    
    private static final Logger LOG = LoggerFactory.getLogger(DocumentTableViewerBuilder.class);

    public static TableViewer crateDocumentTable(@Nonnull final Composite group, final boolean showHierarchy) {
        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        Composite tableComposite = new Composite(group, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(tableComposite);
        tableComposite.setLayout(tableColumnLayout);

        final TableViewer tableViewer = new TableViewer(tableComposite, SWT.H_SCROLL | SWT.V_SCROLL
                | SWT.MULTI | SWT.FULL_SELECTION);
        tableViewer.getTable().setHeaderVisible(true);
        tableViewer.getTable().setLinesVisible(true);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(tableViewer.getTable());


        TableViewerColumn column;

        if (showHierarchy) {
            column = createHierarchyColumn(tableColumnLayout, tableViewer);
            createSubjectColumn(tableColumnLayout, tableViewer);
            createDateColumn(tableColumnLayout, tableViewer);
        }else {
            createSubjectColumn(tableColumnLayout, tableViewer);
            column = createDateColumn(tableColumnLayout, tableViewer);
        }

        createDescColumn(tableColumnLayout, tableViewer);
        createKeywordsColumn(tableColumnLayout, tableViewer);

        AbstractColumnViewerSorter columnViewerSorter = new AbstractColumnViewerSorter(tableViewer, column) {

            @Override
            protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                IDocument doc1 = (IDocument) e1;
                IDocument doc2 = (IDocument) e2;
                return compareStrings(doc1.getCreatedDate().toString(), doc2.getCreatedDate()
                        .toString());
            }
        };

        columnViewerSorter.setSorter(columnViewerSorter, AbstractColumnViewerSorter.DESC);
        tableViewer.setContentProvider(new TableContentProvider());
        return tableViewer;
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
                IDocument document = (IDocument) cell.getElement();
                cell.setText(document.getKeywords());
            }
        });
        new AbstractColumnViewerSorter(tableViewer, column2) {

            @Override
            protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                IDocument doc1 = (IDocument) e1;
                IDocument doc2 = (IDocument) e2;
                return compareStrings(doc1.getKeywords(), doc2.getKeywords());
            }
        };

        tableColumnLayout.setColumnData(column2.getColumn(), new ColumnWeightData(2, 75, true));
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
                IDocument document = (IDocument) cell.getElement();
                cell.setText(document.getDesclong());
            }
        });
        new AbstractColumnViewerSorter(tableViewer, column2) {

            @Override
            protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                IDocument doc1 = (IDocument) e1;
                IDocument doc2 = (IDocument) e2;
                return compareStrings(doc1.getDesclong(), doc2.getDesclong());
            }
        };

        tableColumnLayout.setColumnData(column2.getColumn(), new ColumnWeightData(6, 140, true));
    }

    /**
     * @param tableColumnLayout
     * @param tableViewer
     * @return
     */
    private static TableViewerColumn createHierarchyColumn(final TableColumnLayout tableColumnLayout,
                                              final TableViewer tableViewer) {
        // Column Hierarchy
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setText("Hierarchy");
        column.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(@Nonnull final ViewerCell cell) {
                cell.setText( ((IDocument) cell.getElement()).getLocation());
            }
        });
        new AbstractColumnViewerSorter(tableViewer, column) {

            @Override
            protected int doCompare(@Nullable final Viewer viewer,
                                    @Nullable final Object e1,
                                    @Nullable final Object e2) {
                if ( (e1 != null) && (e2 != null)) {
                    IDocument doc1 = (IDocument) e1;
                    IDocument doc2 = (IDocument) e2;
                    return compareStrings(doc1.getLocation(), doc2.getLocation());
                }
                return -1;
            }

        };

        tableColumnLayout.setColumnData(column.getColumn(), new ColumnWeightData(2, 100, true));

        return column;
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
                cell.setText(((IDocument) cell.getElement()).getCreatedDate().toString());
            }
        });

        tableColumnLayout.setColumnData(column.getColumn(), new ColumnWeightData(3, 80, true));

        return column;
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
                cell.setText(((IDocument) cell.getElement()).getSubject());
            }
        });
        new AbstractColumnViewerSorter(tableViewer, column) {

            @Override
            protected int doCompare(@Nullable final Viewer viewer,@Nullable  final Object e1,@Nullable  final Object e2) {
                if((e1!=null)&&(e2!=null)) {
                    IDocument doc1 = (IDocument) e1;
                    IDocument doc2 = (IDocument) e2;
                    return compareStrings(doc1.getSubject(), doc2.getSubject());
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
                cell.setText(((IDocument) cell.getElement()).getMimeType());
            }
        });

        new AbstractColumnViewerSorter(tableViewer, column) {

            @Override
            protected int doCompare(final Viewer viewer, final Object e1, final Object e2) {
                IDocument doc1 = (IDocument) e1;
                IDocument doc2 = (IDocument) e2;
                return compareStrings(doc1.getMimeType(), doc2.getMimeType());
            }
        };

        tableColumnLayout.setColumnData(column.getColumn(), new ColumnPixelData(30, true));
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
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.2 $
     * @since 17.08.2010
     */
    private abstract static class AbstractColumnViewerSorter extends ViewerComparator {
        public static final int ASC = 1;

        public static final int NONE = 0;

        public static final int DESC = -1;

        private int direction = 0;

        private final TableViewerColumn column;

        private final ColumnViewer viewer;

        public AbstractColumnViewerSorter(final ColumnViewer viewer, final TableViewerColumn column) {
            this.column = column;
            this.viewer = viewer;
            this.column.getColumn().addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(final SelectionEvent e) {
                    if( AbstractColumnViewerSorter.this.viewer.getComparator() != null ) {
                        if( AbstractColumnViewerSorter.this.viewer.getComparator() == AbstractColumnViewerSorter.this ) {
                            int tdirection = AbstractColumnViewerSorter.this.direction;

                            if( tdirection == ASC ) {
                                setSorter(AbstractColumnViewerSorter.this, DESC);
                            } else if( tdirection == DESC ) {
                                setSorter(AbstractColumnViewerSorter.this, NONE);
                            }
                        } else {
                            setSorter(AbstractColumnViewerSorter.this, ASC);
                        }
                    } else {
                        setSorter(AbstractColumnViewerSorter.this, ASC);
                    }
                }
            });
        }

        public void setSorter(final AbstractColumnViewerSorter sorter, final int direction) {
            if( direction == NONE ) {
                column.getColumn().getParent().setSortColumn(null);
                column.getColumn().getParent().setSortDirection(SWT.NONE);
                viewer.setComparator(null);
            } else {
                column.getColumn().getParent().setSortColumn(column.getColumn());
                sorter.direction = direction;

                if( direction == ASC ) {
                    column.getColumn().getParent().setSortDirection(SWT.DOWN);
                } else {
                    column.getColumn().getParent().setSortDirection(SWT.UP);
                }

                if( viewer.getComparator() == sorter ) {
                    viewer.refresh();
                } else {
                    viewer.setComparator(sorter);
                }

            }
        }

        @Override
        public int compare(final Viewer viewer, final Object e1, final Object e2) {
            return direction * doCompare(viewer, e1, e2);
        }

        protected abstract int doCompare(Viewer viewer, Object e1, Object e2);
    }

    public static void makeMenus(@Nonnull final TableViewer viewer) {
        Menu menu = new Menu(viewer.getControl());
        MenuItem showItem = new MenuItem(menu, SWT.PUSH);
        showItem.addSelectionListener(new ShowFileSelectionListener(viewer));
        showItem.setText("&Open");
        showItem.setImage(PlatformUI.getWorkbench().getSharedImages()
                .getImage(ISharedImages.IMG_OBJ_FOLDER));

        MenuItem saveItem = new MenuItem(menu, SWT.PUSH);
        saveItem.addSelectionListener(new SaveFileSelectionListener(viewer));
        saveItem.setText("&Save");
        saveItem.setImage(PlatformUI.getWorkbench().getSharedImages()
                .getImage(ISharedImages.IMG_ETOOL_SAVEAS_EDIT));

        MenuItem renameItem = new MenuItem(menu, SWT.PUSH);
        renameItem.addSelectionListener(new AddFile2DBSelectionListener(viewer));
        renameItem.setText("&Update");

        viewer.getTable().setMenu(menu);
    }


    /**
     * This class provides the content for the table.
     */
    private static class TableContentProvider implements IStructuredContentProvider {

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings("unchecked")
        public final Object[] getElements(final Object arg0) {
            if (arg0 instanceof List) {
                List<IDocument> list = (List<IDocument>) arg0;
                return list.toArray(new IDocument[list.size()]);
            } else if (arg0 instanceof Set) {
                Set docSet = (Set) arg0;
                return docSet.toArray(new IDocument[docSet.size()]);

            }

            return null;
        }

        /**
         * Disposes any resources.
         */
        @Override
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
        @Override
        public final void inputChanged(final Viewer arg0, final Object arg1, final Object arg2) {
            // do noting
        }
    }

    public static AddFile2DBSelectionListener getAddFile2DBSelectionListener(final TableViewer viewer) {
        return new AddFile2DBSelectionListener(viewer);
    }

    /**
     * @author Rickens Helge
     * @author $Author: $
     * @since 06.01.2011
     */
    private static class SaveFileSelectionListener implements SelectionListener {
        private final TableViewer _parentViewer;

        public SaveFileSelectionListener(@Nonnull final TableViewer parentViewer) {
            _parentViewer = parentViewer;
        }

        @Override
        public void widgetSelected(@Nullable final SelectionEvent e) {
            saveFileWithDialog();
        }

        @Override
        public void widgetDefaultSelected(@Nullable final SelectionEvent e) {
            saveFileWithDialog();
        }

        private void saveFileWithDialog() {
            Shell shell = Display.getCurrent().getActiveShell();
            FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
            StructuredSelection selection = (StructuredSelection) _parentViewer.getSelection();
            IDocument firstElement = (IDocument) selection.getFirstElement();
            fileDialog.setFileName(firstElement.getSubject() + "." + firstElement.getMimeType());
            String open = fileDialog.open();
            if (open != null) {
                File outFile = new File(open);
                try {
                    Helper.writeDocumentFile(outFile, firstElement);
                } catch (PersistenceException e) {
                    DeviceDatabaseErrorDialog.open(null, "Can't open Editor", e);
                    LOG.error("Can't open Editor", e);
                } catch (IOException e) {
                    MessageDialog.openError(null, "Can't File write!", e.getMessage());
                    LOG.error("Can't File write!", e);
                }
            }
        }
    }

    /**
     * @author Rickens Helge
     * @author $Author: $
     * @since 06.01.2011
     */
    private static class AddFile2DBSelectionListener implements SelectionListener {
        private final TableViewer _viewer;

        public AddFile2DBSelectionListener(@Nullable final TableViewer viewer) {
            _viewer = viewer;
        }

        @Override
        public void widgetDefaultSelected(@Nullable final SelectionEvent e) {
            addDocDialog();
        }

        @Override
        public void widgetSelected(@Nullable final SelectionEvent e) {
            addDocDialog();
        }

        private void addDocDialog() {
            DocumentDBO firstElement = null;
            if(_viewer!=null) {
                StructuredSelection selection = (StructuredSelection) _viewer.getSelection();
                firstElement = (DocumentDBO) selection.getFirstElement();
            }
            AddDocDialog addDocDialog = new AddDocDialog(new Shell(), firstElement);
            if (addDocDialog.open() == 0) {
                DocumentDBO document = addDocDialog.getDocument();
                try {
                    Repository.save(document);
                } catch (PersistenceException e) {
                    DeviceDatabaseErrorDialog.open(null, "Can't add File to Node", e);
                    LOG.error("Can't add File to Node", e);
                }
            }
        }
    }

}
