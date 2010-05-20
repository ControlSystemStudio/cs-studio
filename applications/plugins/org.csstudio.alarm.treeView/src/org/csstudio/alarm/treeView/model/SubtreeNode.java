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
 package org.csstudio.alarm.treeView.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.csstudio.alarm.service.declaration.LdapEpicsAlarmCfgObjectClass;

/**
 * A tree node that is the root node of a subtree.
 *
 * @author Joerg Rathlev
 */
public final class SubtreeNode extends AbstractAlarmTreeNode implements IAlarmSubtreeNode  {

	/**
	 * This node's children.
	 */
	private final Map<String, IAlarmTreeNode> _childrenSubtreeMap;

    /**
     * This node's children.
     */
    private final Map<String, IAlarmTreeNode> _childrenPVMap;


	/**
	 * The highest severity of the child nodes.
	 */
	private Severity _highestChildSeverity;

	/**
	 * The highest unacknowledged severity of the child nodes.
	 */
	private Severity _highestUnacknowledgedChildSeverity;


	/**
	 * SubtreeNode Builder.
	 *
	 * @author bknerr
	 * @author $Author$
	 * @version $Revision$
	 * @since 28.04.2010
	 */
	public static final class Builder {
	    private final String _name;
	    private final LdapEpicsAlarmCfgObjectClass _objectClass;
	    private IAlarmSubtreeNode _parent;

	    public Builder(@Nonnull final String name, @Nonnull final LdapEpicsAlarmCfgObjectClass objectClass) {
	        _name = name;
	        _objectClass = objectClass;
	    }

	    public Builder setParent(final IAlarmSubtreeNode parent) {
	        _parent = parent;
	        return this;
	    }

	    public SubtreeNode build() {
	        final SubtreeNode node = new SubtreeNode(_name, _objectClass);
	        if (_parent != null) {
	            _parent.addSubtreeChild(node);
	        }
	        return node;
	    }
	}

	/**
	 * Creates a new node with the specified parent. The node will register
	 * itself as a child at the parent node.
	 *
	 * @param name the name of this node.
	 * @param objectClass the object class of this node.
	 */
	private SubtreeNode(@Nonnull final String name,
	                    @Nonnull final LdapEpicsAlarmCfgObjectClass objectClass) {
	    super(name, objectClass);

		_childrenPVMap = new HashMap<String, IAlarmTreeNode>();
		_childrenSubtreeMap = new HashMap<String, IAlarmTreeNode>();
		_highestChildSeverity = Severity.NO_ALARM;
		_highestUnacknowledgedChildSeverity = Severity.NO_ALARM;
	}

    /**
     * {@inheritDoc}
     */
	public final void removeChild(@Nonnull final IAlarmTreeNode child) {
		final String childName = child.getName();
        if (_childrenPVMap.containsKey(childName)) {
		    _childrenPVMap.remove(childName);
		} else if (_childrenSubtreeMap.containsKey(childName)) {
		    final SubtreeNode node = (SubtreeNode) _childrenSubtreeMap.get(childName);
		    if (node.hasChildren()) {
		        return;
		    }
		    _childrenSubtreeMap.remove(childName);
		}
		refreshSeverities();
	}

	@Nonnull
	public LdapEpicsAlarmCfgObjectClass getObjectClass() {
		return _objectClass;
	}

	/**
	 * Returns the object class recommended for a <code>SubtreeNode</code>
	 * that is a child of this node.
	 *
	 * @return the object class recommended for subtree children of this node.
	 */
	@Nonnull
	public Set<LdapEpicsAlarmCfgObjectClass> getRecommendedChildSubtreeClasses() {
		return _objectClass.getNestedContainerClasses();
	}

    /**
     * {@inheritDoc}
     */
    public void addPVChild(final IAlarmTreeNode child) {
        final String name = child.getName();
        if (_childrenPVMap.containsKey(name)) {
            return;
        }
        _childrenPVMap.put(name, child);
        childSeverityChanged(child);
        child.setParent(this);
    }

    /**
     * {@inheritDoc}
     */
    public void addSubtreeChild(final IAlarmSubtreeNode child) {
        final String name = child.getName();
        if (_childrenSubtreeMap.containsKey(name)) {
            return;
        }
        _childrenSubtreeMap.put(name, child);
        childSeverityChanged(child);
        child.setParent(this);
    }

