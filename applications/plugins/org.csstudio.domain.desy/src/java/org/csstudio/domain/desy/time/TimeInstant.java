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
 *                 but not backed by a lib, still quite strong)
 *
 * @author bknerr
 * @since 16.11.2010
 */
public class TimeInstant implements Comparable<TimeInstant> {

    private static final int NANOS_PER_SECOND = 1000000000;
    private static final int NANOS_PER_MILLIS = 1000000;
    private static final int MILLIS_PER_SECOND = 1000;

    public static final Long MAX_SECONDS = Long.MAX_VALUE / MILLIS_PER_SECOND - 1;

    /**
     * The wrapped immutable joda time instant.
     */
    private final ReadableInstant _instant;

    /**
     * Fractal of a millisecond in nanos.
     */
    private final long _fracMillisInNanos;


    /**
     * Safe time instant builder class for {@link TimeInstant}.
     *
     * @author bknerr
     * @since 22.11.2010
     */
    public static final class TimeInstantBuilder {

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
            return new TimeInstant(seconds*MILLIS_PER_SECOND, 0L);
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
            return new TimeInstant(millis, 0L);
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

            return new TimeInstant(nanos/NANOS_PER_MILLIS, nanos%NANOS_PER_MILLIS);
        }
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
    private TimeInstant(@Nonnull final long millis, @Nonnull final long fracSecMillis) {
        _fracMillisInNanos = fracSecMillis;
        _instant = new Instant(millis);
    }

    /**
     * Constructor.
     * Relies on being properly called by the {@link TimeInstantBuilder}.
     * @param instant
     * @param fracSecInNanos
     */
    private TimeInstant(@Nonnull final Instant instant, final long fracSecInNanos) {
        _instant = instant;
        _fracMillisInNanos = fracSecInNanos;
    }

    public long getFractalMillisInNanos() {
        return _fracMillisInNanos;
    }
    public long getFractalSecondsInNanos() {
        final long l = getMillis() % MILLIS_PER_SECOND;
        return l * NANOS_PER_MILLIS + _fracMillisInNanos;
    }

    public long getNanos() {
        return _instant.getMillis() * NANOS_PER_MILLIS + _fracMillisInNanos; // delegate
    }
    /**
     * Get millis since epoch, 1970/01/01 00:00:00.
     * @return millis
     */
    public long getMillis() {
        return _instant.getMillis(); // delegate
    }
    /**
     * Get seconds since epoch, 1970/01/01 00:00:00.
     * @return seconds
     */
    public long getSeconds() {
        return getMillis() / MILLIS_PER_SECOND;
    }
    @Nonnull
    public ReadableInstant getInstant() {
        return _instant;
    }

    /**
     * Formats the instant with the given joda time formatter.
     * @param sampleTimeFmt the formatter (without nano precision)
     * @return the formatted time string
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
        if (this.compareTo(other) == 1) {
            return true;
        }
        return false;
    }

    public boolean isBefore(@Nonnull final TimeInstant other) {
        if (this.compareTo(other) == -1) {
            return true;
        }
        return false;
    }

    /**
     * Returns a new immutable time instant object.
     * @param millis the number of millis to add (or to subtract if negative
     * @return a new time instant object
     * @throws IllegalArgumentException when millis is negative and
     */
    public TimeInstant plusMillis(final long millis) throws IllegalArgumentException {
        if (millis < 0) {
            throw new IllegalArgumentException("Millis may not be negative, use minusMillis.");
        }
        if (millis == 0) {
            return this;
        }
        final Instant i = new Instant(getMillis() + millis);
        return new TimeInstant(i, _fracMillisInNanos);
    }

    /**
     * Returns a new immutable time instant object.
     * @param millis
     * @return
     * @throws IllegalArgumentException when millis
     */
    public TimeInstant plusNanosPerSecond(final long nanosPerSecond) throws IllegalArgumentException {
        if (nanosPerSecond < 0 || nanosPerSecond >= NANOS_PER_SECOND) {
            throw new IllegalArgumentException("Nanos per second may not be negative and must be smaller " + NANOS_PER_SECOND + ".");
        }
        if (nanosPerSecond == 0) {
            return this;
        }
        long addMillis = nanosPerSecond / NANOS_PER_MILLIS;
        final long addFracMillisInNanos = NANOS_PER_SECOND % NANOS_PER_MILLIS;

        long newNanos = _fracMillisInNanos + addFracMillisInNanos;
        if (newNanos > NANOS_PER_MILLIS) {
            addMillis++;
            newNanos -= NANOS_PER_MILLIS;
        }

        return new TimeInstant(new Instant(getMillis() + addMillis), newNanos);
    }
}
