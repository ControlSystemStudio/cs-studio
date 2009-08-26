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
/*
 * $Id$
 */
package org.csstudio.config.ioconfig.config.view.helper;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.csstudio.config.ioconfig.config.view.NodeConfig;
import org.csstudio.config.ioconfig.model.Document;
import org.csstudio.config.ioconfig.model.Node;
import org.csstudio.config.ioconfig.model.Repository;
import org.csstudio.config.ioconfig.view.Activator;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 20.02.2008
 */
public class DocumentationManageView extends Composite {

    /**
     * @author hrickens
     * @author $Author$
     * @version $Revision$
     * @since 07.08.2009
     */
    private final class ViewerFilterExtension extends ViewerFilter {
        private String _filterString = "";

        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            if (element instanceof Document) {
                boolean status = false;
                Document doc = (Document) element;

                if (doc.getSubject() != null) {
                    status |= doc.getSubject().contains(_filterString);
                }
                if (doc.getMimeType() != null) {
                    status |= doc.getMimeType().contains(_filterString);
                }
                if (doc.getDesclong() != null) {
                    status |= doc.getDesclong().contains(_filterString);
                }
                if (doc.getCreatedDate() != null) {
                    status |= doc.getCreatedDate().toString().contains(_filterString);
                }
                if (doc.getKeywords() != null) {
                    status |= doc.getKeywords().contains(_filterString);
                }
                return status;
            }
            return false;
        }

