/*******************************************************************************
 * Copyright (c) 2010-2014 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.autocomplete.ui.preferences;

import org.csstudio.autocomplete.ui.AutoCompleteUIPlugin;
import org.csstudio.autocomplete.ui.Messages;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * Preference Page, registered in plugin.xml
 * 
 * @author Fred Arnaud (Sopra Group)
 */
public class PreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	/** Initialize */
	public PreferencePage() {
		super(FieldEditorPreferencePage.GRID);
		setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE,
				AutoCompleteUIPlugin.PLUGIN_ID));
		setMessage(Messages.PrefPage_Title);
	}

	/** {@inheritDoc} */
	@Override
	public void init(IWorkbench workbench) {
		// NOP
	}

	/** {@inheritDoc} */
	@Override
	protected void createFieldEditors() {
		final Composite parent = getFieldEditorParent();

		addField(new StringFieldEditor(Preferences.HISTORY_SIZE,
				Messages.PrefPage_HistorySize, parent));

		final Button clearHistory = new Button(parent, SWT.PUSH);
		clearHistory.setText(Messages.PrefPage_ClearHistory);
		clearHistory.setLayoutData(new GridData());
		clearHistory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				AutoCompleteUIPlugin.getDefault().clearSettings();
			}
		});

		final Composite noteWrapper = new Composite(parent, SWT.NONE);
		noteWrapper.setLayoutData(new GridData());
		noteWrapper.setLayout(new GridLayout(2, false));

		final Label noteLabel = new Label(noteWrapper, SWT.NONE);
		FontData fontData = noteLabel.getFont().getFontData()[0];
		fontData.setStyle(SWT.BOLD);
		noteLabel.setFont(new Font(parent.getDisplay(), fontData));
		noteLabel.setText("Note: ");

		final Text note = new Text(noteWrapper, SWT.MULTI | SWT.READ_ONLY);
		note.setBackground(parent.getBackground());
		note.setText("The 'History size' value is the maximum number of entries in the History.\nEach entry is stored only once and the entries of the History are sorted \naccording to their occurrence.");
	}

}
