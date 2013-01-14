/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.runmode;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.EditorActionBarContributor;

/**
 * The toolbar contributor for OPI runner
 * 
 * @author Xihui Chen
 * 
 */
public class OPIRunnerToolBarContributor extends EditorActionBarContributor {

	private OPIRuntimeToolBarDelegate opiRuntimeToolBarDelegate;

	@Override
	public void init(IActionBars bars, IWorkbenchPage page) {
		opiRuntimeToolBarDelegate.init(bars, page);
		super.init(bars, page);		
	}

	public OPIRunnerToolBarContributor() {
		opiRuntimeToolBarDelegate = new OPIRuntimeToolBarDelegate();	
	}

	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		opiRuntimeToolBarDelegate.contributeToToolBar(toolBarManager);
	}

	@Override
	public void setActiveEditor(IEditorPart targetEditor) {
		opiRuntimeToolBarDelegate
				.setActiveOPIRuntime((IOPIRuntime) targetEditor);
	}

}
