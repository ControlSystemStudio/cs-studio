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
 
package org.csstudio.ams.connector.sms.softmodem;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import org.smslib.InboundMessage;
import org.smslib.OutboundMessage;
// import org.smslib.CIncomingMessage;
// import org.smslib.COutgoingMessage;

/**
 * First approach to a kind of software GSM modem.
 * 
 * Status: Not usable yet...
 * 
 * @author Markus Moeller
 *
 */

@SuppressWarnings("unused")
public class CSoftwareService
{
    private CSoftwareDeviceInfo deviceInfo = null;
    private FileWriter fileWriter = null;
    private BufferedWriter msgOutput = null;
    private String strComPort = null;
    private String strManufac = null;
    private String strModel = null;
    private String strSimPin = null;
    private boolean connected = false;
    
    private int iBaudRate = 0;
    
    private final String BASE_DIR = "C:/";
    private final String MESSAGE_DIR = "Temp/SoftModem/";
    
    public CSoftwareService(String comPort, int baudRate, String manufacturer, String model)
    {
        deviceInfo = new CSoftwareDeviceInfo();
        strComPort = comPort;
        iBaudRate = baudRate;
        strManufac = manufacturer;
        strModel = model;
    }
    
    public void setSimPin(String simPin)
    {
        strSimPin = simPin;
    }
    
    public void connect() throws Exception
    {
        try
        {
            fileWriter = new FileWriter(BASE_DIR + MESSAGE_DIR + "Softmodem.txt");
            msgOutput = new BufferedWriter(fileWriter);
        }
        catch(IOException ioe)
        {
            try
            {
                msgOutput.close();
                fileWriter.close();
            }
            catch(Exception e) {}
            
            throw new Exception(ioe.getMessage());
        }
        
        connected = true;
    }

    public boolean getConnected()
    {
        return connected;
    }

    public void disconnect()
    {
        if(msgOutput!=null){try{msgOutput.close();}catch(IOException e){}}
        if(fileWriter!=null){try{fileWriter.close();}catch(IOException e){}}
        
        connected = false;
    }

    public CSoftwareDeviceInfo getDeviceInfo()
    {
        return deviceInfo;
    }

    public void sendMessage(OutboundMessage msg) throws Exception
    {
        msgOutput.write(msg.getText());
        msgOutput.newLine();
        msgOutput.flush();
        
        deviceInfo.getStatistics().incTotalOut();
    }

    public void deleteMessage(InboundMessage msg)
    {        
    }

    public void readMessages(LinkedList<InboundMessage> msgList, int all, int limit)
    {
        synchronized(this)
        {
            try
            {
                this.wait(1000);
            }
            catch(InterruptedException ie) {}
        }
    }
}
