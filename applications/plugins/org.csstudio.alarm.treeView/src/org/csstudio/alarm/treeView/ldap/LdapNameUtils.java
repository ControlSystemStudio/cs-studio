package org.csstudio.alarm.treeView.ldap;


/**
 * Utility functions for working with names from an LDAP directory.
 * 
 * @author Jurij Kodre
 */
public class LdapNameUtils {

	/**
	 * Removes double quotes from a string.
	 */
	public static String removeQuotes(String toClean){
		StringBuffer tc = new StringBuffer(toClean);
		String grr = "\"";
		int pos = tc.indexOf(grr);
		while (pos>-1){
			tc.deleteCharAt(pos);
			pos = tc.indexOf(grr);
		}
		return tc.toString();
	}
	
	/**
	 * Returns the simple name of the given name.
	 * For example, given &quot;a=x,b=y,c=z&quot;, returns &quot;x&quot;.
	 */
	public static String simpleName(String name){
		int pos1 = name.indexOf("=");
		int pos2= name.indexOf(",");
		if (pos2 ==-1 ) {pos2=name.length();} //if comma is not present, we must take last character
		return name.substring(pos1+1,pos2);
	}
	
	/**
	 * Returns the qualified name of the parent of the given name.
	 * For example, given &quot;a=x,b=y,c=z&quot;, returns &quot;b=y,c=z&quot;.
	 * Returns {@code null} if there is no parent.
	 */
	public static String parentName(String name){
		String dum = name;
		if (dum ==null) {dum ="";}
		int pos=dum.indexOf(",");
		if (pos==-1) return null;
		return dum.substring(pos+1);
	}
}