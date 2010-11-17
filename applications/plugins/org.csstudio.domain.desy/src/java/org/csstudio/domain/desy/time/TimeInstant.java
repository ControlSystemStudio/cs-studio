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
package org.csstudio.domain.desy.time;

import javax.annotation.Nonnull;

import org.joda.time.Instant;


/**
 * Immutable base time instant.
 *
 * TODO (bknerr) : consider the usage of Timestamp or a 'valid' supertype immutable, once it is
 * defined.
 * Consider additional fields, depending on what is used most often (probably millis instead of nanos)
 * Consider wrapped jodatime instant (8byte class, 8byte long millis)
 *
 * @author bknerr
 * @since 16.11.2010
 */
public class TimeInstant {

    private static final Long MAX_MILLIS = Long.MAX_VALUE / 1000;

    private final Instant _instant;

    /**
     * Fractal of a second in nanos.
     */
    private final long _fracSecInNanos;


    /**
     * Constructor.
     *
     * @param millis Milliseconds from start of epoch 1970-01-01T00:00:00Z.
     */
    public TimeInstant(@Nonnull final long millis) {
        if (millis < 0 || millis > MAX_MILLIS) {
            throw new IllegalArgumentException("Number of milliseconds for TimeInstant must be non-negative and smaller than Long.MAX_VALUE/1000.");
        }
        _instant = new Instant(millis);
        _fracSecInNanos = Long.valueOf(millis % 1000 * 1000000);
    }

    /**
     * Constructor.
     *
     * @param nanos Nanoseconds from start of epoch 1970-01-01T00:00:00Z.
     */
    public TimeInstant(@Nonnull final Long nanos) {
        if (nanos < 0) {
            throw new IllegalArgumentException("Number of nanos for TimeInstant must be non-negative.");
        }
        _instant = new Instant(nanos / 1000);
        _fracSecInNanos = nanos % 1000000000;
    }

    public Long getFractalSecondInNanos() {
        return _fracSecInNanos;
    }
    public Long getMillis() {
        return _instant.getMillis();
    }
    public Long getSeconds() {
        return getMillis() / 1000;
    }

    /**
     * @return
     */
    public String getFractalNanos() {
        // TODO Auto-generated method stub
        return null;
    }


}
