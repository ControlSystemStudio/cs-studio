/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.persistence;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import javax.security.auth.login.FailedLoginException;

import org.csstudio.apputil.macros.MacroUtil;
import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractLinkingContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.ConnectionModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.persistence.LineAwareXMLParser.LineAwareElement;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.csstudio.opibuilder.util.MacrosInput;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.util.SingleSourceHelper;
import org.csstudio.opibuilder.util.StyleSheetService;
import org.csstudio.opibuilder.util.WidgetDescriptor;
import org.csstudio.opibuilder.util.WidgetsService;
import org.eclipse.core.runtime.IPath;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.osgi.framework.Version;

/**The utility class for XML related operation.
 * @author Xihui Chen
 *
 */
public class XMLUtil {

    public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"; //$NON-NLS-1$

    public static String XMLTAG_DISPLAY = "display"; //$NON-NLS-1$

    public static String XMLTAG_WIDGET = "widget"; //$NON-NLS-1$

    public static String XMLTAG_CONNECTION = "connection"; //$NON-NLS-1$

    public static Set<String> WIDGET_TAGS =
            new HashSet<String>(Arrays.asList(XMLTAG_DISPLAY, XMLTAG_WIDGET, XMLTAG_CONNECTION));

    public static String XMLATTR_TYPEID = "typeId"; //$NON-NLS-1$

    public static String XMLATTR_PROPID = "id"; //$NON-NLS-1$
    public static String XMLATTR_VERSION = "version"; //$NON-NLS-1$

    public static String XMLTAG_WIDGET_UID = AbstractWidgetModel.PROP_WIDGET_UID; //$NON-NLS-1$
    public static String XMLTAG_OPI_FILE = AbstractLinkingContainerModel.PROP_OPI_FILE;

    /**Flatten a widget to XML element.
     * @param widgetModel model of the widget
     * @return the XML element
     */
    public static Element widgetToXMLElement(AbstractWidgetModel widgetModel){

        Element result = new Element((widgetModel instanceof DisplayModel) ? XMLTAG_DISPLAY :
            (widgetModel instanceof ConnectionModel) ? XMLTAG_CONNECTION : XMLTAG_WIDGET);
        result.setAttribute(XMLATTR_TYPEID, widgetModel.getTypeID());
        result.setAttribute(XMLATTR_VERSION, widgetModel.getVersion().toString());
        List<String> propIds = new ArrayList<>(widgetModel.getAllPropertyIDs());
        Collections.sort(propIds);
        StyleSheetService styleService = StyleSheetService.getInstance();
        for(String propId : propIds){
            if(widgetModel.getProperty(propId).isSavable()
                && !styleService.isPropertyHandledByWidgetClass(widgetModel, propId)){
                Element propElement = new Element(propId);
                widgetModel.getProperty(propId).writeToXML(propElement);
                result.addContent(propElement);
            }
        }

        if(widgetModel instanceof AbstractContainerModel && !(widgetModel instanceof AbstractLinkingContainerModel)){
            AbstractContainerModel containerModel = (AbstractContainerModel)widgetModel;
            for(AbstractWidgetModel child : containerModel.getChildren()){
                result.addContent(widgetToXMLElement(child));
            }
        }

        //convert connections on this displayModel to xml element
        if(widgetModel instanceof DisplayModel &&
                ((DisplayModel)widgetModel).getConnectionList() != null){
            for(ConnectionModel connectionModel : ((DisplayModel)widgetModel).getConnectionList()){
                if(!connectionModel.isLoadedFromLinkedOpi()) {
                    Element connElement = widgetToXMLElement(connectionModel);
                    result.addContent(connElement);
                }
            }
        }

        return result;
    }

    /**
     * Create and configure an XMLOutputter object.
     * @param prettyFormat
     * @return the XMLOutputter
     */
    private static XMLOutputter getXMLOutputter(boolean prettyFormat) {
        Format format = Format.getRawFormat();
        if(prettyFormat)
            format.setIndent("  "); //$NON-NLS-1$
        // Always use Unix-style line endings.
        format.setLineSeparator("\n");
        XMLOutputter xmlOutputter = new XMLOutputter();
        xmlOutputter.setFormat(format);
        return xmlOutputter;
    }

