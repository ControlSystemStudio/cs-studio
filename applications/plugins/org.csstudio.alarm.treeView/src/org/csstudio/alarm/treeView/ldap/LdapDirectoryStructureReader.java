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
import java.util.Collections;

import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.model.AbstractAlarmTreeNode;
import org.csstudio.alarm.treeView.model.ObjectClass;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.csstudio.alarm.treeView.preferences.PreferenceConstants;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.engine.Engine;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IPreferencesService;

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
		
		IPreferencesService prefs = Platform.getPreferencesService();
		String facilitiesPref = prefs.getString(AlarmTreePlugin.PLUGIN_ID,
				PreferenceConstants.FACILITIES, "", null);
		_facilityNames = facilitiesPref.split(";");
	}
	
	
	/**
	 * Gets the directory context from the LDAP engine.
	 */
	private void initializeDirectoryContext() {
		_directory = Engine.getInstance().getLdapDirContext();
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final IStatus run(final IProgressMonitor monitor) {
		monitor.beginTask("Updating Alarm Tree structure", IProgressMonitor.UNKNOWN);
		try {
			long startTime = System.currentTimeMillis();
			initializeDirectoryContext();
			for (String facility : _facilityNames) {
				updateStructureOfFacility(facility);
			}
			long endTime = System.currentTimeMillis();
			LOG.debug(this, "Directory structure reader time: " + (endTime-startTime) + "ms");
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
	private void updateStructureOfFacility(final String facility) {
		try {
			LdapName efanName = new LdapName(Collections.singletonList(
					new Rdn("efan", facility)));
			LdapName fullEfanName = (LdapName) new LdapName(ALARM_ROOT)
					.addAll(efanName);
			
			SubtreeNode efanNode = updateNode(_treeRoot, efanName, fullEfanName);
			updateStructureOfSubTree(fullEfanName, efanNode);
		} catch (InvalidNameException e) {
			LOG.error(this, "Error when updating facility subtree in Alarm Tree", e);
		}
	}


	/**
	 * Recursively updates the structure of the given tree.
	 * 
	 * @param treeName
	 *            the LDAP name of the root node of the tree.
	 * @param tree
	 *            the tree.
	 */
	private void updateStructureOfSubTree(final LdapName treeName,
			final SubtreeNode tree) {
		try {
			NameParser nameParser = _directory.getNameParser(treeName);
			NamingEnumeration<NameClassPair> results = _directory.list(treeName);
			while (results.hasMore()) {
				NameClassPair entry = results.next();
				Name cname = new CompositeName(entry.getName());
				LdapName entryName = (LdapName) nameParser.parse(cname.get(0));
				
				// The update job readas only structural nodes, no eren nodes.
				if (LdapNameUtils.objectClass(entryName) != ObjectClass.RECORD) {
					LdapName fullEntryName = (LdapName) ((LdapName) entryName.clone()).addAll(0, treeName);
					SubtreeNode node = updateNode(tree, entryName, fullEntryName);
					updateStructureOfSubTree(fullEntryName, node);
				}
			}
		} catch (NamingException e) {
			LOG.error(this,
					"Error getting list of objects from LDAP directory " +
					"for rootDN=" + treeName, e);
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
			final LdapName relativeName, final LdapName fullName) {
		SubtreeNode node = TreeBuilder.findCreateSubtreeNode(tree, relativeName);
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
		Attribute alarmDisplayAttr = attrs.get("epicsCssAlarmDisplay");
		if (alarmDisplayAttr != null) {
			String display = (String) alarmDisplayAttr.get();
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
		
		Attribute displayAttr = attrs.get("epicsCssDisplay");
		if (displayAttr != null) {
			String display = (String) displayAttr.get();
			if (display != null) {
				node.setCssDisplay(display);
			}
		}
		
		Attribute chartAttr = attrs.get("epicsCssStripChart");
		if (chartAttr != null) {
			String chart = (String) chartAttr.get();
			if (chart != null) {
				node.setCssStripChart(chart);
			}
		}
	}
}
