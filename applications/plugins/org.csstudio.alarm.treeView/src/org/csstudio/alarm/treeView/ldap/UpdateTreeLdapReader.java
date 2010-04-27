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

import java.util.Collections;

import javax.naming.CompositeName;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.model.LdapObjectClass;
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
 * to the tree. It is intended to be run after the {@link AlarmTreeBuilder}
 * job. This job adds to the tree nodes which do not contain any eren entries
 * and reads the attributes of all nodes.
 *
 * @author Joerg Rathlev
 */
public class UpdateTreeLdapReader extends Job {

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
	public UpdateTreeLdapReader(final SubtreeNode treeRoot) {
		super("LDAP Directory Structure Reader");
		_treeRoot = treeRoot;

		final IPreferencesService prefs = Platform.getPreferencesService();
		final String facilitiesPref = prefs.getString(AlarmTreePlugin.PLUGIN_ID,
				PreferenceConstants.FACILITIES, "", null);
		if (facilitiesPref.equals("")) {
			_facilityNames = new String[0];
		} else {
			_facilityNames = facilitiesPref.split(";");
		}
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
			final long startTime = System.currentTimeMillis();
			initializeDirectoryContext();
			ensureAtLeastTestFacilityIsShown();
			for (final String facility : _facilityNames) {
				updateStructureOfFacility(facility);
			}
			final long endTime = System.currentTimeMillis();
			LOG.debug(this, "Directory structure reader time: " + (endTime-startTime) + "ms");
		} finally {
			monitor.done();
		}
		return Status.OK_STATUS;
	}


	private void ensureAtLeastTestFacilityIsShown() {
		if (_facilityNames.length == 0) {
			_facilityNames = new String[] { "TEST" };
		}
	}


	/**
	 * Reads the structure of the subtree for the given facility.
	 *
	 * @param facility
	 *            the facility.
	 */
	private void updateStructureOfFacility(final String facility) {
		try {
			final LdapName efanName = new LdapName(Collections.singletonList(
					new Rdn("efan", facility)));
			final LdapName fullEfanName = (LdapName) new LdapName(ALARM_ROOT)
					.addAll(efanName);

			final SubtreeNode efanNode = updateNode(_treeRoot, efanName, fullEfanName);
			updateStructureOfSubTree(fullEfanName, efanNode);
		} catch (final InvalidNameException e) {
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
			final NameParser nameParser = _directory.getNameParser(treeName);
			final NamingEnumeration<NameClassPair> results = _directory.list(treeName);
			while (results.hasMore()) {
				final NameClassPair entry = results.next();
				final Name cname = new CompositeName(entry.getName());
				final LdapName entryName = (LdapName) nameParser.parse(cname.get(0));

				// The update job readas only structural nodes, no eren nodes.
		        final Rdn rdn = entryName.getRdn(entryName.size() - 1);
				if (!LdapObjectClass.getObjectClassByRdn(rdn.getType()).equals(LdapObjectClass.RECORD)) {
				    final LdapName fullEntryName = (LdapName) ((LdapName) entryName.clone()).addAll(0, treeName);
					final SubtreeNode node = updateNode(tree, entryName, fullEntryName);
					updateStructureOfSubTree(fullEntryName, node);
				}
			}
		} catch (final NamingException e) {
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
		final SubtreeNode node = TreeBuilder.findCreateSubtreeNode(tree, relativeName);
		try {
			final Attributes attrs = _directory.getAttributes(fullName);
			AlarmTreeBuilder.setEpicsAttributes(node, attrs);
		} catch (final NamingException e) {
			LOG.error(this, "Could not read attributes for node " + fullName, e);
		}
		return node;
	}
}
