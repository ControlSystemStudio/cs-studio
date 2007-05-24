package org.csstudio.alarm.treeView.cacher;

import java.util.Hashtable;

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
import org.csstudio.alarm.treeView.preferences.PreferenceConstants;
import org.csstudio.alarm.treeView.views.models.Alarm;
import org.csstudio.alarm.treeView.views.models.ContextTreeObject;
import org.csstudio.alarm.treeView.views.models.ContextTreeParent;
import org.csstudio.alarm.treeView.views.models.LdapConnection;
import org.csstudio.alarm.treeView.views.models.NodeNotFoundException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * This job reads the directory structure from the server.
 * 
 * @author Joerg Rathlev, Jurij Kodre
 */
// TODO: clean up propagagtion of NamingExceptions (an error that occurs while reading
// a single object should not cause the complete job to fail, check that this is not
// the case)
// TODO: understand and document handling of names (i.e. what exactly are sname, rname, name?)
public class CachingThread extends Job {

	// maximum number of entries to return
	private long COUNT_LIMIT = 0;  // 0 means unlimited
	
	// The LDAP directory that is searched
	private DirContext directory;
	
	//we use one of provided TreeParser - each of provided  
	protected ITreeParser tparser;
	//mountPoint - root for the tree
	protected LdapConnection mountPoint;
	//subtrees chosen in preferences page - which needs to be retrieved
	protected String[] structureRoots;
	
	// hard coded root for structure and alarm initialization - it will
	// probably become a CSS convention - but this needs to be changed
	// in case of different directory structure protocol
	private static final String ALARM_ROOT = "ou=EpicsAlarmCfg";
	
	/**
	 * Creates a new CachingThread instance.
	 * 
	 * @param mountPoint the root node of the tree to which items are added.
	 */
	public CachingThread(LdapConnection mountPoint) {
		super("Alarm Tree Directory Reader");
		this.mountPoint = mountPoint;
		
		// Note: the following doesn't work with IEclipsePreferences... see also
		// #environmentFromPreferences for the same problem.
		Preferences prefs = AlarmTreePlugin.getDefault().getPluginPreferences();
		structureRoots = prefs.getString(PreferenceConstants.NODE).split(";");
	}
	
