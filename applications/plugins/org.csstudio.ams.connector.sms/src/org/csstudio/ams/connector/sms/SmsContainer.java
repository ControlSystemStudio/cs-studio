
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
import java.util.Iterator;
import java.util.TreeSet;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import org.csstudio.ams.AmsConstants;
import org.csstudio.ams.Log;

/**
 * @author Markus Moeller
 *
 */
public class SmsContainer implements AmsConstants
{
    /** Content of this container */
    private TreeSet<Sms> content;
    
    /** Id's for the Sms objects */
    private long smsId;
    
    
    /**
     * Standard constructor. Just creates a TreeSet object that holds the Sms objects.
     */
    public SmsContainer()
    {
        content = null;
        this.loadContent("./");
        if(content == null)
        {
            content = new TreeSet<Sms>(new SmsComperator());
            smsId = 1;
        }
    }
    
    /**
     * Creates a Sms object from the given MapMessage object. It acknowledges the JMS message.
     * The phone number is parsed(invalid characters will be removed).
     * 
     * @param message
     * @return At the moment a "nice" error number. Will be changed in future versions.
     */
    public int addSms(Message message)
    {
        Sms sms = null;
        String text = null;
        String recNo = null;
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
                text = msg.getString(MSGPROP_RECEIVERTEXT);
                recNo = msg.getString(MSGPROP_RECEIVERADDR);
                timestamp = msg.getJMSTimestamp();
                String parsedRecNo = null;
                
                if(!acknowledge(message))
                {
                    result = SmsConnectorStart.STAT_ERR_JMSCON;
                }
                else
                {            
                    if(parsedRecNo == null)
                    {
                        try
                        {
                            parsedRecNo = parsePhoneNumber(recNo);
                            sms = new Sms(smsId++, timestamp, parsedRecNo, text, Sms.Type.OUT);
                            content.add(sms);
                            
                            result = SmsConnectorStart.STAT_OK;
                        }
                        catch(Exception e)
                        {
                            Log.log(this, Log.FATAL, "Parsing phone number - failed.");
                            
                            // Although parsing failed, we have to return OK.
                            // Otherwise the application will be forced to restart.
                            result = SmsConnectorStart.STAT_OK;
                        }                   
                    }
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
     * Creates a special Sms object from the given MapMessage object that starts a modem test.
     * It takes the property EVENTTIME from the message and creates the string MODEM_CHECK{$EVENTTIME$}.
     * It acknowledges the JMS message.
     * 
     * @param message
     * @return At the moment a "nice" error number. Will be changed in future versions.
     */
    public int addModemtestSms(Message message)
    {
        Sms sms = null;
        String eventTime = null;
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
                eventTime = msg.getString("EVENTTIME");
                timestamp = msg.getJMSTimestamp();
                
                if(!acknowledge(message))
                {
                    result = SmsConnectorStart.STAT_ERR_JMSCON;
                }
                else
                {            
                    sms = new Sms(smsId++, timestamp, 1, "NONE", "MODEM_CHECK{" + eventTime + "}", Sms.Type.OUT);
                    content.add(sms);
                    
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

    /**
     * Stores the content(the Sms objects) of this container. The method adds the folder name 'var' to
     * the given path.
     * It also stores the current ID for the next Sms object. It is necessary to avoid conflicts with
     * stored Sms objects.
     * 
     * @param filename The path
     * @return True if the Sms objects were stored, false otherwise.
     */
    public boolean storeContent(String path)
    {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        File folder = null;
        String var = "var";
        Long idObj = null;
        boolean success = true;
        
        path = path.trim();
        if((!path.endsWith("/")) && (!path.endsWith("\\")))
        {
            var = "/" + var;
        }
        
        path = path + var;
        
        folder = new File(path);
        if(!folder.exists())
        {
            success = folder.mkdir();
        }
        
        folder = null;
        
        // success is true if the folder yet exists OR the folder was just created
        if(success)
        {
            try
            {
                fos = new FileOutputStream(path + "/sms-container.ser");
                oos = new ObjectOutputStream(fos);
                
                oos.writeObject(content);
                
                if(oos!=null){try{oos.close();}catch(Exception e){}oos=null;}
                if(fos!=null){try{fos.close();}catch(Exception e){}fos=null;}

                fos = new FileOutputStream(path + "/sms-id.ser");
                oos = new ObjectOutputStream(fos);
                
                idObj = new Long(smsId);
                
                oos.writeObject(idObj);
                
                idObj = null;
                
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
                if(oos!=null){try{oos.close();}catch(Exception e){}oos=null;}
                if(fos!=null){try{fos.close();}catch(Exception e){}fos=null;}
            }
        }
        else
        {
            Log.log(this, Log.ERROR, "Cannot create folder to store Sms objects.");
        }
        
        return success;
    }
    
    /**
     * Loads stored Sms objects. The method adds the folder name 'var' to the given path.
     * It also loads the current ID for the next Sms object. It is necessary to avoid conflicts with
     * stored Sms objects.
     * The folder 'var' will be deleted after loading.
     * 
     * @param path The path
     * @return True if the stored objects were loaded, false if no objects were found OR an error occured.
     */
    @SuppressWarnings("unchecked")
    public boolean loadContent(String path)
    {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        File folder = null;
        File smsFile = null;
        File idFile = null;
        String var = "var";
        Long idObj = null;
        boolean success = false;
        
        path = path.trim();
        if((!path.endsWith("/")) && (!path.endsWith("\\")))
        {
            var = "/" + var;
        }
        
        path = path + var;
        
        folder = new File(path);
        if(folder.exists())
        {
            smsFile = new File(path + "/sms-container.ser");
            idFile = new File(path + "/sms-id.ser");
            
            if(smsFile.exists() && idFile.exists())
            {
                try
                {
                    fis = new FileInputStream(smsFile);
                    ois = new ObjectInputStream(fis);
                    
                    content = (TreeSet<Sms>)ois.readObject();
                    
                    if(ois!=null){try{ois.close();}catch(Exception e){}ois=null;}
                    if(fis!=null){try{fis.close();}catch(Exception e){}fis=null;}

                    fis = new FileInputStream(idFile);
                    ois = new ObjectInputStream(fis);
                    
                    idObj = (Long)ois.readObject();
                    
                    this.smsId = idObj.longValue();
                    
                    idObj = null;
                    
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
                Log.log(this, Log.WARN, "The folder 'var' was found, but it does not contain two valid files.");
            }
            
            if(idFile.exists())
            {
                idFile.delete();
                idFile = null;
            }
            
            if(smsFile.exists())
            {
                smsFile.delete();
                smsFile = null;
            }
            
            if(folder.exists())
            {
                folder.delete();
                folder = null;
            }
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
        
        result.append("Inhalt des TreeSets:\n");
        while(iter.hasNext())
        {
            result.append(iter.next().toString() + "\n");
        }
        
        result.append("\n");
        
        return result.toString();
    }
}
