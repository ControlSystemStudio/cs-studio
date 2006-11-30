/**
 * 
 */
package org.csstudio.diag.epics.pvtree;

import java.util.ArrayList;

import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.utility.pv.EPICS_V3_PV;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVListener;
import org.csstudio.utility.pv.PVValue;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.swt.widgets.Display;

/** One item in the PV tree model.
 *  <p>
 *  Since an 'item' is a PV, possibly for a record
 *  which has inputs, and those inputs is what we
 *  want to drill down, this class currently includes
 *  almost all the logic behind the tree creation.
 *  
 *  @author Kay Kasemir
 */
class PVTreeItem extends PlatformObject implements IProcessVariable
{
    private static final String input_types[] =
    {
        "ai", "aai", "bi","mbbiDirect", "mbbi",
        "mbboDirect","longin", "waveform", "subArray", "stringin",
    };
    private static final String output_types[] =
    {
        "ao", "aao", "bo", "mbbo",  "longout", "stringout", "fanout",
    };    
    // TODO: Handle "sub", "genSub", "compress",
    // "event", "histogram", "permissive", "sel", "seq", "state",
   
   /** The model to which this whole tree belongs. */
    private PVTreeModel model;

    /** The parent of this item, or <code>null</code>. */
    private PVTreeItem parent;

    /** The info provided by the parent or creator ("PV", "INPA", ...) */
    private String info;

    /** The name of this PV tree item as shown in the tree. */
    private String pv_name;

    /** The name of the record.
     *  <p>
     *  For example, the 'name' could be 'fred.SEVR', then 'fred'
     *  would be the record name.
     */
    private String record_name;
    
