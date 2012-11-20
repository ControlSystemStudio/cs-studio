package org.csstudio.common.trendplotter.model;

import java.io.PrintWriter;

import org.csstudio.apputil.xml.DOMHelper;
import org.csstudio.apputil.xml.XMLWriter;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Add write XML function to a XYGraphMemento
 * @author Laurent PHILIPPE (GANIL)
 */
public class XYGraphSettingsXMLUtil{


	private XYGraphSettings XYGraphMem;

	public XYGraphSettingsXMLUtil(XYGraphSettings XYGraphMem){
		this.XYGraphMem = XYGraphMem;
	}

	/** Write XML formatted XYGraphMemento => XYGraph configuration settings
     *  @param writer PrintWriter
     */
	public void write(final PrintWriter writer)
    {
		 XMLWriter.start(writer, 1, Model.TAG_TITLE);
		 writer.println();


		 if(XYGraphMem.getTitle() != null)
	    	 XMLWriter.XML(writer, 2, Model.TAG_TITLE_TEXT, XYGraphMem.getTitle());


	     if(XYGraphMem.getTitleColor() != null)
	    	 Model.writeColor(writer, 2, Model.TAG_TITLE_COLOR, XYGraphMem.getTitleColor());


	     if(XYGraphMem.getTitleFontData() != null)
	    	 XMLWriter.XML(writer, 2, Model.TAG_TITLE_FONT, XYGraphMem.getTitleFontData());


	     XMLWriter.end(writer, 1, Model.TAG_TITLE);

	     writer.println();
	     XMLWriter.start(writer, 1, Model.TAG_GRAPH_SETTINGS);
		 writer.println();


		 XMLWriter.XML(writer, 2, Model.TAG_SHOW_TITLE, XYGraphMem.isShowTitle());
		 XMLWriter.XML(writer, 2, Model.TAG_SHOW_LEGEND, XYGraphMem.isShowLegend());
		 XMLWriter.XML(writer, 2, Model.TAG_SHOW_PLOT_AREA_BORDER, XYGraphMem.isShowPlotAreaBorder());
		 XMLWriter.XML(writer, 2, Model.TAG_TRANSPARENT, XYGraphMem.isTransparent());

	     XMLWriter.end(writer, 1, Model.TAG_GRAPH_SETTINGS);



	     writer.println();
    }


	  /** Create {@link XYGraphSettings} from XML document
     *  @param node XML node with item configuration
     *  @return PVItem
     *  @throws Exception on error
     */
	public static XYGraphSettings fromDocument(final Node root) throws Exception
    {
		final XYGraphSettings settings = new XYGraphSettings();

		Element node = DOMHelper.findFirstElementNode(root, Model.TAG_TITLE);

		final String title = DOMHelper.getSubelementString(node, Model.TAG_TITLE_TEXT);
		RGB titleColor = Model.loadColorFromDocument(node, Model.TAG_TITLE_COLOR);

		settings.setTitle(title);

		if (titleColor != null)
			settings.setTitleColor(titleColor);

		final String fontInfo = DOMHelper.getSubelementString(node, Model.TAG_TITLE_FONT);
		if (fontInfo != null && !fontInfo.trim().isEmpty())
		{
			FontData fontData = new FontData(fontInfo);
			//System.err.println("FONT DATA " + fontData.name + " " + fontData.height);
			settings.setTitleFontData(fontData);
		}

		node = DOMHelper.findFirstElementNode(root, Model.TAG_GRAPH_SETTINGS);
		settings.setShowLegend(DOMHelper.getSubelementBoolean(node, Model.TAG_SHOW_LEGEND, true));
		settings.setShowTitle(DOMHelper.getSubelementBoolean(node, Model.TAG_SHOW_TITLE, !title.isEmpty()));
		settings.setShowPlotAreaBorder(DOMHelper.getSubelementBoolean(node, Model.TAG_SHOW_PLOT_AREA_BORDER, false));
		settings.setTransparent(DOMHelper.getSubelementBoolean(node, Model.TAG_TRANSPARENT, false));

        return settings;
    }
}
