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

package org.csstudio.config.savevalue.ui.changelogview;


import org.csstudio.config.savevalue.service.ChangelogEntry;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.PlatformObject;

/**
 * Creates adapters that adapt a <code>ChangelogEntry</code> to an
 * {@link IProcessVariable}.
 * 
 * @author Joerg Rathlev
 */
public class ChangelogEntryProcessVariableAdapterFactory implements
		IAdapterFactory {

	/**
	 * Adapter class which provides an <code>IProcessVariable</code>
	 * implementation for a <code>ChangelogEntry</code>.
	 */
	private static class ChangelogEntryProcessVariableAdapter extends
			PlatformObject {// implements IProcessVariable {
//		TODO jhatje: implement new datatypes
		private final ChangelogEntry _changelogEntry;
		
		ChangelogEntryProcessVariableAdapter(ChangelogEntry entry) {
			_changelogEntry = entry;
		}

		/**
		 * {@inheritDoc}
		 */
		public String getName() {
			return _changelogEntry.getPvName();
		}

		/**
		 * {@inheritDoc}
		 */
		public String getTypeId() {
//			TODO jhatje: implement new datatypes
//			return IProcessVariable.TYPE_ID;
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getAdapter(Object adaptableObject,
			@SuppressWarnings("unchecked") Class adapterType) {
		if (adaptableObject instanceof ChangelogEntry) {
			return new ChangelogEntryProcessVariableAdapter(
					(ChangelogEntry) adaptableObject);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {
//		TODO jhatje: implement new datatypes
//		return new Class[] { IProcessVariable.class };
		return null;
	}
}
