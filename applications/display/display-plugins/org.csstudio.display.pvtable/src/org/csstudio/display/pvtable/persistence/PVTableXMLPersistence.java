/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.persistence;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.csstudio.apputil.xml.DOMHelper;
import org.csstudio.apputil.xml.XMLWriter;
import org.csstudio.display.pvtable.Preferences;
import org.csstudio.display.pvtable.model.Measure;
import org.csstudio.display.pvtable.model.PVTableItem;
import org.csstudio.display.pvtable.model.PVTableModel;
import org.csstudio.display.pvtable.model.SavedArrayValue;
import org.csstudio.display.pvtable.model.SavedScalarValue;
import org.csstudio.display.pvtable.model.SavedValue;
import org.csstudio.display.pvtable.model.TimestampHelper;
import org.csstudio.display.pvtable.model.VTypeHelper;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.ValueFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** Persist PVTableModel as XML file
 *
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PVTableXMLPersistence extends PVTablePersistence
{
    /** File extension used for XML files */
    final public static String FILE_EXTENSION = "pvs";
    final private static String XML_HEADER = "<?xml version=\"1.0\"?>\n<pvtable version=\"3.0\">";
    final private static String XML_TAIL = "</pvtable>\n";
    final private static String ROOT = "pvtable";
    final private static String TOLERANCE = "tolerance";
    final private static String PVLIST = "pvlist";
    final private static String PV = "pv";
    final private static String SELECTED = "selected";
    final private static String NAME = "name";
    final private static String SAVED_VALUE = "saved_value";
    final private static String SAVED_ARRAY = "saved_array_value";
    final private static String SAVED_TIME = "saved_value_timestamp";
    final private static String TIME_MEASURE = "timestamp_measure";
    final private static String ITEM = "item";
    final private static String CONF = "conf";
    final private static String MEASURE = "measure";
    final private static String NBMEASURE = "nbMeasure";
    final private static String READBACK_NAME = "readback";
    final private static String READBACK_SAVED = "readback_value";
    final private static String COMPLETION = "completion";
    final private static String TIMEOUT = "timeout";

    /** {@inheritDoc} */
    @Override
    public String getFileExtension()
    {
        return FILE_EXTENSION;
    }

    /** {@inheritDoc} */
    @Override
    public PVTableModel read(final InputStream stream) throws Exception
    {
        final DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = docBuilder.parse(stream);
        return read(doc);
    }

    /** @param doc XML document
     *  @return PV table model
     *  @throws Exception on error
     */
    private PVTableModel read(final Document doc) throws Exception
    {
        final PVTableModel model = new PVTableModel();
        // Check if it's a <pvtable/>.
        doc.getDocumentElement().normalize();
        Element root_node = doc.getDocumentElement();
        String root_name = root_node.getNodeName();
        if (!root_name.equals(ROOT))
            throw new Exception("Expected <" + ROOT + ">, found <" + root_name + ">");

        // Get the default <tolerance> entry
        double default_tolerance = Preferences.getTolerance();
        try
        {
            default_tolerance = DOMHelper.getSubelementDouble(root_node, TOLERANCE);
        }
        catch (Exception ex)
        {
            default_tolerance = Preferences.getTolerance();
        }

        model.setCompletionTimeout((long) DOMHelper.getSubelementDouble(root_node, TIMEOUT, 60));

        // Get the <pvlist> entry
        Element pvlist = DOMHelper.findFirstElementNode(root_node.getFirstChild(), PVLIST);
        int nbMeasure = DOMHelper.getSubelementInt(root_node, NBMEASURE, 0);
        model.setNbMeasure(nbMeasure);
        if (pvlist != null)
        {
            Element pv = DOMHelper.findFirstElementNode(pvlist.getFirstChild(), PV);
            Measure measure = null;
            while (pv != null)
            {
                String pv_name = DOMHelper.getSubelementString(pv, NAME);
                if (!pv_name.isEmpty())
                {
                    final double tolerance = DOMHelper.getSubelementDouble(pv, TOLERANCE, default_tolerance);
                    final boolean selected = DOMHelper.getSubelementBoolean(pv, SELECTED, true);
                    String timeSaved = DOMHelper.getSubelementString(pv, SAVED_TIME, "");
                    String timeMeasure = DOMHelper.getSubelementString(pv, TIME_MEASURE, "");
                    boolean conf = DOMHelper.getSubelementBoolean(pv, CONF, false);
                    boolean isMeasure = DOMHelper.getSubelementBoolean(pv, MEASURE, false);
                    SavedValue saved = readSavedValue(pv);
                    PVTableItem pvItem = null;
                    if (pv_name.startsWith("#mesure#"))
                        pvItem = model.addItem(pv_name,
                                ValueFactory.newVString("", ValueFactory.newAlarm(AlarmSeverity.NONE, ""),
                                        ValueFactory.newTime(TimestampHelper.parse(timeMeasure))));
                    else
                        pvItem = model.addItem(pv_name, tolerance, saved, timeSaved, conf, measure);
                    pvItem.setSelected(selected);
                    pvItem.setUseCompletion(DOMHelper.getSubelementBoolean(pv, COMPLETION, false));

                    // Legacy files may contain a separate readback PV and value
                    // for this entry
                    pv_name = DOMHelper.getSubelementString(pv, READBACK_NAME);
                    // This legacy entry never supported arrays..
                    saved = new SavedScalarValue(DOMHelper.getSubelementString(pv, READBACK_SAVED));
                    if (isMeasure == true && model.getConfig() != null)
                    {
                        if (pvItem.isMeasureHeader())
                            measure = model.getConfig().addMeasure();
                        if (measure != null)
                        {
                            pvItem.setMeasure(measure);
                            measure.getItems().add(pvItem);
                        }
                    }
                    if (!pv_name.isEmpty())
                    {
                        // If found, add as separate PV, not selected to be
                        // restored
                        pvItem = model.addItem(pv_name, tolerance, saved, timeSaved, conf, measure);
                        pvItem.setSelected(false);
                    }
                }
                pv = DOMHelper.findNextElementNode(pv, PV);
            }
        }
        model.setNbMeasure(nbMeasure);
        return model;
    }

    /** @param pv PV element that might contain saved value, scalar or array
     *  @return {@link SavedValue} or <code>null</code>
     */
    private SavedValue readSavedValue(final Element pv)
    {
        final String saved_scalar = DOMHelper.getSubelementString(pv, SAVED_VALUE, null);
        if (saved_scalar != null)
            return new SavedScalarValue(saved_scalar);

        final Element saved_array = DOMHelper.findFirstElementNode(pv.getFirstChild(), SAVED_ARRAY);
        if (saved_array != null)
        {
            final List<String> items = new ArrayList<>();
            Element item = DOMHelper.findFirstElementNode(saved_array.getFirstChild(), ITEM);
            while (item != null)
            {
                items.add(item.getFirstChild().getNodeValue());
                item = DOMHelper.findNextElementNode(item, ITEM);
            }
            return new SavedArrayValue(items);
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void write(final PVTableModel model, final OutputStream stream)
    {
        final PrintWriter out = new PrintWriter(stream);
        out.println(XML_HEADER);
        XMLWriter.XML(out, 1, NBMEASURE, model.getNbMeasure());
        XMLWriter.XML(out, 1, TIMEOUT, model.getCompletionTimeout());
        XMLWriter.start(out, 1, PVLIST);
        out.println();
        final int N = model.getItemCount();
        for (int i = 0; i < N; ++i)
        {
            final PVTableItem item = model.getItem(i);
            XMLWriter.start(out, 2, PV);
            out.println();
            XMLWriter.XML(out, 3, SELECTED, item.isSelected());
            XMLWriter.XML(out, 3, NAME, item.getName());
            XMLWriter.XML(out, 3, TOLERANCE, item.getTolerance());
            XMLWriter.XML(out, 3, SAVED_TIME, item.getTime_saved());
            XMLWriter.XML(out, 3, TIME_MEASURE, TimestampHelper.format(VTypeHelper.getTimestamp(item.getValue())));
            XMLWriter.XML(out, 3, CONF, item.isConf());
            XMLWriter.XML(out, 3, MEASURE, item.isMeasure());
            final SavedValue saved = item.getSavedValue().orElse(null);
            if (saved instanceof SavedScalarValue)
                XMLWriter.XML(out, 3, SAVED_VALUE, saved.toString());
            else if (saved instanceof SavedArrayValue)
            {
                XMLWriter.start(out, 3, SAVED_ARRAY);
                out.println();
                final SavedArrayValue array = (SavedArrayValue) saved;
                final int AN = array.size();
                for (int el = 0; el < AN; ++el)
                    XMLWriter.XML(out, 4, ITEM, array.get(el));
                XMLWriter.end(out, 3, SAVED_ARRAY);
                out.println();
            }
            XMLWriter.XML(out, 3, COMPLETION, item.isUsingCompletion());
            XMLWriter.end(out, 2, PV);
            out.println();
        }
        XMLWriter.end(out, 1, PVLIST);
        out.println();
        out.println(XML_TAIL);
        out.flush();
        out.close();
    }
}
