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

import java.io.FileNotFoundException;

import javax.annotation.Nonnull;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.declaration.AlarmPreference;
import org.csstudio.alarm.service.declaration.IAlarmConfigurationService;
import org.csstudio.alarm.treeView.ldap.AlarmTreeBuilder;
import org.csstudio.alarm.treeView.model.IAlarmSubtreeNode;
import org.csstudio.alarm.treeView.model.TreeNodeSource;
import org.csstudio.alarm.treeView.views.AlarmTreeView;
import org.csstudio.alarm.treeview.AlarmTreePlugin;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * Alarm tree reader job for facilities from preferences.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 20.05.2010
 */
public final class ImportInitialConfigJob extends Job {

    private static final Logger LOG =
        CentralLogger.getInstance().getLogger(ImportInitialConfigJob.class);

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
        super("Initialize alarm tree job.");
        _alarmTreeView = alarmTreeView;
        _configService = service;
        _rootNode = rootNode;
    }

    @Override
    protected IStatus run(@Nonnull final IProgressMonitor monitor) {
        monitor.beginTask("Initializing alarm tree", IProgressMonitor.UNKNOWN);

        final long startTime = System.currentTimeMillis();
        try {
            ContentModel<LdapEpicsAlarmcfgConfiguration> model = null;

            TreeNodeSource source;
            if (AlarmPreference.ALARMSERVICE_CONFIG_VIA_LDAP.getValue()) {
                model = _configService.retrieveInitialContentModel(AlarmPreference.getFacilityNames());
                source = TreeNodeSource.LDAP;
            } else {
                model = _configService.retrieveInitialContentModelFromFile(AlarmPreference.getConfigFilename());
                source = TreeNodeSource.XML;
            }

            _rootNode.clearChildren(); // removes all nodes below the root node

            final boolean canceled = AlarmTreeBuilder.build(_rootNode, _alarmTreeView.getPVNodeListener(), model, monitor, source);

            if (canceled) {
                return Status.CANCEL_STATUS;
            }
        } catch (final CreateContentModelException e) {
            return new Status(IStatus.ERROR,
                              AlarmTreePlugin.PLUGIN_ID,
                              "Building content model!\n" +
                              "Could not properly build the content model from LDAP or XML: " + e.getMessage());
        } catch (final NamingException e) {
            return new Status(IStatus.ERROR,
                              AlarmTreePlugin.PLUGIN_ID,
                                      "Building Tree!\n" +
                                      "Could not properly build the full tree: " + e.getMessage());
        } catch (final FileNotFoundException e) {
            return new Status(IStatus.ERROR,
                              AlarmTreePlugin.PLUGIN_ID,
                                      "Opening File!\n" +
                                      "Could not properly open the input file stream: " + e.getMessage());
        } finally {
            final long endTime = System.currentTimeMillis();
            LOG.debug("Directory reader time: " + (endTime - startTime) + " mecs");
            monitor.done();
        }
        return Status.OK_STATUS;
    }
}
