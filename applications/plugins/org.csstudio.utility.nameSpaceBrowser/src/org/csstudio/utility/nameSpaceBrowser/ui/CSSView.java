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
package org.csstudio.utility.nameSpaceBrowser.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.csstudio.platform.model.IControlSystemItem;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableDragSource;
import org.csstudio.utility.nameSpaceBrowser.Messages;
import org.csstudio.utility.nameSpaceBrowser.utility.Automat;
import org.csstudio.utility.nameSpaceBrowser.utility.CSSViewParameter;
import org.csstudio.utility.nameSpaceBrowser.utility.NameSpace;
import org.csstudio.utility.nameSpaceBrowser.utility.Automat.Ereignis;
import org.csstudio.utility.nameSpaceBrowser.utility.Automat.Zustand;
import org.csstudio.utility.namespace.utility.ControlSystemItem;
import org.csstudio.utility.namespace.utility.NameSpaceResultList;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;

/***
 * 
 * @author Helge Rickens
 * 
 *         CSSView is a NameSpaceBrowser View-Element. Composed of - A Group with name of "Type" - A
 *         Filter: Textfield [filter] - A List of Elements - is the Element a IProcessVariable
 */
public class CSSView extends Composite implements Observer {
    
    class CSSLabelProvider implements ILabelProvider {
        
        /**
         * (@inheritDoc)
         */
        @Override
        public Image getImage(final Object element) {
            return null;
        }
        
        /**
         * (@inheritDoc)
         */
        @Override
        public String getText(final Object element) {
            if (element instanceof IControlSystemItem) {
                final String[] name = ((IControlSystemItem) element).getName().split("[/ ]");
                return name[name.length - 1];
            } else {
                return element.toString();
            }
        }
        /**
         * (@inheritDoc)
         */
        @Override
        public boolean isLabelProperty(final Object element, final String property) {
            return false;
        }
        
        /**
         * (@inheritDoc)
         */
        @Override
        public void addListener(final ILabelProviderListener listener) {
            // Empty
        }
        
        /**
         * (@inheritDoc)
         */
        @Override
        public void dispose() {
            // Empty
        }
        
        /**
         * (@inheritDoc)
         */
        @Override
        public void removeListener(final ILabelProviderListener listener) {
            // Empty
        }
    }
    
    
    private static enum RegExpParam {
        JAVA_REG_EXP,
        JAVA_REG_EXP_NO_CASE,
        SIMPLE_WIN;
    }
    
    
    // UI elements
    private final Display _display;
    private final Composite _parent;
    private Group _group;
    private Text _filter;
    
    private NameSpaceResultList _resultList;
    
    private TableViewer _tableViewer;
    
    private boolean _hasChild = false;
    private boolean _haveFixFirst = true;
    private CSSView _child;
    private final Automat _automat;
    private final NameSpace _nameSpace;
    private LinkedHashMap<String, ControlSystemItem> _itemList;
    
    private int _start = -1;
    private final IWorkbenchPartSite _site;
    
    private String _defaultPVFilter;
    private CSSViewParameter _cssParameter;
    private final String[] _headlines;
    private final int _level;
    
    private String _fixFirst;
    
    public CSSView(final Composite parent,
                   final Automat automat,
                   final NameSpace nameSpace,
                   final IWorkbenchPartSite site,
                   final String defaultFilter,
                   final String selection,
                   final String[] headlines,
                   final int level,
                   final NameSpaceResultList resultList) {
        this(parent, automat, nameSpace, site, defaultFilter, headlines, level, resultList);
        
        // Make a Textfield to Filter the list. Can text drop
        makeFilterField();
        makeListField(selection);
    }
    
    public CSSView(final Composite parent,
                   final Automat automat,
                   final NameSpace nameSpace,
                   final IWorkbenchPartSite site,
                   final String defaultFilter,
                   final String selection,
                   final String[] headlines,
                   final int level,
                   final NameSpaceResultList resultList,
                   final String fixFrist) {
        this(parent, automat, nameSpace, site, defaultFilter, headlines, level, resultList);
        
        _haveFixFirst = true;
        _fixFirst = fixFrist;
        
        // Make a Textfield to Filter the list. Can text drop
        makeFilterField();
        makeListField(selection);
    }
    
