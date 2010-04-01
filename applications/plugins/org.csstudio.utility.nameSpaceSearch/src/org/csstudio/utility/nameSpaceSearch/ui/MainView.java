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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javax.naming.directory.SearchResult;

import org.csstudio.platform.model.IControlSystemItem;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDragSource;
import org.csstudio.utility.ldap.reader.LDAPReader;
import org.csstudio.utility.ldap.reader.LdapSearchResult;
import org.csstudio.utility.nameSpaceSearch.Activator;
import org.csstudio.utility.nameSpaceSearch.Messages;
import org.csstudio.utility.nameSpaceSearch.preference.PreferenceConstants;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
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
public class MainView extends ViewPart implements Observer{
    /**
     * The Class Id.
     */
    public static final String ID = MainView.class.getName();
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
    private LDAPReader _ldapr;
    private Display _disp;
    private final LdapSearchResult _ldapSearchResult;
    /**
     * The search Button.
     */
    private Button _searchButton;

    /**
     * The Wildcard char.
     */
    private static final String WILDCARD  = "*"; //$NON-NLS-1$

    /**
     *
     * @author hrickens
     * @author $Author$
     * @version $Revision$
     * @since 20.02.2008
     */
    class MyTableLabelProvider implements ITableLabelProvider{
        // No Image
        public Image getColumnImage(final Object element, final int columnIndex) {return null;}


		public String getColumnText(final Object element, final int columnIndex) {
            if (element instanceof ProcessVariable) {
                final ProcessVariable pv = (ProcessVariable) element;
                try{
                	if(pv.getPath()!=null){
                		return pv.getPath()[columnIndex].split("=")[1]; //$NON-NLS-1$
                	}else{
                		return "";
                	}
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
                return "AL: "+o.toString(); //$NON-NLS-1$
            }
            return "toStr: "+element.toString(); //$NON-NLS-1$

        }

        public void addListener(final ILabelProviderListener listener) {}

        public void dispose() {}

        public boolean isLabelProperty(final Object element, final String property) {
            return false;
        }

        public void removeListener(final ILabelProviderListener listener) {}

    }

    class myContentProvider implements IStructuredContentProvider{

        @SuppressWarnings("unchecked")
		public Object[] getElements(final Object inputElement) {
            if (inputElement instanceof ArrayList) {
                return ((ArrayList)inputElement).toArray();
            }
            return (Object[])inputElement;
        }

        public void dispose() {     }

        public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {     }

    }

    public MainView() {
        _ldapSearchResult = new LdapSearchResult();
        _ldapSearchResult.addObserver(this);
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

            public void widgetSelected(final SelectionEvent e) {
                _workIcon.setEnabled(true);
                _searchButton.setEnabled(false);
//              workIcon.setImage(world);
                search(_searchText.getText());
            }
            public void widgetDefaultSelected(final SelectionEvent e) {}

        });

