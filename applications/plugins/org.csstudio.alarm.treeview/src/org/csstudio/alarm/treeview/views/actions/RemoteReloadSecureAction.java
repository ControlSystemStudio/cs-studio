package org.csstudio.alarm.treeview.views.actions;

import javax.annotation.Nonnull;


import org.csstudio.alarm.service.declaration.AlarmPreference;
import org.csstudio.alarm.treeview.localization.Messages;
import org.csstudio.alarm.treeview.views.MessageArea;
import org.csstudio.auth.ui.security.AbstractUserDependentAction;
import org.csstudio.remote.jms.command.IRemoteCommandService;
import org.csstudio.remote.jms.command.RemoteCommandException;
import org.csstudio.servicelocator.ServiceLocator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action to tell other clients remotely to reload the ldap configuration.
 * To do so, the reload service is called.
 * 
 * @author jpenning
 * @since 12.01.2012
 */
public class RemoteReloadSecureAction extends AbstractUserDependentAction {
    
    private static final Logger LOG = LoggerFactory.getLogger(RemoteReloadSecureAction.class);
    
    private static final String RIGHT_ID = "alarmConfiguration"; //$NON-NLS-1$
    private static final boolean DEFAULT_PERMISSION = false;
    
    private final MessageArea _messageArea;
    
    public RemoteReloadSecureAction(@Nonnull final MessageArea messageArea) {
        super(RIGHT_ID, DEFAULT_PERMISSION);
        _messageArea = messageArea;
    }
    
    @Override
    protected void doWork() {
        IRemoteCommandService service = ServiceLocator.getService(IRemoteCommandService.class);
        if (service != null) {
            try {
                if (isSendCommandConfirmed()) {
                    sendCommand(service);
                }
            } catch (RemoteCommandException e) {
                LOG.error("Sending 'Remote Reload' command failed", e); //$NON-NLS-1$
                _messageArea.showMessage(SWT.ICON_ERROR,
                                         "", Messages.RemoteReloadSecureAction_SendingCmd_Failed); //$NON-NLS-1$
            }
        } else {
            LOG.error("Sending 'Remote Reload' command failed due to missing service"); //$NON-NLS-1$
            _messageArea
                    .showMessage(SWT.ICON_ERROR,
                                 "", Messages.RemoteReloadSecureAction_SendingCmd_Failed_ServiceMissing); //$NON-NLS-1$
        }
    }
    
    private boolean isSendCommandConfirmed() {
        final MessageBox messageBox = new MessageBox(Display.getDefault().getActiveShell(), SWT.OK
                | SWT.CANCEL | SWT.ICON_QUESTION);
        messageBox.setText(Messages.RemoteReloadSecureAction_Title);
        messageBox.setMessage(Messages.RemoteReloadSecureAction_Text);
        final int buttonID = messageBox.open();
        return buttonID == SWT.OK;
    }
    
    private void sendCommand(@Nonnull final IRemoteCommandService service) throws RemoteCommandException {
        service.sendCommand(AlarmPreference.getClientGroup(),
                            IRemoteCommandService.ReloadFromLdapCommand);
    }
}
