/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.alarm.beast.ui;

import org.csstudio.alarm.beast.client.AlarmTreePosition;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

/** Helper for creating icons for alarm tree actions
 *  with a decoration that reflects the location
 *  of an item within the alarm tree
 *  @author Kay Kasemir
 */
public class AlarmTreeActionIcon extends CompositeImageDescriptor
{
    /** Create ImageDescriptor for Alarm Tree related Icon
     *  @param base_image_path Plugin path to the basic icon
     *  @param tree_position Tree position that determines the decoration or <code>null</code>
     *  @return ImageDescriptor
     */
    @SuppressWarnings("nls")
    public static ImageDescriptor createIcon(final String base_image_path,
            final AlarmTreePosition tree_position)
    {
        if (tree_position == AlarmTreePosition.Area)
            return new AlarmTreeActionIcon(base_image_path,
                                             "icons/dec_area.gif");
        if (tree_position == AlarmTreePosition.System)
            return new AlarmTreeActionIcon(base_image_path,
                                             "icons/dec_sys.gif");
        // Else: No decoration, use basic icon
        return Activator.getImageDescriptor(base_image_path);
    }

    final private ImageData base_image;
    final private String decorator_image_path;
    
    private AlarmTreeActionIcon(final String base_image_path,
                                  final String decorator_image_path)
    {
        this.base_image =
            Activator.getImageDescriptor(base_image_path).getImageData();
        this.decorator_image_path = decorator_image_path;
    }
    
    @Override
    protected void drawCompositeImage(int width, int height)
    {
        drawImage(base_image, 0, 0);
        final ImageData decoration =
            Activator.getImageDescriptor(decorator_image_path).getImageData();
        drawImage(decoration, 0, 0);
    }

    @Override
    protected Point getSize()
    {
        return new Point(base_image.width, base_image.height);
    }
}