    /** The PV used for getting the current value. */
    private PV pv;
    private String value = null;
    private int severity_code;
    private PVListener pv_listener = new PVListener()
    {
        public void pvDisconnected(PV pv)
        {
            value = "<disconnected>";
            updateValue();
        }
        public void pvValueUpdate(PV pv)
        {
            try
            {
                value = PVValue.toString(pv.getValue());
                severity_code = pv.getSeverityCode();
                if (severity_code != 0)
                    value = value + " [" + pv.getSeverity() + "]";
                updateValue();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };
    
    /** The PV used for getting the record type. */
    private PV type_pv;
    private String type;
    private PVListener type_pv_listener = new PVListener()
    {
        public void pvDisconnected(PV pv)
        {}
        public void pvValueUpdate(PV pv)
        {
            try
            {
                type = PVValue.toString(pv.getValue());
                updateType();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };
    
    /** Used to read the links of this pv. */
    private int input_index;
    private PV link_pv = null;
    private String link_value;
    private PVListener link_pv_listener = new PVListener()
    {
        public void pvDisconnected(PV pv)
        {}
        public void pvValueUpdate(PV pv)
        {
            try
            {
                link_value = PVValue.toString(pv.getValue());
                // The value could be
                // a) a record name followed by "... NPP NMS". Remove that.
                // b) a hardware input/output "@... " or "#...". Keep that.
                if (link_value.length() > 1 &&
                    link_value.charAt(0) != '@' &&
                    link_value.charAt(0) != '#')
                {
                    int i = link_value.indexOf(' ');
                    if (i > 0)
                        link_value = link_value.substring(0, i);
                }
                updateInput();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };
    
    /** Tree item children, populated with info from the input links. */
    private ArrayList<PVTreeItem> links = new ArrayList<PVTreeItem>();

    /** Create a new PV tree item.
     *  @param model The model to which this whole tree belongs.
     *  @param parent The parent of this item, or <code>null</code>.
     *  @param info The info provided by the parent or creator ("PV", "INPA", ...)
     *  @param pv_name The name of this PV entry.
     */
    public PVTreeItem(PVTreeModel model,
            PVTreeItem parent,
            String info,
            String pv_name)
    {
        System.out.println("New Tree item " + pv_name);
        this.model = model;
        this.parent = parent;
        this.info = info;
        this.pv_name = pv_name;
        this.type = null;
        
        // Split the record name off.
        int sep = pv_name.lastIndexOf('.');
        if (sep > 0)
            record_name = pv_name.substring(0, sep);
        else
            record_name = pv_name;

        // Avoid loops.
        // If the model already contains an entry with this name,
        // we simply display this new item, but we won't
        // follow its input links.
        PVTreeItem other = model.findPV(pv_name);

        // Now add this one, otherwise the previous call would have found 'this'.
        if (parent != null)
            parent.links.add(this);

        try
        {
            pv = new EPICS_V3_PV(pv_name);        
            pv.addListener(pv_listener);
            pv.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        // Get type from 'other', previously used PV or via CA
        if (other != null)
        {
            type = other.type;
            System.out.println("Known item, not traversing inputs (again)");
        }
        else
        {
            try
            {
                type_pv = new EPICS_V3_PV(record_name + ".RTYP", true);
                type_pv.addListener(type_pv_listener);
                type_pv.start();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /** Dispose this and all child entries. */
    public void dispose()
    {
        for (PVTreeItem item : links)
            item.dispose();
        pv.removeListener(pv_listener);
        pv.stop();
        pv = null;
        disposeLinkPV();
        disposeTypePV();
    }

    private void disposeTypePV()
    {
        if (type_pv != null)
        {
            type_pv.removeListener(type_pv_listener);
            type_pv.stop();
            type_pv = null;
        }
    }

    private void disposeLinkPV()
    {
        if (link_pv != null)
        {
            link_pv.removeListener(link_pv_listener);
            link_pv.stop();
            link_pv = null;
        }
    }
    
    /** @return Returns the name of this PV. */
    public String getName()
    {   return pv_name; }
    
    // @see IProcessVariable
    public String getTypeId()
    {   return IProcessVariable.TYPE_ID;    }

    /** @return Returns the severity code of this PV's value. */
    public int getSeverityCode()
    {
        return severity_code;
    }
    
    /** @return Returns the record type of this item or <code>null</code>. */
    public String getType()
    {
        return type;
    }
    
    /** @return Returns the parent or <code>null</code>. */
    public PVTreeItem getParent()
    {
        return parent;
    }

    /** @return Returns the first link or <code>null</code>. */
    public PVTreeItem getFirstLink()
    {
        if (links.size() > 0)
            return links.get(0);
        return null;
    }
    
    /** @return Returns the all links. */
    public PVTreeItem[] getLinks()
    {
        return (PVTreeItem[]) links.toArray(new PVTreeItem[links.size()]);
    }

    /** @return Returns <code>true</code> if this item has any links. */
    public boolean hasLinks()
    {
        return links.size() > 0;
    }

    /** @return Returns a String. No really, it does! */
    public String toString()
    {
        StringBuffer b = new StringBuffer();
        b.append(info);
        b.append(" '");
        b.append(pv_name);
        b.append("'");
        if (type != null)
        {
            b.append("  (");
            b.append(type);
            b.append(")");
        }
        if (value != null)
        {
            b.append("  =  '");
            b.append(value);
            b.append("'");
        }
        return b.toString();
    }

    /** Thread-save handling of the 'value' update. */
    private void updateValue()
    {
        Display.getDefault().asyncExec(new Runnable()
        {
            public void run()
            {   // Display the received type of this record.
                model.itemUpdated(PVTreeItem.this);
            }
        });
    }

    /** Thread-save handling of the 'type' update. */
    private void updateType()
    {
        System.out.println(pv_name + " received type '" + type + "'");
        Display.getDefault().asyncExec(new Runnable()
        {
            public void run()
            {
                // We got the type, so close the connection.
                disposeTypePV();
                // Display the received type of this record.
                model.itemChanged(PVTreeItem.this);
                
                if (type.startsWith("calc"))
                    // Read the calc or calcout's first input
                    getCalcInput(0);
                else
                {   // read INP or DOL
                    for (String typ : input_types)
                        if (type.equals(typ))
                        {
                            getLink(record_name + ".INP");
                            return;
                        }
                    for (String typ : output_types)
                        if (type.equals(typ))
                        {
                            getLink(record_name + ".DOL");
                            return;
                        }
                }
            }
        });
    }
    
    /** Helper for reading a calc record's input link. */
    private void getCalcInput(int i)
    {
        input_index = i;
        String link_name = record_name +
                ".INP" + Character.toString((char)('A' + input_index));
        getLink(link_name);
    }

    /** Helper for reading any link by PV name. */
    private void getLink(String link_name)
    {
        disposeLinkPV();
        try
        {
            link_pv = new EPICS_V3_PV(link_name);
            link_pv.addListener(link_pv_listener);
            link_pv.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /** Thread-save handling of the 'input_value' update. */
    private void updateInput()
    {
        System.out.println(link_pv.getName() + " received '" + link_value + "'");
        Display.getDefault().asyncExec(new Runnable()
        {
            public void run()
            {
                boolean is_output = link_pv.getName().endsWith("DOL");
                disposeLinkPV();
                boolean is_calc = type.startsWith("calc");
                String info;
                if (is_output)
                    info = "DOL";
                else if (is_calc)
                    info = "INP" + Character.toString((char)('A' + input_index));
                else info = "INP";
                if (link_value.length() > 0)
                {
                    new PVTreeItem(model, PVTreeItem.this, info, link_value);
                    model.itemChanged(PVTreeItem.this);
                }
                if (is_calc && input_index < 11) // get INPB...INPL
                    getCalcInput(input_index + 1);
            }
        });
    }
}
