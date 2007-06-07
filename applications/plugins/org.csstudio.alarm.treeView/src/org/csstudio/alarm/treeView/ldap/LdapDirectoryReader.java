package org.csstudio.alarm.treeView.ldap;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.SizeLimitExceededException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.csstudio.alarm.treeView.AlarmTreePlugin;
import org.csstudio.alarm.treeView.model.Alarm;
import org.csstudio.alarm.treeView.model.ProcessVariableNode;
import org.csstudio.alarm.treeView.model.Severity;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.csstudio.alarm.treeView.preferences.PreferenceConstants;
import org.csstudio.platform.logging.CentralLogger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;

/**
 * This job reads the directory structure from the server.
 * 
 * @author Joerg Rathlev, Jurij Kodre
 */
public class LdapDirectoryReader extends Job {

	/**
	 * Maximum number of entries to return in a search.
	 */
	private long COUNT_LIMIT = 0;  // 0 means unlimited
	
	/**
	 * The directory that is searched.
	 */
	private DirContext directory;
	
	/**
	 * The root node of the tree that is built by this reader.
	 */
	private SubtreeNode treeRoot;
	
	/**
	 * The facility names which are read from the directory.
	 */
	private String[] facilityNames;
	
	/**
	 * The root below which the direcoty is searched.
	 */
	// hard coded root for structure and alarm initialization - it will
	// probably become a CSS convention - but this needs to be changed
	// in case of different directory structure protocol
	private static final String ALARM_ROOT = "ou=EpicsAlarmCfg";
	
	private static final CentralLogger log = CentralLogger.getInstance();
	
