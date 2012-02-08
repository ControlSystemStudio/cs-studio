package org.teneighty.lm.test;

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

import java.util.Random;

import junit.framework.TestCase;

import org.teneighty.lm.Matrix;
import org.teneighty.lm.MatrixFactory;


/**
 * Matrix test case.
 *
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
@SuppressWarnings("nls")
public class MatrixTest
	extends TestCase
{


	/**
	 * Epsilon.
	 */
	private static final double EPS = 1.0e-10;

	/**
	 * Times to test.
	 */
	private static final int TIMES = 100;

	/**
	 * Min size.
	 */
	private static final int MIN = 3;

	/**
	 * Max size.
	 */
	private static final int MAX = 50;


	/**
	 * Random.
	 */
	private Random random;


	/**
	 * Constructor.
	 *
	 * @param name the test name.
	 */
	public MatrixTest( final String name )
	{
		super( name );

		// create random...
		this.random = new Random( System.currentTimeMillis() );
	}


	/**
	 * Test inverse.
	 */
	public void testInverse()
	{
		int size = 0;
		for( int index = 0; index < TIMES; index++ )
		{
			size = this.random.nextInt( MAX - MIN ) + MIN;
			this.runSize( size );
		}
	}


	/**
	 * Run the specified size.
	 *
	 * @param size the size.
	 */
    private void runSize( int size )
	{
		MatrixFactory mf = MatrixFactory.getInstance();
		Matrix matrix = mf.newMatrix( size, size );
		Matrix orig = mf.newMatrix( size, size );
		Matrix result = mf.newMatrix( size, size );


		final int row = matrix.getRowCount();
		final int col = matrix.getColumnCount();

		int r, c, c2;
		double val;

		for( r = 0; r < row; r++ )
		{
			for( c = 0; c < col; c++ )
			{
				matrix.set( r, c, this.random.nextInt( 1000 ) );
			}
		}

		copy( matrix, orig );

		try
		{
			matrix.invert();
		}
		catch( IllegalStateException ise )
		{
			// just bad luck, really.
		}

		for( r = 0; r < row; r++ )
		{
			for( c = 0; c < col; c++ )
			{
				val = 0;
				for( c2 = 0; c2 < col; c2++ )
				{
					val = val + orig.get( r, c2 ) * matrix.get( c2, c );
				}

				result.set( r, c, val );
			}
		}

		// check that result = I.
		for( r = 0; r < row; r++ )
		{
			for( c = 0; c < col; c++ )
			{
				val = result.get( r, c );

				if( r == c )
				{
					assertTrue( "Bad diagonal: " + val, Math.abs( 1 - val ) < EPS );
				}
				else
				{
					assertTrue( "Bad non-diagonal: " + val, Math.abs( val ) < EPS );
				}
			}
		}
	}


	/**
	 * Copy matrix.
	 *
	 * @param src the source.
	 * @param dest the destination.
	 */
	private static void copy( final Matrix src, final Matrix dest )
	{
		final int row = src.getRowCount();
		final int col = src.getColumnCount();

		int r, c;

		for( r = 0; r < row; r++ )
		{
			for( c = 0; c < col; c++ )
			{
				dest.set( r, c, src.get( r, c ) );
			}
		}
	}


}
