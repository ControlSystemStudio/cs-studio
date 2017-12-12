/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.ui.util.NoResourceEditorInput;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.Cursors;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.osgi.framework.Bundle;

/**
 * @author Xihui Chen
 *
 */
public class ResourceUtilSSHelperImpl extends ResourceUtilSSHelper {

    private static final Logger LOGGER = Logger.getLogger(ResourceUtilSSHelperImpl.class.getName());
    private static final String CURSOR_PATH = "icons/copy.gif";
    private Cursor copyPvCursor;

    @Override
    public Cursor getCopyPvCursor() {
        if (copyPvCursor == null) {
            Bundle bundle = Platform.getBundle(OPIBuilderPlugin.PLUGIN_ID);
            IPath path = new Path(CURSOR_PATH);
            URL url = FileLocator.find(bundle, path, null);
            try {
                InputStream inputStream = url.openConnection().getInputStream();
                copyPvCursor = new Cursor(Display.getCurrent(), new ImageData(inputStream), 0, 0);
            } catch (IOException e) {
                copyPvCursor = Cursors.HELP;
            }
        }
        return copyPvCursor;
    }

    /*
     * (non-Javadoc)
     * @see org.csstudio.opibuilder.util.ResourceUtilSSHelper#getFile(org.eclipse.core.runtime.IPath)
     */
    @Override
    public File getFile(IPath path) throws Exception {
        final IFile workspace_file = getIFileFromIPath(path);
        // Valid file should either open, or give meaningful exception
        if (workspace_file != null  &&  workspace_file.exists())
            return workspace_file.getLocation().toFile().getAbsoluteFile();

        // Not a workspace file. Try local file system
        File local_file = path.toFile();
        // Path URL for "file:..." so that it opens as FileInputStream
        if (local_file.getPath().startsWith("file:"))
            local_file = new File(local_file.getPath().substring(5));

        return local_file.exists() ? local_file.getAbsoluteFile() : null;

    }


    /* (non-Javadoc)
     * @see org.csstudio.opibuilder.util.ResourceUtilSSHelper#pathToInputStream(org.eclipse.core.runtime.IPath, boolean)
     */
    @Override
    public InputStream pathToInputStream(IPath path, boolean runInUIJob)
            throws Exception {

        // Try workspace location
        final IFile workspace_file = getIFileFromIPath(path);
        // Valid file should either open, or give meaningful exception
        if (workspace_file != null  &&  workspace_file.exists())
            return workspace_file.getContents();

        // Not a workspace file. Try local file system
        File local_file = path.toFile();
        // Path URL for "file:..." so that it opens as FileInputStream
        if (local_file.getPath().startsWith("file:"))
            local_file = new File(local_file.getPath().substring(5));
        String urlString;
        try
        {
            return new FileInputStream(local_file);
        }
        catch (Exception ex)
        {
            // Could not open as local file.
            // Does it look like a URL?
            //TODO:
             // Eclipse Path collapses "//" into "/", revert that: Is this true? Need test on Mac.
             urlString = path.toString();
//             if(!urlString.startsWith("platform") && !urlString.contains("://")) //$NON-NLS-1$ //$NON-NLS-2$
//                 urlString = urlString.replaceFirst(":/", "://"); //$NON-NLS-1$ //$NON-NLS-2$
        // Does it now look like a URL? If not, report the original local file problem
            if (! ResourceUtil.isURL(urlString))
                throw new Exception("Cannot open " + ex.getMessage(), ex);
        }

        // Must be an URL
        final URL url = new URL(urlString);

        return ResourceUtil.openURLStream(url, runInUIJob);

    }

