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

import org.csstudio.alarm.treeView.LdaptreePlugin;
import org.csstudio.alarm.treeView.views.models.Alarm;
import org.csstudio.alarm.treeView.views.models.ContextTreeObject;
import org.csstudio.alarm.treeView.views.models.ContextTreeParent;
import org.csstudio.alarm.treeView.views.models.LdapConnection;
import org.csstudio.alarm.treeView.views.models.NodeNotFoundException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * This job reads the directory structure from the server.
 * <p>
 * Retrieving is optimized: 1. we make only one request on the server (there 
 * could be sizelimit or timelimit set on server - so we should do a workaround 
 * (sth. like BFS or DFS will do) 2. we request only nodes - and from their name we retrive tree structure
 * We don't store structure in this object, but in the provided mountPoint 
 * (LDAPConnection).
 * <p> 
 * For other protocols than LDAP we need some corrections in the code which 
 * slightly assume that we deal with LDAP tree. It supports different name parsers for each protocols, 
 * but we also need support for different query search strings.  
 * <p>
 * It now also retrieve initial alarm states, but it should be done either 
 * differently or the implementation must be slightly different - like to 
 * reset connection if needed and connect to other server in run() method.
 * 
 * @author Jurij Kodre
 */
public class CachingThread extends Job {

	//client side provided sizelimit of result
	protected long SIZE_LIMIT=100000;  // this value is from LdapConnection.java
	//environment properties
	protected Hashtable<String,String> env;
	//DirContext used to perform search on all trees
	protected transient DirContext connection;
	//we use one of provided TreeParser - each of provided  
	protected ITreeParser tparser;
	//mountPoint - root for the tree
	protected LdapConnection mountPoint;
	//subtrees chosen in preferences page - which needs to be retrieved
	protected String[] structureRoots;
	
	//hard coded root for structure and alarm initialization - it will probably become a CSS convention - but this needs to be changed in case of different directory structure protocol
	public static final String alarmCfgRoot="ou=EpicsAlarmCfg";
	public static final String alarmInitialRoot="ou=EpicsAlarmCfg";//"ou=EpicsControls";
	
	/**
	 * Sets which subroots of EpicsAlarmCfg we want to be retrieved
	 * @param structureRoots String array with names (without tree structure! -e.g. Wasseranlagen)
	 */
	public void setStructureRoots(String[] structureRoots) {
		this.structureRoots = structureRoots;
	}

	/**
	 * We construct the Caching thread, here protocol setting if set also overrides InitialContextFactory
	 * @param mountPoint LDAPConnection on which we connect the our roots
	 */
	public CachingThread(ContextTreeParent mountPoint){
		super("Alarm Tree Directory Reader");
		this.env = new Hashtable<String,String>();
	}

	/**
	 * We construct the Caching thread with connection parameters provided in enviroment parameter, here protocol setting if set also overrides InitialContextFactory
	 * 
	 * @param environment connection parameters needs to be set for JNDI connection in Hashtable of course
	 * @param mountPoint LDAPConnection on which we connect the our roots
	 */
	public CachingThread(Hashtable<String,String> environment, LdapConnection mountPoint){
		super("Alarm Tree Directory Reader");
		this.env = environment;
		this.mountPoint = mountPoint; 	
	}
	
	/**
	 * sets the JNDI connection parameter
	 * @param name name od parameter (e.g. "java.naming.factory.initial")
	 * @param parameter value (e.g. "com.sun.jndi.ldap.LdapCtxFactory");
	 */
	public void setParameter(String name, String parameter){
		env.put(name,parameter);
	}
	
	/**
	 * Initializes Connection with given connection parameters
	 * @throws Exception if parameters are not given
	 */
	private void initializeConnection() throws Exception{
		if (env==null) {
			throw new Exception("Parameters for connection not given.");
		}
		else {
	        env.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
	        tparser = new LDAPTreeParser();		    
			connection=new InitialDirContext(env);
		}
	}
	
	/**
	 * This method reads subTree of given subroot (given by parameter mPoint)
	 */
	private synchronized void populateSubTree(String mPoint) throws NamingException{
//		we wants root of the tree also included so we do a little trick - we put root into the name so it will be 
//		parsed and added as root rather than his children 
		String name,rname,rootNode="efan="+mPoint+","+alarmCfgRoot;
		SearchControls ctrl = new SearchControls();
		StringBuffer sbuf;
		ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE); //set to search the whole tree
//		remove when you solve the size limit problem or TODO: workaround
		ctrl.setCountLimit(0);
//		we retrieve all leafes (nodes with no childern) and then populate them in tree 
//		(the populateObject method provides provides parsing names and putting them into tree structure)
		NamingEnumeration enumr = connection.search(rootNode,"eren=*",ctrl);
		long records=0;
		try
		{
			while (enumr.hasMore()){
				records++;
				SearchResult result = (SearchResult)enumr.next();
				name = result.getNameInNamespace();
				rname = result.getName();
				//only getName gives you name without 'o=DESY, c=DE'
				name = tparser.specialClean(name);
				rname = tparser.specialClean(rname);
				sbuf= new StringBuffer(rname);
				sbuf.append(",");
				sbuf.append("efan=");
				sbuf.append(mPoint);
				populateObject(name,sbuf.toString());
			}
		}
		catch (SizeLimitExceededException exc)
		{
			System.err.println("Size limit set on server! Set on:"+records);
		}
		finally{
			enumr.close();
		}
	}
	
	private synchronized void populateAlarms() throws NamingException{
		SearchControls ctrl = new SearchControls();
		String name,rname,sname;
		Hashtable <String,String> props;
		ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE); //set to search the whole tree
		//remove when you solve the size limit problem or TODO: workaround
		ctrl.setReturningAttributes(new String[]{"epicsAlarmSeverity","epicsAlarmStatus","epicsAlarmHighUnAckn"});
		ctrl.setCountLimit(SIZE_LIMIT);		
		//search definition
		NamingEnumeration enumr = connection.search(alarmInitialRoot,"(&(!(epicsAlarmStatus=NO_ALARM))(epicsAlarmSeverity=*))",ctrl);
		Attributes attbs;
		long records=0;
		try
		{
			while (enumr.hasMore()){
				records++;
				SearchResult result = (SearchResult)enumr.next();
				name = result.getNameInNamespace();
				name = tparser.specialClean(name);
				rname = result.getName();
				rname = tparser.specialClean(rname);
				sname = tparser.getMyName(rname);
				StringBuffer pname=new StringBuffer(rname);
				pname.append(",");
				pname.append(alarmInitialRoot);
				if (connection.lookup(pname.toString())!=null){
					attbs = connection.getAttributes(pname.toString());
					Attribute severityat = attbs.get("epicsAlarmSeverity");
					Attribute statusat=attbs.get("epicsAlarmStatus");
					Attribute highun = attbs.get("epicsAlarmHighUnAckn");
					//only getName gives you name without 'o=DESY, c=DE'
					String severity = (String) severityat.get();
					String unseverity;
					if (highun==null){unseverity = "";}
					else {unseverity = (String)highun.get();}
					if (!(severity.equals("NO_ALARM")) && !(severity.equals(""))){
						int sever = 0;
						props = new Hashtable<String,String>();
						if (severity.equals("MAJOR")){sever =7;}
						if (severity.equals("MINOR")){sever =4;}
						if (severity.equals("INVALID")) {sever=2;}
						props.put("NAME",sname);
						props.put("SEVERITY",severity); //suppose there is only one variable
						props.put("STATUS",(String) statusat.get()); // see above
						Alarm alm = new Alarm(sever,name);
						alm.setProperties(props);
						alm.setName(sname);
						alm.setUnAcknowledged(false);
						try {
							mountPoint.triggerAlarmOnNode(alm);
						} catch (NodeNotFoundException e) {
							// TODO Auto-generated catch block
							System.out.println("Structure doesn't contain "+sname+".");
						}
					}
					if (!(unseverity.equals("NO_ALARM")) && !(unseverity.equals(""))){
						int unsever=0;
						if (unseverity.equals("MAJOR")){unsever =7;}
						if (unseverity.equals("MINOR")){unsever =4;}
						if (unseverity.equals("INVALID")) {unsever=2;}
						props = new Hashtable<String,String>();
						props.put("NAME",sname);
						props.put("SEVERITY",unseverity);
						props.put("STATUS",(String) statusat.get()); // see above
						Alarm alm = new Alarm(unsever,name);
						alm.setProperties(props);
						alm.setName(sname);
						alm.setUnAcknowledged(true);
						try {
							mountPoint.triggerAlarmOnNode(alm);
						} catch (NodeNotFoundException e) {
							// TODO Auto-generated catch block
							System.out.println("Structure doesn't contain "+sname+".");
						}
					}
					//populateAlarm(name,rname,props);
				}
				else {
					System.out.println(rname+" cannot be populated!");
				}
			}
		}
		catch (SizeLimitExceededException exc)
		{
			System.out.println("Size limit set on server! Set on:"+records);
		}
		finally{
			enumr.close();
		}
	}
	
	private synchronized void populateObject(String name,String rname){
		populateObject(name,rname,mountPoint);
	}
	
	private synchronized void populateObject(String name,String rname,ContextTreeParent subRoot){
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
				populateSubTree(structureRoots[i]);
			}
			
			monitor.subTask("Initializing alarm states");
			populateAlarms();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		monitor.done();
		return new Status(IStatus.OK, LdaptreePlugin.getDefault().getPluginId(),
				IStatus.OK, "Finished initializing alarm tree", null);
	}
	
	public void resetConnection(){
		if (connection != null)
			try {
				connection.close();
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		connection = null;
	}
	
}
 