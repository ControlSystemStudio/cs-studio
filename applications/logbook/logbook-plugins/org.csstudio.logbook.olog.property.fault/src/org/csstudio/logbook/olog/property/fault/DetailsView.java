package org.csstudio.logbook.olog.property.fault;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.csstudio.logbook.LogEntry;
import org.csstudio.logbook.LogbookClient;
import org.csstudio.logbook.LogbookClientManager;
import org.csstudio.ui.util.AdapterUtil;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

public class DetailsView extends ViewPart {
    
    public static final String ID = "org.csstudio.logbook.olog.property.fault.detailsView";

    private LogbookClient logbookClient;
    private FaultViewWidget faultViewWidget;

    private ISelectionListener selectionListener;

    private Fault fault;

    public DetailsView(){
    }

    public DetailsView(Fault fault) {
        this.fault = fault;
    }

    
    @Override
    public void createPartControl(Composite parent) {
        
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));

        faultViewWidget = new FaultViewWidget(container, SWT.NONE);
        faultViewWidget.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        ISelectionService ss = getSite().getWorkbenchWindow().getSelectionService();
        selectionListener = new ISelectionListener() {

            @Override
            public void selectionChanged(IWorkbenchPart part, ISelection selection) {
                if (faultViewWidget == null || faultViewWidget.isDisposed()) {
                    return;
                }
                if (!selection.isEmpty()) {
                    List<Fault> faults;
                    try {
                        faults = Arrays.asList(AdapterUtil.convert(selection, Fault.class));
                        if (faults.size() == 1) {
                            fault = faults.get(0);
                        }
                        updateUI();
                    } catch (Exception e) {
                        
                    }
                }
            }
        };
        ss.addSelectionListener(org.csstudio.logbook.ui.LogTableView.ID, selectionListener);
        ss.addSelectionListener(org.csstudio.logbook.ui.LogTreeView.ID, selectionListener);
        
        updateUI();
    }

    private void updateUI() {
        if (fault != null) {
            faultViewWidget.setFault(fault);
            ExecutorService ex = Executors.newFixedThreadPool(1);
            ex.execute(() -> {
                try {
                    List<LogEntry> logEntries = new ArrayList<LogEntry>();
                    if (logbookClient == null) {
                        logbookClient = LogbookClientManager.getLogbookClientFactory().getClient();
                    }
                    for (Integer id : fault.getLogIds()) {
                        logEntries.add(logbookClient.findLogEntry(id.longValue()));
                    }
                    Display.getDefault().asyncExec(() -> {
                        faultViewWidget.setLogEntries(logEntries);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void setFocus() {
    }

    public void setFault(Fault fault){
        this.fault = fault;
        updateUI();
    }
}
