/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.visualparts;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**A dialog with two buttons to select which type of script need to be created.
 * @author Xihui Chen
 *
 */
public class ScriptChoiceDialog extends Dialog {
	
	private boolean isEmbedded;

	public ScriptChoiceDialog(Shell parentShell) {
		super(parentShell);
	}	

	
	@Override
	protected Control createContents(Composite parent) {
		parent.setLayout(new GridLayout(1, true));
		
		Button chooseFileButton = new Button(parent, SWT.PUSH);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 50;
		chooseFileButton.setLayoutData(gd);
		chooseFileButton.setText("Choose Script File...");
		chooseFileButton.setImage(CustomMediaFactory.getInstance().getImageFromPlugin(
				OPIBuilderPlugin.PLUGIN_ID, "icons/folder.gif"));
		chooseFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isEmbedded = false;
				okPressed();
			}
		});
		Button embedButton = new Button(parent, SWT.PUSH);
		embedButton.setLayoutData(gd);
		embedButton.setText("Create an Embedded Script...");
		embedButton.setImage(CustomMediaFactory.getInstance().getImageFromPlugin(
				OPIBuilderPlugin.PLUGIN_ID, "icons/edit.gif"));
		embedButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isEmbedded = true;
				okPressed();
			}
		});
		return parent;
	}

	/**
	 * @return the isEmbedded
	 */
	public boolean isEmbedded() {
		return isEmbedded;
	}

	

}
