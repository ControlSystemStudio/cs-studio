/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.sds.components.ui.internal.figures;

/**
 * A strip chart figure.
 *
 * @author Joerg Rathlev
 */
public final class StripChartFigure extends AbstractChartFigure {

    /**
     * The maximum number of values that are recorded for a channel.
     */
    private static final int MAX_BUFFER_SIZE =  16000;

    /**
     * Array containing one buffer of values per channel.
     */
    private RingBuffer[] _values;

    /**
     * The number of values to be recorded per channel. Note that this will be
     * larger than the capacity of each ring buffer if this value exceeds
     * {@link #MAX_BUFFER_SIZE}.
     */
    private int _valuesPerChannel;

    /**
     * The timespan covered by the x-axis.
     */
    private double _xAxisTimespan;

    /**
     * The greatest data value.
     */
    private double _greatestDataValue;

    /**
     * The lowest data value.
     */
    private double _lowestDataValue;

    /**
     * The timespan between the first and the last data value for a series. This
     * value may be slightly different from the x-axis timespan due to rounding
     * differences.
     */
    private double _dataSeriesTimespan;

    /**
     * Creates a new strip chart figure.
     *
     * @param numberOfChannels
     *            the number of channels to be supported by this figure.
     * @param valuesPerChannel
     *            the number of values to be displayed per channel.
     * @param dataSeriesTimespan
     *            the time at which the last value in a series is recorded, in
     *            seconds. This value can be slightly greater than the length of
     *            the x-axis because the last value may be to the left of the
     *            x-axis. For example, if the x-axis length is 10 seconds and
     *            the update interval is 4 seconds, the
     *            <code>dataSeriesTimespan</code> will be 12 seconds.
     */
    public StripChartFigure(final int numberOfChannels,
            final int valuesPerChannel, final double dataSeriesTimespan) {
        super(numberOfChannels);
        _valuesPerChannel = valuesPerChannel;
        _values = new RingBuffer[numberOfChannels];
        _dataSeriesTimespan = dataSeriesTimespan;
        for (int i = 0; i < _values.length; i++) {
            _values[i] = new RingBuffer(Math.min(valuesPerChannel, MAX_BUFFER_SIZE));
        }
    }

    /**
     * Sets the x-axis timespan of this figure.
     *
     * @param timespan
     *            the timespan in seconds.
     */
    public void setXAxisTimespan(final double timespan) {
        _xAxisTimespan = timespan;
        xAxisRangeChanged();
    }

    /**
     * Adds the next value for the data series with the specified index to the
     * plot. This figure does not itself keep track of the exact times at which
     * this method is called. It is assumed that this method is called by the
     * edit part at a regular interval.
     *
     * @param index
     *            the data index.
     * @param value
     *            the current value.
     */
    public synchronized void addValue(final int index, final double value) {
        _values[index].addValue(value);
        if (value > _greatestDataValue) {
            _greatestDataValue = value;
            dataRangeChanged();
        } else if (value < _lowestDataValue) {
            _lowestDataValue = value;
            dataRangeChanged();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void dataValues(final int index,
            final IDataPointProcessor processor) {
        _values[index].processValues(processor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double greatestDataValue() {
        return _greatestDataValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double lowestDataValue() {
        return _lowestDataValue;
    }

    /**
     * Returns the greatest x-axis value. For a strip chart, this is always zero
     * (the time of the latest recorded value). Note that for a strip chart, the
     * x-axis maximum is lower than the x-axis minimum.
     *
     * @return zero.
     */
    @Override
    protected double xAxisMaximum() {
        return 0.0;
    }

    /**
     * Returns the lowest x-axis value. For a strip chart, this is the x-axis
     * timespan value, which is the age of the oldest recorded value in seconds.
     * Note that for a strip chart, the x-axis maximum is lower than the x-axis
     * minimum.
     *
     * @return the lowest x-axis value.
     */
    @Override
    protected double xAxisMinimum() {
        return _xAxisTimespan;
    }

    /**
     * A simple ring buffer for <code>double</code> values.
     *
     * @author Joerg Rathlev
     */
    private final class RingBuffer {
        /**
         * The values stored in the buffer.
         */
        private double[] _values;

        /**
         * The next write index.
         */
        private int _nextWriteIndex;

        /**
         * The number of values stored in the buffer.
         */
        private int _size;

        /**
         * Creates a new, empty buffer with the specified capacity.
         *
         * @param capacity
         *            the capacity.
         */
        private RingBuffer(final int capacity) {
            _values = new double[capacity];
            _nextWriteIndex = 0;
            _size = 0;
        }

        /**
         * Adds a value to this buffer.
         *
         * @param value
         *            the value.
         */
        private synchronized void addValue(final double value) {
            _values[_nextWriteIndex++] = value;
            if (_size < _values.length) {
                _size++;
            }
            if (_nextWriteIndex >= _values.length) {
                _nextWriteIndex = 0;
            }
        }

        /**
         * Processes the values in this buffer with the specified processor.
         * Values are processed in reverse, i.e., the latest value is processed
         * first and the oldest value is processed last.
         *
         * @param processor
         *            the processor.
         */
        private synchronized void processValues(final IDataPointProcessor processor) {
            int counter = 0;
            int i = _nextWriteIndex;
            while (counter < _size) {
                if (--i < 0) {
                    i = _values.length - 1;
                }
                double xValue = _dataSeriesTimespan * (((double) counter) / (((double) _valuesPerChannel) - 1));
                processor.processDataPoint(xValue, _values[i]);
                counter++;
            }
        }
    }

}
