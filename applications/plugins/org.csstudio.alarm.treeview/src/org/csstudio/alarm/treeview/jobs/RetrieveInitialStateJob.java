package org.csstudio.alarm.treeview.jobs;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.declaration.IAlarmInitItem;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.alarm.treeview.model.IAlarmProcessVariableNode;
import org.csstudio.alarm.treeview.model.IAlarmSubtreeNode;
import org.csstudio.alarm.treeview.model.PVNodeItem;
import org.csstudio.alarm.treeview.AlarmTreePlugin;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * Retrieve the initial alarm state for the given pvs from the control system.
 * 
 * @author jpenning
 * @since 09.02.2011
 */
public class RetrieveInitialStateJob extends Job {
    
    private static final Logger LOG = CentralLogger.getInstance()
            .getLogger(RetrieveInitialStateJob.class);
    
    private List<IAlarmSubtreeNode> _rootNodes;
    
    public RetrieveInitialStateJob() {
        super("Retrieve initial alarm state");
    }
    
    public void setRootNodes(@Nonnull final List<IAlarmSubtreeNode> rootNodes) {
        _rootNodes = rootNodes;
    }
    
    @Override
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask("Initializing alarm tree", IProgressMonitor.UNKNOWN);

        
        try {
            // guard
            if (_rootNodes == null) {
                return new Status(IStatus.ERROR, AlarmTreePlugin.PLUGIN_ID, "No XML based nodes found");
            }
            
            for (IAlarmSubtreeNode rootNode : _rootNodes) {
                retrieveInitialAlarmState(rootNode);
            }
        } finally {
            monitor.done();
        }
        
        return Status.OK_STATUS;
    }
    
    private void retrieveInitialAlarmState(@Nonnull final IAlarmSubtreeNode rootNode) {
        if (rootNode == null) {
            throw new IllegalStateException("Root node must not be null");
        }
        
        final List<IAlarmProcessVariableNode> pvNodes = rootNode.findAllProcessVariableNodes();
        
        final List<IAlarmInitItem> initItems = new ArrayList<IAlarmInitItem>();
        
        for (final IAlarmProcessVariableNode pvNode : pvNodes) {
            initItems.add(new PVNodeItem(pvNode));
        }
        
        final IAlarmService alarmService = AlarmTreePlugin.getDefault().getAlarmService();
        if (alarmService != null) {
            LOG.info("Initial state retrieval for " + initItems.size() + " items starts");
            long start = System.currentTimeMillis();
            alarmService.retrieveInitialState(initItems);
            LOG.info("Initial state retrieval for " + initItems.size() + " items ends after " + (System.currentTimeMillis() - start) + " msec");
        } else {
            LOG.warn("Initial state could not be retrieved because alarm service is not available.");
        }
    }
    
}
