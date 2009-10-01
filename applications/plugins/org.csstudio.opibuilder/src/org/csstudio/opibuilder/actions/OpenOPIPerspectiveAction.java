package org.csstudio.opibuilder.actions;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;


/**The action that opens the OPI Editor perspective
 * @author Xihui Chen
 *
 */
public class OpenOPIPerspectiveAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;
	
	public void dispose() {

	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void run(IAction action) {
		try {
			PlatformUI.getWorkbench().showPerspective(
					"org.csstudio.opibuilder.opieditor", window);
		} catch (WorkbenchException e) {
			final String message = NLS.bind(
					"Failed to open OPI Editor perspective. \n{0}", e.getMessage());
			MessageDialog.openError(null, "Error", 
						message);	
			CentralLogger.getInstance().error(this, message, e);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {

	}

}
