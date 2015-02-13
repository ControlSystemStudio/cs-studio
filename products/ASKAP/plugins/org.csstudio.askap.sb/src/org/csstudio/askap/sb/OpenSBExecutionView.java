package org.csstudio.askap.sb;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class OpenSBExecutionView extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		return SBExecutionView.openSBExecutionView();
	}

}
