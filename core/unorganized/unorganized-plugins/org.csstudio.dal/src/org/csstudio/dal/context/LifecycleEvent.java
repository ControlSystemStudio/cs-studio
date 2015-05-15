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

package org.csstudio.dal.context;

import java.util.EventObject;


/**
 * Event delivered by <code>LifecycleReporter</code> instances.
 *
 * @author Gasper Tkacik (gasper.tkacikATcosylab.com)
 */
public class LifecycleEvent extends EventObject
{
    private static final long serialVersionUID = 5970470075593327956L;
    private LifecycleState state;

    /**
         * Event constructor.
         *
         * @param source source of the event, non-<code>null</code>
         * @param state the phase indicated by this event
         */
    public LifecycleEvent(LifecycleReporter source, LifecycleState state)
    {
        super(source);
        this.state = state;
    }

    /**
     * Returns the current phase of the initialization or destruction
     * process.
     *
     * @return the current phase of the process
     */
    public LifecycleState getState()
    {
        return state;
    }

    /**
     * Returns a short summary about this instance.
     *
     * @return internal state of this
     */
    public String toString()
    {
        return "LifecycleEvent = { source='" + source + "' state='" + state
        + "' }";
    }
} /* __oOo__ */


/* __oOo__ */
