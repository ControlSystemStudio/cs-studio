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

package org.csstudio.platform.libs.jms;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.jms.ConnectionFactory;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;

import org.csstudio.platform.libs.jms.preferences.PreferenceConstants;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
/**
 * Class used to establish connection with jms server.
 */
public class MessageReceiver {
    /**
     * Properties for the Connection.
     */
    private Hashtable<String, String>   _properties  = null;
    /**
     * Context.
     */
    private Context                     _context     = null;
    /**
     * The Connection Factory.
     */
    private ConnectionFactory           _factory     = null;
    /**
     * The Connetion.
     */
    private Connection                  _connection  = null;
    /**
     * The Sesstion.
     */
    private Session                     _session     = null;
    /**
     * A Container for the Messanges.
     */
    private MessageConsumer[]           _receiver    = null;
    /**
     * The Text Message.
     */
    private TextMessage                 _textMessage = null;
    /**
     * A Message.
     */
    private Message                     _message     = null;
    /**
     * A JMS Topic.
     */
    private Topic		                _destination = null;  // if ! topic: Destination
    /**
     * ???.
     */
	private String[] _queues;
    /**
     * The listener of the Messages.
     */
    private MessageListener _listener;
    /**
     * Used Failover.
     */
    private boolean _failover;
    /**
     * counts of Retrais.
     */
    private int _countsOfRetrais;
	private ConnectionJob cj;
	private IJobChangeListener jobChangeListener;
    
    

    /**
     * 
     * @author hrickens
     * @author $Author$
     * @version $Revision$
     * @since 15.06.2007
     */
    class ConnectionJob extends Job{

        /**
         * @param name of the Job.
         */
        public ConnectionJob(final String name) {
            super(name);
        }

        /**
         * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
         * @param monitor The Progress Monitor. 
         * @return the Prozess ending Status.
         */
        @Override
        protected IStatus run(final IProgressMonitor monitor) {
            try {
                _factory = (ConnectionFactory) _context.lookup("ConnectionFactory");
                _connection = _factory.createConnection();
                _session = _connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                //
                // here we can decide whether we will get any messages regardless whether we are connected or not
                // for now we use here the only_when_online method
                //
                _receiver = new MessageConsumer[_queues.length];
                for (int i=0;i<_queues.length;i++){
                    if (monitor.isCanceled()){ return Status.CANCEL_STATUS;}
                    /*
                     * changed from OpenJMS to ActiveMQ
                     * MCL 2007-05-23
                     */
                    //destination = (Topic)context.lookup(queues[i]);
                    _destination = _session.createTopic(_queues[i]);
                    _receiver[i] = _session.createConsumer(_destination);
                    _receiver[i].setMessageListener(_listener);
                }
                    /*else {
                // create permanent connection:
                    receiver = session.createDurableSubscriber(destination, uniqueNameOfCssInstance);
                }*/
                _connection.start();
            } catch (NamingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (JMSException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return Status.CANCEL_STATUS;
            }

//            receiver.setMessageListener(listener);

            return Status.OK_STATUS;
//            return Job.ASYNC_FINISH;
        }
        
    }

