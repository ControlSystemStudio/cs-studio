/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pvtable.model;

import java.io.InputStream;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.csstudio.apputil.xml.DOMHelper;
import org.csstudio.display.pvtable.Plugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** A PV List model.
 *  <p>
 *  Maintains a list of PVs, handles add/remove/start/stop etc.
 *  <p>
 *  This model subscribes to PV changes.
 *  But instead of passing them on right away to PVListModelListeners,
 *  it marks entries as changed and only invokes listeners from a periodic
 *  thread. This is meant to throttle the rate of GUI updates.
 *
 *  @author Kay Kasemir
 */
public class PVListModel
{
    private static final String file_header = "<pvtable version=\"1.0\">\n"; //$NON-NLS-1$
    private static final String file_tail = "</pvtable>\n"; //$NON-NLS-1$
	private static final double DEFAULT_TOLERANCE = 0.001;

    private String description = ""; //$NON-NLS-1$
    private double tolerance = DEFAULT_TOLERANCE;
    private boolean isRunning = false;

    /** The list of entries that's the "data" of this model. */
    private CopyOnWriteArrayList<PVListModelEntry> entries =
        new CopyOnWriteArrayList<PVListModelEntry>();

    /** The listeners to the structure of this model. */
    private CopyOnWriteArrayList<PVListModelListener> model_listeners
         = new CopyOnWriteArrayList<PVListModelListener>();

    /** Thread that periodically checks for pending value redraws. */
    private PVListModelValueUpdateThread update_thread;

    /** Create a new list model. */
    public PVListModel()
    {
        update_thread = new PVListModelValueUpdateThread(this);
    }

    /** Set a new description.
     *  <p>
     *  Does not notify listeners...
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /** @return Returns the current description. */
    public String getDescription()
    {   return description; }

    /** Set a new tolerance. */
    public void setTolerance(double tolerance)
    {
        this.tolerance = tolerance;
        fireEntriesChanged();
    }

    /** @return Returns the current tolerance. */
    public double getTolerance()
    {   return tolerance; }

    /** Must be invoked when 'done' by the creator of the model. */
    public void dispose()
    {
        update_thread.dispose();
        while (! entries.isEmpty())
            removeEntry(0);
        entries.clear();
        entries = null;
    }

    /** @return Returns <code>true</code> if this model has been disposed. */
    public boolean isDisposed()
    {   return entries == null;  }

    /** Add a listener
     * @param listener The new listener
     */
    public void addModelListener(PVListModelListener listener)
    {
        model_listeners.addIfAbsent(listener);
    }

    /** Remove a listener
     * @param listener The listener to remove
     */
    public void removeModelListener(PVListModelListener listener)
    {
        model_listeners.remove(listener);
    }

    /** @return Returns number of entries (rows) in model. */
    public int getEntryCount()
    {   return entries.size();  }

    /** @return Returns entry (row). */
	public PVListEntry getEntry(int i)
	{
        if (i >= 0  &&  i < entries.size())
            return entries.get(i);
        return null;
	}

	/** Add a PV.
	 *  <p>
	 *  Duplicates are ignored, i.e. the pv_name must not already be used
     *  for any primary PV in the table. Use as readback PV is OK.
	 */
	public void addPV(String pv_name)
	{
        // Avoid duplicate PV entries for the 'main' PV of entries.
        for (PVListEntry e : entries)
            if (e.getPV().getName().equals(pv_name))
                return;

        PVListModelEntry entry = silentlyAddPV(true,
                        pv_name, new SavedValue(),
                        null, new SavedValue());
        if (isRunning)
            entry.start();
        fireEntryAdded(entry);
	}

    /** Add PV without checking for duplicates and without informing listeners. */
    private PVListModelEntry silentlyAddPV(boolean selected,
                    String pv_name, SavedValue saved_value,
                    String readback_name, SavedValue readback_value)
    {
        PVListModelEntry entry = new PVListModelEntry(selected,
                pv_name, saved_value,
                readback_name, readback_value);
        entries.add(entry);
        return entry;
    }

