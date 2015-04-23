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
package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.ActionsProperty;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.csstudio.ui.util.CustomMediaFactory;

/**
 * 
 * @author Helge Rickens, Kai Meyer, Xihui Chen
 *
 */
public final class MenuButtonModel extends AbstractPVWidgetModel implements ITextModel{
	private static final boolean DEFAULT_ACTIONS_FROM_PV = false;
	/**
	 * The ID of the label property.
	 */
	public static final String PROP_LABEL = "label"; //$NON-NLS-1$

	public static final String PROP_ACTIONS_FROM_PV = "actions_from_pv"; //$NON-NLS-1$
	
	
	public static final String PROP_TRANSPARENT = "transparent";	//$NON-NLS-1$

	/**
	 * The default value of the height property.
	 */
	private static final int DEFAULT_HEIGHT = 40;

	/**
	 * The default value of the width property.
	 */
	private static final int DEFAULT_WIDTH = 100;
	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.opibuilder.widgets.MenuButton";
	
	/**
	 * Constructor.
	 */
	public MenuButtonModel() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setBorderStyle(BorderStyle.BUTTON_RAISED);
		setForegroundColor(CustomMediaFactory.COLOR_BLACK);
		setPropertyValue(PROP_BORDER_ALARMSENSITIVE, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureProperties() {
		addProperty(new StringProperty(PROP_LABEL, "Label",
				WidgetPropertyCategory.Display, "")); //$NON-NLS-1$
		addProperty(new BooleanProperty(PROP_ACTIONS_FROM_PV, "Actions From PV", 
				WidgetPropertyCategory.Behavior, DEFAULT_ACTIONS_FROM_PV));
		addProperty(new BooleanProperty(PROP_TRANSPARENT, "Transparent", 
				WidgetPropertyCategory.Display, false));
		removeProperty(PROP_ACTIONS);		
		addProperty(new ActionsProperty(PROP_ACTIONS, "Actions", 
				WidgetPropertyCategory.Behavior, false));
		
		setPropertyVisible(PROP_ACTIONS, !DEFAULT_ACTIONS_FROM_PV);
		
	}
	


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTypeID() {
		return ID;
	}
	/**
	 * Return the label text.
	 * 
	 * @return The label text.
	 */
	public String getLabel() {
		return (String) getProperty(PROP_LABEL).getPropertyValue();
	}
	
	@Override
	public String getText() {
		return getLabel();
	}
	
	@Override
	public void setText(String text) {
		setPropertyValue(PROP_LABEL, text);
	}

	public boolean isActionsFromPV(){
		return (Boolean)getCastedPropertyValue(PROP_ACTIONS_FROM_PV);
	}

	public boolean isTransparent() {
		return (Boolean)getPropertyValue(PROP_TRANSPARENT);
	}
	
}
