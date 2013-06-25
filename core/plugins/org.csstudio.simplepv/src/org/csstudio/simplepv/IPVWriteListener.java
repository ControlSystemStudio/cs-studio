/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.simplepv;

/**A listener on PV's events related to write.
 * @author Xihui Chen
 *
 */
public interface IPVWriteListener {

	/**Will be called when a write is finished. 
	 * @param pv the pv on which the write event happened.
	 * @param isWriteSucceeded true if the write was successful.
	 */
	void onWriteFinished(IPV pv, boolean isWriteSucceeded);
	
	/**Will be called when write connection or permission changed.
	 * @param pv the pv whose connection or permission has changed.
	 */
	void onWriteConnectionChange(IPV pv);
	
	void onException(IPV pv, Exception exception);
	

	/**
	 * An empty implementation of {@link IPVWriteListener} for convenience.
	 *
	 */
	public class Stub implements IPVWriteListener{

		public void onWriteFinished(IPV pv, boolean isWriteSucceeded) {
			
		}

		public void onWriteConnectionChange(IPV pv) {
			
		}

		public void onException(IPV pv, Exception exception) {
			
		}
	}




}
