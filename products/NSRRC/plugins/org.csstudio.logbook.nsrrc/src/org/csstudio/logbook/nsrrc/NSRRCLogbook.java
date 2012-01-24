package org.csstudio.logbook.nsrrc;
/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Date;

import org.csstudio.logbook.ILogbook;

/** (Demo) of an NSRRC logbook
 *  
 *  <p>Simply writes all logbook entries to a file.
 *  
 *  <p>Ignores attachments, only logs their file name.
 *  
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class NSRRCLogbook implements ILogbook
{
	final public static String LOGBOOK_FILE = "/tmp/logbook.txt";
    final private String logbook;

	public NSRRCLogbook(final String logbook)
	{
		this.logbook = logbook;
	}

    @Override
	public void createEntry(final String title, final String text, final String... file_names)
			throws Exception
	{
	    final PrintWriter out = new PrintWriter(new FileOutputStream(LOGBOOK_FILE, true));
		try
		{
			out.println("Logbook: " + logbook);
			out.println("Date   : " + new Date());
			out.println("Title  : " + title);
			out.println(text);
			for (String file : file_names)
				out.println("Attach : " + file);
			out.println("-----------------------------");
		}
		finally
		{
			out.close();
		}
	}

	@Override
	public void createEntry(final String title, final String text, final String[] filenames,
			final String[] captions) throws Exception
	{
		createEntry(title, text, filenames);
	}

	@Override
	public void close()
	{
	    // NOP
	}
}
