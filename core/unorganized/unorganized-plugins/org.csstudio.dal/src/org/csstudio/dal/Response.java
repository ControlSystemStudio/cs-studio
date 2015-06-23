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

import org.csstudio.dal.context.Identifiable;


/**
 * This interface describes a response to a request. A response to request can be for example
 * a change of value, an error, ...
 *
 * @author ikriznar
 *
 */
public interface Response<T>
{
    /**
     * Returns <code>Request</code> object, which initiated this
     * response.
     *
     * @return initial <code>Request</code>
     */
    public Request<T> getRequest();

    /**
     * Returns <code>true</code> if this is last response in series of
     * responses.
     *
     * @return <code>true</code> if it is last response
     */
    public boolean isLast();

    /**
     * Returns <code>true</code> if the request was successfully completed.
     *
     * @return returns true if response is a success
     */
    public boolean success();

    /**
     * Returns a value if the request caused a value change. Otherwise it should
     * return <code>null</code>.
     *
     * @return a value;
     */
    public T getValue();

    /**
     * Optional identification tag of the response. Interpretation depends on asynchronus methods
     * which generated this response.
     *
     * @return identification tag.
     */
    public Object getIdTag();

    /**
     * This is a convenience method that returns a number if value is a number.
     * Otherwise it should return <code>null</code>
     *
     * @return a number
     */
    public Number getNumber();

    /**
     * If result of the request is an error this method will return it. Otherwise it
     * will return <code>null</code>
     *
     * @return error
     */
    public Exception getError();

    /**
     * Returns the source of the response, same as in associated Request.
     *
     * @see Request#getSource()
     *
     * @return response source
     */
    public Identifiable getSource();

    /**
     * Returns the condition of the response
     *
     * @return response condition
     */
    public DynamicValueCondition getCondition();

    /**
     * Returns the timestamp of the response
     *
     * @return timestamp of response
     */
    public Timestamp getTimestamp();
}

/* __oOo__ */
