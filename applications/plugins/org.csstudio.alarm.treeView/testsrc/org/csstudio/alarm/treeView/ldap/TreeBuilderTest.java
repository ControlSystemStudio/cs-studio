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

import org.csstudio.alarm.treeView.model.ProcessVariableNode;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import junit.framework.TestCase;

/**
 * @author Joerg Rathlev
 */
public class TreeBuilderTest {

	private SubtreeNode _tree;
	private SubtreeNode _a;
	private SubtreeNode _b;
	private ProcessVariableNode _pv1;
	
	/**
	 * <p>Initializes a tree for testing. The tree will have the following
	 * structure:</p>
	 * 
	 * <pre>
	 * root           [SubtreeNode]
	 *   +--a         [SubtreeNode]
	 *      +--b      [SubtreeNode]
	 *      +--pv:1   [ProcessVariableNode]
	 * </pre>
	 */
	@Before
	public void setUp() throws Exception {
		_tree = new SubtreeNode("root");
		_a = new SubtreeNode(_tree, "a");
		_b = new SubtreeNode(_a, "b");
		_pv1 = new ProcessVariableNode(_a, "pv:1");
	}
	
	@Test
	public void testFindExistingSubtreeNode() {
		String name = "x=b,y=a";
		SubtreeNode node = TreeBuilder.findCreateSubtreeNode(_tree, name);
		assertSame(_b, node);
	}
	
	@Test
	public void testCreateNewSubtreeNode() throws Exception {
		String name = "x=c,y=a";
		SubtreeNode node = TreeBuilder.findCreateSubtreeNode(_tree, name);
		// _a should now have a child called "c"
		assertTrue(_a.getChild("c") instanceof SubtreeNode);
		assertSame(_a, node.getParent());
	}
	
	@Test
	public void testFindParentOfPv() throws Exception {
		String name = "x=pv:1,y=a";
		SubtreeNode parent = TreeBuilder.findCreateParentNode(_tree, name);
		assertSame(_a, parent);
	}
	
	@Test
	public void testCreateParentForPv() throws Exception {
		String name = "x=pv:2,y=c,z=a";
		SubtreeNode parent = TreeBuilder.findCreateParentNode(_tree, name);
		assertTrue(_a.getChild("c") instanceof SubtreeNode);
		assertSame(_a, parent.getParent());
	}

}
