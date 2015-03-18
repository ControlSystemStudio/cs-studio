/*******************************************************************************
 * Copyright (c) 2010-2015 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.batik;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.batik.util.CSSConstants;
import org.eclipse.swt.graphics.Color;

/**
 * @author Fred Arnaud (Sopra Steria Group) - ITER
 */
public abstract class AbstractNodeWrapper {

	protected abstract String getOriginalData();

	protected abstract String getData();

	protected abstract void setData(String data);

	protected abstract void reset();

	public void update(Color colorToChange, Color newColor) {
		if (colorToChange == null || newColor == null
				|| colorToChange.equals(newColor)) {
			return;
		}

		Matcher matcher = null;
		String svgOldColor = toHexString(colorToChange.getRed(), colorToChange.getGreen(), colorToChange.getBlue());
		String svgNewColor = toHexString(newColor.getRed(), newColor.getGreen(), newColor.getBlue());
		Pattern fillPattern = Pattern.compile("(?i)" + CSSConstants.CSS_FILL_PROPERTY + ":" + svgOldColor);
		Pattern strokePattern = Pattern.compile("(?i)" + CSSConstants.CSS_STROKE_PROPERTY + ":" + svgOldColor);
		String fillReplace = CSSConstants.CSS_FILL_PROPERTY + ":" + svgNewColor;
		String strokeReplace = CSSConstants.CSS_STROKE_PROPERTY + ":" + svgNewColor;

		String data = getOriginalData();
		matcher = fillPattern.matcher(data);
		data = matcher.replaceAll(fillReplace);
		matcher = strokePattern.matcher(data);
		data = matcher.replaceAll(strokeReplace);
		data = replaceRGB(colorToChange, newColor, data);
		setData(data);
	}

	private String replaceRGB(Color oldColor, Color newColor, String data) {
		Pattern rgbPattern = Pattern
				.compile("(?i)rgb\\(([0-9]+\\.?[0-9]*)%,([0-9]+\\.?[0-9]*)%,([0-9]+\\.?[0-9]*)%\\)");
		int nr = Math.round(newColor.getRed() / 255f * 100);
		int ng = Math.round(newColor.getGreen() / 255f * 100);
		int nb = Math.round(newColor.getBlue() / 255f * 100);
		String rgbReplace = "rgb(" + nr + "%," + ng + "%," + nb + "%)";
		Matcher matcher = rgbPattern.matcher(data);
		StringBuilder sb = new StringBuilder();
		int previousEnd = 0;
		while (matcher.find()) {
			int r = Math.round(Float.valueOf(matcher.group(1)) * 255 / 100);
			int g = Math.round(Float.valueOf(matcher.group(2)) * 255 / 100);
			int b = Math.round(Float.valueOf(matcher.group(3)) * 255 / 100);
			if (r == oldColor.getRed() && g == oldColor.getGreen()
					&& b == oldColor.getBlue()) {
				int newStart = matcher.start();
				int newEnd = matcher.end();
				sb.append(data.subSequence(previousEnd, newStart));
				sb.append(rgbReplace);
				previousEnd = newEnd;
			}
		}
		sb.append(data.subSequence(previousEnd, data.length()));
		return sb.toString();
	}

	private String toHexString(int r, int g, int b) {
		return "#" + toSVGHexValue(r) + toSVGHexValue(g) + toSVGHexValue(b);
	}

	private String toSVGHexValue(int number) {
		StringBuilder builder = new StringBuilder(Integer.toHexString(number & 0xff));
		while (builder.length() < 2) {
			builder.insert(0, '0'); // pad with leading zero if needed
		}
		return builder.toString().toUpperCase();
	}

}
