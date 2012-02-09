
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

package org.csstudio.ams.delivery.voicemail.isdn;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import org.csstudio.ams.delivery.voicemail.speech.SpeechProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.lt.signalproc.util.AudioConverterUtils;
import uk.co.mmscomputing.device.capi.CapiCallApplication;
import uk.co.mmscomputing.device.capi.CapiChannel;
import uk.co.mmscomputing.device.capi.CapiMetadata;
import uk.co.mmscomputing.device.capi.exception.CapiException;
import uk.co.mmscomputing.device.capi.plc.DisconnectInd;
import uk.co.mmscomputing.util.metadata.Metadata;
import uk.co.mmscomputing.util.metadata.MetadataListener;

/**
 * @author Markus Moeller
 *
 */
public class CapiCaller implements MetadataListener {
    
    private static final Logger LOG = LoggerFactory.getLogger(CapiCaller.class);
    
    /** CAPI Interface for sending and receiving telephone calls */
    private CapiCallApplication caller = null;
    
    /** Meta data for the CAPI client */
    private CapiMetadata md = null;

    /** CAPI channel object */
    private CapiChannel channel = null;

    /** Class that encapsulates the MaryClient */
    private SpeechProducer speech = null;
    
    /** Flag that indicates whether or not the object is busy(is making a call) */
    private boolean busy;
    
    public CapiCaller() throws CapiCallerException {
        initSpeechProducer();
        initCapiApplication();
        busy = false;
    }
    
    private void initCapiApplication() throws CapiCallerException {
        
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
        
        try {
            caller = new CapiCallApplication(md);
            caller.start();
        } catch(CapiException ce) {
            throw new CapiCallerException(ce.getMessage());
        }
    }

    private void initSpeechProducer() throws CapiCallerException {
        
        speech = SpeechProducer.getInstance();
        if(!speech.isConnected()) {
            speech.closeAll();
            speech = null;
            throw new CapiCallerException("Connection to MARY server failed.");
        }
    }
    
    public CallInfo makeCallWithoutReply(String telephoneNumber, String message) throws CapiCallerException {
        
        CallInfo callInfo = new CallInfo(telephoneNumber, TextType.ALARM_WOCONFIRM);
        ByteArrayOutputStream baos = null;
        String key = null;
        boolean repeat = true;
        
        if(speech.getInputType().compareToIgnoreCase("text_de") == 0) {
            baos = speech.getAudioStream("Guten Tag. Dies ist eine Nachricht des Alarmsystems. Benutzen Sie die Taste 1, um den Text zu hören.");
        } else {
            baos = speech.getAudioStream("Hello. This is a message from the alarm system.  Use key 1 to hear the text.");            
        }
        
        busy = true;
        
        try {
            
            LOG.info("Try connecting to " + telephoneNumber + ". Will wait for 60 sec.");
            
            // send connect request and wait for connection (max 60 sec.)
            channel = caller.connect(telephoneNumber, 60000);
            
            if(!channel.isConnected()) {
                
                busy = false;
                
                synchronized(caller) {
                    try {
                        LOG.debug("CapiCallApplication() is waiting...");
                        caller.wait(5000);
                    } catch(InterruptedException ie) {
                        // Can be ignored
                    }
                }

                return callInfo;
            }
            
            // waste input data
            channel.getInputStream().close();
            
            LOG.info("Connected to " + telephoneNumber);
            
            // Send first text
            writeStream(baos);

            // Wait until key 1 have been pressed
            key = getMenuChoice();
            if (key.compareTo("1") != 0) {
                
                busy = false;
                
                synchronized(caller) {
                    try {
                        LOG.debug("CapiCallApplication() is waiting...");
                        caller.wait(5000);
                    } catch(InterruptedException ie) {
                        // Can be ignored
                    }
                }
                
                if (channel != null) {
                    try{channel.close();}catch(IOException ioe) {
                    // Can be ignored
                    }
                    channel=null;
                }
                
                return callInfo;
            }

            while (repeat) {
                
                // Send the alarm text
                writeStream(message);

                // Send info / menu text
                if(speech.getInputType().compareToIgnoreCase("text_de") == 0) {
                    writeStream("Benutzen Sie die Taste 1, wenn Sie den Text nochmal hören wollen.");
                } else {
                    writeStream("Use key 1, if you want to hear the text again.");
                }
                
                // wait for 'length' DTMF tones within 10 secs
                key = getMenuChoice();
                
                if(key.equals("1")) {
                    LOG.debug("** REPEATING ** ");
                } else {
                    LOG.debug("** DONE **");
                    repeat = false;
                }
            }
            
            if(speech.getInputType().compareToIgnoreCase("text_de") == 0) {
                writeStream("Danke. Auf Wiederhören.");
            } else {
                writeStream("Thank you. Good bye.");
            }
        } catch(Exception e) {
            busy = false;
            LOG.error("[*** Exception ***]: {}", e.getMessage());
            throw new CapiCallerException(e.getMessage());
        } finally {
            if(channel != null) {
                try{channel.close();}catch(IOException e) {
                    // Can be ignored
                }
                channel = null;
            }
        }
        
        synchronized(caller) {
            try {
                LOG.debug("CapiCallApplication() is waiting...");
                caller.wait(5000);
            } catch(InterruptedException ie) {
                // Can be ignored
            }
        }

        busy = false;
        callInfo.setSuccess(true);

        return callInfo;
    }
    
