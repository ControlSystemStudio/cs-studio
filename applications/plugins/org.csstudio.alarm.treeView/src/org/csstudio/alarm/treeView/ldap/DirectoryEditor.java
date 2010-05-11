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

import java.net.URL;
import java.util.Set;

import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.SizeLimitExceededException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.csstudio.alarm.service.declaration.AlarmTreeLdapConstants;
import org.csstudio.alarm.service.declaration.LdapEpicsAlarmCfgObjectClass;
import org.csstudio.alarm.treeView.model.AbstractAlarmTreeNode;
import org.csstudio.alarm.treeView.model.AlarmTreeNodePropertyId;
import org.csstudio.alarm.treeView.model.IAlarmTreeNode;
import org.csstudio.alarm.treeView.model.ProcessVariableNode;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.LdapFieldsAndAttributes;
import org.csstudio.utility.ldap.engine.Engine;

/**
 * Editor for the alarm tree in the LDAP directory. The methods of this class
 * update the LDAP directory and also the tree model.
 *
 * @author Joerg Rathlev
 */
public final class DirectoryEditor {

	private static final String CONTROLS_ROOT = "ou=EpicsControls";

	/**
	 * The logger that is used by this class.
	 */
	private static final CentralLogger LOG = CentralLogger.getInstance();

	/**
	 * The directory that is searched.
	 */
	private static DirContext _directory;


	static {
		_directory = Engine.getInstance().getLdapDirContext();
	}


	/**
	 * Private constructor.
	 */
	private DirectoryEditor() {
	}


	/**
	 * Deletes the given node from the directory.
	 *
	 * @param node
	 *            the node to delete.
	 * @throws DirectoryEditException
	 *             if the node could not be deleted.
	 */
	public static void delete(final IAlarmTreeNode node)
			throws DirectoryEditException {
		final LdapName name = fullLdapName(node);
		try {
			LOG.debug(DirectoryEditor.class, "Unbinding " + name);
			_directory.unbind(name);
			node.getParent().removeChild(node);
		} catch (final NamingException e) {
			LOG.error(DirectoryEditor.class,
					"Error unbinding directory entry", e);
			throw new DirectoryEditException(e.getMessage(), e);
		}
	}


	/**
	 * Modifies the help guidance attribute of the given node in the directory.
	 *
	 * @param node
	 *            the node.
	 * @param value
	 *            the new value for the attribute. Use <code>null</code> to
	 *            remove the attribute.
	 * @throws DirectoryEditException
	 *             if the attribute could not be modified.
	 */
	public static void modifyHelpGuidance(final AbstractAlarmTreeNode node,
			final String value) throws DirectoryEditException {
		modifyAttribute(node, "epicsHelpGuidance", value);
		node.setHelpGuidance(value);
	}


	/**
	 * Modifies the help page attribute of the given node in the directory.
	 *
	 * @param node
	 *            the node.
	 * @param value
	 *            the new value for the attribute. Use <code>null</code> to
	 *            remove the attribute.
	 * @throws DirectoryEditException
	 *             if the attribute could not be modified.
	 */
	public static void modifyHelpPage(final AbstractAlarmTreeNode node,
			final URL value) throws DirectoryEditException {
		modifyAttribute(node, "epicsHelpPage", value.toString());
		node.setHelpPage(value);
	}


	/**
	 * Modifies the CSS alarm display attribute of the given node in the
	 * directory.
	 *
	 * @param node
	 *            the node.
	 * @param value
	 *            the new value for the attribute. Use <code>null</code> to
	 *            remove the attribute.
	 * @throws DirectoryEditException
	 *             if the attribute could not be modified.
	 */
	public static void modifyCssAlarmDisplay(final AbstractAlarmTreeNode node,
			final String value) throws DirectoryEditException {
		modifyAttribute(node, "epicsCssAlarmDisplay", value);
		node.setCssAlarmDisplay(value);
	}


