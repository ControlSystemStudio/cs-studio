
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

package org.csstudio.alarm.jms2ora.util;

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

import org.csstudio.alarm.jms2ora.Jms2OraActivator;
import org.csstudio.alarm.jms2ora.preferences.PreferenceConstants;
import org.csstudio.alarm.jms2ora.service.ArchiveMessage;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author  Markus Moeller
 * @version 2.0
 * @deprecated
 */

@Deprecated
public final class MessageFileHandler implements FilenameFilter {

    /** Static instance reference */
    private static MessageFileHandler instance = null;

    /** the class logger */
    private static final Logger LOG = LoggerFactory.getLogger(MessageFileHandler.class);

    /** True if the folder 'nirvana' exists. This folder holds the stored message object content. */
    private boolean existsObjectFolder;

    /** Name of the folder that holds the stored message content */
    private String objectDir;

    /** Prefix for the file names */
    private final String prefix = "message_";

    private MessageFileHandler() {

        final IPreferencesService prefs = Platform.getPreferencesService();
        String temp = prefs.getString(Jms2OraActivator.PLUGIN_ID, PreferenceConstants.STORAGE_DIRECTORY, "./var/", null);
        if(!temp.endsWith("/")) {
            temp += "/";
        }

        objectDir = prefs.getString(Jms2OraActivator.PLUGIN_ID, PreferenceConstants.MESSAGE_DIRECTORY, "nirvana/", null);
        objectDir = temp + objectDir;

        createObjectFolder();
    }

    /**
     *
     * @return The instance of this class
     */
    public static synchronized MessageFileHandler getInstance() {

        if(instance == null) {
            instance = new MessageFileHandler();
        }

        return instance;
    }

    /**
     *
     */
    @Override
    public boolean accept(final File dir, final String name) {
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
        File file = null;
        int result = 0;

        file = new File(objectDir);
        fileList = file.list(this);

        if(fileList != null) {
            result = fileList.length;
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
        File name = null;

        name = new File(objectDir);
        result = name.list(this);

        if(result.length == 0) {

            result = null;
            result = new String[1];
            result[0] = "No message file names available.";
        }

        name = null;

        return result;
    }

    private String[] collectMessageFilesName() {

        String[] result = null;
        File name = null;

        name = new File(objectDir);
        result = name.list(this);

        name = null;

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
                    content = this.readMessageContent(objectDir + name[i]);
                    result[i] = content.toString();
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
        int result = 0;

        list = new File(objectDir);

        fileList = list.list(this);
        if(fileList == null) {
            list = null;
            return result;
        }

        for (final String element : fileList) {

            del = new File(objectDir + element);
            if(del.delete()) {
                result++;
            }

            del = null;
        }

        list = null;

        return result;
    }

    public boolean exitsObjectFolder() {
        return existsObjectFolder;
    }

    /**
     *
     */
    private void createObjectFolder() {

        final File folder = new File(objectDir);

        existsObjectFolder = true;

        if(!folder.exists()) {

            final boolean result = folder.mkdir();
            if(result) {
                LOG.info("Folder " + objectDir + " was created.");
                existsObjectFolder = true;
            } else {
                LOG.warn("Folder " + objectDir + " was NOT created.");
                existsObjectFolder = false;
            }
        }
    }

    /**
     * <code>writeMapMessageToFile</code> writes a map message object to disk.
     *
     * @param content - The MessageContent object that have to be stored on disk.
     */

    public void writeMessageContentToFile(final ArchiveMessage content) {

        final SimpleDateFormat dfm = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
        GregorianCalendar cal = null;
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        String fn = null;

        if(!content.hasContent()) {
            LOG.info("Message does not contain content.");
            return;
        }

        if(!existsObjectFolder) {
            LOG.warn("Object folder '" + objectDir + "' does not exist. Message cannot be stored.");
            return;
        }

        cal = new GregorianCalendar();
        fn  = prefix + dfm.format(cal.getTime());

        try {
            fos = new FileOutputStream(objectDir + fn + ".ser");
            oos = new ObjectOutputStream(fos);

            // Write the MessageContent object to disk
            oos.writeObject(content);
        } catch(final FileNotFoundException fnfe) {
            LOG.error("FileNotFoundException : " + fnfe.getMessage());
        } catch(final IOException ioe) {
            LOG.error("IOException : " + ioe.getMessage());
        } finally {
            if(oos != null) {
                try{
                    oos.close();
                } catch (final IOException ioe) {
                    // Can be ignored
                }
            }
            if(fos != null){try{fos.close();}catch(final IOException ioe){/* Can be ignored */}}

            oos = null;
            fos = null;
        }
    }

    private ArchiveMessage readMessageContent(final String fileName) {

        ArchiveMessage content = null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;

        try {
            fis = new FileInputStream(fileName);
            ois = new ObjectInputStream(fis);

            // Write the MessageContent object to disk
            content = (ArchiveMessage)ois.readObject();
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

        return content;
    }
}
