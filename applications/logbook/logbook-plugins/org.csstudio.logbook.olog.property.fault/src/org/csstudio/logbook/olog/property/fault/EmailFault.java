package org.csstudio.logbook.olog.property.fault;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.email.EMailSender;
import org.csstudio.email.Preferences;
import org.csstudio.email.ui.EMailSenderDialog;
import org.csstudio.ui.util.AbstractAdaptedHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.handlers.HandlerUtil;

public class EmailFault extends AbstractAdaptedHandler<Fault> implements IHandler {

    private static final Logger log = Logger.getLogger(EmailFault.class.getCanonicalName());
    
    public EmailFault() {
        super(Fault.class);
    }

    @Override
    protected void execute(List<Fault> faults, ExecutionEvent event) throws Exception {
        for (Fault fault : faults) {
            if (fault.getContact() != null && !fault.getContact().isEmpty()) {
                EMailSender mailer;
                try {
                    EMailSenderDialog dialog = new EMailSenderDialog(HandlerUtil.getActiveShell(event), 
                            Preferences.getSMTP_Host(), 
                            "cs-studio", 
                            fault.getContact(),
                            FaultAdapter.faultSummaryString(fault),
                            FaultAdapter.faultString(fault));
                    dialog.open();
                } catch (Exception e) {
                    log.log(Level.WARNING, "Failed to send fault message", e);
                }
            }
            
        }
    }

}
