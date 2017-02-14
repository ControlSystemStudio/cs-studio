package org.csstudio.logbook.olog.property.fault;
import java.util.List;
import java.util.logging.Logger;

import org.csstudio.ui.util.AbstractAdaptedHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.handlers.HandlerUtil;

public class ExportFault extends AbstractAdaptedHandler<Fault> {

    private static final Logger log = Logger.getLogger(ExportFault.class.getCanonicalName());
    
    public ExportFault() {
        super(Fault.class);
    }

    @Override
    protected void execute(List<Fault> faults, ExecutionEvent event) throws Exception {
        FaultExportDialog dialog = new FaultExportDialog(HandlerUtil.getActiveShell(event), faults);
        dialog.open();
    }


}
