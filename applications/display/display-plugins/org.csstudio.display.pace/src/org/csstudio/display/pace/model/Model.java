/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pace.model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.csstudio.apputil.xml.DOMHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** PACE Data model
 *  @author Delphy Nypaver Armstrong
 *  @author Kay Kasemir
 *
 *    reviewed by Delphy 01/28/09
 */
public class Model
{
    // XML Tags
    private static final String XML_ROOT = "paceconfig"; //$NON-NLS-1$
    private static final String XML_TITLE = "title"; //$NON-NLS-1$
    private static final String XML_COLUMNS = "columns"; //$NON-NLS-1$
    private static final String XML_COLUMN = "column"; //$NON-NLS-1$
    private static final String XML_INSTANCES = "instances"; //$NON-NLS-1$
    private static final String XML_INSTANCE = "instance"; //$NON-NLS-1$

    /** Overall title */
    final private String title;

    /** Column definitions */
    final private ArrayList<Column> columns = new ArrayList<Column>();

    /** Instances, rows with cells */
    final private ArrayList<Instance> instances = new ArrayList<Instance>();

    /** Listener to be notified of model changes */
    final private CopyOnWriteArrayList<ModelListener> listeners =
        new CopyOnWriteArrayList<ModelListener>();

    /** Initialize model from XML file stream
     *  @param stream Stream for XML file
     *  @throws Exception on error: Missing XML elements, errors in macros,
     *          problems in PV creation
     */
    @SuppressWarnings("nls")
    public Model(final InputStream stream) throws Exception
    {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        final DocumentBuilder db = dbf.newDocumentBuilder();
        final Document doc = db.parse(stream);

        // Check root element
        final Element root_node = doc.getDocumentElement();
        root_node.normalize();
        final String root_name = root_node.getNodeName();
        if (!root_name.equals(XML_ROOT))
            throw new Exception("Got " + root_name + " instead of 'paceconfig'");

        // Using org.csstudio.apputil.xml.DOMHelper plugin for parsing the XML file

        // Get Title
        title = DOMHelper.getSubelementString(root_node, XML_TITLE);

        // Read column definitions: Locate list of columns
        final Element cols_node = DOMHelper.findFirstElementNode(root_node.getFirstChild(), XML_COLUMNS);
        if (cols_node == null)
            return; // empty file? Is that an error or just empty?
        // Traverse down to first column definition, loop over them
        Element col_node =
            DOMHelper.findFirstElementNode(cols_node.getFirstChild(), XML_COLUMN);
        while (col_node != null)
        {
            final Column column = Column.fromDOM(col_node);
            columns.add(column);
            col_node = DOMHelper.findNextElementNode(col_node, XML_COLUMN);
        }

        // Locate instance definitions
        final Element insts_node = DOMHelper.findFirstElementNode(root_node.getFirstChild(), XML_INSTANCES);
        if (insts_node == null)
            return;
        // Traverse down to first instance definition, loop over them
        Element inst_node =
            DOMHelper.findFirstElementNode(insts_node.getFirstChild(), XML_INSTANCE);
        while (inst_node != null)
        {
            final Instance instance = Instance.fromDOM(this, inst_node);
            instances.add(instance);
            inst_node = DOMHelper.findNextElementNode(inst_node, XML_INSTANCE);
        }

        // Create cells, passing exceptions back up
        for (Instance instance : instances)
            instance.createCells(columns);
    }

    /** @param listener Listener to add */
    public void addListener(final ModelListener listener)
    {
        listeners.add(listener);
    }

    /** @param listener Listener to remove */
    public void removeListener(final ModelListener listener)
    {
        listeners.remove(listener);
    }

    /** @return Overall title of the Model */
    public String getTitle()
    {
        return title;
    }

    /** @return The number of columns. */
    public int getColumnCount()
    {
        return columns.size();
    }

    /** @param i Index 0 .. getColumnCount()-1
     *  @return Column at given index
     */
    public Column getColumn(final int i)
    {
        return columns.get(i);
    }

    /** @return The number of rows in the table. */
    public int getInstanceCount()
    {
        return instances.size();
    }

    /** @param i Index 0 .. getInstanceCount()-1
     *  @return Instance at given index
     */
    public Instance getInstance(final int i)
    {
        return instances.get(i);
    }

    /** @return <code>true</code> if any cell has been edited. */
    public boolean isEdited()
    {
        for (Instance instance : instances)
            for (int c = 0; c < getColumnCount(); c++)
                if (instance.getCell(c).isEdited())
                    return true;
        return false;
    }

    /** Start the PV connections of all cells in model
     *  @throws Exception on error
     */
    public void start() throws Exception
    {
        for (Instance instance : instances)
            for (int c = 0; c < getColumnCount(); c++)
                instance.getCell(c).start();
    }

    /** Stop the PV connections of all cells in model */
    public void stop()
    {
        for (Instance instance : instances)
            for (int c = 0; c < getColumnCount(); c++)
                instance.getCell(c).stop();
    }

    /** Save values entered by user to the PVs.
     *  Any cells with 'user' values meant to replace
     *  the original PV value get written to the PV.
     *  @param user_name Name of the user to be logged for cells with
     *                   a last user meta PV
     *  @throws Exception on error writing to the PV
     */
    public void saveUserValues(final String user_name) throws Exception
    {
        for (Instance instance : instances)
        {
            for (int c = 0; c < getColumnCount(); c++)
                instance.getCell(c).saveUserValue(user_name);
        }
    }

    /** Restore cells to state before trying to save user values
     *  @throws Exception on error writing to the PV
     */
    public void revertOriginalValues() throws Exception
    {
        for (Instance instance : instances)
        {
            for (int c = 0; c < getColumnCount(); c++)
                instance.getCell(c).revertOriginalValue();
        }
    }

    /** Clear all user values.
     *  <p>Meant to be called after user values have been saved,
     *  or to globally clear anything entered.
     */
    public void clearUserValues()
    {
        for (Instance instance : instances)
        {
            for (int c = 0; c < getColumnCount(); c++)
                instance.getCell(c).clearUserValue();
        }
    }

    /** Notify listeners of cell update
     *  @param cell Cell that changed
     */
    void fireCellUpdate(final Cell cell)
    {
        for (ModelListener listener : listeners)
            listener.cellUpdate(cell);
    }

    /** @return Info string for debugging */
    @SuppressWarnings("nls")
    @Override
    public String toString()
    {
        return "PACE Model '" + title + "'";
    }
}
