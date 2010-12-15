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

import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * Test, what else.
 *
 * @author bknerr
 * @since 10.12.2010
 */
public class DesyDomainTypeSupportUnitTest {

    @Before
    public void setup() {
        AbstractArchiveTypeConversionSupport.install();
    }

    @Test
    public void testScalarDoubleArchiveStringConversion() {

        try {
            final Double d = Double.valueOf(1.01010101010101010100101010000010010);
            final String sd = d.toString();
            final String archiveString = DesyDomainTypeSupport.toArchiveString(d);
            Assert.assertTrue(archiveString.equals(sd));
            final Double dFromA = DesyDomainTypeSupport.fromScalarArchiveString(Double.class, archiveString);
            Assert.assertTrue(dFromA.equals(d));
        } catch (final ConversionTypeSupportException e) {
            Assert.fail();
        }

    }

    @Test
    public void testScalarIntegerArchiveStringConversion() {
        try {
            final Integer i = Integer.valueOf(-1234567);
            final String si = i.toString();
            final String archiveString = DesyDomainTypeSupport.toArchiveString(i);
            Assert.assertTrue(archiveString.equals(si));
            final Integer iFromA = DesyDomainTypeSupport.fromScalarArchiveString(Integer.class, archiveString);
            Assert.assertTrue(iFromA.equals(i));
        } catch (final ConversionTypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testScalarByteArchiveStringConversion() {
        try {
            final Byte b = Byte.valueOf((byte) -128);
            final String sb = b.toString();
            final String archiveString = DesyDomainTypeSupport.toArchiveString(b);
            Assert.assertTrue(archiveString.equals(sb));
            final Byte bFromA = DesyDomainTypeSupport.fromScalarArchiveString(Byte.class, archiveString);
            Assert.assertTrue(bFromA.equals(b));
        } catch (final ConversionTypeSupportException e) {
            Assert.fail();
        }

    }

    @Test
    public void testScalarFloatArchiveStringConversion() {
        try {
            final Float f = Float.valueOf(44.44F);
            final String sf = f.toString();
            final String archiveString = DesyDomainTypeSupport.toArchiveString(f);
            Assert.assertTrue(archiveString.equals(sf));
            final Float fFromA = DesyDomainTypeSupport.fromScalarArchiveString(Float.class, archiveString);
            Assert.assertTrue(fFromA.equals(f));
        } catch (final ConversionTypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testScalarStringArchiveStringConversion() {
        // TODO (bknerr) for all number types...

        try {
            final String archiveString = DesyDomainTypeSupport.toArchiveString("test me");
            Assert.assertTrue(archiveString.equals("test me"));
            final String sFromA = DesyDomainTypeSupport.fromScalarArchiveString(String.class, archiveString);
            Assert.assertTrue(sFromA.equals(archiveString));
        } catch (final ConversionTypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testMultiScalarEmptyConversion() {

        final List<String> valuesEmpty = Lists.newArrayList();
        try {
            final String archiveString = DesyDomainTypeSupport.toArchiveString(valuesEmpty);
            Assert.assertEquals("", archiveString);

            DesyDomainTypeSupport.fromMultiScalarArchiveString(IDoNotExist.class, "Iwasborninafactory,,,,whohoo");
        } catch (final Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testMultiScalarMisMatchConversion() {
        try {
            DesyDomainTypeSupport.fromMultiScalarArchiveString(Integer.class, "theshapeofpunk,tocome");
        } catch (final ConversionTypeSupportException e) {
            Assert.assertTrue(true);
        }

    }

    @Test
    public void testMultiScalarStringConversion() {
        final Collection<String> valuesS = Lists.newArrayList("modest", "mouse");
        try {
            final String archiveString = DesyDomainTypeSupport.toArchiveString(valuesS);
            Assert.assertEquals("modest\\,mouse", archiveString);
        } catch (final ConversionTypeSupportException e) {
            Assert.fail();
        }

    }

    @Test
    public void testMultiScalarIntegerConversion() {
        final Collection<Integer> valuesI = Lists.newArrayList(1,2,3,4);
        try {
            final String archiveString = DesyDomainTypeSupport.toArchiveString(valuesI);
            Assert.assertEquals("1\\,2\\,3\\,4", archiveString);
        } catch (final ConversionTypeSupportException e) {
            Assert.fail();
        }

    }

    @Test
    public void testMultiScalarDoubleConversion() {
        final Collection<Double> valuesD = Lists.newArrayList(1.0,2.0);
        try {
            final String archiveString = DesyDomainTypeSupport.toArchiveString(valuesD);
            Assert.assertEquals("1.0\\,2.0", archiveString);
        } catch (final ConversionTypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testMultiScalarFloatConversion() {
        final Collection<Float> valuesF = Lists.newArrayList(1.0F, 2.0F);
        try {
            final String archiveString = DesyDomainTypeSupport.toArchiveString(valuesF);
            Assert.assertEquals("1.0\\,2.0", archiveString);
        } catch (final ConversionTypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testMultiScalarByteConversion() {
        final Collection<Byte> valuesB = Lists.newArrayList(Byte.valueOf("127"),
                                                            Byte.valueOf("-128"));
        try {
            final String archiveString = DesyDomainTypeSupport.toArchiveString(valuesB);
            Assert.assertEquals("127\\,-128", archiveString);
        } catch (final ConversionTypeSupportException e) {
            Assert.fail();
        }
    }

    /**
     * For test purposes.
     *
     * @author bknerr
     * @since 13.12.2010
     */
    private static final class IDoNotExist {
        public IDoNotExist() {
            // EMPTY
        }
    }
    @Test(expected=RuntimeException.class)
    public void testTypeNotSuppportedException() {
        try {
            DesyDomainTypeSupport.toArchiveString(new IDoNotExist());
        } catch (final ConversionTypeSupportException e) {
            Assert.fail("Unexpected exception");
        }
    }
}
