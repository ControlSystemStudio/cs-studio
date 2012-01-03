
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

package org.csstudio.ams.connector.voicemail.speech;

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
import org.csstudio.ams.Log;
import org.csstudio.ams.connector.voicemail.VoicemailConnectorPlugin;
import org.csstudio.ams.connector.voicemail.internal.VoicemailConnectorPreferenceKey;
import org.eclipse.jface.preference.IPreferenceStore;

import de.dfki.lt.mary.client.MaryClient;

/**
 * @author Markus Moeller
 *
 */
public class SpeechProducer
{
    /** Client class for the MARY TTS server */
    private MaryClient mary = null;
    
    /** Address of MARY server */
    private String maryHost = null;
    
    /** Port of MARY server */
    private int maryPort = 0;

    /** Default input type (TEXT_DE, TEXT_EN) */
    private String inputType = null;

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
    private SpeechProducer()
    {
        IPreferenceStore store = VoicemailConnectorPlugin.getDefault().getPreferenceStore();
        String address = store.getString(VoicemailConnectorPreferenceKey.P_MARY_HOST);
        String inputType = store.getString(VoicemailConnectorPreferenceKey.P_MARY_DEFAULT_LANGUAGE);
        int port = 0;
        
        try
        {
            port = Integer.parseInt(store.getString(VoicemailConnectorPreferenceKey.P_MARY_PORT));
        }
        catch(NumberFormatException nfe)
        {
            port = 59125;
            Log.log(this, Log.WARN, "Cannot read the port for the MARY server. Using default port: " + port);
        }

        maryHost = address;
        maryPort = port;
        this.inputType = inputType;
        
        try
        {
            mary = new MaryClient(maryHost, maryPort);

            connected = true;
        }
        catch(IOException e)
        {
            Log.log(this, Log.ERROR, "Cannot init MARY client.");

            connected = false;
        }
    }
    
    public static synchronized SpeechProducer getInstance()
    {
        if(instance == null)
        {
            instance = new SpeechProducer();
        }
        
        return instance;
    }
    
    public ByteArrayOutputStream getAudioStream(String text)
    {
        ByteArrayOutputStream baos = null;
        
        if(mary == null)
        {
            return null;
        }
        
        text = workOnText(text);
        
        String outputType = "AUDIO";
        String audioType = "WAVE";
        String defaultVoiceName = null;
        
        baos = new ByteArrayOutputStream();
        
        try
        {
            mary.process(text, inputType, outputType, audioType, defaultVoiceName, baos);
        }
        catch(UnknownHostException uhe)
        {
            Log.log(Log.ERROR, " *** UnknownHostException *** : " + uhe.getMessage());
            
            baos = null;
        }
        catch(IOException ioe)
        {
            Log.log(Log.ERROR, " *** IOException *** : " + ioe.getMessage());
            
            baos = null;
        }
        
        if(baos == null)
        {
            baos = readErrorSpeechText();
        }
        
        return baos;
    }
    
    private String workOnText(String text)
    {
        StringBuffer newText = new StringBuffer();
        
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(text);
        
        Date d = null;
        
        int begin = 0;
        int start = 0;
        int ende = text.length();
        
        while(m.find(start))
        {
            start = m.start();
            ende = m.end();
            
            /*System.out.println("Start: " + start);
            System.out.println("Ende:  " + ende);
            System.out.println("Part:  [" + text.substring(m.start(), m.end()) + "]");
            */
            
            newText.append(text.substring(begin, start));
            
            try
            {
                d = sdf.parse(text.substring(m.start(), m.end()));

                newText.append(df.format(d) + " Uhr");
            }
            catch(ParseException e)
            {
                Log.log(this, Log.ERROR, " *** ParseException *** : " + e.getMessage());
                
                newText.append(" Übersetzungsfehler");
            }
            
            begin = ende;
            start = m.end();
        }
        
        newText.append(text.substring(begin));
        
        return newText.toString();
    }

    public ByteArrayOutputStream readErrorSpeechText()
    {
        ByteArrayOutputStream baos = null;
        FileInputStream file = null;
        byte[] audio = new byte[65536];
        int read = 0;
        
        try
        {
            file = new FileInputStream("./speech/error_server_offline.wav");
            baos = new ByteArrayOutputStream();
            while(file.available() > 0)
            {
                read = file.read(audio);
                baos.write(audio, 0, read);
                Log.log(Log.DEBUG, "read = " + read);
            }
        }
        catch(FileNotFoundException fnfe)
        {
            Log.log(this, Log.ERROR, " *** FileNotFoundException *** : " + fnfe.getMessage());
        }
        catch(IOException ioe)
        {
            Log.log(this, Log.ERROR, " *** IOException *** : " + ioe.getMessage());
            
            if(baos != null)
            {
                try{baos.close();}catch(IOException e){ }                
                baos = null;
            }                       
        }
        finally
        {
            if(file != null)
            {
                try{file.close();}catch(IOException e){ }                
                file = null;
            }             
        }
        
        return baos;
    }
    
    public boolean isConnected()
    {
        return connected;
    }
    
    public void closeAll()
    {
        mary = null;
    }

    public String getInputType()
    {
        return inputType;
    }
}
