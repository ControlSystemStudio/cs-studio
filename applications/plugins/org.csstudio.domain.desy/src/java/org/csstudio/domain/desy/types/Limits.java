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
package org.csstudio.domain.desy.types;

import javax.annotation.Nonnull;

/**
 * General class to hold limits.
 * {@link Limits#getLow()}.comparedTo{@link Limits#getHigh()} is guaranteed to yield -1 or 0
 *
 * @author bknerr
 * @since Mar 3, 2011
 * @param <V> the comparable value for these limits
 */
public final class Limits<V extends Comparable<? super V>> {

    private final V _low;
    private final V _high;

    /**
     * Constructor.
     */
    private Limits(@Nonnull final V low,
                   @Nonnull final V high) {
        _low = low;
        _high = high;

        if (!Comparable.class.isAssignableFrom(_low.getClass())) {
            throw new IllegalArgumentException("Type is not assignable from " + Comparable.class.getName());

        }
        if (_low.compareTo(_high) > 1) {
            throw new IllegalArgumentException("Low limit is larger than high limit.");
        }
    }

    /**
     * Factory method.
     *
     * @param low the low limit
     * @param high the high limit
     * @return the new instance
     * @param <W>
     * @throws IllegalArgumentException on low being larger comparedTo high
     */
    @Nonnull
    public static
    <W extends Comparable<? super W>> Limits<W> create(@Nonnull final W low,
                                                       @Nonnull final W high) {
        return new Limits<W>(low, high);
    }

    @Nonnull
    public V getLow() {
        return _low;
    }

    @Nonnull
    public V getHigh() {
        return _high;
    }
}
