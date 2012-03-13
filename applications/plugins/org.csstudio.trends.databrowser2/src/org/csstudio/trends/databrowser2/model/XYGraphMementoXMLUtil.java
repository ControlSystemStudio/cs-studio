package org.csstudio.trends.databrowser2.model;

import java.io.PrintWriter;

import org.csstudio.apputil.xml.XMLWriter;
import org.csstudio.swt.xygraph.undo.XYGraphMemento;


/**
 * Add write XML function to a XYGraphMemento
 * @author Laurent PHILIPPE (GANIL)
 */
public class XYGraphMementoXMLUtil{

	
	private XYGraphMemento XYGraphMem;

	public XYGraphMementoXMLUtil(XYGraphMemento XYGraphMem){
		this.XYGraphMem = XYGraphMem;
	}
	
	/** Write XML formatted XYGraphMemento => XYGraph configuration settings
     *  @param writer PrintWriter
     */
	public void write(final PrintWriter writer)
    {
		 XMLWriter.start(writer, 1, Model.TAG_XYGRAPHMEMENTO);
		 writer.println();
	     
		 
		 if(XYGraphMem.getTitle() != null)
	    	 XMLWriter.XML(writer, 2, Model.TAG_TITLE, XYGraphMem.getTitle());
	     
		  
	     if(XYGraphMem.getTitleColor() != null)
	    	 Model.writeColor(writer, 2, Model.TAG_TITLE_COLOR, XYGraphMem.getTitleColor().getRGB());
	   
	    
	     if(XYGraphMem.getTitleFontData() != null)
	    	 XMLWriter.XML(writer, 2, Model.TAG_TITLE_FONT, XYGraphMem.getTitleFontData());
	    
	     
	     XMLWriter.end(writer, 1, Model.TAG_XYGRAPHMEMENTO);
	     writer.println();
    }
}
