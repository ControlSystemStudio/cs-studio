
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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.Serializable;

/**
 * TODO: Methode analyzeAnswer() überarbeiten
 * @author Albert Kagarmanov
 * @author Markus Moeller
 *
 */
public class AnswerData implements Serializable {
    
    /** Generated serial version id */
    private static final long serialVersionUID = -4161166221666291956L;

    /** Default string for egu */
    private final static String UNDEF_EGU = " ";
    
    /** Number of samples */
    private int count;
    
    /** Error type */
    private int error;
    
    /** Type of data */
    private int type;
    
    /** The data itself */
    private double[] data;
    
    /** Timestamps of the data */
    private int[] time; 
    
    /** The microsecond part of the timestamps */
    private int[] uTime;
    
    /** The status of the data */
    private int[] status;
    
    /** The precision */
    private int precision;
    
    /** */
    private double displayHigh;
    
    /** */
    private double displayLow;
    
    /** */
    private double highAlarm;
    
    /** */
    private double highWarning;
    
    /** */
    private double lowWarning;
    
    /** */
    private double lowAlarm;
    
    /** */
    private String egu;
    
    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * @return the error
     */
    public int getError() {
        return error;
    }

    /**
     * @param error the error to set
     */
    public void setError(int error) {
        this.error = error;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the data
     */
    public double[] getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(double[] data) {
        this.data = data;
    }

    /**
     * @return the time
     */
    public int[] getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(int[] time) {
        this.time = time;
    }

    /**
     * @return the u_time
     */
    public int[] getUTime() {
        return uTime;
    }

    /**
     * @param uTime the u_time to set
     */
    public void setUTime(int[] uTime) {
        this.uTime = uTime;
    }

    /**
     * @return the status
     */
    public int[] getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(int[] status) {
        this.status = status;
    }

    /**
     * @return the displayHigh
     */
    public double getDisplayHigh() {
        return displayHigh;
    }

    /**
     * @param displayHigh the displayHigh to set
     */
    public void setDisplayHigh(double displayHigh) {
        this.displayHigh = displayHigh;
    }

    /**
     * @return the displayLow
     */
    public double getDisplayLow() {
        return displayLow;
    }

    /**
     * @param displayLow the displayLow to set
     */
    public void setDisplayLow(double displayLow) {
        this.displayLow = displayLow;
    }

    /**
     * @return the highAlarm
     */
    public double getHighAlarm() {
        return highAlarm;
    }

    /**
     * @param highAlarm the highAlarm to set
     */
    public void setHighAlarm(double highAlarm) {
        this.highAlarm = highAlarm;
    }

    /**
     * @return the highWarning
     */
    public double getHighWarning() {
        return highWarning;
    }

    /**
     * @param highWarning the highWarning to set
     */
    public void setHighWarning(double highWarning) {
        this.highWarning = highWarning;
    }

    /**
     * @return the lowWarning
     */
    public double getLowWarning() {
        return lowWarning;
    }

    /**
     * @param lowWarning the lowWarning to set
     */
    public void setLowWarning(double lowWarning) {
        this.lowWarning = lowWarning;
    }

    /**
     * @return the lowAlarm
     */
    public double getLowAlarm() {
        return lowAlarm;
    }

    /**
     * @param lowAlarm the lowAlarm to set
     */
    public void setLowAlarm(double lowAlarm) {
        this.lowAlarm = lowAlarm;
    }

    /**
     * @return the egu
     */
    public String getEgu() {
        return egu;
    }

    /**
     * @param egu the egu to set
     */
    public void setEgu(String egu) {
        this.egu = egu;
    }
    
    // From AAPI-Web:
    // public  double   getPrecision()  {return (precision);} 
    /**
     * 
     */
    public int getPrecision() {
        return precision;
    } 

    /**
     * 
     * @param precision
     * @return
     */
    public int setPrecision(int precision) {
        return this.precision = precision;
    } 

    /**
     * Analyzing of byteArray package coming from AAPI-server
     * and extracting all data from that.
     */
    public AnswerData analyzeAnswer(byte[] answer, int command) {
        
        if (answer == null) { 
            System.err.println("AAPI analyzeAnswer: null answer");
            return null;   
        }
        
        DataInputStream readStream = new DataInputStream(new ByteArrayInputStream(answer));
        try { 
            int cmd = readStream.readInt();
            if (command != cmd) {
                 System.err.println("AAPI analyzeAnswer: returnCommandTag = " + cmd + " != requestCommandTag = " + cmd);
                 return null;   
            }
            
            @SuppressWarnings("unused")
            int err = readStream.readInt();
            
            @SuppressWarnings("unused")
            int ver = readStream.readInt();
            
            int PV_size = readStream.readInt();
            for(int i = 0;i < PV_size;i++) {
                
                error = readStream.readInt();
                type = readStream.readInt();
                count = readStream.readInt();
                if (count < 0) { 
                    System.err.println("AAPI analyzeAnswer: negative array size");
                    return null;   
                }
                
                data = new double[count];
                time = new int[count];
                uTime = new int[count];
                status = new int[count];
                
                for(int j = 0;j < count;j++) {
                    
                    time[j] = readStream.readInt();
                    uTime[j] = readStream.readInt(); 
                    status[j] = readStream.readInt();
                    data[j] = readStream.readDouble();
                    
                    // !!! The next three lines are from the CSS AAPI package !!!
                    // AAPI curently return old VAX-VMS-style status
                    // status[j] = 0;
                    // if(debug) System.out.println("to = " + time[j] + " U = " + u_time[j]);
                }
                
                precision = readStream.readInt();
                displayHigh = readStream.readDouble();                
                displayLow = readStream.readDouble();                   
                highAlarm = readStream.readDouble();                                
                highWarning = readStream.readDouble();
                lowAlarm = readStream.readDouble();                                
                lowWarning = readStream.readDouble();
                int eguLen = readStream.readInt();

//                if(debug) System.out.println("eguLen = " + eguLen);

                if(eguLen < 0) { 
                    System.err.println("AAPI analyzeAnswer: negative array size");
                    return null;   
                } else if(eguLen == 0) {
                    egu = new String(UNDEF_EGU);
                } else {
                    char[] eguAsArray = new char[eguLen];
                    for(int j = 0;j < eguLen;j++) {
                        // Looks like one more symbol here
                        eguAsArray[j]= (char)readStream.readByte();
                    }
                    
                    egu = new String(eguAsArray);
                }
                
                // From CSS-AAPI:
                // eguLen--; //Looks like one more symbol here
//                if ( eguLen < -1)
//                { 
//                    System.err.println("AAPI analyzeAnswer: negative array size");
//                    return null;   
//                }
//                else if((eguLen == -1) || (eguLen == 0))
//                {
//                    egu = new String(undefEgu);
//                }
//                else
//                {
//                    char[] eguAsArray = new char[eguLen];
//                    for(int j = 0;j < eguLen;j++)
//                    {
//                        // Looks like one more symbol here                    
//                        eguAsArray[j]= (char)readStream.readByte();
//                    }
//                    
//                    egu = new String(eguAsArray);
//                }

            }
        } catch(Exception e) { 
            System.err.println("AAPI-server read buffer error " + e);
            return null;
        }
        
        return this;    
    }
    
    /**
     * 
     */
    public String toString() {
    	
    	StringBuilder str = new StringBuilder();
    	
    	str.append("AnswerData {");
    	str.append("count=" + count + ",");
    	str.append("error=" + error + ",");
    	str.append("type=" + type + ",");
    	str.append("precision=" + precision + ",");
    	str.append("displayHigh=" + displayHigh + ",");
    	str.append("displayLow=" + displayLow + ",");
    	str.append("highAlarm=" + highAlarm + ",");
    	str.append("highWarning=" + highWarning + ",");
    	str.append("lowWarning=" + lowWarning + ",");
    	str.append("lowAlarm=" + lowAlarm + ",");
    	str.append("egu=" + egu);
    	str.append("}");
    	
    	return str.toString();
    }
}
