
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
 *
 */

package de.desy.aapi;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * TODO: Überarbeiten
 * @author Markus Moeller
 */
public class RequestData implements Serializable {
    
    /** Generated serial version id */
    private static final long serialVersionUID = 7895218395892414206L;
    
    /** */
    private int fromTime; 
    
    /** */
    private int uFromTime;
    
    /** */
    private int toTime;
    
    /** */
    private int uToTime; 
    
    /** */
    private int num;
    
    /** */
    private int conversionTag;
    
    /** */
    private double conversParam;
    
    /** */
    private int pvSize;
    
    /** */
    private String[] PV;

    /**
     * @return the fromTime
     */
    public int getFromTime() {
        return fromTime;
    }

    /**
     * @param fromTime the fromTime to set
     */
    public void setFromTime(int fromTime) {
        this.fromTime = fromTime;
    }

    /**
     * @return the uFromTime
     */
    public int getUFromTime() {
        return uFromTime;
    }

    /**
     * @param uFromTime the uFromTime to set
     */
    public void setUFromTime(int uFromTime) {
        this.uFromTime = uFromTime;
    }

    /**
     * @return the toTime
     */
    public int getToTime() {
        return toTime;
    }

    /**
     * @param toTime the toTime to set
     */
    public void setToTime(int toTime) {
        this.toTime = toTime;
    }

    /**
     * @return the uToTime
     */
    public int getUToTime() {
        return uToTime;
    }

    /**
     * @param uToTime the uToTime to set
     */
    public void setUToTime(int uToTime) {
        this.uToTime = uToTime;
    }

    /**
     * @return the num
     */
    public int getNum() {
        return num;
    }

    /**
     * @param num the num to set
     */
    public void setNum(int num) {
        this.num = num;
    }

    /**
     * @return the conversionTag
     */
    public int getConversionTag() {
        return conversionTag;
    }

    /**
     * @param conversionTag the conversionTag to set
     */
    public void setConversionTag(int conversionTag) {
        this.conversionTag = conversionTag;
    }

    /**
     * @return the conversParam
     */
    public double getConversParam() {
        return conversParam;
    }

    /**
     * @param conversParam the conversParam to set
     */
    public void setConversParam(double conversParam) {
        this.conversParam = conversParam;
    }

    /**
     * @return the pvSize
     */
    public int getPvSize() {
        return pvSize;
    }

    /**
     * @param pvSize the pvSize to set
     */
    public void setPvSize(int pvSize) {
        this.pvSize = pvSize;
    }

    /**
     * @return the pV
     */
    public String[] getPV() {
        return PV;
    }

    /**
     * @param pV the pV to set
     */
    public void setPV(String[] pv) {
        PV = pv;
    }
    
    /**
     * Preparing byteArray package for sending over TCP/IP
     */
    public byte[] buildPacketFromData(int cmd) throws AapiException {
        
    	byte[] packet = null;
    	
        try {
        	
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            DataOutputStream dout = new DataOutputStream(bout);
            dout.writeInt(fromTime);
            dout.writeInt(uFromTime);
            dout.writeInt(toTime);
            dout.writeInt(uToTime);
            dout.writeInt(num);
            dout.writeInt(conversionTag);
            dout.writeDouble(conversParam);
            dout.writeInt(PV.length);
       
            for(int i = 0;i < PV.length;i++) {
                char[] strArr=PV[i].toCharArray();
                for(int j=0;j<strArr.length;j++) dout.writeByte(strArr[j]);          
                dout.writeChar('\0');
            }
    
            packet = bout.toByteArray();
        } catch(IOException ioe) {
            throw new AapiException("AAPI-server send error: " + ioe.getMessage());
        }
        
        return packet;
    }

    /**
     * AAPI header length claculation
     */
    public  int calculateLength() {
        
        int len = AAPI.REQUEST_HEADER_SIZE * AAPI.BYTE_SIZE;
        for(int i = 0; i < PV.length;i++) {
            len += ((PV[i]).length() + 1);
        }
        
        return len;
    }
}
