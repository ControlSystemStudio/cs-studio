package org.csstudio.config.ioconfig.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

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
import org.hibernate.cfg.Configuration;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

//CHECKSTYLE:OFF
public class AbstractNodeDBOTest {

    @Test
    @Ignore("Need a correct TEST DB")
    public void loadNode() throws PersistenceException {

        final Collection<FacilityDBO> result = Repository.load(FacilityDBO.class);

        assertFalse(result.isEmpty());

        for(final FacilityDBO f: result) {
            assertNull(f.getParent());
            if (8061 == f.getId()) {
                assertEquals("XMTS", f.getName());
//                assertEquals("DB Move Test", f.getName());
                assertEquals(19, f.getChildren().size());
            }
        }

    }

    @Test
    @Ignore("Need a correct TEST DB")
    public void loadIoc() throws PersistenceException {

        final Collection<IocDBO> result = Repository.load(IocDBO.class);

        assertFalse(result.isEmpty());

        for(final IocDBO ioc: result) {
            assertNotNull(ioc.getParent());
            if (8062== ioc.getId()) {
                assertEquals("XMTS", ioc.getParent().getName());
            }
        }

    }

    @Test
    @Ignore("Need a correct TEST DB")
    public void createDocument() throws PersistenceException {
        final Collection<DocumentDBO> result = Repository.load(DocumentDBO.class);

        assertFalse(result.isEmpty());

        final DocumentDBO first = result.iterator().next();

        final Collection<FacilityDBO> facilitys = Repository.load(FacilityDBO.class);
        final FacilityDBO facility = facilitys.iterator().next();

        facility.addDocument(first);

//        Repository.saveOrUpdate(facility);
        Repository.update(facility);

        final FacilityDBO load = Repository.load(FacilityDBO.class, facility.getId());
        assertTrue(load.getDocuments().contains(first));

    }


    @Ignore("Need a correct TEST DB")
    @Test
    public void deletNode() throws PersistenceException {
//        Collection<Facility> result = Repository.load(Facility.class);

        final FacilityDBO load = Repository.load(FacilityDBO.class, 8061);
        final IocDBO ioc = new IocDBO(load);
        ioc.setName("unitest-"+System.currentTimeMillis());
        load.save();

        final ProfibusSubnetDBO profibusSubnet = new ProfibusSubnetDBO(ioc);
        ioc.save();

        load.removeChild(ioc);
        load.save();

        assertNull(Repository.load(IocDBO.class, ioc.getId()));
        assertNull(Repository.load(ProfibusSubnetDBO.class, profibusSubnet.getId()));

    }

    @BeforeClass
    public static void setUp() {
        Repository.injectIRepository(new HibernateRepository());

        final Configuration cfg = new AnnotationConfiguration()
                //.addPackage("org.csstudio.config.ioconfig.model")
        .addAnnotatedClass(NodeImageDBO.class)
        .addAnnotatedClass(ChannelDBO.class)
        .addAnnotatedClass(ChannelStructureDBO.class)
        .addAnnotatedClass(ModuleDBO.class)
        .addAnnotatedClass(SlaveDBO.class)
        .addAnnotatedClass(MasterDBO.class)
        .addAnnotatedClass(ProfibusSubnetDBO.class)
        .addAnnotatedClass(GSDModuleDBO.class)
        .addAnnotatedClass(IocDBO.class)
        .addAnnotatedClass(FacilityDBO.class)
        .addAnnotatedClass(AbstractNodeDBO.class)
        .addAnnotatedClass(GSDFileDBO.class)
        .addAnnotatedClass(ModuleChannelPrototypeDBO.class)
        .addAnnotatedClass(DocumentDBO.class)
                .setProperty("hibernate.dialect", "org.hibernate.dialect.Oracle9Dialect")
                //.setProperty("hibernate.connection.datasource", "java:comp/env/jdbc/test")
                .setProperty("hibernate.order_updates", "true")
                .setProperty(
                        "hibernate.connection.url",
                        "jdbc:oracle:thin:@(DESCRIPTION ="
                                + "(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv01.desy.de)(PORT = 1521))"
                                + "(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv02.desy.de)(PORT = 1521))"
                                + "(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv03.desy.de)(PORT = 1521))"
                                + "(LOAD_BALANCE = yes)" + "(CONNECT_DATA ="
                                + "(SERVER = DEDICATED)" + "(SERVICE_NAME = desy_db.desy.de)"
                                + "(FAILOVER_MODE =" + "(TYPE = NONE)" + "(METHOD = BASIC)"
                                + "(RETRIES = 180)" + "(DELAY = 5)" + ")))")
                .setProperty("hibernate.connection.driver_class", "oracle.jdbc.driver.OracleDriver")
                .setProperty("hibernate.connection.username", "krykmant").setProperty(
                        "hibernate.connection.password", "krykmant").setProperty(
                        "transaction.factory_class",
                        "org.hibernate.transaction.JDBCTransactionFactory").setProperty(
                        "hibernate.cache.provider_class",
                        "org.hibernate.cache.HashtableCacheProvider")
                        //.setProperty("hibernate.hbm2ddl.auto", "update")
                .setProperty("hibernate.show_sql", "false");

        HibernateManager.getInstance().setSessionFactory(cfg.buildSessionFactory());
        // set Timeout to 1 min
        HibernateManager.getInstance().setTimeout(60);
    }

}
//CHECKSTYLE:ON
