/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
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
package org.csstudio.alarm.treeView.views;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.declaration.AlarmConnectionException;
import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.alarm.service.declaration.IAlarmConnectionMonitor;
import org.csstudio.alarm.table.SendAcknowledge;
import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.ldap.AlarmTreeBuilder;
import org.csstudio.alarm.treeView.ldap.DirectoryEditException;
import org.csstudio.alarm.treeView.ldap.DirectoryEditor;
import org.csstudio.alarm.treeView.ldap.UpdateTreeLdapReader;
import org.csstudio.alarm.treeView.model.IAlarmTreeNode;
import org.csstudio.alarm.treeView.model.LdapObjectClass;
import org.csstudio.alarm.treeView.model.ProcessVariableNode;
import org.csstudio.alarm.treeView.model.Severity;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.csstudio.alarm.treeView.service.AlarmMessageListener;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.platform.ui.internal.dataexchange.ProcessVariableNameTransfer;
import org.csstudio.platform.ui.util.EditorUtil;
import org.csstudio.sds.ui.runmode.RunModeService;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
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
import org.eclipse.jface.window.Window;
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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
import org.eclipse.ui.progress.PendingUpdateAdapter;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;

/**
 * Tree view of process variables and their alarm state. This view uses LDAP to get a hierarchy of
 * process variables and displays them in a tree view. Process variables which are in an alarm state
 * are visually marked in the view.
 *
 * @author Joerg Rathlev
 */
public class AlarmTreeView extends ViewPart {

