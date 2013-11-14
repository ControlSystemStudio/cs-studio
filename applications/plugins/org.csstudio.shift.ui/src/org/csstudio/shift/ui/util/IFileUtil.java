package org.csstudio.shift.ui.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;

public class IFileUtil {

    private final static IFileUtil instance = new IFileUtil();
    private final static Map<IWorkbenchPart, IFile> fileMap = new HashMap<IWorkbenchPart, IFile>();

    private IFileUtil() {
    	PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().addPartListener(new IPartListener2() {

		    @Override
		    public void partClosed(final IWorkbenchPartReference partRef) {
			if (fileMap.containsKey(partRef.getPart(false))) {
			    try {
				fileMap.get(partRef.getPart(false)).delete(true, null);
			    } catch (CoreException e) {
			    	e.printStackTrace();
			    }
			    fileMap.remove(partRef.getPart(false));
			}
		    }

		    @Override
		    public void partActivated(final IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		    }

		    @Override
		    public void partBroughtToTop(final IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		    }

		    @Override
		    public void partDeactivated(final IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		    }

		    @Override
		    public void partOpened(final IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		    }

		    @Override
		    public void partHidden(final IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		    }

		    @Override
		    public void partVisible(final IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		    }

		    @Override
		    public void partInputChanged(final IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		    }
		});

    }

    public static IFileUtil getInstance() {
	return instance;
    }

    public IFile createFileResource(final String fileName,final  InputStream inputStream)
	    throws IOException {
        if (fileName != null && !fileName.isEmpty()) {
            final File file = new File(fileName);
            final OutputStream out = new FileOutputStream(file);
            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
            out.write(buf, 0, len);
            }
            inputStream.close();
            out.close();
            IFile ifile = createFileResource(file);
            file.delete();
            return ifile;
        } else {
            return null;
        }
    }

    public IFile createFileResource(final File file) {
        final IWorkspace workspace = ResourcesPlugin.getWorkspace();
        final IProject project = workspace.getRoot().getProject("External Files");
        final IFile ifile = project.getFile(file.getName());
        try {
            if (!project.exists())
            project.create(null);
            if (!project.isOpen())
            project.open(null);
            project.setHidden(true);
            if (!ifile.exists())
            ifile.create(new FileInputStream(file), IResource.NONE, null);
            return ifile;
        } catch (CoreException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
        }

    public IFile createURLFileResource(final URI uri) throws IOException {
        final int slashIndex = uri.toString().lastIndexOf('/');
        final String fileName = uri.toString().substring(slashIndex + 1);
        if (fileName != null && !fileName.isEmpty()) {
            final File file = new File(fileName);
            final InputStream ip = uri.toURL().openConnection().getInputStream();
            final OutputStream out = new FileOutputStream(file);
            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = ip.read(buf)) > 0) {
            out.write(buf, 0, len);
            }
            ip.close();
            out.close();
            return createFileResource(file);
        } else {
            return null;
        }
    }

    /**
     * This will do the cleanup once the editor associated with the temporary
     * file has been closed
     * 
     * @param part
     * @param file
     */
    public void registerPart(final IWorkbenchPart part, final IFile file) {
	fileMap.put(part, file);
    }

}
