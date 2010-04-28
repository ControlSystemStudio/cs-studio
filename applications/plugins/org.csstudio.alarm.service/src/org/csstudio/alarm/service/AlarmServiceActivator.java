/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron, Member of the Helmholtz
 * Association, (DESY), HAMBURG, GERMANY. THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN
 * "../AS IS" BASIS. WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 * THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE IN
 * ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS
 * DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. NO USE OF ANY SOFTWARE IS
 * AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER. DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE,
 * SUPPORT, UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE FULL LICENSE SPECIFYING FOR THE SOFTWARE
 * THE REDISTRIBUTION, MODIFICATION, USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE
 * DISTRIBUTION OF THIS PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY
 * FIND A COPY AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM $Id: AlarmServiceActivator.java,v 1.2
 * 2010/04/26 09:35:22 jpenning Exp $
 */
package org.csstudio.alarm.service;

import java.util.Dictionary;
import java.util.Hashtable;

import org.csstudio.alarm.service.declaration.IAlarmService;
import org.csstudio.alarm.service.internal.AlarmServiceJMSImpl;
import org.csstudio.platform.logging.CentralLogger;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator decides, which implementation is used for the alarm service.
 * 
 * @author jpenning
 * @author $Author$
 * @version $Revision$
 * @since 26.04.2010
 */
public class AlarmServiceActivator implements BundleActivator {
    
    private final CentralLogger _log = CentralLogger.getInstance();
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void start(final BundleContext context) throws Exception {
        _log.debug(this, "Starting AlarmService");
        
        Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put("service.vendor", "DESY");
        properties.put("service.description", "Alarm service with JMS or DAL implementation");
        
        // Provide implementation for alarm service
        // TODO jp The implementation must be determined dynamically
        context.registerService(IAlarmService.class.getName(),
                                new AlarmServiceJMSImpl(),
                                properties);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(@SuppressWarnings("unused") final BundleContext context) throws Exception {
        _log.debug(this, "Stopping AlarmService");
    }
    
}
