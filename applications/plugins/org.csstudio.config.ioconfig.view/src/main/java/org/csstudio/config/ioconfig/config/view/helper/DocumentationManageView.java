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
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.csstudio.config.ioconfig.config.view.NodeConfig;
import org.csstudio.config.ioconfig.model.Document;
import org.csstudio.config.ioconfig.model.IDocument;
import org.csstudio.config.ioconfig.model.Node;
import org.csstudio.config.ioconfig.model.Repository;
import org.csstudio.config.ioconfig.model.tools.Helper;
import org.csstudio.config.ioconfig.view.Activator;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 20.02.2008
 */
public class DocumentationManageView extends Composite {

    private final class SaveFileSelectionListener implements SelectionListener {
        private final TableViewer _parentViewer;

        public SaveFileSelectionListener(TableViewer parentViewer) {
            _parentViewer = parentViewer;
        }

        public void widgetSelected(SelectionEvent e) {
            saveFileWithDialog();
        }

        public void widgetDefaultSelected(SelectionEvent e) {
            saveFileWithDialog();
        }

        private void saveFileWithDialog() {
            FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
            StructuredSelection selection = (StructuredSelection) _parentViewer.getSelection();
            Document firstElement = (Document) selection.getFirstElement();
            fileDialog.setFileName(firstElement.getSubject() + "." + firstElement.getMimeType());
            String open = fileDialog.open();
            if (open != null) {
                File outFile = new File(open);
                try {
                    Helper.writeDocumentFile(outFile, firstElement);
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private final class AddFile2DBSelectionListener implements SelectionListener {
        private final TableViewer _viewer;

        public AddFile2DBSelectionListener(TableViewer viewer) {
            _viewer = viewer;
        }

        public void widgetDefaultSelected(SelectionEvent e) {
            addDocDialog();
        }

        public void widgetSelected(SelectionEvent e) {
            addDocDialog();
        }

        private void addDocDialog() {
            Document firstElement = null;
            if(_viewer!=null) {
                StructuredSelection selection = (StructuredSelection) _viewer.getSelection();
                firstElement = (Document) selection.getFirstElement();
            }
            AddDocDialog addDocDialog = new AddDocDialog(new Shell(), firstElement);
            if (addDocDialog.open() == 0) {
                Document document = addDocDialog.getDocument();
                Repository.save(document);
            }
        }
    }

    private final class ShowFileSelectionListener implements SelectionListener {
        private final TableViewer _parentViewer;

        public ShowFileSelectionListener(TableViewer parentViewer) {
            _parentViewer = parentViewer;
        }

        public void widgetSelected(SelectionEvent e) {
            openFileInBrowser();
        }

        public void widgetDefaultSelected(SelectionEvent e) {
            openFileInBrowser();
        }

        private void openFileInBrowser() {
            StructuredSelection selection = (StructuredSelection) _parentViewer.getSelection();
            Document firstElement = (Document) selection.getFirstElement();
            File createTempFile = null;
            try {
                String filename = firstElement.getSubject();
                if (filename == null || filename.length() < 1) {
                    filename = "showTmp";
                }
                createTempFile = File.createTempFile(filename, "." + firstElement.getMimeType());
                Helper.writeDocumentFile(createTempFile, firstElement);
                if (createTempFile != null && createTempFile.isFile()) {
                    if (Desktop.isDesktopSupported()) {
                        if (Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                            Desktop.getDesktop().open(createTempFile);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

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
            if (element instanceof IDocument) {
                boolean status = false;
                IDocument doc = (IDocument) element;

                if (doc.getSubject() != null) {
                    status |= doc.getSubject().toLowerCase().contains(_filterString.toLowerCase());
                }
                if (doc.getMimeType() != null) {
                    status |= doc.getMimeType().toLowerCase().contains(_filterString.toLowerCase());
                }
                if (doc.getDesclong() != null) {
                    status |= doc.getDesclong().toLowerCase().contains(_filterString.toLowerCase());
                }
                if (doc.getCreatedDate() != null) {
                    status |= doc.getCreatedDate().toString().contains(_filterString);
                }
                if (doc.getKeywords() != null) {
                    status |= doc.getKeywords().toLowerCase().contains(_filterString.toLowerCase());
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
                return docSet.toArray(new IDocument[docSet.size()]);

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
    private Composite _mainComposite;

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
        this.setLayout(new GridLayout(1,false));
        GridData layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
        this.setLayoutData(layoutData);
        _parentNodeConfig = parentNodeConfig;
        
        // -Body
        GridLayoutFactory fillDefaults = GridLayoutFactory.fillDefaults();
        ScrolledComposite scrolledComposite = new ScrolledComposite(this,
                SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,1,1));
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);
        fillDefaults.numColumns(3);
        scrolledComposite.setLayout(fillDefaults.create());

        _mainComposite = new Composite(scrolledComposite, SWT.NONE);
        _mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        fillDefaults.numColumns(3);
        _mainComposite.setLayout(fillDefaults.create());
        
        scrolledComposite.setContent(_mainComposite);
        scrolledComposite.setMinSize(700,250);
       
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
        TableViewerEditor.create(_docResorceTableViewer, new ColumnViewerEditorActivationStrategy(
                _docResorceTableViewer), ColumnViewerEditor.DEFAULT);

        // makeMenus(_docAvailableTableViewer);
        makeMenus(_docResorceTableViewer);
    }

    private void makeMenus(TableViewer viewer) {
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

    private Composite makeGroup(String groupHead) {
        Group searchGroup = new Group(_mainComposite, SWT.NO_SCROLL);
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
        Composite chosserComposite = new Composite(_mainComposite, SWT.NONE);
        GridData layoutData = new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1);
        chosserComposite.setLayoutData(layoutData);
        GridLayoutFactory fillDefaults = GridLayoutFactory.fillDefaults();
        GridLayout create = fillDefaults.create();
        create.marginTop=15;
        chosserComposite.setLayout(create);

        Button refreshButton = new Button(chosserComposite, SWT.FLAT);
        refreshButton.setLayoutData(new GridData(SWT.CENTER, SWT.BEGINNING, false, false));
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
        addNewDocButton.addSelectionListener(new AddFile2DBSelectionListener(null));

        new Label(chosserComposite, SWT.NONE);
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

        label = new Label(chosserComposite, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
//        // Save
//        Button saveButton = new Button(chosserComposite, SWT.PUSH);
//        saveButton.setText("Save");
//        saveButton.setToolTipText("Show the selected Documents");
//        saveButton.setEnabled(true);
//        saveButton.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, false));
//        saveButton.addSelectionListener(new SaveFileSelectionListener(_docAvailableTableViewer));
//
//        // Show
//        Button showButton = new Button(chosserComposite, SWT.PUSH);
//        showButton.setText("Show");
//        showButton.setToolTipText("Show the selected Documents");
//        showButton.setEnabled(true);
//        showButton.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, false, false));
//        showButton.addSelectionListener(new ShowFileSelectionListener(_docResorceTableViewer));

    }

    /**
     * 
     */
    private void makeAvailableDocTable() {
//        /* final */TableSorter sorter = new TableSorter(0, false, 1, false);
        // ViewerSorter sorter = new ViewerSorter();
        Composite availableGroup = makeGroup("Available");

        // Table table = makeTable(availableGroup, _docAvailableTableViewer);
        // _docAvailableTableViewer = new TableViewer(table);
        // _docAvailableTableViewer.setLabelProvider(new DocTableLabelProvider());

        _docAvailableTableViewer = makeTable(availableGroup);

        _docAvailableTableViewer.setContentProvider(new TableContentProvider());
//        _docAvailableTableViewer.setSorter(sorter);

        makeMenus(_docAvailableTableViewer);
    }

    private TableViewer makeTable(Composite group) {
        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        Composite tableComposite = new Composite(group, SWT.BORDER);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(tableComposite);
        tableComposite.setLayout(tableColumnLayout);

        String[] columnHeads = new String[] { "Desc", "Key Words" };
        int[] columnWeigth = new int[] { 6, 2 };
        int[] columnWidths = new int[] { 140, 75 };

        final TableViewer tableViewer = new TableViewer(tableComposite, SWT.H_SCROLL | SWT.V_SCROLL
                | SWT.MULTI | SWT.FULL_SELECTION);
        tableViewer.getTable().setHeaderVisible(true);
        tableViewer.getTable().setLinesVisible(true);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(tableViewer.getTable());

        // Column Subject
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setText("Subject");
        column.setLabelProvider(new CellLabelProvider() {
            public void update(ViewerCell cell) {
                cell.setText(((IDocument) cell.getElement()).getSubject());
            }
        });
        new ColumnViewerSorter(tableViewer, column) {
            
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                Document doc1 = (Document) e1;  
                Document doc2 = (Document) e2;  
                return compareStrings(doc1.getSubject(), doc2.getSubject());
            }

        };
        
        tableColumnLayout.setColumnData(column.getColumn(), new ColumnWeightData(2, 100, true));

        // Column Mime Type
        column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setText("MimeType");
        column.setLabelProvider(new CellLabelProvider() {
            public void update(ViewerCell cell) {
                cell.setText(((IDocument) cell.getElement()).getMimeType());
            }
        });
        
        new ColumnViewerSorter(tableViewer, column) {
            
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                Document doc1 = (Document) e1;  
                Document doc2 = (Document) e2;  
                return compareStrings(doc1.getMimeType(), doc2.getMimeType());
            }
        };
        
        tableColumnLayout.setColumnData(column.getColumn(), new ColumnPixelData(30, true));

        // Column Create Date
        column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.getColumn().setText("Create Date");
        column.setLabelProvider(new CellLabelProvider() {
            public void update(ViewerCell cell) {
                cell.setText(((IDocument) cell.getElement()).getCreatedDate().toString());
            }
        });
        
        ColumnViewerSorter columnViewerSorter = new ColumnViewerSorter(tableViewer, column) {
            
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                Document doc1 = (Document) e1;  
                Document doc2 = (Document) e2;  
                return compareStrings(doc1.getCreatedDate().toString(), doc2.getCreatedDate().toString());
            }
        };
        
        tableColumnLayout.setColumnData(column.getColumn(), new ColumnWeightData(3, 80, true));
        //
        for (int i = 0; i < columnWidths.length; i++) {
            final TableViewerColumn column2 = new TableViewerColumn(tableViewer, SWT.NONE);
            column2.getColumn().setText(columnHeads[i]);
            final int cloumnNo = i;
            column2.setLabelProvider(new CellLabelProvider() {
                @Override
                public void update(ViewerCell cell) {
                    IDocument document = (IDocument) cell.getElement();
                    switch (cloumnNo) {
                        case 0:
                            cell.setText(document.getDesclong());
                            break;
                        case 1:
                            cell.setText(document.getKeywords());
                            break;
                        default:
                            break;
                    }
                }
            });
            new ColumnViewerSorter(tableViewer, column2) {
                
                @Override
                protected int doCompare(Viewer viewer, Object e1, Object e2) {
                    Document doc1 = (Document) e1;  
                    Document doc2 = (Document) e2;
                    switch (cloumnNo) {
                        case 0:
                            return compareStrings(doc1.getDesclong(), doc2.getDesclong());
                        case 1:
                            return compareStrings(doc1.getKeywords(), doc2.getKeywords());
                    }
                    return 0;
                }
            };

            tableColumnLayout.setColumnData(column2.getColumn(), new ColumnWeightData(
                    columnWeigth[i], columnWidths[i], true));
        }
        columnViewerSorter.setSorter(columnViewerSorter, ColumnViewerSorter.DESC);
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

    private static abstract class ColumnViewerSorter extends ViewerComparator {
        public static final int ASC = 1;
        
        public static final int NONE = 0;
        
        public static final int DESC = -1;
        
        private int direction = 0;
        
        private TableViewerColumn column;
        
        private ColumnViewer viewer;
        
        public ColumnViewerSorter(ColumnViewer viewer, TableViewerColumn column) {
            this.column = column;
            this.viewer = viewer;
            this.column.getColumn().addSelectionListener(new SelectionAdapter() {

                public void widgetSelected(SelectionEvent e) {
                    if( ColumnViewerSorter.this.viewer.getComparator() != null ) {
                        if( ColumnViewerSorter.this.viewer.getComparator() == ColumnViewerSorter.this ) {
                            int tdirection = ColumnViewerSorter.this.direction;
                            
                            if( tdirection == ASC ) {
                                setSorter(ColumnViewerSorter.this, DESC);
                            } else if( tdirection == DESC ) {
                                setSorter(ColumnViewerSorter.this, NONE);
                            }
                        } else {
                            setSorter(ColumnViewerSorter.this, ASC);
                        }
                    } else {
                        setSorter(ColumnViewerSorter.this, ASC);
                    }
                }
            });
        }
        
        public void setSorter(ColumnViewerSorter sorter, int direction) {
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

        public int compare(Viewer viewer, Object e1, Object e2) {
            return direction * doCompare(viewer, e1, e2);
        }
        
        protected abstract int doCompare(Viewer viewer, Object e1, Object e2);
    }

    private int compareStrings(String string1, String string2) {
        if(string1==null&&string2==null) {
            return 0;
        } else if(string1==null) {
            return 1;
        } else if(string2==null) {
            return -1;
        }
        return string1.compareToIgnoreCase(string2);
    }

}
