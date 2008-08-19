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
	 * Array containing one buffer of values per channel.
	 */
	private RingBuffer[] _values;
	
	/**
	 * The number of values to be recorded per channel. This is also the size
	 * of each ring buffer.
	 */
	private int _valuesPerChannel;
	
	/**
	 * The lower bound of the x-axis.
	 */
	private double _xAxisMinimum;
	
	/**
	 * The upper bound of the x-axis.
	 */
	private double _xAxisMaximum;
	
	/**
	 * Creates a new strip chart figure.
	 * 
	 * @param numberOfChannels
	 *            the number of channels to be supported by this figure.
	 * @param valuesPerChannel
	 *            the number of values to be displayed per channel.
	 */
	public StripChartFigure(final int numberOfChannels,
			final int valuesPerChannel) {
		super(numberOfChannels);
		_valuesPerChannel = valuesPerChannel;
		_values = new RingBuffer[numberOfChannels];
		for (int i = 0; i < _values.length; i++) {
			_values[i] = new RingBuffer(valuesPerChannel);
		}
	}
	
	/**
	 * Sets the x-axis range of this figure. Note that for a strip chart figure,
	 * the minimum will usually be greater than the maximum because the axis
	 * runs from right to left.
	 * 
	 * @param minimum
	 *            the minimum value.
	 * @param maximum
	 *            the maximum value.
	 */
	public void setXAxisRange(final double minimum, final double maximum) {
		_xAxisMinimum = minimum;
		_xAxisMaximum = maximum;
		xAxisRangeChanged();
	}
	
	public void setCurrentValue(final int index, final double value) {
		// FIXME: this is not thread-safe
		_values[index].addValue(value);
		// FIXME: call only when range has actually changed
		dataRangeChanged();
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
		return 100;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double lowestDataValue() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double xAxisMaximum() {
		return _xAxisMaximum;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double xAxisMinimum() {
		return _xAxisMinimum;
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
		private void addValue(final double value) {
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
		private void processValues(final IDataPointProcessor processor) {
			int counter = 0;
			int i = _nextWriteIndex - 1;
			while (counter < _size) {
				double xValue = Math.abs(_xAxisMaximum - _xAxisMinimum) * (((double) counter) / ((double) _valuesPerChannel));
				processor.processDataPoint(xValue, _values[i]);
				counter++;
				if (--i < 0) {
					i = _values.length - 1;
				}
			}
		}
	}

}
