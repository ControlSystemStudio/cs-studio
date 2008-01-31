package org.csstudio.alarm.treeView.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * A tree node that is the root node of a subtree.
 * 
 * @author Joerg Rathlev
 */
public class SubtreeNode implements IAdaptable, IAlarmTreeNode {
	
	private List<IAlarmTreeNode> children;
	private SubtreeNode parent;
	private String name;
	private Severity highestChildSeverity;
	private Severity highestUnacknowledgedChildSeverity;

	
	/**
	 * Creates a new node with the specified parent. The node will register
	 * itself as a child at the parent node.
	 * @param parent the parent node.
	 * @param name the name of this node.
	 */
	public SubtreeNode(SubtreeNode parent, String name) {
		if (name == null) 
			throw new NullPointerException("name must not be null");
		
		this.parent = parent;
		this.name = name;
		children = new ArrayList<IAlarmTreeNode>();
		if (parent != null) {
			parent.children.add(this);
		}
		highestChildSeverity = Severity.NO_ALARM;
		highestUnacknowledgedChildSeverity = Severity.NO_ALARM;
	}
	
	/**
	 * Creates a new node that does not have a parent node.
	 * @param name the name of this node.
	 */
	public SubtreeNode(String name){
		this(null, name);
	}
	
	/**
	 * Adds the specified child node to the list of this node's children. Note:
	 * it is not checked whether the parent node of the child is correctly set
	 * to this node. This method is intended to be called only by constructors
	 * of nodes.
	 * 
	 * @param child the child node to add. 
	 */
	void addChild(IAlarmTreeNode child) {
		children.add(child);
	}
	
	/**
	 * Returns the highest severity of the alarms in the subtree below this
	 * node.
	 */
	public Severity getAlarmSeverity() {
		return highestChildSeverity;
	}
	
	/**
	 * Returns the highest severity of the unacknowledged alarms in the subtree
	 * below this node.
	 */
	public Severity getUnacknowledgedAlarmSeverity() {
		return highestUnacknowledgedChildSeverity;
	}
	
	/**
	 * Returns whether this node has any children.
	 * @return {@code true} if this node has children, {@code false} otherwise.
	 */
	public boolean hasChildren() {
		return children.size() > 0;
	}
	
	/**
	 * Returns the children of this node.
	 */
	public IAlarmTreeNode[] getChildren() {
		return children.toArray(new IAlarmTreeNode[children.size()]);		
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

	public Object getAdapter(@SuppressWarnings("unchecked") Class adapter) {
		if (adapter == IPropertySource.class){
			return new AlarmTreeNodePropertySource(this);
		}
		return null;
	}

	/**
	 * Returns a string representation of this node.
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasAlarm() {
		return highestChildSeverity.isAlarm() || highestUnacknowledgedChildSeverity.isAlarm();
	}

	/**
	 * Signals to this node that the alarm severity of one of its children
	 * changed. This method must be called by children of this node when their
	 * severity changes. If the change causes a severity change for this node,
	 * this node will in turn notify its parent, so that the highest severity
	 * is propagated to the root of the tree.
	 * 
	 * @param child the child node whose severity status has changed.
	 */
	void childSeverityChanged(IAlarmTreeNode child) {
		boolean thisNodeChanged = false;
		
		// If the severity is higher than the current highest severity, simply
		// set it as the new highest severity and propagate it to the parent.
		// Otherwise, there might still be other children with a higher
		// severity. So we look for the highest severity of the children, and
		// propagate that upwards (if it is different from the current
		// severity).
		
		// Start with the active alarm severity
		Severity active = child.getAlarmSeverity();
		if (active.compareTo(highestChildSeverity) > 0) {
			highestChildSeverity = active;
			thisNodeChanged = true;
		} else {
			active = findHighestChildSeverity();
			if (!active.equals(highestChildSeverity)) {
				highestChildSeverity = active;
				thisNodeChanged = true;
			}
		}
		// Now the highest unacknowledged severity
		Severity unack = child.getUnacknowledgedAlarmSeverity();
		if (unack.compareTo(highestUnacknowledgedChildSeverity) > 0) {
			highestUnacknowledgedChildSeverity = unack;
			thisNodeChanged = true;
		} else {
			unack = findHighestUnacknowledgedChildSeverity();
			if (!unack.equals(highestUnacknowledgedChildSeverity)) {
				highestUnacknowledgedChildSeverity = unack;
				thisNodeChanged = true;
			}
		}
		
		// Notify parent if this node changed
		if (thisNodeChanged && parent != null) {
			parent.childSeverityChanged(this);
		}
	}

	/**
	 * Returns the highest severity of the children of this node.
	 */
	private Severity findHighestChildSeverity() {
		Severity highest = Severity.NO_ALARM;
		for (IAlarmTreeNode node : children) {
			if (node.getAlarmSeverity().compareTo(highest) > 0) {
				highest = node.getAlarmSeverity();
			}
		}
		return highest;
	}
	
	/**
	 * Returns the highest unacknowledged severity of the children of this node.
	 */
	private Severity findHighestUnacknowledgedChildSeverity() {
		Severity highest = Severity.NO_ALARM;
		for (IAlarmTreeNode node : children) {
			if (node.getUnacknowledgedAlarmSeverity().compareTo(highest) > 0) {
				highest = node.getUnacknowledgedAlarmSeverity();
			}
		}
		return highest;
	}

	/**
	 * Returns the direct child with the specified name. If no such child
	 * exists, returns {@code null}.
	 * @param name the name of the child.
	 */
	public IAlarmTreeNode getChild(String name) {
		return searchNode(name, false);
	}
	
	/**
	 * Searches the subtree rooted at this node for a node with the specified
	 * name. If the node is not found, returns {@code null}.
	 * @param name the name of the node.
	 */
	public ProcessVariableNode findProcessVariableNode(String name) {
		return (ProcessVariableNode) searchNode(name, true);
	}

	/**
	 * Searches the children of this node for a node with the specified name.
	 * If {@code recursive} is set to {@code true}, recursively searches the
	 * full subtree rooted at this node.
	 * 
	 * @param name the name of the node to search for.
	 * @param recursive set to {@code true} to perform a recursive search. If
	 *        set to {@code false}, only the direct children of this node will
	 *        be searched.
	 * @return the node with the specified name, or {@code null} if no such
	 *        node was found.
	 */
	private IAlarmTreeNode searchNode(String name, boolean recursive) {
		for (IAlarmTreeNode child : children) {
			if (child.getName().equals(name)) {
				return child;
			}
			if (recursive && child instanceof SubtreeNode) {
				IAlarmTreeNode recursionResult =
					((SubtreeNode) child).searchNode(name, true);
				if (recursionResult != null) {
					return recursionResult;
				}
			}
		}
		// not found
		return null;
	}

}
