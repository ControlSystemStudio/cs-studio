/**
 * 
 */
package org.csstudio.diag.epics.pvtree;

import java.util.ArrayList;

import org.csstudio.platform.data.ISeverity;
import org.csstudio.platform.data.IValue;
import org.csstudio.platform.data.ValueUtil;
import org.csstudio.platform.model.IProcessVariable;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;
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
    static final boolean debug = false;
    
    /** sub record type */
    private static final String SUB = "sub"; //$NON-NLS-1$

    /** genSub record type */
    private static final String GENSUB = "genSub"; //$NON-NLS-1$

    /** calc record type */
    private static final String CALC = "calc"; //$NON-NLS-1$

    /** seq record type */
    private static final String SEQ = "seq"; //$NON-NLS-1$

    // NOTE: Keep the type names in these arrays alphabetical,
    //       just in case we later want to use binary search!
    @SuppressWarnings("nls")
    private static final String input_types[] =
    {
        "aai",
        "ai",
        "bi",
        "compress",
        "longin",
        "mbbi",
        "mbbiDirect",
        "mbboDirect",
        "stringin",
        "subArray",
        "waveform",
    };
    @SuppressWarnings("nls")
    private static final String output_types[] =
    {
        "aao",
        "ao",
        "bo",
        "fanout",
        "longout",
        "mbbo",
        "stringout",
    };    
    // TODO: Handle "event", "histogram", "permissive", "sel", "state"?
   
    /** INP field */
    private static final String INP = "INP"; //$NON-NLS-1$

    /** SELN field */
    private static final String SELN = "SELN"; //$NON-NLS-1$

    /** DOL field */
    private static final String DOL = "DOL"; //$NON-NLS-1$

   /** The model to which this whole tree belongs. */
    private final PVTreeModel model;

    /** The parent of this item, or <code>null</code>. */
    private final PVTreeItem parent;

    /** The info provided by the parent or creator ("PV", "INPA", ...) */
    private final String info;

    /** The name of this PV tree item as shown in the tree. */
    private final String pv_name;

    /** The name of the record.
     *  <p>
     *  For example, the 'name' could be 'fred.SEVR', then 'fred'
     *  would be the record name.
     */
    private final String record_name;
    
    /** The PV used for getting the current value. */
    private PV pv;
    
    /** Most recent value. */
    private volatile String value = null;
   
    /** Most recent severity. */
    private volatile ISeverity severity = null;
    
    private PVListener pv_listener = new PVListener()
    {
        public void pvDisconnected(PV pv)
        {
            value = "<disconnected>"; //$NON-NLS-1$
            severity = null;
            updateValue();
        }
        @SuppressWarnings("nls")
        public void pvValueUpdate(PV pv)
        {
            try
            {
                IValue pv_value = pv.getValue();
                value = ValueUtil.formatValueAndSeverity(pv_value);
                severity = pv_value.getSeverity();
                updateValue();
            }
            catch (Exception e)
            {
                Plugin.logException("pvValueUpdate", e);
            }
        }
    };
    
    /** The PV used for getting the record type. */
    private PV type_pv;
    private String type;
    private PVListener type_pv_listener = new PVListener()
    {
        public void pvDisconnected(PV pv)
        {
            // NOP
        }
        public void pvValueUpdate(PV pv)
        {
            try
            {
                type = pv.getValue().format();
                updateType();
            }
            catch (Exception e)
            {
                Plugin.logException("pvValueUpdate", e); //$NON-NLS-1$
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
        {
            // NOP
        }
        public void pvValueUpdate(PV pv)
        {
            try
            {
                link_value = pv.getValue().format();
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
                Plugin.logException("pvValueUpdate", e); //$NON-NLS-1$
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
        this.model = model;
        this.parent = parent;
        this.info = info;
        this.pv_name = pv_name;
        this.type = null;
        
        // In case this is "record.field", get the record name.
        int sep = pv_name.lastIndexOf('.');
        if (sep > 0)
            record_name = pv_name.substring(0, sep);
        else
            record_name = pv_name;

        if (debug)
        {
            System.out.print("New Tree item '" + pv_name + "'"); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println(", record name '" + record_name + "'"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        // Avoid loops.
        // If the model already contains an entry with this name,
        // we simply display this new item, but we won't
        // follow its input links.
        PVTreeItem other = model.findPV(pv_name);
        
        // Now add this one, otherwise the previous call would have found 'this'.
        if (parent != null)
            parent.links.add(this);

        // Is this a link to follow, or just a constant to display?
        // Hardware links "@vme..." or constant numbers "-12.3"
        // cause us to stop here:
        if (pv_name.startsWith("@") || pv_name.matches("^-?[0-9]"))  //$NON-NLS-1$//$NON-NLS-2$
        {
            pv = null;
            return;
        }

        // Try to read the pv
        try
        {
            pv = createPV(pv_name);        
            pv.addListener(pv_listener);
            pv.start();
        }
        catch (Exception e)
        {
            Plugin.logException("PV creation error", e); //$NON-NLS-1$
        }
        // Get type from 'other', previously used PV or via CA
        if (other != null)
        {
            type = other.type;
            if (debug)
                System.out.println("Known item, not traversing inputs (again)"); //$NON-NLS-1$
        }
        else
        {
            try
            {
                type_pv = createPV(record_name + ".RTYP"); //$NON-NLS-1$
                type_pv.addListener(type_pv_listener);
                type_pv.start();
            }
            catch (Exception e)
            {
                Plugin.logException("PV creation error", e); //$NON-NLS-1$
            }
        }
    }
    
    /** Dispose this and all child entries. */
    public void dispose()
    {
        for (PVTreeItem item : links)
            item.dispose();
        if (pv != null)
        {
            pv.removeListener(pv_listener);
            pv.stop();
            pv = null;
        }
        disposeLinkPV();
        disposeTypePV();
    }
    
    /** @return PV for the given name */
    @SuppressWarnings("nls")
    private PV createPV(final String name)
    {
        try
        {
            return PVFactory.createPV(name);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Plugin.logException("Cannot create PV '" + name + "'", ex);
        }
        return null;
    }
    
    /** Delete the type_pv */
    private void disposeTypePV()
    {
        if (type_pv != null)
        {
            type_pv.removeListener(type_pv_listener);
            type_pv.stop();
            type_pv = null;
        }
    }

    /** Delete the link_pv */
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
    
    /** @return Severity of current value. May be <code>null</code>. */
    public ISeverity getSeverity()
    {   return severity; }
    
    // @see IProcessVariable
    public String getTypeId()
    {   return IProcessVariable.TYPE_ID;    }

    /** @return Returns the record type of this item or <code>null</code>. */
    public String getType()
    {   return type;    }
    
    /** @return Returns the parent or <code>null</code>. */
    public PVTreeItem getParent()
    {   return parent;    }

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
    @SuppressWarnings("nls")
    @Override
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
            b.append("  =  ");
            b.append(value);
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
    @SuppressWarnings("nls")
    private void updateType()
    {
        if (debug)
            System.out.println(pv_name + " received type '" + type + "'");
        Display.getDefault().asyncExec(new Runnable()
        {
            public void run()
            {
                // Already disposed?
                if (type_pv == null)
                    return;
                // We got the type, so close the connection.
                disposeTypePV();
                // Display the received type of this record.
                model.itemChanged(PVTreeItem.this);
                
                if (isCalcType(type))
                    // Read the sub, calc or calcout's first input
                    getCalcInput(0);
                else
                {   // read INP?
                    for (String typ : input_types)
                        if (type.equals(typ))
                        {
                            getLink(record_name + "." + INP);
                            return;
                        }
                    // read DOL?
                    for (String typ : output_types)
                        if (type.equals(typ))
                        {
                            getLink(record_name + "." + DOL);
                            return;
                        }
                    if (type.equals(SEQ))
                    {
                        getLink(record_name + "." + SELN);
                        return;
                    }
                    // Give up
                    Plugin.logError("Unknown record type '" + type + "'");
                }
            }
        });
    }
    
    /** @return <code>true</code> if record type is like a calc record,
     *          i.e. has INPA, INPB, ... INPL.
     */
    private boolean isCalcType(final String type)
    {
        return type.equals(SUB)
            || type.equals(GENSUB)
            || type.startsWith(CALC);
    }
    
    /** Helper for reading a calc record's input link. */
    @SuppressWarnings("nls")
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
            link_pv = createPV(link_name);
            link_pv.addListener(link_pv_listener);
            link_pv.start();
        }
        catch (Exception e)
        {
            Plugin.logException("PV creation error", e); //$NON-NLS-1$
        }
    }

    /** Thread-save handling of the 'input_value' update. */
    @SuppressWarnings("nls")
    private void updateInput()
    {
        if (debug)
            System.out.println(link_pv.getName() + " received '" + link_value + "'");
        Display.getDefault().asyncExec(new Runnable()
        {
            public void run()
            {
                if (link_pv == null)
                {
                    if (debug)
                        System.out.println(pv_name + " already disposed");
                    return;
                }
                final boolean is_output = link_pv.getName().endsWith(DOL);
                disposeLinkPV();
                final boolean is_calc = isCalcType(type);
                final boolean is_seq = type.equals(SEQ);
                String info;
                if (is_output)
                    info = DOL;
                else if (is_calc)
                    info = INP + Character.toString((char)('A' + input_index));
                else if (is_seq)
                    info = SELN;
                else
                    info = INP;
                if (link_value.length() > 0)
                {
                    new PVTreeItem(model, PVTreeItem.this, info, link_value);
                    model.itemChanged(PVTreeItem.this);
                }
                // TODO Reads inputs up to INPL. How many are there really?
                if (is_calc && input_index < 11) // get INPB...INPL
                    getCalcInput(input_index + 1);
            }
        });
    }
}
