package org.csstudio.logbook.sns;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import oracle.jdbc.driver.OracleTypes;
import oracle.sql.BLOB;

import org.csstudio.logbook.ILogbook;
import org.csstudio.platform.utility.rdb.RDBUtil;

/**
 * SNS logbook
 * 
 * @author Delphy Nypaver Armstrong
 * @author Kay Kasemir
 */
public class SNSLogbook implements ILogbook
{
    final private int MAX_TEXT_SIZE;
    private static final String DEFAULT_BADGE_NUMBER = "999992"; //$NON-NLS-1$
    final private RDBUtil rdb;
    final private String logbook;
    final String badge_number;

    /** Constructor
     *  @param rdb RDB connection
     *  @param user User (for which we'll try to get the badge number)
     *  @param logbook SNS logbook to use
     *  @throws Exception on error
     */
    public SNSLogbook(final RDBUtil rdb, final String user,
            final String logbook) throws Exception
    {
        this.rdb = rdb;
        this.logbook = logbook;
        badge_number = getBadgeNumber(user);
        /* The maximum allowed size of logbook text entry */
        MAX_TEXT_SIZE = getContentLength();
    }

    /** Create the Elog entry 
     * 
     *  @param String title   title of the elog entry
     *  @param String text    user entered text for the elog entry
     *  @param String imageName  name of the image to attach or null if no image
     *  @throws Exception
     *  @throws SQLException
     */
    @SuppressWarnings("nls")
    public void createEntry(String title, String text, String imageName)
            throws Exception
    {
       /** If the input imageName is null - meaning there's no image to attach
        *  and the text is not too large for an elog entry, create an average elog 
        *  entry.
        */

        if (imageName == null && text!=null && text.length()<MAX_TEXT_SIZE)
        {
            final String mysql = "call logbook.logbook_pkg.insert_logbook_entry"
                    + "(?, ?, ?, ?, ?)";
            final String category = "";
            // create an sql call for the elog
            final CallableStatement statement = rdb.getConnection().prepareCall(mysql);
            try
            {
                statement.setString(1, badge_number);
                statement.setString(2, logbook);
                statement.setString(3, title);
                statement.setString(4, category);
                statement.setString(5, text);
                statement.executeQuery();
            }
            finally
            {
                statement.close();
            }
            return;
        }
        
        // If text is made into an attachment due to size restraints, replace the
        // text that is now an attachment with text to explain why there is an
        // attachment
   final String info = "Input text exceeds " + MAX_TEXT_SIZE + " characters, see attachment.";
 
        // Get the Entry ID from the RDB for the attachment/image.
        final int entry_id =  getEntryID(title, info);

        // If an image was input add the image to the elog. 
        if(imageName != null)
        {   
           addFileToElog(imageName, "I", entry_id);
        }
        // Copy the oversized text entry to a text file and create and elog entry with
        //  this text file as an attachment.
        if(text!=null && text.length()>=MAX_TEXT_SIZE)
        {
           // Get a name for the created text file
           final File tmp_file = File.createTempFile("Logbook", ".txt");
           final String fname = tmp_file.getAbsolutePath();
           
           // Store the oversized text entry in the text file
           setContents(tmp_file, text);
           // Add the text attachment to the elog
           addFileToElog(fname, "A", entry_id);
           tmp_file.deleteOnExit();
        }
    }

    /** There will be multiple attachments, so ask the RDB for an entry id to
     *  be used to keep all the attachments for this elog entry together.
     * 
     *  @param String title   title of the elog entry
     *  @param String text    explanatory text for the elog entry
     *  @param String imageName  name of the image to attach or null if no image
     *  @throws Exception
     *  @throws SQLException
     */

