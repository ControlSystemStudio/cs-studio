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

import java.security.AccessController;
import java.security.PrivilegedAction;


/**
 * A class that can create <code>Matrix</code> implementations. You can change
 * the instance created by setting the
 * <code>org.teneighty.lm.MatrixFactory</code> system property to the name of
 * the class you want instantiated. You must make this change <i>before</i>
 * this class is classloaded.
 * <p>
 * This class uses the abstract factory and singleton design patterns.
 *
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
@SuppressWarnings("nls")
public abstract class MatrixFactory
	extends Object
{


	/**
	 * Property name.
	 */
	public static final String IMPLEMENTATION_PROPERTY = "org.teneighty.lm.MatrixFactory";

	/**
	 * Default implementation.
	 */
	private static final String DEFAULT_IMPLEMENTATION = "org.teneighty.lm.DefaultMatrixFactory";

	/**
	 * The lone instance of this class.
	 */
	private static MatrixFactory instance;


	/**
	 * Class initializer.
	 * <p>
	 * Creates the instance... not much else.
	 */
	static
	{

		try
		{
			// create the lone instance!
			instance = AccessController.doPrivileged( new PrivilegedAction<MatrixFactory>()
			{


				/**
				 * Create the new instance, and so forth.
				 *
				 * @return Matrix a new factory instance.
				 * @throws SecurityException If stuff fails.
				 */
				@SuppressWarnings("rawtypes")
                @Override
                public MatrixFactory run()
					throws SecurityException
				{
					String prop = System.getProperty( IMPLEMENTATION_PROPERTY );
					if( prop == null )
					{
						prop = DEFAULT_IMPLEMENTATION;
					}

					try
					{
						// lookup the class.
						Class clazz = Class.forName( prop, true, Thread.currentThread().getContextClassLoader() );

						// instantiate
						MatrixFactory factory = (MatrixFactory)clazz.newInstance();

						// finit!
						return ( factory );
					}
					catch( final ClassNotFoundException cnfe )
					{
						throw new SecurityException( cnfe );
					}
					catch( final InstantiationException ie )
					{
						throw new SecurityException( ie );
					}
					catch( final IllegalAccessException iae )
					{
						throw new SecurityException( iae );
					}

					// never get here.
				}


			} );

		}
		catch( final SecurityException se )
		{
			// most likely, the guy changed the matrix factory impl to something
			// stupid or non-existent.
			InternalError ie = new InternalError( "Unable to create MatrixFactory implementation" );
			ie.initCause( se );
			throw se;
		}


	}


	/**
	 * Get the instance of this class.
	 *
	 * @return MatrixFactory the instance.
	 */
	public static MatrixFactory getInstance()
	{
		return ( instance );
	}


	/**
	 * Constructor.
	 * <p>
	 * Does nothing. Should be considered <code>private protected</code>.
	 */
	protected MatrixFactory()
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
	public abstract Matrix newMatrix( int rows, int cols )
		throws IllegalArgumentException;


}
