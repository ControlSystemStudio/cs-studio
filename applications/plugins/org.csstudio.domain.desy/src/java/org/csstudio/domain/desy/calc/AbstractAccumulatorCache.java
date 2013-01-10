/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.domain.desy.calc;



import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.epics.pvmanager.Function;
import org.epics.pvmanager.ValueCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstract value cache wrapper that offers an accumulation method to modify
 * the cached value.
 *
 * @author bknerr
 * @since 26.11.2010
 * @param <A> the type of the value to be accumulated
 * @param <R> the return type of the accumulation result
 */
public abstract class AbstractAccumulatorCache<A, R> extends Function<R> {

    private static final Logger LOG =
        LoggerFactory.getLogger(AbstractAccumulatorCache.class);

    private final ValueCache<R> _accumulatedValue;
    private long _num;

    /**
     * Constructor.
     * @param type
     */
    public AbstractAccumulatorCache(@Nonnull final Class<R> type) {
        _accumulatedValue = new ValueCache<R>(type);
        _num = 0;
    }

    public void accumulate(@Nonnull final A nextValue) {
        final R val = calculateAccumulation(_accumulatedValue.getValue(), nextValue);
        if (_num >= Integer.MAX_VALUE) {
            LOG.warn("Potential accumulation overflow detected - number of accumulations is reset to zero.");
            _num = 0;
        }
        _num++;
        _accumulatedValue.setValue(val);
    }


    public void clear() {
        _accumulatedValue.setValue(null);
        _num = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public R getValue() {
        return _accumulatedValue.getValue();
    }

    protected long getNumberOfAccumulations() {
        return _num;
    }

    /**
     * Calculates the accumulation from the present value in the cache and the newly arriving
     * value. The return value will become the cached value for the next accumulation.<br/>
     * Note that on call of this method, the accumulation counter has not yet been increased.
     *
     * @param accumulatedValue the currently accumulated value in the cache.
     * @param nextValue the new value to be accumulated
     * @return the result to which this cache's value will be set
     */
    @Nonnull
    protected abstract R calculateAccumulation(@CheckForNull final R accumulatedValue,
                                               @Nonnull final A nextValue);
}
