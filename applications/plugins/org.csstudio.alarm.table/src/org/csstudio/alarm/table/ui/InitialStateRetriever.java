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
package org.csstudio.alarm.table.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmMessageKey;
import org.csstudio.alarm.service.declaration.AlarmServiceException;
import org.csstudio.alarm.service.declaration.IAlarmInitItem;
import org.csstudio.alarm.service.declaration.IAlarmMessage;
import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.dataModel.AbstractMessageList;
import org.csstudio.alarm.table.dataModel.BasicMessage;
import org.csstudio.servicelocator.ServiceLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * Helper class for the alarm table to retrieve the initial state of pvs.
 *
 * The caller provides the message list as destination for the initial state and
 * 1. calls the retrieval synchronously or
 * 2. creates a job to do the retrieval asynchronously.
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 22.06.2010
 */
public class InitialStateRetriever {
    
    // destination for the initialization
    private final AbstractMessageList _messageList;

    public InitialStateRetriever(@Nonnull final AbstractMessageList messageList) {
        _messageList = messageList;
    }

    /**
     * The pvs for which the initial state shall be retrieved are fetched according to preferences
     * either from ldap (based on selected facilities) or from an xml configuration file.
     */
    public void retrieveInitialState() throws AlarmServiceException {
        IAlarmService alarmService = ServiceLocator.getService(IAlarmService.class);
        if (alarmService != null) {
            
            final Set<String> pvNames = alarmService.getPvNames();
            final List<IAlarmInitItem> initItems = new ArrayList<IAlarmInitItem>();
            
            for (final String pvName : pvNames) {
                initItems.add(new PVItem(pvName, _messageList));
            }
            
            alarmService.retrieveInitialState(initItems);
        } else {
            throw new AlarmServiceException("Cannot retrieve initial state: Alarm Service not available");
        }
    }

    @Nonnull
    public Job newRetrieveInitialStateJob() {
        return new Job("Retrieve initial state of PVs") {

            @Override
            @Nonnull
            protected IStatus run(@Nonnull final IProgressMonitor monitor) {
                monitor.beginTask("Retrieving initial state of PVs", IProgressMonitor.UNKNOWN);

                IStatus result = Status.OK_STATUS;
                try {
                    retrieveInitialState();
                } catch (final Exception e) {
                    result = new Status(IStatus.ERROR, JmsLogsPlugin.PLUGIN_ID,
                                        "Could not fetch PVs to initialize.\nCause: "
                                        + e.getMessage(), e);
                } finally {
                    monitor.done();
                }
                return result;
            }
        };

    }

    /**
     * The message list gets updated when the initial state is retrieved.
     */
    private static class PVItem implements IAlarmInitItem {
        // Destination for the messages
        private final AbstractMessageList _messageList;
        private final String _pvName;

        protected PVItem(@Nonnull final String pvName, @Nonnull final AbstractMessageList messageList) {
            _pvName = pvName;
            _messageList = messageList;
        }

        @Override
        @Nonnull
        public String getPVName() {
            return _pvName;
        }

        @Override
        public void init(@Nonnull final IAlarmMessage message) {
            //            LOG.debug("init for pv " + _pvName + ", msg: " + message);
            // TODO (jpenning) review: this must be thread-safe
            _messageList.addMessage(new BasicMessage(message.getMap()));
        }

        @Override
        public void acknowledge() {
            // retrieve entry from table and set acknowledged
            for (BasicMessage message : _messageList.getMessageList()) {
                if (message.getName().equals(_pvName)) {
                    message.setProperty(AlarmMessageKey.ACK.getDefiningName(), "true");
                    _messageList.updateMessage(message); // fire
                    break;
                }
            }
        }

        @Override
        public void notFound(@Nonnull final String pvName) {
            // TODO (jpenning) NYI notFound
        }
    }

}