	/**
	 * Returns the highest severity of the alarms in the subtree below this
	 * node.
	 *
	 * @return the alarm severity for this node.
	 */
	public final Severity getAlarmSeverity() {
		return _highestChildSeverity;
	}

	/**
	 * Returns the highest severity of the unacknowledged alarms in the subtree
	 * below this node.
	 *
	 * @return the unacknowledged alarm severity for this node.
	 */
	public final Severity getUnacknowledgedAlarmSeverity() {
		return _highestUnacknowledgedChildSeverity;
	}

	/**
	 * Returns whether this node has any children.
	 * @return {@code true} if this node has children, {@code false} otherwise.
	 */
	public final boolean hasChildren() {
		return !_childrenSubtreeMap.isEmpty() || !_childrenPVMap.isEmpty();
	}

	/**
	 * Returns the children of this node.
	 * @return the children of this node.
	 */
	@Nonnull
	public final IAlarmTreeNode[] getChildren() {

	    final List<IAlarmTreeNode> children =
	        new ArrayList<IAlarmTreeNode>(_childrenPVMap.size() + _childrenSubtreeMap.size());
	    children.addAll(_childrenPVMap.values());
	    children.addAll(_childrenSubtreeMap.values());

		return children.toArray(new IAlarmTreeNode[children.size()]);
	}

	/**
	 * {@inheritDoc}
	 */
	public final boolean hasAlarm() {
		return _highestChildSeverity.isAlarm() || _highestUnacknowledgedChildSeverity.isAlarm();
	}


	/**
	 * {@inheritDoc}
	 */
	public final void childSeverityChanged(@Nonnull final IAlarmTreeNode child) {
		boolean thisNodeChanged = false;

		// If the severity is higher than the current highest severity, simply
		// set it as the new highest severity and propagate it to the parent.
		// Otherwise, there might still be other children with a higher
		// severity. So we look for the highest severity of the children, and
		// propagate that upwards (if it is different from the current
		// severity).

		// Start with the active alarm severity
		final Severity active = child.getAlarmSeverity();
		if (active.compareTo(_highestChildSeverity) > 0) {
			_highestChildSeverity = active;
			thisNodeChanged = true;
		} else {
			thisNodeChanged |= refreshActiveSeverity();
		}
		// Now the highest unacknowledged severity
		final Severity unack = child.getUnacknowledgedAlarmSeverity();
		if (unack.compareTo(_highestUnacknowledgedChildSeverity) > 0) {
			_highestUnacknowledgedChildSeverity = unack;
			thisNodeChanged = true;
		} else {
			thisNodeChanged |= refreshHighestUnacknowledgedSeverity();
		}

		// Notify parent if this node changed
		final IAlarmSubtreeNode parent = getParent();
		if (thisNodeChanged && (parent != null)) {
			parent.childSeverityChanged(this);
		}
	}

	/**
	 * Refreshes the severites of this node by searching its children for the
	 * highest severities.
	 */
	private void refreshSeverities() {
		boolean thisNodeChanged = false;
		thisNodeChanged |= refreshActiveSeverity();
		thisNodeChanged |= refreshHighestUnacknowledgedSeverity();

		final IAlarmSubtreeNode parent = getParent();
		if (thisNodeChanged && (parent != null)) {
			parent.childSeverityChanged(this);
		}
	}

	/**
	 * Refreshes the active severity of this node by searching its children for
	 * the highest severity.
	 *
	 * @return <code>true</code> if the severity of this node was changed,
	 *         <code>false</code> otherwise.
	 */
	private boolean refreshActiveSeverity() {
		final Severity s = findHighestChildSeverity();
		if (!s.equals(_highestChildSeverity)) {
			_highestChildSeverity = s;
			return true;
		}
		return false;
	}

	/**
	 * Refreshes the highest unacknowledged severity of this node by searching
	 * its children for the highest unacknowledged severity.
	 *
	 * @return <code>true</code> if the severity of this node was changed,
	 *         <code>false</code> otherwise.
	 */
	private boolean refreshHighestUnacknowledgedSeverity() {
		final Severity s = findHighestUnacknowledgedChildSeverity();
		if (!s.equals(_highestUnacknowledgedChildSeverity)) {
			_highestUnacknowledgedChildSeverity = s;
			return true;
		}
		return false;
	}

