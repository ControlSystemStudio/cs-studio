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

package org.csstudio.alarm.treeView.jms;

import java.util.List;

import org.csstudio.alarm.treeView.model.Alarm;
import org.csstudio.alarm.treeView.model.ProcessVariableNode;
import org.csstudio.alarm.treeView.model.Severity;
import org.csstudio.alarm.treeView.model.SubtreeNode;
import org.csstudio.alarm.treeView.views.AlarmTreeView;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * Instances of this class update the alarm tree in response to alarm events.
 * 
 * @author Joerg Rathlev
 */
public final class AlarmTreeUpdater {
	
	/**
	 * The tree which is updated by this updater.
	 */
	private SubtreeNode _tree;

	/**
	 * Creates a new updater.
	 * 
	 * @param tree
	 *            the tree to be updated by this updater.
	 */
	public AlarmTreeUpdater(final SubtreeNode tree) {
		if (tree == null) {
			throw new NullPointerException("tree must not be null");
		}
		
		_tree = tree;
	}

	/**
	 * Updates the tree when an alarm message was received.
	 * 
	 * @param name
	 *            the name of the process variable for which the alarm was
	 *            received.
	 * @param severity
	 *            the severity of the alarm.
	 */
	void applyAlarm(final String name, final Severity severity) {
		List<ProcessVariableNode> nodes = findNodes(name);
		for (ProcessVariableNode node : nodes) {
			if (severity.isAlarm()) {
				Alarm alarm = new Alarm(name, severity);
				node.setActiveAlarm(alarm);
			} else {
				node.cancelAlarm();
			}
		}
		refreshView();
	}

	/**
	 * Updates the tree when an acknowledgement message was received.
	 * 
	 * @param name
	 *            the name of the process variable to which the acknowledgement
	 *            applies.
	 */
	void applyAcknowledgement(final String name) {
		List<ProcessVariableNode> nodes = findNodes(name);
		for (ProcessVariableNode node : nodes) {
			node.removeHighestUnacknowledgedAlarm();
		}
		refreshView();
	}

	/**
	 * Refreshes the alarm tree view. 
	 */
	private void refreshView() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				// FIXME: improve this!
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				IViewPart view = page.findView(AlarmTreeView.getID());
				if (view instanceof AlarmTreeView) {
					((AlarmTreeView) view).refresh();
				}
			}
		});
	}

	/**
	 * Returns a list of the alarm nodes with the given process variable name.
	 * 
	 * @param name
	 *            the process variable name.
	 * @return a list of alarm nodes. If no nodes are found, returns an empty
	 *         list.
	 */
	private List<ProcessVariableNode> findNodes(final String name) {
		return _tree.findProcessVariableNodes(name);
	}

}
