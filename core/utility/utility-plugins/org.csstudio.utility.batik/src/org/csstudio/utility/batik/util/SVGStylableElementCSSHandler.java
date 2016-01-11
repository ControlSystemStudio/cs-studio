/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.batik.util;

import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.css.engine.StyleDeclaration;
import org.apache.batik.css.engine.value.FloatValue;
import org.apache.batik.css.engine.value.RGBColorValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.dom.svg.SVGStylableElement;
import org.eclipse.swt.graphics.Color;

/**
 * Manages the update of CSS defined colors of {@link SVGStylableElement}. Always updates the original style.
 *
 * @author Fred Arnaud (Sopra Steria Group) - ITER
 */
public class SVGStylableElementCSSHandler implements ICSSHandler {

    private final CSSEngine cssEngine;
    private final SVGStylableElement element;
    private final CloneableStyleDeclaration originalStyle;
    private final String originalCSSText;

    public SVGStylableElementCSSHandler(CSSEngine cssEngine, SVGStylableElement element) {
        this.cssEngine = cssEngine;
        this.element = element;
        this.originalCSSText = element.getStyle().getCssText();
        SVGStylableElement.StyleDeclaration embedStyle = (SVGStylableElement.StyleDeclaration) element.getStyle();
        this.originalStyle = new CloneableStyleDeclaration(embedStyle.getStyleDeclaration());
    }

    public void updateCSSColor(Color colorToChange, Color newColor) {
        if (colorToChange == null || newColor == null || colorToChange.equals(newColor)) {
            return;
        }

        FloatValue newRedValue = new FloatValue((short) 1, (float) newColor.getRed());
        FloatValue newGreenValue = new FloatValue((short) 1, (float) newColor.getGreen());
        FloatValue newBlueValue = new FloatValue((short) 1, (float) newColor.getBlue());
        RGBColorValue newRGBColorValue = new RGBColorValue(newRedValue, newGreenValue, newBlueValue);

        StyleDeclaration sd = originalStyle.clone();
        int sdlen = sd.size();
        for (int sdindex = 0; sdindex < sdlen; sdindex++) {
            Value val = sd.getValue(sdindex);
            if (val instanceof RGBColorValue) {
                RGBColorValue colorVal = (RGBColorValue) val;
                if (isSameColor(colorVal, colorToChange)) {
                    sd.put(sdindex, newRGBColorValue, sd.getIndex(sdindex), sd.getPriority(sdindex));
                }
            }
        }
        element.getStyle().setCssText(sd.toString(cssEngine));
    }

    private boolean isSameColor(RGBColorValue colorVal, Color swtColor) {
        if (colorVal.getCssText().contains("%")) {
            int nr = Math.round(swtColor.getRed() / 255f * 100);
            int ng = Math.round(swtColor.getGreen() / 255f * 100);
            int nb = Math.round(swtColor.getBlue() / 255f * 100);
            int or = Math.round(colorVal.getRed().getFloatValue());
            int og = Math.round(colorVal.getGreen().getFloatValue());
            int ob = Math.round(colorVal.getBlue().getFloatValue());
            if (or == nr && og == ng && ob == nb) {
                return true;
            }
        } else if (colorVal.getRed().getFloatValue() == swtColor.getRed()
                && colorVal.getGreen().getFloatValue() == swtColor.getGreen()
                && colorVal.getBlue().getFloatValue() == swtColor.getBlue()) {
            return true;
        }
        return false;
    }

    @Override
    public void resetCSSStyle() {
        element.getStyle().setCssText(originalCSSText);
    }

    protected class CloneableStyleDeclaration extends StyleDeclaration {

        public CloneableStyleDeclaration(StyleDeclaration sd) {
            this.count = sd.size();
            this.values = new Value[count];
            for (int idx = 0; idx < count; idx++) {
                this.values[idx] = sd.getValue(idx);
            }
            this.indexes = new int[count];
            for (int idx = 0; idx < count; idx++) {
                this.indexes[idx] = sd.getIndex(idx);
            }
            this.priorities = new boolean[count];
            for (int idx = 0; idx < count; idx++) {
                this.priorities[idx] = sd.getPriority(idx);
            }
        }

        public CloneableStyleDeclaration clone() {
            return new CloneableStyleDeclaration(this);
        }

    }

}
