package org.csstudio.opibuilder.util;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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

/**
 *
 * <code>SchemaService</code> provides definitions of default values for widget properties. The schema is defined by an
 * opi file, which contains the different boy widgets. The properties of those widgets define the default values used
 * in the BOY editor. Whenever a widget is used in the opi editor these default values are applied to the model.
 * In addition, the schema also defines widget classes, provided via cascading style sheets. The stylesheets define
 * further define the styling of widgets according to the widget type and value of the widget class property of each
 * widget.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a> (widget classes only)
 *
 */
public final class SchemaService {

    private static SchemaService instance;

    private final Map<String, AbstractWidgetModel> schemaWidgetsMap;

    private SchemaService() {
        schemaWidgetsMap = new HashMap<>();
    }

    public static synchronized final SchemaService getInstance() {
        if (instance == null) {
            instance = new SchemaService();
            instance.reLoad();
        }
        return instance;
    }

    /**
     * Reload schema opi.
     */
    public void reLoad() {
        schemaWidgetsMap.clear();
        StyleSheetService.getInstance().clear();
        final IPath schemaOPI = PreferencesHelper.getSchemaOPIPath();
        final IPath[] styleSheets = PreferencesHelper.getWidgetClassesStylesheetPath();
        if ((schemaOPI == null || schemaOPI.isEmpty()) && styleSheets.length == 0) {
            return;
        }

        if (Display.getCurrent() != null) {
            // in UI thread, show progress dialog
            IRunnableWithProgress job = monitor -> {
                monitor.beginTask("Loading schema and stylesheet", IProgressMonitor.UNKNOWN);
                loadSchema(schemaOPI, styleSheets);
                monitor.done();
            };
            try {
                new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(true, false, job);
            } catch (Exception e) {
                ErrorHandlerUtil.handleError("Failed to load schema/stylesheet", e);
            }
        } else {
            loadSchema(schemaOPI, styleSheets);
        }
    }

    /**
     * @param schemaOPI
     */
    public void loadSchema(final IPath schemaOPI) {
        loadSchema(schemaOPI, null);
    }

    private void loadSchema(final IPath schemaOPI, final IPath[] styleSheets) {
        // first load the stylesheets, so that they can already be applied to the schema widgets
        if (styleSheets.length > 0) {
            for (IPath cssPath : styleSheets) {
                if (cssPath.isEmpty()) continue;
                try (InputStream inputStream = ResourceUtil.pathToInputStream(cssPath, false)) {
                    StyleSheetService.getInstance().addStyleSheet(cssPath);
                } catch (Exception e) {
                    String message = "Failed to load opi style sheets: " + cssPath;
                    OPIBuilderPlugin.getLogger().log(Level.WARNING, message, e);
                    ConsoleService.getInstance().writeError(message + "\n" + e);//$NON-NLS-1$
                }
            }
        }
        if (schemaOPI != null && !schemaOPI.isEmpty()) {
            try (InputStream inputStream = ResourceUtil.pathToInputStream(schemaOPI, false)) {
                DisplayModel displayModel = new DisplayModel(schemaOPI);
                XMLUtil.fillDisplayModelFromInputStream(inputStream, displayModel, Display.getDefault());
                schemaWidgetsMap.put(displayModel.getTypeID(), displayModel);
                loadModelFromContainer(displayModel);
                if (!displayModel.getConnectionList().isEmpty()) {
                    schemaWidgetsMap.put(ConnectionModel.ID, displayModel.getConnectionList().get(0));
                }
            } catch (Exception e) {
                String message = "Failed to load schema file: " + schemaOPI;
                OPIBuilderPlugin.getLogger().log(Level.WARNING, message, e);
                ConsoleService.getInstance().writeError(message + "\n" + e);//$NON-NLS-1$
            }
        }
    }

    private void loadModelFromContainer(AbstractContainerModel containerModel) {
        for (AbstractWidgetModel model : containerModel.getChildren()) {
            // always add only the first model of its type that is found
            // the main container might contain several instances of the same widget
            // (e.g. GroupingContainer can appear multiple times; it is by default the base
            // layer of a tab and sash - we don't want the tab to override our container settings)
            AbstractWidgetModel existing = schemaWidgetsMap.get(model.getTypeID());
            if (existing == null) {
                schemaWidgetsMap.put(model.getTypeID(), model);
                applyWidgetClassProperties(model);
            }
            if (model instanceof AbstractContainerModel)
                loadModelFromContainer((AbstractContainerModel) model);
        }
    }

    /**
     * Returns true if the given property is defined by the widget class or widget type defined in the style sheet.
     *
     * @param model the model the property belongs to
     * @param propertyId the id of the property that is being checked if it is set by the style sheet
     * @return true if defined by class/type or false if the property is free
     */
    public boolean isPropertyHandledidgetClass(AbstractWidgetModel model, String propertyId) {
        return StyleSheetService.getInstance().isPropertyHandledByWidgetClass(model, propertyId);
    }

    /**
     * Apply the schema to the widget model. There is always at most one schema for every widget ID. If the schema
     * exists all widget properties defined in the schema are applied to the given model.
     *
     * @param widgetModel the model to apply the schema to
     */
    public void applySchema(AbstractWidgetModel widgetModel) {
        if (schemaWidgetsMap.isEmpty())
            return;
        if (schemaWidgetsMap.containsKey(widgetModel.getTypeID())) {
            AbstractWidgetModel schemaWidgetModel = schemaWidgetsMap.get(widgetModel.getTypeID());
            for (String id : schemaWidgetModel.getAllPropertyIDs()) {
                widgetModel.setPropertyValue(id, schemaWidgetModel.getPropertyValue(id), false);
            }
        }
    }

    /**
     * Returns the list of all classes names defined in the schema for the particular widget type.
     *
     * @param widgetID the widget type for which the classes are requested
     * @return the list of all available classes for the given model
     */
    public List<String> getAvailableClassesForWidgetType(String widgetID) {
        if (widgetID == null) {
            return Collections.emptyList();
        } else {
            return StyleSheetService.getInstance().getAvailableClassesForWidgetType(widgetID);
        }
    }

    /**
     * Load the widget class for the specified model. If it exists, all properties defined in the class model (which
     * were not configured in the rules file to be skipped) are applied to the given model. If the class model for the
     * widget does not exist, nothing happens.
     *
     * @param model the model to which the widget class settings are applied
     */
    public void applyWidgetClassProperties(final AbstractWidgetModel model) {
        Map<String, Object> properties = StyleSheetService.getInstance().getPropertiesForWidget(model);
        properties.forEach((propertyId, propertyValue) -> model.setPropertyValue(propertyId, propertyValue));
    }

    /**
     * Return the default property value of the widget when it is created.
     *
     * @param typeId typeId of the widget.
     * @param propId propId of the property.
     */
    public Object getDefaultPropertyValue(String typeId, String propId) {
        if (schemaWidgetsMap.containsKey(typeId))
            return schemaWidgetsMap.get(typeId).getPropertyValue(propId);
        WidgetDescriptor desc = WidgetsService.getInstance().getWidgetDescriptor(typeId);
        if (desc != null)
            return desc.getWidgetModel().getPropertyValue(propId);
        if (typeId.equals(ConnectionModel.ID))
            return new ConnectionModel(null).getPropertyValue(propId);
        return null;
    }
}
