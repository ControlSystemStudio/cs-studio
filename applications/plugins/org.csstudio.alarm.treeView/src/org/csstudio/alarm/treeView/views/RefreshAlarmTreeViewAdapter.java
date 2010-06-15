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
package org.csstudio.alarm.treeView.views;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.declaration.AlarmMessageKey;
import org.csstudio.alarm.service.declaration.IAlarmInitItem;
import org.csstudio.alarm.service.declaration.IAlarmMessage;
import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.model.Alarm;
import org.csstudio.alarm.treeView.model.ProcessVariableNode;
import org.csstudio.alarm.treeView.model.Severity;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.csstudio.alarm.treeView.service.AlarmMessageListener;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

/**
 * Alarm tree view refresh adapter
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 20.05.2010
 */
public class RefreshAlarmTreeViewAdapter extends JobChangeAdapter {

    private final AlarmTreeView _alarmTreeView;
    private final SubtreeNode _adapterRootNode;

    /**
     * Constructor.
     * @param rootNode
     * @param alarmTreeView TODO
     */
    RefreshAlarmTreeViewAdapter(@Nonnull final AlarmTreeView alarmTreeView,
                                @Nonnull final SubtreeNode rootNode) {
        _alarmTreeView = alarmTreeView;
        _adapterRootNode = rootNode;
    }

    @Override
    public void done(@Nullable final IJobChangeEvent innerEvent) {

        // TODO (jpenning) mc retrieveInitialStateSynchronously not enabled
        //            _alarmTreeView.retrieveInitialStateSynchronously(_adapterRootNode);

        _alarmTreeView.asyncSetViewerInput(_adapterRootNode); // Display the new tree.

        final AlarmMessageListener alarmListener = _alarmTreeView.getAlarmListener();

        alarmListener.setUpdater(new AlarmTreeUpdater(_adapterRootNode));

        _alarmTreeView.getSite().getShell().getDisplay().asyncExec(new Runnable() {
            public void run() {
                RefreshAlarmTreeViewAdapter.this._alarmTreeView.getViewer().refresh();
            }
        });
    }

    private void retrieveInitialStateSynchronously(@Nonnull final SubtreeNode rootNode) {
        final List<ProcessVariableNode> pvNodes = rootNode.findAllProcessVariableNodes();
        final List<PVNodeItem> initItems = new ArrayList<PVNodeItem>();

        for (final ProcessVariableNode pvNode : pvNodes) {
            initItems.add(new PVNodeItem(pvNode));
        }

        AlarmTreePlugin.getDefault().getAlarmService().retrieveInitialState(initItems);
    }

    /**
     * The alarm tag of the PV node will be updated when the initial state was retrieved.
     */
    private static class PVNodeItem implements IAlarmInitItem {
        private static final Logger LOG = CentralLogger.getInstance()
                .getLogger(RefreshAlarmTreeViewAdapter.PVNodeItem.class);

        private final ProcessVariableNode _pvNode;

        protected PVNodeItem(@Nonnull final ProcessVariableNode pvNode) {
            _pvNode = pvNode;
        }

        public String getPVName() {
            return _pvNode.getName();
        }

        public void init(@Nonnull final IAlarmMessage alarmMessage) {
            // TODO jp Init: NYI initial state for alarm tree
            final String severityString = alarmMessage.getString(AlarmMessageKey.SEVERITY);
            Date eventTime;
            try {
                eventTime = DateFormat.getInstance().parse(alarmMessage.getString(AlarmMessageKey.EVENTTIME));
                final Alarm alarm = new Alarm(alarmMessage.getString(AlarmMessageKey.NAME),
                                              Severity.parseSeverity(severityString), eventTime);
                _pvNode.updateAlarm(alarm);
            } catch (ParseException e) {
                LOG.error("Could not retrieve eventtime from " + alarmMessage.getString(AlarmMessageKey.EVENTTIME), e);
            }
        }
    }


}
