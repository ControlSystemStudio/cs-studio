/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.config.ioconfig.config.view.helper;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.DocumentDBO;
import org.csstudio.config.ioconfig.model.IDocument;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.csstudio.config.ioconfig.model.tools.Helper;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
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
public final class DocumentTableViewerBuilder {
    
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
                final StructuredSelection selection = (StructuredSelection) _viewer.getSelection();
                firstElement = (DocumentDBO) selection.getFirstElement();
            }
            final AddDocDialog addDocDialog = new AddDocDialog(new Shell(), firstElement);
            if (addDocDialog.open() == 0) {
                final DocumentDBO document = addDocDialog.getDocument();
                try {
                    Repository.save(document);
                } catch (final PersistenceException e) {
                    DeviceDatabaseErrorDialog.open(null, "Can't add File to Node", e);
                    LOG.error("Can't add File to Node", e);
                }
            }
        }
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
        public void widgetDefaultSelected(@Nullable final SelectionEvent e) {
            saveFileWithDialog();
        }
        
        @Override
        public void widgetSelected(@Nullable final SelectionEvent e) {
            saveFileWithDialog();
        }
        
        private void saveFileWithDialog() {
            final Shell shell = Display.getCurrent().getActiveShell();
            final FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
            final StructuredSelection selection = (StructuredSelection) _parentViewer.getSelection();
            final IDocument firstElement = (IDocument) selection.getFirstElement();
            fileDialog.setFileName(firstElement.getSubject() + "." + firstElement.getMimeType());
            final String open = fileDialog.open();
            if (open != null) {
                final File outFile = new File(open);
                try {
                    Helper.writeDocumentFile(outFile, firstElement);
                } catch (final PersistenceException e) {
                    DeviceDatabaseErrorDialog.open(null, "Can't open Editor", e);
                    LOG.error("Can't open Editor", e);
                } catch (final IOException e) {
                    MessageDialog.openError(null, "Can't File write!", e.getMessage());
                    LOG.error("Can't File write!", e);
                }
            }
        }
    }
    
    /**
     * This class provides the content for the table.
     */
    private static class TableContentProvider implements IStructuredContentProvider {
        
        /**
         * Constructor.
         */
        public TableContentProvider() {
            // Constructor.
        }
        
        /**
         * Disposes any resources.
         */
        @Override
        public final void dispose() {
            // We don't create any resources, so we don't dispose any
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings("unchecked")
        @CheckForNull
        public final Object[] getElements(@Nullable final Object arg0) {
            if (arg0 instanceof List) {
                final List<IDocument> list = (List<IDocument>) arg0;
                return list.toArray(new IDocument[list.size()]);
            } else if (arg0 instanceof Set) {
                final Set<IDocument> docSet = (Set<IDocument>) arg0;
                return docSet.toArray(new IDocument[docSet.size()]);
                
            }
            
            return null;
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
        public final void inputChanged(@Nonnull final Viewer arg0, @Nullable final Object arg1, @Nullable final Object arg2) {
            // do noting
        }
    }
    
    protected static final Logger LOG = LoggerFactory.getLogger(DocumentTableViewerBuilder.class);
    
    /**
     * Constructor.
     */
    private DocumentTableViewerBuilder() {
        // Constructor.
    }
    
    @Nonnull
    public static TableViewer crateDocumentTable(@Nonnull final Composite group, final boolean showHierarchy) {
        final TableColumnLayout tableColumnLayout = new TableColumnLayout();
        final Composite tableComposite = new Composite(group, SWT.BORDER);
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
        
        final AbstractColumnViewerSorter columnViewerSorter = new AbstractColumnViewerSorter(tableViewer, column) {
            
            @Override
            protected int doCompare(@Nonnull final Viewer viewer, @Nonnull final Object e1, @Nonnull final Object e2) {
                final IDocument doc1 = (IDocument) e1;
                final IDocument doc2 = (IDocument) e2;
                final Date createdDate1 = doc1.getCreatedDate();
                final Date createdDate2 = doc2.getCreatedDate();
                final String date1 = createdDate1==null?"":createdDate1.toString();
                final String date2 = createdDate2==null?"":createdDate2.toString();
                return compareStrings(date1, date2);
            }
        };
        
        columnViewerSorter.setSorter(columnViewerSorter, SORT_DIRECTION.DESC);
        tableViewer.setContentProvider(new TableContentProvider());
        return tableViewer;
    }
    
    @Nonnull 
    public static AddFile2DBSelectionListener getAddFile2DBSelectionListener(@Nonnull final TableViewer viewer) {
        return new AddFile2DBSelectionListener(viewer);
    }
    
    public static void makeMenus(@Nonnull final TableViewer viewer) {
        final Menu menu = new Menu(viewer.getControl());
        final MenuItem showItem = new MenuItem(menu, SWT.PUSH);
        showItem.addSelectionListener(new ShowFileSelectionListener(viewer));
        showItem.setText("&Open");
        showItem.setImage(PlatformUI.getWorkbench().getSharedImages()
                          .getImage(ISharedImages.IMG_OBJ_FOLDER));
        
        final MenuItem saveItem = new MenuItem(menu, SWT.PUSH);
        saveItem.addSelectionListener(new SaveFileSelectionListener(viewer));
        saveItem.setText("&Save");
        saveItem.setImage(PlatformUI.getWorkbench().getSharedImages()
                          .getImage(ISharedImages.IMG_ETOOL_SAVEAS_EDIT));
        
        final MenuItem renameItem = new MenuItem(menu, SWT.PUSH);
        renameItem.addSelectionListener(new AddFile2DBSelectionListener(viewer));
        renameItem.setText("&Update");
        
        viewer.getTable().setMenu(menu);
    }
    
    protected static int compareStrings(@CheckForNull final String string1, @CheckForNull final String string2) {
        if(string1==null&&string2==null) {
            return 0;
        } else if(string1==null) {
            return 1;
        } else if(string2==null) {
            return -1;
        }
        return string1.compareToIgnoreCase(string2);
    }
    
    @Nonnull 
    private static TableViewerColumn createDateColumn(@Nonnull final TableColumnLayout tableColumnLayout,
                                                      @Nonnull final TableViewer tableViewer) {
        final TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setText("Create Date");
        column.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(@Nonnull final ViewerCell cell) {
                final IDocument iDocument = (IDocument) cell.getElement();
                final Date createdDate = iDocument==null?null:iDocument.getCreatedDate();
                final String date = createdDate == null ? "" : createdDate.toString();
                cell.setText(date);
            }
        });

        tableColumnLayout.setColumnData(column.getColumn(), new ColumnWeightData(3, 80, true));
        
        return column;
    }
    
    
    /**
     * @param tableColumnLayout
     * @param tableViewer
     */
    @SuppressWarnings("unused")
    private static void createDescColumn(@Nonnull final TableColumnLayout tableColumnLayout,
                                         @Nonnull final TableViewer tableViewer) {
        final TableViewerColumn column2 = new TableViewerColumn(tableViewer, SWT.NONE);
        column2.getColumn().setText("Desc");
        column2.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(@Nonnull final ViewerCell cell) {
                final IDocument document = (IDocument) cell.getElement();
                cell.setText(document.getDesclong());
            }
        });
        new AbstractColumnViewerSorter(tableViewer, column2) {
            
            @Override
            protected int doCompare(@Nonnull final Viewer viewer, @Nonnull final Object e1, @Nonnull final Object e2) {
                final IDocument doc1 = (IDocument) e1;
                final IDocument doc2 = (IDocument) e2;
                return compareStrings(doc1.getDesclong(), doc2.getDesclong());
            }
        };
        
        tableColumnLayout.setColumnData(column2.getColumn(), new ColumnWeightData(6, 140, true));
    }
    
    @SuppressWarnings("unused")
    @Nonnull 
    private static TableViewerColumn createHierarchyColumn(@Nonnull final TableColumnLayout tableColumnLayout,
                                                           @Nonnull final TableViewer tableViewer) {
        // Column Hierarchy
        final TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
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
                if ( e1 != null && e2 != null) {
                    final IDocument doc1 = (IDocument) e1;
                    final IDocument doc2 = (IDocument) e2;
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
     */
    @SuppressWarnings("unused")
    private static void createKeywordsColumn(@Nonnull final TableColumnLayout tableColumnLayout,
                                             @Nonnull final TableViewer tableViewer) {
        final TableViewerColumn column2 = new TableViewerColumn(tableViewer, SWT.NONE);
        column2.getColumn().setText("Key Words");
        column2.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(@Nonnull final ViewerCell cell) {
                final IDocument document = (IDocument) cell.getElement();
                cell.setText(document.getKeywords());
            }
        });
        new AbstractColumnViewerSorter(tableViewer, column2) {
            
            @Override
            protected int doCompare(@Nonnull final Viewer viewer, @Nonnull final Object e1, @Nonnull final Object e2) {
                final IDocument doc1 = (IDocument) e1;
                final IDocument doc2 = (IDocument) e2;
                return compareStrings(doc1.getKeywords(), doc2.getKeywords());
            }
        };
        
        tableColumnLayout.setColumnData(column2.getColumn(), new ColumnWeightData(2, 75, true));
    }
    
    /**
     * @param tableColumnLayout
     * @param tableViewer
     */
    @SuppressWarnings("unused")
    private static void createSubjectColumn(@Nonnull final TableColumnLayout tableColumnLayout,
                                            @Nonnull final TableViewer tableViewer) {
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
                if(e1!=null&&e2!=null) {
                    final IDocument doc1 = (IDocument) e1;
                    final IDocument doc2 = (IDocument) e2;
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
            protected int doCompare(@Nonnull final Viewer viewer, @Nonnull final Object e1, @Nonnull final Object e2) {
                final IDocument doc1 = (IDocument) e1;
                final IDocument doc2 = (IDocument) e2;
                return compareStrings(doc1.getMimeType(), doc2.getMimeType());
            }
        };
        
        tableColumnLayout.setColumnData(column.getColumn(), new ColumnPixelData(30, true));
    }
    
}
