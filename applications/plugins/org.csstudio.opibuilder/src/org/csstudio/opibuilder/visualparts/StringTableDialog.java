/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.visualparts;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.ui.util.swt.stringtable.RowEditDialog;
import org.csstudio.ui.util.swt.stringtable.StringTableEditor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**A dialog for a string table (2D String Array) editing.
 * @author Xihui Chen
 *
 */
public class StringTableDialog extends Dialog {
	
	private String title;	
	private List<String[]> contents;
	private StringTableEditor tableEditor;
	
	private String[] headers;
	private int[] columnsMinWidth;
	private boolean[] editable;
	private RowEditDialog rowEditDialog;

	protected StringTableDialog(Shell parentShell, List<String[]> contents, String[] headers,
			int[] conlumnsMinWidth, boolean[] editable, RowEditDialog rowEditDialog , String dialogTitle) {
		super(parentShell);
		this.title = dialogTitle;
		this.contents = new ArrayList<String[]>();
		this.contents.addAll(contents);
		this.headers = headers;
		this.columnsMinWidth = conlumnsMinWidth;
		this.editable = editable;
		this.rowEditDialog = rowEditDialog;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite parent_Composite = (Composite) super.createDialogArea(parent);
		final Composite mainComposite = new Composite(parent_Composite, SWT.None);			
		mainComposite.setLayout(new GridLayout(1, false));
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
	
		tableEditor = new StringTableEditor(
				mainComposite, headers, editable, contents, rowEditDialog, columnsMinWidth);
		tableEditor.setLayoutData(gridData);		
		return parent_Composite;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		if (title != null) {
			shell.setText(title);
		}
	}

	public List<String[]> getResult() {
		return contents;
	}
	
	
	
}
