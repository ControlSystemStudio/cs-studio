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
package org.csstudio.alarm.treeView.model;

import java.util.Date;

import javax.annotation.Nonnull;

import org.apache.log4j.Logger;
import org.csstudio.alarm.service.declaration.AlarmMessageKey;
import org.csstudio.alarm.service.declaration.IAlarmInitItem;
import org.csstudio.alarm.service.declaration.IAlarmMessage;
import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.platform.logging.CentralLogger;

/**
 * A pv is represented by a node in the alarm tree. To retrieve the initial state of a pv the alarm service is called.
 * This class maps an AlarmInitItem onto a node in the tree to display the alarm state.
 *
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 21.07.2010
 */
public class PVNodeItem implements IAlarmInitItem {
    private static final Logger LOG = CentralLogger.getInstance().getLogger(PVNodeItem.class);

    private final IAlarmProcessVariableNode _pvNode;

    public PVNodeItem(@Nonnull final IAlarmProcessVariableNode pvNode) {
        _pvNode = pvNode;
    }

    @Override
    @Nonnull
    public String getPVName() {
        return _pvNode.getName();
    }

    /**
     * The alarm tag of the PV node will be updated when the initial state was retrieved.
     */
    @Override
    public void init(@Nonnull final IAlarmMessage alarmMessage) {
        // TODO (jpenning) Review access to alarm message properties
        final String name = alarmMessage.getString(AlarmMessageKey.NAME);
        if (name != null) {
            final Alarm alarm = new Alarm(name, alarmMessage.getSeverity(), alarmMessage
                    .getEventtimeOrCurrentTime());
            _pvNode.updateAlarm(alarm);
        } else {
            LOG.warn("Could not retrieve name from " + alarmMessage);
        }
    }

    @Override
    public void notFound(@Nonnull final String pvName) {
        final Alarm alarm = new Alarm(pvName,
                                      EpicsAlarmSeverity.UNKNOWN,
                                      new Date(System.currentTimeMillis()));
        _pvNode.updateAlarm(alarm);
    }

}