    private CSSView(final Composite parent,
                    final Automat automat,
                    final NameSpace nameSpace,
                    final IWorkbenchPartSite site,
                    final String defaultFilter,
                    final String[] headlines,
                    final int level,
                    final NameSpaceResultList resultList) {
        super(parent, SWT.NONE);
        _display = parent.getDisplay();
        
        this._automat = automat;
        this._nameSpace = nameSpace;
        this._parent = parent;
        this._site = site;
        this._headlines = headlines;
        this._level = level;
        _defaultPVFilter = defaultFilter;
        
        init();
        
        setObservable(resultList); // FIXME (bknerr) : Antipattern - herein 'this' pointer is used although object is not completely constructed
    }
    
    /**
     *  dispose self and Children
     */
    @Override
    public void dispose() {
        if (_hasChild) {
            _hasChild = false;
            _child.dispose();
            while (!_child.isDisposed()) {
                // Empty // TODO (bknerr) :
            }
        }
        
        ((GridLayout) _parent.getLayout()).numColumns--;
        super.dispose();
        _parent.layout(false);
        
    }
    
    /**
     * @param list
     *            Fill the list with ProcessVariable or ControlSystemItem
     */
    private void fillItemList(final List<ControlSystemItem> list) {
        if (list == null) {
            return;
        }
        
        _itemList = new LinkedHashMap<String, ControlSystemItem>();
        if (_cssParameter.newCSSView && _haveFixFirst) {
            if (_fixFirst == null) {
                _start = 0;
                _itemList.put(Messages.getString("CSSView.All"),
                              new ControlSystemItem(Messages.getString("CSSView.All"), _cssParameter.filter + "ALL,")); //$NON-NLS-1$ //$NON-NLS-2$
            } else if (_fixFirst.trim().length() > 0) {
                _itemList.put(_fixFirst, new ControlSystemItem(_fixFirst, _cssParameter.filter + _fixFirst)); //$NON-NLS-1$ //$NON-NLS-2$
            }
            
        } else {
            _filter.setText(_defaultPVFilter);
        }
        
        for (final ControlSystemItem row : list) {
            _itemList.put(row.getName(), row);
        }
        
        if (!list.isEmpty()) {
            _group.setText(_headlines[_level]);
        }
    }
    
    public Group getGroup() {
        return _group;
    }
    
    //    public org.eclipse.swt.widgets.List getList() {
    //        return listViewer.getTable();
    //    }
    public org.eclipse.swt.widgets.Table getList() {
        return _tableViewer.getTable();
    }
    
    /**
     * @param selection
     * @return CSSViewParameter
     */
    private CSSViewParameter getParameter(final String selection) {
        return _automat.goDown(Ereignis.DOWN, selection);
    }
    