	/**
	 * Modifies the CSS display attribute of the given node in the directory.
	 *
	 * @param node
	 *            the node.
	 * @param value
	 *            the new value for the attribute. Use <code>null</code> to
	 *            remove the attribute.
	 * @throws DirectoryEditException
	 *             if the attribute could not be modified.
	 */
	public static void modifyCssDisplay(final AbstractAlarmTreeNode node,
			final String value) throws DirectoryEditException {
		modifyAttribute(node, "epicsCssDisplay", value);
		node.setCssDisplay(value);
	}


	/**
	 * Modifies the CSS strip chart attribute of the given node in the
	 * directory.
	 *
	 * @param node
	 *            the node.
	 * @param value
	 *            the new value for the attribute. Use <code>null</code> to
	 *            remove the attribute.
	 * @throws DirectoryEditException
	 *             if the attribute could not be modified.
	 */
	public static void modifyCssStripChart(final AbstractAlarmTreeNode node,
			final String value) throws DirectoryEditException {
		modifyAttribute(node, "epicsCssStripChart", value);
		node.setCssStripChart(value);
	}


	/**
	 * Modifies an attribute of a directory entry.
	 *
	 * @param node
	 *            the node.
	 * @param attribute
	 *            the id of the attribute to modify.
	 * @param value
	 *            the new attribute value. Use <code>null</code> to remove the
	 *            attribute.
	 * @throws DirectoryEditException
	 *             if the attribute could not be modified.
	 */
	private static void modifyAttribute(final IAlarmTreeNode node,
			final String attribute, final String value)
			throws DirectoryEditException {
		final LdapName name = fullLdapName(node);
		final Attribute attr = new BasicAttribute(attribute, value);
		final int op = value == null
				? DirContext.REMOVE_ATTRIBUTE : DirContext.REPLACE_ATTRIBUTE;
		final ModificationItem[] mods = new ModificationItem[] {
				new ModificationItem(op, attr) };
		try {
			LOG.debug(DirectoryEditor.class, name + ": " + mods[0]);
			_directory.modifyAttributes(name, mods);
		} catch (final NamingException e) {
			LOG.error(DirectoryEditor.class, "Failed: " + name + ": " + mods[0], e);
			throw new DirectoryEditException(e.getMessage(), e);
		}
	}

	/**
	 * Renames a node.
	 *
	 * @param node
	 *            the node.
	 * @param newName
	 *            the new name. This should be just the simple name, not an LDAP
	 *            name.
	 * @throws DirectoryEditException
	 *             if an error occurs.
	 */
	public static void rename(final IAlarmTreeNode node, final String newName)
			throws DirectoryEditException {
		try {
			final LdapName oldLdapName = fullLdapName(node);
			final String type = oldLdapName.getRdn(oldLdapName.size() - 1).getType();
			Rdn newRdn;
			newRdn = new Rdn(type, newName);
			final LdapName newLdapName = (LdapName) oldLdapName.getPrefix(oldLdapName.size() - 1);
			newLdapName.add(newRdn);
			_directory.rename(oldLdapName, newLdapName);
			node.setName(newName);
		} catch (final NamingException e) {
			LOG.error(DirectoryEditor.class, "Error renaming node", e);
			throw new DirectoryEditException(e.getMessage(), e);
		}
	}

	/**
	 * Moves a node into a new subtree node. If the node is a subtree node, the
	 * whole subtree will be moved, including its children.
	 *
	 * @param node
	 *            the node.
	 * @param target
	 *            the target node which will become the new parent of the node.
	 * @throws DirectoryEditException
	 *             if an error occurs.
	 */
	public static void moveNode(final IAlarmTreeNode node, final SubtreeNode target)
			throws DirectoryEditException {
		/*
		 * Note: I tried to use _directory.rename(...) here, but that failed
		 * with an "LDAP: error code 50 - Insufficient Access Rights". So for
		 * now, this code uses copy-and-delete to move the node.
		 */
		copyNode(node, target);
		deleteRecursively(node);
	}

