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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import javax.naming.ldap.LdapName;

import org.csstudio.alarm.treeView.model.ObjectClass;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Joerg Rathlev
 */
public class TreeBuilderTest {

	private SubtreeNode _tree;
	private SubtreeNode _a;
	private SubtreeNode _b;
	private LdapName _nameB;
	
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
		_tree = new SubtreeNode("root");
		_a = new SubtreeNode(_tree, "a", ObjectClass.FACILITY);
		_b = new SubtreeNode(_a, "b", ObjectClass.COMPONENT);
		
		_nameB = new LdapName("ecom=b,efan=a");
	}
	
	@Test
	public void testDirectoryNames() throws Exception {
		assertEquals(new LdapName("efan=a"), _a.getLdapName());
		assertEquals(new LdapName("ecom=b,efan=a"), _b.getLdapName());
	}
	
	@Test
	public void testDirectoryNameOfNodeWithSpecialCharacters() throws Exception {
		LdapName name = new LdapName("ecom=c\\=1\\,2,efan=a");
		SubtreeNode node = TreeBuilder.findCreateSubtreeNode(_tree, name);
		assertEquals(name, node.getLdapName());
	}
	
	@Test
	public void testFindExistingSubtreeNode() throws Exception {
		SubtreeNode node = TreeBuilder.findCreateSubtreeNode(_tree, _nameB);
		assertSame(_b, node);
	}
	
	@Test
	public void testCreateNewSubtreeNode() throws Exception {
		LdapName name = new LdapName("ecom=c,efan=a");
		SubtreeNode node = TreeBuilder.findCreateSubtreeNode(_tree, name);
		assertNotNull(node);
		assertSame(node, _a.getChild("c"));
		assertSame(_a, node.getParent());
		assertEquals(name, node.getLdapName());
	}
	
	@Test
	public void testCreateSubtreeNodeWithSpecialCharacters() throws Exception {
		LdapName name = new LdapName("ecom=c\\=1\\,2,efan=a");
		SubtreeNode node = TreeBuilder.findCreateSubtreeNode(_tree, name);
		assertNotNull(node);
		assertSame(node, _a.getChild("c=1,2"));
		assertSame(_a, node.getParent());
	}
	
	@Test
	public void testFindParentOfPv() throws Exception {
		LdapName name = new LdapName("eren=pv:1,efan=a");
		SubtreeNode parent = TreeBuilder.findCreateParentNode(_tree, name);
		assertSame(_a, parent);
	}
	
	@Test
	public void testFindParentOfPvWithSpecialCharacters() throws Exception {
		LdapName name = new LdapName("eren=pv\\=1\\,2,efan=a");
		SubtreeNode parent = TreeBuilder.findCreateParentNode(_tree, name);
		assertSame(_a, parent);
	}
	
	@Test
	public void testCreateParentForPv() throws Exception {
		LdapName name = new LdapName("efen=pv:2,ecom=c,efan=a");
		SubtreeNode parent = TreeBuilder.findCreateParentNode(_tree, name);
		assertTrue(_a.getChild("c") instanceof SubtreeNode);
		assertSame(_a, parent.getParent());
		assertEquals(new LdapName("ecom=c,efan=a"), parent.getLdapName());
	}
}
