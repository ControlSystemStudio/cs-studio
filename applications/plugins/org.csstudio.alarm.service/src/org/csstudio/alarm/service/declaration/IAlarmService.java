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
package org.csstudio.alarm.service.declaration;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration;
import org.csstudio.utility.treemodel.ContentModel;

/**
 * This service abstracts access to alarm messages from PVs via DAL and JMS resp. 
 * 
 * The configuration model contains the alarm tree configuration. It is displayed in the alarm tree view
 * and it is also used in the alarm table to define which pvs are watched.
 * 
 * The alarm service observes the remote command service to update the configuration when being told to do so.
 * Therefore clients may register to be notified when an update has taken place.
 *   
 * @author jpenning
 * @since 19.01.2012
 */
public interface IAlarmService {
    
    /**
     * The configuration model will be read lazily. The source for the model depends on the preferences.
     * The configuration model is cached internally, so if you want to update, you must invalidate the cache.
     * 
     * @return the model of the alarm configuration
     * @throws AlarmServiceException
     */
    @Nonnull
    ContentModel<LdapEpicsAlarmcfgConfiguration> getConfiguration() throws AlarmServiceException;
    
    /**
     * Invalidate the cache for the configuration.
     * The next time you get the configuration it will be read from ldap and not taken from the cache.
     */
    void invalidateConfigurationCache();
    
    /**
     * Creates a set of pv names from the current configuration and returns it.
     * 
     * @return set of pv names
     * @throws AlarmServiceException
     */
    @Nonnull
    Set<String> getPvNames() throws AlarmServiceException;
    
    /**
     * clients implemented this, when they want to be notified on changes of the facility configuration
     */
    interface IListener {
        
        // This is called, when the configuration has been updated (usually in ldap).
        void configurationUpdated();
        
        // This is called, when the server which forwards the alarms (usually dal2jms) has finished reloading.
        void alarmServerReloaded();
        
        // This is called, when the server which forwards the alarms (usually dal2jms) has finished startup.
        void alarmServerStarted();
        
        // This is called, when the server which forwards the alarms (usually dal2jms) is told to shutdown.
        // This is serious, it means that clients will no longer receive alarm updates.
        void alarmServerWillStop();
    }
    
    /**
     * Register the given listener for a configuration update.
     * If it is registered more than once, it will be called more than once.
     * @param listener
     */
    void register(@Nonnull final IListener listener);
    
    /**
     * Deregister the given listener, if the listener has not been registered, nothing happens.
     * @param listener
     */
    void deregister(@Nonnull final IListener listener);
    
    /**
     * Synchronously retrieve the initial alarm state of the given PVs.
     *
     * On entry, the init items list has to contain the names of all PVs which should be initialized.
     * The implementations of the alarm init item may contain other data as well.
     *
     * When the state could be retrieved, the init method of the init item is called from the service.
     * Handling of connections is done internally in the service.
     * 
     * @param initItems
     * @throws AlarmServiceException
     */
    void retrieveInitialState(@Nonnull List<IAlarmInitItem> initItems) throws AlarmServiceException;
    
    /**
     * Create a new connection to the underlying alarm system.
     *
     * @return the currently available implementation of the alarm connection
     */
    @Nonnull
    IAlarmConnection newAlarmConnection();
    
}
