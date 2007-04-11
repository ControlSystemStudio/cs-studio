/**
 * 
 */
package org.csstudio.alarm.table.logTable;

import org.csstudio.alarm.table.dataModel.JMSMessage;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.csstudio.alarm.table.dataModel.JMSMessage;


/**
 * Sorter for log table viewer. Insert newest message at the top.
 * 
 * @author jhatje
 */
public class JMSMessageSorterLog extends ViewerSorter {

	public int compare(Viewer viewer, Object o1, Object o2) {

		JMSMessage jmsm1 = (JMSMessage) o1;
		JMSMessage jmsm2 = (JMSMessage) o2;
				
		return (collator.compare(jmsm2.getProperty("EVENTTIME"), jmsm1.getProperty("EVENTTIME")));
	}
}
