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

import org.eclipse.core.runtime.PlatformObject;

/**
 * Abstract base class for alarm tree nodes.
 * 
 * @author Joerg Rathlev
 */
public abstract class AbstractAlarmTreeNode extends PlatformObject implements
		IAlarmTreeNode {

	/**
	 * The name of the CSS alarm display associated with this node.
	 */
	private String _cssAlarmDisplay;
	
	/**
	 * The URL of the help page for this node.
	 */
	private URL _helpPage;
	
	/**
	 * The help guidance string for this node.
	 */
	private String _helpGuidance;
	
	/**
	 * The name of the CSS display file associated with this node.
	 */
	private String _cssDisplay;
	
	/**
	 * The name of the CSS strip chart file associated with this node.
	 */
	private String _cssStripChart;

	/**
	 * Creates a new abstract alarm tree node.
	 */
	public AbstractAlarmTreeNode() {
		super();
	}

	/**
	 * Sets the CSS alarm display for this node.
	 * @param display the CSS alarm display for this node.
	 */
	public final void setCssAlarmDisplay(final String display) {
		this._cssAlarmDisplay = display;
	}

	/**
	 * {@inheritDoc}
	 */
	public final String getCssAlarmDisplay() {
		return _cssAlarmDisplay;
	}

	/**
	 * {@inheritDoc}
	 */
	public final String getCssDisplay() {
		return _cssDisplay;
	}

	/**
	 * Sets the name of the CSS display for this node.
	 * @param cssDisplay the name of the CSS display for this node.
	 */
	public final void setCssDisplay(final String cssDisplay) {
		_cssDisplay = cssDisplay;
	}

	/**
	 * Sets a help page for this node.
	 * @param helpPage the help page URI.
	 */
	public final void setHelpPage(final URL helpPage) {
		this._helpPage = helpPage;
	}

	/**
	 * {@inheritDoc}
	 */
	public final URL getHelpPage() {
		return _helpPage;
	}

	/**
	 * Sets a help guidance string for this node.
	 * @param helpGuidance a help guidance string.
	 */
	public final void setHelpGuidance(final String helpGuidance) {
		this._helpGuidance = helpGuidance;
	}

	/**
	 * {@inheritDoc}
	 */
	public final String getHelpGuidance() {
		return _helpGuidance;
	}

	/**
	 * {@inheritDoc}
	 */
	public final String getCssStripChart() {
		return _cssStripChart;
	}

	/**
	 * Sets the CSS strip chart file for this node.
	 * @param cssStripChart the name of the file.
	 */
	public final void setCssStripChart(final String cssStripChart) {
		_cssStripChart = cssStripChart;
	}

}
