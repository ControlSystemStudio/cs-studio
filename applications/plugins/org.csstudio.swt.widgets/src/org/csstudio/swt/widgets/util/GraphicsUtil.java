package org.csstudio.swt.widgets.util;

import org.csstudio.swt.widgets.Preferences;
import org.eclipse.draw2d.Graphics;

/**The utility class contains functions that all related with graphics.
 * @author Xihui Chen
 *
 */
public class GraphicsUtil {

	public static synchronized boolean testPatternSupported(Graphics graphics){
		if(!Preferences.useAdvancedGraphics())
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
	
	
	
	
	
}
