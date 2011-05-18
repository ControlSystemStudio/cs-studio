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
package org.csstudio.alarm.treeview.model;

import javax.annotation.Nonnull;

/**
 * This node is displayed in the alarm tree view.
 *
 * @author bknerr
 * @author $Author$
 * @version $Revision$
 * @since 16.06.2010
 */
public interface IAlarmProcessVariableNode extends IAlarmTreeNode {

    /**
     * Returns the active alarm of this node.
     */
    @Nonnull
    Alarm getAlarm();

    /**
     * Removes the highest unacknowledged alarm from this node.
     * This happens when the alarm is acknowledged.
     */
    void acknowledgeAlarm();

    /**
     * Returns the highest unacknowledged alarm of this node.
     */
    @Nonnull
    Alarm getHighestUnacknowledgedAlarm();

    /**
     * Updates the active alarm state of this node. This method should be called
     * when a new alarm message was received. The alarm state is updated to the
     * new alarm only if the new alarm occured after the current alarm.
     *
     * @param alarm the new alarm.
     */
    void updateAlarm(@Nonnull Alarm alarm);
    
    /**
     * Is called from the parent after this node has been added.
     */
    void wasAdded();

    /**
     * Is called from the parent after this node has been removed.
     */
    void wasRemoved();

}
