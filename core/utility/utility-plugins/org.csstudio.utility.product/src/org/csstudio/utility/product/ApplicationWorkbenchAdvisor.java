/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.product;

import java.net.URL;

import org.csstudio.startup.application.OpenDocumentEventProcessor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.IDEInternalWorkbenchImages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.osgi.framework.Bundle;

/** Tell the workbench how to behave.
 *  @author Kay Kasemir
 *  @author Xihui Chen - IDE-specific workbench images
 */
@SuppressWarnings("restriction")
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor
{
    private OpenDocumentEventProcessor openDocProcessor;

    public ApplicationWorkbenchAdvisor(
            OpenDocumentEventProcessor openDocProcessor) {
        this.openDocProcessor = openDocProcessor;
    }

    @Override
    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
                    final IWorkbenchWindowConfigurer configurer)
    {
        return new ApplicationWorkbenchWindowAdvisor(configurer);
    }

    @SuppressWarnings("nls")
    @Override
    public void initialize(final IWorkbenchConfigurer configurer)
    {
        // Per default, state is not preserved (RCP book 5.1.1)
        configurer.setSaveAndRestore(true);

        // Register adapters needed by Navigator view to display workspace files
        IDE.registerAdapters();

        // Declares all IDE-specific workbench images. This includes both "shared"
        // images (named in {@link IDE.SharedImages}) and internal images.
        configurer.declareImage(IDE.SharedImages.IMG_OBJ_PROJECT,
                Activator.getImageDescriptor("icons/project_open.png"), true);
        configurer.declareImage(IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED,
                Activator.getImageDescriptor("icons/project_close.png"), true);

        declareWorkbenchImages();

        IEclipseContext context = PlatformUI.getWorkbench().getService(IEclipseContext.class);
        PerspectiveSaver ps = ContextInjectionFactory.make(PerspectiveSaver.class, context);
        ps.init();
    }

    /** @return ID of initial perspective */
    @Override
    public String getInitialWindowPerspectiveId()
    {
        return CSStudioPerspective.ID;
    }

    @Override
    public void eventLoopIdle(final Display display)
    {
        if(openDocProcessor != null)
            openDocProcessor.catchUp(display);
        super.eventLoopIdle(display);
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
     * Workaround for RCP bug: https://bugs.eclipse.org/bugs/show_bug.cgi?id=234252
     *
     * Declares all IDE-specific workbench images. This includes both "shared"
     * images (named in {@link IDE.SharedImages}) and internal images (named in
     * {@link org.eclipse.ui.internal.ide.IDEInternalWorkbenchImages}).
     *
     * @see org.eclipse.ui.internal.ide.IDEWorkbenchAdvisor#declareImage
     */
    private void declareWorkbenchImages() {
        final String ICONS_PATH = "$nl$/icons/full/";//$NON-NLS-1$
        final String PATH_ELOCALTOOL = ICONS_PATH + "elcl16/"; // Enabled //$NON-NLS-1$
        final String PATH_DLOCALTOOL = ICONS_PATH + "dlcl16/"; // Disabled //$NON-NLS-1$
        final String PATH_ETOOL = ICONS_PATH + "etool16/"; // Enabled toolbar //$NON-NLS-1$
        final String PATH_DTOOL = ICONS_PATH + "dtool16/"; // Disabled toolbar //$NON-NLS-1$
        final String PATH_OBJECT = ICONS_PATH + "obj16/"; // Model object //$NON-NLS-1$
        final String PATH_WIZBAN = ICONS_PATH + "wizban/"; // Wizard //$NON-NLS-1$
        final String PATH_EVIEW = ICONS_PATH + "eview16/"; //$NON-NLS-1$
        Bundle ideBundle = Platform.getBundle(IDEWorkbenchPlugin.IDE_WORKBENCH);

        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_ETOOL_BUILD_EXEC,
                PATH_ETOOL + "build_exec.gif", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_ETOOL_BUILD_EXEC_HOVER,
                PATH_ETOOL + "build_exec.gif", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_ETOOL_BUILD_EXEC_DISABLED,
                PATH_DTOOL + "build_exec.gif", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle,IDEInternalWorkbenchImages.IMG_ETOOL_SEARCH_SRC,
                PATH_ETOOL + "search_src.gif", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_ETOOL_SEARCH_SRC_HOVER,
                PATH_ETOOL + "search_src.gif", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_ETOOL_SEARCH_SRC_DISABLED,
                PATH_DTOOL + "search_src.gif", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_ETOOL_NEXT_NAV,
                PATH_ETOOL + "next_nav.gif", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_ETOOL_PREVIOUS_NAV,
                PATH_ETOOL + "prev_nav.gif", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_WIZBAN_NEWPRJ_WIZ,
                PATH_WIZBAN + "newprj_wiz.png", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_WIZBAN_NEWFOLDER_WIZ,
                PATH_WIZBAN + "newfolder_wiz.png", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_WIZBAN_NEWFILE_WIZ,
                PATH_WIZBAN + "newfile_wiz.png", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_WIZBAN_IMPORTDIR_WIZ,
                PATH_WIZBAN + "importdir_wiz.png", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_WIZBAN_IMPORTZIP_WIZ,
                PATH_WIZBAN + "importzip_wiz.png", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_WIZBAN_EXPORTDIR_WIZ,
                PATH_WIZBAN + "exportdir_wiz.png", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_WIZBAN_EXPORTZIP_WIZ,
                PATH_WIZBAN + "exportzip_wiz.png", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_WIZBAN_RESOURCEWORKINGSET_WIZ,
                PATH_WIZBAN + "workset_wiz.png", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_DLGBAN_SAVEAS_DLG,
                PATH_WIZBAN + "saveas_wiz.png", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_DLGBAN_QUICKFIX_DLG,
                PATH_WIZBAN + "quick_fix.png", false); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDE.SharedImages.IMG_OBJ_PROJECT,
                PATH_OBJECT + "prj_obj.gif", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED,
                PATH_OBJECT + "cprj_obj.gif", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDE.SharedImages.IMG_OPEN_MARKER,
                PATH_ELOCALTOOL + "gotoobj_tsk.gif", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_ELCL_QUICK_FIX_ENABLED,
                PATH_ELOCALTOOL + "smartmode_co.gif", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_DLCL_QUICK_FIX_DISABLED,
                PATH_DLOCALTOOL + "smartmode_co.gif", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_OBJS_FIXABLE_WARNING,
                PATH_OBJECT + "quickfix_warning_obj.gif", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_OBJS_FIXABLE_ERROR,
                PATH_OBJECT + "quickfix_error_obj.gif", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDE.SharedImages.IMG_OBJS_TASK_TSK,
                PATH_OBJECT + "taskmrk_tsk.gif", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDE.SharedImages.IMG_OBJS_BKMRK_TSK,
                PATH_OBJECT + "bkmrk_tsk.gif", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_OBJS_COMPLETE_TSK,
                PATH_OBJECT + "complete_tsk.gif", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_OBJS_INCOMPLETE_TSK,
                PATH_OBJECT + "incomplete_tsk.gif", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_OBJS_WELCOME_ITEM,
                PATH_OBJECT + "welcome_item.gif", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_OBJS_WELCOME_BANNER,
                PATH_OBJECT + "welcome_banner.gif", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_OBJS_ERROR_PATH,
                PATH_OBJECT + "error_tsk.gif", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_OBJS_WARNING_PATH,
                PATH_OBJECT + "warn_tsk.gif", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_OBJS_INFO_PATH,
                PATH_OBJECT + "info_tsk.gif", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_LCL_FLAT_LAYOUT,
                PATH_ELOCALTOOL + "flatLayout.gif", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_LCL_HIERARCHICAL_LAYOUT,
                PATH_ELOCALTOOL + "hierarchicalLayout.gif", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_ETOOL_PROBLEM_CATEGORY,
                PATH_ETOOL + "problem_category.gif", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_ETOOL_PROBLEMS_VIEW,
                PATH_EVIEW + "problems_view.gif", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_ETOOL_PROBLEMS_VIEW_ERROR,
                PATH_EVIEW + "problems_view_error.gif", true); //$NON-NLS-1$
        declareWorkbenchImage(ideBundle, IDEInternalWorkbenchImages.IMG_ETOOL_PROBLEMS_VIEW_WARNING,
                PATH_EVIEW + "problems_view_warning.gif", true); //$NON-NLS-1$
    }

    private void declareWorkbenchImage(Bundle ideBundle, String symbolicName, String path, boolean shared) {
        URL url = FileLocator.find(ideBundle, new Path(path), null);
        ImageDescriptor desc = ImageDescriptor.createFromURL(url);
        getWorkbenchConfigurer().declareImage(symbolicName, desc, shared);
    }

}
