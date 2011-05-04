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
import java.util.List;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration;

import com.google.common.collect.Maps;

/**
 * A tree node that is the root node of a subtree.
 *
 * @author Joerg Rathlev
 */
public final class SubtreeNode extends AbstractAlarmTreeNode implements IAlarmSubtreeNode  {

	/**
	 * This node's children.
	 */
	private final Map<String, IAlarmSubtreeNode> _childrenSubtreeMap;

    /**
     * This node's children.
     */
    private final Map<String, IAlarmProcessVariableNode> _childrenPVMap;


	/**
	 * The highest severity of the child nodes.
	 */
	private EpicsAlarmSeverity _highestChildSeverity;

	/**
	 * The highest unacknowledged severity of the child nodes.
	 */
	private EpicsAlarmSeverity _highestUnacknowledgedChildSeverity;


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
	    private final LdapEpicsAlarmcfgConfiguration _configurationType;
	    private final TreeNodeSource _source;
	    private IAlarmSubtreeNode _parent;

	    public Builder(@Nonnull final String name,
	                   @Nonnull final LdapEpicsAlarmcfgConfiguration configurationType,
	                   @Nonnull final TreeNodeSource source) {
	        _name = name;
	        _source = source;

	        // FIXME (bknerr) : transform SUBCOMPONENT and IOC to COMPONENT
	        if (configurationType.equals(LdapEpicsAlarmcfgConfiguration.SUBCOMPONENT) ||
	            configurationType.equals(LdapEpicsAlarmcfgConfiguration.IOC)) {

	            _configurationType = LdapEpicsAlarmcfgConfiguration.COMPONENT;
	        } else {
	            _configurationType = configurationType;
	        }
	    }

	    @Nonnull
	    public Builder setParent(@Nullable final IAlarmSubtreeNode parent) {
	        _parent = parent;
	        return this;
	    }

