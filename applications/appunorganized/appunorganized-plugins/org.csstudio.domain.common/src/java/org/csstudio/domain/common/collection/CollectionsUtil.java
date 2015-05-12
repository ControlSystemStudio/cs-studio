/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.domain.common.collection;

import java.util.Collection;
import java.util.Iterator;


/**
 * Utility class for some missing things on collections, indeed sometimes there are...
 *
 * @author bknerr
 * @since 17.08.2011
 */
public final class CollectionsUtil {

    /**
     * Constructor.
     */
    private CollectionsUtil() {
        // Empty
    }

    /**
     * Creates a string representation according to the {@link java.util.AbstractCollection#toString()}
     * method, but with a limited length param.
     * Such that, if limit is >= the collection's size the original representation is returned,
     * otherwise the collection's representation ends after the limit-th element with
     * ",...]".
     *
     * @param <E> the type of the elements in the collection
     * @param coll the collection to transformed into string
     * @param limit the maximum number of elements to represented in the string
     * @return the (limited length) string representation
     */
    public static <E> String toLimitLengthString(final Collection<E> coll,
                                                  final int limit) {
        if (fallBackToStandardImplementation(coll, limit)) {
            return coll.toString();
        }
        final Iterator<E> iter = coll.iterator();

        final StringBuilder sb = new StringBuilder();
        sb.append('[');

        final int min = Math.min(coll.size(), limit);
        for (int i = 0; i < min; i++) {
            final E e = iter.next();
            sb.append(e == coll ? "(the collection itself)" : e); // recursive check, when an element is the collection itself
            if (!iter.hasNext() || i+1 >= min) {
                return sb.append(",...]").toString();
            }
            sb.append(", ");
        }
        return "[]";
    }

    private static <E> boolean fallBackToStandardImplementation(final Collection<E> coll,
                                                                final int limit) {
        return limit >= coll.size() || limit < 0 || coll.isEmpty();
    }
}
