/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.model;

/**The abstract model for layout widgets.
 * @author Xihui Chen
 *
 */
public abstract class AbstractLayoutModel extends AbstractWidgetModel {

	private static final int FIXED_WIDTH = 16;

	public AbstractLayoutModel() {
		setLocation(0,0);
		setSize(FIXED_WIDTH, FIXED_WIDTH);
	}
	
	@Override
	protected void configureBaseProperties() {
		super.configureBaseProperties();
		setPropertyVisible(PROP_BORDER_COLOR,false);
		setPropertyVisible(PROP_BORDER_STYLE,false);
		setPropertyVisible(PROP_BORDER_WIDTH,false);
		setPropertyVisible(PROP_SCRIPTS,false);
		setPropertyVisible(PROP_VISIBLE,false);
		setPropertyVisible(PROP_ENABLED,false);
		setPropertyVisible(PROP_TOOLTIP,false);
		setPropertyVisible(PROP_ACTIONS,false);
		setPropertyVisible(PROP_COLOR_BACKGROUND,false);
		setPropertyVisible(PROP_COLOR_FOREGROUND,false);
		setPropertyVisible(PROP_XPOS,false);
		setPropertyVisible(PROP_YPOS,false);
		setPropertyVisible(PROP_WIDTH,false);
		setPropertyVisible(PROP_HEIGHT,false);
		setPropertyVisible(PROP_RULES,false);
		setPropertyVisible(PROP_SCALE_OPTIONS, false);
	}
	
	@Override
	public void setPropertyValue(Object id, Object value) {
		if(((id.equals(PROP_XPOS) || id.equals(PROP_YPOS)) && ((Integer)value)!=0)||
				((id.equals(PROP_WIDTH) || id.equals(PROP_HEIGHT)) && ((Integer)value)!=FIXED_WIDTH)
		)
			return;
		
		super.setPropertyValue(id, value);
	}
}
