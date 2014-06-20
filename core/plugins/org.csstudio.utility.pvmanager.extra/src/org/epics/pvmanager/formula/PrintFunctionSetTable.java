/**
 * Copyright (C) 2010-14 pvmanager developers. See COPYRIGHT.TXT
 * All rights reserved. Use is subject to license terms. See LICENSE.TXT
 */
package org.epics.pvmanager.formula;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Utility class to print an prefilled HTML table from a function set.
 * 
 * @author carcassi
 */
public class PrintFunctionSetTable {
    
    /**
     * Entry point for the utility.
     * 
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        FormulaFunctionSet set = new ArrayFunctionSet();
        List<FormulaFunction> functions = new ArrayList<>(set.getFunctions());
        Collections.sort(functions, new Comparator<FormulaFunction>() {

            @Override
            public int compare(FormulaFunction o1, FormulaFunction o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        
        System.out.println("        <table border=\"1\" style=\"border-collapse: collapse\">\n" +
"            <thead>\n" +
"                <tr>\n" +
"                    <th>Signature</th>\n" +
"                    <th>Description</th>\n" +
"                    <th>Null handling</th>\n" +
"                    <th>Alarm</th>\n" +
"                    <th>Time</th>\n" +
"                </tr>\n" +
"            </thead>\n" +
"            <tbody>");
        
        for (FormulaFunction formulaFunction : functions) {
            System.out.println("                <tr>");
            System.out.println("                    <td><code>" + FormulaFunctions.formatSignature(formulaFunction) + "</code></td>");
            System.out.println("                    <td>" + formulaFunction.getDescription() + "</td>");
            System.out.println("                    <td>Null if one of the arguments is null</td>");
            System.out.println("                    <td>Highest alarm of the arguments</td>");
            System.out.println("                    <td>Latest valid time of the arguments or now if no valid time is found</td>");
            System.out.println("                </tr>");
        }
        
        System.out.println("            </tbody>\n" +
"        </table>");
    }
}
