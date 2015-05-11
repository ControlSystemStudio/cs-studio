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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


/**
 * A very simple default matrix implementation.
 * <p>
 * This implementation is not threadsafe.
 *
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
final class DefaultMatrix
	extends Object
	implements Matrix, Serializable
{


	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 29838324L;


	/**
	 * Array of data.
	 */
	private double[][] matrix;

	/**
	 * Row permutation maps (used during inversion).
	 */
	private transient int[] indexc, indexr, ipiv;


	/**
	 * Constructor.
	 *
	 * @param rows the number of rows.
	 * @param cols the number of cols.
	 * @throws IllegalArgumentException If <code>rows</code> or
	 *         <code>cols</code> is illegal.
	 */
	DefaultMatrix( final int rows, final int cols )
		throws IllegalArgumentException
	{
		if( rows < 1 || cols < 1 )
		{
			throw new IllegalArgumentException();
		}

		// create matrix!
		this.matrix = new double[ rows ][ cols ];

		// create stuff.
		this.indexc = new int[ rows ];
		this.indexr = new int[ rows ];
		this.ipiv = new int[ rows ];
	}


	/**
	 * Get row count.
	 *
	 * @return int get row count.
	 */
	@Override
    public int getRowCount()
	{
		return ( this.matrix.length );
	}


	/**
	 * Get column count.
	 *
	 * @return int col count.
	 */
	@Override
    public int getColumnCount()
	{
		return ( this.matrix[ 0 ].length );
	}


	/**
	 * Multiply this matrix by the specified vector.
	 *
	 * @param vector the vector by which to multiply.
	 * @return double[] <code>result</code>.
	 * @throws IllegalArgumentException If <code>vector</code> has the wrong
	 *         length.
	 * @throws NullPointerException If <code>vector</code> is <code>null</code>.
	 */
	@Override
    public double[] multiply( final double[] vector )
		throws IllegalArgumentException, NullPointerException
	{
		return ( this.multiply( vector, new double[ this.getColumnCount() ] ) );
	}


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
	@Override
    public double[] multiply( final double[] vector, final double[] result )
		throws IllegalArgumentException, NullPointerException
	{
		final int row = this.getRowCount();
		final int col = this.getColumnCount();

		if( vector.length != row || result.length != col )
		{
			throw new IllegalArgumentException();
		}

		int index, jindex;

		for( index = 0; index < row; index++ )
		{
			result[ index ] = 0;
			for( jindex = 0; jindex < col; jindex++ )
			{
				result[ index ] += this.get( index, jindex ) * vector[ jindex ];
			}
		}

		return ( result );
	}


	/**
	 * Invert this matrix.
	 *
	 * @throws IllegalStateException If this matrix is singular, or not square.
	 */
	@Override
    public void invert()
		throws IllegalStateException
	{
		if( this.getColumnCount() != this.getRowCount() )
		{
			throw new IllegalStateException();
		}

		int i, icol, irow, j, k, l, ll;
		double big, dum, pivinv;

		final int n = this.getRowCount();

		for( j = 0; j < n; j++ )
		{
			this.ipiv[ j ] = 0;
		}

		irow = icol = 0;

		for( i = 0; i < n; i++ )
		{
			big = 0;

			for( j = 0; j < n; j++ )
			{
				if( this.ipiv[ j ] != 1 )
				{
					for( k = 0; k < n; k++ )
					{
						if( this.ipiv[ k ] == 0 )
						{
							if( Math.abs( this.matrix[ j ][ k ] ) >= big )
							{
								big = Math.abs( this.matrix[ j ][ k ] );
								irow = j;
								icol = k;
							}
						}
					}
				}
			}

			this.ipiv[ icol ]++;

			if( irow != icol )
			{
				for( l = 0; l < n; l++ )
				{
					swap( irow, l, icol, l );
				}
			}

			this.indexr[ i ] = irow;
			this.indexc[ i ] = icol;

			if( this.matrix[ icol ][ icol ] == 0.0d )
			{
				throw new IllegalStateException();
			}

			pivinv = 1.0 / this.matrix[ icol ][ icol ];
			this.matrix[ icol ][ icol ] = 1.0;

			for( l = 0; l < n; l++ )
			{
				this.matrix[ icol ][ l ] *= pivinv;
			}

			for( ll = 0; ll < n; ll++ )
			{
				if( ll != icol )
				{
					dum = this.matrix[ ll ][ icol ];
					this.matrix[ ll ][ icol ] = 0;

					for( l = 0; l < n; l++ )
					{
						this.matrix[ ll ][ l ] -= this.matrix[ icol ][ l ] * dum;
					}
				}
			}
		}

		for( l = ( n - 1 ); l >= 0; l-- )
		{
			if( this.indexr[ l ] != this.indexc[ l ] )
			{
				for( k = 0; k < n; k++ )
				{
					this.swap( k, this.indexr[ l ], k, this.indexc[ l ] );
				}
			}
		}
	}


	/**
	 * Swap the specified positions.
	 *
	 * @param r1 the first row position.
	 * @param c1 the first column position.
	 * @param r2 the second row position.
	 * @param c2 the second column position.
	 */
	private void swap( int r1, int c1, int r2, int c2 )
	{
		double val = this.matrix[ r1 ][ c1 ];
		this.matrix[ r1 ][ c1 ] = this.matrix[ r2 ][ c2 ];
		this.matrix[ r2 ][ c2 ] = val;
	}


	/**
	 * Get the value at the specified coordinates.
	 *
	 * @param row the row.
	 * @param col the column.
	 * @return double the value.
	 * @throws ArrayIndexOutOfBoundsException If <code>row</code> or
	 *         <code>col</code> are out of bounds.
	 */
	@Override
    public double get( final int row, final int col )
		throws ArrayIndexOutOfBoundsException
	{
		return ( this.matrix[ row ][ col ] );
	}


	/**
	 * Set the value at the specified coorindates.
	 *
	 * @param row the row.
	 * @param col the column.
	 * @param value the value.
	 * @throws ArrayIndexOutOfBoundsException If <code>row</code> or
	 *         <code>col</code> are out of bounds.
	 */
	@Override
    public void set( final int row, final int col, final double value )
		throws ArrayIndexOutOfBoundsException
	{
		this.matrix[ row ][ col ] = value;
	}


	/**
	 * A better hashcode, based on size of this matrix.
	 *
	 * @return int a better hashcode.
	 */
	@Override
	public int hashCode()
	{
		return ( this.getRowCount() ^ this.getColumnCount() );
	}


	/**
	 * A better equals.
	 *
	 * @param other the other object.
	 * @return boolean true if equal.
	 */
	@Override
	public boolean equals( final Object other )
	{
		// obvious tests...
		if( other == null )
		{
			return ( false );
		}

		if( other == this )
		{
			return ( true );
		}

		if( Matrix.class.isAssignableFrom( other.getClass() ) == false )
		{
			return ( false );
		}

		// check size first.
		Matrix that = (Matrix)other;
		if( that.getRowCount() != this.getRowCount() )
		{
			return ( false );
		}

		if( that.getColumnCount() != this.getColumnCount() )
		{
			return ( false );
		}

		for( int jindex = 0, index = 0; index < this.getRowCount(); index++ )
		{
			for( jindex = 0; jindex < this.getColumnCount(); jindex++ )
			{
				if( this.matrix[ index ][ jindex ] != that.get( index, jindex ) )
				{
					return ( false );
				}
			}
		}

		// ok, they're equal.
		return ( true );
	}


	/**
	 * To string (for debugging).
	 *
	 * @return String a string.
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		int r, c;
		final int row = this.getRowCount();
		final int col = this.getColumnCount();

		for( r = 0; r < row; r++ )
		{
			for( c = 0; c < col; c++ )
			{
				sb.append( this.matrix[ r ][ c ] );
				sb.append( ' ' );
			}

			if( r != ( row - 1 ) )
			{
				sb.append( '\n' );
			}
		}


		return ( sb.toString() );
	}


	/**
	 * Serialize this object to the specified output stream.
	 *
	 * @param out the stream to which to serialize this object.
	 * @throws IOException If this object cannot be serialized.
	 */
	private void writeObject( final ObjectOutputStream out )
		throws IOException
	{
		// Write non-transient fields.
		out.defaultWriteObject();
	}


	/**
	 * Deserialize this object from the specified stream.
	 *
	 * @param in the stream from which to read data.
	 * @throws IOException If this object cannot properly read from the specified
	 *         stream.
	 * @throws ClassNotFoundException If deserialization tries to classload an
	 *         undefined class.
	 */
	private void readObject( final ObjectInputStream in )
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		// get row count... matrix object will exist by now.
		int rows = this.getRowCount();

		// create stuff.
		this.indexc = new int[ rows ];
		this.indexr = new int[ rows ];
		this.ipiv = new int[ rows ];
	}


}
