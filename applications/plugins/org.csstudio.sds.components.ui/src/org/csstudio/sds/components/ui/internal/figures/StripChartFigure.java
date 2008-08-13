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

	private RingBuffer[] _values;
	
	private int _valuesPerChannel;
	
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
	
	public void setCurrentValue(final int index, final double value) {
		_values[index].addValue(value);
		dataRangeChanged();
		xAxisRangeChanged();
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
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double xAxisMinimum() {
		return _valuesPerChannel;
	}
	
	/**
	 * A simple ring buffer for <code>double</code> values.
	 * 
	 * @author Joerg Rathlev
	 */
	private static final class RingBuffer {
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
		 * 
		 * @param processor
		 *            the processor.
		 */
		private void processValues(final IDataPointProcessor processor) {
			for (int i = 0; i < _size; i++) {
				processor.processDataPoint(i, _values[i]);
			}
		}
	}

}
