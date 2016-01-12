/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.validation;

import java.io.File;
import java.net.URL;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 *
 * <code>Activator</code> handles the lifecycle of this plugin.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class Activator extends AbstractUIPlugin {

    /** The ID of this plugin */
    public static final String ID = "org.csstudio.opibuilder.validation";

    /** Preference if backup of the file should be made before quick fix is applied */
    public static final String PREF_DO_BACKUP = "doBackup";
    /** Preference if user should be asked if she wants to make backup of the quick fixed file */
    public static final String PREF_SHOW_BACKUP_DIALOG = "showBackupDialog";
    /** Preference if the validation summary should be displayed after each validation */
    public static final String PREF_SHOW_SUMMARY = "showSummary";
    /** Preference the name of the file that provides the rules */
    public static final String PREF_RULES_FILE = "rulesFile";
    /** Preference if the markers in the problem view should be nested */
    public static final String PREF_NEST_MARKERS = "nestMarkers";
    /** Preference if all workspace makers should be cleared at the start of each validation */
    public static final String PREF_CLEAR_MARKERS = "clearMarkers";
    /** Preference if markers should be displayed in the default editor or in the text editor when double clicked */
    public static final String PREF_SHOW_MARKERS_IN_DEFAULT_EDITOR = "showMarkersInDefaultEditor";
    /** Preference if resource should be saved before validating it */
    public static final String PREF_SAVE_BEFORE_VALIDATION = "saveResourcesBeforeValidation";
    /** Preference if an info message should be issued when a jython script is used */
    public static final String PREF_WARN_ABOUT_JYTHON_SCRIPTS = "warnAboutJythonScripts";

    private static Activator instance;

    private Image quickFixImage;

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start(BundleContext context) throws Exception {
        instance = this;
        super.start(context);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        instance = null;
        if (quickFixImage != null) {
            quickFixImage.dispose();
        }
        super.stop(context);
    }

    /**
     * Constructs and returns the shared image used by the quick fix dialog.
     *
     * @return the shared quick fix image
     */
    public Image getQuickFixImage() {
        if (quickFixImage == null) {
            quickFixImage = CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
                    OPIBuilderPlugin.PLUGIN_ID, "icons/edit.gif").createImage();
        }
        return quickFixImage;
    }

    /**
     * @return the shared instance of this activator
     */
    public static Activator getInstance() {
        return instance;
    }

    /**
     * @return true if the backup of the files should be created or false otherwise
     */
    public boolean isDoBackup() {
        return getPreferenceStore().getBoolean(PREF_DO_BACKUP);
    }

    /**
     * @return true if the dialog to ask for backup should be shown or false otherwise
     */
    public boolean isShowBackupDialog() {
        return getPreferenceStore().getBoolean(PREF_SHOW_BACKUP_DIALOG);
    }

    /**
     * @return true if the summary dialog should be displayed at the end of validation
     */
    public boolean isShowSummaryDialog() {
        return getPreferenceStore().getBoolean(PREF_SHOW_SUMMARY);
    }

    /**
     * @return the path to the file that contains the rules settings
     */
    public IPath getRulesFile() {
        String s = getPreferenceStore().getString(PREF_RULES_FILE);
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        return getExistFileInRepoAndSearchPath(s);
    }

    /**
     * @return true if the markers in the problems view should be nested or false otherwise
     */
    public boolean isNestMarkers() {
        return getPreferenceStore().getBoolean(PREF_NEST_MARKERS);
    }

    /**
     * @return true if all markers are cleared before each validation run
     */
    public boolean isClearMarkers() {
        return getPreferenceStore().getBoolean(PREF_CLEAR_MARKERS);
    }

    /**
     * @return true if the markers are displayed in the default editor (e.g. OPI editor) or false if the markers
     *          are displayed in the text editor
     */
    public boolean isShowMarkersInDefaultEditor() {
        return getPreferenceStore().getBoolean(PREF_SHOW_MARKERS_IN_DEFAULT_EDITOR);
    }

    /**
     * @return true if dirty resources are automatically saved before being validated
     */
    public boolean isSaveBeforeValidation() {
        return getPreferenceStore().getBoolean(PREF_SAVE_BEFORE_VALIDATION);
    }

    /**
     * @return true if a warning is raised whenever jython scripts are used or false otherwise
     */
    public boolean isWarnAboutJythonScripts() {
        return getPreferenceStore().getBoolean(PREF_WARN_ABOUT_JYTHON_SCRIPTS);
    }

    private static IPath getExistFileInRepoAndSearchPath(String pathString){
        IPath originPath = ResourceUtil.getPathFromString(pathString);
        if(originPath == null) {
            return null;
        } else if (originPath.toFile().exists()) {
            return originPath;
        } else {
            Location loc = Platform.getInstanceLocation();
            URL url = loc.getURL();
            File workspaceLocation = new File(url.getFile());
            File file = new File(workspaceLocation, pathString);
            if (file.exists()) {
                return ResourceUtil.getPathFromString(file.getAbsolutePath());
            }
        }
        return null;

    }
}
