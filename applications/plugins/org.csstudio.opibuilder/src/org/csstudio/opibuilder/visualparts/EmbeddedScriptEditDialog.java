/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.visualparts;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.script.ScriptData;
import org.csstudio.opibuilder.script.ScriptService;
import org.csstudio.opibuilder.script.ScriptService.ScriptType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**The dialog for embedded script editing.
 * @author Xihui Chen
 *
 */
public class EmbeddedScriptEditDialog extends HelpTrayDialog {
	
	private ScriptData scriptData;
	
	private Text nameText, scriptText;
	
	private Combo scriptTypeCombo;
	

	/**Constructor.
	 * @param parentShell
	 * @param scriptData the scriptData to be edited. null if a new scriptdata to be created.
	 */
	public EmbeddedScriptEditDialog(Shell parentShell, ScriptData scriptData) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);		
		if(scriptData != null)
			this.scriptData = scriptData.getCopy();
	}
	
	
	@Override
	protected void okPressed() {
		if(nameText.getText().trim().isEmpty()){
			MessageDialog.openError(getShell(), "Error", "Script name cannot be empty");
			return;
		}
		if(scriptData == null)
			scriptData = new ScriptData();
				
		scriptData.setEmbedded(true);
		scriptData.setScriptName(nameText.getText());
		scriptData.setScriptText(scriptText.getText());
		scriptData.setScriptType(ScriptType.values()[scriptTypeCombo.getSelectionIndex()]);
		super.okPressed();
	}
	
	@Override
	protected String getHelpResourcePath() {
		return "/" + OPIBuilderPlugin.PLUGIN_ID + "/html/Script.html"; //$NON-NLS-1$; //$NON-NLS-2$
	}
	
	public ScriptData getResult(){
		
		return scriptData;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);		
		shell.setText("Edit Script");		
	}
	
	/**
	 * Creates 'wrapping' label with the given text.
	 * 
	 * @param parent
	 *            The parent for the label
	 * @param text
	 *            The text for the label
	 */
	private void createLabel(final Composite parent, final String text) {
		Label label = new Label(parent, SWT.WRAP);
		label.setText(text);
		label.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false));
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite dialogArea = (Composite) super.createDialogArea(parent);
		dialogArea.setLayout(new GridLayout(2, false));
		createLabel(dialogArea, "Name: ");
		GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		nameText = new Text(dialogArea, SWT.BORDER);
		if(scriptData != null)
			nameText.setText(scriptData.getScriptName());
		else{
			nameText.setText("EmbeddedScript");
			nameText.selectAll();
		}
		nameText.setLayoutData(gd);
		createLabel(dialogArea, "Script Type: ");
		scriptTypeCombo = new Combo(dialogArea, SWT.DROP_DOWN|SWT.READ_ONLY);
		scriptTypeCombo.setItems(ScriptType.stringValues());
		if(scriptData != null)
			scriptTypeCombo.select(scriptData.getScriptType().ordinal());
		else
			scriptTypeCombo.select(0);
		scriptTypeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(scriptData == null){
					if(scriptTypeCombo.getSelectionIndex() == ScriptType.JAVASCRIPT.ordinal() &&
						scriptText.getText().trim().equals(ScriptService.DEFAULT_PYTHONSCRIPT_HEADER.trim()))
						scriptText.setText(ScriptService.DEFAULT_JS_HEADER);
					else if (scriptTypeCombo.getSelectionIndex() == ScriptType.PYTHON.ordinal() &&
						scriptText.getText().trim().equals(ScriptService.DEFAULT_JS_HEADER.trim()))
						scriptText.setText(ScriptService.DEFAULT_PYTHONSCRIPT_HEADER);
				}
				
			}
		});
		scriptTypeCombo.setLayoutData(gd);
		scriptText = new Text(dialogArea, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		gd.widthHint = 400;
		gd.heightHint = 200;
		scriptText.setLayoutData(gd);
		if(scriptData != null)
			scriptText.setText(scriptData.getScriptText());
		else
			scriptText.setText(ScriptService.DEFAULT_JS_HEADER);
		return this.dialogArea;
	}
}
