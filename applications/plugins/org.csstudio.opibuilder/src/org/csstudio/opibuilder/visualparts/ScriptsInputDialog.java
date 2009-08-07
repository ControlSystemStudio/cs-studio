package org.csstudio.opibuilder.visualparts;

import java.util.List;

import org.csstudio.opibuilder.properties.support.ScriptData;
import org.csstudio.opibuilder.properties.support.ScriptsInput;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class ScriptsInputDialog extends TitleAreaDialog {

	
	private List<ScriptData> scriptDataList;	
	public ScriptsInputDialog(Shell parentShell, ScriptsInput scriptsInput, String dialogTitle) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.scriptDataList = scriptsInput.getCopy().getScriptList();
		setTitle(dialogTitle);
	}
	
	
	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite parent_Composite = (Composite) super.createDialogArea(parent);
		
		final Composite composite = new Composite(parent_Composite, SWT.None);
		
		
		
		
		
		return parent_Composite;
		
		
	}
	
	
	
	

	

}
