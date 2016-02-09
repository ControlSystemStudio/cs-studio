/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.util;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.apputil.macros.Macros;
import org.csstudio.java.string.StringSplitter;
import org.csstudio.opibuilder.properties.MacrosProperty;

/**The value type definition for {@link MacrosProperty}, which describes the input
 * for a Macros Property.
 * TODO Combine with MacroTable
 * TODO Hide the actual map implementation
 * TODO Why does the order of macros matter? For environment vars, the order doesn't matter. Can still replace macros recursively as in $($(M))
 * @author Xihui Chen
 */
public class MacrosInput extends Macros {

    private boolean include_parent_macros;

    private static final char ITEM_SEPARATOR = ','; //$NON-NLS-1$
    private static final char MACRO_SEPARATOR = '='; //$NON-NLS-1$
    private static final char QUOTE = '\"'; //$NON-NLS-1$

    public MacrosInput(Map<String, String> macros, boolean include_parent_macros) {
        super(macros);
        this.include_parent_macros = include_parent_macros;
    }

    /**
     * @return the include_parent_macros
     */
    public final boolean isInclude_parent_macros() {
        return include_parent_macros;
    }

    /**
     * @param include_parent_macros the include_parent_macros to set
     */
    public final void setInclude_parent_macros(boolean include_parent_macros) {
        this.include_parent_macros = include_parent_macros;
    }

    public MacrosInput getCopy(){
        MacrosInput result = new MacrosInput(
                new LinkedHashMap<String, String>(), include_parent_macros);
        result.putAll(macrosMap);
        return result;
    }

    @Override
    public String toString() {
        return (include_parent_macros? "{" + "Parent Macros" + //$NON-NLS-1$
                "} " : "") + macrosMap.toString();
    }

    @Override
    public int hashCode(){
         int result = HashCodeUtil.SEED;
         //collect the contributions of various fields
         result = HashCodeUtil.hash(result, include_parent_macros);
         result = HashCodeUtil.hash(result, macrosMap);
         result = HashCodeUtil.hash(result, macrosMap.keySet().toArray());
         return result;
    }


    @Override
    public boolean equals(Object obj) {
        if(obj != null && obj instanceof MacrosInput){
            MacrosInput input = (MacrosInput)obj;
            if(include_parent_macros != input.isInclude_parent_macros())
                return false;
            if(!macrosMap.equals(input.getMapCopy()))
                return false;
            List<Object> keyList = Arrays.asList(macrosMap.keySet().toArray());
            List<Object> inputKeyList = Arrays.asList(input.keySet().toArray());
            if(keyList.equals(inputKeyList))
                return true;
            else
                return false;

        }else
            return false;

    }

    /**
     * Return a map corresponding to all defined macros.
     * @return a shallow copy of the macros map.
     */
    public Map<String, String> getMapCopy() {
        return new LinkedHashMap<String, String>(macrosMap);
    }

    /**
     * @return a String with format like this: "true", "macro1 = hello", "macro2 = hello2"
     */
    public String toPersistenceString(){
        StringBuilder result = new StringBuilder();
        result.append(QUOTE + Boolean.toString(include_parent_macros) + QUOTE);
        for(String key : macrosMap.keySet()){
            result.append(ITEM_SEPARATOR + "" + QUOTE + key + //$NON-NLS-1$
                    MACRO_SEPARATOR + macrosMap.get(key) + QUOTE);
        }
        return result.toString();
    }

    // TODO Offer a parser just for "macro1 = hello", "macro2 = hello2" without the inital "true", "false
    /**Parse MacrosInput from persistence string.
     * @param s
     * @return
     * @throws Exception
     */
    public static MacrosInput recoverFromString(String s) throws Exception{
        String[] items = StringSplitter.splitIgnoreInQuotes(s, ITEM_SEPARATOR, true);
        MacrosInput macrosInput = new MacrosInput(new LinkedHashMap<String, String>(), true);
        for(int i= 0; i<items.length; i++){
            if(i == 0)
                macrosInput.setInclude_parent_macros(Boolean.valueOf(items[i]));
            else{
                String[] macro = StringSplitter.splitIgnoreInQuotes(items[i], MACRO_SEPARATOR, true);
                if(macro.length == 2)
                    macrosInput.put(macro[0], macro[1]);
                else if(macro.length == 1) //if it is an empty value macro
                    macrosInput.put(macro[0], ""); //$NON-NLS-1$
            }
        }
        return macrosInput;
    }

}
