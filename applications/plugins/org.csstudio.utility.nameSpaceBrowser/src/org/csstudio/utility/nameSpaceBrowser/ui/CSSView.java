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

import org.csstudio.apputil.ui.dialog.ErrorDetailDialog;
import org.csstudio.ui.util.dnd.ControlSystemDragSource;
import org.csstudio.utility.nameSpaceBrowser.Messages;
import org.csstudio.utility.nameSpaceBrowser.utility.Automat;
import org.csstudio.utility.nameSpaceBrowser.utility.Automat.NameSpaceBrowserState;
import org.csstudio.utility.nameSpaceBrowser.utility.CSSViewParameter;
import org.csstudio.utility.nameSpaceBrowser.utility.NameSpace;
import org.csstudio.utility.namespace.utility.ControlSystemItem;
import org.csstudio.utility.namespace.utility.NameSpaceSearchResult;
import org.csstudio.utility.namespace.utility.ProcessVariableItem;
import org.csstudio.csdata.ProcessVariable;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
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

    class CSSLabelProvider implements ILabelProvider, IFontProvider{

        
        private final Font _font;
        private final Font _boldFont;


        /**
         * Constructor.
         * @param font 
         */
        public CSSLabelProvider(Font font) {
            FontData fontData = font.getFontData()[0];
            _font = new Font(null, fontData);
            _boldFont = new Font(null, new FontData(fontData.getName(), fontData.getHeight(), SWT.BOLD));
        }
        
        
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
            if (element instanceof ProcessVariable) {
                final String[] name = ((ProcessVariable) element).getName().split("[/ ]");
                return name[name.length - 1];
            }
            return element.toString();
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
        public void removeListener(final ILabelProviderListener listener) {
            // Empty
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Font getFont(Object element) {
            Font font = _font;
            if(element instanceof ControlSystemItem) {
                if(((ControlSystemItem) element).isRedundant()) {
                    font = _boldFont;
                }
            }
            return font;
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public void dispose() {
            _font.dispose();
            _boldFont.dispose();
        }
    }


    // UI elements
    private final Display _display;
    private final Composite _parent;
    private Group _group;
    private Text _filter;

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
                   final int level) throws Exception {
        this(parent, automat, nameSpace, site, defaultFilter, selection, headlines, level, null);
    }

    public CSSView(final Composite parent,
                   final Automat automat,
                   final NameSpace nameSpace,
                   final IWorkbenchPartSite site,
                   final String defaultFilter,
                   final String selection,
                   final String[] headlines,
                   final int level,
                   final String fixFrist) throws Exception {
        super(parent, SWT.NONE);
        _display = parent.getDisplay();

        _automat = automat;
        _nameSpace = nameSpace;
        _parent = parent;
        _site = site;
        _headlines = headlines;
        _level = level;
        _defaultPVFilter = defaultFilter;

        init();

        NameSpaceSearchResult searchResult = nameSpace.getSearchResult();
        // FIXME (bknerr) : Antipattern - here, the 'this' pointer is used although object is not completely constructed
        searchResult.addObserver(this);
        _fixFirst = fixFrist;
        _haveFixFirst = _fixFirst!=null;

        // Make a Textfield to Filter the list. Can text drop
        makeFilterField();
        makeListField(selection);
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
                // Empty
            }
        }
        _nameSpace.stop();
        
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
        return _automat.goDown(selection);
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
     * @throws Exception 
     */
    protected void makeChild(final CSSViewParameter viewParams) throws Exception {
        _parent.setRedraw(false);
        // Has a child, destroy it.
        if (_hasChild) {
            _child.dispose();
            while (!_child.isDisposed()) {
             // EMPTY
            }
        }
        // make new child

        ((GridLayout) _parent.getLayout()).numColumns++;

        // The first element is the "All" element
        if ((_tableViewer.getTable().getSelectionIndex() > _start) || (_fixFirst != null)) {

            final String selectionString = _tableViewer.getSelection().toString();
            final String stringWithoutBrackets = selectionString.substring(1, selectionString.length() - 1);
            final String stringWithoutPrefix[] = stringWithoutBrackets.split("\\s+");
            final ControlSystemItem csi;
            if (stringWithoutPrefix.length>1) {
            	final String stringWithoutColon = stringWithoutPrefix[1].substring(1, stringWithoutPrefix[1].length() - 1);
            	System.out.println(stringWithoutColon);
            	csi = _itemList.get(stringWithoutColon);
            } else {
            	csi = new ControlSystemItem("","");
            }
            _child = new CSSView(_parent,
                                 _automat,
                                 _nameSpace.createNew(),
                                 _site,
                                 _defaultPVFilter,
                                 csi.getPath(),
                                 _headlines,
                                 _level + 1,
                                 _fixFirst); //$NON-NLS-1$
        } else {

            final Collection<ControlSystemItem> values = _itemList.values();
            final ControlSystemItem[] array = values.toArray(new ControlSystemItem[values.size()]);
            final ControlSystemItem item = array[0];
            final String path = item.getPath();
            final String[] fields = path.split("=");
            final String df = fields[0] + "=*,";

            _child = new CSSView(_parent, _automat, _nameSpace.createNew(), _site, _defaultPVFilter, df, _headlines, _level + 1); //$NON-NLS-1$
        }
        _hasChild = true;

        _parent.setRedraw(true);
    }

    // Make the MB3 ContextMenu
    private void makeContextMenu() {
        final MenuManager man = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        final Control contr = _tableViewer.getControl();
        man.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(final IMenuManager manager) {
                manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
            }
        });
        final Menu menu = man.createContextMenu(contr);
        contr.setMenu(menu);
        _site.registerContextMenu(man, _tableViewer);
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
            public void dragLeave(final DropTargetEvent event) {
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
            public void dragOver(final DropTargetEvent event) {
             // EMPTY
            }

            @Override
            public void drop(final DropTargetEvent event) {
                if (textTransfer.isSupportedType(event.currentDataType)) {
                    _filter.insert((String) event.data);
                }
            }

            @Override
            public void dropAccept(final DropTargetEvent event) {
             // EMPTY
            }

        });

        _filter.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(final KeyEvent e) {
             // EMPTY
            }

            @Override
            public void keyReleased(final KeyEvent e) {
                if (e.keyCode == SWT.CR) {
                    _tableViewer.setInput(_itemList.values().toArray());
                } else if (e.keyCode == SWT.F1) {
                    PlatformUI.getWorkbench().getHelpSystem().displayDynamicHelp();
                }
            }

        });

    }

    /**
     * @param selection
     * @throws Exception 
     *
     */
    private void makeListField(final String selection) throws Exception {
        _cssParameter = getParameter(selection);

        final NameSpaceBrowserState state = _automat.getState();
        if (state == NameSpaceBrowserState.RECORD) {
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
            @Override
            public void paintControl(final PaintEvent e) {
                //                listViewer.getList().setSize(getSize().x - 16,
                _tableViewer.getTable().setSize(getSize().x - 16,
                                                getSize().y - (_filter.getSize().y + 31));
            }
        });
        Font font = _tableViewer.getTable().getFont();
        _tableViewer.setLabelProvider(new CSSLabelProvider(font));
        _tableViewer.setContentProvider(new ArrayContentProvider());
        _tableViewer.setSorter(new ViewerSorter());
        //        listViewer.getList().setToolTipText(Messages.getString("CSSView_ToolTip2"));
        _tableViewer.getTable().setToolTipText(Messages.getString("CSSView_ToolTip2"));

        _fixFirst = _cssParameter.fixFirst;


        _nameSpace.setName(_cssParameter.name);
        _nameSpace.setFilter(_cssParameter.filter);
        //_nameSpace.setResult(_resultList);
        _nameSpace.setSelection(selection);

        _nameSpace.start();

        //        listViewer.getList().addKeyListener(new KeyListener() {
        _tableViewer.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(final KeyEvent e) {
             // EMPTY
            }

            @Override
            public void keyReleased(final KeyEvent e) {
                if (e.keyCode == SWT.F1) {
                    PlatformUI.getWorkbench().getHelpSystem().displayDynamicHelp();
                }
            }
        });
    }

    /**
     * String matcher for regExp that only contains * and ? as wildcards.
     * Case-insensitive.
     *
     * @param searchString
     *            this String a not modified (by no Casesensitivity toLowerCase)
     * @param simpleRegExp
     *            this String a modified
     * @return true match search and regExp and false don´t match
     */
    protected boolean searchString(final String searchString, final String simpleRegExp) {

        final String regExp = simpleRegExp.replace("$", "\\$")
                                          .replace(".", "\\.")
                                          .replace("*", ".*")
                                          .replace("?", ".?").toLowerCase() +
                                          ".*";
        return searchString.toLowerCase().matches(regExp);
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

    @Override
    public void update(final Observable arg0, final Object arg1) {

        _display.syncExec(new Runnable() {
            @Override
            public void run() {
                fillItemList(_nameSpace.getSearchResult().getCSIResultList());
                workItemList();
            }
        });
    }

    private void workItemList() {
        // fill the List
        if ((_itemList != null) && (_itemList.size() > 0)) {
            _tableViewer.setInput(new ArrayList<ControlSystemItem>(_itemList.values()));
        }

        _tableViewer.addFilter(new ViewerFilter() {
            @Override
            public Object[] filter(final Viewer viewer, final Object parent, final Object[] elements) {

                final String simpleRegExp = _filter.getText().trim();
                final ArrayList<Object> al = new ArrayList<Object>();
                for (final Object element : elements) {
                    String name = ""; //$NON-NLS-1$
                    if (element instanceof ProcessVariable) {
                        final String[] names = ((ProcessVariable) element).getName().split("[/ ]");
                        name = names[names.length - 1];
                        // name= ((IControlSystemItem) element).getName();
                    }
                    if ((simpleRegExp.length() == 0) || searchString(name, simpleRegExp)) {
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

        final NameSpaceBrowserState zu = _automat.getState();
        if (zu != NameSpaceBrowserState.RECORD) {
            _tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(final SelectionChangedEvent event) {
                    // TODO: Checken ob dadurch vermieden werden kann das ein Elemente zu häufig
                    // angeklickt werden kann.
                    if (_cssParameter.newCSSView) {
                        _parent.setEnabled(false);
                        try {
                            makeChild(_cssParameter);
                        } catch (Exception e) {
                            ErrorDetailDialog errorDetailDialog = new ErrorDetailDialog(null, "Titel", e.getLocalizedMessage(), e.toString());
                            errorDetailDialog.open();
                        }
                        _parent.setEnabled(true);
                    }
                }
            });
        }

        _parent.layout();
        _parent.pack();

        new ControlSystemDragSource(_tableViewer.getControl()) {
			
			@Override
			public Object getSelection() {
                final Object[] obj = ((IStructuredSelection)_tableViewer.getSelection()).toArray();
                final ProcessVariable[] pvs = new ProcessVariable[obj.length];
                for (int i=0; i<pvs.length; ++i)
                    pvs[i] = new ProcessVariable(((ProcessVariableItem)obj[i]).getName());
                return pvs;
			}
		};
        
		
        // Make List Drageble
        //        new ProcessVariableDragSource(listViewer.getControl(), listViewer);
        //        new ProcessVariableDragSource(listViewer.getList(), listViewer);
//        new ProcessVariableDragSource(_tableViewer.getTable(), _tableViewer);
        // MB3
        makeContextMenu();

    }
}
