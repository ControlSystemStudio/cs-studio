/*******************************************************************************
 * Copyright (c) 2010-2015 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.validation.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.properties.ActionsProperty;
import org.csstudio.opibuilder.properties.RulesProperty;
import org.csstudio.opibuilder.properties.ScriptProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.script.RuleData;
import org.csstudio.opibuilder.script.RulesInput;
import org.csstudio.opibuilder.script.ScriptData;
import org.csstudio.opibuilder.script.ScriptsInput;
import org.csstudio.opibuilder.util.MediaService;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.validation.core.XMLParser.LinedElement;
import org.csstudio.opibuilder.widgetActions.AbstractWidgetAction;
import org.csstudio.opibuilder.widgetActions.ActionsInput;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Display;
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
    private OPIColor[] colors;
    private OPIFont[] fonts;
    
    private int numberOfAnalyzedWidgets = 0;
    private int numberOfWidgetsFailures = 0;
    private int numberOfAnalyzedFiles = 0;
    private int numberOfFilesFailures = 0;
    private int numberOfROProperties = 0;
    private int numberOfRWProperties = 0;
    private int numberOfRWFailures = 0;
    private int numberOfWRITEProperties = 0;
    private int numberOfCriticalROFailures = 0;
    private int numberOfMajorROFailures = 0;
    private int numberOfWRITEFailures = 0;
    
    private IPath schemaPath;
//    private WidgetEditPartFactory editPartFactory;
//    private Method registerPropertyChangeHandlersMethod;
//    private Method registerBasePropertyChangeHandlersMethod;
//    private Method doCreateFigureMethod;
        
    private final Map<String, ValidationRule> rules;
    private final Map<Pattern, ValidationRule> patternRules;
    private final Map<String, String[]> additionalAcceptableValues;
    private final Map<Pattern, String[]> patternsAdditionalAcceptableValues;
    
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
     */
    public SchemaVerifier(Map<String,ValidationRule> rules, Map<Pattern,ValidationRule> patternRules,
            Map<String,String[]> additionalAcceptableValues, Map<Pattern,String[]> acceptableValuesPatterns) {
        this(PreferencesHelper.getSchemaOPIPath(), rules, patternRules, additionalAcceptableValues,
                acceptableValuesPatterns);
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
     */
    public SchemaVerifier(IPath pathToSchema, Map<String,ValidationRule> rules,  
            Map<Pattern,ValidationRule> patternRules, Map<String,String[]> additionalAcceptableValues,
            Map<Pattern,String[]> acceptableValuesPatterns) {
        if (pathToSchema == null) {
            throw new IllegalArgumentException("There is no OPI schema defined.");
        }
        this.schemaPath = pathToSchema;
        this.rules = new HashMap<>(rules);
        this.patternRules = new HashMap<>(patternRules);
        this.additionalAcceptableValues = new HashMap<>(additionalAcceptableValues);
        this.patternsAdditionalAcceptableValues = new HashMap<>(acceptableValuesPatterns);
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
     * @param validatedPath the path the OPI or folder containing OPIs.
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
            colors = MediaService.getInstance().getAllPredefinedColors();
            fonts = MediaService.getInstance().getAllPredefinedFonts();
//            editPartFactory = new WidgetEditPartFactory(ExecutionMode.EDIT_MODE);
//            try {
//                registerPropertyChangeHandlersMethod = AbstractBaseEditPart.class
//                        .getDeclaredMethod("registerPropertyChangeHandlers");
//                registerPropertyChangeHandlersMethod.setAccessible(true);
//                registerBasePropertyChangeHandlersMethod = AbstractBaseEditPart.class
//                        .getDeclaredMethod("registerBasePropertyChangeHandlers");
//                registerBasePropertyChangeHandlersMethod.setAccessible(true);
//                doCreateFigureMethod = AbstractBaseEditPart.class
//                        .getDeclaredMethod("doCreateFigure");
//                doCreateFigureMethod.setAccessible(true);
//            } catch (NoSuchMethodException | SecurityException e) {
//                LOGGER.log(Level.WARNING, "Cannot register property change handlers.", e);
//            }
        }
        this.validatedPath = validatedPath;
        
        File file = this.validatedPath.toFile();
        List<ValidationFailure> failures = new ArrayList<>();
        if (file.isFile()) {
            failures.addAll(check(this.validatedPath));
        } else if (file.isDirectory()) {
            List<File> opis = new ArrayList<>();
            gatherOPIFiles(file, opis);
            for (File f : opis) {
                failures.addAll(check(new Path(f.getAbsolutePath())));
            }
        }
        validationFailures.addAll(failures);
        return failures.toArray(new ValidationFailure[failures.size()]);
    }
    
    /**
     * Recursively scans the parent file for all files that have the extension opi.
     * 
     * @param parent the directory to scan
     * @param opis the list into which the found opi files are stored
     */
    private static void gatherOPIFiles(File parent, List<File> opis) {
        for (File f : parent.listFiles()) {
            if (f.isDirectory()) {
                gatherOPIFiles(parent, opis);
            } else if (f.getAbsolutePath().toLowerCase().endsWith(".opi")) {
                opis.add(f);
            }
        }
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
            XMLUtil.fillDisplayModelFromInputStream(inputStream, displayModel, Display.getDefault(),false);
        } catch (Exception e) {
            throw new IOException("Could not read the opi " + opi.toOSString() +".",e);
        }
        
        numberOfAnalyzedFiles++;
        List<ValidationFailure> failures = new NonNullArrayList<>();
        check(opi, displayModel, failures);    
        findCoordinates(failures.toArray(new ValidationFailure[failures.size()]), opi);
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
     * @throws IOException if there was an error opening the file
     */
    private void findCoordinates(ValidationFailure[] failures, IPath path) throws IOException {
        try (InputStream stream = ResourceUtil.pathToInputStream(path, false)) {
            SAXBuilder saxBuilder = XMLParser.createBuilder();
            Document document = saxBuilder.build(stream);
            
            Iterator<?> widgetNodes = document.getDescendants(new ElementFilter(XMLUtil.XMLTAG_WIDGET));
            Iterator<?> displayNodes = document.getDescendants(new ElementFilter(XMLUtil.XMLTAG_DISPLAY));
            Iterator<?> connectionNodes = document.getDescendants(new ElementFilter(XMLUtil.XMLTAG_CONNECTION));
            
            //gather all widgets, displays, and connectors in the same array
            List<LinedElement> list = new ArrayList<>();
            while(widgetNodes.hasNext()) {
                list.add((LinedElement)widgetNodes.next());
            }
            while(displayNodes.hasNext()) {
                list.add((LinedElement)displayNodes.next());
            }
            while(connectionNodes.hasNext()) {
                list.add((LinedElement)connectionNodes.next());
            }
            LinedElement[] widgets = list.toArray(new LinedElement[list.size()]);
            
            for (int m = 0; m < failures.length; m++) {
                String type = failures[m].getWidgetType();
                String name = failures[m].getWidgetName();
                //for every failure find the widget that match the widget typeId 
                for (int i = 0; i < widgets.length; i++) {
                    findProperty:
                    if (type.equals(widgets[i].getAttribute(XMLUtil.XMLATTR_TYPEID).getValue())) {
                        //find the WUID node to get the match
                        Element wuidNode = findPropertyElement(widgets[i],name,AbstractWidgetModel.PROP_WIDGET_UID);
                        if (wuidNode != null) {
                            if (!failures[m].getWUID().equals(wuidNode.getValue())) {
                                break findProperty;
                            }
                        } else {
                            //if it doesn't have UID, assume it is the right widget, otherwise break
                            if (widgets[i].getChild(AbstractWidgetModel.PROP_WIDGET_UID) != null) {
                                break findProperty;
                            }
                        }
                        //if wuid is null, wuids are not defined
                        //find the node describing the property, but only if the name of the widget matches the one in the failure
                        LinedElement node = findPropertyElement(widgets[i],name,failures[m].getProperty());
                        if (node == null && failures[m].getRule() != ValidationRule.RO) {
                            //if no such property is find and the property has a write or RW rule, it is not defined 
                            //in the XML, so mark the widget itself 
                            node = widgets[i];
                        } 
                        
                        if (node != null) {
                            int line = (Integer)node.getLineNumber();
                            //the widgets may have identical names, so check that we didn't have this line in any of the previous failures
                            for (int n = 0; n < m; n++) {
                                if (failures[n].getLineNumber() == line) {
                                    //if the line is duplicated, continue with the next widget
                                    break findProperty;
                                }
                            }
                            //otherwise continue with the next failure
                            failures[m].setLineNumber(line);
                            //check if there are subvalidation failures and find those as well
                            if (failures[m].hasSubFailures()) {
                                SubValidationFailure[] subs = failures[m].getSubFailures();
                                for (SubValidationFailure s : subs) {
                                    if (s.getActualValue() != null) {
                                        LinedElement n = findSubNode(node, s.getSubPropertyTag(), s.getActualValue());
                                        if (n != null) {
                                            s.setLineNumber(n.getLineNumber());
                                        }
                                    } else {
                                        s.setLineNumber(line);
                                    }
                                }
                            }
                            break;
                        }
                    }                    
                }
            }
        } catch (Exception e) {
            throw new IOException("Unable to load opi '" + path + "'.",e);
        }
    }
    
    private static LinedElement findSubNode(LinedElement parent, String tagName, Object actualValue) throws Exception {
        if (parent.getName().equals(tagName)) return parent;
        List<?> children = parent.getChildren();
        try {
            if (actualValue instanceof RuleData) {
                RulesProperty prop = new RulesProperty(AbstractWidgetModel.PROP_RULES, "Rules", 
                        WidgetPropertyCategory.Behavior);
                prop.setWidgetModel(new DisplayModel());
                List<RuleData> rules = prop.readValueFromXML(parent).getRuleDataList();
                for (int i = 0; i < rules.size(); i++) {
                    if (Utilities.areRulesIdentical(rules.get(i), (RuleData)actualValue) == 0) {
                        return (LinedElement)children.get(i);
                    }
                }
            } else if (actualValue instanceof ScriptData) {
                ScriptProperty prop = new ScriptProperty(AbstractWidgetModel.PROP_SCRIPTS, "Scripts", 
                        WidgetPropertyCategory.Behavior);
                List<ScriptData> scripts = prop.readValueFromXML(parent).getScriptList();
                for (int i = 0; i < scripts.size(); i++) {
                    if (Utilities.areScriptsIdentical(scripts.get(i), (ScriptData)actualValue) == 0) {
                        return (LinedElement)children.get(i);
                    }
                }
            } else if (actualValue instanceof AbstractWidgetAction) {
                ActionsProperty prop = new ActionsProperty(AbstractWidgetModel.PROP_ACTIONS, "Actions", 
                        WidgetPropertyCategory.Behavior);
                List<AbstractWidgetAction> actions = prop.readValueFromXML(parent).getActionsList();
                for (int i = 0; i < actions.size(); i++) {
                    if (Utilities.areActionsIdentical(actions.get(i), (AbstractWidgetAction)actualValue) == 0) {
                        return (LinedElement)children.get(i);
                    }
                }
            }
        } catch (Exception e) {
            //just in case if something gets updated in the opibuilder
            LOGGER.log(Level.SEVERE, "Cannot find the node for " + tagName, e);
        }
        return null;
    }
    
    private static LinedElement findPropertyElement(Element node, String name, String property) {
        Element n = node.getChild(Utilities.TAG_NAME);
        if (n.getValue().equals(name)) {
            return (LinedElement)node.getChild(property);
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
     */
    private void check(IPath pathToFile, AbstractContainerModel containerModel, List<ValidationFailure> failures) {
        AbstractWidgetModel original;
        Object orgVal, modelVal;
        String widgetType;
        ValidationRule rule;
        int startingFailures = failures.size();
        
        for (AbstractWidgetModel model : containerModel.getChildren()) {
            numberOfAnalyzedWidgets++;
            widgetType = model.getTypeID();
            original = schema.get(widgetType);
//            initModel(model);
            if (original != null) {
                Set<String> properties = model.getAllPropertyIDs() ;
                for (String p : properties) {
                    rule = getRuleForProperty(p, widgetType);
                    modelVal = model.getPropertyValue(p);
                    orgVal = original.getPropertyValue(p);
                    //if the checked property is not savable (e.g. background color for action button), ignore it
                    //TODO property is always savable, because one needs to activate the edit part to set up the 
                    //visibility, but there are problems with org.eclipse.gef being a dependency to this plugin
                    if (!model.getProperty(p).isSavable()) {
                        continue;
                    }
                    if (rule == ValidationRule.RW) {
                        numberOfRWProperties++;
                        //nothing to do in the case of read/write properties except for fonts and colors
                        if (!isFontColorPropertyDefined(modelVal)) {
                            numberOfRWFailures++;
                            failures.add(new ValidationFailure(pathToFile, model.getWUID(), widgetType, 
                                    model.getName(), p, null, modelVal, rule, false, false, null));
                        }
                        continue;
                    }
                    
                    //actions, rules and scripts are a bit nasty
                    if (AbstractWidgetModel.PROP_ACTIONS.equals(p)) {
                        ActionsInput mi = ((ActionsInput)modelVal);
                        ActionsInput oi = ((ActionsInput)orgVal);
                        failures.add(checkAction(pathToFile,mi,oi,model,rule));
                    } else if (AbstractWidgetModel.PROP_RULES.equals(p)) {
                        List<RuleData> modelRules = ((RulesInput)modelVal).getRuleDataList();
                        List<RuleData> originalRules = ((RulesInput)orgVal).getRuleDataList();
                        failures.add(handleActionsScriptsRules(pathToFile, model.getWUID(), widgetType, 
                                model.getName(), p, modelRules, originalRules, modelVal, orgVal, rule,  
                                (orgRule,modelRule) -> Utilities.areRulesIdentical(orgRule,modelRule),
                                (theRule) -> RulesProperty.XML_ELEMENT_RULE,
                                (therule) -> therule.getName(),
                                (match) -> Utilities.ruleMatchValueToMessage(match)));
                    } else if (AbstractWidgetModel.PROP_SCRIPTS.equals(p)) {
                        List<ScriptData> modelScripts = ((ScriptsInput)modelVal).getScriptList();
                        List<ScriptData> originalScripts = ((ScriptsInput)orgVal).getScriptList();
                        failures.add(handleActionsScriptsRules(pathToFile, model.getWUID(), widgetType, 
                                model.getName(), p, modelScripts, originalScripts, modelVal, orgVal, rule, 
                                (orgScript,modelScript) -> Utilities.areScriptsIdentical(orgScript, modelScript),
                                (script) -> ScriptProperty.XML_ELEMENT_PATH,
                                (script) -> script.isEmbedded() ? script.getScriptName() : script.getPath().toString(),
                                (match) -> Utilities.scriptMatchValueToMessage(match)));
                    } else {
                        if (rule == ValidationRule.RO) {
                            //read-only properties must have identical values, otherwise it is a failure
                            numberOfROProperties++;
                            if (!isPropertyAccepted(widgetType,p,orgVal,modelVal)) {
                                //the failure is always critical, except for fonts and colors if a predefined value was used
                                boolean critical = !isPropertyDefined(modelVal);
                                failures.add(new ValidationFailure(pathToFile, model.getWUID(), widgetType, 
                                    model.getName(), p, orgVal, modelVal, rule, critical,true, null));
                                if (critical) {
                                    numberOfCriticalROFailures++;
                                } else {
                                    numberOfMajorROFailures++;
                                }
                            } else if (!isFontColorPropertyDefined(modelVal)) {
                                numberOfMajorROFailures++;
                                failures.add(new ValidationFailure(pathToFile, model.getWUID(), widgetType, 
                                        model.getName(), p, null, modelVal, rule, false, false, null));
                            }
                        } else if (rule == ValidationRule.WRITE) {
                            //write properties must be different and non null
                            numberOfWRITEProperties++;
                            if (modelVal == null || String.valueOf(modelVal).trim().isEmpty()) {
                                //simple write properties are never critical
                                failures.add(new ValidationFailure(pathToFile, model.getWUID(), widgetType, 
                                    model.getName(), p, orgVal, modelVal, rule, false, false, null)); 
                                numberOfWRITEFailures++;
                            } else if (!isFontColorPropertyDefined(modelVal)) {
                                numberOfWRITEFailures++;
                                failures.add(new ValidationFailure(pathToFile, model.getWUID(), widgetType, 
                                        model.getName(), p, null, modelVal, rule, false, false, null));
                            }
                        }
                    }
                }
            }        
            if (model instanceof AbstractContainerModel) {
                check(pathToFile, (AbstractContainerModel)model, failures);
            }
            
        }  
        if (failures.size() != startingFailures) {
            numberOfWidgetsFailures++;
        }
    }
    
//    private void initModel(AbstractWidgetModel model) {
//        AbstractBaseEditPart editPart = (AbstractBaseEditPart)editPartFactory.createEditPart(null, model);
//        try {
//            if (doCreateFigureMethod != null) {
//                doCreateFigureMethod.invoke(editPart);
//            }
//        } catch (Exception e) {
//            //ignore whatever exception might be thrown, because the edit part is not fully initialised
//            //we just need the properties to be in the proper visible/invisible state
//        } 
//        try {
//            if (registerBasePropertyChangeHandlersMethod != null) {
//                registerBasePropertyChangeHandlersMethod.invoke(editPart);
//            }
//        } catch (Exception e) {} 
//        try{
//            if (registerPropertyChangeHandlersMethod != null) {
//                registerPropertyChangeHandlersMethod.invoke(editPart);
//            }
//        } catch (Exception e) {} 
//    }
    
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
                model.getName(), AbstractWidgetModel.PROP_ACTIONS, modelActions, originalActions, 
                modelInput, originalInput, rule, 
                (orgAction,modelAction) -> Utilities.areActionsIdentical(orgAction, modelAction),
                (action) -> ActionsProperty.XML_ELEMENT_ACTION,
                (action) -> action.getActionType().getDescription() + ": " + action.getDescription(),
                (match) -> Utilities.actionMatchValueToMessage(match));
        List<SubValidationFailure> ff = new ArrayList<>(2);
        if (rule == ValidationRule.RO) {
            if (modelInput.isFirstActionHookedUpToWidget() != originalInput.isFirstActionHookedUpToWidget()) {
                ff.add(new SubValidationFailure(pathToFile, model.getWUID(),
                        model.getTypeID(), model.getName(), AbstractWidgetModel.PROP_ACTIONS, 
                        AbstractWidgetModel.PROP_ACTIONS, 
                        Utilities.PROP_ACTION_HOOK, originalInput.isFirstActionHookedUpToWidget(),
                        modelInput.isFirstActionHookedUpToWidget(), rule, true, true, null));
            }
            if (modelInput.isHookUpAllActionsToWidget() != originalInput.isHookUpAllActionsToWidget()) {
                ff.add(new SubValidationFailure(pathToFile, model.getWUID(),
                        model.getTypeID(), model.getName(), AbstractWidgetModel.PROP_ACTIONS,
                        AbstractWidgetModel.PROP_ACTIONS,
                        Utilities.PROP_ACTION_HOOK_ALL, originalInput.isHookUpAllActionsToWidget(),
                        modelInput.isHookUpAllActionsToWidget(), rule, true, true, null));
            }
        }
        if (!ff.isEmpty()) {
            if (f == null) {
                numberOfCriticalROFailures++;
                f = new ValidationFailure(pathToFile, model.getWUID(), model.getTypeID(), model.getName(), 
                        AbstractWidgetModel.PROP_ACTIONS, originalInput, modelInput, rule, true, true,
                        AbstractWidgetModel.PROP_ACTIONS + ": settings of a READ-ONLY property have been changed");
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
     * @param property the property (actions, scripts, rules)
     * @param model the list containing the stuff from the validated model
     * @param original the list containing the stuff from the schema
     * @param modelVal the model property
     * @param orgVal the schema property
     * @param rule the rule for the property
     * @param comparator the comparator to use when comparing stuff
     * @param subPropertyTagger the function that returns the tag of the sub property that did not match 
     * @param subPropertyDescriptor the helper that can transform the stuff into the sub property name used in the 
     *              failure description
     * @return the validation failure if it was detected or null if everything is OK
     */
    private <T> ValidationFailure handleActionsScriptsRules(IPath resource, String wuid, String widgetType, 
            String widgetName, String property, List<T> model, List<T> original, Object modelVal, Object orgVal,
            ValidationRule rule, Comparator<T> comparator, Function<T,String> subPropertyTagger, 
            Function<T,String> subPropDescriptor, Function<Integer,String> messageGenerator) {
        if (rule == ValidationRule.WRITE) {
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
                            stuff,null,rule,false,true,messageGenerator.apply(mostTightMatchValue)));
                }
            }
            if (!ff.isEmpty()) {
                numberOfWRITEFailures++;
                //if not all the originals are defined, it is a critical failure
                ValidationFailure f = new ValidationFailure(resource,wuid,widgetType,widgetName,
                        property,original,model,rule,true,true,
                        property +": predefined items are missing in a WRITE property");
                f.addSubFailure(ff);
                return f;
            } else if (model.isEmpty()) {
                numberOfWRITEFailures++;
                //if nothing was changed at all, it is a non critical failure
                return new ValidationFailure(resource,wuid,widgetType,widgetName,
                        property,orgVal,modelVal,rule,false,false,
                        property +": nothing has been defined for a WRITE property");                
            }            
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
                            stuff,null,rule,true,true,messageGenerator.apply(mostTightMatchValue)));
                }
            }
                
            if (!notInOriginalStuff.isEmpty()) {
                //model has items that are not in the schema
                for (T a : notInOriginalStuff) {
                    ff.add(new SubValidationFailure(resource,wuid,widgetType,widgetName,
                            property, subPropertyTagger.apply(a), subPropDescriptor.apply(a), 
                            null, a, rule,false,true,null));
                }
            }
            
            if (!ff.isEmpty()) {
                numberOfCriticalROFailures++;
                ValidationFailure f = new ValidationFailure(resource,wuid,widgetType,widgetName,
                        property,orgVal,modelVal,rule,true,true,
                        property + ": READ-ONLY property was changed");
                f.addSubFailure(ff);
                return f;
            }
        }        
        return null;
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
