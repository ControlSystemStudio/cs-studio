
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
 * This class holds some static constants that can be used in every AAPI specific class.
 * 
 * @author mmoeller
 * @version Archive Protocol V2.4
 */
public class AAPI {

    /** */
    public final static double DEADBAND_PARAM = 0.0;
    
    /** */
    public final static int ERROR_TAG = 0;

    /** */
    public final static int AAPI_VERSION = 0x24;

    /** */
    public final static int HEADER_SIZE = 4;

    /** */
    public final static int REQUEST_HEADER_SIZE = 9;

    /** */
    public final static int BYTE_SIZE = 4;

    /** */
    public final static int HEADER_LENGTH = HEADER_SIZE * BYTE_SIZE;

    /** */
    public final static int MAX_NAME_LENGTH = 1024;

    /** Simple description */
    public static final String DESCRIPTION = "AAPI Java library, DESY Hamburg Germany";
    
    //   Next stringArrray corespondent with data reduction methods list:
    
    // 19.1.07 AVERAGE changed to plot-binning
    //  public final static String requestedTypeList[]={"plot-binning","RAW","SHARP",
    //      "SPLINE","FFT","NO_FILTERING","MIN_MAX_AVERAGE","LINEAR_INT","STEP_FUNCTION","LINEAR_AND_STEP"};
    
    //  public final static String requestedTypeList[]={
    //      "AVERAGE","RAW","MIN_MAX_AVERAGE","SHARP","SPLINE"};
    public final static String REQUESTED_TYPE_LIST[] = {
        "SHARP", "RAW", "MIN_MAX_AVERAGE", "AVERAGE", "SPLINE"};        

    public final static String SEVERITY_LIST[] = {"NO_ALARM", "MINOR", "MAJOR", "INVALID", "UNDEF"};
    
    // This list coming from  $Epics/base/include/alarmString.h
    public final static String ALARM_STATUS_STRING[] = {
            "NO_ALARM", "READ", "WRITE", "HIHI", "HIGH", "LOLO", "LOW", "STATE", "COS",
            "COMM", "TIMEOUT", "HWLIMIT", "CALC", "SCAN", "LINK", "SOFT", "BAD_SUB",
            "UDF", "DISABLE", "SIMM", "READ_ACCESS", "WRITE_ACCESS", "UNDEF" };
}
