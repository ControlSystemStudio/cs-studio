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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.config.view.IHasDocumentableObject;
import org.csstudio.config.ioconfig.model.DocumentDBO;
import org.csstudio.config.ioconfig.model.IDocument;
import org.csstudio.config.ioconfig.model.IDocumentable;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.Repository;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.csstudio.config.ioconfig.view.IOConfigActivatorUI;
import org.csstudio.platform.logging.CentralLogger;
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
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.7 $
     * @since 12.05.2011
     */
    private final class RemoveAllDocumentsSelectionListener implements SelectionListener {
        /**
         * Constructor.
         */
        protected RemoveAllDocumentsSelectionListener() {
            // Constructor.
        }

        @Override
        public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
            getDocumentResorce().addAll(getDocumentAvailable());
            getDocumentAvailable().clear();
            setDocAvailableTableInput();
            setDocResorceTableInput();
            setSaveButton();
        }
        
        @Override
        public void widgetSelected(@Nonnull final SelectionEvent e) {
            getDocumentResorce().addAll(getDocumentAvailable());
            getDocumentAvailable().clear();
            setDocAvailableTableInput();
            setDocResorceTableInput();
            setSaveButton();
        }
    }

    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.7 $
     * @since 12.05.2011
     */
    private final class AddAllDocumentsSelectionListener implements SelectionListener {
        /**
         * Constructor.
         */
        protected AddAllDocumentsSelectionListener() {
            // Constructor.
        }

        private void addAll() {
            getDocumentAvailable().addAll(getDocumentResorce());
            getDocumentResorce().clear();
            setDocAvailableTableInput();
            setDocResorceTableInput();
            setSaveButton();
        }
        
        @Override
        public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
            addAll();
        }
        
        @Override
        public void widgetSelected(@Nonnull final SelectionEvent e) {
            addAll();
        }
    }

    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.7 $
     * @since 12.05.2011
     */
    private final class RefreshDocumnetsSelectionListener implements SelectionListener {
        /**
         * Constructor.
         */
        protected RefreshDocumnetsSelectionListener() {
            // Constructor
        }

        private void refreshDocuments() {
            try {
                setDocumentResorce(Repository.loadDocument(true));
                setDocResorceTableInput();
            } catch (PersistenceException e) {
                DeviceDatabaseErrorDialog.open(null, "Can't load Documents!", e);
                CentralLogger.getInstance().error(this, e);
            }
        }
        
        @Override
        public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
            refreshDocuments();
        }
        
        @Override
        public void widgetSelected(@Nonnull final SelectionEvent e) {
            refreshDocuments();
        }
    }

    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.7 $
     * @since 12.05.2011
     */
    private static final class FilterModifyListener implements ModifyListener {
        private final Text _search;
        private final ViewerFilterExtension _filter;
        private final TableViewer _targetTableViewer;
        
        /**
         * Constructor.
         * @param search
         * @param filter
         * @param docResorceTableViewer 
         */
        protected FilterModifyListener(@Nonnull Text search, @Nonnull ViewerFilterExtension filter, @Nonnull TableViewer targetTableViewer) {
            _search = search;
            _filter = filter;
            _targetTableViewer = targetTableViewer;
        }
        
        @Override
        public void modifyText(@Nonnull final ModifyEvent e) {
            _filter.setFilterText(_search.getText());
            _targetTableViewer.refresh();
        }
    }

    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.7 $
     * @since 12.05.2011
     */
    private final class RemoveSelectionListener implements SelectionListener {

        /**
         * Constructor.
         * @param docAvailableTableViewer
         */
        public RemoveSelectionListener() {
            // Constructor.
        }

        @SuppressWarnings("unchecked")
        private void doRemoveDoc() {
            IStructuredSelection sSelect = (IStructuredSelection) _docAvailableTableViewer
                    .getSelection();
            getDocumentResorce().addAll(sSelect.toList());
            getDocumentAvailable().removeAll(sSelect.toList());
            setDocAvailableTableInput();
            setDocResorceTableInput();
            setSaveButton();
        }
        
        @Override
        public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
            doRemoveDoc();
        }
        
        @Override
        public void widgetSelected(@Nonnull final SelectionEvent e) {
            doRemoveDoc();
        }
    }

    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.7 $
     * @since 12.05.2011
     */
    private final class AddDocSelectionListener implements SelectionListener {

        /**
         * Constructor.
         */
        public AddDocSelectionListener() {
            // Constructor.
        }

        @SuppressWarnings("unchecked")
        private void doAddDoc() {
            IStructuredSelection sSelect = (IStructuredSelection) _docResorceTableViewer
                    .getSelection();
            getDocumentResorce().removeAll(sSelect.toList());
            getDocumentAvailable().addAll(sSelect.toList());
            setDocAvailableTableInput();
            setDocResorceTableInput();
            setSaveButton();
        }
        
        @Override
        public void widgetDefaultSelected(@Nonnull final SelectionEvent e) {
            doAddDoc();
        }
        
        @Override
        public void widgetSelected(@Nonnull final SelectionEvent e) {
            doAddDoc();
        }
    }

    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.10 $
     * @since 07.08.2009
     */
    protected static final class ViewerFilterExtension extends ViewerFilter {
        private String _filterString = "";
        
        @Override
        public boolean select(@Nullable final Viewer viewer,
                              @Nullable final Object parentElement,
                              @Nullable final Object element) {
            if(element instanceof IDocument) {
                boolean status = false;
                IDocument doc = (IDocument) element;
                
                String subject = doc.getSubject();
                if(subject != null) {
                    status |= subject.toLowerCase().contains(_filterString.toLowerCase());
                }
                String mimeType = doc.getMimeType();
                if(mimeType != null) {
                    status |= mimeType.toLowerCase().contains(_filterString.toLowerCase());
                }
                String desclong = doc.getDesclong();
                if(desclong != null) {
                    status |= desclong.toLowerCase().contains(_filterString.toLowerCase());
                }
                Date createdDate = doc.getCreatedDate();
                if(createdDate != null) {
                    status |= createdDate.toString().contains(_filterString);
                }
                String keywords = doc.getKeywords();
                if(keywords != null) {
                    status |= keywords.toLowerCase().contains(_filterString.toLowerCase());
                }
                return status;
            }
            return false;
        }
        
        public void setFilterText(@Nonnull final String filterString) {
            _filterString = filterString;
        }
    }
    
    /**
     * The table with a list of all assigned documents.
     */
    private TableViewer _docAvailableTableViewer;
    /**
     * The table with a list of all not assigned documents.
     */
    private TableViewer _docResorceTableViewer;
    /**
     * A List whit the assigned Documents.
     */
    private List<DocumentDBO> _documentAvailable = new ArrayList<DocumentDBO>();
    /**
     * A List whit all Documents.
     */
    private List<DocumentDBO> _documentResorce;
    private boolean _isActivate = false;
    private final Composite _mainComposite;
    private ArrayList<DocumentDBO> _originDocs;
    
    private final IHasDocumentableObject _parentNodeConfig;
    
    /**
     * @param parent
     *            The Parent Composite.
     * @param style
     *            The Composite Style.
     * @param parentNodeConfig
     *            The parent Node configuration.
     */
    public DocumentationManageView(@Nonnull final Composite parent,
                                   final int style,
                                   @Nonnull final IHasDocumentableObject parentNodeConfig) {
        super(parent, style);
        this.setLayout(new GridLayout(1, false));
        GridData layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
        this.setLayoutData(layoutData);
        _parentNodeConfig = parentNodeConfig;
        
        // -Body
        GridLayoutFactory fillDefaults = GridLayoutFactory.fillDefaults();
        ScrolledComposite scrolledComposite = new ScrolledComposite(this, SWT.H_SCROLL
                | SWT.V_SCROLL);
        scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setExpandHorizontal(true);
        fillDefaults.numColumns(3);
        scrolledComposite.setLayout(fillDefaults.create());
        
        _mainComposite = new Composite(scrolledComposite, SWT.NONE);
        _mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        fillDefaults.numColumns(3);
        _mainComposite.setLayout(fillDefaults.create());
        
        scrolledComposite.setContent(_mainComposite);
        scrolledComposite.setMinSize(700, 250);
        
        makeSearchDocTable();
        makeChooser();
        makeAvailableDocTable();
    }
    
    public final void cancel() {
        if(_documentResorce != null) {
            _documentResorce.addAll(_documentAvailable);
        }
        _documentAvailable.clear();
        if(_originDocs != null) {
            _documentAvailable.addAll(_originDocs);
            if(_documentResorce != null) {
                _documentResorce.removeAll(_originDocs);
            }
        }
        setDocAvailableTableInput();
        setDocResorceTableInput();
    }
    
    /**
     * @return all available Documents.
     */
    @Nonnull
    public final Set<DocumentDBO> getDocuments() {
        Set<DocumentDBO> set = new HashSet<DocumentDBO>(_documentAvailable);
        return set;
    }
    
    public final void onActivate() {
        if(!_isActivate) {
            IDocumentable node = _parentNodeConfig.getDocumentableObject();
            if(node != null) {
                setDocs(node.getDocuments());
                _isActivate = true;
            }
        }
    }
    
    @Nonnull
    protected final List<DocumentDBO> getDocumentAvailable() {
        return _documentAvailable;
    }

    @Nonnull
    protected final List<DocumentDBO> getDocumentResorce() {
        return _documentResorce;
    }
    
    protected final void setDocumentResorce(@Nonnull List<DocumentDBO> documentResorce) {
        _documentResorce = documentResorce;
    }
    
    protected final void setDocAvailableTableInput() {
        _docAvailableTableViewer.setInput(_documentAvailable);
    }
    
    protected final void setDocResorceTableInput() {
        _docResorceTableViewer.setInput(_documentResorce);
    }
    
    private void makeAvailableDocTable() {
        Composite availableGroup = makeGroup("Available");
        _docAvailableTableViewer = DocumentTableViewerBuilder.crateDocumentTable(availableGroup,
                                                                                 false);
        DocumentTableViewerBuilder.makeMenus(_docAvailableTableViewer);
    }
    
    private void makeChooser() {
        Composite chosserComposite = new Composite(_mainComposite, SWT.NONE);
        GridData layoutData = new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1);
        chosserComposite.setLayoutData(layoutData);
        GridLayoutFactory fillDefaults = GridLayoutFactory.fillDefaults();
        GridLayout create = fillDefaults.create();
        create.marginTop = 15;
        chosserComposite.setLayout(create);
        
        Button refreshButton = new Button(chosserComposite, SWT.FLAT);
        refreshButton.setLayoutData(new GridData(SWT.CENTER, SWT.BEGINNING, false, false));
        refreshButton.setImage(IOConfigActivatorUI.getImageDescriptor("icons/refresh.gif")
                .createImage());
        refreshButton.setToolTipText("Refresh List of Documents");
        refreshButton.addSelectionListener(new RefreshDocumnetsSelectionListener());
        Label label = new Label(chosserComposite, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
        
        buildNewDocButton(chosserComposite);
        new Label(chosserComposite, SWT.NONE).setText("");
        buildAddAllButton(chosserComposite);
        buildAddButton(chosserComposite);
        buildRemoveButton(chosserComposite);
        buildRemoveAllButton(chosserComposite);
        
        label = new Label(chosserComposite, SWT.NONE);
        label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
        
    }

    /**
     * @param chosserComposite
     */
    private void buildRemoveAllButton(@Nonnull Composite chosserComposite) {
        Button removeAllButton = new Button(chosserComposite, SWT.PUSH);
        removeAllButton.setText("<<");
        removeAllButton.setToolTipText("Remove all Documents");
        removeAllButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        removeAllButton.addSelectionListener(new RemoveAllDocumentsSelectionListener());
    }

    /**
     * @param chosserComposite
     */
    private void buildRemoveButton(@Nonnull Composite chosserComposite) {
        Button removeButton = new Button(chosserComposite, SWT.PUSH);
        removeButton.setText("<");
        removeButton.setToolTipText("Remove all selceted Documents");
        removeButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        removeButton.addSelectionListener(new RemoveSelectionListener());
    }

    /**
     * @param chosserComposite
     */
    private void buildAddButton(@Nonnull Composite chosserComposite) {
        Button addButton = new Button(chosserComposite, SWT.PUSH);
        addButton.setText(">");
        addButton.setToolTipText("Add all selceted Documents");
        addButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        addButton.addSelectionListener(new AddDocSelectionListener());
    }

    /**
     * @param chosserComposite
     */
    private void buildAddAllButton(@Nonnull Composite chosserComposite) {
        Button addAllButton = new Button(chosserComposite, SWT.PUSH);
        addAllButton.setText(">>");
        addAllButton.setToolTipText("Add all Documents");
        addAllButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        addAllButton.addSelectionListener(new AddAllDocumentsSelectionListener());
    }

    /**
     * @param chosserComposite
     */
    private void buildNewDocButton(@Nonnull Composite chosserComposite) {
        Button addNewDocButton = new Button(chosserComposite, SWT.PUSH);
        addNewDocButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        addNewDocButton.setText("<New");
        addNewDocButton.setToolTipText("Add a new Document from the File-System");
        addNewDocButton.setToolTipText("Add a new Document to the Database");
        addNewDocButton.setEnabled(true);
        addNewDocButton.addSelectionListener(DocumentTableViewerBuilder
                .getAddFile2DBSelectionListener(null));
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
     * Generate the Table with the received Documents.
     */
    private void makeSearchDocTable() {
        final ViewerFilterExtension filter = new ViewerFilterExtension();
        
        // GROUP Layout
        Composite searchGroup = makeGroup("Search");
        
        final Text search = new Text(searchGroup, SWT.SINGLE | SWT.SEARCH | SWT.LEAD | SWT.BORDER);
        search.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
        search.setMessage("Filter");
        
        _docResorceTableViewer = DocumentTableViewerBuilder.crateDocumentTable(searchGroup, false);
        
        search.addModifyListener(new FilterModifyListener(search, filter, _docResorceTableViewer));
        try {
            _documentResorce = Repository.loadDocument(false);
        } catch (PersistenceException e) {
            _documentResorce = new ArrayList<DocumentDBO>();
            DeviceDatabaseErrorDialog.open(null, "Can't load Documents!", e);
            CentralLogger.getInstance().error(this, e);
        }
        _docResorceTableViewer.addFilter(filter);
        _docResorceTableViewer.setFilters(new ViewerFilter[] {filter});
        TableViewerEditor.create(_docResorceTableViewer,
                                 new ColumnViewerEditorActivationStrategy(_docResorceTableViewer),
                                 ColumnViewerEditor.DEFAULT);
        
        DocumentTableViewerBuilder.makeMenus(_docResorceTableViewer);
    }
    
    private void setDocs(@CheckForNull final Set<DocumentDBO> set) {
        _originDocs = new ArrayList<DocumentDBO>();
        _documentAvailable = new ArrayList<DocumentDBO>();
        if(set != null) {
            _originDocs.addAll(set);
            _documentAvailable.addAll(set);
            setDocAvailableTableInput();
            if(_documentResorce != null) {
                _documentResorce.removeAll(set);
            }
        }
        setDocResorceTableInput();
    }
    
    protected final void setSaveButton() {
        if(_originDocs.size() == _documentAvailable.size()) {
            ArrayList<DocumentDBO> temp = new ArrayList<DocumentDBO>(_originDocs);
            temp.removeAll(_documentAvailable);
            _parentNodeConfig.setSavebuttonEnabled("documentaion", temp.size() != 0);
        } else {
            _parentNodeConfig.setSavebuttonEnabled("documentaion", true);
        }
    }
    
}
