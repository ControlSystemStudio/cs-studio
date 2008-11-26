
/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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

package org.csstudio.ams.connector.voicemail.isdn;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import org.csstudio.ams.connector.voicemail.speech.SpeechProducer;
import org.csstudio.platform.logging.CentralLogger;
import de.dfki.lt.signalproc.util.AudioConverterUtils;
import uk.co.mmscomputing.device.capi.CapiCallApplication;
import uk.co.mmscomputing.device.capi.CapiChannel;
import uk.co.mmscomputing.device.capi.CapiMetadata;
import uk.co.mmscomputing.device.capi.exception.CapiException;
import uk.co.mmscomputing.util.metadata.Metadata;
import uk.co.mmscomputing.util.metadata.MetadataListener;

/**
 * @author Markus Moeller
 *
 */
public class CapiCaller implements MetadataListener
{
    /** CAPI Interface for sending and receiving telephone calls */
    private CapiCallApplication caller = null;
    
    /** Meta data for the CAPI client */
    private CapiMetadata md = null;

    /** CAPI channel object */
    private CapiChannel channel = null;

    /** Class that encapsulates the MaryClient */
    private SpeechProducer speech = null;
    
    /** Common logger of CSS */
    private CentralLogger logger = null;
    
    /** Flag that indicates whether or not the object is busy(is making a call) */
    private boolean busy;
    
    public CapiCaller() throws CapiCallerException
    {
        logger = CentralLogger.getInstance();
        initSpeechProducer();
        initCapiApplication();
        busy = false;
    }
    
    private void initCapiApplication() throws CapiCallerException
    {
        md = new CapiMetadata();
        md.acceptAllCalls();
        
        // need only one connection
        md.useMaxLogicalConnections(1);
        
        // use first controller
        md.useController(1);
        
        // set some defaults
        md.useALaw();

        md.use64kBit();                                

        // want to listen
        md.addListener(this);
        
        try
        {
            caller = new CapiCallApplication(md);
            caller.start();
        }
        catch(CapiException ce)
        {
            throw new CapiCallerException(ce.getMessage());
        }
    }

    private void initSpeechProducer() throws CapiCallerException
    {
        speech = SpeechProducer.getInstance();
        if(!speech.isConnected())
        {
            speech.closeAll();
            speech = null;
            
            throw new CapiCallerException("Connection to MARY server failed.");
        }
    }
    
    public CallInfo makeCall(String telephoneNumber, String message, String textType) throws CapiCallerException
    {
        CallInfo callInfo = null;
        int type;
        
        try
        {
            type = Integer.parseInt(textType);
        }
        catch(NumberFormatException nfe)
        {
            type = 0;
            logger.warn(this, "Text type is invalid: " + textType);
        }
        
        if(type == 0)
        {
            throw new CapiCallerException("Text type is invalid: " + textType);
        }
        
        switch(type)
        {
            case 1: // TEXTTYPE_ALARM_WOCONFIRM
                
                callInfo = makeCallWithoutReply(telephoneNumber, message);
                break;
                
            case 2: // TEXTTYPE_ALARM_WCONFIRM
            case 3: // TEXTTYPE_ALARMCONFIRM_OK
            case 4: // TEXTTYPE_ALARMCONFIRM_NOK
            case 5: // TEXTTYPE_STATUSCHANGE_OK
            case 6: // TEXTTYPE_STATUSCHANGE_NOK                
        }
        
        
        return callInfo;
    }
    
