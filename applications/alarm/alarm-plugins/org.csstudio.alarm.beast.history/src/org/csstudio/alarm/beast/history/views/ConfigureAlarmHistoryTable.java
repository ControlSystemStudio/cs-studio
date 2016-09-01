package org.csstudio.alarm.beast.history.views;
import org.csstudio.ui.util.dialogs.StringListSelectionDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class ConfigureAlarmHistoryTable extends AbstractHandler {


    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();

        IWorkbenchPage page = window.getActivePage();
        AlarmHistoryView alarmHistoryView = (AlarmHistoryView) page.findView(AlarmHistoryView.ID);

        StringListSelectionDialog dialog = new StringListSelectionDialog(
                window.getShell(), AlarmHistoryView.getColumnname(), alarmHistoryView
                        .getVisibleColumns(),
                "Select Columns to Display");
        if (dialog.open() == IDialogConstants.OK_ID) {
            alarmHistoryView.setVisibleColumns(dialog.getSelectedValues());
        }
        return null;
    }

}