    /**Flatten a widget to XML String.
     * @param widgetModel model of the widget.
     * @param prettyFormat true if the string is in pretty format
     * @return the XML String
     */
    public static String widgetToXMLString(AbstractWidgetModel widgetModel, boolean prettyFormat) {
        XMLOutputter xmlOutputter = getXMLOutputter(prettyFormat);
        return xmlOutputter.outputString(widgetToXMLElement(widgetModel));
    }

    /**Write widget to an output stream.
     * @param widgetModel model of the widget
     * @param out output stream
     * @param prettyFormat true if in pretty format
     * @throws IOException
     */
    public static void widgetToOutputStream(AbstractWidgetModel widgetModel, OutputStream out, boolean prettyFormat) throws IOException{
        XMLOutputter xmlOutputter = getXMLOutputter(prettyFormat);
        out.write(XML_HEADER.getBytes("UTF-8")); //$NON-NLS-1$
        xmlOutputter.output(widgetToXMLElement(widgetModel), out);
    }


    /**Convert an XML String to widget model
     * @param xmlString
     * @return the widget model
     * @throws Exception
     */
    public static AbstractWidgetModel XMLStringToWidget(String xmlString) throws Exception{
        return XMLElementToWidget(stringToXML(xmlString));
    }

    /**Convert an XML element to widget.
     * @param element the element
     * @return model of the widget.
     * @throws Exception
     */
    public static AbstractWidgetModel XMLElementToWidget(Element element) throws Exception {
        return XMLElementToWidget(element, null);
    }

    /**Fill the DisplayModel from an OPI file inputstream
     * @param inputStream the inputstream will be closed in this method before return.
     * @param displayModel. The {@link DisplayModel} to be filled.
     * @param display the display in UI Thread.
     * @throws Exception
     */
    public static void fillDisplayModelFromInputStream(
            final InputStream inputStream, final DisplayModel displayModel, Display display) throws Exception{
        fillDisplayModelFromInputStreamSub(inputStream, displayModel, display, new ArrayList<IPath>());
    }

    private static void fillDisplayModelFromInputStreamSub(
            final InputStream inputStream, final DisplayModel displayModel, Display display, List<IPath> trace) throws Exception{

        if(display == null){
            display = Display.getCurrent();
        }
        IPath opiPath = displayModel.getOpiFilePath();
        if (OPIBuilderPlugin.isRAP() && opiPath != null
                && !SingleSourceHelper.rapIsLoggedIn(display)) {
            //check secured opi paths
            String[] securedPaths = PreferencesHelper.getSecuredOpiPaths();
            if (securedPaths != null){
                for(String securedPath : securedPaths){
                    if(opiPath.toString().startsWith(securedPath)) {
                        if (!SingleSourceHelper.rapAuthenticate(display)) {
                            inputStream.close();
                            throw new FailedLoginException();
                        }
                    }
                }
            }
            //check unsecured opi paths
            if(PreferencesHelper.isWholeSiteSecured()){
                String[] unSecuredPaths = PreferencesHelper.getUnSecuredOpiPaths();
                if (unSecuredPaths != null){
                    boolean shouldBeSecured=true;
                    for(String unSecuredPath : unSecuredPaths){
                        if(opiPath.toString().startsWith(unSecuredPath)) {
                            shouldBeSecured=false;
                            break;
                        }
                    }
                    if(shouldBeSecured){
                        if (!SingleSourceHelper.rapAuthenticate(display)) {
                            inputStream.close();
                            throw new FailedLoginException();
                        }
                    }
                }
            }

        }

        Element root = inputStreamToXML(inputStream);
        if(root != null){
             XMLElementToWidgetSub(root, displayModel, trace);

             //check version
             if(compareVersion(displayModel.getBOYVersion(),
                     OPIBuilderPlugin.getDefault().getBundle().getVersion()) > 0){
                final String message = displayModel.getOpiFilePath() == null ? "This OPI"
                        : displayModel.getOpiFilePath().lastSegment()
                                + " was created in a newer version of BOY ("
                                + displayModel.getBOYVersion().toString()
                                + "). It may not function properly! "
                                + "Please update your " +
                                (OPIBuilderPlugin.isRAP()? "WebOPI":"BOY")
                                + " (" + OPIBuilderPlugin.getDefault().getBundle().getVersion() +
                                ") to the latest version.";
                if(display == null){
                    display = Display.getDefault();
                }
                if (display != null)
                    display.asyncExec(new Runnable() {
                        @Override
                        public void run() {
//                            MessageDialog.openWarning(null, "Warning", message);
                            ConsoleService.getInstance().writeWarning(message);
                            OPIBuilderPlugin.getLogger().log(Level.WARNING,
                                    message); //$NON-NLS-1$
                        }
                    });
             }

        }
        inputStream.close();
    }

