package org.csstudio.trends.databrowser;

import org.csstudio.trends.databrowser.model.IModelItem;
import java.util.Hashtable;
import java.io.InputStream;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public final class DataTypeMapper 
{
	private static String CONFIG_FILE = "META-INF/datatypemappings.xml"; //$NON-NLS-1$
	
	private static DataTypeMapper classInstance;
	
	private final Hashtable<String, IModelItem.DisplayType> dataTypeMap;
	
	private DataTypeMapper() 
	{
		this.dataTypeMap = new Hashtable<String, IModelItem.DisplayType>();
		
		try 
		{
			InputStream confFile = Plugin.getDefault().getBundle().getEntry(CONFIG_FILE).openStream();
			this.read(confFile);
		}
		catch(Exception e) 
		{
			Plugin.logError("Can not read datatypemappings.xml"); //$NON-NLS-1$
		}
	}
	
	public static DataTypeMapper getInstance() {
		
		if(classInstance == null)
			classInstance = new DataTypeMapper();
		
		return classInstance;
	}
	
	private static String generateKey(String server, String dataType)
	{
		return server + "_" + dataType; //$NON-NLS-1$
	}
	
	public IModelItem.DisplayType getDisplayType(String server, String dataType)
	{
		return getDisplayType(server, dataType, IModelItem.DisplayType.Lines);
	}

	public IModelItem.DisplayType getDisplayType(String server, String dataType, IModelItem.DisplayType defaultType) 
	{
		// Lets generate key.
		String key = generateKey(server, dataType);
		
		// Return key.
		if(this.dataTypeMap != null && this.dataTypeMap.containsKey(key))
			return this.dataTypeMap.get(key);

		return defaultType;
	}
	
	private void read(InputStream stream)
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		
		try 
		{
			// Create new parser instance.
			XmlConfigParser parser = new XmlConfigParser();
			// Lets parse xml and fill map.
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(stream, parser);

		} catch (Exception e)
		{
			this.dataTypeMap.clear();
			Plugin.logError(e.getMessage());
		}
	}
	
	private class XmlConfigParser extends DefaultHandler {
		
		public XmlConfigParser() {}
		
		// <server> section element.
		private final String SERVER_ELEMENT = "server"; //$NON-NLS-1$
		// <datatype> section element.
		private final String DATA_TYPE_ELEMENT = "datatype"; //$NON-NLS-1$
		// <datatype name> attribute.
		private final String ATT_DATA_TYPE_NAME = "name"; //$NON-NLS-1$
		// <datatype mapping> attribute.
		private final String ATT_MAPPING = "mapping"; //$NON-NLS-1$
		
		private String serverName = null;
		
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
		{
			if(qName == SERVER_ELEMENT) 
			{
				if(attributes.getLength() > 0 && attributes.getIndex(ATT_DATA_TYPE_NAME) != -1) 
				{
					serverName = attributes.getValue(ATT_DATA_TYPE_NAME);
					
					if(serverName == null || serverName == "") //$NON-NLS-1$
					{
						throw new SAXException("<server name> attribute is empty."); //$NON-NLS-1$
					}
				}
				else {
					throw new SAXException("<server> element does not contain required name attribute."); //$NON-NLS-1$
				}
			}
			
			else if(qName == DATA_TYPE_ELEMENT) 
			{
				if(serverName == null)
					throw new SAXException("<datatype> element is not inside <server> element"); //$NON-NLS-1$
				
				String dataType = null;
				String mapping = null;
				String localAttrName;
				
				for(int i = 0; i < attributes.getLength(); i++)
				{
					// Get name of an attribute.
					localAttrName = attributes.getQName(i);
					
					if(localAttrName == ATT_DATA_TYPE_NAME) 
					{
						dataType = attributes.getValue(i);
					}
					else if(localAttrName == ATT_MAPPING) 
					{
						mapping = attributes.getValue(i);
					}
				}
				
				// Add element to collection.
				if(dataType != null && mapping != null) 
				{
					String key = DataTypeMapper.generateKey(serverName, dataType);
					
					if(!DataTypeMapper.this.dataTypeMap.contains(key)) 
					{
						// Add item to collection.
						try {
							DataTypeMapper.this.dataTypeMap.put(key, IModelItem.DisplayType.valueOf(mapping));
						}
						catch(Exception e) {
							throw new SAXException("Invalid mapping type - {" + mapping +  "}"); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
				}
			}
		}
		
		public void endElement(String uri, String localName, String qName,Attributes attributes) throws SAXException 
		{
			if(qName == SERVER_ELEMENT) 
			{
				serverName = null;
			}
		}
	}
}
