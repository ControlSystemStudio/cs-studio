package org.csstudio.sds.model.optionEnums;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.csstudio.sds.model.TextTypeEnum;
import org.eclipse.swt.SWT;
import org.junit.Before;
import org.junit.Test;

public class TextTypeEnumTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void isValidFormatTest() {
        // Test Text
        assertTrue(TextTypeEnum.TEXT.isValidFormat(""));
        assertTrue(TextTypeEnum.TEXT.isValidFormat("1qayXSW,.-+#"));
        assertTrue(TextTypeEnum.TEXT.isValidFormat("qayXSW,.-+#2454"));
        assertTrue(TextTypeEnum.TEXT.isValidFormat("0"));
        assertTrue(TextTypeEnum.TEXT.isValidFormat("1234567890"));
        assertTrue(TextTypeEnum.TEXT.isValidFormat("12345.67890"));
        assertTrue(TextTypeEnum.TEXT.isValidFormat("-12345.67890"));
        assertTrue(TextTypeEnum.TEXT.isValidFormat("-12345.-67890"));
        assertTrue(TextTypeEnum.TEXT.isValidFormat("1234e123"));
        assertTrue(TextTypeEnum.TEXT.isValidFormat("-1234e-123"));
        assertTrue(TextTypeEnum.TEXT.isValidFormat("+1234e+123"));
        assertTrue(TextTypeEnum.TEXT.isValidFormat("+12.34e+123"));
        assertTrue(TextTypeEnum.TEXT.isValidFormat("+12.34e+12.3"));
        assertTrue(TextTypeEnum.TEXT.isValidFormat("-1234e-123"));
        assertTrue(TextTypeEnum.TEXT.isValidFormat("-12.34e-123"));
        assertTrue(TextTypeEnum.TEXT.isValidFormat("-12.34e-12.3"));
        assertTrue(TextTypeEnum.TEXT.isValidFormat("1AF"));
        assertTrue(TextTypeEnum.TEXT.isValidFormat("1af"));
        assertTrue(TextTypeEnum.TEXT.isValidFormat("af"));
        assertTrue(TextTypeEnum.TEXT.isValidFormat("AF"));
        assertTrue(TextTypeEnum.TEXT.isValidFormat("+1AF"));
        assertTrue(TextTypeEnum.TEXT.isValidFormat("+1af"));
        assertTrue(TextTypeEnum.TEXT.isValidFormat("+af"));
        assertTrue(TextTypeEnum.TEXT.isValidFormat("+AF"));
        assertTrue(TextTypeEnum.TEXT.isValidFormat("-1AF"));
        assertTrue(TextTypeEnum.TEXT.isValidFormat("-1af"));
        assertTrue(TextTypeEnum.TEXT.isValidFormat("-af"));
        assertTrue(TextTypeEnum.TEXT.isValidFormat("-AF"));

