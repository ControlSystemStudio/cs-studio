package org.csstudio.platform.internal.xmlprefs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * This store is used to read and save plugin-specific properties to an XML-file. The XML-File
 * contains 2 levels of property-tags: The first level is specified by the id of the plugin, the
 * second level contain the different properties used by that plugin. The root element will be
 * "csstudio". For example: <csstudio> <org.csstudio.platform.ui.pvtable> <property name="someName"
 * value="someValue" default="someValue" /> <property name="someOtherName" value="someOtherValue" />
 * </org.csstudio.platform.ui.pvtable> </csstudio>
 * 
 * @author Andre Grunow
 * @version 0.1
 */
public class XMLStore
{
	// --------------------------------------------------------------------------------------------

	private static XMLStore instance;
	
	// --------------------------------------------------------------------------------------------

	/**	This file contains all css preferences. */
	private File configFile;

	// --------------------------------------------------------------------------------------------

	private XMLStore()
	{
		try
		{
			configFile = CssConfigFileHandler.getInstance().getConfigFile();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// --------------------------------------------------------------------------------------------

	/**
	 * Setes the default property for the specified plugin and its preference.
	 * 
	 * @param pluginId
	 * @param propertyName
	 * @param defaultValue
	 */
	public void setDefaultProperty(String pluginId, String propertyName, String defaultValue)
	{
		saveProperty(pluginId, propertyName, defaultValue, true);
	}

	// --------------------------------------------------------------------------------------------

	public void setProperty(String pluginId, String propertyName, String propertyValue)
	{
		saveProperty(pluginId, propertyName, propertyValue, false);
	}

	// --------------------------------------------------------------------------------------------

	/**
	 * Adds the specified property to the tag of the specified pluginId. If the tag with the given
	 * PluginID does not exist, it will be automatically created with the given value as
	 * defaultValue. If the specified property is already set, then this property will be
	 * overridden.
	 * 
	 * @param pluginId
	 * @param propertyName
	 * @param value
	 * @require pluginId != null && ! pluginId.equals("")
	 * @require propertyName != null && ! propertyName.equals("")
	 */
	private void saveProperty(String pluginId, String propertyName, String value,
			boolean isDefaultValue)
	{
		assert pluginId != null : "Precondition violation: pluginId != null";
		assert !pluginId.equals("") : "Precondition violation: ! pluginId.equals(\"\")";
		assert propertyName != null : "Precondition violation: propertyName != null";
		assert !propertyName.equals("") : "Precondition violation: ! propertyName.equals(\"\")";

		if (value == null) value = "";

		try
		{
			Document document = loadDocument();
			Element pluginIdElement = document.getRootElement().getChild(pluginId);

			// Did the plugin already specify any settings? Then the corresponding tag exists.
			// Otherwise it should be created.

			if (pluginIdElement == null) pluginIdElement = createPluginTag(document, pluginId);

			// Check, if the specified property already exists for this plugin. If it exists,
			// it will be overridden.

			Element propertyElement = getPropertyElement(pluginIdElement, propertyName);

			// if element does not exist, create it

			if (propertyElement == null)
			{
				propertyElement = new Element("property");
				pluginIdElement.addContent(propertyElement);
			}

			// set the specified name and value

			propertyElement.setAttribute("name", propertyName);

			if (isDefaultValue)
				propertyElement.setAttribute("default", value);

			else
				propertyElement.setAttribute("value", value);

			saveDocument(document);
		}

		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// --------------------------------------------------------------------------------------------

	/**
	 * Returns the value of the specified property of the given pluginId.
	 * 
	 * @param pluginId the id of the plugin, that property belongs to
	 * @param propertyName the property, which value is requested
	 * @param isDefaultValue if true, the default value of the specified property is returned,
	 *            otherwise the current value
	 * @return the value, if the pluginId and the property exist, otherwise an empty String
	 * @require pluginId != null && ! pluginId.equals("")
	 */
	public String getPropertyValue(String pluginId, String propertyName, boolean isDefaultValue)
	{
		assert pluginId != null : "Precondition violation: pluginId != null";
		assert !pluginId.equals("") : "Precondition violation: ! pluginId.equals(\"\")";

		String result = "";

		try
		{
			Document document = loadDocument();
			Element pluginIdElement = document.getRootElement().getChild(pluginId);

			// does the pluginId-tag exist?

			if (pluginIdElement != null)
			{
				Element element = getPropertyElement(pluginIdElement, propertyName);

				// does the specified element of the property exist?

				if (element != null)
				{
					if (isDefaultValue)
						result = element.getAttributeValue("default");

					else if (element.getAttributeValue("value") != null
							&& !element.getAttributeValue("value").equals(""))
						result = element.getAttributeValue("value");

					else
						result = element.getAttributeValue("default");
				}
			}
		}

		catch (Exception e)
		{
			e.printStackTrace();
		}

		return result;
	}

	// --------------------------------------------------------------------------------------------

	/**
	 * Remove the specified property from the pluginId-properties.
	 * 
	 * @param pluginId the id of the plugin which property shall be removed
	 * @param propertyName the property to be removed
	 * @require pluginId != null && ! pluginId.equals("")
	 */
	public void removeProperty(String pluginId, String propertyName)
	{
		assert pluginId != null : "Precondition violation: pluginId != null";
		assert !pluginId.equals("") : "Precondition violation: ! pluginId.equals(\"\")";

		try
		{
			Document document = loadDocument();

			Element pluginIdElement = document.getRootElement().getChild(pluginId);

			// Check, if this pluginId already has a corresponding tag.

			if (pluginIdElement != null)
			{
				Element propertyElement = getPropertyElement(pluginIdElement, propertyName);

				// Check, if the property, which shall be removed, really exists.

				if (propertyElement != null)
				{
					pluginIdElement.removeContent(propertyElement);
					saveDocument(document);
				}
			}
		}

		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// --------------------------------------------------------------------------------------------

	/**
	 * Creates a tag with the specified name.
	 * 
	 * @param document the document, which shall contain the new element
	 * @param pluginId the id of the plugin, which want to specifiy properties
	 * @throws JDOMException
	 * @throws IOException
	 * @return the new element which contains that id as name
	 * @require pluginId != null && ! pluginId.equals("")
	 */
	private Element createPluginTag(Document document, String pluginId) throws JDOMException,
			IOException
	{
		assert pluginId != null : "Precondition violation: pluginId != null";
		assert !pluginId.equals("") : "Precondition violation: ! pluginId.equals(\"\")";

		Element pluginIdElement = new Element(pluginId);
		document.getRootElement().addContent(pluginIdElement);
		return pluginIdElement;
	}

	// --------------------------------------------------------------------------------------------

	/**
	 * Loads the document with the config properties.
	 * 
	 * @throws JDOMException
	 * @throws IOException
	 * @return the document that contains all specified properties
	 * @ensure document != null
	 */
	private Document loadDocument() throws JDOMException, IOException
	{
		SAXBuilder builder = new SAXBuilder();
		Document document = builder.build(configFile);

		assert document != null : "Postcondition violation: document != null";

		return document;
	}

	// --------------------------------------------------------------------------------------------

	/**
	 * Saves the Document to the default file.
	 * 
	 * @param document
	 * @throws JDOMException
	 * @throws IOException
	 * @require document != null
	 */
	private void saveDocument(Document document) throws JDOMException, IOException
	{
		assert document != null : "Precondition violation: document != null";

		FileOutputStream out = new FileOutputStream(configFile);
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		outputter.output(document, out);
		out.flush();
		out.close();
	}

	// --------------------------------------------------------------------------------------------

	/**
	 * Returns the Element that contains the given propertyName as name.
	 * 
	 * @param pluginIdElement the Element (the tag) of the surrounding plugin-tag
	 * @param propertyName the name of the property, of which the element is request
	 * @return the element, if it could be found, otherwise null.
	 * @require pluginIdElement != null
	 * @require propertyName != null && ! propertyName.equals("")
	 */
	private Element getPropertyElement(Element pluginIdElement, String propertyName)
	{
		assert pluginIdElement != null : "Precondition violation: pluginIdElement != null";
		assert propertyName != null : "Precondition violation: propertyName != null";
		assert !propertyName.equals("") : "Precondition violation: ! propertyName.equals(\"\")";

		Element result = null;

		List<Element> list = pluginIdElement.getChildren();

		for (Iterator<Element> iter = list.iterator(); iter.hasNext();)
		{
			Element element = iter.next();

			if (element.getAttributeValue("name").equals(propertyName))
			{
				result = element;
				break;
			}
		}

		return result;
	}
	
	// --------------------------------------------------------------------------------------------

	public String[] getPropertyNames(String pluginId)
	{
		String[] result = null;
		
		try
		{
			Document document = loadDocument();
			Element pluginIdElement = document.getRootElement().getChild(pluginId);

			if (pluginIdElement != null)
			{
				List children = pluginIdElement.getChildren();
				result = new String[children.size()];
				int counter = 0;
				
				for (Iterator iterator = children.iterator(); iterator.hasNext();)
				{
					Element name = (Element) iterator.next();
					result[counter++] = name.getName();
				}
			}
			
			else 
				result = new String[0];
		}
		catch (JDOMException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	// --------------------------------------------------------------------------------------------

	/**
	 * Returns the instance of that store.
	 * 
	 * @return the instacne of that store
	 */
	public static XMLStore getInstance()
	{
		return instance == null
				? instance = new XMLStore()
				: instance;
	}
	
	// --------------------------------------------------------------------------------------------

	/**
	 * Returns an instance of that store, which is reconfigured. That means, that the config file 
	 * that contains the css config file names is parsed again and. This may be useful, if the config
	 * is changed during runtime.
	 * 
	 * @return a reconfigured XMLStore
	 */
	public XMLStore getReconfiguredInstance()
	{
		return instance = new XMLStore(); 
	}
}
