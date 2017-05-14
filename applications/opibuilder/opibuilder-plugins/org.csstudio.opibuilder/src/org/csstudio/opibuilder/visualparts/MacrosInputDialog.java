/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.visualparts;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.csstudio.opibuilder.preferences.MacroEditDialog;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.ui.util.swt.stringtable.StringTableEditor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.jdom.Verifier;

/**The dialog for editing macros.
 * @author Xihui Chen
 *
 */
public class MacrosInputDialog extends Dialog {

    private String title;
    private List<String[]> contents;
    private boolean includeParentMacros;

    private StringTableEditor tableEditor;

    protected MacrosInputDialog(Shell parentShell, MacrosInput macrosInput, String dialogTitle) {
        super(parentShell);
        this.title = dialogTitle;
        this.contents = new ArrayList<String[]>();
        for(String key : macrosInput.keySet()){
            this.contents.add(new String[]{key, macrosInput.get(key)});
        }
        this.includeParentMacros = macrosInput.isInclude_parent_macros();

        // Allow resize
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        final Composite container = (Composite) super.createDialogArea(parent);
        // Table editor should stretch to fill the dialog space, but
        // at least on OS X, it has some minimum size below which it
        // doesn't properly shrink.
        tableEditor = new StringTableEditor(
                container, new String[]{"Name", "Value"}, new boolean[]{true, true},
                contents, new MacroEditDialog(getShell()), new int[]{150, 150});
        tableEditor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Button checkBox = new Button(container, SWT.CHECK);
        checkBox.setSelection(includeParentMacros);
        checkBox.setText("Include macros from parent.");
        checkBox.setLayoutData(new GridData(SWT.FILL, 0, true, false));
        checkBox.addSelectionListener(new SelectionAdapter(){
            @Override
            public void widgetSelected(SelectionEvent e) {
                includeParentMacros = checkBox.getSelection();
            }
        });
        return container;
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

    public MacrosInput getResult() {
        LinkedHashMap<String, String> macrosMap = new LinkedHashMap<String, String>();
        for(String[] row : contents){
            macrosMap.put(row[0], row[1]);
        }
        return new MacrosInput(macrosMap, includeParentMacros);
    }

    @Override
    protected void okPressed() {
        tableEditor.forceFocus();  //this can help the last edit value applied.
        String reason;
        for(String[] row : contents){
            reason = Verifier.checkElementName(row[0]);
            if(reason != null){
                MessageDialog.openError(getShell(),    "Illegal Macro Name",
                        NLS.bind("{0} is not a valid Macro name.\n {1}", row[0], reason));
                return;
            }
        }
        super.okPressed();
    }
}
