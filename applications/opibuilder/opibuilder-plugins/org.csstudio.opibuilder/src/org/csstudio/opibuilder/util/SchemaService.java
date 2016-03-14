package org.csstudio.opibuilder.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.regex.Pattern;

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
 * <code>SchemaService</code> provides definitions of default values for widget properties. The schema is defined
 * by an opi file, which contains the widgets with all properties configured using default values. When a widget is
 * used in the opi editor these default values are applied to the model. In addition, the schema also defines widget
 * classes, which identify different sets of values to be used with each widget type. If a widget has a widget class
 * defined, the values of properties of that widget are changed according to the definition in the schema. The widget
 * class can change in runtime, and every time that happens, the values from the appropriate class are applied to that
 * widget.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a> (widget classes only)
 *
 */
public final class SchemaService {

    private static class NonEmptyKeyMap<T> extends HashMap<String, T> {
        private static final long serialVersionUID = -2019932861512340608L;
        @Override
        public T put(String key, T value) {
            return (key == null || key.isEmpty()) ? null : super.put(key, value);
        }
    }

    private static final Pattern PROPERTY_PATTERN = Pattern.compile("[A-Z0-9a-z_\\.]*");
    private static final String FORCE = "force";

    private static SchemaService instance;

    private final Map<String, AbstractWidgetModel> schemaWidgetsMap;
    // first key is widget type, second key is the widget class name, the value is the model for that class
    private final Map<String, Map<String, AbstractWidgetModel>> widgetsClassesMap;
    // widget class rules define, which properties can be set by the widget class. If a property has match and the
    // value is false, that property will be ignored. If the value is true or the value is missing in the map, the
    // widget class property will be applied
    private final Map<String, Boolean> widgetClassesPropertiesRules;
    // Similar to above the rules can also be specified as patterns. In this case any property that is mattched by the
    // pattern behaves as described above
    private final Map<Pattern, Boolean> widgetClassesPropertiesPatternRules;


    private SchemaService() {
        schemaWidgetsMap = new HashMap<>();
        widgetsClassesMap = new NonEmptyKeyMap<>();
        widgetClassesPropertiesRules = new HashMap<>(100);
        widgetClassesPropertiesPatternRules = new HashMap<>(30);
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
        widgetsClassesMap.clear();
        final IPath schemaOPI = PreferencesHelper.getSchemaOPIPath();
        if (schemaOPI == null || schemaOPI.isEmpty()) {
            return;
        }
        final IPath rulesFile = PreferencesHelper.getWidgetClassesRulesPath();
        if (Display.getCurrent() != null) {
            // in UI thread, show progress dialog
            IRunnableWithProgress job = monitor -> {
                monitor.beginTask("Connecting to " + schemaOPI, IProgressMonitor.UNKNOWN);
                loadSchema(schemaOPI, rulesFile);
                monitor.done();
            };
            try {
                new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(true, false, job);
            } catch (Exception e) {
                ErrorHandlerUtil.handleError("Failed to load schema", e);
            }
        } else {
            loadSchema(schemaOPI, rulesFile);
        }

    }

    /**
     * @param schemaOPI
     */
    public void loadSchema(final IPath schemaOPI) {
        loadSchema(schemaOPI, null);
    }

    private void loadSchema(final IPath schemaOPI, final IPath widgetClassRules) {
        try (InputStream inputStream = ResourceUtil.pathToInputStream(schemaOPI, false)) {
            DisplayModel displayModel = new DisplayModel(schemaOPI);
            XMLUtil.fillDisplayModelFromInputStream(inputStream, displayModel, Display.getDefault());
            schemaWidgetsMap.put(displayModel.getTypeID(), displayModel);
            Map<String, AbstractWidgetModel> map = new NonEmptyKeyMap<>();
            widgetsClassesMap.put(displayModel.getTypeID(), map);
            map.put(displayModel.getWidgetClass(), displayModel);
            loadModelFromContainer(displayModel);
            if(!displayModel.getConnectionList().isEmpty()){
                schemaWidgetsMap.put(ConnectionModel.ID, displayModel.getConnectionList().get(0));
                Map<String,AbstractWidgetModel> cmap = new NonEmptyKeyMap<>();
                widgetsClassesMap.put(ConnectionModel.ID, cmap);
                displayModel.getConnectionList().forEach(m -> cmap.put(m.getWidgetClass(), m));
            }
            loadWidgetClassRulesDefinitions(widgetClassRules);
        } catch (Exception e) {
            String message = "Failed to load schema file: " + schemaOPI;
            OPIBuilderPlugin.getLogger().log(Level.WARNING, message, e);
            ConsoleService.getInstance().writeError(message + "\n" + e);//$NON-NLS-1$
        }
    }

