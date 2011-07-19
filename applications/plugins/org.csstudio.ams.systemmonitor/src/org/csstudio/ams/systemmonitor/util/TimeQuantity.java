
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
 */

package org.csstudio.ams.systemmonitor.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Markus Moeller
 *
 */
public class TimeQuantity {
    
    private static String getPart(String regex, String value) {
        
        String unit = null;
        
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(value);
        if(m.find()) {
            unit = m.group();
        }
        
        return unit;
    }
    
    /**
     * The method gets a string with a time interval containing the time unit(min, s | sec)
     * and converts it to milliseconds.
     * If no time unit is present, the value will be taken as minutes
     *  
     * Note: The method only returns <b>positive</b> values! If the time value is negative, the method
     *       returns -1.
     * 
     * @param v String containing the time quantity in minutes or seconds
     * @return Time in milliseconds or -1 if an error occures
     */
    public static long getMilliSeconds(String v) {
        
        @SuppressWarnings("unused")
        String unit = null;
        @SuppressWarnings("unused")
        String value = null;
        long result = -1;
        
        unit = getPart("min|sec|s", v);
        value = getPart("\\d++", v);
        
        // 15min, 15 min
        
        return result;
    }
    
    /**
     * The method gets a string with a time interval containing the time unit(ms | msec, min)
     * and converts it to seconds.
     * If no time unit is present, the value will be taken as minutes
     * 
     * Note: The method only returns <b>positive</b> values! If the time value is negative, the method
     *       returns -1.
     * 
     * @param v String containing the time quantity in milliseconds or minutes
     * @return Time in seconds or -1 if an error occures
     */
    public static long getSeconds(String v) {
        
        @SuppressWarnings("unused")
        String unit = null;
        @SuppressWarnings("unused")
        String value = null;
        long result = -1;

        unit = getPart("min|msec|ms", v);
        value = getPart("\\d++", v);

        return result;
    }
    
    /**
     * The method gets a string with a time interval containing the time unit(ms | msec, s | sec)
     * and converts it to ms.
     * If no time unit is present, the value will be taken as minutes
     * 
     * Note: The method only returns <b>positive</b> values! If the time value is negative, the method
     *       returns -1.
     * 
     * @param v String containing the time quantity in milliseconds or seconds.
     * @return Time in minutes or -1 if an error occures
     */
    public static long getMinutes(String v) {
        
        @SuppressWarnings("unused")
        String unit = null;
        @SuppressWarnings("unused")
        String value = null;
        long result = -1;

        unit = getPart("msec|ms|sec|s", v);
        value = getPart("\\d++", v);

        return result;
    }
}
