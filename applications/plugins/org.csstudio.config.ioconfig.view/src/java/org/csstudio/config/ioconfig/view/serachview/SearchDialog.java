/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron,
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
 * $Id: SearchDialog.java,v 1.11 2010/08/20 13:33:04 hrickens Exp $
 */
package org.csstudio.config.ioconfig.view.serachview;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.SearchNodeDBO;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.csstudio.config.ioconfig.view.DeviceDatabaseErrorDialog;
import org.csstudio.config.ioconfig.view.ProfiBusTreeView;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.11 $
 * @since 05.06.2009
 */
public class SearchDialog extends Dialog {
    
    /**
     * TODO (hrickens) : 
     * 
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.7 $
     * @since 03.08.2011
     */
    private final class FilterModifyListener implements ModifyListener {
        private final TableViewer _resultTableView;
        private final AbstractStringViewerFilter _viewerFilter;
        private final Text _filerText;
        
        /**
         * Constructor.
         */
        protected FilterModifyListener(@Nonnull final TableViewer resultTableView,
                                       @Nonnull final AbstractStringViewerFilter viewerFilter,
                                       @Nonnull final Text filerText) {
            _resultTableView = resultTableView;
            _viewerFilter = viewerFilter;
            _filerText = filerText;
        }
        
        @Override
        public void modifyText(@Nullable final ModifyEvent e) {
            _viewerFilter.setText(_filerText.getText());
            _resultTableView.refresh();
        }
    }
    /**
     * @author hrickens
     * @author $Author: hrickens $
     * @version $Revision: 1.7 $
     * @since 03.08.2011
     */
    private final class RefreshSelectionListener implements SelectionListener {
        private final Button _csButton;
        private final AbstractStringViewerFilter _viewerFilter;
        private final TableViewer _resultTableView;
        
        protected RefreshSelectionListener(@Nonnull final Button checkButton,
                                         @Nonnull final AbstractStringViewerFilter viewerFilter,
                                         @Nonnull final TableViewer resultTableView) {
            _csButton = checkButton;
            _viewerFilter = viewerFilter;
            _resultTableView = resultTableView;
        }
        
        private void setCaseSensetive() {
            _viewerFilter.setCaseSensetive(_csButton.getSelection());
            _resultTableView.refresh();
        }
        
        @Override
        public void widgetDefaultSelected(@Nullable final SelectionEvent e) {
            setCaseSensetive();
        }
        
        @Override
        public void widgetSelected(@Nullable final SelectionEvent e) {
            setCaseSensetive();
        }
    }
    /**
     * 
     * Viewer filter for the EPICS Address column.
     * 
     * @author hrickens
     * @author $Author: $
     * @since 23.09.2010
     */
    final class EpicsAddressViewerFilter extends AbstractStringViewerFilter {
        
        private boolean checkNodeEpicsAddress(@CheckForNull final SearchNodeDBO node) {
            return node != null && node.getEpicsAddressString() != null;
        }
        
        @Override
        public boolean select(@Nullable final Viewer viewer,
                              @Nullable final Object parentElement, @Nullable final Object element) {
            if (element instanceof SearchNodeDBO) {
                final SearchNodeDBO sNode = (SearchNodeDBO) element;
                if (checkSearchText(getSearchText())
                        && checkNodeEpicsAddress(sNode)) {
                    return compareStrings(sNode.getEpicsAddressString());
                }
            }
            return !checkSearchText(getSearchText());
        }
        
    }
    /**
     * 
     * Viewer filter for the IO Name column.
     * 
     * @author hrickens
     * @author $Author: $
     * @since 23.09.2010
     */
    final class IONameViewerFilter extends AbstractStringViewerFilter {
        
        private boolean checkNodeIOName(@CheckForNull final SearchNodeDBO node) {
            return node != null && node.getIoName() != null;
        }
        
        @Override
        public boolean select(@Nullable final Viewer viewer,
                              @Nullable final Object parentElement, @Nullable final Object element) {
            if (element instanceof SearchNodeDBO) {
                final SearchNodeDBO sNode = (SearchNodeDBO) element;
                if (checkSearchText(getSearchText()) && checkNodeIOName(sNode)) {
                    return compareStrings(sNode.getIoName());
                }
            }
            return !checkSearchText(getSearchText());
        }
        
    }
    /**
     * 
     * Viewer filter for the Name column.
     * 
     * @author hrickens
     * @author $Author: $
     * @since 23.09.2010
     */
    final class NameViewerFilter extends AbstractStringViewerFilter {
        