    /**Fill the DisplayModel from an OPI file inputstream. In RAP, it must be called in UI Thread.
     * @param inputStream the inputstream will be closed in this method before return.
     * @param displayModel
     * @throws Exception
     */
    public static void fillDisplayModelFromInputStream(
            final InputStream inputStream, final DisplayModel displayModel) throws Exception {
        fillDisplayModelFromInputStream(inputStream, displayModel, null);
    }


    /**Construct widget model from XML element. Sometimes it includes filling LinkingContainer and/or construct Connection model between widgets.
     * @param element
     * @param displayModel the root display model. If root of the element is a display, use this display model as root model
     * instead of creating a new one. If this is null, a new one will be created.
     * @return the root widget model
     * @throws Exception
     */
    public static AbstractWidgetModel XMLElementToWidget(Element element, DisplayModel displayModel) throws Exception{
        return XMLElementToWidgetSub(element, displayModel, new ArrayList<IPath> ());
    }

    private static AbstractWidgetModel XMLElementToWidgetSub(Element element, DisplayModel displayModel, List<IPath> trace) throws Exception{
        if(element == null) return null;

        AbstractWidgetModel result = null;

        if(WIDGET_TAGS.contains(element.getName())) {
            result = fillWidgets(element, displayModel);

            if(result instanceof AbstractContainerModel)
                fillLinkingContainersSub((AbstractContainerModel)result, trace);
            fillConnections(element, displayModel);

            return result;
        } else {
            String errorMessage = "Unknown Tag: " + element.getName();
            ConsoleService.getInstance().writeError(errorMessage);
            return null;
        }
    }

    /**Convert XML String to a widget model.
     * @param xmlString
     * @param displayModel the root display model. If root of the element is a display, use this display model as root model
     * instead of creating a new one. If this is null, a new one will be created.
     * @throws Exception
     */
    public static AbstractWidgetModel fillWidgetsFromXMLString(String xmlString, DisplayModel displayModel) throws Exception {
        return fillWidgets(stringToXML(xmlString), displayModel);
    }

