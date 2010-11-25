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

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.joda.time.Instant;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


/**
 * Immutable base time instant for nanosecond precision on base of jodatime {@link Instant}.
 * Delegate anything to instant and combine, if necessary, the nanos of the fractal seconds.
 * Slim memory footprint.
 * Thread safe.
 *
 * If only joda time had nano precision, we wouldn't need our own timestamp at all.
 * Sooner or later we'll have Java 7 with JSR 310, which *is* joda time in disguise and with nanos.
 *
 *
 * TODO (bknerr) : consider the usage of org.epics.pvmanager.TimeStamp (immutable and slim as well,
 *                 but not backed by a well designed lib, still quite strong)
 *
 * @author bknerr
 * @since 16.11.2010
 */
public class TimeInstant implements Comparable<TimeInstant> {

    private static final Long MAX_SECONDS = Long.MAX_VALUE / 1000 - 1;

    /**
     * The wrapped immutable joda time instant.
     */
    private final ReadableInstant _instant;

    /**
     * Fractal of a millisecond in nanos.
     */
    private final long _fracMillisInNanos;

    /**
     * Factory method for a time instant
     * @param millis millis since epoch
     * @return the time instant
     * @throws IllegalArgumentException on negative values for {@param millis}
     */


//    public interface IBuild {
//        TimeInstant build();
//    }
//
//    public interface IWithNanos extends IBuild {
//        public IBuild withNanos(long nanos);
//    }
//
//    public interface IWithMillis extends IWithNanos {
//        public IWithNanos withMillis(long nanos);
//
//        @Override
//        public IBuild withNanos(long nanos);
//    }
//
//    public interface ITimeInstantBuilder extends IWithMillis {
//
//        public IWithMillis withSeconds(long seconds);
//
//        @Override
//        public IBuild withNanos(long seconds);
//        @Override
//        public IWithNanos withMillis(long seconds);
//    }
//    final ITimeInstantBuilder s = null;
//    s.build();
//    // build from time instant builder
//    s.withMillis(0L).build();
//    s.withNanos(0L).build();
//    // seconds from IWithSeconds
//    s.withSeconds(0L).build();
//    // build from time instant builder
//    s.withSeconds(0L).withMillis(0L).build();
//
//    s.withMillis(0L).withNanos(0L).build();
//
//    s.withSeconds(0L).withNanos(0L).build();
//
//
//    s.withSeconds(0L).build();
//
//    s.withSeconds(0L).withMillis(0L).withNanos(0L).build();



    /**
     * Safe time instant builder class for {@link TimeInstant}.
     *
     * @author bknerr
     * @since 22.11.2010
     */
    public static final class TimeInstantBuilder {
        private long _millis;
        private long _nanos;

        /**
         * Builds time instant from current system instant.
         * @return the 'now' time instant
         */
        public static TimeInstant buildFromNow() {


            return buildFromMillis(System.currentTimeMillis());
        }

        /**
         * Builds directly from seconds (always UTC) from 1970/01/01 00:00:00.
         * @param seconds the seconds since epoch
         * @return the time instant object
         * @throws IllegalArgumentException if seconds is smaller 0
         */
        public static TimeInstant buildFromSeconds(final long seconds) throws IllegalArgumentException {
            if (seconds < 0 || seconds > MAX_SECONDS) {
                throw new IllegalArgumentException("Number of seconds for TimeInstant must be non-negative and smaller " + MAX_SECONDS);
            }
            return new TimeInstant(seconds*1000);
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
         * Builds directly from nanos (always UTC) from 1970/01/01 00:00:00.
         * @param nanos the nanoseconds since epoch
         * @return the time instant object
         * @throws IllegalArgumentException if nanos is smaller 0
         */
        public static TimeInstant buildFromNanos(final long nanos) throws IllegalArgumentException {
            if (nanos < 0) {
                throw new IllegalArgumentException("Number of nanoseconds for TimeInstant must be non-negative.");
            }
            return new TimeInstant(nanos/1000000000, nanos%1000000000);
        }
//
//        /**
//         * With seconds (always UTC) from 1970/01/01 00:00:00.
//         * @param seconds
//         * @return this for chaining (withNanos).
//         * @throws IllegalArgumentException if nanos is smaller 0 or greater than 999,999,999
//         */
//        public TimeInstantBuilder withSeconds(final long seconds) {
//            if (seconds < 0 || seconds > MAX_SECONDS) {
//                throw new IllegalArgumentException("Number of seconds for TimeInstant must be non-negative and smaller (Long.MAX_VALUE / 1000) - 1.");
//            }
//            _millis = seconds*1000;
//            return this;
//        }
//
//        /**
//         * With nanos for a fractal second.
//         * @param nanos
//         * @return this for chaining.
//         * @throws IllegalArgumentException if nanos is smaller 0 or greater than 999,999,999
//         */
//        public TimeInstantBuilder withNanos(final long nanos) {
//            if (nanos < 0 || nanos > 999999999) {
//                throw new IllegalArgumentException("Number of nanos for TimeInstant must be non-negative and smaller than 1,000,000,000.");
//            }
//            _millis += nanos / 1000000; // the millis in the nanos
//            _nanos = nanos % 1000;      // the rest of the nanos
//            return this;
//        }
//        /**
//         * Builds the time instant object.
//         * @return the newly built time instant.
//         */
//        public TimeInstant build() {
//            return new TimeInstant(_millis, _nanos);
//        }
    }

