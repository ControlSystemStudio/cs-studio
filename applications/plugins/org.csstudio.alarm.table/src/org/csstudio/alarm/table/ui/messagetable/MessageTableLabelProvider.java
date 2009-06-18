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

import org.csstudio.alarm.table.dataModel.BasicMessage;
import org.csstudio.alarm.table.preferences.JmsLogPreferenceConstants;
import org.csstudio.alarm.table.preferences.JmsLogPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

/**
 * Label Provider to manage the text and color of the fields in the table.
 * 
 * @author jhatje
 * 
 */
public class MessageTableLabelProvider extends LabelProvider implements
        ITableLabelProvider, ITableColorProvider {

    String[] columnNames;

    /**
     * Contructor gets the column names from the table viewer
     * 
     * @param colNames
     */
    public MessageTableLabelProvider(String[] colNames) {
        columnNames = colNames;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang
     * .Object, int)
     */
    public Image getColumnImage(Object element, int columnIndex) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * The label provider ask for the text for a table field. We read the
     * property value of the current message/element for the table column
     * (index).
     * 
     * @return the String for current table field.
     */
    public String getColumnText(Object element, int index) {
        try {
            BasicMessage jmsm = (BasicMessage) element;
            // if (index == 0) {
            // return "true";
            // }
            return jmsm.getProperty(columnNames[index].toUpperCase());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "N/A";
    }

    public String getColumnName(int index) {
        return columnNames[index].toUpperCase();
    }

    /**
     * Check the severity of the current message (element) and return the color
     * defined in the preference pages.
     * 
     * @return color for the current field
     */
    public Color getBackground(Object element, int columnIndex) {
        BasicMessage jmsm = (BasicMessage) element;
        Color backgroundColor = readSeverityColor(jmsm);
//        if (jmsm.isBackgroundColorGray()) {
//            if (backgroundColor == null) {
//                backgroundColor = new Color(null, 255, 255, 255);
//            }
//            int red = backgroundColor.getRed();
//            int green = backgroundColor.getGreen();
//            int blue = backgroundColor.getBlue();
//            if (red < 125) {
//                red = red + 130;
//            } else {
//                red = 255;
//            }
//            if (green < 125) {
//                green = green + 130;
//            } else {
//                green = 255;
//            }
//            if (blue < 125) {
//                blue = blue + 130;
//            } else {
//                blue = 255;
//            }
//            backgroundColor = new Color(null, red, green, blue);
//        }
        return backgroundColor;
    }

    Color readSeverityColor(BasicMessage jmsm) {
        IPreferenceStore lvpp = new JmsLogPreferencePage().getPreferenceStore();
        //
        // if we connect to the ALARM topic - we get alarms
        // we do not have to check for the type!
        // if ((jmsm.getProperty("TYPE").equalsIgnoreCase("Alarm"))) {
        Color backgroundColor = null;
        if ((jmsm.getProperty("SEVERITY_KEY").equals(lvpp
                .getString(JmsLogPreferenceConstants.KEY0)))) {
            StringTokenizer st = new StringTokenizer(lvpp
                    .getString(JmsLogPreferenceConstants.COLOR0), ",");
            backgroundColor = new Color(null, Integer.parseInt(st.nextToken()),
                    Integer.parseInt(st.nextToken()), Integer.parseInt(st
                            .nextToken()));
        } else if ((jmsm.getProperty("SEVERITY_KEY").equals(lvpp
                .getString(JmsLogPreferenceConstants.KEY1)))) {
            StringTokenizer st = new StringTokenizer(lvpp
                    .getString(JmsLogPreferenceConstants.COLOR1), ",");
            backgroundColor = new Color(null, Integer.parseInt(st.nextToken()),
                    Integer.parseInt(st.nextToken()), Integer.parseInt(st
                            .nextToken()));
        } else if ((jmsm.getProperty("SEVERITY_KEY").equals(lvpp
                .getString(JmsLogPreferenceConstants.KEY2)))) {
            StringTokenizer st = new StringTokenizer(lvpp
                    .getString(JmsLogPreferenceConstants.COLOR2), ",");
            backgroundColor = new Color(null, Integer.parseInt(st.nextToken()),
                    Integer.parseInt(st.nextToken()), Integer.parseInt(st
                            .nextToken()));
        } else if ((jmsm.getProperty("SEVERITY_KEY").equals(lvpp
                .getString(JmsLogPreferenceConstants.KEY3)))) {
            StringTokenizer st = new StringTokenizer(lvpp
                    .getString(JmsLogPreferenceConstants.COLOR3), ",");
            backgroundColor = new Color(null, Integer.parseInt(st.nextToken()),
                    Integer.parseInt(st.nextToken()), Integer.parseInt(st
                            .nextToken()));
        } else if ((jmsm.getProperty("SEVERITY_KEY").equals(lvpp
                .getString(JmsLogPreferenceConstants.KEY4)))) {
            StringTokenizer st = new StringTokenizer(lvpp
                    .getString(JmsLogPreferenceConstants.COLOR4), ",");
            backgroundColor = new Color(null, Integer.parseInt(st.nextToken()),
                    Integer.parseInt(st.nextToken()), Integer.parseInt(st
                            .nextToken()));
        } else if ((jmsm.getProperty("SEVERITY_KEY").equals(lvpp
                .getString(JmsLogPreferenceConstants.KEY5)))) {
            StringTokenizer st = new StringTokenizer(lvpp
                    .getString(JmsLogPreferenceConstants.COLOR5), ",");
            backgroundColor = new Color(null, Integer.parseInt(st.nextToken()),
                    Integer.parseInt(st.nextToken()), Integer.parseInt(st
                            .nextToken()));
        } else if ((jmsm.getProperty("SEVERITY_KEY").equals(lvpp
                .getString(JmsLogPreferenceConstants.KEY6)))) {
            StringTokenizer st = new StringTokenizer(lvpp
                    .getString(JmsLogPreferenceConstants.COLOR6), ",");
            backgroundColor = new Color(null, Integer.parseInt(st.nextToken()),
                    Integer.parseInt(st.nextToken()), Integer.parseInt(st
                            .nextToken()));
        } else if ((jmsm.getProperty("SEVERITY_KEY").equals(lvpp
                .getString(JmsLogPreferenceConstants.KEY7)))) {
            StringTokenizer st = new StringTokenizer(lvpp
                    .getString(JmsLogPreferenceConstants.COLOR7), ",");
            backgroundColor = new Color(null, Integer.parseInt(st.nextToken()),
                    Integer.parseInt(st.nextToken()), Integer.parseInt(st
                            .nextToken()));
        } else if ((jmsm.getProperty("SEVERITY_KEY").equals(lvpp
                .getString(JmsLogPreferenceConstants.KEY8)))) {
            StringTokenizer st = new StringTokenizer(lvpp
                    .getString(JmsLogPreferenceConstants.COLOR8), ",");
            backgroundColor = new Color(null, Integer.parseInt(st.nextToken()),
                    Integer.parseInt(st.nextToken()), Integer.parseInt(st
                            .nextToken()));
        } else if ((jmsm.getProperty("SEVERITY_KEY").equals(lvpp
                .getString(JmsLogPreferenceConstants.KEY9)))) {
            StringTokenizer st = new StringTokenizer(lvpp
                    .getString(JmsLogPreferenceConstants.COLOR9), ",");
            backgroundColor = new Color(null, Integer.parseInt(st.nextToken()),
                    Integer.parseInt(st.nextToken()), Integer.parseInt(st
                            .nextToken()));
        }
        return backgroundColor;
    }

    public Color getForeground(Object element, int columnIndex) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * To set new configuration of column names. The View classes are registered
     * to the Property listener and must change the columnName Array in the
     * LabelProvider, too!
     * 
     * @param _columnNames
     */
    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

}
