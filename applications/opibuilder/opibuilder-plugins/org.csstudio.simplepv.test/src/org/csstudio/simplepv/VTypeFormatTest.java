package org.csstudio.simplepv;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import static org.hamcrest.CoreMatchers.*;

import org.diirt.util.text.NumberFormats;
import org.diirt.vtype.Display;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;
import org.junit.Test;


public class VTypeFormatTest {

    Display displayFiveDp = ValueFactory.newDisplay(Double.NaN, Double.NaN,
            Double.NaN, "%", NumberFormats.format(5), Double.NaN, Double.NaN,
            Double.NaN, Double.NaN, Double.NaN);

    VType longValue = ValueFactory.newVLong(Long.valueOf(4294906129L), ValueFactory.alarmNone(),
            ValueFactory.timeNow(), ValueFactory.displayNone());
    VType intValue = ValueFactory.newVInt(4003, ValueFactory.alarmNone(),
            ValueFactory.timeNow(), ValueFactory.displayNone());
    VType intForStringValue = ValueFactory.newVInt(97, ValueFactory.alarmNone(),
            ValueFactory.timeNow(), ValueFactory.displayNone());
    VType smallDoubleValue =  ValueFactory.newVDouble(1.234567e-7);
    VType smallDoubleValueFiveDp =  ValueFactory.newVDouble(1.234567e-7, displayFiveDp);
    VType doubleNaNValue =  ValueFactory.newVDouble(Double.NaN);
    VType doubleInfValue =  ValueFactory.newVDouble(Double.POSITIVE_INFINITY);
    VType smallNegativeDoubleValue =  ValueFactory.newVDouble(-1.234567e-7);
    VType bigDoubleValueNoFormat =  ValueFactory.newVDouble(6.54321e23);
    VType orderTenDoubleValueFiveDp =  ValueFactory.newVDouble(21.251235, displayFiveDp);
    VType sexaPositiveValue = ValueFactory.newVDouble(12.5824414);
    VType sexaNegativeValue = ValueFactory.newVDouble(-12.5824414);
    VType sexaRoundedValue = ValueFactory.newVDouble(12.9999999);

    @Test
    public void expFormatValueOfSmallDoubleWithPrecisionMinus1WithDisplayIsExpWithFormatDp() {
        assertThat(VTypeHelper.formatValue(FormatEnum.EXP, smallDoubleValueFiveDp, -1), is("1.23457E-7"));
    }

    @Test
    public void expFormatValueOfSmallDoubleWithPrecision2IsExpWith2dp() {
        assertThat(VTypeHelper.formatValue(FormatEnum.EXP, smallDoubleValue, 2), is("1.23E-7"));
    }

    @Test
    public void expFormatValueOfDoubleHasCharacteristicLtTen() {
        assertThat(VTypeHelper.formatValue(FormatEnum.EXP, orderTenDoubleValueFiveDp, 4), is("2.1251E1"));
    }

    @Test
    public void expFormatValueOfBigDoubleHasCorrectPositiveExponent() {
        assertThat(VTypeHelper.formatValue(FormatEnum.EXP, bigDoubleValueNoFormat, 2), is("6.54E23"));
    }

    @Test
    public void hexFormatValueOfIntegerIsUpperCaseHexWithPrefix() {
        assertThat(VTypeHelper.formatValue(FormatEnum.HEX, intValue, 1), is("0xFA3"));
    }

    @Test
    public void hexFormatValueOfLongIsUpperCaseHexWithPrefix() {
        assertThat(VTypeHelper.formatValue(FormatEnum.HEX, longValue, 1), is("0xFFFF1111"));
    }

    @Test
    public void hex64FormatValueOfLongIsLowerCaseHexWithPrefix() {
        assertThat(VTypeHelper.formatValue(FormatEnum.HEX64, longValue, 1), is("0xffff1111"));
    }

    @Test
    public void stringFormatValueOfIntegerIsCharacter() {
        assertThat(VTypeHelper.formatValue(FormatEnum.STRING, intForStringValue, 1), is("a"));
    }

    @Test
    public void engFormatValueOfSmallDoubleWithDefaultPrecisionHasCharacteristicLtThousand() {
        assertThat(VTypeHelper.formatValue(FormatEnum.ENG, smallDoubleValue, 4), is("123.4567E-9"));
    }

    @Test
    public void engFormatValueOfSmallNegativeDoubleWithPrecisionTwoHasTwoDP() {
        assertThat(VTypeHelper.formatValue(FormatEnum.ENG, smallNegativeDoubleValue, 2), is("-123.46E-9"));
    }

