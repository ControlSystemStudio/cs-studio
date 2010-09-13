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

import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.COMPONENT;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.FACILITY;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.IOC;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.RECORD;
import static org.csstudio.utility.ldap.treeconfiguration.LdapEpicsControlsConfiguration.ROOT;
import static org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes.FIELD_ASSIGNMENT;
import static org.csstudio.utility.ldap.treeconfiguration.LdapFieldsAndAttributes.FIELD_WILDCARD;
import static org.csstudio.utility.ldap.utils.LdapUtils.createLdapName;

import java.util.ArrayList;
import java.util.HashMap;

import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;

import org.csstudio.platform.model.IControlSystemItem;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDragSource;
import org.csstudio.utility.ldap.service.ILdapReadCompletedCallback;
import org.csstudio.utility.ldap.service.ILdapSearchParams;
import org.csstudio.utility.ldap.service.ILdapSearchResult;
import org.csstudio.utility.ldap.service.ILdapService;
import org.csstudio.utility.ldap.utils.LdapSearchResult;
import org.csstudio.utility.nameSpaceSearch.Activator;
import org.csstudio.utility.nameSpaceSearch.Messages;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
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

    private final Preferences _pluginPreferences = Activator.getDefault().getPluginPreferences();

    private Text _searchText;
    private TableViewer _resultTableView;
    private boolean _lastSortBackward;
    private final int[] _sorts = {0,0,0};
    private Image _up;
    private Image _upOld;
    private Image _down;
    private Image _downOld;
    private final HashMap<String, String> _headline = new HashMap<String, String>();
    private Image _workDisable;
    private Label _workIcon;
    private Job _ldapr;
    private Display _disp;
    private final ILdapSearchResult _ldapSearchResult;
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
            if (element instanceof ProcessVariable) {
                final ProcessVariable pv = (ProcessVariable) element;
                try{
                    if(pv.getPath()!=null){
                        return pv.getPath()[columnIndex].split("=")[1]; //$NON-NLS-1$
                    }
                    return "";
                }catch (final ArrayIndexOutOfBoundsException e) {
                    return "";
                }

            }
            if (element instanceof ArrayList) {
                @SuppressWarnings("unchecked")
                final
                Object o = ((ArrayList)element).get(columnIndex);
                if (o instanceof IControlSystemItem) {
                    return ((IProcessVariable)o).getName();
                }
                return "AL: " + o.toString(); //$NON-NLS-1$
            }
            return "toStr: " + element.toString(); //$NON-NLS-1$

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
        public boolean isLabelProperty(final Object element, final String property) {
            return false;
        }

        @Override
        public void removeListener(final ILabelProviderListener listener) {
            // EMPTY
        }
    }

    class myContentProvider implements IStructuredContentProvider{

        @Override
        @SuppressWarnings("unchecked")
		public Object[] getElements(final Object inputElement) {
            if (inputElement instanceof ArrayList) {
                return ((ArrayList)inputElement).toArray();
            }
            return (Object[])inputElement;
        }

        @Override
        public void dispose() {
            // EMPTY
        }

        @Override
        public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
            // EMPTY
        }

    }


    public MainView() {
        _ldapSearchResult = new LdapSearchResult();
    }

    /**
     * Make the Plugin UI.
     * - A Text-field for the Searchword [searchText].
     * - A Pushbutton to start search [serachButton].
     * - A result-table to view the result [ergebnissTable].
     *   - Header as Button to Sort the table.
     *   - D&D/MB3 function on a row.
     *  @param parent the Parent Composite.
     *
     **/
    @Override
    public final void createPartControl(final Composite parent) {
        _disp = parent.getDisplay();
        parent.setLayout(new GridLayout(3,false));
        _up = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/up.gif").createImage(); //$NON-NLS-1$
        _down = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/down.gif").createImage(); //$NON-NLS-1$
        _upOld = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/up_old.gif").createImage(); //$NON-NLS-1$
        _downOld = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/down_old.gif").createImage(); //$NON-NLS-1$
        _workDisable = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/LDAPLupe.gif").createImage(); //$NON-NLS-1$

        _searchText = makeSearchField(parent);

        // make Search Button
        _searchButton = new Button(parent,SWT.PUSH);
        _searchButton.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false,1,1));
        _searchButton.setFont(new Font(parent.getDisplay(),Messages.MainView_SearchButtonFont,10,SWT.NONE));
        _searchButton.setText(Messages.MainView_searchButton); //$NON-NLS-1$

        // make Serach Activity Icon
        _workIcon = new Label(parent,SWT.NONE);
        _workIcon.setImage(_workDisable);
        _workIcon.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false,1,1));
        _workIcon.setEnabled(false);
