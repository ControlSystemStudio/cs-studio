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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.Repository;
import org.csstudio.config.ioconfig.model.SearchNodeDBO;
import org.csstudio.config.ioconfig.model.tools.NodeMap;
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

    private static final Logger LOG = LoggerFactory.getLogger(SearchDialog.class);
    
	private SearchNodeDBO _searchNode;
	private AbstractNodeDBO _selectedNode;
	private Integer _selectedId;

	/**
	 * 
	 * Sorter for each column of the Search Dialog Table.
	 * 
	 * @author hrickens
	 * @author $Author: $
	 * @since 23.09.2010
	 */
	private final class SortSelectionListener implements SelectionListener {
		private final ViewerSorterExtension _sorter;
		private final int _state;

		SortSelectionListener(@Nonnull ViewerSorterExtension sorter, int state) {
			_sorter = sorter;
			_state = state;
		}

		@Override
		public void widgetDefaultSelected(@Nullable SelectionEvent e) {
			setState();
		}

		@Override
		public void widgetSelected(@Nullable SelectionEvent e) {
			setState();
		}

		private void setState() {
			_sorter.setState(_state);
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
	private final class ViewerSorterExtension extends ViewerSorter {
		private int _state = 0;
		private boolean _asc;
		private Viewer _viewer;

		public ViewerSorterExtension() {
			// Default Constructor.
		}

		@Override
		public int compare(@Nonnull Viewer viewer, @Nullable Object e1,
				@Nullable Object e2) {
			_viewer = viewer;
			if (e1 instanceof SearchNodeDBO && e2 instanceof SearchNodeDBO) {
				SearchNodeDBO node1 = (SearchNodeDBO) e1;
				SearchNodeDBO node2 = (SearchNodeDBO) e2;
				return compareSearchNodeDBO(node1, node2);
			}
			return super.compare(viewer, e1, e2);
		}

		/**
		 */
		// CHECKSTYLE OFF: CyclomaticComplexity
		private int compareSearchNodeDBO(@Nonnull SearchNodeDBO node1,
				@Nonnull SearchNodeDBO node2) {
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
				return compareString(node1.getEpicsAddressString(),
						node2.getEpicsAddressString(), asc);
			case 3:
				return compareString(node1.getCreatedBy(),
						node2.getCreatedBy(), asc);
			case 4:
				return compareDate(node1.getCreatedOn(), node2.getCreatedOn(),
						asc);
			case 5:
				return compareString(node1.getUpdatedBy(),
						node2.getUpdatedBy(), asc);
			case 6:
				return compareDate(node1.getUpdatedOn(), node2.getUpdatedOn(),
						asc);
			case 7:
				return asc * (node1.getId() - node2.getId());
			case 8:
				return compareNumber(node1.getParentId(), node2.getParentId(),
						asc);
			default:
			}
			return asc;
		}

		// CHECKSTYLE ON: CyclomaticComplexity

		private int compareNumber(@Nullable Number parentId,
				@Nullable Number parentId2, int asc) {
			int compareTo = 0;
			if (parentId == null) {
				compareTo = -1;
			} else if (parentId2 == null) {
				compareTo = 1;
			} else {
				compareTo = parentId.intValue() - parentId2.intValue();
			}
			return asc * compareTo;
		}

		private int compareDate(@CheckForNull Date date1,
				@CheckForNull Date date2, int asc) {
			if (date1 == null && date2 == null) {
				return 0;
			}
			if (date1 == null) {
				return -asc;
			}
			if (date2 == null) {
				return asc;
			}

			if (date1.before(date2)) {
				return asc;
			}
			return -asc;
		}

		private int compareString(@CheckForNull String string1,
				@CheckForNull String string2, int asc) {

			if (string1 == null && string2 == null) {
				return 0;
			}
			if (string1 == null) {
				return asc;
			}
			if (string2 == null) {
				return -asc;
			}
			return asc * string1.compareTo(string2);
		}

		public void setState(int state) {
			if (_state == state) {
				_asc = !_asc;
			} else {
				_asc = true;
				_state = state;
			}
			if (_viewer != null) {
				_viewer.refresh();
			}
		}
	}

	/**
	 * 
	 * Abstract Viewer Filter for String columns.
	 * 
	 * @author hrickens
	 * @author $Author: $
	 * @since 23.09.2010
	 */
	private abstract class AbstractStringViewerFilter extends ViewerFilter {

		private boolean _caseSensetive = false;
		private String _searchText = "";

		public AbstractStringViewerFilter() {
			// Default Constructor
		}

		/**
		 * @param sNode
		 * @return
		 */
		protected boolean compareStrings(@Nonnull String text) {
			String string1;
			String string2 = getSearchText();
            if (isCaseSensetive()) {
				string1 = text;
			} else {
				string1 = text.toLowerCase();
				if(string2!=null) {
				    string2 = string2.toLowerCase();
				}
			}
			return string1.contains(string2);
		}

		public void setCaseSensetive(boolean caseSensetive) {
			_caseSensetive = caseSensetive;
		}

		public boolean isCaseSensetive() {
			return _caseSensetive;
		}

		public void setText(@Nullable String searchText) {
			_searchText = searchText;
		}

		@CheckForNull
		public String getSearchText() {
			return _searchText;
		}

		protected boolean checkSearchText(@CheckForNull String searchText) {
			return _searchText != null && _searchText.trim().length() > 0;
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

		private boolean checkNodeName(@CheckForNull final SearchNodeDBO node) {
			return node != null && node.getName() != null;
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

		@Override
		public boolean select(@Nullable Viewer viewer,
				@Nullable Object parentElement, @Nullable Object element) {
			if (element instanceof SearchNodeDBO) {
				SearchNodeDBO sNode = (SearchNodeDBO) element;
				if (checkSearchText(getSearchText()) && checkNodeIOName(sNode)) {
					return compareStrings(sNode.getIoName());
				}
			}
			return !checkSearchText(getSearchText());
		}

		private boolean checkNodeIOName(@CheckForNull SearchNodeDBO node) {
			return node != null && node.getIoName() != null;
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

		@Override
		public boolean select(@Nullable Viewer viewer,
				@Nullable Object parentElement, @Nullable Object element) {
			if (element instanceof SearchNodeDBO) {
				SearchNodeDBO sNode = (SearchNodeDBO) element;
				if (checkSearchText(getSearchText())
						&& checkNodeEpicsAddress(sNode)) {
					return compareStrings(sNode.getEpicsAddressString());
				}
			}
			return !checkSearchText(getSearchText());
		}

		private boolean checkNodeEpicsAddress(@CheckForNull SearchNodeDBO node) {
			return node != null && node.getEpicsAddressString() != null;
		}

	}

	/**
	 * This class provides the content for the table.
	 */
	public class TableContentProvider implements IStructuredContentProvider {

		@Override
		@CheckForNull
		@SuppressWarnings("rawtypes")
		public Object[] getElements(@Nullable Object inputElement) {
			if (inputElement instanceof List) {
				return ((List) inputElement).toArray();
			}
			return null;
		}

		@Override
		public void dispose() {
			// We don't create any resources, so we don't dispose any
		}

		@Override
		public void inputChanged(@Nullable Viewer viewer,
				@Nullable Object oldInput, @Nullable Object newInput) {
		    // Empty
		}

	}

	private List<SearchNodeDBO> _load;
	private final ProfiBusTreeView _profiBusTreeView;

	protected SearchDialog(@Nullable Shell parentShell,
			@Nonnull ProfiBusTreeView profiBusTreeView) {
		super(parentShell);
		_profiBusTreeView = profiBusTreeView;
		setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.MAX | SWT.RESIZE
				| SWT.PRIMARY_MODAL);
		try {
            _load = Repository.load(SearchNodeDBO.class);
        } catch (PersistenceException e) {
            _load = null;
            DeviceDatabaseErrorDialog.open(null, "Can't read from Datebase!", e);
            LOG.error("Can't read from Datebase!", e);
        }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(@Nonnull Composite parent) {
		Composite localDialogArea1 = (Composite) super.createDialogArea(parent);
		localDialogArea1.getShell().setText("Search DDB Node");
		localDialogArea1.setLayout(GridLayoutFactory.swtDefaults()
				.numColumns(6).equalWidth(false).create());

		Label label = new Label(localDialogArea1, SWT.NONE);
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false, 2, 1));
		label.setText("Name:");
		label = new Label(localDialogArea1, SWT.NONE);
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false, 2, 1));
		label.setText("IO Name:");
		label = new Label(localDialogArea1, SWT.NONE);
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false, 2, 1));
		label.setText("EPICS Address:");

		GridDataFactory gdfText = GridDataFactory.swtDefaults()
				.align(SWT.FILL, SWT.CENTER).grab(true, false);

		final Button csNameButton = new Button(localDialogArea1, SWT.CHECK);
		csNameButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER,
				false, false));
		csNameButton.setToolTipText("Case Sensitive");

		final Text searchTextName = new Text(localDialogArea1, SWT.SINGLE
				| SWT.LEAD | SWT.BORDER | SWT.SEARCH);
		gdfText.applyTo(searchTextName);
		searchTextName.setMessage("Name Filter");

		final Button csIONameButton = new Button(localDialogArea1, SWT.CHECK);
		csIONameButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER,
				false, false));
		csIONameButton.setToolTipText("Case Sensitive");
		final Text searchTextIOName = new Text(localDialogArea1, SWT.SINGLE
				| SWT.LEAD | SWT.BORDER | SWT.SEARCH);
		gdfText.applyTo(searchTextIOName);
		searchTextIOName.setMessage("IO Name Filter");
		TableColumnLayout tableColumnLayout = new TableColumnLayout();

		final Button csEASButton = new Button(localDialogArea1, SWT.CHECK);
		csEASButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER,
				false, false));
		csEASButton.setToolTipText("Case Sensitive");
		final Text searchTextAddressString = new Text(localDialogArea1,
				SWT.SINGLE | SWT.LEAD | SWT.BORDER | SWT.SEARCH);
		gdfText.applyTo(searchTextAddressString);
		searchTextAddressString.setMessage("Epics Address Filter");

		Composite tableComposite = new Composite(localDialogArea1,
				SWT.FULL_SELECTION);
		GridDataFactory.fillDefaults().grab(true, true).span(6, 1)
				.hint(800, 300).applyTo(tableComposite);
		tableComposite.setLayout(tableColumnLayout);

		final TableViewer resultTableView = new TableViewer(tableComposite);
		resultTableView.getTable().setHeaderVisible(true);
		resultTableView.getTable().setLinesVisible(true);
		final ViewerSorterExtension sorter = new ViewerSorterExtension();
		resultTableView.setSorter(sorter);

		// Column Subject
		int state = 0;
		TableViewerColumn columnName = new TableViewerColumn(resultTableView,
				SWT.NONE);
		columnName.getColumn().addSelectionListener(
				new SortSelectionListener(sorter, state++));
		columnName.getColumn().setText("Name");
		columnName.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(@Nonnull ViewerCell cell) {
				if (cell.getElement() instanceof SearchNodeDBO) {
					cell.setText(((SearchNodeDBO) cell.getElement()).getName());
				}
			}
		});
		tableColumnLayout.setColumnData(columnName.getColumn(),
				new ColumnWeightData(3, 40, true));

		TableViewerColumn columnIOName = new TableViewerColumn(resultTableView,
				SWT.FULL_SELECTION);
		columnIOName.getColumn().setText("IO Name");
		columnIOName.getColumn().addSelectionListener(
				new SortSelectionListener(sorter, state++));
		columnIOName.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(@Nonnull ViewerCell cell) {
				if (cell.getElement() instanceof SearchNodeDBO) {
					SearchNodeDBO channel = (SearchNodeDBO) cell.getElement();
					cell.setText(channel.getIoName());
				}
			}
		});
		tableColumnLayout.setColumnData(columnIOName.getColumn(),
				new ColumnWeightData(3, 40, true));

		TableViewerColumn columnEpicsAddress = new TableViewerColumn(
				resultTableView, SWT.NONE);
		columnEpicsAddress.getColumn().addSelectionListener(
				new SortSelectionListener(sorter, state++));
		columnEpicsAddress.getColumn().setText("Epics Address");
		columnEpicsAddress.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(@Nonnull ViewerCell cell) {
				if (cell.getElement() instanceof SearchNodeDBO) {
					SearchNodeDBO channel = (SearchNodeDBO) cell.getElement();
					cell.setText(channel.getEpicsAddressString());
				}
			}
		});
		tableColumnLayout.setColumnData(columnEpicsAddress.getColumn(),
				new ColumnWeightData(2, 40, true));

		TableViewerColumn columnCreateBy = new TableViewerColumn(
				resultTableView, SWT.NONE);
		columnCreateBy.getColumn().addSelectionListener(
				new SortSelectionListener(sorter, state++));
		columnCreateBy.getColumn().setText("Create By");
		columnCreateBy.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(@Nonnull ViewerCell cell) {
				if (cell.getElement() instanceof SearchNodeDBO) {
					String createdBy = ((SearchNodeDBO) cell.getElement())
							.getCreatedBy();
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
		tableColumnLayout.setColumnData(columnCreateBy.getColumn(),
				new ColumnWeightData(2, 40, true));

		TableViewerColumn columnCreateOn = new TableViewerColumn(
				resultTableView, SWT.NONE);
		columnCreateOn.getColumn().addSelectionListener(
				new SortSelectionListener(sorter, state++));
		columnCreateOn.getColumn().setText("Create On");
		columnCreateOn.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(@Nonnull ViewerCell cell) {
				if (cell.getElement() instanceof SearchNodeDBO) {
					Date createdOn = ((SearchNodeDBO) cell.getElement())
							.getCreatedOn();
					if (createdOn != null) {
						cell.setText(createdOn.toString());
					}
				}
			}
		});
		tableColumnLayout.setColumnData(columnCreateOn.getColumn(),
				new ColumnWeightData(2, 100, true));

		TableViewerColumn columnUpdatedBy = new TableViewerColumn(
				resultTableView, SWT.NONE);
		columnUpdatedBy.getColumn().addSelectionListener(
				new SortSelectionListener(sorter, state++));
		columnUpdatedBy.getColumn().setText("Updated By");
		columnUpdatedBy.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(@Nonnull ViewerCell cell) {
				if (cell.getElement() instanceof SearchNodeDBO) {
					String updatedBy = ((SearchNodeDBO) cell.getElement())
							.getUpdatedBy();
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
		tableColumnLayout.setColumnData(columnUpdatedBy.getColumn(),
				new ColumnWeightData(2, 40, true));

		TableViewerColumn columnUpdatedOn = new TableViewerColumn(
				resultTableView, SWT.NONE);
		columnUpdatedOn.getColumn().addSelectionListener(
				new SortSelectionListener(sorter, state++));
		columnUpdatedOn.getColumn().setText("Updated On");
		columnUpdatedOn.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(@Nonnull ViewerCell cell) {
				if (cell.getElement() instanceof SearchNodeDBO) {
					Date updatedOn = ((SearchNodeDBO) cell.getElement())
							.getUpdatedOn();
					if (updatedOn != null) {
						cell.setText(updatedOn.toString());
					}
				}
			}
		});
		tableColumnLayout.setColumnData(columnUpdatedOn.getColumn(),
				new ColumnWeightData(2, 100, true));

		TableViewerColumn columnId = new TableViewerColumn(resultTableView,
				SWT.RIGHT);
		columnId.getColumn().addSelectionListener(
				new SortSelectionListener(sorter, state++));
		columnId.getColumn().setText("DB Id");
		columnId.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(@Nonnull ViewerCell cell) {
				if (cell.getElement() instanceof SearchNodeDBO) {
					Integer id = ((SearchNodeDBO) cell.getElement()).getId();
					cell.setText(id.toString());
				}
			}
		});
		tableColumnLayout.setColumnData(columnId.getColumn(),
				new ColumnWeightData(2, 2, true));

		TableViewerColumn columnParentId = new TableViewerColumn(
				resultTableView, SWT.RIGHT);
		columnParentId.getColumn().addSelectionListener(
				new SortSelectionListener(sorter, state++));
		columnParentId.getColumn().setText("ParentId");
		columnParentId.setLabelProvider(new CellLabelProvider() {
			@Override
			public void update(@Nonnull ViewerCell cell) {
				if (cell.getElement() instanceof SearchNodeDBO) {
					Integer id = ((SearchNodeDBO) cell.getElement())
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

		final NameViewerFilter nameViewerFilter = new NameViewerFilter();
		resultTableView.addFilter(nameViewerFilter);
		final IONameViewerFilter ioNameViewerFilter = new IONameViewerFilter();
		resultTableView.addFilter(ioNameViewerFilter);
		final EpicsAddressViewerFilter epicsAddressViewerFilter = new EpicsAddressViewerFilter();
		resultTableView.addFilter(epicsAddressViewerFilter);

		csNameButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(@Nullable SelectionEvent e) {
				setCaseSensetive();
			}

			@Override
			public void widgetDefaultSelected(@Nullable SelectionEvent e) {
				setCaseSensetive();
			}

			private void setCaseSensetive() {
				nameViewerFilter.setCaseSensetive(csNameButton.getSelection());
				resultTableView.refresh();
			}
		});

		searchTextName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(@Nullable ModifyEvent e) {
				nameViewerFilter.setText(searchTextName.getText());
				resultTableView.refresh();
			}

		});

		csIONameButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(@Nullable SelectionEvent e) {
				setCaseSensetive();
			}

			@Override
			public void widgetDefaultSelected(@Nullable SelectionEvent e) {
				setCaseSensetive();
			}

			private void setCaseSensetive() {
				ioNameViewerFilter.setCaseSensetive(csIONameButton.getSelection());
				resultTableView.refresh();
			}
		});

		searchTextIOName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(@Nullable ModifyEvent e) {
				ioNameViewerFilter.setText(searchTextIOName.getText());
				resultTableView.refresh();
			}

		});

		csEASButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(@Nullable SelectionEvent e) {
				setCaseSensetive();
			}

			@Override
			public void widgetDefaultSelected(@Nullable SelectionEvent e) {
				setCaseSensetive();
			}

			private void setCaseSensetive() {
				epicsAddressViewerFilter.setCaseSensetive(csEASButton
						.getSelection());
				resultTableView.refresh();
			}
		});

		searchTextAddressString.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(@Nullable ModifyEvent e) {
				epicsAddressViewerFilter.setText(searchTextAddressString
						.getText());
				resultTableView.refresh();
			}

		});

		resultTableView
				.addSelectionChangedListener(new ISelectionChangedListener() {
					@Override
					public void selectionChanged(
							@Nonnull SelectionChangedEvent event) {
						StructuredSelection selection = (StructuredSelection) event
								.getSelection();
						setSearchNode((SearchNodeDBO) selection
								.getFirstElement());
						SearchNodeDBO searchNode = getSearchNode();
                        if (searchNode != null) {
							setSelectedId(searchNode.getId());
							Integer selectedId = getSelectedId();
                            if (selectedId!=null && selectedId > 0) {
								setSelectedNode(NodeMap.get(selectedId));
							}
						} else {
							setSelectedId(null);
						}
					}
				});

		resultTableView.setInput(_load);
		return localDialogArea1;
	}

	@Override
	protected final void okPressed() {
		if (_selectedNode == null && getSelectedId() != null
				&& getSelectedId() > 0) {
			// boolean openQuestion =
			// MessageDialog.openQuestion(this.getParentShell(),
			// "Nicht geladen",
			// "Ihre Auswahl wurde noch nicht geladen. Soll sie jetzt geladen werden?");
			// if (openQuestion) {
			try {
                AbstractNodeDBO load = Repository.load(AbstractNodeDBO.class,
                		getSelectedId());
                setSelectedNode(load);
            } catch (PersistenceException e) {
                _searchNode=null;
                DeviceDatabaseErrorDialog.open(null, "Can't load seleceted node! Database Error.", e);
                LOG.error("Can't load seleceted node! Database Error.", e);
            }
			if (_searchNode != null) {
				AbstractNodeDBO parentNode = _selectedNode;
				while (!parentNode.isRootNode()) {
					parentNode = parentNode.getParent();
				}
				FacilityDBO facility = (FacilityDBO) parentNode;
				if (facility.getId() == _searchNode.getId()) {
					_profiBusTreeView.getTreeViewer().setSelection(
							new StructuredSelection(facility));
					super.okPressed();
					return;
				} else {
					_profiBusTreeView.getTreeViewer()
							.expandToLevel(facility, 1);
				}
			}
			showNode();
		} else if (_selectedNode != null) {
			showNode();
			
		}
		super.okPressed();
	}

	/**
	 * 
	 */
	private void showNode() {
	    _profiBusTreeView.getViewer().setSelection(new StructuredSelection(_selectedNode));
	}

	public final void setSearchNode(@Nullable SearchNodeDBO searchNode) {
		_searchNode = searchNode;
	}

	@CheckForNull
	public final SearchNodeDBO getSearchNode() {
		return _searchNode;
	}

	public final void setSelectedNode(@Nullable AbstractNodeDBO selectedNode) {
		_selectedNode = selectedNode;
	}

	public final void setSelectedId(@Nullable Integer selectedId) {
		_selectedId = selectedId;
	}

	@CheckForNull
	public final Integer getSelectedId() {
		return _selectedId;
	}
}
