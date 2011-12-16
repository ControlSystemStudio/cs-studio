
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
import org.csstudio.ams.delivery.AbstractDeliveryWorker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
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
                                                  IGenericServiceListener<ISessionService> {
    
    private static Logger LOG = LoggerFactory.getLogger(DeliverySystemApplication.class);
	
    /** The ECF service */
    private ISessionService xmppService;

    private ArrayList<AbstractDeliveryWorker> deliveryWorker;
    
    private ArrayList<Thread> deliveryThreads;
    
    private Object lock;
    
    private boolean running;
    
    public DeliverySystemApplication() {
        deliveryWorker = new ArrayList<AbstractDeliveryWorker>();
        deliveryThreads = new ArrayList<Thread>();
        lock = new Object();
        running = true;
    }
    
    /**
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	@Override
    public Object start(IApplicationContext context) throws Exception {
		
	    Activator.getPlugin().addSessionServiceListener(this);
	    
        IExtensionRegistry extReg = Platform.getExtensionRegistry();
        IConfigurationElement[] confElements = extReg
                .getConfigurationElementsFor("org.csstudio.ams.delivery.DeliveryWorker");
        
        LOG.debug("Implementationen: {}", confElements.length);
        
        if(confElements.length > 0) {
            
            for (int i = 0; i < confElements.length; i++) {
                
                try {
                    
                    AbstractDeliveryWorker worker =
                            (AbstractDeliveryWorker) confElements[i]
                                    .createExecutableExtension("class");
                    deliveryWorker.add(worker);
                    Thread thread = new Thread(worker);
                    thread.start();
                    deliveryThreads.add(thread);
                } catch (CoreException ce) {
                    LOG.error("*** CoreException *** : ", ce);
                    running = false;
                }
            }
        } else {
            LOG.warn("No extension elements found.");
            running = false;
        }
	    
        context.applicationRunning();
        LOG.info("Initialization finished. Start working.");
        
        while (running) {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException ie) {
                    LOG.warn("Application was interrupted.");
                }
            }
        }
        
        for (AbstractDeliveryWorker o : deliveryWorker) {
            o.stopWorking();
        }
        
        if (xmppService != null) {
            synchronized (xmppService) {
                try {
                    xmppService.wait(500);
                } catch (InterruptedException ie) {
                    LOG.warn("XMPP service waited and was interrupted.");
                }
            }
            xmppService.disconnect();
            LOG.info("XMPP disconnected.");
        }
        
	    LOG.info("Leaving application...");
		return IApplication.EXIT_OK;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public void bindService(final ISessionService sessionService) {

        try {
            sessionService.connect("ams-delivery-system", "ams", "krynfs.desy.de");
            xmppService = sessionService;
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
	    running = false;
        synchronized (lock) {
            lock.notify();
        }
	}
}
