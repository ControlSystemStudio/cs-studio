/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.scan.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.csstudio.ui.util.wizards.WizardNewFileCreationPage;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * The Class NewScanFileWizardPage.
 * @author benhad naceur @ sopra group 
 */
public class NewScanFileWizardPage extends WizardNewFileCreationPage {

	/** The Constant DEFAULT_SCN_HEADER. */
	public static final String DEFAULT_SCN_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n <commands> \n </commands>";
	    
    /** The Constant SCAN_FILE_EXTENSION. */
    public static final String SCAN_FILE_EXTENSION = "scn";
    
	/**
	 * Instantiates a new new scan file wizard page.
	 *
	 * @param pageName the page name
	 * @param selection the selection
	 */
	public NewScanFileWizardPage(String pageName, IStructuredSelection selection) {
		super(pageName, selection);
		setTitle("Create a new Scan File");
		setDescription("Create a new Scan file in the selected project or folder.");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected InputStream getInitialContents() {
		InputStream result = new ByteArrayInputStream(DEFAULT_SCN_HEADER.getBytes());
		return result;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getNewFileLabel() {
		return "Scan File Name:";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFileExtension() {
		return SCAN_FILE_EXTENSION;
	}

}
