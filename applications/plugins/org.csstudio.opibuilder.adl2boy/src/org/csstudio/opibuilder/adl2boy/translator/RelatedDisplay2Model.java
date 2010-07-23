package org.csstudio.opibuilder.adl2boy.translator;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.widgetActions.ActionsInput;
import org.csstudio.opibuilder.widgetActions.OpenDisplayAction;
import org.csstudio.opibuilder.widgets.model.MenuButtonModel;
import org.csstudio.utility.adlparser.fileParser.ADLWidget;
import org.csstudio.utility.adlparser.fileParser.widgetParts.RelatedDisplayItem;
import org.csstudio.utility.adlparser.fileParser.widgets.RelatedDisplay;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.graphics.RGB;

public class RelatedDisplay2Model extends AbstractADL2Model {
	MenuButtonModel menuModel = new MenuButtonModel();

	public RelatedDisplay2Model(ADLWidget adlWidget, RGB[] colorMap, AbstractContainerModel parentModel) {
		super(adlWidget, colorMap, parentModel);
		parentModel.addChild(menuModel, true);
		RelatedDisplay rdWidget = new RelatedDisplay(adlWidget);
		if (rdWidget != null) {
			setADLObjectProps(rdWidget, menuModel);
			if (rdWidget.isForeColorDefined() ){
				menuModel.setForegroundColor( colorMap[rdWidget.getForegroundColor()] );
			}
			if (rdWidget.isBackColorDefined()){
				menuModel.setBackgroundColor( colorMap[rdWidget.getBackgroundColor()] );
			}
			RelatedDisplayItem[] rdDisplays = rdWidget.getRelatedDisplayItems();
			if ( rdDisplays.length > 0){
				ActionsInput ai = menuModel.getActionsInput();
				for (int ii=0; ii< rdDisplays.length; ii++){
					if (!(rdDisplays[ii].getName().replaceAll("\"", "").equals(""))){
						OpenDisplayAction odAction = new OpenDisplayAction();
	
						//Try to add the filename to the PROP_PATH
						IPath fPath = new Path(rdDisplays[ii].getName().replaceAll("\"", "").replace(".adl", ".opi"));
						System.out.println("Related display file: " + rdDisplays[ii].getName().replace(".adl", ".opi"));
						System.out.println("Related display file from IPath: " + fPath.toString() + ", " + fPath.getFileExtension());
						odAction.setPropertyValue(OpenDisplayAction.PROP_PATH, fPath);
	
						//Try to add macros
						System.out.println ("args " + rdDisplays[ii].getArgs());
						if (rdDisplays[ii].getArgs() != null){
							String argsIn = "true, " + rdDisplays[ii].getArgs().replaceAll("\"", "");
							MacrosInput macIn;
							try {
								macIn = MacrosInput.recoverFromString( argsIn);
								odAction.setPropertyValue(OpenDisplayAction.PROP_MACROS, macIn );
							} catch (Exception e) {
								e.printStackTrace();
							}
							
						}
						if (rdDisplays[ii].getLabel() != null){
							odAction.setPropertyValue(OpenDisplayAction.PROP_DESCRIPTION, rdDisplays[ii].getLabel().replaceAll("\"", ""));
						}
						if ((rdDisplays[ii].getPolicy() != null) ) {                          // policy is present
							if ( rdDisplays[ii].getPolicy().equals("replace display") ){      // replace the display
								odAction.setPropertyValue(OpenDisplayAction.PROP_REPLACE, true);
							}
							else {															  // don't replace the display
								odAction.setPropertyValue(OpenDisplayAction.PROP_REPLACE, false);
							}
						}
						else {                                                                 // policy not present go to default
							odAction.setPropertyValue(OpenDisplayAction.PROP_REPLACE, false);  //don't replace the display
						}
						ai.addAction(odAction);
					}
				}
			}
		}
		String label = rdWidget.getLabel();
		if (label.startsWith("-")){          // leading "-" was used to flag not using the icon.  Just don't use the icon and throw this away
			label = label.substring(1);
		}
		menuModel.setPropertyValue(MenuButtonModel.PROP_LABEL, label);
		//TODO Add Visual property to RelatedDisplay2Model
		//TODO Add remove parent display to RelatedDisplay2Model
	}

	@Override
	public	AbstractWidgetModel getWidgetModel() {
		return menuModel;
	}

}
