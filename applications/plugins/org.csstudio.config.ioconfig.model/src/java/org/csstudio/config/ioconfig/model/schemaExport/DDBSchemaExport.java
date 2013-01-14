/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
 * $Id: DesyKrykCodeTemplates.xml,v 1.7 2010/04/20 11:43:22 bknerr Exp $
 */
package org.csstudio.config.ioconfig.model.schemaExport;

import java.util.ArrayList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.config.ioconfig.model.AbstractNodeSharedImpl;
import org.csstudio.config.ioconfig.model.DocumentDBO;
import org.csstudio.config.ioconfig.model.FacilityDBO;
import org.csstudio.config.ioconfig.model.IocDBO;
import org.csstudio.config.ioconfig.model.NodeImageDBO;
import org.csstudio.config.ioconfig.model.PV2IONameMatcherModelDBO;
import org.csstudio.config.ioconfig.model.SearchNodeDBO;
import org.csstudio.config.ioconfig.model.SensorsDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelStructureDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFileDBO;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.MasterDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototypeDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleDBO;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnetDBO;
import org.csstudio.config.ioconfig.model.pbmodel.SlaveDBO;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

/**
 * Create a DDL file for all used Table from the IO-Config.
 * 
 * 
 * 
 * 
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 17.05.2011
 */
public final class DDBSchemaExport {
    
    /**
     * Constructor.
     */
    private DDBSchemaExport() {
        // Constructor.
    }
    
    /**
     * @return
     */
    @Nonnull
    private static AnnotationConfiguration buildConfig() {
        final ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        classes.add(NodeImageDBO.class);
        classes.add(ChannelDBO.class);
        classes.add(ChannelStructureDBO.class);
        classes.add(ModuleDBO.class);
        classes.add(SlaveDBO.class);
        classes.add(MasterDBO.class);
        classes.add(ProfibusSubnetDBO.class);
        classes.add(GSDModuleDBO.class);
        classes.add(IocDBO.class);
        classes.add(FacilityDBO.class);
        classes.add(AbstractNodeSharedImpl.class);
        classes.add(GSDFileDBO.class);
        classes.add(ModuleChannelPrototypeDBO.class);
        classes.add(DocumentDBO.class);
        classes.add(SearchNodeDBO.class);
        classes.add(SensorsDBO.class);
        classes.add(PV2IONameMatcherModelDBO.class);
        
        final AnnotationConfiguration cfg = new AnnotationConfiguration();
        for (final Class<?> clazz : classes) {
            cfg.addAnnotatedClass(clazz);
        }
        cfg.setProperty("org.hibernate.cfg.Environment.MAX_FETCH_DEPTH", "0")
        .setProperty("hibernate.connection.driver_class","oracle.jdbc.driver.OracleDriver")
        .setProperty("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect")
        .setProperty("hibernate.order_updates", "false")
        .setProperty("hibernate.connection.url","jdbc:oracle:thin:@localhost:1521:XE")
        .setProperty("hibernate.connection.username","KRYKMAN")
        .setProperty("hibernate.connection.password","KRYKMAN")
        .setProperty("hibernate.show_sql", "false")
        .setProperty("transaction.factory_class",
        "org.hibernate.transaction.JDBCTransactionFactory")
        .setProperty("hibernate.cache.provider_class",
        "org.hibernate.cache.HashtableCacheProvider")
        .setProperty("hibernate.cache.use_minimal_puts", "true")
        .setProperty("hibernate.cache.use_query_cache", "true")
        .setProperty("hibernate.cache.use_second_level_cache", "true")
        .setProperty("hibernate.hbm2ddl.auto", "update");
        return cfg;
    }
    
    /**
     * @param args
     */
    public static void main(@Nullable final String[] args) {
        final AnnotationConfiguration config = buildConfig();
        new SchemaExport(config).setOutputFile("DDB_DDL.sql").create(true, false);
    }
    
    
}