    /**
     * 
     * @throws NamingException Throws an JMS NamingException.
     */
    public MessageReceiver() throws NamingException{
        _properties = new Hashtable<String, String>();
        _properties.put(Context.INITIAL_CONTEXT_FACTORY,
        		JmsPlugin.getDefault().getPluginPreferences().getString(PreferenceConstants.INITIAL_CONTEXT_FACTORY));
        String url = JmsPlugin.getDefault().getPluginPreferences().getString(PreferenceConstants.URL);
//        if(url.contains("failover:")){
//            _failover=true;
//            int index = url.indexOf("maxReconnectAttempts=");
//            if(index<0){
//                url = url.concat("?maxReconnectAttempts=3");
//            }else{
//                int index2 = url.indexOf("&", index);
//                if(index2<0){
//                    url = url.substring(0,index-1)+"maxReconnectAttempts=3"; 
//                }else{
//                    url = url.substring(0,index-1)+"maxReconnectAttempts=3"+url.substring(index2);     
//                }
//            }
//            
//        }else{_failover=false;}
        
        ActiveMQURL uri = new ActiveMQURL(url);
        
        if(uri.getPrefix()!=null){
            _failover=true;
            if(uri.getMaxReconnectAttempts()==null){
                uri.setMaxReconnectAttempts("maxReconnectAttempts=3");
            }
        }else{_failover=false;}
        
//        System.out.println("URL1: "+uri.getURL());
        _properties.put(Context.PROVIDER_URL,uri.getURL());
        _context = new InitialContext(_properties);
//        destination = (Topic) context.lookup(JmsPlugin.getDefault().getPluginPreferences().getString(PreferenceConstants.QUEUE).split(",")[0]);
        _queues = JmsPlugin.getDefault().getPluginPreferences().getString(PreferenceConstants.QUEUE).split(",");
    }

    /**
     * 
     * @param initialContextFactory the Initial Context Factory. 
     * @param providerURL The URL from Provider.
     * @param queues A Queue
     * @throws NamingException Throws an JMS NamingException.
     */
    public MessageReceiver(final String initialContextFactory, final String providerURL, final String[] queues)throws NamingException{
        _properties = new Hashtable<String, String>();
        _properties.put(Context.INITIAL_CONTEXT_FACTORY,initialContextFactory);
        
        ActiveMQURL uri = new ActiveMQURL(providerURL);
       
        if(uri.getPrefix()!=null){
            _failover=true;
            if(uri.getMaxReconnectAttempts()==null){
                uri.setMaxReconnectAttempts("maxReconnectAttempts=3");
            }
        }else{_failover=false;}
//        System.out.println("URL2: "+uri.getURL());
        _properties.put(Context.PROVIDER_URL,uri.getURL());
        _context = new InitialContext(_properties);
//        destination = (Topic)context.lookup(queues[0]);
        this._queues = queues;
    }

    /**
     * Parameter is listener, the one to be notified.
     * @param listener of Message.
     * 
     */
	public final void startListener(final MessageListener listener) {
	    _listener = listener;
        cj = new ConnectionJob("JMS Connetion");
        jobChangeListener = new IJobChangeListener(){
            public void done(final IJobChangeEvent event) {
                if(event.getResult().isOK()){
                    System.out.println("JMS Connecting is done : \r\n"+_properties+"\r\n");
                }else{
                    System.out.println("JMS Server ("+_properties.get(Context.PROVIDER_URL)+") is not available!!!");
                    ActiveMQURL aURL = new ActiveMQURL(_properties.get(Context.PROVIDER_URL));
                    aURL.setMaxReconnectAttempts("maxReconnectAttempts=0");
                    _properties.put(Context.PROVIDER_URL,aURL.getURL());
                    System.out.println(" Retry with "+_properties.get(Context.PROVIDER_URL));
                    startListener(listener);
                }
            }

            public void running(final IJobChangeEvent event) {
                System.out.println("JMS Connecting to \r\n"+_properties+"\r\n"); 
            }
            // do nothing
            public void aboutToRun(final IJobChangeEvent event) {}
            public void awake(final IJobChangeEvent event) {}
            public void scheduled(final IJobChangeEvent event) {}
            public void sleeping(final IJobChangeEvent event) {}
            
        };
        cj.addJobChangeListener(jobChangeListener);
        cj.setSystem(true);
        cj.schedule();
        
	}

	/**
	 * Cleans up resources.
     * @throws Exception ___. 
     * 
	 */
	public final void stopListening() throws Exception{
		cj.removeJobChangeListener(jobChangeListener);
		cj.cancel();
		for (MessageConsumer r: _receiver) {
			r.close();
			r=null;
		}
        _session.close();
        _connection.stop();
        _connection.close();
        _properties  = null;
        _context     = null;
        _factory     = null;
        _connection  = null;
        _session     = null;
        _receiver    = null;
        _textMessage = null;
        _message     = null;
        _destination = null;
        
	}



}
