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
     *  @throws Exception on error
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

        // Get Title
        title = DOMHelper.getSubelementString(root_node, XML_TITLE);
       
        // Read column definitions
        final Element cols_node = DOMHelper.findFirstElementNode(root_node.getFirstChild(), XML_COLUMNS);
        if (cols_node == null)
            return;
        Element col_node =
            DOMHelper.findFirstElementNode(cols_node.getFirstChild(), XML_COLUMN);
        while (col_node != null)
        {
            final Column column = Column.fromDOM(col_node);
            columns.add(column);
            col_node = DOMHelper.findNextElementNode(col_node, XML_COLUMN);
        }
        
        // Read instance definitions
        final Element insts_node = DOMHelper.findFirstElementNode(root_node.getFirstChild(), XML_INSTANCES);
        if (insts_node == null)
            return;
        Element inst_node =
            DOMHelper.findFirstElementNode(insts_node.getFirstChild(), XML_INSTANCE);
        while (inst_node != null)
        {
            final Instance instance = Instance.fromDOM(this, inst_node);
            instances.add(instance);
            inst_node = DOMHelper.findNextElementNode(inst_node, XML_INSTANCE);
        }
        
        // Create cells
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
    
    /** @return <code>true</code> if any call has been edited. */
    public boolean isEdited()
    {
        for (Instance instance : instances)
            for (int c = 0; c < getColumnCount(); c++)
                if (instance.getCell(c).isEdited())
                    return true;
        return false;
    }

    /** Start the PV connections 
     *  @throws Exception on error
     */
    public void start() throws Exception
    {
        for (Instance instance : instances)
            for (int c = 0; c < getColumnCount(); c++)
                instance.getCell(c).start();
    }

    /** Stop the PV connections */
    public void stop()
    {
        for (Instance instance : instances)
            for (int c = 0; c < getColumnCount(); c++)
                instance.getCell(c).stop();
    }

    
    /** Save values entered by user to PVs
     *  @throws Exception on error
     */
    public void saveUserValues() throws Exception
    {
        for (Instance instance : instances)
        {
            for (int c = 0; c < getColumnCount(); c++)
                instance.getCell(c).saveUserValue();
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
