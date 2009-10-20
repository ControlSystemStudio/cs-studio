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
 * $Id$
 */
package org.csstudio.config.ioconfig.view;

import java.util.Date;
import java.util.List;

import org.csstudio.config.ioconfig.model.Repository;
import org.csstudio.config.ioconfig.model.SearchNode;
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
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 05.06.2009
 */
public class SearchDialog extends Dialog {

    private SearchNode _searchNode;

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

        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            if (e1 instanceof SearchNode && e2 instanceof SearchNode) {
                SearchNode node1 = (SearchNode) e1;
                SearchNode node2 = (SearchNode) e2;
                int asc = 1;
                if(_asc) {
                    asc = -1;
                }
                switch(_state) {
                    case 0:
                        if(node1.getName()!=null&&node2.getName()!=null) {
                            return asc * node1.getName().compareTo(node2.getName());
                        }
                        break;
                    case 1:
                        if(node1.getIoName()!=null&&node2.getIoName()!=null) {
                            return asc * node1.getIoName().compareTo(node2.getIoName());
                        }
                        break;
                    case 2:
                        if(node1.getEpicsAddressString()!=null&&node2.getEpicsAddressString()!=null) {
                            return asc * node1.getEpicsAddressString().compareTo(node2.getEpicsAddressString());
                        }
                        break;
                    case 3:
                        return asc * node1.getCreatedBy().compareTo(node2.getCreatedBy());
                    case 4:
                        return asc * node1.getCreatedOn().compareTo(node2.getCreatedOn());
                    case 5:
                        return asc * node1.getUpdatedBy().compareTo(node2.getUpdatedBy());
                    case 6:
                        return asc * node1.getUpdatedOn().compareTo(node2.getUpdatedOn());
                    case 7:
                        return asc * node1.getId()-node2.getId();
                    case 8:
                        return asc * node1.getParentId().compareTo(node2.getParentId());
                }
                return asc;
            }
            return super.compare(viewer, e1, e2);
        }

        public void setState(int state) {
            if(_state == state) {
                _asc=!_asc;
            }else {
                _asc=!false;
                _state = state;
            }
        }
    }

    private final class NameViewerFilter extends ViewerFilter {
        private String _searchText = "";

        private NameViewerFilter() {
        }

        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            if (element instanceof SearchNode) {
                SearchNode sNode = (SearchNode) element;
                if (checkSearchText(_searchText) && checkNodeName(sNode)) {
                    boolean contains = sNode.getName().contains(_searchText);
                    return contains;
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
    }

    private final class IONameViewerFilter extends ViewerFilter {
        private String _searchText = "";

        private IONameViewerFilter() {
        }

        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            if (element instanceof SearchNode) {
                SearchNode sNode = (SearchNode) element;
                if (checkSearchText(_searchText) && checkNodeIOName(sNode)) {
                    boolean contains = sNode.getIoName().contains(_searchText);
                    return contains;
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
    }

    private final class EpicsAddressViewerFilter extends ViewerFilter {
        private String _searchText = "";

        private EpicsAddressViewerFilter() {
        }

        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            if (element instanceof SearchNode) {
                SearchNode sNode = (SearchNode) element;
                if (checkSearchText(_searchText) && checkNodeEpicsAddress(sNode)) {
                    boolean contains = sNode.getEpicsAddressString().contains(_searchText);
                    return contains;
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

    protected SearchDialog(Shell parentShell, ProfiBusTreeView profiBusTreeView) {
        super(parentShell);
        setShellStyle(SWT.RESIZE|parentShell.getStyle());
        _load = Repository.load(SearchNode.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite dialogArea = (Composite) super.createDialogArea(parent);
        dialogArea.getShell().setText("Search DDB Node");
        dialogArea.setLayout(GridLayoutFactory.swtDefaults().numColumns(3).equalWidth(true)
                .create());
        new Label(dialogArea, SWT.NONE).setText("Name:");
        new Label(dialogArea, SWT.NONE).setText("IO Name:");
        new Label(dialogArea, SWT.NONE).setText("EPICS Address:");

        GridDataFactory gdfText = GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(
                true, false);

        final Text searchTextName = new Text(dialogArea, SWT.SINGLE | SWT.LEAD | SWT.BORDER
                | SWT.SEARCH);
        gdfText.applyTo(searchTextName);
        searchTextName.setMessage("Name Filter");

        final Text searchTextIOName = new Text(dialogArea, SWT.SINGLE | SWT.LEAD | SWT.BORDER
                | SWT.SEARCH);
        gdfText.applyTo(searchTextIOName);
        searchTextIOName.setMessage("IO Name Filter");
        TableColumnLayout tableColumnLayout = new TableColumnLayout();

        final Text searchTextAddressString = new Text(dialogArea, SWT.SINGLE | SWT.LEAD
                | SWT.BORDER | SWT.SEARCH);
        gdfText.applyTo(searchTextAddressString);
        searchTextAddressString.setMessage("Epics Address Filter");

        Composite tableComposite = new Composite(dialogArea, SWT.BORDER | SWT.FULL_SELECTION);
        GridDataFactory.fillDefaults().grab(true, true).span(3, 1).hint(800, 300).applyTo(
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
        tableColumnLayout.setColumnData(columnName.getColumn(), new ColumnWeightData(2, 40, true));

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
                .setColumnData(columnIOName.getColumn(), new ColumnWeightData(2, 40, true));

        TableViewerColumn columnEpicsAddress = new TableViewerColumn(resultTableView, SWT.NONE);
        columnEpicsAddress.getColumn().addSelectionListener(new SortSelectionListener(sorter, state++));
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
        columnUpdatedBy.getColumn().addSelectionListener(new SortSelectionListener(sorter, state++));
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
        columnUpdatedOn.getColumn().addSelectionListener(new SortSelectionListener(sorter, state++));
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

        searchTextName.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                nameViewerFilter.setText(searchTextName.getText());
                resultTableView.refresh();
            }

        });

        searchTextIOName.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                ioNameViewerFilter.setText(searchTextIOName.getText());
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
            }
        });

        resultTableView.setInput(_load);
        columnName.getColumn().pack();
        columnIOName.getColumn().pack();
        columnEpicsAddress.getColumn().pack();
        columnCreateBy.getColumn().pack();
        columnUpdatedBy.getColumn().pack();
        columnId.getColumn().pack();
        columnParentId.getColumn().pack();

        /**/
        return dialogArea;
    }

    public SearchNode getSelectedNode() {
        return _searchNode;
        // return getNode(_searchNode);
    }

}