	    @SuppressWarnings("synthetic-access")
        @Nonnull
	    public SubtreeNode build() {
	        final SubtreeNode node = new SubtreeNode(_name, _configurationType, _source);
	        if (_parent != null) {
                boolean couldAdd = _parent.addChild(node);
                if (!couldAdd) {
                    throw new IllegalStateException("Could not add node '" + node.getName() + "' to parent '" + _parent.getName() + "'");
                }
	            _parent.addChild(node);
	        }
	        return node;
	    }
	}

	/**
	 * Creates a new node.
	 *
	 * @param name the name of this node.
	 * @param configurationType the object class of this node.
	 * @param source
	 */
	private SubtreeNode(@Nonnull final String name,
	                    @Nonnull final LdapEpicsAlarmcfgConfiguration configurationType,
	                    @Nonnull final TreeNodeSource source) {
	    super(name, configurationType, source);

		_childrenPVMap = Maps.newTreeMap();
		_childrenSubtreeMap = Maps.newTreeMap();
		_highestChildSeverity = EpicsAlarmSeverity.UNKNOWN;
		_highestUnacknowledgedChildSeverity = EpicsAlarmSeverity.UNKNOWN;
	}

    /**
     * {@inheritDoc}
     */
	@Override
    public void removeChild(@Nonnull final IAlarmTreeNode child) {
		final String childName = child.getName();
        if (_childrenPVMap.containsKey(childName)) {
		    final IAlarmProcessVariableNode processVariableNode = _childrenPVMap.remove(childName);
		    processVariableNode.wasRemoved();
		} else if (_childrenSubtreeMap.containsKey(childName)) {
		    _childrenSubtreeMap.remove(childName);
		}
		refreshSeverities();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void removeChildren() {
	    for (final IAlarmTreeNode child : getChildren()) {
	        if (child instanceof IAlarmSubtreeNode) {
	            ((IAlarmSubtreeNode) child).removeChildren();
	        }
	        removeChild(child);
        }
	}

	
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canAddChild(@Nonnull final String name) {
        return !_childrenPVMap.containsKey(name)
                && !_childrenSubtreeMap.containsKey(name);
    }

	/**
	 * {@inheritDoc}
	 */
    @Override
    public boolean addChild(@Nonnull final IAlarmTreeNode child) {
        final String name = child.getName();

        if (_childrenPVMap.containsKey(name)) {
            return false;
        }
        if (_childrenSubtreeMap.containsKey(name)) {
            return false;
        }
        if (child instanceof IAlarmProcessVariableNode) {
            _childrenPVMap.put(name, (IAlarmProcessVariableNode) child);
            ((IAlarmProcessVariableNode) child).wasAdded();
        } else if (child instanceof IAlarmSubtreeNode) {
            _childrenSubtreeMap.put(name, (IAlarmSubtreeNode) child);
        } else {
            return false;
        }
        childSeverityChanged(child);
        child.setParent(this);
        return true;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    @CheckForNull
    public EpicsAlarmSeverity getAlarmSeverity() {
		return _highestChildSeverity;
	}

	/**
	 * Returns the highest severity of the unacknowledged alarms in the subtree
	 * below this node.
	 *
	 * @return the unacknowledged alarm severity for this node.
	 */
    @Override
    @CheckForNull
    public EpicsAlarmSeverity getUnacknowledgedAlarmSeverity() {
		return _highestUnacknowledgedChildSeverity;
	}

    /**
     * {@inheritDoc}
     */
	@Override
    public boolean hasChildren() {
		return !_childrenSubtreeMap.isEmpty() || !_childrenPVMap.isEmpty();
	}

    /**
     * {@inheritDoc}
     */
	@Override
    @Nonnull
	public List<IAlarmTreeNode> getChildren() {
	    final List<IAlarmTreeNode> children =
	        new ArrayList<IAlarmTreeNode>(_childrenPVMap.size() + _childrenSubtreeMap.size());
	    children.addAll(_childrenSubtreeMap.values());
	    children.addAll(_childrenPVMap.values());
	    return children;
	}

	/**
	 * {@inheritDoc}
	 */
//	public boolean hasAlarm() {
//		return _highestChildSeverity.isAlarm() || _highestUnacknowledgedChildSeverity.isAlarm();
//	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void childSeverityChanged(@Nonnull final IAlarmTreeNode child) {
		boolean thisNodeChanged = false;

		// If the severity is higher than the current highest severity, simply
		// set it as the new highest severity and propagate it to the parent.
		// Otherwise, there might still be other children with a higher
		// severity. So we look for the highest severity of the children, and
		// propagate that upwards (if it is different from the current
		// severity).

		// Start with the active alarm severity
		final EpicsAlarmSeverity changedChildSeverity = child.getAlarmSeverity();
		if (changedChildSeverity.compareTo(_highestChildSeverity) > 0) {
			_highestChildSeverity = changedChildSeverity;
			thisNodeChanged = true;
		} else {
			thisNodeChanged |= refreshActiveSeverity();
		}
		// Now the highest unacknowledged severity
		final EpicsAlarmSeverity unack = child.getUnacknowledgedAlarmSeverity();
		if (unack.compareTo(_highestUnacknowledgedChildSeverity) > 0) {
			_highestUnacknowledgedChildSeverity = unack;
			thisNodeChanged = true;
		} else {
			thisNodeChanged |= refreshHighestUnacknowledgedSeverity();
		}

		// Notify parent if this node changed
		final IAlarmSubtreeNode parent = getParent();
		if (thisNodeChanged && parent != null) {
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
		if (thisNodeChanged && parent != null) {
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
		final EpicsAlarmSeverity s = findHighestChildSeverity();
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
		final EpicsAlarmSeverity s = findHighestUnacknowledgedChildSeverity();
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
	@Nonnull
	private EpicsAlarmSeverity findHighestChildSeverity() {
		EpicsAlarmSeverity highest = EpicsAlarmSeverity.getLowest();

		for (final IAlarmTreeNode node : getChildren()) {
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
	@Nonnull
	private EpicsAlarmSeverity findHighestUnacknowledgedChildSeverity() {
		EpicsAlarmSeverity highest = EpicsAlarmSeverity.getLowest();

		for (final IAlarmTreeNode node : getChildren()) {
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
	@Override
    @CheckForNull
	public IAlarmTreeNode getChild(@Nonnull final String name) {
	    if (_childrenPVMap.containsKey(name)) {
	        return _childrenPVMap.get(name);
	    }
		return _childrenSubtreeMap.get(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    @Nonnull
	public List<IAlarmProcessVariableNode> findProcessVariableNodes(@Nonnull final String name) {

	    final List<IAlarmProcessVariableNode> result =
	        new ArrayList<IAlarmProcessVariableNode>();

	    final IAlarmProcessVariableNode pv = _childrenPVMap.get(name);
	    if (pv != null) {
	        result.add(pv);
	    }

		for (final IAlarmTreeNode child : _childrenSubtreeMap.values()) {

		    final List<IAlarmProcessVariableNode> subList =
		        ((SubtreeNode) child).findProcessVariableNodes(name);
		    result.addAll(subList);
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    @Nonnull
	public List<IAlarmProcessVariableNode> findAllProcessVariableNodes() {

	    final List<IAlarmProcessVariableNode> result = new ArrayList<IAlarmProcessVariableNode>();
	    result.addAll(_childrenPVMap.values());

	    for (final IAlarmSubtreeNode child : _childrenSubtreeMap.values()) {
	        final List<IAlarmProcessVariableNode> subList = child.findAllProcessVariableNodes();
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
	@Override
    @Nonnull
	public Collection<IAlarmProcessVariableNode> collectUnacknowledgedAlarms() {

	    final Collection<IAlarmProcessVariableNode> result = new ArrayList<IAlarmProcessVariableNode>();

		for (final IAlarmTreeNode child : getChildren()) {
		    if (child instanceof IAlarmSubtreeNode) {
		        result.addAll( ((IAlarmSubtreeNode) child).collectUnacknowledgedAlarms());
		    } else if (child instanceof IAlarmProcessVariableNode) {
		        if (child.getUnacknowledgedAlarmSeverity() != EpicsAlarmSeverity.NO_ALARM) {
		            result.add((IAlarmProcessVariableNode) child);
		        }
		    }
		}

		return result;
	}

	/**
	 * Removes all children, and resets all internal states.
	 */
	@Override
    public void clearChildren() {
	    // Tell all the pv nodes, that they are going to be removed now
	    for (final IAlarmProcessVariableNode processVariableNode : _childrenPVMap.values()) {
	        processVariableNode.wasRemoved();
	    }
	    _childrenPVMap.clear();
	    _childrenSubtreeMap.clear();
	    _highestChildSeverity = EpicsAlarmSeverity.UNKNOWN;
	    _highestUnacknowledgedChildSeverity = EpicsAlarmSeverity.UNKNOWN;

	}
}