    public CallInfo makeCallWithoutReply(String telephoneNumber, String message) throws CapiCallerException
    {
        CallInfo callInfo = null;
        AudioInputStream ais = null;
        ByteArrayOutputStream baos = null;
        boolean repeat = true;
        
        if(speech.getInputType().compareToIgnoreCase("text_de") == 0)
        {
            baos = speech.getAudioStream("Guten Tag. Dies ist eine Nachricht des Alarmsystems. Folgende Alarmmeldung wurde gesendet.");
        }
        else
        {
            baos = speech.getAudioStream("Hello. This is a message from the alarm system. The following message has been sent.");            
        }
        
        busy = true;
        
        try
        {
            logger.info(this, "Try connecting to " + telephoneNumber + ". Will wait for 60 sec.");
            
            // send connect request and wait for connection (max 60 sec.)
            channel = caller.connect(telephoneNumber, 60000);
            
            // waste input data
            channel.getInputStream().close();
            
            logger.info(this, "Connected to " + telephoneNumber);
            
            try
            {
                logger.info(this, "Try sending data to " + telephoneNumber);
                
                if(baos == null)
                {
                    busy = false;
                    
                    throw new CapiCallerException("Cannot create speech stream.");
                }
                
                ais = AudioSystem.getAudioInputStream(new ByteArrayInputStream(baos.toByteArray()));

                // write from in ==> channel
                channel.writeToOutput(AudioConverterUtils.downSampling(ais, 8000));
            }
            catch(Exception e)
            {
                logger.error(this, e.getMessage());
                
                throw new CapiCallerException(e.getMessage());
            }
            finally
            {                        
                if(ais!=null){try{ais.close();}catch(Exception e){}ais = null;}
                if(baos!=null){try{baos.close();}catch(Exception e){}baos = null;}
            }

            while(repeat)
            {
                // Send the alarm text
                try
                {
                    logger.info(this, "Try sending data to " + telephoneNumber);

                    baos = speech.getAudioStream(message);
                    
                    if(baos == null)
                    {
                        busy = false;
                        
                        throw new CallCenterException("Cannot create speech stream.");
                    }
                    
                    ais = AudioSystem.getAudioInputStream(new ByteArrayInputStream(baos.toByteArray()));

                    // write from in ==> channel
                    channel.writeToOutput(AudioConverterUtils.downSampling(ais, 8000));
                }
                catch(Exception e)
                {
                    logger.error(this, e.getMessage());
                    throw new CapiCallerException(e.getMessage());
                }
                finally
                {                        
                    if(ais!=null){try{ais.close();}catch(Exception e){}ais = null;}
                    if(baos!=null){try{baos.close();}catch(Exception e){}baos = null;}
                }
                
                try
                {
                    logger.info(this, "Try sending data to " + telephoneNumber);

                    baos = speech.getAudioStream("Benutzen Sie die Taste 1, wenn Sie den Text nochmal hören wollen.");
                    
                    if(baos == null)
                    {
                        busy = false;
                        
                        throw new CapiCallerException("Cannot create speech stream.");
                    }
                    
                    ais = AudioSystem.getAudioInputStream(new ByteArrayInputStream(baos.toByteArray()));

                    // write from in ==> channel
                    channel.writeToOutput(AudioConverterUtils.downSampling(ais, 8000));
                }
                catch(Exception e)
                {
                    logger.error(this, e.getMessage());
                    throw new CapiCallerException(e.getMessage());
                }
                finally
                {                        
                    if(ais!=null){try{ais.close();}catch(Exception e){}ais = null;}
                    if(baos!=null){try{baos.close();}catch(Exception e){}baos = null;}
                }

                channel.startDTMF();
                
                // wait for 'length' DTMF tones within 10 secs
                String dtmf = channel.getDTMFDigits(1, 10000);
                
                logger.info(this, "DTMF " + dtmf);
                
                if(dtmf.equals("1"))
                {
                    logger.info(this, "** REPEATING ** ");
                }
                else
                {
                    logger.info(this, "** DONE **");
                    
                    repeat = false;
                }
            }
            
            channel.close();
        }
        catch(Exception e)
        {
            logger.error(this, e.getMessage());
            throw new CapiCallerException(e.getMessage());
        }
        
        synchronized(caller)
        {
            try
            {
                logger.debug(this, "CapiCallApplication() is waiting...");
                caller.wait(5000);
            }
            catch(InterruptedException ie) {}
        }
        if(channel != null)
        {
            try{channel.close();}catch(IOException e) {}
            channel = null;
        }

        busy = false;

        return callInfo;
    }
    
    public boolean isBusy()
    {
        return busy;
    }

    public void update(Object type, Metadata metadata)
    {
        // TODO Auto-generated method stub        
    }
}
