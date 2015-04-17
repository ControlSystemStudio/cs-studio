/* 
 * Copyright (c) 2006 Stiftung Deutsches Elektronen-Synchroton, 
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
package org.csstudio.platform.ui.composites.resourcefilter;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.part.DrillDownAdapter;

/**
 * Class <code>DrillDownComposite</code> implements a simple web style
 * navigation metaphor. Home, back, and "drill into" buttons are added to a tree
 * viewer for easier navigation.
 * 
 * <p>
 * <b>Code is based upon <code>org.eclipse.ui.part.DrillDownComposite</code> in
 * plugin <code>org.eclipse.ui.workbench</code>.</b>
 * </p>
 * 
 * @author Kai Meyer
 * 
 */
// TODO: Copied from org.csstudio.platform.ui.
public class DrillDownComposite extends Composite {

	/**
	 * The ToolBarManager of this DrillDownComposite.
	 */
	private ToolBarManager _toolBarMgr;

	/**
	 * The Treeviewer of this DrillDownComposite.
	 */
	private TreeViewer _fChildTree;

	/**
	 * The DrillDownAdapter for the Tree.
	 */
	private DrillDownAdapter _adapter;

	/**
	 * Constructs a new DrillDownTreeViewer.
	 * 
	 * @param parent
	 *            the parent composite for this control
	 * @param style
	 *            the SWT style for this control
	 */
	public DrillDownComposite(final Composite parent, final int style) {
		super(parent, style);
		createNavigationButtons();
	}

	/**
	 * Creates the navigation buttons for this viewer.
	 */
	private void createNavigationButtons() {
		GridData gid;
		GridLayout layout;

		// Define layout.
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		setLayout(layout);

		// Create a toolbar.
		_toolBarMgr = new ToolBarManager(SWT.FLAT);
		ToolBar toolBar = _toolBarMgr.createControl(this);
		gid = new GridData();
		gid.horizontalAlignment = GridData.FILL;
		gid.verticalAlignment = GridData.BEGINNING;
		toolBar.setLayoutData(gid);
	}

	/**
	 * Sets the child viewer. This method should only be called once, after the
	 * viewer has been created.
	 * 
	 * @param aViewer
	 *            the new child viewer
	 */
	public final void setChildTree(final TreeViewer aViewer) {
		// Save viewer.
		_fChildTree = aViewer;

		// Create adapter.
		_adapter = new DrillDownAdapter(_fChildTree);
		_adapter.addNavigationActions(_toolBarMgr);
		_toolBarMgr.update(true);

		// Set tree layout.
		_fChildTree.getTree().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		layout();
	}

	/**
	 * Delivers the ToolBarManager of this DrillDownComposite.
	 * 
	 * @return ToolBarManager The ToolbarManager of this DrillDownComposite
	 */
	public final ToolBarManager getToolBarManager() {
		return _toolBarMgr;
	}

}
