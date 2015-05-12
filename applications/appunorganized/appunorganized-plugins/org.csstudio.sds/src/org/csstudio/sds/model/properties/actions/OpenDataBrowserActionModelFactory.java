package org.csstudio.sds.model.properties.actions;


public class OpenDataBrowserActionModelFactory implements IActionModelFactory {

    public AbstractWidgetActionModel createWidgetActionModel() {
        return new OpenDataBrowserActionModel();
    }

}
