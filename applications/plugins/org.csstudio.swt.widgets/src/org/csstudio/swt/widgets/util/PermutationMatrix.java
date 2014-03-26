/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.swt.widgets.util;

import java.util.Arrays;

/**
 * Permuation matrix used to flip/rotate images
 * @author Fred Arnaud (Sopra Group)
 *
 */
public class PermutationMatrix {

	private final double[][] matrix;
	
	public PermutationMatrix(double x1, double y1, double x2, double y2) {
		matrix = new double[2][2];
		matrix[0][0] = x1;
		matrix[0][1] = y1;
		matrix[1][0] = x2;
		matrix[1][1] = y2;
	}
	
	public PermutationMatrix(double[][] matrix) {
		this.matrix = matrix;
	}
	
	/**
	 * Generate [2,2] identity matrix
	 */
	public static PermutationMatrix generateIdentityMatrix() {
		final double[][] matrix = new double[][] { { 1, 0 }, { 0, 1 } };
		return new PermutationMatrix(matrix);
	}
	
	/**
	 * Generate horizontal flip [2,2] matrix
	 */
	public static PermutationMatrix generateFlipVMatrix() {
		final double[][] matrix = new double[][] { { 1, 0 }, { 0, -1 } };
		return new PermutationMatrix(matrix);
	}
	
	/**
	 * Generate vertical flip [2,2] matrix
	 */
	public static PermutationMatrix generateFlipHMatrix() {
		final double[][] matrix = new double[][] { { -1, 0 }, { 0, 1 } };
		return new PermutationMatrix(matrix);
	}

	/**
	 * Generate rotation [2,2] matrix
	 */
	public static PermutationMatrix generateRotationMatrix(double angleInDegree) {
		double angleInRadian = angleInDegree * Math.PI / 180.0;
		double sin = Math.sin(angleInRadian);
		double cos = Math.cos(angleInRadian);
		double[][] matrix = new double[2][2];

		matrix[0][0] = cos;
		matrix[0][1] = -sin;
		matrix[1][0] = sin;
		matrix[1][1] = cos;

		return new PermutationMatrix(matrix);
	}
	
	public PermutationMatrix multiply(PermutationMatrix pm) {
		double[][] m1 = getMatrix();
		double[][] m2 = pm.getMatrix();
		
		int p1 = m1.length, p2 = m2.length, q2 = m2[0].length;
		double[][] result = new double[p1][q2];
		for (int i = 0; i < p1; i++)
			for (int j = 0; j < q2; j++)
				for (int k = 0; k < p2; k++)
					result[i][j] += m1[i][k] * m2[k][j];
		return new PermutationMatrix(result);
	}

	public double[][] getMatrix() {
		return matrix;
	}
	
	public void roundToIntegers() {
		matrix[0][0] = Math.round(matrix[0][0]);
		matrix[0][1] = Math.round(matrix[0][1]);
		matrix[1][0] = Math.round(matrix[1][0]);
		matrix[1][1] = Math.round(matrix[1][1]);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(matrix);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PermutationMatrix other = (PermutationMatrix) obj;
		if (!Arrays.deepEquals(matrix, other.matrix))
			return false;
		return true;
	}
	
}