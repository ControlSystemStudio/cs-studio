
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

package org.csstudio.ams.connector.voicemail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;

import org.csstudio.ams.Log;
import de.dfki.lt.mary.client.MaryClient;

/**
 * @author Markus Möller
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
    
    /** Flag that indicates wheather or not the server was connected */
    private boolean connected;
    
    /**
     * 
     * @param address
     * @param port
     */
    public SpeechProducer(String address, int port)
    {
        maryHost = address;
        maryPort = port;
        
        try
        {
            mary = new MaryClient(maryHost, maryPort);
            
            connected = true;
        }
        catch (IOException e)
        {
            Log.log(this, Log.ERROR, "Cannot init MARY client.");

            connected = false;
        }
    }
    
    public ByteArrayOutputStream getAudioStream(String text)
    {
        ByteArrayOutputStream baos = null;
        
        if(mary == null)
        {
            return null;
        }
        
        String inputType = "TEXT_DE";
        String outputType = "AUDIO";
        String audioType = "WAVE";
        String defaultVoiceName = null;
        
        baos = new ByteArrayOutputStream();
        
        try
        {
            mary.process(text, inputType, outputType, audioType,
                defaultVoiceName, baos);
        }
        catch(UnknownHostException uhe)
        {
            uhe.printStackTrace();
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
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
}
