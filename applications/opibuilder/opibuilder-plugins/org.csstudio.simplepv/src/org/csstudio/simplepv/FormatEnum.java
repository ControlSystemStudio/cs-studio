package org.csstudio.simplepv;
/**
 * The format enumeration for formating a pv value to string. The order of the
 * format enumeration should be not be changed to keep the opi compatibility.
 * @author Xihui Chen
 *
 */
public enum FormatEnum {
    /**
     * The default format type when user doesn't explicitly set the format type.
     */
    DEFAULT("Default"),
    /**
     * Format it as a decimal.
     */
    DECIMAL("Decimal"),
    /**
     * Exponential format. For example, 2.023E10
     */
    EXP("Exponential"),
    /**
     *Hex 32 format, for example 0xFDC205
     */
    HEX("Hex 32"),
    /**
     * If possible, convert it to a string. If it is a number or number array, treat
     * it as char or char array.
     */
    STRING("String"),
    /**
     * Display 64 bits value in HEX for example 0xF0DEADBEEF
     */
    HEX64("Hex 64"),
    /**
     * If it is numeric value, automatically display it in decimal or exponential
     * based on the value digits to make sure the string won't be too long. The recommended
     * digits is 4.
     */
    COMPACT("Compact"),
    /**
     * Engineering format (exponent is power of three). For example, 20.23E9
     */
    ENG("Engineering");

    private String description;
    private FormatEnum(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static String[] stringValues(){
        String[] result = new String[values().length];
        int i =0 ;
        for(FormatEnum f : values()){
            result[i++] = f.toString();
        }
        return result;
    }
}