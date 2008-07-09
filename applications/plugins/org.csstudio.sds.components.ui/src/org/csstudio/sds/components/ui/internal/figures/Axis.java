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
 * Maps data values to display coordinates.
 * 
 * @author Joerg Rathlev
 */
final class Axis {
	
	private double _dataLower;
	private double _dataUpper;
	private int _displaySize;

	/**
	 * Creates a new axis.
	 * 
	 * @param dataLower the lower bound of the data range.
	 * @param dataUpper the upper bound of the data range.
	 * @param displaySize the size of the display.
	 */
	Axis(double dataLower, double dataUpper, int displaySize) {
		if (dataLower > dataUpper) {
			throw new IllegalArgumentException("Lower bound must be lower than upper bound");
		}
		if (displaySize < 0) {
			throw new IllegalArgumentException("Invalid display size");
		}
		
		_dataLower = dataLower;
		_dataUpper = dataUpper;
		_displaySize = displaySize;
	}

	/**
	 * Converts a data value to a display coordinate. Note that it is possible
	 * for the returned value to fall outside the display coordinate range of
	 * this axis if the data value is outside the range of data values for this
	 * axis.
	 * 
	 * @param value the data value.
	 * @return the display coordinate.
	 */
	public int valueToCoordinate(double value) {
		double dataRange = _dataUpper - _dataLower;
		double scaling = (_displaySize - 1) / dataRange;
		
		return ((int) Math.round((value - _dataLower) * scaling));
	}

	/**
	 * Sets the data range to a new range.
	 * 
	 * @param lower the new lower bound of the data range.
	 * @param upper the new upper bound of the data range.
	 */
	public void setDataRange(double lower, double upper) {
		if (lower > upper) {
			throw new IllegalArgumentException("Lower bound must be lower than upper bound");
		}
		
		_dataLower = lower;
		_dataUpper = upper;
	}

	/**
	 * Sets the display size to a new value.
	 * 
	 * @param size the new display size.
	 */
	public void setDisplaySize(int size) {
		if (size < 0) {
			throw new IllegalArgumentException("Invalid display size");
		}
		
		_displaySize = size;
	}

}
