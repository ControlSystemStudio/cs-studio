package org.csstudio.config.ioconfig.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;

//CHECKSTYLE:OFF
public class DocumentDBOUnitTest {

    @BeforeClass
    public static void setUp() {
        HibernateRepository repository = new HibernateRepository();
        repository.injectHibernateManager(new HibernateTestManager());
        Repository.injectIRepository(repository);
//        Configuration cfg = new AnnotationConfiguration()
//                .addAnnotatedClass(FacilityDBO.class)
//                .addAnnotatedClass(AbstractNodeDBO.class)
//                .addAnnotatedClass(IocDBO.class)
//                .addAnnotatedClass(DocumentDBO.class)
//                .setProperty("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect")
//                //.setProperty("hibernate.connection.datasource", "java:comp/env/jdbc/test")
//                .setProperty("hibernate.order_updates", "true")
//                .setProperty(
//                        "hibernate.connection.url",
//                        "jdbc:oracle:thin:@(DESCRIPTION ="
//                                + "(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv01.desy.de)(PORT = 1521))"
//                                + "(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv02.desy.de)(PORT = 1521))"
//                                + "(ADDRESS = (PROTOCOL = TCP)(HOST = dbsrv03.desy.de)(PORT = 1521))"
//                                + "(LOAD_BALANCE = yes)" + "(CONNECT_DATA ="
//                                + "(SERVER = DEDICATED)" + "(SERVICE_NAME = desy_db.desy.de)"
//                                + "(FAILOVER_MODE =" + "(TYPE = NONE)" + "(METHOD = BASIC)"
//                                + "(RETRIES = 180)" + "(DELAY = 5)" + ")))")
//                .setProperty("hibernate.connection.driver_class", "oracle.jdbc.driver.OracleDriver")
//                .setProperty("hibernate.connection.username", "krykmant")
//                .setProperty("hibernate.connection.password", "krykmant")
//                .setProperty("transaction.factory_class", "org.hibernate.transaction.JDBCTransactionFactory")
//                .setProperty("hibernate.cache.provider_class",
//                        "org.hibernate.cache.HashtableCacheProvider")
//                        //.setProperty("hibernate.hbm2ddl.auto", "update")
//                        .setProperty("hibernate.show_sql", "true");
//
//        HibernateTestManager.getInstance().setSessionFactory(cfg.buildSessionFactory());
    }

    @Test
    public void readDocuments() throws PersistenceException {
        Collection<DocumentDBO> result = Repository.loadDocument(true);
        assertNotNull(result);
        assertTrue(result.size()>0);
    }

}
//CHECKSTYLE:ON
