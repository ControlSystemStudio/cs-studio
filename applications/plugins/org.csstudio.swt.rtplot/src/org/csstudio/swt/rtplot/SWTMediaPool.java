/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;

/** Thread-safe caching pool of {@link Color}, {@link Font}, {@link Image}
 *
 *  @author Kay Kasemir
 */
public class SWTMediaPool
{
    final private Device device;

    final private Map<RGB, Color> colors = new HashMap<>();
    final private Map<FontData, Font> fonts = new HashMap<>();

    /** Create pool for device
     *
     *  <p>Need to dispose when done
     *  @param device {@link Device}
     */
    public SWTMediaPool(final Device device)
    {
        this.device = device;
    }

    /** Create pool for parent widget
     *
     *  <p>Will be disposed with parent
     *  @param parent Parent widget
     */
    public SWTMediaPool(final Composite parent)
    {
        device = parent.getDisplay();
        parent.addDisposeListener((e) -> dispose());
    }

    /** @param rgb Color description
     *  @return Color
     */
    public Color get(final RGB rgb)
    {
        synchronized (colors)
        {
            Color color = colors.get(rgb);
            if (color == null)
            {
                color = new Color(device, rgb);
                colors.put(rgb, color);
            }
            return color;
        }
    }
    /** @param font_data Font description
     *  @return Font
     */
    public Font get(final FontData font_data)
    {
        synchronized (fonts)
        {
            Font font = fonts.get(font_data);
            if (font == null)
            {
                font = new Font(device, font_data);
                fonts.put(font_data, font);
            }
            return font;
        }
    }

    /** Dispose pooled resources
     *
     *  <p>Needs to be called unless pool is
     *  associated with parent widget
     */
    public void dispose()
    {
        for (Font font : fonts.values())
            font.dispose();
        for (Color color : colors.values())
            color.dispose();
    }
}
