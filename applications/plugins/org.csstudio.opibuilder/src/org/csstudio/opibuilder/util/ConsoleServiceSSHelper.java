/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.util;




/**An empty console service.
 * @author Xihui Chen
 *
 */
public abstract class ConsoleServiceSSHelper{

	public abstract void writeError(String message);

	public abstract void writeWarning(String message) ;

	public abstract void writeInfo(String message);

	public abstract void writeString(String s);

	public abstract void turnOnSystemOutput();
	
	public abstract void turnOffSystemOutput();
}
