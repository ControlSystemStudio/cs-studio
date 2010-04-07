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
package org.csstudio.utility.ldap.reader;

import static org.csstudio.utility.ldap.LdapUtils.EFAN_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapUtils.EPICS_CTRL_FIELD_VALUE;
import static org.csstudio.utility.ldap.LdapUtils.FIELD_ASSIGNMENT;
import static org.csstudio.utility.ldap.LdapUtils.OU_FIELD_NAME;
import static org.csstudio.utility.ldap.LdapUtils.any;
import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.csstudio.utility.ldap.model.LdapContentModel;
import org.junit.Test;

import service.LdapService;
import service.impl.LdapServiceImpl;

/**
 * @author hrickens
 *
 */
public class LDAPReaderTest {

    private final LdapService _service = LdapServiceImpl.getInstance();

    /**
     * Test method for {@link org.csstudio.utility.ldap.reader.LDAPReader#LDAPReader(java.lang.String[],
     * org.csstudio.utility.ldap.LdapSearchResult.utility.LdapResultList)}.
	 */
	@Test
	public void testLDAPServiceFacilityLookup() {
	    final LdapContentModel model =
	        _service.getEntries(new LdapSeachResultObserver(),
	                            OU_FIELD_NAME + FIELD_ASSIGNMENT + EPICS_CTRL_FIELD_VALUE,
	                            any(EFAN_FIELD_NAME));

		Assert.assertTrue(!model.getFacilities().isEmpty());
	}

	/**
	 * Test method for {@link org.csstudio.utility.ldap.reader.LDAPReader#LDAPReader(java.lang.String[],
	 * int, org.csstudio.utility.ldap.LdapSearchResult.utility.ResultList)}.
	 */
	@Test
	public void testLDAPReaderStringArrayIntResultList() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.csstudio.utility.ldap.reader.LDAPReader#LDAPReader(java.lang.String,
	 * java.lang.String, org.csstudio.utility.ldap.LdapSearchResult.utility.ResultList)}.
	 */
	@Test
	public void testLDAPReaderStringStringResultList() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.csstudio.utility.ldap.reader.LDAPReader#LDAPReader(java.lang.String,
	 * java.lang.String, int, org.csstudio.utility.ldap.LdapSearchResult.utility.ResultList)}.
	 */
	@Test
	public void testLDAPReaderStringStringIntResultList() {
		fail("Not yet implemented");
	}

}
