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
 package org.csstudio.alarm.treeView.views;

import java.util.ArrayList;

import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.csstudio.alarm.treeView.ldap.LdapNameUtils;
import org.csstudio.utility.ldap.engine.Engine;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

// used for preferences
// TODO: move into preferences package?
// FIXME: this is still from the first alarm tree (by Jurij Kodre?) and should probably be rewritten
public class AddMountPointDlg extends Dialog {
	public AddMountPointDlg(Shell parentShell) {
		super(parentShell);
		// TODO Auto-generated constructor stub
	}

	protected DirContext connection;
	private List mountPoints;
	public static final String alarmCfgRoot = "ou=EpicsAlarmCfg";
	public String[] result= new String[0];
	
	private void initializeConnection() throws Exception{
		connection= Engine.getInstance().getLdapDirContext();
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
				rname = LdapNameUtils.removeQuotes(rname);
				name = LdapNameUtils.simpleName(rname);
				strcoll.add(name);
			}			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
