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

import static org.csstudio.config.ioconfig.model.preference.PreferenceConstants.DDB_PASSWORD;
import static org.csstudio.config.ioconfig.model.preference.PreferenceConstants.DDB_USER_NAME;
import static org.csstudio.config.ioconfig.model.preference.PreferenceConstants.DIALECT;
import static org.csstudio.config.ioconfig.model.preference.PreferenceConstants.HIBERNATE_CONNECTION_DRIVER_CLASS;
import static org.csstudio.config.ioconfig.model.preference.PreferenceConstants.HIBERNATE_CONNECTION_URL;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.csstudio.config.ioconfig.config.view.ChannelConfigComposite;
import org.csstudio.config.ioconfig.config.view.FacilityConfigComposite;
import org.csstudio.config.ioconfig.config.view.IocConfigComposite;
import org.csstudio.config.ioconfig.config.view.MasterConfigComposite;
import org.csstudio.config.ioconfig.config.view.ModuleConfigComposite;
import org.csstudio.config.ioconfig.config.view.NodeConfig;
import org.csstudio.config.ioconfig.config.view.SlaveConfigComposite;
import org.csstudio.config.ioconfig.config.view.SubNetConfigComposite;
import org.csstudio.config.ioconfig.config.view.helper.ConfigHelper;
import org.csstudio.config.ioconfig.config.view.helper.InfoConfigComposte;
import org.csstudio.config.ioconfig.config.view.helper.ProfibusHelper;
import org.csstudio.config.ioconfig.model.Activator;
import org.csstudio.config.ioconfig.model.Facility;
import org.csstudio.config.ioconfig.model.FacilityLight;
import org.csstudio.config.ioconfig.model.Ioc;
import org.csstudio.config.ioconfig.model.NamedDBClass;
import org.csstudio.config.ioconfig.model.Node;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.Repository;
import org.csstudio.config.ioconfig.model.pbmodel.Channel;
import org.csstudio.config.ioconfig.model.pbmodel.Master;
import org.csstudio.config.ioconfig.model.pbmodel.Module;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnet;
import org.csstudio.config.ioconfig.model.pbmodel.Slave;
import org.csstudio.config.ioconfig.model.xml.ProfibusConfigXMLGenerator;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;

/**
 * @author hrickens
 * @author $Author$
 * @version $Revision$
 * @since 19.06.2007
 */
public class ProfiBusTreeView extends Composite {

    /**
     * The ID of the View.
     */
    public static final String ID = ProfiBusTreeView.class.getName();
    /**
     * The ProfiBus Tree View.
     */
    private TreeViewer _viewer;

    private DrillDownAdapter _drillDownAdapter;

    private IViewSite _site;

    /**
     * The Parent Composite.
     */
    private Composite _parent;

    /**
     * the Selected Node.
     */
    private StructuredSelection _selectedNode;

    /**
     * A Copy from a Node.
     */
    private List<Node> _copiedNodesReferenceList;

    /**
     * This action open an Empty Node. Type of new node dependent on Parent.
     */
    private IAction _newChildrenNodeAction;
    /**
     * This action open an selected Node. Type of new node dependent on Parent.
     */
    private IAction _editNodeAction;
    /**
     * This action open a new level one empty Node. The type of this node is {@link Facility}.
     */
    private IAction _newFacilityAction;
    /**
     * This action open an selected Node. Type of new node dependent on Parent.
     */
    private IAction _doubleClickAction;
    /**
     * A Action to delete the selected Node.
     */
    private IAction _deletNodeAction;
    /**
     * The action to create the XML config file.
     */
    private IAction _createNewXMLConfigFile;
    /**
     * The action to Copy a Node.
     */
    private IAction _copyNodeAction;
    /**
     * The action to paste the copied Node.
     */
    private IAction _pasteNodeAction;
    /**
     * The Action to refresh the TreeView.
     */
    private IAction _refreshAction;
    private IAction _searchAction;
    private IAction _assembleEpicsAddressStringAction;

    private NodeConfig _nodeConfigComposite;
    private ImageDescriptor _paste;
    private ImageDescriptor _pasteDis;
    private Composite _editComposite;
    private List<FacilityLight> _load;
    private Action _newNodeAction;
    private static final Image ICON_WARNING = PlatformUI.getWorkbench().getDisplay()
            .getSystemImage(SWT.ICON_WARNING);

    /**
     * 
     * @author hrickens
     * @author $Author$
     * @version $Revision$
     * @since 27.05.2009
     */
    private final class ThreadExtension extends Thread {
        private final ProgressBar _bar;
        private boolean _run = true;

        private ThreadExtension(ProgressBar bar, int maximum) {
            _bar = bar;
        }

        public void stopThread() {
            _run = false;
        }

        public void run() {
            while (_run) {
                try {
                    Thread.sleep(100);
                    // Thread.yield();
                } catch (Throwable th) {
                }
                if (getDisplay().isDisposed()) {
                    return;
                }
                getDisplay().syncExec(new Runnable() {
                    public void run() {
                        if (_bar.isDisposed()) {
                            return;
                        }
                        _bar.setSelection(_bar.getSelection() + 1);
                        _bar.redraw();
                    }
                });
            }
        }
    }

    /**
     * 
     * @author hrickens
     * @author $Author$
     * @version $Revision$
     * @since 20.06.2007
     */
    // class ViewLabelProvider implements IViewerLabelProvider, ILabelProvider {
    class ViewLabelProvider extends ColumnLabelProvider {

