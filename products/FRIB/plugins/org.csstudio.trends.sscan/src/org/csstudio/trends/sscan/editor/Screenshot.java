/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.sscan.editor;

import java.io.File;

import org.csstudio.swt.xygraph.figures.XYGraph;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;

/** Helper for creating Screen-shot of XYGraph
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Screenshot
{
    private File file;

    /** Create screen-shot
     *  @param graph XYGraph from which to create the screen-shot
     *  @throws Exception on I/O error
     */
    public Screenshot(final XYGraph graph) throws Exception
    {
        // Get name for snapshot file
        try
        {
            file = File.createTempFile("Sscan", ".png");
            file.deleteOnExit();
        }
        catch (Exception ex)
        {
            throw new Exception("Cannot create tmp. file:\n" + ex.getMessage());
        }
        
        // Create snapshot file
        try
        {
            final ImageLoader loader = new ImageLoader();
            final Image image = graph.getImage();
            loader.data = new ImageData[]{image.getImageData()};
            image.dispose();
            loader.save(getFilename(), SWT.IMAGE_PNG);
        }
        catch (Exception ex)
        {
            throw new Exception(
                    NLS.bind("Cannot create snapshot in {0}:\n{1}",
                            getFilename(), ex.getMessage()));
        }
    }

    /** @return File that contains the screenshot */
    public File getFile()
    {
        return file;
    }
    
    /** @return Name of file that contains the screenshot */
    public String getFilename()
    {
        return file.getAbsolutePath();
    }
}