        public NameViewerFilter() {
            // Default Constructor
        }
        
        private boolean checkNodeName(@CheckForNull final SearchNodeDBO node) {
            return node != null && node.getName() != null;
        }
        
        @Override
        public boolean select(@Nullable final Viewer viewer,
                              @Nullable final Object parentElement,
                              @Nullable final Object element) {
            if (element instanceof SearchNodeDBO) {
                final SearchNodeDBO sNode = (SearchNodeDBO) element;
                if (checkSearchText(getSearchText()) && checkNodeName(sNode)) {
                    return compareStrings(sNode.getName());
                }
            }
            return !checkSearchText(getSearchText());
        }
        
    }
    
    /**
     * 
     * Sorter for each column of the Search Dialog Table.
     * 
     * @author hrickens
     * @author $Author: $
     * @since 23.09.2010
     */
    private static final class SortSelectionListener implements SelectionListener {
        private final SearchViewerSorterExtension _sorter;
        private final int _state;
        
        SortSelectionListener(@Nonnull final SearchViewerSorterExtension sorter, final int state) {
            _sorter = sorter;
            _state = state;
        }
        
        private void setState() {
            _sorter.setState(_state);
        }
        
        @Override
        public void widgetDefaultSelected(@Nullable final SelectionEvent e) {
            setState();
        }
        
        @Override
        public void widgetSelected(@Nullable final SelectionEvent e) {
            setState();
        }
    }
    
    /**
     * This class provides the content for the table.
     */
    public class TableContentProvider implements IStructuredContentProvider {
        
        @Override
        public void dispose() {
            // We don't create any resources, so we don't dispose any
        }
        
        @Override
        @CheckForNull
        @SuppressWarnings("rawtypes")
        public Object[] getElements(@Nullable final Object inputElement) {
            if (inputElement instanceof List) {
                return ((List) inputElement).toArray();
            }
            return null;
        }
        
        @Override
        public void inputChanged(@Nullable final Viewer viewer,
                                 @Nullable final Object oldInput, @Nullable final Object newInput) {
            // Empty
        }
        
    }
    
    private static final Logger LOG = LoggerFactory.getLogger(SearchDialog.class);
    
    private SearchNodeDBO _searchNode;
    
    private List<SearchNodeDBO> _load;
    private final ProfiBusTreeView _profiBusTreeView;

    private Map<Integer, SearchNodeDBO> _loadMap;
    
