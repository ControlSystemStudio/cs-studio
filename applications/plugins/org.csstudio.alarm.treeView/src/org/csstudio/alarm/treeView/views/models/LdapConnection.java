package org.csstudio.alarm.treeView.views.models;

import java.util.Hashtable;

import org.csstudio.alarm.treeView.cacher.Attributer;
import org.csstudio.alarm.treeView.cacher.CachingThread;


public class LdapConnection extends ContextTreeParent
{
	private boolean caching = false;
	protected Hashtable<String,String> nameMap;
	protected Hashtable<String,ContextTreeObject> tree;
	protected Hashtable<String,String> env;
	protected String[] structureRoot;
	
		
    public LdapConnection()
    {
        super();
        url = "";
        principal = "";
        credentials = "";
        savePassword = false;
		this.nameMap = new Hashtable<String,String>();
		tree = new Hashtable<String,ContextTreeObject>();
   }

    public LdapConnection(String url, String principal, String credential, String prefsNodes){
    	super();
    	this.url = url;
    	this.principal = principal;
    	this.credentials = credential;
		this.nameMap = new Hashtable<String,String>();
		this.structureRoot = prefsNodes.split(";");
		tree = new Hashtable<String,ContextTreeObject>();
    }
    
    public void setStructureRoot(String prefsNodes){
    	structureRoot = prefsNodes.split(";");
    }
    
    public void initializeCaching(){
    	if (!caching){
    		caching = true;
    		env = new Hashtable<String,String>();
            env.put("java.naming.provider.url", url);
//            env.put("java.naming.security.principal", principal);
//            env.put("java.naming.security.credentials", credentials);		
    		CachingThread cthr = new CachingThread(env,this);
    		cthr.setStructureRoots(structureRoot);
    		// cthr is a system job (it won't show in the GUI) because it was
    		// not initiated by the user and must necessarily run.
    		cthr.setSystem(true);
    		cthr.schedule();

    		// Currently running cthr concurrently in the background does
			// not work correctly, so we join immediately after scheduling
			// the job. XXX: This try-catch-block should be removed as soon
    		// as reading the directory in the background works.
    		try {
				cthr.join();
			} catch (InterruptedException e) {
				// should never happen
			}
			
    		atter = new Attributer(env,this);
    	}
    }
    
    public Hashtable<String, String> getNameMap() {
		return nameMap;
	}

	public Hashtable<String, ContextTreeObject> getTree() {
		return tree;
	}

	public void reset()
    {
    	cthr.resetConnection();
    }
    
/*    public synchronized DirContext getConnection()
        throws NamingException
    {
        if(connection == null)
        {
            Hashtable<String,String> env = new Hashtable();
            env.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
            env.put("java.naming.provider.url", url);
            if(principal != null && principal.length() > 0)
                env.put("java.naming.security.principal", principal);
            if(credentials != null && credentials.length() > 0)
                env.put("java.naming.security.credentials", credentials);
            connection = new InitialDirContext(env);
            cthr = new ConnectionThread(this);
        }
        return connection;
    }*/

	public Attributer getAttributeGetter(){
		return atter;
	}
	
    public Hashtable<String,String> getAttributes(){
    	return env;
    }
	
    public String getCredentials()
    {
        return credentials;
    }

    public void setCredentials(String credentials)
    {
        this.credentials = credentials;
    }

    public String getPrincipal()
    {
        return principal;
    }

    public void setPrincipal(String principal)
    {
        this.principal = principal;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getName()
    {
        return url;
    }

    public boolean isSavePassword()
    {
        return savePassword;
    }

    public void setSavePassword(boolean savePassword)
    {
        this.savePassword = savePassword;
    }

   /* public boolean hasChildren(){
    	return true;
    }*/
    
    private String url;
    private String principal;
    private String credentials;
    private boolean savePassword;

	public void triggerAlarmOnNode(Alarm alarm) throws NodeNotFoundException{
//		try {
		ContextTreeObject objc;
		String name = alarm.getName();
		objc = searchObjectbyName(name);
		if (objc!=null){
			objc.triggerAlarm(alarm);			
		}
		else System.out.println(name+" not found.");
/*		}catch (Exception e){
			e.printStackTrace();
		}*/
	}
	
	public void acknowledgeAlarmOnNodeName(String nodeName) throws NodeNotFoundException{
		ContextTreeObject objc = searchObjectbyName(nodeName);
		objc.acknowledgeMyAlarmState();
	}
	
	private ContextTreeObject searchObjectbyName(String name) throws NodeNotFoundException {
		// TODO Auto-generated method stub
		try {
			return tree.get(nameMap.get(name));
		}
		catch (NullPointerException ne){
			throw new NodeNotFoundException();
		}
	}

	
	public void disableAlarmOnNode(Alarm alarm){
		String name = alarm.getName();
		ContextTreeObject objc;
		try {
			objc = searchObjectbyName(name);
			if (objc!=null) objc.acknowledgeAlarm(alarm);
		} catch (NodeNotFoundException e) {
			System.out.println("Structure doesn't contain "+alarm.getName()+".");
		}
	}
}