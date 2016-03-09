package org.csstudio.opibuilder.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    private final Map<String, AbstractWidgetModel> schemaWidgetsMap;
    private final Map<String, AbstractWidgetModel> widgetsClassesMap;


    private SchemaService() {
        schemaWidgetsMap = new HashMap<>();
        widgetsClassesMap = new HashMap<>();
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
        widgetsClassesMap.clear();
        final IPath schemaOPI = PreferencesHelper.getSchemaOPIPath();
        if (schemaOPI == null || schemaOPI.isEmpty()) {
            return;
        }
        if(Display.getCurrent() != null){ // in UI thread, show progress dialog
            IRunnableWithProgress job = new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException,
                        InterruptedException {
                    monitor.beginTask("Connecting to " + schemaOPI,
                            IProgressMonitor.UNKNOWN);
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
        try {
            InputStream inputStream = ResourceUtil.pathToInputStream(
                    schemaOPI, false);
            DisplayModel displayModel = new DisplayModel(schemaOPI);
            XMLUtil.fillDisplayModelFromInputStream(inputStream,
                    displayModel, Display.getDefault());
            schemaWidgetsMap.put(displayModel.getTypeID(), displayModel);
            widgetsClassesMap.put(displayModel.getName(), displayModel);
            loadModelFromContainer(displayModel);
            if(!displayModel.getConnectionList().isEmpty()){
                schemaWidgetsMap.put(
                        ConnectionModel.ID, displayModel.getConnectionList().get(0));
                for (ConnectionModel m : displayModel.getConnectionList()) {
                    widgetsClassesMap.put(m.getName(), m);
                }
            }
        } catch (Exception e) {
            String message = "Failed to load schema file: " + schemaOPI;
            OPIBuilderPlugin.getLogger().log(Level.WARNING,
                    message, e);
            ConsoleService.getInstance().writeError(message + "\n" + e);//$NON-NLS-1$
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
            widgetsClassesMap.put(model.getName(), model);
            if(model instanceof AbstractContainerModel)
                    loadModelFromContainer((AbstractContainerModel) model);
        }
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
            AbstractWidgetModel schemaWidgetModel = schemaWidgetsMap
                    .get(widgetModel.getTypeID());
            for (String id : schemaWidgetModel.getAllPropertyIDs()) {
                widgetModel.setPropertyValue(id,schemaWidgetModel.getPropertyValue(id), false);
            }
        }
    }

    /**
     * Returns the widget class model that matches the widget class definition in the given model. If the model
     * provided by the <code>widgetModel</code> parameter has the property <code>widget_class</code> defined and
     * is not an empty string, the widget class for that name is loaded. If a widget class with that name does not
     * exist, an empty object is returned.
     *
     * @param widgetModel the widget model for which the widget class is required.
     */
    public Optional<AbstractWidgetModel> getWidgetClass(AbstractWidgetModel widgetModel) {
        if (widgetsClassesMap.isEmpty()) {
            return Optional.empty();
        }
        String wc = widgetModel.getWidgetClass();
        if (wc == null || wc.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(widgetsClassesMap.get(wc));
    }

    /**
     * Returns the list of all classes names defined in the schema.
     *
     * @return the list of all classes names
     */
    public List<String> getAvailableClasses() {
        return Collections.unmodifiableList(new ArrayList<>(widgetsClassesMap.keySet()));
    }

    /**
     * Returns the list of all classes names defined in the schema for the particular widget model. A class is defined
     * for the model if the type of the model matches the type of the model represented by the class.
     *
     * @param model the model for which the classes are requested
     * @return the list of all available classes for the given model
     */
    public List<String> getAvailableClassesForModel(AbstractWidgetModel model) {
        final String type = model.getWidgetType();
        return Collections.unmodifiableList(widgetsClassesMap.entrySet().stream()
                .filter(entry -> type.equals(entry.getValue().getWidgetType()))
                .map(entry -> entry.getKey())
                .collect(Collectors.toList()));
    }

    public void applyWidgetClassProperties(AbstractWidgetModel model) {
        getWidgetClass(model).ifPresent(m -> {
            //TODO
        });
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

    private static final Pattern TRUE_PROPERTY_PATTERN = Pattern.compile("[0-9a-z_\\.]*");
    private static final String FORCE = "force";

    private void loadWidgetClassRulesDefinitions(IPath rulesFile) {
        Map<String, Boolean> generalRules = new HashMap<>();
        Map<String, Boolean> widgetSpecificRules = new HashMap<>();

        if (rulesFile != null) {
            Properties p = new Properties();
            try (FileInputStream stream = new FileInputStream(rulesFile.toFile())) {
                p.load(stream);
            } catch (IOException e) {

            }
            String ruleStr;
            for (Entry<Object, Object> e : p.entrySet()) {
                String key = ((String) e.getKey()).toLowerCase(Locale.UK);
                String value = (String) e.getValue();

                if (TRUE_PROPERTY_PATTERN.matcher(key).matches()) {
                    if (key.indexOf('.') > 0) {
                        // widget specific rule
                        widgetSpecificRules.put(key, FORCE.equalsIgnoreCase(value) ? Boolean.TRUE : Boolean.FALSE);
                    } else {
                        // general rule
                        generalRules.put(key, FORCE.equalsIgnoreCase(value) ? Boolean.TRUE : Boolean.FALSE);
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws Exception{
        Properties p = new Properties();
        p.load(new FileInputStream(new File("widgetClassRules.def")));
        System.out.println(p);
    }

}
