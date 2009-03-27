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
    byte[] b = null;
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
        MAX_TEXT_SIZE = getContentLength();
    }

    /** Create the Elog entry */
    @SuppressWarnings("nls")
    public void createEntry(String title, String text, String imageName)
            throws Exception
    {
       /** If the input imageName is null - meaning there's no image to attach
        *  and the text is not too large for an elog entry, create one.
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
        
        String info = "";
        if(text!=null && text.length()>=MAX_TEXT_SIZE)
           info = "Input text exceeds " + MAX_TEXT_SIZE + " characters, see attachment.";
        else
           info = "Image attached.";

        // Get the Entry ID for the attachment/image.
        final int entry_id =  getEntryID(title, info);

        /** Add text and image to the elog. */
        if(imageName != null)
        {   
           addFileToElog(imageName, "I", entry_id);
        }
        /** Copy the text to a text file and create and elog entry with
         *  this text file as an attachment.
         */
        if(text!=null && text.length()>=MAX_TEXT_SIZE)
        {
           // Get name for snapshot file
           final File tmp_file = File.createTempFile("Logbook", ".txt");
           final String fname = tmp_file.getAbsolutePath();
           
           setContents(tmp_file, text);
           addFileToElog(fname, "A", entry_id);
           tmp_file.deleteOnExit();
        }
    }
    
    private int getEntryID(String title, String text) throws Exception
    {
       int entry_id=-1;
   
      // Initiate the multi-file sql by retrieving the entry_id
      CallableStatement statement = null;
       try
       {
           final String mysql = "call logbook.logbook_pkg.insert_logbook_entry"
                       + "(?, ?, ?, ?, ?, ?)";
           statement = rdb.getConnection().prepareCall(mysql);
           statement.setString(1, badge_number);
           statement.setString(2, logbook);
           statement.setString(3, title);
           statement.setString(4, "");
           statement.setString(5, text);
           statement.registerOutParameter(6, OracleTypes.NUMBER);
           statement.executeQuery();
           entry_id = Integer.parseInt(statement.getString(6));
       }
       finally
       {
          if (statement != null)
               statement.close();
       }
       return entry_id;
    }
    
    private void addFileToElog(String fname, String fileType, int entry_id) throws Exception
    {
 
       final File inputFile = new File(fname);
       final int file_size = (int) inputFile.length();
       FileInputStream input_stream = null;
       try
       {
          input_stream = new FileInputStream(inputFile);
       }
       catch (FileNotFoundException e1)
       {
          System.out.println("Could not find " + fname);
          return;
       }
      CallableStatement statement = null;
      final int ndx = fname.lastIndexOf(".");
      final String extension = fname.substring(ndx+1);
      long fileTypeID = -1;

      if(fileType.equalsIgnoreCase("I")) 
         fileTypeID = fetchImageTypes( extension );
      else {
         fileTypeID = fetchAttachmentTypes( extension );
      }

      BLOB blob = BLOB.createTemporary(rdb.getConnection(), true, BLOB.DURATION_SESSION);
       try
       {
           final String mysql = "call logbook.logbook_pkg.add_entry_attachment"
                       + "(?, ?, ?, ?, ?)";
           statement = rdb.getConnection().prepareCall(mysql);
           statement.setInt(1, entry_id);
           statement.setString(2, fileType);    
           statement.setString(3, fname);
           statement.setLong(4, fileTypeID);
           if(fileType.equals("I"))
              statement.setBinaryStream(5, input_stream, file_size);
           else
           {
              String BlobContent = readFileAsString(fname);
              blob.setBytes( 1L, BlobContent.getBytes() );
              statement.setBlob(5, blob);
           }
           statement.executeQuery();
       }
       finally
       {
          if (input_stream != null)
          try
          {
            input_stream.close();
          }
          catch (IOException e)
          {
             // Ignore
          }  
          
          if (statement != null)
              statement.close();
       }
 
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
       Writer output = new BufferedWriter(new FileWriter(aFile));
       try {
         //FileWriter always assumes default encoding is OK!
         output.write( aContents );
       }
       finally {
         output.close();
       }
     }

    
    /** Get the badge number for the user in the connection dictionary
     *  @return the badge number for the specified user or a default
     */
    @SuppressWarnings("nls")
    private String getBadgeNumber(final String user) throws Exception
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
    * @throws Exception 
    * @throws SQLException */
    private long fetchImageTypes( String image_type ) throws SQLException, Exception  {
       final Statement statement = rdb.getConnection().createStatement();
       try {
          final ResultSet result = statement.executeQuery( "select * from LOGBOOK.IMAGE_TYPE" );
                    
          while ( result.next() ) {
             long ID = result.getLong( "image_type_id" );
             String extension = result.getString( "file_extension" );
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
    
    
    /** Fetch the available attachment types. */
    private long fetchAttachmentTypes( String attachment_type ) throws SQLException, Exception  {
       final Statement statement = rdb.getConnection().createStatement();
       try {
          final ResultSet result = statement.executeQuery( "select * from LOGBOOK.ATTACHMENT_TYPE" );
                    
          while ( result.next() ) {
             long ID = result.getLong( "attachment_type_id" );
             String extension = result.getString( "file_extension" );
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
    
  /** @param filePath the name of the file to open. 
   */ 
   private static String readFileAsString(String filePath) throws java.io.IOException{
       BufferedReader reader = new BufferedReader(
               new FileReader(filePath));
       File file = new File(filePath);
       int size = (int)file.length();
       StringBuffer fileData = new StringBuffer(size);
       char[] buf = new char[size];
       int numRead=0;
       while((numRead=reader.read(buf)) != -1){
           String readData = String.valueOf(buf, 0, numRead);
           fileData.append(readData);
           buf = new char[size];
       }
       reader.close();
       return fileData.toString();
   }

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
