package org.csstudio.opibuilder.util;

import org.eclipse.swt.graphics.RGB;

/**The scheme for alarm color which provide unified colors for alarms.
 * @author Xihui Chen
 *
 */
public class AlarmColorScheme {

	
	public static final String MAJOR = "Major"; //$NON-NLS-1$
	public static final String MINOR = "Minor"; //$NON-NLS-1$
	public static final String INVALID = "Invalid"; //$NON-NLS-1$
	public static final String DISCONNECTED = "Disconnected"; //$NON-NLS-1$

	public static RGB getMajorColor(){
		return MediaService.getInstance().getColor(MAJOR); 
	}
	
	public static RGB getMinorColor(){
		return MediaService.getInstance().getColor(MINOR);
	}
	
	public static RGB getInValidColor(){
		return MediaService.getInstance().getColor(INVALID);
	}
	
	public static RGB getDisconnectedColor(){
		return MediaService.getInstance().getColor(DISCONNECTED);
	}
	
	
}
