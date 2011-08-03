package org.csstudio.config.ioconfig.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.csstudio.config.ioconfig.model.hibernate.HibernateRepository;
import org.csstudio.config.ioconfig.model.hibernate.HibernateTestManager;
import org.csstudio.config.ioconfig.model.hibernate.Repository;
import org.csstudio.config.ioconfig.model.pbmodel.ProfibusSubnetDBO;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author hrickens
 * @author $Author: hrickens $
 * @version $Revision: 1.7 $
 * @since 02.08.2011
 */
public class AbstNodeDBOUnitTest {

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
        assertNotNull(load);
        assertTrue(load.getDocuments().contains(first));

    }


    @Ignore("Need a correct TEST DB")
    @Test
    public void deletNode() throws PersistenceException {
//        Collection<Facility> result = Repository.load(Facility.class);

        final FacilityDBO load = Repository.load(FacilityDBO.class, 8061);
        assertNotNull(load);
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
    public static void setUpBeforeClass() {
        final HibernateRepository repository = new HibernateRepository(new HibernateTestManager());
        Repository.injectIRepository(repository);
    }
    
    @AfterClass
    public static void tearDownAfterClass() {
        Repository.close();
    }
}
//CHECKSTYLE:ON
