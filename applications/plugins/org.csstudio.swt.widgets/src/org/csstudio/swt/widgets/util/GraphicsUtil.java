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
		String value = System.getProperty("prohibit_advanced_graphics"); //$NON-NLS-1$
		if(value != null && value.equals("true")) //$NON-NLS-1$
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