        _searchText.addKeyListener(new KeyListener() {

            public void keyReleased(final KeyEvent e) {
                if(e.keyCode==SWT.CR){
                    _workIcon.setEnabled(true);
                    _searchButton.setEnabled(false);
//                  workIcon.setImage(world);
                    search(_searchText.getText());
                }
            }

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
        search = search.replaceAll("\\*\\**", WILDCARD); //$NON-NLS-1$ //$NON-NLS-2$
        String filter = Activator.getDefault().getPluginPreferences().getString(PreferenceConstants.P_STRING_RECORD_ATTRIBUTE)+
        "="+search; //$NON-NLS-1$
        if(search.compareTo(WILDCARD)!=0) {
            filter = filter.concat(WILDCARD); //$NON-NLS-1$
        }

        if(_headline.isEmpty()){
            _headline.put("efan", Messages.MainView_facility); //$NON-NLS-1$ //$NON-NLS-2$
            _headline.put("ecom", Messages.MainView_ecom); //$NON-NLS-1$ //$NON-NLS-2$
            _headline.put("econ", Messages.MainView_Controller); //$NON-NLS-1$ //$NON-NLS-2$
            _headline.put("eren", Messages.MainView_Record); //$NON-NLS-1$ //$NON-NLS-2$
        }
        _ldapr = new LDAPReader(Activator.getDefault().getPluginPreferences().getString(PreferenceConstants.P_STRING_SEARCH_ROOT),
                filter, _ldapSearchResult);
//        _ldapr.addJobChangeListener(new JobChangeAdapter() {
//            public void done(IJobChangeEvent event) {
//            if (event.getResult().isOK())
//                MainView.this._ergebnisListe.notifyView();
//            }
//         });
        _ldapr.schedule();
        _resultTableView.getTable().layout();
    }

    private void getText() {
        _resultTableView.refresh(false);
        final ArrayList<IControlSystemItem> tableElements = new ArrayList<IControlSystemItem>();

//        ArrayList<String> list = new ArrayList<String>();
//        list.addAll(_ergebnisListe.getSimpleResultNames());

        /*
         * Vorbereitung für die Nutzung der ersten Zeile als Filter Feld.
         */
//        String filter = "";
//        String[] filters = list.get(0).split(",");
//        for (int i=0;i<filters.length;i++){
//            filter = filter.concat(filters[i].split("=")[0]+"=filter field,");
//        }
//        filter = filter.substring(0,filter.length()-1);
//        list.add(0, filter);
//
//        TextCellEditor[] ces = new TextCellEditor[]{new TextCellEditor(ergebnissTableView.getTable()),new TextCellEditor(ergebnissTableView.getTable()),new TextCellEditor(ergebnissTableView.getTable()),new TextCellEditor(ergebnissTableView.getTable())};
//        ergebnissTableView.setCellEditors(ces);
//        ergebnissTableView.setCellModifier(new ICellModifier(){
//
//            public boolean canModify(Object element, String property) {
//                System.out.println("canModify: "+element+" -- "+property);
//                if (element instanceof ControlSystemItem) {
//                    return true;
//
//                }
//                return false;
//            }
//
//            public Object getValue(Object element, String property) {
//                System.out.println("getValue: "+element+" -- "+property);
//                if (element instanceof ControlSystemItem) {
//                    ControlSystemItem new_name = (ControlSystemItem) element;
//                    return new_name.getPath();
//
//                }
//                return null;
//            }
//
//            public void modify(Object element, String property, Object value) {
//                System.out.println("getValue: "+element+" -- "+property+" --- "+value);
//                if (element instanceof ControlSystemItem) {
//                    ControlSystemItem new_name = (ControlSystemItem) element;
//                }
//
//            }
//
//        });
        /*
         * Ende
         * Vorbereitung für die Nutzung der ersten Zeile als Filter Feld.
         */

        int i=0;
        for (final SearchResult result : _ldapSearchResult.getAnswerSet()) {
            final String row = result.getName();
            final String[] elements = row.split(","); //$NON-NLS-1$
            String path =""; //$NON-NLS-1$
            for(int j=0;j<elements.length;j++){
                if((i==0)&&(j>=_resultTableView.getTable().getColumnCount())){
//                  lastSort = new int[elements.length-1];
                    final TableColumn tc = new TableColumn(_resultTableView.getTable(),SWT.NONE);
                    tc.setResizable(true);
                    tc.setWidth(_resultTableView.getTable().getSize().x/4-4); // TODO: 4 replace whit true columsize
                    tc.setToolTipText(Messages.MainView_ToolTip_Sort);
                    tc.setMoveable(true);
                    final int spalte = j;
                    tc.addSelectionListener(new SelectionListener(){
                        boolean backward = true;
                        public void widgetDefaultSelected(final SelectionEvent e) {}
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
                }else if((i==0)&&(j==0)){
                    String tmp = _resultTableView.getTable().getColumn(j).getText();
                    tmp = tmp.substring(0,tmp.lastIndexOf("(")+1)+_ldapSearchResult.getAnswerSet().size()+")"; //$NON-NLS-1$ //$NON-NLS-2$
                    _resultTableView.getTable().getColumn(j).setText(tmp);
                }
                path +=","+elements[j];
            }
            // TODO: hier Stecken irgend wo die Infos um die Table head aus dem LDAP-Tree zu bekommen.
//            System.out.println("Path: "+path);
            if((elements.length==1)&&(elements[0].split("=").length==1)){
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
            _searchText.setText(WILDCARD); //$NON-NLS-1$
            _searchText.setToolTipText(Messages.MainView_ToolTip);

            //   Eclipse
            final int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
            final DropTarget target = new DropTarget(_searchText, operations);

            // Receive data in Text or File format
            final TextTransfer textTransfer = TextTransfer.getInstance();
            final Transfer[] types = new Transfer[] {textTransfer};
            target.setTransfer(types);

            target.addDropListener(new DropTargetListener() {
              public void dragEnter(final DropTargetEvent event) {
                 if (event.detail == DND.DROP_DEFAULT) {
                     if ((event.operations & DND.DROP_COPY) != 0) {
                         event.detail = DND.DROP_COPY;
                     } else {
                         event.detail = DND.DROP_NONE;
                     }
                 }
               }
               public void dragOver(final DropTargetEvent event) {}
               public void dragOperationChanged(final DropTargetEvent event) {
                    if (event.detail == DND.DROP_DEFAULT) {
                        if ((event.operations & DND.DROP_COPY) != 0) {
                            event.detail = DND.DROP_COPY;
                        } else {
                            event.detail = DND.DROP_NONE;
                        }
                    }
                }
                public void dragLeave(final DropTargetEvent event) {      }
                public void dropAccept(final DropTargetEvent event) {     }
                public void drop(final DropTargetEvent event) {
                    if (textTransfer.isSupportedType(event.currentDataType)) {
                       _searchText.insert((String)event.data);
                    }
                }
            });

        return _searchText;
    }

    public void update(final Observable arg0, final Object arg1) {
        _disp.syncExec(new Runnable() {
            public void run() {
                getText();
            }
        });
    }
}
