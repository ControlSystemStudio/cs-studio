package org.csstudio.opibuilder.editor;

import java.util.logging.Logger;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

/**
 * Attach relevant listeners to workbench components in order that perspective
 * handling can be triggered when an OPIEditor is opened.
 *
 * This class could be converted into an abstract class with no dependency on
 * OPIBuilder, then extended by different classes wanting similar behaviour
 * that set up state in the constructor.
 */
public class PerspectiveChecker implements IStartup {

    private static Logger log = Logger.getLogger(PerspectiveChecker.class.getName());

    public final String perspectiveID;
    public final IPreferenceStore prefs;
    public final String preferenceKey;
    public final String dialogTitle;
    public final String dialogMessage;
    public final String savePreferenceMessage;
    public final String switchFailedMessage;

    public PerspectiveChecker() {
        perspectiveID = OPIEditorPerspective.ID;
        prefs = OPIBuilderPlugin.getDefault().getPreferenceStore();
        preferenceKey = PreferencesHelper.SWITCH_TO_OPI_EDITOR_PERSPECTIVE;
        dialogTitle = "Switch to OPI Editor perspective?";
        dialogMessage = "The OPI Editor perspective contains the tools needed for creating and editing OPIs."
                + "Would you like to switch to this perspective?";
        savePreferenceMessage = "Remember my decision";
        switchFailedMessage = "Failed to change to OPI Editor perspective: ";
    }

    @Override
    public void earlyStartup() {
        IWorkbench bench = PlatformUI.getWorkbench();
        for (IWorkbenchWindow window : bench.getWorkbenchWindows()) {
            for (IWorkbenchPage page : window.getPages()) {
                page.addPartListener(new EditorPartListener());
            }
            bench.addWindowListener(new EditorWindowListener());
        }
    }

    /**
     * Listener on workbench that takes action on new windows:
     * <ul>
     * <li>Adds an EditorPartListener to any pages
     * <li>Adds an EditorPageListener to the new window
     * </ul>
     */
    private class EditorWindowListener implements IWindowListener {
        @Override
        public void windowActivated(IWorkbenchWindow window) {}
        @Override
        public void windowClosed(IWorkbenchWindow window) {}
        @Override
        public void windowDeactivated(IWorkbenchWindow window) {}
        @Override
        public void windowOpened(IWorkbenchWindow window) {
            for (IWorkbenchPage page : window.getPages()) {
                page.addPartListener(new EditorPartListener());
            }
            window.addPageListener(new EditorPageListener());
        }
    }

    /**
     * Listener that adds EditorPartListener to any pages on the
     * workbench window to which it is attached.
     */
    private class EditorPageListener implements IPageListener {
        @Override
        public void pageActivated(IWorkbenchPage page) {
            page.addPartListener(new EditorPartListener());
        }
        @Override
        public void pageClosed(IWorkbenchPage page) {}
        @Override
        public void pageOpened(IWorkbenchPage page) {}
    }

    /**
     * Listener on workbench page that checks if a new part is an OPIEditor;
     * if so prompts or changes perspective depending on preference.
     */
    private class EditorPartListener implements IPartListener {

        /**
         * If the part being opened is an OPIEditor, check the preferences to see
         * what behaviour has been selected.  If relevant, prompt user and save
         * associated setting.  Switch perspective depending on preference or user
         * selection.
         * @param part part that is being opened
         */
        @Override
        public void partOpened(IWorkbenchPart part) {
            if (part instanceof OPIEditor) {
                IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                if (!activeWindow.getActivePage().getPerspective().getId().equals(perspectiveID)) {
                    boolean switchPerspective = false;
                    String preferenceSetting = prefs.getString(preferenceKey);
                    switch (preferenceSetting) {
                        case MessageDialogWithToggle.PROMPT:
                            switchPerspective = promptForPerspectiveSwitch(prefs, activeWindow);
                            break;
                        case MessageDialogWithToggle.ALWAYS:
                            switchPerspective = true;
                            break;
                        default:
                            switchPerspective = false;
                    }

                    if (switchPerspective) {
                        try {
                            PlatformUI.getWorkbench().showPerspective(perspectiveID, activeWindow);
                        } catch (WorkbenchException we) {
                            log.warning(switchFailedMessage + we);
                        }
                    }
                }
            }
        }

        /**
         * Opens dialog to ask user whether to change perspective.  If dialog is selected
         * the preference will be saved to the specified preference store.
         * @param prefs IPreferenceStore containing the setting
         * @param window IWorkbenchWindow on which to centre the dialog
         * @return whether to change perspective
         */
        private boolean promptForPerspectiveSwitch(IPreferenceStore prefs, IWorkbenchWindow window) {
            MessageDialogWithToggle md = MessageDialogWithToggle.openYesNoQuestion(
                    window.getShell(), dialogTitle, dialogMessage, savePreferenceMessage, false,
                    prefs, preferenceKey);
            return md.getReturnCode() == IDialogConstants.YES_ID;
        }

        @Override
        public void partDeactivated(IWorkbenchPart part) {}
        @Override
        public void partClosed(IWorkbenchPart part) {}
        @Override
        public void partBroughtToTop(IWorkbenchPart part) {}
        @Override
        public void partActivated(IWorkbenchPart part) {}
    }
}
