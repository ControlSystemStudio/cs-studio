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
package org.csstudio.domain.common.collection;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * Unit test for {@link CollectionsUtil}.
 *
 * @author bknerr
 * @since 17.08.2011
 */
public class CollectionsUtilUnitTest {

    @Test
    public void testEmptiesToLimitLengthString() {
        String result = CollectionsUtil.toLimitLengthString(Lists.newArrayList(), -1);
        Assert.assertEquals("[]", result);

        result = CollectionsUtil.toLimitLengthString(Lists.newArrayList(), 0);
        Assert.assertEquals("[]", result);

        result = CollectionsUtil.toLimitLengthString(Lists.newArrayList(), 1);
        Assert.assertEquals("[]", result);

        result = CollectionsUtil.toLimitLengthString(Lists.newArrayList(), 10);
        Assert.assertEquals("[]", result);
    }

    @Test
    public void testFilledToLimitLengthString() {
        String result = CollectionsUtil.<Double>toLimitLengthString(Lists.newArrayList(1.0, 2.0), -1);
        Assert.assertEquals("[1.0, 2.0]", result);

        result = CollectionsUtil.<Double>toLimitLengthString(Lists.newArrayList(1.0, 2.0), 0);
        Assert.assertEquals("[]", result);

        result = CollectionsUtil.<Double>toLimitLengthString(Lists.newArrayList(1.0, 2.0), 1);
        Assert.assertEquals("[1.0,...]", result);

        result = CollectionsUtil.<Double>toLimitLengthString(Lists.newArrayList(1.0, 2.0), 10);
        Assert.assertEquals("[1.0, 2.0]", result);
    }

    @Test
    public void testCollectionInCollectionToLimitLengthString() {
        final List<Double> list1 = Lists.newArrayList(1.0, 2.0);
        final List<Double> list2 = Lists.newArrayList(-1.0, -2.0);

        String result = CollectionsUtil.toLimitLengthString(Lists.<Object>newArrayList(list1, list2), -1);
        Assert.assertEquals("[[1.0, 2.0], [-1.0, -2.0]]", result);

        result = CollectionsUtil.toLimitLengthString(Lists.<Object>newArrayList(list1, list2), 0);
        Assert.assertEquals("[]", result);

        result = CollectionsUtil.toLimitLengthString(Lists.<Object>newArrayList(list1, list2), 1);
        Assert.assertEquals("[[1.0, 2.0],...]", result);

        result = CollectionsUtil.toLimitLengthString(Lists.<Object>newArrayList(list1, list2), 10);
        Assert.assertEquals("[[1.0, 2.0], [-1.0, -2.0]]", result);


    }
}
