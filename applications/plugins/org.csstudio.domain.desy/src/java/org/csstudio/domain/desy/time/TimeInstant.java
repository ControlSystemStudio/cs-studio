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
import org.joda.time.ReadableInstant;


/**
 * Immutable base time instant for nanosecond precision on base of jodatime {@link Instant}.
 * Slim memory footprint.
 * Thread safe.
 *
 * TODO (bknerr) : consider the usage of Timestamp (in platform.data or pvmanager)
 * or a 'valid' supertype immutable, once it is defined.
 * Consider additional fields, depending on what is used most often (probably millis instead of nanos)
 * Consider wrapped jodatime instant (8byte class, 8byte long millis)
 *
 * @author bknerr
 * @since 16.11.2010
 */
public class TimeInstant {

    private static final Long MAX_SECONDS = Long.MAX_VALUE / 1000 - 1;

    private final ReadableInstant _instant;

    /**
     * Fractal of a second in nanos.
     */
    private final long _fracSecInNanos;

    /**
     * Factory method for a time instant
     * @param millis millis since epoch
     * @return the time instant
     * @throws IllegalArgumentException on negative values for {@param millis}
     */

    /**
     * Safe time instant builder class for {@link TimeInstant}.
     *
     *
     * @author bknerr
     * @since 22.11.2010
     */
    public static final class TimeInstantBuilder {
        long _seconds;
        long _nanos;

        /**
         * Constructor.
         */
        public TimeInstantBuilder() {
            // Empty
        }

        /**
         * Builds time instant from current system instant.
         * @return the 'now' time instant
         */
        public static TimeInstant buildFromNow() {
            return buildFromMillis(System.currentTimeMillis());
        }

        /**
         * Builds directly from millis (always UTC) from 1970/01/01 00:00:00.
         * @param millis the milliseconds since epoch
         * @return the time instant object
         * @throws IllegalArgumentException if millis is smaller 0
         */
        public static TimeInstant buildFromMillis(final long millis) throws IllegalArgumentException {
            if (millis < 0) {
                throw new IllegalArgumentException("Number of milliseconds for TimeInstant must be non-negative.");
            }
            return new TimeInstant(millis);
        }

        /**
         * With seconds (always UTC) from 1970/01/01 00:00:00.
         * @param seconds
         * @return this for chaining (withNanos).
         * @throws IllegalArgumentException if nanos is smaller 0 or greater than 999,999,999
         */
        public TimeInstantBuilder withSeconds(final long seconds) {
            if (seconds < 0 || seconds > MAX_SECONDS) {
                throw new IllegalArgumentException("Number of seconds for TimeInstant must be non-negative and smaller (Long.MAX_VALUE / 1000) - 1.");
            }
            _seconds = seconds;
            return this;
        }

        /**
         * With nanos for a fractal second.
         * @param nanos
         * @return this for chaining.
         * @throws IllegalArgumentException if nanos is smaller 0 or greater than 999,999,999
         */
        public TimeInstantBuilder withNanos(final long nanos) {
            if (nanos < 0 || nanos > 999999999) {
                throw new IllegalArgumentException("Number of nanos for TimeInstant must be non-negative and smaller than 999,999,999.");
            }
            _nanos = nanos;
            return this;
        }
        /**
         * Builds the time instant object.
         * @return the newly built time instant.
         */
        public TimeInstant build() {
            return new TimeInstant(_seconds, _nanos);
        }
    }

    /**
     * Constructor.
     * Relies on being properly called by the {@link TimeInstantBuilder}.
     *
     * @param millis milliseconds from start of epoch 1970-01-01T00:00:00Z.
     */
    private TimeInstant(@Nonnull final long seconds, @Nonnull final long fracSecNanos) {
        _instant = new Instant(seconds*1000 + fracSecNanos/1000000);
        _fracSecInNanos = fracSecNanos;
    }
    /**
     * Constructor.
     * Relies on being properly called by the {@link TimeInstantBuilder}.
     *
     * @param millis milliseconds start of epoch 1970-01-01T00:00:00Z.
     */
    private TimeInstant(@Nonnull final long millis) {
        _instant = new Instant(millis);
        _fracSecInNanos = millis % 1000 * 1000000;
    }

    public long getFractalSecondInNanos() {
        return _fracSecInNanos;
    }
    public long getMillis() {
        return _instant.getMillis(); // delegate
    }
    public long getSeconds() {
        return getMillis() / 1000;
    }

}
