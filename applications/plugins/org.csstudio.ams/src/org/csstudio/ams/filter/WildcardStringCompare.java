
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

package org.csstudio.ams.filter;

import java.util.regex.Pattern;

/*		Test-Results:
String str = new String("ABCDEFGHIJKLMN abcdefghalloHALLO");
System.out.println(WildcardStringCompare.compare(str, "*"));// true
System.out.println(WildcardStringCompare.compare(str, "AB"));// false
System.out.println(WildcardStringCompare.compare(str, "AB*"));// true
System.out.println(WildcardStringCompare.compare(str, "*AB"));// false
System.out.println(WildcardStringCompare.compare(str, "*AB*"));// true
System.out.println();
System.out.println(WildcardStringCompare.compare(str, "*?AB*"));// true
System.out.println(WildcardStringCompare.compare(str, "*?BC*"));// true
System.out.println(WildcardStringCompare.compare(str, "*AB?D*"));// true
System.out.println(WildcardStringCompare.compare(str, "*BC*Hallo*"));// true
System.out.println(WildcardStringCompare.compare(str, "*BC*Hallo"));// true
System.out.println();
System.out.println(WildcardStringCompare.compare(str, "*BC?e?g*hallo"));// true
System.out.println(WildcardStringCompare.compare(str, ""));// false
System.out.println(WildcardStringCompare.compare(str, "*o"));// true
System.out.println(WildcardStringCompare.compare(str, "* *"));// true
System.out.println(WildcardStringCompare.compare(str, "*halloh*"));// true
System.out.println();
System.out.println(WildcardStringCompare.compare(str, "*HaLLO*"));// true
System.out.println(WildcardStringCompare.compare(str, "*H*aLLo"));// true
System.out.println(WildcardStringCompare.compare(str, "*H*aLLo*"));// true
System.out.println(WildcardStringCompare.compare(str, "*hallo*hallo*"));// true
System.out.println(WildcardStringCompare.compare(str, "*hallo?hallo*"));// false
System.out.println();
String string = new String("!§$%&/()=+#-._.gM@");
System.out.println(WildcardStringCompare.compare(string, "*"));// true
System.out.println(WildcardStringCompare.compare(string, "*!*"));// true
System.out.println(WildcardStringCompare.compare(string, "*@"));// true
System.out.println(WildcardStringCompare.compare(string, "*@*"));// true
System.out.println(WildcardStringCompare.compare(string, "*()*"));// true
System.out.println();
System.out.println(WildcardStringCompare.compare(string, "*.*"));// true
System.out.println(WildcardStringCompare.compare(string, "*._.*"));// true
System.out.println(WildcardStringCompare.compare(string, "*.?.*"));// true
System.out.println(WildcardStringCompare.compare(string, "*=*.*"));// true
System.out.println(WildcardStringCompare.compare(string, "*!*@*"));// true
System.out.println();
*/

public abstract class WildcardStringCompare
{
	/**
	 * Compare two Strings (ignore case)
	 * 
	 * @param strMsg		String came from message (may not contain wildcards)
	 * @param wildCardStr	String typed by the user in filter condition (can contain "*" and "?" wildcards)
	 * @return  true - EQUAL ; false - NOT_EQUAL, or null
	 * @throws Exception
	 */
	public static boolean compare(String strMsg, String wildCardStr) throws Exception
	{
		if ((strMsg == null) || (wildCardStr == null))
			return false;
		
		return Pattern.compile(wildcardToRegex(wildCardStr), Pattern.CASE_INSENSITIVE).matcher(strMsg).matches();
	}

	// Search for "*" and "?" wildcards, make Regex conform 
	private static String wildcardToRegex(String wildcard)
	{
	    StringBuffer s = new StringBuffer(wildcard.length());
	    s.append('^');
	
		for (int i = 0, is = wildcard.length() ; i < is ; i++)
		{
		    char c = wildcard.charAt(i);
		    
			switch(c)
			{
				case '*':
					s.append(".*");
					break;
				case '?':
					s.append(".");
					break;
				// escape special regexp-characters
				case '(': case ')': case '[': case ']': case '$':
				case '^': case '.': case '{': case '}': case '|':
				case '\\':
					s.append("\\");
					s.append(c);
					break;
				default:
					s.append(c);
					break;
			}
		}
	
		s.append('$');
		return (s.toString());
	}
}
