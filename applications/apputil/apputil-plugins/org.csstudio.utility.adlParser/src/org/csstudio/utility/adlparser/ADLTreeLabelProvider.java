package org.csstudio.utility.adlparser;

import org.csstudio.utility.adlparser.fileParser.ADLResource;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgetParts.WidgetPart;
import org.csstudio.utility.adlparser.fileParser.widgets.ADLAbstractWidget;
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
        if (element instanceof ADLAbstractWidget){
            ADLAbstractWidget widget = (ADLAbstractWidget)element;
            descriptor = widget.getImageDescriptor();
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
        else if (element instanceof ADLAbstractWidget){
            return ((ADLAbstractWidget)element).getName();
        }
        else if (element instanceof WidgetPart){
            return ((WidgetPart)element).getName();
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