        public Image getImage(final Object obj) {
            if (obj instanceof Node) {
                Node node = (Node) obj;
                return ConfigHelper.getImageFromNode(node);
            } else if (obj instanceof FacilityLight) {
                return ConfigHelper.getImageMaxSize("icons/css.gif", -1, -1);
            }
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#getBackground(java.lang.Object)
         */
        @Override
        public Color getBackground(Object element) {
            if (haveProgrammableModule(element)) {
                return CustomMediaFactory.getInstance().getColor(255, 140, 0);
            }
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#getFont(java.lang.Object)
         */
        @Override
        public Font getFont(Object element) {
            if (haveProgrammableModule(element)) {
                return CustomMediaFactory.getInstance().getFont("Tahoma", 8, SWT.ITALIC);
            }
            return null;
        }

        private boolean haveProgrammableModule(Object element) {
            //TODO: Das finden von Projekt Document Datein führt teilweise dazu das sich CSS Aufhängt! 
//            if (element instanceof Slave) {
//                Slave node = (Slave) element;
//                Set<Document> documents = node.getDocuments();
//                while (documents.iterator().hasNext()) {
//                    Document doc = (Document) documents.iterator().next();
//                    if (doc.getSubject() != null && doc.getSubject().startsWith("Projekt:")) {
//                        return true;
//                    }
//                }
//            }
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang.Object)
         */
        @Override
        public String getToolTipText(Object element) {
            if (haveProgrammableModule(element)) {
                return "Is a programmable Module!";
            }
            return null;
        }

        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
         */
        @Override
        public String getText(Object element) {
            String text = super.getText(element);
            String[] split = text.split("(\r(\n)?)");
            if(split.length>1) {
                text = split[0];
            }
            if (haveProgrammableModule(element)) {
                return text + " [prog]";
            }
            return text;
        }
        
        
    }

    /**
     * 
     * @author hrickens
     * @author $Author$
     * @version $Revision$
     * @since 20.06.2007
     */
    class NameSorter extends ViewerSorter {

        @Override
        public int category(Object element) {
            return super.category(element);
        }

        @Override
        public int compare(final Viewer viewer, final Object e1, final Object e2) {
            if (e1 instanceof NamedDBClass && e2 instanceof NamedDBClass) {
                NamedDBClass node1 = (NamedDBClass) e1;
                NamedDBClass node2 = (NamedDBClass) e2;
                if (node1.getSortIndex() == null || node2.getSortIndex() == null) {
                    return -1;
                }
                int sortIndex = node1.getSortIndex() - node2.getSortIndex().shortValue();
                if (sortIndex == 0) {
                    sortIndex = node1.getId() - node2.getId();
                }
                return sortIndex;
            }
            return 0;
        }
    }

    /**
     * @param parent
     *            The Parent Composit.
     * @param style
     *            The Style of the Composite
     * @param site
     *            The Controll Site
     * @param configComposite
     */
    public ProfiBusTreeView(final Composite parent, final int style, final IViewSite site) {
        super(parent, style);
        new InstanceScope().getNode(Activator.getDefault().getPluginId()).addPreferenceChangeListener(new IPreferenceChangeListener() {
            
            @Override
            public void preferenceChange(PreferenceChangeEvent event) {
                String property = event.getKey();
                if (property.equals(DDB_PASSWORD) || property.equals(DDB_USER_NAME)
                        || property.equals(DIALECT)
                        || property.equals(HIBERNATE_CONNECTION_DRIVER_CLASS)
                        || property.equals(HIBERNATE_CONNECTION_URL)) {
                    _load = Repository.load(FacilityLight.class);
                    _viewer.getTree().removeAll();
                    _viewer.setInput(_load);
                    _viewer.refresh(false);
                }
            }
        });
        _parent = parent;
        _site = site;
        _paste = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
                ISharedImages.IMG_TOOL_PASTE);
        _pasteDis = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
                ISharedImages.IMG_TOOL_PASTE_DISABLED);

        GridLayout layout = new GridLayout(1, true);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.marginLeft = 0;

