package org.csstudio.alarm.treeView.views.actions;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.declaration.IAlarmInitItem;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.model.IAlarmProcessVariableNode;
import org.csstudio.alarm.treeView.model.IAlarmSubtreeNode;
import org.csstudio.alarm.treeView.model.PVNodeItem;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchPartSite;

/**
 * Retrieves the initial state for the selected subtree

 * @author jpenning
 */
public class RetrieveInitialStateAction extends Action {
    private static final Logger LOG = CentralLogger.getInstance()
            .getLogger(RetrieveInitialStateAction.class);
    private final IWorkbenchPartSite _site;
    private final TreeViewer _viewer;
    
    public RetrieveInitialStateAction(@Nonnull final IWorkbenchPartSite site, @Nonnull final TreeViewer viewer) {
        _site = site;
        _viewer = viewer;
    }
    
    @Override
    public void run() {
        final IStructuredSelection selection = (IStructuredSelection) _viewer.getSelection();
        final Object selected = selection.getFirstElement();
        if (selected instanceof SubtreeNode) {
            final SubtreeNode root = (SubtreeNode) selected;
            retrieveInitialStateSynchronously(root);
        }
    }
    
    private void retrieveInitialStateSynchronously(@Nonnull final IAlarmSubtreeNode subtreeNode) {
        final List<IAlarmProcessVariableNode> pvNodes = subtreeNode.findAllProcessVariableNodes();
        final List<IAlarmInitItem> initItems = new ArrayList<IAlarmInitItem>();

        for (final IAlarmProcessVariableNode pvNode : pvNodes) {
            initItems.add(new PVNodeItem(pvNode));
        }

        final IAlarmService alarmService = AlarmTreePlugin.getDefault().getAlarmService();
        if (alarmService != null) {
            alarmService.retrieveInitialState(initItems);
            _viewer.refresh(subtreeNode);
        } else {
            LOG.error("Initial state could not be retrieved because alarm service is not available.");
            MessageDialog.openError(_site.getShell(),
                                    "Retrieve initial state",
                                    "Internal error: Alarm service not available");

        }
    }

}
