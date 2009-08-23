package org.csstudio.opibuilder.widgetActions;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.jface.resource.ImageDescriptor;

public class WidgetActionFactory {
	public enum ActionType{
		OPEN_DISPLAY("Open Display", createImage("icons/OPIBuilder.png")),
		WRITE_PV("Write PV", createImage("icons/OPIBuilder.png")),
		EXECUTE_CMD("Execute Command", createImage("icons/OPIBuilder.png")),
		EXECUTE_JAVASCRIPT("Execute Javascript", createImage("icons/OPIBuilder.png")),
		OPEN_FILE("Open File", createImage("icons/OPIBuilder.png"));
		
		private ImageDescriptor iconImage;
		private String description;
		private ActionType(String description, 
				ImageDescriptor iconImage) {
			this.description = description;
			this.iconImage = iconImage;
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

		
		@Override
		public String toString() {
			return description;
		}
	}
	
	public static AbstractWidgetAction createWidgetAction(ActionType actionType){
		switch (actionType) {
		case OPEN_DISPLAY:
			return new OpenDislayAction();
			

		default:
			break;
		}
		return null;
	}
}
