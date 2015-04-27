
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
import java.util.ArrayList;
import java.util.List;

/**
 * @author Markus Moeller
 */
public class RequestData implements Serializable {
    
    /** Generated serial version id */
    private static final long serialVersionUID = 7895218395892414206L;
    
    /** Start of time interval */
    private int fromTime; 
    
    /** Micro seconds of the start time */
    private int uFromTime;
    
    /** End of time interval */
    private int toTime;
    
    /** Micro seconds of the end time */
    private int uToTime; 
    
    /** Number of requested data samples */
    private int numberOfSamples;
    
    /**
     *  Number of the used conversion method.
     *  To get this number use the enum {@link AapiReductionMethod#getMethodNumber}
     * 
     */
    private int conversionMethod;
    
    /** The Parameter for the conversion method. At the moment it is just {@link AAPI#DEADBAND_PARAM} */
    private double conversParam;
    
    /** List of PV's */
    private List<String> pvList;

    /**
     * Standard constructor
     */
    public RequestData() {
    	fromTime = 0;
    	uFromTime = 0;
    	toTime = 0;
    	uToTime = 0;
    	numberOfSamples = 0;
    	conversParam = AAPI.DEADBAND_PARAM;
    	pvList = new ArrayList<String>();
    }
    
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
    public int getNumberOfSamples() {
        return numberOfSamples;
    }

    /**
     * @param num the num to set
     */
    public void setNumberOfSamples(int num) {
        this.numberOfSamples = num;
    }

    /**
     * @return the conversionTag
     */
    public int getConversionMethod() {
        return conversionMethod;
    }

    /**
     * @param method the number of the conversion method to set
     */
    public void setConversionMethod(int method) {
        this.conversionMethod = method;
    }

    /**
     * @param method the conversion method to set
     */
    public void setConversionMethod(AapiReductionMethod method) {
        this.conversionMethod = method.getMethodNumber();
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
    public int getPvCount() {
        
    	if(pvList != null) {
    		return pvList.size();
    	} else return 0;
    }

    /**
     * @return the pV
     */
    public String[] getPvList() {
    	
    	String[] result = null;
    	
    	if(pvList != null) {
    		result = new String[pvList.size()];
    	} else
    		result = new String[0];
    	
    	return result;
    }

    /**
     * Adds a PV to the list
     * 
     * @param pv
     */
    public void addPv(String pv) {
    	pvList.add(pv);
    }
    
    /**
     * Puts the content of the array into the list of PV's. The old content will be deleted.
     * 
     * @param pV the pV to set
     */
    public void setPvList(String[] pv) {
        
    	if(pv == null) {
    		return;
    	}
    	
    	pvList.clear();
    	
    	for(String s : pv) {
    		pvList.add(s);
    	}
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
            dout.writeInt(numberOfSamples);
            dout.writeInt(conversionMethod);
            dout.writeDouble(conversParam);
            dout.writeInt(pvList.size());
       
            for(int i = 0;i < pvList.size();i++) {
                char[] strArr = pvList.get(i).toCharArray();
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
        for(int i = 0; i < pvList.size();i++) {
            len += ((pvList.get(i)).length() + 1);
        }
        
        return len;
    }
}
