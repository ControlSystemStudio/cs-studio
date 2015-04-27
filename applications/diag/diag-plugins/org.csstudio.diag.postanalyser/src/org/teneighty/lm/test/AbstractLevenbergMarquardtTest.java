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

import junit.framework.TestCase;

import java.util.Random;


/**
 * An abstract test case, from which other LM test cases can extend.
 *
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public abstract class AbstractLevenbergMarquardtTest
	extends TestCase
{
	
	
	/**
	 * Global epsilon tolerance.
	 */
	public static final double EPSILON = 1.0e-2;
	
	
	/**
	 * Random number gneerator.
	 */
	protected final Random random;
	
	
	/**
	 * Constructor.
	 * <p>
	 * Should be considered <code>private protected</code>.
	 * 
	 * @param name test case name.
	 */
	protected AbstractLevenbergMarquardtTest( final String name )
	{
		super( name );
		
		// create random.
		this.random = new Random( System.currentTimeMillis() );
	}

	
	/**
	 * Create a random array of the specified length.
	 * 
	 * @param bound the bound.
	 * @param length the length.
	 * @return double[] random array...
	 */
	protected final double[] randomDoubleArray( final int length, final int bound )
	{
		double[] ret = new double[ length ];
		for( int index = 0; index < length; index++ )
		{
			ret[ index ] = this.nextDouble( bound );
		}
		
		return( ret );
	}

	
	/**
	 * Create a random array of the specified length.
	 * <p>
	 * Values are uniformly distributed between 0 and <code>bound</code>.
	 * 
	 * @param bound the maximum value.
	 * @param dim the dimension.
	 * @param length the length.
	 * @return double[] random array...
	 */
	protected final double[][] randomDoubleArray( final int dim, final int length, final int bound )
	{
		double[][] ret = new double[ dim ][ length ];
		for( int index = 0; index < length; index++ )
		{
			for( int jindex = 0; jindex < dim; jindex++ )
			{
				ret[ jindex ][ index ] = this.nextDouble( bound );
			}
		}
		
		return( ret );
	}

	
	/**
	 * Next double, distributed uniformly between <code>0</code> and <code>bound</code>.
	 * 
	 * @param bound the bound.
	 * @return double a happy random double.
	 */
	protected final double nextDouble( final int bound )
	{
		return( this.random.nextDouble() * bound );
	}
	

	/**
	 * Check the specified parameter vectors against each other, using default epsilon value.
	 * 
	 * @param original the original, known vector.
	 * @param lm the vector found by LM.
	 * @param eps the epsilon to use.
	 * @return true if passed.
	 */
	protected final boolean checkVectors( final double[] original, final double[] lm )
	{
		return( this.checkVectors( original, lm, EPSILON ) );
	}
	

	/**
	 * Check the specified parameter vectors against each other...
	 * 
	 * @param original the original, known vector.
	 * @param lm the vector found by LM.
	 * @param eps the epsilon to use.
	 * @return true if passed.
	 */
	protected final boolean checkVectors( final double[] original, final double[] lm, final double eps )
	{
		for( int index = 0; index < original.length; index++ )
		{
			if( Math.abs( original[ index ] - lm[ index ] ) >= eps )
			{
				return( false );
			}
			
			//assertTrue( "Bad parameter", eps <= Math.abs( original[ index ] - lm[ index ] ) );
		}
		
		return( true );
	}
	
	
}