	/**
	 * Creates a new CachingThread instance.
	 * 
	 * @param treeRoot the root node of the tree to which items are added.
	 */
	public LdapDirectoryReader(SubtreeNode treeRoot) {
		super("Alarm Tree Directory Reader");
		this.treeRoot = treeRoot;
		
		IEclipsePreferences prefs =
			new InstanceScope().getNode(AlarmTreePlugin.PLUGIN_ID);
		facilityNames = prefs.get(PreferenceConstants.FACILITIES, "").split(";");
	}
	
	
	/**
	 * Initializes Connection with connection parameters set up in the
	 * preferences.
	 */
	private void initializeConnection() throws NamingException {
		Hashtable<String, String> env = environmentFromPreferences();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		directory = new InitialDirContext(env);
	}
	
	
	/**
	 * Initializes the LDAP environment (URL, username and password) from this
	 * plug-in's preferences.
	 * @return the LDAP environment.
	 */
	private Hashtable<String, String> environmentFromPreferences() {
		IEclipsePreferences prefs =
			new InstanceScope().getNode(AlarmTreePlugin.PLUGIN_ID);
		String url = prefs.get(PreferenceConstants.LDAP_URL, "");
		String user = prefs.get(PreferenceConstants.LDAP_USER, "");
		String password = prefs.get(PreferenceConstants.LDAP_PASSWORD, "");
		
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.PROVIDER_URL, url);
		env.put(Context.SECURITY_PRINCIPAL, user);
		env.put(Context.SECURITY_CREDENTIALS, password);
		return env;
	}
	
	
	/**
	 * Reads all process variables below the given facility name into the tree.
	 * @param efan the EPICS facility name.
	 */
	private void populateSubTree(String efan) throws NamingException {
		// build dn of the efan object below which we want to search
		String searchRootDN = "efan=" + efan + "," + ALARM_ROOT;

		SearchControls ctrl = new SearchControls();
		ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
		ctrl.setCountLimit(COUNT_LIMIT);
		
		// search all EPICS record names (eren) below the base object
		NamingEnumeration<SearchResult> searchResults =
			directory.search(searchRootDN, "eren=*", ctrl);
		try
		{
			while (searchResults.hasMore()){
				SearchResult result = searchResults.next();
				String relativeName = result.getName();
				relativeName = LdapNameUtils.removeQuotes(relativeName);
				String relativeNameWithEfan = relativeName + ",efan=" + efan;
				ProcessVariableNode node = createTreeNode(relativeNameWithEfan);
				
				// Read the object's alarm status, and trigger an alarm on the node
				// that was just created if there is an alarm.
				evaluateAlarmAttributes(result, node);
			}
		}
		catch (SizeLimitExceededException e)
		{
			log.error(this, "Size limit exceeded while reading search results: "
					+ e.getExplanation(), e);
		}
		finally {
			try {
				searchResults.close();
			}
			catch (NamingException e) {
				log.warn(this, "Error cloing search results", e);
			}
		}
	}
	
	
	/**
	 * Evaluates the alarm attributes (if any) of an object found in the
	 * directory. If there is an alarm, triggers the alarm for the node
	 * in the alarm tree.
	 * 
	 * @param result the object found in the directory.
	 * @param node the node on which the alarm must be triggered.
	 * @throws NamingException if something bad happens...
	 */
	private void evaluateAlarmAttributes(SearchResult result, ProcessVariableNode node) throws NamingException {
		String fullyQualifiedName = result.getNameInNamespace();
		fullyQualifiedName = LdapNameUtils.removeQuotes(fullyQualifiedName);
		
		String simpleName = LdapNameUtils.simpleName(fullyQualifiedName);
		Attributes attrs = result.getAttributes();
		Attribute severityAttr = attrs.get("epicsAlarmSeverity");
		//Attribute statusAttr = attrs.get("epicsAlarmStatus");
		Attribute highUnAcknAttr = attrs.get("epicsAlarmHighUnAckn");
		if (severityAttr != null) {
			String severity = (String) severityAttr.get();
			triggerAlarmIfNecessary(severity, simpleName, true, node);
		}
		else if (highUnAcknAttr != null) {
			String unseverity = (String) highUnAcknAttr.get();
			triggerAlarmIfNecessary(unseverity, simpleName, false, node);
		}
	}
	
	
	/**
	 * Triggers an alarm if the given severity is an alarm severity. The severity
	 * is an alarm severity if it is not NO_ALARM or the empty string.
	 * @param severity the severity.
	 * @param simpleName the simple name of the object.
	 * @param acknowledged whether the alarm is unacknowledged.
	 * @param node the node on which to trigger the alarm.
	 * @throws NamingException if something goes wrong.
	 */
	private void triggerAlarmIfNecessary(String severity, String simpleName,
			boolean acknowledged, ProcessVariableNode node) throws NamingException {
		if (!(severity.equals("NO_ALARM")) && !(severity.equals(""))) {
			Severity sever = new Severity(severity);
			Alarm alm = createAlarm(sever, simpleName, acknowledged);
			node.triggerAlarm(alm);
		}
	}
	
	
	/**
	 * Creates an alarm object.
	 * @param severity the severity of the alarm.
	 * @param simpleName the simple name of the object.
	 * @param acknowledged whether the alarm is unacknowledged.
	 * @return the alarm object.
	 */
	private Alarm createAlarm(Severity severity, String simpleName, boolean acknowledged) {
		Alarm alarm = new Alarm(simpleName, severity);
		if (acknowledged) {
			alarm.acknowledge();
		}
		return alarm;
	}
	
	
	/**
	 * Creates a tree item for the object with the given name.
	 * @param relativeName the object's relative name. This name will be used to determine
	 *        where in the tree to put the object.
	 */
	private ProcessVariableNode createTreeNode(String relativeName) {
		return createTreeNode(relativeName, treeRoot);
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
	private ProcessVariableNode createTreeNode(String relativeName,
			SubtreeNode rootNode) {
		SubtreeNode parentNode = findParentNode(relativeName, rootNode);
		String name = LdapNameUtils.simpleName(relativeName);
		ProcessVariableNode node = new ProcessVariableNode(parentNode, name);
		return node;
	}
	
	
	/**
	 * Finds the parent node of the node with the specified name. If the parent
	 * node does not exist, it is created.
	 * 
	 * @param relativeName the relative name of the node whose parent is to be
	 *        found.
	 * @param root the root node of the tree which is searched.
	 * @return the parent node of the node with the specified name.
	 */
	private SubtreeNode findParentNode(String relativeName, SubtreeNode root) {
		String relativeParentName = LdapNameUtils.parentName(relativeName);
		if (relativeParentName != null) {
			// first, find the parent's parent
			SubtreeNode parentParent = findParentNode(relativeParentName, root);
			// Then, see if the parent's parent already contains the parent
			// we're looking for. If it does, return the existing parent,
			// otherwise create it below its parent.
			String parentName = LdapNameUtils.simpleName(relativeParentName);
			SubtreeNode parent = (SubtreeNode) parentParent.getChild(parentName);
			if (parent != null) {
				return parent;
			} else {
				parent = new SubtreeNode(parentParent, parentName);
				return parent;
			}
		} else {
			// there is no parent node, so return the root node
			return root;
		}
	}
	
	
	/**
	 * Runs this job.
	 * @param monitor the progress monitor to which progress is reported.
	 * @return a status representing the outcome of this job.
	 */
	public IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Initializing Alarm Tree", IProgressMonitor.UNKNOWN);
		try {
			initializeConnection();
			for (String facility : facilityNames) {
				populateSubTree(facility);
			}
			closeConnection();
		} catch (NamingException e) {
			return new Status(IStatus.ERROR, AlarmTreePlugin.PLUGIN_ID,
					IStatus.ERROR, "Error reading from directory", e);
		} finally {
			monitor.done();
		}
		return Status.OK_STATUS;
	}
	
	
	/**
	 * Closes the connection to the directory.
	 */
	private void closeConnection() {
		if (directory != null) {
			try {
				directory.close();
				directory = null;
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
 