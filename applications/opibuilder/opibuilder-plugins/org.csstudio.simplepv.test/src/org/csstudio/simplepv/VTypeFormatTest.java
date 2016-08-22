package org.csstudio.simplepv;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import static org.hamcrest.CoreMatchers.*;

import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;
import org.junit.Test;


public class VTypeFormatTest {

    VType longValue = ValueFactory.newVLong(Long.valueOf(4294906129L), ValueFactory.alarmNone(),
            ValueFactory.timeNow(), ValueFactory.displayNone());
    VType intValue = ValueFactory.newVInt(4003, ValueFactory.alarmNone(),
            ValueFactory.timeNow(), ValueFactory.displayNone());
    VType intForStringValue = ValueFactory.newVInt(97, ValueFactory.alarmNone(),
            ValueFactory.timeNow(), ValueFactory.displayNone());
    VType smallDoubleValue =  ValueFactory.newVDouble(1.234567e-7);
    VType doubleNaNValue =  ValueFactory.newVDouble(Double.NaN);
    VType doubleInfValue =  ValueFactory.newVDouble(Double.POSITIVE_INFINITY);
    VType smallNegativeDoubleValue =  ValueFactory.newVDouble(-1.234567e-7);
    VType bigDoubleValue =  ValueFactory.newVDouble(6.54321e23);
    VType orderTenDoubleValue =  ValueFactory.newVDouble(21.251235);

    @Test
    public void expFormatValueOfSmallDoubleWithPrecisionMinus1IsExpWith4dp() {
        assertThat(VTypeHelper.formatValue(FormatEnum.EXP, smallDoubleValue, -1), is("1.2346E-7"));
    }

    @Test
    public void expFormatValueOfSmallDoubleWithPrecision2IsExpWith2dp() {
        assertThat(VTypeHelper.formatValue(FormatEnum.EXP, smallDoubleValue, 2), is("1.23E-7"));
    }

    @Test
    public void expFormatValueOfDoubleHasCharacteristicLtTen() {
        assertThat(VTypeHelper.formatValue(FormatEnum.EXP, orderTenDoubleValue, -1), is("2.1251E1"));
    }

    @Test
    public void expFormatValueOfBigDoubleHasCorrectPositiveExponent() {
        assertThat(VTypeHelper.formatValue(FormatEnum.EXP, bigDoubleValue, -1), is("6.5432E23"));
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
        assertThat(VTypeHelper.formatValue(FormatEnum.ENG, smallDoubleValue, -1), is("123.4567E-9"));
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
        assertThat(VTypeHelper.formatValue(FormatEnum.DECIMAL, bigDoubleValue, 3), is("654321000000000000000000.000"));
    }

    @Test
    public void decFormatValueOfsmallDoubleHasPrecisionDecimalPlaces() {
        assertThat(VTypeHelper.formatValue(FormatEnum.DECIMAL, smallNegativeDoubleValue, 2), is("-0.00"));
    }

    @Test
    public void decFormatValueOfDoubleHasFullValueForUnsetPrecision() {
        assertThat(VTypeHelper.formatValue(FormatEnum.DECIMAL, orderTenDoubleValue, -1), is("21.251235"));
    }
}
