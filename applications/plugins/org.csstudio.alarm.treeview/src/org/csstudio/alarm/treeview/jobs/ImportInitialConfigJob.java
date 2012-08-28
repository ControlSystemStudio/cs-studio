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
 */
package org.csstudio.alarm.treeview.jobs;

import javax.annotation.Nonnull;

import javax.naming.NamingException;

import org.csstudio.alarm.service.declaration.AlarmPreference;
import org.csstudio.alarm.service.declaration.AlarmServiceException;
import org.csstudio.alarm.service.declaration.IAlarmConfigurationService;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.alarm.treeview.AlarmTreePlugin;
import org.csstudio.alarm.treeview.ldap.AlarmTreeBuilder;
import org.csstudio.alarm.treeview.localization.Messages;
import org.csstudio.alarm.treeview.model.IAlarmSubtreeNode;
import org.csstudio.alarm.treeview.model.TreeNodeSource;
import org.csstudio.alarm.treeview.views.AlarmTreeView;
import org.csstudio.servicelocator.ServiceLocator;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration;
import org.csstudio.utility.treemodel.ContentModel;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Alarm tree reader job for facilities from preferences.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 20.05.2010
 */
public final class ImportInitialConfigJob extends Job {
    
    private static final Logger LOG = LoggerFactory.getLogger(ImportInitialConfigJob.class);
    
    private final IAlarmSubtreeNode _rootNode;
    private final IAlarmConfigurationService _configService;
    
    private final AlarmTreeView _alarmTreeView;
    
    /**
     * Constructor.
     * @param alarmTreeView
     * @param rootNode
     * @param configService
     */
    public ImportInitialConfigJob(@Nonnull final AlarmTreeView alarmTreeView,
                                  @Nonnull final IAlarmSubtreeNode rootNode,
                                  @Nonnull final IAlarmConfigurationService service) {
        super(Messages.ImportInitialConfigJob_Name);
        _alarmTreeView = alarmTreeView;
        _configService = service;
        _rootNode = rootNode;
    }
    
    @Override
    @Nonnull
    protected IStatus run(@Nonnull final IProgressMonitor monitor) {
        monitor.beginTask(Messages.ImportInitialConfigJob_Begin_Initializing, IProgressMonitor.UNKNOWN);
        
        final long startTime = System.currentTimeMillis();
        IAlarmService alarmService = ServiceLocator.getService(IAlarmService.class);
        try {
            ContentModel<LdapEpicsAlarmcfgConfiguration> model = alarmService.getConfiguration();
            _rootNode.clearChildren(); // removes all nodes below the root node
            
            TreeNodeSource source = AlarmPreference.ALARMSERVICE_CONFIG_VIA_LDAP.getValue() ? TreeNodeSource.LDAP
                    : TreeNodeSource.XML;
            final boolean canceled = AlarmTreeBuilder.build(_rootNode,
                                                            _alarmTreeView.getPVNodeListener(),
                                                            model,
                                                            monitor,
                                                            source);
            if (canceled) {
                return Status.CANCEL_STATUS;
            }
        } catch (AlarmServiceException e) {
            return new Status(IStatus.ERROR, AlarmTreePlugin.PLUGIN_ID, e.getMessage());
        } catch (NamingException e) {
            return new Status(IStatus.ERROR, AlarmTreePlugin.PLUGIN_ID, Messages.ImportInitialConfigJob_Status_Title
                    + Messages.ImportInitialConfigJob_Build_Failed + e.getMessage());
        } finally {
            final long endTime = System.currentTimeMillis();
            LOG.debug("Directory reader time: " + (endTime - startTime) + " msecs"); //$NON-NLS-1$ //$NON-NLS-2$
            monitor.done();
        }
        
        return Status.OK_STATUS;
    }
}
