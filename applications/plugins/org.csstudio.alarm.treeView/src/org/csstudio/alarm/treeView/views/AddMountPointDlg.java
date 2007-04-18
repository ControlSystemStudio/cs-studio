package org.csstudio.alarm.treeView.views;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.csstudio.alarm.treeView.cacher.LDAPTreeParser;
import org.csstudio.alarm.treeView.views.models.LdapConnection;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;


public class AddMountPointDlg extends Dialog {
	public AddMountPointDlg(Shell parentShell, Hashtable<String,String> env) {
		super(parentShell);
		this.env = env;
		// TODO Auto-generated constructor stub
	}

	Hashtable<String,String> env;
	protected DirContext connection;
	protected int protocol = LdapConnection.LDAP_PROTOCOL;
	protected LDAPTreeParser tparser;
	private List mountPoints;
	public static final String alarmCfgRoot = "ou=EpicsAlarmCfg";
	public String[] result= new String[0];
	
	private void initializeConnection() throws Exception{
		if (env==null) {
			throw new Exception("Parameters for connection not given.");
		}
		else {
			if (protocol == LdapConnection.LDAP_PROTOCOL){
		        env.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
		        tparser = new LDAPTreeParser();		    
			}
			connection=new InitialDirContext(env);
		}
	}

	private void resetConnection(){
		if (connection != null)
			try {
				connection.close();
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		connection = null;
	}

	private String[] getSubDirs(){
		ArrayList<String> strcoll = new ArrayList<String>();
		try {
			initializeConnection();
			SearchControls ctrl = new SearchControls();
			String name,rname;
			ctrl.setSearchScope(SearchControls.ONELEVEL_SCOPE); //set to search the whole tree
			//remove when you solve the size limit problem or TODO: workaround
			
			NamingEnumeration enumr = connection.search(alarmCfgRoot,"objectclass=*",ctrl);
			//NamingEnumeration enumr = connection.search("","(epicsAlarmSeverity=*)",ctrl);
			while (enumr.hasMore()){
				SearchResult result = (SearchResult)enumr.next();
				rname = result.getName();
				//only getName gives you name without 'o=DESY, c=DE'
				rname = tparser.specialClean(rname);
				name = tparser.getMyName(rname);
				strcoll.add(name);
			}			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			resetConnection();
		}
		try {
			return (String[])(strcoll.toArray(new String[strcoll.size()]));
		}
		catch (Exception e){
			e.printStackTrace();
			return new String[0];
		}
	}
	
	protected void configureShell(Shell newShell)
	{
	    super.configureShell(newShell);
	    newShell.setText("Add new mount point");
	}

	protected Control createDialogArea(Composite parent)
	{	
	    Composite composite = (Composite)super.createDialogArea(parent);
	    GridLayout layout = new GridLayout();
	    layout.numColumns = 1;
	    composite.setLayout(layout);
	    (new Label(composite, 0)).setText("Select mount point to add:");
	    mountPoints = new List(composite,SWT.SINGLE);
	    mountPoints.setItems(getSubDirs());
	    GridData gridData = new GridData(256);
	    mountPoints.setLayoutData(new GridData(256));
	    return composite;
	}

	private void save()
	{
		result = mountPoints.getSelection();
	}

	public String getResult() {
		if (result[0] == null) return "";
		return result[0];
	}

	protected void okPressed()
	{
	    save();
	    setReturnCode(0);
	    super.close();
	}

	
	
}
