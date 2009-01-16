package org.csstudio.display.pace.model;

import org.csstudio.apputil.xml.DOMHelper;
import org.w3c.dom.Element;

/** Definition of a PACE model column.
 *  <p>
 *  Describes one column in the table/model.
 *  The PV is described with macros,
 *  and the Instance definition then replaces the macros
 *  to get the actuall PV for a Cell.
 *
 *  @author Delphy Nypaver Armstrong
 *  @author Kay Kasemir
 */
@SuppressWarnings("nls")
public class Column
{
    final String name;
    final String pv_with_macros;
    final boolean readonly;
    
    /** Parse and create Column from DOM
     *  @param col_node DOM node for column info
     *  @return Column
     */
    public static Column fromDOM(Element col_node)
    {
        final String name = DOMHelper.getSubelementString(col_node, "name");
        final String pv_pattern = DOMHelper.getSubelementString(col_node, "pv");
        final String access = DOMHelper.getSubelementString(col_node, "access");
        return new Column(name, pv_pattern, "ro".equalsIgnoreCase(access));
    }

    /** Initialize Column
     *  @param name Column name/title
     *  @param pv_with_macros Name of the PV with macros
     *  @param readonly read-only column?
     */
    public Column(final String name, final String pv_with_macros,
            final boolean readonly)
    {
        this.name = name;
        this.pv_with_macros = pv_with_macros;
        this.readonly = readonly;
    }

    /** @return Column name/title */
    public String getName()
    {
        return name;
    }

    /** @return <code>true</code> for read-only column */
    public boolean isReadonly()
    {
        return readonly;
    }

    /** @return Name of the PV with unexpanded macros */
    protected String getPvWithMacros()
    {
        return pv_with_macros;
    }

    /** @return String representation for debugging */
    @Override
    public String toString()
    {
        return "Column '" + name + "', PV '" + pv_with_macros + "'" +
               (readonly ? " (read-only)" : "");
    }
}
