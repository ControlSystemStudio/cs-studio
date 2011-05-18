/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmConnectionException;
import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.alarm.service.declaration.IAlarmResource;
import org.csstudio.alarm.treeView.preferences.AlarmTreePreference;
import org.csstudio.alarm.treeView.service.AlarmMessageListener;
import org.csstudio.alarm.treeView.views.AlarmTreeConnectionMonitor;
import org.csstudio.alarm.treeView.views.AlarmTreeView;
import org.csstudio.alarm.treeView.views.Messages;
import org.csstudio.alarm.treeview.AlarmTreePlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * Job that establishes the connection to alarm provider.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 22.06.2010
 */
public final class ConnectionJob extends Job {

    private final AlarmTreeView _view;
    private IAlarmConnection _connection;

    /**
     * Constructor.
     * @param name
     */
    public ConnectionJob(@Nonnull final AlarmTreeView view) {
        super("Connecting via alarm service");
        _view = view;
    }

    @Nonnull
    private IAlarmResource createNewAlarmResource() {
        // JMS: topics: from tree prefs, facilities: don't care,               filename: don't care
        // DAL: topics: don't care,      facilities: from alarm service prefs, filename: ok
        
        String[] topicArray = AlarmTreePreference.JMS_QUEUE.getValue().split(",");
        final IAlarmResource alarmResource =
            AlarmTreePlugin.getDefault().getAlarmService().createAlarmResource(Arrays.asList(topicArray), null);
        return alarmResource;
    }

    @Override
    protected IStatus run(@Nonnull final IProgressMonitor monitor) {
        monitor.beginTask(Messages.AlarmTreeView_Monitor_ConnectionJob_Start, IProgressMonitor.UNKNOWN);
        _connection = AlarmTreePlugin.getDefault().getAlarmService().newAlarmConnection();
        try {
            final AlarmMessageListener listener = _view.getAlarmListener();
            if (listener == null) {
                throw new IllegalStateException("Listener of " +
                                                AlarmTreeView.class.getName() + " mustn't be null.");
            }
            final IAlarmResource alarmResource = createNewAlarmResource();
            final AlarmTreeConnectionMonitor connectionMonitor =
                new AlarmTreeConnectionMonitor(_view, _view.getRootNode());
            _connection.connect(connectionMonitor,
                                                       listener,
                                                       alarmResource);

        } catch (final AlarmConnectionException e) {
            throw new RuntimeException("Could not connect via alarm service", e);
        } finally {
            monitor.done();
        }
        return Status.OK_STATUS;

    }

    @CheckForNull
    public IAlarmConnection getConnection() {
        return _connection;
    }

}
