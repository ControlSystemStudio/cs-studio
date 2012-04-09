/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.sns;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import oracle.jdbc.OracleTypes;
import oracle.sql.BLOB;

import org.csstudio.logbook.ILogbook;
import org.csstudio.platform.utility.rdb.RDBUtil;

/** SNS logbook
 *
 *  @author Delphy Nypaver Armstrong
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SNSLogbook implements ILogbook
{
	final private int MAX_TEXT_SIZE;
	private static final String DEFAULT_BADGE_NUMBER = "999992"; //$NON-NLS-1$
	final private RDBUtil rdb;
	final private String logbook;
	final String badge_number;

	/** Constructor
	 *  @param rdb 	RDB connection
	 *  @param user User (for which we'll try to get the badge number)
	 *  @param logbook SNS logbook to use
	 *  @throws Exception on error
	 */
	public SNSLogbook(final RDBUtil rdb, final String user, final String logbook)
	        throws Exception
	{
		this.rdb = rdb;
		this.logbook = logbook;
		badge_number = getBadgeNumber(user);
		/* The maximum allowed size of logbook text entry */
		MAX_TEXT_SIZE = getContentLength();
	}

	/** {@inheritDoc} */
	@Override
	public void createEntry(final String title, final String text,
	        final String... filenames) throws Exception
    {
		final String[] captions = new String[filenames.length];
		for (int i=0; i<captions.length; ++i)
			captions[i] = new File(filenames[i]).getName();
		createEntry(title, text, filenames, captions);
    }

	/** {@inheritDoc} */
	@Override
	public void createEntry(final String title, final String text,
	        final String[] filenames, final String[] captions) throws Exception
    {
		final int entry_id; // Entry ID from RDB

		if (text.length() < MAX_TEXT_SIZE)
		{
			// Short text goes into entry
			entry_id = createBasicEntry(title, text);
		}
		else
		{
			// If text is made into an attachment due to size restraints,
			// replace the
			// text that is now an attachment with text to explain why there is
			// an
			// attachment
			final String info = "Input text exceeds " + MAX_TEXT_SIZE
			        + " characters, see attachment.";
			entry_id = createBasicEntry(title, info);

			// Get a name for the created text file
			final File tmp_file = File.createTempFile("Logbook", ".txt");
			final String fname = tmp_file.getAbsolutePath();

			// Store the oversized text entry in the text file
			setContents(tmp_file, text);
			// Add the text attachment to the elog
			addFileToElog(fname, "A", entry_id, "Full Text");
			tmp_file.deleteOnExit();
		}

		// Attach remaining files
		for (int i=0; i<filenames.length; ++i)
		{
			final String caption = i < captions.length ? captions[i] : filenames[i];
			addFileToElog(filenames[i], "I", entry_id, caption);
		}
	}

	/** Create basic ELog entry with title and text, obtaining entry ID which
	 *  would allow addition of attachments.
	 *
	 *  @param String title title of the elog entry
	 *  @param String text text for the elog entry
	 *  @throws Exception on error
	 */
	private int createBasicEntry(final String title, final String text)
	        throws Exception
	{
		// Initiate the multi-file sql and retrieve the entry_id
		final String mysql = "call logbook.logbook_pkg.insert_logbook_entry"
		        + "(?, ?, ?, ?, ?, ?)";
		final CallableStatement statement = rdb.getConnection().prepareCall(
		        mysql);
		try
		{
			statement.setString(1, badge_number);
			statement.setString(2, logbook);
			statement.setString(3, title);
			statement.setString(4, "");
			statement.setString(5, text);
			statement.registerOutParameter(6, OracleTypes.NUMBER);
			statement.executeQuery();
			final int entry_id = Integer.parseInt(statement.getString(6));
			return entry_id;
		}
		finally
		{
			statement.close();
		}
	}

	/** Determine the type of attachment, based on file extension, and add it
     *  to the elog entry with the entry_id.
     *
     *  @param fname  input filename, either an image or a text file
     *  @param fileType  "I" for image file, "A" for text file
     *  @param entry_id  ID of entry to which to add this file
     *  @param caption   Caption or 'title' for the attachment
     *  @throws Exception
     */
	private void addFileToElog(final String fname, String fileType,
	        final int entry_id, final String caption) throws Exception
	{
		// Get the file extension
		final int ndx = fname.lastIndexOf(".");
		final String extension = fname.substring(ndx + 1);
		long fileTypeID = getFileTypeId(fileType, extension);

		// If the image type cannot be found in the RDB, change its file type to
		// an attachment and look for the
		// extension as an attachment
		if (fileTypeID == -1 && fileType.equals("I"))
		{
			fileType = "A";
			fileTypeID = getFileTypeId(fileType, extension);
		}

		// Initiate the sql to add attachments to the elog
		final String mysql = "call logbook.logbook_pkg.add_entry_attachment"
		        + "(?, ?, ?, ?, ?)";
		final Connection connection = rdb.getConnection();
		final CallableStatement statement = connection.prepareCall(mysql);
		try
		{
			statement.setInt(1, entry_id);
			statement.setString(2, fileType);
			statement.setString(3, caption);
			statement.setLong(4, fileTypeID);
			final File inputFile = new File(fname);

			// Send the image to the sql.
			if (fileType.equals("I"))
			{
				try
				{
					final int file_size = (int) inputFile.length();
					final FileInputStream input_stream = new FileInputStream(
					        inputFile);
					statement.setBinaryStream(5, input_stream, file_size);
					input_stream.close();
				}
				catch (FileNotFoundException e1)
				{
					System.out.println("Could not find " + fname);
					return;
				}
			}
			// Send the text attachment to the sql
			else
			{
				// Create a Blob to store the attachment in.
				final BLOB blob = BLOB.createTemporary(connection, true,
				        BLOB.DURATION_SESSION);
				blob.setBytes(1L, getBytesFromFile(inputFile));
				statement.setBlob(5, blob);
			}
			statement.executeQuery();
		}
		finally
		{
			statement.close();
		}
	}

	/**
     * Returns the contents of the input file in a byte array
     * @param file File this method should read
     * @return byte[] Returns a byte[] array of the contents of the file
     */
	private static byte[] getBytesFromFile(final File file) throws IOException
	{

		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		/*
		 * You cannot create an array using a long type. It needs to be an int
		 * type. Before converting to an int type, check to ensure that file is
		 * not longer than Integer.MAX_VALUE;
		 */
		if (length > Integer.MAX_VALUE)
		{
			return null;
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while ((offset < bytes.length)
		        && ((numRead = is.read(bytes, offset, bytes.length - offset)) >= 0))
		{

			offset += numRead;

		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length)
		{
			throw new IOException("Could not completely read file "
			        + file.getName());
		}

		is.close();
		return bytes;

	}

	/** Ask the RDB for the fileTypeID, based on whether an image or text is to be
     *  attached and the file extension.
     *
     *  @param fileType    an "I" for image, "A" for attachment.
     *  @param extension   extension of file to be attached
     *
     *  @return extensioID from the RDB, -1 if not found
     */
	long getFileTypeId(String fileType, String extension) throws Exception
	{
		if (fileType.equalsIgnoreCase("I"))
			return fetchImageTypes(extension);

		else
			return fetchAttachmentTypes(extension);
	}

	/** Change the contents of text file in its entirety, overwriting any
     *  existing text.
     *
     *  This style of implementation throws all exceptions to the caller.
     *
     *  @param aFile is an existing file which can be written to.
     *  @throws IllegalArgumentException if param does not comply.
     *  @throws FileNotFoundException if the file does not exist.
     *  @throws IOException if problem encountered during write.
     */
	static public void setContents(final File aFile, final String aContents)
	        throws FileNotFoundException, IOException
	{
		if (aFile == null)
		{
			throw new IllegalArgumentException("File should not be null.");
		}
		if (!aFile.exists())
		{
			throw new FileNotFoundException("File does not exist: " + aFile);
		}
		if (!aFile.isFile())
		{
			throw new IllegalArgumentException("Should not be a directory: "
			        + aFile);
		}
		if (!aFile.canWrite())
		{
			throw new IllegalArgumentException("File cannot be written: "
			        + aFile);
		}

		// use buffering
		final Writer output = new BufferedWriter(new FileWriter(aFile));
		try
		{
			// FileWriter always assumes default encoding is OK!
			output.write(aContents);
		}
		finally
		{
			output.close();
		}
	}

	/** Get the badge number for the user in the connection dictionary
	 *
	 *  @param user user id of person logging in.
	 *  @return the badge number for the specified user or a default
	 *  @throws Exception
	 */
	private String getBadgeNumber(final String user) throws Exception
	{
		final PreparedStatement statement = rdb.getConnection()
		        .prepareStatement(
		                "select bn from OPER.EMPLOYEE_V where user_id=?");
		try
		{
			// OPER.EMPLOYEE_V seems to only keep uppercase user_id entries
			statement.setString(1, user.trim().toUpperCase());
			statement.execute();
			final ResultSet result = statement.getResultSet();
			if (result.next())
			{
				final String badge = result.getString("bn");
				if (badge.length() > 1) return badge;
			}
			// No error, but also not found: fall through
		}
		finally
		{
			statement.close();
		}
		return DEFAULT_BADGE_NUMBER;
	}

	@Override
	public void close()
	{
		rdb.close();
	}

	/** Fetch the available image types.
	 *  @param image_type  the extension of the input image file.
	 *  @return image_type_id from the RDB, -1 if not found
	 *  @throws Exception
	 */
	private long fetchImageTypes(final String image_type) throws Exception
	{
		final Statement statement = rdb.getConnection().createStatement();
		try
		{
			final ResultSet result = statement
			        .executeQuery("select * from LOGBOOK.IMAGE_TYPE");

			while (result.next())
			{
				final long ID = result.getLong("image_type_id");
				final String extension = result.getString("file_extension");
				if (image_type.equalsIgnoreCase(extension)) return ID;
			}
		}
		finally
		{
			statement.close();
		}
		return -1;
	}

	/** Fetch the available attachment types.
	 *  @param attachment_type  the extension of the input attachment file.
	 *  @throws SQLException
	 *  @throws Exception
	 *  @return attachment_type_id from the RDB, -1 if not found
	 */
	private long fetchAttachmentTypes(String attachment_type)
	        throws SQLException, Exception
	{
		final Statement statement = rdb.getConnection().createStatement();
		try
		{
			final ResultSet result = statement
			        .executeQuery("select * from LOGBOOK.ATTACHMENT_TYPE");

			while (result.next())
			{
				long ID = result.getLong("attachment_type_id");
				final String extension = result.getString("file_extension");
				if (attachment_type.equalsIgnoreCase(extension)) return ID;
			}
		}
		finally
		{
			statement.close();
		}
		return -1;
	}

	/** Query the RDB for the specified Content length.
	 * @throws Exception
	 * @return Content length specified in the RDB
	 */
	private int getContentLength() throws Exception
	{
		final ResultSet tables = rdb.getConnection().getMetaData()
		        .getColumns(null, "LOGBOOK", "LOG_ENTRY", "CONTENT");
		if (!tables.next())
		    throw new Exception("Unable to locate LOGBOOK.LOG_ENTRY.CONTENT");
		final int max_elog_text = tables.getInt("COLUMN_SIZE");
		return max_elog_text;
	}
}
