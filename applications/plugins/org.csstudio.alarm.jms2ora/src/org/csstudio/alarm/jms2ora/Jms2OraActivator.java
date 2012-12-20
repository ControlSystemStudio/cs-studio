
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
 *
 */

package org.csstudio.alarm.jms2ora;

import javax.annotation.Nonnull;

import org.csstudio.alarm.jms2ora.service.IMessageWriter;
import org.csstudio.alarm.jms2ora.service.IMetaDataReader;
import org.csstudio.alarm.jms2ora.service.IPersistenceHandler;
import org.csstudio.alarm.jms2ora.service.MessagePersistenceServiceTracker;
import org.csstudio.alarm.jms2ora.service.MessageWriterServiceTracker;
import org.csstudio.alarm.jms2ora.service.MetaDataReaderServiceTracker;
import org.csstudio.domain.desy.service.osgi.OsgiServiceUnavailableException;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.remotercp.common.tracker.GenericServiceTracker;
import org.remotercp.common.tracker.IGenericServiceListener;
import org.remotercp.service.connection.session.ISessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The activator class controls the plug-in life cycle
 */
public class Jms2OraActivator implements BundleActivator {
    
    /** The plug-in ID */
    public static final String PLUGIN_ID = "org.csstudio.alarm.jms2ora";
    
    /** The class logger */
    private static final Logger LOG = LoggerFactory.getLogger(Jms2OraActivator.class);
    
    /** The shared instance */
    private static Jms2OraActivator PLUGIN;

    /** The BundleContext instance */
    private BundleContext bundleContext;

    /** Service tracker for the XMPP login */
    private GenericServiceTracker<ISessionService> _genericServiceTracker;
    
    /** Service tracker for the database writer service */
    private MessageWriterServiceTracker writerServiceTracker;
    
    /** Service tracker for the meta data reader service */
    private MetaDataReaderServiceTracker metaDataServiceTracker;
    
    /** Service tracker for the persistence writer service */
    private MessagePersistenceServiceTracker persistenceServiceTracker;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void start(@Nonnull BundleContext context) throws Exception {
        
        LOG.info("Jms2Ora is starting.");

        PLUGIN = this;
        bundleContext = context;
        
        _genericServiceTracker = new GenericServiceTracker<ISessionService>(
                context, ISessionService.class);
        _genericServiceTracker.open();
        
        metaDataServiceTracker = new MetaDataReaderServiceTracker(context);
        metaDataServiceTracker.open();
        
        persistenceServiceTracker = new MessagePersistenceServiceTracker(context);
        persistenceServiceTracker.open();

        writerServiceTracker = new MessageWriterServiceTracker(context);
        writerServiceTracker.open();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(@Nonnull BundleContext context) throws Exception {
        PLUGIN = null;
        bundleContext = null;
        persistenceServiceTracker.close();
        metaDataServiceTracker.close();
        writerServiceTracker.close();
        _genericServiceTracker.close();
    }

    /**
     * Returns the BundleContext of this application.
     * 
     * @return The bundle context of this plugin
     */
    @Nonnull
    public BundleContext getBundleContext() {
        return bundleContext;
    }
    
	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
    @Nonnull
	public static Jms2OraActivator getDefault() {
		return PLUGIN;
	}

	/**
	 * Returns the plugin id
	 * 
	 * @return The plugin id
	 */
    @Nonnull
    public String getPluginId() {
        return PLUGIN_ID;
    }
    
    /**
     * Adds a service listener for the XMPP service
     * 
     * @param sessionServiceListener
     */
	public void addSessionServiceListener(
			IGenericServiceListener<ISessionService> sessionServiceListener) {
		_genericServiceTracker.addServiceListener(sessionServiceListener);
	}
	
	/**
	 * 
	 * @return The message writer service
	 * @throws OsgiServiceUnavailableException
	 */
	public IMessageWriter getMessageWriterService() throws OsgiServiceUnavailableException {
	    
	    IMessageWriter service = (IMessageWriter) writerServiceTracker.getService();
        if (service == null) {
            throw new OsgiServiceUnavailableException("Message writer service unavailable.");
        }

	    return service;
	}
	
	/**
	 * 
	 * @return The meta data reader service
	 * @throws OsgiServiceUnavailableException
	 */
    public IMetaDataReader getMetaDataReaderService() throws OsgiServiceUnavailableException {
	        
        IMetaDataReader service = (IMetaDataReader) metaDataServiceTracker.getService();
        if (service == null) {
            throw new OsgiServiceUnavailableException("Meta data reader service unavailable.");
        }

        return service;
	}
    
    /**
     * 
     * @return The persistence writer service
     * @throws OsgiServiceUnavailableException
     */
    public IPersistenceHandler getPersistenceWriterService() throws OsgiServiceUnavailableException {
        
        IPersistenceHandler service = (IPersistenceHandler) persistenceServiceTracker.getService();
        if (service == null) {
            throw new OsgiServiceUnavailableException("Persistence writer service unavailable.");
        }

        return service;
    }
}
