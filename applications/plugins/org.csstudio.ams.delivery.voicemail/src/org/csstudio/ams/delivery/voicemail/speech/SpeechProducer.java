
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

package org.csstudio.ams.delivery.voicemail.speech;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import marytts.client.MaryClient;
import marytts.client.http.Address;

import org.csstudio.ams.delivery.voicemail.VoiceMailDeliveryActivator;
import org.csstudio.ams.delivery.voicemail.internal.VoicemailConnectorPreferenceKey;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Markus Moeller
 *
 */
public class SpeechProducer {
    
    private static final Logger LOG = LoggerFactory.getLogger(SpeechProducer.class);
    
    /** Client class for the MARY TTS server */
    private MaryClient mary;
    
    /** Address of MARY server */
    private String maryHost;
    
    /** Port of MARY server */
    private int maryPort;

    /** Default input type (de, en-US) */
    private String local;

    /** Flag that indicates wheather or not the server was connected */
    private boolean connected;
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private SimpleDateFormat df = new SimpleDateFormat("dd. MMMMM yyyy HH:mm:ss");

    private static final String regEx = "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}.[0-9]{3}";

    private static SpeechProducer instance = null;
    
    /**
     * 
     * @param address
     * @param port
     */
    private SpeechProducer() {
        
        IPreferencesService prefs = Platform.getPreferencesService();
        String address = prefs.getString(VoiceMailDeliveryActivator.PLUGIN_ID,
                                         VoicemailConnectorPreferenceKey.P_MARY_HOST,
                                         "localhost",
                                         null);
        String language = prefs.getString(VoiceMailDeliveryActivator.PLUGIN_ID,
                                      VoicemailConnectorPreferenceKey.P_MARY_DEFAULT_LANGUAGE,
                                      "de",
                                      null);
        int port = prefs.getInt(VoiceMailDeliveryActivator.PLUGIN_ID,
                                VoicemailConnectorPreferenceKey.P_MARY_PORT,
                                59125,
                                null);
      
        LOG.info("Port for the MARY server: {}", port);

        maryHost = address;
        maryPort = port;
        this.local = language;
        
        try {
            mary = MaryClient.getMaryClient(new Address(maryHost, maryPort));
            connected = true;
        } catch(IOException e) {
            LOG.error("Cannot init MARY client: {}", e.getMessage());
            connected = false;
        }
    }
    
    public static synchronized SpeechProducer getInstance() {
        if(instance == null) {
            instance = new SpeechProducer();
        }
        return instance;
    }
    
    public ByteArrayOutputStream getAudioStream(String checkText) {
        
        ByteArrayOutputStream baos = null;
        
        if(mary == null) {
            return null;
        }
        
        String text = workOnText(checkText);
        
        String inputType = "TEXT";
        String outputType = "AUDIO";
        String audioType = "WAVE";
        String defaultVoiceName = null;
        
        baos = new ByteArrayOutputStream();
        
        try {
            mary.process(text, inputType, outputType, local, audioType, defaultVoiceName, baos);
        } catch(UnknownHostException uhe) {
            LOG.error("[*** UnknownHostException ***]: " + uhe.getMessage());
            baos = null;
        } catch(IOException ioe) {
            LOG.error("[*** IOException ***]: " + ioe.getMessage());
            baos = null;
        }
        
        if(baos == null) {
            baos = readErrorSpeechText();
        }
        
        return baos;
    }
    
    private String workOnText(String text) {
        
        StringBuffer newText = new StringBuffer();
        
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(text);
        
        Date d = null;
        
        int begin = 0;
        int start = 0;
        int ende = text.length();
        
        while(m.find(start)) {
            
            start = m.start();
            ende = m.end();
            
            /*System.out.println("Start: " + start);
            System.out.println("Ende:  " + ende);
            System.out.println("Part:  [" + text.substring(m.start(), m.end()) + "]");
            */
            
            newText.append(text.substring(begin, start));
            
            try {
                d = sdf.parse(text.substring(m.start(), m.end()));
                newText.append(df.format(d) + " Uhr");
            } catch(ParseException e) {
                LOG.error("[*** ParseException ***]: " + e.getMessage());
                newText.append(" Übersetzungsfehler");
            }
            
            begin = ende;
            start = m.end();
        }
        
        newText.append(text.substring(begin));
        
        return newText.toString();
    }

    public ByteArrayOutputStream readErrorSpeechText() {
        
        ByteArrayOutputStream baos = null;
        FileInputStream file = null;
        byte[] audio = new byte[65536];
        int read = 0;
        
        try {
            file = new FileInputStream("./speech/error_server_offline.wav");
            baos = new ByteArrayOutputStream();
            while(file.available() > 0) {
                read = file.read(audio);
                baos.write(audio, 0, read);
                LOG.debug("read = " + read);
            }
        } catch(FileNotFoundException fnfe) {
            LOG.error("[*** FileNotFoundException ***]: " + fnfe.getMessage());
        } catch(IOException ioe) {
            LOG.error("[*** IOException ***]: " + ioe.getMessage());
            
            if(baos != null) {
                try{baos.close();}catch(IOException e){ /* Ignore Me */ }                
                baos = null;
            }                       
        } finally {
            if(file != null) {
                try{file.close();}catch(IOException e){ /* Ignore Me */ }                
                file = null;
            }             
        }
        
        return baos;
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    public void closeAll() {
        mary = null;
    }

    public String getLocal() {
        return local;
    }
}
