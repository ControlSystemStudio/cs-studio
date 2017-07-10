package org.csstudio.opibuilder.editor;

import java.io.IOException;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * Attach relevant listeners to workbench components in order that perspective
 * handling can be triggered when an OPIEditor is opened.
 */
public class PerspectiveChecker implements IStartup {

    public final String perspectiveID;
    public final ScopedPreferenceStore prefs;
    public final String preferenceKey;

    private static final String DIALOG_TITLE = "Switch to OPI Editor perspective?";
    private static final String DIALOG_MESSAGE = "The OPI Editor perspective contains the tools needed for creating and editing OPIs. Would you like to switch to this perspective?";
    private static final String SAVE_PREFERENCE_MESSAGE = "Remember my decision";
    private static final String SAVE_FAILED_MESSAGE = "Failed to save preferences: ";
    private static final String MISSING_PERSPECTIVE_MESSAGE = "OPI Editor perspective not present and could not be loaded.";

    public PerspectiveChecker() {
        perspectiveID = OPIEditorPerspective.ID;
        prefs = new ScopedPreferenceStore(InstanceScope.INSTANCE, OPIBuilderPlugin.PLUGIN_ID);
        preferenceKey = PreferencesHelper.SWITCH_TO_OPI_EDITOR_PERSPECTIVE;
    }

    /**
     * Add an EditorWindowListener to the workbench, and an EditorPageListener
     * to any open workbench windows.
     */
    @Override
    public void earlyStartup() {
        IWorkbench bench = PlatformUI.getWorkbench();
        bench.addWindowListener(new EditorWindowListener());
        for (IWorkbenchWindow window : bench.getWorkbenchWindows()) {
            for (IWorkbenchPage page : window.getPages()) {
                page.addPartListener(new EditorPartListener());
            }
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
            window.addPageListener(new EditorPageListener());
            for (IWorkbenchPage page : window.getPages()) {
                page.addPartListener(new EditorPartListener());
            }
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
                IWorkbenchWindow partWindow = part.getSite().getWorkbenchWindow();
                IPerspectiveDescriptor perspective = getPerspective(PlatformUI.getWorkbench(), perspectiveID);
                if (perspective == null) {
                    OPIBuilderPlugin.getLogger().warning(MISSING_PERSPECTIVE_MESSAGE);
                } else {
                    if (partWindow != null) {
                        if (!partWindow.getActivePage().getPerspective().getId().equals(perspectiveID)) {
                            if (switchRequired(partWindow)) {
                                partWindow.getActivePage().setPerspective(perspective);
                            }
                        }
                    }
                }
            }
        }

        IPerspectiveDescriptor getPerspective(IWorkbench workbench, String id) {
            IPerspectiveDescriptor[] perspectives = workbench.getPerspectiveRegistry().getPerspectives();
            for (IPerspectiveDescriptor perspective : perspectives) {
                if (perspective.getId().equals(id)) {
                    return perspective;
                }
            }
            return null;
        }

        /**
         * Opens dialog to ask user whether to change perspective.  If dialog is selected
         * the preference will be saved to the specified preference store.
         * @param prefs IPreferenceStore containing the setting
         * @param window IWorkbenchWindow on which to centre the dialog
         * @return whether to change perspective
         */
        private boolean promptForPerspectiveSwitch(ScopedPreferenceStore prefs, IWorkbenchWindow window) {
            MessageDialogWithToggle md = MessageDialogWithToggle.openYesNoQuestion(
                    window.getShell(), DIALOG_TITLE, DIALOG_MESSAGE, SAVE_PREFERENCE_MESSAGE, false,
                    prefs, preferenceKey);
            if (md.getToggleState()) {
                try {
                    prefs.save();
                } catch (IOException e) {
                    OPIBuilderPlugin.getLogger().warning(SAVE_FAILED_MESSAGE + e.getMessage());
                }
            }
            return md.getReturnCode() == IDialogConstants.YES_ID;
        }

        /**
         * Determine if a perspective switch is necessary depending on existing preferences
         * and a user prompt if appropriate.
         * @param window IWorkbenchWindow on which to centre the prompt
         * @return
         */
        private boolean switchRequired(IWorkbenchWindow window) {
            boolean switchPerspective = false;
            String preferenceSetting = prefs.getString(preferenceKey);
            switch (preferenceSetting) {
                case MessageDialogWithToggle.PROMPT:
                    switchPerspective = promptForPerspectiveSwitch(prefs, window);
                    break;
                case MessageDialogWithToggle.ALWAYS:
                    switchPerspective = true;
                    break;
                default:
                    switchPerspective = false;
            }
            return switchPerspective;
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
