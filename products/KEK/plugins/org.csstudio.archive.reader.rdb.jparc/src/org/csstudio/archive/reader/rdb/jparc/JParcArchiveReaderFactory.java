/*******************************************************************************
 * Copyright (c) 2011 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.archive.reader.rdb.jparc;

import org.csstudio.archive.reader.ArchiveReader;
import org.csstudio.archive.reader.ArchiveReaderFactory;

/** {@link ArchiveReaderFactory} for JPARC archive
 * 
 *  @author Kay Kasemir
 */
public class JParcArchiveReaderFactory implements ArchiveReaderFactory
{
	/** Prefix in archive URLs that is used to select the JPARC
	 *  implementation.
	 *  Registered in plugin.xml for this class.
	 */
	final public static String PREFIX = "jparc";
	
	@Override
	public ArchiveReader getArchiveReader(final String url) throws Exception
	{
		// TODO Add preferences?
//        final String user = Preferences.getUser();
//        final String password = Preferences.getPassword();
//        final String schema = Preferences.getSchema();
		final String user = "archive";
		final String password = "$archive";
		return new JParcArchiveReader(url, user, password);
	}
}
