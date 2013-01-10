
/* 
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchroton, 
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

package org.csstudio.ams.application.deliverysystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import org.csstudio.ams.AmsActivator;
import org.csstudio.ams.application.deliverysystem.internal.DeliverySystemPreference;
import org.csstudio.ams.application.deliverysystem.management.ListWorker;
import org.csstudio.ams.application.deliverysystem.management.Restart;
import org.csstudio.ams.application.deliverysystem.management.RestartWorker;
import org.csstudio.ams.application.deliverysystem.management.Stop;
import org.csstudio.ams.application.deliverysystem.management.StopWorker;
import org.csstudio.ams.application.deliverysystem.util.CommonMailer;
import org.csstudio.ams.application.deliverysystem.util.Environment;
import org.csstudio.ams.delivery.AbstractDeliveryWorker;
import org.csstudio.ams.internal.AmsPreferenceKey;
import org.csstudio.utility.jms.sharedconnection.SharedJmsConnections;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class controls all aspects of the application's execution
 */
public class DeliverySystemApplication implements IApplication,
                                                  RemotelyManageable,
                                                  IGenericServiceListener<ISessionService> {
    
    private static Logger LOG = LoggerFactory.getLogger(DeliverySystemApplication.class);
	
    // 6 minutes
    private final static long WORKER_WATCH_INTERVAL = 360000L;

    private XmppSessionHandler xmppSessionHandler;
    
    private Hashtable<AbstractDeliveryWorker, Thread> deliveryWorker;
    
    private long workerStopTimeout;
    
    private Object lock;
    
    private boolean running;
    
    private boolean restart;
    
    public DeliverySystemApplication() {
        xmppSessionHandler = new XmppSessionHandler();
        deliveryWorker = new Hashtable<AbstractDeliveryWorker, Thread>();
        workerStopTimeout = DeliverySystemPreference.WORKER_STOP_TIMEOUT.getValue();
        LOG.info("Timeout for worker shutdown: {}", workerStopTimeout);
        lock = new Object();
        running = true;
        restart = false;
    }
    
    /**
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	@Override
    public Object start(IApplicationContext context) throws Exception {
		
	    xmppSessionHandler.connect(this);
	    
	    IPreferencesService prefs = Platform.getPreferencesService();
	    
	    // FIRST read the JMS preferences and prepare the SharedJmsConnection class
        String url1 = prefs.getString(AmsActivator.PLUGIN_ID,
                                      AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_1,
                                      "tcp://localhost:62616",
                                      null);
        String url2 = prefs.getString(AmsActivator.PLUGIN_ID,
                                      AmsPreferenceKey.P_JMS_AMS_PROVIDER_URL_2,
                                      "tcp://localhost:64616",
                                      null);
        LOG.debug("JMS Consumer URL 1: {}", url1);
        LOG.debug("JMS Consumer URL 2: {}", url2);
        SharedJmsConnections.staticInjectConsumerUrlAndClientId(url1, url2, "AmsDeliverySystemConsumer");
        
        url1 = prefs.getString(AmsActivator.PLUGIN_ID,
                               AmsPreferenceKey.P_JMS_AMS_SENDER_PROVIDER_URL,
                               "tcp://localhost:62616",
                               null);
        LOG.debug("JMS Publisher URL: {}", url1);
        SharedJmsConnections.staticInjectPublisherUrlAndClientId(url1, "AmsDeliverySystemPublisher");

        running = startDeliveryWorker();
	    
        context.applicationRunning();
        LOG.info("Initialization finished. Start working.");
        
        while (running) {
            
            synchronized (lock) {
                try {
                    lock.wait(WORKER_WATCH_INTERVAL);
                } catch (InterruptedException ie) {
                    LOG.warn("Application was interrupted.");
                }
            }
            
            // Check XMPP connection
            if (xmppSessionHandler.isConnected()) {
                LOG.debug("XMPP connection is working.");
            } else {
                LOG.warn("XMPP connection is broken! Try to re-connect.");
                try {
                    xmppSessionHandler.reconnect();
                } catch (XmppSessionException e) {
                    xmppSessionHandler.connect(this);
                }
            }
            
            boolean restartWorker = false;
            String badWorker = "";
            if (running) {
                
                // Check all delivery worker
                Enumeration<AbstractDeliveryWorker> worker = deliveryWorker.keys();
                while (worker.hasMoreElements()) {
                    AbstractDeliveryWorker w = worker.nextElement();
                    if (!w.isWorking()) {
                        badWorker = w.getWorkerName();
                        LOG.warn("{} seemed not to be working.", badWorker);
                        restartWorker = true;
                        break;
                    }
                    LOG.debug("{} is working.", w.getWorkerName());
                }
                
                // Now stop and restart ALL workers to avoid problems with the shared JMS connections 
                if (restartWorker) {
                    
                    String host = Environment.getInstance().getHostName();
                    String[] recipients = null;
                    String value = DeliverySystemPreference.WORKER_STATUS_MAIL.getValue();
                    if (value != null) {
                        if (value.trim().length() > 0) {
                            recipients = value.split(",");
                        }
                    }

                    if (badWorker.equalsIgnoreCase("SmsDeliveryWorker")) {
                        LOG.info("The DeliverySystem will be restarted, because the {} does not work.",
                                 badWorker);
                        running = false;
                        restart = true;
                        CommonMailer.sendMultiMail("smtp.desy.de",
                                                   "ams-mks2@desy.de",
                                                   recipients,
                                                   "DeliverySystem auf " + host + " wird neu gestartet",
                                                   "Das DeliverySystem wird neu gestartet.\nGrund: Der "
                                                   + badWorker + " laeuft nicht.");
                    } else {
                        stopDeliveryWorker();
                        if (startDeliveryWorker()) {
                            CommonMailer.sendMultiMail("smtp.desy.de",
                                                       "ams-mks2@desy.de",
                                                       recipients,
                                                       "Delivery Worker auf " + host + " wurden neu gestartet",
                                                       "Alle Delivery Worker wurden neu gestartet.\nGrund: Der "
                                                       + badWorker + " lief nicht.");
                        } else {
                            running = false;
                            restart = true;
                            LOG.error("Cannot restart the delievery worker. Restart the application.");
                            CommonMailer.sendMultiMail("smtp.desy.de",
                                                       "ams-mks2@desy.de",
                                                       recipients,
                                                       "DeliverySystem auf " + host + " wird neu gestartet",
                                                       "Die Delivery Worker konnten nicht neu gestartet werden, daher wird das DeliverySystem komplett neu gestartet.\nGrund: Der "
                                                       + badWorker + " lief nicht.");
                        }
                    }
                }
            }
        }
        
        Enumeration<AbstractDeliveryWorker> worker = deliveryWorker.keys();
        while (worker.hasMoreElements()) {
            AbstractDeliveryWorker w = worker.nextElement();
            Thread thread = deliveryWorker.get(w);
            LOG.info("Stopping worker: {}", w.getWorkerName());
            w.stopWorking();
            thread.join(5000);
        }
        
        xmppSessionHandler.disconnect();
        
        Integer exitCode = IApplication.EXIT_OK;
        if (restart) {
            LOG.info("Restarting application...");
            exitCode = IApplication.EXIT_RESTART;
        } else {
            LOG.info("Leaving application...");
        }
        
		return exitCode;
	}

	private boolean startDeliveryWorker() {
	    
	    boolean success = true;
	    
	    DeliveryWorkerList workerList = new DeliveryWorkerList();
        
        IExtensionRegistry extReg = Platform.getExtensionRegistry();
        IConfigurationElement[] confElements = extReg
                .getConfigurationElementsFor("org.csstudio.ams.delivery.DeliveryWorker");
        
        if(confElements.length > 0) {

            LOG.info("I've found {} implementations in the extension registry.", confElements.length);
            if (workerList.isEmpty()) {
                LOG.warn("... but no delivery worker is defined in the configuration file. Leaving application.");
                return false;
            }

            for (int i = 0; i < confElements.length; i++) {
                
                String className = confElements[i].getAttribute("class");
                if (workerList.containsWorker(className)) {
                    try {
                        AbstractDeliveryWorker worker =
                                (AbstractDeliveryWorker) confElements[i]
                                        .createExecutableExtension("class");
                        Thread thread = new Thread(worker);
                        thread.start();
                        deliveryWorker.put(worker, thread);
                        LOG.info("Worker has been started: {}", worker.getWorkerName());
                    } catch (CoreException ce) {
                        LOG.error("*** CoreException *** : ", ce);
                        success = false;
                        break;
                    }
                }
            }
        } else {
            LOG.warn("No extension elements found.");
            success = false;
        }
        
        return success;
	}
	
    /**
     * {@inheritDoc}
     */
    @Override
    public void bindService(final ISessionService sessionService) {
        
        Stop.staticInject(this);
        Restart.staticInject(this);

        ListWorker.staticInject(this);
        if (DeliverySystemPreference.ENABLE_WORKER_STOP.getValue()) {
            StopWorker.staticInject(this);
        }
        
        if (DeliverySystemPreference.ENABLE_WORKER_RESTART.getValue()) {
            RestartWorker.staticInject(this);
        }
        
        String xmppServer = DeliverySystemPreference.XMPP_SERVER.getValue();
        String xmppUser = DeliverySystemPreference.XMPP_USER.getValue();
        String xmppPassword = DeliverySystemPreference.XMPP_PASSWORD.getValue();

        try {
            sessionService.connect(xmppUser, xmppPassword, xmppServer);
            xmppSessionHandler.setSessionService(sessionService);
        } catch (final Exception e) {
            LOG.warn("XMPP connection is not available: {}", e.toString());
        }
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void unbindService(final ISessionService service) {
        // Nothing to do here
    }

	/**
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	@Override
    public void stop() {
	    LOG.info("The application is forced to stop.");
	    LOG.info("Restarting application: {}", restart);
	    running = false;
        synchronized (lock) {
            lock.notify();
        }
	}

	@Override
    public void setRestart(boolean restartApplication) {
	    restart = restartApplication;
	}
	
    @Override
    public Collection<String> listDeliveryWorker() {
        ArrayList<String> result = new ArrayList<String>();
        Enumeration<AbstractDeliveryWorker> worker = deliveryWorker.keys();
        while (worker.hasMoreElements()) {
            AbstractDeliveryWorker o = worker.nextElement();
            result.add(o.getWorkerName());
            if (o.isWorking()) {
                result.add("\t- working\n");
            } else {
                result.add("\t- NOT working\n");
            }
        }
        return result;
    }

    @Override
    public void restartDeliveryWorker() {
        stopDeliveryWorker();
        startDeliveryWorker();
    }
    
    @Override
    public void stopDeliveryWorker() {
        Enumeration<AbstractDeliveryWorker> worker = deliveryWorker.keys();
        while (worker.hasMoreElements()) {
            AbstractDeliveryWorker o = worker.nextElement();
            Thread thread = deliveryWorker.get(o);
            if (o != null) {
                o.stopWorking();
                try {
                    thread.join(workerStopTimeout);
                    thread = null;
                } catch (InterruptedException ie) {
                    // Ignore Me!
                }
                o = null;
            }
        }
        deliveryWorker.clear();
    }
}
