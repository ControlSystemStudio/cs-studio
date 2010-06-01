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
package org.csstudio.alarm.treeView.jobs;

import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.declaration.IAlarmConfigurationService;
import org.csstudio.alarm.service.declaration.LdapEpicsAlarmCfgObjectClass;
import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.ldap.AlarmTreeBuilder;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.csstudio.alarm.treeView.preferences.PreferenceConstants;
import org.csstudio.alarm.treeView.views.AlarmTreeView;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.model.ContentModel;
import org.csstudio.utility.ldap.model.ImportContentModelException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * Alarm tree reader job for facilities from preferences.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 20.05.2010
 */
public final class ImportInitialConfigJob extends Job {

    private static final Logger LOG = CentralLogger.getInstance()
            .getLogger(ImportInitialConfigJob.class);

    private final AlarmTreeView _alarmTreeView;
    private final SubtreeNode _rootNode;
    private final IAlarmConfigurationService _configService;

    /**
     * Constructor.
     * @param alarmTreeView
     * @param rootNode
     * @param configService
     */
    public ImportInitialConfigJob(@Nonnull final AlarmTreeView alarmTreeView,
                                  @Nonnull final SubtreeNode rootNode,
                                  @Nonnull final IAlarmConfigurationService service) {
        super("Initialize alarm tree job.");
        _alarmTreeView = alarmTreeView;
        _configService = service;
        _rootNode = rootNode;
    }

    @Override
    protected IStatus run(@Nullable final IProgressMonitor monitor) {
        monitor.beginTask("Initializing alarm tree", IProgressMonitor.UNKNOWN);

        String filePath = "c:\\alarmConfig.xml";
        try {
            final long startTime = System.currentTimeMillis();

            // TODO jp Hack: Need better way to find out whether to use LDAP
            boolean useLDAP = AlarmTreePlugin.getDefault().getLdapService() != null;
            ContentModel<LdapEpicsAlarmCfgObjectClass> model = null;
            if (useLDAP) {
                final String[] facilityNames = PreferenceConstants.retrieveFacilityNames();
                model = _configService.retrieveInitialContentModel(Arrays.asList(facilityNames));
            } else {
                model = _configService.retrieveInitialContentModelFromFile(filePath);
            }

            final boolean canceled = AlarmTreeBuilder.build(_rootNode, model, monitor);

            if (canceled) {
                return Status.CANCEL_STATUS;
            }

            final long endTime = System.currentTimeMillis();
            LOG.debug("Directory reader time: " + (endTime - startTime) + "ms");
        } catch (final NamingException e) {
            MessageDialog.openWarning(_alarmTreeView.getSite().getShell(),
                                      "Building Tree",
                                      "Could not properly build the full tree: " + e.getMessage());
        } catch (ImportContentModelException e) {
            MessageDialog.openWarning(_alarmTreeView.getSite().getShell(),
                                      "Building Tree",
                                      "Error reading config file " + filePath
                                              + "Could not properly build the full tree: "
                                              + e.getMessage());
        } finally {
            monitor.done();
        }
        return Status.OK_STATUS;
    }
}
