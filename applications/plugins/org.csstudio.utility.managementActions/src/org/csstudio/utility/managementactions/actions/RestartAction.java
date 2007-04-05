package org.csstudio.utility.managementactions.actions;

import org.csstudio.platform.libs.dcf.actions.IAction;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * Management action that restarts the CSS instance.
 * 
 * @author Jörg Rathlev
 */
public class RestartAction implements IAction {

	private final CentralLogger log = CentralLogger.getInstance();
	
	/**
	 * Runs this action.
	 * @param param ignored.
	 * @return {@code null}.
	 */
	public Object run(Object param) {
		log.debug(this, "Restart called by management.");
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				log.info(this, "Restarting CSS");
				boolean result = PlatformUI.getWorkbench().restart();
				if (!result) {
					log.warn(this, "Workbench could not be closed, restart failed.");
				}
			}
		});
		return null;
	}

}
