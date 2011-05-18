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
 package org.csstudio.alarm.treeview.model;


import java.sql.Date;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.domain.desy.epics.alarm.EpicsAlarmSeverity;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.utility.ldap.treeconfiguration.LdapEpicsAlarmcfgConfiguration;

/**
 * A tree node that represents a process variable.
 *
 * @author Joerg Rathlev
 */
public final class ProcessVariableNode extends AbstractAlarmTreeNode
    implements IProcessVariable, IAlarmProcessVariableNode {


	/**
	 * The active alarm for this node.
	 */
	private Alarm _activeAlarm;

	/**
	 * The highest unacknowledged alarm for this node.
	 */
	private Alarm _highestUnacknowledgedAlarm;

    /**
     * Allows tracking of life cycle and renaming
     */
    private IProcessVariableNodeListener _listener;

	/**
     * ProcessVariableNode Builder.
     *
     * @author bknerr
     * @author $Author$
     * @version $Revision$
     * @since 28.04.2010
     */
    public static final class Builder {
        private final String _name;
        private final TreeNodeSource _source;
        private IAlarmSubtreeNode _parent;
        private IProcessVariableNodeListener _listener;
        private Alarm _alarm;

        /**
         * Creates a new node for a process variable as a child of the specified
         * parent.
         *
         * @param name the name of the node.
         * @param source
         */
        public Builder(@Nonnull final String name, @Nonnull final TreeNodeSource source) {
            _name = name;
            _source = source;
        }

        /**
         * The parent of the node.
         * @param parent the parent node, can be null if this node is the root
         * @return itself for chaining
         */
        @Nonnull
        public Builder setParent(@Nullable final IAlarmSubtreeNode parent) {
            _parent = parent;
            return this;
        }

        @Nonnull
        public Builder setListener(@Nullable final IProcessVariableNodeListener listener) {
            _listener = listener;
            return this;
        }

        @Nonnull
        public Builder setHighestUnacknowledgedAlarm(@Nullable final Alarm alarm) {
            _alarm = alarm;
            return this;
        }
        /**
         * The final method to build the object instance
         * @return the newly built object
         */
        @SuppressWarnings("synthetic-access")
        @Nonnull
        public ProcessVariableNode build() {
            final ProcessVariableNode node = new ProcessVariableNode(_name, _source);
            if (_listener != null) {
                node.setListener(_listener);
            }
            if (_parent != null) {
                boolean couldAdd = _parent.addChild(node);
                if (!couldAdd) {
                    throw new IllegalStateException("Could not add node '" + node.getName() + "' to parent '" + _parent.getName() + "'");
                }
            }
            if (_alarm != null) {
                node.setHighestUnacknowledgedAlarm(_alarm);
            }
            return node;
        }
    }

    /**
     * Constructor.
     * @param name the simple name of the process variable
     * @param source
     */
	private ProcessVariableNode(@Nonnull final String name, @Nonnull final TreeNodeSource source) {
	    super(name, LdapEpicsAlarmcfgConfiguration.RECORD, source);
		_activeAlarm = new Alarm(name, EpicsAlarmSeverity.UNKNOWN, new Date(0L));
		_highestUnacknowledgedAlarm = new Alarm(name, EpicsAlarmSeverity.UNKNOWN, new Date(0L));
	}


	/**
	 * If a listener was given to the builder, the node will store it
	 *
	 * @param listener
	 */
	void setListener(@Nonnull final IProcessVariableNodeListener listener) {
        _listener = listener;
    }


    /**
	 * {@inheritDoc}
	 */
	@Override
    @Nonnull
	public String getTypeId() {
		return IProcessVariable.TYPE_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    @Nonnull
	public EpicsAlarmSeverity getAlarmSeverity() {
		if (_activeAlarm != null) {
			return _activeAlarm.getSeverity();
		}
        return EpicsAlarmSeverity.NO_ALARM;
	}

    /**
     * {@inheritDoc}
     */
	@Override
    @Nonnull
	public EpicsAlarmSeverity getUnacknowledgedAlarmSeverity() {
		return _highestUnacknowledgedAlarm.getSeverity();
	}

	@Override
	public void wasAdded() {
	    if (_listener != null) {
	        _listener.wasAdded(getName());
	    }
	}

	@Override
	public void wasRemoved() {
	    if (_listener != null) {
	        _listener.wasRemoved(getName());
	    }
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasAlarm() {
		return _activeAlarm != null || _highestUnacknowledgedAlarm != null;
	}

	/**
	 * Updates the active alarm state of this node. This method should be called
	 * when a new alarm message was received. The alarm state is updated to the
	 * new alarm only if the new alarm occured after the current alarm.
	 *
	 * @param alarm the new alarm.
	 */
	@Override
    public void updateAlarm(@Nonnull final Alarm alarm) {
		if (alarm.occuredAfter(_activeAlarm)) {
			_activeAlarm = alarm;
			final EpicsAlarmSeverity severityOfAlarm = alarm.getSeverity();
            final EpicsAlarmSeverity severityOfHighestUnackAlarm = _highestUnacknowledgedAlarm.getSeverity();
//            if (severityOfAlarm.getLevel() > severityOfHighestUnackAlarm.getLevel()) {
            if (severityOfAlarm.compareTo(severityOfHighestUnackAlarm) > 0) {
				_highestUnacknowledgedAlarm = alarm;
			}

			// propagate alarm to the parent node
			final IAlarmSubtreeNode parent = getParent();
			if (parent != null) {
			    parent.childSeverityChanged(this);
			}
		}
	}

    /**
     * {@inheritDoc}
     */
	@Override
    @Nonnull
	public Alarm getAlarm() {
		return _activeAlarm;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    @Nonnull
	public Alarm getHighestUnacknowledgedAlarm() {
		return _highestUnacknowledgedAlarm;
	}

    /**
     * Sets the highest unacknowledged alarm at this node.
     * @param alarm the alarm.
     */
    void setHighestUnacknowledgedAlarm(@Nonnull final Alarm alarm) {
		_highestUnacknowledgedAlarm = alarm;
		final IAlarmSubtreeNode parent = getParent();
		if (parent != null) {
		    parent.childSeverityChanged(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void acknowledgeAlarm() {
	        _highestUnacknowledgedAlarm = new Alarm(_highestUnacknowledgedAlarm.getObjectName(),
	                                                EpicsAlarmSeverity.UNKNOWN,
	                                                new Date(0L));
	        final IAlarmSubtreeNode parent = getParent();
	        if (parent != null) {
	            parent.childSeverityChanged(this);
	        }
	}
}