        public void setFilterText(String filterString) {
            _filterString = filterString;
        }
    }

    /**
     * This class provides the content for the table.
     */
    public class TableContentProvider implements IStructuredContentProvider {

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        public final Object[] getElements(final Object arg0) {
            if (arg0 instanceof List) {
                List<Document> list = (List<Document>) arg0;
                return list.toArray(new Document[list.size()]);
            } else if (arg0 instanceof Set) {
                Set docSet = (Set) arg0;
                return docSet.toArray(new Document[docSet.size()]);

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
        }
    }

    /**
     * 
     * @author hrickens
     * @author $Author$
     * @version $Revision$
     * @since 18.03.2008
     */
    public class TableSorter extends ViewerSorter {
        /**
         * The Sort column.
         */
        private int _column;
        /**
         * Direction of sort for _column.
         */
        private boolean _backward;
        /**
         * Last sort column.
         */
        private int _lastColumn;
        /**
         * Direction of sort for _lastSortBackward.
         */
        private boolean _lastSortBackward;

        /**
         * 
         * @param column
         *            the column to sort used.
         * @param backward
         *            the sort direction.
         * @param lastColumn
         *            the last column to sort used.
         * @param lastSortBackward
         *            the sort direction for the last column.
         */
        public TableSorter(final int column, final boolean backward, final int lastColumn,
                final boolean lastSortBackward) {
            _column = column;
            _backward = backward;
            _lastColumn = lastColumn;
            _lastSortBackward = lastSortBackward;
        }

        /**
         * Sort a table at the last two selected table header. {@inheritDoc}
         */
        @Override
        public final int compare(final Viewer viewer, final Object o1, final Object o2) {
            if (o1 instanceof Document && o2 instanceof Document) {
                Document doc1 = (Document) o1;
                Document doc2 = (Document) o2;
                int multi = -1;
                int erg = 0;
                if (_backward) {
                    multi = 1;
                }
                
                erg = compareColumn(_column, doc1, doc2, multi);
                
                if (erg == 0) {
                    multi = -1;
                    if (_lastSortBackward) {
                        multi = 1;
                    }
                    erg = compareColumn(_lastColumn, doc1, doc2, multi);
                }
                return erg;
            } else {
                return 0;
            }
        }

        private int compareColumn(int column, Document doc1, Document doc2, int multi) {
            int resulte;
            switch (column) {
                default:
                case 0:
                    if(doc1.getSubject()==null&&doc2.getSubject()==null) {
                        resulte = 0;
                    } else if(doc1.getSubject()==null) {
                        resulte = multi;
                    } else if(doc2.getSubject()==null) {
                        resulte = -1*multi;
                    } else {
                        resulte = multi * doc1.getSubject().compareTo(doc2.getSubject());
                    }
                    break;
                case 1:
                    if(doc1.getDesclong()==null&&doc2.getDesclong()==null) {
                        resulte = 0;
                    } else if(doc1.getDesclong()==null) {
                        resulte = multi;
                    } else if(doc2.getDesclong()==null) {
                        resulte = -1*multi;
                    } else {
                        resulte = multi * doc1.getDesclong().compareTo(doc2.getDesclong());
                    }
                    break;
                case 2:
                    if(doc1.getKeywords()==null&&doc2.getKeywords()==null) {
                        resulte = 0;
                    } else if(doc1.getKeywords()==null) {
                        resulte = multi;
                    } else if(doc2.getKeywords()==null) {
                        resulte = -1*multi;
                    } else {
                        resulte = multi * doc1.getKeywords().compareTo(doc2.getKeywords());
                    }
                    break;
            }
            return resulte;
        }

        /**
         * Set the a new column to use for sorting.
         * 
         * @param column
         *            the column that are used.
         */
        public final void setColumn(final int column) {
            if (_column == column) {
                _backward = !_backward;
            } else {
                _lastColumn = column;
                _lastSortBackward = _backward;
                _column = column;
                _backward = false;
            }
        }

    }

    // private Composite _mainComposite;
    /**
     * The table with a list of all not assigned documents.
     */
    private TableViewer _docResorceTableViewer;
    /**
     * The table with a list of all assigned documents.
     */
    private TableViewer _docAvailableTableViewer;
    private NodeConfig _parentNodeConfig;
    private ArrayList<Document> _originDocs;
    /**
     * A List whit all Documents.
     */
    private List<Document> _documentResorce;
    /**
     * A List whit the assigned Documents.
     */
    private List<Document> _documentAvailable = new ArrayList<Document>();
    private boolean _isActicate = false;

    /**
     * @param parent
     *            The Parent Composite.
     * @param style
     *            The Composite Style.
     * @param parentNodeConfig
     *            The parent Node configuration.
     */
    public DocumentationManageView(final Composite parent, final int style,
            final NodeConfig parentNodeConfig) {
        super(parent, style);
        _parentNodeConfig = parentNodeConfig;
        GridLayout layout = new GridLayout(3, false);
        this.setLayout(layout);
        GridData layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
        this.setLayoutData(layoutData);
        makeSearchDocTable();
        makeChooser();
        makeAvailableDocTable();
    }

    /**
     * Generate the Table with the received Documents.
     */
    private void makeSearchDocTable() {
        final ViewerFilterExtension filter = new ViewerFilterExtension();

        // GROUP Layout
        Composite searchGroup = makeGroup("Search");

        final Text search = new Text(searchGroup, SWT.SINGLE | SWT.SEARCH | SWT.LEAD | SWT.BORDER);
        search.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        search.setMessage("Filter");
        search.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                filter.setFilterText(search.getText());
                _docResorceTableViewer.refresh();
            }

        });

        _docResorceTableViewer = makeTable(searchGroup);

        _docResorceTableViewer.setContentProvider(new TableContentProvider());
        _documentResorce = Repository.loadDocument(false);
        _docResorceTableViewer.addFilter(filter);
        _docResorceTableViewer.setFilters(new ViewerFilter[] { filter });

    }

    private Composite makeGroup(String groupHead) {
        Group searchGroup = new Group(this, SWT.NO_SCROLL);
        GridLayout layout = new GridLayout(1, true);
        searchGroup.setLayout(layout);
        searchGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        searchGroup.setText(groupHead);
        return searchGroup;
    }

    /**
     * 
     */
    private void makeChooser() {
        Composite chosserComposite = new Composite(this, SWT.NONE);
        chosserComposite.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1));
        chosserComposite.setLayout(new GridLayout(1, false));

        Button refreshButton = new Button(chosserComposite, SWT.FLAT);
        refreshButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
        refreshButton.setImage(Activator.getImageDescriptor("icons/refresh.gif").createImage());
        refreshButton.setToolTipText("Refresh List of Documents");
        refreshButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {
                refreshDocuments();
            }

            public void widgetSelected(SelectionEvent e) {
                refreshDocuments();
            }

            private void refreshDocuments() {
                _documentResorce = Repository.loadDocument(true);
                _docResorceTableViewer.setInput(_documentResorce);
            }

        });
        Label label = new Label(chosserComposite, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
        Button addNewDocButton = new Button(chosserComposite, SWT.PUSH);
        addNewDocButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        addNewDocButton.setText("<New");
        addNewDocButton.setToolTipText("Add a new Document from the File-System");
        addNewDocButton.setToolTipText("Add a new Document to the Database");
        addNewDocButton.setEnabled(true);
        addNewDocButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {
                addDocDialog();
            }

            public void widgetSelected(SelectionEvent e) {
                addDocDialog();
            }

            private void addDocDialog() {
                AddDocDialog addDocDialog = new AddDocDialog(new Shell()) {

                };
                if (addDocDialog.open() == 0) {

                }
            }

        });

        // Add
        Button addAllButton = new Button(chosserComposite, SWT.PUSH);
        addAllButton.setText(">>");
        addAllButton.setToolTipText("Add all Documents");
        addAllButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        addAllButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(final SelectionEvent e) {
                addAll();
            }

            public void widgetSelected(final SelectionEvent e) {
                addAll();
            }

            private void addAll() {
                _documentAvailable.addAll(_documentResorce);
                _documentResorce.clear();
                _docAvailableTableViewer.setInput(_documentAvailable);
                _docResorceTableViewer.setInput(_documentResorce);
                setSaveButton();
            }

        });

        Button addButton = new Button(chosserComposite, SWT.PUSH);
        addButton.setText(">");
        addButton.setToolTipText("Add all selceted Documents");
        addButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        addButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {
                doAddDoc();
            }

            public void widgetSelected(SelectionEvent e) {
                doAddDoc();
            }

            @SuppressWarnings("unchecked")
            private void doAddDoc() {
                IStructuredSelection sSelect = (IStructuredSelection) _docResorceTableViewer
                        .getSelection();
                _documentResorce.removeAll(sSelect.toList());
                _documentAvailable.addAll(sSelect.toList());
                _docAvailableTableViewer.setInput(_documentAvailable);
                _docResorceTableViewer.setInput(_documentResorce);
                setSaveButton();
            }

        });

        // Remove
        Button removeButton = new Button(chosserComposite, SWT.PUSH);
        removeButton.setText("<");
        removeButton.setToolTipText("Remove all selceted Documents");
        removeButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        removeButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(/* final */SelectionEvent e) {
                doRemoveDoc();
            }

            public void widgetSelected(/* final */SelectionEvent e) {
                doRemoveDoc();
            }

            @SuppressWarnings("unchecked")
            private void doRemoveDoc() {
                IStructuredSelection sSelect = (IStructuredSelection) _docAvailableTableViewer
                        .getSelection();
                _documentResorce.addAll(sSelect.toList());
                _documentAvailable.removeAll(sSelect.toList());
                _docAvailableTableViewer.setInput(_documentAvailable);
                _docResorceTableViewer.setInput(_documentResorce);
                setSaveButton();
            }

        });

        Button removeAllButton = new Button(chosserComposite, SWT.PUSH);
        removeAllButton.setText("<<");
        removeAllButton.setToolTipText("Remove all Documents");
        removeAllButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        removeAllButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(final SelectionEvent e) {
                _documentResorce.addAll(_documentAvailable);
                _documentAvailable.clear();
                _docAvailableTableViewer.setInput(_documentAvailable);
                _docResorceTableViewer.setInput(_documentResorce);
                setSaveButton();
            }

            public void widgetSelected(final SelectionEvent e) {
                _documentResorce.addAll(_documentAvailable);
                _documentAvailable.clear();
                _docAvailableTableViewer.setInput(_documentAvailable);
                _docResorceTableViewer.setInput(_documentResorce);
                setSaveButton();
            }

        });

        // Save
        label = new Label(chosserComposite, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
        Button saveButton = new Button(chosserComposite, SWT.PUSH);
        saveButton.setText("Save");
        saveButton.setToolTipText("Show the selected Documents");
        saveButton.setEnabled(true);
        saveButton.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, false));
        saveButton.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                saveFileWithDialog();
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                saveFileWithDialog();
            }

            private void saveFileWithDialog() {
                FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
                StructuredSelection selection = (StructuredSelection) _docAvailableTableViewer
                        .getSelection();
                Document firstElement = (Document) selection.getFirstElement();
                fileDialog
                        .setFileName(firstElement.getSubject() + "." + firstElement.getMimeType());
                String open = fileDialog.open();
                if (open != null) {
                    File outFile = new File(open);
                    writeDocumentFile(outFile, firstElement);
                }
            }

        });

        // Show
        // label = new Label(chosserComposite, SWT.NONE);
        // label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
        Button showButton = new Button(chosserComposite, SWT.PUSH);
        showButton.setText("Show");
        showButton.setToolTipText("Show the selected Documents");
        showButton.setEnabled(true);
        showButton.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, false));
        showButton.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                openFileInBrowser();
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                openFileInBrowser();
            }

            private void openFileInBrowser() {
                StructuredSelection selection = (StructuredSelection) _docAvailableTableViewer
                        .getSelection();
                Document firstElement = (Document) selection.getFirstElement();
                File createTempFile = null;
                try {
                    String filename = firstElement.getSubject();
                    if(filename==null||filename.length()<1) {
                        filename = "showTmp";
                    }
                    createTempFile = File.createTempFile(filename, "."
                            + firstElement.getMimeType());
                    writeDocumentFile(createTempFile, firstElement);
                    if(createTempFile!=null&&createTempFile.isFile()) {
                        if(Desktop.isDesktopSupported()) {
                            //TODO: The VM crashed when open or Brows a File.
                            if(Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                                CentralLogger.getInstance().debug(this,"Desktop unterstützt Open!");
                                Desktop.getDesktop().open(createTempFile);
                            }
                            if(Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                                CentralLogger.getInstance().debug(this,"Desktop unterstützt Browse!");
                                Desktop.getDesktop().browse(createTempFile.toURI());
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 
     */
    private void makeAvailableDocTable() {
        /* final */TableSorter sorter = new TableSorter(0, false, 1, false);
        // ViewerSorter sorter = new ViewerSorter();
        Composite availableGroup = makeGroup("Available");

        // Table table = makeTable(availableGroup, _docAvailableTableViewer);
        // _docAvailableTableViewer = new TableViewer(table);
        // _docAvailableTableViewer.setLabelProvider(new DocTableLabelProvider());

        _docAvailableTableViewer = makeTable(availableGroup);

        _docAvailableTableViewer.setContentProvider(new TableContentProvider());
        _docAvailableTableViewer.setSorter(sorter);
    }

    private TableViewer makeTable(Composite group) {
        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        Composite tableComposite = new Composite(group, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(tableComposite);
        tableComposite.setLayout(tableColumnLayout);

        String[] columnHeads = new String[] { "Desc", "Create Date", "Key Words" };
        int[] columnWeigth = new int[] { 3, 1, 1 };
        int[] columnWidths = new int[] { 150, 75, 75 };

        TableViewer tableViewer = new TableViewer(tableComposite, SWT.H_SCROLL | SWT.V_SCROLL
                | SWT.MULTI | SWT.FULL_SELECTION);
        tableViewer.getTable().setHeaderVisible(true);
        tableViewer.getTable().setLinesVisible(true);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(tableViewer.getTable());

        // Column Subject
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setText("Subject");
        column.setLabelProvider(new CellLabelProvider() {
            public void update(ViewerCell cell) {
                cell.setText(((Document) cell.getElement()).getSubject());
            }
        });
        tableColumnLayout.setColumnData(column.getColumn(), new ColumnWeightData(2, 100, true));

        // Column Mime Type
        column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setText("MimeType");
        column.setLabelProvider(new CellLabelProvider() {
            public void update(ViewerCell cell) {
                cell.setText(((Document) cell.getElement()).getMimeType());
            }
        });
        tableColumnLayout.setColumnData(column.getColumn(), new ColumnPixelData(30, true));

        //
        for (int i = 0; i < columnWidths.length; i++) {
            column = new TableViewerColumn(tableViewer, SWT.NONE);
            column.getColumn().setText(columnHeads[i]);
            final int cloumnNo = i;
            column.setLabelProvider(new CellLabelProvider() {
                @Override
                public void update(ViewerCell cell) {
                    switch (cloumnNo) {
                        case 0:
                            cell.setText(((Document) cell.getElement()).getDesclong());
                            break;
                        case 1:
                            cell
                                    .setText(((Document) cell.getElement()).getCreatedDate()
                                            .toString());
                            break;
                        case 2:
                            cell.setText(((Document) cell.getElement()).getKeywords());
                            break;
                        default:
                            break;
                    }
                }
            });
            tableColumnLayout.setColumnData(column.getColumn(), new ColumnWeightData(
                    columnWeigth[i], columnWidths[i], true));
        }
        return tableViewer;
    }

    private void setDocs(final Set<Document> set) {
        _originDocs = new ArrayList<Document>(set);
        _documentAvailable = new ArrayList<Document>(set);
        if (set != null) {
            _docAvailableTableViewer.setInput(set);
            if (_documentResorce != null) {
                _documentResorce.removeAll(set);
            }
        }
        _docResorceTableViewer.setInput(_documentResorce);
    }

    /**
     * 
     * @return
     */
    private void setSaveButton() {
        if (_originDocs.size() == _documentAvailable.size()) {
            ArrayList<Document> temp = new ArrayList<Document>(_originDocs);
            temp.removeAll(_documentAvailable);
            _parentNodeConfig.setSavebuttonEnabled("documentaion", temp.size() != 0);
        } else {
            _parentNodeConfig.setSavebuttonEnabled("documentaion", true);
        }
    }

    /**
     * @return all available Documents.
     */
    public Set<Document> getDocuments() {
        // _docAvailableTableViewer.getTable().selectAll();
        // IStructuredSelection structSele = (IStructuredSelection) _docAvailableTableViewer
        // .getSelection();

        Set<Document> set = new HashSet<Document>(_documentAvailable);
        return set;
    }

    public void cancel() {
        if (_documentResorce != null) {
            _documentResorce.addAll(_documentAvailable);
        }
        _documentAvailable.clear();
        if (_originDocs != null) {
            _documentAvailable.addAll(_originDocs);
            if (_documentResorce != null) {
                _documentResorce.removeAll(_originDocs);
            }
        }
        _docAvailableTableViewer.setInput(_documentAvailable);
        _docResorceTableViewer.setInput(_documentResorce);
    }

    public void onActivate() {
        if (!_isActicate) {
            Node node = _parentNodeConfig.getNode();
            setDocs(node.getDocuments());
            _isActicate = true;
        }
    }

    private static void writeDocumentFile(File outFile, Document document) {
        try {
            InputStream inputStream;
            inputStream = document.getImage().getBinaryStream();
            byte[] buffer = new byte[8192];
            int bytesRead = 0;
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(outFile);
                while ((bytesRead = inputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
                fileOutputStream.flush();
                inputStream.close();
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