    /**
     * 
     * @param telephoneNumber
     * @param message
     * @param chainIdAndPos
     * @return CallInfo object
     * @throws CapiCallerException
     */
    public CallInfo makeCallWithReply(String telephoneNumber,
                                      String message,
                                      String chainIdAndPos) throws CapiCallerException {
        
        CallInfo callInfo = new CallInfo(telephoneNumber, TextType.ALARM_WCONFIRM, chainIdAndPos);
        ByteArrayOutputStream baos = null;
        String confirm = null;
        String key = null;
        boolean repeat = true;
        
        if(speech.getInputType().compareToIgnoreCase("text_de") == 0) {
            baos = speech.getAudioStream("Guten Tag. Dies ist eine Nachricht des Alarmsystems. Benutzen Sie die Taste 1, um den Text zu hören.");
        } else {
            baos = speech.getAudioStream("Hello. This is a message from the alarm system. Use key 1 to hear the text.");            
        }
        
        busy = true;
        
        try {
            
            LOG.info("Try connecting to " + telephoneNumber + ". Will wait for 60 sec.");
            
            // send connect request and wait for connection (max 60 sec.)
            channel = caller.connect(telephoneNumber, 60000);
            
            if(!channel.isConnected()) {
                
                busy = false;
                
                synchronized(caller) {
                    try {
                        LOG.debug("CapiCallApplication() is waiting...");
                        caller.wait(5000);
                    } catch(InterruptedException ie) {
                        // Can be ignored
                    }
                }

                return callInfo;
            }

            // waste input data
            channel.getInputStream().close();
            
            LOG.info("Connected to " + telephoneNumber);
            
            //TODO
            // Send first text
            writeStream(baos);

            // Wait until key 1 have been pressed
            key = getMenuChoice();
            if(key.compareTo("1") != 0) {
                
                busy = false;
                
                synchronized(caller) {
                    try {
                        LOG.debug("CapiCallApplication() is waiting...");
                        caller.wait(5000);
                    }
                    catch(InterruptedException ie) {
                        // Can be ignored
                    }
                }
                
                if(channel!=null) {
                    try {
                        channel.close();
                    } catch (IOException ioe) {
                        // Can be ignored
                    }
                    channel=null;
                }
                
                return callInfo;
            }
            
            // Send the alarm text and repeat it if necessary
            while(repeat) {
                
                writeStream(message);
                
                if(speech.getInputType().compareToIgnoreCase("text_de") == 0) {
                    writeStream("Benutzen Sie die Taste 1, wenn Sie den Text nochmal hören wollen.");
                } else {
                    writeStream("Use key 1, if you want to hear the text again.");
                }
                
                // wait for 'length' DTMF tones within 10 secs
                key = getMenuChoice();
                
                if(key.equals("1")) {
                    LOG.debug("** REPEATING ** ");
                } else {
                    LOG.debug("** DONE **");
                    repeat = false;
                }
            }
            
            // Confirmation code
            if(speech.getInputType().compareToIgnoreCase("text_de") == 0) {
                writeStream("Geben Sie Ihren BestÃ¤tigungsnummer ein und drücken Sie dann die Rautetaste.");
            } else {
                writeStream("Type in your confirmation code and press the hash or pound key.");
            }
            
            confirm = getConfirmationCode();
            
            if(confirm != null) {
                callInfo.setConfirmationCode(confirm);
            }
            
            //TODO:
            if(speech.getInputType().compareToIgnoreCase("text_de") == 0) {
                writeStream("Danke. Auf Wiederhören.");
            } else {
                writeStream("Thank you. Good bye.");
            }
        } catch(Exception e) {
            busy = false;
            LOG.error("[*** Exception ***]: {}", e.getMessage());
            throw new CapiCallerException(e.getMessage());
        } finally {
            if(channel!=null){try{channel.close();}catch(IOException ioe){/* Can be ignored */}channel=null;}
        }
        
        synchronized(caller) {
            try {
                LOG.debug("CapiCallApplication() is waiting...");
                caller.wait(5000);
            } catch(InterruptedException ie) {/* Can be ignored */}
        }

        busy = false;
        callInfo.setSuccess(true);
        
        return callInfo;
    }
    
