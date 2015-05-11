
/*
 * Copyright (c) 2012 Stiftung Deutsches Elektronen-Synchrotron,
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
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */

package org.csstudio.utility.jms.sharedconnection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import javax.jms.JMSException;
import org.csstudio.utility.jms.sharedconnection.ISharedConnectionHandle;
import org.csstudio.utility.jms.sharedconnection.MonitorableSharedConnection;

/**
 * @author mmoeller
 * @version 1.0
 * @since 02.04.2012
 */
public class RedundantClientConnection {
    
    /** The static array that contains the URL's for the redundant connection */
    private static Vector<String> jmsUrl;
    
    private static String clientId;
    
    private static Map<String, MonitorableSharedConnection> connections;
    
    static {
        jmsUrl = new Vector<String>();
        connections = new HashMap<String, MonitorableSharedConnection>();
        clientId = null;
    }
    
    public static void injectUrls(String url1, String url2) throws ClientConnectionException {
        if ((url1 == null) || (url2 == null)) {
            throw new ClientConnectionException("Argument(s) is(are) null.");
        }
        if (!jmsUrl.isEmpty()) {
            throw new ClientConnectionException("The JMS URL's are already defined. This method must not be called more than one time.");
        }
        jmsUrl.add(url1);
        jmsUrl.add(url2);
    }
    
    public static void injectUrls(String[] url) throws ClientConnectionException {
        if (url == null) {
            throw new ClientConnectionException("Argument is null.");
        }
        if (!jmsUrl.isEmpty()) {
            throw new ClientConnectionException("The JMS URL's are already defined. This method must not be called more than one time.");
        }
        for (String s : url) {
            jmsUrl.add(s);
        }
    }
    
    public static void injectClientId(String id) {
        clientId = id;
    }
    
    public synchronized static ISharedConnectionHandle[] getConnectionHandles() throws ClientConnectionException {
        
        if (connections.isEmpty()) {
            if (jmsUrl.isEmpty()) {
                throw new ClientConnectionException("The JMS URL's are not defined. First call staticInject(String[] url).");
            }
            for (String s : jmsUrl) {
                MonitorableSharedConnection con = new MonitorableSharedConnection(clientId, s);
                connections.put(s, con);
            }
        }
        
        ISharedConnectionHandle[] result = new ISharedConnectionHandle[connections.size()];
        Iterator<String> iter = connections.keySet().iterator();
        int count = 0;
        while (iter.hasNext()) {
            try {
                String key = iter.next();
                result[count++] = connections.get(key).createHandle();
            } catch (JMSException e) {
                throw new ClientConnectionException(e.getMessage());
            }
        }
        
        return result;
    }
}
