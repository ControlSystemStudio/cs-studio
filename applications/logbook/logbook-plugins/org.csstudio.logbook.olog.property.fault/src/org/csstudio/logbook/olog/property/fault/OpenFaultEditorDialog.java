package org.csstudio.logbook.olog.property.fault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.util.LogEntryUtil;
import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * This command is to handle 2 cases
 * 1. a single fault is selected and is being edited.
 * 2. a single fault is being updated with a list of log entries.
 *
 * @author Kunal Shroff
 *
 */
public class OpenFaultEditorDialog extends AbstractHandler{

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
        final Fault fault;
        final LogEntry faultLog;
        List<Integer> logIds = new ArrayList<Integer>();

        IAdapterManager adapterManager = Platform.getAdapterManager();
        List<LogEntry> selectedLogs = new ArrayList<LogEntry>();

        if (selection instanceof IStructuredSelection) {
            IStructuredSelection strucSelection = (IStructuredSelection) selection;

            for (Object iterable_element : strucSelection.toList()) {
                selectedLogs.add((LogEntry) adapterManager.getAdapter(iterable_element, LogEntry.class));
            }

        }

        logIds.addAll(selectedLogs.stream().map((logEntry)->{
          return Integer.valueOf(String.valueOf(logEntry.getId()));}).collect(Collectors.toList()));

        // Find the log entries with faults associated with them.
        List<LogEntry> faultEntries = Arrays.asList(AdapterUtil.convert(selection, LogEntry.class));
        if(faultEntries != null) {
            faultEntries = faultEntries.stream().filter((logEntry)->{
                return LogEntryUtil.getProperty(logEntry, FaultAdapter.FAULT_PROPERTY_NAME) != null;
            }).collect(Collectors.toList());
        }
        if(faultEntries.size() == 1){
            faultLog = faultEntries.get(0);
            fault = FaultAdapter.extractFaultFromLogEntry(faultLog);

            FaultEditorDialog dialog = new FaultEditorDialog(HandlerUtil.getActiveShell(event), false, fault, faultLog, logIds);
            dialog.setBlockOnOpen(false);
            // Initialize the logbooks and tags

            Display.getDefault().asyncExec(() -> {
                if (dialog.open() == Window.OK) {
                }
            });
        } else {
            // Incorrect number of faults were selected for updating
            // Open and error dialog
        }
        return null;
    }

}
