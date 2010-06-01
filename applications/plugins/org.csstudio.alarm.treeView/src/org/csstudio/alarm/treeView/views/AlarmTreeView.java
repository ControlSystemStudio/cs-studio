/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN
 * "../AS IS" BASIS. WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN
 * ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS
 * DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS
 * AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE
 * THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.alarm.treeView.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.declaration.AlarmConnectionException;
import org.csstudio.alarm.service.declaration.AlarmTreeLdapConstants;
import org.csstudio.alarm.service.declaration.IAlarmConfigurationService;
import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.alarm.service.declaration.IAlarmConnectionMonitor;
import org.csstudio.alarm.service.declaration.LdapEpicsAlarmCfgObjectClass;
import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.jobs.ImportInitialConfigJob;
import org.csstudio.alarm.treeView.jobs.ImportXmlFileJob;
import org.csstudio.alarm.treeView.ldap.DirectoryEditException;
import org.csstudio.alarm.treeView.ldap.DirectoryEditor;
import org.csstudio.alarm.treeView.model.IAlarmSubtreeNode;
import org.csstudio.alarm.treeView.model.IAlarmTreeNode;
import org.csstudio.alarm.treeView.model.ProcessVariableNode;
import org.csstudio.alarm.treeView.model.Severity;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.csstudio.alarm.treeView.service.AlarmMessageListener;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableNameTransfer;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.DelegatingDragAdapter;
import org.eclipse.jface.util.DelegatingDropAdapter;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.util.TransferDragSourceListener;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
import org.eclipse.ui.progress.PendingUpdateAdapter;

/**
 * Tree view of process variables and their alarm state. This view uses LDAP to get a hierarchy of
 * process variables and displays them in a tree view. Process variables which are in an alarm state
 * are visually marked in the view.
 *
 * @author Joerg Rathlev
 */
public class AlarmTreeView extends ViewPart {

    /**
     * Job change adapter t
     * TODO (bknerr) :
     *
     * @author bknerr
     * @author $Author$
     * @version $Revision$
     * @since 20.05.2010
     */
    private class RefreshAlarmTreeViewAdapter extends JobChangeAdapter {

        private final AlarmTreeView _alarmTreeView;
        private final SubtreeNode _rootNode;

        /**
         * Constructor.
         * @param rootNode
         * @param alarmTreeView TODO
         */
        RefreshAlarmTreeViewAdapter(@Nonnull final AlarmTreeView alarmTreeView,
                                    @Nonnull final SubtreeNode rootNode) {
            _alarmTreeView = alarmTreeView;
            _rootNode = rootNode;
        }

        @Override
        public void done(@Nullable final IJobChangeEvent innerEvent) {

            // TODO jp-mc retrieveInitialStateSynchronously not enabled
            //            _alarmTreeView.retrieveInitialStateSynchronously(_rootNode);

            _alarmTreeView.asyncSetViewerInput(_rootNode); // Display the new tree.

            _alarmListener.setUpdater(new AlarmTreeUpdater(_rootNode));

            _alarmTreeView.getSite().getShell().getDisplay().asyncExec(new Runnable() {
                public void run() {
                    RefreshAlarmTreeViewAdapter.this._alarmTreeView._viewer.refresh();
                }
            });
        }
    }

    /**
     * Monitors the connection to the backend system and displays a message in the tree view if the
     * connection fails. When the connection is established or restored, triggers loading the
     * current state from the LDAP directory.
     */
    private final class AlarmTreeConnectionMonitor implements IAlarmConnectionMonitor {

