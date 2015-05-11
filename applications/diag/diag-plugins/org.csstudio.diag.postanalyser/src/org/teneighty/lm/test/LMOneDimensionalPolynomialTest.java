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

import org.teneighty.lm.CostFunction;
import org.teneighty.lm.LevenbergMarquardt;


/**
 * Test the LM fitter.
 *
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
@SuppressWarnings("nls")
public class LMOneDimensionalPolynomialTest
	extends AbstractLevenbergMarquardtTest
{


	/**
	 * The number of 1-dimensional polynomial tests to run.
	 */
	private static final int TIMES = 100;

	/**
	 * Minimum 1d poly test degree.
	 */
	private static final int MIN_DEGREE = 1;

	/**
	 * Maximum 1d poly test degree.
	 */
	private static final int MAX_DEGREE = 8;

	/**
	 * Poly 1d point count.
	 */
	private static final int POINTS = 100;

	/**
	 * Poly 1d coefficient magnitude.
	 */
	private static final int COEFFICIENT_BOUND = 100;


	/**
	 * Constructor.
	 *
	 * @param name the name.
	 */
	public LMOneDimensionalPolynomialTest( final String name )
	{
		super( name );
	}


	/**
	 * Test single dimension polynomial.
	 */
	public void testPolynomial()
	{
		System.out.print( "Starting 1-dimensional polynomial test" );

		int degree = 0;
		int passed = 0;
		int frac = TIMES / 10;

		long then = System.currentTimeMillis();

		for( int index = 0; index < TIMES; index++ )
		{
			degree = MIN_DEGREE + this.random.nextInt( MAX_DEGREE - MIN_DEGREE );
			passed += this.runOneDimensionPolynomial( degree, POINTS ) ? 1 : 0;

			if( ( index % frac ) == 0 )
			{
				System.out.print( '.' );
			}
		}
		System.out.println();

		long now = System.currentTimeMillis();

		// print a report
		System.out.println( "One Dimensional Polynomial Test" );
		System.out.println( "-------------------------------" );
		System.out.println( "Tests run:         " + TIMES );
		System.out.println( "Min degree:        " + MIN_DEGREE );
		System.out.println( "Max degree:        " + MAX_DEGREE );
		System.out.println( "Coefficient Bound: " + COEFFICIENT_BOUND );
		System.out.println( "Data points:       " + POINTS );
		System.out.println( "Epsilon value:     " + EPSILON );
		System.out.println( "Time taken:        " + ( now - then ) + " ms" );
		System.out.println( "Percentage passed: " + (int)( 100 * passed / TIMES ) + "%" );
		System.out.println();
	}


	/**
	 * Test a single dimension polynomial, of the specified degree.
	 *
	 * @param deg the degree.
	 * @param data_size the number of data points to simulate.
	 * @return boolean true if passed.
	 */
	private boolean runOneDimensionPolynomial( final int deg, final int data_size )
	{
		// create cost function...
		CostPolynomial cost = new CostPolynomial( deg );

		// create random params.
		double[] params = this.randomDoubleArray( deg + 1, COEFFICIENT_BOUND );

		// make our guess as lame as possible.
		double[] guess = new double[ ( deg + 1 ) ];
		for( int index = 0; index < guess.length; index++ )
		{
			guess[ index ] = 1;
		}

		// create new noisy polynomial.
		NoisyPolynomial noise = new NoisyPolynomial( this.random, params );

		// create some data points...
		// double[][] points = this.randomDoubleArray( data_size, 1 );
		double[][] points = new double[ data_size ][ 1 ];
		for( int index = 0; index < data_size; index++ )
		{
			points[ index ][ 0 ] = index;
		}

		// fill in values
		double[] values = new double[ data_size ];
		for( int index = 0; index < data_size; index++ )
		{
			values[ index ] = noise.evaluate( points[ index ] );
		}

		// create new LM solver.
		LevenbergMarquardt lm = new LevenbergMarquardt( data_size, 1 );
		lm.setPoints( points );
		lm.setCostFunction( cost );
		lm.setValues( values );
		lm.setGuess( guess );

		// solve it.
		lm.solve();

		// get params.
		double[] lm_params = lm.getParameters();

		// check vectors.
		return( this.checkVectors( params, lm_params ) );
	}


	/**
	 * A cost polynomial, of the specified degree.
	 *
	 * @author Fran Lattanzio
	 * @version $Revision$ $Date$
	 */
	static class CostPolynomial
		extends Object
		implements CostFunction
	{


		/**
		 * The degree of this polynomial.
		 */
		private int degree;


		/**
		 * Constructor.
		 *
		 * @param d the degree.
		 */
		public CostPolynomial( final int d )
		{
			super();

			// store degree.
			this.degree = d;
		}


		/**
		 * Evaluate the cost function at the specified tuple.
		 *
		 * @param values the vector of data to evaluate.
		 * @param params vector containing the current parameters of variation.
		 * @return double the value of this function.
		 */
		@Override
        public double evaluate( double[] values, double[] params )
		{
			double pow = values[ 0 ];
			double result = 0;

			for( int index = 0; index < this.degree + 1; index++ )
			{
				if( index == 0 )
				{
					result += params[ ( this.degree - index ) ];
				}
				else
				{
					result += pow * params[ ( this.degree - index ) ];
					pow *= values[ 0 ];
				}
			}

			return ( result );
		}


		/**
		 * Returns the derivative of this function, with respect to the
		 * <code>ith</code> <b>parameter</b>, evaluated at the specified tuple.
		 *
		 * @param values the vector of data to evaluate.
		 * @param params vector containing the current parameters of variation.
		 * @param ith the parameter (number) with respect to which the derivative is
		 *        taken.
		 * @return double the value of this function.
		 */
		@Override
        public double derive( double[] values, double[] params, int ith )
		{
			if( ith == this.degree )
			{
				return ( 1 );
			}

			return ( Math.pow( values[ 0 ], ( this.degree - ith ) ) );
		}


		/**
		 * Get the parameter count.
		 *
		 * @return int the param count.
		 */
		@Override
        public int getParameterCount()
		{
			return ( this.degree + 1 );
		}

	}


	/**
	 * A random, noisy polynomial that we will attempt to match...
	 *
	 * @author Fran Lattanzio
	 * @version $Revision$ $Date$
	 */
	static class NoisyPolynomial
		extends Object
	{

		/**
		 * Factors.
		 */
		private double[] factors;

		/**
		 * Degree.
		 */
		private int deg;


		/**
		 * Constructor.
		 *
		 * @param rand a random.
		 * @param factors the factors.
		 */
		NoisyPolynomial( final Random rand, final double[] factors )
		{
			super();

			// store stuff.
			this.factors = factors;
			this.deg = factors.length - 1;
		}


		/**
		 * Eval, at the specified point.
		 *
		 * @param input the input.
		 * @return double the value.
		 */
		double evaluate( double[] input )
		{
			double pow = input[ 0 ];
			double val = 0;

			for( int index = 0; index < this.factors.length; index++ )
			{
				if( index == 0 )
				{
					val += this.factors[ ( this.deg - index ) ];
				}
				else
				{
					val += pow * this.factors[ ( this.deg - index ) ];
					pow *= input[ 0 ];
				}
			}

			return ( val );
		}


	}


}
