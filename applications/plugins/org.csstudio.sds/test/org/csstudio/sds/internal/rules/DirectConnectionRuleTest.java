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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test case for class {@link DirectConnectionRule}.
 *
 * @author Alexander Will
 * @version $Revision: 1.1 $
 *
 */
public final class DirectConnectionRuleTest {

    /**
     * Test for {@link DirectConnectionRule#evaluate(Object[])}.
     */
    @Test
    public void testEvaluate() {
        DirectConnectionRule rule = new DirectConnectionRule();

        double testObject1 = 20.0;
        String testObject2 = "Test Object 2"; //$NON-NLS-1$
        Object testObject3 = new Object();

        assertEquals(testObject1, rule.evaluate(new Object[] { testObject1,
                testObject2, testObject3 }));
        assertEquals(testObject2, rule.evaluate(new Object[] { testObject2,
                testObject1, testObject3 }));
        assertEquals(testObject3, rule.evaluate(new Object[] { testObject3,
                testObject2, testObject1 }));

        assertEquals(0, rule.evaluate(null));
    }

}
