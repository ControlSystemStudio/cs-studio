/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.util;




/**The console service whose implementation is determined in fragment.
 * The IMPL is allowed to be NULL.
 * @author Xihui Chen
 *
 */
public class ConsoleService{

	private static ConsoleService instance;
	
	private static final ConsoleServiceSSHelper IMPL;
	
	static {
		IMPL = (ConsoleServiceSSHelper)ImplementationLoader.newInstance(
				ConsoleServiceSSHelper.class, false);
	}
	
	
	/**
	 * Return the only one instance of this class.
	 *
	 * @return The only one instance of this class.
	 */
	public synchronized static ConsoleService getInstance() {
		if (instance == null) {
			instance = new ConsoleService();
		}
		return instance;
	}

	
	public void writeError(String message) {
		if(IMPL != null)
			IMPL.writeError(message);
	}

	
	public void writeWarning(String message) {
		if(IMPL != null)
			IMPL.writeWarning(message);
	}

	
	public void writeInfo(String message) {
		if(IMPL != null)
			IMPL.writeInfo(message);
	}

	
	public void writeString(String s) {
		if(IMPL != null)
			IMPL.writeString(s);
	}

	
	/**
	 * Direct system output to BOY console. 
	 * <b>Warning: </b>To make this take effect for the Python script calling this method, 
	 * it is required to rerun the OPI with the Python script so that the Python interpreter
	 * has a chance to reload system output. 
	 */
	public void turnOnSystemOutput(){
		if(IMPL != null)
			IMPL.turnOnSystemOutput();
	}
	
	/**
	 * Turn off displaying system output in BOY console and 
	 * reset system output to original output.
	 * <b>Warning: </b>It is required to rerun the OPI if this method is called from Python script. 
	 */
	public void turnOffSystemOutput() {
		if(IMPL != null)
			IMPL.turnOffSystemOutput();
	}
}
