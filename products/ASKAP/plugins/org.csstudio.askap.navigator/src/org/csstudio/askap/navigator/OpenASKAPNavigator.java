package org.csstudio.askap.navigator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class OpenASKAPNavigator extends AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		return NavigatorView.openNavigationView();
	}

}
