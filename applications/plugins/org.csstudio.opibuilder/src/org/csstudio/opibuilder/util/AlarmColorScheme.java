package org.csstudio.opibuilder.util;

import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.swt.graphics.RGB;

/**The scheme for alarm color which provide unified colors for alarms.
 * @author Xihui Chen
 *
 */
public class AlarmColorScheme {

	public static RGB getMajorColor(){
		return CustomMediaFactory.COLOR_RED;
	}
	
	public static RGB getMinorColor(){
		return CustomMediaFactory.COLOR_ORANGE;
	}
	
	public static RGB getInValidColor(){
		return CustomMediaFactory.COLOR_PINK;
	}
	
	
}
