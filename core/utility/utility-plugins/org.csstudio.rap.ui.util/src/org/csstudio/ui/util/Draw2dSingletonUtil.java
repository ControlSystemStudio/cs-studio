package org.csstudio.ui.util;

import org.eclipse.draw2d.TextUtilities;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.SharedMessages;

public class Draw2dSingletonUtil {

	public static String ZoomManager_FIT_ALL = SharedMessages.getFitAllAction_Label();
	public static String ZoomManager_FIT_WIDTH = SharedMessages.getFitWidthAction_Label();
	public static String ZoomManager_FIT_HEIGHT = SharedMessages.getFitHeightAction_Label();

	public static TextUtilities getTextUtilities() {
		return TextUtilities.INSTANCE();
	}

	public static Rectangle getRectangle() {
		return Rectangle.getSINGLETON();
	}

}
