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

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.util.SchemaService;
import org.csstudio.ui.util.wizards.WizardNewFileCreationPage;
import org.eclipse.jface.viewers.IStructuredSelection;

/**Wizard page for the creation of new OPI files.
 * @author Xihui Chen
 *
 */
public class NewOPIFileWizardPage extends WizardNewFileCreationPage {

	public NewOPIFileWizardPage(String pageName, IStructuredSelection selection) {
		super(pageName, selection);
		setTitle("Create a new OPI File");
		setDescription("Create a new OPI file in the selected project or folder.");
	}
	
	@Override
	protected InputStream getInitialContents() {
		DisplayModel displayModel = new DisplayModel();
		SchemaService.getInstance().applySchema(displayModel);
		String s = XMLUtil.widgetToXMLString(displayModel, true);
		InputStream result = new ByteArrayInputStream(s.getBytes());
		return result;
	}
	
	
	@Override
	protected String getNewFileLabel() {
		return "OPI File Name:";
	}
	
	@Override
	public String getFileExtension() {
		return OPIBuilderPlugin.OPI_FILE_EXTENSION;
	}

}
