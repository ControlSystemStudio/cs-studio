
/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
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

package org.csstudio.tine2jms.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.csstudio.tine2jms.AlarmMessage;

/**
 * @author Markus Moeller
 *
 */
public class JmsProducer {
    
    private Context context = null;
    private ConnectionFactory factory = null;
    private Connection connection = null;
    private Session session = null;
    private MessageProducer producer = null;
    private Topic topic = null;
    private String topicName = null;
    private String clientId = null;
    
    private SimpleDateFormat dateFormat = null;
    
    public JmsProducer(String clientId, String providerURL, String topicName)
    throws JmsProducerException {
        
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        
        Hashtable<String, String> properties = new Hashtable<String, String>();
        properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        properties.put(Context.PROVIDER_URL, providerURL);
        
        try {
            
            context = new InitialContext(properties);
            factory = (ConnectionFactory)context.lookup("ConnectionFactory");
            
            connection = factory.createConnection();
            connection.setClientID(clientId);
            this.clientId = clientId;
            
            connection.start();
    
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            
            topic = session.createTopic(topicName);
            this.topicName = topicName;
            
            producer = session.createProducer(topic);
            
        } catch(JMSException jmse) {
            throw new JmsProducerException(jmse.getMessage(), jmse);
        } catch(NamingException ne) {
            throw new JmsProducerException(ne.getMessage(), ne);
        }
    }
    
    public synchronized void sendMessage(MapMessage message) throws JmsProducerException {
        try {
            producer.send(message);
        } catch (JMSException jmse) {
            throw new JmsProducerException(jmse.getMessage(), jmse);
        }
    }

    public void closeAll() {
        if (producer != null){try{producer.close();}
        catch(JMSException e){}producer=null;}
        topic = null;
        if (session != null){try{session.close();}
        catch(JMSException e){}session=null;}
        if (connection != null){try{connection.stop();}
        catch(JMSException e){}}
        if (connection != null){try{connection.close();}
        catch(JMSException e){}connection=null;}
        factory = null;
        if (context != null){try{context.close();}
        catch(NamingException e){}context=null;}
    }

    public synchronized MapMessage createMapMessages(AlarmMessage alarm) throws JMSException {
        MapMessage msg = session.createMapMessage();

        Date date = new Date(alarm.getAlarmMessage().getTimeStamp());
        
        msg.setString("TYPE", "tine-alarm");
        msg.setString("EVENTTIME", dateFormat.format(date));
        msg.setString("SEVERITY", SeverityMapper.getEPICSSeverity(alarm.getAlarmMessage().getAlarmSeverity()));
        msg.setString("NAME", "dal-tine://" + alarm.getAlarmMessage().getDevice());
        msg.setString("TEXT", alarm.getAlarmMessage().getAlarmDescriptorAsString());
        msg.setString("FACILITY", alarm.getContext());
        msg.setString("HOST", alarm.getAlarmMessage().getServer());

        date = null;
        
        return msg;
    }

    public String getTopicName() {
        return topicName;
    }

    public String getClientId() {
        return clientId;
    }
}
