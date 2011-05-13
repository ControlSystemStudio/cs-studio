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

import org.csstudio.data.values.IEnumeratedValue;
import org.csstudio.data.values.TimestampFactory;
import org.csstudio.data.values.ValueFactory;
import org.csstudio.domain.desy.epics.typesupport.EpicsIMetaDataTypeSupport;
import org.csstudio.domain.desy.epics.typesupport.EpicsIValueTypeSupport;
import org.csstudio.domain.desy.typesupport.TypeSupportException;
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
        EpicsIMetaDataTypeSupport.install();
    }


    @Test(expected=TypeSupportException.class)
    public void testIValue2SystemVariableConversionReturnsNull() throws TypeSupportException {
        @SuppressWarnings({ "unchecked", "unused" })
        final EpicsSystemVariable<List<Double>> cssV =
            (EpicsSystemVariable<List<Double>>) EpicsIValueTypeSupport.toSystemVariable("foo",
                                                                                        ValueFactory.createDoubleValue(TimestampFactory.now(),
                                                                                                                       ValueFactory.createMinorSeverity(),
                                                                                                                       "HIHI",
                                                                                                                       null,
                                                                                                                       null,
                                                                                                                       null));
    }

    @Test
    public void testDoubleValue2SystemVariableConversion() {
        try {
            @SuppressWarnings("unchecked")
            final EpicsSystemVariable<List<Double>> cssV =
                (EpicsSystemVariable<List<Double>>) EpicsIValueTypeSupport.toSystemVariable("foo",
                                                    ValueFactory.createDoubleValue(TimestampFactory.now(),
                                                                      ValueFactory.createMinorSeverity(),
                                                                      "HIHI",
                                                                      null,
                                                                      null,
                                                                      new double[]{1.0, 2.0}));
            Assert.assertNotNull(cssV);
            Assert.assertEquals(2, cssV.getData().size());
            Assert.assertEquals(Double.valueOf(1.0), cssV.getData().get(0));
            Assert.assertEquals(Double.valueOf(2.0), cssV.getData().get(1));
        } catch (final TypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testLongValue2SystemVariableConversion() {
        try {
            @SuppressWarnings("unchecked")
            final EpicsSystemVariable<List<Long>> cssV =
                (EpicsSystemVariable<List<Long>>) EpicsIValueTypeSupport.toSystemVariable("foo",
                                                    ValueFactory.createLongValue(TimestampFactory.now(),
                                                                    ValueFactory.createMinorSeverity(),
                                                                    "HIHI",
                                                                    null,
                                                                    null,
                                                                    new long[]{1L, 2L}));
            Assert.assertNotNull(cssV);
            Assert.assertEquals(2, cssV.getData().size());
            Assert.assertEquals(Long.valueOf(1L), cssV.getData().get(0));
            Assert.assertEquals(Long.valueOf(2L), cssV.getData().get(1));
        } catch (final TypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testEnumValue2SystemVariableConversion() {
        try {
            final IEnumeratedValue eVal = ValueFactory.createEnumeratedValue(TimestampFactory.now(),
                                                                             ValueFactory.createMinorSeverity(),
                                                                             "HIHI",
                                                                             ValueFactory.createEnumeratedMetaData(new String[] {"radio", "soulwax", "part", "3"}),
                                                                             null,
                                                                             new int[]{2});

            @SuppressWarnings("unchecked")
            final EpicsSystemVariable<EpicsEnum> cssV = 
                (EpicsSystemVariable<EpicsEnum>) EpicsIValueTypeSupport.toSystemVariable("foo", eVal);
            Assert.assertNotNull(cssV);
            Assert.assertEquals("part", cssV.getData().getState());
            Assert.assertEquals(Integer.valueOf(2), cssV.getData().getStateIndex());
        } catch (final TypeSupportException e) {
            Assert.fail();
        }
    }

    @Test
    public void testStringValue2SystemVariableConversion() {
        try {
            @SuppressWarnings("unchecked")
            final EpicsSystemVariable<List<String>> sysVar =
                (EpicsSystemVariable<List<String>>) EpicsIValueTypeSupport.toSystemVariable("foo",
                                                    ValueFactory.createStringValue(TimestampFactory.now(),
                                                                      ValueFactory.createMinorSeverity(),
                                                                      "HIHI",
                                                                      null,
                                                                      new String[]{"small", "black"}));
            final List<String> cssV = sysVar.getData();
            Assert.assertNotNull(cssV);
            Assert.assertEquals(2, cssV.size());
            Assert.assertEquals("small", cssV.get(0));
            Assert.assertEquals("black", cssV.get(1));
        } catch (final TypeSupportException e) {
            Assert.fail();
        }
    }
}
