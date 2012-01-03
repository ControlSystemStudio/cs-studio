/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.wizards;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.csstudio.opibuilder.script.ScriptService;
import org.csstudio.ui.util.wizards.WizardNewFileCreationPage;
import org.eclipse.jface.viewers.IStructuredSelection;

/**Wizard page for the creation of new javascript files.
 * @author Xihui Chen
 *
 */
public class NewJavaScriptWizardPage extends WizardNewFileCreationPage {


	public NewJavaScriptWizardPage(String pageName, IStructuredSelection selection) {
		super(pageName, selection);
		setTitle("Create a new javascript");
		setDescription("Create a new javascript in the selected project or folder.");
	}
	
	@Override
	protected InputStream getInitialContents() {
		String s = ScriptService.DEFAULT_JS_HEADER; 
		InputStream result = new ByteArrayInputStream(s.getBytes());
		return result;
	}
	
	
	@Override
	protected String getNewFileLabel() {
		return "Javascript File Name:";
	}
	
	@Override
	public String getFileExtension() {
		return "js"; //$NON-NLS-1$
	}

}
