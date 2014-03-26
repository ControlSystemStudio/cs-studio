/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

//+======================================================================
// $HeadURL$
// $Id$
//
// Project       : CODAC Core System
//
// Description   : Active Help implementation to open CODAC documents in pdf format.
//
// Author(s)     : Takashi Nakamoto, Cosylab
//
// Copyright (c) : 2010-2014 ITER Organization,
//                 CS 90 046
//                 13067 St. Paul-lez-Durance Cedex
//                 France
//
//-======================================================================

package org.csstudio.iter.css.product.activehelp;

import org.eclipse.help.ILiveHelpAction;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import java.io.IOException;
import java.io.File;

/**
 * Active Help implementation to open CODAC documents in pdf format.
 * @author Takashi Nakamoto, Cosylab
 */
public class ActiveHelpOpenCodacPdf implements ILiveHelpAction {
	public static final String PREFS_ID = "org.csstudio.iter.css.product";
	public static final String PDF_DOC_ROOT = "pdf_doc_root";
	public static final String PDF_VIEWER = "pdf_viewer";

	String pdfFile;

	public void setInitializationString(String data) {
		pdfFile = data;
	}

	public void run() {
		final String codacRoot = System.getenv("CODAC_ROOT");
		final String defaultPdfDocRoot = codacRoot + "/doc/pdf";

		final IPreferencesService prefs = Platform.getPreferencesService();
		String pdfDocRoot = prefs.getString(PREFS_ID, PDF_DOC_ROOT, defaultPdfDocRoot, null);
		String pdfViewer = prefs.getString(PREFS_ID, PDF_VIEWER, "acroread %s", null);

		String pdfPath = pdfDocRoot + File.separator + pdfFile;
		String cmd = pdfViewer.replaceFirst("%s", pdfPath);

		try {
			Runtime rt = Runtime.getRuntime();
			rt.exec(cmd);
		} catch (IOException ex) {
			ex.printStackTrace();
			System.err.println("Failed to run: " + cmd);
		}

		//		System.out.println("Opening PDF file: " + cmd);
	}
}
