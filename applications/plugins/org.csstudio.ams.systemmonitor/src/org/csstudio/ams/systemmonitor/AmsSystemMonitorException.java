
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

package org.csstudio.ams.systemmonitor;

/**
 * @author Markus Moeller
 *
 */
public class AmsSystemMonitorException extends Exception {
    
    /** Generated serial version id */
    private static final long serialVersionUID = -7301610647440573057L;

    /** Error code */
    private int errorCode;
    
    public static final int ERROR_CODE_UNDEFINED = 0;
    public static final int ERROR_CODE_SYSTEM_MONITOR = 1;
    public static final int ERROR_CODE_AMS = 2;
    public static final int ERROR_CODE_SMS_CONNECTOR_ERROR = 4;
    public static final int ERROR_CODE_SMS_CONNECTOR_WARN = 8;
    public static final int ERROR_CODE_JMS_CONNECTION = 16;
    public static final int ERROR_CODE_TIMEOUT = 32;
    
    public AmsSystemMonitorException() {
        super();
        this.errorCode = ERROR_CODE_UNDEFINED;
    }
    
    public AmsSystemMonitorException(final String message) {
        super(message);
        this.errorCode = ERROR_CODE_UNDEFINED;
    }

    public AmsSystemMonitorException(final String message, final Throwable cause)  {
        super(message, cause);
        this.errorCode = ERROR_CODE_UNDEFINED;
    }

    public AmsSystemMonitorException(final Throwable cause) {
        super(cause);
        this.errorCode = ERROR_CODE_UNDEFINED;
    }
    
    public AmsSystemMonitorException(final int error) {
        super();
        this.errorCode = error;
    }
    
    public AmsSystemMonitorException(final String message, final int error) {
        super(message);
        this.errorCode = error;
    }

    public AmsSystemMonitorException(final String message,
                                     final Throwable cause, final int error) {
        super(message, cause);
        this.errorCode = error;
    }

    public AmsSystemMonitorException(final Throwable cause, final int error) {
        super(cause);
        this.errorCode = error;
    }
    
    public int getErrorCode()
    {
        return errorCode;
    }
}
