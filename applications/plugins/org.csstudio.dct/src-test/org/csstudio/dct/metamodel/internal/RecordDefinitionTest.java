/**
 *
 */
package org.csstudio.dct.metamodel.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.csstudio.dct.metamodel.IFieldDefinition;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link RecordDefinition}.
 *
 * @author Sven Wende
 *
 */
public final class RecordDefinitionTest {

    private RecordDefinition recordDefinition;

    private IFieldDefinition fieldDefinition1, fieldDefinition2;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        fieldDefinition1 = new FieldDefinition("SEVR", "DBF_MENU");
        fieldDefinition2 = new FieldDefinition("PREC", "DBF_SHORT");

        recordDefinition = new RecordDefinition("ai");
        recordDefinition.addFieldDefinition(fieldDefinition1);
        recordDefinition.addFieldDefinition(fieldDefinition2);
    }

    /**
     * Test method for {@link org.csstudio.dct.metamodel.internal.RecordDefinition#RecordDefinition(java.lang.String)}.
     */
    @Test
    public void testRecordDefinition() {
        assertEquals("ai", recordDefinition.getType());
    }

    /**
     * Test method for
     * {@link org.csstudio.dct.metamodel.internal.RecordDefinition#getFieldDefinitions(String)}.
     */
    @Test
    public void testGetFieldDefinition() {
        assertEquals(fieldDefinition1, recordDefinition.getFieldDefinitions("SEVR"));
        assertEquals(fieldDefinition2, recordDefinition.getFieldDefinitions("PREC"));
    }

    /**
     * Test method for
     * {@link org.csstudio.dct.metamodel.internal.RecordDefinition#getFieldDefinitions()}.
     */
    @Test
    public void testGetFieldDefinitions() {
        Collection<IFieldDefinition> fields = recordDefinition.getFieldDefinitions();
        assertEquals(2, fields.size());
        assertTrue(fields.contains(fieldDefinition1));
        assertTrue(fields.contains(fieldDefinition2));
    }

    /**
     * Test method for
     * {@link org.csstudio.dct.metamodel.internal.RecordDefinition#addFieldDefinition(org.csstudio.dct.metamodel.IFieldDefinition)}.
     */
        @Test
        public void testAddFieldDefinition() {
            IFieldDefinition def = new FieldDefinition("EGUL", "DBF_DOUBLE");
            assertFalse(recordDefinition.getFieldDefinitions().contains(def));
            recordDefinition.addFieldDefinition(def);
            assertTrue(recordDefinition.getFieldDefinitions().contains(def));
        }

    /**
         * Test method for {@link org.csstudio.dct.metamodel.internal.RecordDefinition#removeFieldDefinition(org.csstudio.dct.metamodel.IFieldDefinition)}.
         */
        @Test
        public void testRemoveFieldDefinition() {
            assertTrue(recordDefinition.getFieldDefinitions().contains(fieldDefinition1));
            assertTrue(recordDefinition.getFieldDefinitions().contains(fieldDefinition2));
            recordDefinition.removeFieldDefinition(fieldDefinition1);
            recordDefinition.removeFieldDefinition(fieldDefinition2);
            assertFalse(recordDefinition.getFieldDefinitions().contains(fieldDefinition1));
            assertFalse(recordDefinition.getFieldDefinitions().contains(fieldDefinition2));
            assertTrue(recordDefinition.getFieldDefinitions().isEmpty());
        }

    /**
     * Test method for {@link org.csstudio.dct.metamodel.internal.RecordDefinition#getType()}.
     */
    @Test
    public void testGetType() {
        assertEquals("ai", recordDefinition.getType());
    }

}
