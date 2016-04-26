/**
 * 
 */
package org.csstudio.alarm.beast.history.views;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.csdata.TimestampedPV;
import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import static org.csstudio.alarm.beast.history.views.AlarmHistoryQueryParameters.AlarmHistoryQueryBuilder.*;

/**
 * @author Kunal Shroff
 *
 */
public class ShowAlarmHistoryCommand extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        // create the Alarm history query
        final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
        AlarmHistoryQueryParameters alarmHistoryQueryParameters = buildQuery().build();

        if (Platform.isRunning()) {
            if (selection instanceof IStructuredSelection) {
                TimestampedPV[] timestampedPVs = AdapterUtil.convert(selection, TimestampedPV.class);
                if (timestampedPVs == null || timestampedPVs.length == 0) {
                    // failed to convert to TimestampedPVs try ProcessVariable
                    ProcessVariable[] pvs = AdapterUtil.convert(selection, ProcessVariable.class);
                    if (pvs != null && pvs.length > 0) {
                        alarmHistoryQueryParameters = Platform.getAdapterManager().getAdapter(pvs,
                                AlarmHistoryQueryParameters.class);
                    }
                } else {
                    alarmHistoryQueryParameters = Platform.getAdapterManager().getAdapter(timestampedPVs,
                            AlarmHistoryQueryParameters.class);
                }
            }
        }

        final IWorkbench workbench = PlatformUI.getWorkbench();
        final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();

        IWorkbenchPage page = window.getActivePage();
        try {
            AlarmHistoryView alarmHistoryView = (AlarmHistoryView)page.showView(AlarmHistoryView.ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        AlarmHistoryView alarmHistoryView = (AlarmHistoryView) page.findView(AlarmHistoryView.ID);
        alarmHistoryView.setAlarmHistoryQueryParameters(alarmHistoryQueryParameters);
        return null;
    }

}
