/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgetActions;

import org.csstudio.opibuilder.runmode.OPIShell;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory.ActionType;
import org.eclipse.core.runtime.IPath;

/**The action running another OPI file.
 * @author Xihui Chen
 *
 */
public class OpenOPIShellAction extends AbstractOpenOPIAction {

	@Override
	protected void configureProperties() {
		super.configureProperties();
	}
	
	@Override
	protected void openOPI(IPath absolutePath) {
		OPIShell.openOPIShell(absolutePath, getMacrosInput());
	}

	@Override
	public ActionType getActionType() {
		return ActionType.OPEN_OPI_SHELL;
	}


	@Override
	public String getDefaultDescription() {
		return "Open " + getPath();
	}

}