	/**
	 * Recursively deletes a node and all of its children.
	 *
	 * @param node
	 *            the node.
	 * @throws DirectoryEditException
	 *             if an error occurs.
	 */
	private static void deleteRecursively(final IAlarmTreeNode node)
			throws DirectoryEditException {
		if (node instanceof SubtreeNode) {
			deleteChildren((SubtreeNode) node);
		}
		delete(node);
	}

	/**
	 * Deletes the children of a subtree node (recursively).
	 *
	 * @param node
	 *            the node.
	 * @throws DirectoryEditException
	 *             if an error occurs.
	 */
	private static void deleteChildren(final SubtreeNode node)
			throws DirectoryEditException {
		for (final IAlarmTreeNode child : node.getChildren()) {
			deleteRecursively(child);
		}
	}


	/**
	 * Creates a copy of a node under a new subtree node. If the node to be
	 * copied is a subtree node, all of its children will be copied into the new
	 * node, too.
	 *
	 * @param node
	 *            the original node.
	 * @param target
	 *            the target into which the node will be copied.
	 * @throws DirectoryEditException
	 *             if an error occurs.
	 */
	public static void copyNode(final IAlarmTreeNode node, final SubtreeNode target)
			throws DirectoryEditException {
		if (node instanceof ProcessVariableNode) {
			copyProcessVariableNode((ProcessVariableNode) node, target);
		} else {
			copySubtree((SubtreeNode) node, target);
		}
	}

	/**
	 * Creates a subtree node which is a copy of the given subtree node. All
	 * children of the original subtree node will be copied into the new node.
	 *
	 * @param node
	 *            the original.
	 * @param target
	 *            the target.
	 * @throws DirectoryEditException
	 *             if an error occurs.
	 */
	private static void copySubtree(final SubtreeNode node, final SubtreeNode target)
			throws DirectoryEditException {
		copyDirectoryEntry(node, target);
		final SubtreeNode copy =
		    new SubtreeNode.Builder(node.getName(), node.getObjectClass()).setParent(target).build();
		copyProperties(node, copy);
		copyChildren(node, copy);
	}

	/**
	 * Copies the child nodes of the given subtree node into the target node.
	 * This method works recursively, the children of the copied nodes are
	 * copied as well.
	 *
	 * @param node
	 *            the node which contains the original children.
	 * @param target
	 *            the target node into which the children will be copied.
	 */
	private static void copyChildren(final SubtreeNode node, final SubtreeNode target)
			throws DirectoryEditException {
		for (final IAlarmTreeNode child : node.getChildren()) {
			copyNode(child, target);
		}
	}


	/**
	 * Creates a new process variable node which is a copy of the given process
	 * variable node.
	 *
	 * @param node
	 *            the original.
	 * @param target
	 *            the target.
	 * @throws DirectoryEditException
	 *             if an error occurs.
	 */
	private static void copyProcessVariableNode(final ProcessVariableNode node,
			final SubtreeNode target) throws DirectoryEditException {
		copyDirectoryEntry(node, target);
		final ProcessVariableNode copy = new ProcessVariableNode.Builder(node.getName()).setParent(target).build();
		copy.updateAlarm(node.getAlarm());
		copy.setHighestUnacknowledgedAlarm(node.getHighestUnacknowledgedAlarm());
		copyProperties(node, copy);
	}

	/**
	 * Copies an entry in the LDAP directory. All of the entries attributes will
	 * be copied into the new entry.
	 *
	 * @param node
	 *            the original node which will be copied.
	 * @param target
	 *            the target node which will be the parent of the copy.
	 * @throws DirectoryEditException
	 *             if an error occurs.
	 */
	private static void copyDirectoryEntry(final IAlarmTreeNode node,
			final SubtreeNode target) throws DirectoryEditException {
		try {
			Attributes attributes = _directory.getAttributes(fullLdapName(node));
			attributes = (Attributes) attributes.clone();
			final LdapName newName = (LdapName) fullLdapName(target).add(
					new Rdn(node.getObjectClass().getRdnType(), node
							.getName()));
			createEntry(newName, attributes);
		} catch (final NamingException e) {
			LOG.error(DirectoryEditor.class,
					"Error creating directory entry", e);
			throw new DirectoryEditException(e.getMessage(), e);
		}
	}

