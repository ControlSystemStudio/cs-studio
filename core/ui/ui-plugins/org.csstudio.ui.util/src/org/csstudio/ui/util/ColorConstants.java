/*******************************************************************************
* Copyright (c) 2010-2016 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.ui.util;

import org.eclipse.swt.graphics.Color;

/**
 * <code>ColorConstants</code> provide a single source access to the real ColorConstants, which for some reason are
 * not same for RAP and RCP. RAP implements ColorConstants in a class with static methods, RCP does it with an
 * interface with fields.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 */
public class ColorConstants {

    public static final Color button = org.eclipse.draw2d.ColorConstants.button;
    public static final Color black = org.eclipse.draw2d.ColorConstants.black;
    public static final Color white = org.eclipse.draw2d.ColorConstants.white;
    public static final Color darkGreen = org.eclipse.draw2d.ColorConstants.darkGreen;
    public static final Color yellow = org.eclipse.draw2d.ColorConstants.yellow;
    public static final Color darkGray = org.eclipse.draw2d.ColorConstants.darkGray;
    public static final Color red = org.eclipse.draw2d.ColorConstants.red;
    public static final Color gray = org.eclipse.draw2d.ColorConstants.gray;
    public static final Color green = org.eclipse.draw2d.ColorConstants.green;
    public static final Color blue = org.eclipse.draw2d.ColorConstants.blue;
    public static final Color cyan = org.eclipse.draw2d.ColorConstants.cyan;
    public static final Color buttonLightest = org.eclipse.draw2d.ColorConstants.buttonLightest;
    public static final Color buttonDarkest = org.eclipse.draw2d.ColorConstants.buttonDarkest;
    public static final Color buttonDarker = org.eclipse.draw2d.ColorConstants.buttonDarker;
}
