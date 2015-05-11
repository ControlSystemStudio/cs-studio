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
 * The cost function interface. You must implement this interface to describe
 * your cost function.
 * 
 * @author Fran Lattanzio
 * @version $Revision$ $Date$
 */
public interface CostFunction
{


	/**
	 * Evaluate the cost function at the specified tuple.
	 * 
	 * @param values the vector of data to evaluate.
	 * @param params vector containing the current parameters of variation.
	 * @return double the value of this function.
	 */
	public double evaluate( double[] values, double[] params );


	/**
	 * Returns the derivative of this function, with respect to the <code>ith</code>
	 * <b>parameter</b>, evaluated at the specified tuple.
	 * 
	 * @param values the vector of data to evaluate.
	 * @param params vector containing the current parameters of variation.
	 * @param ith the parameter (number) with respect to which the derivative is
	 *        taken.
	 * @return double the value of this function.
	 */
	public double derive( double[] values, double[] params, int ith );
	
	
	/**
	 * Get the parameter count.
	 * 
	 * @return int the param count.
	 */
	public int getParameterCount();
	
	
}
