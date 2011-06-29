
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

import java.io.IOException;
import org.csstudio.ams.Log;
import org.csstudio.ams.connector.voicemail.Telegram;
import org.csstudio.ams.connector.voicemail.speech.SpeechProducer;
import uk.co.mmscomputing.device.capi.CapiChannel;
import uk.co.mmscomputing.device.capi.CapiMetadata;
import uk.co.mmscomputing.device.capi.CapiServerApplication;
import uk.co.mmscomputing.device.capi.exception.CapiException;
import uk.co.mmscomputing.util.metadata.Metadata;
import uk.co.mmscomputing.util.metadata.MetadataListener;

/**
 * @author Markus Moeller
 *
 */
public class CapiReceiver extends Thread implements MetadataListener
{
    /** CAPI Interface for sending and receiving telephone calls */
    private CapiServerApplication receiver = null;
    
    /** Class that encapsulates the MaryClient */
    private SpeechProducer speech = null;
    
    /** Meta data for the CAPI client */
    private CapiMetadata md = null;
    
    /** CAPI channel object */
    private CapiChannel channel = null;
    
    /**  */
    private long statusChanged = 0;
    
    public CapiReceiver() throws CapiReceiverException
    {
        initSpeechProducer();
        refreshStatus();
        
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
            receiver = new CapiServerApplication(md);
            receiver.start();
        }
        catch(CapiException ce)
        {
            throw new CapiReceiverException(ce.getMessage());
        }
    }
    
    private void initSpeechProducer() throws CapiReceiverException
    {
        speech = SpeechProducer.getInstance();
        if(!speech.isConnected())
        {
            speech.closeAll();
            speech = null;
            
            throw new CapiReceiverException("Connection to MARY server failed.");
        }        
    }

    @Override
    public void run()
    {
        // TODO Auto-generated method stub
        
    }
    
    public Telegram readTelegram()
    {
        Telegram tel = null;
        
        try
        {
            channel = receiver.accept();
        }
        catch(InterruptedException ie)
        {
            // TODO Auto-generated catch block
            ie.printStackTrace();
        }
        finally
        {
            if(channel != null)
            {
                try{channel.close();}catch(IOException e) {/* Can be ignored */}
                channel = null;
            }
        }

        return tel;
    }
    
    public boolean isWorking()
    {
        return ((System.currentTimeMillis() - statusChanged) > 180000 ? false : true);
    }
    
    private void refreshStatus()
    {
        this.statusChanged = System.currentTimeMillis();
    }
    
    public void update(Object type, Metadata metadata)
    {
        Log.log(this, Log.DEBUG, type.toString());
    }
}
