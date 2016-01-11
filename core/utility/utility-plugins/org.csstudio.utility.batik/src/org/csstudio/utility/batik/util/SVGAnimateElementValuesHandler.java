/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.batik.util;

import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.dom.svg.SVGOMAnimateElement;
import org.csstudio.utility.batik.Activator;
import org.eclipse.swt.graphics.Color;
import org.w3c.dom.svg.SVGAnimateElement;

/**
 * Manages the update of defined colors values of {@link SVGAnimateElement}.
 * Always updates the original values.
 *
 * @author Fred Arnaud (Sopra Steria Group) - ITER
 */
public class SVGAnimateElementValuesHandler implements ICSSHandler {

    private final SVGAnimateElement element;
    private final String originalValuesStr;

    public SVGAnimateElementValuesHandler(CSSEngine cssEngine,
            SVGAnimateElement element) {
        this.element = element;
        this.originalValuesStr = element.getAttribute("values");
    }

    public void updateCSSColor(Color colorToChange, Color newColor) {
        if (colorToChange == null || newColor == null
                || colorToChange.equals(newColor)) {
            return;
        }
        try {
            String newValuesStr = replaceValues(originalValuesStr, colorToChange, newColor);
            element.setAttribute("values", newValuesStr);
            // Set context to null to force hot refresh of animation
            ((SVGOMAnimateElement) element).setSVGContext(null);
        } catch (Exception e) {
            Activator.getLogger().log(Level.SEVERE, e.getMessage());
        }
    }

    private String replaceValues(String originalValues, Color colorToChange,
            Color newColor) {
        StringBuilder sb = new StringBuilder();
        String[] values = originalValues.split(";");
        Pattern rgbPattern = Pattern.compile("rgb\\(([^\\)]+)\\)");
        for (String value : values) {
            String newValue = value.trim();
            Matcher matcher = rgbPattern.matcher(value.trim());
            if (matcher.matches()) {
                String rgbStr = matcher.group(1).trim();
                String[] rgb = rgbStr.split(",");
                if (rgbStr.contains("%")) {
                    int cr = Math.round(colorToChange.getRed() / 255f * 100);
                    int cg = Math.round(colorToChange.getGreen() / 255f * 100);
                    int cb = Math.round(colorToChange.getBlue() / 255f * 100);
                    int or = Math.round(Float.valueOf(rgb[0].replace('%', ' ').trim()));
                    int og = Math.round(Float.valueOf(rgb[1].replace('%', ' ').trim()));
                    int ob = Math.round(Float.valueOf(rgb[2].replace('%', ' ').trim()));
                    if (or == cr && og == cg && ob == cb) {
                        int nr = Math.round(newColor.getRed() / 255f * 100);
                        int ng = Math.round(newColor.getGreen() / 255f * 100);
                        int nb = Math.round(newColor.getBlue() / 255f * 100);
                        newValue = "rgb(" + nr + "%," + ng + "%," + nb + "%)";
                    }
                } else {
                    int cr = colorToChange.getRed();
                    int cg = colorToChange.getGreen();
                    int cb = colorToChange.getBlue();
                    int or = Integer.valueOf(rgb[0].trim());
                    int og = Integer.valueOf(rgb[1].trim());
                    int ob = Integer.valueOf(rgb[2].trim());
                    if (or == cr && og == cg && ob == cb) {
                        int nr = newColor.getRed();
                        int ng = newColor.getGreen();
                        int nb = newColor.getBlue();
                        newValue = "rgb(" + nr + "," + ng + "," + nb + ")";
                    }
                }
            } else if (value.trim().startsWith("#")) {
                String svgOldColor = toHexString(colorToChange.getRed(), colorToChange.getGreen(), colorToChange.getBlue());
                String svgNewColor = toHexString(newColor.getRed(), newColor.getGreen(), newColor.getBlue());
                if (svgOldColor.equals(value.trim())) {
                    newValue = svgNewColor;
                }
            }
            sb.append(newValue + ";");
        }
        if (sb.length() == 0) {
            return originalValues;
        }
        return sb.substring(0, sb.length() - 1).toString();
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

    @Override
    public void resetCSSStyle() {
        element.setAttribute("values", originalValuesStr);
        ((SVGOMAnimateElement) element).setSVGContext(null);
    }

}
