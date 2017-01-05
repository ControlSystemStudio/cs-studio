package org.csstudio.opibuilder.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.csstudio.opibuilder.datadefinition.DisplayScaleData;
import org.csstudio.opibuilder.datadefinition.WidgetScaleData;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

import cz.vutbr.web.css.CSSFactory;
import cz.vutbr.web.css.CombinedSelector;
import cz.vutbr.web.css.Declaration;
import cz.vutbr.web.css.RuleBlock;
import cz.vutbr.web.css.RuleFontFace;
import cz.vutbr.web.css.RuleSet;
import cz.vutbr.web.css.Selector;
import cz.vutbr.web.css.Selector.Combinator;
import cz.vutbr.web.css.StyleSheet;
import cz.vutbr.web.css.Term;

/**
 *
 * <code>StyleSheetService</code> is a service that can parse cascading style sheet documents and apply the properties
 * to a boy widget.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class StyleSheetService {

    private static class WidgetClass {

        // the map of property ids and values as they are understood by the widget models
        Map<String, Object> realPropertyValues;
        Map<String, Object> defaultProperties = new HashMap<>();
        Map<String, Object> realDefaultProperties;
        final Map<String, Object> properties;
        final String widgetClass;
        final String widgetType;
        final String parentWidgetClass;
        final String parentWidgetType;

        /**
         * Construct a widget class, which has a parent class defined. Every such class inherits all properties of its
         * parent. There are no restrictions to which which type can extend a particular type or class, meaning that a
         * <code>Combo</code> widget type can extend <code>Field</code> widget type if so desired.
         *
         * @param widgetType the widget type
         * @param widgetClass the widget class
         * @param parentWidgetType the widget type of the parent
         * @param parentWidgetClass the widget class of the parent
         * @return widget class for the above parameters
         */
        static WidgetClass of(String widgetType, String widgetClass, String parentWidgetType,
            String parentWidgetClass) {
            return new WidgetClass(widgetType, widgetClass, parentWidgetType, parentWidgetClass);
        }

        /**
         * Construct a standalone widget class (does not inherit properties of any other class). It is not needed that
         * the type and class are both defined. Either of them can be null, however at least one has to be non-null and
         * non-empty.
         *
         * @param widgetType the widget type
         * @param widgetClass the widget class
         * @return the widget class for the above parameters
         */
        static WidgetClass of(String widgetType, String widgetClass) {
            return new WidgetClass(widgetType, widgetClass, null, null);
        }

        private WidgetClass(String widgetType, String widgetClass, String parentWidgetType, String parentWidgetClass) {
            this.widgetClass = widgetClass;
            this.widgetType = widgetType;
            this.parentWidgetClass = parentWidgetClass;
            this.parentWidgetType = parentWidgetType;
            this.properties = new HashMap<>();
        }

        /**
         * Set the default widget type properties. These are not merged with the widget class properties and are
         * shared among all widgets of the same type
         *
         * @param properties the properties
         */
        void setDefaultProperties(Map<String,Object> properties) {
            this.defaultProperties = properties;
        }

        /**
         * Replace existing properties of this class with a new set of properties.
         *
         * @param properties the new set of properties for this widget class
         */
        void setProperties(Map<String, Object> properties) {
            this.properties.clear();
            this.properties.putAll(properties);
        }

        /**
         * Returns true if this is the default style sheet for a particular widget type (no widget class defined).
         *
         * <pre>
         * <code>
         * widgetType {
         *    property: value;
         * }
         * </code>
         * </pre>
         *
         * @param widgetType the widget type which this class is checked for
         * @return true if this definition is the default widget type definition for the given type
         */
        boolean isDefaultWidgetTypeFor(String widgetType) {
            return widgetType.equalsIgnoreCase(this.widgetType) && widgetClass == null && parentWidgetClass == null
                && parentWidgetType == null;
        }

        /**
         * Merge the properties of the given widget class with this one. If both classes have the same property, the
         * property of this class has precedence. The merged properties are set for this widget class.
         *
         * @param wc the widget class to merge its properties into this one
         */
        void merge(WidgetClass wc) {
            Map<String, Object> props = new HashMap<>(wc.properties);
            props.putAll(properties);
            setProperties(props);
        }
    }

    // Different properties names and values used in the rules definitions
    private static final String FONT_FAMILY = "font-family";
    private static final String FONT_WEIGHT = "font-weight";
    private static final String FONT_STYLE = "font-style";
    private static final String FONT_BOLD = "bold";
    private static final String FONT_ITALIC = "italic";
    private static final String FONT_SIZE = "font-size";
    private static final String FONT_SRC = "src";
    private static final String TRUE = "true";
    private static final String FALSE = "false";
    // The name of the class of the default widget type rule in the widgetTypeClasses map (the rules without class
    // defined in .css), also the name of the default widget type (the rules without type in .css) in the maps within
    // the widgetTypeClasses
    private static final String DEFAULT_TYPE_CLASS = ".";

    private static final StyleSheetService INSTANCE = new StyleSheetService();

    public static final StyleSheetService getInstance() {
        return INSTANCE;
    }

    private static final Logger LOGGER = Logger.getLogger(StyleSheetService.class.getName());

    //use synchronized maps, because the reload can be called from the UI or non UI thread
    private Map<String, OPIColor> colors =  Collections.synchronizedMap(new HashMap<>());
    private Map<String, OPIFont> fonts =  Collections.synchronizedMap(new HashMap<>());
    // <widget type, <widget class name, <WC(property name, property value)>>>
    private Map<String, Map<String, WidgetClass>> widgetTypeClasses = Collections.synchronizedMap(new HashMap<>());

    /**
     * Returns the list of all widget class names for the specified widget type ID. The widget type can be either the
     * short type ID (e.g. combo) or the long one (e.g. org.csstudio.opibuilder.widgets.combo). In the later case the
     * type is stripped from the prefixes and only the last part is used to identify the widget classes.
     *
     * @param widgetTypeID the widget type id
     * @return unmodifiable list of widget classes that can be assigned to the widget of the given type
     */
    public List<String> getAvailableClassesForWidgetType(String widgetTypeID) {
        // strip the widget id of any prefixes: the widget type in css cannot contain any dots,
        // therefore we take only the last part of the widget id
        String wid = getId(widgetTypeID);
        // gather default classes (valid for all widget types)
        Map<String, WidgetClass> classes = widgetTypeClasses.get(DEFAULT_TYPE_CLASS);
        Set<String> availableClasses = new HashSet<>();
        if (classes != null) {
            availableClasses.addAll(classes.keySet());
        }
        // and the classes specific to the given widget
        classes = widgetTypeClasses.get(wid);
        if (classes != null) {
            availableClasses.addAll(classes.keySet());
        }
        //remove the DEFAULT_TYPE_CLASS if defined, because that is not a class
        availableClasses.remove(DEFAULT_TYPE_CLASS);
        List<String> retVal = new ArrayList<>(availableClasses);
        Collections.sort(retVal);
        return Collections.unmodifiableList(retVal);
    }

    /**
     * Returns the map of all property name and value pairs defined for the widget class applied to the given model. If
     * the model has the widget class defined, these are the properties of its widget class. If the model does not have
     * any widget class, these are the the default properties of the widget type (if defined in the stylesheet). The
     * returned map contains the property names as keys and property values in appropriate format as values. If there
     * are no style sheet rules for this model an empty map is returned.
     *
     * @param model the model specifying the widget class and type for which the property values are requested
     * @return the map of property name and value pairs
     */
    public Map<String, Object> getPropertiesForWidget(AbstractWidgetModel model) {
        List<WidgetClass> classes = getWidgetClass(model, true);
        if (classes.isEmpty()) {
            return Collections.emptyMap();
        } else {
            Map<String,Object> props = new HashMap<>();
            Map<String,Object> retVal = new HashMap<>();
            for (WidgetClass wc : classes) {
                if (wc.realPropertyValues == null) {
                    // transform the properties to the appropriate value
                    Map<String, Object> properties = wc.properties;
                    wc.realPropertyValues = new HashMap<>();
                    properties.forEach((propertyID, value) -> wc.realPropertyValues.put(propertyID,
                        transformValueToPropertyType(value, model.getProperty(propertyID), model)));
                    wc.realDefaultProperties = new HashMap<>();
                    properties = wc.defaultProperties;
                    properties.forEach((propertyID, value) -> wc.realDefaultProperties.put(propertyID,
                        transformValueToPropertyType(value, model.getProperty(propertyID), model)));
                }
                //collect the class properties separately from the default properties
                props.putAll(wc.realPropertyValues);
                //merge default properties first and then add the class specific properties. This way class specific
                //properties always have precedence over any default ones
                retVal.putAll(wc.realDefaultProperties);
            }
            retVal.putAll(props);
            return retVal;
        }
    }

    /**
     * Transform the property value as read from the style sheet document to the type understood by widget property.
     *
     * @param value the value to transform
     * @param property the property which defines the type into which the value should be transformed
     * @param model the widget model that ownss the property
     * @return transformed value if transformation was successful, or the same value if unsuccessful
     */
    @SuppressWarnings("unchecked")
    private static Object transformValueToPropertyType(Object value, AbstractWidgetProperty property,
        AbstractWidgetModel model) {
        if (property == null) {
            return value;
        }
        Object newValue = property.checkValue(value);
        if (newValue != null) {
            return newValue;
        }
        try {
            if (property.getDefaultValue() instanceof WidgetScaleData && value instanceof List) {
                List<Boolean> scaleOptions = (List<Boolean>) value;
                if (scaleOptions.size() > 2) {
                    return new WidgetScaleData(model, scaleOptions.get(0), scaleOptions.get(1), scaleOptions.get(2));
                }
            } else if (property.getDefaultValue() instanceof DisplayScaleData && value instanceof List) {
                List<Object> scaleOptions = (List<Object>) value;
                DisplayScaleData dsd = new DisplayScaleData(model);
                boolean firstNumber = true;
                for (int i = 0; i < scaleOptions.size(); i++) {
                    Object v = scaleOptions.get(i);
                    if (v instanceof Boolean) {
                        dsd.setPropertyValue(DisplayScaleData.PROP_AUTO_SCALE_WIDGETS, (Boolean) v);
                    } else if (v instanceof Number) {
                        if (firstNumber) {
                            dsd.setPropertyValue(DisplayScaleData.PROP_MIN_WIDTH, ((Number) v).intValue());
                        } else {
                            dsd.setPropertyValue(DisplayScaleData.PROP_MIN_HEIGHT, ((Number) v).intValue());
                        }
                        firstNumber = false;
                    }
                }
                return dsd;
            }
        } catch (ClassCastException e) {
            LOGGER.log(Level.WARNING, "Value of type '{0}' could not be transformed to type '{1}' for roperty '{2}'.",
                new Object[] { value.getClass(), property.getDefaultValue().getClass(), property.getPropertyID() });
        }
        // if value could not be transformed, return the original value
        return value;
    }

    /**
     * Returns the widget classes that are applicable to the given model. The widget class are chosen based on the
     * widget type and the widget class value in the model. All widget classes that are set on this widget are returned
     * in a list, order in the same way as they are ordered in the widget class value property.
     *
     * @param model the model for which the widget class is requested
     * @param includeDefault true to include the widget type default or false to exclude it
     * @return the list widget classes
     */
    private List<WidgetClass> getWidgetClass(AbstractWidgetModel model, boolean includeDefault) {
        String wclass = model.getWidgetClassValue();
        WidgetClass wc = null;
        Map<String, WidgetClass> values = widgetTypeClasses.get(getId(model.getTypeID()));
        List<WidgetClass> retVal = new ArrayList<>();
        if (wclass == null || wclass.isEmpty() || "null".equals(wclass)) {
            if (includeDefault) {
                // take the type specific widget class
                if (values != null) {
                    wc = values.get(DEFAULT_TYPE_CLASS);
                    if (wc != null) {
                        retVal.add(wc);
                    }
                }
            }
        } else {
            String[] classes = wclass.split("[ ]+");
            for (String c : classes) {
                // take the widget class specific ones
                if (values != null) {
                    wc = values.get(c);
                }
                if (wc == null) {
                    // if the widget class is not defined for the widget type, take the type specific class
                    values = widgetTypeClasses.get(DEFAULT_TYPE_CLASS);
                    if (values != null) {
                        wc = values.get(c);
                        if (wc != null) {
                            retVal.add(wc);
                        }
                    }
                } else {
                    retVal.add(wc);
                }
            }
        }
        return retVal;
    }

    /**
     * Returns true if the property for the given widget model is set by the widget class applied to the model. If the
     * property is not changed by the model's current widget class (the value was set manually), false is returned.
     *
     * @param model the model to provide the widget class specification
     * @param propertyId the property name which is being checked
     * @return true if the value of property is defined in the widget class or false if the value is free
     */
    public boolean isPropertyHandledByWidgetClass(AbstractWidgetModel model, String propertyId) {
        //if the widget class is not defined, than do not include the widget type settings
        List<WidgetClass> classes = getWidgetClass(model, false);
        if (classes.isEmpty()) {
            return false;
        } else {
            for (WidgetClass wc : classes) {
                if (wc.properties.containsKey(propertyId)) {
                    return true;
                } else if (wc.defaultProperties.containsKey(propertyId)) {
                    return true;
                }
            }
            return false;
        }

    }

    /**
     * Clear all style definitions in this service.
     */
    public void clear() {
        colors.clear();
        fonts.clear();
        widgetTypeClasses.clear();
    }

    /**
     * Clear all values that are already cached by this service and load the style sheet.
     *
     * @param pathToCss the path to the style sheet to load
     * @throws IOException if there was an error loading the file
     */
    public void loadStyleSheet(IPath pathToCss) throws IOException {
        clear();
        addStyleSheet(pathToCss);
    }

    /**
     * Open the style sheet and add the rules to the already loaded set of rules.
     *
     * @param pathToCss the path to the css file
     * @throws IOException if there was an error loading the file
     */
    public void addStyleSheet(IPath pathToCss) throws IOException {
        StyleSheet css;
        try {
            css = CSSFactory.parse(ResourceUtil.getFile(pathToCss).toURI().toURL(), StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            throw new IOException(e);
        }
        // in first pass read only the fonts and colours, to have the constants ready if needed later
        for (RuleBlock<?> rule : css) {
            if (rule instanceof RuleSet) {
                RuleSet rs = (RuleSet) rule;
                CombinedSelector[] selectors = rs.getSelectors();
                if (selectors.length == 1 && "*".equals(selectors[0].getLastSelector().getElementName())) {
                    // this is the colours and other constants definition rule
                    parseConstant(rs);
                }
            } else if (rule instanceof RuleFontFace) {
                parseFontFace((RuleFontFace) rule);
            } else {
                LOGGER.log(Level.WARNING, "Unknown rule {0}", rule);
            }
        }
        List<WidgetClass> widgetClasses = new ArrayList<>();
        // now read the rest
        for (RuleBlock<?> rule : css) {
            if (rule instanceof RuleSet) {
                RuleSet rs = (RuleSet) rule;
                CombinedSelector[] selectors = rs.getSelectors();
                if (selectors.length > 0 && !"*".equals(selectors[0].getLastSelector().getElementName())) {
                    List<WidgetClass> classes = new ArrayList<>();
                    for (CombinedSelector cs : selectors) {
                        toWidgetClass(cs).ifPresent(wc -> classes.add(wc));
                    }
                    Map<String, Object> map = parseRule(rs);
                    classes.forEach(cl -> cl.setProperties(map));
                    widgetClasses.addAll(classes);
                }
            }
        }
        // combine the properties children etc.
        for (WidgetClass wc : widgetClasses) {
            handleWidgetClass(wc, widgetClasses);
        }
    }

    /**
     * Find the parents of the given widgets and combine the widget's properties with the parents' properties. The
     * widget is added to the map.
     *
     * @param wc the widget class to handle
     * @param widgetClasses the list of all available widget classes
     */
    private void handleWidgetClass(WidgetClass wc, List<WidgetClass> widgetClasses) {
        // if the class has already been handled, skip it
        Map<String, WidgetClass> map = this.widgetTypeClasses
            .get(wc.widgetType == null ? DEFAULT_TYPE_CLASS : wc.widgetType.toLowerCase());
        if (map != null && map.containsKey(wc.widgetClass == null ? DEFAULT_TYPE_CLASS : wc.widgetClass)) {
            return;
        }

        // otherwise check its parent and recursively gather all parent's and parent's parent's and ... properties
        String parentClass = wc.parentWidgetClass;
        String parentType = wc.parentWidgetType;
        if (parentClass == null && parentType == null) {
            // there is no parent class for this one
            addDefaultProperties(wc, widgetClasses);
            return;
        } else {
            for (WidgetClass parent : widgetClasses) {
                if (parent == wc) {
                    continue;
                }
                if (Objects.equals(parentClass, parent.widgetClass) && Objects.equals(parentType, parent.widgetType)) {
                    // if parent was found, make sure that parent has the properties of its own parent
                    handleWidgetClass(parent, widgetClasses);
                    addDefaultProperties(wc, widgetClasses);
                    wc.merge(parent);
                    return;
                }
            }
        }
    }

    /**
     * Add the default properties for the given widget class. The default properties are the ones that are defined for
     * the same widget type as the widget type of the given class. If the class has no widget type, there is nothing to
     * merge. The widget class is added to the map when this method completes.
     *
     * @param wc
     * @param widgetClasses
     */
    private void addDefaultProperties(WidgetClass wc, List<WidgetClass> widgetClasses) {
        String wtype = wc.widgetType == null ? DEFAULT_TYPE_CLASS : wc.widgetType;
        if (!DEFAULT_TYPE_CLASS.equals(wtype)) {
            // if it has a widget type, find the widget type and gather its properties
            for (WidgetClass defaultParent : widgetClasses) {
                if (wc == defaultParent)
                    continue;
                if (defaultParent.isDefaultWidgetTypeFor(wtype)) {
                    // gather default properties and add them to this widget class
                    wc.setDefaultProperties(defaultParent.properties);
                    break;
                }
            }
        }
        // find the map of all widget classes for this widget type and add a new widget class
        Map<String, WidgetClass> map = this.widgetTypeClasses.get(wtype.toLowerCase());
        if (map == null) {
            map = new HashMap<>();
            this.widgetTypeClasses.put(wtype.toLowerCase(), map);
        }
        // if widget class is null this is the default widget type
        map.put(wc.widgetClass == null ? DEFAULT_TYPE_CLASS : wc.widgetClass, wc);
    }

    /**
     * Parse the combined selector and creates an appropriate widget class. The class contains the widget type and class
     * as well as the type and class of its parent.
     *
     * @param cs the combined selector to parse
     * @return the widget class matching the selector
     */
    private static Optional<WidgetClass> toWidgetClass(CombinedSelector cs) {
        if (cs.size() == 1) {
            // single class or type definition
            Selector selector = cs.get(0);
            String widgetType = selector.getElementName();
            String widgetClass = selector.getClassName();
            if (widgetType != null || widgetClass != null) {
                return Optional.of(WidgetClass.of(widgetType, widgetClass));
            }
        } else if (cs.size() == 2) {
            // extension definition
            Selector parent = cs.get(0);
            String parentWidgetType = parent.getElementName();
            String parentWidgetClass = parent.getClassName();
            Selector child = cs.get(1);
            if (child.getCombinator() == Combinator.CHILD) {
                String widgetType = child.getElementName();
                String widgetClass = child.getClassName();
                if (widgetType != null || widgetClass != null) {
                    return Optional.of(WidgetClass.of(widgetType, widgetClass, parentWidgetType, parentWidgetClass));
                }
            } else {
                LOGGER.log(Level.WARNING, "Child class definition expected but not found: {0}", cs);
                return Optional.empty();
            }
        }
        LOGGER.log(Level.WARNING, "Unknown rule definition: {0}", cs);
        return Optional.empty();

    }

    /**
     * Parse the rule, which defines a particular constant. The rule is defined for the widget type *.
     *
     * @param rs the rule to parse
     */
    private void parseConstant(RuleSet rs) {
        for (Declaration property : rs) {
            String name = property.getProperty();
            if (property.size() == 1) {
                Object value = property.get(0).getValue();
                if (value instanceof java.awt.Color) {
                    java.awt.Color c = (java.awt.Color) value;
                    RGB rgb = new RGB(c.getRed(), c.getGreen(), c.getBlue());
                    colors.put(name, new OPIColor(name, rgb, false));
                } else {
                    LOGGER.log(Level.WARNING, "Unknown property value of the constant property '{0}' near line '{1}'.",
                        new Object[] { name, property.getSource().getLine() });
                }
            } else {
                LOGGER.log(Level.WARNING,
                    "Constant property '{0}' near line '{1}' should have exactly one value but has '{2}'.",
                    new Object[] { name, property.getSource().getLine(), property.size() });
            }
        }
    }

    /**
     * Parse the rule and returns the map of property name and value pairs defined by the rule.
     *
     * @param set the rule to parse
     * @return map of properties in the given rule
     */
    private Map<String, Object> parseRule(RuleSet set) {
        Map<String, Object> propertyValues = new HashMap<>();
        for (Declaration property : set) {
            String propertyName = property.getProperty();
            if (property.isEmpty()) {
                LOGGER.log(Level.WARNING, "Property {0} near line {1} should have at least one value.",
                    new Object[] { propertyName, property.getSource().getLine() });
            } else if (property.size() == 1) {
                Object value = objectToPropertyValue(property.get(0).getValue());
                propertyValues.put(propertyName, value);
            } else {
                List<Object> values = property.stream().map(value -> objectToPropertyValue(value.getValue()))
                    .collect(Collectors.toList());
                propertyValues.put(propertyName, values);

            }
        }
        return propertyValues;
    }

    /**
     * Transforms the object to property value by resolving the fonts and colours constants as well as parsing boolean
     * values.
     *
     * @param object the object to transform
     * @return parsed value (if known at this time)
     */
    private Object objectToPropertyValue(Object object) {
        if (object instanceof java.awt.Color) {
            java.awt.Color c = (java.awt.Color) object;
            RGB rgb = new RGB(c.getRed(), c.getGreen(), c.getBlue());
            return new OPIColor(rgb);
        } else if (object instanceof String) {
            // check if the property is a name of an existing font or colour
            Object val = fonts.get(object);
            if (val == null)
                val = colors.get(object);
            if (val != null)
                return val;
            // if not font or colour, perhaps a Boolean
            String v = (String) object;
            if (TRUE.equalsIgnoreCase(v) || FALSE.equalsIgnoreCase(v)) {
                return Boolean.parseBoolean(v);
            }
            return v;
        } else {
            return object;
        }
    }

    /**
     * Parse the font-face rule and add the settings to the fonts map.
     *
     * @param rff the rule to parse
     */
    private void parseFontFace(RuleFontFace rff) {
        FontData[] fd = JFaceResources.getDefaultFont().getFontData();
        String name = null;
        int fontStyle = SWT.NORMAL;
        float fontSize = fd[0].getHeight();
        String fontType = fd[0].getName();
        String property;
        int line = -1;
        boolean styleWidgetDefined = false;
        for (Declaration d : rff) {
            property = d.getProperty();
            Term<?> value = d.get(0);
            if (FONT_FAMILY.equalsIgnoreCase(property)) {
                name = (String) value.getValue();
            } else if (FONT_WEIGHT.equalsIgnoreCase(property)) {
                styleWidgetDefined = true;
                String weight = (String) value.getValue();
                if (FONT_BOLD.equalsIgnoreCase(weight)) {
                    fontStyle |= SWT.BOLD;
                }
            } else if (FONT_STYLE.equalsIgnoreCase(property)) {
                styleWidgetDefined = true;
                String style = (String) value.getValue();
                if (FONT_ITALIC.equalsIgnoreCase(style)) {
                    fontStyle |= SWT.ITALIC;
                }
            } else if (FONT_SIZE.equalsIgnoreCase(property)) {
                fontSize = (Float) value.getValue();
            } else if (FONT_SRC.equalsIgnoreCase(property)) {
                fontType = (String) value.getValue();
            }
            line = d.getSource().getLine();
        }
        if (name == null) {
            LOGGER.log(Level.WARNING, "font-family property not defined for a @font-face rule near line {0}", line);
        } else {
            FontData nfd = new FontData(fontType, (int) fontSize, styleWidgetDefined ? fontStyle : fd[0].getStyle());
            fonts.put(name, new OPIFont(name, nfd));
        }
    }

    /**
     * Transforms the widget type ID to a widget type expected to be found in the style sheet. This is the last segment
     * of the given type (after the last dot)
     *
     * @param widgetTypeID the widget type ID
     * @return the widget type id as expected to be defined in the css file
     */
    private static String getId(String widgetTypeID) {
        String wid = widgetTypeID;
        if (wid.charAt(wid.length() - 1) == '.') {
            wid = widgetTypeID.substring(0, wid.length() - 1);
        }
        int idx = widgetTypeID.lastIndexOf('.');
        if (idx >= 0 && idx < widgetTypeID.length() - 1) {
            wid = wid.substring(idx + 1);
        }
        return wid.toLowerCase();
    }
}
