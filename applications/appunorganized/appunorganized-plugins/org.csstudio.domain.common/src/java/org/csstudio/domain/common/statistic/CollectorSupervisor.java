/*
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */
 package org.csstudio.domain.common.statistic;

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

    public synchronized static CollectorSupervisor getInstance() {
        //
        // get an instance of our sigleton
        //
        if ( thisCollectorSupervisor == null) {
            thisCollectorSupervisor = new CollectorSupervisor();
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
        final String[] _COLUMN_NAMES = "Application,Descriptor,Counter,Actual value,Date Actual,Count Actual,Highest Value,Date Highest,Count Highest,Lowest Value,Date Lowest,Count Lowest,Mean Value abs,Mean Value rel.,Alarm Limit (abs),Alarm Limit (rel),Hard Limit".split(",");
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
