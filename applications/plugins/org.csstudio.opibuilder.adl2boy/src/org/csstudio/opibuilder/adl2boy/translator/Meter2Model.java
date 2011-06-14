/*************************************************************************\
* Copyright (c) 2010  UChicago Argonne, LLC
* This file is distributed subject to a Software License Agreement found
* in the file LICENSE that is included with this distribution.
/*************************************************************************/

package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.widgets.model.AbstractMarkedWidgetModel;
import org.csstudio.opibuilder.widgets.model.MeterModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.Meter;
import org.eclipse.swt.graphics.RGB;

public class Meter2Model extends AbstractADL2Model {

	public Meter2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
	}

	@Override
	public void processWidget(ADLWidget adlWidget) {
		Meter meterWidget = new Meter(adlWidget);
		if (meterWidget != null) {
			setADLObjectProps(meterWidget, widgetModel);
			setADLMonitorProps(meterWidget, widgetModel);
		}

		//set color mode
		String color_mode = meterWidget.getColor_mode();
		if ( color_mode.equals("static") ){
			widgetModel.setPropertyValue(AbstractPVWidgetModel.PROP_FORECOLOR_ALARMSENSITIVE, false);
		}
		else if (color_mode.equals("alarm") ){
			widgetModel.setPropertyValue(AbstractPVWidgetModel.PROP_FORECOLOR_ALARMSENSITIVE, true);
		}
		else if (color_mode.equals("discrete") ){
			widgetModel.setPropertyValue(AbstractPVWidgetModel.PROP_FORECOLOR_ALARMSENSITIVE, false);
			//TODO Meter2Model Figure out what to do if colorMode is discrete
		}
		
		//TODO Add PV Limits to Meter2Model
		// Decorate the meter Model
		//TODO Meter2Model cannot show value or channel at this time. can this be added to the widget or do we need to make a grouping container.
		String label = meterWidget.getLabel();
		if ( label.equals("none")){
			widgetModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_MARKERS, false);
			widgetModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_SCALE, false);
		}
		if ( label.equals("no decorations")){
			widgetModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_MARKERS, false);
			widgetModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_SCALE, false);
		}
		if ( label.equals("outline")){
			widgetModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_MARKERS, false);
			widgetModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_SCALE, true);
		}
		if ( label.equals("limits")){
			widgetModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_MARKERS, false);
			widgetModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_SCALE, true);
		}
		if ( label.equals("channel")){
			widgetModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_MARKERS, false);
			widgetModel.setPropertyValue(AbstractMarkedWidgetModel.PROP_SHOW_SCALE, true);
		}
	}

	@Override
	public void makeModel(ADLWidget adlWidget,
			AbstractContainerModel parentModel) {
		widgetModel = new MeterModel();
		parentModel.addChild(widgetModel, true);
	}
}
