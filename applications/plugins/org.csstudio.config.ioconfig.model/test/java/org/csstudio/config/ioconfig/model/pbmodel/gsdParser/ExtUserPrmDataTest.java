package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ExtUserPrmDataTest {

    @Test
    public void text() {
        ExtUserPrmData out = new ExtUserPrmData(null, null, "desc");
        assertEquals(out.getText(), "desc");
        out.setText("");
        assertEquals(out.getText(), "");
        out.setText("^1234567890ß´qwertzuiopü+asdfghjklöä#yxcvbnm,.-QAY\\\"");
        assertEquals(out.getText(), "^1234567890ß´qwertzuiopü+asdfghjklöä#yxcvbnm,.-QAY\\\"");

    }

    @Test
    public void dataType() {
        ExtUserPrmData out = new ExtUserPrmData(null, null, null);
        assertEquals(out.getDataType(), "");
        out.setDataType("UINT8");
        assertEquals(out.getDataType(), "UINT8");
        out.setDataType("^1234567890ß´qwertzuiopü+asdfghjklöä#yxcvbnm,.-QAY\\\"");
        assertEquals(out.getDataType(), "^1234567890ß´qwertzuiopü+asdfghjklöä#yxcvbnm,.-QAY\\\"");
        out.setDataType("Bit(1)");
        assertEquals(out.getDataType(), "Bit(1)");
        assertEquals(1, out.getMinValue());
        assertEquals(1, out.getMaxValue());
        out.setDataType("Bit (100)");
        assertEquals(out.getDataType(), "Bit (100)");
        assertEquals(out.getMinValue(), 100);
        assertEquals(out.getMaxValue(), 100);
        out.setDataType("BitArea(1-5)");
        assertEquals(out.getDataType(), "BitArea(1-5)");
        assertEquals(out.getMinValue(), 1);
        assertEquals(out.getMaxValue(), 5);
        out.setDataType("BitArea (15-50)");
        assertEquals(out.getDataType(), "BitArea (15-50)");
        assertEquals(out.getMinValue(), 15);
        assertEquals(out.getMaxValue(), 50);
    }

    @Test
    public void defaults() {
        ExtUserPrmData out = new ExtUserPrmData(null, null, null);
        assertTrue(out.getDefault()==0);
        out.setDefault("0");
        assertTrue(out.getDefault()==0);
        out.setDefault("-100000000");
        assertTrue(out.getDefault()==-100000000);
        out.setDefault("100000000");
        assertTrue(out.getDefault()==100000000);

        
        out.setDefault("0xA");
        assertFalse(out.getDefault()==10);
        assertTrue(out.getDefault()==0);
        out.setDefault("ten");
        assertFalse(out.getDefault()==10);
        assertTrue(out.getDefault()==0);
    }


    @Test
    public void minBit() {
        ExtUserPrmData out = new ExtUserPrmData(null, null, null);
        assertTrue(out.getMinBit()==0);
        assertTrue(out.getMinBit()==0);
        out.setMinBit("0");
        assertTrue(out.getMinBit()==0);
        out.setMinBit("-100000000");
        assertTrue(out.getMinBit()==-100000000);
        out.setMinBit("100000000");
        assertTrue(out.getMinBit()==100000000);

        
        out.setMinBit("0xA");
        assertFalse(out.getMinBit()==10);
        assertTrue(out.getMinBit()==0);
        out.setMinBit("ten");
        assertFalse(out.getMinBit()==10);
        assertTrue(out.getMinBit()==0);
    }

    @Test
    public void maxBit() {
        ExtUserPrmData out = new ExtUserPrmData(null, null, null);
        assertTrue(out.getMaxBit()==0);
        out.setMaxBit("0");
        assertTrue(out.getMaxBit()==0);
        out.setMaxBit("-100000000");
        assertTrue(out.getMaxBit()==-100000000);
        out.setMaxBit("100000000");
        assertTrue(out.getMaxBit()==100000000);

        
        out.setMaxBit("0xA");
        assertFalse(out.getMaxBit()==10);
        assertTrue(out.getMaxBit()==0);
        out.setMaxBit("ten");
        assertFalse(out.getMaxBit()==10);
        assertTrue(out.getMaxBit()==0);

    }

    @Test
    public void maxValue() {
        ExtUserPrmData out = new ExtUserPrmData(null, null, null);
        assertTrue(out.getMaxValue()==0);
        out.setMaxValue("0");
        assertTrue(out.getMaxValue()==0);
        out.setMaxValue("-100000000");
        assertTrue(out.getMaxValue()==-100000000);
        out.setMaxValue("100000000");
        assertTrue(out.getMaxValue()==100000000);

        
        out.setMaxValue("0xA");
        assertFalse(out.getMaxValue()==10);
        assertTrue(out.getMaxValue()==0);
        out.setMaxValue("ten");
        assertFalse(out.getMaxValue()==10);
        assertTrue(out.getMaxValue()==0);

    }

}
