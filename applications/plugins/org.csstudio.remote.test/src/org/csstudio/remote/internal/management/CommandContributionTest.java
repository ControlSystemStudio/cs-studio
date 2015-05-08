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

package org.csstudio.remote.internal.management;


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.csstudio.remote.management.CommandParameterDefinition;
import org.csstudio.remote.management.CommandParameterEnumValue;
import org.csstudio.remote.management.CommandParameterType;
import org.csstudio.remote.management.CommandParameters;
import org.csstudio.remote.management.CommandResult;
import org.csstudio.remote.management.IDynamicParameterValues;
import org.csstudio.remote.management.IManagementCommand;
import org.junit.Before;
import org.junit.Test;


/**
 * @author Joerg Rathlev
 */
public class CommandContributionTest {

    private IManagementCommand _dummyImplementation;

    @Before
    public void setUp() {
        _dummyImplementation = new IManagementCommand() {
            public CommandResult execute(CommandParameters parameters) {
                return null;
            }
        };
    }

    @Test
    public void testDescription() throws Exception {
        CommandContribution c = new CommandContribution.Builder()
                .setIdentifier("id")
                .setLabel("label")
                .setCommandImplementation(_dummyImplementation)
                .build();
        assertEquals("id", c.getDescription().getIdentifier());
        assertEquals("label", c.getDescription().getLabel());
        assertEquals(0, c.getDescription().getParameters().length);
        assertSame(_dummyImplementation, c.getCommandImplementation());
    }

    @Test
    public void testStringParameter() throws Exception {
        CommandParameterDefinition parameterDefinition = new CommandParameterDefinition.Builder()
                .setIdentifier("param")
                .setLabel("Parameter")
                .setType(CommandParameterType.STRING)
                .build();
        CommandContribution c = new CommandContribution.Builder()
                .setIdentifier("id")
                .setLabel("label")
                .setCommandImplementation(_dummyImplementation)
                .addParameter(parameterDefinition, null)
                .build();
        assertEquals(1, c.getDescription().getParameters().length);
        assertEquals(parameterDefinition, c.getDescription().getParameters()[0]);
    }

    @Test
    public void testDynamicEnumerationParameter() throws Exception {
        CommandParameterDefinition parameterDefinition = new CommandParameterDefinition.Builder()
                .setIdentifier("param")
                .setLabel("Parameter")
                .setType(CommandParameterType.DYNAMIC_ENUMERATION)
                .build();
        final CommandParameterEnumValue enumValue =
            new CommandParameterEnumValue("value", "Value Label");
        IDynamicParameterValues dynamicValues = new IDynamicParameterValues() {
            public CommandParameterEnumValue[] getEnumerationValues() {
                return new CommandParameterEnumValue[] { enumValue };
            }
        };
        CommandContribution c = new CommandContribution.Builder()
                .setIdentifier("id")
                .setLabel("label")
                .setCommandImplementation(_dummyImplementation)
                .addParameter(parameterDefinition, dynamicValues)
                .build();
        assertEquals(1, c.getDescription().getParameters().length);
        assertArrayEquals(new CommandParameterEnumValue[] { enumValue },
                c.getDynamicEnumerationValues("param"));
    }
}
