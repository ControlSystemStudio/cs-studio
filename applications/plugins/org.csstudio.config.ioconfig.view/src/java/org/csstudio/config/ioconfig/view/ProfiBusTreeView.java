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

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.log4j.Logger;
import org.csstudio.config.ioconfig.commands.CallEditor;
import org.csstudio.config.ioconfig.commands.CallNewChildrenNodeEditor;
import org.csstudio.config.ioconfig.commands.CallNewFacilityEditor;
import org.csstudio.config.ioconfig.commands.CallNewSiblingNodeEditor;
import org.csstudio.config.ioconfig.config.view.helper.ConfigHelper;
import org.csstudio.config.ioconfig.config.view.helper.ProfibusHelper;
import org.csstudio.config.ioconfig.editorparts.AbstractNodeEditor;
import org.csstudio.config.ioconfig.model.Activator;
import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.IocDBO;
import org.csstudio.config.ioconfig.model.NamedDBClass;
import org.csstudio.config.ioconfig.model.AbstractNodeDBO;
import org.csstudio.config.ioconfig.model.PersistenceException;
import org.csstudio.config.ioconfig.model.Repository;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelDBO;
import org.csstudio.config.ioconfig.model.pbmodel.MasterDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnetDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
import org.csstudio.config.ioconfig.model.siemens.ProfibusConfigSiemensGenerator;
import org.csstudio.config.ioconfig.model.tools.NodeMap;
import org.csstudio.config.ioconfig.model.xml.ProfibusConfigXMLGenerator;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
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
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
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
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.DrillDownAdapter;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @since 19.06.2007
 */
public class ProfiBusTreeView extends Composite {

	private static final Logger LOG = CentralLogger.getInstance().getLogger(
			ProfiBusTreeView.class);
	
    /**
     * The ID of the View.
     */
    public static final String ID = ProfiBusTreeView.class.getName();
    public static final String PARENT_NODE_ID = "org.csstudio.config.ioconfig.parent.node";
    private static final String NEW_NODE_COMMAND_ID = CallNewSiblingNodeEditor.getEditorID();
//    private static final String NEW_NODE_COMMAND_ID = IocEditor.ID;
    private final IViewSite _site;

    /**
     * The Parent Composite.
     */
    private final Composite _parent;
    /**
     * The ProfiBus Tree View.
     */
    private final TreeViewer _viewer;
    /**
     * The parent Composite for the Node Config Composite.
     */
    //    private Composite _parentConfigComposite;
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
    //    /**
    //     * The actual open Node Config Composite.
    //     */
    //    private NodeConfig _nodeConfigComposite;
    /**
     * A List of all loaded {@link FacilityDBO}'s
     */
    private List<FacilityDBO> _load;
    private Action _infoDialogAction;
    private AbstractNodeEditor _openNodeEditor;
    private Action _createNewSiemensConfigFile;

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
    public ProfiBusTreeView(@Nonnull final Composite parent, final int style,@Nonnull final IViewSite site) {
        super(parent, style);
        new InstanceScope().getNode(Activator.getDefault().getPluginId())
                .addPreferenceChangeListener(new HibernateDBPreferenceChangeListener());
        _parent = parent;
        _site = site;

        GridLayout layout = new GridLayout(1, true);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.marginLeft = 0;

        this.setLayout(layout);
        this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        _viewer = new TreeViewer(this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        ColumnViewerEditorActivationStrategy editorActivationStrategy = new ColumnViewerEditorActivationStrategy(getViewer());
        TreeViewerEditor.create(getViewer(), editorActivationStrategy, ColumnViewerEditor.DEFAULT);
        _drillDownAdapter = new DrillDownAdapter(getViewer());
        getViewer().setContentProvider(new ProfibusTreeContentProvider());

        getViewer().setLabelProvider(new ViewLabelProvider());
        getViewer().setSorter(new NameSorter());
        getViewer().getTree().setHeaderVisible(false);
        getViewer().getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        _site.setSelectionProvider(getViewer());
        ColumnViewerToolTipSupport.enableFor(getViewer());

        LOG.debug("ID: " + _site.getId());
        LOG.debug("PlugIn ID: " + _site.getPluginId());
        LOG.debug("Name: " + _site.getRegisteredName());
        LOG.debug("SecID: " + _site.getSecondaryId());

        // ---
        getViewer().setInput("Please wait a moment");
        try {

            Job loadJob = new Job("DBLoader") {

                @Override
                protected IStatus run(@Nonnull final IProgressMonitor monitor) {
                    monitor.beginTask("DBLoaderMonitor", IProgressMonitor.UNKNOWN);
                    monitor.setTaskName("Load \t-\tStart Time: " + new Date());

                    PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

                        @Override
                        public void run() {
                            getViewer().getTree().setEnabled(false);
                            setLoad(Repository.load(FacilityDBO.class));
                            getViewer().setInput(getLoad());
                            getViewer().getTree().setEnabled(true);
                        }
                    });
                    monitor.done();
                    return Status.OK_STATUS;
                }

            };
            loadJob.setUser(true);
            loadJob.schedule();

        } catch (RuntimeException e) {
            ProfibusHelper.openErrorDialog(_site.getShell(),
                                           "Data Base Error",
                                           "Device Data Base (DDB) Error\n"
                                                   + "Can't load the Root data",
                                           null,
                                           e);
            return;
        }
        // ---

        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();

        getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
			public void selectionChanged(@Nonnull final SelectionChangedEvent event) {
                if (event.getSelection() instanceof StructuredSelection) {
                    setSelectedNode((StructuredSelection) event.getSelection());
                    if ( (getSelectedNode() != null) && !getSelectedNode().isEmpty()) {

                        _editNodeAction.run();
                    }
                }
            }
        });

        this.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(@Nonnull final DisposeEvent e) {
                // TODO: Umstellen auf Editor
                //                checkDirtyConfig(_nodeConfigComposite);
                Repository.close();
            }
        });

    }

    /**
     * Add a new Facility to the tree root.
     *
     * @param node
     *            the new Facility.
     */
    public final void addFacility(@Nullable final AbstractNodeDBO node) {
        if (node instanceof FacilityDBO) {
            //            _load.add((Facility) node);
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
    private void editNode() {
        _editNodeAction.setEnabled(false);
        //        setEditComposite();

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
        }
    }

    /**
     * Expand the complete Tree.
     */
    public final void expandAll() {
        /*
         * TODO: Wird nicht mehr gemacht da es von der Performenc her unklug ist.
         * Es werden einfach zuviele Nodes auf einmal geladen, was zu Laden zeiten
         * in Minutenbreiche führt
         */
    }

    protected void fillContextMenu(@Nonnull final IMenuManager manager) {
        Object selectedNode = getSelectedNode().getFirstElement();
        if (selectedNode instanceof FacilityDBO) {
            setContriebutionActions("New Ioc", FacilityDBO.class, IocDBO.class, manager);
            manager.add(new Separator());
            manager.add(_createNewXMLConfigFile);
            manager.add(_createNewSiemensConfigFile);
        } else if (selectedNode instanceof IocDBO) {
            setContriebutionActions("New Subnet", IocDBO.class, ProfibusSubnetDBO.class, manager);
            manager.add(new Separator());
            manager.add(_createNewXMLConfigFile);
            manager.add(_createNewSiemensConfigFile);
        } else if (selectedNode instanceof ProfibusSubnetDBO) {
            setContriebutionActions("New Master", ProfibusSubnetDBO.class, MasterDBO.class, manager);
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

	/**
	 * @param manager
	 */
	private void fillModuleContextMenu(final IMenuManager manager) {
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
        manager.add(_infoDialogAction);
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
                .getImageDescriptorFromPlugin(ActivatorUI.PLUGIN_ID, "icons/expand_all.gif");
        Action expandAllAction = new Action() {
            @Override
            public void run() {
                expandAll();
            }
        };
        expandAllAction.setText("Expand All");
        expandAllAction.setToolTipText("Expand All");
        expandAllAction.setImageDescriptor(iDesc);
        _site.getActionBars().getToolBarManager().add(expandAllAction);

        iDesc = CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(ActivatorUI.PLUGIN_ID, "icons/collapse_all.gif");
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
            public void doubleClick(@Nonnull final DoubleClickEvent event) {
                _doubleClickAction.run();
            }
        });
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
        makeTreeNodeRenameAction();
        makeRefreshAction();
        makeInfoDialogAction();
    }

    private void makeInfoDialogAction() {
        _infoDialogAction = new Action() {
            @Override
            public void run() {
                openInfoDialog();
            }

        };
        _infoDialogAction.setText("Info");
        _infoDialogAction.setToolTipText("Action 1 tooltip");
        _infoDialogAction.setAccelerator('i');
        _infoDialogAction
                .setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
    }

    /**
     * Generate a Action that reassemble the EPICS Address String for the selected {@link AbstractNodeDBO} and
     * all Children.
     */
    private void makeAssembleEpicsAddressStringAction() {
        _assembleEpicsAddressStringAction = new Action() {
            @Override
            public void run() {
                Object selectedNode = getSelectedNode().getFirstElement();
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
                .getImageDescriptorFromPlugin(ActivatorUI.PLUGIN_ID, "icons/refresh.gif"));

    }

    private void makeCreateNewXMLConfigFile() {
        _createNewXMLConfigFile = new Action("Create") {
            private void makeXMLFile(final File path, final ProfibusSubnetDBO subnet) {
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

            @Override
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
                Object selectedNode = getSelectedNode().getFirstElement();
                if (selectedNode instanceof ProfibusSubnetDBO) {
                    ProfibusSubnetDBO subnet = (ProfibusSubnetDBO) selectedNode;
                    LOG.info("Create XML for Subnet: " + subnet);
                    makeXMLFile(path, subnet);

                } else if (selectedNode instanceof IocDBO) {
                    IocDBO ioc = (IocDBO) selectedNode;
                    LOG.info("Create XML for Ioc: " + ioc);
                    for (ProfibusSubnetDBO subnet : ioc.getProfibusSubnets()) {
                        makeXMLFile(path, subnet);
                    }
                } else if (selectedNode instanceof FacilityDBO) {
                    FacilityDBO facility = (FacilityDBO) selectedNode;
                    LOG.info("Create XML for Facility: " + facility);
                    for (IocDBO ioc : facility.getIoc()) {
                        for (ProfibusSubnetDBO subnet : ioc.getProfibusSubnets()) {
                            makeXMLFile(path, subnet);
                        }
                    }
                }
            }
        };
        _createNewXMLConfigFile.setToolTipText("Action Create tooltip");
        _createNewXMLConfigFile
                .setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_OBJ_FILE));
    }

    private void makeCreateNewSiemensConfigFile() {
        _createNewSiemensConfigFile = new Action("Create Siemens") {
            private void makeXMLFile(final File path, final ProfibusSubnetDBO subnet) {
                ProfibusConfigSiemensGenerator cfg = new ProfibusConfigSiemensGenerator(subnet.getName());
                cfg.setSubnet(subnet);
                File xmlFile = new File(path, subnet.getName() + ".cfg");
                if (xmlFile.exists()) {
                    MessageBox box = new MessageBox(Display.getDefault().getActiveShell(),
                                                    SWT.ICON_WARNING | SWT.YES | SWT.NO);
                    box.setMessage("The file " + xmlFile.getName() + " exist! Overwrite?");
                    int erg = box.open();
                    if (erg == SWT.YES) {
                        try {
                            cfg.getXmlFile(xmlFile);
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
                        cfg.getXmlFile(xmlFile);
                    } catch (IOException e) {
                        MessageBox abortBox = new MessageBox(Display.getDefault().getActiveShell(),
                                                             SWT.ICON_WARNING | SWT.ABORT);
                        abortBox.setMessage("The file " + xmlFile.getName() + " can not created!");
                        abortBox.open();
                    }
                }
            }

            @Override
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
                Object selectedNode = getSelectedNode().getFirstElement();
                if (selectedNode instanceof ProfibusSubnetDBO) {
                    ProfibusSubnetDBO subnet = (ProfibusSubnetDBO) selectedNode;
                    LOG.info("Create XML for Subnet: " + subnet);
                    makeXMLFile(path, subnet);

                } else if (selectedNode instanceof IocDBO) {
                    IocDBO ioc = (IocDBO) selectedNode;
                    LOG.info("Create XML for Ioc: " + ioc);
                    for (ProfibusSubnetDBO subnet : ioc.getProfibusSubnets()) {
                        makeXMLFile(path, subnet);
                    }
                } else if (selectedNode instanceof FacilityDBO) {
                    FacilityDBO facility = (FacilityDBO) selectedNode;
                    LOG.info("Create XML for Facility: " + facility);
                    for (IocDBO ioc : facility.getIoc()) {
                        for (ProfibusSubnetDBO subnet : ioc.getProfibusSubnets()) {
                            makeXMLFile(path, subnet);
                        }
                    }
                }
            }
        };
        _createNewSiemensConfigFile.setToolTipText("Action Create tooltip");
        _createNewSiemensConfigFile
        .setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_OBJ_FILE));
    }

    private void makeDeletNodeAction() {
        _deletNodeAction = new Action() {

            @Override
            @SuppressWarnings("unchecked")
            public void run() {
                boolean openConfirm = MessageDialog.openConfirm(getShell(), "Delete Node", String
                        .format("Delete %1s: %2s", getSelectedNode().toArray()[0].getClass()
                                .getSimpleName(), getSelectedNode()));
                if (openConfirm) {
                    AbstractNodeDBO parent = null;
                    NamedDBClass dbClass = null;
                    Iterator<NamedDBClass> iterator = getSelectedNode().iterator();
                    while (iterator.hasNext()) {
                        dbClass = iterator.next();
                        if (dbClass instanceof FacilityDBO) {
                            FacilityDBO fac = (FacilityDBO) dbClass;
                            try {
                                Repository.removeNode(fac);
                                getLoad().remove(fac);
                                getViewer().remove(getLoad());
                            } catch (Exception e) {
                                ProfibusHelper
                                        .openErrorDialog(_site.getShell(),
                                                         "Data Base Error",
                                                         "Device Data Base (DDB) Error\n"
                                                                 + "Can't delete the %1s '%2s' (ID: %3s)",
                                                         fac,
                                                         e);

                                return;
                            }
                        } else if (dbClass instanceof AbstractNodeDBO) {
                            AbstractNodeDBO node = (AbstractNodeDBO) dbClass;
                            parent = node.getParent();
                            parent.removeChild(node);
                            try {
                                parent.save();
                            } catch (PersistenceException e) {
                                ProfibusHelper
                                        .openErrorDialog(_site.getShell(),
                                                         "Data Base Error",
                                                         "Device Data Base (DDB) Error\n"
                                                        +"Can't delete the %1s '%2s' (ID: %3s)",
                                                         node,
                                                         e);
                            }
                        }
                        dbClass = parent;
                    }
                    if (parent != null) {
                        setSelectedNode(new StructuredSelection(parent));
                        refresh(parent);
                        getTreeViewer().setSelection(getSelectedNode(), true);
                    } else {
                        if (dbClass == null) {
                        	refresh();
                        } else {
                        	refresh(dbClass);
                        }
                    }
                    _editNodeAction.run();
                }
            }
        };
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
//                    handlerService.executeCommand(CallNewSiblingNodeEditor.getEditorID(), null);
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
                _copiedNodesReferenceList = getSelectedNode().toList();
                _move = false;
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
                _copiedNodesReferenceList = getSelectedNode().toList();
                _move = true;
            }
        };
        _cutNodeAction.setText("Cut");
        _cutNodeAction.setAccelerator(SWT.CTRL | 'x');
        _cutNodeAction.setToolTipText("Cut this Node");
        _cutNodeAction.setImageDescriptor(getSharedImageDescriptor(ISharedImages.IMG_TOOL_CUT));
    }

    private void makePasteNodeAction() {
        _pasteNodeAction = new Action() {
            @Override
            @SuppressWarnings("static-access")
            public void run() {
                Object firstElement = getSelectedNode().getFirstElement();
                AbstractNodeDBO selectedNode;
                if (firstElement instanceof AbstractNodeDBO) {
                    selectedNode = (AbstractNodeDBO) firstElement;
                } else {
                    return;
                }

                for (AbstractNodeDBO node2Copy : _copiedNodesReferenceList) {
                    if (node2Copy instanceof FacilityDBO) {
                        FacilityDBO copy = (FacilityDBO) selectedNode.copyThisTo(null);
                        //                        FacilityLight facilityLight = new FacilityLight(copy);
                        //                        _load.add(facilityLight);
                        getLoad().add(copy);
                        getViewer().setInput(getLoad());
                        //                        _viewer.setSelection(new StructuredSelection(facilityLight));
                        getViewer().setSelection(new StructuredSelection(copy));

                    } else if (selectedNode.getClass().isInstance(node2Copy.getParent())) {
                        AbstractNodeDBO copy = null;
                        if (_move) {
                            AbstractNodeDBO oldParent = node2Copy.getParent();
                            oldParent.removeChild(node2Copy);
                            AbstractNodeDBO node = selectedNode.getChildrenAsMap().get(node2Copy
                                    .getSortIndex());
                            if (node != null) {
                                int freeStationAddress = selectedNode
                                        .getfirstFreeStationAddress(selectedNode.MAX_STATION_ADDRESS);
                                node2Copy.setSortIndex(freeStationAddress);
                            }
                            selectedNode.addChild(node2Copy);
                            try {
                                selectedNode.save();
                            } catch (PersistenceException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        } else {
                            // paste to a Parent
                            copy = node2Copy.copyThisTo(selectedNode);
                            copy.setDirty(true);
                            copy.setSortIndexNonHibernate(selectedNode
                                    .getfirstFreeStationAddress(copy.MAX_STATION_ADDRESS));
                        }
                        getViewer().refresh();
                        getViewer().setSelection(new StructuredSelection(copy));
                    } else if (selectedNode.getClass().isInstance(node2Copy)) {
                        AbstractNodeDBO nodeCopy = null;
                        if (_move) {
                            AbstractNodeDBO oldParent = node2Copy.getParent();
                            oldParent.removeChild(node2Copy);
                            AbstractNodeDBO parent = selectedNode.getParent();
                            node2Copy.setSortIndex((int)selectedNode.getSortIndex());
                            parent.addChild(node2Copy);
                            try {
                                parent.save();
                            } catch (PersistenceException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
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
                }
            }
        };
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

            public void run() {
                SearchDialog searchDialog = new SearchDialog(getShell(), ProfiBusTreeView.this);
                searchDialog.open();
            }

        };
        _searchAction.setText("Search");
        _searchAction.setToolTipText("Search a Node");
        _searchAction.setImageDescriptor(CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(ActivatorUI.PLUGIN_ID, "icons/search.png"));
    }

    private void makeTreeNodeRenameAction() {

        // Create the editor and set its attributes
        final TreeEditor editor = new TreeEditor(getViewer().getTree());
        editor.horizontalAlignment = SWT.LEFT;
        editor.grabHorizontal = true;
        _doubleClickAction = new Action() {

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

                // If the text field loses focus, set its text into the tree
                // and end the editing session
                text.addFocusListener(new FocusAdapter() {
                    public void focusLost(final FocusEvent event) {
                        text.dispose();
                    }
                });

                /*
                 * If they hit Enter, set the text into the tree and end the editing session. If
                 * they hit Escape, ignore the text and end the editing session.
                 */
                text.addKeyListener(new KeyAdapter() {
                    //TODO: Umstellen auf Editor
                    //                    public void keyPressed(final KeyEvent event) {
                    //                        switch (event.keyCode) {
                    //                            case SWT.CR:
                    //                            case SWT.KEYPAD_CR:
                    //                                // Enter hit--set the text into the tree and drop through
                    //                                String changedText = text.getText();
                    //                                if (node instanceof Channel) {
                    //                                    ((Channel) node).setIoName(changedText);
                    //                                    if (_nodeConfigComposite instanceof ChannelConfigComposite) {
                    //                                        ((ChannelConfigComposite) _nodeConfigComposite)
                    //                                                .setIoNameText(changedText);
                    //                                    }
                    //                                } else {
                    //                                    _nodeConfigComposite.setName(text.getText());
                    //                                }
                    //                                _nodeConfigComposite.store();
                    //                                item.setText(node.toString());
                    //                                text.dispose();
                    //                            case SWT.ESC:
                    //                                // End editing session
                    //                                text.dispose();
                    //                                break;
                    //                            default:
                    //                                break;
                    //                        }
                    //                    }
                });

                // Set the text field into the editor
                editor.setEditor(text, item);
            }
        };

    }

    private void makeRefreshAction() {
        _refreshAction = new Action() {
            @Override
            public void run() {
                refresh();
            }
        };

        _refreshAction.setText("Refresh");
        _refreshAction.setToolTipText("Refresh the Tree");
        _refreshAction.setImageDescriptor(CustomMediaFactory.getInstance()
                .getImageDescriptorFromPlugin(ActivatorUI.PLUGIN_ID, "icons/refresh.gif"));
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
    @SuppressWarnings("unchecked")
    private void setContriebutionActions(final String text,
                                         final Class clazz,
                                         final Class childClazz,
                                         final IMenuManager manager) {
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

    /**
     * @param nc
     */
    private boolean checkDirtyConfig() {//final NodeConfig nc) {
    //        if (nc.isDirty()) {
    //            String[] buttonLabels = new String[] { "&Save", "Don't Save", "&Cancel" };
    //            MessageDialog id = new MessageDialog(getShell(),
    //                                                 "Node not saved!",
    //                                                 null,
    //                                                 "The Node is not saved.\r\n Save now?",
    //                                                 MessageDialog.WARNING,
    //                                                 buttonLabels,
    //                                                 2);
    //            id.setBlockOnOpen(true);
    //            switch (id.open()) {
    //                case Dialog.OK:
    //                    // Persist the node.
    //                    nc.store();
    //                    break;
    //                case Dialog.CANCEL:
    //                    // don't save the actual node and change to the new selected.
    //                    Node node = nc.getNode();
    //                    if ( (node != null) && (node.getId() < 1)) {
    //                        if (node instanceof Facility) {
    //                            _viewer.remove(node);
    //                        } else if (node.getParent() != null) {
    //                            nc.getNode().getParent().removeChild(nc.getNode());
    //                        }
    //                    }
    //                    nc.cancel();
    //                    _editNodeAction.setEnabled(true);
    //                    break;
    //                default:
    //                    _viewer.setSelection(new StructuredSelection(nc), true);
    //                    _editNodeAction.setEnabled(true);
    //                    return false;
    //            }
    //            id.close();
    //        }
        return true;
    }

    private void openEditor(@Nonnull final String editorID) {
        IHandlerService handlerService = (IHandlerService) _site
                .getService(IHandlerService.class);
        AbstractNodeDBO node = null;
        try {
//            ParameterizedCommand cp = createParameterizedCommand(node);
//            handlerService.executeCommand(cp, null);
            handlerService.executeCommand(editorID, null);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(),ex);
        }
    }

    /**
     * @return
     * @throws NotDefinedException
     */
    @Nonnull
    private ParameterizedCommand createParameterizedCommand(@Nonnull final AbstractNodeDBO parent) throws NotDefinedException {
        Command newNodeCommand = getNewNodeCommand();
        IParameter nodeParamter = newNodeCommand.getParameter(PARENT_NODE_ID);
//        Parameterization nodeParameterization = new Parameterization(nodeParamter, String.valueOf(parent.getId()));//parent);
        Parameterization nodeParameterization = new Parameterization(nodeParamter, String.valueOf(parent.getId()));//parent);
        ParameterizedCommand cmd =
            new ParameterizedCommand(newNodeCommand,
                    new Parameterization[] {nodeParameterization});
        return cmd;    }

    /**
     * Returns the new node command.
     *
     * @return the new node command.
     */
    private Command getNewNodeCommand() {
        ICommandService commandService =
            (ICommandService) getSite().getService(ICommandService.class);
        return commandService.getCommand(NEW_NODE_COMMAND_ID);
    }

    private void openInfoDialog() {
        Shell shell = new Shell(getShell(), SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
        Dialog infoDialog = new Dialog(shell) {
            @Override
            protected Control createDialogArea(@Nonnull final Composite parent) {
                Composite createDialogArea = (Composite) super.createDialogArea(parent);
                createDialogArea.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

                createDialogArea.setLayout(GridLayoutFactory.swtDefaults().equalWidth(true)
                        .numColumns(3).create());
                Label label = new Label(createDialogArea, SWT.NONE);
                label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
                label.setText("Nodes: " + NodeMap.getNumberOfNodes());

                label = new Label(createDialogArea, SWT.NONE);
                label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
                // label.setText("ClassCallCount: " + Diagnose.getCounts());

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

                Text text = new Text(createDialogArea, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
                text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));
                // text.setText(Diagnose.getString());

                label = new Label(createDialogArea, SWT.NONE);
                createDialogArea.pack();
                return createDialogArea;
            }
        };
        infoDialog.open();
    }

    private void openNewEmptyChildrenNode() {
        Object node = getSelectedNode().getFirstElement();
        openEditor(CallNewChildrenNodeEditor.getEditorID());
    }

    private void openNewEmptySiblingNode() {
        Object node = getSelectedNode().getFirstElement();
        openEditor(CallNewSiblingNodeEditor.getEditorID());
    }

    /**
     * @return the site
     */
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
    	if(_openNodeEditor!=null && _openNodeEditor.equals(openNodeEditor)) {
    		_openNodeEditor = null;
    	}
    }
    
    @CheckForNull
    public AbstractNodeEditor  getOpenEditor() {
    	return _openNodeEditor;
    }

    protected void setSelectedNode(StructuredSelection selectedNode) {
		_selectedNode = selectedNode;
	}

    protected StructuredSelection getSelectedNode() {
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

	/**
	 * 
	 * TODO (hrickens) : 
	 * 
	 * @author hrickens
	 * @author $Author: $
	 * @since 05.10.2010
	 */
	private final class HibernateDBPreferenceChangeListener implements
			IPreferenceChangeListener {
		
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
		        setLoad(Repository.load(FacilityDBO.class));
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
        public int category(final Object element) {
            return super.category(element);
        }

        @Override
        public int compare(final Viewer viewer, final Object e1, final Object e2) {
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
    class ViewLabelProvider extends ColumnLabelProvider {

        private final Color PROGRAMMABLE_MARKER_COLOR = CustomMediaFactory.getInstance()
                .getColor(255, 140, 0);
        private final Font PROGRAMMABLE_MARKER_FONT = CustomMediaFactory.getInstance()
                .getFont("Tahoma", 8, SWT.ITALIC);

        @Override
        public Color getBackground(final Object element) {
            if (haveProgrammableModule(element)) {
                return PROGRAMMABLE_MARKER_COLOR;
            }
            return null;
        }

        @Override
        public Font getFont(final Object element) {
            if (haveProgrammableModule(element)) {
                return PROGRAMMABLE_MARKER_FONT;
            }
            return null;
        }

        public Image getImage(final Object obj) {
            if (obj instanceof AbstractNodeDBO) {
                AbstractNodeDBO node = (AbstractNodeDBO) obj;
                return ConfigHelper.getImageFromNode(node);
            } else if (obj instanceof FacilityDBO) {
                return ConfigHelper.getImageMaxSize("icons/css.gif", -1, -1);
            }
            return null;
        }

        @Override
        public String getText(final Object element) {
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
        public String getToolTipText(final Object element) {
            if (haveProgrammableModule(element)) {
                return "Is a programmable Module!";
            }
            return null;
        }

        private boolean haveProgrammableModule(final Object element) {
            /* TODO: (hrickens) Das finden von Projekt Document Datein führt teilweise dazu das sich CSS
             * Aufhängt!
             * if (element instanceof Slave) {
             * Slave node = (Slave) element;
             * Set<Document> documents = node.getDocuments();
             * while (documents.iterator().hasNext()) {
             * Document doc = (Document) documents.iterator().next();
             * if (doc.getSubject() != null && doc.getSubject().startsWith("Projekt:")) {
             * return true;
             * }
             * }
             * }
             */
            return false;
        }

    }
}
