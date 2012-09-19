package org.csstudio.ams.performancetesttool;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

import com.beust.jcommander.JCommander;

public class AMSPerformanceTest {
    
    class ReceiveTimeLogger implements MessageListener {
        private final long[] receiveTimes;
        private int receiveCounter;
        
        ReceiveTimeLogger() {
            receiveTimes = new long[options.count];
            receiveCounter = 0;
        }
        
        @Override
        public void onMessage(Message m) {
            int number = -1;
            try {
                number = Integer.parseInt(((MapMessage) m).getString("VALUE"));
            } catch (Exception e) {
                System.err.println("Warning: received message which was not a MapMessage, or did not contain field VALUE:\n" + e);
            }
            synchronized (this) {
                if (number >= 0 && number < receiveTimes.length) { 
                    receiveTimes[number] = System.nanoTime();
                    ++receiveCounter;
                    if (receiveCounter == options.count) {
                        notifyAll();
                    }
                }
            }
        }
        
        public boolean allMessagesReceived() {
            synchronized (this) {
                return receiveCounter >= options.count;
            }
        }
        
        public long[] getReceiveTimes() {
            synchronized (this) {
                return receiveTimes;
            }
        }
    }
    
    private final CommandLineArgs options;
    private List<Connection> connections;
    private List<Session> listenerSessions;
    private long[] sendTimes;
    private ReceiveTimeLogger listener;
    private String sendTopic;
    private int delay;
	private long startTime;
	private long endTime;

    public AMSPerformanceTest(CommandLineArgs options) {
        this.options = options;
        connections = new ArrayList<Connection>();
        listenerSessions = new ArrayList<Session>();
        sendTimes = new long[options.count];
        listener = new ReceiveTimeLogger();
        
        if (options.component == null) {
            sendTopic = "ALARM";
            if (options.receiveFromTopics.size() == 0) {
                options.receiveFromTopics.add("JMS_Connector_Test");
            }
        } else if (options.component.equals("decision")) {
            sendTopic = "ALARM";
            if (options.receiveFromTopics.size() == 0) {
//                options.receiveFromTopics.add("T_AMS_MESSAGEMINDER");
                options.receiveFromTopics.add("T_AMS_DISTRIBUTE");
            }
        } else if (options.component.equals("minder")) {
            sendTopic = "T_AMS_MESSAGEMINDER";
            if (options.receiveFromTopics.size() == 0) {
                options.receiveFromTopics.add("T_AMS_DISTRIBUTE");
            }
        } else if (options.component.equals("distributor")) {
            sendTopic = "T_AMS_DISTRIBUTE";
            if (options.receiveFromTopics.size() == 0) {
//            	options.receiveFromTopics.add("T_AMS_CON_JMS");
                options.receiveFromTopics.add("T_AMS_CON_MAIL");
            }
        } else if (options.component.equals("jmsconnector")) {
            sendTopic = "T_AMS_CON_JMS";
            if (options.receiveFromTopics.size() == 0) {
                options.receiveFromTopics.add("JMS_Connector_Test");
            }
        } else {
            throw new RuntimeException("Invalid parameter: -component " + options.component);
        }
        
        if (options.rate > 0) {
            if (options.rate > 1000) {
                System.err.println("Warning: using maximum rate limit of 1000 messages per second.");
                options.rate = 1000;
            }
            delay = 1000 / options.rate;
        } else {
            delay = 0;
        }
    }

    public void run() throws JMSException {
        connectJMS();
        startListening();
        startTime = System.currentTimeMillis();
        sendMessages();
        waitUntilAllMessagesReceived();
        stopListening();
        endTime = System.currentTimeMillis();
        disconnectJMS();
        printMeasurements();
    }

    private void printMeasurements() {
        long[] receiveTimes = listener.getReceiveTimes();
        long firstSend = sendTimes[0];
        long lastSend = sendTimes[0];
        long firstReceive = receiveTimes[0];
        long lastReceive = receiveTimes[0];
        long latencySum = 0;
        for (int i = 0; i < options.count; i++) {
            firstSend = Math.min(firstSend, sendTimes[i]);
            lastSend = Math.max(lastSend, sendTimes[i]);
            firstReceive = Math.min(firstReceive, receiveTimes[i]);
            lastReceive = Math.max(lastReceive, receiveTimes[i]);
            latencySum += receiveTimes[i] - sendTimes[i];
        }
        long sendDuration = lastSend - firstSend;
        double sendRate = (double) options.count / ((double) sendDuration / 1e9);
        long receiveDuration = lastReceive - firstReceive;
        double receiveRate = (double) options.count / ((double) receiveDuration / 1e9);
        double averageLatency = (double) latencySum / 1e6 / options.count;
        
        System.out.println(String.format("Number of messages: %d", options.count));
        System.out.println(String.format("Send rate:          %.1f messages/s", sendRate));
        System.out.println(String.format("Receive rate:       %.1f messages/s", receiveRate));
        System.out.println(String.format("Average latency:    %.0f ms", averageLatency));
        System.out.println(String.format("Test time:          %d ms", endTime - startTime));
    }

    private void sendMessages() throws JMSException {
        MapMessageTemplate messageTemplate = new MapMessageTemplate(new File(options.templateFile));
        Session senderSession = connections.get(0).createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = senderSession.createTopic(sendTopic);
        MessageProducer producer = senderSession.createProducer(topic);
        producer.setDeliveryMode(options.nonpersistant ? DeliveryMode.NON_PERSISTENT : DeliveryMode.PERSISTENT);
        for (int i = 0; i < options.count; i++) {
            MapMessage message = senderSession.createMapMessage();
            messageTemplate.applyTo(message);
            message.setString("VALUE", String.valueOf(i));
            sendTimes[i] = System.nanoTime();
            producer.send(message);
            if (delay > 0) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
        senderSession.close();
    }

    private void waitUntilAllMessagesReceived() {
        synchronized (listener) {
            while (!listener.allMessagesReceived()) {
                try {
                    listener.wait();
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
    }

    private void connectJMS() throws JMSException {
        for (String uri : options.uris) {
            ConnectionFactory cf = new ActiveMQConnectionFactory(uri);
            Connection conn = cf.createConnection();
            connections.add(conn);
            conn.start();
        }
    }
    
    private void disconnectJMS() throws JMSException {
        for (Connection c : connections) {
            c.close();
        }
    }
    
    private void startListening() throws JMSException {
        try {
            for (Connection c : connections) {
                final Session session = c.createSession(false, Session.AUTO_ACKNOWLEDGE);
                listenerSessions.add(session);
                for (String topicName : options.receiveFromTopics) {
                    Topic topic = session.createTopic(topicName);
                    MessageConsumer consumer = session.createConsumer(topic);
                    consumer.setMessageListener(listener);
                }
            }
        } catch (JMSException e) {
            throw new RuntimeException("JMS error during listener registration", e);
        }
    }
    
    public void stopListening() {
        for (Session s : listenerSessions) {
            try {
                s.close();
            } catch (JMSException e) {
                System.err.println("Error when closing listener session: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        CommandLineArgs arguments = new CommandLineArgs();
        JCommander commander = new JCommander(arguments, args);
        if (arguments.showUsage) {
            commander.usage();
            return;
        }
        try {
            new AMSPerformanceTest(arguments).run();
        } catch (Exception e) {
            System.err.println("An error occured, the test was aborted");
            System.err.println(e.getMessage());
        }
    }
}
