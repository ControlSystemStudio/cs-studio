package org.csstudio.trends.databrowser2.persistence;

import java.io.PrintWriter;
import java.util.logging.Level;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.csstudio.apputil.xml.DOMHelper;
import org.csstudio.trends.databrowser2.Activator;
import org.csstudio.trends.databrowser2.model.Model;
import org.eclipse.swt.graphics.RGB;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Write XML using JAXB.
 * 
 * @author Fred Arnaud (Sopra Group) - ITER
 */
public class XYGraphSettingsXMLUtil {

	public static final String TAG_TITLE = "title";
	public static final String TAG_TITLE_TEXT = "text";
	public static final String TAG_TITLE_COLOR = "color";
	public static final String TAG_TITLE_FONT = "font";
	public static final String TAG_GRAPH_SETTINGS = "graph_settings";
	public static final String TAG_SHOW_TITLE = "show_title";
	public static final String TAG_SHOW_LEGEND = "show_legend";
	public static final String TAG_SHOW_PLOT_AREA_BORDER = "show_plot_area_border";
	public static final String TAG_TRANSPARENT = "transparent";

	/**
	 * Write XML formatted {@link XYGraphSettings}.
	 * 
	 * @param settings
	 * @param writer
	 */
	public static void write(final XYGraphSettings settings,
			final PrintWriter writer) {
		try {
			JAXBContext jaxbCtx = JAXBContext
					.newInstance(XYGraphSettings.class);
			Marshaller m = jaxbCtx.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.setProperty(Marshaller.JAXB_FRAGMENT, true);
			m.marshal(settings, writer);
		} catch (JAXBException e) {
			Activator.getLogger().log(Level.WARNING,
					"Problem writing XYGraph settings: {1}",
					new Object[] { e.getMessage() });
		}
	}

	/**
	 * Read XML formatted {@link XYGraphSettings}.
	 * 
	 * @param root
	 * @return
	 */
	public static XYGraphSettings read(final Node root) {
		if (root == null)
			return null;
		try {
			JAXBContext jaxbCtx = JAXBContext
					.newInstance(XYGraphSettings.class);
			Unmarshaller um = jaxbCtx.createUnmarshaller();
			return (XYGraphSettings) um.unmarshal(root);
		} catch (JAXBException e) {
			Activator.getLogger().log(Level.WARNING,
					"Problem reading XYGraph settings from {0}: {1}",
					new Object[] { root.getNodeName(), e.getMessage() });
		}
		return null;
	}

	/**
	 * Retro-compatibility method to read XML formatted {@link XYGraphSettings}.
	 * 
	 * @param root
	 * @return
	 * @throws Exception
	 */
	public static XYGraphSettings readOldSettings(final Node root)
			throws Exception {
		final XYGraphSettings settings = new XYGraphSettings();

		Element node = DOMHelper.findFirstElementNode(root, TAG_TITLE);
		final String title = DOMHelper.getSubelementString(node, TAG_TITLE_TEXT);
		settings.setTitle(title);
		RGB titleColor = Model.loadColorFromDocument(node, TAG_TITLE_COLOR);
		if (titleColor != null) {
			ColorSettings colorSettings = new ColorSettings();
			colorSettings.setRed(titleColor.red);
			colorSettings.setGreen(titleColor.green);
			colorSettings.setBlue(titleColor.blue);
			settings.setTitleColor(colorSettings);
		}
		final String fontInfo = DOMHelper.getSubelementString(node, TAG_TITLE_FONT);
		settings.setTitleFont(fontInfo);

		node = DOMHelper.findFirstElementNode(root, TAG_GRAPH_SETTINGS);
		settings.setShowLegend(DOMHelper.getSubelementBoolean(node, TAG_SHOW_LEGEND, true));
		settings.setShowTitle(DOMHelper.getSubelementBoolean(node, TAG_SHOW_TITLE, !title.isEmpty()));
		settings.setShowPlotAreaBorder(DOMHelper.getSubelementBoolean(node, TAG_SHOW_PLOT_AREA_BORDER, false));
		settings.setTransparent(DOMHelper.getSubelementBoolean(node, TAG_TRANSPARENT, false));

		return settings;
	}

}
