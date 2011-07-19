/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.apputil.ui.swt;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

/** TabFolder that keeps ImagePreview items for a list of image files,
 *  allowing addition, removal of images
 *  @author Kay Kasemir
 */
public class ImageTabFolder
{
    final private TabFolder tab_folder;
    final private List<String> image_filenames = new ArrayList<String>();

    /** Initialize: Create tab folder, ... */
    public ImageTabFolder(Composite parent, int style)
    {
        tab_folder = new TabFolder(parent, style);
        hookDragAndDrop(parent);
    }

    /** @return TabFolder control */
    public TabFolder getControl()
    {
        return tab_folder;
    }

    /** Create an "Add Image" button that will interact with the image tabs
     *  @param parent Parent widget
     *  @return SWT button
     */
    public Button createAddButton(final Composite parent)
    {
        final Button button = new Button(parent, SWT.PUSH);
        button.setText(Messages.AddImage);
        button.setToolTipText(Messages.AddImageTT);
        button.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                addImage();
            }
        });

        return button;
    }

    /** Create an "Add Image" button that will interact with the image tabs
     *  @param parent Parent widget
     *  @param full Complete screenshot, or only application window?
     *  @return SWT button
     */
    public Button createScreenshotButton(final Composite parent, final boolean full)
    {
        final Button button = new Button(parent, SWT.PUSH);
        if (full)
        {
        	button.setText(Messages.AddFullScreenshot);
        	button.setToolTipText(Messages.AddFullScreenshotTT);
        }
        else
        {
        	button.setText(Messages.AddApplicationScreenshot);
        	button.setToolTipText(Messages.AddApplicationScreenshotTT);
        }
        button.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                addScreenshot(full);
            }
        });

        return button;
    }
    
	/** Allow dropping file names (presumably images) */
    private void hookDragAndDrop(Composite parent)
    {
        // Use the whole parent as drop target.
        // When dropping into text fields, the text widget itself will fetch the text,
        // but anywhere else it will pick the image
        DropTarget file_drop = new DropTarget(parent, DND.DROP_MOVE | DND.DROP_COPY);
        file_drop.setTransfer(new Transfer[]
        {
                FileTransfer.getInstance()
        });
        file_drop.addDropListener(new DropTargetAdapter()
        {
            @Override
            public void drop(final DropTargetEvent event)
            {
                final String names[] = (String[]) event.data;
                for (String name : names)
                    addImage(name);
            }
        });
    }

    /** Add image preview to tab folder
     *  @param filename Image file name
     */
    public void addImage(final String filename)
    {
        // Add tab item
        final TabItem tab = new TabItem(tab_folder, 0);
        tab.setText(NLS.bind(Messages.ImageTabFmt, tab_folder.getItemCount()));

        final Composite box = new Composite(tab_folder, 0);
        box.setLayout(new GridLayout(2, false));

        // Preview
        final ImagePreview image_preview = new ImagePreview(box, null, filename);
        image_preview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // Delete button
        final Button delete = new Button(box, SWT.PUSH);
        delete.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
        delete.setText(Messages.RemoveImage);
        delete.setToolTipText(Messages.RemoveImageTT);
        delete.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                TabItem tabs[] = tab_folder.getItems();
                for (int i=0; i<tabs.length; ++i)
                    if (tabs[i] == tab)
                    {
                        removeImage(i);
                        return;
                    }
            }
        });

        // File name label
        final Label label = new Label(box, 0);
        label.setText(filename);
        label.setLayoutData(new GridData(SWT.FILL, 0, true, false, 2, 1));

        tab.setControl(box);

        // Select the newly added tab, i.e. the last one
        tab_folder.setSelection(tab_folder.getItemCount()-1);

        // Add file name to list
        image_filenames.add(filename);
    }

    @SuppressWarnings("nls")
    protected void addScreenshot(final boolean full)
    {
    	// Hide the shell that displays the dialog
    	// to keep the dialog itself out of the screenshot
    	tab_folder.getShell().setVisible(false);
    	
    	// Take the screen shot
		final Image image = full
			? Screenshot.getFullScreenshot()
			: Screenshot.getApplicationScreenshot();

		// Show the dialog again
		tab_folder.getShell().setVisible(true);

        // Write to file
        try
        {
        	final File screenshot_file = File.createTempFile("screenshot", ".png");
        	screenshot_file.deleteOnExit();

        	final ImageLoader loader = new ImageLoader();
            loader.data = new ImageData[] { image.getImageData() };
            image.dispose();
    	    // Save
    	    loader.save(screenshot_file.getPath(), SWT.IMAGE_PNG);
        	
        	addImage(screenshot_file.getPath());
        }
        catch (Exception ex)
        {
        	MessageDialog.openError(tab_folder.getShell(),
        			"Error", ex.getMessage());
        }
    }
    
    /** Remove image from preview and list of images-to-add
     *  @param i Index of image
     */
    protected void removeImage(final int i)
    {
        // Remove tab with preview
        final TabItem tab = tab_folder.getItem(i);
        final Control tab_control = tab.getControl();
        tab.dispose();
        tab_control.dispose();

        // Remove from list of file names
        image_filenames.remove(i);

        // Re-number the tabs
        final TabItem tabs[] = tab_folder.getItems();
        for (int t=i; t<tabs.length; ++t)
            tabs[t].setText(NLS.bind(Messages.ImageTabFmt, t+1));
    }

    /** @return Image file names */
    public String[] getFilenames()
    {
        return image_filenames.toArray(new String[image_filenames.size()]);
    }

    /** Prompt for image file to add */
    protected void addImage()
    {
        final FileDialog dlg = new FileDialog(getControl().getShell(), SWT.OPEN);
        dlg.setFilterExtensions(new String [] { "*.png" }); //$NON-NLS-1$
        dlg.setFilterNames(new String [] { "PNG Image" }); //$NON-NLS-1$
        final String filename = dlg.open();
        if (filename != null)
            addImage(filename);
    }
}
