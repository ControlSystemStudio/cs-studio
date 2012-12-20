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

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;


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
public final class TimeInstant implements Comparable<TimeInstant>, Serializable {


    public static final DateTimeFormatter STD_DATE_FMT =
        DateTimeFormat.forPattern("yyyy-MM-dd");
    public static final DateTimeFormatter STD_TIME_FMT =
        DateTimeFormat.forPattern("HH:mm:ss");
    public static final DateTimeFormatter STD_TIME_FMT_FOR_FS =
        new DateTimeFormatterBuilder().appendHourOfDay(2)
                                      .appendLiteral("_")
                                      .appendMinuteOfHour(2)
                                      .appendLiteral("_")
                                      .appendSecondOfMinute(2)
                                      .toFormatter();
    public static final DateTimeFormatter STD_DATETIME_FMT_FOR_FS =
        new DateTimeFormatterBuilder().append(STD_DATE_FMT)
                                      .appendLiteral("_")
                                      .appendHourOfDay(2)
                                      .appendLiteral("h")
                                      .appendMinuteOfHour(2)
                                      .appendLiteral("m")
                                      .appendSecondOfMinute(2)
                                      .appendLiteral("s")
                                      .toFormatter();
    public static final DateTimeFormatter STD_DATETIME_FMT =
        new DateTimeFormatterBuilder().append(STD_DATE_FMT)
                                      .appendLiteral(" ")
                                      .append(STD_TIME_FMT)
                                      .toFormatter();
    public static final DateTimeFormatter STD_DATETIME_FMT_WITH_MILLIS =
        new DateTimeFormatterBuilder().append(STD_DATETIME_FMT)
                                      .appendLiteral(".")
                                      .appendMillisOfSecond(3)
                                      .toFormatter();

    public static final PeriodFormatter STD_DURATION_FMT =
        new PeriodFormatterBuilder().appendHours()
                                    .appendSuffix(":")
                                    .appendMinutes()
                                    .appendSuffix(":")
                                    .appendSeconds()
                                    .toFormatter();
    public static final PeriodFormatter STD_DURATION_WITH_MILLIS_FMT =
        new PeriodFormatterBuilder().append(STD_DURATION_FMT)
                                    .appendMillis().toFormatter();


    public static final int NANOS_PER_SECOND = 1000000000;
    public static final int NANOS_PER_MILLIS = 1000000;
    public static final int MILLIS_PER_SECOND = 1000;

    public static final Long MAX_SECONDS = Long.MAX_VALUE / MILLIS_PER_SECOND - 1;

    private static final long serialVersionUID = 3157468437971986526L;

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
         * Constructor.
         */
        private TimeInstantBuilder() {
            // Empty
        }

        /**
         * Builds time instant from current system instant.
         * @return the 'now' time instant
         */
        @Nonnull
        public static TimeInstant fromNow() {
            return fromMillis(System.currentTimeMillis());
        }

