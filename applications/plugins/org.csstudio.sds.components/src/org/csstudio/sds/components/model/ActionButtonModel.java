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
package org.csstudio.sds.components.model;

import java.util.HashMap;
import java.util.Map;

import org.csstudio.sds.components.internal.localization.Messages;
import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.FontProperty;
import org.csstudio.sds.model.properties.MapProperty;
import org.csstudio.sds.model.properties.OptionProperty;
import org.csstudio.sds.model.properties.StringProperty;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;

/**
 * An action button widget model.
 * 
 * @author Sven Wende
 * @version $Revision$
 * 
 */
public final class ActionButtonModel extends AbstractWidgetModel {
	/**
	 * The ID of the label property.
	 */
	public static final String PROP_LABEL = "label"; //$NON-NLS-1$

	/**
	 * The ID of the resource property.
	 */
	public static final String PROP_RESOURCE = "resource"; //$NON-NLS-1$
	
	/**
	 * The ID of the action property.
	 */
	public static final String PROP_ACTION =  "action"; //$NON-NLS-1$
	
	/**
	 * The ID of the font property.
	 */
	public static final String PROP_FONT = "font"; //$NON-NLS-1$
	
	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "element.actionbutton"; //$NON-NLS-1$

	/**
	 * The default value of the height property.
	 */
	private static final int DEFAULT_HEIGHT = 20;

	/**
	 * The default value of the width property.
	 */
	private static final int DEFAULT_WIDTH = 80;

	/**
	 * The default value for alias property.
	 */
	private static final Map<String, String> DEFAULT_MAP = new HashMap<String, String>();
	
	/**
	 * Standard constructor.
	 */
	public ActionButtonModel() {
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
		addProperty(PROP_LABEL, new StringProperty(Messages.LabelElement_LABEL,
				WidgetPropertyCategory.Display, "")); //$NON-NLS-1$
		addProperty(PROP_RESOURCE, new StringProperty("Resource",
				WidgetPropertyCategory.Behaviour, ""));
//		addProperty(PROP_RESOURCE, new MapProperty("Resource",
//				WidgetPropertyCategory.Behaviour, DEFAULT_MAP));
		addProperty(PROP_ACTION, new OptionProperty("Action",
				WidgetPropertyCategory.Behaviour, new String[]{"Open Display As Shell", "Open Display As View"}, 0));
		addProperty(PROP_FONT, new FontProperty("Font",
				WidgetPropertyCategory.Display, new FontData("Arial", 8, SWT.NONE))); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDoubleTestProperty() {
		return PROP_LABEL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getColorTestProperty() {
		return PROP_COLOR_BACKGROUND;
	}

	/**
	 * Return the label text.
	 * 
	 * @return The label text.
	 */
	public String getLabel() {
		return (String) getProperty(PROP_LABEL).getPropertyValue();
	}

	/**
	 * Return the label font.
	 * 
	 * @return The label font.
	 */
	public FontData getFont() {
		return (FontData) getProperty(PROP_FONT).getPropertyValue();
	}
	
	public String getResource() {
		return (String) getProperty(PROP_RESOURCE).getPropertyValue();
	}
	
//	public Map<String, String> getResource() {
//		return (Map<String, String>) getProperty(PROP_RESOURCE).getPropertyValue();
//	}
	
	public int getAction() {
		return (Integer) getProperty(PROP_ACTION).getPropertyValue();
	}
}
