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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import javax.security.auth.login.FailedLoginException;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.ConnectionModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.preferences.PreferencesHelper;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.ErrorHandlerUtil;
import org.csstudio.opibuilder.util.SingleSourceHelper;
import org.csstudio.opibuilder.util.WidgetDescriptor;
import org.csstudio.opibuilder.util.WidgetsService;
import org.eclipse.core.runtime.IPath;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.jdom.Document;
import org.jdom.Element;
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
	
	public static String XMLATTR_TYPEID = "typeId"; //$NON-NLS-1$

	public static String XMLATTR_PROPID = "id"; //$NON-NLS-1$
	public static String XMLATTR_VERSION = "version"; //$NON-NLS-1$

	public static String XMLTAG_WIDGET_UID = AbstractWidgetModel.PROP_WIDGET_UID; //$NON-NLS-1$
	
	/**Flatten a widget to XML element.
	 * @param widgetModel model of the widget
	 * @return the XML element
	 */
	public static Element widgetToXMLElement(AbstractWidgetModel widgetModel){

		Element result = new Element((widgetModel instanceof DisplayModel) ? XMLTAG_DISPLAY :
			(widgetModel instanceof ConnectionModel) ? XMLTAG_CONNECTION : XMLTAG_WIDGET);
		result.setAttribute(XMLATTR_TYPEID, widgetModel.getTypeID());
		result.setAttribute(XMLATTR_VERSION, widgetModel.getVersion().toString());
		for(String propId : widgetModel.getAllPropertyIDs()){
			if(widgetModel.getProperty(propId).isSavable()){
				Element propElement = new Element(propId);
				widgetModel.getProperty(propId).writeToXML(propElement);
				result.addContent(propElement);
			}
		}

		if(widgetModel instanceof AbstractContainerModel){
			AbstractContainerModel containerModel = (AbstractContainerModel)widgetModel;
			for(AbstractWidgetModel child : containerModel.getChildren()){
				result.addContent(widgetToXMLElement(child));
			}
		}
		
		//convert connections on this displayModel to xml element
		if(widgetModel instanceof DisplayModel && 
				((DisplayModel)widgetModel).getConnectionList() != null){			
			for(ConnectionModel connectionModel : ((DisplayModel)widgetModel).getConnectionList()){
				Element connElement = widgetToXMLElement(connectionModel);
				result.addContent(connElement);
			}		
		}

		return result;
	}

	/**Flatten a widget to XML String.
	 * @param widgetModel model of the widget.
	 * @param prettyFormat true if the string is in pretty format
	 * @return the XML String
	 */
	public static String widgetToXMLString(AbstractWidgetModel widgetModel, boolean prettyFormat){
		Format format = Format.getRawFormat();		
		if(prettyFormat)
			format.setIndent("  "); //$NON-NLS-1$
		XMLOutputter xmlOutputter = new XMLOutputter();
		xmlOutputter.setFormat(format);

		return xmlOutputter.outputString(widgetToXMLElement(widgetModel));
	}

	/**Write widget to an output stream.
	 * @param widgetModel model of the widget
	 * @param out output stream
	 * @param prettyFormat true if in pretty format
	 * @throws IOException
	 */
	public static void widgetToOutputStream(AbstractWidgetModel widgetModel, OutputStream out, boolean prettyFormat) throws IOException{
		Format format = Format.getRawFormat();		
		if(prettyFormat)
			format.setIndent("  "); //$NON-NLS-1$
		XMLOutputter xmlOutputter = new XMLOutputter(format);
		out.write(XML_HEADER.getBytes("UTF-8")); //$NON-NLS-1$
		xmlOutputter.output(widgetToXMLElement(widgetModel), out);
	}


	/**Convert an XML String to widget model
	 * @param xmlString
	 * @return the widget model
	 * @throws Exception
	 */
	public static AbstractWidgetModel XMLStringToWidget(String xmlString) throws Exception{
		SAXBuilder saxBuilder = new SAXBuilder();	
		InputStream stream= new ByteArrayInputStream(xmlString.getBytes("UTF-8")); //$NON-NLS-1$
		Document doc = saxBuilder.build(stream);
		Element root = doc.getRootElement();
		return XMLElementToWidget(root);
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
		
		SAXBuilder saxBuilder = new SAXBuilder();
		Document doc = saxBuilder.build(inputStream);
		Element root = doc.getRootElement();
		if(root != null){
			 XMLElementToWidget(root, displayModel);
			 
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
//							MessageDialog.openWarning(null, "Warning", message);
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


	/**Convert XML Element to a widget model.
	 * @param element
	 * @param displayModel the root display model. If root of the element is a display, use this display model as root model 
	 * instead of creating a new one. If this is null, a new one will be created. 
	 * @return the root widget model
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public static AbstractWidgetModel XMLElementToWidget(Element element, DisplayModel displayModel) throws Exception{
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
		
		String versionOnFile = element.getAttributeValue(XMLATTR_VERSION);
		rootWidgetModel.setVersionOnFile(Version.parseVersion(versionOnFile));
		//fill root widget model
		List children = element.getChildren();
		Iterator iterator = children.iterator();
		Set<String> propIdSet = rootWidgetModel.getAllPropertyIDs();
		while (iterator.hasNext()) {
			Element subElement = (Element) iterator.next();
			//handle property
			if(propIdSet.contains(subElement.getName())){
				String propId = subElement.getName();
				try {				
					rootWidgetModel.setPropertyValue(propId,
							rootWidgetModel.getProperty(propId).readValueFromXML(subElement));
				} catch (Exception e) {
					String errorMessage = "Failed to read the " + propId + " property for " + rootWidgetModel.getName() +". " +
							"The default property value will be setted instead. \n" + e;
					//MessageDialog.openError(null, "OPI File format error", errorMessage + "\n" + e.getMessage());
					OPIBuilderPlugin.getLogger().log(Level.WARNING, errorMessage, e);
					ConsoleService.getInstance().writeWarning(errorMessage);
				}
			}else if(subElement.getName().equals(XMLTAG_WIDGET)){
				if(rootWidgetModel instanceof AbstractContainerModel){
					AbstractWidgetModel child =XMLElementToWidget(subElement, displayModel);
					if(child != null)
						((AbstractContainerModel) rootWidgetModel).addChild(child);
				}
			}else if(subElement.getName().equals(XMLTAG_CONNECTION)){
				//fill connection model, widgetModel will be connected when
				//connection model is created.
				if(rootWidgetModel instanceof DisplayModel){					
						XMLElementToWidget(subElement,(DisplayModel)rootWidgetModel);
				}				
			}
		}
		if(displayModel !=null)
			rootWidgetModel.processVersionDifference(displayModel.getBOYVersion());
		
		return rootWidgetModel;
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

}
