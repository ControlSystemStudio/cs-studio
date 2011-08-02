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
package org.csstudio.config.ioconfig.model;

import java.util.List;

import javax.annotation.Nonnull;

import org.hibernate.cfg.AnnotationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Hibernate Manager for jUnit tests!
 *
 *
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.14 $
 * @since 03.06.2009
 */
public final class HibernateTestManager extends AbstractHibernateManager {
    
    static final Logger LOG = LoggerFactory.getLogger(HibernateTestManager.class);
    private static HibernateTestManager _INSTANCE;
    
    private AnnotationConfiguration _cfg = new AnnotationConfiguration();

    
    @Override
    protected void buildConifg() {
        
        _cfg = new AnnotationConfiguration();
        for (Class<?> clazz : getClasses()) {
            _cfg.addAnnotatedClass(clazz);
        }
        _cfg.setProperty("org.hibernate.cfg.Environment.MAX_FETCH_DEPTH", "0")
                .setProperty("hibernate.connection.driver_class", "oracle.jdbc.driver.OracleDriver")
                .setProperty("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect")
                .setProperty("hibernate.order_updates", "false")
                .setProperty("hibernate.connection.url",
                             "jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv01.desy.de)(PORT = 1521))(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv02.desy.de)(PORT = 1521))(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv03.desy.de)(PORT = 1521))(LOAD_BALANCE = yes)(CONNECT_DATA = (SERVER = DEDICATED)(SERVICE_NAME = desy_db.desy.de)(FAILOVER_MODE = (TYPE = NONE)(METHOD = BASIC)(RETRIES = 180)(DELAY = 5))))")
                .setProperty("hibernate.connection.username", "KRYKMANT")
                .setProperty("hibernate.connection.password", "KRYKMANT")
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
                .setProperty("c3p0.timeout", "1800").setProperty("c3p0.acquire_increment", "1")
                .setProperty("c3p0.idle_test_period", "100")
                // sec
                .setProperty("c3p0.max_statements", "1")
                //                .setProperty("hibernate.hbm2ddl.auto", "update")
                .setProperty("hibernate.show_sql", "false");
        //                .setProperty("hibernate.format_sql", "true")
        //                .setProperty("hibernate.use_sql_comments", "true")
        //                .setProperty("hibernate.cache.use_second_level_cache", "true");
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    AnnotationConfiguration getCfg() {
        return _cfg;
    }
    
    public void addClasses(@Nonnull final List<Class<?>> classes) {
        getClasses().addAll(classes);
    }
    
    public void removeClasses(@Nonnull final List<Class<?>> classes) {
        getClasses().removeAll(classes);
    }

    @Nonnull
    protected static synchronized HibernateTestManager getInstance() {
        if(_INSTANCE == null) {
            _INSTANCE = new HibernateTestManager();
        }
        return _INSTANCE;
    }
}