	/**
	 * Copies the properties from one node to another node.
	 *
	 * @param source
	 *            the source.
	 * @param destination
	 *            the destination.
	 */
	private static void copyProperties(final AbstractAlarmTreeNode source,
			final AbstractAlarmTreeNode destination) {
		for (final AlarmTreeNodePropertyId id : AlarmTreeNodePropertyId.values()) {
			final String value = source.getOwnProperty(id);
			if (value != null) {
				destination.setProperty(id, value);
			}
		}
	}


	/**
	 * Creates an entry for a process variable record (eren) in the directory
	 * below the given parent.
	 *
	 * @param parent
	 *            the parent node.
	 * @param recordName
	 *            the name of the process variable record.
	 * @throws DirectoryEditException if the entry could not be created.
	 */
	public static void createProcessVariableRecord(
			final SubtreeNode parent, final String recordName)
			throws DirectoryEditException {
		try {
			final Rdn rdn = new Rdn(LdapEpicsAlarmCfgObjectClass.RECORD.getRdnType(), recordName);
			final LdapName fullName = (LdapName) ((LdapName) fullLdapName(parent).clone()).add(rdn);
			Attributes attrs = baseAttributesForEntry(LdapEpicsAlarmCfgObjectClass.RECORD, rdn);
			attrs = addAlarmAttributes(attrs, recordName);
			createEntry(fullName, attrs);
			final ProcessVariableNode node = new ProcessVariableNode.Builder(recordName).setParent(parent).build();
			AlarmTreeNodeModifier.setAlarmState(node, attrs);
		} catch (final NamingException e) {
			LOG.error(DirectoryEditor.class,
					"Error creating directory entry", e);
			throw new DirectoryEditException(e.getMessage(), e);
		}
	}


	/**
	 * Creates an entry for a component (ecom) in the directory below the given
	 * parent.
	 *
	 * @param parent
	 *            the parent node.
	 * @param componentName
	 *            the name of the component.
	 * @throws DirectoryEditException
	 *             if the entry could not be created.
	 */
	public static void createComponent(final SubtreeNode parent,
	                                   final String componentName) throws DirectoryEditException {
		final Set<LdapEpicsAlarmCfgObjectClass> oclasses = parent.getRecommendedChildSubtreeClasses();
		if (oclasses.isEmpty()) {
			createEntry(fullLdapName(parent), componentName, LdapEpicsAlarmCfgObjectClass.RECORD);
		}

		final LdapEpicsAlarmCfgObjectClass oclass = LdapEpicsAlarmCfgObjectClass.COMPONENT;
		new SubtreeNode.Builder(componentName, oclass).setParent(parent).build();
	}


	/**
	 * Creates a new directory entry.
	 *
	 * @param parentName
	 *            the full name of the parent entry.
	 * @param name
	 *            the name of the entry to be created.
	 * @param objectClass
	 *            the object class of the entry to be created.
	 * @throws DirectoryEditException
	 *             if the entry could not be created.
	 */
	private static void createEntry(final LdapName parentName,
	                                final String name,
	                                final LdapEpicsAlarmCfgObjectClass objectClass) throws DirectoryEditException {
		try {
			final Rdn rdn = new Rdn(objectClass.getRdnType(), name);
			final LdapName fullName = (LdapName) ((LdapName) parentName.clone()).add(rdn);
			final Attributes attrs = baseAttributesForEntry(objectClass, rdn);
			createEntry(fullName, attrs);
		} catch (final NamingException e) {
			LOG.error(DirectoryEditor.class,
					"Error creating directory entry", e);
			throw new DirectoryEditException(e.getMessage(), e);
		}
	}