    public SearchDialog(@Nullable final Shell parentShell,
                           @Nonnull final ProfiBusTreeView profiBusTreeView) {
        super(parentShell);
        _profiBusTreeView = profiBusTreeView;
        setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.MAX | SWT.RESIZE
                      | SWT.PRIMARY_MODAL);
        try {
            _loadMap = new HashMap<Integer, SearchNodeDBO>();
            _load = Repository.load(SearchNodeDBO.class);
            for (SearchNodeDBO node : _load) {
                _loadMap.put(node.getId(), node);
            }
        } catch (final PersistenceException e) {
            _load = null;
            DeviceDatabaseErrorDialog.open(null, "Can't read from Datebase!", e);
            LOG.error("Can't read from Datebase!", e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected Control createDialogArea(@Nonnull final Composite parent) {
        final Composite localDialogArea1 = (Composite) super.createDialogArea(parent);
        localDialogArea1.getShell().setText("Search DDB Node");
        localDialogArea1.setLayout(GridLayoutFactory.swtDefaults().numColumns(6).equalWidth(false).create());
        
        buildSearchTitels(localDialogArea1);
        
        final GridDataFactory gdfText = GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
                                                .grab(true, false);
        
        final Button csNameButton = new Button(localDialogArea1, SWT.CHECK);
        csNameButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        csNameButton.setToolTipText("Case Sensitive");
        final Text searchTextName = new Text(localDialogArea1, SWT.SINGLE | SWT.LEAD | SWT.BORDER
                                                               | SWT.SEARCH);
        gdfText.applyTo(searchTextName);
        searchTextName.setMessage("Name Filter");
        
        final Button csIONameButton = new Button(localDialogArea1, SWT.CHECK);
        csIONameButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        csIONameButton.setToolTipText("Case Sensitive");
        final Text searchTextIOName =  new Text(localDialogArea1, SWT.SINGLE | SWT.LEAD | SWT.BORDER
                                                                 | SWT.SEARCH);
        gdfText.applyTo(searchTextIOName);
        searchTextIOName.setMessage("IO Name Filter");
        final TableColumnLayout tableColumnLayout = new TableColumnLayout();
        
        final Button csEASButton = new Button(localDialogArea1, SWT.CHECK);
        csEASButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        csEASButton.setToolTipText("Case Sensitive");
        final Text searchTextAddressString =  new Text(localDialogArea1, SWT.SINGLE | SWT.LEAD
                                                                        | SWT.BORDER | SWT.SEARCH);
        gdfText.applyTo(searchTextAddressString);
        searchTextAddressString.setMessage("Epics Address Filter");
        
        final TableViewer resultTableView = buildSearchTable(localDialogArea1, tableColumnLayout);
        
        final NameViewerFilter nameViewerFilter = new NameViewerFilter();
        resultTableView.addFilter(nameViewerFilter);
        final IONameViewerFilter ioNameViewerFilter = new IONameViewerFilter();
        resultTableView.addFilter(ioNameViewerFilter);
        final EpicsAddressViewerFilter epicsAddressViewerFilter = new EpicsAddressViewerFilter();
        resultTableView.addFilter(epicsAddressViewerFilter);
        
        csNameButton.addSelectionListener(new RefreshSelectionListener(csNameButton, nameViewerFilter, resultTableView));
        FilterModifyListener listener = new FilterModifyListener(resultTableView, nameViewerFilter, searchTextName);
        searchTextName.addModifyListener(listener);
        
        csIONameButton.addSelectionListener(new RefreshSelectionListener(csIONameButton, ioNameViewerFilter, resultTableView));
        listener = new FilterModifyListener(resultTableView, ioNameViewerFilter, searchTextIOName);
        searchTextIOName.addModifyListener(listener);
        
        csEASButton.addSelectionListener(new RefreshSelectionListener(csEASButton, epicsAddressViewerFilter, resultTableView));
        listener = new FilterModifyListener(resultTableView,epicsAddressViewerFilter,searchTextAddressString);
        searchTextAddressString.addModifyListener(listener);
        
        resultTableView.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(@Nonnull final SelectionChangedEvent event) {
                final StructuredSelection selection = (StructuredSelection) event.getSelection();
                setSearchNode((SearchNodeDBO) selection.getFirstElement());
            }
        });
        
        resultTableView.setInput(_load);
        return localDialogArea1;
    }

