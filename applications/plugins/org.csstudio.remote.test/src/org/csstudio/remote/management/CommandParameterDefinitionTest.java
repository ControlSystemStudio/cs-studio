/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.remote.management;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.csstudio.remote.management.CommandParameterDefinition.Builder;

import org.junit.Before;
import org.junit.Test;


/**
 * Test for CommandParameterDefinition and CommandParameter.
 *
 * @author Joerg Rathlev
 */
public class CommandParameterDefinitionTest {

    private CommandParameterDefinition stringParam;
    private CommandParameterDefinition intParam;
    private CommandParameterDefinition enumParam;
    private CommandParameterDefinition dynamicEnumParam;

    private CommandParameterEnumValue enumValue;

    @Before
    public void setUp() {
        enumValue = new CommandParameterEnumValue("enum", "Enum");

        stringParam = new Builder()
                .setType(CommandParameterType.STRING)
                .setIdentifier("string")
                .setLabel("String")
                .build();
        intParam = new Builder()
                .setType(CommandParameterType.INTEGER)
                .setIdentifier("int")
                .setLabel("Int")
                .setMinimum(Integer.MIN_VALUE)
                .setMaximum(Integer.MAX_VALUE)
                .build();
        enumParam = new Builder()
                .setType(CommandParameterType.ENUMERATION)
                .setIdentifier("enum")
                .setLabel("Enum")
                .addEnumerationValue(enumValue)
                .build();
        dynamicEnumParam = new Builder()
                .setType(CommandParameterType.DYNAMIC_ENUMERATION)
                .setIdentifier("dynamic")
                .setLabel("Dynamic")
                .build();
    }

    @Test
    public void testStringParameterDefinition() throws Exception {
        assertEquals(CommandParameterType.STRING, stringParam.getType());
        assertEquals("string", stringParam.getIdentifier());
        assertEquals("String", stringParam.getLabel());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStringParameterRequiresIdentifier() throws Exception {
        new Builder()
                .setType(CommandParameterType.STRING)
                .setIdentifier(null)
                .setLabel("label")
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStringParameterRequiresLabel() throws Exception {
        new Builder()
                .setType(CommandParameterType.STRING)
                .setIdentifier("id")
                .setLabel(null)
                .build();
    }

    @Test
    public void testLegalStringParameterValues() throws Exception {
        assertTrue(stringParam.isLegalParameterValue("foo"));
        assertTrue(stringParam.isLegalParameterValue(""));

        assertFalse(stringParam.isLegalParameterValue(null));
        assertFalse(stringParam.isLegalParameterValue(new Object()));
        assertFalse(stringParam.isLegalParameterValue(42));
    }

    @Test
    public void testStringParameterHasNoEnumerationValues() throws Exception {
        assertNull(stringParam.getEnumerationValues());
    }

    @Test
    public void testIntegerParameterDefinition() throws Exception {
        assertEquals(CommandParameterType.INTEGER, intParam.getType());
        assertEquals("int", intParam.getIdentifier());
        assertEquals("Int", intParam.getLabel());
        assertEquals(Integer.MIN_VALUE, intParam.getMinimum());
        assertEquals(Integer.MAX_VALUE, intParam.getMaximum());
    }

    @Test
    public void testLegalIntegerParameterValues() throws Exception {
        assertTrue(intParam.isLegalParameterValue(0));
        assertTrue(intParam.isLegalParameterValue(Integer.MIN_VALUE));
        assertTrue(intParam.isLegalParameterValue(Integer.MAX_VALUE));

        assertFalse(intParam.isLegalParameterValue(null));
        assertFalse(intParam.isLegalParameterValue(new Object()));
        assertFalse(intParam.isLegalParameterValue("foo"));
    }

    @Test
    public void testLegalIntegerParameterValuesWithBounds() throws Exception {
        CommandParameterDefinition def = new Builder()
                .setType(CommandParameterType.INTEGER)
                .setIdentifier("id")
                .setLabel("label")
                .setMinimum(0)
                .setMaximum(100)
                .build();

        assertTrue(def.isLegalParameterValue(0));
        assertTrue(def.isLegalParameterValue(50));
        assertTrue(def.isLegalParameterValue(100));

        assertFalse(def.isLegalParameterValue(-1));
        assertFalse(def.isLegalParameterValue(Integer.MIN_VALUE));
        assertFalse(def.isLegalParameterValue(101));
        assertFalse(def.isLegalParameterValue(Integer.MAX_VALUE));
    }

    @Test
    public void testEnumerationParameterDefinition() throws Exception {
        assertEquals(CommandParameterType.ENUMERATION, enumParam.getType());
        assertEquals("enum", enumParam.getIdentifier());
        assertEquals("Enum", enumParam.getLabel());
        assertNotNull(enumParam.getEnumerationValues());
        assertEquals(1, enumParam.getEnumerationValues().length);
        assertArrayEquals(new CommandParameterEnumValue[] {enumValue},
                enumParam.getEnumerationValues());
    }

    @Test
    public void testLegalEnumerationParameterValues() throws Exception {
        assertTrue(enumParam.isLegalParameterValue("enum"));

        // undefined enumeration value
        assertFalse(enumParam.isLegalParameterValue("foo"));

        assertFalse(enumParam.isLegalParameterValue(null));
        assertFalse(enumParam.isLegalParameterValue(new Object()));
        assertFalse(enumParam.isLegalParameterValue(42));
    }

    @Test
    public void testDynamicEnumerationParameterDefinition() throws Exception {
        assertEquals(CommandParameterType.DYNAMIC_ENUMERATION, dynamicEnumParam.getType());
        assertEquals("dynamic", dynamicEnumParam.getIdentifier());
        assertEquals("Dynamic", dynamicEnumParam.getLabel());
    }

    @Test
    public void testLegalDynamicEnumerationParameterValues() throws Exception {
        // the values of a dynamic enumeration are not defined in the parameter
        // definition, so any string value should be legal
        assertTrue(dynamicEnumParam.isLegalParameterValue("foo"));
        assertTrue(dynamicEnumParam.isLegalParameterValue("bar"));

        assertFalse(dynamicEnumParam.isLegalParameterValue(null));
        assertFalse(dynamicEnumParam.isLegalParameterValue(new Object()));
        assertFalse(dynamicEnumParam.isLegalParameterValue(42));
    }
}
