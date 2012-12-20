/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.alarm.table.ui.actions;

import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.AlarmConnectionException;
import org.csstudio.alarm.service.declaration.AlarmPreference;
import org.csstudio.alarm.service.declaration.IAlarmConnection;
import org.csstudio.alarm.table.JmsLogsPlugin;
import org.csstudio.alarm.table.internal.localization.Messages;
import org.csstudio.alarm.table.ui.AlarmView;
import org.csstudio.alarm.table.ui.IConnectionHolder;
import org.csstudio.alarm.table.ui.MessageArea;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;

/**
 * Factory for the actions in the log / alarm / archive tools.
 * 
 * @author jpenning
 * @since 12.01.2012
 */
public class LogTableViewActionFactory {
    
    @Nonnull
    public static Action createAndRegisterRetrieveInitialStateAction(@Nonnull final IWorkbenchPartSite site,
                                                                     @Nonnull final AlarmView alarmView) {
        final Action action = new Action() {
            @Override
            public void run() {
                alarmView.retrieveInitialStateForAllPvs();
                // here we should start a job to retrieve the initial state
                //                    alarmService.retrieveInitialState();
                //                try {
                //                } catch (AlarmConnectionException e) {
                //                    MessageDialog.openError(site.getShell(), Messages.LogView_reloadErrorTitle, //$NON-NLS-1$
                //                                            e.getMessage() + "\n"
                //                                                    + Messages.LogView_reloadErrorHint);
                //                }
            }
            
        };
        
        action.setText(Messages.LogView_retrieveInitialStateText);
        action.setToolTipText(Messages.LogView_retrieveInitialStateToolTop);
        action.setImageDescriptor(JmsLogsPlugin
                .getImageDescriptor("icons/retrieveInitialState.gif")); //$NON-NLS-1$
        return action;
    }
    
    @Nonnull
    public static Action createAndRegisterReloadAction(@Nonnull final IWorkbenchPartSite site,
                                                       @Nonnull final IConnectionHolder connectionHolder) {
        final Action reloadAction = new Action() {
            @Override
            public void run() {
                try {
                    IAlarmConnection connection = connectionHolder.getConnection();
                    if (connection != null) {
                        connection.reloadPVsFromResource();
                    } else {
                        MessageDialog.openError(site.getShell(), Messages.LogView_reloadErrorTitle, //$NON-NLS-1$
                                                Messages.LogView_reloadErrorHint);
                    }
                } catch (AlarmConnectionException e) {
                    MessageDialog.openError(site.getShell(), Messages.LogView_reloadErrorTitle, //$NON-NLS-1$
                                            e.getMessage() + "\n" //$NON-NLS-1$
                                                    + Messages.LogView_reloadErrorHint);
                }
            }
            
        };
        
        final String source = AlarmPreference.ALARMSERVICE_CONFIG_VIA_LDAP.getValue() ? " LDAP" //$NON-NLS-1$
                : " XML"; //$NON-NLS-1$
        reloadAction.setText(Messages.LogView_reloadText + source);
        reloadAction.setToolTipText(Messages.LogView_reloadToolTip + source);
        reloadAction.setImageDescriptor(JmsLogsPlugin.getImageDescriptor("icons/refresh.gif")); //$NON-NLS-1$
        return reloadAction;
    }
    
    @Nonnull
    public static Action createAndRegisterPropertyViewAction(@Nonnull final IWorkbenchPartSite site,
                                                             @Nonnull final String viewId) {
        final Action showPropertyViewAction = new Action() {
            @Override
            public void run() {
                try {
                    site.getPage().showView(viewId);
                } catch (final PartInitException e) {
                    MessageDialog.openError(site.getShell(), "Alarm Table", //$NON-NLS-1$
                                            e.getMessage());
                }
            }
        };
        showPropertyViewAction.setText(Messages.LogView_properties);
        showPropertyViewAction.setToolTipText(Messages.LogView_propertiesToolTip);
        
        final IViewRegistry viewRegistry = site.getWorkbenchWindow().getWorkbench()
                .getViewRegistry();
        final IViewDescriptor viewDesc = viewRegistry.find(viewId);
        showPropertyViewAction.setImageDescriptor(viewDesc.getImageDescriptor());
        
        return showPropertyViewAction;
    }
    
    @Nonnull
    public static Action createAndRegisterMessageAreaToggleAction(@Nonnull final MessageArea messageArea) {
        final Action displayMessageAreaAction = new Action() {
            @Override
            public void run() {
                if (messageArea.isVisible()) {
                    messageArea.hide();
                } else {
                    messageArea.show();
                }
            }
        };
        displayMessageAreaAction.setText(Messages.LogView_messageArea);
        displayMessageAreaAction.setToolTipText(Messages.LogView_messageAreaToolTip);
        
        displayMessageAreaAction.setImageDescriptor(JmsLogsPlugin
                .getImageDescriptor("icons/details_view.gif")); //$NON-NLS-1$
        return displayMessageAreaAction;
    }
    
}
