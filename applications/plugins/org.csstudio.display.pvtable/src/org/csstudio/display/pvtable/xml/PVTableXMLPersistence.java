/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.xml;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.csstudio.apputil.xml.DOMHelper;
import org.csstudio.apputil.xml.XMLWriter;
import org.csstudio.display.pvtable.Preferences;
import org.csstudio.display.pvtable.model.PVTableItem;
import org.csstudio.display.pvtable.model.PVTableModel;
import org.csstudio.display.pvtable.model.VTypeHelper;
import org.epics.vtype.VType;
import org.epics.vtype.ValueFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** Persist PVTableModel as XML file
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class PVTableXMLPersistence
{
    final private static String XML_HEADER = "<?xml version=\"1.0\"?>\n<pvtable version=\"2.0\">";
    final private static String XML_TAIL = "</pvtable>\n";
    final private static String ROOT = "pvtable";
    final private static String TOLERANCE = "tolerance";
    final private static String PVLIST = "pvlist";
    final private static String PV = "pv";
    final private static String SELECTED = "selected";
    final private static String NAME = "name";
    final private static String SAVED = "saved_value";
    final private static String READBACK_NAME = "readback";
    final private static String READBACK_SAVED = "readback_value";
    
    /** @param stream XML stream
     *  @return PV table model
     *  @throws Exception on error
     */
    public static PVTableModel read(final InputStream stream) throws Exception
    {
        final DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder();
        Document doc = docBuilder.parse(stream);
        return read(doc);
    }

    /** @param doc XML document
     *  @return PV table model
     *  @throws Exception on error
     */
    private static PVTableModel read(final Document doc) throws Exception
    {
        final PVTableModel model = new PVTableModel();
        // Check if it's a <pvtable/>.
        doc.getDocumentElement().normalize();
        Element root_node = doc.getDocumentElement();
        String root_name = root_node.getNodeName();
        if (!root_name.equals(ROOT))
            throw new Exception("Expected <" + ROOT + ">, found <" + root_name
                    + ">");
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

        // Get the <pvlist> entry
        Element pvlist = DOMHelper.findFirstElementNode(root_node
                .getFirstChild(), PVLIST);
        if (pvlist != null)
        {
            Element pv = DOMHelper.findFirstElementNode(pvlist.getFirstChild(),
                    PV);
            while (pv != null)
            {
                String pv_name = DOMHelper.getSubelementString(pv, NAME);
                if (! pv_name.isEmpty())
                {
                    final double tolerance = DOMHelper.getSubelementDouble(pv, TOLERANCE, default_tolerance);
                    final boolean selected = DOMHelper.getSubelementBoolean(pv, SELECTED, true);
                    VType saved = createValue(DOMHelper.getSubelementString(pv, SAVED));
                    PVTableItem item = model.addItem(pv_name, tolerance, saved);
                    item.setSelected(selected);
                    
                    // Legacy files may contain a separate readback PV and value for this entry
                    pv_name = DOMHelper.getSubelementString(pv, READBACK_NAME);
                    saved = createValue(DOMHelper.getSubelementString(pv, READBACK_SAVED));
                    if (! pv_name.isEmpty())
                    {   // If found, add as separate PV, not selected to be restored
                        item = model.addItem(pv_name, tolerance, saved);
                        item.setSelected(false);
                    }
                }
                pv = DOMHelper.findNextElementNode(pv, PV);
            }
        }
        return model;
    }

    /** @param value_text Text of a value
     *  @return VType for that text, either VNumber(VDouble) or VString, or <code>null</code>
     */
    private static VType createValue(final String value_text)
    {
        if (value_text.isEmpty())
            return null;
        // Try to parse as number
        try
        {
            final double value = Double.parseDouble(value_text);
            return ValueFactory.newVDouble(value);
        }
        catch (NumberFormatException ex)
        {
            // Not a number, fall through to return VString
        }
        return ValueFactory.newVString(value_text, ValueFactory.alarmNone(), ValueFactory.timeNow());
    }

    /** @param model PV table model
     *  @param stream Stream to which model is written as XML
     */
    public static void write(final PVTableModel model, final OutputStream stream)
    {
        final PrintWriter out = new PrintWriter(stream);

        out.println(XML_HEADER);
        
        XMLWriter.start(out, 1, PVLIST);
        out.println();
        
        final int N = model.getItemCount();
        for (int i=0; i<N; ++i)
        {
            final PVTableItem item = model.getItem(i);
            XMLWriter.start(out, 2, PV);
            out.println();
            XMLWriter.XML(out, 3, SELECTED, true);
            XMLWriter.XML(out, 3, NAME, item.getName());
            XMLWriter.XML(out, 3, TOLERANCE, item.getTolerance());
            final VType saved = item.getSavedValue();
            if (saved != null)
                XMLWriter.XML(out, 3, SAVED, VTypeHelper.toString(saved));
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
