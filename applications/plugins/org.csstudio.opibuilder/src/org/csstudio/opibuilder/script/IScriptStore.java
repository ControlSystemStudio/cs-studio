/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.script;

/**
 * A store to save script related information and register or unregister script for PVs input.
 * @author Xiuhi Chen
 *
 */
public interface IScriptStore {

	/**
	 * Remove listeners from PV. Dispose related resource if needed.
	 */
	public void unRegister();
	
	
}