        //Text Alias
        assertTrue(TextTypeEnum.ALIAS.isValidFormat(""));
        assertTrue(TextTypeEnum.ALIAS.isValidFormat("1qayXSW,.-+#"));
        assertTrue(TextTypeEnum.ALIAS.isValidFormat("qayXSW,.-+#2454"));
        assertTrue(TextTypeEnum.ALIAS.isValidFormat("0"));
        assertTrue(TextTypeEnum.ALIAS.isValidFormat("1234567890"));
        assertTrue(TextTypeEnum.ALIAS.isValidFormat("12345.67890"));
        assertTrue(TextTypeEnum.ALIAS.isValidFormat("-12345.67890"));
        assertTrue(TextTypeEnum.ALIAS.isValidFormat("-12345.-67890"));
        assertTrue(TextTypeEnum.ALIAS.isValidFormat("1234e123"));
        assertTrue(TextTypeEnum.ALIAS.isValidFormat("-1234e-123"));
        assertTrue(TextTypeEnum.ALIAS.isValidFormat("+1234e+123"));
        assertTrue(TextTypeEnum.ALIAS.isValidFormat("+12.34e+123"));
        assertTrue(TextTypeEnum.ALIAS.isValidFormat("+12.34e+12.3"));
        assertTrue(TextTypeEnum.ALIAS.isValidFormat("-1234e-123"));
        assertTrue(TextTypeEnum.ALIAS.isValidFormat("-12.34e-123"));
        assertTrue(TextTypeEnum.ALIAS.isValidFormat("-12.34e-12.3"));
        assertTrue(TextTypeEnum.ALIAS.isValidFormat("1AF"));
        assertTrue(TextTypeEnum.ALIAS.isValidFormat("1af"));
        assertTrue(TextTypeEnum.ALIAS.isValidFormat("af"));
        assertTrue(TextTypeEnum.ALIAS.isValidFormat("AF"));
        assertTrue(TextTypeEnum.ALIAS.isValidFormat("+1AF"));
        assertTrue(TextTypeEnum.ALIAS.isValidFormat("+1af"));
        assertTrue(TextTypeEnum.ALIAS.isValidFormat("+af"));
        assertTrue(TextTypeEnum.ALIAS.isValidFormat("+AF"));
        assertTrue(TextTypeEnum.ALIAS.isValidFormat("-1AF"));
        assertTrue(TextTypeEnum.ALIAS.isValidFormat("-1af"));
        assertTrue(TextTypeEnum.ALIAS.isValidFormat("-af"));
        assertTrue(TextTypeEnum.ALIAS.isValidFormat("-AF"));



        // Test Double
     // --String
        assertFalse(TextTypeEnum.DOUBLE.isValidFormat(""));
        assertFalse(TextTypeEnum.DOUBLE.isValidFormat("1qayXSW,.-+#"));
        assertFalse(TextTypeEnum.DOUBLE.isValidFormat("qayXSW,.-+#2454"));
        // --Integer
        assertTrue(TextTypeEnum.DOUBLE.isValidFormat("0"));
        assertTrue(TextTypeEnum.DOUBLE.isValidFormat("1234567890"));
        assertTrue(TextTypeEnum.DOUBLE.isValidFormat("-1234567890"));

        // --Float
        assertTrue(TextTypeEnum.DOUBLE.isValidFormat("12345.67890"));
        assertTrue(TextTypeEnum.DOUBLE.isValidFormat("-12345.67890"));
        assertFalse(TextTypeEnum.DOUBLE.isValidFormat("-12345.-67890"));
        // --Exp
        assertTrue(TextTypeEnum.DOUBLE.isValidFormat("1234e123"));
        assertTrue(TextTypeEnum.DOUBLE.isValidFormat("-1234e123"));
        assertTrue(TextTypeEnum.DOUBLE.isValidFormat("+1234e+123"));
        assertTrue(TextTypeEnum.DOUBLE.isValidFormat("+12.34e+123"));
        assertFalse(TextTypeEnum.DOUBLE.isValidFormat("+12.34e+12.3"));
        assertTrue(TextTypeEnum.DOUBLE.isValidFormat("-1234e-123"));
        assertTrue(TextTypeEnum.DOUBLE.isValidFormat("-12.34e-123"));
        assertFalse(TextTypeEnum.DOUBLE.isValidFormat("-12.34e-12.3"));
        // --Hex
        assertFalse(TextTypeEnum.DOUBLE.isValidFormat("1AF"));
        assertFalse(TextTypeEnum.DOUBLE.isValidFormat("1af"));
        assertFalse(TextTypeEnum.DOUBLE.isValidFormat("af"));
        assertFalse(TextTypeEnum.DOUBLE.isValidFormat("AF"));
        assertFalse(TextTypeEnum.DOUBLE.isValidFormat("+1AF"));
        assertFalse(TextTypeEnum.DOUBLE.isValidFormat("+1af"));
        assertFalse(TextTypeEnum.DOUBLE.isValidFormat("+af"));
        assertFalse(TextTypeEnum.DOUBLE.isValidFormat("+AF"));
        assertFalse(TextTypeEnum.DOUBLE.isValidFormat("-1AF"));
        assertFalse(TextTypeEnum.DOUBLE.isValidFormat("-1af"));
        assertFalse(TextTypeEnum.DOUBLE.isValidFormat("-af"));
        assertFalse(TextTypeEnum.DOUBLE.isValidFormat("-AF"));

