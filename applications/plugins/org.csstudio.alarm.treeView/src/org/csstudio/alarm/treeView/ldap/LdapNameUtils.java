/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.alarm.treeView.ldap;


/**
 * Utility functions for working with names from an LDAP directory.
 * 
 * @author Jurij Kodre
 */
public final class LdapNameUtils {
	
	/**
	 * Constructor.
	 */
	private LdapNameUtils() {
	}

	/**
	 * Removes double quotes from a string.
	 * @param toClean the string to be cleaned.
	 * @return the cleaned string.
	 */
	public static String removeQuotes(final String toClean) {
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
	 * @param name the name.
	 * @return the simple name.
	 */
	public static String simpleName(final String name){
		int pos1 = name.indexOf("=");
		int pos2= name.indexOf(",");
		if (pos2 ==-1 ) {pos2=name.length();} //if comma is not present, we must take last character
		return name.substring(pos1+1,pos2);
	}
	
	/**
	 * Returns the object class of the given name.
	 * For example, given &quot;a=x,b=y,c=z&quot;, returns &quot;a&quot;.
	 * @param name the name.
	 * @return the object class.
	 */
	public static String objectClass(final String name) {
		int pos1 = name.indexOf("=");
		return name.substring(0, pos1);
	}
	
	/**
	 * Returns the qualified name of the parent of the given name.
	 * For example, given &quot;a=x,b=y,c=z&quot;, returns &quot;b=y,c=z&quot;.
	 * Returns {@code null} if there is no parent.
	 * @param name the name.
	 * @return the parent name or {@code null}.
	 */
	public static String parentName(final String name){
		String dum = name;
		if (dum ==null) {dum ="";}
		int pos=dum.indexOf(",");
		if (pos==-1) {
			return null;
		}
		return dum.substring(pos+1);
	}
}
