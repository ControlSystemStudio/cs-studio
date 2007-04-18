package org.csstudio.alarm.treeView.views;

import org.csstudio.alarm.treeView.LdaptreePlugin;
import org.csstudio.alarm.treeView.preferences.PreferenceConstants;
import org.csstudio.alarm.treeView.views.models.AlarmConnection;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class AlarmJMSDialog extends Dialog{

	private AlarmConnection con;
	private Text url;
	private Text topic;
	protected AlarmJMSDialog(Shell parentShell, AlarmConnection con) {
		super(parentShell);
		this.con = con;
	}
	protected void configureShell(Shell newShell)
	{
	    super.configureShell(newShell);
	    newShell.setText("Alarm JMS connection");
	}

	protected Control createDialogArea(Composite parent)
	{	
		LdaptreePlugin myPluginInstance = LdaptreePlugin.getDefault();
	    Composite composite = (Composite)super.createDialogArea(parent);
	    GridLayout layout = new GridLayout();
	    layout.numColumns = 2;
	    composite.setLayout(layout);
	    (new Label(composite, 0)).setText("URL:");
	    url = new Text(composite, 2048);
	    url.setText(con.getUrl().equals("") ? myPluginInstance.getPluginPreferences().getString(PreferenceConstants.JMSURL) : con.getUrl());
	    GridData gridData = new GridData(256);
	    gridData.widthHint = convertWidthInCharsToPixels(Math.max(con.getUrl().length(), 40));
	    url.setLayoutData(gridData);
	    (new Label(composite, 0)).setText("Topic:");
	    topic = new Text(composite, 2048);
	    topic.setText(con.getTopicName().equals("") ? myPluginInstance.getPluginPreferences().getString(PreferenceConstants.USER) : con.getTopicName());
	    topic.setLayoutData(new GridData(256));
	    return composite;
	}

	private void save()
	{
	    con.setUrl(url.getText());
	    con.setTopicName(topic.getText());
	}

	protected void okPressed()
	{
	    save();
	    setReturnCode(0);
	    super.close();
	}


}
