package org.csstudio.config.ioconfig.model;

import static org.junit.Assert.*;

import java.util.Collection;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.junit.BeforeClass;
import org.junit.Test;

public class DocumentTest {

    @BeforeClass
    public static void setUp() {
        Repository.injectIRepository(new HibernateReposetory());
        Configuration cfg = new AnnotationConfiguration()
                .addAnnotatedClass(Facility.class)
                .addAnnotatedClass(Node.class)
                .addAnnotatedClass(Ioc.class)
                .addAnnotatedClass(Document.class)
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
                .setProperty("hibernate.connection.username", "krykmant")
                .setProperty("hibernate.connection.password", "krykmant")
                .setProperty("transaction.factory_class", "org.hibernate.transaction.JDBCTransactionFactory")
                .setProperty("hibernate.cache.provider_class",
                        "org.hibernate.cache.HashtableCacheProvider")
                        //.setProperty("hibernate.hbm2ddl.auto", "update")
                        .setProperty("hibernate.show_sql", "true");

        HibernateManager.setSessionFactory(cfg.buildSessionFactory());
    }

    @Test
    public void readDocuments() throws PersistenceException {
        Collection<Document> result = Repository.loadDocument(true);
        assertNotNull(result);
        assertTrue(result.size()>0);
    }

}
