/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.script;

/**The data structure which include the pvName and trigger flag 
 * @author Xihui Chen
 *
 */
public class PVTuple{
	public String pvName;
	public boolean trigger;
	
	public PVTuple(String pvName, boolean trigger) {
		this.pvName = pvName;
		this.trigger = trigger;
	}
	
	public PVTuple getCopy(){
		return new PVTuple(pvName, trigger);
	}
	
}
