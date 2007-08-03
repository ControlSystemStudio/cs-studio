package org.csstudio.platform.statistic;

import java.io.StringWriter;
import java.util.Vector;

import org.jdom.Attribute;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

public final class CollectorSupervisor {
    
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
    
    public String getCollectionAsXMLString () {
        final String[] _COLUMN_NAMES = "Application,Descriptor,Counter,Actual value,Date,Count,Highest Value,Date,Count,Lowest Value,Date,Count,Mean Value abs,Mean Value rel.,Alarm Limit (abs),Alarm Limit (rel),Hard Limit".split(",");
//        File xmlFile = new File("c:\\tmp\\XMLStatisticFile.xml");
        System.out.println("Make new Doc");
        try{
            DocType dt = new DocType("CSS-StatisticProtocol");
            Element root = new Element("StatisticProtocol");
            Attribute version = new Attribute("Version","0.1");
            version.setAttributeType(Attribute.ID_TYPE);
            root.setAttribute(version);
            Document d = new Document(root,dt);
            Element collectionSupervisor = new Element("CollectionSupervisor");
            collectionSupervisor.setAttribute("size",collectorVector.size()+"");
            root.addContent(collectionSupervisor);
            for (Collector collector : collectorVector) {
                int i=0;
                Element collectorElement = new Element("Collector");
                collectionSupervisor.addContent(collectorElement);
                Element column = new Element("COLUMN");
                column.setAttribute("Name", _COLUMN_NAMES[i++]);
                column.setText(collector.getApplication());
                collectorElement.addContent(column);
                column = new Element("COLUMN"); 
                column.setAttribute("Name", _COLUMN_NAMES[i++]);
                column.setText(collector.getDescriptor());
                collectorElement.addContent(column);
                column = new Element("COLUMN"); 
                column.setAttribute("Name", _COLUMN_NAMES[i++]);
                column.setText(collector.getCount().toString());
                collectorElement.addContent(column);
                column = new Element("COLUMN"); 
                column.setAttribute("Name", _COLUMN_NAMES[i++]);
                column.setText(collector.getActualValue().getValue().toString());
                collectorElement.addContent(column);
                column = new Element("COLUMN"); 
                column.setAttribute("Name", _COLUMN_NAMES[i++]);
                column.setText(Collector.dateToString(collector.getActualValue().getTime()));
                collectorElement.addContent(column);
                column = new Element("COLUMN"); 
                column.setAttribute("Name", _COLUMN_NAMES[i++]);
                column.setText(collector.getActualValue().getCount().toString());
                collectorElement.addContent(column);
                column = new Element("COLUMN"); 
                column.setAttribute("Name", _COLUMN_NAMES[i++]);
                column.setText(collector.getHighestValue().getValue().toString());
                collectorElement.addContent(column);
                column = new Element("COLUMN"); 
                column.setAttribute("Name", _COLUMN_NAMES[i++]);
                column.setText(Collector.dateToString(collector.getHighestValue().getTime()));
                collectorElement.addContent(column);
                column = new Element("COLUMN"); 
                column.setAttribute("Name", _COLUMN_NAMES[i++]);
                column.setText(collector.getHighestValue().getCount().toString());
                collectorElement.addContent(column);
                column = new Element("COLUMN"); 
                column.setAttribute("Name", _COLUMN_NAMES[i++]);
                if(collector.getLowestValue().getValue()!=null){
                    column.setText(collector.getLowestValue().getValue().toString());
                }
                collectorElement.addContent(column);
                column = new Element("COLUMN"); column.setAttribute("Name", _COLUMN_NAMES[i++]);
                if(collector.getLowestValue().getTime()!=null){
                    column.setText(Collector.dateToString(collector.getLowestValue().getTime()));
                }
                collectorElement.addContent(column);
                column = new Element("COLUMN"); column.setAttribute("Name", _COLUMN_NAMES[i++]);
                if(collector.getLowestValue().getCount()!=null){
                    column.setText(collector.getLowestValue().getCount().toString());
                }
                collectorElement.addContent(column);
                column = new Element("COLUMN"); column.setAttribute("Name", _COLUMN_NAMES[i++]);
                if(collector.getMeanValueAbsolute()!=null){
                    column.setText(collector.getMeanValueAbsolute().toString());
                }
                collectorElement.addContent(column);
                column = new Element("COLUMN"); column.setAttribute("Name", _COLUMN_NAMES[i++]);
                if(collector.getMeanValuerelative()!=null){
                    column.setText(collector.getMeanValuerelative().toString());
                }
                collectorElement.addContent(column);
                column = new Element("COLUMN"); column.setAttribute("Name", _COLUMN_NAMES[i++]);
                if(collector.getAlarmHandler().getHighAbsoluteLimit()!=null){
                    column.setText(collector.getAlarmHandler().getHighAbsoluteLimit().toString());
                }
                collectorElement.addContent(column);
                column = new Element("COLUMN"); column.setAttribute("Name", _COLUMN_NAMES[i++]);
                if(collector.getAlarmHandler().getHighRelativeLimit()!=null){
                    column.setText(collector.getAlarmHandler().getHighRelativeLimit().toString());
                }
                collectorElement.addContent(column);
                column = new Element("COLUMN"); column.setAttribute("Name", _COLUMN_NAMES[i++]);
                if(collector.getHardLimit()!=null){
                    column.setText(collector.getHardLimit().toString());
                }
                collectorElement.addContent(column);
            }
            StringWriter sw = new StringWriter();
            XMLOutputter xmlOutputter = new XMLOutputter();
            xmlOutputter.output(d, sw);
            System.out.println(sw.toString());
            return sw.toString();
            
        }catch(Exception e){
            e.printStackTrace();
        }
        return "";
    }
    public Vector<Collector> getCollectorVector() {
        return collectorVector;
    }

    public void setCollectorVector(final Vector<Collector> collectorVector) {
        this.collectorVector = collectorVector;
    }
    
    public void addCollector( final Collector collector) {
        collectorVector.add( collector);
    }

}
