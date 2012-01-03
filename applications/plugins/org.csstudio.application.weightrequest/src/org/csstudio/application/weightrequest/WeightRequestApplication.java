
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

package org.csstudio.application.weightrequest;

import org.csstudio.application.weightrequest.server.CaServer;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class controls all aspects of the application's execution
 */
public class WeightRequestApplication implements IApplication,
                                                 IGenericServiceListener<ISessionService> {

    private static Logger LOG = LoggerFactory.getLogger(WeightRequestApplication.class);
    
    private CaServer caServer;
    
    private ISessionService xmppService;

    public WeightRequestApplication() {
        this.caServer = new CaServer();
        this.xmppService = null;
    }

    @Override
    public Object start(IApplicationContext context) throws Exception {
      
        LOG.info("Starting application.");

        Activator.getDefault().addSessionServiceListener(this);
        context.applicationRunning();

        this.caServer.run();

        LOG.info("Stopping application.");

        if (xmppService != null) {
            synchronized (xmppService) {
                try {
                    this.xmppService.wait(500L);
                } catch (InterruptedException ie) {
                    // Can be ignored
                }
            }
        
            this.xmppService.disconnect();
            LOG.info("XMPP disconnected.");
        }
        
        return IApplication.EXIT_OK;
    }

    @Override
    public void stop()
    {
      synchronized (this.caServer) {
        this.caServer.stop();
      }
    }

    @Override
    public void bindService(ISessionService service) {
      try {
          service.connect("weightrequest", "weightrequest", "krynfs.desy.de");
          xmppService = service;
      } catch (Exception e) {
          LOG.warn("XMPP connection is not available: {}", e.toString());
      }
    }

    @Override
    public void unbindService(ISessionService service) {
        // Nothing to do here
    }
}
