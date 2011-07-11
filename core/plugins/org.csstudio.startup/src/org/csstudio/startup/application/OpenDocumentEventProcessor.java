/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.startup.application;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.csstudio.openfile.DisplayUtil;
import org.csstudio.startup.Plugin;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * Helper class used to open file from command line.
 *
 * @author Xihui Chen
 */
public class OpenDocumentEventProcessor implements Listener {
	// Unless called with files to open, this list is never used,
	// so initialize with 0 capacity
    final private List<String> filesToOpen = new ArrayList<String>(0);

	public final static String OPEN_DOC_PROCESSOR = "css.openDocProcessor"; //$NON-NLS-1$

	/**
	 * Constructor.
	 * @param display display used as a source of event
	 */
	public OpenDocumentEventProcessor(Display display) {
		display.addListener(SWT.OpenDocument, this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
	 */
	public void handleEvent(Event event) {
		final String path = event.text;
		if (path == null)
			return;
		// If we start supporting events that can arrive on a non-UI thread, the following
		// line will need to be in a "synchronized" block:
		filesToOpen.add(path);
	}

	/**
	 * Process delayed events.
	 * @param display display associated with the workbench
	 */
	public void catchUp(Display display) {
		if (filesToOpen.isEmpty())
			return;

		// If we start supporting events that can arrive on a non-UI thread, the following
		// lines will need to be in a "synchronized" block:
		String[] filePaths = new String[filesToOpen.size()];
		filesToOpen.toArray(filePaths);
		filesToOpen.clear();

		for(int i = 0; i < filePaths.length; i++) {
			openFile(display, filePaths[i]);
		}
	}

	private void openFile(Display display, final String path) {
		display.asyncExec(new Runnable() {
			public void run() {
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (window == null)
					return;

				int ds = path.lastIndexOf('.');
				int de = path.substring(ds+1).indexOf(' ');
				String pathPart = path;
				String ext;
				String data = null;
				if(de ==-1){
					ext = path.substring(ds +1).trim();
				}
				else{
					de += ds+1;
					pathPart = path.substring(0,de);
					ext = path.substring(ds+1, de).trim();
					data = path.substring(de + 1);
					data = replaceAsciiCode(data);
				}
				//open file with DisplayUtil if it is a supported Display file
				if(DisplayUtil.getInstance().isExtensionSupported(ext)){
					try {
						DisplayUtil.getInstance().openDisplay(pathPart
								, data);
						Shell shell = window.getShell();
						if (shell != null) {
							if (shell.getMinimized())
								shell.setMinimized(false);
							shell.forceActive();
						}
						return;
					} catch (Exception e) {
						String msg = NLS.bind("The file ''{0}'' could not be opened as a display. \n " +
								"It will be opened by default editor",	path);
						MessageDialog.openError(window.getShell(), "Open Display", msg);
						Logger.getLogger(Plugin.ID).log(Level.WARNING, msg, e);
					}
				}


				IFileStore fileStore = EFS.getLocalFileSystem().getStore(new Path(path));
				IFileInfo fetchInfo = fileStore.fetchInfo();
				if (!fetchInfo.isDirectory() && fetchInfo.exists()) {
					IWorkbenchPage page = window.getActivePage();
					if (page == null) {
						String msg = "No window available";
						MessageDialog.open(MessageDialog.ERROR, window.getShell(),
								"Open File",
								msg, SWT.SHEET);
					}
					try {


						IDE.openInternalEditorOnFileStore(page, fileStore);
						Shell shell = window.getShell();
						if (shell != null) {
							if (shell.getMinimized())
								shell.setMinimized(false);
							shell.forceActive();
						}
					} catch (PartInitException e) {
						String msg = NLS.bind("The file ''{0}'' could not be opened. See log for details.",
										fileStore.getName());
						CoreException eLog = new PartInitException(e.getMessage());
						Logger.getLogger(Plugin.ID).log(Level.WARNING, "Cannot open " + fileStore.getName(), eLog);
						MessageDialog.open(MessageDialog.ERROR, window.getShell(),
								"Open File",
								msg, SWT.SHEET);
					}
				} else {
					String msg = NLS.bind("The file ''{0}'' could not be opened. See log for details.", path);
					MessageDialog.open(MessageDialog.ERROR, window.getShell(),
							"Open File",
							msg, SWT.SHEET);
				}
			}
		});
	}
	
	/**Replace ascii code with its characters in a string. The ascii code in the string must 
	 * follow this format <code>[\ascii]</code>. For example: <code>abc[\58]def</code> will be replaced with <code>abc:def</code>.
	 * @param input the input string 
	 */
	private static String replaceAsciiCode(final String input){
		String output = input;
		Pattern p = Pattern.compile("\\x5b\\\\\\d+\\x5d"); //$NON-NLS-1$
		Matcher m = p.matcher(input);
		while(m.find()){
			String g = m.group();
			String asciiString = g.substring(2,g.length()-1);
			char code = (char) Integer.parseInt(asciiString);
			output = output.replace(g, ""+code);
		}
		return output;
	}
}
