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

        out.setValueRange("-100", "0");
        assertEquals(-100, out.getMinValue());
        assertEquals(0, out.getMaxValue());
        
        out.setValueRange("-200000000", "-100000000");
        assertEquals(-200000000, out.getMinValue());
        assertEquals(-100000000, out.getMaxValue());
        
        out.setValueRange("200000000", "100000000");
        assertEquals(100000000, out.getMinValue());
        assertEquals(200000000, out.getMaxValue());

        out.setValueRange("0xA", "0xA0");
        assertEquals(10, out.getMinValue());
        assertEquals(160, out.getMaxValue());

    }

}
