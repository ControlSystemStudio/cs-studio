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

package org.csstudio.alarm.table;

//import org.csstudio.alarm.table.logTable.JMSLogTableViewer;
//import org.csstudio.alarm.table.preferences.ArchiveViewPreferenceConstants;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;

/**
 * Implementation of <code>IPropertyChangeListener</code> to actualise the
 * TableViewer in case of changes in the column settings.
 *
 * @author jhatje
 *
 */
public class ColumnPropertyChangeListener implements IPropertyChangeListener {

	/** String for the columns in the preference page. **/
	private String _pString;

//	/** TableViewer for actualisation. */
//	private JMSLogTableViewer _jlv;
//
//
//	public ColumnPropertyChangeListener(String p_string, JMSLogTableViewer jlv) {
//		this._pString = p_string;
//		this._jlv = jlv;
//	}

	/**
	 * Get new _columnNames-String from preference page,
	 * set new order, add new columns, delete old columns.
	 */
	@Override
    public void propertyChange(final PropertyChangeEvent event) {

//		JmsLogsPlugin.logInfo("Column settings changed");
//
//		String [] _columnNames = JmsLogsPlugin
//				.getDefault()
//				.getPluginPreferences()
//				.getString(p_string)
//				.split(";"); //$NON-NLS-1$
////		jlv.setColumnNames(_columnNames);
//
//		Table t = jlv.getTable();
//		TableColumn[] tc = t.getColumns();
//
//		for (TableColumn column : tc) {
//			column.dispose();
//		}
//
//		for (String columnName : _columnNames) {
//			TableColumn tableColumn = new TableColumn(t, SWT.CENTER);
//			tableColumn.setText(columnName);
//			tableColumn.setWidth(100);
//		}
//
//
//
////		int diff = _columnNames.length - tc.length;
////
////		if (diff > 0) {
////			for (int i = 0; i < diff; i++) {
////				TableColumn tableColumn = new TableColumn(t, SWT.CENTER);
////				tableColumn.setText(new Integer(i).toString());
////				tableColumn.setWidth(100);
////			}
////		} else if (diff < 0) {
////			diff = (-1) * diff;
////			for (int i = 0; i < diff; i++) {
////				tc[i].dispose();
////			}
////		}
////		tc = t.getColumns();
////
////		for (int i = 0; i < tc.length; i++) {
////			tc[i].setText(_columnNames[i]);
////		}
//		jlv.refresh(true);

	}

}
