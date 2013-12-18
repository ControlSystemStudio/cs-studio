package org.csstudio.trends.databrowser2.persistence;

import org.csstudio.swt.xygraph.linearscale.Range;

/**
 * XML DTO for {@link Range}.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class RangeSettings {

	private double lower;
	private double upper;

	public static RangeSettings fromSWT(Range swtRange) {
		if (swtRange == null)
			return null;
		RangeSettings settings = new RangeSettings();
		settings.setLower(swtRange.getLower());
		settings.setUpper(swtRange.getUpper());
		return settings;
	}

	public Range toSWT() {
		return new Range(lower, upper);
	}

	public double getLower() {
		return lower;
	}

	public void setLower(double lower) {
		this.lower = lower;
	}

	public double getUpper() {
		return upper;
	}

	public void setUpper(double upper) {
		this.upper = upper;
	}

}
