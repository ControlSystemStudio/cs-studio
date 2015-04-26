/*******************************************************************************
 * Copyright (c) 2014-2015 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.trends.databrowser2.persistence;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.csstudio.apputil.xml.DOMHelper;
import org.csstudio.apputil.xml.XMLWriter;
import org.csstudio.swt.rtplot.SWTMediaPool;
import org.csstudio.trends.databrowser2.Activator;
import org.csstudio.trends.databrowser2.model.AnnotationInfo;
import org.csstudio.trends.databrowser2.model.ArchiveRescale;
import org.csstudio.trends.databrowser2.model.AxisConfig;
import org.csstudio.trends.databrowser2.model.FormulaItem;
import org.csstudio.trends.databrowser2.model.Model;
import org.csstudio.trends.databrowser2.model.ModelItem;
import org.csstudio.trends.databrowser2.model.PVItem;
import org.csstudio.trends.databrowser2.preferences.Preferences;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** Load and save {@link Model} as XML file
 *
 *  <p>Attempts to load files going back to very early versions of the
 *  Data Browser, as well as those which contained the xyGraphSettings
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class XMLPersistence
{
    // XML file tags
    final public static String TAG_DATABROWSER = "databrowser";

    final public static String TAG_TITLE = "title";
    final public static String TAG_SAVE_CHANGES = "save_changes";
    final public static String TAG_GRID = "grid";
    final public static String TAG_SCROLL = "scroll";
    final public static String TAG_UPDATE_PERIOD = "update_period";
    final public static String TAG_SCROLL_STEP = "scroll_step";
    final public static String TAG_START = "start";
    final public static String TAG_END = "end";
    final public static String TAG_ARCHIVE_RESCALE = "archive_rescale";
    final public static String TAG_BACKGROUND = "background";
    final public static String TAG_TITLE_FONT = "title_font";
    final public static String TAG_LABEL_FONT = "label_font";
    final public static String TAG_SCALE_FONT = "scale_font";
    final public static String TAG_AXES = "axes";
    final public static String TAG_ANNOTATIONS = "annotations";
    final public static String TAG_PVLIST = "pvlist";

    final public static String TAG_COLOR = "color";
    final public static String TAG_RED = "red";
    final public static String TAG_GREEN = "green";
    final public static String TAG_BLUE = "blue";

    final public static String TAG_AXIS = "axis";
    final public static String TAG_VISIBLE = "visible";
    final public static String TAG_NAME = "name";
    final public static String TAG_USE_AXIS_NAME = "use_axis_name";
    final public static String TAG_USE_TRACE_NAMES = "use_trace_names";
    final public static String TAG_RIGHT = "right";
    final public static String TAG_MAX = "max";
    final public static String TAG_MIN = "min";
    final public static String TAG_AUTO_SCALE = "autoscale";
    final public static String TAG_LOG_SCALE = "log_scale";

    final public static String TAG_ANNOTATION = "annotation";
    final public static String TAG_PV = "pv";
    final public static String TAG_TIME = "time";
    final public static String TAG_VALUE = "value";
    final public static String TAG_OFFSET = "offset";
    final public static String TAG_TEXT = "text";

    final public static String TAG_X = "x";
    final public static String TAG_Y = "y";

    final public static String TAG_DISPLAYNAME = "display_name";
    final public static String TAG_TRACE_TYPE = "trace_type";
    final public static String TAG_LINEWIDTH = "linewidth";
    final public static String TAG_POINT_TYPE = "point_type";
    final public static String TAG_POINT_SIZE = "point_size";
    final public static String TAG_WAVEFORM_INDEX = "waveform_index";
    final public static String TAG_SCAN_PERIOD = "period";
    final public static String TAG_LIVE_SAMPLE_BUFFER_SIZE = "ring_size";
    final public static String TAG_REQUEST = "request";
    final public static String TAG_ARCHIVE = "archive";

    final public static String TAG_URL = "url";
    final public static String TAG_KEY = "key";

    final public static String TAG_FORMULA = "formula";
    final public static String TAG_INPUT = "input";

    final private static String TAG_OLD_XYGRAPH_SETTINGS = "xyGraphSettings";

    /** @param model Model to load
     *  @param stream XML stream
     *  @throws Exception on error
     */
    public void load(final Model model, final InputStream stream) throws Exception
    {
        final DocumentBuilder docBuilder =
                DocumentBuilderFactory.newInstance().newDocumentBuilder();
        final Document doc = docBuilder.parse(stream);
        load(model, doc);
    }

    private void load(final Model model, final Document doc) throws Exception
    {
        if (model.getItems().iterator().hasNext())
            throw new RuntimeException("Model was already in use");

        // Check if it's a <databrowser/>.
        doc.getDocumentElement().normalize();
        final Element root_node = doc.getDocumentElement();
        if (!root_node.getNodeName().equals(TAG_DATABROWSER))
            throw new Exception("Expected " + TAG_DATABROWSER + " but got " + root_node.getNodeName());

        // Global settings
        String title = DOMHelper.getSubelementString(root_node, TAG_TITLE);
        if (! title.isEmpty())
            model.setTitle(title);
        model.setSaveChanges(DOMHelper.getSubelementBoolean(root_node, TAG_SAVE_CHANGES, true));
        model.setGridVisible(DOMHelper.getSubelementBoolean(root_node, TAG_GRID, false));
        model.enableScrolling(DOMHelper.getSubelementBoolean(root_node, TAG_SCROLL, true));
        model.setUpdatePeriod(DOMHelper.getSubelementDouble(root_node, TAG_UPDATE_PERIOD, Preferences.getUpdatePeriod()));
        try
        {
            model.setScrollStep( Duration.ofSeconds(
                    DOMHelper.getSubelementInt(root_node, TAG_SCROLL_STEP, (int) Preferences.getScrollStep().getSeconds())));
        }
        catch (Throwable ex)
        {
            // Ignore
        }

        final String start = DOMHelper.getSubelementString(root_node, TAG_START);
        final String end = DOMHelper.getSubelementString(root_node, TAG_END);
        if (start.length() > 0  &&  end.length() > 0)
            model.setTimerange(start, end);

        final String rescale = DOMHelper.getSubelementString(root_node, TAG_ARCHIVE_RESCALE, ArchiveRescale.STAGGER.name());
        try
        {
            model.setArchiveRescale(ArchiveRescale.valueOf(rescale));
        }
        catch (Throwable ex)
        {
            // Ignore
        }

        // Value Axes
        Element list = DOMHelper.findFirstElementNode(root_node.getFirstChild(), TAG_AXES);
        if (list != null)
        {
            Element item = DOMHelper.findFirstElementNode(
                    list.getFirstChild(), TAG_AXIS);
            while (item != null)
            {
                model.addAxis(AxisConfig.fromDocument(item));
                item = DOMHelper.findNextElementNode(item, TAG_AXIS);
            }
        }
        else
        {   // Check for legacy <xyGraphSettings> <axisSettingsList>
            list = DOMHelper.findFirstElementNode(root_node.getFirstChild(), TAG_OLD_XYGRAPH_SETTINGS);
            if (list != null)
            {
                loadColorFromDocument(list, "plotAreaBackColor").ifPresent(model::setPlotBackground);

                Element item = DOMHelper.findFirstElementNode(list.getFirstChild(), "axisSettingsList");
                if (item != null)
                {
                    // First axis is 'X'
                    model.setGridVisible(DOMHelper.getSubelementBoolean(item, "showMajorGrid", false));

                    // Read 'Y' axes
                    item = DOMHelper.findNextElementNode(item, "axisSettingsList");
                    while (item != null)
                    {
                        final String name = DOMHelper.getSubelementString(item, "title", null);
                        final AxisConfig axis = new AxisConfig(name);
                        loadColorFromDocument(item, "foregroundColor").ifPresent(axis::setColor);
                        axis.setGridVisible(DOMHelper.getSubelementBoolean(item, "showMajorGrid", false));
                        axis.setLogScale(DOMHelper.getSubelementBoolean(item, "logScale", false));
                        axis.setAutoScale(DOMHelper.getSubelementBoolean(item, "autoScale", false));
                        final Element range = DOMHelper.findFirstElementNode(item.getFirstChild(), "range");
                        if (range != null)
                        {
                            double min =  DOMHelper.getSubelementDouble(range, "lower", axis.getMin());
                            double max =  DOMHelper.getSubelementDouble(range, "upper", axis.getMax());
                            axis.setRange(min, max);
                        }
                        model.addAxis(axis);

                        // Using legacy settings from _last_ axis for fonts
                        loadFontFromDocument(item, "scaleFont").ifPresent(model::setScaleFont);
                        loadFontFromDocument(item, "titleFont").ifPresent(model::setLabelFont);

                        item = DOMHelper.findNextElementNode(item, "axisSettingsList");
                    }
                }
            }
        }

        // New settings, possibly replacing settings from legacy <xyGraphSettings> <axisSettingsList>
        loadColorFromDocument(root_node, TAG_BACKGROUND).ifPresent(model::setPlotBackground);
        loadFontFromDocument(root_node, TAG_TITLE_FONT).ifPresent(model::setTitleFont);
        loadFontFromDocument(root_node, TAG_LABEL_FONT).ifPresent(model::setLabelFont);
        loadFontFromDocument(root_node, TAG_SCALE_FONT).ifPresent(model::setScaleFont);

        // Load Annotations
        list = DOMHelper.findFirstElementNode(root_node.getFirstChild(), TAG_ANNOTATIONS);
        if (list != null)
        {
            // Load PV items
            final List<AnnotationInfo> annotations = new ArrayList<>();
            Element item = DOMHelper.findFirstElementNode(list.getFirstChild(), TAG_ANNOTATION);
            while (item != null)
            {
                try
                {
                    annotations.add(AnnotationInfo.fromDocument(item));
                }
                catch (Throwable ex)
                {
                    Activator.getLogger().log(Level.INFO, "XML error in Annotation", ex);
                }
                item = DOMHelper.findNextElementNode(item, TAG_ANNOTATION);
            }
            model.setAnnotations(annotations);
        }

        // Load PVs/Formulas
        list = DOMHelper.findFirstElementNode(root_node.getFirstChild(), TAG_PVLIST);
        if (list != null)
        {
            // Load PV items
            Element item = DOMHelper.findFirstElementNode(
                    list.getFirstChild(), TAG_PV);
            while (item != null)
            {
                final PVItem model_item = PVItem.fromDocument(model, item);
                // Adding item creates the axis for it if not already there
                model.addItem(model_item);
                // Ancient data browser stored axis configuration with each item: Update axis from that.
                final AxisConfig axis = model_item.getAxis();
                String s = DOMHelper.getSubelementString(item, TAG_AUTO_SCALE);
                if (s.equalsIgnoreCase("true"))
                    axis.setAutoScale(true);
                s = DOMHelper.getSubelementString(item, TAG_LOG_SCALE);
                if (s.equalsIgnoreCase("true"))
                    axis.setLogScale(true);
                final double min = DOMHelper.getSubelementDouble(item, TAG_MIN, axis.getMin());
                final double max = DOMHelper.getSubelementDouble(item, TAG_MAX, axis.getMax());
                axis.setRange(min, max);

                item = DOMHelper.findNextElementNode(item, TAG_PV);
            }
            // Load Formulas
            item = DOMHelper.findFirstElementNode(
                    list.getFirstChild(), TAG_FORMULA);
            while (item != null)
            {
                model.addItem(FormulaItem.fromDocument(model, item));
                item = DOMHelper.findNextElementNode(item, TAG_FORMULA);
            }
        }

        // Update items from legacy <xyGraphSettings>
        list = DOMHelper.findFirstElementNode(root_node.getFirstChild(), TAG_OLD_XYGRAPH_SETTINGS);
        if (list != null)
        {
            title = DOMHelper.getSubelementString(list, TAG_TITLE);
            if (! title.isEmpty())
                model.setTitle(title);

            final Iterator<ModelItem> model_items = model.getItems().iterator();
            Element item = DOMHelper.findFirstElementNode(list.getFirstChild(), "traceSettingsList");
            while (item != null)
            {
                if (! model_items.hasNext())
                    break;
                final ModelItem pv = model_items.next();
                loadColorFromDocument(item, "traceColor").ifPresent(pv::setColor);
                pv.setLineWidth(DOMHelper.getSubelementInt(item, "lineWidth", pv.getLineWidth()));
                pv.setDisplayName(DOMHelper.getSubelementString(item, "name", pv.getDisplayName()));
                item = DOMHelper.findNextElementNode(item, "traceSettingsList");
            }
        }
    }

    /** Load RGB color from XML document
     *  @param node Parent node of the color
     *  @return RGB
     */
    public static Optional<RGB> loadColorFromDocument(final Element node)
    {
        return loadColorFromDocument(node, TAG_COLOR);
    }

    /** Load RGB color from XML document
     *  @param node Parent node of the color
     *  @param color_tag Name of tag that contains the color
     *  @return RGB
     */
    public static Optional<RGB> loadColorFromDocument(final Element node, final String color_tag)
    {
        if (node == null)
            return Optional.of(new RGB(0, 0, 0));
        final Element color =
            DOMHelper.findFirstElementNode(node.getFirstChild(), color_tag);
        if (color == null)
            return Optional.empty();
        final int red = DOMHelper.getSubelementInt(color, TAG_RED, 0);
        final int green = DOMHelper.getSubelementInt(color, TAG_GREEN, 0);
        final int blue = DOMHelper.getSubelementInt(color, TAG_BLUE, 0);
        return Optional.of(new RGB(red, green, blue));
    }

    /** Load font from XML document
     *  @param node Parent node of the color
     *  @param font_tag Name of tag that contains the font
     *  @return FontData or <code>null</code> if no color found
     */
    public static Optional<FontData> loadFontFromDocument(final Element node, final String font_tag)
    {
        final String desc = DOMHelper.getSubelementString(node, font_tag);
        if (desc == null  ||  desc.isEmpty())
            return Optional.empty();
        return Optional.of(SWTMediaPool.getFontFromDescription(desc));
    }

    /** Write XML formatted Model content.
     *  @param model Model to write
     *  @param out OutputStream, will NOT be closed when done.
     */
    public void write(final Model model, final OutputStream out)
    {
        final PrintWriter writer = new PrintWriter(out);

        XMLWriter.header(writer);
        XMLWriter.start(writer, 0, TAG_DATABROWSER);
        writer.println();

        XMLWriter.XML(writer, 1, TAG_TITLE, model.getTitle().orElse(""));
        XMLWriter.XML(writer, 1, TAG_SAVE_CHANGES, model.shouldSaveChanges());

        // Time axis
        XMLWriter.XML(writer, 1, TAG_GRID, model.isGridVisible());
        XMLWriter.XML(writer, 1, TAG_SCROLL, model.isScrollEnabled());
        XMLWriter.XML(writer, 1, TAG_UPDATE_PERIOD, model.getUpdatePeriod());
        XMLWriter.XML(writer, 1, TAG_SCROLL_STEP, model.getScrollStep().getSeconds());
        XMLWriter.XML(writer, 1, TAG_START, model.getStartSpec());
        XMLWriter.XML(writer, 1, TAG_END, model.getEndSpec());

        XMLWriter.XML(writer, 1, TAG_ARCHIVE_RESCALE, model.getArchiveRescale().name());

        writeColor(writer, 1, TAG_BACKGROUND, model.getPlotBackground());
        XMLWriter.XML(writer, 1, TAG_TITLE_FONT, SWTMediaPool.getFontDescription(model.getTitleFont()));
        XMLWriter.XML(writer, 1, TAG_LABEL_FONT, SWTMediaPool.getFontDescription(model.getLabelFont()));
        XMLWriter.XML(writer, 1, TAG_SCALE_FONT, SWTMediaPool.getFontDescription(model.getScaleFont()));

        // Value axes
        XMLWriter.start(writer, 1, TAG_AXES);
        writer.println();
        for (AxisConfig axis : model.getAxes())
            axis.write(writer);
        XMLWriter.end(writer, 1, TAG_AXES);
        writer.println();

        // Annotations
        XMLWriter.start(writer, 1, TAG_ANNOTATIONS);
        writer.println();
        for (AnnotationInfo annotation : model.getAnnotations())
            annotation.write(writer);
        XMLWriter.end(writer, 1, TAG_ANNOTATIONS);
        writer.println();

        // PVs (Formulas)
        XMLWriter.start(writer, 1, TAG_PVLIST);
        writer.println();
        for (ModelItem item : model.getItems())
            item.write(writer);
        XMLWriter.end(writer, 1, TAG_PVLIST);
        writer.println();

        XMLWriter.end(writer, 0, TAG_DATABROWSER);
        writer.flush();
    }

    /** Write RGB color to XML document
     *  @param writer
     *  @param level Indentation level
     *  @param tag_name
     *  @param color
     */
    public static void writeColor(final PrintWriter writer, final int level,
            final String tag_name, final RGB color)
    {
        XMLWriter.start(writer, level, tag_name);
        writer.println();
        XMLWriter.XML(writer, level+1, TAG_RED, color.red);
        XMLWriter.XML(writer, level+1, TAG_GREEN, color.green);
        XMLWriter.XML(writer, level+1, TAG_BLUE, color.blue);
        XMLWriter.end(writer, level, tag_name);
        writer.println();
    }
}
