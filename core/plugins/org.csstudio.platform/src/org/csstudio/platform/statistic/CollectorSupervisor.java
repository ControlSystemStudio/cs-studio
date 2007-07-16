package org.csstudio.platform.statistic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Vector;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class CollectorSupervisor {
	
	private static CollectorSupervisor thisCollectorSupervisor= null;
	
	private Vector<Collector> collectorVector = null;
	
	public CollectorSupervisor () {
		
		collectorVector= new Vector<Collector>();
	}
	
	public static CollectorSupervisor getInstance() {
		//
		// get an instance of our sigleton
		//
		if ( thisCollectorSupervisor == null) {
			synchronized (CollectorSupervisor.class) {
				if (thisCollectorSupervisor == null) {
					thisCollectorSupervisor = new CollectorSupervisor();
				}
			}
		}
		return thisCollectorSupervisor;
	}
	
	public void printCollection () {
		/*
		 * print all actuall collections
		 */
		System.out.println("======== Collection Supervisor - Printout overview  ================");
		System.out.println("Vector-Size: " + collectorVector.size());
		String singleStatus = null;
		for ( int i = 0; i< collectorVector.size(); i++) {
			singleStatus = collectorVector.elementAt(i).getCollectorStatus();
			System.out.print(singleStatus);
		}
	}
	
	public String getCollectionAsString () {
		/*
		 * print all actuall collections
		 */
		String result = "======== Collection Supervisor - Printout overview  ================";
		result += "Vector-Size: " + collectorVector.size();

		for ( int i = 0; i< collectorVector.size(); i++) {
			result += collectorVector.elementAt(i).getCollectorStatus();
		}
		return result;
	}
    
    public File getCollectionAsXMLFile () {
        /*
        final String[] _COLUMN_NAMES = new String[] {"Application","Descriptor","Counter","Actual value","Date","Count","Highest Value","Date","Count","Lowest Value","Date","Count","Mean Value abs","Mean Value rel.","Alarm Limit (abs)","Alarm Limit (rel)","Hard Limit"};
        File xmlFile = new File("XMLStatisticFile.xml");
        XMLOutputFactory factory = XMLOutputFactory.newInstance(); 
        try {
            XMLStreamWriter writer = factory.createXMLStreamWriter(new FileOutputStream(xmlFile));
            writer.writeStartDocument("0.1");
            writer.writeStartElement("Collection Supervisor");
                writer.writeAttribute("size", collectorVector.size()+"");
                for (Collector collector : collectorVector) {
                    int i=0;
                    writer.writeStartElement(_COLUMN_NAMES[i++]);
                        writer.writeCharacters(collector.getApplication());
                    writer.writeEndElement();
                    writer.writeStartElement(_COLUMN_NAMES[i++]);
                    writer.writeCharacters(collector.getDescriptor());
                    writer.writeEndElement();
                    writer.writeStartElement(_COLUMN_NAMES[i++]);
                    writer.writeCharacters(collector.getCount().toString());
                    writer.writeEndElement();
                    writer.writeStartElement(_COLUMN_NAMES[i++]);
                    writer.writeCharacters(collector.getActualValue().getValue().toString());
                    writer.writeEndElement();
                    writer.writeStartElement(_COLUMN_NAMES[i++]);
                    writer.writeCharacters(collector.getActualValue().getTime().toString());
                    writer.writeEndElement();
                    writer.writeStartElement(_COLUMN_NAMES[i++]);
                    writer.writeCharacters(collector.getActualValue().getCount().toString());
                    writer.writeEndElement();
                    writer.writeStartElement(_COLUMN_NAMES[i++]);
                    writer.writeCharacters(collector.getHighestValue().getValue().toString());
                    writer.writeEndElement();
                    writer.writeStartElement(_COLUMN_NAMES[i++]);
                    writer.writeCharacters(collector.getHighestValue().getTime().toString());
                    writer.writeEndElement();
                    writer.writeStartElement(_COLUMN_NAMES[i++]);
                    writer.writeCharacters(collector.getHighestValue().getCount().toString());
                    writer.writeEndElement();
                    writer.writeStartElement(_COLUMN_NAMES[i++]);
                    writer.writeCharacters(collector.getLowestValue().getValue().toString());
                    writer.writeEndElement();
                    writer.writeStartElement(_COLUMN_NAMES[i++]);
                    writer.writeCharacters(collector.getLowestValue().getTime().toString());
                    writer.writeEndElement();
                    writer.writeStartElement(_COLUMN_NAMES[i++]);
                    writer.writeCharacters(collector.getLowestValue().getCount().toString());
                    writer.writeEndElement();
                    writer.writeStartElement(_COLUMN_NAMES[i++]);
                    writer.writeCharacters(collector.getMeanValueAbsolute().toString());
                    writer.writeEndElement();
                    writer.writeStartElement(_COLUMN_NAMES[i++]);
                    writer.writeCharacters(collector.getMeanValuerelative().toString());
                    writer.writeEndElement();
                    writer.writeStartElement(_COLUMN_NAMES[i++]);
                    writer.writeCharacters(collector.getAlarmHandler().getHighAbsoluteLimit().toString());
                    writer.writeEndElement();
                    writer.writeStartElement(_COLUMN_NAMES[i++]);
                    writer.writeCharacters(collector.getAlarmHandler().getHighRelativeLimit().toString());
                    writer.writeEndElement();
                    writer.writeStartElement(_COLUMN_NAMES[i++]);
                    writer.writeCharacters(collector.getHardLimit().toString());
                    writer.writeEndElement();
                }
                writer.writeEndElement();
            writer.writeEndDocument();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XMLStreamException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        return xmlFile;
        */
        return null;
    }
	public Vector<Collector> getCollectorVector() {
		return collectorVector;
	}

	public void setCollectorVector(Vector<Collector> collectorVector) {
		this.collectorVector = collectorVector;
	}
	
	public void addCollector( Collector collector) {
		collectorVector.add( collector);
	}

}
