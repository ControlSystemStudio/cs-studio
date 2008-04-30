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

import org.csstudio.alarm.treeView.model.SubtreeNode;

/**
 * Utility class for building the tree model. The methods of this class only
 * create tree itemsin the object model of the tree. No LDAP entries are created
 * or modified by this class.
 * 
 * @author Joerg Rathlev
 */
final class TreeBuilder {

	/**
	 * Private constructor.
	 */
	private TreeBuilder() {
	}

	
	/**
	 * Finds a node with the given name in the given tree. If the node does
	 * not exist yet, it is created.
	 * 
	 * @param root
	 *            the root node of the tree to search.
	 * @param name
	 *            the LDAP name of the node to search.
	 * @return the node.
	 */
	static SubtreeNode findCreateSubtreeNode(final SubtreeNode root,
			final String name) {
		SubtreeNode directParent = findCreateParentNode(root, name);
		String simpleName = LdapNameUtils.simpleName(name);
		SubtreeNode result = (SubtreeNode) directParent.getChild(simpleName);
		if (result == null) {
			result = new SubtreeNode(directParent, simpleName, LdapNameUtils.objectClass(name));
		}
		return result;
	}
	
	
	/**
	 * Finds the parent node of the node with the specified name. If the parent
	 * node does not exist, it is created.
	 * 
	 * @param root
	 *            the root node of the tree which is searched.
	 * @param relativeName
	 *            the relative LDAP name of the node whose parent is to be
	 *            found.
	 * @return the parent node of the node with the specified name.
	 */
	static SubtreeNode findCreateParentNode(final SubtreeNode root,
			final String relativeName) {
		String parentName = LdapNameUtils.parentName(relativeName);
		if (parentName != null) {
			SubtreeNode parent = findCreateSubtreeNode(root, parentName);
			return parent;
		} else {
			// there is no parent node, so return the root node
			return root;
		}
	}

}
