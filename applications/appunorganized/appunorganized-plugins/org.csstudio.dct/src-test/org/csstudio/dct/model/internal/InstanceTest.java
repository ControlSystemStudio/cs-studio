/**
 *
 */
package org.csstudio.dct.model.internal;

import static org.easymock.classextension.EasyMock.createMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.UUID;

import org.csstudio.dct.model.IContainer;
import org.csstudio.dct.model.IPrototype;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link Instance}.
 *
 * @author Sven Wende
 *
 */
public final class InstanceTest {
    private IPrototype prototype1, prototype2;

    private Instance instance;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        prototype1 = new Prototype("prototype1", UUID.randomUUID());
        prototype1.addRecord(new Record("r1", "ai", UUID.randomUUID()));
        prototype1.addRecord(new Record("r2", "ai", UUID.randomUUID()));

        prototype2 = new Prototype("prototype2", UUID.randomUUID());
        prototype2.addRecord(new Record("r3", "ai", UUID.randomUUID()));
        prototype2.addInstance(new Instance(prototype1, UUID.randomUUID()));

        instance = new Instance("instance", prototype2, UUID.randomUUID());
        instance.setParameterValue("name_a", "value_a");
        instance.setParameterValue("name_b", "value_b");
    }

    /**
     * Test method for {@link org.csstudio.dct.model.internal.Instance#equals()}.
     */
    @Test
    public void testEquals() {
        UUID uid = UUID.randomUUID();
        Instance i1 = new Instance(prototype1, uid);
        Instance i2 = new Instance(prototype1, uid);

        // .. parent
        assertEquals(i1, i2);

        // .. name
        i1.setName("A");
        assertNotSame(i1, i2);
        i2.setName("A");
        assertEquals(i1, i2);

        // .. parameter values
        i1.setParameterValue("a","a");
        assertNotSame(i1, i2);
        i2.setParameterValue("a","a");
        assertEquals(i1, i2);

        // .. properties
        i1.addProperty("a","a");
        assertNotSame(i1, i2);
        i2.addProperty("a","a");
        assertEquals(i1, i2);

        // .. records
        Record record = new Record("r", "ai", uid);
        i1.addRecord(record);
        assertNotSame(i1, i2);
        i2.addRecord(record);
        assertEquals(i1, i2);

        // .. instances
        i1.addInstance(instance);
        assertNotSame(i1, i2);
        i2.addInstance(instance);
        assertEquals(i1, i2);

    }


    /**
     * Test method for {@link org.csstudio.dct.model.internal.Instance#Instance(org.csstudio.dct.model.IContainer)}.
     */
    @Test
    public void testInstanceIContainer() {
        assertEquals(prototype2, instance.getParent());
    }

    /**
     * Test method for {@link org.csstudio.dct.model.internal.Instance#Instance(java.lang.String, org.csstudio.dct.model.IContainer)}.
     */
    @Test
    public void testInstanceStringIContainer() {
        assertEquals("instance", instance.getName());
        assertEquals(prototype2, instance.getParent());
    }

    /**
     * Test method for {@link org.csstudio.dct.model.internal.Instance#getParameterValues()}.
     */
    @Test
    public void testGetParameterValues() {
        Map<String, String> values = instance.getParameterValues();
        assertEquals(2, values.size());
        assertEquals("value_a", values.get("name_a"));
        assertEquals("value_b", values.get("name_b"));
    }

    /**
     * Test method for {@link org.csstudio.dct.model.internal.Instance#setParameterValue(java.lang.String, java.lang.String)}.
     */
    @Test
    public void testSetParameterValue() {
        // change a parameter value
        instance.setParameterValue("name_a", "value_a_changed");

        // add a new parameter value
        instance.setParameterValue("name_c", "value_c");

        Map<String, String> values = instance.getParameterValues();
        assertEquals(3, values.size());
        assertEquals("value_a_changed", values.get("name_a"));
        assertEquals("value_b", values.get("name_b"));
        assertEquals("value_c", values.get("name_c"));
    }

    /**
     * Test method for {@link org.csstudio.dct.model.internal.Instance#getParameterValue(java.lang.String)}.
     */
    @Test
    public void testGetParameterValue() {
        assertEquals("value_a", instance.getParameterValue("name_a"));
        assertEquals("value_b", instance.getParameterValue("name_b"));
    }

    /**
     * Test method for {@link org.csstudio.dct.model.internal.Instance#hasParameterValue(java.lang.String)}.
     */
    @Test
    public void testHasParameterValue() {
        assertTrue(instance.hasParameterValue("name_a"));
        assertTrue(instance.hasParameterValue("name_b"));
        assertFalse(instance.hasParameterValue("name_x"));
    }

    /**
     * Test method for {@link org.csstudio.dct.model.internal.Instance#getContainer()}.
     */
    @Test
    public void testGetContainer() {
        assertNull(instance.getContainer());
    }

    /**
     * Test method for {@link org.csstudio.dct.model.internal.Instance#setContainer(org.csstudio.dct.model.IInstanceContainer)}.
     */
    @Test
    public void testSetContainer() {
        IContainer container = createMock(IContainer.class);
        instance.setContainer(container);
        assertEquals(container, instance.getContainer());
    }

    /**
     * Test method for {@link org.csstudio.dct.model.internal.Instance#getPrototype()}.
     */
    @Test
    public void testGetPrototype() {
        assertEquals(prototype2, instance.getPrototype());
    }

}
