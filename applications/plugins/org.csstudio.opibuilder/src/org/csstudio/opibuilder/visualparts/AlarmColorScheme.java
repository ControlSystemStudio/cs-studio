package org.csstudio.opibuilder.visualparts;

import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.swt.graphics.RGB;

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
