/**
 * 
 */
package org.csstudio.logbook.ui.util;

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

/**
 * @author shroffk
 * 
 */
public class IFileUtil {

    private final static IFileUtil instance = new IFileUtil();
    private final static Map<IWorkbenchPart, IFile> fileMap = new HashMap<IWorkbenchPart, IFile>();

    private IFileUtil() {
	PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
		.addPartListener(new IPartListener2() {

		    @Override
		    public void partClosed(IWorkbenchPartReference partRef) {
			if (fileMap.containsKey(partRef.getPart(false))) {
			    try {
				fileMap.get(partRef.getPart(false)).delete(
					true, null);
			    } catch (CoreException e) {
				e.printStackTrace();
			    }
			    fileMap.remove(partRef.getPart(false));
			}
		    }

		    @Override
		    public void partActivated(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		    }

		    @Override
		    public void partBroughtToTop(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		    }

		    @Override
		    public void partDeactivated(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		    }

		    @Override
		    public void partOpened(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		    }

		    @Override
		    public void partHidden(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		    }

		    @Override
		    public void partVisible(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		    }

		    @Override
		    public void partInputChanged(IWorkbenchPartReference partRef) {
			// TODO Auto-generated method stub

		    }
		});

    }

    public static IFileUtil getInstance() {
	return instance;
    }

    public IFile createFileResource(String fileName, InputStream inputStream)
	    throws IOException {
	if (fileName != null && !fileName.isEmpty()) {
	    File file = new File(fileName);
	    OutputStream out = new FileOutputStream(file);
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

    public IFile createFileResource(File file) {
	IWorkspace workspace = ResourcesPlugin.getWorkspace();
	IProject project = workspace.getRoot().getProject("External Files");
	IFile ifile = project.getFile(file.getName());
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

    public IFile createURLFileResource(URI uri) throws IOException {
	int slashIndex = uri.toString().lastIndexOf('/');
	String fileName = uri.toString().substring(slashIndex + 1);
	if (fileName != null && !fileName.isEmpty()) {
	    File file = new File(fileName);
	    InputStream ip = uri.toURL().openConnection().getInputStream();
	    OutputStream out = new FileOutputStream(file);
	    // Transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = ip.read(buf)) > 0) {
		out.write(buf, 0, len);
	    }
	    ip.close();
	    out.close();
	    return createFileResource(file);
	} else
	    return null;
    }

    /**
     * This will do the cleanup once the editor associated with the temporary
     * file has been closed
     * 
     * @param part
     * @param file
     */
    public void registerPart(IWorkbenchPart part, IFile file) {
	fileMap.put(part, file);
    }

}
