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

import org.csstudio.platform.model.IControlSystemItem;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDragSource;
import org.csstudio.utility.nameSpaceSearch.Activator;
import org.csstudio.utility.nameSpaceSearch.Messages;
import org.csstudio.utility.nameSpaceSearch.preference.PreferenceConstants;
import org.csstudio.utility.ldap.reader.ErgebnisListe;
import org.csstudio.utility.ldap.reader.LDAPReader;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
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
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
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

public class MainView extends ViewPart implements Observer{
    public static final String ID = MainView.class.getName();
    private Text searchText;
    private TableViewer ergebnissTableView;
    private boolean lastSortBackward;
    private int[] sorts = {0,0,0};
    private Image up;
    private Image up_old;
    private Image down;
    private Image down_old;
    private HashMap<String, String> headline = new HashMap<String, String>();
//  private Image world;
    private Image work_disable;
    private Label workIcon;
    private LDAPReader ldapr;
    private Display disp;
    private ErgebnisListe ergebnisListe;
    private Button searchButton;
    
    private String WILDCARD  = "*"; //$NON-NLS-1$

    class myTableLabelProvider implements ITableLabelProvider{
        // No Image
        public Image getColumnImage(Object element, int columnIndex) {return null;}

        public String getColumnText(Object element, int columnIndex) {
            if (element instanceof ProcessVariable) {
                ProcessVariable pv = (ProcessVariable) element;
                try{
                    return pv.getPath()[columnIndex].split("=")[1]; //$NON-NLS-1$
                }catch (ArrayIndexOutOfBoundsException e) {
                    return "";
                }

            }
            if (element instanceof ArrayList) {
                Object o = ((ArrayList)element).get(columnIndex);
                if (o instanceof IControlSystemItem) {
                    return ((IProcessVariable)o).getName();
                }
                return "AL: "+o.toString(); //$NON-NLS-1$
            }
            return "toStr: "+element.toString(); //$NON-NLS-1$

        }

        public void addListener(ILabelProviderListener listener) {}

        public void dispose() {}

        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        public void removeListener(ILabelProviderListener listener) {}

    }

    class myContentProvider implements IStructuredContentProvider{

        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof ArrayList) {
                return ((ArrayList)inputElement).toArray();
            }
            return (Object[])inputElement;
        }

        public void dispose() {     }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {     }

    }

    /***************************************************************************
     *
     * Make the Plugin UI
     * - A Textfield for the Searchword [searchText]
     * - A Pushbutton to start search [serachButton]
     * - A resulttable to view the resulte [ergebnissTable]
     *   - Header as Button to Sort the table
     *   - D&D/MB3 function on a row
     *
     ***************************************************************************/

    public MainView() {
        ergebnisListe = new ErgebnisListe();
        ergebnisListe.addObserver(this);
    }

    @Override
    public void createPartControl(Composite parent){
        disp = parent.getDisplay();
        parent.setLayout(new GridLayout(3,false));
        up = Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/up.gif").createImage(); //$NON-NLS-1$
        down = Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/down.gif").createImage(); //$NON-NLS-1$
        up_old = Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/up_old.gif").createImage(); //$NON-NLS-1$
        down_old = Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/down_old.gif").createImage(); //$NON-NLS-1$
        work_disable = Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/LDAPLupe.gif").createImage(); //$NON-NLS-1$

        searchText = makeSearchField(parent);

        // make Search Button
        searchButton = new Button(parent,SWT.PUSH);
        searchButton.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false,1,1));
        searchButton.setFont(new Font(parent.getDisplay(),Messages.MainView_SearchButtonFont,10,SWT.NONE));
        searchButton.setText(Messages.MainView_searchButton); //$NON-NLS-1$

        // make Serach Activity Icon
        workIcon = new Label(parent,SWT.NONE);
        workIcon.setImage(work_disable);
        workIcon.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false,1,1));
        workIcon.setEnabled(false);
//      world.getImageData().

        // make ErgebnisTable
        ergebnissTableView = new TableViewer(parent,SWT.MULTI|SWT.FULL_SELECTION);
        Table ergebnissTable = ergebnissTableView.getTable();
        ergebnissTable.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,3,1));
        ergebnissTable.setLinesVisible (true);
        ergebnissTable.setHeaderVisible (true);

        ergebnissTableView.setContentProvider(new myContentProvider());
        ergebnissTableView.setLabelProvider(new myTableLabelProvider());


        // add Listeners
        searchButton.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                workIcon.setEnabled(true);
                searchButton.setEnabled(false);
