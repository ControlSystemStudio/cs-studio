package org.csstudio.opibuilder.widgets.util;

import org.eclipse.draw2d.Graphics;

public class GraphicsUtil {

	public static synchronized boolean testPatternSupported(Graphics graphics){
		boolean support3D = true;
		//just test if pattern is supported on the platform.		
		try {						
			graphics.setBackgroundPattern(null);			
		} catch (Exception e) {
			support3D= false;				
		}
		
		return support3D;
	}
	
}
