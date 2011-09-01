/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.runmode;

import org.csstudio.opibuilder.model.DisplayModel;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.rwt.graphics.Graphics;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

/**
 * The editor for running of OPI.
 * 
 * @author Xihui Chen
 * 
 */
public class OPIRunner extends EditorPart implements IOPIRuntime{


	public static final String ID = "org.csstudio.opibuilder.OPIRunner"; //$NON-NLS-1$
	

	private OPIRuntimeDelegate opiRuntimeDelegate;

	public OPIRunner() {
		opiRuntimeDelegate = new OPIRuntimeDelegate(this);
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public void init(final IEditorSite site, final IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		opiRuntimeDelegate.init(site, input);
	}
	
	public void setOPIInput(IEditorInput input) throws PartInitException {
		init(getEditorSite(), input);
	}


	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {		
		opiRuntimeDelegate.createGUI(parent);		
	}

	@Override
	public void setFocus() {}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		Object obj = opiRuntimeDelegate.getAdapter(adapter);
		if(obj != null)
			return obj;
		else
			return super.getAdapter(adapter);

	}

	public void setWorkbenchPartName(String name) {
		setPartName(name);
	}

	public OPIRuntimeDelegate getOPIRuntimeDelegate() {
		return opiRuntimeDelegate;
	}
	
	public IEditorInput getOPIInput() {
		return getOPIRuntimeDelegate().getEditorInput();
	}
	
	public DisplayModel getDisplayModel() {
		return getOPIRuntimeDelegate().getDisplayModel();
	}
}
