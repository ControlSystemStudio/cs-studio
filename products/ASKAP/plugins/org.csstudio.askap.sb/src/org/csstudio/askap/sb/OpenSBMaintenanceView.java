package org.csstudio.askap.sb;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class OpenSBMaintenanceView extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			PerspectiveFactory.showPerspective();
		} catch (Exception ex) {
			// never mind
		}
		return SBMaintenanceView.openSBMaintenanceView();
	}

}
