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

import java.net.URL;


/**
 * A node in the alarm tree.
 * 
 * @author Joerg Rathlev
 */
public interface IAlarmTreeNode {

	/**
	 * Returns the name of this node.
	 * @return the name of this node.
	 */
	String getName();

	/**
	 * Returns the parent node of this node. If this node does not have a
	 * parent, returns {@code null}.
	 * @return the parent node of this node.
	 */
	IAlarmTreeNode getParent();
	
	/**
	 * Returns the alarm severity for this node. If this node has children,
	 * returns the highest severity of the childrens' alarms. Returns severity
	 * NO_ALARM if this node is a subtree root without any children or if this
	 * node represents a process variable which is not in an alarm state.
	 * 
	 * @return the alarm severity for this node.
	 * 
	 * @see #getUnacknowledgedAlarmSeverity()
	 */
	Severity getAlarmSeverity();
	
	/**
	 * Returns the severity of the highest unacknowledged alarm for this node.
	 * If this node has children, returns the highest unacknowledged severity
	 * of the childrens' alarms. Returns severity NO_ALARM if this node is a
	 * subtree root without any children or if this node represents a process
	 * variable which doesn't have any unacknowledged alarms.
	 * 
	 * @return the severity of the highest unacknowledged alarm for this node.
	 * 
	 * @see #getAlarmSeverity()
	 */
	Severity getUnacknowledgedAlarmSeverity();

	/**
	 * Returns {@code true} if there is an alarm for this node or its children.
	 * @return whether there is an alarm for this node or its children.
	 */
	boolean hasAlarm();

	/**
	 * Returns the URL of this node's help page.
	 * 
	 * @return the URL of this node's help page, or <code>null</code> if this
	 *         node does not have a help page.
	 */
	URL getHelpPage();

	/**
	 * Returns this node's help guidance string.
	 * 
	 * @return this node's help guidance string, or <code>null</code> if no
	 *         help guidance string is configured for this node.
	 */
	String getHelpGuidance();
	
	/**
	 * Returns the name of the CSS-SDS alarm display file configured for this
	 * node.
	 * 
	 * @return the name of this node's alarm display file, or <code>null</code>
	 *         if no alarm display is configured for this node.
	 */
	String getCssAlarmDisplay();

}
