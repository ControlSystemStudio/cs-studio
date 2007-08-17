package org.csstudio.platform.ui.internal.actions;

import java.util.ArrayList;

import org.csstudio.platform.ui.workbench.FileEditorInput;
import org.csstudio.platform.ui.workbench.FileStoreEditorInput;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class OpenLocalFileAction extends Action implements IWorkbenchWindowActionDelegate {
	
	private IWorkbenchWindow window;
	private String filterPath;
	
	/**
	 * Creates a new action for opening a local file.
	 */
	public OpenLocalFileAction() {
		setEnabled(true);
	}

	public void dispose() {
		window =  null;
		filterPath =  null;
	}

	public void init(IWorkbenchWindow window) {
		this.window =  window;
		filterPath =  System.getProperty("user.home"); //$NON-NLS-1$
	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run() {
		FileDialog dialog =  new FileDialog(window.getShell(), SWT.OPEN | SWT.MULTI);
		dialog.setText("Open File");
		dialog.setFilterPath(filterPath);
		dialog.open();
		String[] names =  dialog.getFileNames();

		if (names != null) {
			filterPath =  dialog.getFilterPath();

			int numberOfFilesNotFound =  0;
			StringBuffer notFound =  new StringBuffer();
			for (int i =  0; i < names.length; i++) {
				IFileStore fileStore =  EFS.getLocalFileSystem().getStore(new Path(filterPath));
				fileStore =  fileStore.getChild(names[i]);
				if (!fileStore.fetchInfo().isDirectory() && fileStore.fetchInfo().exists()) {
					IWorkbenchPage page =  window.getActivePage();
					
					IEditorInput input = getEditorInput(fileStore);
			        String editorId = getEditorId(fileStore);
			        
			        try {
						page.openEditor(input, editorId);
					} catch (PartInitException e) {
						MessageDialog.openError(window.getShell(), "Editor not found", "An Editor for '"+fileStore.getName()+" couldn't be found");
						e.printStackTrace();
					}
				} else {
					if (++numberOfFilesNotFound > 1)
						notFound.append('\n');
					notFound.append(fileStore.getName());
				}
			}

			if (numberOfFilesNotFound > 0) {
				MessageDialog.openError(window.getShell(), "File not found", ""+numberOfFilesNotFound+" file(s) couldn't be found");
			}
		}
	}
	
	/**
	 * Create the Editor Input appropriate for the given <code>IFileStore</code>.
	 * The result is a normal file editor input if the file exists in the
	 * workspace and, if not, we create a wrapper capable of managing an
	 * 'external' file using its <code>IFileStore</code>.
	 * 
	 * @param fileStore
	 *            The file store to provide the editor input for
	 * @return The editor input associated with the given file store
	 */
	private static IEditorInput getEditorInput(IFileStore fileStore) {
		IFile workspaceFile = getWorkspaceFile(fileStore);
		if (workspaceFile != null)
			return new FileEditorInput(workspaceFile);
		return new FileStoreEditorInput(fileStore);
	}

	/**
	 * Determine whether or not the <code>IFileStore</code> represents a file
	 * currently in the workspace.
	 * 
	 * @param fileStore
	 *            The <code>IFileStore</code> to test
	 * @return The workspace's <code>IFile</code> if it exists or
	 *         <code>null</code> if not
	 */
	private static IFile getWorkspaceFile(IFileStore fileStore) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IFile[] files = root.findFilesForLocationURI(fileStore.toURI());
		files = filterNonExistentFiles(files);
		if (files == null || files.length == 0)
			return null;

		// for now only return the first file
		return files[0];
	}
	
	/**
	 * Filter the incoming array of <code>IFile</code> elements by removing
	 * any that do not currently exist in the workspace.
	 * 
	 * @param files
	 *            The array of <code>IFile</code> elements
	 * @return The filtered array
	 */
	@SuppressWarnings("unchecked")
	private static IFile[] filterNonExistentFiles(IFile[] files) {
		if (files == null)
			return null;

		int length = files.length;
		ArrayList existentFiles = new ArrayList(length);
		for (int i = 0; i < length; i++) {
			if (files[i].exists())
				existentFiles.add(files[i]);
		}
		return (IFile[]) existentFiles.toArray(new IFile[existentFiles.size()]);
	}
	
	/**
     * Get the id of the editor associated with the given <code>IFileStore</code>.
     * 
	 * @param workbench
	 * 	         the Workbench to use to determine the appropriate editor's id 
     * @param fileStore
     *           the <code>IFileStore</code> representing the file for which the editor id is desired
	 * @return the id of the appropriate editor
	 * @since 3.3
	 */
	private static String getEditorId(IFileStore fileStore) {
		IEditorDescriptor descriptor;
		try {
			descriptor = getEditorDescriptor(fileStore.getName(), true);;
		} catch (PartInitException e) {
			return null;
		}
		if (descriptor != null)
			return descriptor.getId();
		return null;
	}
	
	/**
	 * Returns an editor descriptor appropriate for opening a file resource with
	 * the given name.
	 * <p>
	 * The editor descriptor is determined using a multi-step process. This
	 * method will attempt to infer the content type of the file if
	 * <code>inferContentType</code> is <code>true</code>.
	 * </p>
	 * <ol>
	 * <li>The file is consulted for a persistent property named
	 * <code>IDE.EDITOR_KEY</code> containing the preferred editor id to be
	 * used.</li>
	 * <li>The workbench editor registry is consulted to determine if an editor
	 * extension has been registered for the file type. If so, an instance of
	 * the editor extension is opened on the file. See
	 * <code>IEditorRegistry.getDefaultEditor(String)</code>.</li>
	 * <li>The operating system is consulted to determine if an in-place
	 * component editor is available (e.g. OLE editor on Win32 platforms).</li>
	 * <li>The operating system is consulted to determine if an external editor
	 * is available.</li>
	 * </ol>
	 * </p>
	 * 
	 * @param name
	 *            the file name
	 * @param inferContentType
	 *            attempt to infer the content type from the file name if this
	 *            is <code>true</code>
	 * @return an editor descriptor, appropriate for opening the file
	 * @throws PartInitException
	 *             if no editor can be found
	 * @since 3.1
	 */
	public static IEditorDescriptor getEditorDescriptor(String name,
			boolean inferContentType) throws PartInitException {

		if (name == null) {
			throw new IllegalArgumentException();
		}

		IContentType contentType = inferContentType ? Platform
				.getContentTypeManager().findContentTypeFor(name) : null;
		IEditorRegistry editorReg = PlatformUI.getWorkbench()
				.getEditorRegistry();

		return getEditorDescriptor(name, editorReg, editorReg.getDefaultEditor(
				name, contentType));
	}
	
	/**
	 * Get the editor descriptor for a given name using the editorDescriptor
	 * passed in as a default as a starting point.
	 * 
	 * @param name
	 *            The name of the element to open.
	 * @param editorReg
	 *            The editor registry to do the lookups from.
	 * @param defaultDescriptor
	 *            IEditorDescriptor or <code>null</code>
	 * @return IEditorDescriptor
	 * @throws PartInitException
	 *             if no valid editor can be found
	 * 
	 * @since 3.1
	 */
	private static IEditorDescriptor getEditorDescriptor(String name,
			IEditorRegistry editorReg, IEditorDescriptor defaultDescriptor)
			throws PartInitException {

		if (defaultDescriptor != null) {
			return defaultDescriptor;
		}

		IEditorDescriptor editorDesc = defaultDescriptor;

		// next check the OS for in-place editor (OLE on Win32)
		if (editorReg.isSystemInPlaceEditorAvailable(name)) {
			editorDesc = editorReg
					.findEditor(IEditorRegistry.SYSTEM_INPLACE_EDITOR_ID);
		}

		// next check with the OS for an external editor
		if (editorDesc == null
				&& editorReg.isSystemExternalEditorAvailable(name)) {
			editorDesc = editorReg
					.findEditor(IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID);
		}

		return editorDesc;
	}

}
