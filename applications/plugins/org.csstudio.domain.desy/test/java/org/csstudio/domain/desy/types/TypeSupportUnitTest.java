/*
 * Copyright (c) 2010 Stiftung Deutsches Elektronen-Synchrotron,
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
package org.csstudio.domain.desy.types;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * Test, what else.
 *
 * @author bknerr
 * @since 10.12.2010
 */
public class TypeSupportUnitTest {

    @Before
    public void setup() {
        AbstractArchiveTypeConversionSupport.install();
    }

    @Test
    public void testNumberArchiveStringConversion() {

        try {
            final Double d = Double.valueOf(1.01010101010101010100101010000010010);
            final String sd = d.toString();
            final String archiveString = TypeSupport.toArchiveString(d);
            Assert.assertTrue(archiveString.equals(sd));
            final Double dFromA = TypeSupport.fromScalarArchiveString(Double.class, archiveString);
            Assert.assertTrue(dFromA.equals(d));
        } catch (final ConversionTypeSupportException e) {
            Assert.fail();
        }

        try {
            final Integer i = Integer.valueOf(-1234567);
            final String si = i.toString();
            final String archiveString = TypeSupport.toArchiveString(i);
            Assert.assertTrue(archiveString.equals(si));
            final Integer iFromA = TypeSupport.fromScalarArchiveString(Integer.class, archiveString);
            Assert.assertTrue(iFromA.equals(i));
        } catch (final ConversionTypeSupportException e) {
            Assert.fail();
        }

        // TODO (bknerr) for all number types...
    }

    @Test
    public void testIterableArchiveStringConversion() {
        try {
            final List<Integer> is = Lists.newArrayList(1,2,3,4);
            final String ss = Joiner.on(",").join(is);
            final String archiveString = TypeSupport.toArchiveString(is);
            Assert.assertTrue(archiveString.equals(ss));
            final Iterable<?> isFromA = TypeSupport.fromScalarArchiveString(Iterable.class, archiveString);


        } catch (final ConversionTypeSupportException e) {
            Assert.fail();
        }

    }
}
