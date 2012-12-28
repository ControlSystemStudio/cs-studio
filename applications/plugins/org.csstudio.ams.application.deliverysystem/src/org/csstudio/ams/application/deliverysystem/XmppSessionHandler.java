
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
 */

package org.csstudio.ams.application.deliverysystem;

import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mmoeller
 * @since 05.12.2012
 */
public class XmppSessionHandler {
    
    private static final Logger LOG = LoggerFactory.getLogger(XmppSessionHandler.class);
    
    /** The ECF service */
    private ISessionService xmppService;

    private IGenericServiceListener<ISessionService> serviceListener;
    
    public XmppSessionHandler() {
        this(null);
    }
    
    public XmppSessionHandler(ISessionService service) {
        xmppService = service;
        serviceListener = null;
    }
    
    public void setSessionService(ISessionService service) {
        xmppService = service;
    }
    
    public void disconnect() {
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
    }

    public void connect(IGenericServiceListener<ISessionService> listener) {
        Activator.getPlugin().addSessionServiceListener(listener);
        serviceListener = listener; 
    }
    
    public void reconnect() throws XmppSessionException {
        if (serviceListener == null) {
            throw new XmppSessionException("Service listener must not be null. Call method connect() first!");
        }
        Activator.getPlugin().addSessionServiceListener(serviceListener);
    }
    
    public boolean isConnected() {
        boolean connected = false;
        if (xmppService != null) {
            connected = (xmppService.getConnectedID() != null);
        }
        return connected;
    }
}
