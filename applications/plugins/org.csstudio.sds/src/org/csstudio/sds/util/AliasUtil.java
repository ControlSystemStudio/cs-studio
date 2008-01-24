package org.csstudio.sds.util;

import java.util.Map;


/**
 * @author Kai Meyer, Helge Rickens
 * 
 * @deprecated use {@link ChannelReferenceValidationUtil#createCanonicalName(String, Map)} instead
 *
 */
public class AliasUtil {
	
	/**
	 * Returns the resolved process variable.
	 * @param pvKey An alias
	 * @param aliases The map of known aliases
	 * @return The resolves process variable
	 * 
	 * @deprecated use {@link ChannelReferenceValidationUtil#createCanonicalName(String, Map)} instead
	 */
	public static String getAliasName(final String pvKey, final Map<String, String> aliases) {
		if (pvKey!=null && aliases!=null) {
		    String text=pvKey;
		    String prefix ="";
		    String key="";
		    String postfix ="";
		    while(text.contains("$")){
		        int start = pvKey.indexOf("$");
		        prefix = prefix.concat(pvKey.substring(0,start++));
		        int end = pvKey.indexOf("$",start);
    			key = pvKey.substring(start,end++);
    			postfix = pvKey.substring(end).concat(postfix);
    			if (aliases.containsKey(key)) {
    				text= aliases.get(key);
    			}else{
    			    break;
    			}
		    }
		    return prefix+text+postfix;
		}
		return pvKey;
	}

}
