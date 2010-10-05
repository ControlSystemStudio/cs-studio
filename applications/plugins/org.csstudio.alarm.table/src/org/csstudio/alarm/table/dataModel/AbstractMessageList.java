/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.alarm.table.dataModel;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;

import javax.annotation.Nonnull;

/**
 * List of messages (incoming from JMS).
 * Messages can be added, updated and removed and are kept in sync with the content provider (referred to as an IMessageViewer).
 * 
 * @author jhatje
 */
public abstract class AbstractMessageList {

	/**
	 * Listeners to update on changes.
	 */
	private final Set<IMessageViewer> changeListeners = new HashSet<IMessageViewer>();

	/**
	 * Time when the list is started.
	 */
	private final Date _startTime;

	public AbstractMessageList() {
		_startTime = (new GregorianCalendar(TimeZone.getTimeZone("ECT")))
				.getTime();
	}

	@Nonnull
	public Date getStartTime() {
		return _startTime;
	}

	/**
	 * Add a new Message to the collection of change listeners
	 */
	public void addMessage(@Nonnull final BasicMessage jmsm) {
		final Iterator<IMessageViewer> iterator = changeListeners.iterator();
		while (iterator.hasNext()) {
			(iterator.next()).addJMSMessage(jmsm);
		}
	}

	/**
	 * Add a new MessageList<HashMap> to the collection of change listeners
	 */
	public final void addMessages(@Nonnull final BasicMessage[] messageList) {
		final Iterator<IMessageViewer> iterator = changeListeners.iterator();
		while (iterator.hasNext()) {
			(iterator.next()).addJMSMessages(messageList);
		}
	}
	// TODO (jpenning) ML addMessages is not overridden in Log/AlarmMessageList?!

	/**
	 * Call listeners to update the message.
	 */
	public void updateMessage(@Nonnull final BasicMessage jmsm) {
		final Iterator<IMessageViewer> iterator = changeListeners.iterator();
		while (iterator.hasNext()) {
			(iterator.next()).updateJMSMessage(jmsm);
		}
	}

	/**
	 * Remove a message from the list.
	 */
	public void removeMessage(@Nonnull final BasicMessage jmsm) {
		final Iterator<IMessageViewer> iterator = changeListeners.iterator();
		while (iterator.hasNext()) {
			(iterator.next()).removeJMSMessage(jmsm);
		}
	}

	/**
	 * Remove an array of messages from the list.
	 */
	public void removeMessages(@Nonnull final BasicMessage[] jmsm) {
		final Iterator<IMessageViewer> iterator = changeListeners.iterator();
		while (iterator.hasNext()) {
			(iterator.next()).removeJMSMessage(jmsm);
		}
	}

	public void addChangeListener(@Nonnull final IMessageViewer viewer) {
		changeListeners.add(viewer);
	}

	public void removeChangeListener(@Nonnull final IMessageViewer viewer) {
		changeListeners.remove(viewer);
	}

	@Nonnull
	public abstract Vector<? extends BasicMessage> getMessageList();
	
	public int getMessageListSize() {
		return getMessageList().size();
	}

}
