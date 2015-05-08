/*******************************************************************************
 * Copyright (c) 2013 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.utility.singlesource;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;

/** Helper for accessing UI.
 *
 *  <p>This implementation provides the common support.
 *  Derived classes can add support that is specific to RCP or RAP.
 *
 *  <p>Client code should obtain a {@link UIHelper} via the {@link SingleSourcePlugin}
 *
 *  @author Kay Kasemir
 *  @author Xihui Chen - Similar code in BOY/WebOPI
 */
@SuppressWarnings("nls")
public class UIHelper
{
    /** Supported User Interface Toolkits */
    public enum UI
    {
        /** Rich Client Platform: SWT */
        RCP,

        /** Remote Application Platform: RWT */
        RAP
    };

    final private UI ui;

    /** Initialize */
    public UIHelper()
    {
        if (SWT.getPlatform().startsWith("rap"))
            ui = UI.RAP;
        else
            ui = UI.RCP;
    }

    /** @return {@link UI} */
    public UI getUI()
    {
        return ui;
    }

    /**
     * Open the file into the default editor
     * @param page Target page
     * @param path Path of the file to open
     * @throws Exception
     */
    public void openEditor(final IWorkbenchPage page, IPath path)
            throws Exception {
        return;
    }

    /** Prompt for file name to save data
     *
     *  @param shell Parent shell
     *  @param original Original file name, may be <code>null</code>
     *  @param extension Extension to enforce, without ".". May be <code>null</code>
     *  @return
     */
    public IPath openSaveDialog(final Shell shell, final IPath original, final String extension)
    {
        return openDialog(shell, SWT.SAVE, original, extension);
    }

    /** Prompt for file name
     *
     *  @param shell Parent shell
     *  @param style Style of window
     *  @param original Original file name, may be <code>null</code>
     *  @param extension Extension to enforce, without ".". May be <code>null</code>
     *  @return
     */
    public IPath openDialog(final Shell shell, final int style,
            final IPath original, final String extension) {
        return openDialog(shell, style, original, extension, null);
    }

    /**
     * Prompt for file name
     *
     * @param shell Parent shell
     * @param style Style of window
     * @param original Original file name, may be <code>null</code>
     * @param extension Extension to enforce, without ".". May be <code>null</code>
     * @param title dialog tile
     * @return
     */
    public IPath openDialog(final Shell shell, final int style,
            final IPath original, final String extension, final String title) {
        return null;
    }

    /**
     * Prompt for file name
     *
     * @param shell Parent shell
     * @param style open dialog style
     * @param original Original file name, may be <code>null</code>
     * @param extension Extension to enforce, without ".". May be <code>null</code>
     * @return
     */
    public String openOutsideWorkspaceDialog(final Shell shell,
            final int style, final IPath original, final String extension) {
        return null;
    }

    /**
     * Copy contents to clipboard
     * @param contents
     */
    public void copyToClipboard(String[] contents) {
    }

    /**
     * Write the message into console named consoleName
     *
     * @param consoleName Console name
     * @param imageDescriptor
     * @param message Message to write
     */
    public void writeToConsole(final String consoleName,
            final ImageDescriptor imageDescriptor, final String message) {
        return;
    }

    /** @param display Display
     *  @param drawable Drawable
     *  @param bounds bounds of that drawable
     *  @return Image with screenshot of the drawable
     */
    public Image getScreenshot(
            final Display display,
            final Drawable drawable,
            final Rectangle bounds) {

        return null;
    }

    /**Popup login dialog to authenticate user with the registered login module.
     * This method must be called in UI thread.
     * @param display display of the session.
     * @param retry the allowed number of retries.
     * @return true if login successfully.
     */
    public boolean rapAuthenticate(Display display){
        return false;
    }

    /**Check if current RAP session is logged in.
     * @param display
     * @return
     */
    public boolean rapIsLoggedIn(Display display){
        return false;
    }

    /** @param site Site on which to enable/disable closing
     *  @param enable_close Enable the close button, allow closing the part?
     */
    public void enableClose(IWorkbenchPartSite site, boolean enable_close) {
        // By default, this is not supported
    }

    /** @param view View to 'detach' */
    public void detachView(IViewPart view) {
        // By default, this is not supported
    }
}