    private void init() {
        // set Layout
        this.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));
        this.setLayout(new FillLayout());
        _group = new Group(this, SWT.LINE_SOLID);
        _group.setLayout(new GridLayout(1, false));
        
    }
    
    /**
     * Creates the new child CSSView.
     * @param viewParams
     */
    protected void makeChild(final CSSViewParameter viewParams) {
        _parent.setRedraw(false);
        // Has a child, destroy it.
        if (_hasChild) {
            _child.dispose();
            while (!_child.isDisposed()) {
                ;
            }
        }
        // make new child
        
        ((GridLayout) _parent.getLayout()).numColumns++;
        // The first element is the "All" element
        if ((_tableViewer.getTable().getSelectionIndex() > _start) || (_fixFirst != null)) {
            
            final String selectionString = _tableViewer.getSelection().toString();
            final String stringWithoutBrackets = selectionString.substring(1, selectionString.length() - 1);
            final ControlSystemItem csi = _itemList.get(stringWithoutBrackets);
            
            if (_fixFirst == null) {
                _child =
                    new CSSView(_parent, _automat, _nameSpace, _site, _defaultPVFilter, csi.getPath(), _headlines, _level + 1, _resultList.getNew()); //$NON-NLS-1$
            } else {
                _child =
                    new CSSView(_parent, _automat, _nameSpace, _site, _defaultPVFilter, csi.getPath(), _headlines, _level + 1, _resultList.getNew(), _fixFirst); //$NON-NLS-1$
            }
        } else {
            
            final Collection<ControlSystemItem> values = _itemList.values();
            final ControlSystemItem[] array = values.toArray(new ControlSystemItem[values.size()]);
            final ControlSystemItem item = array[0];
            final String path = item.getPath();
            final String[] fields = path.split("=");
            final String df = fields[0] + "=*,";
            
            _child = new CSSView(_parent, _automat, _nameSpace, _site, _defaultPVFilter, df, _headlines, _level + 1, _resultList.getNew()); //$NON-NLS-1$
        }
        _hasChild = true;
        
        _parent.setRedraw(true);
    }
    
    // Make the MB3 ContextMenu
    private void makeContextMenu() {
        final MenuManager manager = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        final Control contr = _tableViewer.getControl();
        manager.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(final IMenuManager manager) {
                manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
            }
        });
        // contr.addMouseListener(new MouseAdapter() {
        // @Override
        // public void mouseDown(MouseEvent e) {
        // super.mouseDown(e);
        // if (e.button == 3) {
        // list.getSelection();
        // //
        // list.getList().setSelection(e.y/list.getList().getItemHeight()+list.getList().getVerticalBar().getSelection());
        // }
        // }
        // });
        final Menu menu = manager.createContextMenu(contr);
        contr.setMenu(menu);
        _site.registerContextMenu(manager, _tableViewer);
    }
    
    private void makeFilterField() {
        _filter = new Text(_group, SWT.SINGLE | SWT.BORDER);
        _filter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        _filter.setToolTipText(Messages.getString("CSSView_ToolTip1"));
        // Eclipse
        final int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
        final DropTarget target = new DropTarget(_filter, operations);
        
        // Receive data in Text or File format
        final TextTransfer textTransfer = TextTransfer.getInstance();
        target.setTransfer(new Transfer[] { textTransfer });
        
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
            
            public void dragLeave(final DropTargetEvent event) {
            }
            
            public void dragOperationChanged(final DropTargetEvent event) {
                if (event.detail == DND.DROP_DEFAULT) {
                    if ((event.operations & DND.DROP_COPY) != 0) {
                        event.detail = DND.DROP_COPY;
                    } else {
                        event.detail = DND.DROP_NONE;
                    }
                }
            }
            
            public void dragOver(final DropTargetEvent event) {
            }
            
            public void drop(final DropTargetEvent event) {
                if (textTransfer.isSupportedType(event.currentDataType)) {
                    _filter.insert((String) event.data);
                }
            }
            
            public void dropAccept(final DropTargetEvent event) {
            }
            
        });
        
        _filter.addKeyListener(new KeyListener() {
            public void keyPressed(final KeyEvent e) {
            }
            
            public void keyReleased(final KeyEvent e) {
                if (e.keyCode == SWT.CR) {
                    _tableViewer.setInput(new ArrayList<Object>(_itemList.values()).toArray());
                } else if (e.keyCode == SWT.F1) {
                    PlatformUI.getWorkbench().getHelpSystem().displayDynamicHelp();
                }
            }
            
        });
        
    }
    
    /**
     * @param selection
     * 
     */
    private void makeListField(final String selection) {
        _cssParameter = getParameter(selection);
        
        final Zustand zu = _automat.getZustand();
        if (zu == Zustand.RECORD) {
            //            listViewer = new ListViewer(group, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
            _tableViewer = new TableViewer(_group, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
        } else {
            //            listViewer = new ListViewer(group, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
            _tableViewer = new TableViewer(_group, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
        }
        _tableViewer.getControl().getStyle();
        //        listViewer.getList().setSize(getClientArea().x - 100, getClientArea().y - 100);
        _tableViewer.getTable().setSize(getClientArea().x - 100, getClientArea().y - 100);
        //        listViewer.getList().addPaintListener(new PaintListener() {
        _tableViewer.getTable().addPaintListener(new PaintListener() {
            public void paintControl(final PaintEvent e) {
                //                listViewer.getList().setSize(getSize().x - 16,
                _tableViewer.getTable().setSize(getSize().x - 16,
                                                getSize().y - (_filter.getSize().y + 31));
            }
        });
        _tableViewer.setLabelProvider(new CSSLabelProvider());
        _tableViewer.setContentProvider(new ArrayContentProvider());
        _tableViewer.setSorter(new ViewerSorter());
        //        listViewer.getList().setToolTipText(Messages.getString("CSSView_ToolTip2"));
        _tableViewer.getTable().setToolTipText(Messages.getString("CSSView_ToolTip2"));
        
        _nameSpace.setName(_cssParameter.name);
        _nameSpace.setFilter(_cssParameter.filter);
        _fixFirst = _cssParameter.fixFirst;
        _nameSpace.setErgebnisListe(_resultList);
        _nameSpace.setSelection(selection);
        _nameSpace.start();
        
        //        listViewer.getList().addKeyListener(new KeyListener() {
        _tableViewer.getTable().addKeyListener(new KeyListener() {
            public void keyPressed(final KeyEvent e) {
            }
            
            public void keyReleased(final KeyEvent e) {
                if (e.keyCode == SWT.F1) {
                    PlatformUI.getWorkbench().getHelpSystem().displayDynamicHelp();
                }
            }
        });
    }
    
    /**
     * @param serach1
     *            this String a not modified (by no Casesensitivity toLowerCase)
     * @param regExp
     *            this String a modified
     * @return true match search and regExp and false don´t match
     */
    protected boolean searchString(final String search1, final String regExp) {
        return searchString(search1, regExp, RegExpParam.SIMPLE_WIN);
    }
    
    /**
     * @param serach1
     *            this String a not modified (by no Casesensitivity toLowerCase)
     * @param regExp
     *            this String a modified by searchType
     * @param searchTyp
     *            :
     * 
     *            <pre>
     * SIMPLE_WIN: Search used only * and ? as Wildcard
     * JAVA_REG_EXP: used String match
     * JAVA_REG_EXP_NO_CASE: used String match after toLowerCase
     * </pre>
     * @return true match search and regExp and false don´t match
     */
    
    protected boolean searchString(final String search1, final String regExp, final RegExpParam searchTyp) { // FIXME (bknerr) : encapsulate somewhere else !
        switch (searchTyp) {
            case JAVA_REG_EXP:
                return search1.matches(regExp);
            case JAVA_REG_EXP_NO_CASE:
                return search1.toLowerCase().matches(regExp.toLowerCase());
            case SIMPLE_WIN:
            default:
                return search1.toLowerCase().matches(
                                                     regExp.replace("$", "\\$").replace(".", "\\.").replace("*", ".*").replace(
                                                                                                                               "?", ".?").toLowerCase()
                                                                                                                               + ".*");
        }
        
    }
    
    // Setzt den Defaultfilter für IProzessVariablen
    public void setDefaultFilter(final String defaultPVFilter) {
        this._defaultPVFilter = defaultPVFilter;
        if (!_cssParameter.newCSSView) {
            _filter.setText(defaultPVFilter);
        }
        if (_hasChild) {
            _child.setDefaultFilter(defaultPVFilter);
        }
    }
    
    public void update(final Observable arg0, final Object arg1) {
        _display.syncExec(new Runnable() {
            public void run() {
                fillItemList(_resultList.getCSIResultList());
                workItemList();
            }
        });
    }
    
    private void workItemList() {
        // fill the List
        if ((_itemList != null) && (_itemList.size() > 1)) {
            _tableViewer.setInput(new ArrayList<ControlSystemItem>(_itemList.values()));
        }
        
        _tableViewer.addFilter(new ViewerFilter() {
            @Override
            public Object[] filter(final Viewer viewer, final Object parent, final Object[] elements) {
                
                final String search = _filter.getText().trim();
                final ArrayList<Object> al = new ArrayList<Object>();
                for (final Object element : elements) {
                    String name = ""; //$NON-NLS-1$
                    if (element instanceof IControlSystemItem) {
                        final String[] names = ((IControlSystemItem) element).getName().split("[/ ]");
                        name = names[names.length - 1];
                        // name= ((IControlSystemItem) element).getName();
                    }
                    if ((search.length() == 0) || searchString(name, search)) {
                        al.add(element);
                    }
                }
                return al.toArray();
            }
            
            @Override
            public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
                return true;
            }
        });
        
        final Zustand zu = _automat.getZustand();
        if (zu != Zustand.RECORD) {
            _tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
                public void selectionChanged(final SelectionChangedEvent event) {
                    // TODO: Checken ob dadurch vermieden werden kann das ein Elemente zu häufig
                    // angeklickt werden kann.
                    if (_cssParameter.newCSSView) {
                        _parent.setEnabled(false);
                        makeChild(_cssParameter);
                        _parent.setEnabled(true);
                    }
                }
            });
        }
        
        _parent.layout();
        _parent.pack();
        
        // Make List Drageble
        //        new ProcessVariableDragSource(listViewer.getControl(), listViewer);
        //        new ProcessVariableDragSource(listViewer.getList(), listViewer);
        new ProcessVariableDragSource(_tableViewer.getTable(), _tableViewer);
        // MB3
        makeContextMenu();
        
    }
    
    /**
     * Sets the observable entity (resultList) in this observer (CSSView)
     * and adds <code>this</code> object as observer to its list.
     * 
     * @param resultList the observable object for <code>this</code> observer
     */
    public void setObservable(final NameSpaceResultList resultList) {
        _resultList = resultList;
        _resultList.addObserver(this);
    }
}
