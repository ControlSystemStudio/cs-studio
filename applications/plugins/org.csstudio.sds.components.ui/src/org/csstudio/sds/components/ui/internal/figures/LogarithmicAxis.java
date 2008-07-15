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
 * A logarithmic axis.
 * 
 * @author Joerg Rathlev
 */
final class LogarithmicAxis implements IAxis {
	
	private double _dataLower;
	private double _dataUpper;
	private int _displaySize;

	/**
	 * Creates a new logarithmic axis.
	 * 
	 * @param dataLower
	 *            the lower bound of the data range.
	 * @param dataUpper
	 *            the uppper bound of the data range.
	 * @param displaySize
	 *            the display size.
	 */
	LogarithmicAxis(double dataLower, double dataUpper, int displaySize) {
//		if (dataLower > dataUpper) {
//			throw new IllegalArgumentException(
//					"Lower bound must be lower than upper bound");
//		}
//		if (dataLower <= 0 || dataUpper <= 0) {
//			throw new IllegalArgumentException(
//					"Negative or zero bound for logarithmic axis");
//		}
		if (displaySize < 0) {
			throw new IllegalArgumentException("Invalid display size");
		}

		_dataLower = dataLower;
		_dataUpper = dataUpper;
		_displaySize = displaySize;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDataRange(double lower, double upper) {
//		if (lower > upper) {
//			throw new IllegalArgumentException(
//					"Lower bound must be lower than upper bound");
//		}
//		if (lower <= 0 || upper <= 0) {
//			throw new IllegalArgumentException(
//					"Negative or zero bound for logarithmic axis");
//		}

		_dataLower = lower;
		_dataUpper = upper;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDisplaySize(int size) {
		if (size < 0) {
			throw new IllegalArgumentException("Invalid display size");
		}
		
		_displaySize = size;
	}

	/**
	 * {@inheritDoc}
	 */
	public int valueToCoordinate(double value) {
		// TODO: handling of value <= 0. Currently returns 0.
		
		double dataRange = Math.log10(_dataUpper) - Math.log10(_dataLower);
		double scaling = (_displaySize - 1) / dataRange;

		return ((int) Math.round((Math.log10(value) - Math.log10(_dataLower)) * scaling));
	}

}
