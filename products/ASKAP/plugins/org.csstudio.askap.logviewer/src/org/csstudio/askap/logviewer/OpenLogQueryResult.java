package org.csstudio.askap.logviewer;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class OpenLogQueryResult extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			Perspective.showPerspective();
		} catch (Exception ex) {
			// never mind
		}
		LogQuery.openLogQueryView();
		return LogQueryResultViewer.openLogResultViewer();
	}

}
