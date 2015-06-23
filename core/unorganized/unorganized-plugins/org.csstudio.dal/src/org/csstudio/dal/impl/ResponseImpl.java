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

package org.csstudio.dal.impl;

import org.csstudio.dal.DynamicValueCondition;
import org.csstudio.dal.Request;
import org.csstudio.dal.Response;
import org.csstudio.dal.Timestamp;
import org.csstudio.dal.context.Identifiable;
import org.csstudio.dal.simple.impl.DataUtil;


/**
 * Default response implementation
 *
 */
public class ResponseImpl<T> implements Response<T>
{
    private Request<T> request;
    private T value;
    private String idTag;
    private DynamicValueCondition condition;
    private boolean success;
    private Exception error;
    private boolean last;
    private Identifiable source;
    private Timestamp timestamp;

    /**
     * Creates a new ResponseImpl object.
     *
     * @param source response source
     * @param r the request this response is the response of
     * @param value response value
     * @param idTag optional response identification tag
     * @param success <code>true</code> if response is a success
     * @param error response error
     * @param cond response condition
     * @param timestamp response timestamp. If timestamp is 0, current time will be used.
     * @param last <code>true</code> if this is the last response.
     */
    public ResponseImpl(Identifiable source, Request<T> r, T value,
        String idTag, boolean success, Exception error,
        DynamicValueCondition cond, Timestamp timestamp, boolean last)
    {
        this.source = source;
        this.request = r;
        this.value = value;
        this.idTag = idTag;
        this.condition = cond;
        this.success = success;
        this.error = error;
        this.last = last;

        if (timestamp == null) {
            this.timestamp = new Timestamp();
        } else {
            this.timestamp = timestamp;
        }
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.Response#getCondition()
     */
    public DynamicValueCondition getCondition()
    {
        return condition;
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.Response#getError()
     */
    public Exception getError()
    {
        return error;
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.Response#getNumber()
     */
    public Number getNumber()
    {
        return DataUtil.castToNumber(value);
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.Response#getRequest()
     */
    public Request<T> getRequest()
    {
        return request;
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.Response#getValue()
     */
    public T getValue()
    {
        return value;
    }

    /*
     *  (non-Javadoc)
     * @see org.csstudio.dal.Response#getIdTag()
     */
    public String getIdTag()
    {
        return idTag;
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.Response#isLast()
     */
    public boolean isLast()
    {
        return last;
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.Response#success()
     */
    public boolean success()
    {
        return success;
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.Response#getSource()
     */
    public Identifiable getSource()
    {
        return source;
    }

    /* (non-Javadoc)
     * @see org.csstudio.dal.Response#getTimestamp()
     */
    public Timestamp getTimestamp()
    {
        return timestamp;
    }

    @Override
    public String toString() {
        StringBuilder sb= new StringBuilder(256);
        sb.append("Response{");
        sb.append(source.getIdentifier().getUniqueName());
        sb.append(',');
        sb.append(idTag);
        sb.append(',');
        sb.append(value);
        sb.append(',');
        sb.append(timestamp);
        sb.append('}');

        return sb.toString();
    }
}

/* __oOo__ */
