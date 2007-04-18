package org.csstudio.alarm.treeView.views.models;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.PriorityQueue;

import javax.naming.NamingException;

import org.csstudio.alarm.treeView.cacher.Attributer;
import org.csstudio.alarm.treeView.cacher.CachingThread;
import org.eclipse.ui.views.properties.IPropertySource;


public class ContextTreeObject implements ISimpleTreeObject{
	private String name;
	private Alarm myPassiveAlarmState;
	private Alarm myActiveAlarmState;
	private Hashtable<String,String> attributes;
	public int alarmSeverity=0;
	private ContextTreeObject parent;
	private PriorityQueue<Alarm> alarms;
	private PriorityQueue<Alarm> unacknowledgedAlarms;
	protected CachingThread cthr;
	protected ContextTreeObjectPropertySource treeObjPS;
	protected boolean atribsget= false; //when we get attribs from server, this is set to true
	private String dn;
	private String rname;
	protected Attributer atter;

	//the same ugly procedure to get Attributer from the root
	public Attributer getAttributeGetter(){
		if (atter==null) atter = parent.getAttributeGetter();
		return atter;
	}
	
	public void setAttribsget(){
		atribsget = true;
	}
	
	public boolean isAtribsget() {
		return atribsget;
	}

	public void setAtribsget(boolean atribsget) {
		this.atribsget = atribsget;
	}

	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public ContextTreeObject(){
		dn = new String("");
		alarms = new PriorityQueue<Alarm>();
		unacknowledgedAlarms = new PriorityQueue<Alarm>();
		attributes = new Hashtable<String,String>();		
	}
		
	
	public ContextTreeObject(ContextTreeParent parent)
	{
		this();
		this.parent=parent;
		this.cthr = parent.cthr;
		parent.addChild(this);
	}


	public ContextTreeObject getParent() {
		return parent;
	}
	
	public void addAttribute (String name, String value){
		getAttributes().put(name,value);
	}
	
	public String getValuebyName(String name){
		return getAttributes().get(name);
	}
	
	public void updateAttribute(String name, String newvalue){
		if (getAttributes().containsKey(name)){
			String oldvalue = getAttributes().get(name);
			getAttributes().put(name,newvalue);
			//also we have to update attribute on the server
			try {
				getAttributeGetter().updateAttribute(getRname(),name,newvalue);
			} catch (NamingException e) {
				// of course we must return the old value if anything goes wrong
				getAttributes().put(name,oldvalue);
				e.printStackTrace();
			}
		}
		else {
			System.out.println("Adding an attribute if it is not yet set was not supported. Maybe it is also not needed");
		}
	}
	public Hashtable<String, String> getAttributes() {
		if ((attributes.isEmpty()) || (attributes == null)) {
			//if we haven't got attributes - we got them from server
			try {
				attributes = getAttributeGetter().retrieveAttributes(getRname());
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				attributes.put("ERROR","This node is not found in directory structure!");
			}
		}
		return attributes;
	}

	public Object getAdapter(Class adapter) {
		if (adapter == IPropertySource.class){
			if (treeObjPS == null){
				treeObjPS = new ContextTreeObjectPropertySource(this,name);
			}
			return treeObjPS;
		}
		return null;
	}
	
	public String toString(){
		return getName();
	}

	public Alarm getMaxAlarmObject() {
		// TODO Auto-generated method stub
		return alarms.peek();
	}
	
	public int getMaxAlarm() {
		// TODO Auto-generated method stub
		if (alarms.isEmpty()) {return 0;}
		return alarms.peek().getSeverity();
	}

	public Alarm getMaxUnacknowledgedAlarmObject() {
		// TODO Auto-generated method stub
		return unacknowledgedAlarms.peek();
	}
	
	public int getMaxUnacknowledgedAlarm() {
		// TODO Auto-generated method stub
		if (unacknowledgedAlarms.isEmpty()) {return 0;}
		return unacknowledgedAlarms.peek().getSeverity();
	}
	
	
	public void triggerAlarm(int severity, String source){
		distributeAlarm(new Alarm(severity,source));
	}
	
	public void triggerAlarm(Alarm alm){
		//i know this is stupid to replace alarm with another object just because i don't have an idea how to update my priority queues effectively after modifying their elements
		//with this method we assume that ONLY ONE alarm state is possible per leaf(node with no children)!!! - otherwise we have to number our alarms in leaf
		if (alm.isUnAcknowledged()) {
			if (myActiveAlarmState!=null){disableAlarm(myActiveAlarmState);}
			myActiveAlarmState = alm;
		}
		else {
			if (myPassiveAlarmState!=null){disableAlarm(myPassiveAlarmState);}
			myPassiveAlarmState = alm;
		}
		distributeAlarm(alm);
	}
	
	public void distributeAlarm(Alarm alm){
		if (alm.isUnAcknowledged()) unacknowledgedAlarms.add(alm);
		else alarms.add(alm);
		if (getParent()!=null){
			getParent().distributeAlarm(alm);
		}
	}
	
	public void acknowledgeAlarm(Alarm alm){
		if (alm.isUnAcknowledged()){
			if (myActiveAlarmState == alm) {
				myActiveAlarmState = null;
			}
		}
		else {
			if (myPassiveAlarmState == alm) {
				myPassiveAlarmState = null;
			}			
		}
		disableAlarm(alm);
	}
	public void disableAlarm(Alarm alm) {
		if (alm.isUnAcknowledged()) unacknowledgedAlarms.remove(alm);
		else alarms.remove(alm);
		if (getParent()!=null){
			getParent().disableAlarm(alm);
		}
	}
	
	public int getAlarmCount(){
		return alarms.size();
	}

	public int getUnacknowledgedAlarmCount(){
		return unacknowledgedAlarms.size();
	}
	
	public Iterator<Alarm> getUnacknowledgedAlarmList(){
		return unacknowledgedAlarms.iterator();
	}
	
	
	public Iterator<Alarm> getAlarmList(){
		return alarms.iterator();
	}

	public String getRname() {
		return rname;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public void setRname(String rname) {
		this.rname = rname;
	}

	public String getDn() {
		return dn;
	}

	public int getTotalAlarmCount() {
		return getAlarmCount()+getUnacknowledgedAlarmCount();
	}
	
	public void acknowledgeMyAlarmState(){
		if (myActiveAlarmState!=null){
			disableAlarm(myActiveAlarmState);
			myActiveAlarmState=null;
		}
	}
	
	public Alarm getMyActiveAlarmState() {
		return myActiveAlarmState;
	}

	public Alarm getMyPassiveAlarmState() {
		return myPassiveAlarmState;
	}
	
}
