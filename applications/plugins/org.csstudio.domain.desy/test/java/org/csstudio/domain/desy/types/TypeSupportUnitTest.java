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

import org.csstudio.domain.desy.time.TimeInstant.TimeInstantBuilder;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.data.ValueFactory;
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
public class TypeSupportUnitTest {

    @Before
    public void setup() {
        AbstractArchiveTypeConversionSupport.install();
        AbstractIValueConversionTypeSupport.install();
    }

    @Test
    public void testScalarDoubleArchiveStringConversion() {

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

    }

    @Test
    public void testScalarIntegerArchiveStringConversion() {
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

    }

    @Test
    public void testScalarByteArchiveStringConversion() {
        try {
            final Byte b = Byte.valueOf((byte) -128);
            final String sb = b.toString();
            final String archiveString = TypeSupport.toArchiveString(b);
            Assert.assertTrue(archiveString.equals(sb));
            final Byte bFromA = TypeSupport.fromScalarArchiveString(Byte.class, archiveString);
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
            final String archiveString = TypeSupport.toArchiveString(f);
            Assert.assertTrue(archiveString.equals(sf));
            final Float fFromA = TypeSupport.fromScalarArchiveString(Float.class, archiveString);
            Assert.assertTrue(fFromA.equals(f));
        } catch (final ConversionTypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testScalarStringArchiveStringConversion() {
        // TODO (bknerr) for all number types...

        try {
            final String archiveString = TypeSupport.toArchiveString("test me");
            Assert.assertTrue(archiveString.equals("test me"));
            final String sFromA = TypeSupport.fromScalarArchiveString(String.class, archiveString);
            Assert.assertTrue(sFromA.equals(archiveString));
        } catch (final ConversionTypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testMultiScalarEmptyConversion() {

        final List<String> valuesEmpty = Lists.newArrayList();
        try {
            final String archiveString = TypeSupport.toArchiveString(valuesEmpty);
            Assert.assertEquals("", archiveString);

            TypeSupport.fromMultiScalarArchiveString(IDoNotExist.class, "Iwasborninafactory,,,,whohoo");
        } catch (final Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testMultiScalarMisMatchConversion() {
        try {
            TypeSupport.fromMultiScalarArchiveString(Integer.class, "theshapeofpunk,tocome");
        } catch (final ConversionTypeSupportException e) {
            Assert.assertTrue(true);
        }

    }

    @Test
    public void testMultiScalarStringConversion() {
        final Collection<String> valuesS = Lists.newArrayList("modest", "mouse");
        try {
            final String archiveString = TypeSupport.toArchiveString(valuesS);
            Assert.assertEquals("modest\\,mouse", archiveString);
        } catch (final ConversionTypeSupportException e) {
            Assert.fail();
        }

    }

    @Test
    public void testMultiScalarIntegerConversion() {
        final Collection<Integer> valuesI = Lists.newArrayList(1,2,3,4);
        try {
            final String archiveString = TypeSupport.toArchiveString(valuesI);
            Assert.assertEquals("1\\,2\\,3\\,4", archiveString);
        } catch (final ConversionTypeSupportException e) {
            Assert.fail();
        }

    }

    @Test
    public void testMultiScalarDoubleConversion() {
        final Collection<Double> valuesD = Lists.newArrayList(1.0,2.0);
        try {
            final String archiveString = TypeSupport.toArchiveString(valuesD);
            Assert.assertEquals("1.0\\,2.0", archiveString);
        } catch (final ConversionTypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testMultiScalarFloatConversion() {
        final Collection<Float> valuesF = Lists.newArrayList(1.0F, 2.0F);
        try {
            final String archiveString = TypeSupport.toArchiveString(valuesF);
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
            final String archiveString = TypeSupport.toArchiveString(valuesB);
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
            TypeSupport.toArchiveString(new IDoNotExist());
        } catch (final ConversionTypeSupportException e) {
            Assert.fail("Unexpected exception");
        }
    }

    @Test
    public void testIValue2CssValueConversionReturnsNull() {
        try {
            final ICssAlarmValueType<List<Double>> cssV =
                TypeSupport.toCssType(ValueFactory.createDoubleValue(TimestampFactory.now(),
                                                                     ValueFactory.createMinorSeverity(),
                                                                     "HIHI",
                                                                     null,
                                                                     null,
                                                                     null),
                                                                     null,
                                                                     TimeInstantBuilder.buildFromNow());
            Assert.assertNull(cssV);
        } catch (final ConversionTypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testDoubleValue2CssValueConversion() {
        try {
            final ICssAlarmValueType<List<Double>> cssV =
                TypeSupport.toCssType(ValueFactory.createDoubleValue(TimestampFactory.now(),
                                                                     ValueFactory.createMinorSeverity(),
                                                                     "HIHI",
                                                                     null,
                                                                     null,
                                                                     new double[]{1.0, 2.0}),
                                                                     null,
                                                                     TimeInstantBuilder.buildFromNow());
            Assert.assertEquals(2, cssV.getValueData().size());
            Assert.assertEquals(Double.valueOf(1.0), cssV.getValueData().get(0));
            Assert.assertEquals(Double.valueOf(2.0), cssV.getValueData().get(1));
        } catch (final ConversionTypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testLongValue2CssValueConversion() {
        try {
            final ICssAlarmValueType<List<Long>> cssV =
                TypeSupport.toCssType(ValueFactory.createLongValue(TimestampFactory.now(),
                                                                   ValueFactory.createMinorSeverity(),
                                                                   "HIHI",
                                                                   null,
                                                                   null,
                                                                   new long[]{1L, 2L}),
                                                                   null,
                                                                   TimeInstantBuilder.buildFromNow());
            Assert.assertEquals(2, cssV.getValueData().size());
            Assert.assertEquals(Long.valueOf(1L), cssV.getValueData().get(0));
            Assert.assertEquals(Long.valueOf(2L), cssV.getValueData().get(1));
        } catch (final ConversionTypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testEnumValue2CssValueConversion() {
        try {
            final ICssAlarmValueType<List<Integer>> cssV =
                TypeSupport.toCssType(ValueFactory.createEnumeratedValue(TimestampFactory.now(),
                                                                         ValueFactory.createMinorSeverity(),
                                                                         "HIHI",
                                                                         null,
                                                                         null,
                                                                         new int[]{1, 2}),
                                                                         null,
                                                                         TimeInstantBuilder.buildFromNow());
            Assert.assertEquals(2, cssV.getValueData().size());
            Assert.assertEquals(Integer.valueOf(1), cssV.getValueData().get(0));
            Assert.assertEquals(Integer.valueOf(2), cssV.getValueData().get(1));
        } catch (final ConversionTypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testStringValue2CssValueConversion() {
        try {
            final ICssAlarmValueType<List<String>> cssV =
                TypeSupport.toCssType(ValueFactory.createStringValue(TimestampFactory.now(),
                                                                     ValueFactory.createMinorSeverity(),
                                                                     "HIHI",
                                                                     null,
                                                                     new String[]{"small", "black"}),
                                                                     null,
                                                                         TimeInstantBuilder.buildFromNow());
            Assert.assertEquals(2, cssV.getValueData().size());
            Assert.assertEquals("small", cssV.getValueData().get(0));
            Assert.assertEquals("black", cssV.getValueData().get(1));
        } catch (final ConversionTypeSupportException e) {
            Assert.fail();
        }
    }
}
