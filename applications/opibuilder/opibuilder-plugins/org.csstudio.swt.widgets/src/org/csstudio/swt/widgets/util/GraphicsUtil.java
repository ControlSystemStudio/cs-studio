/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.swt.widgets.util;

import org.csstudio.swt.widgets.Preferences;
import org.eclipse.draw2d.Graphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.RGB;

/**The utility class contains functions that all related with graphics.
 * @author Xihui Chen
 *
 */
public class GraphicsUtil {

	public static synchronized boolean testPatternSupported(Graphics graphics){
		if(SWT.getPlatform().startsWith("rap")) //$NON-NLS-1$
			return false;
		if(!useAdvancedGraphics())
			return false;
		
		boolean support3D = true;
		//just test if pattern is supported on the platform.		
		try {						
			graphics.setBackgroundPattern(null);			
		} catch (Exception e) {
			support3D= false;				
		}
		
		return support3D;
	}

	/**
	 * If advanced graphics is enabled by system setting.
	 */
	public static boolean useAdvancedGraphics() {
		if(!Preferences.useAdvancedGraphics())
			return false;
		String value = System.getProperty(Preferences.PROHIBIT_ADVANCED_GRAPHICS); //$NON-NLS-1$
		if(value != null && value.equals("true")) //$NON-NLS-1$
			return false;
		return true;
	}
	
	public static Pattern createScaledPattern(Graphics graphics, Device device,
			float x1, float y1, float x2, float y2, Color color1, int alpha1,
			Color color2, int alpha2) {
		double scale = graphics.getAbsoluteScale();
		return new Pattern(device, (float) (x1 * scale), (float) (y1 * scale),
				(float) (x2 * scale), (float) (y2 * scale), color1, alpha1, color2,
				alpha2);
	}
	
	public static Pattern createScaledPattern(Graphics graphics, Device device,
			float x1, float y1, float x2, float y2, Color color1, Color color2) {
		double scale = graphics.getAbsoluteScale();
		return new Pattern(device, (float) (x1 * scale), (float) (y1 * scale),
				(float) (x2 * scale), (float) (y2 * scale), color1, color2);
	}
	
	
	/**
	 * Mixes the passed Colors and returns the resulting Color.
	 * 
	 * @param c1
	 *            the first color
	 * @param c2
	 *            the second color
	 * @param weight
	 *            the first color's weight from 0-1
	 * @return the new color
	 * @since 2.0
	 */
	public static RGB mixColors(RGB c1, RGB c2, double weight) {
		return new RGB((int) (c1.red * weight + c2.red
				* (1 - weight)), (int) (c1.green * weight + c2.green
				* (1 - weight)), (int) (c1.blue * weight + c2.blue
				* (1 - weight)));
	}
	
	
	
	
	
}
