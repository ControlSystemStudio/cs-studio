/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.swt.widgets.util;

import org.eclipse.swt.graphics.ImageData;

/**
 * Utility class to change image behavior like color, shape, rotation
 * management, ...
 * @author Fred Arnaud (Sopra Group)
 */
public final class ImageUtils {

	/**
	 * Constructor cannot be call because of static invocation.
	 */
	private ImageUtils() {}
	
	
	public static ImageData applyMatrix(ImageData srcData, PermutationMatrix pm) {
		double[][] matrix = pm.getMatrix();

		// point to rotate about => center of image
		double x0 = 0.5 * (srcData.width - 1);
		double y0 = 0.5 * (srcData.height - 1);

		// apply permutation to 4 corners
		int[] a = translate(0, 0, x0, y0, matrix);
		int[] b = translate(srcData.width - 1, 0, x0, y0, matrix);
		int[] c = translate(srcData.width - 1, srcData.height - 1, x0, y0, matrix);
		int[] d = translate(0, srcData.height - 1, x0, y0, matrix);

		// find new point
		int minX = findMin(a[0], b[0], c[0], d[0]);
		int minY = findMin(a[1], b[1], c[1], d[1]);
		int maxX = findMax(a[0], b[0], c[0], d[0]);
		int maxY = findMax(a[1], b[1], c[1], d[1]);
		int newWidth = maxX - minX;
		int newHeight = maxY - minY;

		ImageData newImageData = new ImageData(newWidth, newHeight,
				srcData.depth, srcData.palette);

		for (int srcX = 0; srcX < srcData.width; srcX++) {
			for (int srcY = 0; srcY < srcData.height; srcY++) {
				int destX = 0, destY = 0;
				int[] destP = translate(srcX, srcY, x0, y0, matrix);
				destX = (int) (destP[0] - minX);
				destY = (int) (destP[1] - minY);

				if (destX >= 0 && destX < newWidth && destY >= 0
						&& destY < newHeight) {
					newImageData.setPixel(destX, destY, srcData.getPixel(srcX, srcY));
					newImageData.setAlpha(destX, destY, srcData.getAlpha(srcX, srcY));
				}
			}
		}
		// Re-set the lost transparency
		newImageData.transparentPixel = srcData.transparentPixel;
		return newImageData;
	}
	
	// calculate new coordinates
	private static int[] translate(int x, int y, double x0, double y0, double[][] matrix) {
		// translate coordinates
		double[][] p = new double[2][1];
		p[0][0] = x - x0;
		p[1][0] = y - y0;
		// apply permutation
		double[][] pp = multiply(matrix, p);
		// translate back
		int[] result = new int[2];
		result[0] = (int) (pp[0][0] + x0);
		result[1] = (int) (pp[1][0] + y0);
		return result;
	}
	
	// multiply matrices
	private static double[][] multiply(double[][] m1, double[][] m2) {
		int p1 = m1.length, p2 = m2.length, q2 = m2[0].length;
		double[][] result = new double[p1][q2];
		for (int i = 0; i < p1; i++)
			for (int j = 0; j < q2; j++)
				for (int k = 0; k < p2; k++)
					result[i][j] += m1[i][k] * m2[k][j];
		return result;
	}
	
	private static int findMax(int a, int b, int c, int d) {
		int result = Math.max(a, b);
		result = Math.max(result, c);
		result = Math.max(result, d);
		return result;
	}
	
	private static int findMin(int a, int b, int c, int d) {
		int result = Math.min(a, b);
		result = Math.min(result, c);
		result = Math.min(result, d);
		return result;
	}
	
}