//      world.getImageData().

        // make ErgebnisTable
        _resultTableView = new TableViewer(parent,SWT.MULTI|SWT.FULL_SELECTION);
        final Table ergebnissTable = _resultTableView.getTable();
        ergebnissTable.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,3,1));
        ergebnissTable.setLinesVisible (true);
        ergebnissTable.setHeaderVisible (true);

        _resultTableView.setContentProvider(new myContentProvider());
        _resultTableView.setLabelProvider(new MyTableLabelProvider());


        // add Listeners
        _searchButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                _workIcon.setEnabled(true);
                _searchButton.setEnabled(false);
//              workIcon.setImage(world);
                search(_searchText.getText());
            }
            @Override
            public void widgetDefaultSelected(final SelectionEvent e) {}

        });

        _searchText.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(final KeyEvent e) {
                if(e.keyCode==SWT.CR){
                    _workIcon.setEnabled(true);
                    _searchButton.setEnabled(false);
//                  workIcon.setImage(world);
                    search(_searchText.getText());
                }
            }

            @Override
            public void keyPressed(final KeyEvent e) {}

        });

        // Make Table row Drageble
        new ProcessVariableDragSource(_resultTableView.getControl(), _resultTableView);
        // MB3
        makeContextMenu();
        _searchText.forceFocus();
        parent.update();
        parent.layout();
    }

        @Override
    public void setFocus() {
        _searchText.forceFocus();
    }

    public void startSearch(final String search){
        _searchText.setText(search);
        search(search);
    }

    /***************************************************************************
     *
     * @param search
     * - Clear the resulttable
     * - start a LDAP search
     * - fill the  resulttable
     *   - first step generate the tableheadbuttons for sort the table
     *
     ***************************************************************************/
    protected void search(String search) {
        // Leere die Tabelle
        _resultTableView.getTable().removeAll();
        _resultTableView.getTable().clearAll();
        _resultTableView.refresh();
        // ersetzt mehrfach vorkommende '*' durch einen. Da die LDAP abfrage damit nicht zurecht kommt.
        search = search.replaceAll("\\*\\**", FIELD_WILDCARD); //$NON-NLS-1$ //$NON-NLS-2$

        String filter = RECORD.getNodeTypeName() + FIELD_ASSIGNMENT + search; //$NON-NLS-1$

        if(search.compareTo(FIELD_WILDCARD) != 0) {
            filter = filter.concat(FIELD_WILDCARD); //$NON-NLS-1$
        }

        if(_headline.isEmpty()){
            _headline.put(FACILITY.getNodeTypeName(), Messages.MainView_facility); //$NON-NLS-1$ //$NON-NLS-2$
            _headline.put(COMPONENT.getNodeTypeName(), Messages.MainView_ecom); //$NON-NLS-1$ //$NON-NLS-2$
            _headline.put(IOC.getNodeTypeName(), Messages.MainView_Controller); //$NON-NLS-1$ //$NON-NLS-2$
            _headline.put(RECORD.getNodeTypeName(), Messages.MainView_Record); //$NON-NLS-1$ //$NON-NLS-2$
        }
        final String finalFilter = filter;
        final ILdapSearchParams params = new ILdapSearchParams() {
            @Override
            public LdapName getSearchRoot() {
                return createLdapName(ROOT.getNodeTypeName(), ROOT.getRootTypeValue());
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
            _ldapr = service.createLdapReaderJob(params,
                                                 _ldapSearchResult,
                                                 new ILdapReadCompletedCallback() {
                @Override
                public void onLdapReadComplete() {
                    _disp.syncExec(new Runnable() {
                        @Override
                        public void run() {
                            getText();
                        }
                    });
                }
            });

            _ldapr.schedule();
        } else {
            MessageDialog.openError(getSite().getShell(), "LDAP Access", "LDAP service unavailable. Retry later.");
        }
        _resultTableView.getTable().layout();
    }

    private void getText() {
        _resultTableView.refresh(false);
        final ArrayList<IControlSystemItem> tableElements = new ArrayList<IControlSystemItem>();


        int i=0;
        for (final SearchResult result : _ldapSearchResult.getAnswerSet()) {
            final String row = result.getName();
            final String[] elements = row.split(","); //$NON-NLS-1$
            String path =""; //$NON-NLS-1$
            for(int j=0;j<elements.length;j++){
                if(i==0&&j>=_resultTableView.getTable().getColumnCount()){
//                  lastSort = new int[elements.length-1];
                    final TableColumn tc = new TableColumn(_resultTableView.getTable(),SWT.NONE);
                    tc.setResizable(true);
                    tc.setWidth(_resultTableView.getTable().getSize().x/4-4); // TODO: 4 replace whit true columsize
                    tc.setToolTipText(Messages.MainView_ToolTip_Sort);
                    tc.setMoveable(true);
                    final int spalte = j;
                    tc.addSelectionListener(new SelectionListener(){
                        boolean backward = true;
                        @Override
                        public void widgetDefaultSelected(final SelectionEvent e) {}
                        @Override
                        public void widgetSelected(final SelectionEvent e) {
                                backward=!backward;
                                tc.setAlignment(SWT.LEFT);
                                if(_sorts[0]!=spalte){
                                    final TableColumn[] chil = tc.getParent().getColumns();
                                    chil[_sorts[1]].setImage(null);
                                    _sorts[1]=_sorts[0];
                                    _lastSortBackward=backward;
                                    if(_lastSortBackward) {
                                        chil[_sorts[1]].setImage(_downOld);
                                    } else {
                                        chil[_sorts[1]].setImage(_upOld);
                                    }
                                }
                                _sorts[0]=spalte;
                                _resultTableView.setSorter(new TableSorter(_sorts[0],backward,_sorts[1], _lastSortBackward));
                                if(backward) {
                                    tc.setImage(_down);
                                } else {
                                    tc.setImage(_up);
                                }
                                _lastSortBackward=backward;
                        }
                    });
                    String temp=_headline.get(elements[j].split("=")[0]);
                    if(temp==null) {
                        temp = elements[j].split("=")[0]; //$NON-NLS-1$
                    }
                    if(j==0){
                        temp=temp.concat(" ("+_ldapSearchResult.getAnswerSet().size()+")"); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    tc.setText(temp);
                }else if(i==0&&j==0){
                    String tmp = _resultTableView.getTable().getColumn(j).getText();
                    tmp = tmp.substring(0,tmp.lastIndexOf("(")+1)+_ldapSearchResult.getAnswerSet().size()+")"; //$NON-NLS-1$ //$NON-NLS-2$
                    _resultTableView.getTable().getColumn(j).setText(tmp);
                }
                path +=","+elements[j];
            }
            // TODO: hier Stecken irgend wo die Infos um die Table head aus dem LDAP-Tree zu bekommen.
//            System.out.println("Path: "+path);
            if(elements.length==1&&elements[0].split("=").length==1){
            	elements[0] = "="+elements[0];
            	for (int k = 1; k < elements.length; k++) {
					elements[k] = elements[0];
				}
            }
            tableElements.add(new ProcessVariable(elements[0].split("=")[1],elements)); //$NON-NLS-1$
            i++;
        }
//      System.out.println("Thread test 2");
        _resultTableView.setContentProvider(new myContentProvider());
        _resultTableView.setLabelProvider(new MyTableLabelProvider());
        _resultTableView.setInput(tableElements);
        _resultTableView.refresh(true);
//      workIcon.setImage(work_disable);
        _searchButton.setEnabled(true);
        _workIcon.setEnabled(false);
//      parent.layout();
//      System.out.println("Thread test ende");
    }

    /**
     * Make the MB3-ContextMenu.
     */
    private void makeContextMenu() {
        final MenuManager manager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        final Control contr = _resultTableView.getControl();
        manager.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(final IMenuManager manager) {
                manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
            }
        });
        final Menu menu = manager.createContextMenu(contr);
        contr.setMenu(menu);
        getSite().registerContextMenu(manager, _resultTableView);
    }
    /**
     *
     * - Make the searchtext.
     *   - Layout
     *   - Dropsource
     *
     * @return
     */
    private Text makeSearchField(final Composite parent) {
            _searchText = new Text(parent,SWT.BORDER|SWT.SINGLE);
            _searchText.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false,1,1));
            _searchText.setText(FIELD_WILDCARD); //$NON-NLS-1$
            _searchText.setToolTipText(Messages.MainView_ToolTip);

            //   Eclipse
            final int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
            final DropTarget target = new DropTarget(_searchText, operations);

            // Receive data in Text or File format
            final TextTransfer textTransfer = TextTransfer.getInstance();
            final Transfer[] types = new Transfer[] {textTransfer};
            target.setTransfer(types);

            target.addDropListener(new DropTargetListener() {
              @Override
            public void dragEnter(final DropTargetEvent event) {
                 if (event.detail == DND.DROP_DEFAULT) {
                     if ((event.operations & DND.DROP_COPY) != 0) {
                         event.detail = DND.DROP_COPY;
                     } else {
                         event.detail = DND.DROP_NONE;
                     }
                 }
               }
               @Override
            public void dragOver(final DropTargetEvent event) {
                   // EMPTY
               }
               @Override
            public void dragOperationChanged(final DropTargetEvent event) {
                    if (event.detail == DND.DROP_DEFAULT) {
                        if ((event.operations & DND.DROP_COPY) != 0) {
                            event.detail = DND.DROP_COPY;
                        } else {
                            event.detail = DND.DROP_NONE;
                        }
                    }
                }
                @Override
                public void dragLeave(final DropTargetEvent event) {
                    // EMPTY
                }
                @Override
                public void dropAccept(final DropTargetEvent event) {
                    // EMPTY
                }
                @Override
                public void drop(final DropTargetEvent event) {
                    if (textTransfer.isSupportedType(event.currentDataType)) {
                       _searchText.insert((String)event.data);
                    }
                }
            });

        return _searchText;
    }

}
