
/* 
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, 
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

/**
 * TODO (mmoeller) : 
 * 
 * @author mmoeller
 * @version Archive Protocol V2.4
 * @since 06.12.2010
 */
public enum AapiServerError {
    
    NO_ERROR(0, "OK"),
    TCP_READ_ERROR(1, "TCP_READ_ERROR: Try (re)start server or Network Problem."),
    NO_MEMORY(2, "NO_MEMORY: Not enough memory in server side."),
    BAD_CMD_NUM(3, "BAD_CMD_NUM: Server or Network Problem"),
    CANT_READ_LENGTH(4, "CANT_READ_LENGTH: Server or Network Problem"),
    CANT_READ_PACKET(5, "CANT_READ_PACKET: Server or Network Problem:"),
    DESERIALISATION_PROBLEM(6, "DESERIALISATION_PROBLEM: Server or Network Problem"),
    BAD_CMD(7, "BAD_CMD: Server or Network Problem"),
    CANT_DESERIAL(8, "CANT_DESERIAL: Server or Network Problem"),
    ADD_HEADER_PROBLEM(9, "ADD_HEADER_PROBLEM: Server or Network Problem"),
    BAD_DATA_HANDLE(10, "BAD_DATA_HANDLE: Server Problem"),
    NO_SUCH_METHOD(11, "NO_SUCH_METHOD: Server Problem"),
    FROM_MORE_THEN_TO(12, "FROM_MORE_THEN_TO: Bad region choosen"),
    BAD_MAX_NUM(13, "BAD_MAX_NUM: Problem with number of points in server side"),
    BAD_AVER_METHOD(14, "BAD_AVER_METHOD: Server Problem with average Method"), 
    BAD_TIME(15, "BAD_TIME: Bad time region choose"), 
    CAN_T_OPEN_FILE(16, "CAN_T_OPEN_FILE: Server side file reading problem"), 
    BAD_FGETS(17, "BAD_FGETS: Server side file reading problem"), 
    BAD_HR_FILE(18, "BAD_HR_FILE: Server side file reading problem"), 
    BAD_GET_CHANNEL_INFO(19, "BAD_GET_CHANNEL_INFO: Server side get info about channels problem"),  
    BAD_GET_CHANNEL_LIST(20, "BAD_GET_CHANNEL_LIST: Server side get list of channels problem"), 
    BAD_GET_HIERARCHY(21, "BAD_GET_HIERARCHY: Server side get hierarchy list of channels problem"),
    BAD_RAW_METHOD(22, "BAD_RAW_METHOD: Server Problem with raw Method"), 
    BAD_GET_FILTER_LIST(23, "BAD_GET_FILTER_LIST: Server side get list of method problem"),
    BAD_NO_FILTER_METHOD(24, "BAD_NO_FILTER_METHOD: Server Problem with RAW Method"), 
    NO_FILTER_BIG(25, "Server: Number of return points exeed limit, decrease plot region"),
    BAD_FFT_METHOD(26, "BAD_FFT_METHOD: Server Problem with FFT Method"),
    BAD_GET_REG_EXP(27, "BAD_GET_REG_EXP: Server side get regExp list of channels problem"),
    BAD_GET_SKELETON_INFO(28, "BAD_GET_SKELETON_INFO: Server Problem with Get Skeleton Info Method"), 
    BAD_MMA_METHOD(29, "BAD_MMA_METHOD: Server Problem with Min/MAX/Average Method");

    /** */
    private int errorNumber;
    
    /** */
    private String errorString;
    
    /**
     * 
     * @param errorNumber
     * @param errorString
     */
    private AapiServerError(int errorNumber, String errorString) {
        
        this.errorNumber = errorNumber;
        this.errorString = errorString;
    }
    
    public int getErrorNumber() {
        
        return errorNumber;
    }
    
    /**
     * 
     * @return
     */
    public static int getMaxErrorNumber() {
        
        int result = 0;
        
        for(AapiServerError o : AapiServerError.values()) {
            
            if(o.getErrorNumber() >= result) {
                
                result = o.getErrorNumber();
            }
        }
        
        return result;
    }
    
    /**
     * 
     */
    public String toString() {
        
        return errorString;
    }
}
