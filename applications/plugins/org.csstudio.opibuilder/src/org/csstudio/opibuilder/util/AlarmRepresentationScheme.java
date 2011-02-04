package org.csstudio.opibuilder.util;

import org.csstudio.opibuilder.visualparts.BorderFactory;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.Border;
import org.eclipse.swt.graphics.RGB;

/**The scheme for alarm color which provide unified colors and borders for alarms.
 * @author Xihui Chen
 *
 */
public class AlarmRepresentationScheme {

	
	public static final String MAJOR = "Major"; //$NON-NLS-1$
	public static final String MINOR = "Minor"; //$NON-NLS-1$
	public static final String INVALID = "Invalid"; //$NON-NLS-1$
	public static final String DISCONNECTED = "Disconnected"; //$NON-NLS-1$
	
	private static final AbstractBorder DISCONNECT_BORDER = BorderFactory.createBorder(
			BorderStyle.TITLE_BAR, 1, AlarmRepresentationScheme.getDisconnectedColor(), 
			"Disconnected");

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
	
	public static Border getMajorBorder(){
		return BorderFactory.createBorder(BorderStyle.LINE, 2, getMajorColor(), ""); //$NON-NLS-1$
	}
	
	public static Border getMinorBorder(){
		return BorderFactory.createBorder(BorderStyle.LINE, 2, getMinorColor(), ""); //$NON-NLS-1$
	}
	
	public static Border getInvalidBorder(){
		return BorderFactory.createBorder(BorderStyle.LINE, 2, getInValidColor(), ""); //$NON-NLS-1$
	}
	
	public static Border getDisonnectedBorder(){
		return DISCONNECT_BORDER;
	}
}
