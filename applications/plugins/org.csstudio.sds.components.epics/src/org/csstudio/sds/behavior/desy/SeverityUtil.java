package org.csstudio.sds.behavior.desy;

import org.csstudio.sds.model.BorderStyleEnum;
import org.csstudio.sds.util.ColorAndFontUtil;
import org.epics.css.dal.simple.Severity;

public class SeverityUtil {
	public static String determineColorBySeverity(Severity severity) {
		assert severity != null;

		String color = "#000000";

		if (severity.isOK()) {
			// .. green
			color = ColorAndFontUtil.toHex(0, 216, 0);
		} else if (severity.isMinor()) {
			// .. yellow
			color = ColorAndFontUtil.toHex(251, 243, 74);
		} else if (severity.isMajor()) {
			// .. red
			color = ColorAndFontUtil.toHex(253, 0, 0);
		} else {
			// .. white
			color = ColorAndFontUtil.toHex(255, 255, 255);
		}

		return color;
	}

	public static BorderStyleEnum determineBorderStyleBySeverity(Severity severity) {
		return severity.isOK()?BorderStyleEnum.NONE:BorderStyleEnum.LINE;
	}
	
	public static int determineBorderWidthBySeverity(Severity severity) {
		return severity.isOK()?0:3;
	}
}
