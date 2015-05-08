/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A waveform figure. This figure supports drawing an arbitrary number of data
 * arrays. The actual maximum number of data arrays must be specified when
 * the figure is created.
 *
 * @author Sven Wende, Kai Meyer, Joerg Rathlev
 */
public final class WaveformFigure extends AbstractChartFigure {

    private static final Logger LOG = LoggerFactory.getLogger(WaveformFigure.class);

    /**
     * The displayed waveform data.
     */
    private double[][] _data;

    /**
     * A double, representing the maximum value of the data.
     */
    private double _max = 0;

    /**
     * A double, representing the minimum value of the data.
     */
    private double _min = 0;

    /**
     * The length of the longest data array.
     */
    private int _longestDataLength;

    /**
     * Standard constructor.
     *
     * @param dataCount
     *            the number of data arrays to be displayed by this figure. Must
     *            be a positive integer number.
     */
    public WaveformFigure(final int dataCount) {
        super(dataCount);
        _data = new double[dataCount][0];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void dataValues(final int index,
            final IDataPointProcessor processor) {
        for (int i = 0; i < _data[index].length; i++) {
            processor.processDataPoint(i, _data[index][i]);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double greatestDataValue() {
        return _max;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double lowestDataValue() {
        return _min;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double xAxisMaximum() {
        return _longestDataLength;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected double xAxisMinimum() {
        return 0;
    }

    /**
     * Sets the data array with the specified index. Repaints this figure
     * afterwards.
     *
     * @param index
     *            the index of the data to set. This must be a positive integer
     *            or zero, and smaller than the number of data arrays specified
     *            in the constructor of this figure.
     * @param data
     *            the waveform data.
     */
    public void setData(final int index, final double[] data) {
        LOG.debug("setData called with index=" + index);

        _data[index] = data;
        if (data.length > _longestDataLength) {
            _longestDataLength = data.length;
        } else {
            _longestDataLength = 0;
            for (double[] dataArray : _data) {
                if (dataArray.length > _longestDataLength) {
                    _longestDataLength = dataArray.length;
                }
            }
        }
        xAxisRangeChanged();
        calculateDataRange();
    }

    /**
     * Calculates the data range.
     */
    private void calculateDataRange() {
        final double oldMin = _min;
        final double oldMax = _max;

        // Initialize min and max with the first value from the first data
        // array that contains values.
        double min = 0;
        double max = 0;
        boolean initialized = false;
        for (double[] data : _data) {
            if (data.length > 0) {
                min = data[0];
                max = data[0];
                initialized = true;
                break;
            }
        }
        if (!initialized) {
            // no array contains any data
            return;
        }

        for (double[] data : _data) {
            for (double value : data) {
                if (value > max) {
                    max = value;
                } else if (value < min) {
                    min = value;
                }
            }
        }

        if (min != oldMin || max != oldMax) {
            LOG.debug("calculated new data range: [" + min + "," + max + "]");

            _min = min;
            _max = max;
            dataRangeChanged();
        }
    }
}
