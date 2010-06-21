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


import java.sql.Date;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.csstudio.alarm.service.declaration.LdapEpicsAlarmCfgObjectClass;
import org.csstudio.platform.model.IProcessVariable;

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
     * ProcessVariableNode Builder.
     *
     * @author bknerr
     * @author $Author$
     * @version $Revision$
     * @since 28.04.2010
     */
    public static final class Builder {
        private final String _name;
        private IAlarmSubtreeNode _parent;

        /**
         * Creates a new node for a process variable as a child of the specified
         * parent.
         *
         * @param name the name of the node.
         */
        public Builder(@Nonnull final String name) {
            _name = name;
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

        /**
         * The final method to build the object instance
         * @return the newly built object
         */
        @Nonnull
        public ProcessVariableNode build() {
            final ProcessVariableNode node = new ProcessVariableNode(_name);
            if (_parent != null) {
                _parent.addPVChild(node);
            }
            return node;
        }
    }

    /**
     * Constructor.
     * @param name the simple name of the process variable
     */
	private ProcessVariableNode(@Nonnull final String name) {
	    super(name, LdapEpicsAlarmCfgObjectClass.RECORD);
		_activeAlarm = new Alarm(name, Severity.UNKNOWN, new Date(0L));
		_highestUnacknowledgedAlarm = new Alarm(name, Severity.UNKNOWN, new Date(0L));
	}


	/**
	 * {@inheritDoc}
	 */
	@Nonnull
	public String getTypeId() {
		return IProcessVariable.TYPE_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Nonnull
	public Severity getAlarmSeverity() {
		if (_activeAlarm != null) {
			return _activeAlarm.getSeverity();
		}
        return Severity.NO_ALARM;
	}

    /**
     * {@inheritDoc}
     */
	@Nonnull
	public Severity getUnacknowledgedAlarmSeverity() {
		if (_highestUnacknowledgedAlarm != null) {
			return _highestUnacknowledgedAlarm.getSeverity();
		}
        return Severity.NO_ALARM;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasAlarm() {
		return (_activeAlarm != null) || (_highestUnacknowledgedAlarm != null);
	}

	/**
	 * Updates the active alarm state of this node. This method should be called
	 * when a new alarm message was received. The alarm state is updated to the
	 * new alarm only if the new alarm occured after the current alarm.
	 *
	 * @param alarm the new alarm.
	 */
	public void updateAlarm(@Nonnull final Alarm alarm) {
		if ((alarm != null) && alarm.occuredAfter(_activeAlarm)) {
			_activeAlarm = alarm;
			Severity severityOfAlarm = alarm.getSeverity();
            Severity severityOfHighestUnackAlarm = _highestUnacknowledgedAlarm.getSeverity();
            if (severityOfAlarm.getLevel() > severityOfHighestUnackAlarm.getLevel()) {
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
	@Nonnull
	public Alarm getAlarm() {
		return _activeAlarm;
	}

	/**
	 * {@inheritDoc}
	 */
	@Nonnull
	public Alarm getHighestUnacknowledgedAlarm() {
		return _highestUnacknowledgedAlarm;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setHighestUnacknowledgedAlarm(@Nonnull final Alarm alarm) {
		_highestUnacknowledgedAlarm = alarm;
		final IAlarmSubtreeNode parent = getParent();
		if (parent != null) {
		    parent.childSeverityChanged(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeHighestUnacknowledgedAlarm() {
	    if (_highestUnacknowledgedAlarm != null) {
	        _highestUnacknowledgedAlarm = null;
	        final IAlarmSubtreeNode parent = getParent();
	        if (parent != null) {
	            parent.childSeverityChanged(this);
	        }
	    }
	}
}
