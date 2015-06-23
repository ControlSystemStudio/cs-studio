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

package org.csstudio.dal;

import java.util.Iterator;

import org.csstudio.dal.context.Identifiable;


/**
 * An interface which describes a request.  A request can have a number of responses. Response
 * objects implement the <code>Response</code> interface.
 * @author ikriznar
 *
 */
public interface Request<T> extends Iterable<Response<T>>
{
    /**
     * Returns the Iterator for the response storage.
     *
     * @return Response iterator
     */
    public Iterator<Response<T>> responses();

    /**
     * Returns true if there are any responses available
     *
     * @return true if response available
     */
    public boolean hasResponse();

    /**
     * Returns the source of the request
     *
     * @return source of the request
     */
    public Identifiable getSource();

    /**
     * Returns <code>true</code> if request has been completed.
     *
     * @return <code>true</code> if request was completed.
     */
    public boolean isCompleted();

    /**
     * Returns the first response to this request.
     *
     * @return the first response
     */
    public Response<T> getFirstResponse();

    /**
     * Returns the last arrived response.
     *
     * @return the last response
     */
    public Response<T> getLastResponse();

    /**
     * Blocks call until last response is received. <br><b>NOTE: </b> call from this method is returned after events
     * are dispatched on ResponseListeners.
     *
     * @return final value received with done event.
     */
    public T waitUntilDone();
}

/* __oOo__ */
