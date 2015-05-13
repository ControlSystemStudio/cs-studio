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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

/**
 * @author Joerg Rathlev
 */
public class CommandParametersTest {

    @Test
    public void testSetAndGetParameters() throws Exception {
        CommandParameters params = new CommandParameters();
        assertNull(params.get("string"));
        assertNull(params.get("int"));
        assertNull(params.get("enum"));

        params.set("string", "foo");
        params.set("int", Integer.valueOf(42));
        params.set("enum", new CommandParameterEnumValue("bar", "baz"));

        assertEquals("foo", params.get("string"));
        assertEquals(42, params.get("int"));
        CommandParameterEnumValue e = (CommandParameterEnumValue) params.get("enum");
        assertEquals("bar", e.getValue());
        assertEquals("baz", e.getLabel());
    }

    @Test
    public void testParameterIdentifiers() throws Exception {
        CommandParameters params = new CommandParameters();
        params.set("foo", "bar");
        params.set("baz", "quux");

        Set<String> parameterIDs = params.identifiers();
        assertEquals(2, parameterIDs.size());
        assertTrue(parameterIDs.contains("foo"));
        assertTrue(parameterIDs.contains("baz"));
    }

    @Test
    public void testToString() throws Exception {
        CommandParameters params = new CommandParameters();
        assertNotNull(params.toString());
        params.set("foo", "bar");
        assertNotNull(params.toString());
    }
}
