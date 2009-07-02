/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.diag.icsiocmonitor.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A report about the state of the connection to the IOCs as observed by an
 * interconnection server. This is basically a data transfer object to transfer
 * the information from the interconnection server to a monitor application.
 * 
 * @author Joerg Rathlev
 */
public final class IocConnectionReport implements Serializable {

	private static final long serialVersionUID = 2L;
	
	private final String _reportingServer;
	private final List<IocConnectionReportItem> _items;

	/**
	 * Creates a new report.
	 * 
	 * @param server
	 *            the interconnection server which created this report.
	 * @param items
	 *            the report items.
	 * @throws NullPointerException
	 *             if <code>server</code> or <code>items</code> is
	 *             <code>null</code>.
	 */
	public IocConnectionReport(String server, Collection<IocConnectionReportItem> items) {
		if (server == null || items == null) {
			throw new NullPointerException();
		}
		
		_reportingServer = server;
		_items = new ArrayList<IocConnectionReportItem>(items);
	}

	/**
	 * Returns the name of the interconnection server which created this report.
	 * 
	 * @return the name of the interconnection server.
	 */
	public String getReportingServer() {
		return _reportingServer;
	}
	
	/**
	 * Returns the items of this report.
	 * 
	 * @return an unmodifiable list containing the items of this report.
	 */
	public List<IocConnectionReportItem> getItems() {
		return Collections.unmodifiableList(_items);
	}
}
