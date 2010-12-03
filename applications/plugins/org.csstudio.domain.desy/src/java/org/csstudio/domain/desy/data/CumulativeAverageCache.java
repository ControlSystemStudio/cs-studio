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
package org.csstudio.domain.desy.data;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.domain.desy.time.TimeInstant;
import org.csstudio.domain.desy.types.ConversionTypeSupportException;
import org.csstudio.domain.desy.types.CssDouble;
import org.csstudio.domain.desy.types.ICssValueType;
import org.csstudio.domain.desy.types.TypeSupport;


/**
 * Accumulation cache that implements a cumulative average for the accumulated values.
 * The returned value on {@link CumulativeAverageCache#getValue()} is the sum over all
 * accumulated values divided by their number.
 *
 * For {@code }
 *
 *
 * @author bknerr
 * @since 26.11.2010
 * @param <V> the basic type of value(s) of the CssValueType
 * @param <A> value type parameter, has to have a conversion type support
 *            {@link ConversionTypeSupport#}
 */
public class CumulativeAverageCache<V, A extends ICssValueType<V>> extends AbstractAccumulatorCache<A, CssDouble> {

    /**
     * Constructor.
     */
    public CumulativeAverageCache() {
        super(CssDouble.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected CssDouble calculateAccumulation(@CheckForNull final CssDouble accVal,
                                              @Nonnull final A nVal) throws ConversionTypeSupportException {
        final CssDouble nextDDouble = TypeSupport.toCssDouble(nVal);

        final Double nextVal = nextDDouble.getValueData();
        final TimeInstant timestamp = nextDDouble.getTimestamp();
        final Double curVal = accVal.getValueData();

        Double result;
        if (curVal != null) {
            result = curVal + nextVal;
            // better to calc division once on #getValue)
            //final int n = getNumberOfAccumulations();
            //result = curVal + (nextVal - curVal)/(n + 1);
        } else {
            result = nextVal;
        }
        return new CssDouble(result, timestamp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public CssDouble getValue() {
        final CssDouble dVal = super.getValue();
        if (dVal != null) {
            final Double value = dVal.getValueData();
            if (value!= null) {
                final int n = getNumberOfAccumulations();
                return new CssDouble(value / n, dVal.getTimestamp());
            }
        }
        return null;
    }
}
