/**
 * 
 */
package org.csstudio.alarm.table.logTable;

import org.csstudio.alarm.table.dataModel.JMSMessage;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;


/**
 * @author jhatje
 *
 */
public class JMSMessageSorter extends ViewerSorter {

	public int compare(Viewer viewer, Object o1, Object o2) {

		JMSMessage jmsm1 = (JMSMessage) o1;
		JMSMessage jmsm2 = (JMSMessage) o2;

//		Integer one = new Integer(jmsm1.getSeverityNumber());
//		Integer two = new Integer(jmsm2.getSeverityNumber());
		
		Integer one;
		try {
			one = new Integer(jmsm1.getProperty("SEVERITY_NUMBER"));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			one = new Integer(-1);
		}
		
		
		Integer two;
		try {
			two = new Integer(jmsm2.getProperty("SEVERITY_NUMBER"));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			two = new Integer(-1);
		}
		
		
		return (two - one);
//		
//		if (jmsm1.getSeverity())
//		
//		switch (criteria) {
//			case DESCRIPTION :
//				return compareDescriptions(task1, task2);
//			case OWNER :
//				return compareOwners(task1, task2);
//			case PERCENT_COMPLETE :
//				return comparePercentComplete(task1, task2);
//			default:
//				return 0;
//		}
	}
	
}
