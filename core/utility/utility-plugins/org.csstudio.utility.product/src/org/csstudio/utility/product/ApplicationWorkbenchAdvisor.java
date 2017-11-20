/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.product;

import java.net.URL;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.IDEInternalWorkbenchImages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.osgi.framework.Bundle;

/**
 * Tell the workbench how to behave.
 *
 * @author Kay Kasemir
 * @author Xihui Chen - IDE-specific workbench images
 *
 * Updated {@link ApplicationWorkbenchAdvisor#declareWorkbenchImages()} with new IDE specific images to
 * be in line with Eclipse 4.6 Neon. CSS uses same IDE specific images as Eclipse. Ref: bug: https://bugs.eclipse.org/bugs/show_bug.cgi?id=234252
 *
 * @author <a href="mailto:borut.terpinc@cosylab.com">Borut Terpinc</a>
 */


@SuppressWarnings("restriction")
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

    public ApplicationWorkbenchAdvisor() {
    }

    @Override
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(final IWorkbenchWindowConfigurer configurer) {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

    @SuppressWarnings("nls")
    @Override
    public void initialize(final IWorkbenchConfigurer configurer) {
        // Per default, state is not preserved (RCP book 5.1.1)
        configurer.setSaveAndRestore(true);

        // Register adapters needed by Navigator view to display workspace files
        IDE.registerAdapters();

        // declare workbench specific images
        // According to bug -- bug: https://bugs.eclipse.org/bugs/show_bug.cgi?id=234252
        declareWorkbenchImages();
    }

    /** @return ID of initial perspective */
    @Override
    public String getInitialWindowPerspectiveId() {
        return CSStudioPerspective.ID;
    }

    @Override
    public boolean preShutdown() {
        try {
            ResourcesPlugin.getWorkspace().save(true, new NullProgressMonitor());
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Declares all IDE-specific workbench images. CSS uses the same IDE-specific images as Eclipse product.
     * This includes both "shared"
     * images (named in {@link org.eclipse.ui.ide.IDE.SharedImages}) and
     * internal images (named in
     * {@link org.eclipse.ui.internal.ide.IDEInternalWorkbenchImages}).
     *
     * @see IWorkbenchConfigurer#declareImage
     */
    private void declareWorkbenchImages() {

        /**
         * prefix:
         * e - enabled icons
         * d - disabled icons
         *
         * lcl - toolbar icons
         * tool - toolbar icons
         * obj - model object icons
         * wizban - wizdard icons
         * eviewview - icons
         */

        final String ICONS_PATH = "$nl$/icons/full/";//$NON-NLS-1$
        final String PATH_ELOCALTOOL = ICONS_PATH + "elcl16/"; //$NON-NLS-1$
        final String PATH_DLOCALTOOL = ICONS_PATH + "dlcl16/"; //$NON-NLS-1$
        final String PATH_ETOOL = ICONS_PATH + "etool16/"; //$NON-NLS-1$
        final String PATH_DTOOL = ICONS_PATH + "dtool16/"; //$NON-NLS-1$
        final String PATH_OBJECT = ICONS_PATH + "obj16/"; //$NON-NLS-1$
        final String PATH_WIZBAN = ICONS_PATH + "wizban/"; //$NON-NLS-1$
        final String PATH_EVIEW = ICONS_PATH + "eview16/"; //$NON-NLS-1$
        Bundle ideBundle = Platform.getBundle(IDEWorkbenchPlugin.IDE_WORKBENCH);

        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_ETOOL_BUILD_EXEC, PATH_ETOOL + "build_exec.png", false);
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_ETOOL_BUILD_EXEC_HOVER, PATH_ETOOL + "build_exec.png", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_ETOOL_BUILD_EXEC_DISABLED, PATH_DTOOL + "build_exec.png", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_ETOOL_SEARCH_SRC, PATH_ETOOL + "search_src.png", //$NON-NLS-1$
                false);
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_ETOOL_SEARCH_SRC_HOVER, PATH_ETOOL + "search_src.png", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_ETOOL_SEARCH_SRC_DISABLED, PATH_DTOOL + "search_src.png", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_ETOOL_NEXT_NAV, PATH_ETOOL + "next_nav.png", //$NON-NLS-1$
                false);
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_ETOOL_PREVIOUS_NAV, PATH_ETOOL + "prev_nav.png", //$NON-NLS-1$
                false);
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_WIZBAN_NEWPRJ_WIZ, PATH_WIZBAN + "newprj_wiz.png", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_WIZBAN_NEWFOLDER_WIZ, PATH_WIZBAN + "newfolder_wiz.png", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_WIZBAN_NEWFILE_WIZ, PATH_WIZBAN + "newfile_wiz.png", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_WIZBAN_IMPORTDIR_WIZ, PATH_WIZBAN + "importdir_wiz.png", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_WIZBAN_IMPORTZIP_WIZ, PATH_WIZBAN + "importzip_wiz.png", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_WIZBAN_EXPORTDIR_WIZ, PATH_WIZBAN + "exportdir_wiz.png", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_WIZBAN_EXPORTZIP_WIZ, PATH_WIZBAN + "exportzip_wiz.png", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_WIZBAN_RESOURCEWORKINGSET_WIZ, PATH_WIZBAN + "workset_wiz.png", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_DLGBAN_SAVEAS_DLG, PATH_WIZBAN + "saveas_wiz.png", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_DLGBAN_QUICKFIX_DLG, PATH_WIZBAN + "quick_fix.png", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDE.SharedImages.IMG_OBJ_PROJECT, PATH_OBJECT + "prj_obj.png", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED, PATH_OBJECT + "cprj_obj.png", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDE.SharedImages.IMG_OPEN_MARKER, PATH_ELOCALTOOL + "gotoobj_tsk.png", true); //$NON-NLS-1$

        // Quick fix icons
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_ELCL_QUICK_FIX_ENABLED, PATH_ELOCALTOOL + "smartmode_co.png", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_DLCL_QUICK_FIX_DISABLED, PATH_DLOCALTOOL + "smartmode_co.png", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_OBJS_FIXABLE_WARNING, PATH_OBJECT + "quickfix_warning_obj.png", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_OBJS_FIXABLE_ERROR, PATH_OBJECT + "quickfix_error_obj.png", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_OBJS_FIXABLE_INFO, PATH_OBJECT + "quickfix_info_obj.png", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDE.SharedImages.IMG_OBJS_TASK_TSK, PATH_OBJECT + "taskmrk_tsk.png", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDE.SharedImages.IMG_OBJS_BKMRK_TSK, PATH_OBJECT + "bkmrk_tsk.png", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_OBJS_COMPLETE_TSK, PATH_OBJECT + "complete_tsk.png", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_OBJS_INCOMPLETE_TSK, PATH_OBJECT + "incomplete_tsk.png", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_OBJS_WELCOME_ITEM, PATH_OBJECT + "welcome_item.png", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_OBJS_WELCOME_BANNER, PATH_OBJECT + "welcome_banner.png", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_OBJS_ERROR_PATH, PATH_OBJECT + "error_tsk.png", //$NON-NLS-1$
                true);
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_OBJS_WARNING_PATH, PATH_OBJECT + "warn_tsk.png", //$NON-NLS-1$
                true);
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_OBJS_INFO_PATH, PATH_OBJECT + "info_tsk.png", //$NON-NLS-1$
                true);

        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_LCL_FLAT_LAYOUT, PATH_ELOCALTOOL + "flatLayout.png", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_LCL_HIERARCHICAL_LAYOUT, PATH_ELOCALTOOL + "hierarchicalLayout.png", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_ETOOL_PROBLEM_CATEGORY, PATH_ETOOL + "problem_category.png", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_ETOOL_PROBLEMS_VIEW, PATH_EVIEW + "problems_view.png", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_ETOOL_PROBLEMS_VIEW_ERROR, PATH_EVIEW + "problems_view_error.png", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_ETOOL_PROBLEMS_VIEW_WARNING, PATH_EVIEW + "problems_view_warning.png", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_ETOOL_PROBLEMS_VIEW_INFO, PATH_EVIEW + "problems_view_info.png", true); //$NON-NLS-1$
    }

    /**
     * Declares an IDE-specific workbench image.
     *
     * @param symbolicName
     *            the symbolic name of the image
     * @param path
     *            the path of the image file; this path is relative to the base
     *            of the IDE plug-in
     * @param shared
     *            <code>true</code> if this is a shared image, and
     *            <code>false</code> if this is not a shared image
     * @see IWorkbenchConfigurer#declareImage
     */
    private void declareWorkbenchImage(Bundle ideBundle, String symbolicName, String path, boolean shared) {
        URL url = FileLocator.find(ideBundle, new Path(path), null);
        ImageDescriptor desc = ImageDescriptor.createFromURL(url);
        getWorkbenchConfigurer().declareImage(symbolicName, desc, shared);
    }

}
