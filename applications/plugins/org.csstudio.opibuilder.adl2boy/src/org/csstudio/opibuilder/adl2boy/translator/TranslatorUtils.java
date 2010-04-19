package org.csstudio.opibuilder.adl2boy.translator;

import java.util.ArrayList;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.eclipse.swt.graphics.RGB;

public class TranslatorUtils {

	public static void ConvertChildren(ArrayList<ADLWidget> childWidgets, AbstractContainerModel parentModel, RGB colorMap[]){
		for (ADLWidget adlWidget : childWidgets){
			try {
				String widgetType = adlWidget.getType();
				if (widgetType.equals("arc")){
					parentModel.addChild((new Arc2Model(adlWidget, colorMap)).getWidgetModel());
					printNotCompletelyHandledMessage(widgetType);
				}
				else if (widgetType.equals("bar")){
					parentModel.addChild((new Bar2Model(adlWidget, colorMap)).getWidgetModel());
					printNotCompletelyHandledMessage(widgetType);
				
				}
				else if (widgetType.equals("byte")){
					printNotHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("cartesian plot")){
					parentModel.addChild((new CartesianPlot2Model(adlWidget, colorMap)).getWidgetModel());
					printNotCompletelyHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("choice button")){
					printNotHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("composite")){
					parentModel.addChild((new Composite2Model(adlWidget, colorMap)).getWidgetModel());
					printNotCompletelyHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("dynamic symbol")){
					printNotHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("file")){
					printNotHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("image")){
					parentModel.addChild((new Image2Model(adlWidget, colorMap)).getWidgetModel());
					printNotCompletelyHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("indicator")){
					printNotHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("menu")){
					parentModel.addChild((new Menu2Model(adlWidget, colorMap)).getWidgetModel());
					printNotCompletelyHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("message button")){
					parentModel.addChild((new MessageButton2Model(adlWidget, colorMap)).getWidgetModel());
					printNotCompletelyHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("toggle button")){
					printNotHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("meter")){
					parentModel.addChild((new Meter2Model(adlWidget, colorMap)).getWidgetModel());
					printNotHandledMessage(widgetType);
						
				}
				else if (widgetType.equals("oval")){
					parentModel.addChild((new Oval2Model(adlWidget, colorMap)).getWidgetModel());
					printNotCompletelyHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("polygon")){
					parentModel.addChild((new Polygon2Model(adlWidget, colorMap)).getWidgetModel());
					printNotCompletelyHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("polyline")){
					parentModel.addChild((new PolyLine2Model(adlWidget, colorMap)).getWidgetModel());
					printNotCompletelyHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("line")){
					printNotHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("rectangle")){
					parentModel.addChild((new Rectangle2Model(adlWidget, colorMap)).getWidgetModel());
					printNotCompletelyHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("related display")){
					parentModel.addChild((new RelatedDisplay2Model(adlWidget, colorMap)).getWidgetModel());
					printNotCompletelyHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("strip chart")){
					printNotHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("text")){
					parentModel.addChild((new Text2Model(adlWidget, colorMap)).getWidgetModel());
					printNotCompletelyHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("text update")){
					parentModel.addChild((new TextUpdate2Model(adlWidget, colorMap)).getWidgetModel());
					printNotCompletelyHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("text entry")){
					parentModel.addChild((new TextEntry2Model(adlWidget, colorMap)).getWidgetModel());
					printNotHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("valuator")){
					parentModel.addChild((new Valuator2Model(adlWidget, colorMap)).getWidgetModel());
					printNotCompletelyHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("basic attribute")){
					printNotHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("dynamic attribute")){
					printNotHandledMessage(widgetType);
					
				}
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
	}

	/** 
	 * Print message that a given ADL file structure is not handled.
	 */
	private static void printNotHandledMessage(String type) {
		System.out.println("EditHandler: " + type + " is not handled");
	}
	private static void printNotCompletelyHandledMessage(String type) {
		System.out.println("EditHandler: " + type + " is not completely handled");
	}

}
