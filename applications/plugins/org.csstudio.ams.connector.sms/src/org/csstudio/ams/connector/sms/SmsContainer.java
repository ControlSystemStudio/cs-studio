
/* 
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron, 
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
 */

package org.csstudio.ams.connector.sms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import org.csstudio.ams.AmsConstants;
import org.csstudio.ams.Log;

/**
 * @author Markus Moeller
 *
 */
public class SmsContainer implements AmsConstants {
    
    /** Content of this container */
    private TreeSet<Sms> content;
    
    /** Content of this container */
    private Vector<Sms> badMessages;

    /** */
    private final String VAR_DIR = "var";
    
    /** */
    private final String SMS_DIR = "sms";

    /** */
    private final String BAD_DIR = "bad";

    /**
     * Standard constructor. Just creates a TreeSet object that holds the Sms objects.
     */
    public SmsContainer() {
        
        content = null;
        
        this.loadContent();
        if(content == null) {
            content = new TreeSet<Sms>(new SmsComperator());
        }
        
        if(badMessages == null) {
            badMessages = new Vector<Sms>();
        }
    }
    
    /**
     * Creates a Sms object from the given MapMessage object. It acknowledges the JMS message.
     * The phone number is parsed(invalid characters will be removed).
     * 
     * @param message
     * @return At the moment a "nice" error number. Will be changed in future versions.
     */
    public int addSms(Message message) {
        
        Sms sms = null;
        String text = null;
        String recNo = null;
        long timestamp = 0;
        int result = SmsConnectorStart.STAT_ERR_UNDEFINED;
        
        if(message == null) {
            return SmsConnectorStart.STAT_OK;
        }
        
        if(!(message instanceof MapMessage)) {
            Log.log(this, Log.DEBUG, "Received message is not a MapMessage object.");
            if(!acknowledge(message)) {
                result = SmsConnectorStart.STAT_ERR_JMSCON;
            } else {
                result = SmsConnectorStart.STAT_OK;
            }
        } else {
            MapMessage msg = (MapMessage) message;
            
            try {
                text = msg.getString(MSGPROP_RECEIVERTEXT);
                recNo = msg.getString(MSGPROP_RECEIVERADDR);
                timestamp = msg.getJMSTimestamp();
                String parsedRecNo = null;
                
                if(!acknowledge(message)) {
                    result = SmsConnectorStart.STAT_ERR_JMSCON;
                } else {            
                    if(parsedRecNo == null) {
                        try {
                            parsedRecNo = parsePhoneNumber(recNo);
                            sms = new Sms(timestamp, parsedRecNo, text, Sms.Type.OUT);
                            content.add(sms);
                            
                            result = SmsConnectorStart.STAT_OK;
                        } catch(Exception e) {
                            Log.log(this, Log.FATAL, "Parsing phone number - failed.");
                            
                            // Although parsing failed, we have to return OK.
                            // Otherwise the application will be forced to restart.
                            result = SmsConnectorStart.STAT_OK;
                        }                   
                    }
                }
            } catch(JMSException jmse) {
                result = SmsConnectorStart.STAT_ERR_JMSCON;
            }            
        }
        
        return result;
    }

    /**
     * Creates a special Sms object from the given MapMessage object that starts a device(modem) test.
     * It takes the property CLASS from the message and creates the string MODEM_CHECK{$CLASS$}.
     * It checks if the receiver for this message is this connector. 
     * It acknowledges the JMS message.
     * 
     * @param message
     * @return At the moment a "nice" error number. Will be changed in future versions.
     */
    public int addModemtestSms(Message message)
    {
        Sms sms = null;
        String checkId = null;
        String dest = null;
        long timestamp = 0;
        int result = SmsConnectorStart.STAT_ERR_UNDEFINED;
        
        if(message == null)
        {
            return SmsConnectorStart.STAT_OK;
        }
        
        if(!(message instanceof MapMessage))
        {
            Log.log(this, Log.DEBUG, "Received message is not a MapMessage object.");
            if(!acknowledge(message))
            {
                result = SmsConnectorStart.STAT_ERR_JMSCON;
            }
            else
            {
                result = SmsConnectorStart.STAT_OK;
            }
        }
        else
        {
            MapMessage msg = (MapMessage) message;
                        
            try
            {
                dest = msg.getString("DESTINATION");
                if(dest != null)
                {
                    if((dest.compareTo("*") == 0) || 
                       (dest.compareToIgnoreCase(SmsConnectorPlugin.CONNECTOR_ID) == 0))
                    {
                        checkId = msg.getString("CLASS");
                        timestamp = msg.getJMSTimestamp();
                        
                        if(!acknowledge(message))
                        {
                            result = SmsConnectorStart.STAT_ERR_JMSCON;
                        }
                        else
                        {            
                            sms = new Sms(timestamp, 1, "NONE", "MODEM_CHECK{" + checkId + "}", Sms.Type.OUT);
                            content.add(sms);
                            
                            result = SmsConnectorStart.STAT_OK;
                        }
                    }
                }
                else
                {
                    result = SmsConnectorStart.STAT_OK;
                }
            }
            catch(JMSException jmse)
            {
                result = SmsConnectorStart.STAT_ERR_JMSCON;
            }            
        }
        
        return result;
    }
    
