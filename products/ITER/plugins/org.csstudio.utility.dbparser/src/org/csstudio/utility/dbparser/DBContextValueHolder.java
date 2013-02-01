/*******************************************************************************
* Copyright (c) 2010-2013 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.utility.dbparser;

/**
 * Share an object {@link DBContext} within the same runnable {@link Thread}. It
 * allows to share an instance for many methods usage without adding this
 * instance in the methods header.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 * 
 */
public class DBContextValueHolder {

//	private static final ThreadLocal<DBContext> VALUE_HOLDER = new ThreadLocal<DBContext>() {
//		@Override
//		protected DBContext initialValue() {
//			// Create a new instance
//			return new DBContext();
//		}
//	};
	private static final DBContext context = new DBContext();
	
	/**
	 * Singleton = private constructor
	 */
	private DBContextValueHolder() {
		// Singleton
	}

	/**
	 * Get an instance of {@link DBContext} per {@link Thread}. At first call, a
	 * new object is instantiated. The next calls to this method with the same
	 * runnable{@link Thread} still returns the same object instance.
	 */
	public static DBContext get() {
		return context;
//		return VALUE_HOLDER.get();
	}

	/**
	 * Remove the shared object instance {@link DBContext}. This method has to
	 * be called to deallocate resources in memory. Once time done, a new object
	 * is instantiated on {@link #get()} method call. This is needed because of
	 * thread usage into container which could make memory leak or could inherit
	 * the {@link DBContext} of the previous call.
	 */
	public static void dispose() {
//		VALUE_HOLDER.remove();
	}
}