    /* (non-Javadoc)
     * @see org.csstudio.opibuilder.util.ResourceUtilSSHelper#getInputStreamFromEditorInput(org.eclipse.ui.IEditorInput)
     */
    @Override
    public InputStream getInputStreamFromEditorInput(IEditorInput editorInput) {
        InputStream result = null;
        if (editorInput instanceof FileEditorInput) {
            try {
                result = ((FileEditorInput) editorInput).getFile()
                        .getContents();
            } catch (CoreException e) {
                LOGGER.log(Level.SEVERE, "Error while trying to access input stream of an editor.", e);
                e.printStackTrace();
            }
        } else if (editorInput instanceof FileStoreEditorInput) {
            IPath path = URIUtil.toPath(((FileStoreEditorInput) editorInput)
                    .getURI());
            try {
                result = new FileInputStream(path.toFile());
            } catch (FileNotFoundException e) {
                //ignore
            }
        }

        return result;
    }

    /* (non-Javadoc)
     * @see org.csstudio.opibuilder.util.ResourceUtilSSHelper#isExistingWorkspaceFile(org.eclipse.core.runtime.IPath)
     */
    @Override
    public boolean isExistingWorkspaceFile(IPath path) {
        return getIFileFromIPath(path) != null;
    }

    /* (non-Javadoc)
     * @see org.csstudio.opibuilder.util.ResourceUtilSSHelper#getPathInEditor(org.eclipse.ui.IEditorInput)
     */
    @Override
    public IPath getPathInEditor(IEditorInput input) {
        if(input instanceof NoResourceEditorInput)
            input = ((NoResourceEditorInput)input).getOriginEditorInput();
        if(input instanceof FileEditorInput)
            return ((FileEditorInput)input).getFile().getFullPath();
        else if(input instanceof IPathEditorInput)
            return ((IPathEditorInput)input).getPath();
        else if(input instanceof FileStoreEditorInput) {
            IPath path = URIUtil.toPath(((FileStoreEditorInput) input)
                    .getURI());
            return path;
        }
            return null;
    }

    /* (non-Javadoc)
     * @see org.csstudio.opibuilder.util.ResourceUtilSSHelper#workspacePathToSysPath(org.eclipse.core.runtime.IPath)
     */
    @Override
    public IPath workspacePathToSysPath(IPath path) {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        IResource resource = root.findMember(path);
        if(resource != null)
            return resource.getLocation();  //existing resource
        else
            return root.getFile(path).getLocation(); //for not existing resource
    }



    /**Get the IFile from IPath.
     * @param path Path to file in workspace
     * @return the IFile. <code>null</code> if no IFile on the path, file does not exist, internal error.
     */
    public static IFile getIFileFromIPath(final IPath path)
    {
        try
        {
            final IResource r = ResourcesPlugin.getWorkspace().getRoot().findMember(
                    path, false);
            if (r!= null && r instanceof IFile)
            {
                final IFile file = (IFile) r;
                if (file.exists())
                    return file;
            }
        }
        catch (Exception ex)
        {
            // Ignored
        }
        return null;
    }

    /**Get screenshot image from GraphicalViewer
     * @param viewer the GraphicalViewer
     * @return the screenshot image
     */
    @Override
    public Image getScreenShotImage(GraphicalViewer viewer){
        GC gc = new GC(viewer.getControl());
        final Image image = new Image(Display.getDefault(), viewer.getControl()
                .getSize().x, viewer.getControl().getSize().y);
        gc.copyArea(image, 0, 0);
        /* This is a workaround for issue 2345 - empty screenshot
         * https://github.com/ControlSystemStudio/cs-studio/issues/2345
         *
         * The workaround is calling gc.copyArea twice.
         */
        gc.copyArea(image, 0, 0);
        gc.dispose();
        return image;
    }

    @Override
    public IEditorInput editorInputFromPath(IPath path) {
        IEditorInput editorInput = null;
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
        // Files outside the workspace are handled differently
        // by Eclipse.
        if (!ResourceUtil.isExistingWorkspaceFile(path)
                && ResourceUtil.isExistingLocalFile(path)) {
            IFileStore fileStore = EFS.getLocalFileSystem()
                    .getStore(file.getFullPath());
            editorInput = new FileStoreEditorInput(fileStore);
        } else {
            editorInput = new FileEditorInput(file);
        }
        return editorInput;
    }

}
