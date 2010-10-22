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
 * $Id: DocumentationManageView.java,v 1.10 2010/08/20 13:33:00 hrickens Exp $
 */
package org.csstudio.config.ioconfig.config.view.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.config.view.IHasDocumentableObject;
import org.csstudio.config.ioconfig.model.DocumentDBO;
import org.csstudio.config.ioconfig.model.IDocument;
import org.csstudio.config.ioconfig.model.IDocumentable;
import org.csstudio.config.ioconfig.model.Repository;
import org.csstudio.config.ioconfig.view.ActivatorUI;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.10 $
 * @since 20.02.2008
 */
public class DocumentationManageView extends Composite {

    /**
     * The table with a list of all not assigned documents.
     */
    private TableViewer _docResorceTableViewer;
    /**
     * The table with a list of all assigned documents.
     */
    private TableViewer _docAvailableTableViewer;
    private final IHasDocumentableObject _parentNodeConfig;
    private ArrayList<DocumentDBO> _originDocs;
    /**
     * A List whit all Documents.
     */
    private List<DocumentDBO> _documentResorce;
    /**
     * A List whit the assigned Documents.
     */
    private List<DocumentDBO> _documentAvailable = new ArrayList<DocumentDBO>();
    private boolean _isActivate = false;
    private final Composite _mainComposite;

    /**
     * @param parent
     *            The Parent Composite.
     * @param style
     *            The Composite Style.
     * @param parentNodeConfig
     *            The parent Node configuration.
     */
    public DocumentationManageView(@Nonnull final Composite parent, final int style,
                                   @Nonnull final IHasDocumentableObject parentNodeConfig) {
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
     * @return all available Documents.
     */
    @Nonnull
    public Set<DocumentDBO> getDocuments() {
        // _docAvailableTableViewer.getTable().selectAll();
        // IStructuredSelection structSele = (IStructuredSelection) _docAvailableTableViewer
        // .getSelection();

        Set<DocumentDBO> set = new HashSet<DocumentDBO>(_documentAvailable);
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
        if (!_isActivate) {
            IDocumentable node = _parentNodeConfig.getDocumentableObject();
            setDocs(node.getDocuments());
            _isActivate = true;
        }
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

            public void modifyText(@Nonnull final ModifyEvent e) {
                filter.setFilterText(search.getText());
                _docResorceTableViewer.refresh();
            }

        });

        _docResorceTableViewer = DocumentTableViewerBuilder.crateDocumentTable(searchGroup, false);

        _documentResorce = Repository.loadDocument(false);
        _docResorceTableViewer.addFilter(filter);
        _docResorceTableViewer.setFilters(new ViewerFilter[] {filter});
        TableViewerEditor.create(_docResorceTableViewer, new ColumnViewerEditorActivationStrategy(
                _docResorceTableViewer), ColumnViewerEditor.DEFAULT);

        // makeMenus(_docAvailableTableViewer);
        DocumentTableViewerBuilder.makeMenus(_docResorceTableViewer);
    }

    @Nonnull
    private Composite makeGroup(@Nonnull final String groupHead) {
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
        refreshButton.setImage(ActivatorUI.getImageDescriptor("icons/refresh.gif").createImage());
        refreshButton.setToolTipText("Refresh List of Documents");
        refreshButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
                refreshDocuments();
            }

            public void widgetSelected(@Nonnull final SelectionEvent e) {
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
        addNewDocButton.addSelectionListener(DocumentTableViewerBuilder.getAddFile2DBSelectionListener(null));

        new Label(chosserComposite, SWT.NONE);
        // Add
        Button addAllButton = new Button(chosserComposite, SWT.PUSH);
        addAllButton.setText(">>");
        addAllButton.setToolTipText("Add all Documents");
        addAllButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        addAllButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
                addAll();
            }

            public void widgetSelected(@Nonnull final SelectionEvent e) {
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

            public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
                doAddDoc();
            }

            public void widgetSelected(@Nonnull final SelectionEvent e) {
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

            public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
                doRemoveDoc();
            }

            public void widgetSelected(@Nonnull final SelectionEvent e) {
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

            public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
                _documentResorce.addAll(_documentAvailable);
                _documentAvailable.clear();
                _docAvailableTableViewer.setInput(_documentAvailable);
                _docResorceTableViewer.setInput(_documentResorce);
                setSaveButton();
            }

            public void widgetSelected(@Nonnull final SelectionEvent e) {
                _documentResorce.addAll(_documentAvailable);
                _documentAvailable.clear();
                _docAvailableTableViewer.setInput(_documentAvailable);
                _docResorceTableViewer.setInput(_documentResorce);
                setSaveButton();
            }

        });

        label = new Label(chosserComposite, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));

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

        _docAvailableTableViewer = DocumentTableViewerBuilder.crateDocumentTable(availableGroup, false);

//        _docAvailableTableViewer.setSorter(sorter);

        DocumentTableViewerBuilder.makeMenus(_docAvailableTableViewer);
    }



    private void setDocs(final Set<DocumentDBO> set) {
        _originDocs = new ArrayList<DocumentDBO>();
        _documentAvailable = new ArrayList<DocumentDBO>();
        if (set != null) {
            _originDocs.addAll(set);
            _documentAvailable.addAll(set);
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
            ArrayList<DocumentDBO> temp = new ArrayList<DocumentDBO>(_originDocs);
            temp.removeAll(_documentAvailable);
            _parentNodeConfig.setSavebuttonEnabled("documentaion", temp.size() != 0);
        } else {
            _parentNodeConfig.setSavebuttonEnabled("documentaion", true);
        }
    }



    //    private final class ShowFileSelectionListener implements SelectionListener {
    //        private final TableViewer _parentViewer;
    //
    //        public ShowFileSelectionListener(TableViewer parentViewer) {
    //            _parentViewer = parentViewer;
    //        }
    //
    //        public void widgetSelected(SelectionEvent e) {
    //            openFileInBrowser();
    //        }
    //
    //        public void widgetDefaultSelected(SelectionEvent e) {
    //            openFileInBrowser();
    //        }
    //
    //        private void openFileInBrowser() {
    //            StructuredSelection selection = (StructuredSelection) _parentViewer.getSelection();
    //            Document firstElement = (Document) selection.getFirstElement();
    //            File createTempFile = null;
    //            try {
    //                String filename = firstElement.getSubject();
    //                if (filename == null || filename.length() < 1) {
    //                    filename = "showTmp";
    //                }
    //                createTempFile = File.createTempFile(filename, "." + firstElement.getMimeType());
    //                Helper.writeDocumentFile(createTempFile, firstElement);
    //                if (createTempFile != null && createTempFile.isFile()) {
    //                    if (Desktop.isDesktopSupported()) {
    //                        if (Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
    //                            Desktop.getDesktop().open(createTempFile);
    //                        }
    //                    }
    //                }
    //            } catch (IOException e) {
    //                e.printStackTrace();
    //            } catch (SQLException e) {
    //                e.printStackTrace();
    //            }
    //        }
    //    }

        /**
         * @author hrickens
         * @author $Author: hrickens $
         * @version $Revision: 1.10 $
         * @since 07.08.2009
         */
        private final class ViewerFilterExtension extends ViewerFilter {
            private String _filterString = "";

            @Override
            public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
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

            public void setFilterText(final String filterString) {
                _filterString = filterString;
            }
        }

}
