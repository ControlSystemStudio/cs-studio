
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

package org.csstudio.ams.distributor;

/**
 * @author Markus Moeller
 *
 */
@SuppressWarnings("hiding")
public class ConnectorTopic {
    
    /** Name of the topic */
    private String topicName;
    
    /** Name of the connector */
    private String connectorName;

    /** Indicates if the connector gets the complete alarm message */
    private boolean getsFullMessage;
    
    public ConnectorTopic(String topicName,
                          String connectorName,
                          boolean getsFullMessage) {
        
        this.topicName = topicName;
        this.connectorName = connectorName;
        this.getsFullMessage = getsFullMessage;
    }

    public ConnectorTopic(String topicName, String connectorName) {
        this(topicName, connectorName, false);
    }

    @Override
    public String toString() {
        
        StringBuilder s = new StringBuilder();
        
        s.append("ConnectorTopic{");
        s.append(topicName + ",");
        s.append(connectorName + ",");
        s.append(getsFullMessage);
        s.append("}");
        
        return s.toString();
    }
    
    /**
     * @return the topicName
     */
    public String getTopicName() {
        return topicName;
    }

    /**
     * @param topicName the topicName to set
     */
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    /**
     * @return the connectorName
     */
    public String getConnectorName() {
        return connectorName;
    }

    /**
     * @param connectorName the connectorName to set
     */
    public void setConnectorName(String connectorName) {
        this.connectorName = connectorName;
    }

    /**
     * @return the getsFullMessage
     */
    public boolean isFullMessageReceiver() {
        return getsFullMessage;
    }

    /**
     * @param fullMessage the getsFullMessage to set
     */
    public void setFullMessageReceiver(boolean fullMessage) {
        this.getsFullMessage = fullMessage;
    }
}
