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


import org.csstudio.platform.model.IProcessVariable;

/**
 * A tree node that represents a process variable.
 * 
 * @author Joerg Rathlev
 */
public class ProcessVariableNode extends AbstractAlarmTreeNode 
		implements IAlarmTreeNode, IProcessVariable {
	
	/**
	 * This node's parent node.
	 */
	private SubtreeNode _parent;
	
	/**
	 * The name of this node.
	 */
	private String _name;
	
	/**
	 * The active alarm for this node.
	 */
	private Alarm _activeAlarm;
	
	/**
	 * The highest unacknowledged alarm for this node.
	 */
	private Alarm _highestUnacknowledgedAlarm;
	
	/**
	 * An alarm object representing NO_ALARM for this node.
	 */
	private final Alarm _noAlarm;
	
	/**
	 * Creates a new node for a process variable as a child of the specified
	 * parent.
	 * 
	 * @param parent the parent node for the node.
	 * @param name the name of the node.
	 */
	public ProcessVariableNode(final SubtreeNode parent, final String name) {
		if (parent == null || name == null) {
			throw new NullPointerException("parent and name must not be null");
		}
		
		this._parent = parent;
		parent.addChild(this);
		this._name = name;
		this._activeAlarm = null;
		this._highestUnacknowledgedAlarm = null;
		this._noAlarm = new Alarm(name, Severity.NO_ALARM);
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
	public final IAlarmTreeNode getParent() {
		return _parent;
	}

	/**
	 * {@inheritDoc}
	 */
	public final String getTypeId() {
		return IProcessVariable.TYPE_ID;
	}
	
	/**
	 * Returns the severity of the alarm for this node. If there is no alarm
	 * for this node, returns NO_ALARM.
	 * 
	 * @return the severity of the alarm for this node.
	 */
	public final Severity getAlarmSeverity() {
		if (_activeAlarm != null) {
			return _activeAlarm.getSeverity();
		} else {
			return Severity.NO_ALARM;
		}
	}
	
	/**
	 * Returns the severity of the highest unacknowledged alarm for this node.
	 * If there is no unacknowledged alarm for this node, returns NO_ALARM.
	 * 
	 * @return the severity of the highest unacknowledged alarm for this node.
	 */
	public final Severity getUnacknowledgedAlarmSeverity() {
		if (_highestUnacknowledgedAlarm != null) {
			return _highestUnacknowledgedAlarm.getSeverity();
		} else {
			return Severity.NO_ALARM;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public final boolean hasAlarm() {
		return _activeAlarm != null || _highestUnacknowledgedAlarm != null;
	}
	
	/**
	 * Sets an active alarm at this node.
	 * @param alarm the alarm.
	 */
	public final void setActiveAlarm(final Alarm alarm) {
		this._activeAlarm = alarm;

		// Increase the highest unacknowledged alarm if the new active alarm
		// has a higher severity than the current highest unacknowledged
		// alarm.
		if (_highestUnacknowledgedAlarm == null
				|| alarm.compareTo(_highestUnacknowledgedAlarm) > 0) {
			_highestUnacknowledgedAlarm = alarm;
		}
		
		// propagate alarm to the parent node
		_parent.childSeverityChanged(this);
	}
	
	/**
	 * Sets the highest unacknowledged alarm at this node.
	 * @param alarm the alarm.
	 */
	public final void setHighestUnacknowledgedAlarm(final Alarm alarm) {
		this._highestUnacknowledgedAlarm = alarm;
		_parent.childSeverityChanged(this);
	}
	
	/**
	 * Cancels the alarm at this node. If there is no alarm, does nothing.
	 */
	public final void cancelAlarm() {
		if (_activeAlarm != null) {
			_activeAlarm = _noAlarm;
			_parent.childSeverityChanged(this);
		}
	}

	/**
	 * Removes the highest unacknowledged alarm from this node.
	 */
	public final void removeHighestUnacknowledgedAlarm() {
		if (_highestUnacknowledgedAlarm != null) {
			_highestUnacknowledgedAlarm = _noAlarm;
			_parent.childSeverityChanged(this);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String toString() {
		return _name;
	}
}
