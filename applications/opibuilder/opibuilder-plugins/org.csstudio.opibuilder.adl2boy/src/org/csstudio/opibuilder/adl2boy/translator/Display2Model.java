/*************************************************************************\
* Copyright (c) 2010  UChicago Argonne, LLC
* This file is distributed subject to a Software License Agreement found
* in the file LICENSE that is included with this distribution.
/*************************************************************************/

package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.ADLDisplay;
import org.eclipse.swt.graphics.RGB;

/**
 * 
 * @author John Hammonds, Argonne National Laboratory
 *
 */
public class Display2Model extends AbstractADL2Model {
	
	public Display2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
	}

	public Display2Model(RGB[] colorMap){
		super(colorMap);
	}


	public void makeModel(ADLWidget adlWidget, AbstractContainerModel parentModel){
		widgetModel = new DisplayModel();
	}
	
	@Override
	public void processWidget(ADLWidget adlWidget) {
		ADLDisplay adlDisp = new ADLDisplay(adlWidget);
	
		setDisplayColors(adlDisp);
		setShowGrid(adlDisp);
		setSnapGeometry(adlDisp);
		widgetModel.setPropertyValue(DisplayModel.PROP_GRID_SPACE, adlDisp.get_gridSpacing());
	}

	/**
	 * @param adlDisp
	 */
	public void setDisplayColors(ADLDisplay adlDisp) {
		if (adlDisp !=null){
			setADLObjectProps(adlDisp, widgetModel);
			setColor(adlDisp.getBackgroundColor(), AbstractWidgetModel.PROP_COLOR_BACKGROUND);

			setColor(adlDisp.getForegroundColor(), AbstractWidgetModel.PROP_COLOR_FOREGROUND);
			
		}
	}

	/**
	 * @param adlDisp
	 */
	public void setSnapGeometry(ADLDisplay adlDisp) {
		if ( adlDisp.is_snapToGrid()){
			widgetModel.setPropertyValue(DisplayModel.PROP_SNAP_GEOMETRY, true);
		}
		else {
			widgetModel.setPropertyValue(DisplayModel.PROP_SNAP_GEOMETRY, false);
		}
	}

	/**
	 * @param adlDisp
	 */
	public void setShowGrid(ADLDisplay adlDisp) {
		if ( adlDisp.is_gridOn()){
			widgetModel.setPropertyValue(DisplayModel.PROP_SHOW_GRID, true);
		}
		else {
			widgetModel.setPropertyValue(DisplayModel.PROP_SHOW_GRID, false);
		}
	}
}
