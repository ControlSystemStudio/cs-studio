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
	
    /**
     * Creates a new LDAP connection.
     * 
     * @param url the LDAP URL.
     * @param principal the LDAP principal (user name).
     * @param credential the LDAP credentials (password).
     * @param rootNodes the root nodes that should be queried.
     */
	public LdapConnection() {
    	super();
		this.nameMap = new Hashtable<String,String>();
		tree = new Hashtable<String,ContextTreeObject>();
    }
    
    public void initializeCaching(){
    	if (!caching){
    		caching = true;
    		env = new Hashtable<String,String>();
    		CachingThread cthr = new CachingThread(this);
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

	public Attributer getAttributeGetter(){
		return atter;
	}
	
    public Hashtable<String,String> getAttributes(){
    	return env;
    }
	
    public String getName()
    {
        return "LdapConnection"; // TODO
    }

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