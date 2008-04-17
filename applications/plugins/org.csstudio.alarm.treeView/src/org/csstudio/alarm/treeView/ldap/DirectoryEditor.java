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

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;

import org.csstudio.alarm.treeView.model.IAlarmTreeNode;
import org.csstudio.alarm.treeView.model.ProcessVariableNode;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.engine.Engine;

/**
 * Editor for the alarm tree in the LDAP directory. The methods of this class
 * update the LDAP directory and also the tree model.
 * 
 * @author Joerg Rathlev
 */
public final class DirectoryEditor {

	/**
	 * The root below which the direcoty is edited.
	 */
	// TODO: refactor (code duplication, see LdapDirectoryReader)
	private static final String ALARM_ROOT = "ou=EpicsAlarmCfg";

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
		String name;
		if (node instanceof ProcessVariableNode) {
			name = "eren=" + node.getName() + ","
				+ ((SubtreeNode) node.getParent()).getDirectoryName();
		} else {
			name = ((SubtreeNode) node).getDirectoryName();
		}
		name += "," + ALARM_ROOT;
		delete(name);
	}
	
	
	/**
	 * Deletes the object with the given name from the directory.
	 * 
	 * @param name
	 *            the object's name.
	 * @throws DirectoryEditException
	 *             if the entry could not be deleted.
	 */
	private static void delete(final String name)
			throws DirectoryEditException {
		try {
			LOG.debug(DirectoryEditor.class, "Unbinding " + name);
			_directory.unbind(name);
		} catch (NamingException e) {
			LOG.error(DirectoryEditor.class,
					"Error unbinding directory entry", e);
			throw new DirectoryEditException(e.getMessage(), e);
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
	 * @return the process variable node representing the new entry.
	 * @throws DirectoryEditException if the entry could not be created.
	 */
	public static ProcessVariableNode createProcessVariableRecord(
			final SubtreeNode parent, final String recordName)
			throws DirectoryEditException {
		String parentName = parent.getDirectoryName();
		String erenName = "eren=" + recordName + "," + parentName + "," + ALARM_ROOT;
		Attributes attrs = attributesForRecord(recordName);
		try {
			LOG.debug(DirectoryEditor.class,
					"Creating entry " + erenName + " with attributes " + attrs);
			_directory.bind(erenName, null, attrs);
		} catch (NamingException e) {
			LOG.error(DirectoryEditor.class,
					"Error creating directory entry", e);
			throw new DirectoryEditException(e.getMessage(), e);
		}
		return new ProcessVariableNode(parent, recordName);
	}
	
	
	/**
	 * Creates an entry for a component (ecom) in the directory below the given
	 * parent.
	 * 
	 * @param parent
	 *            the parent node.
	 * @param componentName
	 *            the name of the component.
	 * @return the subtree node representing the new entry.
	 * @throws DirectoryEditException
	 *             if the entry could not be created.
	 */
	public static SubtreeNode createComponent(final SubtreeNode parent,
			final String componentName) throws DirectoryEditException {
		String parentName = parent.getDirectoryName();
		String ecomName = "ecom=" + componentName + "," + parentName + "," + ALARM_ROOT;
		Attributes attrs = attributesForComponent(componentName);
		try {
			LOG.debug(DirectoryEditor.class,
					"Creating entry " + ecomName + " with attributes " + attrs);
			_directory.bind(ecomName, null, attrs);
		} catch (NamingException e) {
			LOG.error(DirectoryEditor.class,
					"Error creating directory entry", e);
			throw new DirectoryEditException(e.getMessage(), e);
		}
		SubtreeNode node = new SubtreeNode(parent, componentName);
		node.setObjectClass("ecom");
		return node;
	}
	

	/**
	 * Returns the attributes for a new component with the given name.
	 * 
	 * @param componentName
	 *            the name of the component.
	 * @return the attributes.
	 */
	private static Attributes attributesForComponent(final String componentName) {
		BasicAttributes result = new BasicAttributes();
		result.put("objectClass", "epicsComponent");
		result.put("ecom", componentName);
		return result;
	}


	/**
	 * Returns the attributes for a new record with the given name.
	 * 
	 * @param recordName
	 *            the name of the record.
	 * @return the attributes for the new record.
	 */
	private static Attributes attributesForRecord(final String recordName) {
		BasicAttributes result = new BasicAttributes();
		result.put("objectClass", "epicsRecord");
		result.put("eren", recordName);
		return result;
	}
}
