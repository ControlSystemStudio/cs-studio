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

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.SizeLimitExceededException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.model.AbstractAlarmTreeNode;
import org.csstudio.alarm.treeView.model.Alarm;
import org.csstudio.alarm.treeView.model.ProcessVariableNode;
import org.csstudio.alarm.treeView.model.Severity;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.csstudio.alarm.treeView.preferences.PreferenceConstants;
import org.csstudio.platform.logging.CentralLogger;
import org.csstudio.utility.ldap.engine.Engine;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * This job reads the record entries (eren) from the LDAP directory and creates
 * the nodes representing them in the tree. The parent nodes are also created,
 * but their attributes are not read by this job. Empty nodes which do not
 * contain any eren objects are not created by this job.
 * 
 * @author Joerg Rathlev, Jurij Kodre
 */
public class LdapDirectoryReader extends Job {

	/**
	 * Maximum number of entries to return in a search.
	 */
	private static final long COUNT_LIMIT = 0;  // 0 means unlimited
	
	/**
	 * The directory that is searched.
	 */
	private DirContext _directory;
	
	/**
	 * The root node of the tree that is built by this reader.
	 */
	private SubtreeNode _treeRoot;
	
	/**
	 * The facility names which are read from the directory.
	 */
	private String[] _facilityNames;
	
	/**
	 * The root below which the direcoty is searched.
	 */
	// hard coded root for structure and alarm initialization - it will
	// probably become a CSS convention - but this needs to be changed
	// in case of different directory structure protocol
	private static final String ALARM_ROOT = "ou=EpicsAlarmCfg";
	
	/**
	 * The logger that is used by this class.
	 */
	private static final CentralLogger LOG = CentralLogger.getInstance();
	
	/**
	 * Creates a new LDAP directory reader.
	 * 
	 * @param treeRoot the root node of the tree to which items are added.
	 */
	public LdapDirectoryReader(final SubtreeNode treeRoot) {
		super("Alarm Tree Directory Reader");
		this._treeRoot = treeRoot;
		
		Preferences prefs = AlarmTreePlugin.getDefault().getPluginPreferences();
		_facilityNames = prefs.getString(PreferenceConstants.FACILITIES).split(";");
	}
	
	
	/**
	 * Gets the directory context from the LDAP engine.
	 */
	private void initializeDirectoryContext() {
		_directory = Engine.getInstance().getLdapDirContext();
	}
	
	
	/**
	 * Reads all process variables below the given facility name into the tree.
	 * @param efan the EPICS facility name.
	 * @throws NamingException if an LDAP error occurs.
	 */
	private void populateSubTree(final String efan) throws NamingException {
		// build dn of the efan object below which we want to search
		String searchRootDN = "efan=" + efan + "," + ALARM_ROOT;

		SearchControls ctrl = new SearchControls();
		ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
		ctrl.setCountLimit(COUNT_LIMIT);
		
		// search all EPICS record names (eren) below the base object
		NamingEnumeration<SearchResult> searchResults =
			_directory.search(searchRootDN, "eren=*", ctrl);
		try {
			while (searchResults.hasMore()){
				SearchResult result = searchResults.next();
				String relativeName = result.getName();
				relativeName = LdapNameUtils.removeQuotes(relativeName);
				String relativeNameWithEfan = relativeName + ",efan=" + efan;
				ProcessVariableNode node = createTreeNode(relativeNameWithEfan);
				
				// Read the object's alarm status, and trigger an alarm on the node
				// that was just created if there is an alarm.
				evaluateAttributes(result, node);
			}
		} catch (SizeLimitExceededException e) {
			LOG.error(this, "Size limit exceeded while reading search results: "
					+ e.getExplanation(), e);
		} finally {
			try {
				searchResults.close();
			} catch (NamingException e) {
				LOG.warn(this, "Error cloing search results", e);
			}
		}
	}
	
	
	/**
	 * Evaluates the attributes (if any) of an object found in the
	 * directory. If there is an alarm, triggers the alarm for the node
	 * in the alarm tree.
	 * 
	 * @param result the object found in the directory.
	 * @param node the node on which the alarm must be triggered.
	 * @throws NamingException if something bad happens...
	 */
	private void evaluateAttributes(final SearchResult result,
			final ProcessVariableNode node) throws NamingException {
		Attributes attrs = result.getAttributes();
		setAlarmState(node, attrs);
		setEpicsAttributes(node, attrs);
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
	 *             if an error occurs.
	 */
	private void setEpicsAttributes(final AbstractAlarmTreeNode node, final Attributes attrs)
			throws NamingException {
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
	 * Sets the alarm state of the given node based on the given attributes.
	 * 
	 * @param node
	 *            the node.
	 * @param attrs
	 *            the attributes.
	 * @throws NamingException
	 *             if an error occurs.
	 */
	private void setAlarmState(final ProcessVariableNode node, final Attributes attrs)
			throws NamingException {
		Attribute severityAttr = attrs.get("epicsAlarmSeverity");
		Attribute highUnAcknAttr = attrs.get("epicsAlarmHighUnAckn");
		if (severityAttr != null) {
			String severity = (String) severityAttr.get();
			if (severity != null) {
				Severity s = Severity.parseSeverity(severity);
				node.setActiveAlarm(new Alarm("", s));
			}
		}
		Severity unack = Severity.NO_ALARM;
		if (highUnAcknAttr != null) {
			String severity = (String) highUnAcknAttr.get();
			if (severity != null) {
				unack = Severity.parseSeverity(severity);
			}
		}
		node.setHighestUnacknowledgedAlarm(new Alarm("", unack));
	}
	
	
	/**
	 * Creates a tree item for the object with the given name.
	 * @param relativeName the object's relative name. This name will be used to determine
	 *        where in the tree to put the object.
	 * @return the tree item for the given object.
	 */
	private ProcessVariableNode createTreeNode(final String relativeName) {
		return createTreeNode(relativeName, _treeRoot);
	}
	
	
	/**
	 * Creates a new node with the given name and inserts it into the tree.
	 * 
	 * @param relativeName the relative name of the node. The relative name
	 *        determines the position in the tree where the node will be
	 *        inserted.
	 * @param rootNode the root node of the tree.
	 * @return the created node.
	 */
	private ProcessVariableNode createTreeNode(final String relativeName,
			final SubtreeNode rootNode) {
		SubtreeNode parentNode = TreeBuilder.findCreateParentNode(rootNode, relativeName);
		String name = LdapNameUtils.simpleName(relativeName);
		ProcessVariableNode node = new ProcessVariableNode(parentNode, name);
		return node;
	}
	
	
	/**
	 * Runs this job.
	 * @param monitor the progress monitor to which progress is reported.
	 * @return a status representing the outcome of this job.
	 */
	public final IStatus run(final IProgressMonitor monitor) {
		monitor.beginTask("Initializing Alarm Tree", IProgressMonitor.UNKNOWN);
		try {
			long startTime = System.currentTimeMillis();
			initializeDirectoryContext();
			for (String facility : _facilityNames) {
				populateSubTree(facility);
			}
			long endTime = System.currentTimeMillis();
			LOG.debug(this, "Directory reader time: " + (endTime-startTime) + "ms");
		} catch (NamingException e) {
			return new Status(IStatus.ERROR, AlarmTreePlugin.PLUGIN_ID,
					IStatus.ERROR, "Error reading from directory: " + e.getMessage(), e);
		} finally {
			monitor.done();
		}
		return Status.OK_STATUS;
	}
	
}
