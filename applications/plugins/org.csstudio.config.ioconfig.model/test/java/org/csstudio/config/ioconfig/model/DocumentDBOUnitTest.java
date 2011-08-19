package org.csstudio.config.ioconfig.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.csstudio.config.ioconfig.model.hibernate.HibernateRepository;
import org.csstudio.config.ioconfig.model.hibernate.HibernateTestManager;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 07.07.2011
 */
public class DocumentDBOUnitTest {

    @BeforeClass
    public static void setUpBeforeClass() {
        final HibernateRepository repository = new HibernateRepository(new HibernateTestManager());
        Repository.injectIRepository(repository);
    }

    @Test
    public void readDocuments() throws PersistenceException {
        final Collection<DocumentDBO> result = Repository.loadDocument(true);
        assertNotNull(result);
        assertTrue(result.size()>0);
    }
    
    @AfterClass
    public static void tearDownAfterClass() {
        Repository.close();
    }
}