        this.setLayout(layout);
        this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        _viewer = new TreeViewer(this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        ColumnViewerEditorActivationStrategy editorActivationStrategy = new ColumnViewerEditorActivationStrategy(
                _viewer);
        TreeViewerEditor.create(_viewer, editorActivationStrategy, TreeViewerEditor.DEFAULT);
        _drillDownAdapter = new DrillDownAdapter(_viewer);
        _viewer.setContentProvider(new ProfibusTreeContentProvider(_site));

        _viewer.setLabelProvider(new ViewLabelProvider());
        _viewer.setSorter(new NameSorter());
        _viewer.getTree().setHeaderVisible(false);
        _viewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        ColumnViewerToolTipSupport.enableFor(_viewer);

        CentralLogger.getInstance().debug(this, "ID: " + _site.getId());
        CentralLogger.getInstance().debug(this, "PlugIn ID: " + _site.getPluginId());
        CentralLogger.getInstance().debug(this, "Name: " + _site.getRegisteredName());
        CentralLogger.getInstance().debug(this, "SecID: " + _site.getSecondaryId());
        _load = Repository.load(FacilityLight.class);
        _viewer.setInput(_load);

        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();
        // ------------------------------------------------------------------------------
        // Versuch DnD innerhalb des Baumes die Node zu verschieben.
        // ------------------------------------------------------------------------------
        // DragSource source = new DragSource(_viewer.getControl(),
        // DND.DragStart);
        // Transfer[] transferTypes = new Transfer[] {};//
        // GadgetTransfer.getInstance()};
        // _viewer.addDragSupport(DND.DragStart, transferTypes,
        // source.getDragSourceEffect());
        // _viewer.getTree().addDragDetectListener(new DragDetectListener(){
        //
        // public void dragDetected(final DragDetectEvent e) {
        // System.out.println("drag data: "+e.data);
        // System.out.println("drag Source: "+e.getSource());
        // System.out.println("Item: "+_viewer.getTree().getItem(new
        // Point(e.x,e.y)));
        // }
        //            
        // });

        _viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(final SelectionChangedEvent event) {
                if (event.getSelection() instanceof StructuredSelection) {
                    _selectedNode = (StructuredSelection) event.getSelection();
                    if (_selectedNode != null && !_selectedNode.isEmpty()) {
                        _editNodeAction.run();
                    }
                }
            }
        });
    }

    /**
     * 
     * @return the Control of the TreeViewer
     */
    public final TreeViewer getTreeViewer() {
        return _viewer;
    }

    private void hookContextMenu() {
        final MenuManager popupMenuMgr = new MenuManager("#PopupMenu");
        popupMenuMgr.setRemoveAllWhenShown(true);
        popupMenuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(final IMenuManager manager) {

                ProfiBusTreeView.this.fillContextMenu(manager);
            }
        });
        Menu menu = popupMenuMgr.createContextMenu(_viewer.getControl());
        menu.setVisible(false);

        _viewer.getControl().setMenu(menu);
        _site.registerContextMenu(popupMenuMgr, _viewer);
        ImageDescriptor iDesc = CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
                ActivatorUI.PLUGIN_ID, "icons/expand_all.gif");
        Action expandAllAction = new Action() {
            public void run() {
                expandAll();
            }
        };
        expandAllAction.setText("Expand All");
        expandAllAction.setToolTipText("Expand All");
        expandAllAction.setImageDescriptor(iDesc);
        _site.getActionBars().getToolBarManager().add(expandAllAction);

        iDesc = CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(ActivatorUI.PLUGIN_ID,
                "icons/collapse_all.gif");
        Action collapseAllAction = new Action() {
            public void run() {
                _viewer.collapseAll();
            }
        };
        collapseAllAction.setText("Collapse All");
        collapseAllAction.setToolTipText("Collapse All");
        collapseAllAction.setImageDescriptor(iDesc);
        _site.getActionBars().getToolBarManager().add(collapseAllAction);
        ToolBar tB = new ToolBar(_viewer.getTree(), SWT.NONE);
        ToolBarManager tBM = new ToolBarManager(tB);
        tBM.add(collapseAllAction);
        tBM.createControl(_viewer.getTree());
    }

    private void contributeToActionBars() {
        IActionBars bars = _site.getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalPullDown(final IMenuManager manager) {
        manager.add(_newChildrenNodeAction);
        manager.add(new Separator());
        manager.add(_editNodeAction);
        manager.add(_copyNodeAction);
        manager.add(new Separator());
    }

    private void fillContextMenu(final IMenuManager manager) {
        Object selectedNode = _selectedNode.getFirstElement();
        if (selectedNode instanceof Facility || selectedNode instanceof FacilityLight) {
            setContriebutionActions("New Ioc", Facility.class, Ioc.class, manager);
            manager.add(new Separator());
            manager.add(_createNewXMLConfigFile);
        } else if (selectedNode instanceof Ioc) {
            setContriebutionActions("New Subnet", Ioc.class, ProfibusSubnet.class, manager);
            manager.add(new Separator());
            manager.add(_createNewXMLConfigFile);
        } else if (selectedNode instanceof ProfibusSubnet) {
            setContriebutionActions("New Master", ProfibusSubnet.class, Master.class, manager);
            manager.add(_createNewXMLConfigFile);
        } else if (selectedNode instanceof Master) {
            setContriebutionActions("New Slave", Master.class, Slave.class, manager);
        } else if (selectedNode instanceof Slave) {
            _newNodeAction.setText("Add new "+Slave.class.getSimpleName());
            manager.add(_newNodeAction);
            setContriebutionActions("New Module", Slave.class, Module.class, manager);
        } else if (selectedNode instanceof Module) {
            _newNodeAction.setText("Add new "+Module.class.getSimpleName());
            manager.add(_newNodeAction);
            manager.add(_copyNodeAction);
            if (_copiedNodesReferenceList != null && _copiedNodesReferenceList.size() > 0
                    && (Module.class.isInstance(_copiedNodesReferenceList.get(0)))) {
                _pasteNodeAction.setEnabled(true);
                _pasteNodeAction.setImageDescriptor(_paste);
            } else {
                _pasteNodeAction.setEnabled(false);
                _pasteNodeAction.setImageDescriptor(_pasteDis);
            }
            manager.add(_pasteNodeAction);
            manager.add(_deletNodeAction);
            manager.add(new Separator());
        }
        manager.add(_assembleEpicsAddressStringAction);
        manager.add(new Separator());
        _drillDownAdapter.addNavigationActions(manager);
        // Other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    /**
     * Set the Action to handle Node's.<br>
     * - new Child<br>
     * - copy<br>
     * - paste<br>
     * - delete<br>
     * 
     * @param text
     *            Set the Text for this new Node Action.
     * @param clazz
     *            the Node class to check can paste.
     * @param childClazz
     *            the Node child class to check can paste.
     * @param manager
     *            The {@link IMenuManager} to add the Actions.
     */
    @SuppressWarnings("unchecked")
    private void setContriebutionActions(final String text, final Class clazz,
            final Class childClazz, final IMenuManager manager) {
        _newChildrenNodeAction.setText(text);
        if ((_copiedNodesReferenceList != null)
                && (_copiedNodesReferenceList.size() > 0)
                && (clazz.isInstance(_copiedNodesReferenceList.get(0))
                        || childClazz.isInstance(_copiedNodesReferenceList.get(0)) || (clazz
                        .equals(Facility.class) && FacilityLight.class
                        .isInstance(_copiedNodesReferenceList.get(0))))) {
            _pasteNodeAction.setEnabled(true);
            _pasteNodeAction.setImageDescriptor(_paste);
        } else {
            _pasteNodeAction.setEnabled(false);
            _pasteNodeAction.setImageDescriptor(_pasteDis);
        }
        manager.add(_newChildrenNodeAction);
        manager.add(_copyNodeAction);
        manager.add(_pasteNodeAction);
        manager.add(_deletNodeAction);
    }

    private void fillLocalToolBar(final IToolBarManager manager) {
        manager.add(_newFacilityAction);
        manager.add(_refreshAction);
        manager.add(new Separator());
        _drillDownAdapter.addNavigationActions(manager);
        manager.add(new Separator());
        manager.add(_searchAction);
    }

    private void makeActions() {

        makeNewChildrenNodeAction();
        
        makeNewNodeAction();
        
        makeEditNodeAction();

        makeNewFacilityAction();

        makeSearchAction();

        makeAssembleEpicsAddressStringAction();

        _copyNodeAction = new Action() {
            @SuppressWarnings("unchecked")
            public void run() {
                _copiedNodesReferenceList = _selectedNode.toList();
            }
        };
        _copyNodeAction.setText("&Copy");
        _copyNodeAction.setToolTipText("Copy this Node");
        _copyNodeAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_TOOL_COPY));

        makePasteNodeAction();

        makeDeletNodeAction();

        makeCreateNewXMLConfigFile();

        makeTreeNodeRenameAction();

        _refreshAction = new Action() {
            public void run() {
                refresh();
            }
        };

        _refreshAction.setText("Refresh");
        _refreshAction.setToolTipText("Refresh the Tree");
        _refreshAction.setImageDescriptor(CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(ActivatorUI.PLUGIN_ID, "icons/refresh.gif"));
    }


    private void makeTreeNodeRenameAction() {

        // Create the editor and set its attributes
        final TreeEditor editor = new TreeEditor(_viewer.getTree());
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;
        _doubleClickAction = new Action() {

            public void run() {
                Tree tree = _viewer.getTree();
                final NamedDBClass node = (NamedDBClass) ((StructuredSelection) _viewer
                        .getSelection()).getFirstElement();
                final TreeItem item = tree.getSelection()[0];
                // Create a text field to do the editing
                String editText = "";
                if (node instanceof Channel) {
                    editText = ((Channel) node).getIoName();
                } else {
                    editText = node.getName();
                }
                if (editText == null) {
                    editText = "";
                }
                final Text text = new Text(tree, SWT.BORDER);
                text.setText(editText);
                text.selectAll();
                text.setFocus();

                // If the text field loses focus, set its text into the tree
                // and end the editing session
                text.addFocusListener(new FocusAdapter() {
                    public void focusLost(FocusEvent event) {
                        text.dispose();
                    }
                });

                /*
                 * If they hit Enter, set the text into the tree and end the editing session. If
                 * they hit Escape, ignore the text and end the editing session.
                 */
                text.addKeyListener(new KeyAdapter() {
                    public void keyPressed(KeyEvent event) {
                        switch (event.keyCode) {
                            case SWT.CR:
                            case SWT.KEYPAD_CR:
                                // Enter hit--set the text into the tree and
                                // drop through
                                String changedText = text.getText();
                                if (node instanceof Channel) {
                                    ((Channel) node).setIoName(changedText);
                                    if (_nodeConfigComposite instanceof ChannelConfigComposite) {
                                        ((ChannelConfigComposite) _nodeConfigComposite)
                                                .setIoNameText(changedText);
                                    }
                                } else {
                                    _nodeConfigComposite.setName(text.getText());
                                }
                                _nodeConfigComposite.store();
                                item.setText(node.toString());
                                text.dispose();
                            case SWT.ESC:
                                // End editing session
                                text.dispose();
                                break;
                            default:
                                break;
                        }
                    }
                });

                // Set the text field into the editor
                editor.setEditor(text, item);
            }
        };

    }

    private void makeCreateNewXMLConfigFile() {
        _createNewXMLConfigFile = new Action("Create") {
            public void run() {
                // TODO: Multi Selection XML Create.
                final String filterPathKey = "FilterPath";
                IEclipsePreferences pref = new DefaultScope().getNode(Activator.PLUGIN_ID);
                String filterPath = pref.get(filterPathKey, "");
                DirectoryDialog dDialog = new DirectoryDialog(_parent.getShell());
                dDialog.setFilterPath(filterPath);
                filterPath = dDialog.open();
                File path = new File(filterPath);
                pref.put(filterPathKey, filterPath);
                Object selectedNode = _selectedNode.getFirstElement();
                if (selectedNode instanceof ProfibusSubnet) {
                    ProfibusSubnet subnet = (ProfibusSubnet) selectedNode;
                    CentralLogger.getInstance().info(this, "Create XML for Subnet: " + subnet);
                    makeXMLFile(path, subnet);

                } else if (selectedNode instanceof Ioc) {
                    Ioc ioc = (Ioc) selectedNode;
                    CentralLogger.getInstance().info(this, "Create XML for Ioc: " + ioc);
                    for (ProfibusSubnet subnet : ioc.getProfibusSubnets()) {
                        makeXMLFile(path, subnet);
                    }
                } else if (selectedNode instanceof Facility) {
                    Facility facility = (Facility) selectedNode;
                    CentralLogger.getInstance().info(this, "Create XML for Facility: " + facility);
                    for (Ioc ioc : facility.getIoc()) {
                        for (ProfibusSubnet subnet : ioc.getProfibusSubnets()) {
                            makeXMLFile(path, subnet);
                        }
                    }
                } else if (selectedNode instanceof FacilityLight) {
                    FacilityLight fL = (FacilityLight) selectedNode;
                    CentralLogger.getInstance().info(this, "Create XML for Facility: " + fL);
                    try {
                        Facility facility = fL.getFacility();
                        for (Ioc ioc : facility.getIoc()) {
                            for (ProfibusSubnet subnet : ioc.getProfibusSubnets()) {
                                makeXMLFile(path, subnet);
                            }
                        }
                    } catch (RuntimeException e) {
                        ProfibusHelper.openErrorDialog(_site.getShell(), "Data Base Error",
                                "Device Data Base (DDB) Error\n"
                                        + "Can't load the %1s '%2s' (ID: %3s)", fL, e);
                        return;
                    }

                }
            }

            private void makeXMLFile(File path, ProfibusSubnet subnet) {
                ProfibusConfigXMLGenerator xml = new ProfibusConfigXMLGenerator(subnet.getName());
                xml.setSubnet(subnet);
                File xmlFile = new File(path, subnet.getName() + ".xml");
                if (xmlFile.exists()) {
                    MessageBox box = new MessageBox(Display.getDefault().getActiveShell(),
                            SWT.ICON_WARNING | SWT.YES | SWT.NO);
                    box.setMessage("The file " + xmlFile.getName() + " exist! Overwrite?");
                    int erg = box.open();
                    if (erg == SWT.YES) {
                        try {
                            xml.getXmlFile(xmlFile);
                        } catch (IOException e) {
                            MessageBox abortBox = new MessageBox(Display.getDefault()
                                    .getActiveShell(), SWT.ICON_WARNING | SWT.ABORT);
                            abortBox.setMessage("The file " + xmlFile.getName()
                                    + " can not created!");
                            abortBox.open();
                        }
                    }
                } else {
                    try {
                        xmlFile.createNewFile();
                        xml.getXmlFile(xmlFile);
                    } catch (IOException e) {
                        MessageBox abortBox = new MessageBox(Display.getDefault().getActiveShell(),
                                SWT.ICON_WARNING | SWT.ABORT);
                        abortBox.setMessage("The file " + xmlFile.getName() + " can not created!");
                        abortBox.open();
                    }
                }
            }
        };
        _createNewXMLConfigFile.setToolTipText("Action Create tooltip");
        _createNewXMLConfigFile.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
    }

    private void makeDeletNodeAction() {
        _deletNodeAction = new Action() {

            @SuppressWarnings("unchecked")
            public void run() {
                boolean openConfirm = MessageDialog.openConfirm(getShell(), "Delete Node", String
                        .format("You are will delete %1s: %2s", _selectedNode.toArray()[0]
                                .getClass().getSimpleName(), _selectedNode));
                if (openConfirm) {
                    Node parent = null;
                    NamedDBClass dbClass = null;
                    Iterator<NamedDBClass> iterator = _selectedNode.iterator();
                    while (iterator.hasNext()) {
                        dbClass = iterator.next();
                        if (dbClass instanceof Facility) {
                            Facility fac = (Facility) dbClass;
                            try {
                                Repository.removeNode(fac);
                                _load.remove(fac);
                                _viewer.remove(_load);
                            } catch (Exception e) {
                                ProfibusHelper.openErrorDialog(_site.getShell(), "Data Base Error",
                                        "Device Data Base (DDB) Error\n"
                                                + "Can't delete the %1s '%2s' (ID: %3s)", fac, e);

                                return;
                            }
                        } else if (dbClass instanceof FacilityLight) {
                            FacilityLight fL = (FacilityLight) dbClass;
                            try {
                                Repository.removeNode(fL.getFacility());
                                _load.remove(fL);
                                _viewer.setInput(_load);
                            } catch (RuntimeException e) {
                                ProfibusHelper.openErrorDialog(_site.getShell(), "Data Base Error",
                                        "Device Data Base (DDB) Error\n"
                                                + "Can't delete the %1s '%2s' (ID: %3s)", fL, e);
                                return;
                            }
                        } else if (dbClass instanceof Node) {
                            Node node = (Node) dbClass;
                            parent = node.getParent();
                            parent.removeChild(node);
                            try {
                                parent.save();
                            } catch (PersistenceException e) {
                                ProfibusHelper.openErrorDialog(_site.getShell(), "Data Base Error",
                                        "Device Data Base (DDB) Error\n"
                                                + "Can't delete the %1s '%2s' (ID: %3s)", node, e);
                            }
                        }
                        dbClass = parent;
                    }
                    if (parent != null) {
                        _selectedNode = new StructuredSelection(parent);
                        refresh(parent);
                        getTreeViewer().setSelection(_selectedNode, true);
                    } else {
                        if (dbClass == null) {
                            _viewer.refresh();
                        } else {
                            _viewer.refresh(dbClass);
                        }
                    }
                    _editNodeAction.run();
                }
            }
        };
        _deletNodeAction.setText("Delete");
        _deletNodeAction.setAccelerator(SWT.DEL);
        _deletNodeAction.setToolTipText("Delete this Node");
        _deletNodeAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_OBJS_ERROR_TSK));
    }

    private void makePasteNodeAction() {
        _pasteNodeAction = new Action() {
            @SuppressWarnings("static-access")
            public void run() {
                Object firstElement = _selectedNode.getFirstElement();
                Node selectedNode;
                if (firstElement instanceof Node) {
                    selectedNode = (Node) firstElement;
                } else if (firstElement instanceof FacilityLight) {
                    FacilityLight fL = (FacilityLight) firstElement;
                    selectedNode = fL.getFacility();
                } else {
                    return;
                }

                for (Node node2Copy : _copiedNodesReferenceList) {
                    if (node2Copy instanceof Facility) {
                        Facility copy = (Facility) selectedNode.copyThisTo(null);
                        FacilityLight facilityLight = new FacilityLight(copy);
                        _load.add(facilityLight);
                        _viewer.setInput(_load);
                        _viewer.setSelection(new StructuredSelection(facilityLight));

                    } else if (selectedNode.getClass().isInstance(node2Copy.getParent())) {
                        // paste to a Parent
                        Node copy = node2Copy.copyThisTo(selectedNode);
                        copy.setDirty(true);
                        copy.setSortIndexNonHibernate(selectedNode
                                .getfirstFreeStationAddress(copy.MAX_STATION_ADDRESS));
                        _viewer.refresh();
                        _viewer.setSelection(new StructuredSelection(copy));
                    } else if (selectedNode.getClass().isInstance(node2Copy)) {
                        // paste to a sibling
                        short targetIndex = (short) (selectedNode.getSortIndex());
                        Node nodeCopy = node2Copy.copyThisTo(selectedNode.getParent());
                        nodeCopy.moveSortIndex(targetIndex);
                        refresh();
                        // refresh(nodeCopy.getParent());
                        _viewer.setSelection(new StructuredSelection(nodeCopy));
                    }
                }
            }
        };
        _pasteNodeAction.setText("Paste");
        _pasteNodeAction.setAccelerator('v');
        _pasteNodeAction.setToolTipText("Paste this Node");
    }

    private void makeNewFacilityAction() {
        _newFacilityAction = new Action() {

            public void run() {
                setEditComposite();
                if (_parent instanceof SashForm) {
                    SashForm form = (SashForm) _parent;
                    int[] weights = form.getWeights();
                    Control[] childrens = _parent.getChildren();
                    for (int i = 1; i < childrens.length; i++) {
                        if (childrens[i] instanceof Composite) {
                            childrens[i].dispose();
                        }
                    }
                    new FacilityConfigComposite(_editComposite, ProfiBusTreeView.this, null);

                    int[] test = form.getWeights();
                    if (weights.length != test.length) {
                        for (int i = 0; i < weights.length && i < test.length; i++) {
                            test[i] = weights[i];
                        }
                        form.setWeights(test);
                    } else {
                        form.setWeights(weights);
                    }
                } else {
                    Control[] childrens = _parent.getChildren();
                    for (int i = 1; i < childrens.length; i++) {
                        if (childrens[i] instanceof Composite) {
                            childrens[i].dispose();
                        }
                    }
                    new FacilityConfigComposite(_editComposite, ProfiBusTreeView.this, null);
                }
                _editComposite.getParent().layout(true);
            }
        };
        _newFacilityAction.setText("new Facility");
        _newFacilityAction.setToolTipText("Create a new Facility");
        _newFacilityAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD));

    }

    private void makeAssembleEpicsAddressStringAction() {
        _assembleEpicsAddressStringAction = new Action() {
            public void run() {
                Object selectedNode = _selectedNode.getFirstElement();
                if (selectedNode instanceof Node) {
                    Node node = (Node) selectedNode;
                    try {
                        node.assembleEpicsAddressString();
                    } catch (PersistenceException e) {
                        // TODO Handle DDB Error
                        e.printStackTrace();
                    }
                }
            }
        };
        _assembleEpicsAddressStringAction.setText("Refresh EPCICS Adr");
        _assembleEpicsAddressStringAction
                .setToolTipText("Refesh from all childen the EPICS Address Strings");
        _assembleEpicsAddressStringAction.setImageDescriptor(CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(ActivatorUI.PLUGIN_ID, "icons/refresh.gif"));

    }

    private void makeSearchAction() {
        _searchAction = new Action() {

            public void run() {
                SearchDialog searchDialog = new SearchDialog(new Shell(SWT.RESIZE),
                        ProfiBusTreeView.this);
                int open = searchDialog.open();
                if (open == 0) {
                    NamedDBClass selectedNode = null;
                    List<Integer> rootPath = Repository.getRootPath(searchDialog.getSelectedNode()
                            .getId());
                    if (rootPath.size() > 0) {
                        for (FacilityLight fl : _load) {
                            if (rootPath.get(rootPath.size() - 1) == fl.getId()) {
                                selectedNode = fl;
                                Facility facility = fl.getFacility();
                                if (rootPath.size() > 1) {
                                    selectedNode = goPath(facility, rootPath, 1);
                                }
                                break;
                            }
                        }
                    }
                    StructuredSelection ss = new StructuredSelection(selectedNode);
                    _viewer.setSelection(ss, true);
                    _viewer.refresh();

                }

            }

            private NamedDBClass goPath(Node node, List<Integer> rootPath, int i) {
                if (rootPath.size() > i) {
                    Integer id = rootPath.get(rootPath.size() - (i + 1));
                    Set<? extends Node> childrens = node.getChildren();
                    if (!childrens.isEmpty()) {
                        for (Node childeren : childrens) {
                            if (childeren.getId() == id) {
                                return goPath(childeren, rootPath, i + 1);
                            }
                        }
                    }
                }
                return node;
            }
        };
        _searchAction.setText("Search");
        _searchAction.setToolTipText("Search a Node");
        _searchAction.setImageDescriptor(CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(ActivatorUI.PLUGIN_ID, "icons/search.png"));
    }

    private void makeEditNodeAction() {
        _editNodeAction = new Action() {

            public void run() {
                if (getEnabled()) {
                    if (_parent instanceof SashForm) {
                        SashForm form = (SashForm) _parent;
                        int[] weights = form.getWeights();
                        editNode();
                        int[] test = form.getWeights();
                        if (weights.length != test.length) {
                            for (int i = 0; i < weights.length && i < test.length; i++) {
                                test[i] = weights[i];
                            }
                            form.setWeights(test);
                        } else {
                            form.setWeights(weights);
                        }
                    } else {
                        editNode();
                    }
                }
            }
        };
        _editNodeAction.setText("Edit");
        _editNodeAction.setToolTipText("Edit Node");
        _editNodeAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD));

    }

    private void makeNewChildrenNodeAction() {
        _newChildrenNodeAction = new Action() {
            public void run() {
                openNewEmptyChildrenNode();
            }
        };
        _newChildrenNodeAction.setText("New");
        _newChildrenNodeAction.setToolTipText("Action 1 tooltip");
        _newChildrenNodeAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
    }

    private void makeNewNodeAction() {
        _newNodeAction = new Action() {
            public void run() {
                openNewEmptyNode();
            }
        };
        _newNodeAction.setText("New");
        _newNodeAction.setToolTipText("Action 1 tooltip");
        _newNodeAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
                .getImageDescriptor(ISharedImages.IMG_ETOOL_HOME_NAV));
    }
    
    private void hookDoubleClickAction() {
        _viewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(final DoubleClickEvent event) {
                _doubleClickAction.run();
            }
        });
    }

    private void openNewEmptyChildrenNode() {
        // clearEditComposite();
        setEditComposite();
        Object selectedNode = _selectedNode.getFirstElement();
        if (selectedNode instanceof Facility || selectedNode instanceof FacilityLight) {
            _nodeConfigComposite = new IocConfigComposite(_editComposite, this, null);
        } else if (selectedNode instanceof Ioc) {
            _nodeConfigComposite = new SubNetConfigComposite(_editComposite, this, null);
        } else if (selectedNode instanceof ProfibusSubnet) {
            _nodeConfigComposite = new MasterConfigComposite(_editComposite, this, null);
        } else if (selectedNode instanceof Master) {
            _nodeConfigComposite = new SlaveConfigComposite(_editComposite, this, null);
        } else if (selectedNode instanceof Slave) {
            _nodeConfigComposite = new ModuleConfigComposite(_editComposite, this, null);
        } else if (selectedNode instanceof Module) {
            _nodeConfigComposite = new ChannelConfigComposite(_editComposite, this, null);
        } // Do nothing (have no sub-elements)
        // else if (_selectedNode instanceof Channel) {
        //        
        // }
        _editComposite.getParent().layout(true);
    }

    private void openNewEmptyNode() {
        setEditComposite();
        Object selectedNode = _selectedNode.getFirstElement();
        if (selectedNode instanceof Facility) {
            Node node = (Node) selectedNode; 
            _nodeConfigComposite = new FacilityConfigComposite(_editComposite, this, (short) (node.getSortIndex()+1));
        } else if(selectedNode instanceof FacilityLight) {
            FacilityLight node = (FacilityLight) selectedNode; 
            _nodeConfigComposite = new FacilityConfigComposite(_editComposite, this,(short) (node.getSortIndex()+1));
        } else if (selectedNode instanceof Ioc) {
            _nodeConfigComposite = new IocConfigComposite(_editComposite, this, null);
        } else if (selectedNode instanceof ProfibusSubnet) {
            _nodeConfigComposite = new SubNetConfigComposite(_editComposite, this, null);
        } else if (selectedNode instanceof Master) {
            _nodeConfigComposite = new MasterConfigComposite(_editComposite, this, null);
        } else if (selectedNode instanceof Slave) {
            _nodeConfigComposite = new SlaveConfigComposite(_editComposite, this, null);
        } else if (selectedNode instanceof Module) {
            _nodeConfigComposite = new ModuleConfigComposite(_editComposite, this, null);
        } 
        _editComposite.getParent().layout(true);
    }

    
    /**
     * Open a ConfigComposite for the tree selection Node.
     */
    private void editNode() {
        _editNodeAction.setEnabled(false);
        setEditComposite();

        Object selectedNode = _selectedNode.getFirstElement();
        if (selectedNode instanceof FacilityLight) {
            FacilityLight f = (FacilityLight) selectedNode;
            Shell shell = new Shell(getDisplay());
            final ProgressBar bar;
            ThreadExtension thread = null;
            try {
                bar = new ProgressBar(shell, SWT.INDETERMINATE);
                // final ProgressBar bar = new ProgressBar(_editComposite,
                // SWT.INDETERMINATE);
                bar.setBounds(10, 10, 200, 24);
                shell.setBounds(getDisplay().getClientArea().width / 4 - 115, getDisplay()
                        .getBounds().height / 2 - 44, 230, 88);
                shell.open();
                final int maximum = bar.getMaximum();

                thread = new ThreadExtension(bar, maximum);
                thread.start();
                // das wird beim erstenmal eine zeitlang dauern...
                Facility facility = f.getFacility();
                _nodeConfigComposite = new FacilityConfigComposite(_editComposite, this, facility); // XXX
            } catch (RuntimeException e) {
                ProfibusHelper.openErrorDialog(_site.getShell(), "Data Base Error",
                        "Device Data Base (DDB) Error\n" + "Can't load the %1s '%2s' (ID: %3s)", f,
                        e);
                return;
            } finally {
                if (shell != null && !shell.isDisposed()) {
                    shell.close();
                }
                if (thread != null) {
                    thread.stopThread();
                }
            }
            // bar.dispose();
        } else if (selectedNode instanceof Facility) {
            _nodeConfigComposite = new FacilityConfigComposite(_editComposite, this,
                    ((Facility) selectedNode));
        } else if (selectedNode instanceof Ioc) {
            _nodeConfigComposite = new IocConfigComposite(_editComposite, this,
                    ((Ioc) selectedNode));
        } else if (selectedNode instanceof ProfibusSubnet) {
            _nodeConfigComposite = new SubNetConfigComposite(_editComposite, this,
                    ((ProfibusSubnet) selectedNode));
        } else if (selectedNode instanceof Master) {
            _nodeConfigComposite = new MasterConfigComposite(_editComposite, this,
                    ((Master) selectedNode));
        } else if (selectedNode instanceof Slave) {
            _nodeConfigComposite = new SlaveConfigComposite(_editComposite, this,
                    ((Slave) selectedNode));
        } else if (selectedNode instanceof Module) {
            _nodeConfigComposite = new ModuleConfigComposite(_editComposite, this,
                    ((Module) selectedNode));
        } else if (selectedNode instanceof Channel) {
            _nodeConfigComposite = new ChannelConfigComposite(_editComposite, this,
                    ((Channel) selectedNode));
        } else {
            Node node = (Node) selectedNode;
            String nodeText = "";
            if (node != null) {
                nodeText = node.toString();
            }
            _nodeConfigComposite = new InfoConfigComposte(_editComposite, this, SWT.NONE, node,
                    nodeText);
        }
        _editNodeAction.setEnabled(true);
        _editComposite.getParent().layout(false);
    }

    private void setEditComposite() {
        if (_editComposite != null && !_editComposite.isDisposed()) {
            Control[] childrens = _editComposite.getChildren();
            for (int i = 0; i < childrens.length; i++) {
                if (childrens[i] instanceof NodeConfig && _selectedNode != null) {
                    NodeConfig nc = (NodeConfig) childrens[i];
                    if (nc.isDirty()) {
                        String[] buttonLabels = new String[] { "&Save", "Don't Save", "&Cancel" };
                        MessageDialog id = new MessageDialog(getShell(), "Node not saved!",
                                ICON_WARNING, "The Node is not saved.\r\n Save now?",
                                MessageDialog.WARNING, buttonLabels, 2);
                        id.setBlockOnOpen(true);
                        switch (id.open()) {
                            case Dialog.OK:
                                // Persist the node.
                                nc.store();
                                break;
                            case Dialog.CANCEL:
                                // don't save the actual node and change to the
                                // new selected.
                                Node node = nc.getNode();
                                if (node != null && node.getId() < 1) {
                                    if (node instanceof Facility) {
                                        Facility fac = (Facility) node;
                                        _viewer.remove(fac.getFacilityLigth());
                                    } else if (node.getParent() != null) {
                                        nc.getNode().getParent().removeChild(nc.getNode());
                                    }
                                }
                                nc.cancel();
                                // _viewer.setSelection(_selectedNode);
                                _editNodeAction.setEnabled(true);
                                break;
                            default:
                                // TreePath[] expandedTreePaths =
                                // _viewer.getExpandedTreePaths();
                                // Object[] expandedElements =
                                // _viewer.getExpandedElements();
                                _viewer.setSelection(new StructuredSelection(nc), true);
                                _editNodeAction.setEnabled(true);

                                return;
                        }
                        id.close();
                    }
                }
            }
            _editComposite.dispose();
        }

        _editComposite = null;
        IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        IWorkbenchPage page = window.getActivePage();
        IViewPart showView;
        try {
            showView = page.showView(NodeConfigView.ID);
            if (showView instanceof NodeConfigView) {
                _editComposite = new Composite(((NodeConfigView) showView).getComposite(), SWT.None);
                FillLayout layout = new FillLayout();
                _editComposite.setLayout(layout);
            }
        } catch (PartInitException e) {
            e.printStackTrace();
        }
    }

    /** refresh the Tree. Reload all Nodes */
    public final void refresh() {
        _viewer.setInput(new Object());
        _viewer.refresh();
    }

    /**
     * Refresh the Tree. Reload element Nodes
     * 
     * @param element
     *            Down at this element the tree are refreshed.
     */
    public final void refresh(final Object element) {
        _viewer.refresh(element, true);
    }

    /**
     * Expand the complete Tree.
     */
    public final void expandAll() {
        for (FacilityLight object : _load) {
            if (object.isLoaded()) {
                _viewer.expandToLevel(object, TreeViewer.ALL_LEVELS);
            }
        }
        Control[] childs = _parent.getChildren();
        for (Control control : childs) {
            if (control instanceof NodeConfig) {
                NodeConfig nodeConfig = (NodeConfig) control;
                ISelection sel = new StructuredSelection(nodeConfig.getNode());
                _viewer.setSelection(sel);
            }
        }
    }
}
