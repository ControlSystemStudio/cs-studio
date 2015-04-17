/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.runmode;

import org.eclipse.gef.tools.SelectionTool;

/**
 * Work around a bug in GEF: right click is recognized as mouse exit event 
 * in LightWeightSystem, so handleButtonUp() will not be invoked for right click button up.
 * This will cause unexpected select behavior.
 * @author Xihui Chen
 *
 */
public class RuntimePatchedSelectionTool extends SelectionTool{

	@Override
	protected boolean handleViewerExited() {
		getCurrentInput().setMouseButton(3, false);
		handleButtonUp(3);
		return super.handleViewerExited();
	}
	
}
