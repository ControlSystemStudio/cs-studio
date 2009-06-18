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
 package org.csstudio.alarm.table.ui.messagetable;

import org.csstudio.alarm.table.dataModel.BasicMessage;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

public class MessageTableColumnSorter extends ViewerComparator {

	private String _columnName = null;

	private boolean _ascending = false;

    private TableViewer _tableViewer = null;

	public MessageTableColumnSorter(TableViewer tableViewer, String colName, boolean backwards) {
		super();
        _tableViewer = tableViewer;
		_columnName = colName;
		_ascending = backwards;
	}

	public int compare(Viewer viewer, Object o1, Object o2) {
		
	    BasicMessage jmsm1 = (BasicMessage) o1;
		BasicMessage jmsm2 = (BasicMessage) o2;
		int ascendingInt = 1;
		if (_ascending) {
			ascendingInt = -1;
		}

		String property1 = jmsm1.getProperty(_columnName.toUpperCase());
		String property2 = jmsm2.getProperty(_columnName.toUpperCase());
		int result = super.compare(_tableViewer, property1, property2);
		if (result == 0) {
		    String eventtime1 = jmsm1.getProperty("EVENTTIME");
		    String eventtime2 = jmsm2.getProperty("EVENTTIME");
		    result = super.compare(_tableViewer, eventtime1, eventtime2);
		}
		int resultOrder = ascendingInt * result;
        return resultOrder;
	}
}