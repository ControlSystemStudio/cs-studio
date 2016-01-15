/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.batik.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.batik.css.engine.Rule;
import org.apache.batik.css.engine.SVGCSSEngine;
import org.apache.batik.css.engine.StyleDeclaration;
import org.apache.batik.css.engine.StyleRule;
import org.apache.batik.css.engine.StyleSheet;
import org.apache.batik.css.engine.value.FloatValue;
import org.apache.batik.css.engine.value.RGBColorValue;
import org.apache.batik.css.engine.value.Value;
import org.eclipse.swt.graphics.Color;

/**
 * Manages the update of CSS defined colors of {@link StyleSheet}. Always updates the original style.
 *
 * @author Fred Arnaud (Sopra Steria Group) - ITER
 */
public class StyleSheetCSSHandler implements ICSSHandler {

    private final Map<StyleRule, CloneableStyleDeclaration> originalStyles;

    public StyleSheetCSSHandler(SVGCSSEngine cssEngine, StyleSheet styleSheet) {
        this.originalStyles = new HashMap<StyleRule, CloneableStyleDeclaration>();
        int numRules = styleSheet.getSize();
        for (int ruleIndex = 0; ruleIndex < numRules; ruleIndex++) {
            Rule rule = styleSheet.getRule(ruleIndex);
            if (rule instanceof StyleRule) {
                StyleRule sr = ((StyleRule) rule);
                StyleDeclaration sd = sr.getStyleDeclaration();
                originalStyles.put(sr, new CloneableStyleDeclaration(sd));
            }
        }
    }

    @Override
    public void updateCSSColor(Color colorToChange, Color newColor) {
        if (colorToChange == null || newColor == null || colorToChange.equals(newColor)) {
            return;
        }

        FloatValue newRedValue = new FloatValue((short) 1, (float) newColor.getRed());
        FloatValue newGreenValue = new FloatValue((short) 1, (float) newColor.getGreen());
        FloatValue newBlueValue = new FloatValue((short) 1, (float) newColor.getBlue());
        RGBColorValue newRGBColorValue = new RGBColorValue(newRedValue, newGreenValue, newBlueValue);

        for (Entry<StyleRule, CloneableStyleDeclaration> entry : originalStyles.entrySet()) {
            StyleRule sr = entry.getKey();
            StyleDeclaration sdClone = entry.getValue().clone();
            int sdlen = sdClone.size();
            for (int sdindex = 0; sdindex < sdlen; sdindex++) {
                Value val = sdClone.getValue(sdindex);
                if (val instanceof RGBColorValue) {
                    RGBColorValue colorVal = (RGBColorValue) val;
                    if (isSameColor(colorVal, colorToChange)) {
                        sdClone.put(sdindex, newRGBColorValue, sdClone.getIndex(sdindex), sdClone.getPriority(sdindex));
                    }
                }
            }
            sr.setStyleDeclaration(sdClone);
        }
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
        for (Entry<StyleRule, CloneableStyleDeclaration> entry : originalStyles.entrySet()) {
            entry.getKey().setStyleDeclaration(entry.getValue());
        }
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
