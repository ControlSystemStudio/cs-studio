/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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
package org.csstudio.platform.libs.epics;

/** Constant definitions for plug-in preferences
 *  <p>
 *  The plugin reads these Eclipse preferences to
 *  set the corresponding JCA properties.
 *  
 *  @author Original author unknown
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PreferenceConstants
{
    /** Use pure java or not? Values "true", "false" */
    final public static String PURE_JAVA = "use_pure_java";
    
    /** How to monitor (subscribe): Values "VALUE", "ARCHIVE", "ALARM" */
    final public static String MONITOR = "monitor";
    
    /** List of IP addresses, separated by space */
    final public static String ADDR_LIST = "addr_list"; 
    
    /** Add automatic IP entries? Values "true", "false" */
    final public static String AUTO_ADDR_LIST = "auto_addr_list";
    
    // See Channel Access docu for rest
    final public static String TIMEOUT = "conn_tmo";
    final public static String BEACON_PERIOD = "beacon_period";
    final public static String REPEATER_PORT = "repeater_port";
    final public static String SERVER_PORT = "server_port";
    final public static String MAX_ARRAY_BYTES = "max_array_bytes";
}
