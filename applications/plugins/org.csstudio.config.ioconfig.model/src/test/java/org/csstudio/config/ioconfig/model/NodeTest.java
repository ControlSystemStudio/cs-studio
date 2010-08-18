package org.csstudio.config.ioconfig.model;

import java.util.Collection;

import org.csstudio.config.ioconfig.model.pbmodel.Channel;
import org.csstudio.config.ioconfig.model.pbmodel.ChannelStructure;
import org.csstudio.config.ioconfig.model.pbmodel.GSDFile;
import org.csstudio.config.ioconfig.model.pbmodel.GSDModule;
import org.csstudio.config.ioconfig.model.pbmodel.Master;
import org.csstudio.config.ioconfig.model.pbmodel.Module;
import org.csstudio.config.ioconfig.model.pbmodel.ModuleChannelPrototype;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnet;
import org.csstudio.config.ioconfig.model.pbmodel.Slave;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class NodeTest {

    @Test
    public void loadNode() {
        
        Collection<Facility> result = Repository.load(Facility.class);
        
        assertFalse(result.isEmpty());
        
        for(Facility f: result) {
            assertNull(f.getParent());
            if (8061 == f.getId()) {
                assertEquals("DB Move Test", f.getName());
                assertEquals(1, f.getChildren().size());
            }
        }
        
    }
    
    @Test
    public void loadIoc() {
        
        Collection<Ioc> result = Repository.load(Ioc.class);

        assertFalse(result.isEmpty());

        for(Ioc ioc: result) {
            assertNotNull(ioc.getParent());
            if (8062== ioc.getId()) {
                assertEquals("DB Move Test", ioc.getParent().getName());
            }
        }
        
    }
    @Test
    public void createDocument() throws PersistenceException {
        Collection<Document> result = Repository.load(Document.class);
        
        assertFalse(result.isEmpty());
        
        Document first = result.iterator().next();
        
        Collection<Facility> facilitys = Repository.load(Facility.class);
        Facility facility = facilitys.iterator().next();
        
        facility.addDocument(first);
        
//        Repository.saveOrUpdate(facility);
        Repository.update(facility);
        
        Facility load = Repository.load(Facility.class, facility.getId());
        assertTrue(load.getDocuments().contains(first));
        
    }
    
    @Test
    public void deletNode() throws PersistenceException {
//        Collection<Facility> result = Repository.load(Facility.class);

        Facility load = Repository.load(Facility.class, 8061);
        Ioc ioc = new Ioc(load);
        ioc.setName("unitest-"+System.currentTimeMillis());
        load.save();

        ProfibusSubnet profibusSubnet = new ProfibusSubnet(ioc);
        ioc.save();

        load.removeChild(ioc);
        load.save();
        
        assertNull(Repository.load(Ioc.class, ioc.getId()));
        assertNull(Repository.load(ProfibusSubnet.class, profibusSubnet.getId()));
        
    }
    
    @Test
    public void loadFacility() {
        
//        Collection<FacilityLight> result = Repository.loadFacilityLight();
        Collection<FacilityLight> result = Repository.load(FacilityLight.class);

        assertFalse(result.isEmpty());
        boolean haveEveryWhereChildren = false;
        for (FacilityLight l: result) {
            haveEveryWhereChildren |= l.hasChildren();
        }
        assertTrue(haveEveryWhereChildren);
    }

    @BeforeClass
    public static void setUp() {
        Repository.injectIRepository(new HibernateRepository());

        Configuration cfg = new AnnotationConfiguration()
                //.addPackage("org.csstudio.config.ioconfig.model")
        .addAnnotatedClass(NodeImage.class)
        .addAnnotatedClass(Channel.class)
        .addAnnotatedClass(ChannelStructure.class)
        .addAnnotatedClass(Module.class)
        .addAnnotatedClass(Slave.class)
        .addAnnotatedClass(Master.class)
        .addAnnotatedClass(ProfibusSubnet.class)
        .addAnnotatedClass(GSDModule.class)
        .addAnnotatedClass(Ioc.class)
        .addAnnotatedClass(Facility.class)
        .addAnnotatedClass(Node.class)
        .addAnnotatedClass(GSDFile.class)
        .addAnnotatedClass(ModuleChannelPrototype.class)
        .addAnnotatedClass(Document.class)
        
        .addAnnotatedClass(FacilityLight.class)
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

        HibernateManager.setSessionFactory(cfg.buildSessionFactory());
        // set Timeout to 1 min
        HibernateManager.setTimeout(60);
    }

}
