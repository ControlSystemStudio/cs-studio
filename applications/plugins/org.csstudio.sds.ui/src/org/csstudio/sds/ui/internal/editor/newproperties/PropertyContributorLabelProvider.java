package org.csstudio.sds.ui.internal.editor.newproperties;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetModelFactoryService;
import org.csstudio.ui.util.CustomMediaFactory;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.Image;

public class PropertyContributorLabelProvider implements ILabelProvider {

    public Image getImage(final Object element) {
        AbstractWidgetModel widget = getWidget(element);

        if((widget!=null) && (widget.getTypeID()!=null)) {
            String pluginId=WidgetModelFactoryService.getInstance().getContributingPluginId(widget.getTypeID());
            String iconPath = WidgetModelFactoryService.getInstance().getIcon(widget.getTypeID());

            if((pluginId!=null) && (iconPath!=null)) {
                return CustomMediaFactory.getInstance().getImageFromPlugin(pluginId, iconPath);
            }
        }

        return null;
    }

    public String getText(final Object o) {
        AbstractWidgetModel widget = getWidget(o);
        String[] split = widget.getTypeID().split("\\.");
        String typeID = split[split.length-1];
        return widget!=null?widget.getName()+" ("+typeID+")":"";
    }

    public void addListener(final ILabelProviderListener listener) {

    }

    public void dispose() {

    }

    public boolean isLabelProperty(final Object element, final String property) {
        return false;
    }

    public void removeListener(final ILabelProviderListener listener) {

    }

    private AbstractWidgetModel getWidget(final Object o) {

        if (o instanceof IStructuredSelection) {
            IStructuredSelection sel = (IStructuredSelection) o;
            if (sel.getFirstElement() instanceof EditPart) {
                EditPart ep = (EditPart) sel.getFirstElement();

                if (ep.getModel() instanceof AbstractWidgetModel) {
                    return (AbstractWidgetModel) ep.getModel();
                }
            }
        }

        return null;
    }
}