    /** Remove an entry. */
    public void removeEntry(PVListEntry entry)
    {
        int i = entries.indexOf(entry);
        if (i < 0)
        {
            System.err.println("PVListModel.removeEntry: Cannot find " + entry); //$NON-NLS-1$
            return;
        }
        removeEntry(i);
    }

    /** Remove an entry. */
    private void removeEntry(int i)
    {
        PVListModelEntry entry = entries.remove(i);
        entry.dispose();
        // Notify Listeners
        for (PVListModelListener listener : model_listeners)
            listener.entryRemoved(entry);
    }

    /** Set a new update period. */
    public void setUpdatePeriod(double seconds)
    {
        update_thread.setDelay((int)(seconds * 1000));
     }

    /** @return The update period in seconds. */
    public double getUpdatePeriod()
    {   return update_thread.getDelay() / 1000.0; }

    /** Set or clear the selection state of the given entry.
     *  @see PVListEntry#setSelected()
     */
    public void setSelected(PVListEntry entry_to_change, boolean new_state)
    {
        PVListModelEntry entry = (PVListModelEntry) entry_to_change;
        entry.setSelected(new_state);
        fireEntriesChanged();
    }

    @SuppressWarnings("nls")
    public void modifyReadbackPV(PVListEntry entry_to_change, String new_readback_name)
    {
        PVListModelEntry entry = (PVListModelEntry) entry_to_change;
        if (entries.indexOf(entry) < 0)
        {
            System.err.println("PVListModel.modifyReadbackPV: Cannot find " + entry);
            return;
        }
        entry.setReadbackPV(new_readback_name);
        fireEntriesChanged();
    }

    /** Load model from XML file stream. */
    public void load(InputStream stream) throws Exception
    {
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder();
        Document doc = docBuilder.parse(stream);
        loadFromDocument(doc);
    }

    /** Load model from DOM document. */
    @SuppressWarnings("nls")
    private void loadFromDocument(Document doc) throws Exception
    {
        boolean was_running = isRunning;
        if (was_running)
            stop();
        entries.clear();

        Exception error = null;
        try
        {
            // Check if it's a <pvtable/>.
            doc.getDocumentElement().normalize();
            Element root_node = doc.getDocumentElement();
            String root_name = root_node.getNodeName();
            if (!root_name.equals("pvtable"))
                throw new Exception("Expected <pvtable>, found <" + root_name
                        + ">");
            // Get the <description> entry
            try
            {
                description = DOMHelper.getSubelementString(root_node, "description");
            }
            catch (Exception e)
            {
                description = "";
            }
            // Get the <tolerance> entry
            try
            {
                tolerance = DOMHelper.getSubelementDouble(root_node, "tolerance");
            }
            catch (Exception e)
            {
                tolerance = DEFAULT_TOLERANCE;
            }
            // Get the <update_period> entry
            try
            {
                setUpdatePeriod(
                        DOMHelper.getSubelementDouble(root_node, "update_period"));
            }
            catch (Exception e)
            {
                Plugin.getLogger().log(Level.SEVERE, "Period update", e);
            }

            // Get the <pvlist> entry
            Element pvlist = DOMHelper.findFirstElementNode(root_node
                    .getFirstChild(), "pvlist");
            if (pvlist != null)
            {
                Element pv = DOMHelper.findFirstElementNode(pvlist.getFirstChild(),
                        "pv");
                while (pv != null)
                {
                    String sel_txt= DOMHelper.getSubelementString(pv, "selected");
                    boolean selected =
                        sel_txt.length() < 1  ||  sel_txt.equals("true");
                    String pv_name = DOMHelper.getSubelementString(pv, "name");
                    SavedValue saved = SavedValue.fromString(
                             DOMHelper.getSubelementString(pv, "saved_value"));
                    String readback_name =
                        DOMHelper.getSubelementString(pv, "readback");
                    SavedValue readback = SavedValue.fromString(
                          DOMHelper.getSubelementString(pv, "readback_value"));
                    silentlyAddPV(selected, pv_name, saved,
                            	  readback_name, readback);
                    pv = DOMHelper.findNextElementNode(pv, "pv");
                }
            }
        }
        catch (Exception e)
        {
            error = e;
        }
        // Notify Listeners
        fireEntriesChanged();
        if (was_running)
            start();
        // If there was an error, pass back up
        if (error != null)
            throw error;
    }