    /**
     * Validates a node name.
     */
    private final class NodeNameInputValidator implements IInputValidator {
        public String isValid(final String newText) {
            if (newText.equals("")) {
                return "Please enter a name.";
            } else if (newText.matches("^\\s.*") || newText.matches(".*\\s$")) {
                return "The name cannot begin or end with whitespace.";
            } else {
                return null; // input is valid
            }
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
                    hideMessage();

                    // TODO: This rebuilds the whole tree from
                    // scratch. It would be better for the
                    // usability to resynchronize only.
                    startDirectoryReaderJob();
                }
            });
        }

        public void onDisconnect() {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    setMessage(SWT.ICON_WARNING,
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
            final SubtreeNode firstParent = nodes.get(0).getParent();
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
        }

        public void dragOver(final DropTargetEvent event) {
            event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL | DND.FEEDBACK_SELECT;
        }

        public void dropAccept(final DropTargetEvent event) {
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
                        if ((o == dropTarget) || isChild(dropTarget, (IAlarmTreeNode) o)) {
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
        private boolean isChild(final SubtreeNode child, final IAlarmTreeNode parent) {
            final SubtreeNode directParent = child.getParent();
            if (directParent == null) {
                return false;
            }
            if (directParent == parent) {
                return true;
            } else {
                return isChild(directParent, parent);
            }
        }

        public void dragEnter(final DropTargetEvent event) {
        }

        public void dragOperationChanged(final DropTargetEvent event) {
        }

        public void dragLeave(final DropTargetEvent event) {
        }

        public void dragOver(final DropTargetEvent event) {
            event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL | DND.FEEDBACK_SELECT;
        }

        public void dropAccept(final DropTargetEvent event) {
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
     * This action opens the strip chart associated with the selected node.
     */
    private class OpenStripChartAction extends Action {

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            final IAlarmTreeNode node = getSelectedNode();
            if (node != null) {
                final IPath path = new Path(node.getCssStripChart());

                // The following code assumes that the path is relative to
                // the Eclipse workspace.
                final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
                final IWorkbenchPage page = getSite().getPage();
                try {
                    EditorUtil.openEditor(page, file);
                } catch (final PartInitException e) {
                    MessageDialog.openError(getSite().getShell(), "Alarm Tree", e.getMessage());
                }
            }
        }

        /**
         * Returns the node that is currently selected in the tree.
         *
         * @return the selected node, or <code>null</code> if the selection is empty or the selected
         *         node is not of type <code>IAlarmTreeNode</code>.
         */
        private IAlarmTreeNode getSelectedNode() {
            final IStructuredSelection selection = (IStructuredSelection) _viewer.getSelection();
            final Object selected = selection.getFirstElement();
            if (selected instanceof IAlarmTreeNode) {
                return (IAlarmTreeNode) selected;
            }
            return null;
        }
    }

    /**
     * The ID of this view.
     */
    private static final String ID = "org.csstudio.alarm.treeView.views.LdapTView";

    /**
     * The ID of the property view.
     */
    private static final String PROPERTY_VIEW_ID = "org.eclipse.ui.views.PropertySheet";

    /**
     * The tree viewer that displays the alarm objects.
     */
    private TreeViewer _viewer;

    /**
     * The reload action.
     */
    private Action _reloadAction;

    /**
     * The subscriber to the alarm topic.
     */
    private IAlarmConnection _connection;
    private AlarmMessageListener _alarmListener;

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
     * The message area which can display error messages inside the view part.
     */
    private Composite _messageArea;

    /**
     * The icon displayed in the message area.
     */
    private Label _messageAreaIcon;

    /**
     * The message displayed in the message area.
     */
    private Label _messageAreaMessage;

    /**
     * The description displayed in the message area.
     */
    private Label _messageAreaDescription;

    /**
     * A filter which hides all nodes which are not currently in an alarm state.
     */
    private ViewerFilter _currentAlarmFilter;

    /**
     * The action which toggles the filter on and off.
     */
    private Action _toggleFilterAction;

    /**
     * Whether the filter is active.
     */
    private boolean _isFilterActive;

    /**
     * The logger used by this view.
     */
    private final Logger _log = CentralLogger.getInstance().getLogger(this);

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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void createPartControl(final Composite parent) {
        final GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        parent.setLayout(layout);

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
        _messageAreaDescription.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

        _viewer = new TreeViewer(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        _viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        _viewer.setContentProvider(new AlarmTreeContentProvider());
        _viewer.setLabelProvider(new AlarmTreeLabelProvider());
        _viewer.setComparator(new ViewerComparator());

        _currentAlarmFilter = new CurrentAlarmFilter();

        initializeContextMenu();
        makeActions();
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
        _log.debug("Starting connection.");

        if (_connection != null) {
            // There is still an old connection. This shouldn't happen.
            _connection.disconnect();
            _log.warn("There was an active connection when starting a new connection");
        }

        final IWorkbenchSiteProgressService progressService = (IWorkbenchSiteProgressService) getSite()
                .getAdapter(IWorkbenchSiteProgressService.class);
        final Job connectionJob = new Job("Connecting via alarm service") {

            @Override
            protected IStatus run(final IProgressMonitor monitor) {
                monitor.beginTask("Connecting via alarm service", IProgressMonitor.UNKNOWN);
                _connection = AlarmTreePlugin.getDefault().getAlarmService().newAlarmConnection();
                _alarmListener = new AlarmMessageListener();

                try {
                    _connection.connectWithListener(new AlarmTreeConnectionMonitor(),
                                                    _alarmListener);
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
    private void startDirectoryReaderJob() {
        _log.debug("Starting directory reader.");
        final IWorkbenchSiteProgressService progressService = (IWorkbenchSiteProgressService) getSite()
                .getAdapter(IWorkbenchSiteProgressService.class);
        final SubtreeNode rootNode = new SubtreeNode("ROOT");

        final Job directoryReaderJob = new Job("LDAPDirectoryReader") {
            @Override
            protected IStatus run(final IProgressMonitor monitor) {
                monitor.beginTask("Initializing Alarm Tree", IProgressMonitor.UNKNOWN);

                try {
                    final long startTime = System.currentTimeMillis();

                    final boolean canceled = AlarmTreeBuilder.build(rootNode, monitor);
                    if (canceled) {
                        return Status.CANCEL_STATUS;
                    }

                    final long endTime = System.currentTimeMillis();
                    _log.debug("Directory reader time: " + (endTime - startTime) + "ms");
                } catch (final NamingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    monitor.done();
                }
                return Status.OK_STATUS;
            }
        };


        directoryReaderJob.addJobChangeListener(
            new JobChangeAdapter() {
                @Override
                public void done(final IJobChangeEvent event) {
                    asyncSetViewerInput(rootNode); // Display the new tree.
                    final Job directoryUpdater = new UpdateTreeLdapReader(rootNode);
                    directoryUpdater.addJobChangeListener(new JobChangeAdapter() {
                        @Override
                        public void done(final IJobChangeEvent innerEvent) {
                            _alarmListener.setUpdater(new AlarmTreeUpdater(rootNode)); // Apply updates to the new tree
                            getSite().getShell().getDisplay().asyncExec(new Runnable() {
                                                                            public void run() {
                                                                                _viewer.refresh();
                                                                            }
                                                                        }
                            );
                        }
                    });
                    progressService.schedule(directoryUpdater, 0, true);
                }
            }
        );

        // Set the tree to which updates are applied to null. This means updates
        // will be queued for later application.
        _alarmListener.setUpdater(null);

        // The directory is read in the background. Until then, set the viewer's
        // input to a placeholder object.
        _viewer.setInput(new Object[] { new PendingUpdateAdapter() });

        // Start the directory reader job.
        progressService.schedule(directoryReaderJob, 0, true);
    }

    /**
     * Sets the message displayed in the message area of this view part.
     *
     * @param icon
     *            the icon to be displayed next to the message. Must be one of
     *            <code>SWT.ICON_ERROR</code>, <code>SWT.ICON_INFORMATION</code>,
     *            <code>SWT.ICON_WARNING</code>, <code>SWT.ICON_QUESTION</code>.
     * @param message
     *            the message.
     * @param description
     *            a descriptive text.
     */
    private void setMessage(final int icon, final String message, final String description) {
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
    private void hideMessage() {
        _messageArea.setVisible(false);
        ((GridData) _messageArea.getLayoutData()).exclude = true;
        _messageArea.getParent().layout();
    }

    /**
     * Sets the input for the tree. The actual work will be done asynchronously in the UI thread.
     *
     * @param inputElement
     *            the new input element.
     */
    private void asyncSetViewerInput(final SubtreeNode inputElement) {
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
     * @param event
     *            the selection event.
     */
    private void selectionChanged(final SelectionChangedEvent event) {
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
     * @param node
     *            the node.
     * @return <code>true</code> if the node has a strip chart, <code>false</code> otherwise.
     */
    private boolean hasCssStripChart(final Object node) {
        if (node instanceof IAlarmTreeNode) {
            return ((IAlarmTreeNode) node).getCssStripChart() != null;
        }
        return false;
    }

    /**
     * Returns whether the given node has a CSS display.
     *
     * @param node
     *            the node.
     * @return <code>true</code> if the node has a display, <code>false</code> otherwise.
     */
    private boolean hasCssDisplay(final Object node) {
        if (node instanceof IAlarmTreeNode) {
            return ((IAlarmTreeNode) node).getCssDisplay() != null;
        }
        return false;
    }

    /**
     * Return whether help guidance is available for the given node.
     *
     * @param node
     *            the node.
     * @return <code>true</code> if the node has a help guidance string, <code>false</code>
     *         otherwise.
     */
    private boolean hasHelpGuidance(final Object node) {
        if (node instanceof IAlarmTreeNode) {
            return ((IAlarmTreeNode) node).getHelpGuidance() != null;
        }
        return false;
    }

    /**
     * Return whether the given node has an associated help page.
     *
     * @param node
     *            the node.
     * @return <code>true</code> if the node has an associated help page, <code>false</code>
     *         otherwise.
     */
    private boolean hasHelpPage(final Object node) {
        if (node instanceof IAlarmTreeNode) {
            return ((IAlarmTreeNode) node).getHelpPage() != null;
        }
        return false;
    }

    /**
     * Returns whether the given process variable node in the tree has an associated CSS alarm
     * display configured.
     *
     * @param node
     *            the node.
     * @return <code>true</code> if a CSS alarm display is configured for the node,
     *         <code>false</code> otherwise.
     */
    private boolean hasCssAlarmDisplay(final Object node) {
        if (node instanceof IAlarmTreeNode) {
            final String display = ((IAlarmTreeNode) node).getCssAlarmDisplay();
            return (display != null) && display.matches(".+\\.css-sds");
        }
        return false;
    }

    /**
     * Returns whether the given selection contains at least one node with an unacknowledged alarm.
     *
     * @param sel
     *            the selection.
     * @return <code>true</code> if the selection contains a node with an unacknowledged alarm,
     *         <code>false</code> otherwise.
     */
    private boolean containsNodeWithUnackAlarm(final IStructuredSelection sel) {
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
            public void menuAboutToShow(final IMenuManager manager) {
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
     * @param manager
     *            the menu manager.
     */
    private void fillLocalPullDown(final IMenuManager manager) {
    }

    /**
     * Adds the context menu actions.
     *
     * @param menu
     *            the menu manager.
     */
    private void fillContextMenu(final IMenuManager menu) {
        final IStructuredSelection selection = (IStructuredSelection) _viewer.getSelection();
        if (selection.size() > 0) {
            menu.add(_acknowledgeAction);
        }
        if (selection.size() == 1) {
            menu.add(_runCssAlarmDisplayAction);
            menu.add(_runCssDisplayAction);
            menu.add(_openCssStripChartAction);
            menu.add(_showHelpGuidanceAction);
            menu.add(_showHelpPageAction);
            menu.add(new Separator("edit"));
            menu.add(_renameAction);
            menu.add(_deleteNodeAction);
        }
        if ((selection.size() == 1) && (selection.getFirstElement() instanceof SubtreeNode)) {
            menu.add(_createRecordAction);
            updateCreateComponentActionText();
            menu.add(_createComponentAction);
        }

        // adds a separator after which contributed actions from other plug-ins
        // will be displayed
        menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    /**
     * Updates the text of the Create Component action based on the object class of the currently
     * selected node.
     */
    private void updateCreateComponentActionText() {
        final SubtreeNode node = (SubtreeNode) ((IStructuredSelection) _viewer.getSelection())
                .getFirstElement();
        final LdapObjectClass oclass = node.getRecommendedChildSubtreeClass();
        if (oclass == LdapObjectClass.SUBCOMPONENT) {
            _createComponentAction.setText("Create Subcomponent");
        } else {
            _createComponentAction.setText("Create Component");
        }
    }

    /**
     * Adds the tool bar actions.
     *
     * @param manager
     *            the menu manager.
     */
    private void fillLocalToolBar(final IToolBarManager manager) {
        manager.add(_toggleFilterAction);
        manager.add(new Separator());
        manager.add(_showPropertyViewAction);
        manager.add(_reloadAction);
    }

    /**
     * Creates the actions offered by this view.
     */
    private void makeActions() {
        _reloadAction = new Action() {
            @Override
            public void run() {
                startDirectoryReaderJob();
            }
        };
        _reloadAction.setText("Reload");
        _reloadAction.setToolTipText("Reload");
        _reloadAction.setImageDescriptor(AlarmTreePlugin.getImageDescriptor("./icons/refresh.gif"));

        _acknowledgeAction = new Action() {
            @Override
            public void run() {
                final Set<Map<String, String>> messages = new HashSet<Map<String, String>>();
                final IStructuredSelection selection = (IStructuredSelection) _viewer.getSelection();
                for (final Iterator<?> i = selection.iterator(); i.hasNext();) {
                    final Object o = i.next();
                    if (o instanceof SubtreeNode) {
                        final SubtreeNode snode = (SubtreeNode) o;
                        for (final ProcessVariableNode pvnode : snode.collectUnacknowledgedAlarms()) {
                            final String name = pvnode.getName();
                            final Severity severity = pvnode.getUnacknowledgedAlarmSeverity();
                            final Map<String, String> properties = new HashMap<String, String>();
                            properties.put("NAME", name);
                            properties.put("SEVERITY", severity.toString());
                            messages.add(properties);
                        }
                    } else if (o instanceof ProcessVariableNode) {
                        final ProcessVariableNode pvnode = (ProcessVariableNode) o;
                        final String name = pvnode.getName();
                        final Severity severity = pvnode.getUnacknowledgedAlarmSeverity();
                        final Map<String, String> properties = new HashMap<String, String>();
                        properties.put("NAME", name);
                        properties.put("SEVERITY", severity.toString());
                        messages.add(properties);
                    }
                }
                if (!messages.isEmpty()) {
                    CentralLogger.getInstance().debug(this,
                                                      "Scheduling send acknowledgement ("
                                                              + messages.size() + " messages)");
                    final SendAcknowledge ackJob = SendAcknowledge.newFromProperties(messages);
                    ackJob.schedule();
                }
            }
        };
        _acknowledgeAction.setText("Send Acknowledgement");
        _acknowledgeAction.setToolTipText("Send alarm acknowledgement");
        _acknowledgeAction.setEnabled(false);

        _runCssAlarmDisplayAction = new Action() {
            @Override
            public void run() {
                final IStructuredSelection selection = (IStructuredSelection) _viewer.getSelection();
                final Object selected = selection.getFirstElement();
                if (selected instanceof IAlarmTreeNode) {
                    final IAlarmTreeNode node = (IAlarmTreeNode) selected;
                    final IPath path = new Path(node.getCssAlarmDisplay());
                    final Map<String, String> aliases = new HashMap<String, String>();
                    if (node instanceof ProcessVariableNode) {
                        aliases.put("channel", node.getName());
                    }
                    CentralLogger.getInstance().debug(this, "Opening display: " + path);
                    RunModeService.getInstance().openDisplayShellInRunMode(path, aliases);
                }
            }
        };
        _runCssAlarmDisplayAction.setText("Run Alarm Display");
        _runCssAlarmDisplayAction.setToolTipText("Run the alarm display for this PV");
        _runCssAlarmDisplayAction.setEnabled(false);

        _runCssDisplayAction = new Action() {
            @Override
            public void run() {
                final IStructuredSelection selection = (IStructuredSelection) _viewer.getSelection();
                final Object selected = selection.getFirstElement();
                if (selected instanceof IAlarmTreeNode) {
                    final IAlarmTreeNode node = (IAlarmTreeNode) selected;
                    final IPath path = new Path(node.getCssDisplay());
                    final Map<String, String> aliases = new HashMap<String, String>();
                    if (node instanceof ProcessVariableNode) {
                        aliases.put("channel", node.getName());
                    }
                    CentralLogger.getInstance().debug(this, "Opening display: " + path);
                    RunModeService.getInstance().openDisplayShellInRunMode(path, aliases);
                }
            }
        };
        _runCssDisplayAction.setText("Run Display");
        _runCssDisplayAction.setToolTipText("Run the display for this PV");
        _runCssDisplayAction.setEnabled(false);

        _openCssStripChartAction = new OpenStripChartAction();
        _openCssStripChartAction.setText("Open Strip Chart");
        _openCssStripChartAction.setToolTipText("Open the strip chart for this node");
        _openCssStripChartAction.setEnabled(false);

        _showHelpGuidanceAction = new Action() {
            @Override
            public void run() {
                final IStructuredSelection selection = (IStructuredSelection) _viewer.getSelection();
                final Object selected = selection.getFirstElement();
                if (selected instanceof IAlarmTreeNode) {
                    final IAlarmTreeNode node = (IAlarmTreeNode) selected;
                    final String helpGuidance = node.getHelpGuidance();
                    if (helpGuidance != null) {
                        MessageDialog.openInformation(getSite().getShell(),
                                                      node.getName(),
                                                      helpGuidance);
                    }
                }
            }
        };
        _showHelpGuidanceAction.setText("Show Help Guidance");
        _showHelpGuidanceAction.setToolTipText("Show the help guidance for this node");
        _showHelpGuidanceAction.setEnabled(false);

        _showHelpPageAction = new Action() {
            @Override
            public void run() {
                final IStructuredSelection selection = (IStructuredSelection) _viewer.getSelection();
                final Object selected = selection.getFirstElement();
                if (selected instanceof IAlarmTreeNode) {
                    final IAlarmTreeNode node = (IAlarmTreeNode) selected;
                    final URL helpPage = node.getHelpPage();
                    if (helpPage != null) {
                        try {
                            // Note: we have to pass a browser id here to work
                            // around a bug in eclipse. The method documentation
                            // says that createBrowser accepts null but it will
                            // throw a NullPointerException.
                            // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=194988
                            final IWebBrowser browser = PlatformUI.getWorkbench().getBrowserSupport()
                                    .createBrowser("workaround");
                            browser.openURL(helpPage);
                        } catch (final PartInitException e) {
                            CentralLogger.getInstance()
                                    .error(this, "Failed to initialize workbench browser.", e);
                        }
                    }
                }
            }
        };
        _showHelpPageAction.setText("Open Help Page");
        _showHelpPageAction.setToolTipText("Open the help page for this node in the web browser");
        _showHelpPageAction.setEnabled(false);

        _createRecordAction = new Action() {
            @Override
            public void run() {
                final IStructuredSelection selection = (IStructuredSelection) _viewer.getSelection();
                final Object selected = selection.getFirstElement();
                if (selected instanceof SubtreeNode) {
                    final SubtreeNode parent = (SubtreeNode) selected;
                    final String name = promptForRecordName();
                    if ((name != null) && !name.equals("")) {
                        try {
                            DirectoryEditor.createProcessVariableRecord(parent, name);
                        } catch (final DirectoryEditException e) {
                            MessageDialog.openError(getSite().getShell(),
                                                    "Create New Record",
                                                    "Could not create the new record: "
                                                            + e.getMessage());
                        }
                        _viewer.refresh(parent);
                    }
                }
            }

            private String promptForRecordName() {
                final InputDialog dialog = new InputDialog(getSite().getShell(),
                                                     "Create New Record",
                                                     "Record name:",
                                                     null,
                                                     new NodeNameInputValidator());
                if (Window.OK == dialog.open()) {
                    return dialog.getValue();
                }
                return null;
            }
        };
        _createRecordAction.setText("Create Record...");

        _createComponentAction = new Action() {
            @Override
            public void run() {
                final IStructuredSelection selection = (IStructuredSelection) _viewer.getSelection();
                final Object selected = selection.getFirstElement();
                if (selected instanceof SubtreeNode) {
                    final SubtreeNode parent = (SubtreeNode) selected;
                    final String name = promptForRecordName();
                    if ((name != null) && !name.equals("")) {
                        try {
                            DirectoryEditor.createComponent(parent, name);
                        } catch (final DirectoryEditException e) {
                            MessageDialog.openError(getSite().getShell(),
                                                    "Create New Component",
                                                    "Could not create the new component: "
                                                            + e.getMessage());
                        }
                        _viewer.refresh(parent);
                    }
                }
            }

            private String promptForRecordName() {
                final InputDialog dialog = new InputDialog(getSite().getShell(),
                                                     "Create New Component",
                                                     "Component name:",
                                                     null,
                                                     new NodeNameInputValidator());
                if (Window.OK == dialog.open()) {
                    return dialog.getValue();
                }
                return null;
            }
        };
        _createComponentAction.setText("Create Component...");

        _renameAction = new Action() {
            @Override
            public void run() {
                final IStructuredSelection selection = (IStructuredSelection) _viewer.getSelection();
                final IAlarmTreeNode selected = (IAlarmTreeNode) selection.getFirstElement();
                final String name = promptForNewName(selected.getName());
                if (name != null) {
                    try {
                        DirectoryEditor.rename(selected, name);
                    } catch (final DirectoryEditException e) {
                        MessageDialog.openError(getSite().getShell(),
                                                "Rename",
                                                "Could not rename the entry: " + e.getMessage());
                    }
                    _viewer.refresh(selected);
                }
            }

            private String promptForNewName(final String oldName) {
                final InputDialog dialog = new InputDialog(getSite().getShell(),
                                                     "Rename",
                                                     "Name:",
                                                     oldName,
                                                     new NodeNameInputValidator());
                if (Window.OK == dialog.open()) {
                    return dialog.getValue();
                }
                return null;
            }
        };
        _renameAction.setText("Rename...");

        _deleteNodeAction = new Action() {
            @Override
            public void run() {
                final IStructuredSelection selection = (IStructuredSelection) _viewer.getSelection();
                final Object selected = selection.getFirstElement();
                if (selected instanceof IAlarmTreeNode) {
                    final IAlarmTreeNode nodeToDelete = (IAlarmTreeNode) selected;
                    final SubtreeNode parent = nodeToDelete.getParent();
                    try {
                        DirectoryEditor.delete(nodeToDelete);
                        _viewer.refresh(parent);
                    } catch (final DirectoryEditException e) {
                        MessageDialog.openError(getSite().getShell(),
                                                "Delete",
                                                "Could not delete this node: " + e.getMessage());
                    }
                }
            }
        };
        _deleteNodeAction.setText("Delete");

        _showPropertyViewAction = new Action() {
            @Override
            public void run() {
                try {
                    getSite().getPage().showView(PROPERTY_VIEW_ID);
                } catch (final PartInitException e) {
                    MessageDialog.openError(getSite().getShell(), "Alarm Tree", e.getMessage());
                }
            }
        };
        _showPropertyViewAction.setText("Properties");
        _showPropertyViewAction.setToolTipText("Show property view");

        final IViewRegistry viewRegistry = getSite().getWorkbenchWindow().getWorkbench()
                .getViewRegistry();
        final IViewDescriptor viewDesc = viewRegistry.find(PROPERTY_VIEW_ID);
        _showPropertyViewAction.setImageDescriptor(viewDesc.getImageDescriptor());

        _toggleFilterAction = new Action("Show Only Alarms", IAction.AS_CHECK_BOX) {
            @Override
            public void run() {
                if (_isFilterActive) {
                    _viewer.removeFilter(_currentAlarmFilter);
                    _isFilterActive = false;
                } else {
                    _viewer.addFilter(_currentAlarmFilter);
                    _isFilterActive = true;
                }
            }
        };
        _toggleFilterAction.setToolTipText("Show Only Alarms");
        _toggleFilterAction.setChecked(_isFilterActive);
        _toggleFilterAction.setImageDescriptor(AlarmTreePlugin
                .getImageDescriptor("./icons/no_alarm_filter.png"));
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
}
