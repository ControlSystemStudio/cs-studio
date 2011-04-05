package org.csstudio.opibuilder.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public final class SchemaService {
	
	
	private static SchemaService instance;
	
	private Map<String, AbstractWidgetModel> schemaWidgetsMap;
	
	public SchemaService() {
		schemaWidgetsMap = new HashMap<String, AbstractWidgetModel>();
		reLoad();
	}
	
	public synchronized static final SchemaService getInstance() {
		if(instance == null)
			instance = new SchemaService();
		return instance;
	}
	
	
	/**
	 * Reload schema opi.
	 */
	public void reLoad(){
		schemaWidgetsMap.clear();
		IPath schemaOPI = PreferencesHelper.getSchemaOPIPath();
		if(schemaOPI == null || schemaOPI.isEmpty()){
			return;
		}
		try {
			InputStream inputStream = ResourceUtil.pathToInputStream(schemaOPI);
			DisplayModel displayModel = new DisplayModel();
			XMLUtil.fillDisplayModelFromInputStream(inputStream, displayModel);
			for(AbstractWidgetModel model : displayModel.getChildren()){
				schemaWidgetsMap.put(model.getTypeID(), model);
			}
		} catch (Exception e) {
			  OPIBuilderPlugin.getLogger().log(Level.WARNING,
			            "Failed to load schema opi: " + schemaOPI, e); //$NON-NLS-1$
				String message = "Failed to load schema opi!\n" + e;				
				ConsoleService.getInstance().writeError(message);
		}		
	}
	
	public void applySchema(AbstractWidgetModel widgetModel){
		if(schemaWidgetsMap.isEmpty())
			return;
		if(schemaWidgetsMap.containsKey(widgetModel.getTypeID())){
			AbstractWidgetModel schemaWidgetModel = schemaWidgetsMap.get(widgetModel.getTypeID());
			for(String id : schemaWidgetModel.getAllPropertyIDs()){
				widgetModel.setPropertyValue(id, schemaWidgetModel.getPropertyValue(id));
			}
		}
	}
	
	
	

}
