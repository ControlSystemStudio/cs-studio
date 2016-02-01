/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.util;

import java.net.URI;
import java.util.Objects;
import java.util.logging.Level;

import org.csstudio.email.EMailSender;
import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.actions.PrintDisplayAction;
import org.csstudio.opibuilder.actions.SendEMailAction;
import org.csstudio.opibuilder.actions.SendToElogAction;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.runmode.IOPIRuntime;
import org.csstudio.opibuilder.runmode.OPIShell;
import org.csstudio.opibuilder.runmode.OPIView;
import org.csstudio.opibuilder.widgetActions.OpenFileAction;
import org.csstudio.ui.util.dialogs.ExceptionDetailsErrorDialog;
import org.csstudio.ui.util.dialogs.ResourceSelectionDialog;
import org.csstudio.utility.file.IFileUtil;
import org.csstudio.utility.singlesource.SingleSourcePlugin;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.progress.UIJob;

public class SingleSourceHelperImpl extends SingleSourceHelper{

    @Override
    protected GC iGetImageGC(Image image) {
        return new GC(image);
    }

    /** Open the file in its associated editor,
     *  supporting workspace files, local file system files or URLs
     *  @param openFileAction Action for opening a file
     */
    @Override
    @SuppressWarnings("nls")
    protected void iOpenFileActionRun(final OpenFileAction openFileAction)
    {
        final UIJob job = new UIJob(openFileAction.getDescription()){
            @Override
            public IStatus runInUIThread(final IProgressMonitor monitor) {
                // Open editor on new file.
                final IWorkbenchWindow dw = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                if (dw == null)
                    return Status.OK_STATUS; // Not really OK..
                try
                {
                    final IWorkbenchPage page = Objects.requireNonNull(dw.getActivePage());

                    IPath absolutePath = openFileAction.getPath();
                    if (!absolutePath.isAbsolute())
                        absolutePath = ResourceUtil.buildAbsolutePath(
                                    openFileAction.getWidgetModel(), absolutePath);

                    // Workspace file?
                    IFile file = ResourceUtilSSHelperImpl.getIFileFromIPath(absolutePath);
                    if (file != null)
                        IDE.openEditor(page, file, true);
                    else if (ResourceUtil.isExistingLocalFile(absolutePath))
                    {   // Local file system
                        try
                        {
                            IFileStore localFile =    EFS.getLocalFileSystem().getStore(absolutePath);
                            IDE.openEditorOnFileStore(page, localFile);
                        }
                        catch (Exception e)
                        {
                            throw new Exception("Cannot open local file system location " + openFileAction.getPath(), e);
                        }
                    }
                    else
                    {   // Attempt to download URL into local file (since IDE.openEditor needs local file)
                        final URI uri = new URI(openFileAction.getPath().toString());
                        file = IFileUtil.getInstance().createURLFileResource(uri);
                        IDE.openEditor(page, file, true);
                    }
                }
                catch (Exception e)
                {
                    final String message = "Failed to open file " + openFileAction.getPath();
                    ExceptionDetailsErrorDialog.openError(dw.getShell(), "Failed to open file", message, e);
                    OPIBuilderPlugin.getLogger().log(Level.WARNING, message, e);
                    ConsoleService.getInstance().writeError(message);
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    @Override
    protected void iAddPaintListener(Control control,
            PaintListener paintListener) {
        control.addPaintListener(paintListener);

    }

    @Override
    protected void iRemovePaintListener(Control control,
            PaintListener paintListener) {
        control.removePaintListener(paintListener);

    }

    @Override
    protected void iRegisterRCPRuntimeActions(ActionRegistry actionRegistry,
            IOPIRuntime opiRuntime) {
        actionRegistry.registerAction(new PrintDisplayAction(opiRuntime));
        if (SendToElogAction.isElogAvailable())
            actionRegistry
                    .registerAction(new SendToElogAction(opiRuntime));
        if (EMailSender.isEmailSupported())
            actionRegistry.registerAction(new SendEMailAction(opiRuntime));

    }

    @Override
    protected void iappendRCPRuntimeActionsToMenu(
            ActionRegistry actionRegistry, IMenuManager menu) {
        IAction action = actionRegistry.getAction(SendToElogAction.ID);
        if (action != null)
            menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
        action = actionRegistry.getAction(SendEMailAction.ID);
        if (action != null)
            menu.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
        menu.appendToGroup(GEFActionConstants.GROUP_EDIT,
                actionRegistry.getAction(ActionFactory.PRINT.getId()));

    }

    @Override
    protected IPath iRcpGetPathFromWorkspaceFileDialog(IPath startPath,
            String[] extensions) {
        ResourceSelectionDialog rsDialog = new ResourceSelectionDialog(
                Display.getCurrent().getActiveShell(), "Choose File", extensions);
        if(startPath != null)
            rsDialog.setSelectedResource(startPath);

        if(rsDialog.open() == Window.OK){
            return rsDialog.getSelectedResource();
        }
        return null;
    }


    //////////////////////////// RAP Related Stuff ///////////////////////////////


    @Override
    protected void iRapActivatebaseEditPart(AbstractBaseEditPart editPart) {

    }

    @Override
    protected void iRapDeactivatebaseEditPart(AbstractBaseEditPart editPart) {

    }

    @Override
    protected void iRapOpenOPIInNewWindow(IPath path) {

    }

    @Override
    protected void iRapAddDisplayDisposeListener(Display display,
            Runnable runnable) {

    }

    @Override
    protected void iRapPlayWavFile(IPath absolutePath) {

    }

    @Override
    protected void iRapOPIViewCreatePartControl(OPIView opiView,
            Composite parent) {

    }

    @Override
    protected void iRapPluginStartUp() {

    }



    @Override
    protected void iRapOpenWebPage(String hyperLink) {

    }

    @Override
    protected boolean iRapAuthenticate(Display display) {
        return false;
    }

    @Override
    protected boolean iRapIsLoggedIn(Display display) {
        return false;
    }

    @Override
    protected void iOpenEditor(IWorkbenchPage page, IPath path)
            throws Exception {
        SingleSourcePlugin.getUIHelper().openEditor(page, path);
    }


    @Override
    protected void iOpenOPIShell(IPath path, MacrosInput input) {
        OPIShell.openOPIShell(path, input);
    }

    @Override
    protected IOPIRuntime iGetOPIShellForShell(Shell shell) {
        return OPIShell.getOPIShellForShell(shell);
    }
}
