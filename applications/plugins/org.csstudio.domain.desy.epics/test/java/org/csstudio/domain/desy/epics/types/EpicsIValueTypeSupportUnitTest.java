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
package org.csstudio.domain.desy.epics.types;

import java.util.List;

import org.csstudio.domain.desy.types.ITimedCssAlarmValueType;
import org.csstudio.domain.desy.types.TypeSupportException;
import org.csstudio.platform.data.IEnumeratedValue;
import org.csstudio.platform.data.TimestampFactory;
import org.csstudio.platform.data.ValueFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Epics type support test class.
 *
 * @author bknerr
 * @since 15.12.2010
 */
public class EpicsIValueTypeSupportUnitTest {

    @Before
    public void setup() {
        EpicsIValueTypeSupport.install();
    }


    @Test
    public void testIValue2CssValueConversionReturnsNull() {
        try {
            final ITimedCssAlarmValueType<List<Double>> cssV =
                EpicsIValueTypeSupport.toCssType(ValueFactory.createDoubleValue(TimestampFactory.now(),
                                                                          ValueFactory.createMinorSeverity(),
                                                                          "HIHI",
                                                                          null,
                                                                          null,
                                                                          null));
            Assert.assertNull(cssV);
        } catch (final TypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testDoubleValue2CssValueConversion() {
        try {
            final ITimedCssAlarmValueType<List<Double>> cssV =
                EpicsIValueTypeSupport.toCssType(ValueFactory.createDoubleValue(TimestampFactory.now(),
                                                                          ValueFactory.createMinorSeverity(),
                                                                          "HIHI",
                                                                          null,
                                                                          null,
                                                                          new double[]{1.0, 2.0}));
            Assert.assertNotNull(cssV);
            Assert.assertEquals(2, cssV.getValueData().size());
            Assert.assertEquals(Double.valueOf(1.0), cssV.getValueData().get(0));
            Assert.assertEquals(Double.valueOf(2.0), cssV.getValueData().get(1));
        } catch (final TypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testLongValue2CssValueConversion() {
        try {
            final ITimedCssAlarmValueType<List<Long>> cssV =
                EpicsIValueTypeSupport.toCssType(ValueFactory.createLongValue(TimestampFactory.now(),
                                                                        ValueFactory.createMinorSeverity(),
                                                                        "HIHI",
                                                                        null,
                                                                        null,
                                                                        new long[]{1L, 2L}));
            Assert.assertNotNull(cssV);
            Assert.assertEquals(2, cssV.getValueData().size());
            Assert.assertEquals(Long.valueOf(1L), cssV.getValueData().get(0));
            Assert.assertEquals(Long.valueOf(2L), cssV.getValueData().get(1));
        } catch (final TypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testEnumValue2CssValueConversion() {
        try {
            final IEnumeratedValue eVal = ValueFactory.createEnumeratedValue(TimestampFactory.now(),
                                                                             ValueFactory.createMinorSeverity(),
                                                                             "HIHI",
                                                                             ValueFactory.createEnumeratedMetaData(new String[] {"radio", "soulwax", "part", "3"}),
                                                                             null,
                                                                             new int[]{2});

            final ITimedCssAlarmValueType<EpicsEnumTriple> cssV = EpicsIValueTypeSupport.toCssType(eVal);
            Assert.assertNotNull(cssV);
            Assert.assertEquals(Integer.valueOf(2), cssV.getValueData().getIndex());
            Assert.assertEquals("part", cssV.getValueData().getState());
            Assert.assertEquals(null, cssV.getValueData().getRaw());
        } catch (final TypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testStringValue2CssValueConversion() {
        try {
            final ITimedCssAlarmValueType<List<String>> cssV =
                EpicsIValueTypeSupport.toCssType(ValueFactory.createStringValue(TimestampFactory.now(),
                                                                          ValueFactory.createMinorSeverity(),
                                                                          "HIHI",
                                                                          null,
                                                                          new String[]{"small", "black"}));
            Assert.assertNotNull(cssV);
            Assert.assertEquals(2, cssV.getValueData().size());
            Assert.assertEquals("small", cssV.getValueData().get(0));
            Assert.assertEquals("black", cssV.getValueData().get(1));
        } catch (final TypeSupportException e) {
            Assert.fail();
        }
    }
}