    private int getEntryID(String title, String text) throws Exception
    {
      // Initiate the multi-file sql and retrieve the entry_id
       final String mysql = "call logbook.logbook_pkg.insert_logbook_entry"
          + "(?, ?, ?, ?, ?, ?)";
       final CallableStatement statement = rdb.getConnection().prepareCall(mysql);
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
     *  @param String title   title of the elog entry
     *  @param String text    explanatory text for the elog entry
     *  @param String imageName  name of the image to attach or null if no image
     *  @throws Exception
     *  @throws SQLException
     */
    
    private void addFileToElog(String fname, String fileType, int entry_id) throws SQLException, Exception
    {
 
       final File inputFile = new File(fname);
       final int file_size = (int) inputFile.length();
 
      // Get the file extension
      final int ndx = fname.lastIndexOf(".");
      final String extension = fname.substring(ndx+1);
      final long fileTypeID = getFileTypeId(fileType, extension);

      // Create a Blob to store the attachment in.
      final BLOB blob = BLOB.createTemporary(rdb.getConnection(), true, BLOB.DURATION_SESSION);
    
          // Initiate the sql to add attachments to the elog
       final String mysql = "call logbook.logbook_pkg.add_entry_attachment"
                       + "(?, ?, ?, ?, ?)";
       final CallableStatement statement = rdb.getConnection().prepareCall(mysql);
       try
       {
           statement.setInt(1, entry_id);
           statement.setString(2, fileType);    
           statement.setString(3, fname);
           statement.setLong(4, fileTypeID);
           
           // Send the image to the sql.
           if(fileType.equals("I"))
           {
              try
              {
                 final FileInputStream input_stream = new FileInputStream(inputFile);
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
              final String BlobContent = readFileAsString(fname);
              // blob.setBytes( 1L, text.getBytes() );
              blob.setBytes( 1L, BlobContent.getBytes() );
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
     * Ask the RDB for the fileTypeID, based on whether an image or text is to be
     * attached and the file extension.
     *  
     * @param fileType    an "I" for image, "A" for attachment.
     * @param extension   extension of file to be attached
     * 
     */
    long getFileTypeId(String fileType, String extension) throws Exception
    {
       if(fileType.equalsIgnoreCase("I")) 
           return fetchImageTypes( extension );
       else
           return fetchAttachmentTypes( extension );
    }

    /**
     * Change the contents of text file in its entirety, overwriting any
     * existing text.
     *
     * This style of implementation throws all exceptions to the caller.
     *
     * @param aFile is an existing file which can be written to.
     * @throws IllegalArgumentException if param does not comply.
     * @throws FileNotFoundException if the file does not exist.
     * @throws IOException if problem encountered during write.
     */
     static public void setContents(File aFile, String aContents)
                                    throws FileNotFoundException, IOException {
       if (aFile == null) {
         throw new IllegalArgumentException("File should not be null.");
       }
       if (!aFile.exists()) {
         throw new FileNotFoundException ("File does not exist: " + aFile);
       }
       if (!aFile.isFile()) {
         throw new IllegalArgumentException("Should not be a directory: " + aFile);
       }
       if (!aFile.canWrite()) {
         throw new IllegalArgumentException("File cannot be written: " + aFile);
       }

       //use buffering
       final Writer output = new BufferedWriter(new FileWriter(aFile));
       try {
         //FileWriter always assumes default encoding is OK!
         output.write( aContents );
       }
       finally {
         output.close();
       }
     }

    
    /** Get the badge number for the user in the connection dictionary
     * 
     *  @param user   user id of person logging in.
     *  @return the badge number for the specified user or a default
     *  @throws Exception
     *  @throws SQLException
     */
    @SuppressWarnings("nls")
    private String getBadgeNumber(final String user) throws Exception, SQLException
    {
        final PreparedStatement statement = rdb.getConnection()
        .prepareStatement("select bn from OPER.EMPLOYEE_V where user_id=?");
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

    public void close()
    {
        rdb.close();
    }
    
    /** Fetch the available image types. 
    * @param image_type  the extension of the input image file.
    * @throws SQLException
    * @throws Exception
    * @return image_type_id from the RDB, -1 if not found
    */
    private long fetchImageTypes( String image_type ) throws SQLException, Exception  {
       final Statement statement = rdb.getConnection().createStatement();
       try {
          final ResultSet result = statement.executeQuery( "select * from LOGBOOK.IMAGE_TYPE" );
                    
          while ( result.next() ) {
             final long ID = result.getLong( "image_type_id" );
             final String extension = result.getString( "file_extension" );
             if(image_type.equalsIgnoreCase(extension))
                return ID;
          }
       }
       finally
       {
           statement.close();
       }
      return -1;
    }
    
    
    /** Fetch the available attachment types.
    * @param attachment_type  the extension of the input attachment file.
    * @throws SQLException
    * @throws Exception
    * @return attachment_type_id from the RDB, -1 if not found
    */
    private long fetchAttachmentTypes( String attachment_type ) throws SQLException, Exception  {
       final Statement statement = rdb.getConnection().createStatement();
       try {
          final ResultSet result = statement.executeQuery( "select * from LOGBOOK.ATTACHMENT_TYPE" );
                    
          while ( result.next() ) {
             long ID = result.getLong( "attachment_type_id" );
             final String extension = result.getString( "file_extension" );
             if(attachment_type.equalsIgnoreCase(extension))
                return ID;
          }
       }
       finally
       {
           statement.close();
       }
      return -1;
    }
    
    /** Read the input attachment file as a text file.
     * @param filePath   the name of the file to read
     * @throws IOException
     * @return the file contents as a string
     */
   private static String readFileAsString(String filePath) throws java.io.IOException{
       final BufferedReader reader = new BufferedReader(
               new FileReader(filePath));
       final File file = new File(filePath);
       // Get the size of the input file for buffer sizes
       final int size = (int)file.length();
       final StringBuffer fileData = new StringBuffer(size);
       final char[] buf = new char[size];
       int numRead=0;
       // Read each character in the file and store it in a String
       while((numRead=reader.read(buf)) != -1){
           final String readData = String.valueOf(buf, 0, numRead);
           fileData.append(readData);
       }
       reader.close();
       return fileData.toString();
   }

   /** Query the RDB for the specified Content length.
    * @throws Exception
    * @return Content length specified in the RDB
    */
   public int getContentLength() throws Exception    
   {     
      final ResultSet tables = rdb.getConnection().getMetaData().getColumns(null, "LOGBOOK", 
                                       "LOG_ENTRY", "CONTENT");    
       if (!tables.next())         
      throw new Exception("Unable to locate LOGBOOK.LOG_ENTRY.CONTENT");      
      final int max_elog_text = tables.getInt("COLUMN_SIZE");   
       return max_elog_text; 
   }
}