        assertTrue(TextTypeEnum.DOUBLE.isValidFormat("0x1AF"));
        assertTrue(TextTypeEnum.DOUBLE.isValidFormat("0x1af"));
        assertTrue(TextTypeEnum.DOUBLE.isValidFormat("0xaf"));
        assertTrue(TextTypeEnum.DOUBLE.isValidFormat("0xAF"));
        assertTrue(TextTypeEnum.DOUBLE.isValidFormat("0x13"));
        assertFalse(TextTypeEnum.DOUBLE.isValidFormat("+0x1AF"));
        assertFalse(TextTypeEnum.DOUBLE.isValidFormat("+0x1af"));
        assertFalse(TextTypeEnum.DOUBLE.isValidFormat("+0xaf"));
        assertFalse(TextTypeEnum.DOUBLE.isValidFormat("+0xAF"));
        assertFalse(TextTypeEnum.DOUBLE.isValidFormat("+0x13"));
        assertTrue(TextTypeEnum.DOUBLE.isValidFormat("-0x1AF"));
        assertTrue(TextTypeEnum.DOUBLE.isValidFormat("-0x1af"));
        assertTrue(TextTypeEnum.DOUBLE.isValidFormat("-0xaf"));
        assertTrue(TextTypeEnum.DOUBLE.isValidFormat("-0xAF"));
        assertTrue(TextTypeEnum.DOUBLE.isValidFormat("-0x16"));
        // Test Exp
     // --String
        assertFalse(TextTypeEnum.EXP.isValidFormat(""));
        assertFalse(TextTypeEnum.EXP.isValidFormat("1qayXSW,.-+#"));
        assertFalse(TextTypeEnum.EXP.isValidFormat("qayXSW,.-+#2454"));
        // --Integer
        assertTrue(TextTypeEnum.EXP.isValidFormat("0"));
        assertTrue(TextTypeEnum.EXP.isValidFormat("1234567890"));
        assertTrue(TextTypeEnum.EXP.isValidFormat("-1234567890"));

        // --Float
        assertTrue(TextTypeEnum.EXP.isValidFormat("12345.67890"));
        assertTrue(TextTypeEnum.EXP.isValidFormat("-12345.67890"));
        assertFalse(TextTypeEnum.EXP.isValidFormat("-12345.-67890"));
        // --Exp
        assertTrue(TextTypeEnum.EXP.isValidFormat("1234e123"));
        assertTrue(TextTypeEnum.EXP.isValidFormat("-1234e123"));
        assertTrue(TextTypeEnum.EXP.isValidFormat("+1234e+123"));
        assertTrue(TextTypeEnum.EXP.isValidFormat("+12.34e+123"));
        assertFalse(TextTypeEnum.EXP.isValidFormat("+12.34e+12.3"));
        assertTrue(TextTypeEnum.EXP.isValidFormat("-1234e-123"));
        assertTrue(TextTypeEnum.EXP.isValidFormat("-12.34e-123"));
        assertFalse(TextTypeEnum.EXP.isValidFormat("-12.34e-12.3"));
        // --Hex
        assertFalse(TextTypeEnum.EXP.isValidFormat("1AF"));
        assertFalse(TextTypeEnum.EXP.isValidFormat("1af"));
        assertFalse(TextTypeEnum.EXP.isValidFormat("af"));
        assertFalse(TextTypeEnum.EXP.isValidFormat("AF"));
        assertFalse(TextTypeEnum.EXP.isValidFormat("+1AF"));
        assertFalse(TextTypeEnum.EXP.isValidFormat("+1af"));
        assertFalse(TextTypeEnum.EXP.isValidFormat("+af"));
        assertFalse(TextTypeEnum.EXP.isValidFormat("+AF"));
        assertFalse(TextTypeEnum.EXP.isValidFormat("-1AF"));
        assertFalse(TextTypeEnum.EXP.isValidFormat("-1af"));
        assertFalse(TextTypeEnum.EXP.isValidFormat("-af"));
        assertFalse(TextTypeEnum.EXP.isValidFormat("-AF"));

