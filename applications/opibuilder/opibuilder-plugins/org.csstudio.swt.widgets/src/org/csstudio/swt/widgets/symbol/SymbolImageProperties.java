/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.symbol;

import org.csstudio.swt.widgets.symbol.util.PermutationMatrix;
import org.eclipse.swt.graphics.Color;

/**
 * Handler for {@link SymbolImage} properties.
 *
 * @author Fred Arnaud (Sopra Group)
 */
public class SymbolImageProperties {

    private int topCrop = 0;
    private int bottomCrop = 0;
    private int leftCrop = 0;
    private int rightCrop = 0;
    private boolean stretch = true;
    private boolean autoSize = true;
    private PermutationMatrix matrix;
    private boolean animationDisabled = false;
    private boolean alignedToNearestSecond = false;
    private Color backgroundColor;
    private Color colorToChange;

    public int getTopCrop() {
        return topCrop;
    }

    public void setTopCrop(int topCrop) {
        this.topCrop = topCrop;
    }

    public int getBottomCrop() {
        return bottomCrop;
    }

    public void setBottomCrop(int bottomCrop) {
        this.bottomCrop = bottomCrop;
    }

    public int getLeftCrop() {
        return leftCrop;
    }

    public void setLeftCrop(int leftCrop) {
        this.leftCrop = leftCrop;
    }

    public int getRightCrop() {
        return rightCrop;
    }

    public void setRightCrop(int rightCrop) {
        this.rightCrop = rightCrop;
    }

    public boolean isStretch() {
        return stretch;
    }

    public void setStretch(boolean stretch) {
        this.stretch = stretch;
    }

    public boolean isAutoSize() {
        return autoSize;
    }

    public void setAutoSize(boolean autoSize) {
        this.autoSize = autoSize;
    }

    public PermutationMatrix getMatrix() {
        return matrix;
    }

    public void setMatrix(PermutationMatrix matrix) {
        this.matrix = matrix;
    }

    public boolean isAnimationDisabled() {
        return animationDisabled;
    }

    public void setAnimationDisabled(boolean animationDisabled) {
        this.animationDisabled = animationDisabled;
    }

    public boolean isAlignedToNearestSecond() {
        return alignedToNearestSecond;
    }

    public void setAlignedToNearestSecond(boolean alignedToNearestSecond) {
        this.alignedToNearestSecond = alignedToNearestSecond;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Color getColorToChange() {
        return colorToChange;
    }

    public void setColorToChange(Color colorToChange) {
        this.colorToChange = colorToChange;
    }

}
