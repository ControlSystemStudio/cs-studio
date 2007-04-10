/**
 * 
 */
package org.csstudio.alarm.table.logTable;

import org.csstudio.alarm.table.dataModel.JMSMessage;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.csstudio.alarm.table.dataModel.JMSMessage;


/**
 * @author jhatje
 *
 */
public class JMSMessageSorter extends ViewerSorter {

	public int compare(Viewer viewer, Object o1, Object o2) {

		JMSMessage jmsm1 = (JMSMessage) o1;
		JMSMessage jmsm2 = (JMSMessage) o2;
		
		Integer one;
		try {
			//
			// Matthias 05-04-2007 replaced
			// one = new Integer(jmsm1.getProperty("SEVERITY_NUMBER"));
			one = JMSMessage.severityToNumber(jmsm1.getProperty("SEVERITY"));
		} catch (NumberFormatException e) {
			one = new Integer(-1);
		}

		Integer two;
		try {
			two = JMSMessage.severityToNumber(jmsm2.getProperty("SEVERITY"));
		} catch (NumberFormatException e) {
			two = new Integer(-1);
		}
		
		return (two - one);
	}
}
