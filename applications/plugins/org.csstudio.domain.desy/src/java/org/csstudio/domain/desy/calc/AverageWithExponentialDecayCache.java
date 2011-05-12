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
package org.csstudio.domain.desy.calc;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * Cumulative average with exponential decay of older values.<br/>
 * Accumulated values are multiplied by 'decay' for any accumulation, the newly arriving value
 * is multiplied by (1-decay).
 *
 * @author bknerr
 * @since 15.02.2011
 */
public class AverageWithExponentialDecayCache extends AbstractAccumulatorCache<Double, Double> {

    private final Double _decay;


    /**
     * Constructor.
     * @param decay accumulated values decaying with this factor, with the newly arrived one is weighted with 1-decay
     */
    public AverageWithExponentialDecayCache(@Nonnull final Double decay) {
        super(Double.class);
        if (decay < 0.0 || decay > 1.0) {
            throw new IllegalArgumentException("Decay has to be in [0.0, 1.0]");
        }
        _decay = decay;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    protected Double calculateAccumulation(@CheckForNull final Double accVal,
                                           @Nonnull final Double nextVal) {
        if (accVal != null) {
            return accVal * _decay + nextVal*(1.0 - _decay);
        }
        return nextVal;
    }

    @Nonnull
    public Double getDecay() {
        return _decay;
    }

    public long getNumberOfAveragedValues() {
        return super.getNumberOfAccumulations();
    }
}
