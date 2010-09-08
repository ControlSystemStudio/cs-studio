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

package org.csstudio.diag.interconnectionServer.internal.management;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.csstudio.diag.interconnectionServer.server.IocConnection;
import org.csstudio.diag.interconnectionServer.server.IocConnectionManager;
import org.csstudio.platform.management.CommandParameterEnumValue;
import org.csstudio.platform.management.IDynamicParameterValues;

/**
 * Enumeration of IOCs for the management commands.
 *
 * @author Joerg Rathlev
 */
public class IocEnumeration implements IDynamicParameterValues {

	/**
	 * {@inheritDoc}
	 */
	public CommandParameterEnumValue[] getEnumerationValues() {
		final Collection<IocConnection> iocs =
			IocConnectionManager.INSTANCE.getIocConnections();
		final List<CommandParameterEnumValue> result =
			new ArrayList<CommandParameterEnumValue>(iocs.size());
		for (final IocConnection ioc : iocs) {
			final String hostname = ioc.getHost();
			final String logicalName = ioc.getLogicalIocName();
			final String label = logicalName + " (" + hostname + ")";
			result.add(new CommandParameterEnumValue(hostname, label));
		}
		return result.toArray(new CommandParameterEnumValue[result.size()]);
	}

}
