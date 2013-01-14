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
package org.csstudio.utility.nameSpaceSearch.ui;

import static org.csstudio.utility.ldap.service.util.LdapUtils.createLdapName;

import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.COMPONENT;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.FACILITY;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.IOC;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.RECORD;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.UNIT;
import static org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes.FIELD_ASSIGNMENT;
import static org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes.FIELD_WILDCARD;

import java.util.ArrayList;
import java.util.HashMap;

import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.ui.util.dnd.ControlSystemDragSource;
import org.csstudio.ui.util.dnd.ControlSystemDropTarget;
import org.csstudio.utility.ldap.service.ILdapReadCompletedCallback;
import org.csstudio.utility.ldap.service.ILdapReaderJob;
import org.csstudio.utility.ldap.service.ILdapSearchParams;
import org.csstudio.utility.ldap.service.ILdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.nameSpaceSearch.Activator;
import org.csstudio.utility.nameSpaceSearch.Messages;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * 
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 05.03.2008
 */
public class MainView extends ViewPart {
	/**
	 * The Class Id.
	 */
	public static final String ID = MainView.class.getName();

	private Text _searchText;
	private TableViewer _resultTableView;
	private boolean _lastSortBackward;
	private final int[] _sorts = { 0, 0, 0 };
	private Image _up;
	private Image _upOld;
	private Image _down;
	private Image _downOld;
	private final HashMap<String, String> _headline = new HashMap<String, String>();
	private Image _workDisable;
	private Label _workIcon;
	ILdapReaderJob _readerJob;
	private Display _disp;
	/**
	 * The search Button.
	 */
	private Button _searchButton;

	/**
	 * 
	 * @author hrickens
	 * @author $Author$
	 * @version $Revision$
	 * @since 20.02.2008
	 */
	class MyTableLabelProvider implements ITableLabelProvider {
		// No Image
		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			if (element instanceof ProcessVariableItem) {
				final ProcessVariableItem pv = (ProcessVariableItem) element;
				try {
					if (pv.getPath() != null) {
						return pv.getPath()[columnIndex].split("=")[1]; //$NON-NLS-1$
					}
					return "";
				} catch (final ArrayIndexOutOfBoundsException e) {
					return "";
				}

			} else if (element instanceof ArrayList) {
				@SuppressWarnings("unchecked")
				final ProcessVariableItem o = ((ArrayList<ProcessVariableItem>) element)
						.get(columnIndex);
				return o.getName();
			}
			return element.toString(); //$NON-NLS-1$

		}

		@Override
		public void addListener(final ILabelProviderListener listener) {
			// EMPTY
		}

		@Override
		public void dispose() {
			// EMPTY
		}

		@Override
		public boolean isLabelProperty(final Object element,
				final String property) {
			return false;
		}

