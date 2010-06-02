/*
 * Copyright (c) 2009 Stiftung Deutsches Elektronen-Synchrotron,
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

import static org.junit.Assert.assertEquals;

import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.csstudio.alarm.service.declaration.LdapEpicsAlarmCfgObjectClass;
import org.csstudio.utility.ldap.LdapNameUtils;
import org.junit.Test;


/**
 * @author Joerg Rathlev
 */
public class LdapNameUtilsTest {

	@Test
	public void testSimpleNameOfSingleRdnName() throws Exception {
		final LdapName name = new LdapName("foo=bar");
		assertEquals("bar", LdapNameUtils.simpleName(name));
	}

	@Test
	public void testSimpleNameOfHierarchicalLdapName() throws Exception {
		final LdapName name = new LdapName("foo=bar,ou=Test,dc=example,dc=com");
		assertEquals("bar", LdapNameUtils.simpleName(name));
	}

	@Test
	public void testSimpleNameOfNameWithSpecialCharacters() throws Exception {
		final LdapName name = new LdapName("foo=name/with\\=special\\,characters");
		assertEquals("name/with=special,characters", LdapNameUtils.simpleName(name));
	}

	@Test
	public void testObjectClassOfSingleRdnName() throws Exception {
		final LdapName name = new LdapName("efan=foobar");
		final Rdn rdn = name.getRdn(name.size() - 1);
		assertEquals(LdapEpicsAlarmCfgObjectClass.FACILITY, LdapEpicsAlarmCfgObjectClass.FACILITY.getNodeTypeByNodeTypeName(rdn.getType()));
	}

	@Test
	public void testObjectClassOfHierarchicalLdapName() throws Exception {
		final LdapName name = new LdapName("eren=foobar,ou=Test,dc=example,dc=com");
		final Rdn rdn = name.getRdn(name.size() - 1);
		assertEquals(LdapEpicsAlarmCfgObjectClass.RECORD, LdapEpicsAlarmCfgObjectClass.RECORD.getNodeTypeByNodeTypeName(rdn.getType()));
	}

}
