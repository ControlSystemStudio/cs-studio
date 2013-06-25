/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.simplepv;

/**The listener on PV's events related to read.
 * @author Xihui Chen
 *
 */
public interface IPVReadListener {
	
	/**Will be called when PV value changed.
	 * @param pv the pv whose value has changed.
	 */
	void onValueChange(IPV pv);
	
	/**Will be called when connection state changed.
	 * @param pv the pv whose connection state changed.
	 */
	void onConnectionChange(IPV pv);
	
	/**If no {@link ExceptionHandler} was given to the PV,
	 * this method will be called when exception happened.
	 * Otherwise, the exception will be handled by the {@link ExceptionHandler}.
	 * @param pv the pv which has read related exception happened.
	 * @param exception the exception that has been caught.
	 */
	void onException(IPV pv, Exception exception);
	
	/**
	 * An empty implementation of {@link IPVReadListener} for convenience.
	 *
	 */
	public class Stub implements IPVReadListener{

		public void onValueChange(IPV pv) {
			
		}

		public void onConnectionChange(IPV pv) {
			
		}

		public void onException(IPV pv, Exception exception) {
			
		}
		
	}
	

}
