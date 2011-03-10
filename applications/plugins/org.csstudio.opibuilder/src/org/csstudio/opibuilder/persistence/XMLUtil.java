/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.persistence;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.util.ConsoleService;
import org.csstudio.opibuilder.util.WidgetDescriptor;
import org.csstudio.opibuilder.util.WidgetsService;
import org.eclipse.osgi.util.NLS;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**The utility class for XML related operation.
 * @author Xihui Chen
 *
 */
public class XMLUtil {

	public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"; //$NON-NLS-1$

	public static String XMLTAG_DISPLAY = "display"; //$NON-NLS-1$

	public static String XMLTAG_WIDGET = "widget"; //$NON-NLS-1$

	public static String XMLATTR_TYPEID = "typeId"; //$NON-NLS-1$

	public static String XMLATTR_PROPID = "id"; //$NON-NLS-1$
	public static String XMLATTR_VERSION = "version"; //$NON-NLS-1$


	public static Element widgetToXMLElement(AbstractWidgetModel widgetModel){

		Element result = new Element(widgetModel instanceof DisplayModel ? XMLTAG_DISPLAY :
			XMLTAG_WIDGET);
		result.setAttribute(XMLATTR_TYPEID, widgetModel.getTypeID());
		result.setAttribute(XMLATTR_VERSION, widgetModel.getVersion());
		for(String propId : widgetModel.getAllPropertyIDs()){
			if(widgetModel.getProperty(propId).isVisibleInPropSheet()){
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

		return result;
	}

	public static String widgetToXMLString(AbstractWidgetModel widgetModel, boolean prettyFormat){
		XMLOutputter xmlOutputter = new XMLOutputter(prettyFormat ? Format.getPrettyFormat() :
			Format.getRawFormat());
		return xmlOutputter.outputString(widgetToXMLElement(widgetModel));
	}

	public static void widgetToOutputStream(AbstractWidgetModel widgetModel, OutputStream out, boolean prettyFormat) throws IOException{
		XMLOutputter xmlOutputter = new XMLOutputter(prettyFormat ? Format.getPrettyFormat() :
			Format.getRawFormat());
		out.write(XML_HEADER.getBytes());
		xmlOutputter.output(widgetToXMLElement(widgetModel), out);
	}


	public static AbstractWidgetModel XMLStringToWidget(String xmlString) throws Exception{
		SAXBuilder saxBuilder = new SAXBuilder();
		Document doc = saxBuilder.build(new ByteArrayInputStream(xmlString.getBytes()));
		Element root = doc.getRootElement();
		return XMLElementToWidget(root);
	}

	public static AbstractWidgetModel XMLElementToWidget(Element element) throws Exception {
		return XMLElementToWidget(element, null);
	}

	/**fill the DisplayModel from an OPI file inputstream
	 * @param inputStream the inputstream will be closed in this method before return.
	 * @param displayModel
	 * @throws Exception
	 */
	public static void fillDisplayModelFromInputStream(
			InputStream inputStream, DisplayModel displayModel) throws Exception{
		SAXBuilder saxBuilder = new SAXBuilder();
		Document doc = saxBuilder.build(inputStream);
		Element root = doc.getRootElement();
		if(root != null)
			 XMLElementToWidget(root, displayModel);
		inputStream.close();
	}




	/**
	 * @param element
	 * @param displayModel
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static AbstractWidgetModel XMLElementToWidget(Element element, DisplayModel displayModel) throws Exception{
		AbstractWidgetModel rootWidgetModel = null;
		if(element.getName().equals(XMLTAG_DISPLAY)){
			if(displayModel != null)
				rootWidgetModel =displayModel;
			else
				rootWidgetModel = new DisplayModel();
		}
		else if(element.getName().equals(XMLTAG_WIDGET)){
			String typeId = element.getAttributeValue(XMLATTR_TYPEID);
			WidgetDescriptor desc = WidgetsService.getInstance().getWidgetDescriptor(typeId);
			if(desc != null)
				rootWidgetModel = desc.getWidgetModel();
			if(rootWidgetModel == null){
				String errorMessage = NLS.bind("Fail to load the widget: {0}\n " +
					"The widget may not exist, as a consequnce, the widget will be ignored.", typeId);
				ConsoleService.getInstance().writeError(errorMessage);
				return null;
			}
		}else {
			String errorMessage = "Unknown Tag: " + element.getName();
			ConsoleService.getInstance().writeError(errorMessage);
			return null;
		}


			//throw new Exception("The element is not a widget");


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
					AbstractWidgetModel child =XMLElementToWidget(subElement);
					if(child != null)
						((AbstractContainerModel) rootWidgetModel).addChild(child);
				}
			}else {
				//String warningMessage = subElement.getName() + " cannot be recogonized as a property or widget by the OPI file parser. " +
				//		"It will be ignored as a consequence.";
				//MessageDialog.openWarning(null, "OPI File format warning", warningMessage);
			}
		}
		return rootWidgetModel;
	}

}
