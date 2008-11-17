
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
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import org.csstudio.ams.Log;
import org.csstudio.ams.connector.voicemail.VoicemailConnectorPlugin;
import org.csstudio.ams.connector.voicemail.internal.SampleService;
import org.csstudio.ams.connector.voicemail.speech.SpeechProducer;
import org.eclipse.jface.preference.IPreferenceStore;
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
public class CallCenter implements MetadataListener
{
    /** CAPI Interface for sending and receiving telephone calls */
    private CapiCallApplication appl = null;
    
    /** Class that encapsulates the MaryClient */
    private SpeechProducer speech = null;
    
    /** Flag that indicates whether or not the instance of this class is doing a calling */
    private boolean busy;
    
    public CallCenter() throws CallCenterException
    {
        busy = false;
        
        initSpeechProducer();
    }
    
    private void initSpeechProducer() throws CallCenterException
    {
        int port;

        IPreferenceStore store = VoicemailConnectorPlugin.getDefault().getPreferenceStore();
        String address = store.getString(SampleService.P_MARY_HOST);
        String inputType = store.getString(SampleService.P_MARY_DEFAULT_LANGUAGE);
        
        try
        {
            port = Integer.parseInt(store.getString(SampleService.P_MARY_PORT));
        }
        catch(NumberFormatException nfe)
        {
            port = 59125;
            Log.log(this, Log.WARN, "Cannot read the port for the MARY server. Using default port: " + port);
        }
        
        speech = new SpeechProducer(address, port, inputType);
        if(!speech.isConnected())
        {
            speech.closeAll();
            speech = null;
            
            throw new CallCenterException("Connection to MARY server failed.");
        }
    }

    public void makeCall(String receiver, String text) throws CallCenterException
    {
        AudioInputStream ais = null;
        ByteArrayOutputStream baos = null;
        
        baos = speech.getAudioStream("Guten Tag. Dies ist eine Nachricht des Alarmsystems. Folgende Alarmmeldung wurde gesendet. " + text);
        
        if(baos == null)
        {
            throw new CallCenterException("Cannot create speech stream.");
        }
        
        /*
        try
        {
            ais = AudioSystem.getAudioInputStream();
        }
        catch(UnsupportedAudioFileException uafe)
        {
            throw new CallCenterException(uafe.getMessage());
        }
        catch(IOException ioe)
        {
            throw new CallCenterException(ioe.getMessage());
        }
        finally
        {
            if(ais != null)
            {
                try{ais.close();}catch(Exception e){}
                ais = null;
            }
            
            if(baos != null)
            {
                try{baos.close();}catch(Exception e){}
                baos = null;
            }
        }
        */
        
        CapiMetadata md = new CapiMetadata();

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
            appl = new CapiCallApplication(md);
            
            // start capi thread
            appl.start();
            
            busy = true;
            
            try
            {
                System.err.println("Try connecting to " + receiver + ". Will wait for 10 sec.");
                
                // send connect request and wait for connection (max 10 sec.)
                CapiChannel channel = appl.connect(receiver, 10000);
                
                // waste input data
                channel.getInputStream().close();
                
                System.err.println("Connected to " + receiver);

                /*
                String  dtmfcode="0123";
                
                channel.startDTMF();
                String dtmf=channel.getDTMFDigits(dtmfcode.length(),60000);// wait for 'length' DTMF tones within 60secs
                System.err.println("DTMF "+dtmf+" ["+dtmfcode+"]");
                if(dtmfcode.equals(dtmf)){
                  System.out.println("\n\n\nSuccess "+dtmf+"\n\n\n");
                }else{
                  System.out.println("\n\n\nOps "+dtmf+"\n\n\n");
                }
                */
                
                try
                {
                    System.err.println("Try sending data to " + receiver);

                    ais = AudioSystem.getAudioInputStream(new ByteArrayInputStream(baos.toByteArray()));

                    // write from in ==> channel
                    channel.writeToOutput(AudioConverterUtils.downSampling(ais, 8000));
                }
                catch(Exception e)
                {
                    System.err.println(e.getMessage());
                }
                
                channel.close();
            }
            catch(Exception e)
            {
                System.err.println(e.getMessage());
            }
        
            // ais.close();
            appl.close();
        }
        catch(CapiException ce)
        {
            busy = false;
            
            throw new CallCenterException(ce.getMessage());
        }
        finally
        {
            busy = false;
        }
    }
    
    public boolean isBusy()
    {
        return busy;
    }

    public void update(Object type, Metadata metadata)
    {
        // disconnected -> close application
        if(type instanceof DisconnectInd)
        {
            System.err.println("End SpeechSend.");
        }
        else if(type instanceof Exception)
        {
            System.err.println(type);
            // System.err.println(((Exception)data).getMessage());
            ((Exception)type).printStackTrace();
        }
        else
        {
            System.err.println(type);
        }
    }
}
