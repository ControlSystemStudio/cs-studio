package org.csstudio.platform.statistic;

import java.io.StringWriter;
import java.util.Vector;

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
    
    public final String getCollectionAsXMLString () {
        final String[] _COLUMN_NAMES = new String[] {"Application","Descriptor","Counter","Actual value","Date","Count","Highest Value","Date","Count","Lowest Value","Date","Count","Mean Value abs","Mean Value rel.","Alarm Limit abs","Alarm Limit rel","Hard Limit"};
//        File xmlFile = new File("c:\\tmp\\XMLStatisticFile.xml");
        System.out.println("Make new Doc");
        try{
            DocType dt = new DocType("CSS-StatisticProtocol");
            Element root = new Element("StatisticProtocol");
            root.setAttribute("Version","0.1");
            Document d = new Document(root,dt);
            Element collectionSupervisor = new Element("CollectionSupervisor");
            collectionSupervisor.setAttribute("size",collectorVector.size()+"");
            root.addContent(collectionSupervisor);
            for (Collector collector : collectorVector) {
                int i=0;
                Element collectorElement = new Element("Collector");
                collectionSupervisor.addContent(collectorElement);
                Element coulmn = new Element(_COLUMN_NAMES[i++].replace(' ', '_'));
                coulmn.setText(collector.getApplication());
                collectorElement.addContent(coulmn);
                coulmn = new Element(_COLUMN_NAMES[i++].replace(' ', '_'));
                coulmn.setText(collector.getDescriptor());
                collectorElement.addContent(coulmn);
                coulmn = new Element(_COLUMN_NAMES[i++].replace(' ', '_'));
                coulmn.setText(collector.getCount().toString());
                collectorElement.addContent(coulmn);
                coulmn = new Element(_COLUMN_NAMES[i++].replace(' ', '_'));
                coulmn.setText(collector.getActualValue().getValue().toString());
                collectorElement.addContent(coulmn);
                coulmn = new Element(_COLUMN_NAMES[i++].replace(' ', '_'));
                coulmn.setText(collector.getActualValue().getTime().toString());
                collectorElement.addContent(coulmn);
                coulmn = new Element(_COLUMN_NAMES[i++].replace(' ', '_'));
                coulmn.setText(collector.getActualValue().getCount().toString());
                collectorElement.addContent(coulmn);
                coulmn = new Element(_COLUMN_NAMES[i++].replace(' ', '_'));
                coulmn.setText(collector.getHighestValue().getValue().toString());
                collectorElement.addContent(coulmn);
                coulmn = new Element(_COLUMN_NAMES[i++].replace(' ', '_'));
                coulmn.setText(collector.getHighestValue().getTime().toString());
                collectorElement.addContent(coulmn);
                coulmn = new Element(_COLUMN_NAMES[i++].replace(' ', '_'));
                coulmn.setText(collector.getHighestValue().getCount().toString());
                collectorElement.addContent(coulmn);
                coulmn = new Element(_COLUMN_NAMES[i++].replace(' ', '_'));
                if(collector.getLowestValue().getValue()!=null){
                    coulmn.setText(collector.getLowestValue().getValue().toString());
                }
                collectorElement.addContent(coulmn);
                coulmn = new Element(_COLUMN_NAMES[i++].replace(' ', '_'));
                if(collector.getLowestValue().getTime()!=null){
                    coulmn.setText(collector.getLowestValue().getTime().toString());
                }
                collectorElement.addContent(coulmn);
                coulmn = new Element(_COLUMN_NAMES[i++].replace(' ', '_'));
                if(collector.getLowestValue().getCount()!=null){
                    coulmn.setText(collector.getLowestValue().getCount().toString());
                }
                collectorElement.addContent(coulmn);
                coulmn = new Element(_COLUMN_NAMES[i++].replace(' ', '_'));
                if(collector.getMeanValueAbsolute()!=null){
                    coulmn.setText(collector.getMeanValueAbsolute().toString());
                }
                collectorElement.addContent(coulmn);
                coulmn = new Element(_COLUMN_NAMES[i++].replace(' ', '_'));
                if(collector.getMeanValuerelative()!=null){
                    coulmn.setText(collector.getMeanValuerelative().toString());
                }
                collectorElement.addContent(coulmn);
                coulmn = new Element(_COLUMN_NAMES[i++].replace(' ', '_'));
                if(collector.getAlarmHandler().getHighAbsoluteLimit()!=null){
                    coulmn.setText(collector.getAlarmHandler().getHighAbsoluteLimit().toString());
                }
                collectorElement.addContent(coulmn);
                coulmn = new Element(_COLUMN_NAMES[i++].replace(' ', '_'));
                if(collector.getAlarmHandler().getHighRelativeLimit()!=null){
                    coulmn.setText(collector.getAlarmHandler().getHighRelativeLimit().toString());
                }
                collectorElement.addContent(coulmn);
                coulmn = new Element(_COLUMN_NAMES[i++].replace(' ', '_'));
                if(collector.getHardLimit()!=null){
                    coulmn.setText(collector.getHardLimit().toString());
                }
                collectorElement.addContent(coulmn);
            }
            StringWriter sw = new StringWriter();
            XMLOutputter xmlOutputter = new XMLOutputter();
            xmlOutputter.output(d, sw);
            return sw.toString();
            
        }catch(Exception e){
            e.printStackTrace();
        }
        return "";
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
