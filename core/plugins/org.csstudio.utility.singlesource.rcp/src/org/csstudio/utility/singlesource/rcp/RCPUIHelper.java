/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.singlesource.rcp;



import java.io.IOException;

import org.csstudio.ui.util.dialogs.ResourceSelectionDialog;
import org.csstudio.utility.singlesource.UIHelper;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.eclipse.ui.ide.IDE;

/** Helper for accessing RCP UI.
 * 
 *  @author Kay Kasemir
 *  @author Xihui Chen - Similar code in BOY/WebOPI
 */
public class RCPUIHelper extends UIHelper
{
	
    /** {@inheritDoc} 
     * @throws Exception */
    @Override
	public void openEditor(final IWorkbenchPage page, IPath path) throws Exception {
        // Copied from org.eclipse.ui.actions.OpenFileAction
        // in org.eclipse.ui.ide
        final IFile resource = RCPResourceHelper.getFileForPath(path);
        if (resource == null)
            throw new Exception(NLS.bind("Cannot find {0} in workspace", path));
        IDE.openEditor(page, resource);
	}
	
    /** {@inheritDoc} */
    @Override
    public IPath openSaveDialog(final Shell shell, final IPath original, final String extension)
    {
        final SaveAsDialog dlg = new SaveAsDialog(shell);
        dlg.setBlockOnOpen(true);
        
        final IFile orig_file = RCPResourceHelper.getFileForPath(original);
        if (orig_file != null)
            dlg.setOriginalFile(orig_file);
        if (dlg.open() != Window.OK)
            return null;

        // The path to the new resource relative to the workspace
        IPath path = dlg.getResult();
        if (path == null)
            return null;
        if (extension != null)
        {
            // Assert certain file extension
            final String ext = path.getFileExtension();
            if (ext == null  ||  !ext.equals(extension))
                path = path.removeFileExtension().addFileExtension(extension);
        }
        return path;
    }

	public IPath openDialog(final Shell shell, final int style,
			final IPath original, final String extension, String title) {
		if (title == null) {
			title = "Select File";
		}
        // Prompt for file
        final ResourceSelectionDialog res =
                new ResourceSelectionDialog(shell, title, new String[] { extension });
        if (res.open() != Window.OK)
            return null;
        
        return res.getSelectedResource();
	}

	/** {@inheritDoc} */
	@Override
	public void copyToClipboard(String[] contents) {
		// Copy as text to clipboard
		final Clipboard clipboard = new Clipboard(PlatformUI.getWorkbench()
				.getDisplay());
		clipboard.setContents(contents,
				new Transfer[] { TextTransfer.getInstance() });
	}

	/** {@inheritDoc} */
	@Override
	public void writeToConsole(final String consoleName,
			final ImageDescriptor imageDescriptor, final String message) {
		final MessageConsole console = getConsole(consoleName, imageDescriptor);
		if (console != null) {
			final MessageConsoleStream console_out = console.newMessageStream();
			console_out.println(message);
			try {
				console_out.close();
			} catch (IOException e) {
				// Ignored
			}
		}
	}


	/** Get a console in the Eclipse Console View for dumping the output
	 *  of invoked alarm actions.
	 *  <p>
	 *  Code based on
	 *  http://wiki.eclipse.org/FAQ_How_do_I_write_to_the_console_from_a_plug-in%3F
	 * @param imageDescriptor 
	 *
	 *  @return MessageConsole, newly created or one that already existed.
	 */
	private MessageConsole getConsole(final String consoleName,
			ImageDescriptor imageDescriptor) {
		if (consoleName == null) {
			return null;
		}
		final ConsolePlugin plugin = ConsolePlugin.getDefault();
		final IConsoleManager manager = plugin.getConsoleManager();
		final IConsole[] consoles = manager.getConsoles();
		for (int i = 0; i < consoles.length; i++)
			if (consoleName.equals(consoles[i].getName()))
				return (MessageConsole) consoles[i];
		// no console found, so create a new one
		final MessageConsole myConsole = new MessageConsole(consoleName,
				imageDescriptor);
		// There is no default console buffer limit in chars or lines?
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=46871
		// 2k char limit, keep 1k
		myConsole.setWaterMarks(1024, 2048);
		manager.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

    /** {@inheritDoc} */
	@Override
	public Image getScreenshot(
			final Display display,
			final Drawable drawable,
			final Rectangle bounds)
    {
        final GC gc = new GC(drawable);
		final Image image = new Image(display, bounds);
        gc.copyArea(image, 0, 0);
        gc.dispose();

        return image;
    }
}
