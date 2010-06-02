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

import javax.annotation.Nonnull;
import javax.naming.NamingException;

import org.csstudio.alarm.service.declaration.IAlarmConfigurationService;
import org.csstudio.alarm.service.declaration.LdapEpicsAlarmCfgObjectClass;
import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.ldap.AlarmTreeBuilder;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.csstudio.utility.treemodel.ContentModel;
import org.csstudio.utility.treemodel.CreateContentModelException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * Job to import an XML file of the alarm tree.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 19.05.2010
 */
public final class ImportXmlFileJob extends Job {

    private final IAlarmConfigurationService _configService;
    private final SubtreeNode _rootNode;
    private String _filePath;


    /**
     * Constructor.
     * @param name
     * @param rootNode
     * @param alarmTreeView TODO
     */
    public ImportXmlFileJob(@Nonnull final String name,
                            @Nonnull final IAlarmConfigurationService service,
                            @Nonnull final SubtreeNode rootNode) {
        super(name);
        _configService = service;
        _rootNode = rootNode;
    }

    public void setXmlFilePath(@Nonnull final String filePath) {
        _filePath = filePath;
    }

    @Override
    protected IStatus run(@Nonnull final IProgressMonitor monitor) {
        monitor.beginTask("Reading alarm tree from XML file", IProgressMonitor.UNKNOWN);

        try {
            final ContentModel<LdapEpicsAlarmCfgObjectClass> model =
                _configService.retrieveInitialContentModelFromFile(this._filePath);

            final boolean canceled = AlarmTreeBuilder.build(_rootNode, model, monitor);
            if (canceled) {
                return Status.CANCEL_STATUS;
            }

        } catch (final CreateContentModelException e) {
            return new Status(IStatus.ERROR, AlarmTreePlugin.PLUGIN_ID, "Could not import file: " + e.getMessage(), e);
        } catch (final NamingException e) {
            return new Status(IStatus.ERROR, AlarmTreePlugin.PLUGIN_ID, "Could not properly build the full alarm tree: " + e.getMessage(), e);
        } finally {
            monitor.done();
        }
        return Status.OK_STATUS;
    }
}
