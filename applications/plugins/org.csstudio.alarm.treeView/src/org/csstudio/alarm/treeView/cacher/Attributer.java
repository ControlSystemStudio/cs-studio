package org.csstudio.alarm.treeView.cacher;

import java.util.Hashtable;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;

import org.csstudio.alarm.treeView.views.models.ContextTreeParent;
import org.csstudio.alarm.treeView.views.models.LdapConnection;

/**
 * 
 * @author JURIJ KODRE
 * Attributer is an object which handles all requests for attributes after the structure was retrieved. 
 * It connects again to LDAP server and retrieves attributes on need. It does not do any caching.
 * and TODO: maybe we can also implement method for adding attributes
 *
 */
public class Attributer {
	//environment hashtable for LDAP connection
	protected Hashtable<String,String> env;
	//that DirContext provides through the whole tree
	protected transient DirContext connection;
	//provides connection with ldap connection
	protected LdapConnection mountPoint;
	
	//hardcoded source to structure and alarm data
	//TODO: also here 
	public static final String alarmCfgRoot="ou=EpicsAlarmCfg";
	
	/**
	 * First possibility to initalize - we initialize environment properties hashtable 
	 * and waiting to be filled with connection parameters
	 * @deprecated
	 */ 
	public Attributer(ContextTreeParent mountPoint){
		this.env = new Hashtable<String,String>();
	}
	
	/**
	 * Second possibility - we use outside made hashtable with connection parameters
	 * @param environment connection parameters for Connection - WARNING: initial factory can be overridden by 
	 * protocol parameter set  
	 */ 
	public Attributer(Hashtable<String,String> environment, LdapConnection mountPoint){
		this.env = environment;
		this.mountPoint = mountPoint; 	
	}

	public Hashtable getEnvironment() { 
		return env;
	}
	
	/**
	 * provides a way to set parameters after creation
	 * @param name name of parameter
	 * @param parameter value of parameter
	 */
	public void setParameter(String name, String parameter){
		env.put(name,parameter);
	}
	/**
	 * Initialize connection to directory service
	 * @throws Exception table with connection parameters is not provided
	 */
	private void initializeConnection() throws Exception{
		if (env==null) {
			throw new Exception("Parameters for connection not given.");
		}
		else {
	        env.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
			connection=new InitialDirContext(env);
		}
	}
	
	/**
	 * this method sets an attribute the new value
	 * @param rname name of node whose attribute needs to be set - we need regular name and not NameinNamespace
	 * @param name name of value that needs to be set
	 * @param newvalue new value to set an attribute to
	 * @throws NamingException if rname parameter not present the real attribute - see the documentation of DirContext.modifyAttributes
	 */
	public void updateAttribute(String rname, String name, String newvalue) throws NamingException {
		//we must create a list of modifications 
		ModificationItem[] modifs = new ModificationItem[1];
		modifs[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(name,newvalue));
		connection.modifyAttributes(rname,modifs);
	}	
	
	/**
	 * This method retrieve attributes of node specified in rname parameter
	 * @param rname name of node whose attribute needs to be set - we need regular name and not NameinNamespace
	 * @return Hashtable with all attributes of the node (name of attribute -> value)
	 * @throws NamingException if rname parameter not present the real attribute - see the documentation of DirContext.modifyAttributes
	 */
	public Hashtable<String,String> retrieveAttributes(String rname) throws NamingException{
		//we try to establish connection on the first time
		if (connection==null) {
			try {
				initializeConnection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Hashtable<String,String> atthash = new Hashtable<String,String>();
		Attributes atts;
		StringBuffer buf = new StringBuffer("");
		//we need to provide name of the root information in rname - that is because we must also included root in the tree 
		buf.append(rname); buf.append(","); buf.append(alarmCfgRoot);
		String rrname = buf.toString();
		atts = connection.getAttributes(rrname);
		//now the usual stuff - we get keys first and then retrieve values one by one per key 
		for (NamingEnumeration ne = atts.getIDs(); ne.hasMore();){					
			String attName = (String)ne.next();
			Attribute att = atts.get(attName);
			//this is because we dont want to create new string buffer on every iteration in retrieving properties of many nodes it could be very time consuming
			buf.delete(0,buf.length());
			for(int i = 0; i < att.size(); i++)
			{
				//dont know it seems that we could have more than one object as attribute in this case we split them with comma
				if (i>0){buf.append(",");}
				buf.append(att.get(i));
			}
			//we add the attribute to hashtable
			atthash.put(attName,buf.toString());
		}
		return atthash;
	}
	/**
	 * resets connection so we can connect to other directory structure providers/servers
	 *
	 */
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