
package org.csstudio.websuite.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * The eLogbook is storing logbook entries as well as images from screen dumps
 * but also any other kind of files in Oracle. 
 * The design is modular in a way that reading and writing to/ from eLog sources is
 * encapsulated in dedicated classes which are extended from the base class 
 * LogBook. 
 * A basic feature of this eLogbook is the ability to read entries from other 
 * logbooks and display internal and external logbook entries in the correct 
 * sequential order.
 * References in the Oracle-eLogbook can point to any other logbook.
 * Icons in the html pages will lead to these entries accordingly.
 * This implementation of the eLogbook is based on the logbook implemented
 * initially for TTF by the MVP group at DESY.
 *
 * (C) DESY Hamburg 2003
 *
 * @author Matthias Clausen DESY/MKS-2
 * @param 
 * @return
 * @version 1.5.9
 *
 * The Utility class
 * is used to define some useful utility methods
 * Initial implementation by Matthias Clausen DESY/MKS-2 September-2003
 */

public class Utility
{
    /**
     * The method returns a string representation of the value that is stored in the string <code>checkString</code>.
     * The result will be rounded regarding the precision.
     * 
     * @param checkString
     * @param precision
     * @return
     */
    public static String precision(String checkString, int precision) {
        
        DecimalFormatSymbols dcf = null;
        DecimalFormat format = null;
        String result = "";
        double dv = 0.0;
        
        if(checkString == null) {
            return result;
        }

        checkString = checkString.trim();
        
        if(precision < 0) {
            precision = 0;
        }

        dcf = new DecimalFormatSymbols(Locale.US);
        dcf.setDecimalSeparator('.');

        try {
            dv = Double.parseDouble(checkString);

            format = new DecimalFormat("0.#", dcf);
            format.setMinimumFractionDigits(precision);
            format.setMaximumFractionDigits(precision);
            result = format.format(dv);
        } catch(NumberFormatException nfe) {
            result = checkString;
        }
        
        return result;
    }
}