//              workIcon.setImage(world);
                search(searchText.getText());
            }
            public void widgetDefaultSelected(SelectionEvent e) {}

        });

        searchText.addKeyListener(new KeyListener() {

            public void keyReleased(KeyEvent e) {
                if(e.keyCode==SWT.CR){
                    workIcon.setEnabled(true);
                    searchButton.setEnabled(false);
//                  workIcon.setImage(world);
                    search(searchText.getText());
                }
            }

            public void keyPressed(KeyEvent e) {}

        });

        // Make Table row Drageble
        new ProcessVariableDragSource(ergebnissTableView.getControl(), ergebnissTableView);
        // MB3
        makeContextMenu();
        searchText.forceFocus();
        parent.update();
        parent.layout();
    }

        @Override
    public void setFocus() {
        searchText.forceFocus();
    }

    public void startSearch(String search){
        searchText.setText(search);
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
        ergebnissTableView.getTable().removeAll();
        ergebnissTableView.getTable().clearAll();
        ergebnissTableView.refresh();
        // ersetzt mehrfach vorkommende '*' durch einen. Da die LDAP abfrage damit nicht zurecht kommt.
        search = search.replaceAll("\\*\\**", WILDCARD); //$NON-NLS-1$ //$NON-NLS-2$
        String filter = Activator.getDefault().getPluginPreferences().getString(PreferenceConstants.P_STRING_RECORD_ATTRIEBUT)+
        "="+search; //$NON-NLS-1$
        if(search.compareTo(WILDCARD)!=0) //$NON-NLS-1$
            filter = filter.concat(WILDCARD); //$NON-NLS-1$

        if(headline.isEmpty()){
            headline.put("efan", Messages.MainView_facility); //$NON-NLS-1$ //$NON-NLS-2$
            headline.put("ecom", Messages.MainView_ecom); //$NON-NLS-1$ //$NON-NLS-2$
            headline.put("econ", Messages.MainView_Controller); //$NON-NLS-1$ //$NON-NLS-2$
            headline.put("eren", Messages.MainView_Record); //$NON-NLS-1$ //$NON-NLS-2$
        }
        ldapr = new LDAPReader(Activator.getDefault().getPluginPreferences().getString(PreferenceConstants.P_STRING_SEARCH_ROOT),
                filter, ergebnisListe);
        ldapr.addJobChangeListener(new JobChangeAdapter() {
            public void done(IJobChangeEvent event) {
            if (event.getResult().isOK())
                MainView.this.ergebnisListe.notifyView();
            }
         });
        ldapr.schedule();
        ergebnissTableView.getTable().layout();
    }

    private void getText() {
//      System.out.println("Thread test Start");
        ergebnissTableView.refresh(false);
        ArrayList<IControlSystemItem> tableElements = new ArrayList<IControlSystemItem>();
        ArrayList<String> list = new ArrayList<String>();
        list.addAll(ergebnisListe.getAnswer());
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
        
        if(list.size()<1){
            searchButton.setEnabled(true);
            workIcon.setEnabled(false);
            return;
        }
        int i=0;
        boolean first = true;
        for (String row : list) {
            System.out.println(list.size()+": "+row);
            String[] elements = row.split(","); //$NON-NLS-1$
            String path =""; //$NON-NLS-1$
            for(int j=0;j<elements.length;j++){
                if(i==0&&j>=ergebnissTableView.getTable().getColumnCount()){
//                  lastSort = new int[elements.length-1];
                    final TableColumn tc = new TableColumn(ergebnissTableView.getTable(),SWT.NONE);
                    tc.setResizable(true);
                    tc.setWidth(ergebnissTableView.getTable().getSize().x/4-4); // TODO: 4 replace whit true columsize
                    tc.setToolTipText(Messages.MainView_ToolTip_Sort);
                    tc.setMoveable(true);
                    final int spalte = j;
                    tc.addSelectionListener(new SelectionListener(){
                        boolean backward = true;
                        public void widgetDefaultSelected(SelectionEvent e) {}
                        public void widgetSelected(SelectionEvent e) {
                                backward=!backward;
                                tc.setAlignment(SWT.LEFT);
                                if(sorts[0]!=spalte){
                                    TableColumn[] chil = tc.getParent().getColumns();
                                    chil[sorts[1]].setImage(null);
                                    sorts[1]=sorts[0];
                                    lastSortBackward=backward;
                                    if(lastSortBackward)
                                        chil[sorts[1]].setImage(down_old);
                                    else
                                        chil[sorts[1]].setImage(up_old);
                                }
                                sorts[0]=spalte;
                                ergebnissTableView.setSorter(new TableSorter(sorts[0],backward,sorts[1], lastSortBackward));
                                if(backward)
                                    tc.setImage(down);
                                else
                                    tc.setImage(up);
                                lastSortBackward=backward;
                        }
                    });
                    String temp;
                    if((temp=headline.get(elements[j].split("=")[0]))==null) //$NON-NLS-1$
                         temp = elements[j].split("=")[0]; //$NON-NLS-1$
                    if(j==0){
                        temp=temp.concat(" ("+list.size()+")"); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    tc.setText(temp);
                }else if(i==0&&j==0){
                    String tmp = ergebnissTableView.getTable().getColumn(j).getText();
                    tmp = tmp.substring(0,tmp.lastIndexOf("(")+1)+list.size()+")"; //$NON-NLS-1$ //$NON-NLS-2$
                    ergebnissTableView.getTable().getColumn(j).setText(tmp);
                }
                path +=","+elements[j];
            }
            // TODO: hier Stecken irgend wo die Infos um die Table head aus dem LDAP-Tree zu bekommen.
            System.out.println("Path: "+path);
            tableElements.add(new ProcessVariable(elements[0].split("=")[1],elements)); //$NON-NLS-1$
            i++;
        }
//      System.out.println("Thread test 2");
        ergebnissTableView.setContentProvider(new myContentProvider());
        ergebnissTableView.setLabelProvider(new myTableLabelProvider());
        ergebnissTableView.setInput(tableElements);
        ergebnissTableView.refresh(true);
//      workIcon.setImage(work_disable);
        searchButton.setEnabled(true);
        workIcon.setEnabled(false);
//      parent.layout();
//      System.out.println("Thread test ende");
    }

    /*****************************************************************************
     * Make the MB3-ContextMenu
     *
     */
    private void makeContextMenu() {
        MenuManager manager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        Control contr = ergebnissTableView.getControl();
        manager.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
            }
        });
        contr.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                super.mouseDown(e);
                if (e.button == 3) {
                    StructuredSelection s =  (StructuredSelection) ergebnissTableView.getSelection();
                    Object o = s.getFirstElement();
                    if (o instanceof ArrayList) {
                        System.out.println("First is"+((ArrayList)o).get(0));

                    }
                }
            }
        });
        Menu menu = manager.createContextMenu(contr);
        contr.setMenu(menu);
        getSite().registerContextMenu(manager, ergebnissTableView);
    }
    /***
     *
     * - Make the searchtext
     *   - Layout
     *   - Dropsource
     *
     * @return
     */
    private Text makeSearchField(Composite parent) {
            searchText = new Text(parent,SWT.BORDER|SWT.SINGLE);
            searchText.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false,1,1));
            searchText.setText(WILDCARD); //$NON-NLS-1$
            searchText.setToolTipText(Messages.MainView_ToolTip);

            //   Eclipse
            int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
            DropTarget target = new DropTarget(searchText, operations);

            // Receive data in Text or File format
            final TextTransfer textTransfer = TextTransfer.getInstance();
            Transfer[] types = new Transfer[] {textTransfer};
            target.setTransfer(types);

            target.addDropListener(new DropTargetListener() {
              public void dragEnter(DropTargetEvent event) {
                 if (event.detail == DND.DROP_DEFAULT) {
                     if ((event.operations & DND.DROP_COPY) != 0) {
                         event.detail = DND.DROP_COPY;
                     } else {
                         event.detail = DND.DROP_NONE;
                     }
                 }
               }
               public void dragOver(DropTargetEvent event) {}
               public void dragOperationChanged(DropTargetEvent event) {
                    if (event.detail == DND.DROP_DEFAULT) {
                        if ((event.operations & DND.DROP_COPY) != 0) {
                            event.detail = DND.DROP_COPY;
                        } else {
                            event.detail = DND.DROP_NONE;
                        }
                    }
                }
                public void dragLeave(DropTargetEvent event) {      }
                public void dropAccept(DropTargetEvent event) {     }
                public void drop(DropTargetEvent event) {
                    if (textTransfer.isSupportedType(event.currentDataType)) {
                       searchText.insert((String)event.data);
                    }
                }
            });

        return searchText;
    }

    public void update(Observable arg0, Object arg1) {
        disp.syncExec(new Runnable() {
            public void run() {
                getText();
//              answer.setText(text);
//              answer.getParent().layout();
            }
        });
    }
}
