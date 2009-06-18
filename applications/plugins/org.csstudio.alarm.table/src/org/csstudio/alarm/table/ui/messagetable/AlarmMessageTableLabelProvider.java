/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.alarm.table.ui.messagetable;

import java.util.StringTokenizer;

import org.csstudio.alarm.table.dataModel.AlarmMessage;
import org.csstudio.alarm.table.dataModel.BasicMessage;
import org.csstudio.alarm.table.preferences.JmsLogPreferenceConstants;
import org.csstudio.alarm.table.preferences.JmsLogPreferencePage;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * Label Provider to manage the text and color of the fields
 * in the table.
 * 
 * @author jhatje
 *
 */
public class AlarmMessageTableLabelProvider extends MessageTableLabelProvider implements
		ITableLabelProvider, ITableColorProvider {

	public AlarmMessageTableLabelProvider(String[] colNames) {
        super(colNames);
    }

    /**
	 * Check the severity of the current message (element) and
	 * return the color defined in the preference pages.
	 * 
	 * @return color for the current field
	 */
	public Color getBackground(Object element, int columnIndex) {
		AlarmMessage jmsm = (AlarmMessage) element;
        Color backgroundColor = readSeverityColor(jmsm);
	    super.getBackground(element, columnIndex);
		if (jmsm.isOutdated()) {
			if (backgroundColor == null) {
				backgroundColor = new Color(null, 255, 255, 255);
			}
			int red = backgroundColor.getRed();
			int green = backgroundColor.getGreen();
			int blue = backgroundColor.getBlue();
			if (red < 125) {
				red = red + 130;
			} else {
				red = 255;
			}
			if (green < 125) {
				green = green + 130;
			} else {
				green = 255;
			}
			if (blue < 125) {
				blue = blue + 130;
			} else {
				blue = 255;
			}
			backgroundColor = new Color(null, red, green, blue);
		}
		return backgroundColor;
	}
}
