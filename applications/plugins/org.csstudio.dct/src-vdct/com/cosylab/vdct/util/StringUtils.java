package com.cosylab.vdct.util;

/**
 * Copyright (c) 2002, Cosylab, Ltd., Control System Laboratory, www.cosylab.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the Cosylab, Ltd., Control System Laboratory nor the names
 * of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.Color;

/**
 * This type was created in VisualAge.
 */
public class StringUtils {
    private static final String ZERO = "0";
    private static final String ONE = "1";
    //private static final String HEX = "0x";

    private static final String nullString = "";

    private static final String QUOTE = "\"";
    private static final String nonMacroChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-:[]<>;";
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param state boolean
 */
public static String boolean2str(boolean state) {
    if (state) return ONE;
    else return ZERO;
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 18:49:45)
 * @return java.lang.String
 * @param color java.awt.Color
 */
public static String color2string(java.awt.Color color) {
//    return HEX+Integer.toHexString(color.getRGB() & 0xffffff);
    if (color==null)
        return ZERO;
    else
        return Integer.toString(color.getRGB() & 0xffffff);
}
/**
 * This method was created in VisualAge.
 * @param fileName java.lang.String
 * @param newFN java.lang.String
 */
public static String getFileName(String fileName) {
    // fileName can contain path!

    int pos = fileName.lastIndexOf(java.io.File.separatorChar);
    if (pos<0) return fileName;

    return fileName.substring(pos+1);

}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 18:52:04)
 * @return java.awt.Color
 * @param rgb int
 */
public static java.awt.Color int2color(int rgb) {
    // !!! add more or use flyweight
    switch (rgb) {
        case 0x000000 : return Color.black;
        case 0x0000ff : return Color.blue;
        case 0x00ff00 : return Color.green;
        case 0xff0000 : return Color.red;
        case 0xffffff : return Color.white;
        default: return new Color(rgb);
    }

}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param str java.lang.String
 */
public static String quoteIfMacro(String str) {

    boolean needsQuotes = false;
    int len = str.length();

    if (len>0 && Character.isDigit(str.charAt(0)))
        // special case for VDCT, parser does not handle "<digit(s)><alpha>" as whole word
        needsQuotes = true;
    else {
        for (int i=0; (i<len) && !needsQuotes; i++)
            if (nonMacroChars.indexOf(str.charAt(i))<0) needsQuotes=true;
    }

    if (needsQuotes) return QUOTE+str+QUOTE;
    else return str;
}
/**
 * This method was created in VisualAge.
 * @param str java.lang.String
 * @param begining java.lang.String
 */
public static String removeBegining(String str, String begining) {
    if (begining.equals(nullString)) return str;
    else if (str.startsWith(begining)) return str.substring(begining.length());
    else return str;
}
/**
 * This method was created in VisualAge.
 * @param str java.lang.String
 * @param s1 java.lang.String
 * @param s2 java.lang.String
 */
public static String replaceEnding(String str, String s1, String s2) {
    if (str.equals(s1)) return s2;
    else if (!str.endsWith(s1)) return str;

    int pos = str.lastIndexOf(s1);
    if (pos<0) return str;
    return str.substring(0, str.length()-s1.length())+s2;
}
/**
 * This method was created in VisualAge.
 * @param str java.lang.String
 * @param s1 java.lang.String
 * @param s2 java.lang.String
 */
public static String replace(String source, String from, String to)
  {
  StringBuffer sb = new StringBuffer();
  int oldIndex=0, newIndex;
  while (-1 != (newIndex = source.indexOf(from,oldIndex)))
    {
    sb.append(source.substring(oldIndex,newIndex)).append(to);
    oldIndex=newIndex+from.length();
    }
  if (oldIndex < source.length())
    sb.append(source.substring(oldIndex));
  return sb.toString();
  }
/**
 * This method was created in VisualAge.
 * @param fileName java.lang.String
 * @param newFN java.lang.String
 */
public static String replaceFileName(String fileName, String newFN) {
    // fileName can contain path!

    int pos = fileName.lastIndexOf(java.io.File.separatorChar);
    if (pos<0) return newFN;

    String onlyFN = fileName.substring(pos+1);

    return replaceEnding(fileName, onlyFN, newFN);

}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param str java.lang.String
 */
public static boolean str2boolean(String str) {
    return str.trim().equals(ONE);
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 18:52:04)
 * @return java.awt.Color
 * @param str java.lang.String
 */
public static java.awt.Color string2color(String str) {
    int rgb = Integer.parseInt(str);
    // !!! add more or use flyweight
    switch (rgb) {
        case 0x000000 : return Color.black;
        case 0x0000ff : return Color.blue;
        case 0x00ff00 : return Color.green;
        case 0xff0000 : return Color.red;
        case 0xffffff : return Color.white;
        default: return new Color(rgb);
    }

}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 18:52:04)
 * @return java.lang.String
 * @param str java.lang.String
 */
// TODO implement more efficient algorithm
public static String removeQuotesAndLineBreaks(String str) {
    return str.replaceAll("\\\"", "\\\\\"").replaceAll("\\n", "\\\\n");
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 18:52:04)
 * @return java.lang.String
 * @param str java.lang.String
 */
//TODO implement more efficient algorithm
public static String removeQuotes(String str) {
    if (str.indexOf('"')>=0)
        str = str.replaceAll("\\\"", "\\\\\"");
    return str;
}
/**
 * Insert the method's description here.
 * Creation date: (23.4.2001 18:52:04)
 * @return java.awt.Color
 * @param str java.lang.String
 */
public static String incrementName(String newName, String suffix)
{
    String snum = nullString;
    int i = newName.length()-1;
    for (; i>=0 && Character.isDigit(newName.charAt(i)); i--)
        snum = newName.charAt(i) + snum;
    i++;
    if (snum!=nullString)
    {
        // skip leading zeros
        for (; i<(newName.length()-1) && newName.charAt(i)=='0'; i++);

        int len = String.valueOf(Integer.parseInt(snum)).length();
        snum = String.valueOf(Integer.parseInt(snum)+1);

        // preserve number of digits
        if (snum.length()>len && i>0 && newName.charAt(i-1)=='0')
            i--;
    }
    else
        snum = suffix;

    if (i==0)
        newName = snum;
    else
        newName = newName.substring(0, i) + snum;

    return newName;
}


}