        assertTrue(TextTypeEnum.EXP.isValidFormat("0x1AF"));
        assertTrue(TextTypeEnum.EXP.isValidFormat("0x1af"));
        assertTrue(TextTypeEnum.EXP.isValidFormat("0xaf"));
        assertTrue(TextTypeEnum.EXP.isValidFormat("0xAF"));
        assertTrue(TextTypeEnum.EXP.isValidFormat("0x13"));
        assertFalse(TextTypeEnum.EXP.isValidFormat("+0x1AF"));
        assertFalse(TextTypeEnum.EXP.isValidFormat("+0x1af"));
        assertFalse(TextTypeEnum.EXP.isValidFormat("+0xaf"));
        assertFalse(TextTypeEnum.EXP.isValidFormat("+0xAF"));
        assertFalse(TextTypeEnum.EXP.isValidFormat("+0x13"));
        assertTrue(TextTypeEnum.EXP.isValidFormat("-0x1AF"));
        assertTrue(TextTypeEnum.EXP.isValidFormat("-0x1af"));
        assertTrue(TextTypeEnum.EXP.isValidFormat("-0xaf"));
        assertTrue(TextTypeEnum.EXP.isValidFormat("-0xAF"));
        assertTrue(TextTypeEnum.EXP.isValidFormat("-0x16"));


        //Test Hex
        // --String
        assertFalse(TextTypeEnum.HEX.isValidFormat(""));
        assertFalse(TextTypeEnum.HEX.isValidFormat("1qayXSW,.-+#"));
        assertFalse(TextTypeEnum.HEX.isValidFormat("qayXSW,.-+#2454"));
        // --Integer
        assertTrue(TextTypeEnum.HEX.isValidFormat("0"));
        assertTrue(TextTypeEnum.HEX.isValidFormat("1234567890"));
        assertTrue(TextTypeEnum.HEX.isValidFormat("-1234567890"));

        // --Float
        assertTrue(TextTypeEnum.HEX.isValidFormat("12345.67890"));
        assertTrue(TextTypeEnum.HEX.isValidFormat("-12345.67890"));
        assertFalse(TextTypeEnum.HEX.isValidFormat("-12345.-67890"));
        // --Exp
        assertTrue(TextTypeEnum.HEX.isValidFormat("1234e123"));
        assertTrue(TextTypeEnum.HEX.isValidFormat("-1234e123"));
        assertTrue(TextTypeEnum.HEX.isValidFormat("+1234e+123"));
        assertTrue(TextTypeEnum.HEX.isValidFormat("+12.34e+123"));
        assertFalse(TextTypeEnum.HEX.isValidFormat("+12.34e+12.3"));
        assertTrue(TextTypeEnum.HEX.isValidFormat("-1234e-123"));
        assertTrue(TextTypeEnum.HEX.isValidFormat("-12.34e-123"));
        assertFalse(TextTypeEnum.HEX.isValidFormat("-12.34e-12.3"));
        // --Hex
        assertFalse(TextTypeEnum.HEX.isValidFormat("1AF"));
        assertFalse(TextTypeEnum.HEX.isValidFormat("1af"));
        assertFalse(TextTypeEnum.HEX.isValidFormat("af"));
        assertFalse(TextTypeEnum.HEX.isValidFormat("AF"));
        assertFalse(TextTypeEnum.HEX.isValidFormat("+1AF"));
        assertFalse(TextTypeEnum.HEX.isValidFormat("+1af"));
        assertFalse(TextTypeEnum.HEX.isValidFormat("+af"));
        assertFalse(TextTypeEnum.HEX.isValidFormat("+AF"));
        assertFalse(TextTypeEnum.HEX.isValidFormat("-1AF"));
        assertFalse(TextTypeEnum.HEX.isValidFormat("-1af"));
        assertFalse(TextTypeEnum.HEX.isValidFormat("-af"));
        assertFalse(TextTypeEnum.HEX.isValidFormat("-AF"));

