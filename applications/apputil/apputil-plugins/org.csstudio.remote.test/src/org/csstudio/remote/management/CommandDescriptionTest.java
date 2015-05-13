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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


/**
 * @author Joerg Rathlev
 *
 */
public class CommandDescriptionTest {

    @Test
    public void testCommandWithNoParameters() throws Exception {
        // using null to create a command with no parameters; getParameters()
        // must not return null but an empty array
        CommandDescription cd = new CommandDescription("id", "label", null);
        assertEquals("id", cd.getIdentifier());
        assertEquals("label", cd.getLabel());
        assertNotNull(cd.getParameters());
        assertEquals(0, cd.getParameters().length);
    }

    @Test
    public void testCommandWithNoParametersEmptyArray() throws Exception {
        // using empty array to create a command with no parameters
        CommandDescription cd = new CommandDescription("id", "label",
                new CommandParameterDefinition[0]);
        assertEquals("id", cd.getIdentifier());
        assertEquals("label", cd.getLabel());
        assertNotNull(cd.getParameters());
        assertEquals(0, cd.getParameters().length);
    }

    @Test
    public void testCommandWithOneParameter() throws Exception {
        CommandParameterDefinition p1 = new CommandParameterDefinition.Builder()
                .setType(CommandParameterType.STRING)
                .setIdentifier("p1")
                .setLabel("p1-label")
                .build();
        CommandDescription cd = new CommandDescription("id", "label",
                new CommandParameterDefinition[] { p1 });
        assertEquals("id", cd.getIdentifier());
        assertEquals("label", cd.getLabel());
        assertEquals(1, cd.getParameters().length);
        assertEquals(p1, cd.getParameters()[0]);
    }

    @Test
    public void testEqualityAndHashCode() throws Exception {
        CommandDescription cd1 = new CommandDescription("id1", "label", null);
        CommandDescription cd2 = new CommandDescription("id1", "other", null);
        CommandDescription cd3 = new CommandDescription("id3", "label", null);

        // reflexivity: object equals itself
        assertTrue(cd1.equals(cd1));
        assertTrue(cd2.equals(cd2));
        assertTrue(cd3.equals(cd3));

        // cd1 and cd2 have the same ID, should be equal and have same hash code
        assertTrue(cd1.equals(cd2));
        assertTrue(cd2.equals(cd1));
        assertTrue(cd1.hashCode() == cd2.hashCode());

        // neither cd1 nor cd2 is equal to cd3 because the IDs differ
        assertFalse(cd1.equals(cd3));
        assertFalse(cd3.equals(cd1));
        assertFalse(cd2.equals(cd3));
        assertFalse(cd3.equals(cd2));

        // nothing is equal to null
        assertFalse(cd1.equals(null));
        assertFalse(cd2.equals(null));
        assertFalse(cd3.equals(null));
    }

    @Test
    public void testToStringReturnsAString() throws Exception {
        // the exact string is not tested because it is not specified
        assertNotNull(new CommandDescription("id", "label", null).toString());
    }

    @Test(expected = NullPointerException.class)
    public void testIdMustNoBeNull() throws Exception {
        new CommandDescription(null, "label", null);
    }

    @Test(expected = NullPointerException.class)
    public void testLabelMustNotBeNull() throws Exception {
        new CommandDescription("id", null, null);
    }
}
