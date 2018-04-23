package org.csstudio.opibuilder.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
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
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;

public final class SchemaService {

    private static SchemaService instance;
    private Map<String, AbstractWidgetModel> schemaWidgetsMap;


    /*
     * Instantiating the schema service with the modal process dialog uses the UI thread.
     * When process dialog is open in UI thread it interferes with connection which is drawn from/to linking containers.
     *
     * This causes bug with the connections not being displayed.
     *
     * Instance of SchemaService without dialog is created from first instance of WidgetNodeEditPolicy
    */


    private SchemaService(boolean dialog) {
        schemaWidgetsMap = new HashMap<>();
        if (dialog){
          reLoad();
        }
        else {
          reLoadNoProgressMonitor();
        }
    }

    public static final synchronized SchemaService getInstance() {
        if (instance == null)
            instance = new SchemaService(true);
        return instance;
    }

    public static final synchronized SchemaService getInstance(boolean dialog) {
        if (instance == null)
            instance = new SchemaService(dialog);
        return instance;
    }

    /**
     * Reloading schema OPI without the progress monitor
     */

    public void reLoadNoProgressMonitor() {
        schemaWidgetsMap.clear();
        final IPath schemaOPI = PreferencesHelper.getSchemaOPIPath();
        if (schemaOPI == null || schemaOPI.isEmpty()) {
            return;
        }
        OPIBuilderPlugin.getLogger().log(Level.INFO, () -> "Schema service: connecting to " + schemaOPI);
        loadSchema(schemaOPI);
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
        if(Display.getCurrent() != null){ // in UI thread, show progress dialog
            IRunnableWithProgress job = new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException,
                        InterruptedException {
                    monitor.beginTask("Connecting to " + schemaOPI,IProgressMonitor.UNKNOWN);
                    loadSchema(schemaOPI);
                    monitor.done();
                }
            };
            try {
                new ProgressMonitorDialog(
                        Display.getCurrent().getActiveShell()).run(true, false, job);
            } catch (Exception e) {
                ErrorHandlerUtil.handleError("Failed to load schema", e);
            }
        }
        else
            loadSchema(schemaOPI);
    }

    /**
     * @param schemaOPI
     */
    public void loadSchema(final IPath schemaOPI) {
        InputStream inputStream = null;
        try {
            inputStream = ResourceUtil.pathToInputStream(schemaOPI, false);
            DisplayModel displayModel = new DisplayModel(schemaOPI);
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
        finally {
           if (inputStream != null)
            try {
                inputStream.close();
            } catch (IOException e) {
                ErrorHandlerUtil.handleError("Failed to close stream", e);
            }
        }
    }

    private void loadModelFromContainer(AbstractContainerModel containerModel) {
        for(AbstractWidgetModel model : containerModel.getChildren()){
            //always add only the first model of its type that is found
            //the main container might contain several instances of the same widget
            //(e.g. GroupingContainer can appear multiple times; it is by default the base
            //layer of a tab and sash - we don't want the tab to override our container settings)
            if (!schemaWidgetsMap.containsKey(model.getTypeID())) {
                schemaWidgetsMap.put(model.getTypeID(), model);
            }
            if(model instanceof AbstractContainerModel)
                    loadModelFromContainer((AbstractContainerModel) model);
        }
    }

    public void applySchema(AbstractWidgetModel widgetModel) {
        if (schemaWidgetsMap.isEmpty())
            return;
        if (schemaWidgetsMap.containsKey(widgetModel.getTypeID())) {
            AbstractWidgetModel schemaWidgetModel = schemaWidgetsMap
                    .get(widgetModel.getTypeID());
            for (String id : schemaWidgetModel.getAllPropertyIDs()) {
                widgetModel.setPropertyValue(id,schemaWidgetModel.getPropertyValue(id), false);
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