    /**
     * Removes the given Sms object from the content.
     * 
     * @param sms The Sms object that has to be removed.
     */
    public void removeSms(Sms sms)
    {
        if(!content.isEmpty())
        {
            content.remove(sms);
        }
    }
    
    /**
     * Removes the first(=oldest) SMS from the container.
     */
    public void removeFirstSms()
    {
        if(!content.isEmpty())
        {
            content.remove(content.first());
        }
    }

    public void addBadSms(Sms sms)
    {
        badMessages.add(sms);
    }
    
    /**
     * Stores the content(the Sms objects) of this container. The method adds the folder name 'var' to
     * the given path.
     * It also stores the current ID for the next Sms object. It is necessary to avoid conflicts with
     * stored Sms objects.
     * 
     * @return True if the Sms objects were stored, false otherwise.
     */
    public boolean storeContent()
    {
        SimpleDateFormat dateFormat = null;
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        String fileExt = null;
        boolean success = false;
        
        success = createFolder(VAR_DIR);
        
        // success is true if the folder yet exists OR the folder was just created
        if(success)
        {
            // First try to store the 'good' SMS objects
            if(content.isEmpty() == false)
            {
                if(createFolder(VAR_DIR + "/" + SMS_DIR))
                {
                    try
                    {
                        fos = new FileOutputStream(VAR_DIR + "/" + SMS_DIR + "/good-sms.ser");
                        oos = new ObjectOutputStream(fos);
                        
                        oos.writeObject(content);
                        
                        success = true;
                    }
                    catch(FileNotFoundException fnfe)
                    {
                        Log.log(this, Log.ERROR, "*** FileNotFoundException *** : " + fnfe.getMessage());
                        success = false;
                    }
                    catch(IOException ioe)
                    {
                        Log.log(this, Log.ERROR, "*** IOException *** : " + ioe.getMessage());
                        success = false;
                    }
                    finally
                    {
                        if(oos!=null) {
                            try{oos.close();}catch(Exception e){/* Can be ignored */}
                            oos=null;
                        }
                        if(fos!=null) {
                            try{fos.close();}catch(Exception e){/* Can be ignored */}
                            fos=null;
                        }
                    }
                }
            }
            
            if((badMessages.isEmpty() == false) && success)
            {
                if(createFolder(VAR_DIR + "/" + BAD_DIR))
                {
                    try
                    {
                        dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                        fileExt = dateFormat.format(Calendar.getInstance().getTime());
                        
                        fos = new FileOutputStream(VAR_DIR + "/" + BAD_DIR + "/bad-sms-" + fileExt + ".ser");
                        oos = new ObjectOutputStream(fos);
                        
                        oos.writeObject(badMessages);
        
                        success = true;
                    }
                    catch(FileNotFoundException fnfe)
                    {
                        Log.log(this, Log.ERROR, "*** FileNotFoundException *** : " + fnfe.getMessage());
                        success = false;
                    }
                    catch(IOException ioe)
                    {
                        Log.log(this, Log.ERROR, "*** IOException *** : " + ioe.getMessage());
                        success = false;
                    }
                    finally
                    {
                        if(oos!=null) {
                            try{oos.close();}catch(Exception e){/* Can be ignored */}
                            oos=null;
                        }
                        if(fos!=null) {
                            try{fos.close();}catch(Exception e){/* Can be ignored */}fos=null;
                        }
                    }
                }
            }
        }
        else
        {
            Log.log(this, Log.ERROR, "Cannot create folder '" + VAR_DIR + "' to store Sms objects.");
        }
        
        return success;
    }
    
    /**
     * Loads stored Sms objects. The method adds the folder name 'var' to the given path.
     * It also loads the current ID for the next Sms object. It is necessary to avoid conflicts with
     * stored Sms objects.
     * The folder 'var' will be deleted after loading.
     * 
     * @return True if the stored objects were loaded, false if no objects were found OR an error occured.
     */
    @SuppressWarnings("unchecked")
    public boolean loadContent()
    {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        File folder = null;
        File smsFile = null;
        boolean success = false;
        
        folder = new File(VAR_DIR);
        if(folder.exists())
        {
            smsFile = new File(VAR_DIR + "/" + SMS_DIR + "/good-sms.ser");
            
            if(smsFile.exists())
            {
                try
                {
                    fis = new FileInputStream(smsFile);
                    ois = new ObjectInputStream(fis);
                    
                    content = (TreeSet<Sms>)ois.readObject();
                    
                    success = true;
                }
                catch(FileNotFoundException fnfe)
                {
                    Log.log(this, Log.ERROR, "*** FileNotFoundException *** : " + fnfe.getMessage());
                    success = false;
                }
                catch(IOException ioe)
                {
                    Log.log(this, Log.ERROR, "*** IOException *** : " + ioe.getMessage());
                    success = false;
                }
                catch(ClassNotFoundException cnfe)
                {
                    Log.log(this, Log.ERROR, "*** ClassNotFoundException *** : " + cnfe.getMessage());
                    success = false;
                }
                finally
                {
                    if(ois!=null){try{ois.close();}catch(Exception e){}ois=null;}
                    if(fis!=null){try{fis.close();}catch(Exception e){}fis=null;}
                }
            }
            else
            {
                Log.log(this, Log.WARN, "The folder 'var' was found, but it does not contain a valid file.");
            }
                        
            removeFolder(VAR_DIR + "/" + SMS_DIR);
            removeFolder(VAR_DIR);
        }

        return success;
    }
    
