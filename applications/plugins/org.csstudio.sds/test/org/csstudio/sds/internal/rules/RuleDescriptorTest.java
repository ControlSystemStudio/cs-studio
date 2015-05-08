/*
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton,
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
package org.csstudio.sds.internal.rules;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.csstudio.sds.model.IRule;
import org.csstudio.sds.model.PropertyTypesEnum;
import org.junit.Test;

/**
 * Test case for class {@link org.csstudio.sds.internal.rules.RuleDescriptor}.
 *
 * @author Alexander Will
 * @version $Revision: 1.3 $
 *
 */
public final class RuleDescriptorTest {
    /**
     * Test method for {@link org.csstudio.sds.internal.rules.RuleDescriptor}.
     */
    @Test
    public void testRuleDescriptor() {
        String ruleId = "ruleId"; //$NON-NLS-1$
        String description = "description"; //$NON-NLS-1$
        String[] parameterDescriptions = new String[] { "param1", "param2" }; //$NON-NLS-1$ //$NON-NLS-2$
        IRule rule = new DirectConnectionRule();
        RuleDescriptor rd = new RuleDescriptor(ruleId, description,
                parameterDescriptions, new PropertyTypesEnum[] {PropertyTypesEnum.DOUBLE}, rule, true);

        assertEquals(ruleId, rd.getRuleId());
        assertEquals(description, rd.getDescription());
        assertArrayEquals(parameterDescriptions, rd.getParameterDescriptions());
        assertEquals(rule, rd.getRule());
        assertTrue(rd.isScriptedRule());

        assertNotNull(rd.toString());
    }

}
