/**
 *
 */
package org.csstudio.dct.metamodel.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.csstudio.dct.metamodel.IRecordDefinition;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link DatabaseDefinition}.
 *
 * @author Sven Wende
 *
 */
public final class DatabaseDefinitionTest {
    private DatabaseDefinition databaseDefinition;

    private IRecordDefinition recordDefinition1, recordDefinition2;
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        recordDefinition1 = new RecordDefinition("ai");
        recordDefinition2 = new RecordDefinition("ao");

        databaseDefinition = new DatabaseDefinition("1.0.1.a");
        databaseDefinition.addRecordDefinition(recordDefinition1);
        databaseDefinition.addRecordDefinition(recordDefinition2);
    }

    /**
     * Test method for {@link org.csstudio.dct.metamodel.internal.DatabaseDefinition#DatabaseDefinition()}.
     */
    @Test
    public void testDatabaseDefinition() {
        assertEquals("1.0.1.a", databaseDefinition.getDbdVersion());
    }

    /**
     * Test method for {@link org.csstudio.dct.metamodel.internal.DatabaseDefinition#addRecordDefinition(org.csstudio.dct.metamodel.IRecordDefinition)}.
     */
    @Test
    public void testAddRecordDefinition() {
    }

    /**
     * Test method for {@link org.csstudio.dct.metamodel.internal.DatabaseDefinition#getRecordDefinitions()}.
     */
    @Test
    public void testGetRecordDefinitions() {
        List<IRecordDefinition> recordDefinitions = databaseDefinition.getRecordDefinitions();
        assertEquals(2, recordDefinitions.size());
        assertTrue(recordDefinitions.contains(recordDefinition1));
        assertTrue(recordDefinitions.contains(recordDefinition2));
    }

    /**
     * Test method for {@link org.csstudio.dct.metamodel.internal.DatabaseDefinition#getRecordDefinition(String)}.
     */
    @Test
    public void testGetRecordDefinition() {
        assertEquals(recordDefinition1, databaseDefinition.getRecordDefinition("ai"));
        assertEquals(recordDefinition2, databaseDefinition.getRecordDefinition("ao"));
    }

    /**
     * Test method for {@link org.csstudio.dct.metamodel.internal.DatabaseDefinition#removeRecordDefinition(org.csstudio.dct.metamodel.IRecordDefinition)}.
     */
    @Test
    public void testRemoveRecordDefinition() {
        List<IRecordDefinition> recordDefinitions = databaseDefinition.getRecordDefinitions();
        assertTrue(recordDefinitions.contains(recordDefinition1));
        assertTrue(recordDefinitions.contains(recordDefinition2));

        databaseDefinition.removeRecordDefinition(recordDefinition1);
        databaseDefinition.removeRecordDefinition(recordDefinition2);

        recordDefinitions = databaseDefinition.getRecordDefinitions();

        assertEquals(0, recordDefinitions.size());
        assertFalse(recordDefinitions.contains(recordDefinition1));
        assertFalse(recordDefinitions.contains(recordDefinition2));
    }

}
