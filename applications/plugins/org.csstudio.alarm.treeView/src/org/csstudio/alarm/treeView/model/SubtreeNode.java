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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.csstudio.utility.ldap.LdapObjectClass;

/**
 * A tree node that is the root node of a subtree.
 *
 * @author Joerg Rathlev
 */
public final class SubtreeNode extends AbstractAlarmTreeNode implements IAlarmSubtreeNode  {

	/**
	 * This node's children.
	 */
	private final Map<String, IAlarmTreeNode> _childrenMap;

	/**
	 * The name of this node.
	 */
	private String _name;

	/**
	 * The object class of this node in the directory.
	 */
	private final LdapObjectClass _objectClass;

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
	    private final LdapObjectClass _objectClass;
	    private IAlarmSubtreeNode _parent;

	    public Builder(@Nonnull final String name, @Nullable final LdapObjectClass objectClass) {
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
	            _parent.addChild(node);
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
	                    @Nullable final LdapObjectClass objectClass) {

		this._name = name;
		this._objectClass = objectClass;
		_childrenMap = new HashMap<String, IAlarmTreeNode>();
		_highestChildSeverity = Severity.NO_ALARM;
		_highestUnacknowledgedChildSeverity = Severity.NO_ALARM;
	}

    /**
     * {@inheritDoc}
     */
	public final void removeChild(@Nonnull final IAlarmTreeNode child) {
		if (!_childrenMap.containsKey(child.getName())) {
			return;
		}
		if ((child instanceof SubtreeNode)
				&& ((SubtreeNode) child).hasChildren()) {
			return;
		}
		_childrenMap.remove(child.getName());
		refreshSeverities();
	}

	/**
	 * {@inheritDoc}
	 */
	public final LdapObjectClass getObjectClass() {
		return _objectClass;
	}

	/**
	 * Returns the object class recommended for a <code>SubtreeNode</code>
	 * that is a child of this node.
	 *
	 * @return the object class recommended for subtree children of this node.
	 *         <code>null</code> if there is no recommended class.
	 */
	public final LdapObjectClass getRecommendedChildSubtreeClass() {
		return _objectClass.getNestedContainerClass();
	}

	/**
	 * Returns the name of object in the directory which this node represents.
	 * The name depends on the object class and name of this node and the object
	 * classes and names of this node's parent nodes.
	 *
	 * @return the name of this node in the directory.
	 * @deprecated this method does not work correctly if the name of this node
	 *             or one of its parent nodes contains special characters that
	 *             need escaping. Use {@link #getLdapName()} instead.
	 */
//	@Deprecated
//	public final String getDirectoryName() {
//		final StringBuilder result = new StringBuilder();
//		result.append(getObjectClass().getRdnAttribute());
//		result.append("=");
//		result.append(_name);
//		if (_parent.getObjectClass() != null) {
//			// If the parent has an object class (i.e., it is not the root node
//			// of the tree), its directory name must be appended to the result
//			result.append(",");
//			result.append(_parent.getDirectoryName());
//		}
//		return result.toString();
//	}

    /**
     * {@inheritDoc}
     */
	@CheckForNull
    public final LdapName getLdapName() {
		try {
			if (_objectClass == null) {
				return new LdapName("");
			}

			final LdapName result = new LdapName(Collections.singletonList(
					new Rdn(_objectClass.getRdnType(), _name)));
			final IAlarmSubtreeNode parent = getParent();
			if (parent != null) {
				result.addAll(0, parent.getLdapName());
			}
			return result;
		} catch (final InvalidNameException e) {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public final void addChild(@Nonnull final IAlarmTreeNode child) {
	    if (_childrenMap.containsKey(child.getName())) {
	        return;
	    }
		_childrenMap.put(child.getName(), child);
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
		return _childrenMap.size() > 0;
	}

	/**
	 * Returns the children of this node.
	 * @return the children of this node.
	 */
	@Nonnull
	public final IAlarmTreeNode[] getChildren() {
		return _childrenMap.values().toArray(new IAlarmTreeNode[_childrenMap.size()]);
	}

	/**
	 * {@inheritDoc}
	 */
	public final String getName() {
		return _name;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void setName(final String name) {
		_name = name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return _name;
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
		for (final IAlarmTreeNode node : _childrenMap.values()) {
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
		for (final IAlarmTreeNode node : _childrenMap.values()) {
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
		return _childrenMap.get(name);
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

		for (final IAlarmTreeNode child : _childrenMap.values()) {

			if ((child instanceof ProcessVariableNode)
					&& child.getName().equals(name)) {
				result.add((ProcessVariableNode) child);
			} else if (child instanceof SubtreeNode) {
				final List<ProcessVariableNode> subList = ((SubtreeNode) child).findProcessVariableNodes(name);
				result.addAll(subList);
			}
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

		for (final IAlarmTreeNode child : _childrenMap.values()) {
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
