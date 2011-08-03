/*
 * Copyright (c) 2007 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.config.ioconfig.model.hibernate;

import static org.csstudio.config.ioconfig.model.preference.PreferenceConstants.DDB_PASSWORD;
import static org.csstudio.config.ioconfig.model.preference.PreferenceConstants.DDB_TIMEOUT;
import static org.csstudio.config.ioconfig.model.preference.PreferenceConstants.DDB_USER_NAME;
import static org.csstudio.config.ioconfig.model.preference.PreferenceConstants.DIALECT;
import static org.csstudio.config.ioconfig.model.preference.PreferenceConstants.HIBERNATE_CONNECTION_DRIVER_CLASS;
import static org.csstudio.config.ioconfig.model.preference.PreferenceConstants.HIBERNATE_CONNECTION_URL;
import static org.csstudio.config.ioconfig.model.preference.PreferenceConstants.SHOW_SQL;

import javax.annotation.Nonnull;

import org.csstudio.config.ioconfig.model.IOConfigActivator;
import org.csstudio.config.ioconfig.model.preference.PreferenceConstants;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.hibernate.cfg.AnnotationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.14 $
 * @since 03.06.2009
 */
public final class HibernateManager extends AbstractHibernateManager {
    
    protected static final Logger LOG = LoggerFactory.getLogger(HibernateManager.class);

    private static HibernateManager _INSTANCE;

    private AnnotationConfiguration _cfg;
    
    private HibernateManager() {
        // Default constructor
    }
    
    @Override
    protected void buildConifg() {
        final String pluginId = IOConfigActivator.PLUGIN_ID;
        new InstanceScope().getNode(pluginId)
                .addPreferenceChangeListener(new IPreferenceChangeListener() {
                    
                    @Override
                    public void preferenceChange(@Nonnull final PreferenceChangeEvent event) {
                        setProperty(event.getKey(), event.getNewValue());
                        setSessionFactory(getCfg().buildSessionFactory());
                    }
                });
        
        final IPreferencesService prefs = Platform.getPreferencesService();
        _cfg = new AnnotationConfiguration();
        for (Class<?> clazz : getClasses()) {
            _cfg.addAnnotatedClass(clazz);
        }
        _cfg.setProperty("org.hibernate.cfg.Environment.MAX_FETCH_DEPTH", "0")
                .setProperty("hibernate.connection.driver_class",
                             prefs.getString(pluginId, HIBERNATE_CONNECTION_DRIVER_CLASS, "", null))
                .setProperty("hibernate.dialect", prefs.getString(pluginId, DIALECT, "", null))
                .setProperty("hibernate.order_updates", "false")
                .setProperty("hibernate.connection.url",
                             prefs.getString(pluginId, HIBERNATE_CONNECTION_URL, "", null))
                .setProperty("hibernate.connection.username",
                             prefs.getString(pluginId, DDB_USER_NAME, "", null))
                .setProperty("hibernate.connection.password",
                             prefs.getString(pluginId, DDB_PASSWORD, "", null))
                .setProperty("transaction.factory_class",
                             "org.hibernate.transaction.JDBCTransactionFactory")
                .setProperty("hibernate.cache.provider_class",
                             "org.hibernate.cache.HashtableCacheProvider")
                .setProperty("hibernate.cache.use_minimal_puts", "true")
                .setProperty("hibernate.cache.use_query_cache", "true")
                // connection Pool
                .setProperty("hibernate.connection.provider_class",
                             "org.hibernate.connection.C3P0ConnectionProvider")
                .setProperty("c3p0.min_size", "1").setProperty("c3p0.max_size", "3")
                .setProperty("c3p0.timeout", "1800")
                .setProperty("c3p0.acquire_increment", "1")
                .setProperty("c3p0.idle_test_period", "100")
                // sec
                .setProperty("c3p0.max_statements", "1")
//                .setProperty("hibernate.hbm2ddl.auto", "update")
                .setProperty("hibernate.show_sql", "false");
//                .setProperty("hibernate.format_sql", "true")
//                .setProperty("hibernate.use_sql_comments", "true")
//                  .setProperty("hibernate.cache.use_second_level_cache", "true");
        setTimeout(prefs.getInt(pluginId, DDB_TIMEOUT, 90, null));
    }
    
    
    /**
     * Set a Hibernate Property.
     *
     * @param property
     *            the Property to set a new Value.
     * @param value
     *            the value for the Property.
     */
    protected void setProperty(@Nonnull final String property, @Nonnull final Object value) {
        if(property.equals(PreferenceConstants.DDB_TIMEOUT)) {
            if(value instanceof Integer) {
                setTimeout((Integer) value);
            } else if(value instanceof String) {
                setTimeout(Integer.parseInt((String) value));
            }
        } else if(value instanceof String) {
            setStringProperty(property, value);
        }
    }
    
    /**
     * @param property
     * @param value
     */
    private void setStringProperty(@Nonnull final String property, @Nonnull final Object value) {
        final String stringValue = ((String) value).trim();
        
        if(property.equals(DDB_PASSWORD)) {
            _cfg.setProperty("hibernate.connection.password", stringValue);
        } else if(property.equals(DDB_USER_NAME)) {
            _cfg.setProperty("hibernate.connection.username", stringValue);
        } else if(property.equals(DIALECT)) {
            _cfg.setProperty("hibernate.dialect", stringValue);
        } else if(property.equals(HIBERNATE_CONNECTION_DRIVER_CLASS)) {
            _cfg.setProperty("hibernate.connection.driver_class", stringValue);
        } else if(property.equals(HIBERNATE_CONNECTION_URL)) {
            _cfg.setProperty("hibernate.connection.url", stringValue);
            
        } else if(property.equals(SHOW_SQL)) {
            _cfg.setProperty("hibernate.show_sql", stringValue);
            _cfg.setProperty("hibernate.format_sql", stringValue);
            _cfg.setProperty("hibernate.use_sql_comments", stringValue);
            
        }
    }
    
    @Nonnull
    protected static synchronized HibernateManager getInstance() {
        if(_INSTANCE == null) {
            _INSTANCE = new HibernateManager();
        }
        return _INSTANCE;
    }
    
    @Override
    @Nonnull
    protected AnnotationConfiguration getCfg() {
        return _cfg;
    }
}
