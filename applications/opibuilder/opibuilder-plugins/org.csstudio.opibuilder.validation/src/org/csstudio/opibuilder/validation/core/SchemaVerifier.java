/*******************************************************************************
 * Copyright (c) 2010-2016 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.validation.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
import org.csstudio.opibuilder.editparts.ExecutionMode;
import org.csstudio.opibuilder.editparts.WidgetEditPartFactory;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractLinkingContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.ConnectionModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.LineAwareXMLParser;
import org.csstudio.opibuilder.persistence.LineAwareXMLParser.LineAwareElement;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.properties.ActionsProperty;
import org.csstudio.opibuilder.properties.RulesProperty;
import org.csstudio.opibuilder.properties.ScriptProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.script.RuleData;
import org.csstudio.opibuilder.script.RulesInput;
import org.csstudio.opibuilder.script.ScriptData;
import org.csstudio.opibuilder.script.ScriptService.ScriptType;
import org.csstudio.opibuilder.script.ScriptsInput;
import org.csstudio.opibuilder.scriptUtil.FileUtil;
import org.csstudio.opibuilder.util.MediaService;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.validation.Activator;
import org.csstudio.opibuilder.widgetActions.AbstractWidgetAction;
import org.csstudio.opibuilder.widgetActions.ActionsInput;
import org.csstudio.opibuilder.widgets.editparts.ArrayEditPart;
import org.csstudio.opibuilder.widgets.model.ArrayModel;
import org.csstudio.opibuilder.widgets.model.LinkingContainerModel;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.widgets.Display;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;

/**
 *
 * <code>SchemaVerifier</code> verifies if the given OPI or the OPIs located below the given path conform(s) to
 * the the schema defined by the opibuilder. The instructions how to verify individual properties are defined
 * in the rules definitions file.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SchemaVerifier {

    private static final Logger LOGGER = Logger.getLogger(SchemaVerifier.class.getName());

    /**
     *
     * <code>NonNullArrayList</code> is an extension of the array list, which does
     * not accept a null value when passed via the {@link #add(Object)} method.
     *
     * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
     *
     * @param <T>
     */
    private static class NonNullArrayList<T> extends ArrayList<T> {
        private static final long serialVersionUID = 5274768886178889837L;
        @Override
        public boolean add(T e) {
            if (e == null) return false;
            return super.add(e);
        }
    }

    private List<ValidationFailure> validationFailures = new ArrayList<>();
    private IPath validatedPath;
    private Map<String, AbstractWidgetModel> schema;
    private Map<AbstractWidgetModel,List<String>> deprecatedProperties;
    private OPIColor[] colors;
    private OPIFont[] fonts;

    private int numberOfAnalyzedWidgets = 0;
    private int numberOfWidgetsFailures = 0;
    private int numberOfWidgetsWithRules = 0;
    private int numberOfAllRules = 0;
    private int numberOfWidgetsWithScripts = 0;
    private int numberOfWidgetsWithPythonEmbedded = 0;
    private int numberOfWidgetsWithJavascriptEmbedded = 0;
    private int numberOfWidgetsWithPythonStandalone = 0;
    private int numberOfWidgetsWithJavascriptStandalone = 0;
    private int numberOfAnalyzedFiles = 0;
    private int numberOfFilesFailures = 0;
    private int numberOfROProperties = 0;
    private int numberOfRWProperties = 0;
    private int numberOfRWFailures = 0;
    private int numberOfWRITEProperties = 0;
    private int numberOfCriticalROFailures = 0;
    private int numberOfMajorROFailures = 0;
    private int numberOfWRITEFailures = 0;
    private int numberOfDeprecatedFailures = 0;

    private IPath schemaPath;
    private WidgetEditPartFactory editPartFactory;
    private Method registerPropertyChangeHandlersMethod;
    private Method registerBasePropertyChangeHandlersMethod;
    private Method doCreateFigureMethod;
    private Method hookChildMethod;

    private final Map<String, ValidationRule> rules;
    private final Map<Pattern, ValidationRule> patternRules;
    private final Map<String, String[]> additionalAcceptableValues;
    private final Map<Pattern, String[]> patternsAdditionalAcceptableValues;
    private final Map<String, String[]> removedValues;
    private final Map<Pattern, String[]> patternsRemovedValues;

    private boolean warnAboutJythonScripts = Activator.getInstance().isWarnAboutJythonScripts();

    /**
     * Constructs a new schema verifier using the schema path defined in the preferences.
     *
     * @param rules the validation rules to use (keys are the property names, values are the rules for that property)
     * @param patternRules the rules defined by patterns. If a property matches the pattern (and there is no specific
     *          rule for that property in the rules) it will obey the rule of the matched pattern
     * @param additionalAcceptableValues some properties have additional values (besides the one in the OPI Schema),
     *          which are acceptable (e.g. background_color has acceptable values 'IO Background' and
     *          'IO Area Background'
     * @param acceptableValuesPatterns the acceptable values defined by patterns. If a property matches the pattern
     *          (and there is no specific acceptable value for that pattern) the values stored under that pattern
     *          will be used
     * @param removedValues the values of complex properties which should be removed if they exist on the widgets
     * @param removedValuesPatterns the values of complex properties (given by patterns) to be removed if they exist
     */
    public SchemaVerifier(Map<String,ValidationRule> rules, Map<Pattern,ValidationRule> patternRules,
            Map<String,String[]> additionalAcceptableValues, Map<Pattern,String[]> acceptableValuesPatterns,
            Map<String,String[]> removedValues, Map<Pattern,String[]> removedValuesPatterns) {
        this(PreferencesHelper.getSchemaOPIPath(), rules, patternRules, additionalAcceptableValues,
                acceptableValuesPatterns,removedValues,removedValuesPatterns);
    }

    /**
     * Construct a new SchemaVerifier.
     *
     * @param pathToSchema the path to the OPI schema against which the OPIs will be validated
     * @param rules the validation rules to use (keys are the property names, values are the rules for that property)
     * @param patternRules the rules given as pattern. If a property matches the pattern (and there is no specific
     *          rule for that property in the rules) it will obey the rule of the matched pattern
     * @param additionalAcceptableValues some properties have additional values (besides the one in the OPI Schema),
     *          which are acceptable (e.g. background_color has acceptable values 'IO Background' and
     *          'IO Area Background'
     * @param acceptableValuesPatterns the acceptable values defined by patterns. If a property matches the pattern
     *          (and there is no specific acceptable value for that pattern) the values stored under that pattern
     *          will be used
     * @param removedValues the values of complex properties which should be removed if they exist on the widgets
     * @param removedValuesPatterns the values of complex properties (given by patterns) to be removed if they exist
     */
    public SchemaVerifier(IPath pathToSchema, Map<String,ValidationRule> rules,
            Map<Pattern,ValidationRule> patternRules, Map<String,String[]> additionalAcceptableValues,
            Map<Pattern,String[]> acceptableValuesPatterns, Map<String,String[]> removedValues,
            Map<Pattern,String[]> removedValuesPatterns) {
        if (pathToSchema == null) {
            throw new IllegalArgumentException("There is no OPI schema defined.");
        }
        this.schemaPath = pathToSchema;
        this.rules = new HashMap<>(rules);
        this.patternRules = new HashMap<>(patternRules);
        this.additionalAcceptableValues = new HashMap<>(additionalAcceptableValues);
        this.patternsAdditionalAcceptableValues = new HashMap<>(acceptableValuesPatterns);
        this.removedValues = new HashMap<>(removedValues);
        this.patternsRemovedValues = new HashMap<>(removedValuesPatterns);
    }

    /**
     * Returns the list of all validation failures since the last cleanup
     *
     * @return the list of validation failures
     */
    public ValidationFailure[] getValidationFailures() {
        return validationFailures.toArray(new ValidationFailure[validationFailures.size()]);
    }

    /**
     * Removes all validation failures and resets the counters.
     */
    public void clean() {
        validationFailures.clear();
        numberOfAnalyzedWidgets = 0;
        numberOfAnalyzedFiles = 0;
        numberOfROProperties = 0;
        numberOfWRITEProperties = 0;
        numberOfCriticalROFailures = 0;
        numberOfMajorROFailures = 0;
        numberOfWRITEFailures = 0;
        numberOfFilesFailures = 0;
        numberOfWidgetsFailures = 0;
        numberOfDeprecatedFailures = 0;
    }

    /**
     * @return the number of all rules
     */
    public int getNumberOfAllRules() {
        return numberOfAllRules;
    }

    /**
     * @return the number of all widgets that have embedded javascripts attached
     */
    public int getNumberOfWidgetsWithJavascriptEmbedded() {
        return numberOfWidgetsWithJavascriptEmbedded;
    }

    /**
     * @return the number of all widgets that have standalone javascripts attached
     */
    public int getNumberOfWidgetsWithJavascriptStandalone() {
        return numberOfWidgetsWithJavascriptStandalone;
    }

    /**
     * @return the number of all widgets that have embedded python scripts attached
     */
    public int getNumberOfWidgetsWithPythonEmbedded() {
        return numberOfWidgetsWithPythonEmbedded;
    }

    /**
     * @return the number of all widgets that have standalone python scripts attached
     */
    public int getNumberOfWidgetsWithPythonStandalone() {
        return numberOfWidgetsWithPythonStandalone;
    }

    /**
     * @return the number of all widgets that have rules attached
     */
    public int getNumberOfWidgetsWithRules() {
        return numberOfWidgetsWithRules;
    }

    /**
     * @return the number of all widgets that have scripts attached
     */
    public int getNumberOfWidgetsWithScripts() {
        return numberOfWidgetsWithScripts;
    }

    /**
     * @return number of deprecated properties used
     */
    public int getNumberOfDeprecatedFailures() {
        return numberOfDeprecatedFailures;
    }

    /**
     * @return number of analysed files since the last cleanup
     */
    public int getNumberOfAnalyzedFiles() {
        return numberOfAnalyzedFiles;
    }

    /**
     * @return number of analysed widgets since the last cleanup
     */
    public int getNumberOfAnalyzedWidgets() {
        return numberOfAnalyzedWidgets;
    }

    /**
     * @return number of obligatory write failures since the last cleanup
     */
    public int getNumberOfWRITEFailures() {
        return numberOfWRITEFailures;
    }

    /**
     * @return number of analysed obligatory write properties since the last cleanup
     */
    public int getNumberOfWRITEProperties() {
        return numberOfWRITEProperties;
    }

    /**
     * @return number of critical read only failures since the last cleanup
     */
    public int getNumberOfCriticalROFailures() {
        return numberOfCriticalROFailures;
    }

    /**
     * @return number of major read only failures since the last cleanup
     */
    public int getNumberOfMajorROFailures() {
        return numberOfMajorROFailures;
    }

    /**
     * @return number of analysed read only properties since the last cleanup
     */
    public int getNumberOfROProperties() {
        return numberOfROProperties;
    }

    /**
     * @return number of files with failures since the last cleanup
     */
    public int getNumberOfFilesFailures() {
        return numberOfFilesFailures;
    }

    /**
     * @return number of widgets with failures since the last cleanup
     */
    public int getNumberOfWidgetsFailures() {
        return numberOfWidgetsFailures;
    }

    /**
     * @return the number of failed RW checks
     */
    public int getNumberOfRWFailures() {
        return numberOfRWFailures;
    }

    /**
     * @return the number of checked RW properties
     */
    public int getNumberOfRWProperties() {
        return numberOfRWProperties;
    }

    /**
     * Validates the OPIs on the given path and returns the list of all validation failures. If the path is
     * a directory all OPIs below that path are validated.
     *
     * @param validatedPath the path the OPI to validate
     * @return the list of all validation failures
     * @throws IOException if there was an error opening the OPI file or schema
     * @throws IllegalStateException if the schema is not defined
     */
    public ValidationFailure[] validate(IPath validatedPath) throws IOException, IllegalStateException {
        if (validatedPath == null) {
            throw new IOException("Cannot access null path.");
        }
        if (schema == null) {
            schema = Utilities.loadSchema(schemaPath);
            deprecatedProperties = new HashMap<>();
            for (AbstractWidgetModel model : schema.values()) {
                List<String> deprecated = deprecatedProperties.get(model);
                if (deprecated == null) {
                    try {
                        deprecated = getDeprecatedProperties(model.getClass());
                        if (!deprecated.isEmpty()) {
                            deprecatedProperties.put(model, deprecated);
                        }
                    } catch (Exception e) {
                       //ignore
                    }
                }
            }
            colors = MediaService.getInstance().getAllPredefinedColors();
            fonts = MediaService.getInstance().getAllPredefinedFonts();
            editPartFactory = new WidgetEditPartFactory(ExecutionMode.EDIT_MODE);
            try {
                registerPropertyChangeHandlersMethod = AbstractBaseEditPart.class
                        .getDeclaredMethod("registerPropertyChangeHandlers");
                registerPropertyChangeHandlersMethod.setAccessible(true);
                registerBasePropertyChangeHandlersMethod = AbstractBaseEditPart.class
                        .getDeclaredMethod("registerBasePropertyChangeHandlers");
                registerBasePropertyChangeHandlersMethod.setAccessible(true);
                doCreateFigureMethod = AbstractGraphicalEditPart.class
                        .getDeclaredMethod("createFigure");
                doCreateFigureMethod.setAccessible(true);
                hookChildMethod = ArrayEditPart.class.getDeclaredMethod("hookChild",
                        EditPart.class, int.class, boolean.class);
                hookChildMethod.setAccessible(true);
            } catch (NoSuchMethodException | SecurityException e) {
                LOGGER.log(Level.WARNING, "Cannot register property change handlers.", e);
            }
        }
        this.validatedPath = validatedPath;
        File file = null;
        try {
            IFile ifile = ResourcesPlugin.getWorkspace().getRoot().getFile(this.validatedPath);
            file = ifile.getLocation().toFile();
        } catch (Exception e) {
            //maybe it is already a file
            file = this.validatedPath.toFile();
        }
        List<ValidationFailure> failures = new ArrayList<>();
        if (file.isFile()) {
            failures.addAll(check(this.validatedPath));
        } else  {
            throw new IllegalArgumentException(validatedPath.toString() + " is a directory.");
        }
        validationFailures.addAll(failures);
        return failures.toArray(new ValidationFailure[failures.size()]);
    }

    /**
     * Checks if the given OPI matches the schema definition. All detected validation failures are stored into
     * {@link #validationFails} list.
     *
     * @param opi the path to the OPI file
     * @throws IOException if there was an error reading the OPI file
     */
    private List<ValidationFailure> check(IPath opi) throws IOException{
        DisplayModel displayModel = null;
        try (InputStream inputStream = ResourceUtil.pathToInputStream(opi, false)) {
            displayModel = new DisplayModel(opi);
            XMLUtil.fillDisplayModelFromInputStream(inputStream, displayModel, Display.getDefault());
        } catch (Exception e) {
            throw new IOException("Could not read the opi " + opi.toOSString() +".",e);
        }

        numberOfAnalyzedFiles++;
        List<ValidationFailure> failures = new NonNullArrayList<>();
        checkWidget(opi, displayModel, failures);
        findCoordinates(failures, opi);
        if (!failures.isEmpty()) {
            numberOfFilesFailures++;
        }
        return failures;
    }

    /**
     * Scans the OPI given by path to find the occurrences of the failures. If they are found it stores the line
     * number into the failure object. All failures are expected to belong to the same OPI.
     *
     * @param failures the list of failures, which are to be found in the file (should belong to the same path)
     * @param path the path to scan
     * @return a new array of validation failures, which might include additional failures if some deprecated
     *          properties have been used
     * @throws IOException if there was an error opening the file
     */
    private void findCoordinates(List<ValidationFailure> failures, IPath path) throws IOException {
        try (InputStream stream = ResourceUtil.pathToInputStream(path, false)) {

            SAXBuilder saxBuilder = LineAwareXMLParser.createBuilder();
            Document document = saxBuilder.build(stream);

            Iterator<?> widgetNodes = document.getDescendants(new ElementFilter(XMLUtil.XMLTAG_WIDGET));
            Iterator<?> displayNodes = document.getDescendants(new ElementFilter(XMLUtil.XMLTAG_DISPLAY));
            Iterator<?> connectionNodes = document.getDescendants(new ElementFilter(XMLUtil.XMLTAG_CONNECTION));

            //gather all widgets, displays, and connectors in the same array
            List<LineAwareElement> list = new ArrayList<>();
            while(widgetNodes.hasNext()) {
                list.add((LineAwareElement)widgetNodes.next());
            }
            while(displayNodes.hasNext()) {
                list.add((LineAwareElement)displayNodes.next());
            }
            while(connectionNodes.hasNext()) {
                list.add((LineAwareElement)connectionNodes.next());
            }
            LineAwareElement[] widgets = list.toArray(new LineAwareElement[list.size()]);
            Arrays.sort(widgets);

            Collections.sort(failures);
            Map<Integer,LineAwareElement> widgetElements = new HashMap<>();
            int i = 0;
            String name;
            int line;
            LineAwareElement widget;
            for (int m = 0; m < failures.size(); m++) {
                ValidationFailure failure = failures.get(m);
                name = failure.getWidgetName();
                line = failure.getLineNumber();
                //for every failure find the widget that match the line number
                widget = widgetElements.get(line);
                if (widget == null) {
                    //widgets are sorted in the same way as failures
                    for (; i < widgets.length; i++) {
                        if (widgets[i].getLineNumber() == line) {
                            widgetElements.put(failure.getLineNumber(), widgets[i]);

                            setPropertyLineNumber(widgets[i],name,failures,m);
                            while(m < failures.size()-1
                                    && failures.get(m+1).getLineNumber() == failures.get(m).getLineNumber()) {
                                setPropertyLineNumber(widgets[i],name,failures,m+1);
                                m++;
                            }
                            break;
                        }
                    }
                } else {
                    setPropertyLineNumber(widget, name, failures, m);
                }
            }
            for (Entry<AbstractWidgetModel,List<String>> e : deprecatedProperties.entrySet()) {
                AbstractWidgetModel model = e.getKey();
                for (LineAwareElement w : widgets) {
                    Element n = w.getChild(AbstractWidgetModel.PROP_WIDGET_TYPE);
                    boolean correct = false;
                    if (n == null) {
                        Attribute attr = w.getAttribute(XMLUtil.XMLATTR_TYPEID);
                        String fullt = attr.getValue();
                        String t = fullt.substring(fullt.lastIndexOf('.'));
                        correct = model.getWidgetType().equals(t) || model.getWidgetType().equals(fullt);
                    } else {
                        correct = model.getWidgetType().equals(n.getValue());
                    }
                    if (correct) {
                        for (String d : e.getValue()) {
                            LineAwareElement node = (LineAwareElement)w.getChild(d);
                            if (node != null) {
                                String type = n == null ? "" : n.getValue();
                                String widgetName = "";
                                String wuid = null;
                                n = w.getChild(AbstractWidgetModel.PROP_NAME);
                                if (n != null) {
                                    widgetName = n.getValue();
                                }
                                n = w.getChild(AbstractWidgetModel.PROP_WIDGET_UID);
                                if (n != null) {
                                    wuid = n.getValue();
                                }
                                ValidationFailure f = new ValidationFailure(path, wuid, type, widgetName, d, null,
                                        node.getValue(), ValidationRule.DEPRECATED, false, true, null,
                                        node.getLineNumber(), false, model.getClass());
                                numberOfDeprecatedFailures++;
                                failures.add(f);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new IOException("Unable to load opi '" + path + "'.",e);
        }
    }

    private void setPropertyLineNumber(LineAwareElement widget, String name, List<ValidationFailure> failures, int m)
            throws Exception {
        //find the node describing the property, but only if the name of the widget matches the one in the failure
        LineAwareElement node = findPropertyElement(widget,name,failures.get(m).getProperty());
        if (node == null) {
            //if no such property is found, it is not defined in the XML, so mark the widget itself
            node = widget;
        }

        int line = node.getLineNumber();
        ValidationFailure failure = failures.get(m);
        failure.setLineNumber(line);
        //check if there are subvalidation failures and find those as well
        if (failure.hasSubFailures()) {
            SubValidationFailure[] subs = failure.getSubFailures();
            for (SubValidationFailure s : subs) {
                if (s.getActualValue() != null) {
                    LineAwareElement n = findSubNode(node, s.getSubPropertyTag(), s.getActualValue(),s.getModelClass());
                    if (n != null) {
                        s.setLineNumber(n.getLineNumber());
                    }
                } else {
                    s.setLineNumber(line);
                }
            }
        }
    }

    private static LineAwareElement findSubNode(LineAwareElement parent, String tagName, Object actualValue,
            Class<? extends AbstractWidgetModel> modelClass) throws Exception {
        if (parent.getName().equals(tagName)) return parent;
        List<?> children = parent.getChildren();
        try {
            if (actualValue instanceof RuleData) {
                RulesProperty prop = new RulesProperty(AbstractWidgetModel.PROP_RULES, "Rules",
                        WidgetPropertyCategory.Behavior);
                //TODO
                prop.setWidgetModel(modelClass.newInstance());
                List<RuleData> rules = prop.readValueFromXML(parent).getRuleDataList();
                for (int i = 0; i < rules.size(); i++) {
                    if (Utilities.areRulesIdentical(rules.get(i), (RuleData)actualValue) == 0) {
                        return (LineAwareElement)children.get(i);
                    }
                }
            } else if (actualValue instanceof ScriptData) {
                ScriptProperty prop = new ScriptProperty(AbstractWidgetModel.PROP_SCRIPTS, "Scripts",
                        WidgetPropertyCategory.Behavior);
                List<ScriptData> scripts = prop.readValueFromXML(parent).getScriptList();
                for (int i = 0; i < scripts.size(); i++) {
                    if (Utilities.areScriptsIdentical(scripts.get(i), (ScriptData)actualValue) == 0) {
                        return (LineAwareElement)children.get(i);
                    }
                }
            } else if (actualValue instanceof AbstractWidgetAction) {
                ActionsProperty prop = new ActionsProperty(AbstractWidgetModel.PROP_ACTIONS, "Actions",
                        WidgetPropertyCategory.Behavior);
                List<AbstractWidgetAction> actions = prop.readValueFromXML(parent).getActionsList();
                for (int i = 0; i < actions.size(); i++) {
                    if (Utilities.areActionsIdentical(actions.get(i), (AbstractWidgetAction)actualValue) == 0) {
                        return (LineAwareElement)children.get(i);
                    }
                }
            }
        } catch (Exception e) {
            //just in case if something gets updated in the opibuilder
            LOGGER.log(Level.SEVERE, "Cannot find the node for " + tagName, e);
        }
        return null;
    }

    private static LineAwareElement findPropertyElement(Element node, String name, String property) {
        Element n = node.getChild(AbstractWidgetModel.PROP_NAME);
        if (n != null && n.getValue().equals(name)) {
            return (LineAwareElement)node.getChild(property);
        }
        return null;
    }

    /**
     * Checks the container model and its children if they match the definitions in the schema.
     * All detected validation failures are stored into the failures list.
     *
     * @param pathToFile the path to the file that owns the model
     * @param containerModel the container model to check against the schema
     * @param failures the list into which all validation failures are stored
     * @throws IOException
     */
    private void check(IPath pathToFile, AbstractContainerModel containerModel, List<ValidationFailure> failures) throws IOException {
        for (AbstractWidgetModel model : containerModel.getChildren()) {
            checkWidget(pathToFile, model, failures);
        }
        if (containerModel instanceof DisplayModel) {
            for (ConnectionModel model : ((DisplayModel) containerModel).getConnectionList()) {
                checkWidget(pathToFile, model, failures);
            }
        }
    }

    private static List<String> getDeprecatedProperties(Class<? extends AbstractWidgetModel> model)
            throws IllegalArgumentException, IllegalAccessException {
        List<String> deprecated = new ArrayList<>();
        Field[] fields = model.getFields();
        for (Field f :fields) {
            int mod = f.getModifiers();
            if (Modifier.isFinal(mod) && Modifier.isStatic(mod)) {
                if (f.getAnnotation(Deprecated.class) != null) {
                    deprecated.add(String.valueOf(f.get(model)));
                }
            }
        }
        return deprecated;
    }

    private void checkWidget(IPath pathToFile, AbstractWidgetModel model, List<ValidationFailure> failures) throws IOException {
        numberOfAnalyzedWidgets++;
        ValidationRule rule;
        Object orgVal, modelVal;
        String widgetType = model.getTypeID();
        AbstractWidgetModel original = schema.get(widgetType);
        int lineNumber = model.getLineNumber();
        int startingFailures = failures.size();
        initModel(model);
        if (original != null) {
            Set<String> properties = model.getAllPropertyIDs();
            for (String p : properties) {
                rule = getRuleForProperty(p, widgetType);
                modelVal = model.getPropertyValue(p);
                orgVal = original.getPropertyValue(p);
                //if the checked property is not saveable (e.g. background colour for action button), ignore it
                if (!model.getProperty(p).isVisibleInPropSheet()) {
                    continue;
                }
                if (rule == ValidationRule.RW) {
                    numberOfRWProperties++;
                    //nothing to do in the case of read/write properties except removal, but that is handled below
                }
                //actions, rules and scripts are a bit nasty
                if (AbstractWidgetModel.PROP_ACTIONS.equals(p)) {
                    ActionsInput mi = ((ActionsInput)modelVal);
                    ActionsInput oi = ((ActionsInput)orgVal);
                    failures.add(checkAction(pathToFile,mi,oi,model,rule));
                } else if (AbstractWidgetModel.PROP_RULES.equals(p)) {
                    List<RuleData> modelRules = ((RulesInput)modelVal).getRuleDataList();
                    List<RuleData> originalRules = ((RulesInput)orgVal).getRuleDataList();
                    ValidationFailure vf = handleActionsScriptsRules(pathToFile, model.getWUID(), widgetType,
                            model.getName(), model.getClass(), p, modelRules, originalRules, modelVal, orgVal, rule,
                            lineNumber, (orgRule,modelRule) -> Utilities.areRulesIdentical(orgRule,modelRule),
                            (theRule) -> RulesProperty.XML_ELEMENT_RULE,
                            (therule) -> therule.getName(),
                            (match) -> Utilities.ruleMatchValueToMessage(match),
                            (theRule) -> theRule.getName());
                    vf = checkWhatRulesDo(pathToFile, lineNumber, vf, (RulesInput)modelVal);
                    if (!modelRules.isEmpty()) {
                        numberOfWidgetsWithRules++;
                    }
                    numberOfAllRules += modelRules.size();
                    failures.add(vf);
                } else if (AbstractWidgetModel.PROP_SCRIPTS.equals(p)) {
                    List<ScriptData> modelScripts = ((ScriptsInput)modelVal).getScriptList();
                    List<ScriptData> originalScripts = ((ScriptsInput)orgVal).getScriptList();
                    ValidationFailure vf = handleActionsScriptsRules(pathToFile, model.getWUID(), widgetType,
                            model.getName(), model.getClass(), p, modelScripts, originalScripts, modelVal, orgVal,
                            rule, lineNumber,
                            (orgScript,modelScript) -> Utilities.areScriptsIdentical(orgScript, modelScript),
                            (script) -> ScriptProperty.XML_ELEMENT_PATH,
                            (script) -> script.isEmbedded() ? script.getScriptName() : script.getPath().toString(),
                            (match) -> Utilities.scriptMatchValueToMessage(match),
                            (script) -> script.isEmbedded() ? script.getScriptName() : script.getPath().toString());
                    vf = checkWhatScriptsDo(pathToFile, lineNumber, vf, modelScripts, model);
                    if (!modelScripts.isEmpty()) {
                        numberOfWidgetsWithScripts++;
                    }
                    //check which scripts are used and increase the counters accordingly
                    boolean[] vals = new boolean[4];
                    List<ScriptData> jythonScripts = new ArrayList<>();
                    for (ScriptData sd : modelScripts) {
                        if (sd.getScriptType() == ScriptType.JAVASCRIPT) {
                            vals[sd.isEmbedded() ? 0 : 1] = true;
                        } else if (sd.getScriptType() == ScriptType.PYTHON) {
                            vals[sd.isEmbedded() ? 2 : 3] = true;
                            jythonScripts.add(sd);
                        } else if (sd.getScriptType() == null) {
                            String path = sd.getPath().toString().toLowerCase();
                            if (path.endsWith(".js")) {
                                vals[sd.isEmbedded() ? 0 : 1] = true;
                            } else if (path.endsWith(".py")) {
                                vals[sd.isEmbedded() ? 2 : 3] = true;
                            }
                        }
                    }
                    if (vals[0]) numberOfWidgetsWithJavascriptEmbedded++;
                    if (vals[1]) numberOfWidgetsWithJavascriptStandalone++;
                    if (vals[2]) numberOfWidgetsWithPythonEmbedded++;
                    if (vals[3]) numberOfWidgetsWithPythonStandalone++;
                    if (warnAboutJythonScripts) {
                        //put jython scripts validation failure to the problems view
                        if (vals[2] || vals [3]) {
                            if (vf == null) {
                                vf = new ValidationFailure(pathToFile, model.getWUID(), widgetType, model.getName(),
                                        p, orgVal, modelVal, ValidationRule.WRITE, false, false, "Jython script used.",
                                        lineNumber, false, model.getClass());
                            }
                            final ValidationFailure vff = vf;
                            jythonScripts.forEach(e ->
                                vff.addSubFailure(new SubValidationFailure(pathToFile, model.getWUID(), widgetType,
                                    model.getName(), p, ScriptProperty.XML_ELEMENT_PATH, "Jython script used.",
                                    e, null, ValidationRule.WRITE, false, false, e.getScriptName(),
                                    lineNumber, model.getClass())));
                        }
                    }
                    failures.add(vf);
                } else {
                    if (rule == ValidationRule.RO) {
                        //read-only properties must have identical values, otherwise it is a failure
                        numberOfROProperties++;
                        if (!isPropertyAccepted(widgetType,p,orgVal,modelVal)) {
                            //the failure is always critical, except for fonts and colors if a predefined value was used
                            boolean critical = !isPropertyDefined(modelVal);
                            failures.add(new ValidationFailure(pathToFile, model.getWUID(), widgetType,
                                model.getName(), p, orgVal, modelVal, rule, critical,true, null, lineNumber,false,
                                model.getClass()));
                            if (critical) {
                                numberOfCriticalROFailures++;
                            } else {
                                numberOfMajorROFailures++;
                            }
                        } else if (!isFontColorPropertyDefined(modelVal)) {
                            numberOfMajorROFailures++;
                            failures.add(new ValidationFailure(pathToFile, model.getWUID(), widgetType,
                                    model.getName(), p, orgVal, modelVal, rule, false, true, null,
                                    lineNumber,true,model.getClass()));
                        }
                    } else if (rule == ValidationRule.WRITE) {
                        //write properties must be different and non null
                        numberOfWRITEProperties++;
                        if (modelVal == null || String.valueOf(modelVal).trim().isEmpty()) {
                            //simple write properties are never critical
                            failures.add(new ValidationFailure(pathToFile, model.getWUID(), widgetType,
                                model.getName(), p, orgVal, modelVal, rule, false, false, null, lineNumber, false,
                                model.getClass()));
                            numberOfWRITEFailures++;
                        } else if (!isFontColorPropertyDefined(modelVal)) {
                            numberOfWRITEFailures++;
                            failures.add(new ValidationFailure(pathToFile, model.getWUID(), widgetType,
                                    model.getName(), p, orgVal, modelVal, rule, false, true, null,
                                    lineNumber, true,model.getClass()));
                        }
                    }
                }
            }
        }
        if (failures.size() != startingFailures) {
            numberOfWidgetsFailures++;
        }
        if (model instanceof AbstractContainerModel && !(model instanceof LinkingContainerModel)) {
            check(pathToFile, (AbstractContainerModel)model, failures);
        }

    }

    private void initModel(AbstractWidgetModel model) {
        //initialize the model in the GUI thread
        //Even if the GUI thread is not required, if some of the GUI stuff will be loaded directly
        //it might end up being loaded with a wrong class loader. That will cause problems later when
        //the same class will be needed by some other plugin.

       //do not create linking container, because that might try reload the linked OPI, which we don't want at this point
        if (model instanceof LinkingContainerModel) return;
        Display.getDefault().syncExec(() -> {
                EditPart editPart = editPartFactory.createEditPart(null, model);
                if (editPart instanceof AbstractBaseEditPart) {
                    try {
                        if (doCreateFigureMethod != null) {
                            doCreateFigureMethod.invoke(editPart);
                        }
                    } catch (Exception e) {
                        //ignore whatever exception might be thrown, because the edit part is not fully initialised
                        //we just need the properties to be in the proper visible/invisible state
                    }
                    try {
                        if (registerBasePropertyChangeHandlersMethod != null) {
                            registerBasePropertyChangeHandlersMethod.invoke(editPart);
                        }
                    } catch (Exception e) {}
                    try{
                        if (registerPropertyChangeHandlersMethod != null) {
                            registerPropertyChangeHandlersMethod.invoke(editPart);
                        }
                    } catch (Exception e) {}
                }
                if (editPart instanceof ArrayEditPart) {
                    List<AbstractWidgetModel> children = ((ArrayModel)model).getChildren();
                    int i = 0;
                    for (AbstractWidgetModel m : children) {
                        initModel(m);
                        EditPart ep = editPartFactory.createEditPart(null, m);
                        try {
                            hookChildMethod.invoke(editPart, ep, i++, true);
                        } catch (Exception e) {}
                    }
                }

            });

    }

    /**
     * Returns true if the modelVal is accepted value for the property given by the propertyName.
     * Property value is accepted if it is identical to the schema value (orgVal) or if the value
     * is represented by one of the additional acceptable values.
     *
     * @param widgetType the type of the widget to which the property belongs
     * @param propertyName the name of the property to check
     * @param orgVal the value from the schema
     * @param modelVal the value to check
     * @return true if accepted or false otherwise
     */
    private boolean isPropertyAccepted(String widgetType, String propertyName, Object orgVal, Object modelVal) {
        //if values are identical, return true
        if (Objects.equals(modelVal, orgVal)) {
            return true;
        } else if (String.valueOf(modelVal).equals(String.valueOf(orgVal))) {
            return true;
        }
        String[] acceptableValues = getValueFromMap(widgetType, propertyName,
                additionalAcceptableValues, patternsAdditionalAcceptableValues);
        //if there are no additional acceptable values, the value is not accepted
        if (acceptableValues == null) {
            return false;
        }
        String value = String.valueOf(modelVal);
        for (String s : acceptableValues) {
            if (value.equals(s)) return true;
        }
        return false;
    }

    private ValidationFailure checkAction(IPath pathToFile, ActionsInput modelInput, ActionsInput originalInput,
            AbstractWidgetModel model, ValidationRule rule) {
        List<AbstractWidgetAction> modelActions = modelInput.getActionsList();
        List<AbstractWidgetAction> originalActions = originalInput.getActionsList();
        ValidationFailure f = handleActionsScriptsRules(pathToFile, model.getWUID(), model.getTypeID(),
                model.getName(), model.getClass(), AbstractWidgetModel.PROP_ACTIONS, modelActions, originalActions,
                modelInput, originalInput, rule, model.getLineNumber(),
                (orgAction,modelAction) -> Utilities.areActionsIdentical(orgAction, modelAction),
                (action) -> ActionsProperty.XML_ELEMENT_ACTION,
                (action) -> action.getActionType().getDescription() + ": " + action.getDescription(),
                (match) -> Utilities.actionMatchValueToMessage(match),
                (action) -> "");
        List<SubValidationFailure> ff = new ArrayList<>(2);
        if (rule == ValidationRule.RO) {
            if (modelInput.isFirstActionHookedUpToWidget() != originalInput.isFirstActionHookedUpToWidget()) {
                ff.add(new SubValidationFailure(pathToFile, model.getWUID(),
                        model.getTypeID(), model.getName(), AbstractWidgetModel.PROP_ACTIONS,
                        AbstractWidgetModel.PROP_ACTIONS,
                        Utilities.PROP_ACTION_HOOK, originalInput.isFirstActionHookedUpToWidget(),
                        modelInput.isFirstActionHookedUpToWidget(), rule, true, true, null,
                        model.getLineNumber(),model.getClass()));
            }
            if (modelInput.isHookUpAllActionsToWidget() != originalInput.isHookUpAllActionsToWidget()) {
                ff.add(new SubValidationFailure(pathToFile, model.getWUID(),
                        model.getTypeID(), model.getName(), AbstractWidgetModel.PROP_ACTIONS,
                        AbstractWidgetModel.PROP_ACTIONS,
                        Utilities.PROP_ACTION_HOOK_ALL, originalInput.isHookUpAllActionsToWidget(),
                        modelInput.isHookUpAllActionsToWidget(), rule, true, true, null,
                        model.getLineNumber(),model.getClass()));
            }
        }
        if (!ff.isEmpty()) {
            if (f == null) {
                numberOfCriticalROFailures++;
                f = new ValidationFailure(pathToFile, model.getWUID(), model.getTypeID(), model.getName(),
                        AbstractWidgetModel.PROP_ACTIONS, originalInput, modelInput, rule, true, true,
                        AbstractWidgetModel.PROP_ACTIONS + ": settings of a READ-ONLY property have been changed",
                        model.getLineNumber(),false,model.getClass());
            }
            f.addSubFailure(ff);
        }
        return f;
    }

    /**
     * Handles the check of the actions, scripts, and rules (stuff).
     *
     * @param resource the path to the checked file
     * @param wuid the widget unique id that owns the stuff
     * @param widgetType the widget type
     * @param widgetName the widget name
     * @param widgetModel the widget model
     * @param property the property (actions, scripts, rules)
     * @param model the list containing the stuff from the validated model
     * @param original the list containing the stuff from the schema
     * @param modelVal the model property
     * @param orgVal the schema property
     * @param rule the rule for the property
     * @param lineNumber the line at which the model is located in the file
     * @param comparator the comparator to use when comparing stuff
     * @param subPropertyTagger the function that returns the tag of the sub property that did not match
     * @param subPropertyDescriptor the helper that can transform the stuff into the sub property name used in the
     *              failure description
     * @param naming the function that returns the name of the sub property
     * @return the validation failure if it was detected or null if everything is OK
     */
    private <T> ValidationFailure handleActionsScriptsRules(IPath resource, String wuid, String widgetType,
            String widgetName, Class<? extends AbstractWidgetModel> widgetModel, String property,
            List<T> model, List<T> original, Object modelVal, Object orgVal,
            ValidationRule rule, int lineNumber, Comparator<T> comparator, Function<T,String> subPropertyTagger,
            Function<T,String> subPropDescriptor, Function<Integer,String> messageGenerator,
            Function<T,String> naming) {

        if (rule == ValidationRule.RW) {
            List<SubValidationFailure> ffs = checkRemovedValues(resource, wuid, widgetType, widgetName,widgetModel,
                    property, model, rule, lineNumber,
                    subPropertyTagger, subPropDescriptor, messageGenerator, naming);
            if (!ffs.isEmpty()) {
                numberOfRWFailures++;
                ValidationFailure f = new ValidationFailure(resource,wuid,widgetType,widgetName,
                        property,orgVal,modelVal,rule,false,true,
                        property +": unneeded sub property present", lineNumber, false, widgetModel);
                f.addSubFailure(ffs);
                return f;
            }
        } else if (rule == ValidationRule.WRITE) {
            numberOfWRITEProperties++;
            //make sure that all original items are in the checked model
            List<SubValidationFailure> ff = new ArrayList<>();
            for (T stuff : original) {
                findStuff: {
                    int mostTightMatchValue = Integer.MAX_VALUE;
                    for (T m : model) {
                        int v = comparator.compare(stuff, m);
                        if (v == 0) {
                            break findStuff;
                        }
                        if (v < mostTightMatchValue) {
                            mostTightMatchValue = v;
                        }
                    }

                    ff.add(new SubValidationFailure(resource,wuid,widgetType,widgetName,
                            property, subPropertyTagger.apply(stuff), subPropDescriptor.apply(stuff),
                            stuff,null,rule,false,true,messageGenerator.apply(mostTightMatchValue), lineNumber,
                            widgetModel));
                }
            }
            ValidationFailure f = null;
            if (!ff.isEmpty()) {
                numberOfWRITEFailures++;
                //if not all the originals are defined, it is a critical failure
                f = new ValidationFailure(resource,wuid,widgetType,widgetName,
                        property,original,model,rule,true,true,
                        property +": predefined items are missing in a WRITE property", lineNumber,false,
                        widgetModel);
                f.addSubFailure(ff);
            } else if (model.isEmpty()) {
                numberOfWRITEFailures++;
                //if nothing was changed at all, it is a non critical failure
                f = new ValidationFailure(resource,wuid,widgetType,widgetName,
                        property,orgVal,modelVal,rule,false,false,
                        property +": nothing has been defined for a WRITE property", lineNumber,false,
                        widgetModel);
            }
            List<SubValidationFailure> ffs = checkRemovedValues(resource, wuid, widgetType, widgetName, widgetModel,
                    property, model, rule, lineNumber,
                    subPropertyTagger, subPropDescriptor, messageGenerator, naming);
            if (!ffs.isEmpty()) {
                if (f == null) {
                    numberOfWRITEFailures++;
                    f = new ValidationFailure(resource,wuid,widgetType,widgetName,
                            property,orgVal,modelVal,rule,false,true,
                            property +": unneeded sub property present", lineNumber,false,widgetModel);
                }
                f.addSubFailure(ffs);
            }

            return f;
        } else if (rule == ValidationRule.RO) {
            numberOfROProperties++;
            //make sure that all original items are in the checked model
            List<SubValidationFailure> ff = new ArrayList<>();
            //the list of items actions that are in the model, but are not defined in the original
            List<T> notInOriginalStuff = new ArrayList<>(model);
            for (T stuff : original) {
                findStuff: {
                    int mostTightMatchValue = Integer.MAX_VALUE;
                    for (T m : model) {
                        int v = comparator.compare(stuff, m);
                        if (v == 0) {
                            notInOriginalStuff.remove(m);
                            break findStuff;
                        }
                        if (v < mostTightMatchValue) {
                            mostTightMatchValue = v;
                        }
                    }

                    ff.add(new SubValidationFailure(resource,wuid,widgetType,widgetName,
                            property, subPropertyTagger.apply(stuff), subPropDescriptor.apply(stuff),
                            stuff,null,rule,true,true,messageGenerator.apply(mostTightMatchValue), lineNumber,
                            widgetModel));
                }
            }

            if (!notInOriginalStuff.isEmpty()) {
                //model has items that are not in the schema
                for (T a : notInOriginalStuff) {
                    ff.add(new SubValidationFailure(resource,wuid,widgetType,widgetName,
                            property, subPropertyTagger.apply(a), subPropDescriptor.apply(a),
                            null, a, rule,false,true,null, lineNumber,widgetModel));
                }
            }

            if (!ff.isEmpty()) {
                numberOfCriticalROFailures++;
                ValidationFailure f = new ValidationFailure(resource,wuid,widgetType,widgetName,
                        property,orgVal,modelVal,rule,true,true,
                        property + ": READ-ONLY property was changed", lineNumber,false,widgetModel);
                f.addSubFailure(ff);
                return f;
            }
        }
        return null;
    }

    private <T> List<SubValidationFailure> checkRemovedValues(IPath resource, String wuid, String widgetType,
            String widgetName, Class<? extends AbstractWidgetModel> widgetModel, String property, List<T> model,
            ValidationRule rule, int lineNumber, Function<T,String> subPropertyTagger,
            Function<T,String> subPropDescriptor, Function<Integer,String> messageGenerator,
            Function<T,String> naming) {
        String[] valuesToRemove = getValueFromMap(widgetType, property, removedValues, patternsRemovedValues);
        List<SubValidationFailure> ffs = new ArrayList<>();
        if (valuesToRemove != null) {
            for (T m : model) {
                String name = naming.apply(m);
                for (String v : valuesToRemove) {
                    if (v.equals(name)) {
                        ffs.add(new SubValidationFailure(resource, wuid, widgetType, widgetName, property,
                                subPropertyTagger.apply(m), subPropDescriptor.apply(m),
                                null, m, rule, false, true, null, lineNumber, true,widgetModel, null));
                        break;
                    }
                }
            }
        }
        return ffs;
    }

    private ValidationFailure checkWhatRulesDo(IPath pathToFile, int lineNumber, ValidationFailure vf,
            RulesInput rules) {
        for (RuleData rd : rules.getRuleDataList()) {
            AbstractWidgetProperty property = rd.getProperty();
            AbstractWidgetModel model = rd.getWidgetModel();
            ValidationRule rule = getRuleForProperty(property.getPropertyID(), model.getTypeID());
            if (rule == ValidationRule.RO) {
                if (vf == null) {
                    vf = new ValidationFailure(pathToFile, model.getWUID(), model.getTypeID(),
                            model.getName(), AbstractWidgetModel.PROP_RULES,
                            null, rules, rule, true, true, "rules: Read-only property changed by rule.",
                            lineNumber, false, model.getClass());
                }

                vf.addSubFailure(new SubValidationFailure(pathToFile, model.getWUID(),
                        model.getTypeID(), model.getName(), AbstractWidgetModel.PROP_RULES,
                        RulesProperty.XML_ELEMENT_RULE, rd.getName(),
                        null, rd, rule, true, true, "Modifying RO property " + property.getPropertyID() + ".",
                        lineNumber, true, model.getClass(), null));
            }
        }
        return vf;
    }

    private static final String SCRIPT_PROPERTY_MODIFICATION = "widget.setPropertyValue(\"";
    private static final int SCRIPT_LENGTH = SCRIPT_PROPERTY_MODIFICATION.length();

    private ValidationFailure checkWhatScriptsDo(IPath pathToFile, int lineNumber, ValidationFailure vf,
            List<ScriptData> scripts, AbstractWidgetModel model) throws IOException {
        if (scripts.isEmpty()) {
            return vf;
        }
        List<SubValidationFailure> failures = new ArrayList<SubValidationFailure>();
        for (ScriptData sd : scripts) {
            String text = sd.getScriptText();
            if (text != null) {
                failures.addAll(checkScriptLine(sd,text,model,pathToFile,lineNumber,sd.getScriptName(),null));
            } else if (sd.getPath() != null) {
                failures.addAll(checkScript(sd, model, lineNumber));
            }
        }

        if (!failures.isEmpty()) {
            if (vf == null) {
                vf = new ValidationFailure(pathToFile, model.getWUID(), model.getTypeID(),
                      model.getName(), AbstractWidgetModel.PROP_SCRIPTS,
                      null, null, ValidationRule.RO, true, false, "scripts: Read-only property changed by scripts",
                      lineNumber, false, model.getClass());
            }
            vf.addSubFailure(failures);
        }

        return vf;
    }

    private List<SubValidationFailure> checkScriptLine(ScriptData sd, String text, AbstractWidgetModel model,
            IPath pathToFile, int lineNumber, String scriptName, IResource resource) {
        List<SubValidationFailure> failures = new ArrayList<>();
        String widgetType = model.getTypeID();
        int idx = 0;
        while (idx >= 0) {
            idx = text.indexOf(SCRIPT_PROPERTY_MODIFICATION,idx);
            if (idx >= 0) {
                String property = text.substring(idx + SCRIPT_LENGTH, text.indexOf('"',idx+SCRIPT_LENGTH));
                ValidationRule rule = getRuleForProperty(property, widgetType);
                if (rule == ValidationRule.RO) {
                    failures.add(new SubValidationFailure(pathToFile, model.getWUID(),
                          model.getTypeID(), model.getName(), AbstractWidgetModel.PROP_SCRIPTS,
                          ScriptProperty.XML_ELEMENT_SCRIPT_TEXT, scriptName,
                          null, sd, rule, true, false, "Modifying RO property " + property + ".",
                          lineNumber, false, model.getClass(), resource));
                }
                idx++;
            }
        }
        return failures;
    }

    private List<SubValidationFailure> checkScript(ScriptData data, AbstractWidgetModel model, int orgLineNumber)
            throws IOException {
        IPath absoluteScriptPath = data.getPath();
        if(!absoluteScriptPath.isAbsolute()){
            DisplayModel root = model instanceof AbstractLinkingContainerModel ?
                ((AbstractLinkingContainerModel) model).getDisplayModel() : model.getRootDisplayModel();
            absoluteScriptPath = root.getOpiFilePath().removeLastSegments(1).append(absoluteScriptPath);
            if(!ResourceUtil.isExsitingFile(absoluteScriptPath, false)){
                absoluteScriptPath = ResourceUtil.getFileOnSearchPath(data.getPath(), false);
            }
        }
        List<SubValidationFailure> failures = new ArrayList<>();
        try (InputStream inputStream = FileUtil.getInputStreamFromFile(absoluteScriptPath.toString(), null);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) { //$NON-NLS-1$
            int lineNumber = 1;
            String line;
            String path = data.getPath().toString();
            IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(absoluteScriptPath);
            while (reader.ready()) {
                line = reader.readLine();
                if (!line.isEmpty()) {
                    failures.addAll(checkScriptLine(data,line, model, absoluteScriptPath, lineNumber, path,
                            resource));
                }
                lineNumber++;
            }
        } catch (Exception e) {
            failures.add(new SubValidationFailure(data.getPath(), model.getWUID(), model.getTypeID(), model.getName(),
                    AbstractWidgetModel.PROP_SCRIPTS, ScriptProperty.XML_ELEMENT_SCRIPT_TEXT, data.getPath().toString(),
                    null, null, ValidationRule.WRITE, true, false, e.getMessage(), orgLineNumber, model.getClass()));
        }
        return failures;
    }

    /**
     * Checks if the property matches one of the predefined colours or fonts.
     * If yes it returns true otherwise it returns false.
     *
     * @param modelVal the property value to check
     * @return true if a match was found or false otherwise
     */
    private boolean isPropertyDefined(Object modelVal) {
        if (modelVal instanceof OPIColor) {
            for (OPIColor c : colors) {
                if (c.equals(modelVal)) return true;
            }
        } else if (modelVal instanceof OPIFont) {
            for (OPIFont c : fonts) {
                if (c.equals(modelVal)) return true;
            }
        }
        return false;
    }

    /**
     * Checks if the value is a font or colour. If yes it checks if it uses one of the defined values and returns
     * true if yes or false if not. If the value is not font or colour it always returns true.
     *
     * @param modelVal the property value to check
     * @return true if it is a font or colour and a match was found, true if it is any other property
     *          or false otherwise
     */
    private boolean isFontColorPropertyDefined(Object modelVal) {
        if (modelVal instanceof OPIColor || modelVal instanceof OPIFont) {
            return isPropertyDefined(modelVal);
        }
        return true;
    }

    /**
     * Loads the validation rule from the rules map. First the rule for the property of the specified widget
     * (as widget.property) is being loaded. If it is defined it is returned. If it does not exist the widget
     * is trimmed of any prefixes (e.g. org.csstudio.opibuilder.widgets) and the property for the trimmed
     * widget is loaded. If it exist it is returned otherwise a general property rule definition is searched
     * for. If none is found the property is of read/write type.
     *
     * @param property the name of the property (should be in lower case)
     * @param widget the type of the widget (should be in lower case)
     * @return the rule for the property
     */
    private ValidationRule getRuleForProperty(String property, String widget) {
        ValidationRule value = getValueFromMap(widget, property, rules, patternRules);
        if (value == null) {
            return ValidationRule.RW;
        } else {
            return value;
        }
    }

    /**
     * Loads the value that matches the widget and property.
     *
     * @see #getRuleForProperty(String, String)
     * @param widget the type of the widget
     * @param property the name of the property
     * @param values the values map, where the keys are true widget/property combinations
     * @param patterns the map of patterns that widget/property combinations may match
     * @return the value if found or null if nothing matched
     */
    private static <T> T getValueFromMap(String widget, String property, Map<String,T> values,
            Map<Pattern, T> patterns) {
        widget = widget.toLowerCase();
        property = property.toLowerCase();
        String fullProp = widget + "." + property;
        String prop = null;
        T value = values.get(fullProp);
        if (value == null) {
            //try with abbreviated widget - omit org.csstudio.opibuilder.widgets
            prop = widget.substring(widget.lastIndexOf('.')+1) + "." + property;
            value = values.get(prop);
        }
        if (value == null) {
            //try general property definition
            value = values.get(property);
        }

        if (value == null) {
            //no match was found yet, check patterns
            //if one of the patterns matches, add the property to the rules, so we can find it more quickly next time
            for (Entry<Pattern,T> e : patterns.entrySet()) {
                if (e.getKey().matcher(fullProp).matches()) {
                    value = e.getValue();
                    values.put(fullProp, value);
                    return value;
                } else if (e.getKey().matcher(prop).matches()) {
                    value = e.getValue();
                    values.put(prop, value);
                    return value;
                } else if (e.getKey().matcher(property).matches()) {
                    value = e.getValue();
                    values.put(property, value);
                    return value;
                }
            }
        }

        return value;
    }
}
