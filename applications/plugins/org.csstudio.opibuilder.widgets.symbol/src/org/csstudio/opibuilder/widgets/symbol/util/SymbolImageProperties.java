/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.opibuilder.widgets.symbol.util;

import org.csstudio.opibuilder.widgets.symbol.image.AbstractSymbolImage;

/**
 * Handler for {@link AbstractSymbolImage} properties.
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class SymbolImageProperties {

	private int topCrop = 0;
	private int bottomCrop = 0;
	private int leftCrop = 0;
	private int rightCrop = 0;
	private boolean stretch = true;
	private boolean autoSize = true;
	private int degree;
	private boolean flipV;
	private boolean flipH;
	private PermutationMatrix matrix;
	
	public void fillSymbolImage(AbstractSymbolImage asi) {
		asi.setTopCrop(topCrop);
		asi.setBottomCrop(bottomCrop);
		asi.setLeftCrop(leftCrop);
		asi.setRightCrop(rightCrop);
		asi.setStretch(stretch);
		asi.setAutoSize(autoSize);
		asi.setDegree(degree);
		asi.setFlipV(flipV);
		asi.setFlipH(flipH);
		asi.setPermutationMatrix(matrix);
	}

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

	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}

	public boolean isFlipV() {
		return flipV;
	}

	public void setFlipV(boolean flipV) {
		this.flipV = flipV;
	}

	public boolean isFlipH() {
		return flipH;
	}

	public void setFlipH(boolean flipH) {
		this.flipH = flipH;
	}

	public PermutationMatrix getMatrix() {
		return matrix;
	}

	public void setMatrix(PermutationMatrix matrix) {
		this.matrix = matrix;
	}

}
