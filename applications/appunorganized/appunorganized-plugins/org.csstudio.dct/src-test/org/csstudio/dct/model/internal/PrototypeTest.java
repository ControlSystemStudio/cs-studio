/**
 *
 */
package org.csstudio.dct.model.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.UUID;

import org.csstudio.dct.model.IPrototype;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link Prototype}.
 *
 * @author Sven Wende
 *
 */
public final class PrototypeTest {
    private IPrototype prototype;
    private Parameter parameter1;
    private Parameter parameter2;
    private Parameter parameter3;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        prototype = new Prototype("prototype", UUID.randomUUID());
        parameter1 = new Parameter("p1", "1");
        parameter2 = new Parameter("p2", "2");
        parameter3 = new Parameter("p3", "3");

        prototype.addParameter(parameter1);
        prototype.addParameter(parameter2);
    }

    /**
     * Test method for {@link org.csstudio.dct.model.internal.Prototype#equals()}.
     */
    @Test
    public void testEquals() {
        UUID uid = UUID.randomUUID();
        Prototype p1 = new Prototype("p", uid);
        Prototype p2 = new Prototype("p", uid);

        // .. parent
        assertEquals(p1, p2);

        // .. parameters
        p1.addParameter(new Parameter("p1", "v1"));
        assertNotSame(p1, p2);
        p2.addParameter(new Parameter("p1", "v1"));
        assertEquals(p1, p2);

        // .. name
        p1.setName("A");
        assertNotSame(p1, p2);
        p2.setName("A");
        assertEquals(p1, p2);

        // .. properties
        p1.addProperty("a","a");
        assertNotSame(p1, p2);
        p2.addProperty("a","a");
        assertEquals(p1, p2);

        // .. records
        Record record = new Record("r", "ai", uid);
        p1.addRecord(record);
        assertNotSame(p1, p2);
        p2.addRecord(record);
        assertEquals(p1, p2);

        // .. instances
        Instance instance = new Instance(prototype, uid);
        p1.addInstance(instance);
        assertNotSame(p1, p2);
        p2.addInstance(instance);
        assertEquals(p1, p2);

    }

    /**
     * Test method for {@link org.csstudio.dct.model.internal.Prototype#getParameters()}.
     */
    @Test
    public void testGetParameters() {
        assertEquals(2, prototype.getParameters().size());
        assertEquals(parameter1, prototype.getParameters().get(0));
        assertEquals(parameter2, prototype.getParameters().get(1));
    }

    /**
     * Test method for {@link org.csstudio.dct.model.internal.Prototype#addParameter(org.csstudio.dct.model.internal.Parameter)}.
     */
    @Test
    public void testAddParameterParameter() {
        prototype.addParameter(parameter3);
        assertEquals(3, prototype.getParameters().size());
        assertEquals(parameter1, prototype.getParameters().get(0));
        assertEquals(parameter2, prototype.getParameters().get(1));
        assertEquals(parameter3, prototype.getParameters().get(2));
    }

    /**
     * Test method for {@link org.csstudio.dct.model.internal.Prototype#addParameter(int, org.csstudio.dct.model.internal.Parameter)}.
     */
    @Test
    public void testAddParameterIntParameter() {
        prototype.addParameter(1, parameter3);
        assertEquals(3, prototype.getParameters().size());
        assertEquals(parameter1, prototype.getParameters().get(0));
        assertEquals(parameter3, prototype.getParameters().get(1));
        assertEquals(parameter2, prototype.getParameters().get(2));
    }

    /**
     * Test method for {@link org.csstudio.dct.model.internal.Prototype#removeParameter(org.csstudio.dct.model.internal.Parameter)}.
     */
    @Test
    public void testRemoveParameterParameter() {
        prototype.removeParameter(parameter2);
        assertEquals(1, prototype.getParameters().size());
        assertEquals(parameter1, prototype.getParameters().get(0));
    }

    /**
     * Test method for {@link org.csstudio.dct.model.internal.Prototype#removeParameter(int)}.
     */
    @Test
    public void testRemoveParameterInt() {
        prototype.removeParameter(1);
        assertEquals(1, prototype.getParameters().size());
        assertEquals(parameter1, prototype.getParameters().get(0));
    }

    /**
     * Test method for {@link org.csstudio.dct.model.internal.Prototype#hasParameter(java.lang.String)}.
     */
    @Test
    public void testHasParameter() {
        assertTrue(prototype.hasParameter("p1"));
        assertFalse(prototype.hasParameter("xx"));
    }

    /**
     * Test method for {@link org.csstudio.dct.model.internal.Prototype#getParameterValues()}.
     */
    @Test
    public void testGetParameterValues() {
        Map<String, String> values = prototype.getParameterValues();

        assertEquals(2, values.size());
        assertEquals("1", values.get("p1"));
        assertEquals("2", values.get("p2"));
    }

}
