package org.csstudio.askap.logviewer;

import org.csstudio.askap.logviewer.ui.RealtimeTopicSelectionDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class OpenLogViewer extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// user has to choose a topic name
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();

		RealtimeTopicSelectionDialog dialog = new RealtimeTopicSelectionDialog(
				window.getShell());

		String topicName = dialog.open();

		if (topicName == null || topicName.trim().length() == 0)
			return null;

		return LogViewer.openLogViewer(topicName);
	}
}
