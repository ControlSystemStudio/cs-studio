/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.sns;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import oracle.jdbc.OracleTypes;

import org.csstudio.platform.utility.rdb.RDBUtil;

/** SNS logbook support
 *  @author Delphy Nypaver Armstrong - Original version
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class SNSLogbookSupport
{
    /** Maximum allowed size for text entry */
	final private int MAX_TEXT_SIZE;
	private static final String DEFAULT_BADGE_NUMBER = "999992"; //$NON-NLS-1$
	final private RDBUtil rdb;
	final String badge_number;

	/** Initialize
	 *  @param url 	RDB URL
	 *  @param user User (for which we'll try to get the badge number)
	 *  @param password Password
	 *  @throws Exception on error
	 */
	public SNSLogbookSupport(final String url, final String user, final String password)
	        throws Exception
	{
	    this.rdb = RDBUtil.connect(url, user, password, false);
		badge_number = getBadgeNumber(user);
		MAX_TEXT_SIZE = getMaxContentLength();
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
	            if (badge.length() > 1)
	                return badge;
	        }
	        // No error, but also not found: fall through
	    }
	    finally
	    {
	        statement.close();
	    }
	    return DEFAULT_BADGE_NUMBER;
	}

    /** Query the RDB for the specified Content length.
     * @throws Exception
     * @return Content length specified in the RDB
     */
    private int getMaxContentLength() throws Exception
    {
        final ResultSet tables = rdb.getConnection().getMetaData()
                .getColumns(null, "LOGBOOK", "LOG_ENTRY", "CONTENT");
        if (!tables.next())
            throw new Exception("Unable to locate LOGBOOK.LOG_ENTRY.CONTENT");
        final int max_elog_text = tables.getInt("COLUMN_SIZE");
        return max_elog_text;
    }
	
	/** Create entry
	 *  @param logbook
	 *  @param title
	 *  @param text
	 *  @param filenames
	 *  @param captions
	 *  @throws Exception
     *  @return Entry ID
	 */
	public int createEntry(final String logbook, final String title, final String text) throws Exception
    {
		final int entry_id; // Entry ID from RDB

		if (text.length() < MAX_TEXT_SIZE)
		{
			// Short text goes into entry
			entry_id = createBasicEntry(logbook, title, text);
		}
		else
		{
			// If text is made into an attachment due to size restraints,
			// explain why there is an attachment
			final String info = "Input text exceeds " + MAX_TEXT_SIZE
			        + " characters, see attachment.";
			entry_id = createBasicEntry(logbook, title, info);

			// Attach text
			final InputStream stream = new ByteArrayInputStream(text.getBytes());
			// Add the text attachment to the elog
			addAttachment(entry_id, false, "FullEntry.txt", "Full Text", stream);
			stream.close();
		}
		return entry_id;
	}

	/** Create basic ELog entry with title and text, obtaining entry ID which
	 *  would allow addition of attachments.
	 *
	 *  @param logbook Logbook
	 *  @param title title of the elog entry
	 *  @param text text for the elog entry
	 *  @throws Exception on error
	 */
	private int createBasicEntry(final String logbook, final String title, final String text)
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
	 *  @param entry_id ID of entry to which to add this file
	 *  @param is_image Should attachment be treated as image (for display vs. download)?
	 *  @param fname input filename, either an image or a text file
	 *  @param caption Caption or 'title' for the attachment
	 *  @param stream Stream with content of the attachment
     *  @throws Exception on error
     */
	public void addAttachment(final int entry_id, final boolean is_image,
	        final String fname, final String caption,
	        final InputStream stream) throws Exception
	{
	    // Determine file type ID used in RDB for image resp. attachment
	    String fileType = is_image ? "I" : "A";
	    
		// Get the file extension
		final int ndx = fname.lastIndexOf(".");
        String extension;
        long fileTypeID;
        if (ndx > 0)
        {
            extension = fname.substring(ndx + 1);
            fileTypeID = is_image ? fetchImageTypes(extension) : fetchAttachmentTypes(extension);
        }
        else
        {
            extension = "";
            fileTypeID = -1;
        }
		// If the image type cannot be found in the RDB, change its file type to
		// an attachment and look for the
		// extension as an attachment
		if (fileTypeID == -1  &&  is_image)
		{
			fileType = "A";
			fileTypeID = fetchAttachmentTypes(extension);
		}
		if (fileTypeID == -1)
		    throw new Exception("Unsupported file type for '" + fname + "'");

		// Submit to RDB
		final Connection connection = rdb.getConnection();
		final CallableStatement statement = connection.prepareCall(
	        "call logbook.logbook_pkg.add_entry_attachment(?, ?, ?, ?, ?)");
		try
		{
			statement.setInt(1, entry_id);
			statement.setString(2, fileType);
			statement.setString(3, caption);
			statement.setLong(4, fileTypeID);
			statement.setBinaryStream(5, stream);
			statement.executeQuery();
		}
		finally
		{
			statement.close();
		}
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

    public void close()
    {
    	rdb.close();
    }
}
