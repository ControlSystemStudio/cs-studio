package org.csstudio.logbook.olog.property.fault;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.util.LogEntryUtil;
import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.ExtensionContributionFactory;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.services.IServiceLocator;

/**
 * 
 * @author Kunal Shroff
 *
 */
public class FaultCommands extends ExtensionContributionFactory {

    public FaultCommands() {
        
    }

    @Override
    public void createContributionItems(IServiceLocator serviceLocator, IContributionRoot additions) {
        ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
        if (selection instanceof IStructuredSelection) {
            if (!selection.isEmpty()) {
                List<LogEntry> selectedLogs = Arrays.asList(AdapterUtil.convert(selection, LogEntry.class));
                List<LogEntry> faultEntries = new ArrayList<LogEntry>();
                if (selectedLogs != null) {
                    faultEntries.addAll(selectedLogs.stream().filter((logEntry) -> {
                        return LogEntryUtil.getProperty(logEntry, FaultAdapter.FAULT_PROPERTY_NAME) != null;
                    }).collect(Collectors.toList()));
                }
                if (faultEntries.size() == 1 && selectedLogs != null && selectedLogs.size() > 1) {
                    CommandContributionItemParameter p = new CommandContributionItemParameter(serviceLocator, "",
                            "org.csstudio.logbook.olog.property.fault.OpenFaultEditorDialog", SWT.PUSH);
                    p.label = "Add Logs to fault";
                    CommandContributionItem item = new CommandContributionItem(p);
                    item.setVisible(true);
                    additions.addContributionItem(item, null);
                }
            }

        }
    }

}
