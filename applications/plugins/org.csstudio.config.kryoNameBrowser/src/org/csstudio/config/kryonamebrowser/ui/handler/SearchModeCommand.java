package org.csstudio.config.kryonamebrowser.ui.handler;

import org.csstudio.config.kryonamebrowser.ui.MainView;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class SearchModeCommand extends AbstractHandler implements IHandler {

	boolean isChecked;

	public Object execute(ExecutionEvent event) throws ExecutionException {

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage page = window.getActivePage();
		final MainView view = (MainView) page.findView(MainView.ID);

		isChecked = !isChecked;

		if (isChecked) {
			view.getFilter().advancedMode();
		} else {
			view.getFilter().basicMode();
		}

		return null;
	}

}
