package org.csstudio.display.pvmanager.pvtable;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.csstudio.csdata.ProcessVariable;
import org.csstudio.display.pvmanager.pvtable.PVTableModel.Item;

public class PVTableStaXParser {
	static final String PVLIST = "pvlist";
	static final String PV = "pv";
	static final String NAME = "name";
	static final String VALUE = "savedValue";
	static final String ALARM = "alarm";
	static final String TIME = "time";

	private static final Logger logger = Logger
			.getLogger(PVTableStaXParser.class.toString());

	public static List<ProcessVariable> readPVTableFile(String PVTableFile) {
		List<ProcessVariable> items = null;
		ProcessVariable pvName = null;

		try {
			// First create a new XMLInputFactory
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			// Setup a new eventReader
			InputStream in = new FileInputStream(PVTableFile);
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();
					// If we have a pvlist element we create a new item
					if (startElement.getName().getLocalPart() == (PVLIST)) {
						items = new ArrayList<ProcessVariable>();
					}
				}
				if (event.isStartElement()
						&& event.asStartElement().getName().getLocalPart()
								.equals(PV)) {

				}
				if (event.isStartElement()
						&& event.asStartElement().getName().getLocalPart()
								.equals(NAME)) {
					event = eventReader.nextEvent();
					pvName = new ProcessVariable(event.asCharacters()
							.getData());
				}
				if (event.isEndElement()) {
					EndElement endElement = event.asEndElement();
					if (endElement.getName().getLocalPart() == (PV)) {
						items.add(pvName);
					}
				}

				// If we reach the end of an item element we add it to the list
				if (event.isEndElement()) {
					EndElement endElement = event.asEndElement();
					if (endElement.getName().getLocalPart() == (PVLIST)) {

					}
				}
			}
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		} catch (XMLStreamException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
		return items;
	}

	public static ByteArrayOutputStream createByteBuffer(List<Item> list) {
		try {
			// Create a XMLOutputFactory
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			// Create XMLEventWriter
			// XMLEventWriter eventWriter = outputFactory
			ByteArrayOutputStream test = new ByteArrayOutputStream();
			XMLEventWriter eventWriter = outputFactory
					.createXMLEventWriter(test);
			// Create a EventFactory
			XMLEventFactory eventFactory = XMLEventFactory.newInstance();
			XMLEvent end = eventFactory.createDTD("\n");
			// Create and write Start Tag
			StartDocument startDocument = eventFactory.createStartDocument();
			eventWriter.add(startDocument);

			// Create config open tag
			eventWriter.add(eventFactory.createDTD("\n"));
			eventWriter.add(eventFactory.createDTD("\t"));
			eventWriter.add(eventFactory.createStartElement("", "", PVLIST));
			eventWriter.add(end);

			//
			StartElement pvStartElement = eventFactory.createStartElement("",
					"", PV);
			EndElement pvEndElement = eventFactory.createEndElement("", "", PV);

			// Write the different nodes
			for (Item pvTableItem : list) {
				if (pvTableItem.getProcessVariableName() != null) {
					eventWriter.add(eventFactory.createDTD("\t\t"));
					eventWriter.add(pvStartElement);
					eventWriter.add(eventFactory.createDTD("\n"));
					createNode(eventWriter, NAME, pvTableItem
							.getProcessVariableName().getName());
					eventWriter.add(eventFactory.createDTD("\t\t"));
					eventWriter.add(pvEndElement);
					eventWriter.add(eventFactory.createDTD("\n"));
				}
			}

			eventWriter.add(eventFactory.createEndElement("", "", PVLIST));
			eventWriter.add(end);
			eventWriter.add(eventFactory.createEndDocument());
			eventWriter.close();
			return test;

		} catch (FactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}	

	private static void createNode(XMLEventWriter eventWriter, String name,
			String value) throws XMLStreamException {

		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		XMLEvent end = eventFactory.createDTD("\n");
		XMLEvent tab = eventFactory.createDTD("\t\t\t");
		// Create Start node
		StartElement sElement = eventFactory.createStartElement("", "", name);
		eventWriter.add(tab);
		eventWriter.add(sElement);
		// Create Content
		Characters characters = eventFactory.createCharacters(value);
		eventWriter.add(characters);
		// Create End node
		EndElement eElement = eventFactory.createEndElement("", "", name);
		eventWriter.add(eElement);
		eventWriter.add(end);

	}
}
