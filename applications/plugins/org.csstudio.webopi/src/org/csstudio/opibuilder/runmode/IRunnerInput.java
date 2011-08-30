/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.runmode;

import java.io.InputStream;

import org.csstudio.opibuilder.util.MacrosInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IPersistableElement;

public interface IRunnerInput extends IPathEditorInput, IPersistableElement {

	/**
	 * @param displayOpenManager the displayOpenManager to set
	 */
	public void setDisplayOpenManager(
			DisplayOpenManager displayOpenManager);

	/**
	 * @return the displayOpenManager
	 */
	public DisplayOpenManager getDisplayOpenManager();

	/**
	 * @return the macrosInput
	 */
	public MacrosInput getMacrosInput();

	public InputStream getInputStream() throws Exception;
	
}
