/*
 * Copyright (c) 2011 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.domain.desy.calc;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for the average.
 *
 * @author bknerr
 * @since 18.02.2011
 */
public class AverageWithExponentialDecayCacheUnitTest {

    @Test
    public void testDecay0() {
        final AverageWithExponentialDecayCache cache = new AverageWithExponentialDecayCache(0.0);
        Assert.assertEquals(0, cache.getNumberOfAccumulations());
        Assert.assertTrue(Double.valueOf(0.0).equals(cache.getDecay()));
        Assert.assertNull(cache.getValue());

        cache.accumulate(1.0);
        Assert.assertEquals(1, cache.getNumberOfAccumulations());
        Assert.assertTrue(Double.valueOf(1.0).equals(cache.getValue()));

        cache.accumulate(2.0);
        Assert.assertEquals(2, cache.getNumberOfAccumulations());
        Assert.assertTrue(Double.valueOf(2.0).equals(cache.getValue()));

        cache.clear();
        Assert.assertEquals(0, cache.getNumberOfAccumulations());
        Assert.assertTrue(Double.valueOf(0.0).equals(cache.getDecay()));
        Assert.assertNull(cache.getValue());
    }

    @Test
    public void testDecay1() {
        final AverageWithExponentialDecayCache cache = new AverageWithExponentialDecayCache(1.0);
        Assert.assertEquals(0, cache.getNumberOfAccumulations());
        Assert.assertTrue(Double.valueOf(1.0).equals(cache.getDecay()));
        Assert.assertNull(cache.getValue());

        cache.accumulate(1.0);
        Assert.assertEquals(1, cache.getNumberOfAccumulations());
        Assert.assertTrue(Double.valueOf(1.0).equals(cache.getValue()));

        cache.accumulate(2.0);
        Assert.assertEquals(2, cache.getNumberOfAccumulations());
        Assert.assertTrue(Double.valueOf(1.0).equals(cache.getValue()));

        cache.clear();
        Assert.assertEquals(0, cache.getNumberOfAccumulations());
        Assert.assertTrue(Double.valueOf(1.0).equals(cache.getDecay()));
        Assert.assertNull(cache.getValue());

    }
}