    /**
     * Private constructor.
     * Does not check params, since it relies on being properly called by the
     * {@link TimeInstantBuilder}, which also hides this constructor with the two indistinguishable
     * 'long' parameters.
     *
     * @param seconds seconds from start of epoch 1970-01-01T00:00:00Z.
     * @param fracSecNanos fractal nanos of the second.
     */
    private TimeInstant(@Nonnull final long seconds, @Nonnull final long fracSecNanos) {
        _instant = new Instant(seconds*1000 + fracSecNanos/1000000);
        _fracMillisInNanos = fracSecNanos;
    }
    /**
     * Constructor.
     * Relies on being properly called by the {@link TimeInstantBuilder}.
     *
     * @param millis milliseconds start of epoch 1970-01-01T00:00:00Z.
     */
    private TimeInstant(@Nonnull final long millis) {
        _instant = new Instant(millis);
        _fracMillisInNanos = 0;
    }

    public long getFractalSecondInNanos() {
        return _fracMillisInNanos;
    }
    public long getMillis() {
        return _instant.getMillis(); // delegate
    }
    public long getSeconds() {
        return getMillis() / 1000;
    }
    @Nonnull
    public ReadableInstant getInstant() {
        return _instant;
    }

    /**
     * Formats the instant with the given joda time formatter.
     * @param sampleTimeFmt
     * @return
     */
    @Nonnull
    public String formatted(@Nonnull final DateTimeFormatter fmt) {
        return fmt.print(_instant);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(@CheckForNull final TimeInstant other) {
        final int result = _instant.compareTo(other._instant);
        if (result == 0) {
            return _fracMillisInNanos < other._fracMillisInNanos ? -1 :
                                                          _fracMillisInNanos > other._fracMillisInNanos ? 1 :
                                                                                                   0;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + (int) (_fracMillisInNanos ^ _fracMillisInNanos >>> 32);
        result = 31 * result + _instant.hashCode();
        return result;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) { // performance opt
            return true;
        }
        if (!(obj instanceof TimeInstant)) {
            return false;
        }
        final TimeInstant other = (TimeInstant) obj;
        if (_fracMillisInNanos != other._fracMillisInNanos) {
            return false;
        }
        if (!_instant.equals(other._instant)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss").print(_instant) + "." + _fracMillisInNanos;
    }

    public boolean isAfter(@Nonnull final TimeInstant other) {
        if (_instant.isBefore(other.getInstant())) {
            return true;
        } else if (_instant.getMillis() > other.getMillis() + 1) { // + 1 : take care on the additional nanos
            return false;
        }
        // equal millis, compare nanos
        return _fracMillisInNanos > other._fracMillisInNanos;
    }

    public boolean isBefore(@Nonnull final TimeInstant other) {
        return !isAfter(other);
    }

    /**
     * Returns a new immutable time instant object.
     * @param millis
     * @return
     * @throws IllegalArgumentException when millis
     */
    public TimeInstant plus(final long millis) throws IllegalArgumentException {
        // TODO (bknerr) : change that to the new withXXX calls, overflow possible
        return TimeInstantBuilder.buildFromMillis(_instant.getMillis() + millis);
    }
}
