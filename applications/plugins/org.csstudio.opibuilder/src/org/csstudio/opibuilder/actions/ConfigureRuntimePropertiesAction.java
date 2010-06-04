package org.csstudio.opibuilder.actions;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.datadefinition.PropertyData;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.visualparts.RuntimePropertiesEditDialog;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

/**Configure widget properties on runtime.
 * @author Xihui Chen
 *
 */
public class ConfigureRuntimePropertiesAction extends Action {

	private AbstractWidgetModel widgetModel;
	private Shell shell;

	public ConfigureRuntimePropertiesAction(Shell shell, AbstractWidgetModel widgetModel) {
		setText("Configure Runtime Properties...");
		setImageDescriptor(CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
				OPIBuilderPlugin.PLUGIN_ID, "icons/settingRuntimeProperty.gif"));
		this.widgetModel = widgetModel;
		this.shell = shell;
	}
	
	@Override
	public void run() {
		RuntimePropertiesEditDialog dialog = new RuntimePropertiesEditDialog(shell, widgetModel);
		if(dialog.open() == Window.OK){
			for(PropertyData p: dialog.getOutput()){
				widgetModel.setPropertyValue(p.property.getPropertyID(), p.tmpValue);
			}
		}
	}
	
	
}
