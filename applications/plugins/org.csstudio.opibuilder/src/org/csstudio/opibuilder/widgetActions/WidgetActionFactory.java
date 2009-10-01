package org.csstudio.opibuilder.widgetActions;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ImageDescriptor;

public class WidgetActionFactory {
	public enum ActionType{
		OPEN_DISPLAY("Open OPI", createImage("icons/OPIRunner.png")), //$NON-NLS-2$
		WRITE_PV("Write PV", createImage("icons/writePV.png")), //$NON-NLS-2$
		EXECUTE_CMD("Execute Command", createImage("icons/command.gif")), //$NON-NLS-2$
		EXECUTE_JAVASCRIPT("Execute Javascript", createImage("icons/exeJS.png")),//$NON-NLS-2$
		OPEN_FILE("Open File", createImage("icons/openFile.png"));//$NON-NLS-2$
		
		private ImageDescriptor iconImage;
		private String description;
		private ActionType(String description, 
				ImageDescriptor iconImage) {
			this.description = description;
			this.iconImage = iconImage;
		}
		
		/**Parse a string to an ActionType. The string should be equal to the results
		 * of element.toString().
		 * @param actionString.
		 * @return the ActionType. null if parse failed.
		 */
		public static ActionType parseAction(String actionString){
			for(ActionType type : values()){
				if(actionString.equals(type.toString()))
					return type;
			}
			return null;		
		}

		/**
		 * @return the iconImageData
		 */
		public ImageDescriptor getIconImage() {
			return iconImage;
		}
		
		/**
		 * @return the description
		 */
		public String getDescription() {
			return description;
		}

		
		private static ImageDescriptor createImage(String path) {			
			ImageDescriptor image = CustomMediaFactory.getInstance().getImageDescriptorFromPlugin(
					OPIBuilderPlugin.PLUGIN_ID, path);				
			return image;
		}
		
	}
	
	public static AbstractWidgetAction createWidgetAction(ActionType actionType){
		Assert.isNotNull(actionType);
		switch (actionType) {
		case OPEN_DISPLAY:
			return new OpenDisplayAction();			
		case WRITE_PV:
			return new WritePVAction();
		case OPEN_FILE:
			return new OpenFileAction();
		case EXECUTE_CMD:
			return new ExecuteCommandAction();
		case EXECUTE_JAVASCRIPT:
			return new ExecuteJavaScriptAction();
		default:
			break;
		}
		return null;
	}
}
