/**
 * 
 */
package org.csstudio.utility.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.csstudio.ui.util.NoResourceEditorInput;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author shroffk
 * 
 */
public class IFileUtil {

	private static final String TAG_PLUGIN = "IFileUtil"; //$NON-NLS-1$
	private static final String TAG_PART = "part"; //$NON-NLS-1$
	private static final String TAG_PATH = "path"; //$NON-NLS-1$
	private static final String TAG_COUNT = "count"; //$NON-NLS-1$
    private final static IFileUtil instance = new IFileUtil();
    final ConcurrentMap<IFile, AtomicLong> map = new ConcurrentHashMap<IFile, AtomicLong>();
    private final SyncShutdown syncShutdown = new SyncShutdown();

    private class SyncShutdown {
    	private boolean shutdown = false;
    	
    	public synchronized void shutdown() {
    		shutdown = true;
    	}
    	
    	public synchronized boolean status() {
    		return shutdown;
    	}
    }
    
	private IFileUtil() {

		PlatformUI.getWorkbench().addWindowListener(new IWindowListener(){

			@Override
			public void windowActivated(IWorkbenchWindow window) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeactivated(IWorkbenchWindow window) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosed(IWorkbenchWindow window) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowOpened(IWorkbenchWindow window) {
				for(IWorkbenchPage page: window.getPages()){
	        		page.addPartListener(new IPartListener2() {

	        		    @Override
	        		    public void partClosed(IWorkbenchPartReference partRef) {
	        		    	IFile iFile = null;
	        		    	if(!syncShutdown.status()){
	        		    		IWorkbenchPart part = partRef.getPart(false);
								if (partRef instanceof IEditorReference) {
									IEditorInput input = ((IEditorPart)part) == null ? null : ((IEditorPart)part).getEditorInput();
									if (input instanceof NoResourceEditorInput)
										input = ((NoResourceEditorInput)input).getOriginEditorInput();
									iFile = input instanceof FileEditorInput ? ((FileEditorInput)input).getFile(): null;
									
								} else {
									try {
										Field f = part.getClass().getDeclaredField("input");
										f.setAccessible(true);
										IEditorInput element = (IEditorInput) f.get(part);
										Field f2 = element.getClass().getDeclaredField("path");
										f2.setAccessible(true);
										Path fileName = (Path) f2.get(element);
										iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(fileName);

									} catch (NoSuchFieldException
											| SecurityException
											| IllegalArgumentException
											| IllegalAccessException e1) {
										// I don't care if these fail, best
										// attempt to read boy view files								
										iFile = null;
										// e1.printStackTrace();
									}
								}
								if (iFile !=null && !map.isEmpty()){
									if(map.get(iFile).decrementAndGet() <= 0){
		        		    			map.remove(iFile);
		        		    			try {
											iFile.delete(true, null);
										} catch (CoreException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
		        		    		}
								}
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
				
			}} );
		
		attachShutdownListener();
	}

    

    public static IFileUtil getInstance() {
	return instance;
    }

    private void attachShutdownListener(){
    	PlatformUI.getWorkbench().addWorkbenchListener(
				new IWorkbenchListener() {

					@Override
					public boolean preShutdown(IWorkbench workbench,
							boolean forced) {
						syncShutdown.shutdown();
						return true;
					}

					@Override
					public void postShutdown(IWorkbench workbench) {
						// TODO Auto-generated method stub

					}

				});
    	
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
	    map.putIfAbsent(ifile, new AtomicLong(0));
	    map.get(ifile).incrementAndGet();
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
    @Deprecated
    public void registerPart(IWorkbenchPart part, IFile file) {
    	//fileMap.put("F"+String.valueOf(part.hashCode()), file);
    }




	public void saveState(IMemento memento) {
		IMemento path = memento.createChild(TAG_PLUGIN);
		for(Map.Entry<IFile, AtomicLong> entry: map.entrySet()){
			AtomicLong count = entry.getValue();
			String filePath = entry.getKey().getFullPath().toString();
			IMemento part = path.createChild(TAG_PART);
			part.putString(TAG_PATH, filePath);
			part.putInteger(TAG_COUNT, count.intValue());
		}
		
	}


	public void restoreState(IMemento memento) {
		for (IMemento child : memento.getChildren(TAG_PLUGIN)) {
			for (IMemento part : child.getChildren(TAG_PART)) {
				Integer count = part.getInteger(TAG_COUNT);
				String fileName = part.getString(TAG_PATH);
				if (count != null) {
					IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(fileName));
					if (iFile != null) {
						map.put(iFile, new AtomicLong(count));
					}
				}
			}
			
		}
	}

}
