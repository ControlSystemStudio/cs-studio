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
package org.csstudio.utility.ldapUpdater.files;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.domain.desy.time.TimeInstant;

/**
 * Model for the contents of the file that contains the time spans of the last
 * processing of an IOC file.
 *
 * @author bknerr
 * @deprecated 'Last modification' information shall be replaced by a time stamp per IOC sticky in
 * the LDAP persistence layer.
 */
@Deprecated
public final class HistoryFileContentModel {

	private final Map<String, TimeInstant> _historyMap = new HashMap<String, TimeInstant>();

	/**
	 * Constructor.
	 */
	public HistoryFileContentModel() {
		// Empty
	}

	/**
	 * Setter for an entry.
	 * @param name file name
	 * @param timestamp time stamp of last processing
	 */
	public void setEntry(@Nonnull final String name, @Nonnull final TimeInstant timestamp) {
		_historyMap.put(name, timestamp);
	}

	/**
	 * Getter of the entry set.
	 * @return the entry set
	 */
	@Nonnull
	public Set<Entry<String, TimeInstant>> getEntrySet() {
		return _historyMap.entrySet();
	}

	/**
	 * Retrieves the timestamp for a record
	 * @param record the record
	 * @return the timestamp
	 */
	@CheckForNull
	public TimeInstant getTimeForRecord(@Nonnull final String record) {
		return _historyMap.get(record);
	}

	/**
	 * Delegator if set contains the ioc entry
	 * @param iocName the ioc
	 * @return true if the set contains an entry for this ioc
	 */
	public boolean contains(@Nonnull final String iocName) {
		return _historyMap.containsKey(iocName);
	}

	/**
	 * Yields a copy of the set of all contained ioc names.
	 * @return the copied set of ioc names
	 */
	@Nonnull
	public Set<String> getIOCNameKeys() {
		return new HashSet<String>(_historyMap.keySet());
	}
}
