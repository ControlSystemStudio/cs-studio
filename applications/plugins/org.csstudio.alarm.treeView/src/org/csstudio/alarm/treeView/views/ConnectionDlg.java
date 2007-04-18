package org.csstudio.alarm.treeView.views;

import org.csstudio.alarm.treeView.LdaptreePlugin;
import org.csstudio.alarm.treeView.preferences.PreferenceConstants;
import org.csstudio.alarm.treeView.views.models.LdapConnection;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;




public class ConnectionDlg extends Dialog {

	private LdapConnection con;
	private Text url;
	private Text user;
	private Text password;
	private Combo protocol;
	private Button savePassword;
	protected ConnectionDlg(Shell parentShell, LdapConnection con) {
		super(parentShell);
		this.con = con;
		// TODO Auto-generated constructor stub
	}
	protected void configureShell(Shell newShell)
	{
	    super.configureShell(newShell);
	    newShell.setText("Ldap Connection");
	}

	protected Control createDialogArea(Composite parent)
	{	
		LdaptreePlugin myPluginInstance = LdaptreePlugin.getDefault();
//	    WorkbenchHelp.setHelp(parent, "com.cyrone.ldapEclipse.ConnectionDialog");
	    Composite composite = (Composite)super.createDialogArea(parent);
	    GridLayout layout = new GridLayout();
	    layout.numColumns = 2;
	    composite.setLayout(layout);
	    (new Label(composite, 0)).setText("Connection type:");
	    protocol = new Combo(composite, 2048);
	    protocol.add("LDAP");
	    protocol.add("EDS");
	    if (con.getProtocol()>0) {
	    	protocol.select(con.getProtocol()-1);
	    }
	    else {
	    	protocol.select(0);
	    }
	    (new Label(composite, 0)).setText("URL:");
	    url = new Text(composite, 2048);
	    url.setText(con.getUrl().equals("") ? myPluginInstance.getPluginPreferences().getString(PreferenceConstants.URL) : con.getUrl());
	    GridData gridData = new GridData(256);
	    gridData.widthHint = convertWidthInCharsToPixels(Math.max(con.getUrl().length(), 40));
	    url.setLayoutData(gridData);
	    
	    
	    (new Label(composite, 0)).setText("Username:");
	    user = new Text(composite, 2048);
	    user.setText(con.getPrincipal().equals("") ? myPluginInstance.getPluginPreferences().getString(PreferenceConstants.USER) : con.getPrincipal());
	    user.setLayoutData(new GridData(256));
	    (new Label(composite, 0)).setText("Password:");
	    password = new Text(composite, 2048);
	    password.setEchoChar('*');
	    password.setText(con.getCredentials().equals("") ? myPluginInstance.getPluginPreferences().getString(PreferenceConstants.PASSWORD) : con.getCredentials());
	    password.setLayoutData(new GridData(256));
	    (new Label(composite, 0)).setText("Save Password:");
	    savePassword = new Button(composite, 32);
	    savePassword.setSelection(con.isSavePassword());
	    new Label(composite, 0);
/*	    testCon = new Button(composite, 0x20000);
	    testCon.setLayoutData(new GridData(128));
	    testCon.setText("Test Connection");
	    testCon.addMouseListener(new MouseListener() {

	        public void mouseDoubleClick(MouseEvent mouseevent)
	        {
	        }

	        public void mouseDown(MouseEvent mouseevent)
	        {
	        }

	        public void mouseUp(MouseEvent e)
	        {
	            LdapConnection test = new LdapConnection(null);
	            test.setUrl(url.getText());
	            test.setPrincipal(user.getText());
	            test.setCredentials(password.getText());
	            DirContext ctx;
	            try
	            {
	                ctx = test.getConnection();
	            }
	            catch(NamingException e1)
	            {
	                (new ExceptionDialog(testCon.getShell(), "Could not connect to directory", e1)).open();
	                return;
	            }
	            SearchControls c = new SearchControls();
	            c.setSearchScope(1);
	            try
	            {
	                ctx.search("", "objectclass=*", c);
	            }
	            catch(NamingException e2)
	            {
	                (new ExceptionDialog(testCon.getShell(), "Could not read the top level element", e2)).open();
	                return;
	            }
	            MessageDialog.openInformation(testCon.getShell(), "Success", "Connected successfully to the directory");
	        }

	    });*/
	    return composite;
	}

	private void save()
	{
	    con.setUrl(url.getText());
	    con.setCredentials(password.getText());
	    con.setPrincipal(user.getText());
	    con.setSavePassword(savePassword.getSelection());
	    con.setProtocol(protocol.getSelectionIndex()+1);
	}

	protected void okPressed()
	{
	    save();
	    setReturnCode(0);
	    super.close();
	}

	
}
