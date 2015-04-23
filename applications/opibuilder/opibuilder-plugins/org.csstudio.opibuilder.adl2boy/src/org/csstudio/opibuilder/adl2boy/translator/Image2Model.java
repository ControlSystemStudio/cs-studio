/*************************************************************************\
 * Copyright (c) 2010  UChicago Argonne, LLC
 * This file is distributed subject to a Software License Agreement found
 * in the file LICENSE that is included with this distribution.
/*************************************************************************/

package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.widgets.model.ImageModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgets.Image;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.graphics.RGB;

public class Image2Model extends AbstractADL2Model {

	public Image2Model(ADLWidget adlWidget, RGB[] colorMap,
			AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
	}

	@Override
	public void processWidget(ADLWidget adlWidget) {
		className = "Image2Model";
		Image imageWidget = new Image(adlWidget);
		if (imageWidget != null) {
			setADLObjectProps(imageWidget, widgetModel);
			setADLBasicAttributeProps(imageWidget, widgetModel, false);
			setADLDynamicAttributeProps(imageWidget, widgetModel);

		}
		IPath fPath = new Path(imageWidget.getImageName());
		System.out.println(imageWidget.getImageName());
		widgetModel.setPropertyValue(ImageModel.PROP_IMAGE_FILE,
				fPath);
		widgetModel.setPropertyValue(ImageModel.PROP_STRETCH, true);
		// TODO Add Image Type to Image2Model
		TranslatorUtils.printNotHandledWarning(className, "Image Type");
		// TODO Add ImageName 2 Image2Model
		TranslatorUtils.printNotHandledWarning(className, "Image Name");
		// TODO Add ImageCalc to Image2Model
		TranslatorUtils.printNotHandledWarning(className, "Image Calc");
	}

	@Override
	public void makeModel(ADLWidget adlWidget,
			AbstractContainerModel parentModel) {
		widgetModel = new ImageModel();
		parentModel.addChild(widgetModel, true);
	}
}
