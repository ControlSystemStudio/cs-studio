/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR
 * PURPOSE AND  NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING,
 * REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL
 * PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE
 * SOFTWARE THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND
 * OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU
 * MAY FIND A COPY AT {@link http://www.desy.de/legal/license.htm}
 */
package de.desy.language.editor.ui.eventing;

import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.desy.language.libraries.utils.contract.Contract;

/**
 * An enumeration of UI events that may occur in this UI and it's editor. You
 * should use this class to delegate events from asynchronous running components
 * like parser or compiler.
 *
 * @author <a href="mailto:kmeyer@c1-wps.de">Kai Meyer</a>
 * @author <a href="mailto:mz@c1-wps.de">Matthias Zeimer</a>
 * @version 0.1
 */
public enum UIEvent {
    /**
     * This Events indicates the change of one or more rules to be used in the
     * {@link CodeScanner} for highlighting source elements.
     */
    HIGHLIGTHING_RULE_CHANGED,

    /**
     * This Events indicates the change of one or more text attributes in one
     * ore more rules to be used in the {@link CodeScanner} for highlighting
     * source elements.
     */
    TEXT_ATTRIBUTE_CHANGED,

    /**
     * This Events indicates that a request to refresh the highlighting occurred.
     */
    HIGHLIGHTING_REFRESH_REQUEST;

    /**
     * A thread-safe set of all registered listeners.
     */
    private CopyOnWriteArraySet<UIEventListener> _listenerList;

    /**
     * Initiaaize the enumaeration elements.
     */
    UIEvent() {
        this._listenerList = new CopyOnWriteArraySet<UIEventListener>();
    }

    /**
     * Adds a listener to this UI event.
     *
     * @param listener
     *            The listener to register, may not be null.
     */
    public void addListener(final UIEventListener listener) {
        Contract.requireNotNull("listener", listener);

        this._listenerList.add(listener);
    }

    /**
     * Removes a previously registered listener from this UI event. If the
     * listener to be removed was not registered before this request will be
     * ignored.
     *
     * @param listenerToBeRemoved
     *            The listener to be removed, may not be null.
     */
    public void removeListener(final UIEventListener listenerToBeRemoved) {
        Contract.requireNotNull("listener", listenerToBeRemoved);

        this._listenerList.remove(listenerToBeRemoved);
    }

    /**
     * Inform all listener registered to this event of its occurrence. Thrown
     * Exception and Errors of the listeners will be ignored and just logged in
     * the Java-default logger avail at {@link Logger#getLogger(String)}.
     */
    public void triggerEvent() {
        for (final UIEventListener listener : this._listenerList) {
            try {
                listener.eventOccourred();
            } catch (final Throwable t) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
                        "Failed to inform listener!", t);
            }
        }
    }
}
