/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. WITHOUT WARRANTY OF ANY
 * KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN ANY RESPECT, THE USER ASSUMES
 * THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS DISCLAIMER OF WARRANTY
 * CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER
 * EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION,
 * MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY AT
 * HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
package org.csstudio.sds.behavior.desy;

import org.csstudio.sds.model.BorderStyleEnum;
import org.csstudio.sds.util.ColorAndFontUtil;
import org.epics.css.dal.simple.Severity;

public class SeverityUtil {
	public static String determineColorBySeverity(final Severity severity) {
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

	public static BorderStyleEnum determineBorderStyleBySeverity(final Severity severity) {
		return severity.isOK()?BorderStyleEnum.NONE:BorderStyleEnum.LINE;
	}

	public static int determineBorderWidthBySeverity(final Severity severity) {
		return (severity.isOK()||severity.isInvalid())?0:3;
	}
}