	/**
	 * Returns the highest severity of the children of this node.
	 * @return the highest severity of the children of this node.
	 */
	private Severity findHighestChildSeverity() {
		Severity highest = Severity.NO_ALARM;

		final List<IAlarmTreeNode> children = new ArrayList<IAlarmTreeNode>(_childrenPVMap.size() + _childrenSubtreeMap.size());
		children.addAll(_childrenPVMap.values());
		children.addAll(_childrenSubtreeMap.values());

		for (final IAlarmTreeNode node : children) {
			if (node.getAlarmSeverity().compareTo(highest) > 0) {
				highest = node.getAlarmSeverity();
			}
		}
		return highest;
	}

	/**
	 * Returns the highest unacknowledged severity of the children of this node.
	 * @return the highest unacknowledged severity of the children of this node.
	 */
	private Severity findHighestUnacknowledgedChildSeverity() {
		Severity highest = Severity.NO_ALARM;
		final List<IAlarmTreeNode> children = new ArrayList<IAlarmTreeNode>(_childrenPVMap.size() + _childrenSubtreeMap.size());
		children.addAll(_childrenPVMap.values());
		children.addAll(_childrenSubtreeMap.values());

		for (final IAlarmTreeNode node : children) {
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
	 * @return the direct child with the specified name.
	 */
	@CheckForNull
	public IAlarmTreeNode getChild(@Nonnull final String name) {
	    if (_childrenPVMap.containsKey(name)) {
	        return _childrenPVMap.get(name);
	    }
		return _childrenSubtreeMap.get(name);
	}

	/**
	 * Finds all process variable nodes with the given name below this node. If
	 * no nodes are found, returns an empty list.
	 * @param name the name of the nodes.
	 * @return a list of the nodes.
	 */
	@Nonnull
	public List<ProcessVariableNode> findProcessVariableNodes(@Nonnull final String name) {

	    final List<ProcessVariableNode> result = new ArrayList<ProcessVariableNode>();

	    final ProcessVariableNode pv = (ProcessVariableNode) _childrenPVMap.get(name);
	    if (pv != null) {
	        result.add(pv);
	    }

		for (final IAlarmTreeNode child : _childrenSubtreeMap.values()) {

		    final List<ProcessVariableNode> subList = ((SubtreeNode) child).findProcessVariableNodes(name);
		    result.addAll(subList);
		}
		return result;
	}

	/**
	 * Finds all process variable nodes below this node. If
	 * no nodes are found, returns an empty list.
	 * @return a list of the nodes.
	 */
	@SuppressWarnings("unchecked")
    @Nonnull
	public List<ProcessVariableNode> findAllProcessVariableNodes() {

	    final List<ProcessVariableNode> result = new ArrayList<ProcessVariableNode>();
	    result.addAll((Collection<? extends ProcessVariableNode>) _childrenPVMap.values());

	    for (final IAlarmTreeNode child : _childrenSubtreeMap.values()) {
	        final List<ProcessVariableNode> subList = ((SubtreeNode) child).findAllProcessVariableNodes();
	        result.addAll(subList);
	    }
	    return result;
	}

	/**
	 * Returns a collection of the PV nodes (leaf nodes) below this subtree
	 * node that have unacknowledged alarms.
	 *
	 * @return a collection of the PV nodes with unacknowledged alarms.
	 */
	@Nonnull
	public Collection<ProcessVariableNode> collectUnacknowledgedAlarms() {

	    final Collection<ProcessVariableNode> result = new ArrayList<ProcessVariableNode>();

	    final List<IAlarmTreeNode> children = new ArrayList<IAlarmTreeNode>(_childrenPVMap.size() + _childrenSubtreeMap.size());
	    children.addAll(_childrenPVMap.values());
	    children.addAll(_childrenSubtreeMap.values());

		for (final IAlarmTreeNode child : children) {
		    if (child instanceof SubtreeNode) {
		        result.addAll( ((SubtreeNode) child).collectUnacknowledgedAlarms());
		    } else if (child instanceof ProcessVariableNode) {
		        if (child.getUnacknowledgedAlarmSeverity() != Severity.NO_ALARM) {
		            result.add((ProcessVariableNode) child);
		        }
		    }
		}

		return result;
	}


}
