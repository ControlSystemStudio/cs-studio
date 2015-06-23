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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

import com.cosylab.util.ArrayHelper;


/**
 * <p>
 * <code>HistoryHolder</code> iterates trough history iterator and stores
 * retrived data in two arrays: array of timestamps and values. Array of
 * values should have same type as array returned by iterator.
 * Thisimplementation should work with all by datatypes supported types with
 * reasonable performance for short history arrays.
 * </p>
 * <p>
 * Note that this implamintation is not optimized for long history arrays.
 * If returned history is very long, use directly iterator instead and fill
 * data models directly. E.g. series of points in history chart.
 * </p>
 *
 * @author <a href="mailto:igor.kriznar@cosylab.com">Igor Kriznar</a>)
 *
 * @since Oct 1, 2003.
 */
public class HistoryHolder
{
    /** Iterator for which data was extracted. */
    public HistoryIterator iterator;

    /**
     * Array of values. Should have same type as array returned by iterator.
     * E.g. for double iterator values can be casted to <code>double[].</code>
     */
    public Object values = new Object[0];

    /** Array of timestamps. */
    public long[] timestamps = new long[0];

    /** <code>true</code> if order extracted data was reversed. */
    public boolean reversed = false;

    /**
     * Constructor that iterates through history and stores the retrieved data in two arrays
     */
    public HistoryHolder(HistoryIterator it, boolean rev)
        throws DataExchangeException
    {
        reversed = rev;
        iterator = it;

        ArrayList<long[]> times = new ArrayList<long[]>(3);
        ArrayList<Object> val = new ArrayList<Object>(3);

        // get all values
        while (iterator.hasNext()) {
            iterator.next(0);

            long[] t = iterator.getTimestamps();

            // end loop if iterator accidently run out of history
            if (t == null || t.length == 0) {
                break;
            }

            times.add(t);

            Object o = iterator.getValuesAsObject();
            val.add(o);
        }

        if (val.size() == 0) {
            return;
        }

        if (val.size() == 1) {
            timestamps = (long[])times.get(0);
            values = val.get(0);
        }

        // get length and type of data
        Iterator<Object> i = val.iterator();

        int l = 0;

        while (i.hasNext()) {
            l += Array.getLength(i.next());
        }

        Class<?> c = Array.get(val.get(0), 0).getClass();

        if (c.equals(Double.class)) {
            values = new double[l];
        } else if (c.equals(Long.class)) {
            values = new long[l];
        } else if (c.equals(Long.class)) {
            values = new long[l];
        } else {
            values = (Object[])Array.newInstance(Array.get(val.get(0), 0)
                    .getClass(), l);
        }

        int index = 0;
        i = val.iterator();

        while (i.hasNext()) {
            Object o = i.next();
            l = Array.getLength(o);
            System.arraycopy(o, 0, values, index, l);
            index += l;
        }

        if (reversed) {
            ArrayHelper.flip(values);
            ArrayHelper.flip(timestamps);
        }
    }
}

/* __oOo__ */
