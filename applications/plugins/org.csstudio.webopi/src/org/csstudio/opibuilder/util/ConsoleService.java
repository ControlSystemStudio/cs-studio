/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**The console service which manage the console output.
 * @author Xihui Chen, Alexander Will
 *
 */
public class ConsoleService {

	private static final String ENTER = "\n"; //$NON-NLS-1$

	private static ConsoleService instance;

	
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

	private ConsoleService() {

	}

	private String getTimeString(){
		Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$
	    return sdf.format(cal.getTime());

	}
	
	

	/**Write error information to the OPI console.
	 * @param message the output string.
	 */
	public void writeError(final String message){
		//TODO: implement

	}

	/**Write warning information to the OPI console.
	 * @param message the output string.
	 */
	public void writeWarning(String message){
		//TODO: implement

	}

	/**Write information to the OPI console.
	 * @param message the output string.
	 */
	public void writeInfo(String message){
		//TODO: implement
	}

	public void writeString(String s){
		//TODO: implement
	}



	

	private void popConsoleView(){
		//TODO: implement
	}
}
