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

package org.csstudio.alarm.treeview.ldap;

import static org.junit.Assert.assertEquals;

import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.csstudio.alarm.treeView.model.IAlarmProcessVariableNode;
import org.csstudio.alarm.treeView.model.IAlarmSubtreeNode;
import org.csstudio.alarm.treeView.model.ProcessVariableNode;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.csstudio.alarm.treeView.model.TreeNodeSource;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Joerg Rathlev
 */
public class TreeBuilderUnitTest {

    private static final String A = "a";
	private static final String B = "b";
    private static final String C = "c";

    private IAlarmSubtreeNode _tree;
	private IAlarmSubtreeNode _a;
	private IAlarmSubtreeNode _b;
	private IAlarmProcessVariableNode _c;

	/**
	 * <p>Initializes a tree for testing. The tree will have the following
	 * structure:</p>
	 *
	 * <pre>
	 * root           [SubtreeNode]
	 *   +--a         [SubtreeNode, efan=a]
	 *      +--b      [SubtreeNode, ecom=b,efan=a]
	 * </pre>
	 */
	@Before
	public void setUp() throws Exception {
		_tree = new SubtreeNode.Builder(LdapEpicsAlarmcfgConfiguration.UNIT.getUnitTypeValue(), LdapEpicsAlarmcfgConfiguration.UNIT, TreeNodeSource.ROOT).build();
		_a = new SubtreeNode.Builder(A, LdapEpicsAlarmcfgConfiguration.FACILITY, TreeNodeSource.LDAP).setParent(_tree).build();
		_b = new SubtreeNode.Builder(B, LdapEpicsAlarmcfgConfiguration.COMPONENT, TreeNodeSource.LDAP).setParent(_a).build();
		_c = new ProcessVariableNode.Builder(C, TreeNodeSource.LDAP).setParent(_b).build();

	}

	@Test
	public void testDirectoryNames() throws Exception {
	    final LdapName aName = new LdapName("");
	    aName.add(new Rdn(LdapEpicsAlarmcfgConfiguration.UNIT.getNodeTypeName(), LdapEpicsAlarmcfgConfiguration.UNIT.getUnitTypeValue()));
	    aName.add(new Rdn(LdapEpicsAlarmcfgConfiguration.FACILITY.getNodeTypeName(), A));
        assertEquals(aName, _a.getLdapName());

        final LdapName cName = new LdapName(aName.getRdns());
        cName.add(new Rdn(LdapEpicsAlarmcfgConfiguration.COMPONENT.getNodeTypeName(), B));
        cName.add(new Rdn(LdapEpicsAlarmcfgConfiguration.RECORD.getNodeTypeName(), C));
        assertEquals(cName, _c.getLdapName());
	}

}
