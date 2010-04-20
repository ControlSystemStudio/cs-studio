package org.csstudio.utility.adlparser;

import org.csstudio.utility.adlparser.fileParser.ADLResource;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.ADLWidgetUtils;
import org.csstudio.utility.adlparser.fileParser.widgetParts.WidgetPart;
import org.eclipse.jface.viewers.TreeNodeContentProvider;

public class ADLTreeContentProvider extends TreeNodeContentProvider {
	private ADLWidget rootWidget;
	public ADLTreeContentProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		//TODO switch to use WidgetParts and Widgets to get more complete entries
		if (parentElement instanceof ADLWidget){
			rootWidget = (ADLWidget)parentElement;
			return ADLWidgetUtils.getADLWidgetChildren(rootWidget);
		}
		else if (parentElement instanceof WidgetPart){
			return ((WidgetPart)parentElement).getChildren();
		}
		else if(parentElement instanceof ADLResource){
			Object [] ret = { ((ADLResource)parentElement).getValue() };
			return  ret;
		}
		return new Object[0];
	}

	@Override
	public boolean hasChildren(Object element) {
		//TODO switch to use WidgetParts and Widgets to get more complete entries
		if (element instanceof ADLWidget){
			rootWidget = (ADLWidget)element;
			if ((ADLWidgetUtils.getADLWidgetChildren(rootWidget)).length > 0 ){
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
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		//TODO switch to use WidgetParts and Widgets to get more complete entries
		if (inputElement instanceof ADLWidget){
			rootWidget = (ADLWidget)inputElement;
			return ADLWidgetUtils.getADLWidgetChildren(rootWidget);
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
