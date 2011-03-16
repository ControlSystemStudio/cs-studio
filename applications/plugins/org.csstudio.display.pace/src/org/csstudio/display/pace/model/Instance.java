/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.display.pace.model;

import java.util.List;

import org.csstudio.apputil.macros.IMacroTableProvider;
import org.csstudio.apputil.macros.MacroTable;
import org.csstudio.apputil.xml.DOMHelper;
import org.w3c.dom.Element;

/** Definition of a PACE model instance.
 *  <p>
 *  Describes one row in the table/model: Name of the instance, macros to use.
 *  After full initialization by Model, it also holds the cells in the row.
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
    final private IMacroTableProvider macros;
    private Cell[] cells;
    
    /** Parse and create Column from DOM
     *  @param model Model
     *  @param node DOM node for instance info
     *  @return Instance
     *  @throws Exception when expected DOM elements are missing or macros
     *          don't parse
     */
    public static Instance fromDOM(final Model model, Element node) throws Exception
    {
        // Get expected name and macro info
        final String name = DOMHelper.getSubelementString(node, "name");
        final String macro_text = DOMHelper.getSubelementString(node, "macros");
        if (name.length() <= 0)
            throw new Exception("Missing instance name");
        // Parse macro definition. OK to be empty, but errors result in exception
        final IMacroTableProvider macros = new MacroTable(macro_text);
        // Read instance definition from configuration file ... create the instance
        // Cells will be added later in createCells()
        return new Instance(model, name, macros);
    }

    /** Initialize Instance
     *  @param model Model
     *  @param name Instance name, row title
     *  @param macros List of macros
     */
    public Instance(final Model model, final String name, final IMacroTableProvider macros)
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
     *  @throws Exception on error in macros or in PV creation
     */
    void createCells(final List<Column> columns) throws Exception
    {
        cells = new Cell[columns.size()];
        // Using the macros for this instance,
        // create a cell for each column by expanding
        // the macroized PV name of the column
        for (int c = 0; c < cells.length; c++)
            cells[c] = new Cell(this, columns.get(c));
    }

    /** @return Instance name, title for row */
    public String getName()
    {
        return name;
    }

    /** @return Macros that turn macro-ized PV name into name for this instance */
    IMacroTableProvider getMacros()
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
        return "Instance '" + name + "', " + macros.toString();
    }
}
