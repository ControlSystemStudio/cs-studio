/**
 *
 */
package org.csstudio.dct.model.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.UUID;

import org.csstudio.dct.metamodel.internal.RecordDefinition;
import org.csstudio.dct.model.IPrototype;
import org.csstudio.dct.model.IRecord;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link Record}.
 *
 * @author Sven Wende
 *
 */
public final class RecordTest {
    private BaseRecord baseRecord;
    private Record parentRecord;
    private Record record;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        baseRecord = new BaseRecord(new RecordDefinition("ai"));

        parentRecord = new Record(baseRecord, UUID.randomUUID());
        parentRecord.setName("Pump");
        parentRecord.setEpicsName("record_$a$");
        parentRecord.addField("field1", "value1");
        parentRecord.addField("field2", "value2");
        parentRecord.addField("field3", "value3");
        parentRecord.addProperty("property1", "propertyvalue1");
        parentRecord.addProperty("property2", "propertyvalue2");
        parentRecord.addProperty("property3", "propertyvalue3");

        record = new Record(parentRecord, UUID.randomUUID());
        record.addField("field4", "value4");
        record.addProperty("property4", "propertyvalue4");
    }

    /**
     * Test method for {@link org.csstudio.dct.model.internal.Record#equals()}.
     */
    @Test
    public void testEquals() {
        UUID uid = UUID.randomUUID();
        Record r1 = new Record(record, uid);
        Record r2 = new Record(record, uid);

        // .. parent
        assertEquals(r1, r2);

        // .. name
        r1.setName("xx");
        assertNotSame(r1, r2);
        r2.setName("xx");
        assertEquals(r1, r2);

        // .. fields
        r1.addField("a", "a");
        assertNotSame(r1, r2);
        r2.addField("a", "a");
        assertEquals(r1, r2);

        // .. properties
        r1.addProperty("a", "a");
        assertNotSame(r1, r2);
        r2.addProperty("a", "a");
        assertEquals(r1, r2);

        // .. container
        Prototype prototype = new Prototype("a", uid);
        r1.setContainer(prototype);
        assertNotSame(r1, r2);
        r2.setContainer(prototype);
        assertEquals(r1, r2);
    }

    /**
     * Test method for {@link org.csstudio.dct.model.internal.Record#getType()}.
     */
    @Test
    public  void testGetType() {
        assertEquals("ai", parentRecord.getType());
        assertEquals("ai", record.getType());
    }

    /**
     * Test method for
     * {@link org.csstudio.dct.model.internal.Record#getFinalProperties()}.
     */
    @Test
    public  void testGetFinalProperties() {
        Map<String, String> parentProperties = parentRecord.getProperties();
        Map<String, String> properties = record.getProperties();
        Map<String, String> finalProperties = record.getFinalProperties();
        assertNotNull(parentProperties);
        assertNotNull(properties);
        assertNotNull(finalProperties);
        assertEquals(3, parentProperties.size());
        assertEquals(1, properties.size());
        assertEquals(4, finalProperties.size());

        assertEquals(finalProperties.get("property1"), "propertyvalue1");
        assertEquals(finalProperties.get("property2"), "propertyvalue2");
        assertEquals(finalProperties.get("property3"), "propertyvalue3");
        assertEquals(finalProperties.get("property4"), "propertyvalue4");

        // test override
        parentRecord.addProperty("property2", "newpropertyvalue2");
        record.addProperty("property1", "newpropertyvalue1");
        finalProperties = record.getFinalProperties();
        assertEquals(finalProperties.get("property1"), "newpropertyvalue1");
        assertEquals(finalProperties.get("property2"), "newpropertyvalue2");

    }

    /**
     * Test method for
     * {@link org.csstudio.dct.model.internal.Record#addField(java.lang.String, java.lang.Object)}
     * .
     */
    @Test
    public void testAddField() {
        assertNull(record.getField("field5"));
        record.addField("field5", "value5");
        assertEquals("value5", record.getField("field5"));
    }

    /**
     * Test method for
     * {@link org.csstudio.dct.model.internal.Record#getField(java.lang.String)}
     * .
     */
    @Test
    public void testGetField() {
        assertEquals("value1", parentRecord.getField("field1"));
        assertEquals("value2", parentRecord.getField("field2"));
        assertEquals("value3", parentRecord.getField("field3"));
    }

    /**
     * Test method for
     * {@link org.csstudio.dct.model.internal.Record#removeField(java.lang.String)}
     * .
     */
    @Test
    public void testRemoveField() {
        assertEquals("value1", parentRecord.getField("field1"));
        parentRecord.removeField("field1");
        assertNull(parentRecord.getField("field1"));
    }

    /**
     * Test method for
     * {@link org.csstudio.dct.model.internal.Record#getFields()}.
     */
    @Test
    public void testGetFields() {
        Map<String, String> fields = record.getFields();
        assertNotNull(fields);
        assertFalse(fields.isEmpty());
        assertTrue(fields.containsKey("field4"));
        assertEquals(1, fields.size());
    }

    /**
     * Test method for
     * {@link org.csstudio.dct.model.internal.Record#getFinalFields()}.
     */
    @Test
    public void testGetFinalFields() {
        Map<String, String> parentFields = parentRecord.getFields();
        Map<String, String> fields = record.getFields();
        Map<String, String> finalFields = record.getFinalFields();
        assertNotNull(parentFields);
        assertNotNull(fields);
        assertNotNull(finalFields);
        assertEquals(3, parentFields.size());
        assertEquals(1, fields.size());
        assertEquals(4, finalFields.size());

        assertEquals(finalFields.get("field1"), "value1");
        assertEquals(finalFields.get("field2"), "value2");
        assertEquals(finalFields.get("field3"), "value3");
        assertEquals(finalFields.get("field4"), "value4");

        // test override
        parentRecord.addField("field2", "newvalue2");
        record.addField("field1", "newvalue1");
        finalFields = record.getFinalFields();
        assertEquals(finalFields.get("field1"), "newvalue1");
        assertEquals(finalFields.get("field2"), "newvalue2");

    }

    /**
     * Test method for
     * {@link org.csstudio.dct.model.internal.Record#getEpicsNameFromHierarchy(boolean)}
     * .
     */
    public void testGetEpicsNameFromHierarchy() {
        assertEquals("record_$a$", parentRecord.getEpicsName());
        assertNull(record.getEpicsName());
        assertEquals("record_$a$", parentRecord.getEpicsNameFromHierarchy());
        assertEquals("record_$a$", record.getEpicsNameFromHierarchy());

        record.setEpicsName("myname");
        assertEquals("record_$a$", parentRecord.getEpicsName());
        assertEquals("myname", record.getEpicsName());
        assertEquals("record_$a$", parentRecord.getEpicsNameFromHierarchy());
        assertEquals("myname", record.getEpicsNameFromHierarchy());
    }

    /**
     * Test method for
     * {@link org.csstudio.dct.model.internal.Record#getParentRecord()}.
     */
    @Test
    public void testGetParentRecord() {
        assertNull(baseRecord.getParentRecord());
        assertEquals(baseRecord, parentRecord.getParentRecord());
        assertEquals(parentRecord, record.getParentRecord());
    }

    /**
     * Test method for
     * {@link org.csstudio.dct.model.internal.Record#getContainer()}.
     */
    @Test
    public void testGetContainer() {
        assertNull(parentRecord.getContainer());
        assertNull(record.getContainer());
    }

    /**
     * Test method for
     * {@link org.csstudio.dct.model.internal.Record#setContainer(org.csstudio.dct.model.IRecordContainer)}
     * .
     */
    @Test
    public void testSetContainer() {
        Prototype prototype = new Prototype("p", UUID.randomUUID());
        record.setContainer(prototype);
        assertEquals(prototype, record.getContainer());
    }

    /**
         * Test method for
         * {@link org.csstudio.dct.model.internal.Record#isInherited()}
         * .
         */
        @Test
        public void testIsInherited() {
            Prototype prototype = new Prototype("p", UUID.randomUUID());
            parentRecord.setContainer(prototype);
            assertEquals(prototype, parentRecord.getContainer());

            assertFalse(parentRecord.isInherited());
            assertTrue(record.isInherited());

        }

    /**
     * Test method for
     * {@link org.csstudio.dct.model.internal.Record#addDependentRecord(org.csstudio.dct.model.IRecord)}
     * .
     */
    @Test
    public void testGetAddRemoveDependentRecord() {
        assertTrue(parentRecord.getDependentRecords().isEmpty());
        // add
        parentRecord.addDependentRecord(record);
        assertEquals(1, parentRecord.getDependentRecords().size());
        assertEquals(record, parentRecord.getDependentRecords().get(0));
        // remove
        parentRecord.removeDependentRecord(record);
        assertTrue(parentRecord.getDependentRecords().isEmpty());
    }

    /**
     * Test method for
     * {@link org.csstudio.dct.model.internal.Record#equals(Object)} .
     */
    @Test
    public void testEqualsHashCode() {
        UUID id = UUID.randomUUID();

        // .. ids must equal
        IRecord r1 = new Record("name", "ai", id);
        IRecord r2 = new Record("name", "ai", id);
        IRecord r3 = new Record("name", "ai", UUID.randomUUID());

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertNotSame(r1, r3);
        assertNotSame(r1.hashCode(), r3.hashCode());
        assertNotSame(r2, r3);
        assertNotSame(r2.hashCode(), r3.hashCode());

        // .. type must equal
        IRecord r4 = new Record("name", "ao", id);
        assertNotSame(r1, r4);
        assertNotSame(r1.hashCode(), r4.hashCode());

        // .. container must equals
        IPrototype prototype = new Prototype("test", UUID.randomUUID());
        r1.setContainer(prototype);
        assertNotSame(r1, r2);
        assertNotSame(r1.hashCode(), r2.hashCode());
        r2.setContainer(prototype);
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());

        // .. parent must equal
        IRecord r5 = new Record(record, id);
        assertNotSame(r1, r5);
        assertNotSame(r1.hashCode(), r5.hashCode());

        // .. properties must equal
        r1.addProperty("a", "a");
        assertNotSame(r1, r2);
        assertNotSame(r1.hashCode(), r2.hashCode());
        r2.addProperty("a", "a");
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());

        // .. fields must equal
        r1.addField("f1", "v1");
        assertNotSame(r1, r2);
        assertNotSame(r1.hashCode(), r2.hashCode());
        r2.addField("f1", "v1");
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());

        // .. names must equal
        r1.setName("x");
        assertNotSame(r1, r2);
        assertNotSame(r1.hashCode(), r2.hashCode());
        r2.setName("x");
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());




}

}
