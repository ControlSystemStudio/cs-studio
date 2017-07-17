package org.csstudio.trayicon;

import java.io.IOException;

import org.csstudio.utility.product.ApplicationWorkbenchWindowAdvisor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class TrayApplicationWorkbenchWindowAdvisor extends ApplicationWorkbenchWindowAdvisor {

    // This requires internal understanding. Since we have changed the labels on
    // the dialog, MessageButtonWithDialog does not assign standard return codes.
    // This is fixed in Oxygen but for now we need to know what is going to be returned.
    public static final String[] BUTTON_LABELS = {
            Messages.TrayDialog_minimize,
            Messages.TrayDialog_exit,
            Messages.TrayDialog_cancel};
    private static final int MINIMIZE_BUTTON_ID = 256;
    private static final int EXIT_BUTTON_ID = 257;
    private static final int CANCEL_BUTTON_ID = IDialogConstants.CANCEL_ID;
    private static final int DIALOG_CLOSED = -1;

    private TrayIcon trayIcon;

    public TrayApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer, TrayIcon trayIcon) {
        super(configurer);
        this.trayIcon = trayIcon;
    }

    /**
     * Prompt the user for selection of minimise on exit behaviour.
     *
     * @return xx_BUTTON_ID of clicked button or DIALOG_CLOSED
     */
    private int promptForAction() {
        Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        ScopedPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE, Plugin.ID);
        MessageDialogWithToggle dialog = new MessageDialogWithToggle(parent, Messages.TrayDialog_title, null,
                Messages.TrayDialog_question, MessageDialog.QUESTION, BUTTON_LABELS, 2,
                Messages.TrayDialog_rememberDecision, false);
        dialog.open();

        int response = dialog.getReturnCode();

        // Store the decision if checkbox selected on the form
        if (dialog.getToggleState()) {
            if (response == MINIMIZE_BUTTON_ID) {
                store.setValue(TrayIconPreferencePage.MINIMIZE_TO_TRAY, MessageDialogWithToggle.ALWAYS);
            } else if (response == EXIT_BUTTON_ID) {
                store.setValue(TrayIconPreferencePage.MINIMIZE_TO_TRAY, MessageDialogWithToggle.NEVER);
            }
            try {
                store.save();
            } catch (IOException e) {
                Plugin.getLogger().warning(Messages.TrayPreferences_saveFailed + e.getMessage());
            }
        }
        return response;
    }

    /**
     * Manage a close event based on the user preferences, user action
     *
     *  Three possible outcomes:
     *  i) abort the exit (return False)
     *      * user:CANCEL
     *      * user:DIALOG_CLOSED
     *  ii) continue to close this window and possibly the application (return preWindowShellClose())
     *      * preference:NEVER
     *      * user:EXIT
     *      * multiple open windows
     *      * application already minimised
     *  iii) create trayIcon, minimise window but do not exit (return False)
     *      * preference:ALWAYS
     *      * user:MINIMIZE
     *
     * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#preWindowShellClose
     */
    @Override
    public boolean preWindowShellClose() {

        boolean closeWindow;
        int userAction = DIALOG_CLOSED;
        int numWindows = PlatformUI.getWorkbench().getWorkbenchWindowCount();

        IPreferencesService prefs = Platform.getPreferencesService();
        String minPref = prefs.getString(
                Plugin.ID, TrayIconPreferencePage.MINIMIZE_TO_TRAY, null, null);

        if (minPref.equals(MessageDialogWithToggle.PROMPT) &&
                numWindows == 1) {  // no prompt if multiple windows
            userAction = promptForAction();
        }

        if (numWindows > 1 ||
                trayIcon.isMinimized() ||
                minPref.equals(MessageDialogWithToggle.NEVER) ||
                userAction == EXIT_BUTTON_ID) {  // user action: exit
            // allow to continue
            closeWindow = super.preWindowShellClose();
        }
        else if (minPref.equals(MessageDialogWithToggle.ALWAYS) ||
                userAction == MINIMIZE_BUTTON_ID) {
            // minimise the window and block application exit
            trayIcon.minimize();
            closeWindow = false;
        }
        else {  // user_action is CANCEL_BUTTON_ID or DIALOG_CLOSED
            // block application exit
            closeWindow = false;
        }

        return closeWindow;
    }
}
