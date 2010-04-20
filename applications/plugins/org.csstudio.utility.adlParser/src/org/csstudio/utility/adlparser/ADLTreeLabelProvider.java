package org.csstudio.utility.adlparser;

import org.csstudio.utility.adlparser.fileParser.ADLResource;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLBasicAttribute;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLControl;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLMonitor;
import org.csstudio.utility.adlparser.fileParser.widgetParts.ADLObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

public class ADLTreeLabelProvider implements ILabelProvider {

	public ADLTreeLabelProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Image getImage(Object element) {
		//TODO switch to use WidgetParts and Widgets to get more complete entries
		ImageDescriptor descriptor = null;
		Image image = null;
		if (element instanceof ADLWidget){
			ADLWidget rootWidget = (ADLWidget)element;
			if (rootWidget.getType().equals("arc")) {
				descriptor = Activator.getImageDescriptor(IImageKeys.ADL_ARC);
			}
			else if (rootWidget.getType().equals("bar")) {
				descriptor = Activator.getImageDescriptor(IImageKeys.ADL_BAR_MONITOR);
			}
			else if (rootWidget.getType().equals("byte")) {
				descriptor = Activator.getImageDescriptor(IImageKeys.ADL_BYTE_MONITOR);
			}
			else if (rootWidget.getType().equals("cartesian plot")) {
				descriptor = Activator.getImageDescriptor(IImageKeys.ADL_CARTESIAN);
			}
			else if (rootWidget.getType().equals("choice button")) {
				descriptor = Activator.getImageDescriptor(IImageKeys.ADL_CHOICE_BUTTON);
			}
			else if (rootWidget.getType().equals("image")) {
				descriptor = Activator.getImageDescriptor(IImageKeys.ADL_IMAGE);
			}
			else if (rootWidget.getType().equals("indicator")) {
				descriptor = Activator.getImageDescriptor(IImageKeys.ADL_SCALE);
			}
			else if (rootWidget.getType().equals("menu")) {
				descriptor = Activator.getImageDescriptor(IImageKeys.ADL_MENU);
			}
			else if (rootWidget.getType().equals("message button")) {
				descriptor = Activator.getImageDescriptor(IImageKeys.ADL_MESSAGE_BUTTON);
			}
			else if (rootWidget.getType().equals("meter")) {
				descriptor = Activator.getImageDescriptor(IImageKeys.ADL_METER);
			}
			else if (rootWidget.getType().equals("oval")) {
				descriptor = Activator.getImageDescriptor(IImageKeys.ADL_OVAL);
			}
			else if (rootWidget.getType().equals("polygon")) {
				descriptor = Activator.getImageDescriptor(IImageKeys.ADL_POLYGON);
			}
			else if (rootWidget.getType().equals("polyline")) {
				descriptor = Activator.getImageDescriptor(IImageKeys.ADL_POLYLINE);
			}
			else if (rootWidget.getType().equals("line")) {
				descriptor = Activator.getImageDescriptor(IImageKeys.ADL_LINE);
			}
			else if (rootWidget.getType().equals("rectangle")) {
				descriptor = Activator.getImageDescriptor(IImageKeys.ADL_RECTANGLE);
			}
			else if (rootWidget.getType().equals("strip_chart")) {
				descriptor = Activator.getImageDescriptor(IImageKeys.ADL_STRIP_CHART);
			}
			else if (rootWidget.getType().equals("text")) {
				descriptor = Activator.getImageDescriptor(IImageKeys.ADL_TEXT);
			}
			else if (rootWidget.getType().equals("text entry")) {
				descriptor = Activator.getImageDescriptor(IImageKeys.ADL_TEXT_ENTRY);
			}
			else if (rootWidget.getType().equals("text update")) {
				descriptor = Activator.getImageDescriptor(IImageKeys.ADL_TEXT_MONITOR);
			}
			else if (rootWidget.getType().equals("related display")) {
				descriptor = Activator.getImageDescriptor(IImageKeys.ADL_RELATED_DISPLAY);
			}
			else if (rootWidget.getType().equals("valuator")) {
				descriptor = Activator.getImageDescriptor(IImageKeys.ADL_SLIDER);
			}
			if (!(descriptor == null)){
				image = descriptor.createImage();
			}
			
		}
		return image;
	}

	@Override
	public String getText(Object element) {
		//TODO switch to use WidgetParts and Widgets to get more complete entries
		if (element instanceof ADLWidget){
			ADLWidget rootWidget = (ADLWidget)element;
			return rootWidget.getType();
		}
		else if (element instanceof ADLBasicAttribute){
			return "basic attribute";
		}
		else if (element instanceof ADLObject){
			return "object";
		}
		else if (element instanceof ADLControl){
			return "control";
		}
		else if (element instanceof ADLMonitor){
			return "monitor";
		}
		else if (element instanceof ADLResource){
			return ((ADLResource)element).getName();
		}
		else if (element == null){
			return new String("");
		}
		return element.toString();
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	
}
