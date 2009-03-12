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
    
    final ArrayList<String> CmtMacroStr = new ArrayList<String>();
    
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
    
    /** @param comment cell to add */
    public void addComment(final String pvStr)
    {
       CmtMacroStr.add(pvStr);
    }

    /** @param comment cell to add */
    public boolean isComment(final String pvStr)
    {
       for (Instance instance : instances)
          for (int c = 0; c < getColumnCount(); c++)
             for (String MacroStr : CmtMacroStr)
                if(MacroStr.equals(pvStr)){
                   System.out.println(pvStr);
                   return true;
                }
      return false;
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
            {
               instance.getCell(c).start();
      // If the cell has a comment start the pvs containing the name of
      // the person making the change and the date of the change.
               if(instance.getCell(c).hasComments())
               {
                  instance.getCell(c).name_pv.start();
                  instance.getCell(c).date_pv.start();
                  instance.getCell(c).comment_pv.start();
               }
            }
    }

    /** Stop the PV connections of all cells in model */
    public void stop()
    {
        for (Instance instance : instances)
            for (int c = 0; c < getColumnCount(); c++)
            {
                instance.getCell(c).stop();
         // If the cell has a comment stop the pvs containing the name of
         // the person making the change and the date of the change.
                if(instance.getCell(c).hasComments())
                {
                  instance.getCell(c).name_pv.stop();
                  instance.getCell(c).date_pv.stop();
                  instance.getCell(c).comment_pv.stop();
                }
            }
    }

    /** Save values entered by user to the PVs.
     *  Any cells with 'user' values meant to replace
     *  the original PV value gets written to the PV.
     *  @throws Exception on error writing to the PV
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
