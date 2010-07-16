/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.macros;
/**Provides value for a macro.
 * @author Xihui Chen
 *
 */
public interface IMacroTableProvider{
	/**Get value of a macro.
	 * @param macroName the name of the macro
	 * @return the value of the macro, null if no such macro exists.
	 */
	public String getMacroValue(String macroName);
}
