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
 package org.csstudio.platform.libs.dcf.messaging;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.platform.libs.dcf.directory.ContactElement;
import org.csstudio.platform.libs.dcf.directory.IDirectoryChangeListener;
import org.csstudio.platform.libs.dcf.messaging.internal.ProtocolEnumerator;
import org.csstudio.platform.logging.CentralLogger;

/**
 * A connection manager provides services for sending and receiving messages
 * and provides a directory of available communication peers.
 * <p>
 * Classes extending the connection manager must provide
 * a way of sending messages and registering for new message
 * notifications.
 * 
 * @author avodovnik
 */
public abstract class ConnectionManager {
	
	/**
	 * List of directory change listeners registered at this connection manager.
	 */
	private List<IDirectoryChangeListener> _listeners =
		new ArrayList<IDirectoryChangeListener>();
	
	private final CentralLogger log = CentralLogger.getInstance();
	
	/**
	 * Returns the default connection manager.
	 * 
	 * @return Returns an instance of the connection manager selected
	 * by the user.
	 */
	public static ConnectionManager getDefault() 
	{
		// iterate through the preferences and retrieve
		// the connection manager that was selected,
		// instantiate it, and return it
		// TODO: should be modified to get based on user's selection
		return ProtocolEnumerator.getProtocols()[0];
	}
	
	/**
	 * Initializes this connection manager. Implementations of this method
	 * may require a login procedure, so callers of this method should not
	 * assume that it will return quickly.
	 */
	// TODO: It should be document when this method is called and which object
	// is responsible for calling it.
	public abstract void initManager();
	
	/**
	 * Sends a message using the protocol implementation providing
	 * the concrete of this interface.
	 * <p>
	 * <strong>Note:</strong> The interface of this method will be modified
	 * for sane error handling.
	 * 
	 * @param message The message to be sent.
	 * @return Returns null, if the message was sent OK, and the 
	 * exception, that occurred, if there was an error.
	 */
	// XXX: Bogus error handling. Likely causes errors to be silently ignored
	// if the caller ignores the return value. Which is what *all* current
	// callers do!
	public abstract Throwable sendMessage(Message message);
	
	/**
	 * Adds a new message listener to the connection manager. This 
	 * listener is than notified when a message is sent or recieved.
	 * @param listener The listener to be added to the queue.
	 * @param filter The filter to be used to determine which packets
	 * are sent to the filter. If this is null, a default filter is 
	 * created which allows any message to pass through.
	 */
	public abstract void addMessageListener(IMessageListener listener, IMessageListenerFilter filter);
	
	/**
	 * Removes a message listener from the queue.
	 * @param listener The listener to be removed.
	 */
	public abstract void removeMessageListener(IMessageListener listener);
	
	/**
	 * Gets the identification of this connection manager.
	 * @return Returns an identification for this connection manager.
	 */
	public abstract String getId();
	
	/**
	 * Gets the directory structure for the current protocol.
	 * @return Returns an array of ContactElements.
	 */
	public abstract ContactElement[] getDirectory();
	
	/**
	 * Adds a {@link IDirectoryChangeListener} to this connection manager. The
	 * listener will be called when the list of available contacts changes.
	 * @param listener the listener to be added.
	 * @throws NullPointerException if {@code listener} is {@code null}.
	 */
	// TODO: Should this be final? Right now it cannot be final because the
	// ProtocolProxy overrides this method. (Same applies to removeDCL.)
	public void addDirectoryChangeListener(IDirectoryChangeListener listener) {
		if (listener == null)
			throw new NullPointerException();
		log.debug(this, "Adding IDirectoryChangeListener " + listener);
		_listeners.add(listener);
	}
	
	/**
	 * Removes a {@link IDirectoryChangeListener} from this listener. Passing
	 * {@code null} or a listener not registered at this connection manager to
	 * this method has no effect.
	 * @param listener the listener to be removed.
	 */
	public void removeDirectoryChangeListener(IDirectoryChangeListener listener) {
		log.debug(this, "Removing IDirectoryChangeListener " + listener);
		_listeners.remove(listener);
	}
	
	/**
	 * Notifies the directory change listeners registered at this listener
	 * that the available contacts have changed. This method should be called
	 * by implementors of this class when the list of avaiable contacts changes.
	 * 
	 * @param isStructureChange TODO: document this
	 */
	protected void notifyDirectoryChangeListener(boolean isStructureChange) {
		log.debug(this, "Notified of Directory change.");
		for(IDirectoryChangeListener listener : _listeners) {
			listener.notifyDirectoryChange(isStructureChange);
		}
	}
	
}
