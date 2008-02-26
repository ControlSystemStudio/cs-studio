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
 package org.csstudio.alarm.table.logTable;

import org.csstudio.alarm.table.dataModel.JMSMessage;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class JMSMessageColumnSorter extends ViewerSorter {

	private String columnName = null;

	private boolean ascending = false;

	private static final String SEVERITY = "SEVERITY"; //$NON-NLS-1$

	public JMSMessageColumnSorter(String colName) {
		super();
		columnName = colName;
	}

	public JMSMessageColumnSorter(String colName, boolean backwards) {
		super();
		columnName = colName;
		ascending = backwards;
	}

	public int compare(Viewer viewer, Object o1, Object o2) {
		JMSMessage jmsm1 = (JMSMessage) o1;
		JMSMessage jmsm2 = (JMSMessage) o2;
		int bw = 1;
		int type = 0;
		int einsInt = 0, zweiInt = 0;
		float einsF = 0, zweiF = 0;
		if (ascending) {
			bw = -1;
		}

		String s1 = jmsm1.getProperty(columnName.toUpperCase());
		String s2 = jmsm2.getProperty(columnName.toUpperCase());
		try {
			einsInt = Integer.parseInt(s1);
			zweiInt = Integer.parseInt(s2);
		} catch (NumberFormatException nfe) {
			type = 1;
		}
		if (type > 0) {
			try {
				einsF = Float.parseFloat(s1);
				zweiF = Float.parseFloat(s2);
			} catch (NumberFormatException nfe) {
				type = 2;
			}
		}

		switch (type) {
		case 0:
			if (einsInt == zweiInt) {
				return bw
						* collator.compare(jmsm1.getProperty(SEVERITY), jmsm2
								.getProperty(SEVERITY));
			} else
				return bw * (einsInt - zweiInt);
		case 1:
			if (einsF == zweiF) {
				return bw
						* collator.compare(jmsm1.getProperty(SEVERITY), jmsm2
								.getProperty(SEVERITY));
			} else
				return (int) (bw * (einsF - zweiF));
		default:
			int comp = collator.compare(s1, s2);
			if (comp == 0) {
				return bw
						* collator.compare(jmsm1.getProperty(SEVERITY), jmsm2
								.getProperty(SEVERITY));
			} else if (s1.length() == 0) {
				return 1;
			} else if (s2.length() == 0) {
				return -1;
			} else {
				return bw * comp;
			}
		}
	}
}