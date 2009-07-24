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

package org.csstudio.config.savevalue.internal.changelog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.csstudio.config.savevalue.service.ChangelogEntry;

/**
 * Reads a changelog file.
 * 
 * @author Joerg Rathlev
 */
public class ChangelogReader {
	
	private final BufferedReader _reader;

	/**
	 * Creates a new changelog reader which will read from the provided reader.
	 * 
	 * @param reader
	 *            a reader.
	 */
	public ChangelogReader(Reader reader) {
		if (reader == null) {
			throw new IllegalArgumentException("reader was null");
		}
		_reader = new BufferedReader(reader);
	}

	/**
	 * Reads the changelog entries from this reader's input.
	 * 
	 * @return the entries.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public Collection<ChangelogEntry> readEntries() throws IOException {
		// Entries are stored in a map from pv -> entry. The map is
		// used to see if there already is an entry for a given pv,
		// and update the entry if a newer one is read later.
		Map<String, ChangelogEntry> entries =
			new HashMap<String, ChangelogEntry>();
		
		String line;
		while ((line = _reader.readLine()) != null) {
			try {
				ChangelogEntry entry = ChangelogEntrySerializer.deserialize(line);
				entries.put(entry.getPvName(), entry);
			} catch (IllegalArgumentException e) {
				throw new IOException("Error reading the following line:\n"
						+ line + "\n" + e.getMessage());
			}
		}
		return entries.values();
	}
	
	/**
	 * Closes this reader.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public void close() throws IOException {
		_reader.close();
	}

}
