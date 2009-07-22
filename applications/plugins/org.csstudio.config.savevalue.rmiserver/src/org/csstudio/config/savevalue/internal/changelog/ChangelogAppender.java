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

import java.io.IOException;
import java.io.Writer;

import org.csstudio.config.savevalue.service.ChangelogEntry;

/**
 * Appends changelog entries to a changelog file.
 * 
 * @author Joerg Rathlev
 */
public class ChangelogAppender {

	private final Writer _writer;

	/**
	 * Creates a changelog appender that writes changelog entries to the
	 * provided writer.
	 * 
	 * @param writer
	 *            a writer.
	 */
	public ChangelogAppender(Writer writer) {
		if (writer == null) {
			throw new IllegalArgumentException("writer was null");
		}
		_writer = writer;
	}

	/**
	 * Appends the entry to this appender's output.
	 * 
	 * @param entry
	 *            a changelog entry.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public void append(ChangelogEntry entry) throws IOException {
		_writer.append(ChangelogFileFormat.serialize(entry));
	}

	/**
	 * Closes this appender and its writer.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public void close() throws IOException {
		_writer.close();
	}
}