        /**
         * Builds directly from seconds (always UTC) from 1970/01/01 00:00:00.
         * @param seconds the seconds since epoch
         * @return the time instant object
         * @throws IllegalArgumentException if seconds is smaller 0
         */
        @Nonnull
        public static TimeInstant fromSeconds(final long seconds) {
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
        @Nonnull
        public static TimeInstant fromMillis(final long millis) {
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
        @Nonnull
        public static TimeInstant fromNanos(final long nanos) {
            if (nanos < 0) {
                throw new IllegalArgumentException("Number of nanoseconds for TimeInstant must be non-negative.");
            }

            final Instant instant = new Instant(nanos/NANOS_PER_MILLIS);
            final long fracMillisInNanos = nanos%NANOS_PER_MILLIS;
            return new TimeInstant(instant, fracMillisInNanos);
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
    TimeInstant(@Nonnull final long millis,
                @Nonnull final long fracSecMillis) {
        _fracMillisInNanos = fracSecMillis;
        _instant = new Instant(millis);
    }

    /**
     * Constructor.
     * Relies on being properly called by the {@link TimeInstantBuilder}.
     * @param instant
     * @param fracMillisInNanos
     */
    TimeInstant(@Nonnull final Instant instant, final long fracMillisInNanos) {
        _instant = instant;
        _fracMillisInNanos = fracMillisInNanos;
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
     * @param fmt the formatter (without nano precision)
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
    public int compareTo(@Nonnull final TimeInstant other) {
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
    public boolean equals(@Nullable final Object obj) {
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

    @Nonnull
    public TimeInstant plusDuration(@Nonnull final Duration duration) {
        final Instant i = new Instant(getMillis()).plus(duration);
        return new TimeInstant(i, _fracMillisInNanos);
    }

    /**
     * Returns a new immutable time instant object.
     * @param millis nonnegative number of millis to add
     * @return a new time instant object
     * @throws IllegalArgumentException when millis is negative
     */
    @Nonnull
    public TimeInstant plusMillis(final long millis) {
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
     * @param millis nonnegative number of millis to subtract
     * @return a new time instant object
     * @throws IllegalArgumentException when computed time difference would cause instant before epoch
     */
    @Nonnull
    public TimeInstant minusMillis(final long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Millis may not be negative, use plusMillis.");
        }
        if (millis == 0) {
            return this;
        }
        final long diff = getMillis() - millis;
        if (diff < 0L) {
            throw new IllegalArgumentException("Time instant would become negative, meaning before epoch.");
        }
        final Instant i = new Instant(diff);
        return new TimeInstant(i, _fracMillisInNanos);
    }

    /**
     * Returns a new immutable time instant object.
     * @param seconds nonnegative number of seconds to subtract
     * @return a new time instant object
     * @throws IllegalArgumentException when computed time difference would cause instant before epoch
     */
    @Nonnull
    public TimeInstant minusSeconds(@Nonnull final Long intervalInS) {
        if (intervalInS < 0) {
            throw new IllegalArgumentException("Millis may not be negative, use plusMillis.");
        }
        if (intervalInS == 0) {
            return this;
        }
        final long diffMS = getMillis() - intervalInS*MILLIS_PER_SECOND;
        if (diffMS < 0L) {
            throw new IllegalArgumentException("Time instant would become negative, meaning before epoch.");
        }
        final Instant i = new Instant(diffMS);
        return new TimeInstant(i, _fracMillisInNanos);
    }

    /**
     * Returns a new immutable time instant object.
     * @param nanosPerSecond
     * @return the newly constructed time instant
     * @throws IllegalArgumentException when nanosPerSecond are negative or greater than {@TimeInstant#NANOS_PER_SECOND}.
     */
    @Nonnull
    public TimeInstant plusNanosPerSecond(final long nanosPerSecond) {
        if (nanosPerSecond < 0 || nanosPerSecond >= NANOS_PER_SECOND) {
            throw new IllegalArgumentException("Nanos per second may not be negative and must be smaller " + NANOS_PER_SECOND + ".");
        }
        if (nanosPerSecond == 0) {
            return this;
        }
        long addMillis = nanosPerSecond / NANOS_PER_MILLIS;
        final long addFracMillisInNanos = nanosPerSecond % NANOS_PER_MILLIS;

        long newNanos = _fracMillisInNanos + addFracMillisInNanos;
        if (newNanos > NANOS_PER_MILLIS) {
            addMillis++;
            newNanos -= NANOS_PER_MILLIS;
        }

        return new TimeInstant(new Instant(getMillis() + addMillis), newNanos);
    }

    /**
     * Formats the instant with the standard time formatter of this class.
     * @return the formatted time string
     */
    @Nonnull
    public String formatted() {
        return formatted(STD_DATETIME_FMT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String toString() {
        return formatted(STD_DATETIME_FMT_WITH_MILLIS) + String.format("%1$06d", _fracMillisInNanos);
    }

    /**
     * Returns the interval between two time instants in millis in absolute numbers.
     *
     * @param startTime the first time instant
     * @param endTime the second time instant
     * @return the difference in millis, always non-negative
     */
    public static long deltaInMillis(@Nonnull final TimeInstant startTime,
                                     @Nonnull final TimeInstant endTime) {
        return Math.abs(endTime.getMillis() - startTime.getMillis());
    }
}
