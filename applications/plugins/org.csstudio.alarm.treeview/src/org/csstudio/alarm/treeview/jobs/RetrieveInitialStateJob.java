package org.csstudio.alarm.treeview.jobs;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmServiceException;
import org.csstudio.alarm.service.declaration.IAlarmInitItem;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.alarm.treeview.AlarmTreePlugin;
import org.csstudio.alarm.treeview.localization.Messages;
import org.csstudio.alarm.treeview.model.IAlarmProcessVariableNode;
import org.csstudio.alarm.treeview.model.IAlarmSubtreeNode;
import org.csstudio.alarm.treeview.model.PVNodeItem;
import org.csstudio.servicelocator.ServiceLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Retrieve the initial alarm state for the given pvs from the control system.
 * 
 * @author jpenning
 * @since 09.02.2011
 */
public class RetrieveInitialStateJob extends Job {
    
    private static final Logger LOG = LoggerFactory.getLogger(RetrieveInitialStateJob.class);
    
    private List<IAlarmSubtreeNode> _rootNodes;
    
    public RetrieveInitialStateJob() {
        super(Messages.RetrieveInitialStateJob_Name);
    }
    
    public void setRootNodes(@Nonnull final List<IAlarmSubtreeNode> rootNodes) {
        _rootNodes = rootNodes;
    }
    
    @Override
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask(Messages.RetrieveInitialStateJob_Begin_Initializing, IProgressMonitor.UNKNOWN);
        
        try {
            // guard
            if (_rootNodes == null) {
                return new Status(IStatus.ERROR, AlarmTreePlugin.PLUGIN_ID, Messages.RetrieveInitialStateJob_NoXmlNodesFound);
            }
            
            for (IAlarmSubtreeNode rootNode : _rootNodes) {
                retrieveInitialAlarmState(rootNode);
            }
        } catch (AlarmServiceException e) {
            return new Status(IStatus.ERROR, AlarmTreePlugin.PLUGIN_ID, "Cannot retrieve initial state " + e.getMessage());
        } finally {
            monitor.done();
        }
        
        return Status.OK_STATUS;
    }
    
    private void retrieveInitialAlarmState(@Nonnull final IAlarmSubtreeNode rootNode) throws AlarmServiceException {
        if (rootNode == null) {
            throw new IllegalStateException(Messages.RetrieveInitialStateJob_RootNode_Missing);
        }
        
        final List<IAlarmProcessVariableNode> pvNodes = rootNode.findAllProcessVariableNodes();
        
        final List<IAlarmInitItem> initItems = new ArrayList<IAlarmInitItem>();
        
        for (final IAlarmProcessVariableNode pvNode : pvNodes) {
            initItems.add(new PVNodeItem(pvNode));
        }
        
        final IAlarmService alarmService = ServiceLocator.getService(IAlarmService.class);
        if (alarmService != null) {
            alarmService.retrieveInitialState(initItems);
        } else {
            LOG.warn(Messages.RetrieveInitialStateJob_AlarmService_Missing);
        }
    }
    
}