	/**
	 * Initializes Connection with connection parameters set up in the
	 * preferences.
	 */
	private void initializeConnection() {
		Hashtable<String, String> env = environmentFromPreferences();
        env.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
        tparser = new LDAPTreeParser();
		try {
			directory = new InitialDirContext(env);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Initializes the LDAP environment (URL, username and password) from this
	 * plug-in's preferences.
	 * @return the LDAP environment.
	 */
	private Hashtable<String, String> environmentFromPreferences() {
		// this doesn't work, for some reason...
//		IEclipsePreferences prefs = new DefaultScope().getNode(AlarmTreePlugin.PLUGIN_ID);
//		String url = prefs.get(PreferenceConstants.URL, "");
//		String user = prefs.get(PreferenceConstants.USER, "");
//		String password = prefs.get(PreferenceConstants.PASSWORD, "");
		
		IPreferenceStore prefs = AlarmTreePlugin.getDefault().getPreferenceStore();
		String url = prefs.getString(PreferenceConstants.URL);
		String user = prefs.getString(PreferenceConstants.USER);
		String password = prefs.getString(PreferenceConstants.PASSWORD);
		
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put("java.naming.provider.url", url);
		env.put("java.naming.security.principal", user);
		env.put("java.naming.security.credentials", password);
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
		
//		we retrieve all leafes (nodes with no childern) and then populate them in tree 
//		(the populateObject method provides provides parsing names and putting them into tree structure)
		// search all EPICS record names (eren) below the base object
		NamingEnumeration<SearchResult> searchResults =
			directory.search(searchRootDN, "eren=*", ctrl);
		try
		{
			while (searchResults.hasMore()){
				SearchResult result = searchResults.next();
				String name;
				String rname;
				StringBuffer sbuf;
				// XXX: next line can throw UnsupportedOperationException! (not for LDAP but anyway...)
				name = result.getNameInNamespace();
				rname = result.getName();
				
//				System.out.println("result.isRelative():         " + result.isRelative());
//				System.out.println("result.getName():            " + result.getName());
//				System.out.println("result.getNameInNamespace(): " + result.getNameInNamespace());
//				
//				Attributes test = result.getAttributes();
//				NamingEnumeration<? extends Attribute> attrs = test.getAll();
//				while (attrs.hasMore()) {
//					Attribute attr = attrs.next();
//					System.out.println("  " + attr.getID() + ": " + attr.get().toString());
//				}
//				System.out.println("---");

				name = tparser.specialClean(name);
				rname = tparser.specialClean(rname);
				sbuf= new StringBuffer(rname);
				sbuf.append(",");
				sbuf.append("efan=");
				sbuf.append(efan);
				populateObject(name,sbuf.toString());
				
				// Read the object's alarm status, and trigger an alarm on the node
				// that was just created if there is an alarm.
				evaluateAlarmAttributes(result);
			}
		}
		catch (SizeLimitExceededException e)
		{
			System.err.println("Size limit exceeded while reading tree: " + e.getExplanation());
		}
		finally {
			try {
				searchResults.close();
			}
			catch (NamingException e) {
				System.err.println("NamingException while closing search result enumeration:\n" + e);
			}
		}
	}
	
	/**
	 * Evaluates the alarm attributes (if any) of an object found in the
	 * directory. If there is an alarm, triggers the alarm for the node
	 * in the alarm tree.
	 * 
	 * @param result the object found in the directory.
	 * @throws NamingException if something bad happens...
	 */
	private void evaluateAlarmAttributes(SearchResult result) throws NamingException {
		String name = result.getNameInNamespace();
		name = tparser.specialClean(name);
		
		String sname = tparser.getMyName(tparser.specialClean(result.getName()));
		Attributes attrs = result.getAttributes();
		Attribute severityAttr = attrs.get("epicsAlarmSeverity");
		Attribute statusAttr = attrs.get("epicsAlarmStatus");
		Attribute highUnAcknAttr = attrs.get("epicsAlarmHighUnAckn");
		if (severityAttr != null) {
			String severity = (String) severityAttr.get();
			triggerAlarmIfNecessary(severity, name, sname, statusAttr, false);
		}
		else if (highUnAcknAttr != null) {
			String unseverity = (String) highUnAcknAttr.get();
			triggerAlarmIfNecessary(unseverity, name, sname, statusAttr, true);
		}
	}
	
	
	/**
	 * Triggers an alarm if the given severity is an alarm severity. The severity
	 * is an alarm severity if it is not NO_ALARM or the empty string.
	 * @param severity the severity.
	 * @param name the name of the object.  // TODO: better documentation, what is it exactly?
	 * @param sname the sname. // TODO documentation, what is it exactly?
	 * @param status the status attribute from the directory.
	 * @param unAcknowledged whether the alarm is unacknowledged.
	 * @throws NamingException if something goes wrong.
	 */
	private void triggerAlarmIfNecessary(String severity, String name, String sname, Attribute status, boolean unAcknowledged) throws NamingException {
		if (!(severity.equals("NO_ALARM")) && !(severity.equals(""))) {
			int sever = severityIntValueFromString(severity);
			Hashtable<String, String> props =
				alarmProperties(sname, severity, (String) status.get());
			Alarm alm = 
				createAlarm(sever, name, sname, props, true);
			try {
				mountPoint.triggerAlarmOnNode(alm);
			} catch (NodeNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("Structure doesn't contain "+sname+".");
			}
		}
	}
	
	
	/**
	 * Creates an alarm object.
	 * @param severity the severity of the alarm.
	 * @param name the name of the alarm.  // TODO what is it exactly?
	 * @param sname the sname of the alarm.// TODO what is it exactly?
	 * @param properties the properties for the alarm.
	 * @param unAcknowledged whether the alarm is unacknowledged.
	 * @return
	 */
	private Alarm createAlarm(int severity, String name, String sname, Hashtable<String, String> properties, boolean unAcknowledged) {
		Alarm alarm = new Alarm(severity, name);
		alarm.setProperties(properties);
		alarm.setName(sname);
		alarm.setUnAcknowledged(unAcknowledged);
		return alarm;
	}
	
	
	/**
	 * Returns an integer representation of the severity.
	 * @param severityString the severity represented as a string value.
	 * @return the severity represented as an integer value.
	 */
	private int severityIntValueFromString(String severityString) {
		if (severityString.equals("MAJOR")) return 7;
		if (severityString.equals("MINOR")) return 4;
		if (severityString.equals("INVALID")) return 2;
		return 0;
	}
	
	
	/**
	 * Creates the properties table that will be attached to an alarm object.
	 * @param sname the sname TODO: what is it?
	 * @param severity the alarm severity.
	 * @param status the alarm status.
	 * @return the properties for the alarm object.
	 */
	private Hashtable<String, String> alarmProperties(String sname, String severity, String status) {
		Hashtable<String, String> properties = new Hashtable<String, String>();
		properties.put("NAME", sname);
		properties.put("SEVERITY", severity);
		properties.put("STATUS", status);
		return properties;
	}
	
	private void populateObject(String name,String rname){
		populateObject(name,rname,mountPoint);
	}
	
	private void populateObject(String name,String rname,ContextTreeParent subRoot){
		Hashtable<String,String> nameMap = mountPoint.getNameMap();
		Hashtable<String,ContextTreeObject> tree = mountPoint.getTree();
		if (!tree.containsKey(rname)){
			String parentName = tparser.getParentRName(name);
			String parentRName = tparser.getParentRName(rname);
			String myname = tparser.getMyName(name);
			if (parentRName == null){
				//maybe we should have our root and we have to mount it on root 
				ContextTreeParent tpt = new ContextTreeParent(subRoot);
				tpt.setDn(name);
				tpt.setName(myname);
				tpt.setRname(rname);
				if (myname!=null){nameMap.put(myname,rname);}
				tree.put(rname,tpt);
				return;
			}
			if (tree.containsKey(rname)){
			}
			else{
				if (!tree.containsKey(parentRName)){
					populateObject(parentName,parentRName,subRoot);
				}
				else {
				}
				ContextTreeParent tpt = new ContextTreeParent((ContextTreeParent)tree.get(parentRName));
				tpt.setDn(name);
				tpt.setName(myname);
				tpt.setRname(rname);
				nameMap.put(myname,rname);
				tree.put(rname,tpt);
			}			
		}
	}
	
	
	/**
	 * Runs this job.
	 * @param monitor the progress monitor to which progress is reported.
	 * @return a status representing the outcome of this job.
	 */
	public IStatus run(IProgressMonitor monitor){
		monitor.beginTask("Initializing Alarm Tree", IProgressMonitor.UNKNOWN);
		try {
			monitor.subTask("Connecting");
			initializeConnection();
			
			monitor.subTask("Building tree");
			int l = structureRoots.length;
			for (int i=0; i<l; i++){
				System.out.println(structureRoots[i]);
				populateSubTree(structureRoots[i]);
			}
			closeConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		monitor.done();
		return new Status(IStatus.OK, AlarmTreePlugin.getDefault().getPluginId(),
				IStatus.OK, "Finished initializing alarm tree", null);
	}
	
	/**
	 * Closes the connection to the directory.
	 */
	private void closeConnection(){
		if (directory != null)
			try {
				directory.close();
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		directory = null;
	}
	
}
 