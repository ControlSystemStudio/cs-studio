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

package org.csstudio.alarm.treeView.jms;

import org.csstudio.alarm.treeView.model.Severity;

/**
 * An update in response to an alarm or acknowledgement message which has not
 * yet been applied to the tree.
 * 
 * @author Joerg Rathlev
 */
abstract class PendingUpdate {

	/**
	 * Applies this update.
	 * 
	 * @param updater
	 *            the updater which will be used to apply this update.
	 */
	abstract void apply(final AlarmTreeUpdater updater);

	/**
	 * Creates an update which will update the alarm tree based on an
	 * acknowledgement message.
	 * 
	 * @param name
	 *            the name of the node to which the acknowledgement will apply.
	 * @return an update which will apply the acknowledgement.
	 */
	static PendingUpdate createAcknowledgementUpdate(final String name) {
		return new PendingUpdate() {
			@Override
			void apply(final AlarmTreeUpdater updater) {
				updater.applyAcknowledgement(name);
			}
			
			@Override
			public String toString() {
				return new StringBuilder("PendingUpdate[Acknowledgement,name=")
					.append(name).append("]").toString();
			}
		};
	}

	/**
	 * Creates an update which will update the alarm tree based on an alarm
	 * message.
	 * 
	 * @param name
	 *            the name of the node to which the alarm will apply.
	 * @param severity
	 *            the severity of the alarm.
	 * @return an update which will apply the alarm.
	 */
	static PendingUpdate createAlarmUpdate(final String name,
			final Severity severity) {
		return new PendingUpdate() {
			@Override
			void apply(final AlarmTreeUpdater updater) {
				updater.applyAlarm(name, severity);
			}
			
			@Override
			public String toString() {
				return new StringBuilder("PendingUpdate[Alarm,name=")
					.append(name).append(",severity=").append(severity)
					.append("]").toString();
			}
		};
	}
}
