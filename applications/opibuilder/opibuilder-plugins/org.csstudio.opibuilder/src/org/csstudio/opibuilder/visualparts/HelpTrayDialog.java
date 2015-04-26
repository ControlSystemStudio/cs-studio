package org.csstudio.opibuilder.visualparts;

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;

/**A {@link TrayDialog} that is able to open help contents.
 * @author Xihui Chen
 *
 */
public abstract class HelpTrayDialog extends TrayDialog {

	protected HelpTrayDialog(IShellProvider parentShell) {
		super(parentShell);
		setHelpAvailable(true);
	}
	
	protected HelpTrayDialog(Shell shell) {
		super(shell);
		setHelpAvailable(true);
	}
	
	@Override
	protected Control createHelpControl(Composite parent) {
		Control control = super.createHelpControl(parent);
		if(control instanceof ToolBar){
			((ToolBar)control).getItem(0).addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					openHelp();
				}
			});
		}else if(control instanceof Link){
			((Link)control).addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					openHelp();
				}
			});
		}
	
		return control;
	}
	
	protected void openHelp(){
		if(getHelpResourcePath() != null)
			PlatformUI.getWorkbench().getHelpSystem().displayHelpResource(getHelpResourcePath());
	}
	
	/**@see IWorkbenchHelpSystem#displayHelpResource(String)
	 * @return the help resource path. Return null or empty string will not open the help window.
	 */
	protected abstract String getHelpResourcePath();
	

}
