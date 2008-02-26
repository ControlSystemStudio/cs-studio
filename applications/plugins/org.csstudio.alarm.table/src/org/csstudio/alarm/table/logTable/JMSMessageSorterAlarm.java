/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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
 /**
 * 
 */
package org.csstudio.alarm.table.logTable;

import org.csstudio.alarm.table.dataModel.JMSMessage;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.csstudio.alarm.table.dataModel.JMSMessage;


/**
 * Sorter for alarm messages. Insert messages with the highest
 * severity at the top and if two messages have the same serverity
 * the newest at the top.
 * 
 * @author jhatje
 *
 */
public class JMSMessageSorterAlarm extends ViewerSorter {

	public int compare(Viewer viewer, Object o1, Object o2) {

		JMSMessage jmsm1;
		JMSMessage jmsm2;
		
		if ((o1 != null) && (o2 != null)) {
			jmsm1 = (JMSMessage) o2;
			jmsm2 = (JMSMessage) o1;
		} else {
			return 0;
		}
			
		Integer one;
		try {
			one = new Integer(jmsm1.getSeverityNumber());
		} catch (NumberFormatException e) {
			one = new Integer(-1);
		}

		Integer two;
		try {
			two = new Integer(jmsm2.getSeverityNumber());
//			two = JMSMessage.severityToNumber(jmsm2.getProperty("SEVERITY"));
		} catch (NumberFormatException e) {
			two = new Integer(-1);
		}
		
		//same severity -> newest message at the top
		if (two == one) {
			return (collator.compare(jmsm2.getProperty("EVENTTIME"), jmsm1.getProperty("EVENTTIME")));
		}
		
		return (two - one);
	}
}