    /**Convert XML Element to a widget model.
     * @param element
     * @param displayModel the root display model. If root of the element is a display, use this display model as root model
     * instead of creating a new one. If this is null, a new one will be created.
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static AbstractWidgetModel fillWidgets(Element element, DisplayModel displayModel) throws Exception{
        if(element == null) return null;

        AbstractWidgetModel rootWidgetModel = null;

        //Determine root widget model
        if(element.getName().equals(XMLTAG_DISPLAY)){
            if(displayModel != null)
                rootWidgetModel =displayModel;
            else
                rootWidgetModel = new DisplayModel(null);
        }else if(element.getName().equals(XMLTAG_WIDGET)){
            String typeId = element.getAttributeValue(XMLATTR_TYPEID);
            WidgetDescriptor desc = WidgetsService.getInstance().getWidgetDescriptor(typeId);
            if(desc != null)
                rootWidgetModel = desc.getWidgetModel();
            if(rootWidgetModel == null){
                String errorMessage = NLS.bind("Fail to load the widget: {0}\n " +
                    "The widget may not exist, as a consequence, the widget will be ignored.", typeId);
                ErrorHandlerUtil.handleError(errorMessage, new Exception("Widget does not exist."));
                return null;
            }
        }else if(element.getName().equals(XMLTAG_CONNECTION)){
            rootWidgetModel = new ConnectionModel(displayModel);
        }else {
            String errorMessage = "Unknown Tag: " + element.getName();
            ConsoleService.getInstance().writeError(errorMessage);
            return null;
        }

        setPropertiesFromXML(element, rootWidgetModel);

        if(rootWidgetModel instanceof AbstractContainerModel) {
            AbstractContainerModel container = (AbstractContainerModel)rootWidgetModel;
            List children = element.getChildren();
            Iterator iterator = children.iterator();
            while (iterator.hasNext()) {
                Element subElement = (Element) iterator.next();
                if(subElement.getName().equals(XMLTAG_WIDGET))
                    container.addChild(fillWidgets(subElement, displayModel));
            }
        }


        if(displayModel !=null)
            rootWidgetModel.processVersionDifference(displayModel.getBOYVersion());

        return rootWidgetModel;
    }

    /**
     * Fill all LinkingContainers under the model.
     *
     * @param container LinkingContainer to be filled.
     * @throws Exception
     */
    public static void fillLinkingContainers(AbstractContainerModel container) throws Exception {
        fillLinkingContainersSub(container, new ArrayList<IPath>());
    }

