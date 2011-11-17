package org.csstudio.opibuilder.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.ConnectionModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

public final class SchemaService {

	private static SchemaService instance;

	private Map<String, AbstractWidgetModel> schemaWidgetsMap;

	public SchemaService() {
		schemaWidgetsMap = new HashMap<String, AbstractWidgetModel>();
		reLoad();
	}

	public synchronized static final SchemaService getInstance() {
		if (instance == null)
			instance = new SchemaService();
		return instance;
	}

	/**
	 * Reload schema opi.
	 */
	public void reLoad() {
		schemaWidgetsMap.clear();
		final IPath schemaOPI = PreferencesHelper.getSchemaOPIPath();
		if (schemaOPI == null || schemaOPI.isEmpty()) {
			return;
		}
		Job job = new Job("Load Schema File") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Connecting to " + schemaOPI,
						IProgressMonitor.UNKNOWN);
				try {
					InputStream inputStream = ResourceUtil.pathToInputStream(
							schemaOPI, false);
					DisplayModel displayModel = new DisplayModel();
					XMLUtil.fillDisplayModelFromInputStream(inputStream,
							displayModel, Display.getDefault());
					schemaWidgetsMap.put(displayModel.getTypeID(), displayModel);
					loadModelFromContainer(displayModel);
					if(!displayModel.getConnectionList().isEmpty()){
						schemaWidgetsMap.put(
								ConnectionModel.ID, displayModel.getConnectionList().get(0));
					}
				} catch (Exception e) {
					String message = "Failed to load schema file: " + schemaOPI;
					OPIBuilderPlugin.getLogger().log(Level.WARNING,
							message, e);				
					ConsoleService.getInstance().writeError(message + "\n" + e);//$NON-NLS-1$
				}
				monitor.done();
				return Status.OK_STATUS;
			}

			private void loadModelFromContainer(AbstractContainerModel containerModel) {
				for(AbstractWidgetModel model : containerModel.getChildren()){
					schemaWidgetsMap.put(model.getTypeID(), model);
					if(model instanceof AbstractContainerModel)
							loadModelFromContainer((AbstractContainerModel) model);
				}
			}
		};
		job.schedule();

	}

	public void applySchema(AbstractWidgetModel widgetModel) {
		if (schemaWidgetsMap.isEmpty())
			return;
		if (schemaWidgetsMap.containsKey(widgetModel.getTypeID())) {
			AbstractWidgetModel schemaWidgetModel = schemaWidgetsMap
					.get(widgetModel.getTypeID());
			for (String id : schemaWidgetModel.getAllPropertyIDs()) {
				widgetModel.setPropertyValue(id,
						schemaWidgetModel.getPropertyValue(id), false);
			}
		}
	}
	
	/**Return the default property value of the widget when it is created.
	 * @param typeId typeId of the widget.
	 * @param propId propId of the property.
	 */
	public Object getDefaultPropertyValue(String typeId, String propId){
		if(schemaWidgetsMap.containsKey(typeId))
				return schemaWidgetsMap.get(typeId).getPropertyValue(propId);
		WidgetDescriptor desc = WidgetsService.getInstance().getWidgetDescriptor(typeId);
		if(desc != null){
			return desc.getWidgetModel().getPropertyValue(propId);
		}
		if(typeId.equals(ConnectionModel.ID)){
			return new ConnectionModel(null).getPropertyValue(propId);
		}
		return null;
	}

}