		@Override
		public void removeListener(final ILabelProviderListener listener) {
			// EMPTY
		}
	}

	class myContentProvider implements IStructuredContentProvider {

		@Override
		public Object[] getElements(final Object inputElement) {
			if (inputElement instanceof ArrayList) {
				@SuppressWarnings("unchecked")
				ArrayList<Object> elements = (ArrayList<Object>) inputElement;
				if (elements.isEmpty()) {
					return new String[] { "no entry found" };
				}
				return elements.toArray();
			}
			return (Object[]) inputElement;
		}

		@Override
		public void dispose() {
			// EMPTY
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput,
				final Object newInput) {
			// EMPTY
		}

	}

	public MainView() {
	    // EMPTY
	}

	/**
	 * Make the Plugin UI. - A Text-field for the Searchword [searchText]. - A
	 * Pushbutton to start search [serachButton]. - A result-table to view the
	 * result [ergebnissTable]. - Header as Button to Sort the table. - D&D/MB3
	 * function on a row.
	 * 
	 * @param parent
	 *            the Parent Composite.
	 * 
	 **/
	@Override
	@SuppressWarnings("unused")
	public final void createPartControl(final Composite parent) {

		_disp = parent.getDisplay();
		parent.setLayout(new GridLayout(3, false));
		setUp(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
				"icons/up.gif").createImage()); //$NON-NLS-1$
		setDown(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
				"icons/down.gif").createImage()); //$NON-NLS-1$
		setUpOld(AbstractUIPlugin.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, "icons/up_old.gif").createImage()); //$NON-NLS-1$
		setDownOld(AbstractUIPlugin.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, "icons/down_old.gif").createImage()); //$NON-NLS-1$
		_workDisable = AbstractUIPlugin.imageDescriptorFromPlugin(
				Activator.PLUGIN_ID, "icons/LDAPLupe.gif").createImage(); //$NON-NLS-1$

		setSearchText(makeSearchField(parent));

		// make Search Button
		setSearchButton(new Button(parent, SWT.PUSH));
		getSearchButton().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		getSearchButton().setFont(
				new Font(parent.getDisplay(),
						Messages.MainView_SearchButtonFont, 10, SWT.NONE));
		getSearchButton().setText(Messages.MainView_searchButton); //$NON-NLS-1$

		// make Serach Activity Icon
		setWorkIcon(new Label(parent, SWT.NONE));
		getWorkIcon().setImage(_workDisable);
		getWorkIcon().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		getWorkIcon().setEnabled(false);
		// world.getImageData().

		// make ErgebnisTable
		setResultTableView(new TableViewer(parent, SWT.MULTI
				| SWT.FULL_SELECTION));
		final Table ergebnissTable = getResultTableView().getTable();
		ergebnissTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true, 3, 1));
		ergebnissTable.setLinesVisible(true);
		ergebnissTable.setHeaderVisible(true);

		getResultTableView().setContentProvider(new myContentProvider());
		getResultTableView().setLabelProvider(new MyTableLabelProvider());

		// add Listeners
		getSearchButton().addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				getWorkIcon().setEnabled(true);
				getSearchButton().setEnabled(false);
				// workIcon.setImage(world);
				search(getSearchText().getText());
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {/*
																		 * do
																		 * nothing
																		 */
			}

		});

		getSearchText().addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(final KeyEvent e) {
				if (e.keyCode == SWT.CR) {
					getWorkIcon().setEnabled(true);
					getSearchButton().setEnabled(false);
					// workIcon.setImage(world);
					search(getSearchText().getText());
				}
			}

			@Override
			public void keyPressed(final KeyEvent e) {/* do nothing */
			}

		});

		// Make Table row Drageble

		new ControlSystemDragSource(getResultTableView().getControl()) {
			
			@Override
			public Object getSelection() {
                final Object[] obj = ((IStructuredSelection)getResultTableView().getSelection()).toArray();
                final ProcessVariable[] pvs = new ProcessVariable[obj.length];
                for (int i=0; i<pvs.length; ++i)
                    pvs[i] = new ProcessVariable(((ProcessVariableItem)obj[i]).getName());
                return pvs;
			}
		};
		
		// MB3
		makeContextMenu();
		getSearchText().forceFocus();
		parent.update();
		parent.layout();
	}

	@Override
	public void setFocus() {
		getSearchText().forceFocus();
	}

	public void startSearch(final String search) {
		getSearchText().setText(search);
		search(search);
	}

	/***************************************************************************
	 * 
	 * @param searchTemp
	 *            - Clear the resulttable - start a LDAP search - fill the
	 *            resulttable - first step generate the tableheadbuttons for
	 *            sort the table
	 * 
	 ***************************************************************************/
	protected void search(final String search) {
		// clear Leere die Tabelle
		getResultTableView().getTable().removeAll();
		getResultTableView().getTable().clearAll();
		getResultTableView().refresh();
		// ersetzt mehrfach vorkommende '*' durch einen. Da die LDAP abfrage
		// damit nicht zurecht kommt.
		String searchTemp = search.replaceAll("\\*\\**", FIELD_WILDCARD); //$NON-NLS-1$ //$NON-NLS-2$

		String filter = RECORD.getNodeTypeName() + FIELD_ASSIGNMENT
				+ searchTemp; //$NON-NLS-1$

		//(hrickens) [09.05.2011]: It will not automatically search with wildcard at the end 
//		if (searchTemp.compareTo(FIELD_WILDCARD) != 0) {
//			filter = filter.concat(FIELD_WILDCARD); //$NON-NLS-1$
//		}

		if (_headline.isEmpty()) {
			_headline.put(FACILITY.getNodeTypeName(),
					Messages.MainView_facility); //$NON-NLS-1$ //$NON-NLS-2$
			_headline.put(COMPONENT.getNodeTypeName(), Messages.MainView_ecom); //$NON-NLS-1$ //$NON-NLS-2$
			_headline.put(IOC.getNodeTypeName(), Messages.MainView_Controller); //$NON-NLS-1$ //$NON-NLS-2$
			_headline.put(RECORD.getNodeTypeName(), Messages.MainView_Record); //$NON-NLS-1$ //$NON-NLS-2$
		}
		final String finalFilter = filter;
		final ILdapSearchParams params = new ILdapSearchParams() {
			@Override
			public LdapName getSearchRoot() {
				return createLdapName(UNIT.getNodeTypeName(),
						UNIT.getUnitTypeValue());
			}

			@Override
			public String getFilter() {
				return finalFilter;
			}

			@Override
			public int getScope() {
				return SearchControls.SUBTREE_SCOPE;
			}
		};

		final ILdapService service = Activator.getDefault().getLdapService();
		if (service != null) {
			_readerJob = 
			    service.createLdapReaderJob(params,
                        					new ILdapReadCompletedCallback() {
                        						@Override
                        						public void onLdapReadComplete() {
                        							getDisp().syncExec(new Runnable() {
                        								@Override
                        								public void run() {
                        									getText(_readerJob.getSearchResult());
                        								}
                        							});
                        						}
                        					});

			_readerJob.schedule();
		} else {
			MessageDialog.openError(getSite().getShell(), "LDAP Access",
					"LDAP service unavailable. Retry later.");
		}
		getResultTableView().getTable().layout();
	}

	protected void getText(ILdapSearchResult searchResult) {
		getResultTableView().refresh(false);
		final ArrayList<ProcessVariable> tableElements = new ArrayList<ProcessVariable>();

		int i = 0;
		for (final SearchResult result : searchResult.getAnswerSet()) {
			final String row = result.getName();
			final String[] elements = row.split(","); //$NON-NLS-1$
			String path = ""; //$NON-NLS-1$
			for (int j = 0; j < elements.length; j++) {
				if (i == 0 && j >= getResultTableView().getTable().getColumnCount()) {
					final TableColumn tc = new TableColumn(
							getResultTableView().getTable(), SWT.NONE);
					tc.setResizable(true);
					tc.setWidth(getResultTableView().getTable().getSize().x / 4 - 4); // TODO:
																					// 4
																					// replace
																					// whit
																					// true
																					// columsize
					tc.setToolTipText(Messages.MainView_ToolTip_Sort);
					tc.setMoveable(true);
					final int spalte = j;
					tc.addSelectionListener(new SelectionListener() {
						boolean backward = true;

						@Override
						public void widgetDefaultSelected(final SelectionEvent e) {
							// do nothing
						}

						@Override
						public void widgetSelected(final SelectionEvent e) {
							backward = !backward;
							tc.setAlignment(SWT.LEFT);
							if (getSorts()[0] != spalte) {
								final TableColumn[] chil = tc.getParent()
										.getColumns();
								chil[getSorts()[1]].setImage(null);
								getSorts()[1] = getSorts()[0];
								setLastSortBackward(backward);
								if (isLastSortBackward()) {
									chil[getSorts()[1]].setImage(getDownOld());
								} else {
									chil[getSorts()[1]].setImage(getUpOld());
								}
							}
							getSorts()[0] = spalte;
							getResultTableView().setSorter(new TableSorter(
									getSorts()[0], backward, getSorts()[1],
									isLastSortBackward()));
							if (backward) {
								tc.setImage(getDown());
							} else {
								tc.setImage(getUp());
							}
							setLastSortBackward(backward);
						}
					});
					String temp = _headline.get(elements[j].split("=")[0]);
					if (temp == null) {
						temp = elements[j].split("=")[0]; //$NON-NLS-1$
					}
					if (j == 0) {
						temp = temp
								.concat(" (" + searchResult.getAnswerSet().size() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
					}
					tc.setText(temp);
				} else if (i == 0 && j == 0) {
					String tmp = getResultTableView().getTable().getColumn(j)
							.getText();
					tmp = tmp.substring(0, tmp.lastIndexOf("(") + 1) + searchResult.getAnswerSet().size() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
					getResultTableView().getTable().getColumn(j).setText(tmp);
				}
				path += "," + elements[j];
			}
			// TODO: hier Stecken irgend wo die Infos um die Table head aus dem
			// LDAP-Tree zu bekommen.
			if (elements.length == 1 && elements[0].split("=").length == 1) {
				elements[0] = "=" + elements[0];
				for (int k = 1; k < elements.length; k++) {
					elements[k] = elements[0];
				}
			}
			tableElements.add(new ProcessVariableItem(
					elements[0].split("=")[1], elements)); //$NON-NLS-1$
			i++;
		}
		getResultTableView().setContentProvider(new myContentProvider());
		getResultTableView().setLabelProvider(new MyTableLabelProvider());
		getResultTableView().setInput(tableElements);
		getResultTableView().refresh(true);
		getSearchButton().setEnabled(true);
		getWorkIcon().setEnabled(false);
	}

	/**
	 * Make the MB3-ContextMenu.
	 */
	private void makeContextMenu() {
		final MenuManager manager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		final Control contr = getResultTableView().getControl();
		manager.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(final IMenuManager menuManager) {
				menuManager.add(new Separator(
						IWorkbenchActionConstants.MB_ADDITIONS));
			}
		});
		final Menu menu = manager.createContextMenu(contr);
		contr.setMenu(menu);
		getSite().registerContextMenu(manager, getResultTableView());
	}

	/**
	 * 
	 * - Make the searchtext. - Layout - Dropsource
	 * 
	 * @return
	 */
	private Text makeSearchField(final Composite parent) {
		setSearchText(new Text(parent, SWT.BORDER | SWT.SINGLE));
		getSearchText().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		getSearchText().setText(FIELD_WILDCARD); //$NON-NLS-1$
		getSearchText().setToolTipText(Messages.MainView_ToolTip);

		new ControlSystemDropTarget(getSearchText(), ProcessVariable.class, String.class) {
			
			@Override
			public void handleDrop(Object item) {
                if (item instanceof ProcessVariable)
                {
                    final ProcessVariable pvs = (ProcessVariable) item;
                    	getSearchText().setText((String) pvs.getName());
            }
			}
		};
		
//		// Eclipse
//		final int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
//		final DropTarget target = new DropTarget(getSearchText(), operations);
//
//		// Receive data in Text or File format
//		final TextTransfer textTransfer = TextTransfer.getInstance();
//		final Transfer[] types = new Transfer[] { textTransfer };
//		target.setTransfer(types);
//
//		target.addDropListener(new DropTargetListener() {
//			@Override
//			public void dragEnter(final DropTargetEvent event) {
//				if (event.detail == DND.DROP_DEFAULT) {
//					if ((event.operations & DND.DROP_COPY) != 0) {
//						event.detail = DND.DROP_COPY;
//					} else {
//						event.detail = DND.DROP_NONE;
//					}
//				}
//			}
//
//			@Override
//			public void dragOver(final DropTargetEvent event) {
//				// EMPTY
//			}
//
//			@Override
//			public void dragOperationChanged(final DropTargetEvent event) {
//				if (event.detail == DND.DROP_DEFAULT) {
//					if ((event.operations & DND.DROP_COPY) != 0) {
//						event.detail = DND.DROP_COPY;
//					} else {
//						event.detail = DND.DROP_NONE;
//					}
//				}
//			}
//
//			@Override
//			public void dragLeave(final DropTargetEvent event) {
//				// EMPTY
//			}
//
//			@Override
//			public void dropAccept(final DropTargetEvent event) {
//				// EMPTY
//			}
//
//			@Override
//			public void drop(final DropTargetEvent event) {
//				if (textTransfer.isSupportedType(event.currentDataType)) {
//					getSearchText().insert((String) event.data);
//				}
//			}
//		});

		return getSearchText();
	}

	protected void setWorkIcon(Label workIcon) {
		_workIcon = workIcon;
	}

	protected Label getWorkIcon() {
		return _workIcon;
	}

	protected void setSearchButton(Button searchButton) {
		_searchButton = searchButton;
	}

	protected Button getSearchButton() {
		return _searchButton;
	}

	protected void setSearchText(Text searchText) {
		_searchText = searchText;
	}

	protected Text getSearchText() {
		return _searchText;
	}

	protected void setDisp(Display disp) {
		_disp = disp;
	}

	protected Display getDisp() {
		return _disp;
	}

	protected int[] getSorts() {
		return _sorts;
	}

	protected void setLastSortBackward(boolean lastSortBackward) {
		_lastSortBackward = lastSortBackward;
	}

	protected boolean isLastSortBackward() {
		return _lastSortBackward;
	}

	protected void setResultTableView(TableViewer resultTableView) {
		_resultTableView = resultTableView;
	}

	protected TableViewer getResultTableView() {
		return _resultTableView;
	}

	protected void setDownOld(Image downOld) {
		_downOld = downOld;
	}

	protected Image getDownOld() {
		return _downOld;
	}

	protected void setUpOld(Image upOld) {
		_upOld = upOld;
	}

	protected Image getUpOld() {
		return _upOld;
	}

	protected void setDown(Image down) {
		_down = down;
	}

	protected Image getDown() {
		return _down;
	}

	protected void setUp(Image up) {
		_up = up;
	}

	protected Image getUp() {
		return _up;
	}

}