    private static void fillLinkingContainersSub(AbstractContainerModel container, List<IPath> trace) throws Exception{
        if(container instanceof AbstractLinkingContainerModel) {
            AbstractLinkingContainerModel linkingContainer = (AbstractLinkingContainerModel)container;
            List<IPath> tempTrace = new ArrayList<IPath>();
            tempTrace.addAll(trace);
            fillLinkingContainerSub(linkingContainer, tempTrace);
        }

        for(AbstractWidgetModel w : container.getAllDescendants()) {
            if(w instanceof AbstractLinkingContainerModel) {
                AbstractLinkingContainerModel linkingContainer = (AbstractLinkingContainerModel)w;
                List<IPath> tempTrace = new ArrayList<IPath>();
                tempTrace.addAll(trace);
                fillLinkingContainerSub(linkingContainer, tempTrace);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private static void fillConnections(Element element, DisplayModel displayModel) throws Exception {
        if(element.getName().equals(XMLTAG_CONNECTION)) {
            ConnectionModel result = new ConnectionModel(displayModel);
            setPropertiesFromXML(element, result);
        } else if(element.getName().equals(XMLTAG_DISPLAY)){
            List children = element.getChildren();
            Iterator iterator = children.iterator();
            while (iterator.hasNext()) {
                Element subElement = (Element) iterator.next();
                if(subElement.getName().equals(XMLTAG_CONNECTION))  {
                    fillConnections(subElement, displayModel);
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private static void setPropertiesFromXML(Element element, AbstractWidgetModel model) {
        if(model == null || element == null) return;

        String versionOnFile = element.getAttributeValue(XMLATTR_VERSION);
        model.setVersionOnFile(Version.parseVersion(versionOnFile));

        if (element instanceof LineAwareElement) {
            model.setLineNumber(((LineAwareElement) element).getLineNumber());
        }

        List children = element.getChildren();
        Iterator iterator = children.iterator();
        Set<String> propIdSet = model.getAllPropertyIDs();
        Element widgetClassProperty = null;
        while (iterator.hasNext()) {
            Element subElement = (Element) iterator.next();
            //handle property
            if(propIdSet.contains(subElement.getName())){
                String propId = subElement.getName();
                if (AbstractWidgetModel.PROP_WIDGET_CLASS.equals(propId)) {
                    widgetClassProperty = subElement;
                } else {
                    setProperty(subElement, propId, model);
                }
            }
        }
        //set the widget class last to override other properties
        if (widgetClassProperty != null) {
            setProperty(widgetClassProperty, AbstractWidgetModel.PROP_WIDGET_CLASS, model);
        }
    }

    private static void setProperty(Element element, String propId, AbstractWidgetModel model) {
        try {
            model.setPropertyValue(propId,
                    model.getProperty(propId).readValueFromXML(element));
        } catch (Exception e) {
            String errorMessage = "Failed to read the " + propId + " property for " + model.getName() +". " +
                    "The default property value will be setted instead. \n" + e;
            //MessageDialog.openError(null, "OPI File format error", errorMessage + "\n" + e.getMessage());
            OPIBuilderPlugin.getLogger().log(Level.WARNING, errorMessage, e);
            ConsoleService.getInstance().writeWarning(errorMessage);
        }
    }

    /**
     * Load opi file attached to LinkingContainer widget.
     *
     * @param container LinkingContainer to be filled.
     * @throws Exception
     */
    public static void fillLinkingContainer(final AbstractLinkingContainerModel container)
            throws Exception {
        fillLinkingContainerSub(container, new ArrayList<IPath>());
    }

    private static Map<String,String> buildMacroMap(AbstractContainerModel model) {
        Map<String,String> macros = new HashMap<>();
        if (model != null) {
            MacrosInput input = model.getMacrosInput();
            if (input.isInclude_parent_macros()) {
                macros.putAll(buildMacroMap(model.getParent()));
            }
            macros.putAll(input.getMacrosMap());
        }
        return macros;
    }

    private static void fillLinkingContainerSub(final AbstractLinkingContainerModel container, List<IPath> trace)
        throws Exception {

        if(container == null) return;

        if(container.getRootDisplayModel() != null &&
                container.getRootDisplayModel().getOpiFilePath() != null) {
            if(trace.contains(container.getRootDisplayModel().getOpiFilePath())) {
                container.setOPIFilePath("");
                throw new Exception("Opi link contains some loops.\n" + trace.toString());
            } else {
                trace.add(container.getRootDisplayModel().getOpiFilePath());
            }

            IPath path = container.getOPIFilePath();
            if(path != null && !path.isEmpty()) {
                final Map<String,String> macroMap = PreferencesHelper.getMacros();
                macroMap.putAll(buildMacroMap(container));
                String resolvedPath = MacroUtil.replaceMacros(path.toString(), s -> macroMap.get(s));
                path = ResourceUtil.getPathFromString(resolvedPath);

                final DisplayModel inside = new DisplayModel(path);
                inside.setDisplayID(container.getRootDisplayModel(false).getDisplayID());

                try
                {
                    fillDisplayModelFromInputStreamSub(ResourceUtil.pathToInputStream(path), inside, Display.getCurrent(), trace);
                }
                catch (Exception ex)
                {
                    OPIBuilderPlugin.getLogger().log(Level.WARNING, "Failed to load LinkingContainer opi_file " + path, ex);
                }

                // mark connection as it is loaded from linked opi
                for(AbstractWidgetModel w : inside.getAllDescendants())
                    for(ConnectionModel conn : w.getSourceConnections())
                        conn.setLoadedFromLinkedOpi(true);

                AbstractContainerModel loadTarget = inside;

                if(!container.getGroupName().trim().equals("")){ //$NON-NLS-1$
                    AbstractWidgetModel group =
                            inside.getChildByName(container.getGroupName());
                    if(group != null && group instanceof AbstractContainerModel){
                        loadTarget = (AbstractContainerModel) group;
                    }
                }

//                container.addChildren(loadTarget.getChildren(), true);

                container.setDisplayModel(inside);
            }
        }
    }


    /**Compare version without comparing qualifier.
     * @param v1
     * @param v2
     * @return
     */
    private static int compareVersion(Version v1, Version v2) {
        if (v2 == v1) {
            return 0;
        }

        int result = v1.getMajor() - v2.getMajor();
        if (result != 0) {
            return result;
        }

        result = v1.getMinor() - v2.getMinor();
        if (result != 0) {
            return result;
        }

        result = v1.getMicro() - v2.getMicro();
        if (result != 0) {
            return result;
        }

        return 0;
    }

    /**
     * Return the wuid of the closest widget to offset char position in input stream
     * @param in the OPI file input stream
     * @param offset the character offset
     * @return String wuid
     * @throws IOException
     */
    public static String findClosestWidgetUid(InputStream in, int offset)
            throws IOException {
        if (in == null) {
            return null;
        }
        StringBuffer out = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        char[] buf = new char[1024];
        for (int len = br.read(buf); len>0; len = br.read(buf)) {
            out.append(buf, 0, len);
        }
        br.close();
        if (offset + XMLUtil.XMLTAG_WIDGET.length() + 2 >= out.length()) {
            // The offset position is too close to the end of file
            // No widget will be found
            return null;
        }
        int widgetElementStart = offset;
        while (widgetElementStart >= 0
                && !matchXMLTag(out, widgetElementStart, XMLUtil.XMLTAG_WIDGET)) {
            widgetElementStart--;
        }
        if (widgetElementStart > 0) {
            // corresponding widget element found
            int wuidAttrStart = widgetElementStart + 1;
            // looking for <wuid> before a <widget> or </widget
            String xmlEndTagWidget = "/" + XMLUtil.XMLTAG_WIDGET;
            while (!matchXMLTag(out, wuidAttrStart, XMLUtil.XMLTAG_WIDGET_UID)
                    && !matchXMLTag(out, wuidAttrStart, XMLUtil.XMLTAG_WIDGET)
                    && !matchXMLTag(out, wuidAttrStart, xmlEndTagWidget)) {
                wuidAttrStart++;
            }
            if (matchXMLTag(out, wuidAttrStart, XMLUtil.XMLTAG_WIDGET_UID)) {
                //  <wuid> found
                wuidAttrStart = out.indexOf(">", wuidAttrStart);
                if (wuidAttrStart >= 0) {
                    wuidAttrStart++;
                    if (wuidAttrStart < out.length()) {
                        int wuidAttrEnd = out.indexOf("</" + XMLTAG_WIDGET_UID,
                                wuidAttrStart);
                        if (wuidAttrEnd >= 0) {
                            return out.substring(wuidAttrStart, wuidAttrEnd);
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Return true if the string starting at offset in sb matches with xmlTag.
     * @param sb StringBuffer
     * @param offset int
     * @param xmlTag String The XML tag name to check without '&lt;' and '&gt;'
     * @return
     */
    private static boolean matchXMLTag(StringBuffer sb, int offset,
            String xmlTag) {
        if (offset >= sb.length()) {
            return false;
        }
        if (sb.charAt(offset) != '<') {
            return false;
        }
        int indexOfSpace = sb.indexOf(" ", offset);
        int indexOfGt = sb.indexOf(">", offset);
        int indexOfEndTag = Integer.MAX_VALUE;
        if (indexOfSpace >= 0) {
            indexOfEndTag = indexOfSpace;
        }
        if (indexOfGt >= 0 && indexOfGt < indexOfEndTag) {
            indexOfEndTag = indexOfGt;
        }
        if (indexOfEndTag == Integer.MAX_VALUE) {
            return false;
        }
        String potentialTag = sb.substring(offset + 1, indexOfEndTag);
        return potentialTag.equals(xmlTag);
    }

    private static Element inputStreamToXML(InputStream stream) throws JDOMException, IOException {
        SAXBuilder saxBuilder = LineAwareXMLParser.createBuilder();
        Document doc = saxBuilder.build(stream);
        Element root = doc.getRootElement();
        return root;
    }

    private static Element stringToXML(String xmlString) throws JDOMException, IOException {
        InputStream stream= new ByteArrayInputStream(xmlString.getBytes("UTF-8")); //$NON-NLS-1$
        return inputStreamToXML(stream);
    }
}
