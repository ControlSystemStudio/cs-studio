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
package org.csstudio.config.ioconfig.view;

import java.util.Date;
import java.util.List;

import org.csstudio.config.ioconfig.model.Facility;
import org.csstudio.config.ioconfig.model.Node;
import org.csstudio.config.ioconfig.model.Repository;
import org.csstudio.config.ioconfig.model.SearchNode;
import org.csstudio.config.ioconfig.model.tools.NodeMap;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
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

/**
 * 
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.11 $
 * @since 05.06.2009
 */
public class SearchDialog extends Dialog {

    private SearchNode _searchNode;
    private Node _selectedNode;
    private Integer _selectedId;

    private final class SortSelectionListener implements SelectionListener {
        private final ViewerSorterExtension _sorter;
        private final int _state;

        private SortSelectionListener(ViewerSorterExtension sorter, int state) {
            _sorter = sorter;
            _state = state;
        }

        public void widgetDefaultSelected(SelectionEvent e) {
            setState();
        }

        public void widgetSelected(SelectionEvent e) {
            setState();
        }

        private void setState() {
            _sorter.setState(_state);
        }
    }

    private final class ViewerSorterExtension extends ViewerSorter {
        private int _state = 0;
        private boolean _asc;
        private Viewer _viewer;

        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            _viewer = viewer;
            if (e1 instanceof SearchNode && e2 instanceof SearchNode) {
                SearchNode node1 = (SearchNode) e1;
                SearchNode node2 = (SearchNode) e2;
                int asc = 1;
                if (_asc) {
                    asc = -1;
                }
                switch (_state) {
                    case 0:
                        return compareString(node1.getName(), node2.getName(), asc);
                    case 1:
                        return compareString(node1.getIoName(), node2.getIoName(), asc);
                    case 2:
                        return compareString(node1.getEpicsAddressString(), node2.getEpicsAddressString(), asc);
                    case 3:
                        return compareString(node1.getCreatedBy(), node2.getCreatedBy(), asc);
                    case 4:
                        return compareDate(node1.getCreatedOn(), node2.getCreatedOn(), asc);
                    case 5:
                        return compareString(node1.getUpdatedBy(), node2.getUpdatedBy(), asc);
                    case 6:
                        return compareDate(node1.getUpdatedOn(), node2.getUpdatedOn(), asc);
                    case 7:
                        return asc * (node1.getId() - node2.getId());
                    case 8:
                        return asc * (node1.getParentId().compareTo(node2.getParentId()));
                }
                return asc;
            }
            return super.compare(viewer, e1, e2);
        }

        private int compareDate(Date date1, Date date2, int asc) {
            if ( date1 == null &&  date2 == null) {
                return 0;
            }
            if(date1 == null) {
                return -asc;
            }
            if(date2 == null) {
                return asc;
            }

            if(date1.before(date2)) {
                return asc;
            }
            return -asc;
        }

        private int compareString(String string1, String string2, int asc) {
            
            if ( string1 == null &&  string2 == null) {
                return 0;
            }
            if(string1 == null) {
                return asc;
            }
            if(string2 == null) {
                return -asc;
            }
            return asc * string1.compareTo(string2);
        }

