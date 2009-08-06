package org.csstudio.opibuilder.persistence;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
	
	public static String XMLATTR_TYPEID = "typeId"; //$NON-NLS-1$
	
	public static String XMLATTR_PROPID = "id"; //$NON-NLS-1$
	public static String XMLATTR_VERSION = "version"; //$NON-NLS-1$
	
	private static XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
	

	public static Element WidgetToXMLElement(AbstractWidgetModel widgetModel){
		
		Element result = new Element(widgetModel instanceof DisplayModel ? XMLTAG_DISPLAY : 
			XMLTAG_WIDGET);
		result.setAttribute(XMLATTR_TYPEID, widgetModel.getTypeID());
		result.setAttribute(XMLATTR_VERSION, widgetModel.getVersion());
		for(String propId : widgetModel.getAllPropertyIDs()){
			Element propElement = new Element(propId);
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
	
	public static String WidgetToXMLString(AbstractWidgetModel widgetModel, boolean prettyFormat){
		XMLOutputter xmlOutputter = new XMLOutputter(prettyFormat ? Format.getPrettyFormat() : 
			Format.getRawFormat());
		return xmlOutputter.outputString(WidgetToXMLElement(widgetModel));
	}
	
	public static void WidgetToOutputStream(AbstractWidgetModel widgetModel, OutputStream out, boolean prettyFormat) throws IOException{
		XMLOutputter xmlOutputter = new XMLOutputter(prettyFormat ? Format.getPrettyFormat() : 
			Format.getRawFormat());
		out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n".getBytes());
		xmlOutputter.output(WidgetToXMLElement(widgetModel), out);
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
		Set<String> propIdSet = rootWidgetModel.getAllPropertyIDs();
		while (iterator.hasNext()) {
			Element subElement = (Element) iterator.next();		    
			//handle property
			if(propIdSet.contains(subElement.getName())){
				String propId = subElement.getName();
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
