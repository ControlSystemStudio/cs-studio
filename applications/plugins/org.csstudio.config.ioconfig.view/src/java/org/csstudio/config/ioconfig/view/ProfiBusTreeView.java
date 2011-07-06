/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
/*
 * $Id: ProfiBusTreeView.java,v 1.26 2010/08/20 13:33:03 hrickens Exp $
 */
package org.csstudio.config.ioconfig.view;

import static org.csstudio.config.ioconfig.model.preference.PreferenceConstants.DDB_PASSWORD;
import static org.csstudio.config.ioconfig.model.preference.PreferenceConstants.DDB_USER_NAME;
import static org.csstudio.config.ioconfig.model.preference.PreferenceConstants.DIALECT;
import static org.csstudio.config.ioconfig.model.preference.PreferenceConstants.HIBERNATE_CONNECTION_DRIVER_CLASS;
import static org.csstudio.config.ioconfig.model.preference.PreferenceConstants.HIBERNATE_CONNECTION_URL;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.commands.CallEditor;
import org.csstudio.config.ioconfig.commands.CallNewChildrenNodeEditor;
import org.csstudio.config.ioconfig.commands.CallNewFacilityEditor;
import org.csstudio.config.ioconfig.commands.CallNewSiblingNodeEditor;
import org.csstudio.config.ioconfig.config.view.helper.ConfigHelper;
import org.csstudio.config.ioconfig.config.view.helper.ProfibusHelper;
import org.csstudio.config.ioconfig.editorparts.AbstractNodeEditor;
import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.IOConfigActivator;
import org.csstudio.config.ioconfig.model.IocDBO;
import org.csstudio.config.ioconfig.model.NamedDBClass;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.Repository;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelDBO;
import org.csstudio.config.ioconfig.model.pbmodel.MasterDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnetDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
import org.csstudio.config.ioconfig.model.tools.NodeMap;
import org.csstudio.config.ioconfig.view.actions.CreateStatisticAction;
import org.csstudio.config.ioconfig.view.actions.CreateWinModAction;
import org.csstudio.config.ioconfig.view.actions.CreateXMLConfigAction;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
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
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.DrillDownAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @since 19.06.2007
 */
public class ProfiBusTreeView extends Composite {
    
    private static final Logger LOG = LoggerFactory.getLogger(ProfiBusTreeView.class);
    /**
     * The ID of the View.
     */
    public static final String ID = ProfiBusTreeView.class.getName();
    public static final String PARENT_NODE_ID = "org.csstudio.config.ioconfig.parent.node";
    private final IViewSite _site;
    
    /**
     * The ProfiBus Tree View.
     */
    private final TreeViewer _viewer;
    /**
     * The parent Composite for the Node Config Composite.
     */
    private final DrillDownAdapter _drillDownAdapter;
    /**
     * the Selected Node.
     */
    private StructuredSelection _selectedNode;
    /**
     * A Copy from a Node.
     */
    private List<AbstractNodeDBO> _copiedNodesReferenceList;
    /**
     * Select _copiedNodesReferenceList Nodes a Copied or moved
     */
    private boolean _move;
    /**
     * This action open an Empty Node. Type of new node dependent on Parent.
     */
    private IAction _newChildrenNodeAction;
    /**
     * This action open an selected Node. Type of new node dependent on Parent.
     */
    private IAction _editNodeAction;
    /**
     * This Action open a new empty Node. (No Facility!)
     */
    private Action _newNodeAction;
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
     * The action to Cut a Node.
     */
    private Action _cutNodeAction;
    /**
     * The action to paste the copied Node.
     */
    private IAction _pasteNodeAction;
    /**
     * The Action to refresh the TreeView.
     */
    private IAction _refreshAction;
    /**
     * The Action to open the Search-Dialog.
     */
    private IAction _searchAction;
    /**
     * The Action to reassemble the EPICS Address String.
     */
    private IAction _assembleEpicsAddressStringAction;
    /**
     * A List of all loaded {@link FacilityDBO}'s
     */
    private List<FacilityDBO> _load;
    /**
    * The actual open Node Config Editor.
    */
    private AbstractNodeEditor _openNodeEditor;
    private Action _createNewSiemensConfigFile;

    private CreateStatisticAction _createNewStatisticFile;
    
