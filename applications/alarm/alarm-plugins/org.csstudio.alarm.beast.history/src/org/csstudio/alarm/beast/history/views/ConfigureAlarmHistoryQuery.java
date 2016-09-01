package org.csstudio.alarm.beast.history.views;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class ConfigureAlarmHistoryQuery extends AbstractHandler{

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();

        IWorkbenchPage page = window.getActivePage();
        AlarmHistoryView alarmHistoryView = (AlarmHistoryView) page.findView(AlarmHistoryView.ID);

        AlarmHistoryQueryParametersDialog dialog = new AlarmHistoryQueryParametersDialog(
                window.getShell(), alarmHistoryView.getAlarmHistoryQueryParameters(),
                "Configure Alarm History Query");
        if (dialog.open() == IDialogConstants.OK_ID) {
            alarmHistoryView.setAlarmHistoryQueryParameters(dialog.getAlarmHistoryQueryParameters());
        }
        return null;
    }


}
