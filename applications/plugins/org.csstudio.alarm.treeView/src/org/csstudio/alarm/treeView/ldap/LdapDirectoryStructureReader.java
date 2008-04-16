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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.model.AbstractAlarmTreeNode;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.csstudio.alarm.treeView.preferences.PreferenceConstants;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * This job reads the complete directory structure and adds missing nodes
 * to the tree. It is intended to be run after the {@link LdapDirectoryReader}
 * job. This job adds to the tree nodes which do not contain any eren entries
 * and reads the attributes of all nodes.
 * 
 * @author Joerg Rathlev
 */
public class LdapDirectoryStructureReader extends Job {

	/**
	 * The logger that is used by this class.
	 */
	private static final CentralLogger LOG = CentralLogger.getInstance();
	
	/**
	 * The root below which the direcoty is searched.
	 */
	// TODO: refactor (code duplication, see LdapDirectoryReader)
	private static final String ALARM_ROOT = "ou=EpicsAlarmCfg";

	/**
	 * The directory that is searched.
	 */
	// TODO: use LDAP connection from Engine?
	private DirContext _directory;

	/**
	 * The root of the tree on which this job works.
	 */
	private final SubtreeNode _treeRoot;
	
	/**
	 * The facility names which are read from the directory.
	 */
	private String[] _facilityNames;

	
	/**
	 * Creates a new Directory Structure Reader.
	 * 
	 * @param treeRoot
	 *            the root node of the tree which will be updated by this job.
	 */
	public LdapDirectoryStructureReader(final SubtreeNode treeRoot) {
		super("LDAP Directory Structure Reader");
		_treeRoot = treeRoot;
		
		Preferences prefs = AlarmTreePlugin.getDefault().getPluginPreferences();
		_facilityNames = prefs.getString(PreferenceConstants.FACILITIES).split(";");
	}
	
	
	/**
	 * Initializes Connection with connection parameters set up in the
	 * preferences.
	 * @throws NamingException if an LDAP error occurs.
	 */
	// TODO: refactor (code duplication, see LdapDirectoryReader)
	private void initializeConnection() throws NamingException {
		Hashtable<String, String> env = environmentFromPreferences();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		_directory = new InitialDirContext(env);
	}
	
	
	/**
	 * Initializes the LDAP environment (URL, username and password) from this
	 * plug-in's preferences.
	 * @return the LDAP environment.
	 */
	// TODO: refactor (code duplication, see LdapDirectoryReader)
	private Hashtable<String, String> environmentFromPreferences() {
		Preferences prefs = AlarmTreePlugin.getDefault().getPluginPreferences();
		String url = prefs.getString(PreferenceConstants.LDAP_URL);
		String user = prefs.getString(PreferenceConstants.LDAP_USER);
		String password = prefs.getString(PreferenceConstants.LDAP_PASSWORD);
		
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.PROVIDER_URL, url);
		env.put(Context.SECURITY_PRINCIPAL, user);
		env.put(Context.SECURITY_CREDENTIALS, password);
		return env;
	}
	
	
	/**
	 * Closes the connection to the directory.
	 */
	// TODO: refactor (code duplication, see LdapDirectoryReader)
	private void closeConnection() {
		if (_directory != null) {
			try {
				_directory.close();
				_directory = null;
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final IStatus run(final IProgressMonitor monitor) {
		monitor.beginTask("Updating Alarm Tree structure", IProgressMonitor.UNKNOWN);
		try {
			long startTime = System.currentTimeMillis();
			initializeConnection();
			for (String facility : _facilityNames) {
				updateStructureOfSubTree(facility);
			}
			closeConnection();
			long endTime = System.currentTimeMillis();
			LOG.debug(this, "Directory structure reader time: " + (endTime-startTime) + "ms");
		} catch (NamingException e) {
			return new Status(IStatus.ERROR, AlarmTreePlugin.PLUGIN_ID,
					IStatus.ERROR, "Error reading from directory: " + e.getMessage(), e);
		} finally {
			monitor.done();
		}
		return Status.OK_STATUS;
	}


	/**
	 * Reads the structure of the subtree for the given facility.
	 * 
	 * @param facility
	 *            the facility.
	 */
	private void updateStructureOfSubTree(final String facility) {
		String searchRootDN = "efan=" + facility + "," + ALARM_ROOT;
		
		SubtreeNode efanNode = findNode(_treeRoot, "efan=" + facility);
		updateStructureOfSubTreeInternal(searchRootDN, efanNode);
	}


	/**
	 * Recursively updates the structure of the given tree.
	 * 
	 * @param rootDN
	 *            the root DN.
	 * @param tree
	 *            the tree.
	 */
	private void updateStructureOfSubTreeInternal(final String rootDN,
			final SubtreeNode tree) {
		try {
			NamingEnumeration<NameClassPair> results = _directory.list(rootDN);
			while (results.hasMore()) {
				NameClassPair entry = results.next();
				String relativeName = LdapNameUtils.removeQuotes(entry.getName());
				String fullName = relativeName + "," + rootDN;
				
				// The update job readas only structural nodes, no eren nodes.
				if (!fullName.startsWith("eren=")) {
					SubtreeNode node = updateNode(tree, relativeName, fullName);
					updateStructureOfSubTreeInternal(fullName, node);
				}
			}
		} catch (NamingException e) {
			LOG.error(this,
					"Error getting list of objects from LDAP directory " +
					"for rootDN=" + rootDN, e);
		}
	}


	/**
	 * Updates and returns the node with the given name.
	 * 
	 * @param tree
	 *            the tree in which the node is located.
	 * @param relativeName
	 *            the name of the node, relative to <code>tree</code>.
	 * @param fullName
	 *            the full name of the node.
	 * @return the updated node.
	 */
	private SubtreeNode updateNode(final SubtreeNode tree,
			final String relativeName, final String fullName) {
		SubtreeNode node = findNode(tree, relativeName);
		try {
			Attributes attrs = _directory.getAttributes(fullName);
			setEpicsAttributes(node, attrs);
		} catch (NamingException e) {
			LOG.error(this, "Could not read attributes for node " + fullName, e);
		}
		return node;
	}
	

	/**
	 * Sets the EPICS attributes of the given node based on the given
	 * attributes.
	 * 
	 * @param node
	 *            the node.
	 * @param attrs
	 *            the attributes.
	 * @throws NamingException
	 *             if an LDAP error occurs.
	 */
	// TODO: refactor (code duplication, see LdapDirectoryReader)
	private void setEpicsAttributes(final AbstractAlarmTreeNode node,
			final Attributes attrs) throws NamingException {
		Attribute displayAttr = attrs.get("epicsCssAlarmDisplay");
		if (displayAttr != null) {
			String display = (String) displayAttr.get();
			if (display != null) {
				node.setCssAlarmDisplay(display);
			}
		}
		
		Attribute helpPageAttr = attrs.get("epicsHelpPage");
		if (helpPageAttr != null) {
			String helpPage = (String) helpPageAttr.get();
			if (helpPage != null && helpPage.matches("^http://.+")) {
				try {
					node.setHelpPage(new URL(helpPage));
				} catch (MalformedURLException e) {
					LOG.warn(this, "epicsHelpPage attribute for node "
							+ node + " contains a malformed URL");
				}
			}
		}
		
		Attribute helpGuidanceAttr = attrs.get("epicsHelpGuidance");
		if (helpGuidanceAttr != null) {
			String helpGuidance = (String) helpGuidanceAttr.get();
			if (helpGuidance != null) {
				node.setHelpGuidance(helpGuidance);
			}
		}
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
	// TODO: refactor (code duplication, see LdapDirectoryReader)
	private SubtreeNode findNode(final SubtreeNode root, final String name) {
		SubtreeNode directParent;
		String parentName = LdapNameUtils.parentName(name);
		if (parentName != null) {
			// The node is not directly below given root. Search recursively.
			directParent = findNode(root, parentName);
		} else {
			directParent = root;
		}
		String simpleName = LdapNameUtils.simpleName(name);
		SubtreeNode result = (SubtreeNode) directParent.getChild(simpleName);
		if (result == null) {
			result = new SubtreeNode(directParent, simpleName);
		}
		return result;
	}
}