    /**
     * Retrieves the image descriptor for specified image from the workbench's image registry.
     * Unlike Images, image descriptors themselves do not need to be disposed.
     *
     * @param symbolicName
     *            the symbolic name of the image; there are constants declared in this interface for
     *            build-in images that come with the workbench
     * @return the image descriptor, or null if not found
     */
    @CheckForNull
    private static ImageDescriptor getSharedImageDescriptor(@Nonnull final String symbolicName) {
        return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(symbolicName);
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
    public ProfiBusTreeView(@Nonnull final Composite parent, final int style,
                            @Nonnull final IViewSite site) {
        super(parent, style);
        new InstanceScope().getNode(IOConfigActivator.getDefault().getPluginId())
                .addPreferenceChangeListener(new HibernateDBPreferenceChangeListener());
        _site = site;
        
        GridLayout layout = GridLayoutFactory.fillDefaults().equalWidth(true).create();
        this.setLayout(layout);
        this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        _viewer = new TreeViewer(this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        ColumnViewerEditorActivationStrategy editorActivationStrategy = new ColumnViewerEditorActivationStrategy(
                                                                                                                 _viewer);
        TreeViewerEditor.create(_viewer, editorActivationStrategy, ColumnViewerEditor.DEFAULT);
        _drillDownAdapter = new DrillDownAdapter(_viewer);
        _viewer.setContentProvider(new ProfibusTreeContentProvider());
        
        _viewer.setLabelProvider(new ViewLabelProvider());
        _viewer.setSorter(new NameSorter());
        _viewer.getTree().setHeaderVisible(false);
        _viewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        _site.setSelectionProvider(_viewer);
        ColumnViewerToolTipSupport.enableFor(_viewer);
        
        LOG.debug("ID: {}", _site.getId());
        LOG.debug("PlugIn ID: {}", _site.getPluginId());
        LOG.debug("Name: {}", _site.getRegisteredName());
        LOG.debug("SecID: {}", _site.getSecondaryId());
        
        runFacilityLoaderJob();
        
        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();
        
        _viewer.addSelectionChangedListener(new NodeSelcetionChangedListener());
        
        this.addDisposeListener(new DisposeListener() {
            
            @Override
            public void widgetDisposed(@Nonnull final DisposeEvent e) {
                Repository.close();
            }
        });
    }
    
    /**
     * 
     */
    protected void runFacilityLoaderJob() {
        getViewer().setInput("Please wait a moment");
        AbstractNodeEditor openEditor = getOpenEditor();
        if (openEditor != null) {
            openEditor.perfromClose();
        }
        try {
            getViewer().getTree().setEnabled(false);
            Job loadJob = new DBLoderJob("DBLoader");
            loadJob.setUser(true);
            loadJob.schedule();
        } catch (RuntimeException e) {
            ProfibusHelper.openErrorDialog(_site.getShell(), "Data Base Error",
                                           "Device Data Base (DDB) Error\n"
                                                   + "Can't load the Root data", null, e);
            return;
        }
    }
    
    /**
     * Add a new Facility to the tree root.
     *
     * @param node
     *            the new Facility.
     */
    public final void addFacility(@Nullable final AbstractNodeDBO node) {
        if (node instanceof FacilityDBO) {
            getViewer().setInput(node);
        }
    }
    
    private void contributeToActionBars() {
        IActionBars bars = _site.getActionBars();
        fillLocalToolBar(bars.getToolBarManager());
    }
    
    /**
     * Open a ConfigComposite for the tree selection Node.
     */
    protected void editNode() {
        _editNodeAction.setEnabled(false);
        
        closeOpenEditor();
        IHandlerService handlerService = (IHandlerService) _site.getService(IHandlerService.class);
        try {
            handlerService.executeCommand(CallEditor.ID, null);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return;
    }
    
    /**
     *
     */
    public void closeOpenEditor() {
        if (_openNodeEditor != null) {
            _openNodeEditor.perfromClose();
            _openNodeEditor = null;
        }
    }
    
    /**
     * Expand the complete Tree.
     */
    public final void expandAll() {
        /*
         * TODO: Wird nicht mehr gemacht da es von der Performenc her unklug ist. Es werden einfach
         * zuviele Nodes auf einmal geladen, was zu Laden zeiten in Minutenbreiche führt
         */
    }
    
    // CHECKSTYLE OFF: CyclomaticComplexity
    protected void fillContextMenu(@Nonnull final IMenuManager manager) {
        StructuredSelection selection = getSelectedNodes();
        if (selection != null) {
            Object selectedNode = selection.getFirstElement();
            if (selectedNode instanceof FacilityDBO) {
                setContriebutionActions("New Ioc", FacilityDBO.class, IocDBO.class, manager);
                manager.add(new Separator());
                manager.add(_createNewXMLConfigFile);
                manager.add(_createNewSiemensConfigFile);
                manager.add(_createNewStatisticFile);
            } else if (selectedNode instanceof IocDBO) {
                setContriebutionActions("New Subnet", IocDBO.class, ProfibusSubnetDBO.class,
                                        manager);
                manager.add(new Separator());
                manager.add(_createNewXMLConfigFile);
                manager.add(_createNewSiemensConfigFile);
            } else if (selectedNode instanceof ProfibusSubnetDBO) {
                setContriebutionActions("New Master", ProfibusSubnetDBO.class, MasterDBO.class,
                                        manager);
                manager.add(_createNewXMLConfigFile);
                manager.add(_createNewSiemensConfigFile);
            } else if (selectedNode instanceof MasterDBO) {
                setContriebutionActions("New Slave", MasterDBO.class, SlaveDBO.class, manager);
            } else if (selectedNode instanceof SlaveDBO) {
                _newNodeAction.setText("Add new " + SlaveDBO.class.getSimpleName());
                manager.add(_newNodeAction);
                setContriebutionActions("New Module", SlaveDBO.class, ModuleDBO.class, manager);
            } else if (selectedNode instanceof ModuleDBO) {
                fillModuleContextMenu(manager);
            }
            manager.add(_assembleEpicsAddressStringAction);
            manager.add(new Separator());
            _drillDownAdapter.addNavigationActions(manager);
            // Other plug-ins can contribute there actions here
            manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        }
    }
    // CHECKSTYLE ON: CyclomaticComplexity
    
    /**
     * @param manager
     */
    private void fillModuleContextMenu(@Nonnull final IMenuManager manager) {
        _newNodeAction.setText("Add new " + ModuleDBO.class.getSimpleName());
        manager.add(_newNodeAction);
        manager.add(_copyNodeAction);
        manager.add(_cutNodeAction);
        
        boolean pasteEnable = (_copiedNodesReferenceList != null)
                && (_copiedNodesReferenceList.size() > 0)
                && (ModuleDBO.class.isInstance(_copiedNodesReferenceList.get(0)));
        _pasteNodeAction.setEnabled(pasteEnable);
        manager.add(_pasteNodeAction);
        manager.add(_deletNodeAction);
        manager.add(new Separator());
    }
    
    private void fillLocalToolBar(@Nonnull final IToolBarManager manager) {
        manager.add(new Separator());
        manager.add(makeNewFacilityAction());
        manager.add(_refreshAction);
        manager.add(new Separator());
        _drillDownAdapter.addNavigationActions(manager);
        manager.add(new Separator());
        manager.add(_searchAction);
    }
    
    /**
     *
     * @return the Control of the TreeViewer
     */
    @Nonnull
    public final TreeViewer getTreeViewer() {
        return getViewer();
    }
    
    private void hookContextMenu() {
        final MenuManager popupMenuMgr = new MenuManager("#PopupMenu");
        popupMenuMgr.setRemoveAllWhenShown(true);
        popupMenuMgr.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(@Nonnull final IMenuManager manager) {
                ProfiBusTreeView.this.fillContextMenu(manager);
            }
        });
        Menu menu = popupMenuMgr.createContextMenu(getViewer().getControl());
        menu.setVisible(false);
        
        getViewer().getControl().setMenu(menu);
        _site.registerContextMenu(popupMenuMgr, getViewer());
        
        ImageDescriptor iDesc = CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(IOConfigActivatorUI.PLUGIN_ID,
                                              "icons/collapse_all.gif");
        Action collapseAllAction = new Action() {
            @Override
            public void run() {
                getViewer().collapseAll();
            }
        };
        collapseAllAction.setText("Collapse All");
        collapseAllAction.setToolTipText("Collapse All");
        collapseAllAction.setImageDescriptor(iDesc);
        _site.getActionBars().getToolBarManager().add(collapseAllAction);
        ToolBar tB = new ToolBar(getViewer().getTree(), SWT.NONE);
        ToolBarManager tBM = new ToolBarManager(tB);
        tBM.add(collapseAllAction);
        tBM.createControl(getViewer().getTree());
    }
    
    private void hookDoubleClickAction() {
        getViewer().addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(@Nonnull final DoubleClickEvent event) {
                getDoubleClickAction().run();
            }
        });
    }
    
    @Nonnull
    protected IAction getDoubleClickAction() {
        return _doubleClickAction;
    }
    
    private void makeActions() {
        makeNewChildrenNodeAction();
        makeNewNodeAction();
        makeEditNodeAction();
        makeNewFacilityAction();
        makeSearchAction();
        makeAssembleEpicsAddressStringAction();
        makeCopyNodeAction();
        makeCutNodeAction();
        makePasteNodeAction();
        makeDeletNodeAction();
        makeCreateNewXMLConfigFile();
        makeCreateNewSiemensConfigFile();
        makeCreateNewStatisticFile();
        makeTreeNodeRenameAction();
        makeRefreshAction();
    }
    
    /**
     * Generate a Action that reassemble the EPICS Address String for the selected {@link AbstractNodeDBO} and
     * all Children.
     */
    private void makeAssembleEpicsAddressStringAction() {
        _assembleEpicsAddressStringAction = new Action() {
            @Override
            public void run() {
                Object selectedNode = getSelectedNodes().getFirstElement();
                if (selectedNode instanceof AbstractNodeDBO) {
                    AbstractNodeDBO node = (AbstractNodeDBO) selectedNode;
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
                .getImageDescriptorFromPlugin(IOConfigActivatorUI.PLUGIN_ID, "icons/refresh.gif"));
        
    }
    
    private void makeCreateNewXMLConfigFile() {
        _createNewXMLConfigFile = new CreateXMLConfigAction("Create EPICS", this);
        _createNewXMLConfigFile.setToolTipText("Action Create tooltip");
        _createNewXMLConfigFile
                .setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_OBJ_FILE));
    }
    
    private void makeCreateNewSiemensConfigFile() {
        _createNewSiemensConfigFile = new CreateWinModAction("Create WinMod", this);
        _createNewSiemensConfigFile.setToolTipText("Action Create tooltip");
        _createNewSiemensConfigFile
                .setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_OBJ_FILE));
    }
    
    private void makeCreateNewStatisticFile() {
        _createNewStatisticFile = new CreateStatisticAction("Create Statistik", this);
        _createNewStatisticFile.setToolTipText("Action Create tooltip");
        _createNewStatisticFile
                .setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_OBJ_FILE));
    }
    
    private void makeDeletNodeAction() {
        _deletNodeAction = new DeleteNodeActionExtension();
        _deletNodeAction.setText("Delete");
        _deletNodeAction.setAccelerator(SWT.DEL);
        _deletNodeAction.setToolTipText("Delete this Node");
        _deletNodeAction
                .setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_OBJS_ERROR_TSK));
    }
    
    /**
     * Generate a Action that open the {@link AbstractNodeConfig} for the selected {@link AbstractNodeDBO}.
     */
    private void makeEditNodeAction() {
        _editNodeAction = new Action() {
            
            @Override
            public void run() {
                if (getEnabled()) {
                    editNode();
                }
            }
        };
        _editNodeAction.setText("Edit");
        _editNodeAction.setToolTipText("Edit Node");
        _editNodeAction
                .setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD));
        
    }
    
    /**
     * Generate a Action that make a new Children {@link AbstractNodeDBO} and open the Config View.
     */
    private void makeNewChildrenNodeAction() {
        _newChildrenNodeAction = new Action() {
            @Override
            public void run() {
                openNewEmptyChildrenNode();
            }
        };
        _newChildrenNodeAction.setText("New");
        _newChildrenNodeAction.setToolTipText("Action 1 tooltip");
        _newChildrenNodeAction.setAccelerator('n');
        _newChildrenNodeAction
                .setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_OBJ_ADD));
    }
    
    /**
     * This action open a new level one empty Node. The type of this node is {@link FacilityDBO}.
     */
    @Nonnull
    private Action makeNewFacilityAction() {
        Action newFacilityAction = new Action() {
            
            @Override
            public void run() {
                closeOpenEditor();
                IHandlerService handlerService = (IHandlerService) getSite()
                        .getService(IHandlerService.class);
                try {
                    handlerService.executeCommand(CallNewFacilityEditor.ID, null);
                } catch (Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        };
        newFacilityAction.setText("new Facility");
        newFacilityAction.setToolTipText("Create a new Facility");
        newFacilityAction
                .setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD));
        return newFacilityAction;
        
    }
    
    /**
     * Generate a Action that make a new Sibling {@link AbstractNodeDBO} and open the Config View.
     */
    private void makeNewNodeAction() {
        _newNodeAction = new Action() {
            @Override
            public void run() {
                closeOpenEditor();
                openNewEmptySiblingNode();
            }
            
        };
        _newNodeAction.setText("New");
        _newNodeAction.setToolTipText("Action 1 tooltip");
        _newNodeAction
                .setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_ETOOL_HOME_NAV));
    }
    
    private void makeCopyNodeAction() {
        _copyNodeAction = new Action() {
            
            @Override
            @SuppressWarnings("unchecked")
            public void run() {
                _copiedNodesReferenceList = getSelectedNodes().toList();
                setMove(false);
            }
        };
        _copyNodeAction.setText("&Copy");
        _copyNodeAction.setToolTipText("Copy this Node");
        _copyNodeAction.setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_TOOL_COPY));
    }
    
    private void makeCutNodeAction() {
        _cutNodeAction = new Action() {
            
            @Override
            @SuppressWarnings("unchecked")
            public void run() {
                _copiedNodesReferenceList = getSelectedNodes().toList();
                setMove(true);
            }
        };
        _cutNodeAction.setText("Cut");
        _cutNodeAction.setAccelerator(SWT.CTRL | 'x');
        _cutNodeAction.setToolTipText("Cut this Node");
        _cutNodeAction.setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_TOOL_CUT));
    }
    
    private void makePasteNodeAction() {
        _pasteNodeAction = new PasteNodeAction();
        _pasteNodeAction.setText("Paste");
        _pasteNodeAction.setAccelerator('v');
        _pasteNodeAction.setToolTipText("Paste this Node");
        _pasteNodeAction.setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
        _pasteNodeAction
                .setDisabledImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
        
    }
    
    /**
     * Generate a Action that open the {@link SearchDialog}.
     */
    private void makeSearchAction() {
        _searchAction = new Action() {
            
            @Override
            public void run() {
                SearchDialog searchDialog = new SearchDialog(getShell(), ProfiBusTreeView.this);
                searchDialog.open();
            }
            
        };
        _searchAction.setText("Search");
        _searchAction.setToolTipText("Search a Node");
        _searchAction.setImageDescriptor(CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(IOConfigActivatorUI.PLUGIN_ID, "icons/search.png"));
    }
    
    private void makeTreeNodeRenameAction() {
        
        // Create the editor and set its attributes
        final TreeEditor editor = new TreeEditor(getViewer().getTree());
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;
        _doubleClickAction = new RenameNodeAction(editor);
        
    }
    
    private void makeRefreshAction() {
        _refreshAction = new Action() {
            @Override
            public void run() {
                runFacilityLoaderJob();
            }
        };
        
        _refreshAction.setText("Reload");
        _refreshAction.setToolTipText("Reload from the DataBase.");
        _refreshAction.setImageDescriptor(CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(IOConfigActivatorUI.PLUGIN_ID, "icons/refresh.gif"));
    }
    
    public final void reload() {
        _refreshAction.run();
    }
    
    /** refresh the Tree. Reload all Nodes */
    public final void refresh() {
        getViewer().setInput(new Object());
        getViewer().refresh();
    }
    
    /**
     * Refresh the Tree. Reload element Nodes
     *
     * @param element
     *            Down at this element the tree are refreshed.
     */
    public final void refresh(@Nullable final Object element) {
        getViewer().refresh(element, true);
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
    private void setContriebutionActions(@Nonnull final String text, @Nonnull final Class<?> clazz,
                                         @Nonnull final Class<?> childClazz,
                                         @Nonnull final IMenuManager manager) {
        _newChildrenNodeAction.setText(text);
        boolean pasteEnable = (_copiedNodesReferenceList != null)
                && (_copiedNodesReferenceList.size() > 0)
                && (clazz.isInstance(_copiedNodesReferenceList.get(0))
                        || childClazz.isInstance(_copiedNodesReferenceList.get(0)) || (clazz
                        .equals(FacilityDBO.class) && FacilityDBO.class
                        .isInstance(_copiedNodesReferenceList.get(0))));
        _pasteNodeAction.setEnabled(pasteEnable);
        manager.add(_newChildrenNodeAction);
        manager.add(_copyNodeAction);
        manager.add(_cutNodeAction);
        manager.add(_pasteNodeAction);
        manager.add(_deletNodeAction);
    }
    
    private void openEditor(@Nonnull final String editorID) {
        IHandlerService handlerService = (IHandlerService) _site.getService(IHandlerService.class);
        try {
            closeOpenEditor();
            handlerService.executeCommand(editorID, null);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }
    
    protected void openInfoDialog() {
        Shell shell = new Shell(getShell(), SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
        Dialog infoDialog = new InfoDialog(shell);
        infoDialog.open();
    }
    
    protected void openNewEmptyChildrenNode() {
        openEditor(CallNewChildrenNodeEditor.getEditorID());
    }
    
    private void openNewEmptySiblingNode() {
        openEditor(CallNewSiblingNodeEditor.getEditorID());
    }
    
    /**
     * @return the site
     */
    @Nonnull
    public IViewSite getSite() {
        return _site;
    }
    
    /**
     * @param abstractNodeEditor
     */
    public void setOpenEditor(@Nullable final AbstractNodeEditor openNodeEditor) {
        _openNodeEditor = openNodeEditor;
    }
    
    public void removeOpenEditor(@Nullable final AbstractNodeEditor openNodeEditor) {
        if (_openNodeEditor != null && _openNodeEditor.equals(openNodeEditor)) {
            _openNodeEditor = null;
        }
    }
    
    @CheckForNull
    public AbstractNodeEditor getOpenEditor() {
        return _openNodeEditor;
    }
    
    protected void setSelectedNode(@Nullable StructuredSelection selectedNode) {
        _selectedNode = selectedNode;
    }
    
    @CheckForNull
    public StructuredSelection getSelectedNodes() {
        return _selectedNode;
    }
    
    @Nonnull
    protected TreeViewer getViewer() {
        return _viewer;
    }
    
    protected void setLoad(@Nonnull List<FacilityDBO> load) {
        _load = load;
    }
    
    @Nonnull
    protected List<FacilityDBO> getLoad() {
        return _load;
    }
    
    @Nonnull
    protected final IAction getEditNodeAction() {
        assert _editNodeAction != null;
        return _editNodeAction;
    }
    
    protected void setMove(boolean move) {
        _move = move;
    }

    protected boolean isMove() {
        return _move;
    }

    /**
     * @author Rickens Helge
     * @author $Author: $
     * @since 12.01.2011
     */
    private final class DBLoderJob extends Job {
        /**
         * Constructor.
         * @param name The Taskname
         */
        private DBLoderJob(@Nonnull String name) {
            super(name);
        }
        
        @Override
        @Nonnull
        protected IStatus run(@Nonnull final IProgressMonitor monitor) {
            monitor.beginTask("DBLoaderMonitor", IProgressMonitor.UNKNOWN);
            monitor.setTaskName("Load \t-\tStart Time: " + new Date());
            Repository.close();
            try {
                setLoad(Repository.load(FacilityDBO.class));
                PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
                    @Override
                    public void run() {
                        getViewer().setInput(getLoad());
                        getViewer().getTree().setEnabled(true);
                    }
                });
            } catch (PersistenceException e) {
                DeviceDatabaseErrorDialog.open(null, "Can't read from Database!", e);
                LOG.error("Can't read from Database!", e);
            }
            monitor.done();
            return Status.OK_STATUS;
        }
    }
    
    /**
     * 
     * Rename the selected Node on the tree. 
     * 
     * @author hrickens
     * @author $Author: $
     * @since 08.10.2010
     */
    private final class RenameNodeAction extends Action {
        private final TreeEditor _editor;
        
        RenameNodeAction(@Nonnull TreeEditor editor) {
            _editor = editor;
        }
        
        @Override
        public void run() {
            Tree tree = getViewer().getTree();
            final NamedDBClass node = (NamedDBClass) ((StructuredSelection) getViewer()
                    .getSelection()).getFirstElement();
            final TreeItem item = tree.getSelection()[0];
            // Create a text field to do the editing
            String editText = "";
            if (node instanceof ChannelDBO) {
                editText = ((ChannelDBO) node).getIoName();
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
            
            // If the text field loses focus, set its text into the tree and end the editing session
            text.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(@Nonnull final FocusEvent event) {
                    text.dispose();
                }
            });
            
            // Set the text field into the editor
            _editor.setEditor(text, item);
        }
    }
    
    /**
     * 
     * Paste a Node to the selected node in the Tree. 
     * 
     * @author hrickens
     * @author $Author: $
     * @since 08.10.2010
     */
    private final class PasteNodeAction extends Action {
        public PasteNodeAction() {
            // Constructor
        }
        
        @Override
        public void run() {
            Object firstElement = getSelectedNodes().getFirstElement();
            AbstractNodeDBO selectedNode;
            if (firstElement instanceof AbstractNodeDBO) {
                selectedNode = (AbstractNodeDBO) firstElement;
            } else {
                return;
            }
            
            for (AbstractNodeDBO node2Copy : _copiedNodesReferenceList) {
                try {
                    if (node2Copy instanceof FacilityDBO) {
                        copyFacility(selectedNode);
                    } else if (selectedNode.getClass().isInstance(node2Copy.getParent())) {
                        copy2Parent(selectedNode, node2Copy);
                    } else if (selectedNode.getClass().isInstance(node2Copy)) {
                        copy2Sibling(selectedNode, node2Copy);
                    }
                } catch (PersistenceException e) {
                    DeviceDatabaseErrorDialog.open(null, "Can't copy Node! Database Error.", e);
                    LOG.error("Can't copy Node. Device Database Error", e);
                }
            }
        }
        
        /**
         * @param selectedNode
         * @param node2Copy
         * @throws PersistenceException 
         */
        private void copy2Parent(@Nonnull AbstractNodeDBO selectedNode,
                                 @Nonnull AbstractNodeDBO node2Copy) throws PersistenceException {
            AbstractNodeDBO copy = null;
            if (isMove()) {
                AbstractNodeDBO oldParent = node2Copy.getParent();
                oldParent.removeChild(node2Copy);
                Map<Short, AbstractNodeDBO<AbstractNodeDBO, AbstractNodeDBO>> childrenAsMap = selectedNode.getChildrenAsMap();
                AbstractNodeDBO node = childrenAsMap.get(node2Copy.getSortIndex());
                if (node != null) {
                    int freeStationAddress = selectedNode
                            .getfirstFreeStationAddress(AbstractNodeDBO.MAX_STATION_ADDRESS);
                    node2Copy.setSortIndex(freeStationAddress);
                }
                selectedNode.addChild(node2Copy);
                copy = node2Copy;
                selectedNode.save();
            } else {
                // paste to a Parent
                copy = node2Copy.copyThisTo(selectedNode);
                copy.setDirty(true);
                copy.setSortIndexNonHibernate(selectedNode
                        .getfirstFreeStationAddress(AbstractNodeDBO.MAX_STATION_ADDRESS));
            }
            getViewer().refresh();
            getViewer().setSelection(new StructuredSelection(copy));
        }
        
        /**
         * @param selectedNode
         * @param node2Copy
         * @throws PersistenceException 
         */
        private void copy2Sibling(@Nonnull AbstractNodeDBO selectedNode,
                                  @Nonnull AbstractNodeDBO node2Copy) throws PersistenceException {
            AbstractNodeDBO nodeCopy = null;
            if (isMove()) {
                AbstractNodeDBO oldParent = node2Copy.getParent();
                oldParent.removeChild(node2Copy);
                AbstractNodeDBO parent = selectedNode.getParent();
                node2Copy.setSortIndex((int) selectedNode.getSortIndex());
                parent.addChild(node2Copy);
                parent.save();
                nodeCopy = node2Copy;
            } else {
                // paste to a sibling
                short targetIndex = (selectedNode.getSortIndex());
                targetIndex++;
                nodeCopy = node2Copy.copyThisTo(selectedNode.getParent());
                nodeCopy.moveSortIndex(targetIndex);
            }
            refresh();
            getViewer().setSelection(new StructuredSelection(nodeCopy));
        }
        
        /**
         * @param selectedNode
         * @throws PersistenceException 
         */
        private void copyFacility(@Nonnull AbstractNodeDBO selectedNode)
                                                                        throws PersistenceException {
            final FacilityDBO copy = (FacilityDBO) selectedNode.copyThisTo(null);
            copy.setSortIndexNonHibernate(selectedNode.getSortIndex() + 1);
            List<FacilityDBO> load = getLoad();
            load.add(copy);
            getViewer().setInput(load);
            getViewer().setSelection(new StructuredSelection(copy));
        }
    }
    
    /**
     * 
     * Delete the selected Nodes. 
     * 
     * @author hrickens
     * @author $Author: $
     * @since 08.10.2010
     */
    private final class DeleteNodeActionExtension extends Action {
        
        public DeleteNodeActionExtension() {
            // constructor
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public void run() {
            String errMsg = "Device Data Base (DDB) Error\nCan't delete the %1s '%2s' (ID: %3s)";
            String errMsgHead = "Device Database Error";
            boolean openConfirm = MessageDialog.openConfirm(getShell(), "Delete Node", String
                    .format("Delete %1s: %2s", getSelectedNodes().toArray()[0].getClass()
                            .getSimpleName(), getSelectedNodes()));
            if (openConfirm) {
                AbstractNodeDBO parent = null;
                NamedDBClass dbClass = null;
                Iterator<NamedDBClass> iterator = getSelectedNodes().iterator();
                while (iterator.hasNext()) {
                    dbClass = iterator.next();
                    if (dbClass instanceof FacilityDBO) {
                        deleteFacility(errMsg, errMsgHead, dbClass);
                    } else if (dbClass instanceof AbstractNodeDBO) {
                        parent = deleteNode(errMsg, errMsgHead, dbClass);
                    }
                }
                if (parent != null) {
                    setSelectedNode(new StructuredSelection(parent));
                    refresh(parent);
                    getTreeViewer().setSelection(getSelectedNodes(), true);
                } else {
                    refresh();
                }
                _editNodeAction.run();
            }
        }

        /**
         * @param errMsg
         * @param errMsgHead
         * @param dbClass
         * @return
         */
        @CheckForNull
        private AbstractNodeDBO deleteNode(@Nonnull String errMsg,@Nullable String errMsgHead,@Nonnull NamedDBClass dbClass) {
            AbstractNodeDBO parent;
            AbstractNodeDBO node = (AbstractNodeDBO) dbClass;
            parent = node.getParent();
            parent.removeChild(node);
            try {
                parent.save();
            } catch (PersistenceException e) {
                ProfibusHelper.openErrorDialog(getSite().getShell(), errMsgHead, errMsg,
                                               node, e);
                return null;
            }
            return parent;
        }

        /**
         * @param errMsg
         * @param errMsgHead
         * @param dbClass
         */
        private void deleteFacility(@Nonnull String errMsg,@Nullable String errMsgHead,@Nonnull NamedDBClass dbClass) {
            FacilityDBO fac = (FacilityDBO) dbClass;
            try {
                Repository.removeNode(fac);
                getLoad().remove(fac);
                getViewer().remove(getLoad());
            } catch (Exception e) {
                ProfibusHelper.openErrorDialog(getSite().getShell(), errMsgHead, errMsg,
                                               fac, e);
                return;
            }
        }
    }
    
    /**
     * @author hrickens
     * @author $Author: $
     * @since 08.10.2010
     */
    private static final class InfoDialog extends Dialog {
        
        InfoDialog(@Nonnull Shell parentShell) {
            super(parentShell);
        }
        
        @Override
        @Nonnull
        protected Control createDialogArea(@Nonnull final Composite parent) {
            final Composite createDialogArea = (Composite) super.createDialogArea(parent);
            createDialogArea.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
            
            createDialogArea.setLayout(GridLayoutFactory.swtDefaults().equalWidth(true)
                    .numColumns(3).create());
            Label label = new Label(createDialogArea, SWT.NONE);
            label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
            label.setText("Nodes: " + NodeMap.getNumberOfNodes());
            
            label = new Label(createDialogArea, SWT.NONE);
            label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
            
            label = new Label(createDialogArea, SWT.NONE);
            
            label = new Label(createDialogArea, SWT.NONE);
            label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
            label.setText("Assemble: " + NodeMap.getCountAssembleEpicsAddressString());
            
            label = new Label(createDialogArea, SWT.NONE);
            label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
            label.setText("LocalUpdate: " + NodeMap.getLocalUpdate());
            
            label = new Label(createDialogArea, SWT.NONE);
            label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
            label.setText("ChannelConfig: " + NodeMap.getChannelConfigComposite());
            
            final Text text = new Text(createDialogArea, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
            text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
            
            label = new Label(createDialogArea, SWT.NONE);
            createDialogArea.pack();
            return createDialogArea;
        }
    }
    
    /**
     * @author hrickens
     * @author $Author: $
     * @since 07.10.2010
     */
    private final class NodeSelcetionChangedListener implements ISelectionChangedListener {
        
        public NodeSelcetionChangedListener() {
            // Default Constructor
        }
        
        @Override
        public void selectionChanged(@Nonnull final SelectionChangedEvent event) {
            if (event.getSelection() instanceof StructuredSelection) {
                StructuredSelection selection = (StructuredSelection) event.getSelection();
                if (!selection.equals(getSelectedNodes())) {
                    setSelectedNode(selection);
                    if ( (getSelectedNodes() != null) && !getSelectedNodes().isEmpty()) {
                        getEditNodeAction().run();
                    }
                }
            }
        }
    }
    
    /**
     * @author hrickens
     * @author $Author: $
     * @since 05.10.2010
     */
    private final class HibernateDBPreferenceChangeListener implements IPreferenceChangeListener {
        
        public HibernateDBPreferenceChangeListener() {
            // Default Constructor.
        }
        
        @Override
        public void preferenceChange(@Nonnull final PreferenceChangeEvent event) {
            String property = event.getKey();
            if (property.equals(DDB_PASSWORD) || property.equals(DDB_USER_NAME)
                    || property.equals(DIALECT)
                    || property.equals(HIBERNATE_CONNECTION_DRIVER_CLASS)
                    || property.equals(HIBERNATE_CONNECTION_URL)) {
                try {
                    List<FacilityDBO> load = Repository.load(FacilityDBO.class);
                    setLoad(load);
                } catch (PersistenceException e) {
                    setLoad(new ArrayList<FacilityDBO>());
                    DeviceDatabaseErrorDialog.open(null,
                                                   "Can't read from Database! Database Error.", e);
                    LOG.error("Can't read from Database! Database Error.", e);
                }
                getViewer().getTree().removeAll();
                getViewer().setInput(getLoad());
                getViewer().refresh(false);
            }
        }
    }
    
    /**
     *
     * @author hrickens
     * @author $Author: hrickens $
     * @since 20.06.2007
     */
    class NameSorter extends ViewerSorter {
        
        @Override
        public int category(@Nullable final Object element) {
            return super.category(element);
        }
        
        @Override
        public int compare(@Nonnull final Viewer viewer, @Nullable final Object e1,
                           @Nullable final Object e2) {
            if ( (e1 instanceof NamedDBClass) && (e2 instanceof NamedDBClass)) {
                NamedDBClass node1 = (NamedDBClass) e1;
                NamedDBClass node2 = (NamedDBClass) e2;
                if ( (node1.getSortIndex() == null) || (node2.getSortIndex() == null)) {
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
     *
     * @author hrickens
     * @author $Author: hrickens $
     * @since 20.06.2007
     */
    static class ViewLabelProvider extends ColumnLabelProvider {
        
        private static final Color PROGRAMMABLE_MARKER_COLOR = CustomMediaFactory.getInstance()
                .getColor(255, 140, 0);
        private static final Font PROGRAMMABLE_MARKER_FONT = CustomMediaFactory.getInstance()
                .getFont("Tahoma", 8, SWT.ITALIC);
        
        @Override
        @CheckForNull
        public Color getBackground(@Nullable final Object element) {
            if (haveProgrammableModule(element)) {
                return PROGRAMMABLE_MARKER_COLOR;
            }
            return null;
        }
        
        @Override
        @CheckForNull
        public Font getFont(@Nullable final Object element) {
            if (haveProgrammableModule(element)) {
                return PROGRAMMABLE_MARKER_FONT;
            }
            return null;
        }
        
        @Override
        @CheckForNull
        public Image getImage(@Nullable final Object obj) {
            if (obj instanceof AbstractNodeDBO) {
                AbstractNodeDBO node = (AbstractNodeDBO) obj;
                return ConfigHelper.getImageFromNode(node);
            } else if (obj instanceof FacilityDBO) {
                return ConfigHelper.getImageMaxSize("icons/css.gif", -1, -1);
            }
            return null;
        }
        
        @Override
        @CheckForNull
        public String getText(@Nonnull final Object element) {
            String text = super.getText(element);
            String[] split = text.split("(\r(\n)?)");
            if (split.length > 1) {
                text = split[0];
            }
            if (haveProgrammableModule(element)) {
                return text + " [prog]";
            }
            return text;
        }
        
        @Override
        @CheckForNull
        public String getToolTipText(@Nullable final Object element) {
            if (haveProgrammableModule(element)) {
                return "Is a programmable Module!";
            }
            return null;
        }
        
        private boolean haveProgrammableModule(@Nullable final Object element) {
            /*
             * TODO: (hrickens) Das finden von Projekt Document Datein führt teilweise dazu das sich
             * CSS Aufhängt! if (element instanceof Slave) { Slave node = (Slave) element;
             * Set<Document> documents = node.getDocuments(); while (documents.iterator().hasNext())
             * { Document doc = (Document) documents.iterator().next(); if (doc.getSubject() != null
             * && doc.getSubject().startsWith("Projekt:")) { return true; } } }
             */
            return false;
        }
        
    }
}
