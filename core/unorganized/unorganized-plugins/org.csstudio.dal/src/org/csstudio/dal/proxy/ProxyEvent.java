/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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

package org.csstudio.dal.proxy;

import org.csstudio.dal.DynamicValueCondition;
import org.csstudio.dal.context.ConnectionState;

import java.util.EventObject;


/**
 * Event object delivering proxy events.
 *
 * @author ikriznar
 *
 */
public class ProxyEvent<P extends Proxy> extends EventObject
{
    private static final long serialVersionUID = 6073834917280287435L;
    private DynamicValueCondition condition;
    private ConnectionState connectionState;
    private Throwable error;

    /**
     * Creates a new ProxyEvent object.
     *
     * @param source event source
     * @param condition data condition
     * @param connectionState connections state
     * @param error exceptions
     */
    public ProxyEvent(P source, DynamicValueCondition condition,
        ConnectionState connectionState, Throwable error)
    {
        super(source);
        this.condition = condition;
        this.connectionState = connectionState;
        this.error = error;
    }

    /**
     * Returns event source as proxy.
     *
     * @return event source
     */
    @SuppressWarnings("unchecked")
    public P getProxy()
    {
        return (P)getSource();
    }

    /**
     * Returns data condition.
     *
     * @return data condition
     */
    public DynamicValueCondition getCondition()
    {
        return condition;
    }

    /**
     * Returns proxy connection state.
     *
     * @return proxy connection state
     */
    public ConnectionState getConnectionState()
    {
        return connectionState;
    }

    /**
     * Returns event's exception.
     *
     * @return exceptions carried by this event
     */
    public Throwable getError()
    {
        return error;
    }
}

/* __oOo__ */
