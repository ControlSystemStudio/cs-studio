package org.csstudio.alarm.treeView.model;


/**
 * A node in the alarm tree.
 * 
 * @author Joerg Rathlev
 */
public interface IAlarmTreeNode {

	/**
	 * Returns the name of this node.
	 */
	public String getName();

	/**
	 * Returns the parent node of this node. If this node does not have a
	 * parent, returns {@code null}.
	 */
	public IAlarmTreeNode getParent();
	
	/**
	 * Returns the alarm severity for this node. If this node has children,
	 * returns the highest severity of the childrens' alarms. Returns severity
	 * NO_ALARM if this node is a subtree root without any children or if this
	 * node represents a process variable which is not in an alarm state.
	 * 
	 * @see #getUnacknowledgedAlarmSeverity()
	 */
	public Severity getAlarmSeverity();
	
	/**
	 * Returns the severity of the highest unacknowledged alarm for this node.
	 * If this node has children, returns the highest unacknowledged severity
	 * of the childrens' alarms. Returns severity NO_ALARM if this node is a
	 * subtree root without any children or if this node represents a process
	 * variable which doesn't have any unacknowledged alarms.
	 * 
	 * @see #getAlarmSeverity()
	 */
	public Severity getUnacknowledgedAlarmSeverity();

	/**
	 * Returns {@code true} if there is an alarm for this node or its children.
	 */
	public boolean hasAlarm();

}