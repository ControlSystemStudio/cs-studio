package org.csstudio.opibuilder.visualparts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.opibuilder.preferences.MacroEditDialog;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.platform.ui.swt.stringtable.StringTableEditor;
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
		for(String key : macrosInput.getMacrosMap().keySet()){
			this.contents.add(new String[]{key, macrosInput.getMacrosMap().get(key)});
		}
		this.includeParentMacros = macrosInput.isInclude_parent_macros();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite parent_Composite = (Composite) super.createDialogArea(parent);
		final Composite mainComposite = new Composite(parent_Composite, SWT.None);			
		mainComposite.setLayout(new GridLayout(1, false));
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
	
		tableEditor = new StringTableEditor(
				mainComposite, new String[]{"Name", "Value"}, new boolean[]{true, true},
				contents, new MacroEditDialog(getShell()), new int[]{150, 150});
		tableEditor.setLayoutData(gridData);	
		
		final Button checkBox = new Button(mainComposite, SWT.CHECK);
		checkBox.setSelection(includeParentMacros);
		checkBox.setText("Include the macros from parent.");
		checkBox.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				includeParentMacros = checkBox.getSelection();
			}
		});
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

	public MacrosInput getResult() {
		Map<String, String> macrosMap = new HashMap<String, String>();
		for(String[] row : contents){
			macrosMap.put(row[0], row[1]);
		}		
		return new MacrosInput(macrosMap, includeParentMacros);
	}
	
	
	
}
