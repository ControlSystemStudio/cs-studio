/*******************************************************************************
 * Copyright (c) 2014 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.rtplot.util;

import org.eclipse.swt.graphics.RGB;

/** Attempt to generate many 'unique' colors.
 *  @author Kay Kasemir
 */
public class RGBFactory
{
    private float hue = -120.0f;
    private float saturation = 1.0f;
    private float brightness = 1.0f;

    public synchronized RGB next()
    {   // Change hue, which gives the most dramatic difference
        hue += 120.0f;
        if (hue >= 360f)
        {   // Run different set of hues
            hue = (((int) hue)+30) % 360;
            if (hue == 120.0f)
            {   // Cycle darker colors
                brightness -= 0.5f;
                if (brightness <= 0.0f)
                {   // All the same starting at a weaker and darker color
                    // This scheme will then be repeated...
                    hue = 0.0f;
                    saturation = 0.7f;
                    brightness = 0.9f;
                }
            }
        }
        return new RGB(hue, saturation, brightness);
    }
}
