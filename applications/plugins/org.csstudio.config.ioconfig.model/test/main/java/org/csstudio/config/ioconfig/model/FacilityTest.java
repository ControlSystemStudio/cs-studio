package org.csstudio.config.ioconfig.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FacilityTest {

    @Test
    public void createFacility() throws PersistenceException{
        final Facility facility = new Facility();
        facility.setCreatedBy("Creater");
        facility.setCreatedOn(Date.valueOf("2011-11-11"));
        facility.setUpdatedBy("Updater");
        facility.setUpdatedOn(Date.valueOf("2012-12-12"));
        facility.setDescription("description line 1\r\ndescription line 2");
        final Document document = new Document("subDoc","descDoc","keyDoc");
        final Set<Document> docs = new HashSet<Document>();
        docs.add(document);
        facility.setDocuments(docs);
        facility.setName("FacNameTest");
        facility.setSortIndexNonHibernate((short)12);
        facility.setVersion(11);

        assertEquals(0,facility.getId());
        assertEquals(null, facility.getParent());
        assertEquals("Creater", facility.getCreatedBy());
        assertEquals(Date.valueOf("2011-11-11"),facility.getCreatedOn());
        assertEquals("Updater", facility.getUpdatedBy());
        assertEquals(Date.valueOf("2012-12-12"), facility.getUpdatedOn());
        assertEquals("description line 1\r\ndescription line 2",facility.getDescription());
        assertEquals(docs,facility.getDocuments());
        assertEquals("FacNameTest",facility.getName());
        assertEquals((short)12, (short) facility.getSortIndex());
        assertEquals(11,facility.getVersion());

        facility.localSave();

        assertTrue(facility.getId()>0);
        assertEquals(null, facility.getParent());
        assertEquals("Creater", facility.getCreatedBy());
        assertEquals(Date.valueOf("2011-11-11"),facility.getCreatedOn());
        assertEquals("Updater", facility.getUpdatedBy());
        assertEquals(Date.valueOf("2012-12-12"), facility.getUpdatedOn());
        assertEquals("description line 1\r\ndescription line 2",facility.getDescription());
        assertEquals(docs,facility.getDocuments());
        assertEquals("FacNameTest",facility.getName());
        assertEquals((short)12, (short) facility.getSortIndex());
        assertEquals(11,facility.getVersion());
    }
    @Before
    public void setUp() throws Exception {
        Repository.injectIRepository(new DummyRepository());
    }
    @After
    public void setDown() {
        Repository.injectIRepository(null);
    }

}
