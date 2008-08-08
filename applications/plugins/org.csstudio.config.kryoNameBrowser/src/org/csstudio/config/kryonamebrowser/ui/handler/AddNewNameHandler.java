package org.csstudio.config.kryonamebrowser.ui.handler;

import org.csstudio.config.kryonamebrowser.ui.MainView;
import org.csstudio.config.kryonamebrowser.ui.dialog.NewKryoNameComposite;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class AddNewNameHandler extends AbstractHandler implements IHandler {

	public static final String ID = "addNewName.command";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		NewKryoNameComposite dialog = new NewKryoNameComposite(Display
				.getDefault().getActiveShell());

		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage page = window.getActivePage();
		MainView view = (MainView) page.findView(MainView.ID);

		dialog.setLogic(view.getLogic());
		dialog.open();

		return null;
	}

}