        assertTrue(TextTypeEnum.HEX.isValidFormat("0x1AF"));
        assertTrue(TextTypeEnum.HEX.isValidFormat("0x1af"));
        assertTrue(TextTypeEnum.HEX.isValidFormat("0xaf"));
        assertTrue(TextTypeEnum.HEX.isValidFormat("0xAF"));
        assertTrue(TextTypeEnum.HEX.isValidFormat("0x13"));
        assertFalse(TextTypeEnum.HEX.isValidFormat("+0x1AF"));
        assertFalse(TextTypeEnum.HEX.isValidFormat("+0x1af"));
        assertFalse(TextTypeEnum.HEX.isValidFormat("+0xaf"));
        assertFalse(TextTypeEnum.HEX.isValidFormat("+0xAF"));
        assertFalse(TextTypeEnum.HEX.isValidFormat("+0x13"));
        assertTrue(TextTypeEnum.HEX.isValidFormat("-0x1AF"));
        assertTrue(TextTypeEnum.HEX.isValidFormat("-0x1af"));
        assertTrue(TextTypeEnum.HEX.isValidFormat("-0xaf"));
        assertTrue(TextTypeEnum.HEX.isValidFormat("-0xAF"));
        assertTrue(TextTypeEnum.HEX.isValidFormat("-0x16"));

    }

    @Test
    public void isValidCharsTest() {
        // test Text
        assertTrue(TextTypeEnum.TEXT.isValidChars('+', "+", 0));
        assertTrue(TextTypeEnum.TEXT.isValidChars('-', "-", 0));
        assertTrue(TextTypeEnum.TEXT.isValidChars('.', ".", 0));
        assertTrue(TextTypeEnum.TEXT.isValidChars('a', "a", 0));
        assertTrue(TextTypeEnum.TEXT.isValidChars('B', "B", 0));
        assertTrue(TextTypeEnum.TEXT.isValidChars('d', "d", 0));
        assertTrue(TextTypeEnum.TEXT.isValidChars('E', "E", 0));
        assertTrue(TextTypeEnum.TEXT.isValidChars('F', "F", 0));
        assertTrue(TextTypeEnum.TEXT.isValidChars('g', "g", 0));
        assertTrue(TextTypeEnum.TEXT.isValidChars('0', "0", 0));
        assertTrue(TextTypeEnum.TEXT.isValidChars('5', "5", 0));
        assertTrue(TextTypeEnum.TEXT.isValidChars('9', "9", 0));
        assertTrue(TextTypeEnum.TEXT.isValidChars(SWT.CR,""+SWT.CR, 0));
        assertTrue(TextTypeEnum.TEXT.isValidChars(SWT.DEL, ""+SWT.DEL, 0));
        assertTrue(TextTypeEnum.TEXT.isValidChars(SWT.BS, ""+SWT.BS, 0));

        assertTrue(TextTypeEnum.TEXT.isValidChars('+', "+", 1));
        assertTrue(TextTypeEnum.TEXT.isValidChars('-', "-", 1));
        assertTrue(TextTypeEnum.TEXT.isValidChars('.', ".", 1));
        assertTrue(TextTypeEnum.TEXT.isValidChars('a', "a", 1));
        assertTrue(TextTypeEnum.TEXT.isValidChars('B', "B", 1));
        assertTrue(TextTypeEnum.TEXT.isValidChars('d', "d", 1));
        assertTrue(TextTypeEnum.TEXT.isValidChars('E', "E", 1));
        assertTrue(TextTypeEnum.TEXT.isValidChars('F', "F", 1));
        assertTrue(TextTypeEnum.TEXT.isValidChars('g', "g", 1));
        assertTrue(TextTypeEnum.TEXT.isValidChars('1', "1", 1));
        assertTrue(TextTypeEnum.TEXT.isValidChars('5', "5", 1));
        assertTrue(TextTypeEnum.TEXT.isValidChars('9', "9", 1));
        assertTrue(TextTypeEnum.TEXT.isValidChars(SWT.CR,""+SWT.CR, 1));
        assertTrue(TextTypeEnum.TEXT.isValidChars(SWT.DEL, ""+SWT.DEL, 1));
        assertTrue(TextTypeEnum.TEXT.isValidChars(SWT.BS, ""+SWT.BS, 1));

        // test Alias
        assertTrue(TextTypeEnum.ALIAS.isValidChars('+', "+", 0));
        assertTrue(TextTypeEnum.ALIAS.isValidChars('-', "-", 0));
        assertTrue(TextTypeEnum.ALIAS.isValidChars('.', ".", 0));
        assertTrue(TextTypeEnum.ALIAS.isValidChars('a', "a", 0));
        assertTrue(TextTypeEnum.ALIAS.isValidChars('B', "B", 0));
        assertTrue(TextTypeEnum.ALIAS.isValidChars('d', "d", 0));
        assertTrue(TextTypeEnum.ALIAS.isValidChars('E', "E", 0));
        assertTrue(TextTypeEnum.ALIAS.isValidChars('F', "F", 0));
        assertTrue(TextTypeEnum.ALIAS.isValidChars('g', "g", 0));
        assertTrue(TextTypeEnum.ALIAS.isValidChars('0', "0", 0));
        assertTrue(TextTypeEnum.ALIAS.isValidChars('5', "5", 0));
        assertTrue(TextTypeEnum.ALIAS.isValidChars('9', "9", 0));
        assertTrue(TextTypeEnum.ALIAS.isValidChars(SWT.CR,""+SWT.CR, 0));
        assertTrue(TextTypeEnum.ALIAS.isValidChars(SWT.DEL, ""+SWT.DEL, 0));
        assertTrue(TextTypeEnum.ALIAS.isValidChars(SWT.BS, ""+SWT.BS, 0));

        assertTrue(TextTypeEnum.ALIAS.isValidChars('+', "+", 1));
        assertTrue(TextTypeEnum.ALIAS.isValidChars('-', "-", 1));
        assertTrue(TextTypeEnum.ALIAS.isValidChars('.', ".", 1));
        assertTrue(TextTypeEnum.ALIAS.isValidChars('a', "a", 1));
        assertTrue(TextTypeEnum.ALIAS.isValidChars('B', "B", 1));
        assertTrue(TextTypeEnum.ALIAS.isValidChars('d', "d", 1));
        assertTrue(TextTypeEnum.ALIAS.isValidChars('E', "E", 1));
        assertTrue(TextTypeEnum.ALIAS.isValidChars('F', "F", 1));
        assertTrue(TextTypeEnum.ALIAS.isValidChars('g', "g", 1));
        assertTrue(TextTypeEnum.ALIAS.isValidChars('1', "1", 1));
        assertTrue(TextTypeEnum.ALIAS.isValidChars('5', "5", 1));
        assertTrue(TextTypeEnum.ALIAS.isValidChars('9', "9", 1));
        assertTrue(TextTypeEnum.ALIAS.isValidChars(SWT.CR,""+SWT.CR, 1));
        assertTrue(TextTypeEnum.ALIAS.isValidChars(SWT.DEL, ""+SWT.DEL, 1));
        assertTrue(TextTypeEnum.ALIAS.isValidChars(SWT.BS, ""+SWT.BS, 1));

        //test Double
        assertFalse(TextTypeEnum.DOUBLE.isValidChars('+',"+", 0));
        assertTrue(TextTypeEnum.DOUBLE.isValidChars('-', "-", 0));
        assertTrue(TextTypeEnum.DOUBLE.isValidChars('.', ".", 0));
        assertTrue(TextTypeEnum.DOUBLE.isValidChars('a', "a", 0));
        assertTrue(TextTypeEnum.DOUBLE.isValidChars('B', "B", 0));
        assertTrue(TextTypeEnum.DOUBLE.isValidChars('d', "d", 0));
        assertTrue(TextTypeEnum.DOUBLE.isValidChars('E', "E", 0));
        assertTrue(TextTypeEnum.DOUBLE.isValidChars('F', "F", 0));
        assertFalse(TextTypeEnum.DOUBLE.isValidChars('g', "g", 0));
        assertTrue(TextTypeEnum.DOUBLE.isValidChars('0', "0", 0));
        assertTrue(TextTypeEnum.DOUBLE.isValidChars('5', "5", 0));
        assertTrue(TextTypeEnum.DOUBLE.isValidChars('9', "9", 0));
        assertTrue(TextTypeEnum.DOUBLE.isValidChars(SWT.CR,""+SWT.CR, 0));
        assertTrue(TextTypeEnum.DOUBLE.isValidChars(SWT.DEL, ""+SWT.DEL, 0));
        assertTrue(TextTypeEnum.DOUBLE.isValidChars(SWT.BS, ""+SWT.BS, 0));

        assertFalse(TextTypeEnum.DOUBLE.isValidChars('+', "+", 1));
        assertTrue(TextTypeEnum.DOUBLE.isValidChars('-', "-", 1));
        assertTrue(TextTypeEnum.DOUBLE.isValidChars('.', ".", 1));
        assertTrue(TextTypeEnum.DOUBLE.isValidChars('a', "a", 1));
        assertTrue(TextTypeEnum.DOUBLE.isValidChars('B', "B", 1));
        assertTrue(TextTypeEnum.DOUBLE.isValidChars('d', "d", 1));
        assertTrue(TextTypeEnum.DOUBLE.isValidChars('E', "E", 1));
        assertTrue(TextTypeEnum.DOUBLE.isValidChars('F', "F", 1));
        assertFalse(TextTypeEnum.DOUBLE.isValidChars('g', "g", 1));
        assertTrue(TextTypeEnum.DOUBLE.isValidChars('1', "1", 1));
        assertTrue(TextTypeEnum.DOUBLE.isValidChars('5', "5", 1));
        assertTrue(TextTypeEnum.DOUBLE.isValidChars('9', "9", 1));
        assertTrue(TextTypeEnum.DOUBLE.isValidChars(SWT.CR,""+SWT.CR, 1));
        assertTrue(TextTypeEnum.DOUBLE.isValidChars(SWT.DEL, ""+SWT.DEL, 1));
        assertTrue(TextTypeEnum.DOUBLE.isValidChars(SWT.BS, ""+SWT.BS, 1));

        //test HEX
      assertFalse(TextTypeEnum.HEX.isValidChars('+', "+", 0));
      assertTrue(TextTypeEnum.HEX.isValidChars('-', "-", 0));
      assertTrue(TextTypeEnum.HEX.isValidChars('.', ".", 0));
      assertTrue(TextTypeEnum.HEX.isValidChars('a', "a", 0));
      assertTrue(TextTypeEnum.HEX.isValidChars('B', "B", 0));
      assertTrue(TextTypeEnum.HEX.isValidChars('d', "d", 0));
      assertTrue(TextTypeEnum.HEX.isValidChars('E', "E", 0));
      assertTrue(TextTypeEnum.HEX.isValidChars('F', "F", 0));
      assertFalse(TextTypeEnum.HEX.isValidChars('g', "g", 0));
      assertTrue(TextTypeEnum.HEX.isValidChars('0', "0", 0));
      assertTrue(TextTypeEnum.HEX.isValidChars('5', "5", 0));
      assertTrue(TextTypeEnum.HEX.isValidChars('9', "9", 0));
      assertTrue(TextTypeEnum.HEX.isValidChars(SWT.CR,""+SWT.CR, 0));
      assertTrue(TextTypeEnum.HEX.isValidChars(SWT.DEL, ""+SWT.DEL, 0));
      assertTrue(TextTypeEnum.HEX.isValidChars(SWT.BS, ""+SWT.BS, 0));

      assertFalse(TextTypeEnum.HEX.isValidChars('+', "+", 1));
      assertTrue(TextTypeEnum.HEX.isValidChars('-', "-", 1));
      assertTrue(TextTypeEnum.HEX.isValidChars('.', ".", 1));
      assertTrue(TextTypeEnum.HEX.isValidChars('a', "a", 1));
      assertTrue(TextTypeEnum.HEX.isValidChars('B', "B", 1));
      assertTrue(TextTypeEnum.HEX.isValidChars('d', "d", 1));
      assertTrue(TextTypeEnum.HEX.isValidChars('E', "E", 1));
      assertTrue(TextTypeEnum.HEX.isValidChars('F', "F", 1));
      assertFalse(TextTypeEnum.HEX.isValidChars('g', "g", 1));
      assertTrue(TextTypeEnum.HEX.isValidChars('1', "1", 1));
      assertTrue(TextTypeEnum.HEX.isValidChars('5', "5", 1));
      assertTrue(TextTypeEnum.HEX.isValidChars('9', "9", 1));
      assertTrue(TextTypeEnum.HEX.isValidChars(SWT.CR,""+SWT.CR, 1));
      assertTrue(TextTypeEnum.HEX.isValidChars(SWT.DEL, ""+SWT.DEL, 1));
      assertTrue(TextTypeEnum.HEX.isValidChars(SWT.BS, ""+SWT.BS, 1));

      //test Exp
      assertFalse(TextTypeEnum.EXP.isValidChars('+', "+", 0));
      assertTrue(TextTypeEnum.EXP.isValidChars('-', "-", 0));
      assertTrue(TextTypeEnum.EXP.isValidChars('.', ".", 0));
      assertTrue(TextTypeEnum.EXP.isValidChars('a', "a", 0));
      assertTrue(TextTypeEnum.EXP.isValidChars('B', "B", 0));
      assertTrue(TextTypeEnum.EXP.isValidChars('d', "d", 0));
      assertTrue(TextTypeEnum.EXP.isValidChars('E', "E", 0));
      assertTrue(TextTypeEnum.EXP.isValidChars('F', "F", 0));
      assertFalse(TextTypeEnum.EXP.isValidChars('g', "g", 0));
      assertTrue(TextTypeEnum.EXP.isValidChars('0', "0", 0));
      assertTrue(TextTypeEnum.EXP.isValidChars('5', "5", 0));
      assertTrue(TextTypeEnum.EXP.isValidChars('9', "9", 0));
      assertTrue(TextTypeEnum.EXP.isValidChars(SWT.CR,""+SWT.CR, 0));
      assertTrue(TextTypeEnum.EXP.isValidChars(SWT.DEL, ""+SWT.DEL, 0));
      assertTrue(TextTypeEnum.EXP.isValidChars(SWT.BS, ""+SWT.BS, 0));

      assertFalse(TextTypeEnum.EXP.isValidChars('+', "+", 1));
      assertTrue(TextTypeEnum.EXP.isValidChars('-', "-", 1));
      assertTrue(TextTypeEnum.EXP.isValidChars('.', ".", 1));
      assertTrue(TextTypeEnum.EXP.isValidChars('a', "a", 1));
      assertTrue(TextTypeEnum.EXP.isValidChars('B', "B", 1));
      assertTrue(TextTypeEnum.EXP.isValidChars('d', "d", 1));
      assertTrue(TextTypeEnum.EXP.isValidChars('E', "E", 1));
      assertTrue(TextTypeEnum.EXP.isValidChars('F', "F", 1));
      assertFalse(TextTypeEnum.EXP.isValidChars('g', "g", 1));
      assertTrue(TextTypeEnum.EXP.isValidChars('1', "1", 1));
      assertTrue(TextTypeEnum.EXP.isValidChars('5', "5", 1));
      assertTrue(TextTypeEnum.EXP.isValidChars('9', "9", 1));
      assertTrue(TextTypeEnum.EXP.isValidChars(SWT.CR,""+SWT.CR, 1));
      assertTrue(TextTypeEnum.EXP.isValidChars(SWT.DEL, ""+SWT.DEL, 1));
      assertTrue(TextTypeEnum.EXP.isValidChars(SWT.BS, ""+SWT.BS, 1));
    }

}
