/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
 *
 * $Id$
 */
package org.csstudio.alarm.treeView.jobs;

import java.util.Collections;

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.IAlarmConfigurationService;
import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.model.IAlarmSubtreeNode;
import org.csstudio.alarm.treeView.views.AlarmTreeView;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

/**
 * Job factory for the alarm tree view.
 * 
 * The factory ensures proper concatenation of jobs, e.g. after initial import the alarm state is retrieved and then the view is updated.
 *
 * @author jpenning
 * @since 09.02.2011
 */
public final class JobFactory {
    
    //    private static final Logger LOG = CentralLogger.getInstance().getLogger(JobFactory.class);
    
    private static final IAlarmConfigurationService CONFIG_SERVICE = AlarmTreePlugin.getDefault()
            .getAlarmConfigurationService();
    
    @Nonnull
    public static ConnectionJob createConnectionJob(@Nonnull final AlarmTreeView alarmTreeView) {
        final ConnectionJob connectionJob = new ConnectionJob(alarmTreeView);
        
        return connectionJob;
    }
    
    @Nonnull
    public static Job createImportInitialConfigJob(@Nonnull final AlarmTreeView alarmTreeView,
                                                   @Nonnull final IAlarmSubtreeNode rootNode) {
        
        final Job importInitConfigJob = new ImportInitialConfigJob(alarmTreeView,
                                                                   rootNode,
                                                                   CONFIG_SERVICE);
        
        final RetrieveInitialStateJob retrieveInitialStateJob = createRetrieveInitialStateJob();
        importInitConfigJob.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                if (event.getResult() == Status.OK_STATUS) {
                    retrieveInitialStateJob.setRootNodes(Collections.singletonList(rootNode));
                    retrieveInitialStateJob.schedule();
                }
            }
        });
        retrieveInitialStateJob.addJobChangeListener(new RefreshAlarmTreeViewAdapter(alarmTreeView,
                                                                                     rootNode));
        
        return importInitConfigJob;
    }
    
    @Nonnull
    public static ImportXmlFileJob createImportXmlFileJob(@Nonnull final AlarmTreeView alarmTreeView,
                                                          @Nonnull final IAlarmSubtreeNode rootNode) {
        final ImportXmlFileJob importXmlFileJob = new ImportXmlFileJob(alarmTreeView,
                                                                       CONFIG_SERVICE,
                                                                       rootNode);
        
        final RetrieveInitialStateJob retrieveInitialStateJob = createRetrieveInitialStateJob();
        importXmlFileJob.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                if (event.getResult() == Status.OK_STATUS) {
                    retrieveInitialStateJob.setRootNodes(importXmlFileJob.getXmlRootNodes());
                    retrieveInitialStateJob.schedule();
                }
            }
        });
        
        retrieveInitialStateJob.addJobChangeListener(new RefreshAlarmTreeViewAdapter(alarmTreeView,
                                                                                     rootNode));
        
        return importXmlFileJob;
    }
    
    @Nonnull
    public static RetrieveInitialStateJob createRetrieveInitialStateJob() {
        return new RetrieveInitialStateJob();
    }
    
    /**
     * Constructor.
     */
    private JobFactory() {
        // Don't instantiate
    }
    
}
