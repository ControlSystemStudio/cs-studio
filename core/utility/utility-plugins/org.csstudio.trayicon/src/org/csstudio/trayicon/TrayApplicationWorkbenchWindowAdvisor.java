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
    public static final String[] CLOSE_BUTTON_LABELS = {
        Messages.TrayDialog_exit,
        Messages.TrayDialog_cancel};
    private static final int MINIMIZE_BUTTON_ID = 256;
    private static final int EXIT_BUTTON_ID = 257;
    private static final int CLOSE_EXIT_BUTTON_ID = 256;
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
     * Display warning that this is the last window.
     *
     * @return xx_BUTTON_ID of clicked button or DIALOG_CLOSED
     */
    private int warnOfLastWindow() {
        Shell parent = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        ScopedPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE, Plugin.ID);
        MessageDialogWithToggle dialog = new MessageDialogWithToggle(parent, Messages.TrayDialog_closeTitle, null,
                Messages.TrayDialog_warning, MessageDialog.QUESTION, CLOSE_BUTTON_LABELS, 2,
                Messages.TrayDialog_doNotWarnAgain, false);
        dialog.open();

        int response = dialog.getReturnCode();

        // Store the decision if checkbox selected on the form
        if (dialog.getToggleState()) {
            if (response == CLOSE_EXIT_BUTTON_ID) {
                store.setValue(TrayIconPreferencePage.CLOSE_OPTION, Messages.TrayPreferences_close);
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
     *      * minimize preference:NEVER
     *      * close option preference:Just Close
     *      * user:EXIT
     *      * multiple open windows
     *      * application already minimised
     *  iii) create trayIcon, minimise window but do not exit (return False)
     *      * minimize preference:ALWAYS
     *      * close option preference:Ask to minimize
     *      * user:MINIMIZE
     *
     * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#preWindowShellClose
     */
    @Override
    public boolean preWindowShellClose() {

        boolean closeWindow;
        int userAction = DIALOG_CLOSED; // For the minimize dialog
        int userAction2 = DIALOG_CLOSED; // For the warning dialog
        int numWindows = PlatformUI.getWorkbench().getWorkbenchWindowCount();

        IPreferencesService prefs = Platform.getPreferencesService();
        String closePref = prefs.getString(
            Plugin.ID, TrayIconPreferencePage.CLOSE_OPTION, null, null);
        String minPref = prefs.getString(
                Plugin.ID, TrayIconPreferencePage.MINIMIZE_TO_TRAY, null, null);

        // Display warning dialog and get users action to cancel or exit
        if (closePref.equals(Messages.TrayPreferences_warn) && numWindows == 1) {
          userAction2 = warnOfLastWindow();
        }

        // Dialog to minimize will only display if preference for close option is to ask to minimize
        if (closePref.equals(Messages.TrayPreferences_askToMinimize) && minPref.equals(MessageDialogWithToggle.PROMPT) &&
            numWindows == 1) {
          userAction = promptForAction();
        }

        // Case where the application will close. minPref is only application if closePref is ask to minimize
        if (numWindows > 1 || trayIcon.isMinimized() ||
            (closePref.equals(Messages.TrayPreferences_askToMinimize) && minPref.equals(MessageDialogWithToggle.NEVER))
            || closePref.equals(Messages.TrayPreferences_close)
            || userAction == EXIT_BUTTON_ID
            || userAction2 == CLOSE_EXIT_BUTTON_ID) { // user action: exit
          // allow to continue
          closeWindow = super.preWindowShellClose();
        }
        // Case where the application will minimize. minPref is only application if closePref is ask to minimize
        else if ( (closePref.equals(Messages.TrayPreferences_askToMinimize) && minPref.equals(MessageDialogWithToggle.ALWAYS))
            || userAction == MINIMIZE_BUTTON_ID) {
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
