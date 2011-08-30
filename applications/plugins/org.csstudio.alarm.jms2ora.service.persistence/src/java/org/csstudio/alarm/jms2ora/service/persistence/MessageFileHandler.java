
/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchroton, 
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
 *
 */

package org.csstudio.alarm.jms2ora.service.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import org.csstudio.alarm.jms2ora.service.DataDirectory;
import org.csstudio.alarm.jms2ora.service.DataDirectoryException;
import org.csstudio.alarm.jms2ora.service.ArchiveMessage;
import org.csstudio.alarm.jms2ora.service.persistence.internal.PreferenceConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author  Markus Moeller
 * @version 2.0
 */

public class MessageFileHandler implements FilenameFilter {
    
    /** the class logger */
    private static final Logger LOG = LoggerFactory.getLogger(MessageFileHandler.class);
    
    /** The object that holds the paths to the data directories */
    private DataDirectory dataDirectories;
    
    /** Prefix for the file names */
    private final String prefix = "message_";
    
    public MessageFileHandler() {
        IPreferencesService prefs = Platform.getPreferencesService();
        String messageDir = prefs.getString(Activator.getPluginId(), PreferenceConstants.MESSAGE_DIRECTORY, "columns/", null);
        dataDirectories = new DataDirectory(messageDir);
    }
    
    /**
     * 
     */
    @Override
    public boolean accept(File dir, String name) {
        return name.toLowerCase().matches(prefix + "\\d{8}_\\d{9}.ser");
    }
    
    /**
     * The method returns the number of MapMessage objects which were serialized. Uses the {@link MessageFileHandler}
     * class.
     * 
     * @return Number of MessageContent objects which were written to the database because of errors during
     *         processing the message content.
     */
    public int getMessageFilesNumber() {
        
        String[] fileList = null;
        int result = -1;
        
        try {
            File file = dataDirectories.getDataDirectory();
            fileList = file.list(this);
            if(fileList != null) {
                result = fileList.length;
            }
        } catch (DataDirectoryException e) {
            LOG.error("[*** DataDirectoryException ***]: " + e.getMessage());
        }
        
        return result;
    }
    
    public String getMessageFileNamesAsString() {
        
        StringBuffer result = new StringBuffer();
        String[] list = getMessageFileNames();
        
        for(String s : list) {
            result.append(s + "\n");
        }
        
        return result.toString();
    }
    
    /**
     * The method returns an array of String with the file names of all serialized MapMessage objects.
     * Uses the {@link MessageFileHandler} class.
     * 
     * @return Array of String with the file names.
     */
    public String[] getMessageFileNames() {
        
        String[] result = null;
        
        try {
            
            File name = dataDirectories.getDataDirectory();

            result = name.list(this);
            if(result.length == 0) {
                result = null;
                result = new String[1];
                result[0] = "No message file names available.";
            }
            
        } catch (DataDirectoryException dde) {
            result = new String[1];
            result[0] = "Exception was thrown: " + dde.getMessage();
        }

        return result;
    }

    private String[] collectMessageFilesName() {
        
        String[] result = null;
        File name = null;
        
        try {
            name = dataDirectories.getDataDirectory();
            result = name.list(this);
        } catch (DataDirectoryException dde) {
            // Can be ignored
        }
        
        return result;
    }
    
    public String getMessageFileContentAsString() {
        
        StringBuffer result = new StringBuffer();
        String[] list = getMessageFileContent();
        
        for(String s : list) {
            result.append(s + "\n");
        }
        
        return result.toString();
    }
    
    /**
     * The method returns the message content of the stored message content objects.
     * 
     * @return Array of String with the message content
     */
    public String[] getMessageFileContent() {
        
        ArchiveMessage content = null;
        String[] result  = null;
        String[] name = null;
        
        name = this.collectMessageFilesName();
        
        if(name != null) {
            
            if(name.length > 0) {
                
                result = new String[name.length];
                for(int i = 0;i < name.length;i++) {
                    try {
                        content = this.readMessageContent(dataDirectories.getDataDirectory() + name[i]);
                        result[i] = content.toString();
                    } catch (DataDirectoryException dde) {
                        result[i] = "Cannot read message: " + dde.getMessage();
                    }
                }
            } else {
                result = new String[1];
                result[0] = "No message files found.";
            }
        } else {
            result = new String[1];
            result[0] = "No message files found.";
        }
        
        return result;
    }
    
    
    /**
     * The method deletes all message files.
     * 
     * @return Number of deleted files.
     */
    public int deleteAllMessageFiles() {
        
        String[] fileList = null;
        File list = null;
        File del = null;
        int result = -1;
        
        try {
            list = dataDirectories.getDataDirectory();
        } catch (DataDirectoryException e) {
            return result;
        }
        
        fileList = list.list(this);
        if(fileList == null) {
            list = null;
            return result;
        }
        
        for(int i = 0;i < fileList.length;i++) {
            
            try {

                del = new File(dataDirectories.getDataDirectoryAsString() + fileList[i]);
                if(del.delete()) {
                    result++;
                }
                del = null;
                
            } catch (DataDirectoryException dde) {
                // Can be ignored
            }
        }
        
        list = null;
        
        return result;
    }
    
    /**
     * <code>writeMapMessageToFile</code> writes a map message object to disk.
     * 
     * @param content - The MessageContent object that have to be stored on disk.
     */
    public void writeMessageContentToFile(ArchiveMessage content) {
        
        if(!content.hasContent()) {
            LOG.info("Message does not contain content.");
            return;
        }

        if(dataDirectories.existsDataDirectory() == false) {
            LOG.warn("Object folder does not exist. Message cannot be stored.");
            return;
        }
        
        GregorianCalendar cal = new GregorianCalendar();
        SimpleDateFormat dfm = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
        String fn = prefix + dfm.format(cal.getTime());                

        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try {
            fos = new FileOutputStream(dataDirectories.getDataDirectoryAsString() + fn + ".ser");
            oos = new ObjectOutputStream(fos);
            
            // Write the MessageContent object to disk
            oos.writeObject(content);            
        } catch(Exception e) {
            LOG.error("[*** " + e.getClass().getSimpleName() + " ***]: " + e.getMessage());
        } finally {
            if(oos != null){try{oos.close();}catch(IOException ioe){/* Can be ignored */}}
            if(fos != null){try{fos.close();}catch(IOException ioe){/* Can be ignored */}}
            
            oos = null;
            fos = null;            
        }
    }
    
    public ArchiveMessage readMessageContent(String fileName) {
        
        ArchiveMessage content = null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;

        try {
            fis = new FileInputStream(fileName);
            ois = new ObjectInputStream(fis);
            
            // Write the MessageContent object to disk
            content = (ArchiveMessage)ois.readObject();            
        } catch(FileNotFoundException fnfe) {
            LOG.error("FileNotFoundException : " + fnfe.getMessage());
        } catch(IOException ioe) {
            LOG.error("IOException : " + ioe.getMessage());
        } catch (ClassNotFoundException e) {
            LOG.error("ClassNotFoundException : " + e.getMessage());
        } finally {
            if(ois != null){try{ois.close();}catch(IOException ioe){/* Can be ignored */}}
            if(fis != null){try{fis.close();}catch(IOException ioe){/* Can be ignored */}}
            
            ois = null;
            fis = null;            
        }
        
        return content;
    }
}