	/**
	 * Creates a new directory entry.
	 *
	 * @param name
	 *            the full name of the new entry.
	 * @param attributes
	 *            the attributes of the entry.
	 * @throws DirectoryEditException
	 *             if the entry could not be created.
	 */
	private static void createEntry(final LdapName name,
	                                final Attributes attributes) throws DirectoryEditException {
		try {
			LOG.debug(DirectoryEditor.class,
					"Creating entry " + name + " with attributes " + attributes);
			_directory.bind(name, null, attributes);
		} catch (final NamingException e) {
			LOG.error(DirectoryEditor.class,
					"Error creating directory entry", e);
			throw new DirectoryEditException(e.getMessage(), e);
		}
	}

	/**
	 * Returns the full name of the given node in the directory.
	 *
	 * @param node
	 *            the node.
	 * @return the full name of the node in the directory.
	 * @throws DirectoryEditException
	 *             if the node has an invalid name.
	 */
	private static LdapName fullLdapName(final IAlarmTreeNode node)
			throws DirectoryEditException {
		try {
			final LdapName name = new LdapName("");
			if (node instanceof ProcessVariableNode) {
				name.addAll(node.getParent().getLdapName());
				name.add(new Rdn(AlarmTreeLdapConstants.EREN_FIELD_NAME, node.getName()));
			} else {
				name.addAll(((SubtreeNode) node).getLdapName());
			}
			return name;
		} catch (final InvalidNameException e) {
			throw new DirectoryEditException(e.getMessage(), e);
		}
	}

	/**
	 * Returns the base attributes (name, objectclass and EPICS type) for a new
	 * entry with the given object class and name.
	 *
	 * @param objectClass
	 *            the object class of the new entry.
	 * @param rdn
	 *            the relative name of the entry.
	 * @return the attributes for the new entry.
	 */
	private static Attributes baseAttributesForEntry(final LdapEpicsAlarmCfgObjectClass objectClass,
			final Rdn rdn) {
		final Attributes result = rdn.toAttributes();
		result.put(LdapFieldsAndAttributes.ATTR_FIELD_OBJECT_CLASS, objectClass.getDescription());
		result.put("epicsCssType", objectClass.getCssType());
		return result;
	}

	/**
	 * Reads the alarm attributes from the EpicsControls path of the LDAP
	 * directory and adds them to the target attributes.
	 *
	 * @param target
	 *            the attributes that will be extended.
	 * @param recordName
	 *            the name of the record.
	 * @throws NamingException
	 */
	private static Attributes addAlarmAttributes(final Attributes target,
	                                             final String recordName) throws NamingException {
		final Name searchRootName = new LdapName(CONTROLS_ROOT);

		final SearchControls ctrl = new SearchControls();
		ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
		ctrl.setCountLimit(0); // unlimited

		final NamingEnumeration<SearchResult> searchResults =
			_directory.search(searchRootName, "eren=" + recordName, ctrl);
		try {
			if (searchResults.hasMore()) {
				final SearchResult result = searchResults.next();
				final Attributes foundAttributes = result.getAttributes();
				final String[] attrs = { "epicsAlarmSeverity", "epicsAlarmTimeStamp",
						"epicsAlarmHighUnAckn" };
				for (final String attr : attrs) {
					final Attribute foundAttribute = foundAttributes.get(attr);
					if (foundAttribute != null) {
						target.put((Attribute) foundAttribute.clone());
					}
				}
			}
		} catch (final SizeLimitExceededException e) {
			LOG.error(DirectoryEditor.class, "Size limit exceeded while reading search results: "
					+ e.getExplanation(), e);
		} finally {
			try {
				searchResults.close();
			} catch (final NamingException e) {
				LOG.warn(DirectoryEditor.class, "Error closing search results", e);
			}
		}
		return target;
	}
}
