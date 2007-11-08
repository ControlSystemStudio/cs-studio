package org.csstudio.sds.components.ui.internal.figures;


/**
 * <p>Calculates where the tickmarks on an axis should be placed. Ticks are
 * placed at &quot;nice numbers&quot;, i.e. their distance is one of the
 * following:</p>
 * 
 * <ul>
 * <li>1.0 * 10<sup>n</sup></li>
 * <li>2.0 * 10<sup>n</sup></li>
 * <li>2.5 * 10<sup>n</sup></li>
 * <li>5.0 * 10<sup>n</sup></li>
 * </ul>
 * 
 * <p><strong>Warning:</strong> Objects of this class currently do not actually
 * place tickmarks at nice numbers because the algorithm to do that turned out
 * to not work very well. The current algorithm simply tries to reduce the
 * number of significant decimal places of the tickmark distance.</p> 
 * 
 * <p>Usage: set the minimum and maximum data value and the maximum number of
 * tickmarks. Then, get the smallest value at which to place a tickmark and the
 * distance of tickmarks.</p>
 * 
 * @author Joerg Rathlev
 */
class TickCalculator {
	
	/**
	 * The smallest data value.
	 */
	private double min = 0.0;
	
	/**
	 * The largest data value.
	 */
	private double max = 0.0;
	
	/**
	 * The maximum number of tickmarks, as set by the client.
	 */
	private int maxTickCount;
	
	/**
	 * The smallest data value at which a tickmark should be placed.
	 */
	private double smallestTick;
	
	/**
	 * The distance at which the tickmarks should be placed.
	 */
	private double tickDistance;

	/**
	 * Sets the minimum value in the dataset.
	 * @param d the minimum value in the dataset.
	 */
	public void setMinimumValue(double d) {
		this.min = d;
		recalculate();
	}

	/**
	 * Sets the maximum value in the dataset.
	 * @param d the maximum value in the dataset.
	 */
	public void setMaximumValue(double d) {
		this.max = d;
		recalculate();
	}

	/**
	 * Sets the maximum number of tickmarks for which this calculator will 
	 * calculate positions.
	 * @param i the maximum number of tickmarks.
	 */
	public void setMaximumTickCount(int i) {
		this.maxTickCount = i;
		recalculate();
	}
	
	/**
	 * Returns the smallest data value at which a tickmark should be placed.
	 * @return the smallest data value at which a tickmark should be placed.
	 */
	public double getSmallestTick() {
		return smallestTick;
	}

	/**
	 * Returns the distance at which tickmarks should be placed.
	 * @return the distance at which tickmarks should be placed.
	 */
	public double getTickDistance() {
		return tickDistance;
	}

	/**
	 * Recalculates the positions of the tickmarks based on the provided
	 * minimum and maximum data values and maximum number of tickmarks.
	 */
	private void recalculate() {
		if (min > max || maxTickCount < 2) {
			return;
		}
		
		double dataRange = max - min;
		
		// if we were to create exactly maxTickCount ticks, without any
		// snapping to nice numbers, this would be their distance:
		double exactDist = dataRange / (maxTickCount - 1);
		
		// Now, the goal is to find the smallest distance that is larger than
		// or equal to the exact distance, and that is a "nice number".
		
		// the order of magnitude of the exact distance:
		double o = orderOfMagnitude(exactDist);
		
		// Ok, this is basically the algorithm by Kay Kasemir from his
		// org.csstudio.swt.charts component. We're looking at the order
		// of magnitude of the exact distance, and then rounding so that
		// the actual distance of the tickmarks will have one additional
		// significant decimal place. For example, if the order of magnitude
		// of the exact distance is -1, the distance of the tickmarks will
		// have two decimal places. Note that this works better the more
		// tickmarks you have. With a small number of tickmarks, this will
		// almost certainly NOT place them at nice numbers.
		tickDistance = Math.ceil(exactDist / Math.pow(10, o - 1)) * Math.pow(10, o - 1);
		
		// The algorithm below rounds to nice numbers, but is overly
		// aggressive, so it is disabled for now.
		// TODO: find a better algorithm.
		
		// Here is an example for the problem with the algorithm below:
		// With a minimum of -130 and a maximum of 170 and a maximum of
		// four tickmarks, the exact distance would be 100 and that is what
		// is actually used, so tickmarks will be placed at -100, 0 and 100.
		// But if the maximum is increased to 171, the exact distance will be
		// slightly larger than 100, and will be rounded up to 200, so the
		// tickmarks would be placed at -200, 0 and 200, but both -200 and
		// 200 are outside the bounds of the graph! So there will be only one
		// tickmark at 0. The best solution would be to still place the
		// tickmarks at -100, 0 and 100.
		
//		// the (decimal) mantissa of the exact distance:
//		double m = exactDist / Math.pow(10, o);
//		
//		// the exact distance is now
//		// m * 10^o
//		// Increase m to the nearest "nice number" mantissa, or if m is already
//		// larger than 5.0, set m to 1.0 and increase the order of magnitude.
//		if (m <= 1.0) {
//			m = 1.0;
//		} else if (m <= 2.0) {
//			m = 2.0;
//		} else if (m <= 2.5) {
//			m = 2.5;
//		} else if (m <= 5.0) {
//			m = 5.0;
//		} else {
//			m = 1.0;
//			o += 1;
//		}
//		tickDistance = m * Math.pow(10, o);
//		
		// The first tick must be shown at the smallest value that is larger
		// than or equal to the minimum value and a multiple of the interval.
		// This ensures that the ticks really are shown at "nice" numbers, even
		// if the smallest data value is not a nice number, and it ensures that
		// a tick is always shown at 0.0.
		smallestTick = Math.ceil(min / tickDistance) * tickDistance;
	}
	
	/**
	 * Returns the order of magnitude of the given value.
	 * 
	 * @param d a value.
	 * @return the order of magnitude of d.
	 */
	private static double orderOfMagnitude(double d) {
		if (d == 0) {
			return 0.0;
		} else {
			return Math.floor(Math.log10(Math.abs(d)));
		}
	}

}
