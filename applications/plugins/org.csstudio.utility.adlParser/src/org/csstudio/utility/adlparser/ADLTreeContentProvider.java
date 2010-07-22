package org.csstudio.utility.adlparser;

import java.util.ArrayList;

import org.csstudio.utility.adlparser.fileParser.ADLResource;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.ADLWidgetUtils;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLChildren;
import org.csstudio.utility.adlparser.fileParser.widgetParts.WidgetPart;
import org.csstudio.utility.adlparser.fileParser.widgets.ADLAbstractWidget;
import org.eclipse.jface.viewers.TreeNodeContentProvider;

public class ADLTreeContentProvider extends TreeNodeContentProvider {
	private ADLWidget rootWidget;
	public ADLTreeContentProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ADLWidget){
			rootWidget = (ADLWidget)parentElement;
			Object widgets[] = ADLWidgetUtils.adlWidgetArray2ObjectArray(rootWidget.getObjects());
			return widgets;
		}
		else if (parentElement instanceof ADLAbstractWidget){
			return ((ADLAbstractWidget)parentElement).getChildren();
		}
		else if (parentElement instanceof ADLChildren){
			return ADLWidgetUtils.adlWidgetArray2ObjectArray( ((ADLChildren)parentElement).getAdlChildrens());
		}
		else if (parentElement instanceof WidgetPart){
			return ((WidgetPart)parentElement).getChildren();
		}
		else if(parentElement instanceof ADLResource){
			Object [] ret = { ((ADLResource)parentElement).getValue() };
			return  ret;
		}
		else {
			Object [] ret = { ((ADLResource)parentElement).getValue() };
			return  ret;
		}
	}

	@Override
	public boolean hasChildren(Object element) {
		//TODO switch to use WidgetParts and Widgets to get more complete entries
		if (element instanceof ADLWidget){
			rootWidget = (ADLWidget)element;
			if ( (ADLWidgetUtils.adlWidgetArray2ObjectArray(rootWidget.getObjects())).length > 0 ){
				return true;
			}
		}
		else if (element instanceof ADLAbstractWidget ){
			ADLAbstractWidget adlWidget = (ADLAbstractWidget)element;
			System.out.println(adlWidget.toString() + " " + adlWidget.getChildren().length);
			if (adlWidget.getChildren().length > 0 ){
				return true;
			}
		}
		else if (element instanceof WidgetPart ){
			WidgetPart widgetPart = (WidgetPart)element;
			System.out.println(widgetPart.toString() + " " + widgetPart.getChildren().length);
			if (widgetPart.getChildren().length > 0 ){
				return true;
			}
		}
		else if (element instanceof ADLResource){
			return true;
		}
		else if(element instanceof ArrayList){
			int numADLWidgets = 0;
			try {
				ArrayList<ADLWidget>widgetList = (ArrayList<ADLWidget>)element;
				if( widgetList.size() > 0) {
					return true;
				}
			}
			catch (ClassCastException ex){
				ex.printStackTrace();
			}
		}

	return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		//TODO switch to use WidgetParts and Widgets to get more complete entries
		if (inputElement instanceof ADLWidget){
			rootWidget = (ADLWidget)inputElement;
			return ADLWidgetUtils.adlWidgetArray2ObjectArray(rootWidget.getObjects());
		}
		else if (inputElement instanceof ADLAbstractWidget){
			return ((ADLAbstractWidget)inputElement).getChildren();
		}
		else if (inputElement instanceof WidgetPart){
			return ((WidgetPart)inputElement).getChildren();
		}
		else if(inputElement instanceof ADLResource){
			Object [] ret = { ((ADLResource)inputElement).getValue().toString() };
			return  ret;
		}
		
		return new Object[0];
	}

	
}