    private void writeStream(ByteArrayOutputStream data) throws CapiCallerException {
        
        AudioInputStream ais = null;
        
        if(channel == null) {
            return;
        }

        try {
            
            LOG.debug("Try sending data");
            
            ais = AudioSystem.getAudioInputStream(new ByteArrayInputStream(data.toByteArray()));

            // write from in ==> channel
            channel.writeToOutput(AudioConverterUtils.downSampling(ais, 8000));
        } catch(Exception e) {
            LOG.error("[*** Exception ***]: {}", e.getMessage());
            throw new CapiCallerException(e.getMessage());
        } finally {                        
            if(ais!=null){try{ais.close();}catch(Exception e){/* Can be ignored */}ais = null;}
            if(data!=null){try{data.close();}catch(Exception e){/* Can be ignored */}}
        }
    }
    
    private void writeStream(String text) throws CapiCallerException {
        
        ByteArrayOutputStream baos = null;
        AudioInputStream ais = null;
        
        if(channel == null) {
            return;
        }
        
        try {
            
            LOG.debug("Try sending data");

            baos = speech.getAudioStream(text);
            if(baos == null) {
                throw new CapiCallerException("Cannot create speech stream.");
            }
            
            ais = AudioSystem.getAudioInputStream(new ByteArrayInputStream(baos.toByteArray()));

            // write from in ==> channel
            channel.writeToOutput(AudioConverterUtils.downSampling(ais, 8000));
        } catch(Exception e) {
            LOG.error("[*** Exception ***]: {}", e.getMessage());
            throw new CapiCallerException(e.getMessage());
        } finally {                        
            if(ais!=null){try{ais.close();}catch(Exception e){/* Can be ignored */}ais = null;}
            if(baos!=null){try{baos.close();}catch(Exception e){/* Can be ignored */}baos = null;}
        }
    }
    
    private String getConfirmationCode() throws IOException {
        
        String code = "";
        String key = null;
        
        channel.startDTMF();
        
        do {
            
            // wait for 'length' DTMF tones within 10 secs
            try {
                // wait for 'length' DTMF tones within 10 secs
                key = channel.getDTMFDigits(1, 10000);
                
                LOG.debug("DTMF " + key);
            } catch(InterruptedException ie) {/* Can be ignored */}

            if(key != null) {
                if(key.trim().compareToIgnoreCase("#") != 0) {
                    code = code + key;
                }
            } else {
                key = "";
            }
            
            LOG.debug("DTMF " + key);
            
        } while(key.trim().compareToIgnoreCase("#") != 0);
       
        channel.stopDTMF();

        return code;
    }
    
    public String getMenuChoice() throws IOException {
        
        String key = null;
        
        channel.startDTMF();

        try {
            // wait for 'length' DTMF tones within 10 secs
            key = channel.getDTMFDigits(1, 10000);
            
            LOG.debug("DTMF " + key);
        } catch(InterruptedException ie) {/* Can be ignored */}

        channel.stopDTMF();
        
        return key;
    }
    
    public boolean isBusy() {
        return busy;
    }

    @Override
    public void update(Object type, Metadata metadata) {
        
        // disconnected
        if(type instanceof DisconnectInd) {
            
            if(channel != null) {
                
                if(channel.isDTMFEnabled()) {
                    
                    LOG.debug("isDTMFEnabled() = true");
                    
                    try{channel.stopDTMF();}catch(IOException e) {
                        LOG.debug(e.getMessage());
                    }
                }
            }
            
            LOG.debug("Disconnected");
        } else if(type instanceof Exception) {
            LOG.debug(type.toString(), (Exception)type);
        } else {
            LOG.debug(type.toString());
        }
    }
    
    public void close() {
        
        if (speech != null) {
            speech.closeAll();
        }
        
        if (caller != null) {
            caller.close();
        }
    }
}
