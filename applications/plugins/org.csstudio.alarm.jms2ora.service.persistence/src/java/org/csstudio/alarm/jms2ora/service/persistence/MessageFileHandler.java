
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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;
import org.csstudio.alarm.jms2ora.service.ArchiveMessage;
import org.csstudio.alarm.jms2ora.service.DataDirectory;
import org.csstudio.alarm.jms2ora.service.DataDirectoryException;
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

public class MessageFileHandler {

    /** the class logger */
    private static final Logger LOG = LoggerFactory.getLogger(MessageFileHandler.class);

    /** The object that holds the paths to the data directories */
    private final DataDirectory dataDirectories;

    private ArchiveMessageFilter archiveMessageFilter;
    
    @SuppressWarnings("unused")
    private RawMessageFilter rawMessageFilter;
    
    /** Prefix for the file names of the archive messages */
    private final String archivePrefix = "archive-message-";

    /** Prefix for the file names of the archive messages */
    private final String rawPrefix = "raw-message-";

    public MessageFileHandler() {
        final IPreferencesService prefs = Platform.getPreferencesService();
        final String messageDir = prefs.getString(Activator.getPluginId(),
                                                  PreferenceConstants.MESSAGE_DIRECTORY,
                                                  "./var/nirvana",
                                                  null);
        final String messageAltDir = prefs.getString(Activator.getPluginId(),
                                                     PreferenceConstants.MESSAGE_ALT_DIRECTORY,
                                                     null,
                                                     null);
        dataDirectories = new DataDirectory(messageDir, messageAltDir);
        archiveMessageFilter = new ArchiveMessageFilter(archivePrefix);
        rawMessageFilter = new RawMessageFilter(rawPrefix);
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
            final File file = dataDirectories.getDataDirectory();
            fileList = file.list(archiveMessageFilter);
            if(fileList != null) {
                result = fileList.length;
            }
        } catch (final DataDirectoryException e) {
            LOG.error("[*** DataDirectoryException ***]: " + e.getMessage());
        }

        return result;
    }

    public String getMessageFileNamesAsString() {

        final StringBuffer result = new StringBuffer();
        final String[] list = getMessageFileNames();

        for(final String s : list) {
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

            final File name = dataDirectories.getDataDirectory();

            result = name.list(archiveMessageFilter);
            if(result.length == 0) {
                result = null;
                result = new String[1];
                result[0] = "No message file names available.";
            }

        } catch (final DataDirectoryException dde) {
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
            result = name.list(archiveMessageFilter);
        } catch (final DataDirectoryException dde) {
            // Can be ignored
        }

        return result;
    }

    public String getMessageFileContentAsString() {

        final StringBuffer result = new StringBuffer();
        final String[] list = getMessageFileContent();

        for(final String s : list) {
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
                    } catch (final DataDirectoryException dde) {
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

    public ArchiveMessage readMessageContent(final String fileName) {

        ArchiveMessage content = null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;

        File file = new File(fileName);
        
        try {
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);

            // Write the MessageContent object to disk
            content = (ArchiveMessage) ois.readObject();
        } catch(final FileNotFoundException fnfe) {
            LOG.error("FileNotFoundException : " + fnfe.getMessage());
        } catch(final IOException ioe) {
            LOG.error("IOException : " + ioe.getMessage());
        } catch (final ClassNotFoundException e) {
            LOG.error("ClassNotFoundException : " + e.getMessage());
        } finally {
            if(ois != null){try{ois.close();}catch(final IOException ioe){/* Can be ignored */}}
            if(fis != null){try{fis.close();}catch(final IOException ioe){/* Can be ignored */}}

            ois = null;
            fis = null;
        }

        if (file.delete()) {
            LOG.debug("{} deleted.", fileName);
        }

        return content;
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
        } catch (final DataDirectoryException e) {
            return result;
        }

        fileList = list.list(archiveMessageFilter);
        if(fileList == null) {
            list = null;
            return result;
        }

        for (final String element : fileList) {

            try {

                del = new File(dataDirectories.getDataDirectoryAsString() + element);
                if(del.delete()) {
                    result++;
                }
                del = null;

            } catch (final DataDirectoryException dde) {
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
    public int writeMessagesToFile(final Vector<ArchiveMessage> content) {

        if (content.isEmpty()) {
            return 0;
        }
        
        if(dataDirectories.existsDataDirectory() == false) {
            LOG.warn("Object folder does not exist. Message cannot be stored.");
            return -1;
        }

        final GregorianCalendar cal = new GregorianCalendar();
        final SimpleDateFormat dfm = new SimpleDateFormat("yyyyMMdd-HHmmssSSS");
        final Date date = cal.getTime();
        final String dateString = dfm.format(date);
        DecimalFormat nf = new DecimalFormat("#");
        nf.setMinimumIntegerDigits(String.valueOf(content.size()).length());
        
        int count = 1;
        for (ArchiveMessage o : content) {
        
            String fn = archivePrefix + dateString + "-" + nf.format(count++);
            
            FileOutputStream fos = null;
            ObjectOutputStream oos = null;
    
            try {
                fos = new FileOutputStream(dataDirectories.getDataDirectoryAsString() + fn + ".ser");
                oos = new ObjectOutputStream(fos);
    
                // Write the MessageContent object to disk
                oos.writeObject(o);
            } catch (final Exception e) {
                LOG.error("[*** " + e.getClass().getSimpleName() + " ***]: " + e.getMessage());
            } finally {
                if(oos != null){try{oos.close();}catch(final IOException ioe){/* Can be ignored */}}
                if(fos != null){try{fos.close();}catch(final IOException ioe){/* Can be ignored */}}
    
                oos = null;
                fos = null;
            }
        }
        count--;
        return count;
    }
    
    public Vector<ArchiveMessage> readMessagesFromFile() {
        Vector<ArchiveMessage> result = new Vector<ArchiveMessage>();
        try {
            File file = dataDirectories.getDataDirectory();
            String path = file.getAbsolutePath() + System.getProperty("file.separator");
            String[] fileNames = file.list(archiveMessageFilter);
            if (fileNames != null) {
                if (fileNames.length > 0) {
                    for (String s : fileNames) {
                        ArchiveMessage am = readMessageContent(path + s);
                        result.add(am);
                    }
                }
            }
        } catch (DataDirectoryException e) {
            LOG.error("[*** DataDirectoryException ***]: {}", e.getMessage());
        }
        return result;
    }
}
