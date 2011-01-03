
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

/**
 * Answer about ChannelInfo from AAPI-server class.
 * 
 * @author Albert Kagarmanov
 */
public class AnswerChannelInfo {
    
    /** Start time in s (UNIX epoch) */
    private int fromTime; 

    /** The ms of the start time */
    private int fromUTime;
    
    /** End time in s (UNIX epoch) */    
    private int toTime; 

    /** The ms of the end time */
    private int toUTime;
    
    /**
     * 
     * @param tm
     */
    public void setFromTime(int tm) {
        fromTime = tm;
    } 
    
    /**
     * 
     * @return
     */
    public int getFromTime() {
        return fromTime;
    }

    /**
     * 
     * @param tm
     */
    public void setFromUTime(int tm) {
        fromUTime = tm;
    } 
    
    /**
     * 
     * @return
     */
    public int getFromUTime() {
        return fromUTime;
    }
    
    /**
     * 
     * @param tm
     */
    public void setToTime(int tm) {
        toTime = tm;
    } 
    
    /**
     * 
     * @return
     */
    public int getToTime() {
        return toTime;
    }

    /**
     * 
     * @param tm
     */
    public void setToUTime(int tm) {
        toUTime = tm;
    } 

    /**
     * 
     * @return
     */
    public int getToUTime() {
        return toUTime;
    }
    
    /**
     * Analyzing of byteArray package coming from AAPI-server
     * and extracting all data from that.
     * 
     */
    public AnswerChannelInfo analyzeAnswer(byte[] answer, int command) throws AapiException {
        
        if (answer == null) { 
        	throw new AapiException("AAPI analyzeAnswer: null answer");
        }
        
        DataInputStream readStream = new DataInputStream(new ByteArrayInputStream(answer));
        try { 
            int cmd = readStream.readInt();
            if (command != cmd) {
                 throw new AapiException("AAPI analyzeAnswer: returnCommandTag = " + cmd + " != requestCommandTag = " + command);
            }
            
            @SuppressWarnings("unused")
            int err  = readStream.readInt();
            
            @SuppressWarnings("unused")
            int ver  = readStream.readInt();
            
            fromTime = readStream.readInt();
            fromUTime= readStream.readInt();
            toTime   = readStream.readInt();
            toUTime  = readStream.readInt();
        } catch(Exception e) { 
        	throw new AapiException("AAPI-server read buffer error: " + e.getMessage());
        }
        
        return this;    
    }
}
