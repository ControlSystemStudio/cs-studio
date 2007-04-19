package org.csstudio.alarm.treeView.views.models;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.csstudio.alarm.treeView.jms.AlarmTopicSubscriber;
import org.csstudio.alarm.treeView.jms.IMapMessageNotifier;
import org.csstudio.alarm.treeView.views.AlarmTreeView;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;


public class AlarmConnection extends AlarmTreeParent implements IMapMessageNotifier, ISimpleTreeParent {

	private String url;
	private AlarmTopicSubscriber ats;
	private String topicName;
	private LdapConnection linkHierarchy = null;

	
	public void mapHierarchy(LdapConnection linkHierarchy) {
		this.linkHierarchy = linkHierarchy;
	}

	public boolean isMapped(){
		if (linkHierarchy == null) return false;
		return true;
	}
	
	public AlarmConnection(String url, String topicName){
		this.url = url;
		this.topicName = topicName;
		ats = new AlarmTopicSubscriber(url);
		ats.openConnection();
		ats.subscribe(topicName);
	}
	
	
	public AlarmConnection(){
		url ="";
		topicName="";
		name ="";
	}
	
	public void startListening(){
		ats = new AlarmTopicSubscriber(url);
		ats.setMessageNotifier(this);
		ats.openConnection();
		ats.subscribe(topicName);
	}
	
	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public void stopListening(){
		ats.closeConnection();
	}
	
	public void notifyObject(MapMessage msg) {
		try {
			Date tstamp;
			String type = (String)msg.getObject("TYPE");
			if (type==null){
				System.out.println("You must specify message type!!!");
				return;
			}
			if (type.equals("Acknowledge")){
				String name = (String)msg.getObject("NAME");
				try {
					linkHierarchy.acknowledgeAlarmOnNodeName(name);
				} catch (NodeNotFoundException e) {
					// TODO Auto-generated catch block
					System.out.println("There is no alarm such as "+name);
				}
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IViewPart view = page.findView(AlarmTreeView.getID());
				if (view instanceof AlarmTreeView){
					((AlarmTreeView)view).refresh();
				}
				return;
			}
			String name = (String)msg.getObject("NAME");
			String severity = (String)msg.getObject("SEVERITY");
			if (severity == null) {severity="";} 
			int sever = 0;
			if (!(severity.equals("NO_ALARM")) && !(severity.equals(""))){
				if (severity.equals("MAJOR")){sever =7;}
				if (severity.equals("MINOR")){sever =4;}
				if (severity.equals("INVALID")) {sever=2;}
			}
			if (name == null) name = "Noname";
			Hashtable<String,String> props = new Hashtable<String,String>();
			for (Enumeration propets = msg.getPropertyNames(); propets.hasMoreElements();){
				String name1 = (String) propets.nextElement();
				String value = msg.getStringProperty(name1);
				if (value == null){value = "N/A";} 
				props.put(name1,value);
			}
			for (Enumeration propets = msg.getMapNames(); propets.hasMoreElements();){
				String name1 = (String) propets.nextElement();
				String value = msg.getObject(name1).toString();
				if (value == null){value = "N/A";} 
				props.put(name1,value);
			}
			tstamp = new Date(msg.getJMSTimestamp()); 
			props.put("Timestamp",tstamp.toString());
			AlarmTreeParent atp = new  AlarmTreeParent(this);
			atp.setSeverity(sever);
			atp.setName(name);
			atp.setProperties(props);
			if (type.equals("AlarmAck")){atp.setUnAcknowledged(true);}
			else atp.setUnAcknowledged(false);
			this.addChild(atp); //just for the same functionality
			if (linkHierarchy != null){
				try {
					linkHierarchy.triggerAlarmOnNode(atp);
				} catch (NodeNotFoundException e) {
					// TODO Auto-generated catch block
					System.out.println("Structure doesn't contain "+atp.getName()+".");
				}
			}
			msg.acknowledge();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart view = page.findView(AlarmTreeView.getID());
		if (view instanceof AlarmTreeView){
			((AlarmTreeView)view).refresh();
		}
	}

	public void disableAlarm(Alarm alm){
		linkHierarchy.disableAlarmOnNode(alm);
	}
	
	public String getUrl() {
		return url;
	}

	public String getName(){
		return url+" - "+topicName;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
