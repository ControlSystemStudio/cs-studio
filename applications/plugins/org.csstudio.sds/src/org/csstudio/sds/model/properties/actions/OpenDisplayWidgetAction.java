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
package org.csstudio.sds.model.properties.actions;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.ActionType;
import org.csstudio.sds.model.properties.ResourceProperty;
import org.csstudio.sds.model.properties.StringMapProperty;
import org.csstudio.sds.model.properties.StringProperty;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * A {@link WidgetAction}, which opens a display in a shell or in a view..
 * @author Kai Meyer
 */
public final class OpenDisplayWidgetAction extends WidgetAction {
	/**
	 * The ID for the <i>resource</i> property. 
	 */
	public static final String PROP_RESOURCE = "resource";
	/**
	 * The ID for the <i>aliases</i> property. 
	 */
	public static final String PROP_ALIASES = "aliases";
	/**
	 * The ID for the <i>description</i> property. 
	 */
	public static final String PROP_DESCRIPTION = "description";

	/**
	 * Constructor.
	 * @param type The type for the {@link WidgetAction}
	 */
	public OpenDisplayWidgetAction(final ActionType type) {
		super(type.getTitle(), type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createProperties() {
		ResourceProperty resource = new ResourceProperty("Display", WidgetPropertyCategory.Behaviour, new Path(""), new String[] {"css-sds"});
		StringMapProperty aliases = new StringMapProperty("Aliases", WidgetPropertyCategory.Behaviour, new HashMap<String, String>());
		StringProperty description = new StringProperty("Description", WidgetPropertyCategory.Behaviour, "");
		
		addProperty(PROP_RESOURCE, resource);
		addProperty(PROP_ALIASES, aliases);
		addProperty(PROP_DESCRIPTION, description);
	}
	
	/**
	 * Returns the {@link IPath} to the display.
	 * @return The {@link IPath} to the display
	 */
	public IPath getResource() {
		return getProperty(PROP_RESOURCE).getPropertyValue();
	}
	
	/**
	 * Returns the new aliases for the display.
	 * @return The new aliases for the display
	 */
	public Map<String, String> getAliases() {
		return getProperty(PROP_ALIASES).getPropertyValue();
	}
	
	/**
	 * Returns the description.
	 * @return The description
	 */
	public String getDescription() {
		return getProperty(PROP_DESCRIPTION).getPropertyValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getActionLabel() {
		if (getDescription()==null || getDescription().trim().length()==0) {
			StringBuffer buffer = new StringBuffer(this.getType().getTitle());
			buffer.append(" ");
			if (this.getResource().lastSegment()==null) {
				buffer.append("unspecified");
			} else {
				buffer.append(this.getResource().lastSegment());
			}
			return buffer.toString();
		}
		return getDescription();
	}

}