    @Test
    public void engFormatValueOfDoubleHasExponentPowerOfThreeSpreadOfExponents() {

        for (int i=-324; i < 308; i++) {
            VType value =  ValueFactory.newVDouble(Double.parseDouble(String.format("1.234567e%d", i)));
            String[] parts = VTypeHelper.formatValue(FormatEnum.ENG, value, 2).toUpperCase().split("E");
            try {
                int actualExponent = Integer.parseInt(parts[1]);
                assertThat(String.format("%d -> %sE%s", i, parts[0], parts[1]), actualExponent % 3, is(0));
            }
            catch (NumberFormatException ex) {
                fail(String.format("Non numeric exponent for %d", i));
            }
        }
    }
    @Test
    public void decFormatValueOfDoubleInftyisInfty() {
        assertThat(VTypeHelper.formatValue(FormatEnum.DECIMAL, doubleInfValue, 3), is("Infinity"));
    }

    @Test
    public void decFormatValueOfDoubleNaNisDoubleNaNForSetPrecision() {
        assertThat(VTypeHelper.formatValue(FormatEnum.DECIMAL, doubleNaNValue, 3), is("NaN"));
    }

    @Test
    public void decFormatValueOfDoubleNaNisDoubleNaNForUnsetPrecision() {
        assertThat(VTypeHelper.formatValue(FormatEnum.DECIMAL, doubleNaNValue, -1), is("NaN"));
    }

    @Test
    public void decFormatValueOfbigDoubleHasPrecisionDecimalPlaces() {
        assertThat(VTypeHelper.formatValue(FormatEnum.DECIMAL, bigDoubleValueNoFormat, 3), is("654321000000000000000000.000"));
    }

    @Test
    public void decFormatValueOfsmallDoubleHasPrecisionDecimalPlaces() {
        assertThat(VTypeHelper.formatValue(FormatEnum.DECIMAL, smallNegativeDoubleValue, 2), is("-0.00"));
    }

    @Test
    public void decFormatValueOfDoubleHasFullValueForUnsetPrecision() {
        assertThat(VTypeHelper.formatValue(FormatEnum.DECIMAL, orderTenDoubleValueFiveDp, -1), is("21.25124"));
    }

    @Test
    public void sexaFormatVaueOfPositiveValueIsExpWith7dp() {
        assertThat(VTypeHelper.formatValue(FormatEnum.SEXA, sexaPositiveValue, 7), is("12:34:56.789"));
    }

    @Test
    public void sexaFormatVaueOfNegativeValueIsExpWith7dp() {
        assertThat(VTypeHelper.formatValue(FormatEnum.SEXA, sexaNegativeValue, 7), is("-12:34:56.789"));
    }

    @Test
    public void sexaFormatVaueOfRoundedValueIsExpWith7dp() {
        assertThat(VTypeHelper.formatValue(FormatEnum.SEXA, sexaRoundedValue, 7), is("13:00:00.000"));
    }

    @Test
    public void sexaFormatVaueOfUnroundedValueIsExpWith7dp() {
        assertThat(VTypeHelper.formatValue(FormatEnum.SEXA, sexaRoundedValue, 8), is("12:59:59.9996"));
    }

    @Test
    public void sexaFormatVaueOfPositiveValueIsExpWith7dpHMS() {
        assertThat(VTypeHelper.formatValue(FormatEnum.SEXA_HMS, sexaPositiveValue, 7), is("48:03:40.989"));
    }

    @Test
    public void sexaFormatVaueOfNegativeValueIsExpWith7dpHMS() {
        assertThat(VTypeHelper.formatValue(FormatEnum.SEXA_HMS, sexaNegativeValue, 7), is("-48:03:40.989"));
    }

    @Test
    public void sexaFormatVaueOfRoundedValueIsExpWith7dpHMS() {
        assertThat(VTypeHelper.formatValue(FormatEnum.SEXA_HMS, sexaRoundedValue, 7), is("49:39:22.831"));
    }

    @Test
    public void sexaFormatVaueOfPositiveValueIsExpWith7dpDMS() {
        assertThat(VTypeHelper.formatValue(FormatEnum.SEXA_DMS, sexaPositiveValue, 7), is("720:55:14.837"));
    }

    @Test
    public void sexaFormatVaueOfNegativeValueIsExpWith7dpDMS() {
        assertThat(VTypeHelper.formatValue(FormatEnum.SEXA_DMS, sexaNegativeValue, 7), is("-720:55:14.837"));
    }

    @Test
    public void sexaFormatVaueOfRoundedValueIsExpWith7dpDMS() {
        assertThat(VTypeHelper.formatValue(FormatEnum.SEXA_DMS, sexaRoundedValue, 7), is("744:50:42.461"));
    }

}
