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


/**
 * Instances of classes implementing this interface allow the user to traverse history data.
 * Specializations of this interface provide typed data access. History iterators are bulk
 * iterators: they iterate by returning chunks of history data in prescribed sizes, determined
 * by parameter passed to the <code>next()</code> method. This is useful if the amount of data,
 * returned (or estimated by) <code>size()</code> is very large. By doing chunk iteration,
 * the display components can retrieve and display parts of history at the time.
 * The implementation of this interface is not restriced to the <code>AbstractProperty</code>
 * types; every object capable of delivering history data can return instances of
 * <code>HistoryIterator</code> implementations.
 * <b>Note: if you want to specify the size of the fist chunk (after the iterator has been
 * constructed, you have to call <code>next()</code> before you call either timestamp accessing
 * or data accessing method, in a similar way to Java SQL <code>ResultSet</code>). If you don't
 * call <code>next()</code> after the iterator is created, the data accessing methods will transfer
 * the whole history in one go.
 * </b>
 * <p>
 * The ordering of elements returned in the iterators is <b>always</b> from the latest history
 * entry (as defined by its timestamp) towards elements with smaller timestamps. This is
 * implemented because displayers can render the most recent (and probably more important) data
 * first. As well, the iteration may not be completed if enough data items have been received
 * by Datatypes user.
 * </p>
 *
 * @author        Gasper Tkacik (gasper.tkacik@cosylab.com)
 * @version    @@VERSION@@
 */
public interface HistoryIterator
{
    /**
     * Moves the iterator to the next chunk. The chunk will have size <code>length</code>.
     *
     * @param    length    the requested size of the next chunk; if 0 is specified, all data items will be
     *                     returned, values less than 0 are illegal
     * @throws    DataExchangeException
     *                     when the data acquisition of the next data chunk fails
     */
    public void next(int length) throws DataExchangeException;

    /**
     * Returns <code>true</code> if at least one data item is still left in the iterator (i.e.
     * if at least one further chunk of size 1 must be requested).
     *
     * @return <code>true</code>    if more calls to <code>next()</code> are needed
     * @throws DataExchangeException
     *                                 if the query fails in the implementation
     */
    public boolean hasNext() throws DataExchangeException;

    /**
     * Returns the timestamp data in the current chunk. The length of the array may not equal the length
     * requested with <code>next()</code>. This method may be called multiple times, but the
     * data acquisition should be done only for the first time.
     *
     * @return        array of timestamps
     * @throws DataExchangeException if the query fails in the implementation
     */
    public long[] getTimestamps() throws DataExchangeException;

    /**
     * Returns the number of data items in the iterator, or at least an estimation of the size.
     * If the size is unknown, this method should return -1.
     *
     * @return        the estimation of the total data size
     * @throws    DataExchangeException
     *                 if the query for the complete data size fails
     */
    public int size() throws DataExchangeException;

    /**
     * Returns the data array as a Java <code>Object</code>. This object can be cast to the Java array type
     * of the data contained. This is used primarily by generic applications or browsers, but has otherwise
     * the same semantics as type-specific <code>getValues()</code>. This method always returns non-<code>null</code>,
     * even if there is no more data in the iterator (in which case it returns the array of 0 length). This is
     * useful for determining type of the element in the array.
     *
     * @return the values array as a Java <code>Object</code>
     * @throws DataExchangeException if the query fails in the implementation
     */
    public Object getValuesAsObject() throws DataExchangeException;
}

/* __oOo__ */
