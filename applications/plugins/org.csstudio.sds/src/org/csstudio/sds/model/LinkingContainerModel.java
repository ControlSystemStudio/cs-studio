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
package org.csstudio.sds.model;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Model for a container widget that displays linked resources (other displays)
 * within its own bounds.
 *
 * @author Sven Wende
 * @version $Revision: 1.11 $
 *
 */
public final class LinkingContainerModel extends ContainerModel {
	/**
	 * The ID of the resource property.
	 */
	public static final String PROP_RESOURCE = "resource"; //$NON-NLS-1$

	/**
	 * The ID of the auto zoom property.
	 */
	private static final String PROP_AUTOZOOM = "autozoom"; //$NON-NLS-1$

	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.sds.components.LinkingContainer"; //$NON-NLS-1$

	/**
	 * The default value of the height property.
	 */
	private static final int DEFAULT_HEIGHT = 100;

	/**
	 * The default value of the width property.
	 */
	private static final int DEFAULT_WIDTH = 100;

	private boolean isResourceLoaded = false;
	
	/**
	 * Standard constructor.
	 */
	public LinkingContainerModel() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTypeID() {
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureProperties() {
		addResourceProperty(PROP_RESOURCE, "Resource", WidgetPropertyCategory.DISPLAY, new Path(""), new String[] { "css-sds" }, true, PROP_TOOLTIP);
		addBooleanProperty(PROP_AUTOZOOM, "Automatic Zoom", WidgetPropertyCategory.DISPLAY, true, false, PROP_RESOURCE);

		// .. hide properties
		hideProperty(PROP_ROTATION, getTypeID());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getDefaultToolTip() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(createTooltipParameter(PROP_ALIASES) + "\n");
		buffer.append("Resource:\t");
		buffer.append(createTooltipParameter(PROP_RESOURCE));
		return buffer.toString();
	}

	/**
	 * Return the target resource.
	 *
	 * @return The target resource.
	 */
	public IPath getResource() {
		return getResourceProperty(PROP_RESOURCE);
	}

	/**
	 * Returns the auto zoom state.
	 *
	 * @return the auto zoom state
	 */
	public boolean isAutoZoom() {
		return getBooleanProperty(PROP_AUTOZOOM);
	}
	
	public boolean isResourceLoaded() {
		return isResourceLoaded;
	}
	
	public void setResourceLoaded(boolean isResourceLoaded) {
		this.isResourceLoaded = isResourceLoaded;
	}
}
