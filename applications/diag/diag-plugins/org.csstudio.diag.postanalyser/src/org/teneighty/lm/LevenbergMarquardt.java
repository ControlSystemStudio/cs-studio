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

import java.util.Arrays;


/**
 * This class implements the Levenberg-Marquardt algorithm.
 * <p>
 * Unlike most Levenberg-Marquardt implementations, this one is actually object
 * oriented, and maintains a fairly significant amount of internal state. (c.f.
 * A "normal" implementation which is simply a static method to which to pass
 * big blobs of data and, in return, receive an optimized parameter vector.)
 * Using this class is thus quite different from using a "normal"
 * implementation. Let's go through how to use this class, step-by-step (with
 * lots of extraneous prose along the way):
 * <p>
 * Note that instances of this class are not safe for use by multiple threads.
 * To be slightly more precise, concurrent access by multiple threads will
 * <i>not</i> damage instances of this class in the same way that it will
 * damage, for example, a non-threadsafe <code>TreeMap</code>; instances of
 * this class will not be "damaged beyond repair". Instead, the current state of
 * the parameter vector <i>may</i> become corrupted. (This can be fixed by
 * resetting the iteration state.) In any case, you should always ensure serial
 * access to instances of this class.
 * <p>
 * This class does not support serialization or cloning; it does, however,
 * supports <code>equals(Object)</code> and <code>hashCode()</code>.
 *
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
@SuppressWarnings("nls")
public class LevenbergMarquardt
	extends Object
{


	/**
	 * Default accuracy.
	 */
	public static final double ACCURACY = 1.0e-10;

	/**
	 * Default number of iterations.
	 */
	public static final int ITERATIONS = 100;

	/**
	 * Default lambda value.
	 */
	public static final double LAMBDA = 1.00e-2;

	/**
	 * Default lambda multiplier.
	 */
	public static final double MULTIPLIER = 10;


	/**
	 * The cost function.
	 */
	private CostFunction function;

	/**
	 * The length of the data.
	 */
	private int length;

	/**
	 * The dimension of the data.
	 */
	private int dimension;

	/**
	 * Current error.
	 */
	private double cur_error;

	/**
	 * Next error.
	 */
	private double next_error;

	/**
	 * Accuracy/error tolerance.
	 */
	private double accuracy;

	/**
	 * Current lamba.
	 */
	private double lambda;

	/**
	 * Lambda multiplier.
	 */
	private double lambda_mult;

	/**
	 * Current iteration number.
	 */
	private int iter;

	/**
	 * Maximum number of iterations.
	 */
	private int max_iter;

	/**
	 * Parameter count.
	 */
	private int param_count;

	/**
	 * The parameter guess.
	 */
	private double[] guess;

	/**
	 * Has a guess been set.
	 */
	private boolean guess_set;

	/**
	 * The current parameter settings.
	 */
	private double[] cur_params;

	/**
	 * Next set of params.
	 */
	private double[] next_params;

	/**
	 * The data points.
	 */
	private double[][] points;

	/**
	 * Dimensions filled, in the points array.
	 */
	private boolean[] set_points;

	/**
	 * Cardinality of set dimensions.
	 */
	private int set_points_card;

	/**
	 * The values to which to fit.
	 */
	private double[] values;

	/**
	 * The weights (importances) of the data.
	 */
	private double[] data_weights;

	/**
	 * The matrix alpha prime (as named by NR).
	 */
	private Matrix alpha_prime;

	/**
	 * Beta vector.
	 */
	private double[] beta;

	/**
	 * Delta vector.
	 */
	private double[] delta;


	/**
	 * Constructor.
	 *
	 * @param length the length of the input data.
	 * @param dim the dimension of the input data.
	 * @throws IllegalArgumentException If <code>length</code> or
	 *         <code>dim</code> are negative.
	 */
	public LevenbergMarquardt( final int length, final int dim )
		throws IllegalArgumentException
	{
		super();

		if( dim < 1 || length < 1 )
		{
			throw new IllegalArgumentException();
		}

		// store 'em.
		this.dimension = dim;
		this.length = length;

		// set some default stuff.
		this.max_iter = ITERATIONS;
		this.accuracy = ACCURACY;
		this.lambda = LAMBDA;
		this.lambda_mult = MULTIPLIER;
		this.iter = 0;
		this.guess_set = false;

		// set default weights and guess.
		this.data_weights = new double[ length ];
		this.values = new double[ length ];
		this.points = new double[ length ][ dim ];
		this.set_points = new boolean[ dim ];
		this.set_points_card = 0;

		// set initial weights...
		Arrays.fill( this.data_weights, 1.0d );
	}


	/**
	 * Has the points data been set along all dimensions?
	 *
	 * @return boolean true if data has been set along all dimensions.
	 */
	public final boolean isDataSet()
	{
		return ( this.set_points_card == this.dimension );
	}


	/**
	 * Is this LM ready to be solved/stepped/iterated?
	 *
	 * @return boolean true if ready.
	 */
	public final boolean isReady()
	{
		return ( this.function != null && this.guess_set == true && this.isDataSet() == true );
	}


	/**
	 * Has this LM been iterated at least once?
	 *
	 * @return boolean true if iterated once.
	 */
	public final boolean isIterated()
	{
		return ( this.iter != 0 );
	}


	/**
	 * Determine if this instance has reached it's stopping point.
	 * <p>
	 * If you want more control over the stopping criteria, override this method.
	 *
	 * @return boolean <code>true</code> if done.
	 */
	public boolean isFinished()
	{
		return ( this.iter != 0 && ( this.iter >= this.max_iter || this.accuracy >= Math.abs( this.cur_error ) ) );
	}


	/**
	 * Solve this LM system; more precisely, iterate until <code>isFinished</code>
	 * returns <code>false</code>.
	 *
	 * @see #isFinished()
	 */
	public final void solve()
	{
		this.iterate( Integer.MAX_VALUE );
	}


	/**
	 * Iterate the specified number of steps, or until stop criterion are reached,
	 * using the specified lambda value.
	 *
	 * @param lamb the new lambda value.
	 * @param steps the (maximum) number times to step.
	 * @return double the next suggested value for lambda.
	 * @throws IllegalArgumentException If <code>steps</code> is not greater
	 *         than zero.
	 * @throws IllegalStateException If this object is not ready to iterate.
	 * @see #iterate(int)
	 * @see #step()
	 * @see #step(double)
	 */
	public final double iterate( final int steps, final double lamb )
		throws IllegalArgumentException, IllegalStateException
	{
		this.lambda = lamb;
		return ( this.iterate( steps ) );
	}


	/**
	 * Iterate the specified number of steps, or until stop criterion are reached.
	 * <p>
	 * This is the method that actually does stuff...
	 *
	 * @param steps the number times to step.
	 * @return double the next suggested value for lambda.
	 * @throws IllegalArgumentException If <code>steps</code> is not greater
	 *         than zero.
	 * @throws IllegalStateException If this object is not ready to iterate.
	 * @see #iterate(int, double)
	 * @see #step()
	 * @see #step(double)
	 */
	public final double iterate( int steps )
		throws IllegalArgumentException, IllegalStateException
	{
		if( this.isReady() == false )
		{
			throw new IllegalStateException();
		}

		if( steps < 1 )
		{
			throw new IllegalArgumentException();
		}

		if( this.iter == 0 )
		{
			System.arraycopy( this.guess, 0, this.cur_params, 0, this.param_count );
		}

		while( this.isFinished() == false && steps > 0 )
		{
			// compute current error.
			this.cur_error = this.getWeightedError( this.cur_params );

			// compute all the necesarray schmutz.
			this.computeAlphaPrime();
			this.computeBeta();
			this.computeDelta();
			this.computeNextParameterVector();

			// compute next error!
			this.next_error = this.getWeightedError( this.next_params );

			if( this.next_error > this.cur_error )
			{
				// bad things man... bad things.
				this.lambda *= this.lambda_mult;
			}
			else
			{
				// this is what we want.
				this.lambda /= this.lambda_mult;

				// copy params.
				System.arraycopy( this.next_params, 0, this.cur_params, 0, this.param_count );
			}

			// update steps.
			steps -= 1;
			this.iter += 1;
		}

		return ( this.lambda );
	}


	/**
	 * Do one fit iteration, using the current internal lambda value.
	 *
	 * @return double the next suggested lambda value.
	 * @throws IllegalStateException If this object is not ready to iterate.
	 * @see #step(double)
	 * @see #iterate(int)
	 * @see #iterate(int, double)
	 */
	public final double step()
		throws IllegalStateException
	{
		return ( this.iterate( 1, this.lambda ) );
	}


	/**
	 * Do one fit iteration, using the specified lambda value.
	 *
	 * @param lamb the lambda value to use.
	 * @return double the next suggested lambda value.
	 * @throws IllegalStateException If this object is not ready to iterate.
	 * @see #step()
	 * @see #iterate(int)
	 * @see #iterate(int, double)
	 */
	public final double step( final double lamb )
		throws IllegalStateException
	{
		this.lambda = lamb;
		return ( this.iterate( 1 ) );
	}


	/**
	 * Clear this object.
	 * <p>
	 * Invoking this method clears the following:
	 * <ol>
	 * <li>The cost function.</li>
	 * <li>The input points and values.</li>
	 * <li>The initial guess.</li>
	 * <li>The iteration count.</li>
	 * <li>The current lambda value.</li>
	 * <li>The current parameter setting.</li>
	 * <li>The weights.</li>
	 * </ol>
	 *
	 * @see #reset()
	 */
	public final void clear()
	{
		this.reset();
		this.function = null;

		// reset weights.
		Arrays.fill( this.data_weights, 1.0d );

		// reset the points.
		this.set_points_card = 0;
		Arrays.fill( this.set_points, false );
	}


	/**
	 * Reset the iteration state.
	 * <p>
	 * This clears:
	 * <ol>
	 * <li>The iteration count.</li>
	 * <li>The current lambda value.</li>
	 * <li>The current parameter setting.</li>
	 * </ol>
	 *
	 * @see #clear()
	 */
	public final void reset()
	{
		this.guess_set = false;
		this.iter = 0;
	}


	/**
	 * Fill in the alpha matrix, given the current parameter vector.
	 */
	private void computeAlphaPrime()
	{
		int row, col, index;
		double value, deriv;

		for( row = 0; row < this.param_count; row++ )
		{
			for( col = 0; col < this.param_count; col++ )
			{
				for( value = 0, deriv = 0, index = 0; index < this.length; index++ )
				{
					// compute the derivatives, evaluated at the specified vector.
					deriv = this.function.derive( this.points[ index ], this.cur_params, row );
					deriv *= this.function.derive( this.points[ index ], this.cur_params, col );
					value += this.data_weights[ index ] * deriv;
				}

				if( row == col )
				{
					// along the diagonal we multiply by l + lmb.
					value *= ( this.lambda + 1.0d );
				}

				// done, store it.
				this.alpha_prime.set( row, col, value );
			}
		}

	}


	/**
	 * Compute the beta vector.
	 */
	private void computeBeta()
	{
		int row, index;
		double value, deriv;

		for( row = 0; row < this.param_count; row++ )
		{
			for( deriv = 0, value = 0, index = 0; index < this.length; index++ )
			{
				deriv = this.function.evaluate( this.points[ index ], this.cur_params );
				deriv = this.values[ index ] - deriv;
				deriv *= this.function.derive( this.points[ index ], this.cur_params, row );
				value += ( this.data_weights[ index ] * deriv );
			}

			// actually store it!
			this.beta[ row ] = value;
		}
	}


	/**
	 * Compute delta.
	 */
	private void computeDelta()
	{
		// invert alpha.
		this.alpha_prime.invert();

		// multilpy by beta, store in delta.
		this.alpha_prime.multiply( this.beta, this.delta );
	}


	/**
	 * Compute next parameter vector.
	 */
	private void computeNextParameterVector()
	{
		for( int index = 0; index < this.param_count; index++ )
		{
			this.next_params[ index ] = this.cur_params[ index ] + this.delta[ index ];
		}
	}


	/**
	 * Compute chi-squared error, with the specified weights, and the specified
	 * parameter vector.
	 *
	 * @param parameters the parameters to the cost function.
	 * @return double the chi-squared error.
	 */
	private double getWeightedError( final double[] parameters )
	{
		double retme = 0;
		double diff = 0;

		for( int index = 0; index < this.length; index++ )
		{
			diff = Math.abs( this.values[ index ]
					- this.function.evaluate( this.points[ index ], parameters ) );
			retme += this.data_weights[ index ] * diff * diff;
		}

		return ( retme );
	}


	/**
	 * Get the cost function.
	 *
	 * @return CostFunction the function.
	 */
	public final CostFunction getCostFunction()
	{
		return ( this.function );
	}


	/**
	 * Set the cost function.
	 *
	 * @param function the new value for function.
	 * @throws NullPointerException If <code>function</code> is
	 *         <code>null</code>.
	 */
	public final void setCostFunction( final CostFunction function )
		throws NullPointerException
	{
		if( function == null )
		{
			throw new NullPointerException();
		}

		// see if we need to recreate stuff.
		int pc = function.getParameterCount();

		if( pc != this.param_count || this.function == null )
		{
			this.param_count = pc;

			// re-init the guess.
			this.guess = new double[ this.param_count ];
			this.guess_set = false;

			this.cur_params = new double[ this.param_count ];
			this.next_params = new double[ this.param_count ];

			// create matrices and happy vectors.
			MatrixFactory mf = MatrixFactory.getInstance();
			this.alpha_prime = mf.newMatrix( this.param_count, this.param_count );
			this.beta = new double[ this.param_count ];
			this.delta = new double[ this.param_count ];

			// reset iteration count.
			this.iter = 0;
		}

		// store function.
		this.function = function;
	}


	/**
	 * Get the parameters.
	 *
	 * @return double[] the parameters.
	 * @throws IllegalStateException If no iteration has occured.
	 */
	public final double[] getParameters()
		throws IllegalStateException
	{
		return ( this.getParameters( new double[ this.param_count ] ) );
	}


	/**
	 * Get the parameters, storing them in the specified location.
	 *
	 * @param storein the array in which to store the parameters.
	 * @return double[] the parameters.
	 * @throws IllegalStateException If no iteration has occured.
	 * @throws NullPointerException If <code>storein</code> is <code>null</code>.
	 * @throws IndexOutOfBoundsException If copying would cause access of data
	 *         outside array bounds.
	 */
	public final double[] getParameters( final double[] storein )
		throws IllegalStateException, NullPointerException,
		IndexOutOfBoundsException
	{
		if( this.isIterated() == false )
		{
			throw new IllegalStateException();
		}

		// copy params and return them!
		this.arrayCopy( this.cur_params, 0, storein, 0, this.param_count );
		return ( storein );
	}


	/**
     * Get one of the fit parameters.
     *
     * @param index Index of the parameter to get.
     * @return The parameter.
     * @throws IllegalStateException If no iteration has occurred.
     * @throws IndexOutOfBoundsException If index is invalid.
     */
    public final double getParameter( final int index )
        throws IllegalStateException, IllegalArgumentException
    {
        if( this.isIterated() == false )
            throw new IllegalStateException();
        return cur_params[index];
    }


	/**
	 * Get guess.
	 * <p>
	 * The returned array is a clone of the internal representation of the guess.
	 *
	 * @return double[] the guess.
	 * @throws IllegalStateException If no cost function has been set.
	 */
	public final double[] getGuess()
		throws IllegalStateException
	{
		return ( this.getGuess( new double[ this.param_count ] ) );
	}


	/**
	 * Get guess, storring it in the specified array.
	 *
	 * @param storein the place to store the guess.
	 * @return double[] the guess (<code>storein</code> is returned).
	 * @throws IllegalStateException If no cost function has been set.
	 * @throws NullPointerException If <code>storein</code> is <code>null</code>.
	 * @throws IndexOutOfBoundsException If copying would cause access of data
	 *         outside array bounds.
	 */
	public final double[] getGuess( final double[] storein )
		throws IllegalStateException, NullPointerException,
		IndexOutOfBoundsException
	{
		if( this.function == null )
		{
			throw new IllegalStateException();
		}

		// copy it.
		this.arrayCopy( storein, this.guess, this.param_count );
		return ( storein );
	}


	/**
	 * Set the guess (the inital parameter vector).
	 *
	 * @param guess the new value for guess.
	 * @throws NullPointerException If <code>guess</code> is <code>null</code>.
	 * @throws IndexOutOfBoundsException If copying would cause access of data
	 *         outside array bounds.
	 */
	public final void setGuess( final double[] guess )
		throws NullPointerException, IndexOutOfBoundsException
	{
		// copy it over...
		this.arrayCopy( guess, this.guess, this.param_count );

		// we now have a guess!
		this.guess_set = true;
	}


	/**
	 * Get the data points, along the specified dimension.
	 *
	 * @param dim the dimension along which to get.
	 * @return double[][] the points.
	 * @throws IndexOutOfBoundsException If copying would cause access of data
	 *         outside array bounds.
	 * @throws IllegalArgumentException If <code>dim</code> is out of range.
	 * @see #getPoints()
	 * @see #getPoints(double[],int)
	 */
	public final double[] getPoints( final int dim )
		throws IndexOutOfBoundsException, IllegalArgumentException
	{
		return ( this.getPoints( new double[ this.length ], dim ) );
	}


	/**
	 * Get points along the specified dimension, storing them in the specified
	 * array.
	 *
	 * @param dim the dimension along which to get data.
	 * @param storein the array in which to store stuff!
	 * @return double[] the array <code>storein</code>.
	 * @throws NullPointerException If <code>storein</code> is <code>null</code>.
	 * @throws IndexOutOfBoundsException If copying would cause access of data
	 *         outside array bounds.
	 * @throws IllegalArgumentException If <code>dim</code> is out of range.
	 * @see #getPoints()
	 * @see #getPoints(int)
	 */
	public final double[] getPoints( final double[] storein, final int dim )
		throws NullPointerException, IndexOutOfBoundsException,
		IllegalArgumentException
	{
		// check stuff.
		this.checkArray( storein, this.length );

		if( dim < 0 || dim >= this.dimension )
		{
			throw new IllegalArgumentException();
		}

		// copy stuff over.
		for( int index = 0; index < this.length; index++ )
		{
			storein[ index ] = this.points[ index ][ dim ];
		}

		return ( storein );
	}


	/**
	 * Get all data points.
	 *
	 * @return double[][] the points.
	 * @see #getPoints(int)
	 * @see #getPoints(double[][])
	 */
	public final double[][] getPoints()
	{
		return ( this.getPoints( new double[ this.length ][ this.dimension ] ) );
	}


	/**
	 * Get points, storing them in the specified array.
	 *
	 * @param storein the array in which to store stuff!
	 * @return double[][] the array <code>storein</code>.
	 * @throws NullPointerException If <code>storein</code> is <code>null</code>.
	 * @throws IndexOutOfBoundsException If copying would cause access of data
	 *         outside array bounds.
	 * @see #getPoints()
	 * @see #getPoints(double[], int)
	 */
	public final double[][] getPoints( final double[][] storein )
		throws NullPointerException, IndexOutOfBoundsException
	{
		// check big array.
		if( storein == null )
		{
			throw new NullPointerException();
		}

		if( storein.length != this.length )
		{
			throw new IndexOutOfBoundsException();
		}

		// check array chunks beforehand.
		for( int index = 0; index < this.length; index++ )
		{
			this.checkArray( storein[ index ], this.dimension, 0, 1 );
		}

		// copying time...
		for( int index = 0; index < this.length; index++ )
		{
			this.arrayCopy( storein[ index ], this.points[ index ], this.dimension );
		}

		// SUCCESS!
		return ( storein );
	}


	/**
	 * Set the data points.
	 * <p>
	 * This array should be arranged such that the i<sup>th</sup> position
	 * contains a vector that represents the i<sup>th</sup> point. Suppose, for
	 * example, that we had three-dimensional input data (and hence our cost
	 * function should take three arguments). We denote the i<sup>th</sup> input
	 * point by <code>(x<sub>i</sub>, y<sub>i</sub>, z<sub>i</sub>)</code>;
	 * and suppose we have <code>n+1</code> total points. So our inoput data
	 * looks something like:<br>
	 * <code>( (x<sub>0</sub>, y<sub>0</sub>, z<sub>0</sub>), (x<sub>1</sub>,
	 * y<sub>1</sub>, z<sub>1</sub>), ... , (x<sub>n</sub>, y<sub>n</sub>,
	 * z<sub>n</sub>) )</code>
	 * <p>
	 * The array <code>points</code> should therefore be arranged as follows:
	 * <br>
	 * <code>
	 * [ 0 ] -&gt; { [ 0 ] -&gt; x<sub>0</sub>, [ 1 ] -&gt; y<sub>0</sub>, [ 2 ] -&gt; z<sub>0</sub> }<br>
	 * [ 1 ] -&gt; { [ 0 ] -&gt; x<sub>1</sub>, [ 1 ] -&gt; y<sub>1</sub>, [ 2 ] -&gt; z<sub>1</sub> }<br>
	 * ...<br>
	 * [ i ] -&gt; { [ 0 ] -&gt; x<sub>i</sub>, [ 1 ] -&gt; y<sub>i</sub>, [ 2 ] -&gt; z<sub>i</sub> }<br>
	 * ...<br>
	 * [ n ] -&gt; { [ 0 ] -&gt; x<sub>n</sub>, [ 1 ] -&gt; y<sub>n</sub>, [ 2 ] -&gt; z<sub>n</sub> }<br>
	 * </code>
	 *
	 * @param points the new value for points.
	 * @throws NullPointerException If <code>points</code> is <code>null</code>.
	 * @throws IndexOutOfBoundsException If copying would cause access of data
	 *         outside array bounds.
	 */
	public final void setPoints( final double[][] points )
		throws NullPointerException, IndexOutOfBoundsException
	{
		if( points == null )
		{
			throw new NullPointerException();
		}

		if( points.length != this.length )
		{
			throw new IndexOutOfBoundsException();
		}

		// check stuff, like lengths and non-null-ness
		for( int index = 0; index < this.length; index++ )
		{
			this.checkArray( points[ index ], this.dimension );
		}

		// ok, copy stuff over...
		for( int index = 0; index < this.length; index++ )
		{
			this.arrayCopy( points[ index ], this.points[ index ], this.dimension );
		}

		// stupid stuff for dimension checking.
		Arrays.fill( this.set_points, true );
		this.set_points_card = this.dimension;
	}


	/**
	 * Set the points of the specified dimension.
	 * <p>
	 * <i>This method differs significantly from {@link #setPoints(double[][])}!</i>
	 * This method allows you to set a "dimension-at-a-time" of the input points.
	 * Consider, again, the example above: We have three-dimensional input data
	 * and we denote the i<sup>th</sup> input point by <code>(x<sub>i</sub>,
	 * y<sub>i</sub>, z<sub>i</sub>)</code>; and suppose we have <code>n+1</code>
	 * total points. Thus, our input data looks something like:<br>
	 * <code>( (x<sub>0</sub>, y<sub>0</sub>, z<sub>0</sub>), (x<sub>1</sub>,
	 * y<sub>1</sub>, z<sub>1</sub>), ... , (x<sub>n</sub>, y<sub>n</sub>,
	 * z<sub>n</sub>) )</code>.
	 * <p>
	 * Our data, however, is stored in the following format: <br>
	 * <code> double[ ( n + 1 ) ] x_values = { x<sub>0</sub>, x<sub>1</sub>,
	 * ... , x<sub>n</sub> };<br>
	 * double[ ( n + 1 ) ] y_values = { y<sub>0</sub>, y<sub>1</sub>, ... , y<sub>n</sub> };<br>
	 * double[ ( n + 1 ) ] z_values = { z<sub>0</sub>, z<sub>1</sub>, ... , z<sub>n</sub> };<br>
	 * </code>
	 * <p>
	 * Rather than "repack" our data into the format specified by
	 * {@link #setPoints(double[][])}, we can use this method, in the following
	 * way:
	 * <br>
	 * <code> LevenbergMarquardt lm = new LevenbergMarquardt( n + 1, 3 );<br>
	 * <br>
	 * ... // set the cost function and do other happy operations.<br>
	 * <br>
	 * lm.setPoints( x_values, 0 );<br>
	 * lm.setPoints( y_values, 1 );<br>
	 * lm.setPoints( z_values, 2 );<br>
	 * <br>
	 * ... // step or solve lm, extract parameters, and so forth and so on<br>
	 * </code>
	 *
	 * @param points the points.
	 * @param dim the dimension to set.
	 * @throws NullPointerException If <code>points</code> is <code>null</code>.
	 * @throws IndexOutOfBoundsException If copying would cause access of data
	 *         outside array bounds.
	 * @throws IllegalArgumentException If <code>points</code> has incorrect
	 *         length or <code>dim</code> is out of range (i.e. not in [0,dim)).
	 */
	public final void setPoints( final double[] points, final int dim )
		throws NullPointerException, IndexOutOfBoundsException,
		IllegalArgumentException
	{
		// check points.
//		Kay this.checkArray( points, this.dimension, 0, 1 );
        this.checkArray( points, this.length, 0, 1 );

		if( dim < 0 || dim >= this.dimension )
		{
			throw new IllegalArgumentException();
		}

		// ok, copy data!
		for( int index = 0; index < this.length; index++ )
		{
			this.points[ index ][ dim ] = points[ index ];
		}

		if( this.set_points[ dim ] == false )
		{
			// keep track of filled dimensions...
			this.set_points[ dim ] = true;
			this.set_points_card += 1;
		}
	}


	/**
	 * Get values.
	 *
	 * @return double[] the values.
	 */
	public final double[] getValues()
	{
		return ( this.getValues( new double[ this.length ] ) );
	}


	/**
	 * Get values, storing them in the specified array.
	 *
	 * @param storein the location to store the values.
	 * @return double[] the same object as <code>storein</code>.
	 * @throws NullPointerException If <code>storein</code> is <code>null</code>. *
	 * @throws IndexOutOfBoundsException If copying would cause access of data
	 *         outside array bounds.
	 */
	public final double[] getValues( final double[] storein )
		throws NullPointerException, IndexOutOfBoundsException
	{
		this.arrayCopy( this.values, storein, this.length );
		return ( this.values );
	}


	/**
	 * Set values.
	 *
	 * @param values the new value for values.
	 * @throws NullPointerException If <code>values</code> is <code>null</code>.
	 * @throws IndexOutOfBoundsException If copying would cause access of data
	 *         outside array bounds.
	 */
	public final void setValues( final double[] values )
		throws NullPointerException, IndexOutOfBoundsException
	{
		this.arrayCopy( values, this.values, this.length );
	}


	/**
	 * Get the data weights.
	 * <p>
	 * The returned array is a clone of the array used to store the weights
	 * internally (i.e. changing the values in the returned array after calling
	 * this method will <i>not</i> change the weights used by this class).
	 *
	 * @return double[] the data_weights.
	 */
	public final double[] getDataWeights()
	{
		return ( this.getDataWeights( new double[ this.length ] ) );
	}


	/**
	 * Get the data weights, storing them in the specified array.
	 *
	 * @param storein the place to store the weights.
	 * @return double[] the data_weights.
	 * @throws NullPointerException If <code>storein</code> is <code>null</code>.
	 * @throws IndexOutOfBoundsException If copying would cause access of data
	 *         outside array bounds.
	 */
	public final double[] getDataWeights( final double[] storein )
		throws NullPointerException, IndexOutOfBoundsException
	{
		this.arrayCopy( storein, this.data_weights, this.length );
		return ( storein );
	}


	/**
	 * Set the data weights.
	 * <p>
	 * The specified array is copied locally (i.e. changing
	 * <code>data_weights</code> after calling this method will <i>not</i>
	 * change the weights used by this class).
	 *
	 * @param data_weights the new value for data_weights.
	 * @throws IllegalArgumentException If any of the weights are negative, or all
	 *         are zero.
	 * @throws IndexOutOfBoundsException If copying would cause access of data
	 *         outside array bounds.
	 * @throws NullPointerException If <code>data_weights</code> is
	 *         <code>null</code>.
	 */
	public final void setDataWeights( final double[] data_weights )
		throws IllegalArgumentException, NullPointerException
	{
		this.checkArray( data_weights, this.length );

		boolean non_zero = false;
		for( int index = 0; index < this.length; index++ )
		{
			if( data_weights[ index ] < 0.0d
					|| Double.isNaN( this.data_weights[ index ] ) )
			{
				throw new IllegalArgumentException();
			}

			non_zero |= ( data_weights[ index ] != 0.0d );
		}

		if( non_zero == false )
		{
			throw new IllegalArgumentException();
		}

		// ok, copy it over.
		this.arrayCopy( data_weights, this.data_weights, this.length );
	}


	/**
	 * Array copy wrapper.
	 *
	 * @param src the source.
	 * @param dest the destination.
	 * @param len the length to copy.
	 * @throws IndexOutOfBoundsException If copying would cause access of data
	 *         outside array bounds.
	 * @throws NullPointerException if either <code>src</code> or
	 *         <code>dest</code> is <code>null</code>.
	 */
	private void arrayCopy( final double[] src, final double[] dest, final int len )
		throws NullPointerException, IndexOutOfBoundsException
	{
		this.arrayCopy( src, 0, dest, 0, len );
	}


	/**
	 * Array copy wrapper.
	 *
	 * @param src the source.
	 * @param src_off the source offset.
	 * @param dest the destination
	 * @param dest_off the destination offset.
	 * @param len the length to copy.
	 * @throws IndexOutOfBoundsException If copying would cause access of data
	 *         outside array bounds.
	 * @throws NullPointerException if either <code>src</code> or
	 *         <code>dest</code> is <code>null</code>.
	 */
	private void arrayCopy( final double[] src, final int src_off,
			final double[] dest, final int dest_off, final int len )
		throws NullPointerException, IndexOutOfBoundsException
	{
		this.arrayCopy( src, src_off, dest, dest_off, len, 1 );
	}


	/**
	 * Array copy wrapper. This is here so as to make (one day possibly)
	 * supporting strided arrays much easier.
	 *
	 * @param src the source.
	 * @param src_off the source offset.
	 * @param dest the destination
	 * @param dest_off the destination offset.
	 * @param len the length.
	 * @param stride the array stride.
	 * @throws InternalError If <code>stride</code> does not equal 1 (subject to
	 *         deprecation, obviously, when if/when I decide to support strided
	 *         arrays).
	 * @throws IndexOutOfBoundsException If copying would cause access of data
	 *         outside array bounds.
	 * @throws NullPointerException if either <code>src</code> or
	 *         <code>dest</code> is <code>null</code>.
	 */
	private void arrayCopy( final double[] src, final int src_off,
			final double[] dest, final int dest_off, final int len, final int stride )
		throws InternalError, NullPointerException, IndexOutOfBoundsException
	{
		if( stride != 1 )
		{
			throw new InternalError();
		}

		// check both arrays...
		this.checkArray( src, len, src_off, stride );
		this.checkArray( dest, len, dest_off, stride );

		// copy.
		System.arraycopy( src, src_off, dest, dest_off, len );
	}


	/**
	 * Check the specified array, given the specified offset, length, and stride.
	 *
	 * @param array the check.
	 * @param len the length.
	 * @throws NullPointerException If <code>array</code> is <code>null</code>.
	 * @throws IndexOutOfBoundsException If copying would cause access of data
	 *         outside array bounds.
	 */
	private void checkArray( final double[] array, final int len )
		throws NullPointerException, IndexOutOfBoundsException
	{
		this.checkArray( array, len, 0, 1 );
	}


	/**
	 * Check the specified array, given the specified offset, length, and stride.
	 * <p>
	 * Offset and stride are currently ignored; I <i>might</i> support offset
	 * and/or strided arrays in the future.
	 *
	 * @param array the check.
	 * @param len the length.
	 * @param offset the offset (currently ignored).
	 * @param stride the array stride (currently ignored).
	 * @throws NullPointerException If <code>array</code> is <code>null</code>.
	 * @throws IndexOutOfBoundsException If copying would cause access of data
	 *         outside array bounds.
	 */
	private void checkArray( final double[] array, final int len,
			final int offset, final int stride )
		throws NullPointerException, IndexOutOfBoundsException
	{
		if( array == null )
		{
			throw new NullPointerException();
		}

		if( array.length != len )
		{
			throw new IndexOutOfBoundsException();
		}
	}


	/**
	 * Get the dimension.
	 *
	 * @return int the dimension.
	 */
	public final int getDimension()
	{
		return ( this.dimension );
	}


	/**
	 * Get the length.
	 *
	 * @return int the length.
	 */
	public final int getLength()
	{
		return ( this.length );
	}


	/**
	 * Get accuracy.
	 *
	 * @return double the accuracy.
	 */
	public final double getAccuracy()
	{
		return ( this.accuracy );
	}


	/**
	 * Set accuracy.
	 *
	 * @param accuracy the new value for accuracy.
	 */
	public final void setAccuracy( final double accuracy )
	{
		this.accuracy = accuracy;
	}


	/**
	 * Get lambda.
	 *
	 * @return double the lambda.
	 */
	public final double getLambda()
	{
		return ( this.lambda );
	}


	/**
	 * Set lambda.
	 *
	 * @param lambda the new value for lambda.
	 */
	public final void setLambda( double lambda )
	{
		this.lambda = lambda;
	}


	/**
	 * Get the maximum number of iterations.
	 *
	 * @return int the max iteration count.
	 */
	public final int getMaximumIterationCount()
	{
		return ( this.max_iter );
	}


	/**
	 * Set max iteration count.
	 *
	 * @param max_iter the max iteration count.
	 * @throws IllegalArgumentException If <code>max_iter</code> is less than 1.
	 */
	public final void setMaximumIterationCount( final int max_iter )
		throws IllegalArgumentException
	{
		if( max_iter < 1 )
		{
			throw new IllegalArgumentException();
		}

		// store it...
		this.max_iter = max_iter;
	}


	/**
	 * To string.
	 *
	 * @return String a string!
	 */
	@Override
	public String toString()
	{
		return ( "LevenbergMarquardt( " + this.getLength() + ", "
				+ this.getDimension() + " )" );
	}


	/**
	 * Hashcode.
	 *
	 * @return int a happier hashcode.
	 */
	@Override
	public int hashCode()
	{
		return ( this.getLength() ^ this.getDimension() );
	}


	/**
	 * Equals.
	 * <p>
	 * Equality is based on supporting the same length and dimension; not on
	 * <i>any</i> of the following:
	 * <ul>
	 * <li>Cost function</li>
	 * <li>Input points or data</li>
	 * <li>Guess data</li>
	 * <li>Iteration parameters</li>
	 * <li>Iteration state</li>
	 * <li>etc....</li>
	 * </ul>
	 *
	 * @param other the other object.
	 * @return boolean true if equal.
	 */
	@Override
	public boolean equals( final Object other )
	{
		if( other == null )
		{
			return ( false );
		}

		if( other == this )
		{
			return ( true );
		}

		if( LevenbergMarquardt.class.isAssignableFrom( other.getClass() ) == true )
		{
			LevenbergMarquardt lm = (LevenbergMarquardt)other;
			return ( lm.getLength() == this.getLength() && lm.getDimension() == this.getDimension() );
		}

		return ( false );
	}


}