        public void setState(int state) {
            if (_state == state) {
                _asc = !_asc;
            } else {
                _asc = !false;
                _state = state;
            }
            if(_viewer!=null) {
                _viewer.refresh();
            }
        }
    }

    private final class NameViewerFilter extends ViewerFilter {
        private String _searchText = "";
        private boolean _caseSensetive;

        private NameViewerFilter() {
        }

        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            if (element instanceof SearchNode) {
                SearchNode sNode = (SearchNode) element;
                if (checkSearchText(_searchText) && checkNodeName(sNode)) {
                    String string1;
                    String string2;
                    if(_caseSensetive) {
                        string1 = sNode.getName();
                        string2 = _searchText;
                    } else {
                        string1 = sNode.getName().toLowerCase();
                        string2 = _searchText.toLowerCase();
                        
                    }
                    return string1.contains(string2);
                }
            }
            return !checkSearchText(_searchText);
        }

        private boolean checkNodeName(SearchNode node) {
            return node != null && node.getName() != null;
        }

        private boolean checkSearchText(String searchText) {
            return _searchText != null && _searchText.trim().length() > 0;
        }

        public void setText(String searchText) {
            _searchText = searchText;

        }

        public void setCaseSensetive(boolean caseSensetive) {
            _caseSensetive = caseSensetive;
        }
    }

    private final class IONameViewerFilter extends ViewerFilter {
        private String _searchText = "";
        private boolean _caseSensetive;

        private IONameViewerFilter() {
        }

        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            if (element instanceof SearchNode) {
                SearchNode sNode = (SearchNode) element;
                if (checkSearchText(_searchText) && checkNodeIOName(sNode)) {
                    String string1;
                    String string2;
                    if(_caseSensetive) {
                        string1 = sNode.getIoName();
                        string2 = _searchText;
                    } else {
                        string1 = sNode.getIoName().toLowerCase();
                        string2 = _searchText.toLowerCase();
                        
                    }
                    return string1.contains(string2);
                }
            }
            return !checkSearchText(_searchText);
        }

        private boolean checkNodeIOName(SearchNode node) {
            return node != null && node.getIoName() != null;
        }

        private boolean checkSearchText(String searchText) {
            return _searchText != null && _searchText.trim().length() > 0;
        }

        public void setText(String searchText) {
            _searchText = searchText;

        }

        public void setCaseSensetive(boolean caseSensetive) {
            _caseSensetive = caseSensetive;
        }
    }

    private final class EpicsAddressViewerFilter extends ViewerFilter {
        private String _searchText = "";
        private boolean _caseSensetive;

        private EpicsAddressViewerFilter() {
        }

        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            if (element instanceof SearchNode) {
                SearchNode sNode = (SearchNode) element;
                if (checkSearchText(_searchText) && checkNodeEpicsAddress(sNode)) {
                    String string1;
                    String string2;
                    if(_caseSensetive) {
                        string1 = sNode.getEpicsAddressString();
                        string2 = _searchText;
                    } else {
                        string1 = sNode.getEpicsAddressString().toLowerCase();
                        string2 = _searchText.toLowerCase();
                        
                    }
                    return string1.contains(string2);
                }
            }
            return !checkSearchText(_searchText);
        }

        private boolean checkNodeEpicsAddress(SearchNode node) {
            return node != null && node.getEpicsAddressString() != null;
        }

        private boolean checkSearchText(String searchText) {
            return _searchText != null && _searchText.trim().length() > 0;
        }

        public void setText(String searchText) {
            _searchText = searchText;

        }

        public void setCaseSensetive(boolean caseSensetive) {
            _caseSensetive = caseSensetive;
        }
    }

    /**
     * This class provides the content for the table.
     */
    public class TableContentProvider implements IStructuredContentProvider {

        @SuppressWarnings("unchecked")
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof List) {
                return ((List) inputElement).toArray();
            }
            return null;
        }

        public void dispose() {
            // We don't create any resources, so we don't dispose any
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            // TODO Auto-generated method stub

        }

    }

    private List<SearchNode> _load;
    private final ProfiBusTreeView _profiBusTreeView;

    protected SearchDialog(Shell parentShell, ProfiBusTreeView profiBusTreeView) {
        super(parentShell);
        _profiBusTreeView = profiBusTreeView;
//        setShellStyle(SWT.RESIZE | parentShell.getStyle() | SWT.PRIMARY_MODAL);
        setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.MAX | SWT.RESIZE | SWT.PRIMARY_MODAL);
        _load = Repository.load(SearchNode.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite dialogArea = (Composite) super.createDialogArea(parent);
        dialogArea.getShell().setText("Search DDB Node");
        dialogArea.setLayout(GridLayoutFactory.swtDefaults().numColumns(6).equalWidth(false)
                .create());
        
        Label label = new Label(dialogArea, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false,2,1));
        label.setText("Name:");
        label = new Label(dialogArea, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false,2,1));
        label.setText("IO Name:");
        label = new Label(dialogArea, SWT.NONE);
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false,2,1));
        label.setText("EPICS Address:");

        GridDataFactory gdfText = GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(
                true, false);

        final Button csNameButton = new Button(dialogArea, SWT.CHECK);
        csNameButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        csNameButton.setToolTipText("Case Sensitive");

        final Text searchTextName = new Text(dialogArea, SWT.SINGLE | SWT.LEAD | SWT.BORDER
                | SWT.SEARCH);
        gdfText.applyTo(searchTextName);
        searchTextName.setMessage("Name Filter");

        Button csIONameButton = new Button(dialogArea, SWT.CHECK);
        csIONameButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        csIONameButton.setToolTipText("Case Sensitive");
        final Text searchTextIOName = new Text(dialogArea, SWT.SINGLE | SWT.LEAD | SWT.BORDER
                | SWT.SEARCH);
        gdfText.applyTo(searchTextIOName);
        searchTextIOName.setMessage("IO Name Filter");
        TableColumnLayout tableColumnLayout = new TableColumnLayout();

        Button csEASButton = new Button(dialogArea, SWT.CHECK);
        csEASButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        csEASButton.setToolTipText("Case Sensitive");
        final Text searchTextAddressString = new Text(dialogArea, SWT.SINGLE | SWT.LEAD
                | SWT.BORDER | SWT.SEARCH);
        gdfText.applyTo(searchTextAddressString);
        searchTextAddressString.setMessage("Epics Address Filter");

        Composite tableComposite = new Composite(dialogArea, SWT.FULL_SELECTION);
        GridDataFactory.fillDefaults().grab(true, true).span(6, 1).hint(800, 300).applyTo(
                tableComposite);
        tableComposite.setLayout(tableColumnLayout);

        final TableViewer resultTableView = new TableViewer(tableComposite);
        resultTableView.getTable().setHeaderVisible(true);
        resultTableView.getTable().setLinesVisible(true);
        final ViewerSorterExtension sorter = new ViewerSorterExtension();
        resultTableView.setSorter(sorter);

        // Column Subject
        int state = 0;
        TableViewerColumn columnName = new TableViewerColumn(resultTableView, SWT.NONE);
        columnName.getColumn().addSelectionListener(new SortSelectionListener(sorter, state++));
        columnName.getColumn().setText("Name");
        columnName.setLabelProvider(new CellLabelProvider() {
            public void update(ViewerCell cell) {
                if (cell.getElement() instanceof SearchNode) {
                    cell.setText(((SearchNode) cell.getElement()).getName());
                }
            }
        });
        tableColumnLayout.setColumnData(columnName.getColumn(), new ColumnWeightData(3, 40, true));

        TableViewerColumn columnIOName = new TableViewerColumn(resultTableView, SWT.FULL_SELECTION);
        columnIOName.getColumn().setText("IO Name");
        columnIOName.getColumn().addSelectionListener(new SortSelectionListener(sorter, state++));
        columnIOName.setLabelProvider(new CellLabelProvider() {
            public void update(ViewerCell cell) {
                if (cell.getElement() instanceof SearchNode) {
                    SearchNode channel = (SearchNode) cell.getElement();
                    cell.setText(channel.getIoName());
                }
            }
        });
        tableColumnLayout
                .setColumnData(columnIOName.getColumn(), new ColumnWeightData(3, 40, true));

        TableViewerColumn columnEpicsAddress = new TableViewerColumn(resultTableView, SWT.NONE);
        columnEpicsAddress.getColumn().addSelectionListener(
                new SortSelectionListener(sorter, state++));
        columnEpicsAddress.getColumn().setText("Epics Address");
        columnEpicsAddress.setLabelProvider(new CellLabelProvider() {
            public void update(ViewerCell cell) {
                if (cell.getElement() instanceof SearchNode) {
                    SearchNode channel = (SearchNode) cell.getElement();
                    cell.setText(channel.getEpicsAddressString());
                }
            }
        });
        tableColumnLayout.setColumnData(columnEpicsAddress.getColumn(), new ColumnWeightData(2, 40,
                true));

        TableViewerColumn columnCreateBy = new TableViewerColumn(resultTableView, SWT.NONE);
        columnCreateBy.getColumn().addSelectionListener(new SortSelectionListener(sorter, state++));
        columnCreateBy.getColumn().setText("Create By");
        columnCreateBy.setLabelProvider(new CellLabelProvider() {
            public void update(ViewerCell cell) {
                if (cell.getElement() instanceof SearchNode) {
                    String createdBy = ((SearchNode) cell.getElement()).getCreatedBy();
                    if (createdBy != null) {
                        int pos = createdBy.indexOf('@');
                        if (pos > 0) {
                            createdBy = createdBy.substring(0, pos);
                        }
                        cell.setText(createdBy);
                    }

                }
            }
        });
        tableColumnLayout.setColumnData(columnCreateBy.getColumn(), new ColumnWeightData(2, 40,
                true));

        TableViewerColumn columnCreateOn = new TableViewerColumn(resultTableView, SWT.NONE);
        columnCreateOn.getColumn().addSelectionListener(new SortSelectionListener(sorter, state++));
        columnCreateOn.getColumn().setText("Create On");
        columnCreateOn.setLabelProvider(new CellLabelProvider() {
            public void update(ViewerCell cell) {
                if (cell.getElement() instanceof SearchNode) {
                    Date createdOn = ((SearchNode) cell.getElement()).getCreatedOn();
                    if (createdOn != null) {
                        cell.setText(createdOn.toString());
                    }
                }
            }
        });
        tableColumnLayout.setColumnData(columnCreateOn.getColumn(), new ColumnWeightData(2, 100,
                true));

        TableViewerColumn columnUpdatedBy = new TableViewerColumn(resultTableView, SWT.NONE);
        columnUpdatedBy.getColumn()
                .addSelectionListener(new SortSelectionListener(sorter, state++));
        columnUpdatedBy.getColumn().setText("Updated By");
        columnUpdatedBy.setLabelProvider(new CellLabelProvider() {
            public void update(ViewerCell cell) {
                if (cell.getElement() instanceof SearchNode) {
                    String updatedBy = ((SearchNode) cell.getElement()).getUpdatedBy();
                    if (updatedBy != null) {
                        int pos = updatedBy.indexOf('@');
                        if (pos > 0) {
                            updatedBy = updatedBy.substring(0, pos);
                        }
                        cell.setText(updatedBy);
                    }
                }
            }
        });
        tableColumnLayout.setColumnData(columnUpdatedBy.getColumn(), new ColumnWeightData(2, 40,
                true));

        TableViewerColumn columnUpdatedOn = new TableViewerColumn(resultTableView, SWT.NONE);
        columnUpdatedOn.getColumn()
                .addSelectionListener(new SortSelectionListener(sorter, state++));
        columnUpdatedOn.getColumn().setText("Updated On");
        columnUpdatedOn.setLabelProvider(new CellLabelProvider() {
            public void update(ViewerCell cell) {
                if (cell.getElement() instanceof SearchNode) {
                    Date updatedOn = ((SearchNode) cell.getElement()).getUpdatedOn();
                    if (updatedOn != null) {
                        cell.setText(updatedOn.toString());
                    }
                }
            }
        });
        tableColumnLayout.setColumnData(columnUpdatedOn.getColumn(), new ColumnWeightData(2, 100,
                true));

        TableViewerColumn columnId = new TableViewerColumn(resultTableView, SWT.RIGHT);
        columnId.getColumn().addSelectionListener(new SortSelectionListener(sorter, state++));
        columnId.getColumn().setText("DB Id");
        columnId.setLabelProvider(new CellLabelProvider() {
            public void update(ViewerCell cell) {
                if (cell.getElement() instanceof SearchNode) {
                    Integer id = ((SearchNode) cell.getElement()).getId();
                    cell.setText(id.toString());
                }
            }
        });
        tableColumnLayout.setColumnData(columnId.getColumn(), new ColumnWeightData(2, 2, true));

        TableViewerColumn columnParentId = new TableViewerColumn(resultTableView, SWT.RIGHT);
        columnParentId.getColumn().addSelectionListener(new SortSelectionListener(sorter, state++));
        columnParentId.getColumn().setText("ParentId");
        columnParentId.setLabelProvider(new CellLabelProvider() {
            public void update(ViewerCell cell) {
                if (cell.getElement() instanceof SearchNode) {
                    Integer id = ((SearchNode) cell.getElement()).getParentId();
                    if (id != null) {
                        cell.setText(id.toString());
                    }
                }
            }
        });
        tableColumnLayout.setColumnData(columnParentId.getColumn(),
                new ColumnWeightData(2, 2, true));

        resultTableView.setContentProvider(new TableContentProvider());

        final NameViewerFilter nameViewerFilter = new NameViewerFilter();
        resultTableView.addFilter(nameViewerFilter);
        final IONameViewerFilter ioNameViewerFilter = new IONameViewerFilter();
        resultTableView.addFilter(ioNameViewerFilter);
        final EpicsAddressViewerFilter epicsAddressViewerFilter = new EpicsAddressViewerFilter();
        resultTableView.addFilter(epicsAddressViewerFilter);

        csNameButton.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                setCaseSensetive();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                setCaseSensetive();
            }
            
            private void setCaseSensetive() {
                nameViewerFilter.setCaseSensetive(csNameButton.getSelection());
                resultTableView.refresh();
            }
        });

        searchTextName.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                nameViewerFilter.setText(searchTextName.getText());
                resultTableView.refresh();
            }

        });

        csIONameButton.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                setCaseSensetive();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                setCaseSensetive();
            }

            private void setCaseSensetive() {
                ioNameViewerFilter.setCaseSensetive(csNameButton.getSelection());
                resultTableView.refresh();
            }
        });


        searchTextIOName.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                ioNameViewerFilter.setText(searchTextIOName.getText());
                resultTableView.refresh();
            }

        });

        csEASButton.addSelectionListener(new SelectionListener() {
            
            @Override
            public void widgetSelected(SelectionEvent e) {
                setCaseSensetive();
            }
            
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                setCaseSensetive();
            }

            private void setCaseSensetive() {
                epicsAddressViewerFilter.setCaseSensetive(csNameButton.getSelection());
                resultTableView.refresh();
            }
        });
        
        searchTextAddressString.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                epicsAddressViewerFilter.setText(searchTextAddressString.getText());
                resultTableView.refresh();
            }

        });

        resultTableView.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                StructuredSelection selection = (StructuredSelection) event.getSelection();
                _searchNode = (SearchNode) selection.getFirstElement();
                if (_searchNode != null) {
                    _selectedId = _searchNode.getId();
                    if (_selectedId > 0) {
                        _selectedNode = NodeMap.get(_selectedId);
                    }
                } else {
                    _selectedId = null;
                }
            }
        });

        resultTableView.setInput(_load);