    private void loadModelFromContainer(AbstractContainerModel containerModel) {
        for(AbstractWidgetModel model : containerModel.getChildren()){
            //always add only the first model of its type that is found
            //the main container might contain several instances of the same widget
            //(e.g. GroupingContainer can appear multiple times; it is by default the base
            //layer of a tab and sash - we don't want the tab to override our container settings)
            AbstractWidgetModel existing = schemaWidgetsMap.get(model.getTypeID());
            if (existing == null) {
                schemaWidgetsMap.put(model.getTypeID(), model);
            }
            Map<String,AbstractWidgetModel> map = widgetsClassesMap.get(model.getTypeID());
            if (map == null) {
                map = new NonEmptyKeyMap<>();
                widgetsClassesMap.put(model.getTypeID(),map);
            }
            map.put(model.getWidgetClass(), model);
            if(model instanceof AbstractContainerModel)
                loadModelFromContainer((AbstractContainerModel) model);
        }
    }

    /**
     * Returns true if the given property is controlled by the widget class or false otherwise. If the model has no
     * class defined, the property is not controlled by the class.
     *
     * @param model the model the property belongs to
     * @param propertyId the id of the property that is being checked if it is controlled by widget class
     * @return true if controlled by class or false if the property is free
     */
    public boolean isPropertyBoundToWidgetClass(AbstractWidgetModel model, String propertyId) {
        // at this stage we only check if the widget class is set on the model and we don't care if it
        // resolves or not. Perhaps the class is defined as a formula and cannot be evaluated at this time due
        // missing or non initialised pvs
        String wc = model.getWidgetClass();
        if (wc == null || wc.isEmpty()) {
            return false;
        } else {
            String typeId = model.getTypeID();
            String typeIdShort = typeId;
            int idx = typeId.lastIndexOf('.');
            if (idx > 0 && idx < typeId.length()-1) {
                typeIdShort = typeId.substring(idx + 1) + ".";
            }
            typeId += ".";
            return getRule(typeId, typeIdShort, propertyId);
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
            AbstractWidgetModel schemaWidgetModel = schemaWidgetsMap.get(widgetModel.getTypeID());
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
        String wc = widgetModel.getWidgetClassValue();
        if (wc == null || wc.trim().isEmpty()) {
            return Optional.empty();
        }
        Map<String, AbstractWidgetModel> classes = widgetsClassesMap.get(widgetModel.getTypeID());
        if (classes == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(classes.get(wc));
    }

    /**
     * Returns the list of all classes names defined in the schema for the particular widget model. A class is defined
     * for the model if the type of the model matches the type of the model represented by the class.
     *
     * @param model the model for which the classes are requested (if not provided all classes are returned)
     * @return the list of all available classes for the given model
     */
    public List<String> getAvailableClassesForModel(Optional<AbstractWidgetModel> model) {
        if (model.isPresent()) {
            return getAvailableClassesForWidgetType(model.get().getTypeID());
        } else {
            //if no model is specified just return everything
            final ArrayList<String> list = new ArrayList<>(300);
            widgetsClassesMap.values().forEach(m -> list.addAll(m.keySet()));
            Collections.sort(list);
            return Collections.unmodifiableList(list);
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
            Map<String, AbstractWidgetModel> cls = widgetsClassesMap.get(widgetID);
            if (cls == null) {
                return Collections.emptyList();
            } else {
                List<String> classes = new ArrayList<>(cls.keySet());
                Collections.sort(classes);
                return Collections.unmodifiableList(classes);
            }
        }
    }

    /**
     * Load the widget class for the specified model. If it exists, all properties defined in the class model (which
     * were not configured in the rules file to be skipped) are applied to the given model. If the class model for the
     * widget does not exist, nothing happens.
     *
     * @param model the model to which the widget class settings are applied
     */
    public void applyWidgetClassProperties(AbstractWidgetModel model) {
        getWidgetClass(model).ifPresent(widgetClass -> {
            //do not apply the class to a widget that belongs to the schema
            if (widgetClass == model) return;
            String typeId = widgetClass.getTypeID();
            String typeIdShort = typeId;
            int idx = typeId.lastIndexOf('.');
            if (idx > 0 && idx < typeId.length()-1) {
                typeIdShort = typeId.substring(idx + 1) + ".";
            }
            typeId += ".";
            for (String id : widgetClass.getAllPropertyIDs()) {
                if (getRule(typeId + id, typeIdShort + id, id)) {
                    // if rule == true, the property was explicitly defined as force, or it wasn't specified at all
                    model.setPropertyValue(id,widgetClass.getPropertyValue(id));
                }
            }
        });
    }

    private boolean getRule(String widgetType, String widgetTypeShort, String propertyId) {
        Boolean rule = widgetClassesPropertiesRules.get(widgetTypeShort);
        if (rule == null) {
            rule = widgetClassesPropertiesRules.get(widgetType);
        }
        if (rule == null) {
            rule = widgetClassesPropertiesRules.get(propertyId);
        }
        if (rule == null) {
            for (Entry<Pattern, Boolean> e : widgetClassesPropertiesPatternRules.entrySet()) {
                if (e.getKey().matcher(widgetTypeShort).matches()) {
                    return e.getValue();
                } else if (e.getKey().matcher(widgetType).matches()) {
                    return e.getValue();
                } else if (e.getKey().matcher(propertyId).matches()) {
                    return e.getValue();
                }
            }
            return true;
        } else {
            return rule;
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
        if(desc != null)
            return desc.getWidgetModel().getPropertyValue(propId);
        if(typeId.equals(ConnectionModel.ID))
            return new ConnectionModel(null).getPropertyValue(propId);
        return null;
    }

    private void loadWidgetClassRulesDefinitions(IPath rulesFile) {
        widgetClassesPropertiesRules.clear();
        widgetClassesPropertiesPatternRules.clear();
        //widget class and widget type are always skipped
        widgetClassesPropertiesRules.put(AbstractWidgetModel.PROP_WIDGET_CLASS, Boolean.FALSE);
        widgetClassesPropertiesRules.put(AbstractWidgetModel.PROP_WIDGET_CLASS_VALUE, Boolean.FALSE);
        widgetClassesPropertiesRules.put(AbstractWidgetModel.PROP_WIDGET_TYPE, Boolean.FALSE);
        widgetClassesPropertiesRules.put(AbstractWidgetModel.PROP_WIDGET_UID, Boolean.FALSE);
        widgetClassesPropertiesRules.put(ConnectionModel.PROP_SRC_PATH, Boolean.FALSE);
        widgetClassesPropertiesRules.put(ConnectionModel.PROP_SRC_TERM, Boolean.FALSE);
        widgetClassesPropertiesRules.put(ConnectionModel.PROP_SRC_WUID, Boolean.FALSE);
        widgetClassesPropertiesRules.put(ConnectionModel.PROP_TGT_PATH, Boolean.FALSE);
        widgetClassesPropertiesRules.put(ConnectionModel.PROP_TGT_TERM, Boolean.FALSE);
        widgetClassesPropertiesRules.put(ConnectionModel.PROP_TGT_WUID, Boolean.FALSE);
        if (rulesFile == null) {
            //if no file is specified use default rules, which ignores the widget name and position
            widgetClassesPropertiesRules.put(AbstractWidgetModel.PROP_NAME, Boolean.FALSE);
            widgetClassesPropertiesRules.put(AbstractWidgetModel.PROP_XPOS, Boolean.FALSE);
            widgetClassesPropertiesRules.put(AbstractWidgetModel.PROP_YPOS, Boolean.FALSE);
            widgetClassesPropertiesRules.put(AbstractWidgetModel.PROP_RULES, Boolean.FALSE);
        } else {
            try (InputStream inputStream = ResourceUtil.pathToInputStream(rulesFile, false)) {
                Properties p = new Properties();
                p.load(inputStream);
                for (Entry<Object, Object> e : p.entrySet()) {
                    String key = (String) e.getKey();
                    Boolean value = FORCE.equalsIgnoreCase((String) e.getValue()) ? Boolean.TRUE : Boolean.FALSE;
                    if (PROPERTY_PATTERN.matcher(key).matches()) {
                        widgetClassesPropertiesRules.put(key, value);
                    } else {
                        Pattern ptrn = Pattern.compile(key);
                        widgetClassesPropertiesPatternRules.put(ptrn, value);
                    }
                }
            } catch (Exception e) {
                OPIBuilderPlugin.getLogger().log(Level.WARNING, "Could not read the widget class rules from "
                    + rulesFile, e);
            }
        }
    }
}