        public void onConnect() {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    _myMessageArea.hide();

                    // TODO (who?): This rebuilds the whole tree from
                    // scratch. It would be better for the
                    // usability to resynchronize only.
                    startImportInitialConfiguration();
                }
            });
        }

        public void onDisconnect() {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    _myMessageArea
                            .showMessage(SWT.ICON_WARNING,
                                         "Connection error",
                                         "Some or all of the information displayed "
                                                 + "may be outdated. The alarm tree is currently "
                                                 + "not connected to all alarm servers.");
                }
            });
        }

    }

    /**
     * Provides drag support for dragging process variable nodes from the alarm tree using the text
     * transfer.
     */
    private final class AlarmTreeTextDragListener implements TransferDragSourceListener {

        public Transfer getTransfer() {
            return TextTransfer.getInstance();
        }

        public void dragStart(final DragSourceEvent event) {
            final List<IAlarmTreeNode> selectedNodes = selectionToNodeList(_viewer.getSelection());
            event.doit = !selectedNodes.isEmpty() && containsOnlyPVNodes(selectedNodes);
        }

        public void dragSetData(final DragSourceEvent event) {
            final List<IAlarmTreeNode> selectedNodes = selectionToNodeList(_viewer.getSelection());
            final StringBuilder data = new StringBuilder();
            for (final Iterator<IAlarmTreeNode> i = selectedNodes.iterator(); i.hasNext();) {
                final IAlarmTreeNode node = i.next();
                if (node instanceof ProcessVariableNode) {
                    data.append(node.getName());
                    if (i.hasNext()) {
                        data.append(", ");
                    }
                }
            }
            event.data = data.toString();
        }

        public void dragFinished(final DragSourceEvent event) {
            // EMPTY
        }
    }

    /**
     * Provides drag support for dragging process variable nodes from the alarm tree using the
     * process variable name transfer.
     */
    private final class AlarmTreeProcessVariableDragListener implements TransferDragSourceListener {

        public Transfer getTransfer() {
            return ProcessVariableNameTransfer.getInstance();
        }

        public void dragStart(final DragSourceEvent event) {
            final List<IAlarmTreeNode> selectedNodes = selectionToNodeList(_viewer.getSelection());
            event.doit = !selectedNodes.isEmpty() && containsOnlyPVNodes(selectedNodes);
        }

        public void dragSetData(final DragSourceEvent event) {
            final List<IAlarmTreeNode> selectedNodes = selectionToNodeList(_viewer.getSelection());
            event.data = selectedNodes.toArray(new IProcessVariable[selectedNodes.size()]);
        }

        public void dragFinished(final DragSourceEvent event) {
            // EMPTY
        }
    }

    /**
     * Provides drag support for the alarm tree for drag and drop of structural nodes. Drag and drop
     * of structural nodes uses the LocalSelectionTransfer.
     */
    private final class AlarmTreeLocalSelectionDragListener implements TransferDragSourceListener {

        public Transfer getTransfer() {
            return LocalSelectionTransfer.getTransfer();
        }

        public void dragStart(final DragSourceEvent event) {
            final List<IAlarmTreeNode> selectedNodes = selectionToNodeList(_viewer.getSelection());
            event.doit = canDrag(selectedNodes);
            if (event.doit) {
                LocalSelectionTransfer.getTransfer().setSelection(_viewer.getSelection());
            }
        }

        /**
         * Returns whether the given list of nodes can be dragged. The nodes can be dragged if they
         * are all children of the same parent node.
         */
        private boolean canDrag(final List<IAlarmTreeNode> nodes) {
            if (nodes.isEmpty()) {
                return false;
            }
            final IAlarmSubtreeNode firstParent = nodes.get(0).getParent();
            for (final IAlarmTreeNode node : nodes) {
                if (node.getParent() != firstParent) {
                    return false;
                }
            }
            return true;
        }

        public void dragSetData(final DragSourceEvent event) {
            LocalSelectionTransfer.getTransfer().setSelection(_viewer.getSelection());
        }

        public void dragFinished(final DragSourceEvent event) {
            LocalSelectionTransfer.getTransfer().setSelection(null);
        }
    }

    /**
     * Provides support for dropping process variables into the alarm tree.
     */
    private final class AlarmTreeProcessVariableDropListener implements TransferDropTargetListener {

        public Transfer getTransfer() {
            return ProcessVariableNameTransfer.getInstance();
        }

        public boolean isEnabled(final DropTargetEvent event) {
            return dropTargetIsSubtreeNode(event);
        }

        /**
         * Checks if the target of the drop operation is a SubtreeNode.
         */
        private boolean dropTargetIsSubtreeNode(final DropTargetEvent event) {
            return (event.item instanceof TreeItem)
                    && (event.item.getData() instanceof SubtreeNode);
        }

        public void dragEnter(final DropTargetEvent event) {
            // only copy is supported
            event.detail = event.operations & DND.DROP_COPY;
        }

        public void dragOperationChanged(final DropTargetEvent event) {
            // only copy is supported
            event.detail = event.operations & DND.DROP_COPY;
        }

        public void dragLeave(final DropTargetEvent event) {
            // EMPTY
        }

        public void dragOver(final DropTargetEvent event) {
            event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL | DND.FEEDBACK_SELECT;
        }

        public void dropAccept(final DropTargetEvent event) {
            // EMPTY
        }

        public void drop(final DropTargetEvent event) {
            final SubtreeNode parent = (SubtreeNode) event.item.getData();
            final IProcessVariable[] droppedPVs = (IProcessVariable[]) event.data;
            boolean errors = false;
            for (final IProcessVariable pv : droppedPVs) {
                try {
                    DirectoryEditor.createProcessVariableRecord(parent, pv.getName());
                } catch (final DirectoryEditException e) {
                    errors = true;
                }
            }
            _viewer.refresh(parent);
            if (errors) {
                MessageDialog.openError(getSite().getShell(),
                                        "Create New Records",
                                        "One or more of the records could not be created.");
            }
        }
    }

    private final class AlarmTreeLocalSelectionDropListener implements TransferDropTargetListener {

        public Transfer getTransfer() {
            return LocalSelectionTransfer.getTransfer();
        }

        public boolean isEnabled(final DropTargetEvent event) {
            final ISelection selection = LocalSelectionTransfer.getTransfer().getSelection();
            return dropTargetIsSubtreeNode(event) && canDrop(selection, event);
        }

        /**
         * Checks if the target of the drop operation is a SubtreeNode.
         */
        private boolean dropTargetIsSubtreeNode(final DropTargetEvent event) {
            return (event.item instanceof TreeItem)
                    && (event.item.getData() instanceof SubtreeNode);
        }

        /**
         * Checks if the given selection can be dropped into the alarm tree. The selection can be
         * dropped if all of the selected items are alarm tree nodes and the drop target is not one
         * of the nodes or a child of one of the nodes. (The dragged items must also all share a
         * common parent, but this is already checked in the drag listener.)
         */
        private boolean canDrop(final ISelection selection, final DropTargetEvent event) {
            final SubtreeNode dropTarget = (SubtreeNode) event.item.getData();
            if (selection instanceof IStructuredSelection) {
                final IStructuredSelection s = (IStructuredSelection) selection;
                for (final Iterator<?> i = s.iterator(); i.hasNext();) {
                    final Object o = i.next();
                    if (o instanceof IAlarmTreeNode) {
                        if ( (o == dropTarget) || isChild(dropTarget, (IAlarmTreeNode) o)) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            } else {
                return false;
            }
            return true;
        }

        /**
         * Returns whether the first node is a direct or indirect child of the second node.
         */
        private boolean isChild(final IAlarmSubtreeNode directParent2, final IAlarmTreeNode parent) {
            final IAlarmSubtreeNode directParent = directParent2.getParent();
            if (directParent == null) {
                return false;
            }
            if (directParent == parent) {
                return true;
            }
            return isChild(directParent, parent);
        }

        public void dragEnter(final DropTargetEvent event) {
            // EMPTY
        }

        public void dragOperationChanged(final DropTargetEvent event) {
            // EMPTY
        }

        public void dragLeave(final DropTargetEvent event) {
            // EMPTY
        }

        public void dragOver(final DropTargetEvent event) {
            event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL | DND.FEEDBACK_SELECT;
        }

        public void dropAccept(final DropTargetEvent event) {
            // EMPTY
        }

        public void drop(final DropTargetEvent event) {
            final SubtreeNode dropTarget = (SubtreeNode) event.item.getData();
            final List<IAlarmTreeNode> droppedNodes = selectionToNodeList(LocalSelectionTransfer
                    .getTransfer().getSelection());
            if (event.detail == DND.DROP_COPY) {
                try {
                    copyNodes(droppedNodes, dropTarget);
                } catch (final DirectoryEditException e) {
                    MessageDialog.openError(getSite().getShell(),
                                            "Copying Nodes",
                                            "An error occured. The nodes could not be copied.");
                }
            } else if (event.detail == DND.DROP_MOVE) {
                try {
                    moveNodes(droppedNodes, dropTarget);
                } catch (final DirectoryEditException e) {
                    MessageDialog.openError(getSite().getShell(),
                                            "Moving Nodes",
                                            "An error occured. The nodes could not be moved.");
                }
            }
            _viewer.refresh();
        }

        private void copyNodes(final List<IAlarmTreeNode> nodes, final SubtreeNode target) throws DirectoryEditException {
            for (final IAlarmTreeNode node : nodes) {
                DirectoryEditor.copyNode(node, target);
            }
        }

        private void moveNodes(final List<IAlarmTreeNode> nodes, final SubtreeNode target) throws DirectoryEditException {
            for (final IAlarmTreeNode node : nodes) {
                DirectoryEditor.moveNode(node, target);
            }
        }
    }

    /**
     * The ID of this view.
     */
    private static final String ID = "org.csstudio.alarm.treeView.views.AlarmTreeView";

    private final IAlarmConfigurationService _configService = AlarmTreePlugin.getDefault()
            .getAlarmConfigurationService();

    /**
     * The tree viewer that displays the alarm objects.
     */
    TreeViewer _viewer;

    /**
     * The message area above the tree viewer
     */
    private MessageArea _myMessageArea;

    /**
     * The subscriber to the alarm topic.
     */
    private IAlarmConnection _connection;

    /**
     * The callback for the alarm messages
     */
    private AlarmMessageListener _alarmListener;

    /**
     * The reload action.
     */
    private Action _reloadAction;

    /**
     * The import xml file action.
     */
    private Action _importXmlFileAction;

    /**
     * The acknowledge action.
     */
    private Action _acknowledgeAction;

    /**
     * The Run CSS Alarm Display action.
     */
    private Action _runCssAlarmDisplayAction;

    /**
     * The Run CSS Display action.
     */
    private Action _runCssDisplayAction;

    /**
     * The Open CSS Strip Chart action.
     */
    private Action _openCssStripChartAction;

    /**
     * The Show Help Page action.
     */
    private Action _showHelpPageAction;

    /**
     * The Show Help Guidance action.
     */
    private Action _showHelpGuidanceAction;

    /**
     * The Create Record action.
     */
    private Action _createRecordAction;

    /**
     * The Create Component action.
     */
    private Action _createComponentAction;

    /**
     * The Rename action.
     */
    private Action _renameAction;

    /**
     * The Delete action.
     */
    private Action _deleteNodeAction;

    /**
     * The Show Property View action.
     */
    private Action _showPropertyViewAction;

    /**
     * A filter which hides all nodes which are not currently in an alarm state.
     */
    private ViewerFilter _currentAlarmFilter;

    /**
     * The action which toggles the filter on and off.
     */
    private Action _toggleFilterAction;

    /**
     * Saves the currently configured alarm tree as xml file.
     */
    private Action _saveAsXmlFileAction;

    /**
     * Whether the filter is active.
     */
    private Boolean _isFilterActive = Boolean.FALSE;

    /**
     * The logger used by this view.
     */
    static final Logger LOG = CentralLogger.getInstance().getLogger(AlarmTreeView.class);

    /**
     * Converts a selection into a list of selected alarm tree nodes.
     */
    private static List<IAlarmTreeNode> selectionToNodeList(final ISelection selection) {
        final List<IAlarmTreeNode> result = new ArrayList<IAlarmTreeNode>();
        if (selection instanceof IStructuredSelection) {
            final IStructuredSelection s = (IStructuredSelection) selection;
            for (final Iterator<?> i = s.iterator(); i.hasNext();) {
                result.add((IAlarmTreeNode) i.next());
            }
        }
        return result;
    }

    /**
     * Returns whether a list of nodes contains only ProcessVariableNodes.
     */
    private static boolean containsOnlyPVNodes(final List<IAlarmTreeNode> nodes) {
        for (final IAlarmTreeNode node : nodes) {
            if (! (node instanceof ProcessVariableNode)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the id of this view.
     *
     * @return the id of this view.
     */
    public static String getID() {
        return ID;
    }

    /**
     * Creates an LDAP tree viewer.
     */
    public AlarmTreeView() {
        // Empty
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void createPartControl(@Nullable final Composite parent) {

        final GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        parent.setLayout(layout);

        _myMessageArea = new MessageArea(parent);

        _viewer = new TreeViewer(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        _viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        _viewer.setContentProvider(new AlarmTreeContentProvider());
        _viewer.setLabelProvider(new AlarmTreeLabelProvider());
        _viewer.setComparator(new ViewerComparator());

        _currentAlarmFilter = new CurrentAlarmFilter();

        _alarmListener = new AlarmMessageListener();

        initializeContextMenu();

        makeActions(_viewer, _alarmListener, getSite(), _currentAlarmFilter);

        contributeToActionBars();

        getSite().setSelectionProvider(_viewer);

        startConnection();

        _viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(final SelectionChangedEvent event) {
                AlarmTreeView.this.selectionChanged(event);
            }
        });

        addDragAndDropSupport();
    }

    /**
     * Starts the connection.
     */
    private void startConnection() {
        LOG.debug("Starting connection.");

        if (_connection != null) {
            // There is still an old connection. This shouldn't happen.
            _connection.disconnect();
            LOG.warn("There was an active connection when starting a new connection");
        }

        final IWorkbenchSiteProgressService progressService = (IWorkbenchSiteProgressService) getSite()
                .getAdapter(IWorkbenchSiteProgressService.class);
        final Job connectionJob = new Job("Connecting via alarm service") {

            @Override
            protected IStatus run(final IProgressMonitor monitor) {
                monitor.beginTask("Connecting via alarm service", IProgressMonitor.UNKNOWN);
                _connection = AlarmTreePlugin.getDefault().getAlarmService().newAlarmConnection();
                try {
                    _connection.connectWithListener(new AlarmTreeConnectionMonitor(),
                                                    _alarmListener,
                                                    "c:\\alarmConfig.xml");
                } catch (final AlarmConnectionException e) {
                    throw new RuntimeException("Could not connect via alarm service", e);
                }
                return Status.OK_STATUS;
            }
        };
        progressService.schedule(connectionJob, 0, true);
    }

    /**
     * Adds drag and drop support to the tree viewer.
     */
    private void addDragAndDropSupport() {
        final DelegatingDropAdapter dropAdapter = new DelegatingDropAdapter();
        dropAdapter.addDropTargetListener(new AlarmTreeLocalSelectionDropListener());
        dropAdapter.addDropTargetListener(new AlarmTreeProcessVariableDropListener());
        _viewer.addDropSupport(DND.DROP_COPY | DND.DROP_MOVE,
                               dropAdapter.getTransfers(),
                               dropAdapter);

        final DelegatingDragAdapter dragAdapter = new DelegatingDragAdapter();
        dragAdapter.addDragSourceListener(new AlarmTreeLocalSelectionDragListener());
        dragAdapter.addDragSourceListener(new AlarmTreeProcessVariableDragListener());
        dragAdapter.addDragSourceListener(new AlarmTreeTextDragListener());
        _viewer.addDragSupport(DND.DROP_COPY | DND.DROP_MOVE,
                               dragAdapter.getTransfers(),
                               dragAdapter);
    }

    /**
     * Starts a job which reads the contents of the directory in the background.
     */
    private void startImportInitialConfiguration() {
        LOG.debug("Starting directory reader.");
        final IWorkbenchSiteProgressService progressService = (IWorkbenchSiteProgressService) getSite()
                .getAdapter(IWorkbenchSiteProgressService.class);

        final Job importInitialConfigJob = createImportInitialConfigJob();

        // Set the tree to which updates are applied to null. This means updates
        // will be queued for later application.
        _alarmListener.setUpdater(null);

        // The directory is read in the background. Until then, set the viewer's
        // input to a placeholder object.
        _viewer.setInput(new Object[] { new PendingUpdateAdapter() });

        // Start the directory reader job.
        progressService.schedule(importInitialConfigJob, 0, true);
    }

    @Nonnull
    private ImportXmlFileJob createImportXmlFileJob() {
        final SubtreeNode rootNode = new SubtreeNode.Builder(AlarmTreeLdapConstants.EPICS_ALARM_CFG_FIELD_VALUE,
                                                             LdapEpicsAlarmCfgObjectClass.ROOT).build();

        final ImportXmlFileJob importXmlFileJob = new ImportXmlFileJob("importXmlFileJob",
                                                                       _configService,
                                                                       rootNode);
        importXmlFileJob.addJobChangeListener(new RefreshAlarmTreeViewAdapter(this, rootNode));

        return importXmlFileJob;
    }

    @Nonnull
    private Job createImportInitialConfigJob() {

        final SubtreeNode rootNode = new SubtreeNode.Builder(AlarmTreeLdapConstants.EPICS_ALARM_CFG_FIELD_VALUE,
                                                             LdapEpicsAlarmCfgObjectClass.ROOT)
                .build();

        final Job importInitConfigJob = new ImportInitialConfigJob(this, rootNode, _configService);

        importInitConfigJob.addJobChangeListener(new RefreshAlarmTreeViewAdapter(this, rootNode));

        return importInitConfigJob;
    }

    /**
     * Sets the input for the tree. The actual work will be done asynchronously in the UI thread.
     *
     * @param inputElement the new input element.
     */
    void asyncSetViewerInput(@Nonnull final SubtreeNode inputElement) {
        getSite().getShell().getDisplay().asyncExec(new Runnable() {
            public void run() {
                _viewer.setInput(inputElement);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void dispose() {
        _connection.disconnect();
        super.dispose();
    }

    /**
     * Called when the selection of the tree changes.
     *
     * @param event the selection event.
     */
    private void selectionChanged(@Nonnull final SelectionChangedEvent event) {
        final IStructuredSelection sel = (IStructuredSelection) event.getSelection();
        _acknowledgeAction.setEnabled(containsNodeWithUnackAlarm(sel));
        _runCssAlarmDisplayAction.setEnabled(hasCssAlarmDisplay(sel.getFirstElement()));
        _runCssDisplayAction.setEnabled(hasCssDisplay(sel.getFirstElement()));
        _openCssStripChartAction.setEnabled(hasCssStripChart(sel.getFirstElement()));
        _showHelpGuidanceAction.setEnabled(hasHelpGuidance(sel.getFirstElement()));
        _showHelpPageAction.setEnabled(hasHelpPage(sel.getFirstElement()));
    }

    /**
     * Returns whether the given node has a CSS strip chart.
     *
     * @param node the node.
     * @return <code>true</code> if the node has a strip chart, <code>false</code> otherwise.
     */
    private boolean hasCssStripChart(@Nonnull final Object node) {
        if (node instanceof IAlarmTreeNode) {
            return ((IAlarmTreeNode) node).getCssStripChart() != null;
        }
        return false;
    }

    /**
     * Returns whether the given node has a CSS display.
     *
     * @param node the node.
     * @return <code>true</code> if the node has a display, <code>false</code> otherwise.
     */
    private boolean hasCssDisplay(@Nonnull final Object node) {
        if (node instanceof IAlarmTreeNode) {
            return ((IAlarmTreeNode) node).getCssDisplay() != null;
        }
        return false;
    }

    /**
     * Return whether help guidance is available for the given node.
     *
     * @param node the node.
     * @return <code>true</code> if the node has a help guidance string, <code>false</code>
     *         otherwise.
     */
    private boolean hasHelpGuidance(@Nonnull final Object node) {
        if (node instanceof IAlarmTreeNode) {
            return ((IAlarmTreeNode) node).getHelpGuidance() != null;
        }
        return false;
    }

    /**
     * Return whether the given node has an associated help page.
     *
     * @param node the node.
     * @return <code>true</code> if the node has an associated help page, <code>false</code>
     *         otherwise.
     */
    private boolean hasHelpPage(@Nonnull final Object node) {
        if (node instanceof IAlarmTreeNode) {
            return ((IAlarmTreeNode) node).getHelpPage() != null;
        }
        return false;
    }

    /**
     * Returns whether the given process variable node in the tree has an associated CSS alarm
     * display configured.
     *
     * @param node the node.
     * @return <code>true</code> if a CSS alarm display is configured for the node,
     *         <code>false</code> otherwise.
     */
    private boolean hasCssAlarmDisplay(@Nonnull final Object node) {
        if (node instanceof IAlarmTreeNode) {
            final String display = ((IAlarmTreeNode) node).getCssAlarmDisplay();
            return (display != null) && display.matches(".+\\.css-sds");
        }
        return false;
    }

    /**
     * Returns whether the given selection contains at least one node with an unacknowledged alarm.
     *
     * @param sel the selection.
     * @return <code>true</code> if the selection contains a node with an unacknowledged alarm,
     *         <code>false</code> otherwise.
     */
    private boolean containsNodeWithUnackAlarm(@Nonnull final IStructuredSelection sel) {
        final Object selectedElement = sel.getFirstElement();
        // Note: selectedElement is not instance of IAlarmTreeNode if nothing
        // is selected (selectedElement == null), and during initialization,
        // when it is an instance of PendingUpdateAdapter.
        if (selectedElement instanceof IAlarmTreeNode) {
            return ((IAlarmTreeNode) selectedElement).getUnacknowledgedAlarmSeverity() != Severity.NO_ALARM;
        }
        return false;
    }

    /**
     * Adds a context menu to the tree view.
     */
    private void initializeContextMenu() {
        final MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);

        // add menu items to the context menu when it is about to show
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(@Nullable final IMenuManager manager) {
                AlarmTreeView.this.fillContextMenu(manager);
            }
        });

        // add the context menu to the tree viewer
        final Menu contextMenu = menuMgr.createContextMenu(_viewer.getTree());
        _viewer.getTree().setMenu(contextMenu);

        // register the context menu for extension by other plug-ins
        getSite().registerContextMenu(menuMgr, _viewer);
    }

    /**
     * Adds tool buttons and menu items to the action bar of this view.
     */
    private void contributeToActionBars() {
        final IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    /**
     * Adds the actions for the action bar's pull down menu.
     *
     * @param manager the menu manager.
     */
    private void fillLocalPullDown(@Nonnull final IMenuManager manager) {
        // EMPTY
    }

    /**
     * Adds the context menu actions.
     *
     * @param menuManager the menu manager.
     */
    private void fillContextMenu(@Nullable final IMenuManager menuManager) {
        if (menuManager == null) {
            MessageDialog
                    .openError(getSite().getShell(),
                               "Context menu",
                               "Inernal error occurred when trying to open the context menu (IMenuManager is null).");
            return;
        }

        final IStructuredSelection selection = (IStructuredSelection) _viewer.getSelection();
        if (selection.size() > 0) {
            menuManager.add(_acknowledgeAction);
        }
        if (selection.size() == 1) {
            menuManager.add(_runCssAlarmDisplayAction);
            menuManager.add(_runCssDisplayAction);
            menuManager.add(_openCssStripChartAction);
            menuManager.add(_showHelpGuidanceAction);
            menuManager.add(_showHelpPageAction);
            menuManager.add(new Separator("edit"));
            menuManager.add(_renameAction);
            menuManager.add(_deleteNodeAction);

            final Object firstElement = selection.getFirstElement();
            if (firstElement instanceof SubtreeNode) {
                menuManager.add(_createRecordAction);
                menuManager.add(_createComponentAction);

                final LdapEpicsAlarmCfgObjectClass oc = ((SubtreeNode) firstElement)
                        .getObjectClass();
                if (LdapEpicsAlarmCfgObjectClass.FACILITY.equals(oc)) {
                    menuManager.add(_saveAsXmlFileAction);
                }
            }
        }

        // adds a separator after which contributed actions from other plug-ins
        // will be displayed
        menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    /**
     * Adds the tool bar actions.
     *
     * @param manager the menu manager.
     */
    private void fillLocalToolBar(@Nonnull final IToolBarManager manager) {
        manager.add(_toggleFilterAction);
        manager.add(new Separator());
        manager.add(_showPropertyViewAction);
        manager.add(_reloadAction);
        manager.add(_importXmlFileAction);
    }

    /**
     * Creates the actions offered by this view.
     * @param iWorkbenchPartSite
     * @param alarmListener
     * @param viewer
     * @param currentAlarmFilter
     */
    private void makeActions(@Nonnull final TreeViewer viewer,
                             @Nonnull final AlarmMessageListener alarmListener,
                             @Nonnull final IWorkbenchPartSite site,
                             @Nonnull final ViewerFilter currentAlarmFilter) {

        _reloadAction = AlarmTreeViewActionFactory
                .createReloadAction(createImportInitialConfigJob(), site, alarmListener, viewer);

        _importXmlFileAction = AlarmTreeViewActionFactory
                .createImportXmlFileAction(createImportXmlFileJob(), site, alarmListener, viewer);

        _acknowledgeAction = AlarmTreeViewActionFactory.createAcknowledgeAction(viewer);

        _runCssAlarmDisplayAction = AlarmTreeViewActionFactory.createCssAlarmDisplayAction(viewer);

        _runCssDisplayAction = AlarmTreeViewActionFactory.createRunCssDisplayAction(viewer);

        _openCssStripChartAction = AlarmTreeViewActionFactory.createCssStripChartAction(site,
                                                                                        viewer);

        _showHelpGuidanceAction = AlarmTreeViewActionFactory.createShowHelpGuidanceAction(site,
                                                                                          viewer);

        _showHelpPageAction = AlarmTreeViewActionFactory.createShowHelpPageAction(viewer);

        _createRecordAction = AlarmTreeViewActionFactory.createCreateRecordAction(site, viewer);

        _createComponentAction = AlarmTreeViewActionFactory.createCreateComponentAction(site,
                                                                                        viewer);

        _renameAction = AlarmTreeViewActionFactory.createRenameAction(site, viewer);

        _deleteNodeAction = AlarmTreeViewActionFactory.createDeleteNodeAction(site, viewer);

        _showPropertyViewAction = AlarmTreeViewActionFactory.createShowPropertyViewAction(site);

        _toggleFilterAction = AlarmTreeViewActionFactory
                .createToggleFilterAction(this, viewer, currentAlarmFilter);

        _saveAsXmlFileAction = AlarmTreeViewActionFactory.createSaveAsXmlFileAction(site, viewer);

    }

    @Nonnull
    public Boolean getIsFilterActive() {
        return _isFilterActive;
    }

    public void setIsFilterActive(@Nonnull final Boolean isFilterActive) {
        _isFilterActive = isFilterActive;
    }

    /**
     * Passes the focus request to the viewer's control.
     */
    @Override
    public final void setFocus() {
        _viewer.getControl().setFocus();
    }

    /**
     * Refreshes this view.
     */
    public final void refresh() {
        _viewer.refresh();
    }

    /**
     * Encapsulation of the message area. It is located below the tree view.
     */
    private static final class MessageArea {
        /**
         * The message area which can display error messages inside the view part.
         */
        private final Composite _messageArea;

        /**
         * The icon displayed in the message area.
         */
        private final Label _messageAreaIcon;

        /**
         * The message displayed in the message area.
         */
        private final Label _messageAreaMessage;

        /**
         * The description displayed in the message area.
         */
        private final Label _messageAreaDescription;

        public MessageArea(@Nonnull final Composite parent) {
            _messageArea = new Composite(parent, SWT.NONE);
            final GridData messageAreaLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
            messageAreaLayoutData.exclude = true;
            _messageArea.setVisible(false);
            _messageArea.setLayoutData(messageAreaLayoutData);
            _messageArea.setLayout(new GridLayout(2, false));

            _messageAreaIcon = new Label(_messageArea, SWT.NONE);
            _messageAreaIcon.setLayoutData(new GridData(SWT.BEGINNING,
                                                        SWT.BEGINNING,
                                                        false,
                                                        false,
                                                        1,
                                                        2));
            _messageAreaIcon.setImage(Display.getCurrent().getSystemImage(SWT.ICON_WARNING));

            _messageAreaMessage = new Label(_messageArea, SWT.WRAP);
            _messageAreaMessage.setText("Test message");
            // Be careful if changing the GridData below! The label will not wrap
            // correctly for some settings.
            _messageAreaMessage.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

            _messageAreaDescription = new Label(_messageArea, SWT.WRAP);
            _messageAreaDescription.setText("This is an explanation of the test message.");
            // Be careful if changing the GridData below! The label will not wrap
            // correctly for some settings.
            _messageAreaDescription
                    .setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        }

        /**
         * Sets the message displayed in the message area of this view part.
         *
         * @param icon the icon to be displayed next to the message. Must be one of
         *            <code>SWT.ICON_ERROR</code>, <code>SWT.ICON_INFORMATION</code>,
         *            <code>SWT.ICON_WARNING</code>, <code>SWT.ICON_QUESTION</code>.
         * @param message the message.
         * @param description a descriptive text.
         */
        public void showMessage(final int icon, @Nonnull final String message, @Nonnull final String description) {
            _messageAreaIcon.setImage(Display.getCurrent().getSystemImage(icon));
            _messageAreaMessage.setText(message);
            _messageAreaDescription.setText(description);
            _messageArea.layout();

            _messageArea.setVisible(true);
            ((GridData) _messageArea.getLayoutData()).exclude = false;
            _messageArea.getParent().layout();
        }

        /**
         * Hides the message displayed in this view part.
         */
        public void hide() {
            _messageArea.setVisible(false);
            ((GridData) _messageArea.getLayoutData()).exclude = true;
            _messageArea.getParent().layout();
        }

    }
}
