package org.csstudio.sds.model.properties.actions;


public class OpenDataBrowserActionModelFactory implements IActionModelFactory {

    @Override
    public AbstractWidgetActionModel createWidgetActionModel() {
        return new OpenDataBrowserActionModel();
    }

}
