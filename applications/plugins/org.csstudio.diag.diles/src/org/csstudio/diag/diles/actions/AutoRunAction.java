package org.csstudio.diag.diles.actions;

import org.csstudio.diag.diles.palette.DilesPalette;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

public class AutoRunAction extends Action implements IWorkbenchAction {

	private static final String ID = "org.csstudio.diag.diles.actions.AutoRunAction";

	public static boolean getRun() {
		return run;
	}

	public static void setRun(boolean b) {
		run = b;
	}

	public static void stop() {
		checkFeed.interrupt();
	}

	private long lastFeedCheck = 0;

	private int miliseconds = 20;

	private static boolean run = false;

	protected static Thread checkFeed;

	public AutoRunAction() {
		setId(ID);
		setToolTipText("Automatic run");
		setImageDescriptor(ImageDescriptor.createFromFile(DilesPalette.class,
				"icons/run.png"));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionFactory.IWorkbenchAction#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		// runs only if not running already
		if (getRun()) {
			return;
		}

		final Shell shell = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell();

		runThread(shell);
	}

	private void runThread(final Shell shell) {
		setRun(true);

		checkFeed = new RunThread(shell, lastFeedCheck, miliseconds);

		// launch the thread for the first time
		shell.getDisplay().asyncExec(checkFeed);

	}

}
