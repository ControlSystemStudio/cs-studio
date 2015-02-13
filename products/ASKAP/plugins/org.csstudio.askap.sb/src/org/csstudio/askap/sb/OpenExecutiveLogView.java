package org.csstudio.askap.sb;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class OpenExecutiveLogView extends AbstractHandler {

	public OpenExecutiveLogView() {
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ExecutiveLogHelper.getInstance().popConsoleView();
		return null;
	}
}