//        columnName.getColumn().pack();
//        columnIOName.getColumn().pack();
//        columnEpicsAddress.getColumn().pack();
//        columnCreateBy.getColumn().pack();
//        columnUpdatedBy.getColumn().pack();
//        columnId.getColumn().pack();
//        columnParentId.getColumn().pack();

        /**/
        return dialogArea;
    }

    public SearchNode getSelectedNode() {
        return _searchNode;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void okPressed() {
        if (_selectedNode == null && _selectedId != null && _selectedId > 0) {
            boolean openQuestion = MessageDialog.openQuestion(this.getParentShell(), "Nicht geladen",
                    "Ihre Auswahl wurde noch nicht geladen. Soll sie jetzt geladen werden?");
            if (openQuestion) {
                _selectedNode = Repository.load(Node.class, _selectedId);
                if (_selectedNode != null) {
                    Node parentNode = _selectedNode;
                    while (!parentNode.isRootNode()) {
                        parentNode = parentNode.getParent();
                    }
                    List<Facility> input = (List<Facility>)_profiBusTreeView.getTreeViewer().getInput();
                    Facility facility = (Facility) parentNode;
                    if(facility.getId()==_selectedNode.getId()) {
                        _profiBusTreeView.getTreeViewer().setSelection(new StructuredSelection(facility));
                        super.okPressed();
                        return;
                    } else {
                        _profiBusTreeView.getTreeViewer().expandToLevel(facility,1);
                    }
                }
                _profiBusTreeView.getTreeViewer().expandToLevel(_selectedNode, _profiBusTreeView.getTreeViewer().ALL_LEVELS);
                _profiBusTreeView.getTreeViewer().setSelection(new StructuredSelection(_selectedNode));
            }
        }else if(_selectedNode != null) {
            _profiBusTreeView.getTreeViewer().setSelection(new StructuredSelection(_selectedNode));
            _profiBusTreeView.getTreeViewer().getTree().showSelection();
            _profiBusTreeView.layout();
//            _profiBusTreeView.getTreeViewer().expandToLevel(_selectedNode, _profiBusTreeView.getTreeViewer().ALL_LEVELS);
        }
        super.okPressed();
    }
}
