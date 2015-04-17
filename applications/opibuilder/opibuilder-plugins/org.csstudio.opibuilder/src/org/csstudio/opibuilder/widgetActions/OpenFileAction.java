/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgetActions;

import org.csstudio.opibuilder.properties.FilePathProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.SingleSourceHelper;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory.ActionType;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**The action opening a file using its default editor.
 * @author Xihui Chen
 *
 */
public class OpenFileAction extends AbstractWidgetAction {

	public static final String PROP_PATH = "path";//$NON-NLS-1$

	@Override
	protected void configureProperties() {
		addProperty(new FilePathProperty(
				PROP_PATH, "File Path", WidgetPropertyCategory.Basic, new Path(""),
				new String[]{"*"}));

	}

	@Override
	public ActionType getActionType() {
		return ActionType.OPEN_FILE;
	}

	@Override
	public void run() {
		SingleSourceHelper.openFileActionRun(this);
	}

	public IPath getPath(){
		return (IPath)getPropertyValue(PROP_PATH);
	}



	@Override
	public String getDefaultDescription() {
		return super.getDefaultDescription() + " " + getPath(); //$NON-NLS-1$
	}

}
