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
 * A matrix factory that creates instances of <code>DefaultMatrix</code>.
 *
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
final class DefaultMatrixFactory
	extends MatrixFactory
{
	
	
	/**
	 * Constructor.
	 * <p>
	 * Does nothing.
	 */
	DefaultMatrixFactory()
	{
		super();
	}
	
	
	/**
	 * Create and return a new <code>Matrix</code> instance, of the specified
	 * size.
	 * 
	 * @param rows the number of rows.
	 * @param cols the number of columns.
	 * @return Matrix a new Matrix instance of the specified size.
	 * @throws IllegalArgumentException If <code>rows</code> or
	 *         <code>cols</code> is illegal.
	 */
	@Override
	public Matrix newMatrix( int rows, int cols )
		throws IllegalArgumentException
	{
		if( rows < 1 || cols < 1 )
		{
			throw new IllegalArgumentException();
		}
		
		return( new DefaultMatrix( rows, cols ) );
	}
	
	
	/**
	 * Simple equals implementation. Two instances of this class are always considered equal.
	 * 
	 * @param other the other object.
	 * @return boolean <code>true</code> if equal.
	 */
	@Override
	public boolean equals( final Object other )
	{
		if( other == null )
		{
			return( false );
		}
		
		if( other == this )
		{
			return( true );
		}
		
		return( other.getClass().equals( this.getClass() ) == true );		
	}
	
	
	/**
	 * Get a hashcode inline with equals.
	 * 
	 * @return int 1 always.
	 */
	@Override
	public int hashCode()
	{
		return( 1 );
	}
	

}
