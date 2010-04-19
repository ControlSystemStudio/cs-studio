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
					new Arc2Model(adlWidget, colorMap, parentModel);
					printNotCompletelyHandledMessage(widgetType);
				}
				else if (widgetType.equals("bar")){
					new Bar2Model(adlWidget, colorMap, parentModel);
					printNotCompletelyHandledMessage(widgetType);
				
				}
				else if (widgetType.equals("byte")){
					printNotHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("cartesian plot")){
					new CartesianPlot2Model(adlWidget, colorMap,parentModel);
					printNotCompletelyHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("choice button")){
					printNotHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("composite")){
					new Composite2Model(adlWidget, colorMap, parentModel);
					printNotCompletelyHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("dynamic symbol")){
					printNotHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("file")){
					printNotHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("image")){
					new Image2Model(adlWidget, colorMap, parentModel);
					printNotCompletelyHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("indicator")){
					printNotHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("menu")){
					new Menu2Model(adlWidget, colorMap, parentModel);
					printNotCompletelyHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("message button")){
					new MessageButton2Model(adlWidget, colorMap, parentModel);
					printNotCompletelyHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("toggle button")){
					printNotHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("meter")){
					new Meter2Model(adlWidget, colorMap, parentModel);
					printNotHandledMessage(widgetType);
						
				}
				else if (widgetType.equals("oval")){
					new Oval2Model(adlWidget, colorMap, parentModel);
					printNotCompletelyHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("polygon")){
					new Polygon2Model(adlWidget, colorMap, parentModel);
					printNotCompletelyHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("polyline")){
					new PolyLine2Model(adlWidget, colorMap, parentModel);
					printNotCompletelyHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("line")){
					printNotHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("rectangle")){
					new Rectangle2Model(adlWidget, colorMap, parentModel);
					printNotCompletelyHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("related display")){
					new RelatedDisplay2Model(adlWidget, colorMap, parentModel);
					printNotCompletelyHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("strip chart")){
					printNotHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("text")){
					new Text2Model(adlWidget, colorMap, parentModel);
					printNotCompletelyHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("text update")){
					new TextUpdate2Model(adlWidget, colorMap, parentModel);
					printNotCompletelyHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("text entry")){
					new TextEntry2Model(adlWidget, colorMap, parentModel);
					printNotHandledMessage(widgetType);
					
				}
				else if (widgetType.equals("valuator")){
					new Valuator2Model(adlWidget, colorMap, parentModel);
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

	public static int convertTextHeightToFontSize(int h){
		if (h < 9) {
			return 6;
		}
		else if (h < 10 ){
			return 6;
		}
		else if (h < 13) {
			return 8;
		}
		else if (h < 14) {
			return 9;
		}
		else if (h < 15) {
			return 10;
		}
		else if (h < 16) {
			return 12;
		}
		else if (h < 20) {
			return 14;
		}
		else if (h < 21) {
			return 16;
		}
		else if (h < 24) {
			return 18;
		}
		else if (h < 26) {
			return 18;
		}
		else if (h < 27) {
			return 20;
		}
		else if (h < 35) {
			return 24;
		}
		else if (h < 36) {
			return 26;
		}
		else {
			return 30;
		}
	}
}
