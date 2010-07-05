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

import java.util.HashMap;
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
    HashMap<String, Color> _severityColorMapping;

    /**
     * Constructor gets the column names from the table viewer
     *
     * @param colNames
     */
    public MessageTableLabelProvider(final String[] colNames) {
        columnNames = colNames;
        mapSeverityToColor();
    }

    /**
     * Read mapping of severities to colors from preferences and put mapping in
     * a local HashMap. (Performance)
     */
    private void mapSeverityToColor() {
        final IPreferenceStore store = new JmsLogPreferencePage()
                .getPreferenceStore();
        //
        // if we connect to the ALARM topic - we get alarms
        // we do not have to check for the type!
        // if ((jmsm.getProperty("TYPE").equalsIgnoreCase("Alarm"))) {
        _severityColorMapping = new HashMap<String, Color>();

        StringTokenizer st = new StringTokenizer(store
                .getString(JmsLogPreferenceConstants.COLOR0), ",");
        _severityColorMapping.put(store
                .getString(JmsLogPreferenceConstants.KEY0), new Color(null,
                Integer.parseInt(st.nextToken()), Integer.parseInt(st
                        .nextToken()), Integer.parseInt(st.nextToken())));

        st = new StringTokenizer(store
                .getString(JmsLogPreferenceConstants.COLOR1), ",");
        _severityColorMapping.put(store
                .getString(JmsLogPreferenceConstants.KEY1), new Color(null,
                Integer.parseInt(st.nextToken()), Integer.parseInt(st
                        .nextToken()), Integer.parseInt(st.nextToken())));

        st = new StringTokenizer(store
                .getString(JmsLogPreferenceConstants.COLOR2), ",");
        _severityColorMapping.put(store
                .getString(JmsLogPreferenceConstants.KEY2), new Color(null,
                Integer.parseInt(st.nextToken()), Integer.parseInt(st
                        .nextToken()), Integer.parseInt(st.nextToken())));

        st = new StringTokenizer(store
                .getString(JmsLogPreferenceConstants.COLOR3), ",");
        _severityColorMapping.put(store
                .getString(JmsLogPreferenceConstants.KEY3), new Color(null,
                Integer.parseInt(st.nextToken()), Integer.parseInt(st
                        .nextToken()), Integer.parseInt(st.nextToken())));

        st = new StringTokenizer(store
                .getString(JmsLogPreferenceConstants.COLOR3), ",");
        _severityColorMapping.put(store
                .getString(JmsLogPreferenceConstants.KEY3), new Color(null,
                Integer.parseInt(st.nextToken()), Integer.parseInt(st
                        .nextToken()), Integer.parseInt(st.nextToken())));

        st = new StringTokenizer(store
                .getString(JmsLogPreferenceConstants.COLOR4), ",");
        _severityColorMapping.put(store
                .getString(JmsLogPreferenceConstants.KEY4), new Color(null,
                Integer.parseInt(st.nextToken()), Integer.parseInt(st
                        .nextToken()), Integer.parseInt(st.nextToken())));

        st = new StringTokenizer(store
                .getString(JmsLogPreferenceConstants.COLOR5), ",");
        _severityColorMapping.put(store
                .getString(JmsLogPreferenceConstants.KEY5), new Color(null,
                Integer.parseInt(st.nextToken()), Integer.parseInt(st
                        .nextToken()), Integer.parseInt(st.nextToken())));

        st = new StringTokenizer(store
                .getString(JmsLogPreferenceConstants.COLOR6), ",");
        _severityColorMapping.put(store
                .getString(JmsLogPreferenceConstants.KEY6), new Color(null,
                Integer.parseInt(st.nextToken()), Integer.parseInt(st
                        .nextToken()), Integer.parseInt(st.nextToken())));

        st = new StringTokenizer(store
                .getString(JmsLogPreferenceConstants.COLOR7), ",");
        _severityColorMapping.put(store
                .getString(JmsLogPreferenceConstants.KEY7), new Color(null,
                Integer.parseInt(st.nextToken()), Integer.parseInt(st
                        .nextToken()), Integer.parseInt(st.nextToken())));

        st = new StringTokenizer(store
                .getString(JmsLogPreferenceConstants.COLOR8), ",");
        _severityColorMapping.put(store
                .getString(JmsLogPreferenceConstants.KEY8), new Color(null,
                Integer.parseInt(st.nextToken()), Integer.parseInt(st
                        .nextToken()), Integer.parseInt(st.nextToken())));

        st = new StringTokenizer(store
                .getString(JmsLogPreferenceConstants.COLOR9), ",");
        _severityColorMapping.put(store
                .getString(JmsLogPreferenceConstants.KEY9), new Color(null,
                Integer.parseInt(st.nextToken()), Integer.parseInt(st
                        .nextToken()), Integer.parseInt(st.nextToken())));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang
     * .Object, int)
     */
    public Image getColumnImage(final Object element, final int columnIndex) {
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
    public String getColumnText(final Object element, final int index) {

        if ((index >= columnNames.length) || (index < 0)) {
            throw new IndexOutOfBoundsException("Not enough columnNames in message table for this index.");
        }
        if (columnNames[index] == null) {
            return "N/A";
        }
        final BasicMessage jmsm = (BasicMessage) element;
        final String prop = jmsm.getProperty(columnNames[index].toUpperCase());
        return prop != null ? prop : "N/A";
    }

    public String getColumnName(final int index) {
        return columnNames[index].toUpperCase();
    }

    /**
     * Check the severity of the current message (element) and return the color
     * defined in the preference pages.
     *
     * @return color for the current field
     */
    public Color getBackground(final Object element, final int columnIndex) {
        final BasicMessage jmsm = (BasicMessage) element;
        final Color backgroundColor = readSeverityColor(jmsm);
        return backgroundColor;
    }

    Color readSeverityColor(final BasicMessage jmsm) {
        return _severityColorMapping.get(jmsm.getProperty("SEVERITY_KEY"));
    }

    public Color getForeground(final Object element, final int columnIndex) {
        return null;
    }

    /**
     * To set new configuration of column names. The View classes are registered
     * to the Property listener and must change the columnName Array in the
     * LabelProvider, too!
     *
     * @param _columnNames
     */
    public void setColumnNames(final String[] columnNames) {
        this.columnNames = columnNames;
    }

}
