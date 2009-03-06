package org.csstudio.display.pace.model;

import org.csstudio.apputil.xml.DOMHelper;
import org.w3c.dom.Element;

/** Definition of a PACE model column.
 *  <p>
 *  Describes one column in the table/model.
 *  The PV is described with macros,
 *  and the Instance definition then replaces the macros
 *  to get the actual PV for a Cell.
 *
 *  @author Delphy Nypaver Armstrong
 *  @author Kay Kasemir
 *  
 *      reviewed by Delphy 01/29/09
 */
@SuppressWarnings("nls")
public class Column
{
    final String name;
    final String pv_with_macros;
    final boolean readonly;
    final String namePv_with_macros;
    final String datePv_with_macros;
    String name_pv;
    String date_pv;
    final boolean hasComments;

    /** Parse and create Column from DOM
     *  @param col_node DOM node for column info
     *  @return Column
     *  @throws Exception on missing DOM elements
     */
    public static Column fromDOM(final Element col_node) throws Exception
    {
        // Get expected sub-elements
        final String name = DOMHelper.getSubelementString(col_node, "name");
        final String pv_pattern = DOMHelper.getSubelementString(col_node, "pv");
        final String access = DOMHelper.getSubelementString(col_node, "access");
        if (name.length() <= 0)
            throw new Exception("Missing column name");
        if (pv_pattern.length() <= 0)
            throw new Exception("Missing PV name pattern");
        String name_pv = DOMHelper.getSubelementString(col_node, "name_pv");
        String date_pv = DOMHelper.getSubelementString(col_node, "date_pv");
        // When access=="ro", make read-only. Otherwise, including no access
        // info, use read/write
        return new Column(name, pv_pattern, "ro".equalsIgnoreCase(access), 
              name_pv, date_pv);
    }

    /** Initialize Column
     *  @param name Column name/title
     *  @param pv_with_macros Name of the PV with macros
     *  @param readonly read-only column?
     */
    public Column(final String name, final String pv_with_macros,
            final boolean readonly, final String name_pv, final String date_pv)
    {
        this.name = name;
        this.pv_with_macros = pv_with_macros;
        this.readonly = readonly;
        if(name_pv.length()<=0 && date_pv.length()<=0)
           this.hasComments=false;
        else
           this.hasComments=true;

        this.namePv_with_macros = name_pv;
        this.datePv_with_macros = date_pv;
    }

    /** @return Column name/title */
    public String getName()
    {
        return name;
    }

    /** @return name of the person making the change */
    public String getNamePvWithMacros()
    {
        return namePv_with_macros;
    }
    
    /** @return name of the person making the change */
    public String getNamePv()
    {
        return name_pv;
    }
    
    /** @return date/time the change was made */
    public String getDatePvWithMacros()
    {
        return datePv_with_macros;
    }
    
    /** @return date/time the change was made */
    public String getDatePv()
    {
        return date_pv;
    }
    
    /** @return <code>true</code> for read-only column */
    public boolean isReadonly()
    {
        return readonly;
    }
    
    /** @return <code>true</code> for has_comments column */
    public boolean hasComments()
    {
        return hasComments;
    }

    /** @return Name of the PV with unexpanded macros */
    protected String getPvWithMacros()
    {
        return pv_with_macros;
    }

    /** @return Column name, PV, access representation for debugging */
    @Override
    public String toString()
    {
        return "Column '" + name + "', PV '" + pv_with_macros + "'" +
               (readonly ? " (read-only)" : "");
    }

}