    /**
     * Returns the next Sms of the container. The Sms objects are sorted by the attribute
     * <code>smsTimestamp</code>. The oldest message will be returned first.
     * 
     * @return The oldest Sms object of the container.
     */
    public Sms getFirstSms()
    {
        Sms result = null;
        
        if(!content.isEmpty())
        {
            result = content.first();
        }
        
        return result;
    }
    
    /**
     * Returns true if there are some Sms objects in this container.
     * 
     * @return True if the container holds Sms objects, false otherwise.
     */
    public boolean hasContent()
    {
        return !(content.isEmpty());
    }
    
    /**
     * Returns true if there are some bad Sms objects in this container.
     * 
     * @return True if the container holds bad Sms objects, false otherwise.
     */
    public boolean hasBadMessages()
    {
        return !(badMessages.isEmpty());
    }
    
    /**
     * Returns true if this container does not contain any Sms objects.
     * 
     * @return True if the container is empty, false otherwise.
     */
    public boolean isEmpty()
    {
        return content.isEmpty();
    }
    
    /**
     * The method removes characters that are not valid for a phone number(blank, (, ), /, etc.).
     * 
     * @param mobile Phone number
     * @return String containing the clean phone number.
     * 
     * @throws Exception - NOT YET(may be remove this)
     */
    private String parsePhoneNumber(String mobile) throws Exception
    {
        StringBuffer sbMobile = new StringBuffer(mobile);
        StringBuffer sbTest = new StringBuffer("+0123456789");
        int i = 0;

        if (sbMobile.length() > 0)                                              // first char (can be +0123456789)
        {
            if (sbTest.indexOf(String.valueOf(sbMobile.charAt(i))) < 0)         // first char found in sbTest
                sbMobile.deleteCharAt(0);                                       // if not found
            else
                i++;
            
            sbTest.deleteCharAt(0);                                             // delete '+'
        }
        
        while (i < sbMobile.length())                                           // other chars (can be 0123456789)
        {
            if (sbTest.indexOf(String.valueOf(sbMobile.charAt(i))) < 0)         // char found in sbTest
            {
                sbMobile.deleteCharAt(i);                                       // if not found
                continue;                                                       // do not i++
            }
            i++;
        }
        return sbMobile.toString();
    }
    
    /**
     * Acknowledges a JMS message.
     * 
     * @param msg The JMS message that have to be ackowledged.
     * @return True if the message have been ackowledged, false otherwise.
     */
    private boolean acknowledge(Message msg)
    {
        try
        {
            msg.acknowledge();
            return true;
        }
        catch(Exception e)
        {
            Log.log(this, Log.FATAL, "could not acknowledge", e);
        }
        
        return false;
    }
    
    public String showContent()
    {
        StringBuffer result = new StringBuffer();
        
        Iterator<Sms> iter = content.descendingIterator();
        
        result.append("Inhalt des TreeSets:\n\n");
        while(iter.hasNext())
        {
            result.append(iter.next().toString() + "\n");
            result.append("\n--------------------------------------------------------\n");
        }
        
        result.append("\n");
        
        return result.toString();
    }
    
    /**
     * Creates a folder if it does not exist.
     * 
     * @param path
     * @return
     */
    private boolean createFolder(String path)
    {
        File folder = new File(path);
        boolean success = false;
        
        if(!folder.exists())
        {
            success = folder.mkdir();
        }
        else
        {
            success = true;
        }
        
        folder = null;
        
        return success;
    }
    
    /**
     * Removes the folder and all files the folder contains but it does NOT remove the folder if it
     * contains a sub-folder.
     * 
     * @param path
     */
    private void removeFolder(String path)
    {
        File folder = null;
        File[] listContent = null;
        boolean success = false;
        
        folder = new File(path);
        if(folder.exists()) {
            listContent = folder.listFiles();
        }
        
        if(listContent != null) {
            if(listContent.length == 0) {
                success = true;
            }
            
            for(File f : listContent) {
                // Do not delete a folder
                if(f.isDirectory() == false) {
                    success = f.delete();
                    if(!success) break;
                } else {
                    success = false;
                    break;
                }
            }
        }
        
        if(success) {
            folder.delete();
        }
    }
}
