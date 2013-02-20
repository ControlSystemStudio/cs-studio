/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.logbook.sns.elog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import oracle.jdbc.OracleTypes;

import org.csstudio.logbook.sns.StreamHelper;
import org.csstudio.platform.utility.rdb.RDBUtil;

/** SNS 'ELog' support
 *  @author Delphy Nypaver Armstrong - Original version
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class ELog
{
    final private RDBUtil rdb;

    /** Maximum allowed size for text entry */
	final private int MAX_TEXT_SIZE;
	
	private static final String DEFAULT_BADGE_NUMBER = "999992"; //$NON-NLS-1$
	final private String badge_number;
	
	private static List<ELogbook> logbooks = null;
	
	private static List<ELogCategory> categories = null;

	/** Initialize
	 *  @param url 	RDB URL
	 *  @param user User (for which we'll try to get the badge number)
	 *  @param password Password
	 *  @throws Exception on error
	 */
	public ELog(final String url, final String user, final String password)
	        throws Exception
	{
	    this.rdb = RDBUtil.connect(url, user, password, false);
		badge_number = getBadgeNumber(user);
		MAX_TEXT_SIZE = getMaxContentLength();
		// Only initialize logbooks and categories once per JVM
		synchronized (ELog.class)
        {
            if (logbooks == null)
                logbooks = readLogbooks();
            if (categories == null)
                categories = readCategories();
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
	    try
	    (
            final PreparedStatement statement = rdb.getConnection().prepareStatement(
               "SELECT bn FROM OPER.EMPLOYEE_V WHERE user_id=?");
	    )
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
    
    /** Read available logbooks from RDB
     *  @return Available logbooks
     *  @throws Exception on error
     */
    private List<ELogbook> readLogbooks() throws Exception
    {
        final List<ELogbook> logbooks = new ArrayList<>();
        
        try
        (
            final PreparedStatement statement = rdb.getConnection().prepareStatement(
                "SELECT logbook_nm, logbook_id FROM LOGBOOK.logbook_v");
        )
        {
            final ResultSet result = statement.executeQuery();
            while (result.next())
                logbooks.add(new ELogbook(result.getString(1), result.getString(2)));
        }
        return logbooks;
    }
    
    /** Locate logbook by name
     *  @param name Name of logbook
     *  @return {@link ELogbook}
     *  @throws Exception if no known logbook for that name
     */
    private ELogbook findLogbook(final String name) throws Exception
    {
        for (ELogbook logbook : logbooks)
            if (logbook.getName().equals(name))
                return logbook;
        throw new Exception("Unknown logbook '" + name + "'");
    }

    /** List available logbooks
     *  @return List of known logbooks
     */
    public List<String> getLogbooks()
    {
        final List<String> books = new ArrayList<>(logbooks.size());
        for (ELogbook logbook : logbooks)
            books.add(logbook.getName());
        return books;
    }

    /** Read available logbook categories from RDB
     *  @return Available categories
     *  @throws Exception on error
     */
    private List<ELogCategory> readCategories() throws Exception
    {
        final List<ELogCategory> tags = new ArrayList<>();
        try
        (
            final Statement statement = rdb.getConnection().createStatement();
        )
        {
            final ResultSet result = statement.executeQuery(
                    "SELECT cat_id, cat_nm FROM logbook.log_categories_v");
            while (result.next())
                tags.add(new ELogCategory(result.getString(1), result.getString(2)));
        }
        return tags;
    }

    /** @return supported categories */
    public List<ELogCategory> getCategories()
    {
        return categories;
    }
    
    /** Read logbook entry
     *  @param entry_id Log entry ID
     *  @return {@link ELogEntry}
     *  @throws Exception on error
     */
    public ELogEntry getEntry(final long entry_id) throws Exception
    {
        // Get user, title, text
        final String user;
        final Date date;
        final String title;
        final String text;
        try
        (
            final PreparedStatement statement = rdb.getConnection().prepareStatement(
                "SELECT e.log_entry_id, d.pref_first_nm, d.pref_last_nm," +
                "  e.orig_post, e.title, e.content " +
                " FROM LOGBOOK.log_entry e" +
                " LEFT JOIN oper.employee_v d ON d.bn = e.bn" +
                " WHERE (e.pub_stat_id = 'P' OR e.pub_stat_id IS NULL)" +
                " AND e.log_entry_id = ?");
        )
        {
            statement.setLong(1, entry_id);
            final ResultSet result = statement.executeQuery();
            if (! result.next())
                return null;
            user = result.getString(2) + " " + result.getString(3);
            date = result.getDate(4);
            title = result.getString(5);
            text = result.getString(6);
        }

        final List<String> logbooks = getLogbooks(entry_id);
        final List<ELogCategory> categories = getCategories(entry_id);
        
        // Get attachments
        final List<ELogAttachment> images = getImageAttachments(entry_id);
        final List<ELogAttachment> attachments = getOtherAttachments(entry_id);
        
        // Return entry        
        return new ELogEntry(user, date, title, text, logbooks, categories, images, attachments);
    }
    
    /** @param entry_id Log entry ID
     *  @return Names of logbooks for this entry
     *  @throws Exception on error
     */
	private List<String> getLogbooks(final long entry_id) throws Exception
    {
	    final List<String> logbooks = new ArrayList<>();
	    try
        (
            final PreparedStatement statement = rdb.getConnection().prepareStatement(
                "SELECT b.logbook_nm" +
                " FROM LOGBOOK.entry_logbook e" +
                " JOIN LOGBOOK.logbook_v b ON e.logbook_id = b.logbook_id" +
                " AND e.log_entry_id = ?");
        )
        {
            statement.setLong(1, entry_id);
            final ResultSet result = statement.executeQuery();
            while (result.next())
                logbooks.add(result.getString(1));
            result.close();
        }
        return logbooks;
    }

    /** @param entry_id Log entry ID
     *  @return Categories used for this entry
     *  @throws Exception on error
     */
	private List<ELogCategory> getCategories(final long entry_id) throws Exception
	{
	    final List<ELogCategory> logbooks = new ArrayList<>();
        try
        (
            final PreparedStatement statement = rdb.getConnection().prepareStatement(
                "SELECT e.cat_id, c.cat_nm" +
                " FROM LOGBOOK.log_categories_v c" +
                " JOIN LOGBOOK.LOG_ENTRY_CATEGORIES e ON e.cat_id = c.cat_id" +
                " AND e.log_entry_id = ?");
        )
        {
            statement.setLong(1, entry_id);
            final ResultSet result = statement.executeQuery();
            while (result.next())
                logbooks.add(new ELogCategory(result.getString(1), result.getString(2)));
            result.close();
        }
        return logbooks;
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
	public long createEntry(final String logbook, final String title, final String text) throws Exception
    {
		final long entry_id; // Entry ID from RDB

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
			addAttachment(entry_id, "FullEntry.txt", "Full Text", stream);
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
	private long createBasicEntry(final String logbook, final String title, final String text)
	        throws Exception
	{
		try
		(
	        final CallableStatement statement = rdb.getConnection().prepareCall(
                "call logbook.logbook_pkg.insert_logbook_entry(?, ?, ?, ?, ?, ?)");
        )
		{
			statement.setString(1, badge_number);
			statement.setString(2, logbook);
			statement.setString(3, title);
			statement.setString(4, "");
			statement.setString(5, text);
			statement.registerOutParameter(6, OracleTypes.NUMBER);
			statement.executeQuery();
			return statement.getLong(6);
		}
	}

	/** Fetch type ID for image
	 *  @param extension File extension of image file.
	 *  @return image_type_id from the RDB, -1 if not found
	 *  @throws Exception on error
	 */
	private long fetchImageTypes(final String extension) throws Exception
	{
		final PreparedStatement statement = rdb.getConnection().prepareStatement(
	        "SELECT image_type_id FROM LOGBOOK.IMAGE_TYPE WHERE ?=UPPER(file_extension)");
	    statement.setString(1, extension.toUpperCase());
	    return fetchLongResult(statement);
	}

	/** Execute statement and return the first 'long' result
     *  @param statement Statement to execute
     *  @return First result, -1 if nothing was found
     *  @throws Exception on error
     */
    private long fetchLongResult(final PreparedStatement statement) throws Exception
    {
        try
        {
            final ResultSet result = statement.executeQuery();
            final long id;
            if (result.next())
                id = result.getLong(1);
            else
                id = -1;
            result.close();
            return id;
        }
        finally
        {
            statement.close();
        }
    }
	
	/** Fetch ID for attachment
	 *  @param extension Attachment file extension.
	 *  @return attachment_type_id from the RDB, -1 if not found
	 *  @throws Exception on error
	 */
	private long fetchAttachmentTypes(final String extension) throws Exception
	{
		final PreparedStatement statement = rdb.getConnection().prepareStatement(
	        "SELECT attachment_type_id FROM LOGBOOK.ATTACHMENT_TYPE WHERE ?=UPPER(file_extension)");
	    statement.setString(1, extension.toUpperCase());
        return fetchLongResult(statement);
	}

	/** Add another logbook reference to existing entry.
     *
     *  @param entry_id ID of entry to which to add a logbook reference
     *  @param logbook_name Name of the logbook where this entry should also appear
     *  @throws Exception on error
     */
    public void addLogbook(final long entry_id, final String logbook_name) throws Exception
    {
        final ELogbook logbook = findLogbook(logbook_name);
        final Connection connection = rdb.getConnection();
        try
        (
            final PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO LOGBOOK.ENTRY_LOGBOOK(log_entry_id, logbook_id) VALUES(?,?)");
        )
        {
            statement.setLong(1, entry_id);
            statement.setString(2, logbook.getId());
            statement.executeUpdate();
        }
    }
	
	/** Determine the type of attachment, based on file extension, and add it
     *  to the elog entry with the entry_id.
     *
     *  @param entry_id ID of entry to which to add this file
     *  @param fname input filename, must have a file extension
     *  @param caption Caption or 'title' for the attachment
     *  @param stream Stream with attachment content
	 *  @return Attachment that was added
     *  @throws Exception on error
     */
    public ELogAttachment addAttachment(final long entry_id,
            final String fname, final String caption,
            final InputStream stream) throws Exception
    {
    	// Determine file extension
    	final int ndx = fname.lastIndexOf(".");
        if (ndx <= 0)
            throw new Exception("Attachment has no file extension: " + fname);
        final String extension = fname.substring(ndx + 1);
        
        // Determine file type ID used in RDB. First try image
        long fileTypeID = fetchImageTypes(extension);
        final boolean is_image = fileTypeID != -1;
        // Try non-image attachment
        if (! is_image)
            fileTypeID = fetchAttachmentTypes(extension);
    	if (fileTypeID == -1)
    	    throw new Exception("Unsupported file type for '" + fname + "'");

    	// Buffer the attachment data so that we can return it
    	ByteArrayOutputStream data_buf = new ByteArrayOutputStream();
    	StreamHelper.copy(stream, data_buf);
    	stream.close();
    	final byte[] data = data_buf.toByteArray();
    	data_buf.close();
    	data_buf = null;
    	
    	// Submit to RDB
    	final Connection connection = rdb.getConnection();
    	try
    	(
	        final CallableStatement statement = connection.prepareCall(
                "call logbook.logbook_pkg.add_entry_attachment(?, ?, ?, ?, ?)");
        )
    	{
    		statement.setLong(1, entry_id);
    		statement.setString(2, is_image ? "I" : "A");
    		statement.setString(3, caption);
    		statement.setLong(4, fileTypeID);
    		statement.setBinaryStream(5, new ByteArrayInputStream(data));
    		statement.executeQuery();
    	}
    	
    	return new ELogAttachment(is_image, fname, caption, data);
    }

    /** Obtain image attachments
     *  @param entry_id Log entry ID
     *  @return Image Attachments
     *  @throws Exception on error
     */
    public List<ELogAttachment> getImageAttachments(final long entry_id) throws Exception
    {
        final List<ELogAttachment> images = new ArrayList<>();
        final Connection connection = rdb.getConnection();
        try
        (
            final PreparedStatement statement = connection.prepareStatement(
                "SELECT e.image_id, i.image_nm, t.image_type_nm, i.image_data" +
                        " FROM LOGBOOK.LOG_ENTRY_IMAGE e" +
                        " JOIN LOGBOOK.IMAGE i ON e.image_id = i.image_id" +
                        " JOIN LOGBOOK.IMAGE_TYPE t ON i.image_type_id = t.image_type_id" +
                " WHERE log_entry_id=?");
        )
        {
            statement.setLong(1, entry_id);
            final ResultSet result = statement.executeQuery();
            while (result.next())
            {
                final String name = result.getString(2);
                final String type = result.getString(3);
                final Blob blob = result.getBlob(4);
                final byte[] data = blob.getBytes(1, (int) blob.length());
                images.add(new ELogAttachment(true, name, type, data));
            }
        }
        return images;
    }

    /** Obtain non-image attachments
     *  @param entry_id Log entry ID
     *  @return Attachments
     *  @throws Exception on error
     */
    public List<ELogAttachment> getOtherAttachments(final long entry_id) throws Exception
    {
        final List<ELogAttachment> attachments = new ArrayList<>();
        final Connection connection = rdb.getConnection();
        try
        (
            final PreparedStatement statement = connection.prepareStatement(
                "SELECT e.attachment_id, a.attachment_nm, t.attachment_type_nm, a.attachment_data, t.file_extension" +
                        " FROM LOGBOOK.LOG_ENTRY_ATTACHMENT e" +
                        " JOIN LOGBOOK.ATTACHMENT a ON e.attachment_id = a.attachment_id" +
                        " JOIN LOGBOOK.ATTACHMENT_TYPE t ON a.attachment_type_id = t.attachment_type_id" +
                " WHERE log_entry_id=?");
        )
        {
            statement.setLong(1, entry_id);
            final ResultSet result = statement.executeQuery();
            while (result.next())
            {
                final String name = result.getString(2);
                final String type = result.getString(5);
                final Blob blob = result.getBlob(4);
                final byte[] data = blob.getBytes(1, (int) blob.length());
                attachments.add(new ELogAttachment(false, name, type, data));
            }
        }
        return attachments;
    }
    
    /** Add category to entry
     *  @param entry_id Log entry ID
     *  @param tag_name Name of tag to add
     *  @throws Exception on error
     */
    public void addCategory(final long entry_id, final String category_name) throws Exception
    {
        final String tag_id = getTagID(category_name);
        final Connection connection = rdb.getConnection();
        try
        (
            final PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO LOGBOOK.LOG_ENTRY_CATEGORIES(LOG_ENTRY_ID, CAT_ID)" +
                " VALUES(?, ?)");
        )
        {
            statement.setLong(1, entry_id);
            statement.setString(2, tag_id);
            statement.executeUpdate();
        }
    }
    
    /** @param category_name Category name
     *  @return Category ID
     *  @throws Exception when category not known
     */
    private String getTagID(final String category_name) throws Exception
    {
        for (ELogCategory category : categories)
            if (category.getName().equalsIgnoreCase(category_name))
                return category.getID();
        throw new Exception("Unknown logbook category '" + category_name + "'");
    }

    /** Close RDB connection. Must be called when done using the logbook. */
    public void close()
    {
        rdb.close();
    }
}
