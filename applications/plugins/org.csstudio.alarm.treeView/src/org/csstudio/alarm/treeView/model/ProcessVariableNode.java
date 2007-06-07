package org.csstudio.alarm.treeView.model;

import org.csstudio.platform.model.IProcessVariable;
import org.eclipse.core.runtime.PlatformObject;

/**
 * A tree node that represents a process variable.
 * 
 * @author Joerg Rathlev
 */
public class ProcessVariableNode extends PlatformObject 
		implements IAlarmTreeNode, IProcessVariable {
	
	private SubtreeNode parent;
	private String name;
	private Alarm activeAlarm;
	
	/**
	 * Creates a new node for a process variable as a child of the specified
	 * parent.
	 * 
	 * @param parent the parent node for the node.
	 * @param name the name of the node.
	 */
	public ProcessVariableNode(SubtreeNode parent, String name) {
		if (parent == null || name == null)
			throw new NullPointerException("parent and name must not be null");
		
		this.parent = parent;
		parent.addChild(this);
		this.name = name;
		this.activeAlarm = null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public IAlarmTreeNode getParent() {
		return parent;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getTypeId() {
		return IProcessVariable.TYPE_ID;
	}
	
	/**
	 * Returns the severity of the alarm for this node. If there is no alarm
	 * for this node, returns NO_ALARM.
	 */
	public Severity getAlarmSeverity() {
		if (activeAlarm != null) {
			return activeAlarm.getSeverity();
		} else {
			return Severity.NO_ALARM;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean hasAlarm() {
		return activeAlarm != null;
	}
	
	/**
	 * Triggers the specified alarm at this node.
	 * @param alarm the alarm.
	 */
	public void triggerAlarm(Alarm alarm) {
		activeAlarm = alarm;
		// propagate the severity of the alarm to the parent node
		parent.propagateSeverity(alarm.getSeverity());
	}
	
	/**
	 * Cancels the alarm at this node. If there is no alarm, does nothing.
	 */
	public void cancelAlarm() {
		if (activeAlarm != null) {
			activeAlarm = null;
			parent.propagateSeverity(Severity.NO_ALARM);
		}
	}
	
	@Override
	public String toString() {
		return name;
	}
}
