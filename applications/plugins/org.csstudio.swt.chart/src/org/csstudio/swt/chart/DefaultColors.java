/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.chart;

/** Helper for providing default colors.
 * 
 *  @author Kay Kasemir
 */
public class DefaultColors
{
    /** Default colors for newly added series, used over when reaching the end.
     *  <p>
     *  Very hard to find a long list of distinct colors.
     *  This list is definetely too short...
     */
    private static final int[][] default_colors =
    {
        {  21,  21, 196 }, // blue
        { 242,  26,  26 }, // red
        {  33, 179,  33 }, // green
        { 255, 190,   0 }, // (darkish) yellow
        { 128,   0, 255 }, // violett
        {   0,   0,   0 }, // black
        { 243, 132, 132 }, // peachy
        {   0, 255,  11 }, // neon green
        {   0, 214, 255 }, // neon blue
        { 114,  40,   3 }, // brown
        { 255,   0, 240 }, // pink
        { 219, 128,   4 }, // orange
    };

    /**
     * @param num Color index 0, .... No upper end, but colors get reused when
     *            exceeding the number of predefined colors.
     * @return Returns the 'red' component of that default color.
     */
    public final static int getRed(int num)
    {
        num %= default_colors.length;
        return default_colors[num][0];
    }

    /**
     * @param num Color index 0, .... No upper end, but colors get reused when
     *            exceeding the number of predefined colors.
     * @return Returns the 'green' component of that default color.
     */
    public final static int getGreen(int num)
    {
        num %= default_colors.length;
        return default_colors[num][1];
    }

    /**
     * @param num Color index 0, .... No upper end, but colors get reused when
     *            exceeding the number of predefined colors.
     * @return Returns the 'green' component of that default color.
     */
    public final static int getBlue(int num)
    {
        num %= default_colors.length;
        return default_colors[num][2];
    }
}
