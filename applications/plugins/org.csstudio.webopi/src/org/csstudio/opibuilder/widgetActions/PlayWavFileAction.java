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
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.widgetActions.WidgetActionFactory.ActionType;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.rwt.internal.widgets.JSExecutor;
import org.eclipse.swt.widgets.Display;

/**
 * An action which plays a .wav file.
 * 
 * @author Xihui Chen
 * 
 */
public class PlayWavFileAction extends AbstractWidgetAction {

	public static final String PROP_PATH = "path";//$NON-NLS-1$

	@Override
	protected void configureProperties() {
		addProperty(new FilePathProperty(PROP_PATH, "WAV File Path",
				WidgetPropertyCategory.Basic, new Path(""),
				new String[] { "wav" }));

	}

	@Override
	public ActionType getActionType() {
		return ActionType.PLAY_SOUND;
	}

	@Override
	public void run() {		
		if(!ResourceUtil.isURL(getAbsolutePath().toString())){
			MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Not support", 
					"The sound file path must be an URL!");
			return;		}
			
		String code = "document.getElementById(\"dummy\").innerHTML=\"<embed src=\\\""+ //$NON-NLS-1$
		getAbsolutePath() + "\\\" hidden=\\\"true\\\" autostart=\\\"true\\\" loop=\\\"false\\\" />\""; //$NON-NLS-1$
		JSExecutor.executeJS(code);

	}

	private IPath getPath() {
		return (IPath) getPropertyValue(PROP_PATH);
	}

	private IPath getAbsolutePath() {
		// read file
		IPath absolutePath = getPath();
		if (!getPath().isAbsolute()) {
			absolutePath = ResourceUtil.buildAbsolutePath(getWidgetModel(),
					getPath());
		}
		return absolutePath;
	}

	@Override
	public String getDefaultDescription() {
		return super.getDefaultDescription() + " " + getPath(); //$NON-NLS-1$
	}

}
