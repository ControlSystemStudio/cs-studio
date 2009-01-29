package org.csstudio.display.pace.model;

import java.util.List;

import org.csstudio.apputil.xml.DOMHelper;
import org.w3c.dom.Element;

/** Definition of a PACE model instance.
 *  <p>
 *  Describes one row in the table/model: Name of the instance, macros to use.
 *
 *  TODO add describes cells in the row
 *  
 *  @author Delphy Nypaver Armstrong
 *  @author Kay Kasemir
 *  
 *    reviewed by Delphy 01/29/09
 */
@SuppressWarnings("nls")
public class Instance
{
    final private Model model;
    final private String name;
    final private Macro macros[];
    private Cell[] cells;
    
    /** Parse and create Column from DOM
     *  @param model Model
     *  @param node DOM node for instance info
     *  @return Instance
     */
    public static Instance fromDOM(final Model model, Element node) throws Exception
    {
       //TODO Explain what DOM is doing for you 
        final String name = DOMHelper.getSubelementString(node, "name");
        final String macro_text = DOMHelper.getSubelementString(node, "macros");
        //TODO Explain 
        final Macro macros[] = Macro.fromList(macro_text);
        //TODO Read instance definition from configuration file ... create the instance 
        return new Instance(model, name, macros);
    }

    /** Initialize Instance
     *  @param model Model
     *  @param name Instance name, row title
     *  @param macros List of macros
     */
    public Instance(final Model model, final String name, final Macro macros[])
    {
        this.model = model;
        this.name = name;
        this.macros = macros;
    }

    /** @return Model that holds this instance */
    Model getModel()
    {
        return model;
    }

    /** Create cells for each column
     *  @param columns Column definitions
     *  @throws Exception on error 
     */
    void createCells(final List<Column> columns) throws Exception
    {
        cells = new Cell[columns.size()];
        for (int c = 0; c < cells.length; c++)
        {
           // TODO Explain the information in the column class used to create a cell
            cells[c] = new Cell(this, columns.get(c));
        }
    }

    /** @return Instance name, title for row */
    public String getName()
    {
        return name;
    }

    /** @return Macros that turn macro-ized PV name into name for this instance */
    Macro[] getMacros()
    {
        return macros;
    }
    
    /** @param c Cell index, 0 .. model.getColumnCount()-1
     *  @return Cell at given column in this instance/row
     */
    public Cell getCell(final int c)
    {
        return cells[c];
    }

    /** @param column Column in model
     *  @return Cell at given column in this instance/row
     */
    public Cell getCell(final Column column)
    {
        for (int c=0; c<model.getColumnCount(); ++c)
            if (model.getColumn(c) == column)
                return cells[c];
        return null;
    }

    /** @return String representation for debugging */
    @Override
    public String toString()
    {
        final StringBuilder buf = new StringBuilder();
        buf.append("Instance '" + name + "'");
        //TODO Explain what you are appending
        for (int i=0; i<macros.length; ++i)
        {
            if (i > 0)
                buf.append(", " + macros[i].toString());
            else
                buf.append(" " + macros[i].toString());
        }
        return buf.toString();
    }
}
