/*******************************************************************************
 * Copyright (c) 2010-2015 ITER Organization.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.validation.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.ConnectionModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.XMLUtil;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.util.MediaService;
import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.util.OPIFont;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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
            
    private static final String LINE_NUMBER_KEY_NAME = "lineNumber";
    private static final String TAG_NAME = "name";
    
    private List<ValidationFailure> validationFails = new ArrayList<>();
    private IPath validatedPath;
    private Map<String, AbstractWidgetModel> schema;
    private OPIColor[] colors;
    private OPIFont[] fonts;
    
    private int numberOfAnalyzedWidgets = 0;
    private int numberOfWidgetsFailures = 0;
    private int numberOfAnalyzedFiles = 0;
    private int numberOfFilesFailures = 0;
    private int numberOfROProperties = 0;
    private int numberOfWRITEProperties = 0;
    private int numberOfCriticalROFailures = 0;
    private int numberOfMajorROFailures = 0;
    private int numberOfWRITEFailures = 0;
    
    private IPath schemaPath;
        
    private final Map<String, ValidationRule> rules;
    
    /**
     * Constructs a new schema verifier using the schema path defined in the preferences.
     * 
     * @param rules the validation rules to use (keys are the property names, values are the rules for that property)
     */
    public SchemaVerifier(Map<String,ValidationRule> rules) {
        this(PreferencesHelper.getSchemaOPIPath(), rules);
    }
    
    /**
     * Construct a new SchemaVerifier.
     * 
     * @param pathToSchema the path to the OPI schema against which the opis will be validated
     * @param rules the validation rules to use (keys are the property names, values are the rules for that property)
     */
    public SchemaVerifier(IPath pathToSchema, Map<String,ValidationRule> rules) {
        if (pathToSchema == null) {
            throw new IllegalArgumentException("There is no OPI schema defined.");
        }
        this.schemaPath = pathToSchema;
        this.rules = new HashMap<String, ValidationRule>(rules);
    }
    
    /**
     * Returns the list of all validation failures since the last cleanup
     * 
     * @return the list of validation failures
     */
    public ValidationFailure[] getValidationFailures() {
        return validationFails.toArray(new ValidationFailure[validationFails.size()]);
    }
    
    /**
     * Removes all validation failures and resets the counters.
     */
    public void clean() {
        validationFails.clear();
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
            schema = loadSchema(schemaPath);
            colors = MediaService.getInstance().getAllPredefinedColors();
            fonts = MediaService.getInstance().getAllPredefinedFonts();
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
        validationFails.addAll(failures);
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
            XMLUtil.fillDisplayModelFromInputStream(inputStream, displayModel, Display.getDefault());
        } catch (Exception e) {
            throw new IOException("Could not read the opi " + opi.toOSString() +".",e);
        }
        
        numberOfAnalyzedFiles++;
        List<ValidationFailure> failures = new ArrayList<>();
        check(opi, displayModel, failures);    
        findCoordinates(failures.toArray(new ValidationFailure[failures.size()]), opi);
        if (!failures.isEmpty()) {
            numberOfFilesFailures++;
        }
        return failures;
    }
    

    /**
     * Scans the OPI given by path to find the occurrences of the failures. If they are found it stores the line and 
     * column number into the failure object. All failures are expected to belong to the same OPI.
     * 
     * @param failures the list of failures, which are to be found in the file (should belong to the same path)
     * @param path the path to scan
     * @throws IOException if there was an error opening the file
     */
    private void findCoordinates(ValidationFailure[] failures, IPath path) throws IOException {
        //It is slightly inefficient that we read the whole file once again, because we already read
        //it when the failures were created. However, the scanning of a file doesn't take that long 
        //that this should worry anyone 
        if (failures.length == 0) return;
        for (ValidationFailure f : failures) {
            if (!path.equals(f.getPath())) {
                throw new IllegalArgumentException("All validation failures should belong to the same path. " 
                        + f.getPath() + " is not the same as " + path);
            }
        }
        try (InputStream stream = ResourceUtil.pathToInputStream(path, false)) {
            Document document = readXMLWithLineNumbers(stream);
            NodeList widgetNodes = document.getElementsByTagName(XMLUtil.XMLTAG_WIDGET);
            NodeList displayNodes = document.getElementsByTagName(XMLUtil.XMLTAG_DISPLAY);
            NodeList connectionNodes = document.getElementsByTagName(XMLUtil.XMLTAG_CONNECTION);
            //gather all widgets, displays, and connectors in the same array
            Node[] widgets = new Node[widgetNodes.getLength() + displayNodes.getLength() + connectionNodes.getLength()];
            int displays = displayNodes.getLength();
            int widgetCount = widgetNodes.getLength();
            for (int i = 0; i < displays; i++) {
                widgets[i] = displayNodes.item(i);
            }
            for (int i = 0; i < widgetCount; i++) {
                widgets[displays + i] = widgetNodes.item(i);
            }
            for (int i = 0; i < connectionNodes.getLength(); i++) {
                widgets[displays + widgetCount + i] = connectionNodes.item(i);
            }
            
            for (int m = 0; m < failures.length; m++) {
                String type = failures[m].getWidgetType();
                String name = failures[m].getWidgetName();
                //for every failure find the widget that match the widget typeId 
                for (int i = 0; i < widgets.length; i++) {
                    findProperty:
                    if (type.equals(widgets[i].getAttributes().getNamedItem(XMLUtil.XMLATTR_TYPEID).getTextContent())) {
                        //find the node describing the property, but only if the name of the widget matches the one in the failure
                        Node node = findNode(widgets[i],name,failures[m].getProperty());
                        if (node == null && failures[m].getRule() == ValidationRule.WRITE) {
                            //if no such property is find and the property has a write rule, it is not defined in the XML, so mark the widget itself 
                            node = widgets[i];
                        } 
                        
                        if (node != null) {
                            int line = (Integer)node.getUserData(LINE_NUMBER_KEY_NAME);
                            //the widgets may have identical names, so check that we didn't have this line in any of the previous failures
                            for (int n = 0; n < m; n++) {
                                if (failures[n].getLineNumber() == line) {
                                    //if the line is duplicated, continue with the next widget
                                    break findProperty;
                                }
                            }
                            //otherwise continue with the next failure
                            failures[m].setLineNumber(line);
                            break;
                        }
                    }                    
                }
            }
        } catch (Exception e) {
            throw new IOException("Unable to load opi '" + path + "'.");
        }
    }
    
    /**
     * Returns the child node of the given node, which has the tag name identical to property. The node is only
     * returned if there is a child node with the tag name that has the value identical to the given name.
     *  
     * @param node the parent node to search its children
     * @param name the value of the name tag, which needs to match
     * @param property the tag of the node that is returned
     * @return the node if found or null otherwise
     */
    private static Node findNode(Node node, String name, String property) {
        NodeList nodes = node.getChildNodes();
        Node n;
        Node retVal = null;
        boolean isCorrect = false;
        for (int i = 0; i < nodes.getLength(); i++) {
            n = nodes.item(i);
            if (TAG_NAME.equals(n.getNodeName())) {
                isCorrect = name.equals(n.getTextContent());
                if (retVal != null) {
                    return retVal;
                }
            } else if (property.equals(n.getNodeName())) {
                retVal = n;
                if (isCorrect) {
                    return retVal;
                }
            }
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
            Set<String> properties = model.getAllPropertyIDs() ;
            for (String p : properties) {
                rule = getRuleForProperty(p, widgetType);
                if (rule == ValidationRule.RW) {
                    //nothing to do in the case of read/write properties
                    continue;
                } else if (rule == ValidationRule.RO) {
                    //read-only properties must have identical values, otherwise it is a failure
                    numberOfROProperties++;
                    modelVal = model.getPropertyValue(p);
                    orgVal = original.getPropertyValue(p);
                    if (!Objects.equals(modelVal, orgVal)) {
                        //the failure is always critical, except for fonts and colors if a predefined value was used
                        boolean critical = !isPropertyDefined(modelVal);
                        failures.add(new ValidationFailure(pathToFile, widgetType, 
                            model.getName(), p, orgVal, modelVal, rule, critical));
                        if (critical) {
                            numberOfCriticalROFailures++;
                        } else {
                            numberOfMajorROFailures++;
                        }
                    }
                } else if (rule == ValidationRule.WRITE) {
                    //write properties must be different and non null
                    numberOfWRITEProperties++;
                    modelVal = model.getPropertyValue(p);
                    orgVal = original.getPropertyValue(p);
                    if (Objects.equals(modelVal, orgVal) || modelVal == null) {
                        failures.add(new ValidationFailure(pathToFile, widgetType, 
                            model.getName(), p, orgVal, modelVal, rule, false));
                        numberOfWRITEFailures++;
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
    
    /**
     * Checks the property matches one of the predefined colors or fonts. If yes it returns true otherwise it returns false.
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
     * Loads the validation rule from the rules map. First the rule for the property of the specified widget 
     * (as widget.property) is being loaded. If it is defined it is returned. If it does not exist the widget
     * is trimmed of any prefixes (e.g. org.csstudio.opibuilder.widgets) and the property for the trimmed
     * widget is loaded. If it exist it is returned otherwise a general property rule definition is searched 
     * for. If none is found the property is of read/write type. 
     * 
     * @param property the name of the property
     * @param widget the type of the widget
     * @return the rule for the property
     */
    private ValidationRule getRuleForProperty(String property, String widget) {
        String prop = widget + "." + property;
        ValidationRule value = rules.get(prop);
        if (value == null) {
            //try with abbreviated widget - omit org.csstudio.opibuilder.widgets
            prop = widget.substring(widget.lastIndexOf('.')+1) + "." + property;
            value = rules.get(prop);
        }
        if (value == null ) {
            value = rules.get(property);
        }
        if (value == null) {
            return ValidationRule.RW;
        } else {
            return value;
        }        
    }
        
    /**
     * Loads the schema from the given path and stores data into a map, where the keys are the widget IDs and
     * the values are the widget models.
     * 
     * @param path the path to the schema
     * @return a map containing all elements defined in the schema
     * @throws IOException if there was an error reading the schema
     */
    private static Map<String,AbstractWidgetModel> loadSchema(IPath path) throws IOException {
        try (InputStream inputStream = ResourceUtil.pathToInputStream(path, false)) {
            DisplayModel displayModel = new DisplayModel(path);
            XMLUtil.fillDisplayModelFromInputStream(inputStream, displayModel, Display.getDefault());
    
            Map<String, AbstractWidgetModel> map = new HashMap<>();
            map.put(displayModel.getTypeID(), displayModel);
            loadModelFromContainer(displayModel,map);
            if (!displayModel.getConnectionList().isEmpty()) {
                map.put(ConnectionModel.ID, displayModel.getConnectionList().get(0));
            }
            return map;
        } catch (Exception e) {
            throw new IOException("Unable to load the OPI from " + path.toOSString() + ".",e);
        }
    }

    private static void loadModelFromContainer(AbstractContainerModel containerModel,
            Map<String, AbstractWidgetModel> map) {
        for (AbstractWidgetModel model : containerModel.getChildren()) {
            if (!map.containsKey(model.getTypeID())) {
                map.put(model.getTypeID(), model);
            }
            if (model instanceof AbstractContainerModel) {
                loadModelFromContainer((AbstractContainerModel) model, map);
            }
        }
    }
    
    /**
     * Fix the given validation failures. All failures are expected to belong to the same OPI file. The fix replaces
     * the actual value of the validated property with the expected value.
     * 
     * @param failureToFix the validation failures to fix
     * @throws IOException if there was an exception in reading the OPI
     * @throws IllegalArgumentException if the failures do not belong to the same OPI
     */
    public static void fixOPIFailure(ValidationFailure[] failureToFix) throws IOException, IllegalArgumentException {
        if (failureToFix.length == 0) return;
        IPath path = failureToFix[0].getPath();
        for (ValidationFailure f : failureToFix) {
            if (!f.getPath().equals(path)) {
                throw new IllegalArgumentException("All validation failures must belong to the same path.");
            }
        }
        DisplayModel displayModel = null;
        try (InputStream inputStream = ResourceUtil.pathToInputStream(path, false)) {
            displayModel = new DisplayModel(failureToFix[0].getPath());
            XMLUtil.fillDisplayModelFromInputStream(inputStream, displayModel, Display.getDefault());
        } catch (Exception e) {
            throw new IOException("Could not read the opi " + path.toOSString() +".",e);
        }
        
        for (ValidationFailure f : failureToFix) {
            AbstractWidgetModel model = findWidget(displayModel, f);
            if (model == null) {
                continue;
            }
            model.setPropertyValue(f.getProperty(), f.getExpectedValue());
        }
        
        try (FileOutputStream output = new FileOutputStream(path.toFile())) {
            XMLUtil.widgetToOutputStream(displayModel, output, true);
        }        
    }
    
   
    /**
     * Finds the widget model that matches the validation failure.
     * 
     * @param parent the parent to look for the widget model in
     * @param failure the failure to match
     * @return the widget model if found, or null if match was not found
     */
    private static AbstractWidgetModel findWidget(AbstractContainerModel parent, ValidationFailure failure) {
        String widgetType;
        String widgetName;
        for (AbstractWidgetModel model : parent.getChildren()) {
            widgetType = model.getTypeID();
            widgetName = model.getName();
            if (widgetType.equals(failure.getWidgetType()) && widgetName.equals(failure.getWidgetName())) {
                Object obj = model.getPropertyValue(failure.getProperty());
                if (obj.equals(failure.getActualValue())) {
                    return model;
                }
            }
            if (model instanceof AbstractContainerModel) {
                AbstractWidgetModel result = findWidget((AbstractContainerModel)model, failure);
                if (result != null) return result;
            }
        }  
        return null;
    }
    

    /**
     * Creates a document that contains the line numbers of each node.
     * 
     * @param is the input stream
     * @return the document
     * 
     * @throws IOException 
     * @throws SAXException
     */
    private static Document readXMLWithLineNumbers(final InputStream is) throws IOException, SAXException {
        final Document doc;
        final SAXParser parser;
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            parser = factory.newSAXParser();
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            doc = docBuilder.newDocument();
        } catch (final ParserConfigurationException e) {
            throw new RuntimeException("Can't create SAX parser / DOM builder.", e);
        }

        Stack<Element> elementStack = new Stack<Element>();
        StringBuilder textBuffer = new StringBuilder();
        DefaultHandler handler = new DefaultHandler() {
            private Locator locator;

            @Override
            public void setDocumentLocator(final Locator locator) {
                this.locator = locator; 
            }

            @Override
            public void startElement(final String uri, final String localName, final String qName, 
                    final Attributes attributes) throws SAXException {
                addTextIfNeeded();
                final Element el = doc.createElement(qName);
                for (int i = 0; i < attributes.getLength(); i++) {
                    el.setAttribute(attributes.getQName(i), attributes.getValue(i));
                }
                el.setUserData(LINE_NUMBER_KEY_NAME, Integer.valueOf(this.locator.getLineNumber()), null);
                elementStack.push(el);
            }

            @Override
            public void endElement(final String uri, final String localName, final String qName) {
                addTextIfNeeded();
                final Element closedEl = elementStack.pop();
                if (elementStack.isEmpty()) { // Is this the root element?
                    doc.appendChild(closedEl);
                } else {
                    elementStack.peek().appendChild(closedEl);
                }
            }

            @Override
            public void characters(final char ch[], final int start, final int length) throws SAXException {
                textBuffer.append(ch, start, length);
            }

            private void addTextIfNeeded() {
                if (textBuffer.length() > 0) {
                    Element el = elementStack.peek();
                    Node textNode = doc.createTextNode(textBuffer.toString());
                    el.appendChild(textNode);
                    textBuffer.delete(0, textBuffer.length());
                }
            }
        };
        parser.parse(is, handler);

        return doc;
    }
}
