package org.csstudio.opibuilder.persistence;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.model.DisplayModel;
import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.csstudio.opibuilder.util.WidgetsService;
import org.csstudio.platform.logging.CentralLogger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class XMLUtil {
	
	public static String XMLTAG_DISPLAY = "display"; //$NON-NLS-1$
	
	public static String XMLTAG_WIDGET = "widget"; //$NON-NLS-1$

	public static String XMLTAG_PROPERTY = "property"; //$NON-NLS-1$
	
	public static String XMLATTR_TYPEID = "typeId"; //$NON-NLS-1$
	
	public static String XMLATTR_PROPID = "id";
	
	private static XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
	

	public static Element WidgetToXMLElement(AbstractWidgetModel widgetModel){
		
		Element result = new Element(widgetModel instanceof DisplayModel ? XMLTAG_DISPLAY : 
			XMLTAG_WIDGET);
		result.setAttribute(XMLATTR_TYPEID, widgetModel.getTypeID());
		for(String propId : widgetModel.getAllPropertyIDs()){
			Element propElement = new Element(XMLTAG_PROPERTY);
			propElement.setAttribute(XMLATTR_PROPID, propId);
			widgetModel.getProperty(propId).writeToXML(propElement);
			result.addContent(propElement);
		}
		
		if(widgetModel instanceof AbstractContainerModel){
			AbstractContainerModel containerModel = (AbstractContainerModel)widgetModel;
			for(AbstractWidgetModel child : containerModel.getChildren()){
				result.addContent(WidgetToXMLElement(child));
			}			
		}
		
		return result;
	}
	
	public static String WidgetToXMLString(AbstractWidgetModel widgetModel){
		return ElementToString(WidgetToXMLElement(widgetModel));
	}
	
	
	public static String ElementToString(Element element){
		return xmlOutputter.outputString(element);
	}
	
	
	public static AbstractWidgetModel XMLStringToWidget(String xmlString) throws Exception{
		SAXBuilder saxBuilder = new SAXBuilder();
		Document doc = saxBuilder.build(new ByteArrayInputStream(xmlString.getBytes()));
		Element root = doc.getRootElement();
		return XMLElementToWidget(root);		
	}
	
	private static AbstractWidgetModel XMLElementToWidget(Element element) throws Exception {
		return XMLElementToWidget(element, null);
	}

	public static void fillDisplayModelFromInputStream(
			InputStream inputStream, DisplayModel displayModel) throws Exception{
		SAXBuilder saxBuilder = new SAXBuilder();
		Document doc = saxBuilder.build(inputStream);
		Element root = doc.getRootElement();
		if(root != null)
			 XMLElementToWidget(root, displayModel);
		
	}
	
	
	
	
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
			rootWidgetModel = WidgetsService.getInstance().getWidgetDescriptor(
					element.getAttributeValue(XMLATTR_TYPEID)).getWidgetModel();
		}else
			throw new Exception("The element is not a widget");
		
		
		List children = element.getChildren();
		Iterator iterator = children.iterator();
		while (iterator.hasNext()) {
			Element subElement = (Element) iterator.next();		    
			//handle property
			if(subElement.getName().equals(XMLTAG_PROPERTY)){
				String propId = subElement.getAttributeValue(XMLATTR_PROPID);
				rootWidgetModel.setPropertyValue(propId, 
						rootWidgetModel.getProperty(propId).readValueFromXML(subElement));
			}else if(subElement.getName().equals(XMLTAG_WIDGET)){
				if(rootWidgetModel instanceof AbstractContainerModel){
					((AbstractContainerModel) rootWidgetModel).addChild(
						XMLElementToWidget(subElement));
				}
			}
		}		
		return rootWidgetModel;
	}
	
}