    /**
     * @param parent
     */
    private void buildSearchTitels(@Nonnull final Composite parent) {
        Label label = new Label(parent, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
                                         false, 2, 1));
        label.setText("Name:");
        label = new Label(parent, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
                                         false, 2, 1));
        label.setText("IO Name:");
        label = new Label(parent, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
                                         false, 2, 1));
        label.setText("EPICS Address:");
    }

    @Nonnull
    private TableViewer buildSearchTable(@Nonnull final Composite tableParent,
                                         @Nonnull final TableColumnLayout tableColumnLayout) {
        final Composite tableComposite = new Composite(tableParent,
                                                       SWT.FULL_SELECTION);
        GridDataFactory.fillDefaults().grab(true, true).span(6, 1)
        .hint(800, 300).applyTo(tableComposite);
        tableComposite.setLayout(tableColumnLayout);
        
        final TableViewer resultTableView = new TableViewer(tableComposite);
        resultTableView.getTable().setHeaderVisible(true);
        resultTableView.getTable().setLinesVisible(true);
        final SearchViewerSorterExtension sorter = new SearchViewerSorterExtension();
        resultTableView.setSorter(sorter);
        
        // Column Subject
        int state = 0;
        final TableViewerColumn columnName = new TableViewerColumn(resultTableView,
                                                                   SWT.NONE);
        columnName.getColumn().addSelectionListener(
                                                    new SortSelectionListener(sorter, state++));
        columnName.getColumn().setText("Name");
        columnName.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(@Nonnull final ViewerCell cell) {
                if (cell.getElement() instanceof SearchNodeDBO) {
                    cell.setText(((SearchNodeDBO) cell.getElement()).getName());
                }
            }
        });
        tableColumnLayout.setColumnData(columnName.getColumn(),
                                        new ColumnWeightData(3, 40, true));
        
        final TableViewerColumn columnIOName = new TableViewerColumn(resultTableView,
                                                                     SWT.FULL_SELECTION);
        columnIOName.getColumn().setText("IO Name");
        columnIOName.getColumn().addSelectionListener(
                                                      new SortSelectionListener(sorter, state++));
        columnIOName.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(@Nonnull final ViewerCell cell) {
                if (cell.getElement() instanceof SearchNodeDBO) {
                    final SearchNodeDBO channel = (SearchNodeDBO) cell.getElement();
                    cell.setText(channel.getIoName());
                }
            }
        });
        tableColumnLayout.setColumnData(columnIOName.getColumn(),
                                        new ColumnWeightData(3, 40, true));
        
        final TableViewerColumn columnEpicsAddress = new TableViewerColumn(
                                                                           resultTableView, SWT.NONE);
        columnEpicsAddress.getColumn().addSelectionListener(
                                                            new SortSelectionListener(sorter, state++));
        columnEpicsAddress.getColumn().setText("Epics Address");
        columnEpicsAddress.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(@Nonnull final ViewerCell cell) {
                if (cell.getElement() instanceof SearchNodeDBO) {
                    final SearchNodeDBO channel = (SearchNodeDBO) cell.getElement();
                    cell.setText(channel.getEpicsAddressString());
                }
            }
        });
        tableColumnLayout.setColumnData(columnEpicsAddress.getColumn(),
                                        new ColumnWeightData(2, 40, true));
        
        final TableViewerColumn columnCreateBy = new TableViewerColumn(
                                                                       resultTableView, SWT.NONE);
        columnCreateBy.getColumn().addSelectionListener(
                                                        new SortSelectionListener(sorter, state++));
        columnCreateBy.getColumn().setText("Create By");
        columnCreateBy.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(@Nonnull final ViewerCell cell) {
                if (cell.getElement() instanceof SearchNodeDBO) {
                    String createdBy = ((SearchNodeDBO) cell.getElement())
                    .getCreatedBy();
                    if (createdBy != null) {
                        final int pos = createdBy.indexOf('@');
                        if (pos > 0) {
                            createdBy = createdBy.substring(0, pos);
                        }
                        cell.setText(createdBy);
                    }
                    
                }
            }
        });
        tableColumnLayout.setColumnData(columnCreateBy.getColumn(),
                                        new ColumnWeightData(2, 40, true));
        
        final TableViewerColumn columnCreateOn = new TableViewerColumn(
                                                                       resultTableView, SWT.NONE);
        columnCreateOn.getColumn().addSelectionListener(
                                                        new SortSelectionListener(sorter, state++));
        columnCreateOn.getColumn().setText("Create On");
        columnCreateOn.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(@Nonnull final ViewerCell cell) {
                if (cell.getElement() instanceof SearchNodeDBO) {
                    final Date createdOn = ((SearchNodeDBO) cell.getElement())
                    .getCreatedOn();
                    if (createdOn != null) {
                        cell.setText(createdOn.toString());
                    }
                }
            }
        });
        tableColumnLayout.setColumnData(columnCreateOn.getColumn(),
                                        new ColumnWeightData(2, 100, true));
        
        final TableViewerColumn columnUpdatedBy = new TableViewerColumn(
                                                                        resultTableView, SWT.NONE);
        columnUpdatedBy.getColumn().addSelectionListener(
                                                         new SortSelectionListener(sorter, state++));
        columnUpdatedBy.getColumn().setText("Updated By");
        columnUpdatedBy.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(@Nonnull final ViewerCell cell) {
                if (cell.getElement() instanceof SearchNodeDBO) {
                    String updatedBy = ((SearchNodeDBO) cell.getElement())
                    .getUpdatedBy();
                    if (updatedBy != null) {
                        final int pos = updatedBy.indexOf('@');
                        if (pos > 0) {
                            updatedBy = updatedBy.substring(0, pos);
                        }
                        cell.setText(updatedBy);
                    }
                }
            }
        });
        tableColumnLayout.setColumnData(columnUpdatedBy.getColumn(),
                                        new ColumnWeightData(2, 40, true));
        
        final TableViewerColumn columnUpdatedOn = new TableViewerColumn(
                                                                        resultTableView, SWT.NONE);
        columnUpdatedOn.getColumn().addSelectionListener(
                                                         new SortSelectionListener(sorter, state++));
        columnUpdatedOn.getColumn().setText("Updated On");
        columnUpdatedOn.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(@Nonnull final ViewerCell cell) {
                if (cell.getElement() instanceof SearchNodeDBO) {
                    final Date updatedOn = ((SearchNodeDBO) cell.getElement())
                    .getUpdatedOn();
                    if (updatedOn != null) {
                        cell.setText(updatedOn.toString());
                    }
                }
            }
        });
        tableColumnLayout.setColumnData(columnUpdatedOn.getColumn(),
                                        new ColumnWeightData(2, 100, true));
        
        final TableViewerColumn columnId = new TableViewerColumn(resultTableView,
                                                                 SWT.RIGHT);
        columnId.getColumn().addSelectionListener(
                                                  new SortSelectionListener(sorter, state++));
        columnId.getColumn().setText("DB Id");
        columnId.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(@Nonnull final ViewerCell cell) {
                if (cell.getElement() instanceof SearchNodeDBO) {
                    final Integer id = ((SearchNodeDBO) cell.getElement()).getId();
                    cell.setText(id.toString());
                }
            }
        });
        tableColumnLayout.setColumnData(columnId.getColumn(),
                                        new ColumnWeightData(2, 2, true));
        
        final TableViewerColumn columnParentId = new TableViewerColumn(
                                                                       resultTableView, SWT.RIGHT);
        columnParentId.getColumn().addSelectionListener(
                                                        new SortSelectionListener(sorter, state++));
        columnParentId.getColumn().setText("ParentId");
        columnParentId.setLabelProvider(new CellLabelProvider() {
            @Override
            public void update(@Nonnull final ViewerCell cell) {
                if (cell.getElement() instanceof SearchNodeDBO) {
                    final Integer id = ((SearchNodeDBO) cell.getElement())
                    .getParentId();
                    if (id != null) {
                        cell.setText(id.toString());
                    }
                }
            }
        });
        tableColumnLayout.setColumnData(columnParentId.getColumn(),
                                        new ColumnWeightData(2, 2, true));
        
        resultTableView.setContentProvider(new TableContentProvider());
        return resultTableView;
    }
    
    @Override
    protected final void okPressed() {
        if(_searchNode!=null) {
            try {
                selectAbstractNodeFromSearchNode();
            // CHECKSTYLE OFF: EmptyBlock
            } catch (final PersistenceException e) {
                // nothing to do. No Node to show
            }
            // CHECKSTYLE ON: EmptyBlock
        }
        super.okPressed();
    }

    /**
     * @throws PersistenceException
     */
    private void selectAbstractNodeFromSearchNode() throws PersistenceException {
        final List<Integer> nodeIds = new ArrayList<Integer>();
        nodeIds.add(_searchNode.getId());
        Integer parentId = _searchNode.getParentId();
        while(parentId!=null) {
            nodeIds.add(0,parentId);
            final SearchNodeDBO searchNodeDBO = _loadMap.get(parentId);
            if(searchNodeDBO!=null) {
                parentId = searchNodeDBO.getParentId();
            } else {
                parentId = null;
            }
        }
        if(nodeIds.size()>0) {
            @SuppressWarnings("rawtypes")
            AbstractNodeDBO selectedNode;
            final Integer facilityID = nodeIds.remove(0);
            selectedNode = Repository.load(FacilityDBO.class, facilityID);
            final Iterator<Integer> iterator = nodeIds.iterator();
            while (iterator.hasNext()) {
                final Integer nextId = (Integer) iterator.next();
                @SuppressWarnings({ "unchecked", "rawtypes" })
                final Set<AbstractNodeDBO<AbstractNodeDBO, AbstractNodeDBO>> children = (Set<AbstractNodeDBO<AbstractNodeDBO, AbstractNodeDBO>>) selectedNode.getChildren();
                for (@SuppressWarnings("rawtypes") AbstractNodeDBO<AbstractNodeDBO, AbstractNodeDBO> abstractNodeDBO : children) {
                    if(abstractNodeDBO.getId()==nextId) {
                        selectedNode = abstractNodeDBO;
                    }
                }
            }
            showNode(selectedNode);
        }
    }
    
    public final void setSearchNode(@Nullable final SearchNodeDBO searchNode) {
        _searchNode = searchNode;
    }
    
    private void showNode(@CheckForNull final AbstractNodeDBO<?,?> node) {
        if(node!=null) {
            _profiBusTreeView.getViewer().setSelection(new StructuredSelection(node));
        }
    }
}
