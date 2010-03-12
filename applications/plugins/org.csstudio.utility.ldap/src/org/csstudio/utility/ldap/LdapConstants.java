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
package org.csstudio.utility.ldap;

import org.csstudio.platform.util.StringUtil;

/**
 * Constants class for LDAP entries.
 * 
 * @author bknerr
 * @version $Revision$
 * @since 11.03.2010
 */
public class LdapConstants {
	
	/**
	 * Don't instantiate.
	 */
	private LdapConstants() {}
	
	public static final String FIELD_SEPARATOR = ",";
	public static final String FIELD_ASSIGNMENT = "=";
	public static final String EREN_FIELD_NAME = "eren";
	public static final String EFAN_FIELD_NAME = "efan";
	public static final String ECON_FIELD_NAME = "econ";
	public static final String ECOM_FIELD_NAME = "ecom";
	
	public static final String[] FORBIDDEN_SUBSTRINGS = new String[] {
		"/","\\","+","@"
	};

	/**
	 * Filters for forbidden substrings {@link LdapConstants}. 
	 * @param recordName the name to filter
	 * @return true, if the forbidden substring is contained, false otherwise (even for empty and null strings)
	 */
	public static boolean filterLDAPNames(final String recordName) {
		if (!StringUtil.hasLength(recordName)) {
			return false;
		}
		for (String s : FORBIDDEN_SUBSTRINGS) {
			if (recordName.contains(s)) {
				return true;
			}
		}
		return false;
	}

}
