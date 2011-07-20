/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.scriptUtil;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.runmode.RunModeService;
import org.csstudio.opibuilder.runmode.RunModeService.TargetWindow;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.rap.swt.SWT;
import org.eclipse.rwt.widgets.ExternalBrowser;

/**The utility class to facilitate Javascript programming.
 * @author Xihui Chen
 *
 */
public class ScriptUtil {

	/**Open an OPI.
	 * @param widgetController the widgetController to which the script is attached.
	 * @param relative_path the path of the OPI relative to the Display file of the widgetContoller.
	 * @param newWindow true if it will be opened in a new window. false if in a new tab.
	 * @param macrosInput the macrosInput. null if no macros needed.
	 */
	public final static void openOPI(AbstractBaseEditPart widgetController,
			String relative_path, boolean newWindow, MacrosInput macrosInput){
		IPath  path = ResourceUtil.buildAbsolutePath(
				widgetController.getWidgetModel(), ResourceUtil.getPathFromString(relative_path));
		RunModeService.getInstance().runOPI(path,
				newWindow ? TargetWindow.NEW_WINDOW : TargetWindow.SAME_WINDOW, null, macrosInput);
	}

	/**Pop up an Elog dialog to make an Elog entry.
	 * @param filePath path of a file to attach or null.
	 * It could be either a local file system file path
	 * or a workspace file path. File types that the logbook support depend on
	 * implementation but should include *.gif, *.jpg: File will be attached
	 * as image.
	 */
	public final static void makeElogEntry(final String filePath){
		throw new RuntimeException("Elog is not supported in RAP yet");
	}
	
	public final static void openWebPage(String url){
		ExternalBrowser.open("_blank", url, SWT.None); //$NON-NLS-1$
	}

}
