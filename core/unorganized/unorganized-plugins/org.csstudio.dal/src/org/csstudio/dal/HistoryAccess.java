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
 * This interface is implemented by entities that are able to return history data.
 * History data are data value - timestamp pairs. The support for accessing such
 * data is very versatile by means of history iterators that control the access to the
 * data items, if there are a large number of them; and by means of
 * <code>HistoryConstraints</code> instances that determine, before the data are accessed
 * from a (potentially) remote source, what selection rules to use for the data. Note that
 * history iterators and accessors are not useful only for dynamic values, they might also
 * be used to iterate over (for example) events, if the data source supports such
 * iteration.
 *
 * @author        Gasper Tkacik (gasper.tkacik@cosylab.com)
 */
public interface HistoryAccess
{
    /**
     * Creates a new history iterator with specified history
     * constraints. The iterator  can be only partly used by the client (i.e.
     * the iteration does not need to complete).  The resources should be
     * freed when the iterator is being garbage collected.
     *
     * @param hc the constraints that select the range of history elements in
     *        which  the user has an interest, non-<code>null</code>
     *
     * @return an instance of iterator, delivering history entries in the order
     *         from the latest to earliest
     *
     * @throws DataExchangeException if the constraints cannot be met or an
     *         error occurs in the implementation of this datatypes method
     */
    HistoryIterator getHistory(HistoryConstraints hc)
        throws DataExchangeException;
}

/* __oOo__ */