    /* Eclipse likes to deal with files as resources.
     * When we write files directly via java.io.File,
     * the workbench gets out of sync (needs 'refresh').
     *
     * However, the eclipse IFile can only create files
     * via create(InputStream ..) or setContents(InputStream, ...).
     *
     * How do we get this model as an InputStream?
     * Look at PipedOutputStream -> PipedInputStream?
     * That requires another thread (true?).
     * So for now the lame implementation is this:
     * Write everything into a string buffer,
     * create stream from that.
     * --> Could be hard on memory?
     */

    /** @return Returns the whole model as an XML string. */
    @SuppressWarnings("nls")
    public String getXMLContent()
    {
        StringBuffer b = new StringBuffer(1024);
        b.append(file_header);
        b.append("    <description>" + description + "</description>\n");
        b.append("    <tolerance>" + tolerance + "</tolerance>\n");
        b.append("    <update_period>" + getUpdatePeriod() + "</update_period>\n");
        b.append("    <pvlist>\n");
        for (PVListModelEntry entry : entries)
        {
            b.append("        ");
            b.append(entry.toXML());
            b.append("\n");
        }
        b.append("    </pvlist>\n");
        b.append(file_tail);
        String s = b.toString();
        return s;
    }

    /** Subscribe to the PVs, start updates. */
    public void start()
    {
        if (isRunning)
            return;
        for (PVListModelEntry entry : entries)
            entry.start();
        isRunning = true;
        for (PVListModelListener listener : model_listeners)
            listener.runstateChanged(true);
    }

    /** @return Returns <code>true</code> if running.
     *  @see #start()
     *  @see #stop()
     */
    public boolean isRunning()
    {   return isRunning; }

    /** Unsubscribe from PVs, stop updates. */
    public void stop()
    {
        if (!isRunning)
            return;
        for (PVListModelEntry entry : entries)
            entry.stop();
        isRunning = false;
        for (PVListModelListener listener : model_listeners)
            listener.runstateChanged(false);
    }

    /** Take a snapshot of current values. */
    public void takeSnapshot()
    {
        for (PVListModelEntry entry : entries)
            entry.takeSnapshot();
        fireEntriesChanged();
    }

    /** Restore values from snapshot. */
    public void restore() throws Exception
    {
        for (PVListModelEntry entry : entries)
            entry.restore();
    }

    /** Send the entryAdded event */
    private void fireEntryAdded(PVListEntry entry)
    {
        for (PVListModelListener listener : model_listeners)
            listener.entryAdded(entry);
    }

    /** Send the entriesChanged event */
    private void fireEntriesChanged()
    {
        for (PVListModelListener listener : model_listeners)
            listener.entriesChanged();
    }

    /** Notify listeners of all entries that have new values.
     *  <p>
     *  Typically invoked by the value update thread, not directly.
     */
    public void updateAnyChangedEntries()
    {
        boolean anything = false;
        for (PVListModelEntry entry : entries)
        {
            if (entry.testAndResetNewValues() > 0)
            {
                // Used to send special per-entry event in here,
                // but simply redrawing the whole table turned
                // out to be cheaper...
                anything = true;
                break;
            }
        }
        if (anything)
            for (PVListModelListener listener : model_listeners)
                listener.valuesUpdated();
    }
}
