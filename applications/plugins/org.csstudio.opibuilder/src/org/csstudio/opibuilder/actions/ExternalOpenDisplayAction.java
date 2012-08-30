/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.actions;

import java.io.FileNotFoundException;
import java.util.LinkedHashMap;

import org.csstudio.java.string.StringSplitter;
import org.csstudio.openfile.IOpenDisplayAction;
import org.csstudio.opibuilder.runmode.RunModeService;
import org.csstudio.opibuilder.runmode.RunModeService.TargetWindow;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.osgi.util.NLS;

/**Run OPI from external program, such as alarm GUI, data browser...
 * @author Xihui Chen
 *
 */
public class ExternalOpenDisplayAction implements IOpenDisplayAction {

	/**Open OPI file.
	 * @param path the path of the OPI file, it can be a workspace path, file system path, URL 
	 * or a opi file in opi search path.
	 * @param data the input macros in format of {@code "macro1 = hello", "macro2 = hello2"}
	 * @throws Exception
	 */
	public void openDisplay(String path, String data) throws Exception {
		if(path != null && path.trim().length() > 0){
			MacrosInput macrosInput = null;
			//parse macros
			if(data != null && data.trim().length() > 0){
				macrosInput = new MacrosInput(new LinkedHashMap<String, String>(), true);
				 final String pairs[] = StringSplitter.splitIgnoreInQuotes(data, ',', true); //$NON-NLS-1$
			     for (String pair : pairs){
			        final String name_value[] = StringSplitter.splitIgnoreInQuotes(pair, '=', true); //$NON-NLS-1$
			        if (name_value.length == 2)
			        	macrosInput.getMacrosMap().put(name_value[0], name_value[1]);
			        else if(name_value.length == 1)
			        	macrosInput.getMacrosMap().put(name_value[0], ""); 
			        else
			            throw new Exception("Input '" + pair + "' does not match 'name=value'");
			            
			     }		     
			}
			IPath originPath = ResourceUtil.getPathFromString(path);
			if(!originPath.isAbsolute()){
				originPath = ResourceUtil.getFileOnSearchPath(originPath, false);
				if(originPath == null)
					throw new FileNotFoundException(NLS.bind("File {0} doesn't exist on search path.", path));
			}			
			RunModeService.getInstance().runOPI(originPath, TargetWindow.SAME_WINDOW, 
					null, macrosInput, null);
		}
	}

}
