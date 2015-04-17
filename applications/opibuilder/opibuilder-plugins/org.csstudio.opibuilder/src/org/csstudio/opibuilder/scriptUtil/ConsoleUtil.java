/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.scriptUtil;

import org.csstudio.opibuilder.util.ConsoleService;
import org.eclipse.swt.graphics.RGB;

/**The Utility Class to help write information to CSS console.
 * @author Xihui Chen
 *
 */
public class ConsoleUtil {

	/**Write information to CSS console.
	 * @param message the output string.
	 */
	public static void writeInfo(String message){
		ConsoleService.getInstance().writeInfo(message);
	}
	
	/**Write Error information to CSS console.
	 * @param message the output string.
	 */
	public static void writeError(String message){
		ConsoleService.getInstance().writeError(message);
	}
	
	/**Write Warning information to CSS console.
	 * @param message the output string.
	 */
	public static void writeWarning(String message){
		ConsoleService.getInstance().writeWarning(message);
	}

	/**Write pure string to CSS console without any extra headers in black color.
	 * @param string the output string.
	 */
	public static void writeString(String string){
		ConsoleService.getInstance().writeString(string);
	}
	
	
	/**Write pure string to CSS console in specified color.
	 * @param string the output string.
	 * @param red the red component of RGB
	 * @param green the green component of RGB
	 * @param blue the blue component of RGB  
	 */
	public static void writeString(String string, int red, int green, int blue){
		ConsoleService.getInstance().writeString(string, new RGB(red, green, blue));
	}
	
}
