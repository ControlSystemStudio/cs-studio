/*
 * $Id$
 * 
 * Copyright (c) 2006 Fran Lattanzio
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.teneighty.lm;


/**
 * A simple (double precision) matrix interface.
 * <p>
 * See <code>MatrixFactory</code> for information on which implementation the
 * <code>LevenbergMarquardt</code> class will use.
 * <p>
 * <i>You do not need to implement this class!</i> A (very simple) default
 * implementation exists. This class exists to provide hooks for higher
 * performance matrix implementations; in fact, it's fairly trivial to wrap an
 * implementation of this interface around an existing (and higher performance)
 * matrix implementation. (You must also create an associated
 * <code>MatrixFactory</code> implementation, as well as setting some system
 * properties, but doing so is beyond trivial.) See the package manifest for
 * info on how precisely to do this.
 * <p>
 * Note that I choose to use this arrangement, rather than simply use an
 * existing matrix implementation, solely so that this package can be used
 * without an external requirements. At the same time, you can also substitute a
 * high-performance implementation with almost zero overhead. So, basically,
 * this give you, the user, the best of both worlds (in a non-hermaphroditic
 * way, with apologies to Randall).
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 * @see org.teneighty.lm.MatrixFactory
 */
public interface Matrix
{


	/**
	 * Invert this matrix.
	 * 
	 * @throws IllegalStateException If this matrix is singular.
	 */
	public void invert()
		throws IllegalStateException;


	/**
	 * Multiply this matrix by the specified vector.
	 * 
	 * @param vector the vector by which to multiply.
	 * @return double[] <code>result</code>.
	 * @throws IllegalArgumentException If <code>vector</code> has the wrong
	 *         length.
	 * @throws NullPointerException If <code>vector</code> is <code>null</code>
	 */
	public double[] multiply( final double[] vector )
		throws IllegalArgumentException, NullPointerException;


	/**
	 * Multiply this matrix by the specified vector, storing it in the specified
	 * result vector.
	 * 
	 * @param vector the vector by which to multiply.
	 * @param result the place to put the results.
	 * @return double[] <code>result</code>.
	 * @throws IllegalArgumentException If <code>vector</code> or
	 *         <code>result</code> have the wrong length.
	 * @throws NullPointerException If <code>vector</code> or
	 *         <code>result</code> are <code>null</code>.
	 */
	public double[] multiply( final double[] vector, final double[] result )
		throws IllegalArgumentException, NullPointerException;


	/**
	 * Get row count.
	 * 
	 * @return int get row count.
	 */
	public int getRowCount();


	/**
	 * Get column count.
	 * 
	 * @return int col count.
	 */
	public int getColumnCount();


	/**
	 * Get the value at the specified coordinates.
	 * 
	 * @param row the row.
	 * @param col the column.
	 * @return double the value.
	 * @throws ArrayIndexOutOfBoundsException If <code>row</code> or
	 *         <code>col</code> are out of bounds.
	 */
	public double get( int row, int col )
		throws ArrayIndexOutOfBoundsException;


	/**
	 * Set the value at the specified coorindates.
	 * 
	 * @param row the row.
	 * @param col the column.
	 * @param value the value.
	 * @throws ArrayIndexOutOfBoundsException If <code>row</code> or
	 *         <code>col</code> are out of bounds.
	 */
	public void set( int row, int col, double value )
		throws ArrayIndexOutOfBoundsException;


}
