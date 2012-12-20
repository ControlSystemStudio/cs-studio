package org.csstudio.alarm.treeview.views.actions;

import java.util.Collections;

import javax.annotation.Nonnull;

import org.csstudio.alarm.treeview.jobs.RetrieveInitialStateJob;
import org.csstudio.alarm.treeview.model.IAlarmSubtreeNode;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

/**
 * Retrieves the initial state for the selected subtree

 * @author jpenning
 */
public class RetrieveInitialStateAction extends Action {
    private final IWorkbenchPartSite _site;
    private final TreeViewer _viewer;
    private final RetrieveInitialStateJob _retrieveInitialStateJob;
    
    public RetrieveInitialStateAction(@Nonnull final IWorkbenchPartSite site,
                                      @Nonnull final RetrieveInitialStateJob retrieveInitialStateJob,
                                      @Nonnull final TreeViewer viewer) {
        _site = site;
        _retrieveInitialStateJob = retrieveInitialStateJob;
        _viewer = viewer;
    }
    
    @Override
    public void run() {
        final IStructuredSelection selection = (IStructuredSelection) _viewer.getSelection();
        final Object selected = selection.getFirstElement();
        if (selected instanceof IAlarmSubtreeNode) {
            final IAlarmSubtreeNode root = (IAlarmSubtreeNode) selected;
            _retrieveInitialStateJob.setRootNodes(Collections.singletonList(root));
            
            // Start the job.
            final IWorkbenchSiteProgressService progressService = (IWorkbenchSiteProgressService) _site
                    .getAdapter(IWorkbenchSiteProgressService.class);
            progressService.schedule(_retrieveInitialStateJob, 0, true);
            
        }
    }
    
}
