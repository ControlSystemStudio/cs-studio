
/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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

package org.csstudio.ams.application.monitor.jmx;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Set;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.csstudio.ams.application.monitor.internal.AmsMonitorPreference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mmoeller
 * @version 1.0
 * @since 05.12.2011
 */
public class JmsSubscriptionCleaner {
    
    private static final Logger LOG = LoggerFactory.getLogger(JmsSubscriptionCleaner.class);
    
    private String[] jmsUrls;
    
    private MBeanServerConnection mbsc;
    
    private String monitorTopic;
    
    public JmsSubscriptionCleaner(String topicName) {
        
        String jmxHost1 = AmsMonitorPreference.JMX_HOST_1.getValue();
        int jmxPort1 = AmsMonitorPreference.JMX_PORT_1.getValue();
        
        String jmxHost2 = AmsMonitorPreference.JMX_HOST_2.getValue();
        int jmxPort2 = AmsMonitorPreference.JMX_PORT_2.getValue();
        
        jmsUrls = new String[2];
        jmsUrls[0] = "";
        jmsUrls[1] = "";
        
        if (jmxHost1.length() > 0) {
            jmsUrls[0] = "service:jmx:rmi:///jndi/rmi://" + jmxHost1 + ":" + jmxPort1 + "/jmxrmi";
        }
        
        if (jmxHost2.length() > 0) {
            jmsUrls[1] = "service:jmx:rmi:///jndi/rmi://" + jmxHost2 + ":" + jmxPort2 + "/jmxrmi";
        }
        
        monitorTopic = topicName;
    }
    
    public boolean destroySubscription(String name) {
        boolean success = false;
        for (String url : jmsUrls) {
            if (url.length() > 0) {
                success = destroySubscription(url, name);
                if (!success) break;
            }
        }
        return success;
    }
    
    private boolean destroySubscription(String url, String name) {
        
        boolean success = false;

        JMXServiceURL jmxUrl = null;
        JMXConnector jmxc = null;
        
        try {
            
            jmxUrl = new JMXServiceURL(url);
            jmxc = JMXConnectorFactory.connect(jmxUrl, null);
            mbsc = jmxc.getMBeanServerConnection();
            
            //Set<ObjectName> names = mbsc.queryNames(new ObjectName("org.apache.activemq:*,Type=Subscription,persistentMode=Durable,destinationName=" + jmsAmsTopicMonitor), null);
            Set<ObjectName> names = mbsc.queryNames(new ObjectName("org.apache.activemq:*,Type=Subscription,persistentMode=Durable,destinationName=" + monitorTopic), null);
            for (ObjectName on : names) {
                LOG.debug(on.toString());
                if (hasDestroyMethod(on) && isMonitorSubscription(on, name)) {
                    try {
                        mbsc.invoke(on, "destroy", null, null);
                        success = true;
                    } catch (Exception e) {
                        LOG.error("[*** Exception ***]: {}", e.getMessage());
                    }
                }
            }
        } catch (MalformedURLException me) {
            LOG.error("[*** MalformedURLException ***]: {}", me.getMessage());
        } catch (IOException ioe) {
            LOG.error("[*** IOException ***]: {}", ioe.getMessage());
        } catch (MalformedObjectNameException mone) {
            LOG.error("[*** MalformedObjectNameException ***]: {}", mone.getMessage());
        } catch (NullPointerException npe) {
            LOG.error("[*** NullPointerException ***]: {}", npe.getMessage());
        } finally {
            if (jmxc != null) {
                try{jmxc.close();}catch(Exception e){/* Ignore me */}
            }
        }

        return success;
    }

    private boolean hasDestroyMethod(ObjectName name) {
        
        boolean hasMethod = false;
        
        try {
            MBeanInfo pInfo = mbsc.getMBeanInfo(name);
            MBeanOperationInfo[] opInfo = pInfo.getOperations();
            for (MBeanOperationInfo o : opInfo) {
                if (o.getName().compareToIgnoreCase("destroy") == 0) {
                    hasMethod = true;
                    break;
                }
            }
        } catch (InstanceNotFoundException e) {
            LOG.error("[*** InstanceNotFoundException ***]: {}", e.getMessage());
        } catch (IntrospectionException e) {
            LOG.error("[*** IntrospectionException ***]: {}", e.getMessage());
        } catch (ReflectionException e) {
            LOG.error("[*** ReflectionException ***]: {}", e.getMessage());
        } catch (IOException e) {
            LOG.error("[*** ReflectionException ***]: {}", e.getMessage());
        }

        return hasMethod;
    }
    
    private boolean isMonitorSubscription(ObjectName name, String subscriptionId) {
        String value = name.getKeyProperty("name");
        if (value == null) {
            value = name.getKeyProperty("subscriptionID");
            if (value == null) {
                value = "";
            } else {
                String active = name.getKeyProperty("active");
                if (active != null) {
                    if (active.equalsIgnoreCase("false")) {
                        value = "";
                    }
                }
            }
        }
        return (value.contains(subscriptionId));
    }
}
